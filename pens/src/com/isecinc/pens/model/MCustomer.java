package com.isecinc.pens.model;

import static util.ConvertNullUtil.convertToString;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.CustomerDocumentProcess;

/**
 * MCustomer Class
 * 
 * @author Aneak.t
 * @version $Id: MCustomer.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 *          atiz.b : edit for new customer sequence
 * 
 */

public class MCustomer extends I_Model<Customer> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "m_customer";
	public static String COLUMN_ID = "Customer_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "CODE", "NAME", "NAME2", "CUSTOMER_TYPE", "TAX_NO", "TERRITORY", "WEBSITE",
			"BUSINESS_TYPE", "BIRTHDAY", "CREDIT_CHECK", "PAYMENT_TERM", "VAT_CODE", "PAYMENT_METHOD",
			"SHIPPING_METHOD", "USER_ID", "ISACTIVE", "CREATED_BY", "UPDATED_BY", "PARENT_CUSTOMER_ID", "PARTY_TYPE" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Customer find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Customer.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public Customer[] search(String whereCause) throws Exception {
		List<Customer> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Customer.class);
		if (pos.size() == 0) return null;
		Customer[] array = new Customer[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 * Tunnig Method By Wit
	 */
	public Customer[] searchOpt(String whereCause) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		List<Customer> custList = new ArrayList<Customer>();
		Customer[] array = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String sql = "select m_customer.* ,  \n";
			       sql+=" ad_user.CATEGORY,ad_user.ORGANIZATION,ad_user.START_DATE,ad_user.END_DATE, \n";
                   sql+=" ad_user.NAME,ad_user.SOURCE_NAME,ad_user.ID_CARD_NO,ad_user.USER_NAME,ad_user.PASSWORD, \n";
                   sql+=" ad_user.ROLE,ad_user.ISACTIVE,ad_user.CODE,ad_user.UPDATED,ad_user.UPDATED_BY,ad_user.TERRITORY, \n";
                   sql+= " ( select count(*) as tot from t_order od where od.customer_id = m_customer.customer_id)  as order_amount, \n";
                   sql+= " ( select sum(o.net_amount) net_amount from t_order o  where o.doc_status = 'SV'  and o.CUSTOMER_ID= m_customer.CUSTOMER_ID ) \n";
                   sql+= "   - \n";
                   sql+= "  ( select sum(r.receipt_amount) receipt_amount from t_receipt r where r.doc_status = 'SV'   and r.CUSTOMER_ID = m_customer.CUSTOMER_ID) \n";
                   sql+= "  as  total_invoice \n";
                   sql+= " from m_customer  ,ad_user where m_customer.user_id = ad_user.user_id \n";
			       sql+= whereCause;
			       
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				Customer m = new Customer();
				// Mandatory
				m.setId(rst.getInt("CUSTOMER_ID"));
				m.setReferencesID(rst.getInt("REFERENCE_ID"));
				m.setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
				m.setCode(rst.getString("CODE").trim());
				m.setName(rst.getString("NAME").trim());
				m.setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
				m.setTaxNo(ConvertNullUtil.convertToString(rst.getString("TAX_NO")).trim());
				m.setWebsite(ConvertNullUtil.convertToString(rst.getString("WEBSITE")).trim());
				m.setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
				m.setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
				// System.out.println(rst.getString("PARENT_CUSTOMER_ID"));
				if (rst.getInt("PARENT_CUSTOMER_ID") != 0 && rst.getString("PARENT_CUSTOMER_ID") != null) {
					Customer c = new MCustomer().find(rst.getString("PARENT_CUSTOMER_ID"));
					m.setParentID(c.getId());
					m.setParentCode(c.getCode());
					m.setParentName((c.getName() + " " + c.getName2()).trim());
				}
				m.setBirthDay("");
				if (rst.getTimestamp("BIRTHDAY") != null) {
					m.setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
				}
				m.setCreditCheck(ConvertNullUtil.convertToString(rst.getString("CREDIT_CHECK")).trim());
				m.setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
				m.setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
				m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				m.setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
				m.setShippingRoute(ConvertNullUtil.convertToString(rst.getString("SHIPPING_ROUTE")).trim());
				m.setTransitName(ConvertNullUtil.convertToString(rst.getString("TRANSIT_NAME")).trim());
				
				/** **/
				User u = new User();
				u.setId(rst.getInt("USER_ID"));
				u.setCode(rst.getString("CODE").trim());
				u.setName(rst.getString("NAME").trim());
				u.setType(convertToString(rst.getString("ROLE")).trim());
				u.setActive(rst.getString("ISACTIVE").trim());

				// oracle fields
				u.setCategory(convertToString(rst.getString("CATEGORY")).trim());
				u.setOrganization(convertToString(rst.getString("ORGANIZATION")).trim());
				u.setSourceName(convertToString(rst.getString("SOURCE_NAME")).trim());
				u.setIdCardNo(convertToString(rst.getString("ID_CARD_NO")).trim());
				u.setStartDate(convertToString(rst.getString("START_DATE")).trim());
				u.setEndDate(convertToString(rst.getString("END_DATE")).trim());
				u.setTerritory(convertToString(rst.getString("TERRITORY")).trim());

				// sales online fields
				u.setUserName(convertToString(rst.getString("USER_NAME")).trim());
				u.setPassword(convertToString(rst.getString("PASSWORD")).trim());
				u.setConfirmPassword(convertToString(rst.getString("PASSWORD")).trim());
				
				u.activeRoleInfo();
				m.setSalesRepresent(u);
				/** **/
				
				m.setCreditLimit(rst.getDouble("CREDIT_LIMIT"));
				m.setIsActive(rst.getString("ISACTIVE").trim());
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
				m.setExported(rst.getString("EXPORTED"));

				// Total Invoice
				m.setTotalInvoice(rst.getDouble("total_invoice"));
				// Order Amount
				m.setOrderAmount(rst.getInt("order_amount"));

				// set display label
				m.setDisplayLabel();
				
				custList.add(m);
			}
			
			//convert to Obj
			array = new Customer[custList.size()];
			array = custList.toArray(array);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return array;
	}
	/**
	 * Save
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private int id = 0;

	public boolean save(Customer customer, int activeUserID, Connection conn) throws Exception {

		if (customer.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			// String prefix = new DecimalFormat("00").format(Integer.parseInt(customer.getTerritory()));
			// String prefix = customer.getCodePrefix();
			// customer.setCode(new CustomerDocumentProcess().getNextDocumentNo(customer.getSalesRepresent().getCode(),
			// prefix, activeUserID, conn));
			customer.setCode(new CustomerDocumentProcess().getNextDocumentNo(customer.getTerritory(), customer
					.getProvince(), customer.getDistrict(), activeUserID, conn));
		} else {
			id = customer.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "CODE", customer.getCode(), id, conn)) return false;

		Object[] values = { id, ConvertNullUtil.convertToString(customer.getCode()).trim(),
				ConvertNullUtil.convertToString(customer.getName()).trim(),
				ConvertNullUtil.convertToString(customer.getName2()).trim(),
				ConvertNullUtil.convertToString(customer.getCustomerType()).trim(),
				ConvertNullUtil.convertToString(customer.getTaxNo()).trim(),
				ConvertNullUtil.convertToString(customer.getTerritory()).trim(),
				ConvertNullUtil.convertToString(customer.getWebsite()).trim(),
				ConvertNullUtil.convertToString(customer.getBusinessType()).trim(),
				DateToolsUtil.convertToTimeStamp(customer.getBirthDay()), "N",
				ConvertNullUtil.convertToString(customer.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(customer.getVatCode()).trim(),
				ConvertNullUtil.convertToString(customer.getPaymentMethod()).trim(),
				ConvertNullUtil.convertToString(customer.getShippingMethod()).trim(),
				customer.getSalesRepresent().getId(), customer.getIsActive() != null ? customer.getIsActive() : "N",
				activeUserID, activeUserID, customer.getParentID(),
				ConvertNullUtil.convertToString(customer.getPartyType()).trim() };
		if (super.save(TABLE_NAME, columns, values, customer.getId(), conn)) {
			customer.setId(id);
		}
		return true;
	}

	/**
	 * Get Invoice Amount
	 * 
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public double getInvoiceAmount(int customerId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		double orderAmt = 0;
		double paidAmt = 0;
		double creditNoteAmt = 0 ;
		double invoiceAmt = 0;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String sql = "select sum(ROUND(o.net_amount,2)) net_amount ";
			sql += "from t_order o ";
			sql += "where o.doc_status = 'SV' ";
			// sql += "  and o.interfaces = 'Y' ";
			// sql += "  and o.ar_invoice_no is not null ";
			// sql += "  and o.ar_invoice_no <> '' ";
			sql += "  and o.CUSTOMER_ID = " + customerId;

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if (rst.next()) orderAmt = rst.getDouble("net_amount");

			sql = "SELECT SUM(rl.PAID_AMOUNT) paid_amount ";
			sql += "FROM T_RECEIPT_LINE rl , T_RECEIPT r ";
			sql += "WHERE rl.RECEIPT_ID = r.RECEIPT_ID AND r.doc_status = 'SV' ";
			sql += "  AND r.CUSTOMER_ID = " + customerId;
            
			//logger.debug("sql:"+sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) paidAmt = rst.getDouble("paid_amount");
			
			/* Reduce From Credit No*/
			/*sql = "SELECT SUM(t.TOTAL_AMOUNT) as creditNoteAmt FROM ( "+
					" SELECT cn.* FROM t_credit_note cn "+
					" WHERE NOT EXISTS ( SELECT 1 FROM t_order od , t_receipt rc, t_receipt_line rcl WHERE rc.receipt_id = rcl.receipt_id AND od.order_id = rcl.order_id AND rc.doc_status = 'SV'AND od.ar_invoice_no = cn.ar_invoice_no) "+
					" AND NOT EXISTS (SELECT 1 FROM t_receipt rc,t_receipt_cn rccn WHERE rc.receipt_id = rccn.receipt_id AND rc.Doc_Status = 'SV' AND rccn.credit_note_id = cn.credit_note_id) "+
					" AND cn.AR_Invoice_No IS NOT NULL AND cn.active='Y' "+
					" UNION "+
					" SELECT cn.* FROM t_credit_note cn WHERE cn.active='Y' AND cn.ar_invoice_no IS NULL "+
					" AND NOT EXISTS (SELECT 1 FROM t_receipt rc,t_receipt_cn rccn WHERE rc.receipt_id = rccn.receipt_id AND rc.Doc_Status = 'SV' AND rccn.credit_note_id = cn.credit_note_id ) "+
					") t WHERE t.CUSTOMER_ID = " + customerId;
			*/
			sql = "select sum(cn.total_amount) as creditNoteAmt from t_credit_note cn where NOT EXISTS ( SELECT 1 FROM T_ORDER od WHERE od.AR_INVOICE_NO = cn.AR_INVOICE_NO AND (od.PAYMENT = 'Y' OR od.DOC_STATUS = 'VO')) AND cn.active = 'Y' and cn.customer_id = "+customerId;
			
			rst = stmt.executeQuery(sql);
			if (rst.next()) creditNoteAmt = rst.getDouble("creditNoteAmt");
			
			Log.debug("Customer Credit Note Amount : "+creditNoteAmt);

			invoiceAmt = orderAmt - paidAmt + creditNoteAmt;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return invoiceAmt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
