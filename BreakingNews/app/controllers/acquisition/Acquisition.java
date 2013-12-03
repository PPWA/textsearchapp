package controllers.acquisition;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import controllers.analysis.Analysis;
import play.mvc.Result;

public class Acquisition {
	
	private Analysis analysis;
	private static NewsContentHandler handl;
	private static String latestXMLPath;
	
	public Acquisition() {
		analysis = new Analysis();
		latestXMLPath = "";
	}
	
	public static Result startSearch() {
		handl = new NewsContentHandler();
		readXMLFile("RSS1916018878.xml", handl);
		while(!handl.hasStoppedReading()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return play.mvc.Results.ok(handl.getXMLString());
	}
	
/*	public static void startSearchTEST() {
		handl = new NewsContentHandler();
		readXMLFile("RSS1916018878.xml", handl);
		while(!handl.hasStoppedReading()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Getting back result!");
	} */
	
	private static String[] searchNewXMLFiles(String directory) {
		String[] newXMLFiles = new String[10];
		int j = 0;
		
		File file = new File(directory);
		String[] xmlFiles = file.list();
		for(int i=0; i<xmlFiles.length; i++) {
			if(xmlFiles[i] != latestXMLPath) {
				newXMLFiles[j++] = xmlFiles[i];
			} else {
				break;
			}
		}
		
		if(j==0) System.out.println("No new XML files found.");
		return newXMLFiles;
	}
	
	private static void readXMLFile(String pathToFile, NewsContentHandler handl) {
		try {
			System.out.println("Start reading XML...\n");
			// Create reader
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			
			// Path to xml file:
			FileReader reader = new FileReader(pathToFile);
			InputSource inputSource = new InputSource(reader);
			inputSource.setEncoding("UTF-8");
			
			xmlReader.setContentHandler(handl);
			xmlReader.parse(inputSource);
			System.out.println("\nSuccesfully stopped reading XML!");
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
