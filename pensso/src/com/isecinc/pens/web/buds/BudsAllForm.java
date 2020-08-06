package com.isecinc.pens.web.buds;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class BudsAllForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private BudsAllBean criteria = new BudsAllBean();
	private List<BudsAllBean> results;
	private BudsAllBean bean;
	private String pageName;
	private String subPageName;
	private String mode;
   
    
	public String getSubPageName() {
		return subPageName;
	}


	public void setSubPageName(String subPageName) {
		this.subPageName = subPageName;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}


	public BudsAllBean getCriteria() {
		return criteria;
	}


	public void setCriteria(BudsAllBean criteria) {
		this.criteria = criteria;
	}


	public List<BudsAllBean> getResults() {
		return results;
	}


	public void setResults(List<BudsAllBean> results) {
		this.results = results;
	}


	public BudsAllBean getBean() {
		return bean;
	}


	public void setBean(BudsAllBean bean) {
		this.bean = bean;
	}
 
	

	public String getPageName() {
		return pageName;
	}


	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		if(getBean() != null){
		   //getBean().setDispZeroStock("");
		   //getBean().setDispHaveQty("");
			/*if(getBean().getStockBean() != null){
			   getBean().getStockBean().setDispExpireSoon("");
			}*/
		}
		
	}
    
}
