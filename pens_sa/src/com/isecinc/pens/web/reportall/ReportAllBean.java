package com.isecinc.pens.web.reportall;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.isecinc.pens.web.boxno.BoxNoBean;
import com.isecinc.pens.web.projectc.ProjectCBean;
import com.isecinc.pens.web.stock.StockBean;
public class ReportAllBean implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8389174957413702703L;

   private ProjectCBean projectCBean;
   private StockBean stockBean;
   private BoxNoBean boxNoBean;
   
   
	public BoxNoBean getBoxNoBean() {
	return boxNoBean;
}

public void setBoxNoBean(BoxNoBean boxNoBean) {
	this.boxNoBean = boxNoBean;
}

	public ProjectCBean getProjectCBean() {
		return projectCBean;
	}
	
	public void setProjectCBean(ProjectCBean projectCBean) {
		this.projectCBean = projectCBean;
	}

	public StockBean getStockBean() {
		return stockBean;
	}

	public void setStockBean(StockBean stockBean) {
		this.stockBean = stockBean;
	}
   
	 
  
}
