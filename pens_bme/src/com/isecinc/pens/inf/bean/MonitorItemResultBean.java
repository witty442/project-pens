package com.isecinc.pens.inf.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author WITTY
 *
 */
public class MonitorItemResultBean implements Serializable{
   /**
	 * 
	 */
   private static final long serialVersionUID = -5543198782059403439L;
   private int row;
   private   BigDecimal monitorItemId;
   private   String status ;
   private   String msg ;
   private String columnHeadTable;
   
   
public String getColumnHeadTable() {
	return columnHeadTable;
}
public void setColumnHeadTable(String columnHeadTable) {
	this.columnHeadTable = columnHeadTable;
}
public int getRow() {
	return row;
}
public void setRow(int row) {
	this.row = row;
}
public BigDecimal getMonitorItemId() {
	return monitorItemId;
}
public void setMonitorItemId(BigDecimal monitorItemId) {
	this.monitorItemId = monitorItemId;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getMsg() {
	return msg;
}
public void setMsg(String msg) {
	this.msg = msg;
}
   
   
  
}
