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
	}

	@Test
	public void testSearching() {
		assertTrue("Suche sollte nicht blockiert sein.", !Acquisition.isSearching());
		Acquisition.searching();
		assertTrue("Suche sollte nicht blockiert sein.", !Acquisition.isSearching());
	}

}
