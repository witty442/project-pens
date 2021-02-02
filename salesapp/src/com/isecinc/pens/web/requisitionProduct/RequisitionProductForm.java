package com.isecinc.pens.web.requisitionProduct;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.RequisitionProduct;
import com.isecinc.pens.bean.RequisitionProductLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class RequisitionProductForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private RequisitionProductCriteria criteria = new RequisitionProductCriteria();

	private List<RequisitionProduct> results;
	private List<RequisitionProductLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	@SuppressWarnings("unchecked")
	public RequisitionProductForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new RequisitionProductLine();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<RequisitionProductLine>(), factory);
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

	public RequisitionProductCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RequisitionProductCriteria criteria) {
		this.criteria = criteria;
	}

	public List<RequisitionProduct> getResults() {
		return results;
	}

	public void setResults(List<RequisitionProduct> results) {
		this.results = results;
	}

	public RequisitionProduct getRequisitionProduct() {
		return criteria.getRequisitionProduct();
	}

	public void setRequisitionProduct(RequisitionProduct moveOrder) {
		criteria.setRequisitionProduct(moveOrder);
	}

	public List<RequisitionProductLine> getLines() {
		return lines;
	}

	public void setLines(List<RequisitionProductLine> lines) {
		this.lines = lines;
	}
    
	
}
