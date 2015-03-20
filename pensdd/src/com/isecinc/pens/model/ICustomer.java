package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.dataimports.bean.ICustomerBean;

/**
 * ICustomer Class
 * 
 * @author Danai.K
 * @version $Id: ICustomer.java,v 1.0 20/12/2010 00:00:00 Danai.K Exp $
 */

public class ICustomer extends I_Model<ICustomerBean> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "i_customer";
	public static String COLUMN_ID = "ICUSTOMER_ID";

	// Column Sales Online Side active
	private String[] columns = {
		COLUMN_ID, "CODE", "NAME", "NAME2", "CUSTOMER_TYPE", 
		"PARTY_TYPE", "TERRITORY", "PERSON_ID_NO", "EMAIL", 
		"BIRTHDAY", "OCCUPATION", "MONHTLY_INCOME", "CHOLESTEROL", 
		"MEMBER_LEVEL", "MEMBER_TYPE", "REGISTER_DATE", "EXPIRED_DATE", 
		"AGE_MONTH", "CREDIT_CHECK", "CREDIT_LIMIT", "PAYMENT_TERM", 
		"PAYMENT_METHOD", "VAT_CODE", "SHIPPING_DATE", "SHIPPING_TIME", "SHIPPING_TIME_TO", 
		"DELIVERY_GROUP", "ROUND_TRIP", "ORDER_AMOUNT_PERIOD", "ISVIP", 
		"USER_ID", /*"INTERFACES",*/ "EXPORTED", "IMPORTED", "IMPORTED_DETAIL", 
		"ORDER_LINE_REMAIN", "START_NEXT_YEAR", "PREPAID_NEXT_YEAR", 
		/*"ISACTIVE",*/ /*"CREATED",*/ "CREATED_BY", /*"UPDATED",*/ "UPDATED_BY"
	};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ICustomerBean find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ICustomerBean.class);
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
	public ICustomerBean[] search(String whereCause) throws Exception {
		List<ICustomerBean> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ICustomerBean.class);
		if (pos.size() == 0) return null;
		ICustomerBean[] array = new ICustomerBean[pos.size()];
		array = pos.toArray(array);
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
	public boolean save(ICustomerBean customer, int activeUserID, Connection conn) throws Exception {
		/*int id = 0;
		if (customer.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = customer.getId();
		}*/
		int id = customer.getId();

		Object[] values = {
			id, ConvertNullUtil.convertToString(customer.getCode()), ConvertNullUtil.convertToString(customer.getName()), ConvertNullUtil.convertToString(customer.getName2()), ConvertNullUtil.convertToString(customer.getCustomerType()), 
			ConvertNullUtil.convertToString(customer.getPartyType()), ConvertNullUtil.convertToString(customer.getTerritory()), ConvertNullUtil.convertToString(customer.getPersonIDNo()), ConvertNullUtil.convertToString(customer.getEmail()), 
			customer.getBirthDay()==null?null:DateToolsUtil.convertToTimeStamp(customer.getBirthDay()), ConvertNullUtil.convertToString(customer.getOccupation()), customer.getMonthlyIncome(), customer.getChrolesterol(), 
			ConvertNullUtil.convertToString(customer.getMemberLevel()), ConvertNullUtil.convertToString(customer.getMemberType()), customer.getRegisterDate()==null?null:DateToolsUtil.convertToTimeStamp(customer.getRegisterDate()), customer.getExpiredDate()==null?null:DateToolsUtil.convertToTimeStamp(customer.getExpiredDate()), 
			customer.getAgeMonth(), ConvertNullUtil.convertToString(customer.getCreditCheck()), customer.getCreditLimit(), ConvertNullUtil.convertToString(customer.getPaymentTerm()), 
			ConvertNullUtil.convertToString(customer.getPaymentMethod()), ConvertNullUtil.convertToString(customer.getVatCode()), ConvertNullUtil.convertToString(customer.getShippingDate()), ConvertNullUtil.convertToString(customer.getShippingTime()), ConvertNullUtil.convertToString(customer.getShippingTimeTo()), 
			ConvertNullUtil.convertToString(customer.getDeliveryGroup()), ConvertNullUtil.convertToString(customer.getRoundTrip()), customer.getOrderAmountPeriod(), ConvertNullUtil.convertToString(customer.getIsVip()), 
			activeUserID, /*customer.getInterfaces(),*/ ConvertNullUtil.convertToString(customer.getExported()), ConvertNullUtil.convertToString(customer.getImported()), ConvertNullUtil.convertToString(customer.getImportedDetail()), 
			customer.getOrderLineRemain(), customer.getStartNextYear()==null?null:DateToolsUtil.convertToTimeStamp(customer.getStartNextYear()), customer.getPrepaidNextYear(), 
			/*customer.getIsActive(),*/ /*customer.getCreated(),*/ activeUserID, /*customer.getUpdated(),*/ activeUserID
		};

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "CODE", customer.getCode(), 0, conn)) {
			//update
			super.save(TABLE_NAME, columns, values, customer.getId(), conn);
		} else {
			//insert
			super.save(TABLE_NAME, columns, values, 0, conn);
		}

		return true;
	}
}
