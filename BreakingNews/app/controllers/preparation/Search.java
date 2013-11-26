package controllers.preparation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Search {

	private static ScoreDoc lastDocNew;
	private static boolean endNew = false;
	private static ScoreDoc lastDocOld;
	private static boolean endOld = false;

	private final static String NEWTOPICQUERY = "1";
	private final static String OLDTOPICQUERY = "2";
	private static int hitsPerPage = 5;
	private static String indexPath = "index";

	public static List<Document> getDocumentsNew(int offset) {

		String querystr = NEWTOPICQUERY;
		String queryfield = "isNew";
		return getResults(querystr, queryfield, offset, endNew, lastDocNew, NEWTOPICQUERY);
	}

	public static List<Document> getDocumentsOld(int offset) {

		String querystr = OLDTOPICQUERY;
		String queryfield = "isNew";
		return getResults(querystr, queryfield, offset, endOld, lastDocOld, OLDTOPICQUERY);
	}
	
	public static List<Document> getSimilarDocuments(String topicHash) {

		String querystr = topicHash;
		String queryfield = "topicHash";
		return getResults(querystr, queryfield, 0, false, null, "");
	}

	public static List<Document> getNewsPortals() {

		String querystr = NEWTOPICQUERY;
		String queryfield = "";
		return getResults(querystr, queryfield, 0, false, lastDocNew, NEWTOPICQUERY);
	}

	public static List<Document> getResults(String querystr, String queryfield, int offset, boolean end, ScoreDoc lastDoc, String k) {
		try {
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
			List<Document> documents = new ArrayList<Document>();
			ScoreDoc[] hits = null;
			Sort sort = new Sort(new SortField("date", SortField.Type.STRING,true));
			IndexReader reader = null;
			reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Query q = new QueryParser(Version.LUCENE_46, queryfield,analyzer).parse(querystr);

			if (offset == 0) {
				end = false;
				hits = searcher.search(q, hitsPerPage, sort).scoreDocs;
			} else {
				if (end) {
					hits = null;
				} else {
					hits = searcher.searchAfter(lastDoc, q, 3, sort).scoreDocs;
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
