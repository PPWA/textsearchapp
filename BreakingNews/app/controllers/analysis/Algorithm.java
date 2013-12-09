package controllers.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import controllers.Application;
import controllers.preparation.Search;

/**
 * 
 * @author Christian Ochsenkühn
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Algorithm {

	List<String> rareWords = new ArrayList<String>();

	public static String hasSimilarTitle(String queryTitle) {
		// Fall: Gleiche Themen als unterschiedlich erkannt
		// Häufigkeit: wahrscheinlich
		// Folge: Kein Problem, da spätere Funktionen korrigieren

		// Fall: Unterschiedliche Themen als gleich erkannt
		// Häufigkeit: gering
		// Folge: Neues Thema geht unter
		String topicHash = "";
		TopDocs topDocs;
		ScoreDoc[] hits = null;
		IndexSearcher searcher = Application.getSearcher();
		Query query1;
		Query query2;
		String docTitle;
		StringTokenizer queryTokenizer;
		StringTokenizer docTokenizer;
		String currentQueryToken;
		int i = 0;

		try {
			BooleanQuery booleanQuery = new BooleanQuery();
			query1 = new QueryParser(Version.LUCENE_46, "title", Application.getAnalyzer()).parse(queryTitle);
			query2 = NumericRangeQuery.newLongRange("date", Search.getLowerBound(), Search.getUpperBound(), true, true);
			booleanQuery.add(query1, BooleanClause.Occur.MUST);
			booleanQuery.add(query2, BooleanClause.Occur.MUST);
			hits = searcher.search(booleanQuery, 1).scoreDocs;

			if (hits.length == 1) {
				docTitle = searcher.doc(hits[0].doc).get("title");
				queryTokenizer = new StringTokenizer(queryTitle);

				while (queryTokenizer.hasMoreTokens()) {
					currentQueryToken = queryTokenizer.nextToken();
					docTokenizer = new StringTokenizer(docTitle);
					while (docTokenizer.hasMoreTokens()) {
						System.out.println("test");
						if (currentQueryToken.equals(docTokenizer.nextToken())) {
							i++;
							break;
						}
					}
				}

				if (i >= 3) {
					topicHash = searcher.doc(hits[0].doc).get("topichash");
					System.out.println("Aehnlicher Titel gefunden: " + topicHash);
				}	
			}
			Application.closeAll();
		} catch (IOException | ParseException | NullPointerException e) {
			System.out.println("hasSimilarTitle fehlgeschlagen da Index nicht vorhanden.");
		}
		return topicHash;
	}

	public static String hasSimilarRareWords(String text) {
		String topicHash = "";
		// Seltene Wörter herausfinden und global speichern,
		// Artikel suchen die mehr als fünf dieser Wörter aufweisen,
		// bei Fund Abbruch und dessen topichash
		return topicHash;
	}

	public static String hasSimilarBody(String text) {
		String topicHash = "";
		// Artikel suchen die 2-5 der zuvor gespeicherten seltenen begriffe
		// enthalten
		// und mit denen vector space,
		// bei 40 % übereinstimmung abbruch und dessen topichash,
		// wenn keine artikel mit 2-5 gefunden -> neues Thema
		return topicHash;
	}
}