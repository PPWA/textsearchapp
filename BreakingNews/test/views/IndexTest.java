package views;

import static org.junit.Assert.*;

import org.junit.Test;

import play.libs.Json;
import play.mvc.Content;
import play.mvc.HandlerRef;
import play.mvc.Result;
import play.test.Helpers;

public class IndexTest {

	@Test
	public void renderTemplate() {
	  Content html = views.html.index.render("Early Bird News");
	  assertEquals("Sollte Html ausliefern", "text/html", Helpers.contentType(html));
	}

}
