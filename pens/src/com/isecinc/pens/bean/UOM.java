package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;

/**
 * UOM
 * 
 * @author Aneak.t
 * @version $Id: UOM.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class UOM extends I_PO implements Serializable {

	public static final String ADMIN = "AD";
	public static final String TT = "TT";
	public static final String VAN = "VAN";
	public static final String DD = "DD";

	private static final long serialVersionUID = -5712094858581763895L;

	/**
	 * Default Constructor
	 */
	public UOM() {

	}

	/**
	 * Default Constructor
	 * 
	 * @param id
	 * @param code
	 * @param name
	 */
	public UOM(String id, String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public UOM(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getString("UOM_ID").trim());
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setIsActive(rst.getString("ISACTIVE").trim());

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("UOM[%s] [%s]-[%s]", getId(), getCode(), getName());
	}

	private String id;
	private String code;
	private String name;
	private String isActive;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

}
