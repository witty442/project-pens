package com.isecinc.pens.web.db;

import com.isecinc.core.web.I_Form;

/**
 * ManualUpdateAddressForm
 * 
 * @author Witty.B
 * @version $Id: ManualUpdateAddressForm,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */

public class DBManagementForm extends I_Form {

	private static final long serialVersionUID = 9066506758859129582L;

	private DBManagementBean[] results = null;
	
	private int size;
    
	private String[] chkIndex;
	
	public String[] getChkIndex() {
		return chkIndex;
	}

	public void setChkIndex(String[] chkIndex) {
		this.chkIndex = chkIndex;
	}

	public DBManagementBean[] getResults() {
		return results;
	}

	public void setResults(DBManagementBean[] results) {
		this.results = results;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	
	

}
