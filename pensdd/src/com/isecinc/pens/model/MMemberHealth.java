package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.MemberHealth;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.MemberDocumentProcess;

/**
 * MMember Class
 * 
 * @author Aneak.t
 * @version $Id: MMember.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 *          atiz.b : edit for memeber sequence
 * 
 */

public class MMemberHealth extends I_Model<MemberHealth> {

	private static final long serialVersionUID = 353010938349001204L;

	public static String TABLE_NAME = "m_member_health";
	public static String COLUMN_ID = "ID";

	// Column
	private String[] columns = { COLUMN_ID, "CODE", "NAME", "NAME2", "PERSON_ID_NO", "USER_ID", "EMAIL", "TERRITORY",
			"CHOLESTEROL", "BIRTHDAY", "MONHTLY_INCOME", "MEMBER_TYPE", "REGISTER_DATE", "RECOMMENDED_BY",
			"CANCEL_REASON", "MEMBER_LEVEL", "CUSTOMER_TYPE", "PAYMENT_TERM", "VAT_CODE", "PAYMENT_METHOD",
			"PRODUCT_CATEGORY_ID", "ORDER_AMOUNT_PERIOD", "SHIPPING_DATE", "SHIPPING_TIME", "ISACTIVE", "CREATED_BY",
			"UPDATED_BY", "ROUND_TRIP", "RECOMMENDED_TYPE", "RECOMMENDED_ID", "EXPIRED_DATE", "AGE_MONTH",
			"PARTY_TYPE", "DELIVERY_GROUP", "EXPORTED", "INTERFACES", "ISVIP", "SHIPPING_TIME_TO",
			"CREDITCARD_EXPIRED", "OCCUPATION" ,"CUSTOMER_ID","REFERENCE_ID"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MemberHealth find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, MemberHealth.class);
	}

	/**
	 * Find
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public MemberHealth findByCode(String code) throws Exception {
		String whereCause = "";
		whereCause += " AND CODE = '" + code.trim() + "' AND ISACTIVE = 'Y' ";
		MemberHealth[] pos = search(whereCause);
		if (pos != null) return pos[0];
		return null;
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
	public MemberHealth[] search(String whereCause) throws Exception {
		List<MemberHealth> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberHealth.class);
		if (pos.size() == 0) return null;
		MemberHealth[] array = new MemberHealth[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param member
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(MemberHealth member, String userCode, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (member.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			// String prefix = new DecimalFormat("00").format(Integer.parseInt(member.getTerritory()));
			String prefix = "";
			member.setCode(new MemberDocumentProcess().getNextDocumentNo(userCode, prefix, activeUserID, conn));
		} else {
			id = member.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "CODE", member.getCode(), id, conn)) return false;

		Object[] values = { id, ConvertNullUtil.convertToString(member.getCode()).trim(),
				ConvertNullUtil.convertToString(member.getName()).trim(),
				ConvertNullUtil.convertToString(member.getName2()).trim(),
				ConvertNullUtil.convertToString(member.getPersonIDNo()).trim(), activeUserID,
				ConvertNullUtil.convertToString(member.getEmail()).trim(),
				ConvertNullUtil.convertToString(member.getTerritory()).trim(), member.getChrolesterol(),
				DateToolsUtil.convertToTimeStamp(member.getBirthDay()), member.getMonthlyIncome(),
				ConvertNullUtil.convertToString(member.getMemberType()).trim(),
				DateToolsUtil.convertToTimeStamp(member.getRegisterDate()),
				ConvertNullUtil.convertToString(member.getRecommendedBy()).trim(),
				ConvertNullUtil.convertToString(member.getCancelReason()).trim(),
				ConvertNullUtil.convertToString(member.getMemberLevel()).trim(),
				ConvertNullUtil.convertToString(member.getCustomerType()).trim(),
				ConvertNullUtil.convertToString(member.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(member.getVatCode()).trim(),
				ConvertNullUtil.convertToString(member.getPaymentMethod()).trim(), null, member.getOrderAmountPeriod(),
				ConvertNullUtil.convertToString(member.getShippingDate()).trim(),
				ConvertNullUtil.convertToString(member.getShippingTime()).trim(), member.getIsActive(), activeUserID,
				activeUserID, member.getRoundTrip(),
				ConvertNullUtil.convertToString(member.getRecommendedType()).trim(), member.getRecommendedId(),
				DateToolsUtil.convertToTimeStamp(member.getExpiredDate()), member.getAgeMonth(),
				ConvertNullUtil.convertToString(member.getPartyType()).trim(),
				ConvertNullUtil.convertToString(member.getDeliveryGroup()).trim(), "N", "N",
				member.getIsvip() != null ? member.getIsvip() : "N",
				ConvertNullUtil.convertToString(member.getShippingTimeTo()), member.getCreditcardExpired().trim(),
				ConvertNullUtil.convertToString(member.getOccupation()).trim(),
				
				member.getCustomerId(), //edit by tutiya
				member.getReferenceId() //edit by tutiya
				};

		if (super.save(TABLE_NAME, columns, values, member.getId(), conn)) {
			member.setId(id);
		}
		return true;
	}

	/**
	 * Save
	 * 
	 * @param member
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(MemberHealth member, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (member.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			// String prefix = new DecimalFormat("00").format(Integer.parseInt(member.getTerritory()));
			// String prefix = "";
			// member.setCode(new MemberDocumentProcess().getNextDocumentNo(userCode, prefix, activeUserID, conn));
		} else {
			id = member.getId();
		}

		// check duplicate
		// if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "CODE", member.getCode(), 0, conn)) return false;

		Object[] values = { id, ConvertNullUtil.convertToString(member.getCode()).trim(),
				ConvertNullUtil.convertToString(member.getName()).trim(),
				ConvertNullUtil.convertToString(member.getName2()).trim(),
				ConvertNullUtil.convertToString(member.getPersonIDNo()).trim(), activeUserID,
				ConvertNullUtil.convertToString(member.getEmail()).trim(),
				ConvertNullUtil.convertToString(member.getTerritory()).trim(), member.getChrolesterol(),
				DateToolsUtil.convertToTimeStamp(member.getBirthDay()), member.getMonthlyIncome(),
				ConvertNullUtil.convertToString(member.getMemberType()).trim(),
				DateToolsUtil.convertToTimeStamp(member.getRegisterDate()),
				ConvertNullUtil.convertToString(member.getRecommendedBy()).trim(),
				ConvertNullUtil.convertToString(member.getCancelReason()).trim(),
				ConvertNullUtil.convertToString(member.getMemberLevel()).trim(),
				ConvertNullUtil.convertToString(member.getCustomerType()).trim(),
				ConvertNullUtil.convertToString(member.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(member.getVatCode()).trim(),
				ConvertNullUtil.convertToString(member.getPaymentMethod()).trim(), null, member.getOrderAmountPeriod(),
				ConvertNullUtil.convertToString(member.getShippingDate()).trim(),
				ConvertNullUtil.convertToString(member.getShippingTime()).trim(), member.getIsActive(), activeUserID,
				activeUserID, member.getRoundTrip(),
				ConvertNullUtil.convertToString(member.getRecommendedType()).trim(), member.getRecommendedId(),
				DateToolsUtil.convertToTimeStamp(member.getExpiredDate()), member.getAgeMonth(),
				ConvertNullUtil.convertToString(member.getPartyType()).trim(),
				ConvertNullUtil.convertToString(member.getDeliveryGroup()).trim(), "N", "N",
				member.getIsvip() != null ? member.getIsvip() : "N",
				ConvertNullUtil.convertToString(member.getShippingTimeTo()), member.getCreditcardExpired().trim(),
				ConvertNullUtil.convertToString(member.getOccupation()).trim(),
				
				member.getCustomerId(), //edit by tutiya
				member.getReferenceId() //edit by tutiya
				};

		if (super.save(TABLE_NAME, columns, values, member.getId(), conn)) {
			member.setId(id);
		}
		return true;
	}

	/**
	 * Set Member Age & Level
	 * 
	 * @param member
	 */
	public void setMemberAgeLevel(MemberHealth member) {
		List<References> memberLevel = InitialReferences.getReferenes().get(InitialReferences.MEMBER_STATUS);
		String level = "";
		int month = 0;
		month = Integer.parseInt(member.getMemberType());
		// if (member.getMemberType().equals("01")) {
		// month = 3;
		// } else if (member.getMemberType().equals("02")) {
		// month = 6;
		// } else if (member.getMemberType().equals("03")) {
		// month = 9;
		// } else if (member.getMemberType().equals("04")) {
		// month = 12;
		// }
		int ageMonth = member.getAgeMonth();
		ageMonth += month;
		if (ageMonth >= 0 && ageMonth <= 3) {
			level = "R";
		}
		if (ageMonth > 3 && ageMonth <= 6) {
			level = "S";
		}
		if (ageMonth > 6 && ageMonth <= 9) {
			level = "G";
		}
		if (ageMonth > 9 && ageMonth <= 12) {
			level = "D";
		}
		if (ageMonth > 12) {
			level = "P";
		}
		member.setAgeMonth(ageMonth);
		for (References r : memberLevel) {
			if (r.getKey().equalsIgnoreCase(level)) {
				member.setMemberLevel(r.getKey());
				break;
			}
		}
		return;
	}
}
