package com.isecinc.pens.web.ordernissin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.OrderNissin;
import com.isecinc.pens.bean.OrderNissinLine;

/**
 * Order Nissin Form
 * 
 * @author Wittaya.b
 * @version $Id: NissinOrderForm.java,v 1.0 25/11/2020  $
 * 
 */
public class OrderNissinForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private OrderNissinCriteria criteria = new OrderNissinCriteria();

	private List<OrderNissin> results;
	private List<OrderNissinLine> lines = null;
	
	private String deletedId = "";
	private String lineNoDeleteArray ="";
	private String mode;
	private String fromPage;
	
	@SuppressWarnings("unchecked")
	public OrderNissinForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new OrderNissinLine();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<OrderNissinLine>(), factory);
	}
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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

	public OrderNissinCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(OrderNissinCriteria criteria) {
		this.criteria = criteria;
	}

	public List<OrderNissin> getResults() {
		return results;
	}

	public void setResults(List<OrderNissin> results) {
		this.results = results;
	}

	public OrderNissin getBean() {
		return criteria.getBean();
	}

	public void setBean(OrderNissin bean) {
		criteria.setBean(bean);
	}

	public List<OrderNissinLine> getLines() {
		return lines;
	}

	public void setLines(List<OrderNissinLine> lines) {
		this.lines = lines;
	}
    
	
}
