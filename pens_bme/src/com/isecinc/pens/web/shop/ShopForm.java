package com.isecinc.pens.web.shop;

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
public class ShopForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

    private String page;
    private String summaryType;
    private ShopBean bean;
    private ShopBean beanCriteria;
	private List<ShopBean> results;
	private ShopBean summary;
	
	
	public ShopBean getSummary() {
		return summary;
	}

	public void setSummary(ShopBean summary) {
		this.summary = summary;
	}

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
	public ShopBean getBean() {
		return bean;
	}
	public void setBean(ShopBean bean) {
		this.bean = bean;
	}
	public ShopBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ShopBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public List<ShopBean> getResults() {
		return results;
	}
	public void setResults(List<ShopBean> results) {
		this.results = results;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if(getBean() != null){
			getBean().setBmeProductOnly("");
		}
	}
    
}
