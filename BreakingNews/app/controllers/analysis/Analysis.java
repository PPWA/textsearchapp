package controllers.analysis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import controllers.Application;
import controllers.preparation.Search;

/**
 * Gibt die Struktur bzw. den Ablauf f&uuml;r den gesamten Textanalyse-Prozess und
 * seiner einzelnen Schritte vor.
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Analysis {
	/**
	 * Datumsformat in Lucene
	 */
	private static DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

	/**
	 * Wendet verschiedene Analyseschritte auf die Metadaten des Artikels an, um
	 * ein neues Thema zu detektieren. Wurde ein neues Thema in einem Artikel
	 * gefunden, wird f&uuml;r dieses ein neuer TopicHash generiert. Ansonsten wird
	 * der bestehende TopicHash eines Artikels mit dem vermeintlich selben
	 * Sachverhalt verwendet. Am Ende werden die Felder des Artikels, TopiHash
	 * und das Ergebnis der Themen-Pr&uuml;fung in den Lucene-Index geschrieben.
	 * 
	 * @param title
	 *            Der Titel des Nachrichtenartikels
	 * @param publicationDate
	 *            Das Ver&ouml;ffentlichkeitsdatum des Artikels
	 * @param urlsource
	 *            Die Quelle des Original-Artikels
	 * @param urlpicture
	 *            Die Adresse des Artikelbildes
	 * @param text
	 *            Der Volltext des Nachrichtenartikels
	 * @param teaser
	 *            Kurzer Anreißertext &uuml;ber den Inhalt des Artikels
	 * @param Newsanbieter
	 *            , der Artikel ver&ouml;ffentlicht
	 */
	public static void addNewDocument(String title, Date publicationDate,
			String urlsource, String urlpicture, String text, String teaser,
			String newsPortal) {
		String oldTopicHash = "";
		int i = 1;
		String isNew = "1";
		String topicHash = "";
		long date = Long.parseLong(df.format(publicationDate));
		int count = 0;
		Query q;
		byte[] bytesOfMessage;
		byte[] theDigest;
		String explanation = "";
		IndexSearcher searcher;
		IndexWriter writer;
		int x=0;

		System.out.println("Beginn der Themen-Detektion ...");
		while (oldTopicHash.equals("") && i < 4) {
			switch (i) {
			case 1:
				// Requirement 060-1 - Gleicher-Titel-Regel
				System.out.println("(1) Pruefung Gleicher-Titel-Regel ...");
				oldTopicHash = Algorithm.hasSimilarTitle(title);
				explanation = "Ähnlicher Titel in anderem Artikel vorhanden.";
				i++;
				break;
			case 2:
				// Requirement 060-3 - Mehr-als Fünfzehn-Seltene-Regel
				System.out.println("(2) Pruefung Mehr-als-fuenfzehn-Seltene-Regel ...");
				oldTopicHash = Algorithm.hasSimilarRareWords(text);
				explanation = "Ähnliche Zusammenstellung seltener Begriffe in anderem Artikel vorhanden.";
				i++;
				break;
			case 3:
				// Requirement 060-4 - Fünf-bis-fünfzehn-Seltene-Regel
				System.out.println("(3) Pruefung Fuenf-bis-Fuenfzehn-Seltene-Regel ...");
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
				
				//Erzeugung des TopicHashes aus Titel und Publikationsdatum
				while (true) {
					try {
						count = 0;
						bytesOfMessage = (title+publicationDate.toString()+x).getBytes("UTF-8");
						MessageDigest md = MessageDigest.getInstance("MD5");
						theDigest = md.digest(bytesOfMessage);
						topicHash = new String(Hex.encodeHex(theDigest)).substring(0, 6);
					} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
						System.out.println("Erzeugung des Topic-Hashes fehlgeschlagen.");
					}
					
					try {
						//Prüfen ob TopicHash evtl. schon im Index vorhanden
						q = new QueryParser(Version.LUCENE_46, "topichash", Application.getAnalyzer()).parse(topicHash);
						searcher = Application.createSearcher();
						count = searcher.search(q, 1).totalHits;
						searcher.getIndexReader().close();
					} catch (Exception e) {
						System.out.println("Index-Verzeichnis nicht vorhanden.");
					}
					if (count == 0) {
						break;
					} else
					{
						x++;
					}
				}	
				System.out.println("Erzeuge neuen Topic-Hash: "+ topicHash);
		} else {
			System.out.println("Thema bereits vorhanden.");
			isNew = Search.OLDTOPICQUERY;
			topicHash = oldTopicHash;
		}

		Document doc = new Document();
		System.out.println("Lege Artikel im Index ab ...");
		try {
			doc.add(new StringField("isNew", isNew, Field.Store.YES)); // StringField = Field.Index.NOT_ANALYZED
			doc.add(new StringField("topichash", topicHash, Field.Store.YES));
			doc.add(new TextField("title", title, Field.Store.YES)); // TextField = Field.Index.ANALYZED
			doc.add(new StringField("teaser", teaser, Field.Store.YES));
			FieldType type = new FieldType();
				type.setIndexed(true);
				type.setStored(false);
				type.setStoreTermVectors(true);
			doc.add(new Field("text", text, type));
			doc.add(new LongField("date", date, Field.Store.YES));
			doc.add(new StringField("newsportal", newsPortal, Field.Store.YES));
			doc.add(new StoredField("urlsource", urlsource)); // StoredField = Field.Index.NO, Field.Store.YES
			doc.add(new StoredField("urlpicture", urlpicture));
			doc.add(new StoredField("explanation", explanation));
			
			writer = Application.createWriter();
			writer.addDocument(doc);
			writer.close();
			System.out.println("Artikel im Index gespeichert!");
		} catch (Exception e) {
			System.out.println("Artikel konnte nicht im Index gespeichert werden.");
		}
	}
}
