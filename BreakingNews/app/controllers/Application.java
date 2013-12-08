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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import controllers.preparation.Search;

/**
 * Klasse ist der Standard-Einstiegspunkt von Play!
 * 
 * @author Automatisch generiert
 * @version 1.0
 */
public class Application extends Controller {

	// inkl. StandardTokenizer, LowerCaseFilter, StopwortFilter
	private static Analyzer analyzer = new GermanAnalyzer(Version.LUCENE_46);
	private static File file = new File(Search.indexPath);
	/**
	 * Globale Referenz auf den Reader f&uuml;r alle Suchanfragen.
	 */
	private static IndexReader reader;

	private static Directory dir;

	public static Analyzer getAnalyzer() {
		return analyzer;
	}

	public static IndexReader getReader() {
		return reader;
	}

	public static Directory getDir() {
		return dir;
	}

	/**
	 * Gibt die Startseite zur&uuml;ck, sobald eine Client diese aufruft.
	 * 
	 * @return Eine HTTP-Response mit Status-Code 200, dem MIMETYPE text/html
	 *         und des HTML-Codes f&uuml;r die Startseite
	 */
	public static Result index() {
		return ok(index.render("Home"));
	}
	/**
	 * &ouml;ffnet einen SuchReader auf dem angegebenen Index auf der Festplatte und
	 * gibt ihn zur&uuml;ck.
	 * 
	 * @return eine Referenz auf den SearchReader
	 * @throws Exception
	 *             wenn der spezifizierte Index nicht vorhanden ist.
	 */
	public static IndexSearcher getSearcher() {
		try {
			reader = DirectoryReader.open(FSDirectory.open(file));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new DefaultSimilarity()); //enthaehlt Vector Space Model
			return searcher;
		} catch (IOException e) {
			System.out.println("Index-Verzeichnis nicht vorhanden.");
			return null;
		}
	}

	public static IndexWriter getWriter() {
		try {
			dir = FSDirectory.open(file);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,analyzer).
					setSimilarity(new DefaultSimilarity()); //enthaehlt Vector Space Model
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			return new IndexWriter(dir, iwc);
		} catch (Exception e) {
			System.out.println("Index-Verzeichnis nicht vorhanden.");
			return null;
		}
	}

}
