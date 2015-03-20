package com.isecinc.pens.dataimports.bean;

import java.sql.ResultSet;

import com.isecinc.pens.bean.Contact;

public class IContactBean extends Contact {

	private static final long serialVersionUID = 3401948905499145686L;

	/**
	 * Default Constructor
	 */
	public IContactBean() {
		//
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public IContactBean(ResultSet rst) throws Exception {
		setId(rst.getInt("ICONTACT_ID"));
		setImported(rst.getString("IMPORTED"));
	}

	private String imported;
	private String importedDetail;

	public String getImported() {
		return imported;
	}
	public void setImported(String imported) {
		this.imported = imported;
	}
	public String getImportedDetail() {
		return importedDetail;
	}
	public void setImportedDetail(String importedDetail) {
		this.importedDetail = importedDetail;
	}

}
