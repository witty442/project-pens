package com.isecinc.pens.db.backup;

public class DBBackUpConfig {
 
	private String field;
	private String type;
	private boolean nulls;
	private String key;
	private String defaults;
	
	
	public String getDefaults() {
		return defaults;
	}
	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isNulls() {
		return nulls;
	}
	public void setNulls(boolean nulls) {
		this.nulls = nulls;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
