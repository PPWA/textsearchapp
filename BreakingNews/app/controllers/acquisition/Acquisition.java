package controllers.acquisition;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.analysis.Analysis;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Sucht im Ordner "xmlFiles" nach neuen Artikeln im XML-Format, liest sie aus, l&ouml;scht sie aus dem Ordner
 * und gibt die relevanten Daten weiter an die Klasse Analysis.
 * Das Frontend bekommt zudem eine R&uuml;ckmeldung, ob und wie viele Artikel eingelesen wurden.
 * 
 * @author Christian Ochsenk&uuml;hn
 * @version 1.0
 */
public class Acquisition {
	
	private static final String DIR = "xmlFiles/";
	
	private static NewsContentHandler handl;
	private static String latestXMLPath = "";	// Currently not used.
												// Could be used to get new xml files when there are older (already read) files in the directory.
												// To use it: Use searchXMLFilesNewerThanPath() instead of searchNewXMLFiles()
	
	public Acquisition() { }
	
	/**
	 * Sucht nach neuen XML-Dateien im relevanten Ordner, liest diese - wenn vorhanden - aus, l&ouml;scht sie aus dem Ordner
	 * und gibt im Play-Result die Anzahl der eingelesenen Dateien zur&uuml;ck.
	 * @return Anzahl der eingelesenen Artikel (im JSON-Format als "new_art_count")
	 */
	public static Result startSearch() {
		ObjectNode response = Json.newObject();
//		StringBuffer buf = new StringBuffer(); // for testing
		int articleCount = 0;
		ArrayList<String> newXMLFiles = searchNewXMLFiles(DIR);
		
		if(newXMLFiles.isEmpty()) {
			System.out.println("\nAcquisition: No new XML files found.");
		} else {		
			for(int i=0; i<newXMLFiles.size(); i++) {
				handl = new NewsContentHandler();
				
				System.out.println("\nAcquisition: New File: "+newXMLFiles.get(i));
				if(!readXMLFile(DIR+newXMLFiles.get(i), handl))
					continue;
				
				while(!handl.hasStoppedReading()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
//				showMeWhatYouGot(handl); 	// for testing
				
				Calendar threeMonthsAgo = Calendar.getInstance();
				threeMonthsAgo.add(Calendar.MONTH, -3);
				if(handl.getPublicationDate().compareTo(threeMonthsAgo.getTime()) < 0) {
					System.out.println("Acquisition: Article is older than three months and will not be analysed.");
					deleteFile(DIR+newXMLFiles.get(i));
					continue;
				}
				
				String title = "Kein Titel";
				if(!handl.getTitle().equals(""))
					title = handl.getTitle();
				
				Analysis.addNewDocument(
						title,
						handl.getPublicationDate(),
						handl.getUrlSource(),
						handl.getUrlPicture(),
						handl.getText(),
						handl.getTeaser(),
						handl.getNewsPortal()); 
				
				articleCount++;
				deleteFile(DIR+newXMLFiles.get(i));
//				buf.append(handl.getXMLString()+"\n\n-----------------------------------------------\n\n\n");	// for testing
			}
//			System.out.println(buf.toString());	// for testing
		}
		response.put("new_art_count", articleCount);
		return Results.ok(response);
//		return Results.ok(buf.toString());	// for testing
	}
	
	/**
	 * Sucht nach XML-Dateien im vorgegebenen Ordner und gibt deren Dateinamen zur&uuml;ck.
	 * Dateien mit anderen Endungen werden aus dem Ordner entfernt.
	 * @param directory
	 * 			Relative Pfadangabe des zu durchsuchenden Ordners (ausgehend vom Programm-Ursprung)
	 * @return Name der gefundenen XML-Dateien als Strings in einer ArrayList
	 */
	private static ArrayList<String> searchNewXMLFiles(String directory) {
		ArrayList<String> newXMLFiles = new ArrayList<String>();
		
		File file = new File(directory);
		String[] xmlFiles = file.list();
		for(int i=0; i<xmlFiles.length; i++) {			
			if(xmlFiles[i].endsWith(".xml"))
				newXMLFiles.add(xmlFiles[i]);
			else {
				System.out.println("Acquisition: "+xmlFiles[i]+" is not a xml file.");
				deleteFile(directory+xmlFiles[i]);
			}
		}
		return newXMLFiles;
	}

	/**
	 * Aktuell nicht genutzt: Liest alle neuen XML-Dateien bis zur ersten alten Datei ein
	 * und merkt sich den Name der aktuellsten Datei.
	 * @param directory
	 * 			Relative Pfadangabe des zu durchsuchenden Ordners (ausgehend vom Programm-Ursprung)
	 * @return Name der gefundenen XML-Dateien als Strings in einer ArrayList
	 */
	private static ArrayList<String> searchXMLFilesNewerThanPath(String directory) {
		ArrayList<String> newXMLFiles = new ArrayList<String>();
		
		File file = new File(directory);
		String[] xmlFiles = file.list();
		for(int i=0; i<xmlFiles.length; i++) {
			if(xmlFiles[i] != latestXMLPath) {
				newXMLFiles.add(xmlFiles[i]);
				if((i+1<xmlFiles.length) && (xmlFiles[i+1] == latestXMLPath)) {
					latestXMLPath = xmlFiles[i];
					break;
				}
			} else {
				break;
			}
		}
		return newXMLFiles;
	}

	
	/**
	 * Liest XML-Datei aus und nutzt dazu den &uuml;bergebenen NewsContentHandler
	 * @param pathToFile
	 * 			Relativer Pfad der XML-Datei inkl. Dateiname (ausgehend vom Programm-Ursprung)
	 * @param handl
	 * 			NewsContentHandler, welcher das Auslesen &uuml;bernimmt und im Anschluss die gelesenen Informationen enth&auml;lt
	 */
	private static boolean readXMLFile(String pathToFile, NewsContentHandler handl) {
		try {
			long timeStart = System.currentTimeMillis();
			System.out.println("Acquisition: Start reading XML...");
			// Create reader
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			
			// Path to xml file:
			FileReader reader = new FileReader(pathToFile);
			InputSource inputSource = new InputSource(reader);
			inputSource.setEncoding("UTF-8");
			
			xmlReader.setContentHandler(handl);
			xmlReader.parse(inputSource);
			
			String duration = String.valueOf((System.currentTimeMillis() - timeStart) / 1000.);
			System.out.println("Acquisition: Succesfully read XML in "+duration+" seconds!");
			return true;
		} catch (SAXException e) {
			System.out.println("Acquisition: SAX-Error while parsing "+pathToFile);
			handl.setIsEndOfDocument(true);
			deleteFile(pathToFile);
		} catch (FileNotFoundException e) {
			System.out.println("Acquisition: Could not find "+pathToFile+"!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Acquisition: Input/Output-Error while parsing "+pathToFile);
		}
		return false;
	}
	
	/**
	 * L&ouml;scht die Datei des angegebenen Pfads
	 * @param pathToFile
	 * 			Relativer Pfad der Datei inkl. Dateiname (ausgehend vom Programm-Ursprung)
	 * @return True bei erfolgreicher L&ouml;schung, ansonsten false
	 */
	private static boolean deleteFile(String pathToFile) {
		File tempFile = new File(pathToFile);
		boolean deleteSuccess = tempFile.delete();
		if(deleteSuccess) System.out.println("Acquisition.java: Successfully deleted "+pathToFile);
		else System.out.println("Acquisition: Failed to delete "+pathToFile);
		
		return deleteSuccess;
	}
	
	/**
	 * F&uuml;r Testzwecke: Gibt alle vom NewsContentHandler eingelesenen Daten in der Console aus
	 * @param handl
	 */
	private static void showMeWhatYouGot(NewsContentHandler handl) {
		System.out.println("Title: "+handl.getTitle());
		System.out.println("PubDate: "+handl.getPublicationDate());
		System.out.println("Url: "+handl.getUrlSource());
		System.out.println("Pic: "+handl.getUrlPicture());
//		System.out.println("Text: "+handl.getText());
		System.out.println("Teaser: "+handl.getTeaser());
		System.out.println("Portal: "+handl.getNewsPortal());
	}
}
