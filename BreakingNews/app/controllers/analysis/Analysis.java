package controllers.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Analysis {


	private static Directory dir;
	private static IndexWriterConfig iwc;

	public static void initIndex() {
		try {
			dir = FSDirectory.open(new File("index2"));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
			iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
		} catch (Exception e) {

		}
	}

	public static IndexWriter getWriter() {
		try {
			return new IndexWriter(dir, iwc);
		} catch (IOException e) {
			return null;
		}
	}

	public void addNewDocument(String title, Date publicationDate,
			String urlsource, String urlpicture, String text, String newsPortal) {
		
		
		
		
		Document doc = new Document();
		try {
			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topicHash", "hallo", Field.Store.YES));
			doc.add(new IntField("date", 20001211, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			getWriter().addDocument(doc);
			getWriter().close();
		} catch (Exception e) {

		}
	}
	
	
	
	
	
	
	
	
	
	/*
	public static void test(){
		try {
			dir = FSDirectory.open(new File("index"));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
			iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
		
			Document doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));	
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch1", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311191200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			IndexWriter writer = new IndexWriter(dir, iwc);
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch2", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311161200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch3", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311141200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch4", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311121200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch5", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311111200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));	
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch6", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311191200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch7", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311081200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch8", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311071200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch9", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311061200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch10", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311051200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			//------------old-------------------------
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));	
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch1", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311191200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch2", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311161200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch3", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311141200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch4", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311121200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch5", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311111200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));	
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch6", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311191200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch7", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311081200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch8", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311071200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch9", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311061200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topichash", "gC73x8", Field.Store.YES));
			doc.add(new StringField("title", "Bücher-Krise: Chinas Verlage produzieren direkt für den Ramsch10", Field.Store.YES));
			doc.add(new StringField("teaser", "Heute kam es in ...", Field.Store.YES));
			doc.add(new LongField("date", 201311051200L, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt Online", Field.Store.YES));
			doc.add(new StringField("urlsource", "http://www.welt.de/kultur/literarischewelt/article120673225/Chinas-Verlage-produzieren-direkt-fuer-den-Ramsch.html", Field.Store.YES));
			doc.add(new StringField("urlpicture", "http://img.welt.de/img/literarischewelt/crop120673224/875209918-ci3x2s-w260/163625140.jpg", Field.Store.YES));
			doc.add(new StringField("explanation", "Neue Wörter ...", Field.Store.YES));
			writer.addDocument(doc);
			
			writer.close();
		} catch (Exception e) {
		}
	}*/
}
