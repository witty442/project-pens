package com.pens.test.printer;

import java.util.ArrayList;
import java.util.List;

public class PageData {

	private int page;
	private List<LineData> data = new ArrayList<LineData>();;
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<LineData> getData() {
		return data;
	}
	public void setData(List<LineData> data) {
		this.data = data;
	}
	
	
	
}
