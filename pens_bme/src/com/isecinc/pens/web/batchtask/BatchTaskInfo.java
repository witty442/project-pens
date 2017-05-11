package com.isecinc.pens.web.batchtask;

import java.io.Serializable;
import java.util.Map;

public class BatchTaskInfo implements Serializable {

private static final long serialVersionUID = 1L;
	private String param;
	private String buttonName ;
	private String paramName;
	private String paramType;
	private String paramValue;
    private String paramLabel;
    private String paramValid;
	private Map<String, BatchTaskInfo> paramMap;
    
	
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
