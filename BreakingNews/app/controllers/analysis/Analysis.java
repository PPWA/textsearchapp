package controllers.analysis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import controllers.Application;
import controllers.preparation.Search;

/**
 * 
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Analysis {
	
	private static DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

	public static void addNewDocument(String title, Date publicationDate,
			String urlsource, String urlpicture, String text, String teaser,
			String newsPortal) {
		String oldTopicHash = "";
		int i = 1;
		String isNew = "1";
		String topicHash = "";
		long date = Long.parseLong(df.format(publicationDate));
		int l = 0;
		int count = 0;
		Query q;
		byte[] bytesOfMessage;
		byte[] theDigest;
		boolean b = true;
		IndexWriter writer;
		String explanation = "";

		System.out.println("Beginn der Themen-Detektion ...");
		while (oldTopicHash.equals("") && i < 4) {
			switch (i) {
			case 1:
				// Requirement 060-1 - Gleicher-Titel-Regel
				System.out.println("Pruefung Gleicher-Titel-Regel ...");
				oldTopicHash = Algorithm.hasSimilarTitle(title);
				explanation = "Ähnlicher Titel in anderem Artikel vorhanden.";
				i++;
				break;
			case 2:
				// Requirement 060-3 - Mehr-als Fünf-Seltene-Regel
				System.out.println("Pruefung Mehr-als-fuenf-Seltene-Regel ...");
				oldTopicHash = Algorithm.hasSimilarRareWords(text);
				explanation = "Ähnliche Zusammenstellung seltener Begriffe in anderem Artikel vorhanden.";
				i++;
				break;
			case 3:
				// Requirement 060-4 - Zwei-bis-fünf-Seltene-Regel
				System.out.println("Pruefung Zwei-bis-fuenf-Seltene-Regel ...");
				oldTopicHash = Algorithm.hasSimilarBody(text);
				explanation = "Geringe Distanz des Nachrichtentextes zu anderem Artikel.";
				i++;
				break;
			}
		}
		if (oldTopicHash.equals("")) {
			System.out.println("Neues Thema gefunden!");
			explanation = "";
			isNew = Search.NEWTOPICQUERY;
	
				while (b) {
					Random random = new Random();
					l = random.nextInt(1000);
					try {
						bytesOfMessage = ("" + l).getBytes("UTF-8");
						MessageDigest md = MessageDigest.getInstance("MD5");
						theDigest = md.digest(bytesOfMessage);
						topicHash = new String(Hex.encodeHex(theDigest)).substring(0, 6);
					} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
						System.out.println("Erzeugung des Topic-Hashes fehlgeschlagen.");
					}

					try {
						q = new QueryParser(Version.LUCENE_46, "topichash", Application.getAnalyzer()).parse(topicHash);
						count = Application.getSearcher().search(q, 1).totalHits;
						Application.closeAll();
					} catch (Exception e) {
						System.out.println("Index-Verzeichnis nicht vorhanden.");
					}
					
					if (count > 0) {
						l++;
					} else {
						b = false; 
					}
					System.out.println("Erzeuge neuen Topic-Hash: "+ topicHash);
				}			
		} else {
			System.out.println("Thema bereits vorhanden.");
			isNew = Search.OLDTOPICQUERY;
			topicHash = oldTopicHash;
		}

		Document doc = new Document();
		System.out.println("Lege Artikel im Index ab ...");
		try {
			System.out.println(isNew);
			doc.add(new StringField("isNew", isNew, Field.Store.YES)); // StringField = Field.Index.NOT_ANALYZED
			doc.add(new StringField("topichash", topicHash, Field.Store.YES));
			doc.add(new TextField("title", title, Field.Store.YES)); // TextField = Field.Index.ANALYZED
			doc.add(new StringField("teaser", teaser, Field.Store.YES));
			doc.add(new TextField("text", text, Field.Store.NO)); 
			doc.add(new LongField("date", date, Field.Store.YES));
			doc.add(new StringField("newsportal", newsPortal, Field.Store.YES));
			doc.add(new StoredField("urlsource", urlsource)); // StoredField = Field.Index.NO, Field.Store.YES
			doc.add(new StoredField("urlpicture", urlpicture));
			doc.add(new StoredField("explanation", explanation));

			Application.getWriter().addDocument(doc);
			Application.closeAll();
			System.out.println("Artikel im Index gespeichert!");
		} catch (Exception e) {
			System.out.println("Artikel konnte nicht im Index gespeichert werden.");
		}
	}
}
