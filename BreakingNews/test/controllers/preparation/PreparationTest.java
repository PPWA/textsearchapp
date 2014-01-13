package controllers.preparation;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;
import controllers.acquisition.Acquisition;

public class PreparationTest {

	@Test
	public void testGetNewTopics() {
		Result result = Preparation.getNewTopics("0", "");
		assertEquals("Sollte Status-Code 200 liefern.", 200, Helpers.status(result));
		
		JsonNode json = Json.toJson(Helpers.contentAsString(result));
		assertNotNull("Muss ein JSON zurückliefern.", json);
	}

	@Test
	public void testGetOldTopics() {
		Result result = Preparation.getOldTopics("0");
		assertEquals("Sollte Status-Code 200 liefern.", 200, Helpers.status(result));
		
		JsonNode json = Json.toJson(Helpers.contentAsString(result));
		assertNotNull("Muss ein JSON zurückliefern.", json);
	}

	@Test
	public void testGetSimilarArticles() {
		Result result = Preparation.getSimilarArticles("");
		assertEquals("Sollte Status-Code 200 liefern.", 200, Helpers.status(result));
		
		JsonNode json = Json.toJson(Helpers.contentAsString(result));
		assertNotNull("Muss ein JSON zurückliefern.", json);
	}

	@Test
	public void testGetNewsPortals() {
		Result result = Preparation.getNewsPortals();
		assertEquals("Sollte Status-Code 200 liefern.", 200, Helpers.status(result));
		
		JsonNode json = Json.toJson(Helpers.contentAsString(result));
		assertNotNull("Muss ein JSON zurückliefern.", json);
	}

}
