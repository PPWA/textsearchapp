package controllers.acquisition;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import controllers.analysis.Analysis;
import play.mvc.Result;
import play.mvc.Results;

public class Acquisition {
	
	private static final String DIR = "xmlFiles/";
	
	private static Analysis analysis;
	private static NewsContentHandler handl;
	private static String latestXMLPath = "";
	
	public Acquisition() {

	}
	
	public static Result startSearch() {
		ArrayList<String> newXMLFiles = searchNewXMLFiles(DIR);
		if(newXMLFiles.isEmpty()) {
			System.out.println("\nAcquisition.java: No new XML files found.");
			return Results.noContent();
		} else {
			analysis = new Analysis();
			StringBuffer buf = new StringBuffer();
			
			for(int i=0; i<newXMLFiles.size(); i++) {
				handl = new NewsContentHandler();
				
				System.out.println("\nAcquisition.java: New File: "+newXMLFiles.get(i));
				readXMLFile(DIR+newXMLFiles.get(i), handl);
				while(!handl.hasStoppedReading()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				analysis.addNewDocument(
						handl.getTitle(),
						handl.getPublicationDate(),
						handl.getUrlSource(),
						handl.getUrlPicture(),
						handl.getText(),
						handl.getNewsPortal());
				buf.append(handl.getXMLString()+"\n\n-----------------------------------------------\n\n\n");
			}

			return Results.ok(buf.toString());
		}
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
	
	private static ArrayList<String> searchNewXMLFiles(String directory) {
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
	
	private static void readXMLFile(String pathToFile, NewsContentHandler handl) {
		try {
			long timeStart = System.currentTimeMillis();
			System.out.println("Acquisition.java: Start reading XML...");
			// Create reader
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			
			// Path to xml file:
			FileReader reader = new FileReader(pathToFile);
			InputSource inputSource = new InputSource(reader);
			inputSource.setEncoding("UTF-8");
			
			xmlReader.setContentHandler(handl);
			xmlReader.parse(inputSource);
			
			String duration = String.valueOf((System.currentTimeMillis() - timeStart) / 1000.);
			System.out.println("Acquisition.java: Succesfully read XML in "+duration+" seconds!");
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
