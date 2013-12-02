package controllers.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
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
			dir = FSDirectory.open(new File("index"));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
			iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
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
			String urlSource, String urlPicture, String text, String newsPortal) {
		Document doc = new Document();
		try {
			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topicHash", "hallo", Field.Store.YES));
			doc.add(new IntField("date", 20001211, Field.Store.YES));
			doc.add(new StringField("newsportal", "Spiegel", Field.Store.YES));
			getWriter().addDocument(doc);
			getWriter().close();
		} catch (Exception e) {

		}
	}
	
	public static void test(){
		try {
		dir = FSDirectory.open(new File("index"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		
		Document doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topicHash", "hallo", Field.Store.YES));
			doc.add(new IntField("date", 20001211, Field.Store.YES));
			doc.add(new StringField("newsportal", "Spiegel", Field.Store.YES));
			IndexWriter writer = new IndexWriter(dir, iwc);
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topicHash", "hallo", Field.Store.YES));
			doc.add(new IntField("date", 20001211, Field.Store.YES));
			doc.add(new StringField("newsportal", "Spiegel", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "1", Field.Store.YES));
			doc.add(new StringField("topicHash", "hallo", Field.Store.YES));
			doc.add(new IntField("date", 20001211, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt", Field.Store.YES));
			writer.addDocument(doc);
			
			doc = new Document();

			doc.add(new StringField("isNew", "0", Field.Store.YES));
			doc.add(new StringField("topicHash", "hallo", Field.Store.YES));
			doc.add(new IntField("date", 20001211, Field.Store.YES));
			doc.add(new StringField("newsportal", "Welt", Field.Store.YES));
			writer.addDocument(doc);
			
			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
