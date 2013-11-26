package controllers.acquisition;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import play.mvc.Result;

public class Acquisition {
	
	public Acquisition() {
		// TODO Auto-generated constructor stub
	}
	
	public static Result startSearch() {
		return null;
	}
	
	private void readXMLFile(String pathToFile) {
		try {
			// Create reader
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			
			// Path to xml file:
			FileReader reader = new FileReader(pathToFile);
			InputSource inputSource = new InputSource(reader);
			
			xmlReader.setContentHandler(new NewsContentHandler());
			xmlReader.parse(inputSource);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// for testing:
/*	public static void main(String[] args) {
		Acquisition ac = new Acquisition();
		ac.readXMLFile("test.xml");
	} */
}
