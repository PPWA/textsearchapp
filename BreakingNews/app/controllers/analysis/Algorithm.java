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
import org.apache.lucene.analysis.en.PorterStemFilter;
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
 * @author Christian Ochsenk&uuml;hn
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Algorithm {

	// Prozentuales Vorkommen in allen Dokumenten, ab der ein Wort noch als selten gilt:
	private final static float RARE_APPEARANCE = (float) 0.05; // = 5%
	
	// Anzahl der seltenen Wörter, ab der ein Dokument als selbes Thema angesehen wird:
	private final static int RARE_WORDS_IN_DOC = 15;
	
	private static ArrayList<String> rareWords = new ArrayList<String>();
	
	/**
	 * Zun&auml;chst werden Artikel gesucht, in deren Titel mindestens ein Wort aus
	 * dem Titel des zu vergleichenden Artikels auftauchen. Das Dokument mit dem
	 * besten per Vector Space Model festgestellten Scoring wird weiter
	 * untersucht. Es wird gepr&uuml;ft, wie viel W&ouml;rter aus dem Titel des zu
	 * vergleichenden Artikels auch im Titel des vermeintlich &auml;hnlichen Artikels
	 * enthalten sind. Ergibt diese Pr&uuml;fung mehr als 2 identische W&ouml;rter, wird kein neues
	 * Thema angenommen und der TopicHash des &auml;hnlichen Artikels extrahiert.
	 * <br><br>
	 * Fall 1: Gleiche Themen werden als Unterschiedliche erkannt<br>
	 * H&auml;ufigkeit: Wahrscheinlich<br>
	 * Folge: Kein Problem, da sp&auml;tere Funktionen dies noch genauer &uuml;berprüfen
	 * <br><br>
	 * Fall 2: Unterschiedliche Themen als Gleiche erkannt<br>
	 * H&auml;ufigkeit: gering<br>
	 * Folge: Neues Thema geht unter<br>
	 * @author Sebastian Mandel
	 * @param queryTitle
	 *            Titel des Nachrichtenartikels, der auf ein neues Thema hin
	 *            untersucht werden soll
	 * @return TopicHash eines Artikels mit &auml;hnlichem Sachverhalt, sofern ein
	 *         solcher detektiert wird. Ansonsten Leerstring.
	 */
	public static String hasSimilarTitle(String queryTitle) {
		String topicHash = "";
		ScoreDoc[] hits = null;
		IndexSearcher searcher = Application.createSearcher();
		Query query1;
		Query query2;
		String docTitle;
		StringTokenizer queryTokenizer;
		StringTokenizer docTokenizer;
		String currentQueryToken;
		int i = 0;

		try {
			//Erzeugung und Durchführung der Suchanfrage 
			BooleanQuery booleanQuery = new BooleanQuery();
			query1 = new QueryParser(Version.LUCENE_46, "title", Application.getAnalyzer()).parse(queryTitle);
			query2 = NumericRangeQuery.newLongRange("date", Search.getLowerBound(), Search.getUpperBound(), true, true);
			booleanQuery.add(query1, BooleanClause.Occur.MUST);
			booleanQuery.add(query2, BooleanClause.Occur.MUST);
			hits = searcher.search(booleanQuery, 1).scoreDocs;
			
			//Wird ähnlicher Titel gefunden, Prüfung wieviel Wörter in beiden Titeln vorhanden sind
			if (hits.length == 1) {
				docTitle = searcher.doc(hits[0].doc).get("title");
				queryTokenizer = new StringTokenizer(queryTitle);

				while (queryTokenizer.hasMoreTokens()) {
					currentQueryToken = queryTokenizer.nextToken();
					docTokenizer = new StringTokenizer(docTitle);
					while (docTokenizer.hasMoreTokens()) {
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
			searcher.getIndexReader().close();
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
		if(rareRangeMax<1)
			rareRangeMax = 1;
//		System.out.println("rareRangeMax: "+rareRangeMax);
		ArrayList<String> tokens = tokenizeAndRemoveStopWords(text);
		
		IndexSearcher searcher = Application.createSearcher();
		Query query2 = NumericRangeQuery.newLongRange("date",Search.getLowerBound(), Search.getUpperBound(), true, true);
		
		// Integer docId, int sameRareWordsCount
		Map<Integer, Integer> maybeSimilarDocs = new HashMap<Integer, Integer>();
		
		for(int i=0; i<tokens.size(); i++) {
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
				if(topDocs.totalHits>0) {
					topDocs = searcher.search(booleanQuery, topDocs.totalHits);
				
					if(topDocs.totalHits <= rareRangeMax) {
	//				if(true) {
						rareWords.add(tokens.get(i));
	//					System.out.println("Seltenes Wort: "+tokens.get(i));
						
						ScoreDoc[] hits = topDocs.scoreDocs;
						for(int j=0; j<hits.length; j++) {
							int temp = 0;
							
							if(maybeSimilarDocs.get(hits[j].doc)!=null)
								temp = maybeSimilarDocs.get(hits[j].doc);
								
							
							if(++temp >= RARE_WORDS_IN_DOC) {
								System.out.println("More than "+RARE_WORDS_IN_DOC+" rare words in document >>> Old Topic");
								String hash = searcher.doc(hits[j].doc).get("topichash");
								System.out.println("Similar with :"+searcher.doc(hits[j].doc).get("title"));
								searcher.getIndexReader().close();
								return hash;
							}
//							System.out.println("Doc #"+hits[j].doc+" contains "+temp+" rare words.");
							maybeSimilarDocs.put(hits[j].doc, temp);
						}
					} else {
//						System.out.println("Nicht selten, weil "+topDocs.totalHits+" totalHits > Range "+rareRangeMax);
					}
				} else {
//					System.out.println("In keinem Dokument gefunden...");
				}
				
			} catch (IOException | NullPointerException e) {
				System.out.println("hasSimilarRareWords fehlgeschlagen da Index nicht vorhanden.");
			} 
		}
		try {
			searcher.getIndexReader().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("No similar topic found.");
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
	    tokenStream = new PorterStemFilter(tokenStream);
	    
	    ArrayList<String> terms = new ArrayList<String>();
	    try {
	    	tokenStream.reset();
			while (tokenStream.incrementToken()) {
			    String tok = tokenStream.getAttribute(CharTermAttribute.class).toString();
			    try {  
			      Double.parseDouble(tok);
			      continue;
			    } catch(NumberFormatException nfe)  {  
			        // do nothing
			    }
			    
			    boolean addFlag = true;
			    // Avoid storing the same token more than 1 time:
				for(int i=0; i<terms.size(); i++) {
					if(tok.equals(terms.get(i))) {
						addFlag = false;
						break;
					}
				}
				
				if(addFlag)
					terms.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return terms;
	}
}
