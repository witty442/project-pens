package com.isecinc.pens.report.printsticker;

import java.io.Serializable;

/**
 * PrintSticker
 * 
 * @author Aneak.t
 * @version $Id: PrintSticker.java,v 1.0 19/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class PrintLabel implements Serializable{

	private static final long serialVersionUID = 4558582892501673045L;
	
	// Column 1
	private String name1;
	private String code1;
	private String address1;
	private String address11;
	private String address12;
	private String name2;
	private String code2;
	private String address2;
	private String address21;
	private String address22;
	
	
	public String getAddress11() {
		return address11;
	}
	public void setAddress11(String address11) {
		this.address11 = address11;
	}
	public String getAddress22() {
		return address22;
	}
	public void setAddress22(String address22) {
		this.address22 = address22;
	}
	public String getAddress12() {
		return address12;
	}
	public void setAddress12(String address12) {
		this.address12 = address12;
	}
	public String getAddress21() {
		return address21;
	}
	public void setAddress21(String address21) {
		this.address21 = address21;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	
	public String getCode1() {
		return code1;
	}
	public void setCode1(String code1) {
		this.code1 = code1;
	}
	public String getCode2() {
		return code2;
	}
	public void setCode2(String code2) {
		this.code2 = code2;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	
	
}
