package com.isecinc.pens.inf.bean;

import java.io.Serializable;

public class ImageFileBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 180024283927559158L;
	
	private String imageFileName;
	private String generateImageFileName;
	
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	public String getGenerateImageFileName() {
		return generateImageFileName;
	}
	public void setGenerateImageFileName(String generateImageFileName) {
		this.generateImageFileName = generateImageFileName;
	}
	
	
}
