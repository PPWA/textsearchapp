package controllers.preparation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import models.Newsportal;

import org.apache.lucene.document.Document;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Erzeugt aus den von der Klasse Search erhaltenen Ergebnissen eine Ausgabe im
 * JSON-Format und gibt sie an die aufrufende URL zur&uuml;ck.
 * 
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Preparation extends Controller {

	/**
	 * Verwendetes Datumfsformat in Lucene
	 */
//	private static SimpleDateFormat sdfD;
	/**
	 * Verwendetes Datumfsformat in JSON und Frontend
	 */
//	private static SimpleDateFormat sdfS;

	/**
	 * Wandelt das Datum wie es im Lucene-Index gespeichert ist in ein Format um, welches in JSON und damit im Frontend angezeigt wird.
	 * 
	 * @param LDate Umzuwandelndes Datum in Lucene-Schreibweise
	 * @return Das Publikationsdatum im Format DD. MMMM YYYY HH:mm.
	 */
	public static String LDateToJSDate(String LDate) {
		try {
			SimpleDateFormat sdfD = new SimpleDateFormat("yyyyMMddHHmm");
			SimpleDateFormat sdfS = new SimpleDateFormat("dd. MMMM yyyy HH:mm", Locale.GERMAN);
			return sdfS.format(sdfD.parse(LDate)) + " Uhr";
		} catch (Exception e) {
			System.out.println("Formatierung von Datum fehlerhaft.");
			System.out.println(LDate +" ############################################");
			e.printStackTrace();
			return "00.00.0000 00:00 Uhr";
		}
	}

	/**
	 * Pr&uuml;ft die GET-Parameter der aufgerufenen URL auf Plausibilit&auml;t,
	 * fragt die Klasse Search nach Artikeln mit neuen Themen ab und erzeugt aus
	 * deren Meta-Daten einen JSON-String.
	 * 
	 * @param offsetS
	 *            Flag, ob Liste neu beginnen oder an letzter Stelle fortgesetzt
	 *            werden soll
	 * @param keyword
	 *            Suchbegriff f&uuml;r Volltextsuche
	 * @return Eine HTTP-Response mit Status-Code 200, MIMETYPE text/json und
	 *         einem JSON-String, der die Meta-Daten der aktuellsten f&uuml;nf
	 *         Beitr&auml;ge mit neuem Thema enth&auml;lt
	 */
	public static Result getNewTopics(String offsetS, String keyword) {
		int offset;
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode articles = result.arrayNode();
		ObjectNode article;
		Document document;
		List<Document> documents;
		long timeStart = System.currentTimeMillis();

		try {
			offset = Integer.parseInt(offsetS);
		} catch (Exception e) {
			System.out.println("Fehlerhafter GET-Parameter für Offset.");
			return ok(index.render(""));
		}

		documents = Search.getDocumentsNewTopic(offset, keyword);
		System.out.println("JSON wird erzeugt ...");
		for (int i = 0; i < documents.size(); i++) {
			article = Json.newObject();
			document = documents.get(i);
			article.put("art_title", document.get("title"));
			article.put("art_teaser", document.get("teaser"));
			article.put("art_date", LDateToJSDate(document.get("date")));
			article.put("art_urlsource", document.get("urlsource"));
			article.put("art_urlpicture", document.get("urlpicture"));
			article.put("art_newportal", document.get("newsportal"));
			article.put("art_topichash", document.get("topichash"));
			articles.add(article);
		}
		String duration = String.valueOf((System.currentTimeMillis() - timeStart) / 1000.);
		System.out.println("JSON fertiggestellt! Gesamtzeit der Operation: " + duration + " Sekunden.");
		response.put("articles", articles);
		return ok(response);
	}

	/**
	 * Pr&uuml;ft die GET-Parameter der aufgerufenen URL auf Plausibilit&auml;t,
	 * fragt die Klasse Search nach Artikeln mit bereits bekannten Themen ab und
	 * erzeugt aus deren Meta-Daten einen JSON-String.
	 * 
	 * @param offsetS
	 *            Flag, ob Liste neu beginnen oder an letzter Stelle fortgesetzt
	 *            werden soll
	 * @param keyword
	 *            Suchbegriff f&uuml;r Volltextsuche
	 * @return Eine HTTP-Response mit Status-Code 200, MIMETYPE text/json und
	 *         einem JSON-String, der die Meta-Daten der aktuellsten f&uuml;nf
	 *         Beitr&auml;ge mit bereits bekannten Thema enth&auml;lt
	 */
	public static Result getOldTopics(String offsetS) {
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode articles = result.arrayNode();
		Document document;
		ObjectNode article;
		int offset;
		List<Document> documents;
		long timeStart = System.currentTimeMillis();

		try {
			offset = Integer.parseInt(offsetS);
		} catch (Exception e) {
			return ok(index.render("Fehlerhafter Parameter"));
		}
		
		documents = Search.getDocumentsOldTopic(offset);

		System.out.println("JSON wird erzeugt ...");
		for (int i = 0; i < documents.size(); i++) {
			article = Json.newObject();
			document = documents.get(i);
			article.put("art_title", document.get("title"));
			article.put("art_teaser", document.get("teaser"));
			article.put("art_date", LDateToJSDate(document.get("date")));
			article.put("art_urlsource", document.get("urlsource"));
			article.put("art_urlpicture", document.get("urlpicture"));
			article.put("art_newportal", document.get("newsportal"));
			article.put("art_topichash", document.get("topichash"));
			article.put("art_explanation", document.get("explanation"));
			articles.add(article);
		}
		String duration = String.valueOf((System.currentTimeMillis() - timeStart) / 1000.);
		System.out.println("JSON fertiggestellt! Gesamtzeit der Operation:  " + duration + " Sekunden");
		response.put("articles", articles);
		return ok(response);
	}

	/**
	 * Fragt die Klasse Search nach Artikeln ab, die einem bestimmten Thema
	 * zugeh&ouml;rig sind und erzeugt aus deren Meta-Daten einen JSON-String.
	 * 
	 * @param topichash
	 *            Identifier f&uuml;r das zu suchende Thema
	 * @return Eine HTTP-Response mit Status-Code 200, MIMETYPE text/json und
	 *         einem JSON-String, der die Meta-Daten der aktuellsten f&uuml;nf
	 *         Beitr&auml;ge des gesuchten Themas enth&auml;lt
	 */
	public static Result getSimilarArticles(String topicHash) {
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode articles = result.arrayNode();
		Document document;
		ObjectNode article;
		List<Document> documents = Search.getSimilarDocuments(topicHash);
		long timeStart = System.currentTimeMillis();
		
		System.out.println("JSON wird erzeugt ...");
		for (int i = 0; i < documents.size(); i++) {
			article = Json.newObject();
			document = documents.get(i);
			article.put("art_title", document.get("title"));
			article.put("art_teaser", document.get("teaser"));
			article.put("art_date", LDateToJSDate(document.get("date")));
			article.put("art_urlsource", document.get("urlsource"));
			article.put("art_urlpicture", document.get("urlpicture"));
			article.put("art_newportal", document.get("newsportal"));
			article.put("art_topichash", document.get("topichash"));
			articles.add(article);
		}
		String duration = String.valueOf((System.currentTimeMillis() - timeStart) / 1000.);
		System.out.println("JSON fertiggestellt! Gesamtzeit der Operation:  " + duration + " Sekunden");
		response.put("articles", articles);
		return ok(response);
	}

	/**
	 * Fragt die Klasse Search nach allen Newsportalen und die Anzahl der von
	 * ihnen jeweils publizierten Artikel mit neuen Themen ab.
	 * 
	 * @return Eine HTTP-Response mit Status-Code 200, MIMETYPE text/json und
	 *         einem JSON-String, der die Meta-Daten aller Newsportale und die
	 *         Anzahl ihrer Artikel mit neuem Thema enth&auml;lt.
	 */
	public static Result getNewsPortals() {
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode newsportals = result.arrayNode();
		ObjectNode newsportal;
		List<Newsportal> newsportalList = Search.getNewsportalList();
		long timeStart = System.currentTimeMillis();
		
		System.out.println("JSON wird erzeugt ...");
		for (int i = 0; i < newsportalList.size(); i++) {
			newsportal = Json.newObject();
			newsportal.put("np_name", newsportalList.get(i).getName());
			newsportal.put("np_count", newsportalList.get(i).getAnzahl());
			newsportals.add(newsportal);
		}
		String duration = String.valueOf((System.currentTimeMillis() - timeStart) / 1000.);
		System.out.println("JSON fertiggestellt! Gesamtzeit der Operation:  " + duration + " Sekunden");
		response.put("newsportals", newsportals);
		return ok(response);
	}
}
