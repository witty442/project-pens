package com.isecinc.pens.bean;

import java.io.Serializable;

/**
 * @author WITTY
 *
 */
public class ColumnBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8930321549965743474L;
	private String columnName;
	private String columnType;
	private String columnValue;
	private String defaultValue;
	private int textPosition;
	private String externalFunction;
	private String key;
    private String action;
    private int startPosition;
    private int endPosition;
	private String fillSymbol;
	private String roleAction;
	private String requireField;
	private String validateFunc;
	
	
	public String getValidateFunc() {
		return validateFunc;
	}
	public void setValidateFunc(String validateFunc) {
		this.validateFunc = validateFunc;
	}
	
	public String getRequireField() {
		return requireField;
	}
	public void setRequireField(String requireField) {
		this.requireField = requireField;
	}
	public String getRoleAction() {
		return roleAction;
	}
	public void setRoleAction(String roleAction) {
		this.roleAction = roleAction;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	public String getFillSymbol() {
		return fillSymbol;
	}
	public void setFillSymbol(String fillSymbol) {
		this.fillSymbol = fillSymbol;
	}
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public int getEndPosition() {
		return endPosition;
	}
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getExternalFunction() {
		return externalFunction;
	}
	public void setExternalFunction(String externalFunction) {
		this.externalFunction = externalFunction;
	}

	public int getTextPosition() {
		return textPosition;
	}
	public void setTextPosition(int textPosition) {
		this.textPosition = textPosition;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	
	
	
}
