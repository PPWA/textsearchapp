package controllers.analysis;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import controllers.preparation.Search;

/**
 * 
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Analysis {
	
	//inkl. StandardTokenizer, LowerCaseFilter, StopwortFilter
	private static Analyzer analyzer = new GermanAnalyzer(Version.LUCENE_46); 
	private static DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
	private static File file = new File(Search.indexPath);
	private static IndexReader reader;
	private static Directory dir;

	public static IndexSearcher getSearcher() {
		try {
			reader = DirectoryReader.open(FSDirectory.open(file));
			return new IndexSearcher(reader);
		} catch (IOException e) {
			System.out.println("Index-Verzeichnis nicht vorhanden");
			return null;
		}
	}

	public static IndexWriter getWriter() {
		try {
			dir = FSDirectory.open(file);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,
					analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			return new IndexWriter(dir, iwc);
		} catch (Exception e) {
			System.out.println("Index-Verzeichnis nicht vorhanden");
			return null;
		}
	}

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
		while (oldTopicHash.equals("") && i < 5) {
			switch (i) {
			case 1:
				// Requirement 060-1 - Gleicher-Titel-Regel
				System.out.println("Pruefung Gleicher-Titel-Regel ...");
				oldTopicHash = Algorithm.hasSimilarTitle(title);
				explanation = "Titel bereits in anderen Artikel vorhanden.";
				i++;
				break;
			case 2:
				// Requirement 060-2 - Kein-neues-Wort-Regel
				System.out.println("Pruefung Kein-neues-Wort-Regel ...");
				oldTopicHash = Algorithm.hasNoNewWords(text);
				explanation = "Artikel enthält keine neuen Wörter.";
				i++;
				break;
			case 3:
				// Requirement 060-3 - Mehr-als Fünf-Seltene-Regel
				System.out.println("Pruefung Mehr-als-fuenf-Seltene-Regel ...");
				oldTopicHash = Algorithm.hasSimilarRareWords(text);
				explanation = "Artikel enthält zu viele bekannte Wörter.";
				i++;
				break;
			case 4:
				// Requirement 060-4 - Zwei-bis-fünf-Seltene-Regel
				System.out.println("Pruefung Zwei-bis-fuenf-Seltene-Regel ...");
				oldTopicHash = Algorithm.hasSimilarBody(text);
				explanation = "Geringer Abstand zu existierenden Artikel (Vector Space Model).";
				i++;
				break;
			}
		}
		if (oldTopicHash.equals("")) {
			System.out.println("Neues Thema gefunden!");
			explanation = "";
			isNew = Search.NEWTOPICQUERY;

			try {
				System.out.println("Erzeuge neuen Topic-Hash ...");
				while (b) {
					Random random = new Random();
					l = random.nextInt(1000);
					bytesOfMessage = ("" + l).getBytes("UTF-8");
					MessageDigest md = MessageDigest.getInstance("MD5");
					theDigest = md.digest(bytesOfMessage);
					topicHash = new String(Hex.encodeHex(theDigest)).substring(
							0, 6);

					q = new QueryParser(Version.LUCENE_46, "topichash",
							analyzer).parse(topicHash);
					count = getSearcher().search(q, 1).totalHits;

					if (count > 0) {
						l++;
					} else {
						b = false;
					}
				}
				reader.close();
			} catch (Exception e) {
				System.out.println("Erzeugung des Topic-Hashes fehlgeschlagen. Existiert das Index-Verzeichnis?");
				e.printStackTrace();
			}
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
			doc.add(new StringField("title", title, Field.Store.YES));
			doc.add(new TextField("teaser", teaser, Field.Store.YES));
			doc.add(new TextField("text", text, Field.Store.NO)); // TextField = Field.Index.ANALYZED
			doc.add(new LongField("date", date, Field.Store.YES));
			doc.add(new StringField("newsportal", newsPortal, Field.Store.YES));
			doc.add(new StoredField("urlsource", urlsource)); // StoredField = Field.Index.NO, Field.Store.YES
			doc.add(new StoredField("urlpicture", urlpicture));
			doc.add(new StoredField("explanation", explanation));

			writer = getWriter();
			writer.addDocument(doc);
			writer.close();
			dir.close();
			System.out.println("Artikel im Index gespeichert!");
		} catch (Exception e) {
			System.out.println("Artikel konnte nicht im Index gespeichert werden.");
			e.printStackTrace();
		}
	}
}
