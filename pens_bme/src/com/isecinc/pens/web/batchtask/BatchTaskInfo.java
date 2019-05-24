package com.isecinc.pens.web.batchtask;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BatchTaskInfo implements Serializable {

private static final long serialVersionUID = 1L;
	private String param;
	private String buttonName ;
	private String paramName;
	private String paramType;
	private String paramValue;
	private String paramFormFileValue;
    private String paramLabel;
    private String paramValid;
    private String validateScript;
    private String description;
    private String devInfo;
	private Map<String, BatchTaskInfo> paramMap;
	private List<BatchTaskInfo> paramList;
	
	
	public String getDevInfo() {
		return devInfo;
	}
	public void setDevInfo(String devInfo) {
		this.devInfo = devInfo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<BatchTaskInfo> getParamList() {
		return paramList;
	}
	public void setParamList(List<BatchTaskInfo> paramList) {
		this.paramList = paramList;
	}
	public String getParamFormFileValue() {
		return paramFormFileValue;
	}
	public void setParamFormFileValue(String paramFormFileValue) {
		this.paramFormFileValue = paramFormFileValue;
	}
	public String getValidateScript() {
		return validateScript;
	}
	public void setValidateScript(String validateScript) {
		this.validateScript = validateScript;
	}
	public String getParamValid() {
		return paramValid;
	}
	public void setParamValid(String paramValid) {
		this.paramValid = paramValid;
	}
	public String getParamLabel() {
		return paramLabel;
	}
	public void setParamLabel(String paramLabel) {
		this.paramLabel = paramLabel;
	}
	public Map<String, BatchTaskInfo> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, BatchTaskInfo> paramMap) {
		this.paramMap = paramMap;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
    
	
}
