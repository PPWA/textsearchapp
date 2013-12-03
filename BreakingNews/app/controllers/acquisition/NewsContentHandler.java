package controllers.acquisition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
	private StringBuffer textBuf;
	
	private boolean isInItem = false;
	private boolean isEndOfDocument = false;
	
	private boolean isExtractedText = false;
	
	public NewsContentHandler() {
		newsPortal = "";
		title = "";
		publicationDate = "";
		urlSource = "";
		urlPicture = "";
		textBuf = new StringBuffer();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		StringBuffer buf = new StringBuffer();
		for (int i = start; i < start + length; i++) {
		    switch (ch[i]) {
		    case '\\':
		    	buf.append("\\\\");
		    	break;
//		    case '"':
//				buf.append("\\\"");
//				break;
		    case '\n':
//		    	buf.append("\\n");
		    	buf.append(" ");
				break;
		    case '\r':
				buf.append("\\r");
				break;
		    case '\t':
				buf.append("\\t");
				break;
		    default:
				buf.append(ch[i]);
				break;
		    }
		}
		
//		currentValue = new String(ch, start, length);
		currentValue = buf.toString();
		
		if(isExtractedText)
			textBuf.append(buf);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		
		if (localName.equals("item")) {
			isInItem = true;
		}
		if (localName.equals("enclosure") && isInItem) {	// picture-url
			urlPicture = attr.getValue("url");
		} 
		if (localName.equals("ExtractedText") && isInItem) {
			isExtractedText = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if (localName.equals("item")) {
			isInItem = false;
		}
		
		if (localName.equals("title") && !isInItem) {	// newsPortal-title
			newsPortal = currentValue;
		}
		if (localName.equals("title") && isInItem) {	// article-title
			title = currentValue;
		}
		if (localName.equals("pubDate") && isInItem) {
			publicationDate = currentValue;
		}
//		if (localName.equals("guid") && isInItem) {
		if (localName.equals("link") && isInItem) {
			urlSource = currentValue;
		}
		if (localName.equals("ExtractedText") && isInItem) {
			isExtractedText = false;
		} 
	}
	
	@Override
	public void endDocument() throws SAXException {
		isEndOfDocument = true;
	}
	
	
	public String getXMLString() {
		return ("Portal: "+newsPortal+"\nTitel: "+title+"\nDatum: "+getPublicationDate().toString()+"\nSrc: "+urlSource+"\nPic: "+urlPicture+"\n\nText: "+textBuf.toString());
	}
	
	public boolean hasStoppedReading() {
		return isEndOfDocument;
	}
	
	public String getNewsPortal() {
		return newsPortal;
	}

	public String getTitle() {
		return title;
	}

	public Date getPublicationDate() {
		Date pubDate;
		try {
			// example: Sun, 06 Oct 2013 20:58:57 +0200
			pubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(publicationDate);
		} catch (ParseException e) {
			System.out.println("NewsContentHandler.java: Could not parse date ("+publicationDate+")");
			try {
				// example: Sun, 06 Oct 2013 20:58:57 GMT
				pubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).parse(publicationDate);
				System.out.println("NewsContentHandler.java: Next try parsing date succeeded.");
			} catch (ParseException e1) {
				System.out.println("NewsContentHandler.java: Could not parse date, again! Using current date.");
				pubDate = new Date();
			}
		}
		return pubDate;
	}

	public String getUrlSource() {
		return urlSource;
	}

	public String getUrlPicture() {
		return urlPicture;
	}

	public String getText() {
		return textBuf.toString();
	}
	

	@Override
	public void endPrefixMapping(String arg0) throws SAXException { }

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException { }

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException { }

	@Override
	public void setDocumentLocator(Locator arg0) { }

	@Override
	public void skippedEntity(String arg0) throws SAXException { }

	@Override
	public void startDocument() throws SAXException { }

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException { }

}
