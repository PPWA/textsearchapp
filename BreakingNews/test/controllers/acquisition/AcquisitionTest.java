package controllers.acquisition;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import play.test.Helpers;

public class AcquisitionTest {

	@Test
	public void testStartSearch() {
		Result result = Acquisition.startSearch();
		assertEquals("Sollte Status-Code 200 liefern.", 200, Helpers.status(result));
		
		int newArticles = Json.toJson(Helpers.contentAsString(result)).intValue();
		assertTrue("Result darf nicht negativ sein.", newArticles>=0);
		
		/*
		Helpers.running(Helpers.fakeApplication(), new Runnable() {
            public void run() {
            	Result result = Acquisition.startSearch();
            	
            	Result fakeGetResult = Helpers.route(Helpers.fakeRequest(Helpers.GET, "/start-search"));
                assertEquals("Das manuell erhaltene und das per GET-Request erhaltene Result sollten gleich sein.", result, fakeGetResult);
            }
        }); */
	}

	@Test
	public void testSearching() {
		assertTrue("Suche sollte nicht blockiert sein.", !Acquisition.isSearching());
		Acquisition.searching();
		assertTrue("Suche sollte nicht blockiert sein.", !Acquisition.isSearching());
	}

}
