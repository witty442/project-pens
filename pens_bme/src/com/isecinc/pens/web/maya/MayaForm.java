package com.isecinc.pens.web.maya;

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
public class MayaForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

    private String page;
    private String summaryType;
    private MayaBean bean;
    private MayaBean beanCriteria;
	private List<MayaBean> results;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
	public MayaBean getBean() {
		return bean;
	}
	public void setBean(MayaBean bean) {
		this.bean = bean;
	}
	public MayaBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(MayaBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public List<MayaBean> getResults() {
		return results;
	}
	public void setResults(List<MayaBean> results) {
		this.results = results;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		
	}
    
}
