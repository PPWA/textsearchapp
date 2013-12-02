package controllers;

import java.util.List;

import play.data.Form;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * Klasse ist der Standard-Einstiegspunkt von Play!
 * 
 * @author Automatisch generiert
 * @version 1.0
 */
public class Application extends Controller {

	/**
	 * Gibt die Startseite zur&uuml;ck, sobald eine Client diese aufruft.
	 * 
	 * @return Eine HTTP-Response mit Status-Code 200, dem MIMETYPE text/html
	 *         und des HTML-Codes f&uuml;r die Startseite
	 */
	public static Result index() {
		return ok(index.render("Home"));
	}

}
