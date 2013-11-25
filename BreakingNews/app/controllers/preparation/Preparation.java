package controllers.preparation;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Preparation extends Controller{

	public static Result getNewTopics(int offset) {
		return ok("new"+offset);
	}

	public static Result getOldTopics(int offset) {
		return ok("old" + offset);
	}
	
	public static Result getSimilarArticles(int art_id) {
		return ok("similar" + art_id);
	}
	
	public static Result getNewsPortals() {
		return ok("portals");
	}
}
