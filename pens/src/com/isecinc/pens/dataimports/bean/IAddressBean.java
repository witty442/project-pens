package com.isecinc.pens.dataimports.bean;

import java.sql.ResultSet;

import com.isecinc.pens.bean.Address;

public class IAddressBean extends Address {

	private static final long serialVersionUID = 3902976173819733314L;

	/**
	 * Default Constructor
	 */
	public IAddressBean() {
		//
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public IAddressBean(ResultSet rst) throws Exception {
		setId(rst.getInt("IADDRESS_ID"));
		setImported(rst.getString("IMPORTED"));
	}

	private String billToShipTo;
	private String provinceName;
	private String imported;
	private String importedDetail;

	public String getBillToShipTo() {
		return billToShipTo;
	}
	public void setBillToShipTo(String billToShipTo) {
		this.billToShipTo = billToShipTo;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
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
