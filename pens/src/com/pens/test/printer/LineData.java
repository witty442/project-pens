package com.pens.test.printer;

public class LineData {

	private String type;
	private String data;
	private int diff;
	public LineData(String type,String data,int diff){
		this.type = type;
		this.data = data;
		this.diff = diff;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getDiff() {
		return diff;
	}
	public void setDiff(int diff) {
		this.diff = diff;
	}
	
	
}
