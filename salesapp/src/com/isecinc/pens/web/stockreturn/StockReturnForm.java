package com.isecinc.pens.web.stockreturn;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Stock;
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
public class StockReturnForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private StockReturnCriteria criteria = new StockReturnCriteria();

	private int curPage;
	private int totalRow;
	private int totalPage;
	
	
	private List<StockReturn> results;
	private List<StockReturnLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	@SuppressWarnings("unchecked")
	public StockReturnForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new StockLine();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<StockLine>(), factory);
		
	}
	
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

	public StockReturnCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(StockReturnCriteria criteria) {
		this.criteria = criteria;
	}

	public List<StockReturn> getResults() {
		return results;
	}

	public void setResults(List<StockReturn> results) {
		this.results = results;
	}

	public StockReturn getBean() {
		return criteria.getBean();
	}

	public void setBean(StockReturn moveOrder) {
		criteria.setBean(moveOrder);
	}

	
	public List<StockReturnLine> getLines() {
		return lines;
	}

	public void setLines(List<StockReturnLine> lines) {
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
