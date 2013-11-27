package controllers.preparation;

import java.util.List;

import models.Newsportal;

import org.apache.lucene.document.Document;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Allgemeine Aufbereitungsklasse
 * @author Sebastian Mandel
 * @version 1.0
 */
public class Preparation extends Controller {

	/**
	 * Geld auf Konto einzahlen.
	 * <p>
	 * Wenn vorher <code> getKontoStand() = x </code>
	 * und <code> betrag >=0 </code>,
	 * dann danach <code> getKontoStand() = x + betrag </code>
	 * @param betrag positive Zahl, der einzuzahlende Betrag
	 * @throws ArgumentNegativ wenn betrag negativ
	 */
	public static Result getNewTopics(String offsetS) {
		int offset;
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode articles = result.arrayNode();
		ObjectNode article;
		Document document;
		
		try{
			offset = Integer.parseInt(offsetS);
		}
		catch (Exception e)
		{
			return ok(index.render("Fehlerhafter Parameter"));
		}
		
		List<Document> documents = Search.getDocumentsNewTopic(offset);
		for (int i = 0; i < documents.size(); i++) {
			article = Json.newObject();
			document = documents.get(i);
			article.put("isNew", document.get("isNew"));
			article.put("art_id", document.get("id"));
			article.put("art_title", document.get("title"));
			article.put("art_teaser", document.get("teaser"));
			article.put("art_date", document.get("date"));
			article.put("art_urlsource", document.get("urlsource"));
			article.put("art_picture", document.get("picture"));
			article.put("art_newportal", document.get("newsportal"));
			article.put("art_topicHash", document.get("topicHash"));
			article.put("art_explanation", document.get("explanation"));
			articles.add(article);
		}
		response.put("articles", articles);
		return ok(response);
	}

	public static Result getOldTopics(String offsetS) {
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode articles = result.arrayNode();
		Document document;
		ObjectNode article; 
		int offset;
		
		try{
			offset = Integer.parseInt(offsetS);
		}
		catch (Exception e)
		{
			return ok(index.render("Fehlerhafter Parameter"));
		}
		
		List<Document> documents = Search.getDocumentsOldTopic(offset);

		for (int i = 0; i < documents.size(); i++) {
			article = Json.newObject();
			document = documents.get(i);
			article.put("isNew", document.get("isNew"));
			article.put("art_id", document.get("id"));
			article.put("art_title", document.get("title"));
			article.put("art_teaser", document.get("teaser"));
			article.put("art_date", document.get("date"));
			article.put("art_urlsource", document.get("urlsource"));
			article.put("art_picture", document.get("picture"));
			article.put("art_newportal", document.get("newsportal"));
			article.put("art_topicHash", document.get("topicHash"));
			articles.add(article);
		}
		response.put("articles", articles);
		return ok(response);
	}

	public static Result getSimilarArticles(String topicHash) {
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode articles = result.arrayNode();
		Document document;
		ObjectNode article;
		List<Document> documents = Search.getSimilarDocuments(topicHash);
		for (int i = 0; i < documents.size(); i++) {
			article = Json.newObject();
			document = documents.get(i);
			article.put("art_id", document.get("id"));
			article.put("art_title", document.get("title"));
			article.put("art_teaser", document.get("teaser"));
			article.put("art_date", document.get("date"));
			article.put("art_urlsource", document.get("urlsource"));
			article.put("art_picture", document.get("picture"));
			article.put("art_newportal", document.get("newsportal"));
			article.put("art_topichash", document.get("topicHash"));
			articles.add(article);
		}
		response.put("articles", articles);
		return ok(response);
	}

	public static Result getNewsPortals() {
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ArrayNode newsportals = result.arrayNode();
		ObjectNode newsportal;
		List<Newsportal> newsportalList = Search.getNewsportalList();

		for (int i = 0; i < newsportalList.size(); i++) {
			newsportal = Json.newObject();
			newsportal.put("np_name", newsportalList.get(i).getName());
			newsportal.put("np_count", newsportalList.get(i).getAnzahl());
			newsportals.add(newsportal);
		}
		response.put("newsportals", newsportals);
		return ok(response);
	}
}
