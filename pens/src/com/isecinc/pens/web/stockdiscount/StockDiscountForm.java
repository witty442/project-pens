package com.isecinc.pens.web.stockdiscount;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.StockDiscount;
import com.isecinc.pens.bean.StockDiscountLine;
import com.isecinc.pens.bean.StockLine;
import com.isecinc.pens.bean.StockReturn;
import com.isecinc.pens.bean.StockReturnLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class StockDiscountForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private StockDiscountCriteria criteria = new StockDiscountCriteria();

	private int curPage;
	private int totalRow;
	private int totalPage;
	
	private List<StockDiscount> results;
	private List<StockDiscountLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	
	public String getLineNoDeleteArray() {
		return lineNoDeleteArray;
	}

	public void setLineNoDeleteArray(String lineNoDeleteArray) {
		this.lineNoDeleteArray = lineNoDeleteArray;
	}

	public String getDeletedId() {
		return deletedId;
	}

	public void setDeletedId(String deletedId) {
		this.deletedId = deletedId;
	}

	public StockDiscountCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(StockDiscountCriteria criteria) {
		this.criteria = criteria;
	}

	public List<StockDiscount> getResults() {
		return results;
	}

	public void setResults(List<StockDiscount> results) {
		this.results = results;
	}

	public StockDiscount getBean() {
		return criteria.getBean();
	}

	public void setBean(StockDiscount moveOrder) {
		criteria.setBean(moveOrder);
	}

	
	public List<StockDiscountLine> getLines() {
		return lines;
	}

	public void setLines(List<StockDiscountLine> lines) {
		this.lines = lines;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if(getBean() != null){
			getBean().setHaveStock("");
		}
	}
}
