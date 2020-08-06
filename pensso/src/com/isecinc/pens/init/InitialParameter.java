package com.isecinc.pens.init;

import java.sql.Connection;

import javax.servlet.ServletContext;

import util.BeanParameter;
import util.XmlReadUtil;

import com.isecinc.core.init.I_Initial;

/**
 * Initial Parameter
 * 
 * @author Aneak.t
 * @version $Id: InitialParameter.java,v 1.0 10/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InitialParameter extends I_Initial {

	private String realPath;
	private String reportPath;
	private String logoPath;

	/**
	 * Initial with
	 */
	public void init(String initParameter, ServletContext context) {
		// initParameter = getInitParameter("parameterfile")
		// context = getServletContext()
		try {
			if (initParameter != null) {
				realPath = context.getRealPath(initParameter);
				reportPath = context.getRealPath("/reports/");
				logoPath = context.getRealPath("/images/");
			}
			if (BeanParameter.getFileName().equals("")) {
				BeanParameter.setFileName(initParameter);
				setParam();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {}
	}

	/**
	 * Initial with connection.
	 */
	public void init(Connection conn) {

	}

	private void setParam() {
		XmlReadUtil xmlread = new XmlReadUtil(realPath);
		BeanParameter.setDatabase(xmlread.doRead("parameter", "database"));
		BeanParameter.setContextname(xmlread.doRead("parameter", "contextName"));
		// BeanParameter.setReportPath(xmlread.doRead("parameter", "reportPath"));
		// BeanParameter.setLogo(xmlread.doRead("parameter", "logo"));
		BeanParameter.setReportPath(reportPath + (reportPath.indexOf("/") != -1 ? "/" : "\\"));
		BeanParameter.setLogo(logoPath + (logoPath.indexOf("/") != -1 ? "/" : "\\")
				+ xmlread.doRead("parameter", "logo"));
		BeanParameter.setPensTaxNo(xmlread.doRead("parameter", "penstaxno"));

		// modifier
		BeanParameter.setModifierDiscount(xmlread.doRead("modifier", "type-discount"));
		BeanParameter.setModifierPricebreakheader(xmlread.doRead("modifier", "type-pricebreakheader"));
		BeanParameter.setModifierPromotiongood(xmlread.doRead("modifier", "type-promotiongood"));
		BeanParameter.setModifierSurchart(xmlread.doRead("modifier", "type-surchart"));
		BeanParameter.setModifierItemCategory(xmlread.doRead("modifier", "attr-itemCategory"));
		BeanParameter.setModifierItemNumber(xmlread.doRead("modifier", "attr-itemNumber"));
		BeanParameter.setBreakTypePoint(xmlread.doRead("modifier", "breakType-point"));
		BeanParameter.setBreakTypeRecurring(xmlread.doRead("modifier", "breakType-recurring"));
		BeanParameter.setAppMethodAMT(xmlread.doRead("modifier", "appMethod-amt"));
		BeanParameter.setAppMethodPercent(xmlread.doRead("modifier", "appMethod-percent"));
		BeanParameter.setAppMethodNewPrice(xmlread.doRead("modifier", "appMethod-newprice"));

		// qualifier
		BeanParameter.setQualifierContext(xmlread.doRead("qualifier", "context"));
		BeanParameter.setQualifierType(xmlread.doRead("qualifier", "type"));
		BeanParameter.setQualifierVAN(xmlread.doRead("qualifier", "van"));
		BeanParameter.setQualifierTT(xmlread.doRead("qualifier", "tt"));
		BeanParameter.setQualifierDD(xmlread.doRead("qualifier", "dd"));

		BeanParameter.setLineQualifierContext(xmlread.doRead("linequalifier", "context"));
		BeanParameter.setLineQualifierType(xmlread.doRead("linequalifier", "type"));
	}

	/**
	 * Initial
	 */
	public void init() {

	}
}
