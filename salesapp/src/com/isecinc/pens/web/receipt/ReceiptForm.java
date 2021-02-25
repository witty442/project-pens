package com.isecinc.pens.web.receipt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptCN;
import com.isecinc.pens.bean.ReceiptLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private List<ReceiptLine> lines = null;

	private List<ReceiptBy> bys = null;

	private List<ReceiptCN> cns = null;

	private Receipt[] results = null;

	private String deletedId = "";

	private String deletedRecpById = "";

	private String deleteCNId = "";
	private Receipt receipt = new Receipt();

	

	@SuppressWarnings("unchecked")
	public ReceiptForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new ReceiptLine();
			}
		};
		lines = LazyList.decorate(new ArrayList<ReceiptLine>(), factory);

		Factory factory2 = new Factory() {
			public Object create() {
				return new ReceiptBy();
			}
		};
		bys = LazyList.decorate(new ArrayList<ReceiptBy>(), factory2);

		Factory factory3 = new Factory() {
			public Object create() {
				return new ReceiptCN();
			}
		};
		cns = LazyList.decorate(new ArrayList<ReceiptCN>(), factory3);

	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}
	
	public List<ReceiptLine> getLines() {
		return lines;
	}

	public void setLines(List<ReceiptLine> lines) {
		this.lines = lines;
	}

	public Receipt[] getResults() {
		return results;
	}

	public void setResults(Receipt[] results) {
		this.results = results;
	}

	public String getDeletedId() {
		return deletedId;
	}

	public void setDeletedId(String deletedId) {
		this.deletedId = deletedId;
	}

	public String getDeletedRecpById() {
		return deletedRecpById;
	}

	public void setDeletedRecpById(String deletedRecpById) {
		this.deletedRecpById = deletedRecpById;
	}

	public List<ReceiptBy> getBys() {
		return bys;
	}

	public void setBys(List<ReceiptBy> bys) {
		this.bys = bys;
	}

	public List<ReceiptCN> getCns() {
		return cns;
	}

	public void setCns(List<ReceiptCN> cns) {
		this.cns = cns;
	}

	public String getDeleteCNId() {
		return deleteCNId;
	}

	public void setDeleteCNId(String deleteCNId) {
		this.deleteCNId = deleteCNId;
	}

}
