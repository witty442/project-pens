package com.isecinc.pens.report.salesanalyst;

import java.io.Serializable;

/**
 * References
 * 
 * @author Atiz.b
 * @version $Id: References.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public class DisplayBean implements Serializable {

	private static final long serialVersionUID = 4133660991116726735L;
	/**
	 * Constructor
	 * 
	 * @param code
	 * @param key
	 * @param name
	 */
	public DisplayBean(int no ,String code, String key, String name) {
		this.code = code;
		this.name = name;
		this.key = key;
		this.no = no;
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("References[%s]-[%s] [%s]", getCode(), getKey(), getName());
	}

	private int no;
	
	/** CODE */
	private String code;

	/** KEY VALUE */
	private String key;

	/** NAME */
	private String name;

	
	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
