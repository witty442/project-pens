package com.isecinc.pens.web.moveorder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.OrderLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MoveOrderForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private MoveOrderCriteria criteria = new MoveOrderCriteria();

	private List<MoveOrder> results;
	private List<MoveOrderLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	@SuppressWarnings("unchecked")
	public MoveOrderForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new MoveOrderLine();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<OrderLine>(), factory);
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

	public MoveOrderCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MoveOrderCriteria criteria) {
		this.criteria = criteria;
	}

	public List<MoveOrder> getResults() {
		return results;
	}

	public void setResults(List<MoveOrder> results) {
		this.results = results;
	}

	public MoveOrder getMoveOrder() {
		return criteria.getMoveOrder();
	}

	public void setMoveOrder(MoveOrder moveOrder) {
		criteria.setMoveOrder(moveOrder);
	}

	public List<MoveOrderLine> getLines() {
		return lines;
	}

	public void setLines(List<MoveOrderLine> lines) {
		this.lines = lines;
	}
    
	
}
