package com.isecinc.pens.web.b2b;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.struts.upload.FormFile;

import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class B2BBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6601445561355694753L;
	private String dataType;
	
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
}
