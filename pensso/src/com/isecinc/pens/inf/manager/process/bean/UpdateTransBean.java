package com.isecinc.pens.inf.manager.process.bean;

import java.io.Serializable;

public class UpdateTransBean implements Serializable{

  private static final long serialVersionUID = -8305459573793524577L;
  private String tableName;
  private String tableKey;
  private String tableLineStr;
  
    
	public String getTableName() {
	return tableName;
}
public void setTableName(String tableName) {
	this.tableName = tableName;
}
	public String getTableKey() {
		return tableKey;
	}
	public void setTableKey(String tableKey) {
		this.tableKey = tableKey;
	}
	public String getTableLineStr() {
		return tableLineStr;
	}
	public void setTableLineStr(String tableLineStr) {
		this.tableLineStr = tableLineStr;
	}
  
  
}
