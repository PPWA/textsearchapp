package controllers.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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

	// Prozentuales Vorkommen in allen Dokumenten, ab der ein Wort noch als selten gilt:
	private final static float RARE_APPEARANCE = (float) 0.15; // = 15%
	
	// Anzahl der seltenen Wörter, ab der ein Dokument als selbes Thema angesehen wird:
	private final static int RARE_WORDS_IN_DOC = 5;
	
	private static ArrayList<String> rareWords = new ArrayList<String>();
	

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
		// Seltene Wörter herausfinden und global speichern,
		// Artikel suchen die mehr als fünf dieser Wörter aufweisen,
		// bei Fund Abbruch und dessen topichash

		int rareRangeMax = (int) (Application.getNumberOfAllDocuments()*RARE_APPEARANCE);
		ArrayList<String> tokens = tokenizeAndRemoveStopWords(text);
		
		IndexSearcher searcher = Application.getSearcher();
		Query query2 = NumericRangeQuery.newLongRange("date",Search.getLowerBound(), Search.getUpperBound(), true, true);
		
		// Integer docId, int sameRareWordsCount
		Map<Integer, Integer> maybeSimilarDocs = new HashMap<Integer, Integer>();
		
		for(int i=0; i<tokens.size(); i++) {
			System.out.println("\n\""+tokens.get(i)+"\":");
			BooleanQuery booleanQuery = new BooleanQuery();
			try {
				Query query1 = new QueryParser(Version.LUCENE_46, "text",Application.getAnalyzer()).parse(tokens.get(i));
				booleanQuery.add(query1, BooleanClause.Occur.MUST);
				booleanQuery.add(query2, BooleanClause.Occur.MUST);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			try {
				TopDocs topDocs = searcher.search(booleanQuery, 1);
				System.out.println("Gefunden in "+topDocs.totalHits+" Dokumenten.");
				
				if(topDocs.totalHits <= rareRangeMax) {
					rareWords.add(tokens.get(i));
					
					ScoreDoc[] hits = topDocs.scoreDocs;
					for(int j=0; j<hits.length; j++) {
						int temp = 0;
						
						if(maybeSimilarDocs.get(hits[j].doc)!=null)
							temp = maybeSimilarDocs.get(hits[j].doc);
							
						
						if(++temp >= RARE_WORDS_IN_DOC) {
							System.out.println("Mehr als "+RARE_WORDS_IN_DOC+" seltene Worte in Dokument >>> Kein neues Thema.");
							return searcher.doc(hits[0].doc).get("topichash");
						}
						maybeSimilarDocs.put(hits[j].doc, temp);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

		return "";
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
	
	public static ArrayList<String> tokenizeAndRemoveStopWords(String input) {  
	    TokenStream tokenStream = new StandardTokenizer(
	            Version.LUCENE_46, new StringReader(input));
	    tokenStream = new StopFilter(Version.LUCENE_46, tokenStream, ((GermanAnalyzer)Application.getAnalyzer()).getStopwordSet());
//	    tokenStream = new PorterStemFilter(tokenStream);
	    
	    ArrayList<String> terms = new ArrayList<String>();
	    try {
	    	tokenStream.reset();
			while (tokenStream.incrementToken()) {
			    terms.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return terms;
	}
}