package com.isecinc.pens.inf.manager.process;

import java.io.Serializable;
import java.util.List;

import com.isecinc.pens.bean.ImageFileBean;

public class ExportBean implements Serializable{
	
private static final long serialVersionUID = -2440587676203384074L;

private StringBuffer dataExportBuff;
private List<ImageFileBean> imageFileList;

public StringBuffer getDataExportBuff() {
	return dataExportBuff;
}
public void setDataExportBuff(StringBuffer dataExportBuff) {
	this.dataExportBuff = dataExportBuff;
}
public List<ImageFileBean> getImageFileList() {
	return imageFileList;
}
public void setImageFileList(List<ImageFileBean> imageFileList) {
	this.imageFileList = imageFileList;
}
  
  
  
}
