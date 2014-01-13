package controllers.analysis;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.junit.Before;
import org.junit.Test;

import controllers.preparation.Preparation;
import controllers.preparation.Search;

/**
 * Testet die Schnittstelle der Klasse Analysis
 * 
 * @author Christian Ochsenk&uuuml;hn
 *
 */
public class AnalysisTest {
	private static DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
	private String title, urlSource, urlPicture, text, teaser, newsPortal;
	private Date publicationDate;
	
	/**
	 * Initialisiert ein neues Dokument mit pseudo-zuf&auml;llig generierten Elementen.
	 * Beispiel eines Elements: "title<Zufallszahl>title"
	 */
	@Before
	public void initDocumentValues() {
		title = getRandomString("title");
		urlSource = getRandomString("urlSource");
		urlPicture = getRandomString("urlPicture");
		text = getRandomString("text");
		teaser = getRandomString("teaser");
		newsPortal = getRandomString("newsPortal");
		
		publicationDate = Calendar.getInstance().getTime();
	}
	
	private String getRandomString(String str) {
		return str+(int)(Math.random()*1000+1)+str;
	}

	/**
	 * F&uuml;gt ein neues Dokument zur Analyse hinzu und pr&uuml;ft, ob alle Elemente des Dokuments auch richtig abgespeichert
	 * und wieder ausgelesen werden.
	 */
	@Test
	public void testAddNewDocument() {
		Analysis.addNewDocument(title, publicationDate, urlSource, urlPicture, text, teaser, newsPortal);
		
		/* Das eingelesene Dokument wird entweder ein neues oder ein altes Thema aufweisen */
		Document newDoc = Search.getDocumentsNewTopic(0, "").get(0);
		Document oldDoc = Search.getDocumentsOldTopic(0).get(0);

		assertTrue("Eingelesener Titel muss ausgelesenem entsprechen.", newDoc.get("title").equals(title) || oldDoc.get("title").equals(title));
		assertTrue("Eingelesene Url muss ausgelesener entsprechen.", newDoc.get("urlsource").equals(urlSource) || oldDoc.get("urlsource").equals(urlSource));
		assertTrue("Eingelesene Url muss ausgelesener entsprechen.", newDoc.get("urlpicture").equals(urlPicture) || oldDoc.get("urlpicture").equals(urlPicture));
		assertTrue("Eingelesener Teaser muss ausgelesenem entsprechen.", newDoc.get("teaser").equals(teaser) || oldDoc.get("teaser").equals(teaser));
		assertTrue("Eingelesenes Newsportal muss ausgelesenem entsprechen.", newDoc.get("newsportal").equals(newsPortal) || oldDoc.get("newsportal").equals(newsPortal));
		
		String date = String.valueOf(Long.parseLong(df.format(publicationDate)));
		assertTrue("Eingelesenes Datum muss ausgelesenem entsprechen.", newDoc.get("date").equals(date) || oldDoc.get("date").equals(date));
	}

}
