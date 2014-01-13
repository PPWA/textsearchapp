package controllers.analysis;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class AnalysisTest {
	private String title, urlSource, urlPicture, text, teaser, newsPortal;
	private Date publicationDate;
	
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
		return str+(int)(Math.random()*9+1)+str;
	}

	@Test
	public void testAddNewDocument() {
		Analysis.addNewDocument(title, publicationDate, urlSource, urlPicture, text, teaser, newsPortal);
	}

}
