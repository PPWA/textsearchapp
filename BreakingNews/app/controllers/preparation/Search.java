package controllers.preparation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Newsportal;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import controllers.Application;

/**
 * Erzeugt je nach Anfrage der Klasse Preparation eine Query, stellt diese an
 * den persistenten Lucene-Index und liefert die Suchergebnisse an die
 * aufrufende Stelle zur&uuml;ck.
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Search {

	/**
	 * Speichert das zuletzt ermittelte Dokument bei Suchanfragen f&uuml;r neue
	 * Themen
	 */
	private static ScoreDoc lastDocNew;
	/**
	 * Speichert den Zustand, ob Ende der Suchergebnisliste f&uuml;r neue Themen
	 * erreicht ist.
	 */
	private static boolean endNew = false;
	/**
	 * Speichert das zuletzt ermittelte Dokument bei Suchanfragen f&uuml;r bereits
	 * bekannte Themen
	 */
	private static ScoreDoc lastDocOld;
	/**
	 * Speichert den Zustand, ob Ende der Suchergebnisliste f&uuml;r bereits bekannte
	 * Themen erreicht ist.
	 */
	private static boolean endOld = false;
	/**
	 * Suchbegriff zur Ermittelung von Artikeln mit neuen Themen
	 */
	public final static String NEWTOPICQUERY = "1";
	/**
	 * Suchbegriff zur Ermittelung von Artikeln mit bereits bekannten Themen
	 */
	public final static String OLDTOPICQUERY = "0";
	/**
	 * Anzahl der zu ermittelnden Suchergebnisse, wenn Liste mit Listenbeginn
	 * startet
	 */
	private static int hitsPerPage = 5;
	/** Gibt in ganzen Tagen an, wie weit bei Suchanfragen in die Vergangenheit
	 * zur&uuml;ckgeschaut werden soll
	 */
	private static int timeframe = 90;
	/**
	 * Gibt das aktuelle Datum als Integer zur&uuml;ck.
	 * 
	 * @return das aktuelle Datum als Integer
	 */
	public static Long getUpperBound() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		return Long.parseLong(df.format(new Date()));
	}

	/**
	 * Ermittelt das Datum f&uuml;r den Beginn des Zeitfensters, das f&uuml;r alle Suchen
	 * verwendet wird.
	 * 
	 * @return Den Beginn des Suchzeitraumes
	 */
	public static long getLowerBound() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, -timeframe);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		return Long.parseLong(df.format(c.getTime()));
	}

	/**
	 * Erzeugt Suchbedingungen f&uuml;r die Suche nach neuen Themen und stellt die
	 * Anfrage an die zentrale Suchfunktion
	 * 
	 * @param offset
	 *            Flag, ob Ergebnisliste von vorn beginnt oder an letzter
	 *            bekannter Position fortgesetzt wird.
	 * @param keyword
	 *            Suchbegriff f&uuml;r die Volltextsuche
	 * @return Eine Liste der aktuellsten Dokumente innerhalb des Suchzeitraumes
	 *         mit neuen Themen
	 */
	public static List<Document> getDocumentsNewTopic(int offset, String keyword) {
		String querystr = NEWTOPICQUERY;
		String queryfield = "isNew";
		return getResults(querystr, queryfield, offset, endNew, lastDocNew,
				NEWTOPICQUERY, keyword);
	}

	/**
	 * Erzeugt Suchbedingungen f&uuml;r die Suche nach bereits bekannten Themen und
	 * stellt die Anfrage an die zentrale Suchfunktion
	 * 
	 * @param offset
	 *            Flag, ob Ergebnisliste von vorn beginnt oder an letzter
	 *            bekannter Position fortgesetzt wird.
	 * @return Eine Liste der aktuellsten Dokumente innerhalb des Suchzeitraumes
	 *         mit bereits bekannten Themen
	 */
	public static List<Document> getDocumentsOldTopic(int offset) {

		String querystr = OLDTOPICQUERY;
		String queryfield = "isNew";
		return getResults(querystr, queryfield, offset, endOld, lastDocOld,
				OLDTOPICQUERY, "");
	}

	/**
	 * Erzeugt Suchbedingungen f&uuml;r die Suche nach neuen Themen und stellt die
	 * Anfrage an die zentrale Suchfunktion
	 * 
	 * @param topicHash
	 *            Identifier f&uuml;r das Thema, nach dessen zugeh&ouml;rigen Artikeln
	 *            gesucht werden soll
	 * @return Eine Liste der aktuellsten Dokumente innerhalb des Suchzeitraumes
	 *         die zu dem angegebenen Thema passen
	 */
	public static List<Document> getSimilarDocuments(String topicHash) {

		String querystr = topicHash;
		String queryfield = "topichash";
		return getResults(querystr, queryfield, 0, false, null, "", "");
	}

	/**
	 * Erzeugt f&uuml;r jedes individuelle Newsportal ein Objekt und ermittelt die
	 * Anzahl der jeweils publizierten Artikel mit neuen Themen innerhalb des
	 * Suchzeitraumes.
	 * 
	 * @return Eine Liste von Newsportal-Objekten
	 */
	public static List<Newsportal> getNewsportalList() {
		String lastName = "";
		String currentName = "";
		Newsportal currentNewsportal = null;
		List<Newsportal> newsportalList = new ArrayList<Newsportal>();
		List<Document> documents = getAllResultsNewTopic();

		for (int i = 0; i < documents.size(); i++) {
			currentName = documents.get(i).get("newsportal");
			if (!currentName.equals(lastName)) {
				currentNewsportal = new Newsportal(currentName);
				currentNewsportal.raise();
				newsportalList.add((currentNewsportal));
				lastName = currentName;
			} else {
				currentNewsportal.raise();
			}
		}
		return newsportalList;
	}

	/**
	 * Erstellt eine Suchanfrage zur Ermittlung aller Dokumente mit neuen Themen
	 * innerhalb des Suchzeitraumes und stellt die Anfrage an den Lucene-Index.
	 * 
	 * @return Eine Liste aller Dokumente innerhalb des Suchzeitraumes, die ein
	 *         neues Thema enthalten
	 */
	public static List<Document> getAllResultsNewTopic() {
		try {
			List<Document> documents = new ArrayList<Document>();
			Sort sort = new Sort(new SortField("newsportal", SortField.Type.STRING,
					true));
			ScoreDoc[] hits = null;
			IndexSearcher searcher = Application.createSearcher();
			
			// Erstellung der Suchanfrage
			System.out.println("Suchanfrage an Lucene wird durchgefuehrt ...");
			BooleanQuery booleanQuery = new BooleanQuery();
			Query query1 = new TermQuery(new Term("isNew", NEWTOPICQUERY));
			Query query2 = NumericRangeQuery.newLongRange("date",
					getLowerBound(), getUpperBound(), true, true);
			booleanQuery.add(query1, BooleanClause.Occur.MUST);
			booleanQuery.add(query2, BooleanClause.Occur.MUST);
			int count = searcher.search(booleanQuery, 1).totalHits;
			hits = searcher.search(booleanQuery, count, sort).scoreDocs;

			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				documents.add(d);
			}
			searcher.getIndexReader().close();
			System.out.println("Suchergebnisse ermittelt!");
			return documents;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fehler beim Ausfuehren der Suchanfrage");
			return new ArrayList<Document>();
		}
	}

	/**
	 * Erstellt verschiedene Suchanfragen und stellt die Anfrage an den Lucene-Index.
	 * 
	 * @param querystr Suchbegriff
	 * @param queryfield Suchfeld, in denen der Suchbegriff vorhanden sein muss
	 * @param Flag, ob Liste neu beginnen oder an bestimmter Stelle fortgesetzt werden soll
	 * @param end Flag, ob bei letzten Suchvorgang das Ende der Liste bereits erreicht wurde
	 * @param lastDoc Speichert das zuletzt ermittelte Dokument der letzten Suchanfrage
	 * @param Zeigt an, ob es sich um eine Suchanfrage nach neuen oder nach bereits bekannten Themen handelt
	 * @param keyword Sekund&auml;rer Suchbegriff f&uuml;r die Volltextsuche im Nachrichteninhalt
	 * @return Eine Liste der aktuellsten Dokumente, die allen Suchbedingungen entsprechen
	 */
	public static List<Document> getResults(String querystr, String queryfield,
			int offset, boolean end, ScoreDoc lastDoc, String k, String keyword) {
		try {
			List<Document> documents = new ArrayList<Document>();
			ScoreDoc[] hits = null;
			Sort sort = new Sort(new SortField("date", SortField.Type.LONG,true)); // Sortierung
			IndexSearcher searcher = Application.createSearcher();

			// Erstellung der Suchanfrage
			System.out.println("Suchanfrage an Lucene wird durchgefuehrt ...");
			BooleanQuery booleanQuery = new BooleanQuery();
			Query query1 = new TermQuery(new Term(queryfield, querystr)); // isNew
			Query query2 = NumericRangeQuery.newLongRange("date", 
					getLowerBound(), getUpperBound(), true, true); // Datum
			booleanQuery.add(query1, BooleanClause.Occur.MUST);
			booleanQuery.add(query2, BooleanClause.Occur.MUST);
			if (!keyword.equals("")) {
				Query query3 = new TermQuery(new Term("text", keyword)); //Suchbegriff
				booleanQuery.add(query3, BooleanClause.Occur.MUST);
			}
			if (offset == 0) {
				// Listenanfang
				end = false;
				hits = searcher.search(booleanQuery, hitsPerPage, sort).scoreDocs;
			} else {
				// Listenfortsetzung
				if (end) {
					hits = null;
				} else {
					hits = searcher.searchAfter(lastDoc, booleanQuery, 3, sort).scoreDocs;
				}
			}
			// Caching des jeweils letzten Dokuments der Suchanfrage, damit
			// searchAfter() ausgeführt werden kann
			try {
				lastDoc = hits[hits.length - 1];
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					documents.add(d);
				}
			} catch (Exception e) {
				lastDoc = null;
				end = true;
			}
			searcher.getIndexReader().close();
			if (k == NEWTOPICQUERY) {
				lastDocNew = lastDoc;
				endNew = end;
			} else if (k == OLDTOPICQUERY) {
				lastDocOld = lastDoc;
				endOld = end;
			}
			System.out.println("Suchergebnisse ermittelt!");
			return documents;
		} catch (Exception e) {
			System.out.println("Fehler beim Ausfuehren der Suchanfrage");
			return new ArrayList<Document>();
		}
	}
}
