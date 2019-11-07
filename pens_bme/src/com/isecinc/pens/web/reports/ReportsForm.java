package com.isecinc.pens.web.reports;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReportsForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private ReportsBean criteria = new ReportsBean();
	private List<ReportsBean> results;
	private ReportsBean bean;
	private String pageName;
   
    
	public ReportsBean getCriteria() {
		return criteria;
	}


	public void setCriteria(ReportsBean criteria) {
		this.criteria = criteria;
	}


	public List<ReportsBean> getResults() {
		return results;
	}


	public void setResults(List<ReportsBean> results) {
		this.results = results;
	}


	public ReportsBean getBean() {
		return bean;
	}


	public void setBean(ReportsBean bean) {
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
		   getBean().setDispZeroStock("");
		   getBean().setDispHaveQty("");
		}
		
	}
    
}
