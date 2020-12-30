package com.isecinc.pens.web.customernissin;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.CustomerNissin;

/**
 * Customer Form
 * 
 * @author Aneak.t
 * @version $Id: CustomerForm.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class CustomerNissinForm extends I_Form implements Serializable{

	private static final long serialVersionUID = 9066506758859129582L;

	private CustomerNissinCriteria criteria = new CustomerNissinCriteria();

	private CustomerNissin[] results = null;
	
	private int curPage;
	private int totalRow;
	private int totalPage;


	public CustomerNissinCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CustomerNissinCriteria criteria) {
		this.criteria = criteria;
	}

	public CustomerNissin[] getResults() {
		return results;
	}

	public void setResults(CustomerNissin[] results) {
		this.results = results;
	}

	public CustomerNissin getCustomer() {
		return criteria.getCustomer();
	}

	public void setCustomer(CustomerNissin customer) {
		criteria.setCustomer(customer);
	}


	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		  if(getCustomer() !=null){
		  }
	}

}
