package soulib.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**TODO 作りかけ*/
@Deprecated
public class AjaxLib extends DefaultHandler{
	
	public HashMap<String, String> map=new HashMap<String,String>();
	
	public static void a() throws ParserConfigurationException, SAXException, IOException{
		SAXParserFactory s=SAXParserFactory.newInstance();
		SAXParser p=s.newSAXParser();
		InputStream is=null;
		p.parse(is,new AjaxLib());
	}
}
