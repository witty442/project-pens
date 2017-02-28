package com.isecinc.pens.report.salesanalyst;

public class ConfigBean implements Comparable {
 String name;
 String value;
 String dispText;
 
 public ConfigBean(String name,String value,String dispText) {
	 this.name = name;
	 this.value = value;
	 this.dispText = dispText;
 }
 
public String getName() {
	return name; 
}
public void setName(String name) {
	this.name = name;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public String getDispText() {
	return dispText;
}
public void setDispText(String dispText) {
	this.dispText = dispText;
}

public int compareTo(Object o) {
	if (!(o instanceof ConfigBean))
		throw new ClassCastException();
	
	ConfigBean config = (ConfigBean)o;
	
	return value.compareTo(config.getValue());
}
 
}
