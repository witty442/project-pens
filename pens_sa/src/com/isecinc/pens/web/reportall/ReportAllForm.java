package com.isecinc.pens.web.reportall;

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
public class ReportAllForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private ReportAllBean criteria = new ReportAllBean();
	private List<ReportAllBean> results;
	private ReportAllBean bean;
	private String pageName;
   
    
	public ReportAllBean getCriteria() {
		return criteria;
	}


	public void setCriteria(ReportAllBean criteria) {
		this.criteria = criteria;
	}


	public List<ReportAllBean> getResults() {
		return results;
	}


	public void setResults(List<ReportAllBean> results) {
		this.results = results;
	}


	public ReportAllBean getBean() {
		return bean;
	}


	public void setBean(ReportAllBean bean) {
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
			if(getBean().getStockBean() != null){
			   getBean().getStockBean().setDispExpireSoon("");
			}
		}
		
	}
    
}
