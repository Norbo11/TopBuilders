package com.github.norbo11.topbuilders.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Language {
	public static void load(String language) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    //factory.setValidating(true);
	    //factory.setIgnoringElementContentWhitespace(true);
	    try {
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        File file = new File("test.xml");
	        Document doc = builder.parse(file);
	        // Do something with the document here.
	    } catch (ParserConfigurationException e) {
	    } catch (SAXException e) {
	    } catch (IOException e) { 
	    }
	}
}
