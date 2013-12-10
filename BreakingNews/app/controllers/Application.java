package controllers;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * Klasse ist der Standard-Einstiegspunkt von Play!
 * 
 * @author Automatisch generiert
 * @version 1.0
 */
public class Application extends Controller {

	// inkl. StandardTokenizer, LowerCaseFilter, StopwortFilter
	private static Analyzer analyzer = new GermanAnalyzer(Version.LUCENE_46);
	private static File file = new File("index2");
	/***
	 * Globale Referenz auf den Reader f&uuml;r alle Suchanfragen.
	 */

	private static Similarity sim = new DefaultSimilarity() {
		public float idf(long i, long i1) {
			return 1;
		}
	};
	
	/**
	 * &Ouml;ffnet einen SuchReader auf dem angegebenen Index auf der Festplatte
	 * und gibt ihn zur&uuml;ck.
	 * 
	 * @return eine Referenz auf den SearchReader
	 * @throws Exception
	 *             wenn der spezifizierte Index nicht vorhanden ist.
	 */
	public static IndexSearcher createSearcher() {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(file));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(sim);
			return searcher;
		} catch (Exception e) {
			System.out.println("Index-Verzeichnis nicht vorhanden.");
			return null;
		}
	}
	
	public static IndexWriter createWriter() {
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer).setSimilarity(sim);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(FSDirectory.open(file), iwc);
			return writer;
		} catch (Exception e) {
			System.out.println("Index-Verzeichnis nicht vorhanden.");
			return null;
		}
	}
	
	public static Analyzer getAnalyzer() {
		return analyzer;
	}
	
	/**
	 * Gibt die Anzahl aller im Index gespeicherten Dokumente zur&uuml;ck.
	 * @return Anzahl aller Dokumente im Index
	 */
	public static int getNumberOfAllDocuments() {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(file));
			int temp = reader.numDocs();
			reader.close();
			return temp;
		} catch (IOException e) {
			System.out.println("Application: Can not read documents-count!");
			return 0;
		}
	}
		
	/**
	 * Gibt die Startseite zur&uuml;ck, sobald ein Client diese aufruft.
	 * 
	 * @return Eine HTTP-Response mit Status-Code 200, dem MIMETYPE text/html
	 *         und des HTML-Codes f&uuml;r die Startseite
	 */
	public static Result index() {
		return ok(index.render("Home"));
	}
}
