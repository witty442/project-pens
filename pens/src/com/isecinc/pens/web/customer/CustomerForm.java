package com.isecinc.pens.web.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Customer;

/**
 * Customer Form
 * 
 * @author Aneak.t
 * @version $Id: CustomerForm.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class CustomerForm extends I_Form implements Serializable{

	private static final long serialVersionUID = 9066506758859129582L;

	private CustomerCriteria criteria = new CustomerCriteria();

	private Customer[] results = null;
	private FormFile imageFile;

	List<Address> addresses = null;

	List<Contact> contacts = null;
	
	private int curPage;
	private int totalRow;
	private int totalPage;


	public FormFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(FormFile imageFile) {
		this.imageFile = imageFile;
	}

	@SuppressWarnings("unchecked")
	public CustomerForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new Address();
			}
		};
		addresses = LazyList.decorate(new ArrayList<Address>(), factory);

		Factory factory2 = new Factory() {
			public Object create() {
				return new Contact();
			}
		};
		contacts = LazyList.decorate(new ArrayList<Contact>(), factory2);
	}

	public CustomerCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CustomerCriteria criteria) {
		this.criteria = criteria;
	}

	public Customer[] getResults() {
		return results;
	}

	public void setResults(Customer[] results) {
		this.results = results;
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
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		  if(getCustomer() !=null){
			 getCustomer().setDispHaveTrip("");
			 getCustomer().setDispTotalInvoice("");
		  }
	}

}
