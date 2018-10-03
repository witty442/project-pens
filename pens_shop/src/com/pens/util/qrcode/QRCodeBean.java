package com.pens.util.qrcode;

import java.io.Serializable;
import java.util.List;

public class QRCodeBean implements Serializable{
	
  /**
	 * 
	 */
  private static final long serialVersionUID = 4446398027457597977L;
  private int seq;
  private String name;
  private String value;
  private String type;
  private String desc;
  private List<QRCodeBean> subItemList;
  
  
public List<QRCodeBean> getSubItemList() {
	return subItemList;
}
public void setSubItemList(List<QRCodeBean> subItemList) {
	this.subItemList = subItemList;
}
public int getSeq() {
	return seq;
}
public void setSeq(int seq) {
	this.seq = seq;
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
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getDesc() {
	return desc;
}
public void setDesc(String desc) {
	this.desc = desc;
}

  
}
