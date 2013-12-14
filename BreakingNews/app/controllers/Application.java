package controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * Klasse ist der Standard-Einstiegspunkt von Play! und enth&auml;lt globale Einstellungen und Konstanten.
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Application extends Controller {

	// inkl. StandardTokenizer, LowerCaseFilter, StopwortFilter
	/**
	 * Globale Referenz auf den Analyzer f&uuml;r alle Suchanfragen.
	 */
	private static Analyzer analyzer = new GermanAnalyzer(Version.LUCENE_46);
	/**
	 * Globale Referenz das Filehandle.
	 */
	private static File file = new File("index2");
	/**
	 * &Uuml;berschriebene Similarity f&uuml;r alle Suchanfragen und Indexierungen
	 */
	private static Similarity sim = new DefaultSimilarity() {

		/**
		 * &Uuml;berschreibt bei der Berechnung des Vector Space Models die
		 * invertierte Termfrequenz um dabei unber&uuml;cksichtigt zu lassen, wie oft
		 * ein Term im gesamten Index vorkommt.
		 * 
		 * @param docFreq Anzahl der Dokumente, die Term enthalten,
		 * @param numDocs Gesamtanzahl der Dokumente im Index
		 * @return Konstanten Wert 1
		 */
		public float idf(long docFreq, long numDocs) {
			return 1;
		}
	};

	/**
	 * Erzeugt einen neuen Reader und einen Searcher auf dem angegebenen Index auf der Festplatte
	 * und gibt ihn zur&uuml;ck.
	 * 
	 * @return eine Referenz auf den Searcher
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

	/**
	 * Erzeugt einen IndexWriter auf dem angegebenen Index
	 * 
	 * @return eine Referenz auf den IndexWriter
	 */
	public static IndexWriter createWriter() {
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46,
					analyzer).setSimilarity(sim);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(FSDirectory.open(file), iwc);
			return writer;
		} catch (Exception e) {
			System.out.println("Index-Verzeichnis nicht vorhanden.");
			return null;
		}
	}
	
	/**
	 * 
	 * @return Eine Referenz auf den Analzyer
	 */
	public static Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * Gibt die Anzahl aller im Index gespeicherten Dokumente zur&uuml;ck.
	 * @author Christian Ochsenk&uuml;hn
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
	 * Holt sich die Term-Frequenz eines bestimmten Dokuments und gibt diese zur&uuml;ck.
	 * @author Christian Ochsenk&uuml;hn
	 * @param docId: Dokumenten-Id im Index
	 * @return Alle Terme inklusive Anzahl des Vorkommens im Dokument
	 */
	public static Map<String, Integer> getTermFrequencies(int docId) {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(file));
			Terms vector = reader.getTermVector(docId, "text");
			reader.close();
			TermsEnum termsEnum = null;
			termsEnum = vector.iterator(termsEnum);
			Map<String, Integer> frequencies = new HashMap<String, Integer>();
			BytesRef byteText = null;
			while ((byteText = termsEnum.next()) != null) {
			    String term = byteText.utf8ToString();
			    int freq = (int) termsEnum.totalTermFreq();
			    frequencies.put(term, freq);
			}
			return frequencies;
		} catch (IOException e) {
			System.out.println("Application: Can not get termfrequencies!");
		} catch(NullPointerException ne) {
			System.out.println("Application: No termvector found.");
		}
		return null;
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
