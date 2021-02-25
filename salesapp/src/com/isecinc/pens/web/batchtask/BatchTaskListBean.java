package com.isecinc.pens.web.batchtask;

import java.io.Serializable;
import java.util.List;

public class BatchTaskListBean implements Serializable{
	
 /**
	 * 
	 */
private static final long serialVersionUID = 3959954038212708379L;

private String value;
private String disp;

private String listBoxName;
private List<BatchTaskListBean> listBoxData;

public BatchTaskListBean(String value,String disp){
	this.value =value;
	this.disp = disp;
}
public BatchTaskListBean(){
}
public String getListBoxName() {
	return listBoxName;
}
public void setListBoxName(String listBoxName) {
	this.listBoxName = listBoxName;
}
public List<BatchTaskListBean> getListBoxData() {
	return listBoxData;
}
public void setListBoxData(List<BatchTaskListBean> listBoxData) {
	this.listBoxData = listBoxData;
}

public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public String getDisp() {
	return disp;
}
public void setDisp(String disp) {
	this.disp = disp;
}
 
 
}
