package controllers.analysis;

import java.io.File;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
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

public class Analysis {

	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

	public static IndexWriter getWriter() throws Exception {
		Directory dir = FSDirectory.open(new File(Search.indexPath));
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,
				analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		return new IndexWriter(dir, iwc);
	}

	public static void addNewDocument(String title, Date publicationDate,
			String urlsource, String urlpicture, String text, String newsPortal) {
		String oldTopicHash = "";
		int i = 1;
		String isNew = "1";
		String topicHash = "";
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		long date = Long.parseLong(df.format(publicationDate));
		int l = 0;
		int count = 0;
		IndexReader reader;
		IndexSearcher searcher;
		Query q;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		byte[] bytesOfMessage;
		byte[] theDigest;
		boolean b = true;
		IndexWriter writer;

		while (oldTopicHash.equals("") && i < 5) {
			switch (i) {
			case 1:
				// Requirement 060-1 - Gleicher-Titel-Regel
				oldTopicHash = isSameTitle(title);
				i++;
				break;
			case 2:
				// Requirement 060-2 - Kein-neues-Wort-Regel
				oldTopicHash = isNoNewWord(text);
				i++;
				break;
			case 3:
				// Requirement 060-3 - Mehr-als Fünf-Seltene-Regel
				oldTopicHash = areMoreThanFive(text);
				i++;
				break;
			case 4:
				// Requirement 060-4 - Zwei-bis-fünf-Seltene-Regel
				oldTopicHash = hasCloseVectorSpace(text);
				i++;
				break;
			}
		}
		if (oldTopicHash.equals("")) {
			isNew = Search.NEWTOPICQUERY;

			try {
				while (b) {
					Random random = new Random();
					l = random.nextInt(1000);
					bytesOfMessage = ("" + l).getBytes("UTF-8");
					MessageDigest md = MessageDigest.getInstance("MD5");
					theDigest = md.digest(bytesOfMessage);
					topicHash = new String(Hex.encodeHex(theDigest)).substring(
							0, 6);

					reader = DirectoryReader.open(FSDirectory.open(new File(
							Search.indexPath)));
					searcher = new IndexSearcher(reader);
					q = new QueryParser(Version.LUCENE_46, "topichash",
							analyzer).parse(topicHash);
					count = searcher.search(q, 1).totalHits;
					if (count > 0) {
						l++;
					} else {
						b = false;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			isNew = Search.OLDTOPICQUERY;
			topicHash = oldTopicHash;
		}

		Document doc = new Document();
		try {
			doc.add(new StringField("isNew", isNew, Field.Store.YES));
			doc.add(new StringField("topichash", topicHash, Field.Store.YES));
			doc.add(new StringField("title", title, Field.Store.YES));
			doc.add(new TextField("text", text, Field.Store.YES));
			doc.add(new LongField("date", date, Field.Store.YES));
			doc.add(new StringField("newsportal", newsPortal, Field.Store.YES));
			doc.add(new StringField("urlsource", urlsource, Field.Store.YES));
			doc.add(new StringField("urlpicture", urlpicture, Field.Store.YES));
			writer = getWriter();
			writer.addDocument(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String isSameTitle(String title) {
		String topicHash = "";

		return topicHash;
	}

	public static String isNoNewWord(String text) {
		String topicHash = "";

		return topicHash;
	}

	public static String areMoreThanFive(String text) {
		String topicHash = "";

		return topicHash;
	}

	public static String hasCloseVectorSpace(String text) {
		String topicHash = "";

		return topicHash;
	}

	/*
	 * public static void test(){ try { dir = FSDirectory.open(new
	 * File("index")); Analyzer analyzer = new
	 * StandardAnalyzer(Version.LUCENE_46); iwc = new
	 * IndexWriterConfig(Version.LUCENE_46, analyzer);
	 * iwc.setOpenMode(OpenMode.CREATE);
	 * 
	 * Document doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch1",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311191200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); IndexWriter writer = new
	 * IndexWriter(dir, iwc); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch2",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311161200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch3",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311141200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch4",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311121200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch5",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311111200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch6",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311191200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch7",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311081200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch8",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311071200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch9",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311061200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "1", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch10",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311051200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * //------------old------------------------- doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch1",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311191200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch2",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311161200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch3",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311141200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch4",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311121200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch5",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311111200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch6",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311191200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch7",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311081200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch8",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311071200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch9",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311061200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * doc = new Document();
	 * 
	 * doc.add(new StringField("isNew", "0", Field.Store.YES)); doc.add(new
	 * StringField("topichash", "gC73x8", Field.Store.YES)); doc.add(new
	 * StringField("title",
	 * "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch10",
	 * Field.Store.YES)); doc.add(new StringField("teaser",
	 * "Heute kam es in ...", Field.Store.YES)); doc.add(new LongField("date",
	 * 201311051200L, Field.Store.YES)); doc.add(new StringField("newsportal",
	 * "Welt Online", Field.Store.YES)); doc.add(new StringField("urlsource",
	 * "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html"
	 * , Field.Store.YES)); doc.add(new StringField("urlpicture",
	 * "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg"
	 * , Field.Store.YES)); doc.add(new StringField("explanation",
	 * "Neue Wörter ...", Field.Store.YES)); writer.addDocument(doc);
	 * 
	 * writer.close(); } catch (Exception e) { } }
	 */
}
