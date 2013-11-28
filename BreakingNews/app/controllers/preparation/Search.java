package controllers.preparation;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import models.Newsportal;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
import org.apache.lucene.store.FSDirectory;

public class Search {

	private static ScoreDoc lastDocNew;
	private static boolean endNew = false;
	private static ScoreDoc lastDocOld;
	private static boolean endOld = false;
	private final static String NEWTOPICQUERY = "1";
	private final static String OLDTOPICQUERY = "2";
	private static int hitsPerPage = 5;
	private static String indexPath = "index";
	private static int timeframe = 20000;
	private static IndexReader reader;

	public static IndexSearcher getSearcher() throws Exception {
		reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		return new IndexSearcher(reader);
	}
	
	public static int getUpperBound(){
		DateFormat df = new SimpleDateFormat("yyyymmdd");
		return Integer.parseInt(df.format(new Date()));
	}
	
	public static int getLowerBound(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, -timeframe);
		DateFormat df = new SimpleDateFormat("yyyymmdd");
		return Integer.parseInt(df.format(c.getTime()));
	}
	
	public static List<Document> getDocumentsNewTopic(int offset, String keyword) {
		String querystr = NEWTOPICQUERY;
		String queryfield = "isNew";
		return getResults(querystr, queryfield,  offset, endNew, lastDocNew, NEWTOPICQUERY, keyword);
	}

	public static List<Document> getDocumentsOldTopic(int offset) {

		String querystr = OLDTOPICQUERY;
		String queryfield = "isNew";
		return getResults(querystr, queryfield,  offset, endOld, lastDocOld, OLDTOPICQUERY,"");
	}
	
	public static List<Document> getSimilarDocuments(String topicHash) {

		String querystr = topicHash;
		String queryfield = "topicHash";
		return getResults(querystr, queryfield,  0, false, null, "","");
	}

	public static List<Newsportal> getNewsportalList() {
		String lastName ="";
		String currentName ="";
		Newsportal currentNewsportal = null;
		List<Newsportal> newsportalList = new ArrayList<Newsportal>();
		List<Document> documents = getAllResultsNewTopic();
		for (int i = 0; i < documents.size(); i++) {
			currentName = documents.get(i).get("newsportal"); 
			if (!currentName.equals(lastName)){
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
	
	public static List<Document> getAllResultsNewTopic(){
		try {
			List<Document> documents = new ArrayList<Document>();
			Sort sort = new Sort(new SortField("date", SortField.Type.STRING,true));
			ScoreDoc[] hits = null;
			IndexSearcher searcher = getSearcher();
			
			BooleanQuery booleanQuery = new BooleanQuery();
			Query query1 = new TermQuery(new Term("isNew", NEWTOPICQUERY));
			Query query2 = NumericRangeQuery.newIntRange("date", getLowerBound(), getUpperBound(), true, true);
			booleanQuery.add(query1, BooleanClause.Occur.MUST);
			booleanQuery.add(query2, BooleanClause.Occur.MUST);
			int count = searcher.search(booleanQuery,1).totalHits;
			hits = searcher.search(booleanQuery,count,sort).scoreDocs;
			
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				documents.add(d);
			}
			return documents;
		} catch(Exception e){
			return new ArrayList<Document>();
		}
	}
	
	public static List<Document> getResults(String querystr, String queryfield, int offset, boolean end, ScoreDoc lastDoc, String k, String keyword) {
		try {
			List<Document> documents = new ArrayList<Document>();
			ScoreDoc[] hits = null;
			Sort sort = new Sort(new SortField("date", SortField.Type.STRING,true));
			IndexSearcher searcher = getSearcher();

			BooleanQuery booleanQuery = new BooleanQuery();
			Query query1 = new TermQuery(new Term(queryfield, querystr));
			Query query2 = NumericRangeQuery.newIntRange("date", getLowerBound(), getUpperBound(), true, true);
			booleanQuery.add(query1, BooleanClause.Occur.MUST);
			booleanQuery.add(query2, BooleanClause.Occur.MUST);
			if (!keyword.equals("")){
				Query query3 = new TermQuery(new Term("text", keyword));
				booleanQuery.add(query3, BooleanClause.Occur.MUST);
			}

			if (offset == 0) {
				end = false;
				hits = searcher.search(booleanQuery, hitsPerPage, sort).scoreDocs;
			} else {
				if (end) {
					hits = null;
				} else {
					hits = searcher.searchAfter(lastDoc, booleanQuery, 3, sort).scoreDocs;
				}
			}
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
			reader.close();
			if (k == NEWTOPICQUERY) {
				lastDocNew = lastDoc;
				endNew = end;
			} else if (k == OLDTOPICQUERY) {
				lastDocOld = lastDoc;
				endOld = end;
			}
			return documents;
		} catch (Exception e) {
			return new ArrayList<Document>();
		}
	}
}
