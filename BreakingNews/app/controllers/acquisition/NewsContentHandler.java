package controllers.acquisition;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class NewsContentHandler implements ContentHandler {
	
	private String currentValue;
	
	private String newsPortal;
	private String title;
	private String publicationDate;
	private String urlSource;
	private String urlPicture;
	private String text;
	
	private boolean isInItem = false;
	
	public NewsContentHandler() {
		System.out.println("NewsContentHandler()");
		newsPortal = "";
		title = "";
		publicationDate = "";
		urlSource = "";
		urlPicture = "";
		text = "";
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		
		if (localName.equals("item")) {
			isInItem = true;
		}
		
		// article picture-url:
		if (localName.equals("media:content") && isInItem) {
			urlPicture = attr.getValue("url");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if (localName.equals("item")) {
			isInItem = false;
		}
		
		// newsPortal:
		if (localName.equals("title") && !isInItem) {
			newsPortal = currentValue;
		}
		// article title:
		if (localName.equals("title") && isInItem) {
			title = currentValue;
		}
		// article date:
		if (localName.equals("pubDate") && isInItem) {
			publicationDate = currentValue;
		}
		// article url:
		if (localName.equals("guid") && isInItem) {
			urlSource = currentValue;
		}
		// article text:
		if (localName.equals("ExtractedText") && isInItem) {
			text = currentValue;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		System.out.println(newsPortal+" | "+title+" | "+publicationDate+" | "+urlSource+" | "+urlPicture+" | "+text);
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
