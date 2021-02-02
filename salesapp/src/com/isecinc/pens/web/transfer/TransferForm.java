package com.isecinc.pens.web.transfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.TransferBean;

/**
 * Transfer Form
 * 
 * @author Witty
 * @version $Id: TransferForm.java,v 1.0 09/01/2019 00:00:00  $
 * 
 */
public class TransferForm extends I_Form {


	/**
	 * 
	 */
	private static final long serialVersionUID = 197385728851742476L;

	private TransferBean bean = new TransferBean();
	private TransferBean beanCriteria = new TransferBean();
	private TransferBean summary = new TransferBean();
	private List<TransferBean> results = null;
	private List<TransferBean> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	
	public TransferBean getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(TransferBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}

	public TransferBean getSummary() {
		return summary;
	}

	public void setSummary(TransferBean summary) {
		this.summary = summary;
	}

	public List<TransferBean> getLines() {
		return lines;
	}

	public void setLines(List<TransferBean> lines) {
		this.lines = lines;
	}

	public List<TransferBean> getResults() {
		return results;
	}

	public void setResults(List<TransferBean> results) {
		this.results = results;
	}

	public String getLineNoDeleteArray() {
		return lineNoDeleteArray;
	}

	public void setLineNoDeleteArray(String lineNoDeleteArray) {
		this.lineNoDeleteArray = lineNoDeleteArray;
	}

	public String getDeletedId() {
		return deletedId;
	}

	public void setDeletedId(String deletedId) {
		this.deletedId = deletedId;
	}
    
	public TransferBean getBean() {
		return bean;
	}

	public void setBean(TransferBean bean) {
		this.bean = bean;
	}
}
