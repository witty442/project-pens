package com.isecinc.pens.web.adminconsole;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;


/**
 * Customer Form
 * 
 * @author Aneak.t
 * @version $Id: CustomerForm.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class AdminConsoleForm extends I_Form {

  private static final long serialVersionUID = 9066506758859129582L;

  String configInfo  ="";
  String configInfoTest ="";

  String q1 ;
  String q2 ;

  String resultQ1 = "";
  String resultQ2 ="";

  String eSQL = "";
  String eOutput = "";
  String resultBKDB = "";
  String resultClearDB = "";
  
  
public String getResultClearDB() {
	return resultClearDB;
}
public void setResultClearDB(String resultClearDB) {
	this.resultClearDB = resultClearDB;
}
public String getConfigInfo() {
	return configInfo;
}
public void setConfigInfo(String configInfo) {
	this.configInfo = configInfo;
}
public String getConfigInfoTest() {
	return configInfoTest;
}
public void setConfigInfoTest(String configInfoTest) {
	this.configInfoTest = configInfoTest;
}
public String getQ1() {
	return q1;
}
public void setQ1(String q1) {
	this.q1 = q1;
}
public String getQ2() {
	return q2;
}
public void setQ2(String q2) {
	this.q2 = q2;
}
public String getResultQ1() {
	return resultQ1;
}
public void setResultQ1(String resultQ1) {
	this.resultQ1 = resultQ1;
}
public String getResultQ2() {
	return resultQ2;
}
public void setResultQ2(String resultQ2) {
	this.resultQ2 = resultQ2;
}
public String geteSQL() {
	return eSQL;
}
public void seteSQL(String eSQL) {
	this.eSQL = eSQL;
}
public String geteOutput() {
	return eOutput;
}
public void seteOutput(String eOutput) {
	this.eOutput = eOutput;
}
public String getResultBKDB() {
	return resultBKDB;
}
public void setResultBKDB(String resultBKDB) {
	this.resultBKDB = resultBKDB;
}
  
  

}
