package util;

//Read XML
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlReadUtil {
	private static Document doc;

	public XmlReadUtil(String strfile) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(strfile));
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public String doRead(String Parent, String child) {
		try {

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList listOfParam = doc.getElementsByTagName(Parent);
			for (int s = 0; s < listOfParam.getLength(); s++) {
				Node dbtype = listOfParam.item(s);
				if (dbtype.getNodeType() == Node.ELEMENT_NODE) {
					Element dbparamelement = (Element) dbtype;
					NodeList listconnpool = dbparamelement.getElementsByTagName(child);

					Element connpoolelement = (Element) listconnpool.item(0);
					NodeList listchildconnpool = connpoolelement.getChildNodes();

					return ((Node) listchildconnpool.item(0)).getNodeValue().trim();

				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return "";

	}
}
