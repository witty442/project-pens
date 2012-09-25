package com.isecinc.pens.dataimports.bean;

import java.sql.ResultSet;

import com.isecinc.pens.bean.MemberProduct;

public class IMemberProductBean extends MemberProduct {

	private static final long serialVersionUID = -2027307398384958580L;

	/**
	 * Default Constructor
	 */
	public IMemberProductBean() {
		//
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public IMemberProductBean(ResultSet rst) throws Exception {
		setId(rst.getInt("IMEMBER_PRODUCT_ID"));
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
