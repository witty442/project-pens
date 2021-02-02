package com.isecinc.pens.web.stock;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.StockLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class StockForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private StockCriteria criteria = new StockCriteria();

    /** Customer **/
	private Customer[] resultsCust = null;
	List<Address> addresses = null;
	List<Contact> contacts = null;
	private int curPage;
	private int totalRow;
	private int totalPage;
	
	
	private List<Stock> results;
	private List<StockLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";

	@SuppressWarnings("unchecked")
	public StockForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new StockLine();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<StockLine>(), factory);
		
		Factory factory2 = new Factory() {
			public Object create() {
				return new Address();
			}
		};
		addresses = LazyList.decorate(new ArrayList<Address>(), factory);

		Factory factory3 = new Factory() {
			public Object create() {
				return new Contact();
			}
		};
		contacts = LazyList.decorate(new ArrayList<Contact>(), factory2);
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

	public StockCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(StockCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Stock> getResults() {
		return results;
	}

	public void setResults(List<Stock> results) {
		this.results = results;
	}

	public Stock getBean() {
		return criteria.getBean();
	}

	public void setBean(Stock moveOrder) {
		criteria.setBean(moveOrder);
	}

	public List<StockLine> getLines() {
		return lines;
	}

	public void setLines(List<StockLine> lines) {
		this.lines = lines;
	}
	public Customer getCustomer() {
		return criteria.getCustomer();
	}

	public void setCustomer(Customer customer) {
		criteria.setCustomer(customer);
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
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

	public Customer[] getResultsCust() {
		return resultsCust;
	}

	public void setResultsCust(Customer[] resultsCust) {
		this.resultsCust = resultsCust;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if(getBean() != null){
			getBean().setHaveStock("");
		}
	}
}
