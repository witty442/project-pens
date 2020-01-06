package com.isecinc.pens.web.importall;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

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
public class ImportAllForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private ImportAllBean criteria = new ImportAllBean();
	private List<ImportAllBean> results;
	private ImportAllBean bean;
	private String pageName;
    private FormFile dataFile;
    private FormFile dataFile2;
    private FormFile dataFile3;
    
    
    
	public FormFile getDataFile3() {
		return dataFile3;
	}


	public void setDataFile3(FormFile dataFile3) {
		this.dataFile3 = dataFile3;
	}


	public FormFile getDataFile() {
		return dataFile;
	}


	public void setDataFile(FormFile dataFile) {
		this.dataFile = dataFile;
	}


	public FormFile getDataFile2() {
		return dataFile2;
	}


	public void setDataFile2(FormFile dataFile2) {
		this.dataFile2 = dataFile2;
	}


	public ImportAllBean getCriteria() {
		return criteria;
	}


	public void setCriteria(ImportAllBean criteria) {
		this.criteria = criteria;
	}


	public List<ImportAllBean> getResults() {
		return results;
	}


	public void setResults(List<ImportAllBean> results) {
		this.results = results;
	}


	public ImportAllBean getBean() {
		return bean;
	}


	public void setBean(ImportAllBean bean) {
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