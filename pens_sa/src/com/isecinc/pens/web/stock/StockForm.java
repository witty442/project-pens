package com.isecinc.pens.web.stock;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class StockForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<StockBean> results = new ArrayList<StockBean>();
    
    private String pageName;
    private StockBean bean ;
    private StockBean beanCriteria ;
    
	public List<StockBean> getResults() {
		return results;
	}
	public void setResults(List<StockBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public StockBean getBean() {
		return bean;
	}
	public void setBean(StockBean bean) {
		this.bean = bean;
	}
	public StockBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StockBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	  if(getBean() !=null){
		  getBean().setDispRequestDate("");
		  getBean().setDispLastUpdate("");
		  getBean().setDispOrderOnly("");
		  getBean().setDispExpired("");
		  getBean().setDispExpireSoon("");
	  }
		
	}
    
   
}
