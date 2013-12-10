package controllers.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import controllers.Application;
import controllers.preparation.Search;

public class TestAlgo {
	
	// Prozentuales Vorkommen in allen Dokumenten, ab der ein Wort noch als selten gilt:
	private final static float RARE_APPEARANCE = (float) 0.15; // in percent
	
	// Anzahl der seltenen Wörter, ab der ein Dokument als selbes Thema angesehen wird:
	private final static int RARE_WORDS_IN_DOC = 5;
	
	private static ArrayList<String> rareWords = new ArrayList<String>();
	
	private static Similarity sim;
	public static ArrayList<Integer> freqs;
	
	public TestAlgo() {

	}
	
	/*public static String hasSimilarRareWords(String text) {
		int rareRangeMax = (int) (Application.getNumberOfAllDocuments()*RARE_APPEARANCE);
		
		ArrayList<String> tokens = tokenizeAndRemoveStopWords(text);
		int max = 0;
		int min = 0;
		
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
			
			sim = new DefaultSimilarity() {
				public float tf(float freq) {
					TestAlgo.freqs.add((int) freq);
					return super.tf(freq);
				}
			};
			searcher.setSimilarity(sim);
			freqs = new ArrayList<Integer>(); 
			
			try {
				TopDocs topDocs = searcher.search(booleanQuery, 1);
				System.out.println("Gefunden in "+topDocs.totalHits+" Dokumenten.");
				
				if(topDocs.totalHits <= rareRangeMax) {
					rareWords.add(tokens.get(i));
					for(int j=0; j<freqs.size(); j++) {
						System.out.println("In Dok"+(j+1)+": "+freqs.get(j)+" mal.");
					} 
					
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
				
				if(topDocs.totalHits > max)
					max = topDocs.totalHits;
				if(i==0 || topDocs.totalHits < min)
					min = topDocs.totalHits; 	
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

		return "";
	}
	

	public static void checkTF(String word) {
		Term term = new Term("text",word);
		IndexReader reader;
		reader = Application.getReader();
		
		try {
			DocsEnum docEnum = MultiFields.getTermDocsEnum(reader, MultiFields.getLiveDocs(reader), "text", term.bytes());
			int termFreq = 0;

			int doc = DocsEnum.NO_MORE_DOCS;
			int i=0;
			docEnum.nextDoc();
			System.out.println("Freq: "+docEnum.freq());
				while ((doc = docEnum.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
//				    termFreq += docEnum.freq();
					System.out.println("In Dok"+i+" ist "+word+" "+docEnum.freq()+" mal.");
					i++;
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	*//**
	 * Term-Frequenz aller Begriffe in allen Dokumenten
	 * @return
	 *//*
	public static String checkTermFrequencies() {
		TFIDFSimilarity tfSim = new DefaultSimilarity();
		IndexReader reader;
//		try {
			reader = Application.getReader();
		} catch (IOException e1) {
			System.out.println("Algorithm.java, checkTermTrequencies(): Error getting IndexReader.");
			return "";
		} 
		
		for (int docID=0; docID<Application.getNumberOfAllDocuments(); docID++) {      
	        try {
				TermsEnum termsEnum = MultiFields.getTerms(reader, "text").iterator(null);
				DocsEnum docsEnum = null;
 
				Terms vector = reader.getTermVector(docID, "text");
   
				try {
					termsEnum = vector.iterator(termsEnum);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				
				BytesRef bytesRef = null;
				while ((bytesRef = termsEnum.next()) != null) {
				    System.out.println("While-Aussen++");
					if (termsEnum.seekExact(bytesRef)) {
				    	String term = bytesRef.utf8ToString(); 
				      
				        float tf = 0;   
						docsEnum = termsEnum.docs(null, null, DocsEnum.FLAG_FREQS);
						while (docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
							tf =  tfSim.tf(docsEnum.freq());
							System.out.println(term+": "+tf);
						} 
				    } 
				}
			} catch (IOException e) {
				System.out.println("Algorithm.java, checkTermTrequencies(): IOException!");
				return "";
			} 
		}

		return null;
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

	
	public static void main(String[] args) {
		String teeeest = "Leopard, Leopard, Zebra, Tiger. Seit Jahren versuchen Designer ihr Glück mit Animal-Prints. In freier Wildbahn gab es diese Muster meist in der Damenmode zu bewundern. Bisher. Denn die wilde Mode-Safari hat sich in die aktuellen Herren-Kollektionen eingeschlichen – nicht bei schrillen Underground-Designern, sondern bei den Großen der Branche wie Burberry Prorsum oder Louis Vuitton. Wenn es um Mode mit exotischen Mustern geht, verhalten sich Männer meist scheu wie ein Zebra. Ausnahme: Rockstars. Typen wie Rod Stewart (68) oder Steven Tyler (65) von „Aerosmith“ tragen seit Jahrzehnten Tierfell-Optik – und kommen damit großartig an bei den Frauen.";
//		String teeeest = "Privatsphäre";
		
		TestAlgo.hasSimilarRareWords(teeeest);
//		TestAlgo.checkTF("Jahren");
//		TestAlgo.checkTermFrequencies();
	}*/

}
