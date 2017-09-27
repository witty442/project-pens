package com.isecinc.pens.bean;

import java.util.Map;

public class TaskStoreBean {
  private boolean valid;
  private Map<String,String> storeMap;
  private Map<String,String> storeErrorMap;
  
  
public Map<String, String> getStoreErrorMap() {
	return storeErrorMap;
}
public void setStoreErrorMap(Map<String, String> storeErrorMap) {
	this.storeErrorMap = storeErrorMap;
}
public boolean isValid() {
	return valid;
}
public void setValid(boolean valid) {
	this.valid = valid;
}
public Map<String, String> getStoreMap() {
	return storeMap;
}
public void setStoreMap(Map<String, String> storeMap) {
	this.storeMap = storeMap;
}
  
  
}
