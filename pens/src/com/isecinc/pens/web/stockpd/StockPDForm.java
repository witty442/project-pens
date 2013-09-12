package com.isecinc.pens.web.stockpd;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.StockPD;
import com.isecinc.pens.bean.StockPDLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class StockPDForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private StockPDCriteria criteria = new StockPDCriteria();

	private List<StockPD> results;
	private List<StockPDLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	@SuppressWarnings("unchecked")
	public StockPDForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new StockPDLine();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<StockPDLine>(), factory);
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

	public StockPDCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(StockPDCriteria criteria) {
		this.criteria = criteria;
	}

	public List<StockPD> getResults() {
		return results;
	}

	public void setResults(List<StockPD> results) {
		this.results = results;
	}

	public StockPD getStockPD() {
		return criteria.getStockPD();
	}

	public void setStockPD(StockPD stockPD) {
		criteria.setStockPD(stockPD);
	}

	public List<StockPDLine> getLines() {
		return lines;
	}

	public void setLines(List<StockPDLine> lines) {
		this.lines = lines;
	}
    
	
}
