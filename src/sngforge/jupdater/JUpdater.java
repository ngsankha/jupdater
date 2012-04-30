/*
 * JUpdater - A simple library to check whether a new update for a program is available
 * 
 *  Copyright (C) 2012, Sankha Narayan Guria
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sngforge.jupdater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Main class of JUpdater
 * 
 * @author Sankha Narayan Guria (sankha93@gmail.com)
 */
public class JUpdater {

	private URL url;
	private File file;
	
	/*
	 * Constructs a JUpdater object with a local config and a remote config file reference
	 * 
	 * @param local the local file path
	 * @param remote the remote URL of the update config file
	 */
	public JUpdater(String local,String remote) throws MalformedURLException{
		url=new URL(remote);
		file=new File(local);
	}
	
	/*
	 * Constructs a JUpdater object with a local config and a remote config file reference
	 * 
	 * @param local the local file path
	 * @param remote the remote URL of the update config file
	 */
	public JUpdater(File local,URL remote){
		file=local;
		url=remote;
	}
	
	/*
	 * Gets the version of the program that exists on the server
	 * 
	 * @return the version string of the copy on server
	 */
	public String getServerVersion() throws Exception{
		String ver="";
		URLConnection uc=url.openConnection();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom=db.parse(uc.getInputStream());
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("version");
		if(nl != null && nl.getLength() > 0) {
			Element el=(Element)nl.item(0);
			ver=el.getFirstChild().getTextContent();
		}
		return ver;
	}
	
	/*
	 * Gets the version of the program that exists on the local machine
	 * 
	 * @return the version string of the copy on local machine
	 */
	public String getLocalVersion()throws Exception{
		String ver="";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom=db.parse(file);
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("version");
		if(nl != null && nl.getLength() > 0) {
			Element el=(Element)nl.item(0);
			ver=el.getFirstChild().getTextContent();
		}
		return ver;
	}
	
	/*
	 * Automatically checks if an update needs to be performed
	 * 
	 * @return true if update is available, false otherwise
	 */
	public boolean isUpdateRequired() throws Exception{
        String s1 = normalisedVersion(getServerVersion());
        String s2 = normalisedVersion(getLocalVersion());
        int cmp = s1.compareTo(s2);
        if(cmp>0)
        	return true;
        else
        	return false;
    }

    private static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    private static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
    
    /*
	 * Gets the description of the current update available
	 * 
	 * @return the description of the currently available update
	 */
    public String getUpdateDetails() throws ParserConfigurationException, SAXException, IOException{
    	String det="";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom=db.parse(file);
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("details");
		if(nl != null && nl.getLength() > 0) {
			Element el=(Element)nl.item(0);
			det=el.getFirstChild().getTextContent();
		}
		return det;
    }
    
    /*
	 * Gets the URL of the current update available
	 * 
	 * @return the url of the currently available update
	 */
	public URL getUpdateURL() throws ParserConfigurationException, SAXException, IOException{
		String url="";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom=db.parse(file);
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("url");
		if(nl != null && nl.getLength() > 0) {
			Element el=(Element)nl.item(0);
			url=el.getFirstChild().getTextContent();
		}
		return new URL(url);
	}
}
