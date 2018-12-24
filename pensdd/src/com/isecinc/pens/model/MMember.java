package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ProductCategory;
import com.isecinc.pens.bean.ResultBean;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.MemberDocumentProcess;
import com.isecinc.pens.web.member.MemberForm;

/**
 * MMember Class
 * 
 * @author Aneak.t
 * @version $Id: MMember.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 *          atiz.b : edit for memeber sequence
 * 
 */

public class MMember extends I_Model<Member> {

	private static final long serialVersionUID = 353010938349001204L;

	public static String TABLE_NAME = "m_customer";
	public static String COLUMN_ID = "Customer_ID";
	private final int MAX_DISP_MEMBER = 500;
	// Column
	private String[] columns = { COLUMN_ID, "CODE", "NAME", "NAME2", "PERSON_ID_NO", "USER_ID", "EMAIL", "TERRITORY",
			"CHOLESTEROL", "BIRTHDAY", "MONHTLY_INCOME", "MEMBER_TYPE", "REGISTER_DATE","FIRST_DELIVERLY_DATE", "RECOMMENDED_BY",
			"CANCEL_REASON", "MEMBER_LEVEL", "CUSTOMER_TYPE", "PAYMENT_TERM", "VAT_CODE", "PAYMENT_METHOD",
			"PRODUCT_CATEGORY_ID", "ORDER_AMOUNT_PERIOD", "SHIPPING_DATE", "SHIPPING_TIME", "ISACTIVE", "CREATED_BY",
			"UPDATED_BY", "ROUND_TRIP", "RECOMMENDED_TYPE", "RECOMMENDED_ID", "EXPIRED_DATE", "AGE_MONTH",
			"PARTY_TYPE", "DELIVERY_GROUP", "EXPORTED", "INTERFACES", "ISVIP", "SHIPPING_TIME_TO",
			"CREDITCARD_EXPIRED", "OCCUPATION", "FREE_OF_CHART","PAYMENT_TYPE","CARD_NO","CARD_BANK"
			,"CARD_NAME","OLD_PRICE_FLAG","TOTAL_ORDER_QTY"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Member find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Member.class);
	}

	/**
	 * Find
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public Member findByCode(String code) throws Exception {
		String whereCause = "";
		whereCause += " AND CODE = '" + code.trim() + "' AND ISACTIVE = 'Y' ";
		Member[] pos = search(whereCause);
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
	public Member[] search(String whereCause) throws Exception {
		List<Member> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Member.class);
		if (pos.size() == 0) return null;
		Member[] array = new Member[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	public ResultBean searchNew(MemberForm memberForm) throws Exception {
		ResultBean resultBean = new ResultBean();
		List<Member> pos = new ArrayList<Member>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst =null;
		int countRecord = 0;
		boolean nextLoop = true;
		boolean moreMaxRecord = false;
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			StringBuffer w = new StringBuffer("");
			w.append("SELECT distinct \n");
			w.append("m.CUSTOMER_ID,m.REFERENCE_ID,CODE, \n");
			w.append("m.NAME,m.NAME2,CUSTOMER_TYPE, \n");
			w.append("TAX_NO,TERRITORY,WEBSITE, \n");
			w.append("BUSINESS_TYPE,m.ISACTIVE,m.USER_ID, \n");
			w.append("PERSON_ID_NO,EMAIL,CREDIT_CHECK, \n");
			w.append("REGISTER_DATE,FIRST_DELIVERLY_DATE,PAYMENT_TERM, \n");
			w.append("VAT_CODE,CREDIT_LIMIT,BIRTHDAY, \n");
			w.append("OCCUPATION,SHIPPING_METHOD,ORDER_DATE, \n");
			w.append("SHIPPING_DATE,SHIPPING_ROUTE,MONHTLY_INCOME, \n");
			w.append("CHOLESTEROL,MEMBER_LEVEL,RECOMMENDED_BY, \n");
			w.append("CANCEL_REASON,MEMBER_TYPE,m.PRODUCT_CATEGORY_ID, \n");
		    w.append("m.CREATED,m.CREATED_BY,m.UPDATED, \n");
		    w.append("m.UPDATED_BY,ORDER_AMOUNT_PERIOD,PAYMENT_METHOD, \n");
			w.append("PARENT_CUSTOMER_ID,TRANSIT_NAME,RECOMMENDED_TYPE, \n");
			w.append("RECOMMENDED_ID,SHIPPING_TIME,ROUND_TRIP, \n");
			w.append("EXPIRED_DATE,AGE_MONTH,m.INTERFACES, \n");
			w.append("PARTY_TYPE,m.EXPORTED,DELIVERY_GROUP, \n");
			w.append("isvip,SHIPPING_TIME_TO,CREDITCARD_EXPIRED, \n");
			w.append("PAYMENT_TYPE,FREE_OF_CHART,CARD_BANK, \n");
			w.append("CARD_NO,card_name,OLD_PRICE_FLAG, \n");
			w.append("(SELECT NAME FROM c_reference r WHERE r.value = m.member_type and r.code ='MemberType') as MEMBER_TYPE_LABEL, \n");
			w.append("(SELECT NAME FROM c_reference r WHERE r.value = m.member_level and r.code ='MemberStatus') as MEMBER_LEVEL_LABEL, \n");
			w.append("(SELECT NAME FROM c_reference r WHERE r.value = m.isactive and r.code ='Active') as ACTIVE_LABEL \n");
			w.append(",pc.product_category_id,pc.NAME as PC_NAME ,pc.ISACTIVE as PC_ISACTIVE \n");
			w.append("FROM m_customer m \n");
			w.append("LEFT OUTER JOIN m_contact c \n");
			w.append("ON m.customer_id = c.customer_id \n");
			w.append("LEFT OUTER JOIN m_product_category pc \n");
			w.append("ON m.product_category_id = pc.product_category_id \n");
			
			w.append("WHERE 1=1 \n");
			if ( !Utils.isNull(memberForm.getMember().getCode()).equals("")) {
				w.append(" AND m.CODE LIKE '"+ Utils.isNull(memberForm.getMember().getCode()).replace("\'", "\\\'").replace("\"", "\\\"") + "%' \n");
			}
			if ( !Utils.isNull(memberForm.getMember().getName()).equals("")) {
				w.append(" AND m.NAME LIKE '%"+ Utils.isNull(memberForm.getMember().getName()).replace("\'", "\\\'").replace("\"", "\\\"") + "%' \n");
			}
			if (!Utils.isNull(memberForm.getMember().getName2()).equals("")) {
				w.append(" AND m.NAME2 LIKE '%"+ Utils.isNull(memberForm.getMember().getName2()).replace("\'", "\\\'").replace("\"", "\\\"") + "%' \n");
			}
			if ( !Utils.isNull(memberForm.getMember().getPersonIDNo()).equals("")) {
				w.append(" AND m.PERSON_ID_NO LIKE '%"+ Utils.isNull(memberForm.getMember().getPersonIDNo()).replace("\'", "\\\'").replace("\"", "\\\"")+ "%' \n");
			}

			if ( !Utils.isNull(memberForm.getContacts().get(0).getPhone()).equals("")) {			
				w.append(" AND c.PHONE LIKE '"+ Utils.isNull(memberForm.getContacts().get(0).getPhone()) + "%' \n");
			}
			
			if ( !Utils.isNull(memberForm.getContacts().get(0).getMobile()).equals("")) {			
				w.append(" AND c.MOBILE LIKE '"+ Utils.isNull(memberForm.getContacts().get(0).getMobile()) + "%' \n");
			}
			
			if (!Utils.isNull(memberForm.getMember().getMemberType()).equals("")) {
				w.append(" AND m.MEMBER_TYPE = '" + Utils.isNull(memberForm.getMember().getMemberType()) + "' \n");
			}
			if ( !Utils.isNull(memberForm.getMember().getMemberLevel()).equals("")) {
				w.append(" AND m.MEMBER_LEVEL = '" + Utils.isNull(memberForm.getMember().getMemberLevel()) + "' \n");
			}
			if ( !Utils.isNull(memberForm.getMember().getIsActive()).equals("")) {
				w.append(" AND m.ISACTIVE = '" + memberForm.getMember().getIsActive() + "' \n");
			}
			w.append(" AND m.CUSTOMER_TYPE = 'DD' \n");
			w.append(" ORDER BY CAST(m.CODE as unsigned) \n");
			
			logger.info("sql:"+w.toString());
			
			ps = conn.prepareStatement(w.toString());
			rst = ps.executeQuery();
			while(rst.next() && nextLoop){
				
				Member m = new Member();
				m.setId(rst.getInt("CUSTOMER_ID"));
				m.setCode(rst.getString("CODE").trim());
				m.setName(rst.getString("NAME").trim());
				m.setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
				m.setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
				m.setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
				m.setIsActive(rst.getString("ISACTIVE").trim());
				m.setActiveLabel(rst.getString("ACTIVE_LABEL"));
				
				m.setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
				m.setPersonIDNo(ConvertNullUtil.convertToString(rst.getString("PERSON_ID_NO")).trim());
				m.setEmail(ConvertNullUtil.convertToString(rst.getString("EMAIL")).trim());
				m.setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
				m.setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
				m.setExpiredDate("");
				if (rst.getTimestamp("EXPIRED_DATE") != null) {
					m.setExpiredDate(DateToolsUtil.convertToString(rst.getTimestamp("EXPIRED_DATE")));
				}
				m.setBirthDay("");
				if (rst.getTimestamp("BIRTHDAY") != null) {
					m.setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
				}
				m.setOccupation(ConvertNullUtil.convertToString(rst.getString("OCCUPATION")).trim());
				m.setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
				m.setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
				m.setShippingDate(ConvertNullUtil.convertToString(rst.getString("SHIPPING_DATE")).trim());
				m.setShippingTime(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME")).trim());
				m.setShippingTimeTo(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME_TO")).trim());
				m.setMonthlyIncome(rst.getDouble("MONHTLY_INCOME"));
				m.setChrolesterol(rst.getDouble("CHOLESTEROL"));
				m.setMemberLevel(ConvertNullUtil.convertToString(rst.getString("MEMBER_LEVEL")).trim());
				m.setMemberLevelLabel(rst.getString("MEMBER_LEVEL_LABEL"));
				m.setRecommendedBy(ConvertNullUtil.convertToString(rst.getString("RECOMMENDED_BY")).trim());
				m.setRegisterDate("");
				//System.out.println("rst.getTimestamp-->"+rst.getTimestamp("REGISTER_DATE"));
				if (rst.getTimestamp("REGISTER_DATE") != null) {
					m.setRegisterDate(DateToolsUtil.convertToString(rst.getTimestamp("REGISTER_DATE")));
				}
				m.setFirstDeliverlyDate("");
				if (rst.getTimestamp("FIRST_DELIVERLY_DATE") != null) {
					m.setFirstDeliverlyDate(DateToolsUtil.convertToString(rst.getTimestamp("FIRST_DELIVERLY_DATE")));
				}
				
				m.setMemberType(ConvertNullUtil.convertToString(rst.getString("MEMBER_TYPE")).trim());
				m.setMemberTypeLabel(rst.getString("MEMBER_TYPE_LABEL"));
				
				m.setCreated(rst.getTimestamp("CREATED"));
				m.setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
				m.setUpdated(rst.getTimestamp("UPDATED"));
				m.setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));
				m.setOrderAmountPeriod(rst.getInt("ORDER_AMOUNT_PERIOD"));
				m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				m.setParentCustomerId(rst.getInt("PARENT_CUSTOMER_ID"));
				
				//m.setProductCategory(new MProductCategory().find(String.valueOf(rst.getInt("PRODUCT_CATEGORY_ID"))));
				ProductCategory pc = new ProductCategory();
				pc.setId(rst.getInt("PRODUCT_CATEGORY_ID"));
				pc.setName(rst.getString("PC_NAME"));
				pc.setIsActive(rst.getString("PC_ISACTIVE"));
				m.setProductCategory(pc);
				
				String rt = ConvertNullUtil.convertToString(rst.getString("ROUND_TRIP")).trim();
				m.setRoundTrip(rt.length() > 0 ? String.valueOf(new Double(rt).intValue()) : "0");
		
				m.setRecommendedType(ConvertNullUtil.convertToString(rst.getString("RECOMMENDED_TYPE")).trim());
				if (rst.getString("RECOMMENDED_ID") != null && rst.getInt("RECOMMENDED_ID") != 0) {
					m.setRecommendedId(rst.getInt("RECOMMENDED_ID"));
					// setRecommendedCode(new MMember().find(String.valueOf(getRecommendedId())).getCode());
					Member member = new MMember().find(String.valueOf(m.getRecommendedId()));
					m.setRecommendedCode("");
					if (member != null) {
						m.setRecommendedCode(member.getCode());
					}
				} else {
					m.setRecommendedCode("");
				}
				m.setAgeMonth(rst.getInt("AGE_MONTH"));
				m.setCancelReason(ConvertNullUtil.convertToString(rst.getString("CANCEL_REASON")).trim());
		
				if (m.getExpiredDate() != null && !m.getExpiredDate().equals("")) {
					m.setDaysBeforeExpire(String.valueOf(DateToolsUtil.calDiffDate(DateToolsUtil.DAY, DateToolsUtil
							.getCurrentDateTime("dd/MM/yyyy"), m.getExpiredDate())));
				}
		
				if (m.getDaysBeforeExpire() != null && !m.getDaysBeforeExpire().equals("")) {
					if (Integer.parseInt(m.getDaysBeforeExpire()) < 0) {
						m.setExpired(true);
						m.setDaysBeforeExpire(SystemProperties.getCaption("MemberExpired", null));
					}
				}
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
				m.setDeliveryGroup(ConvertNullUtil.convertToString(rst.getString("DELIVERY_GROUP")));
				m.setExported(rst.getString("EXPORTED"));
				m.setIsvip(rst.getString("ISVIP"));
				m.setCreditcardExpired(ConvertNullUtil.convertToString(rst.getString("CREDITCARD_EXPIRED")).trim());
				// set display label
				//m.setDisplayLabel();
				m.setIsFreeOfChart(rst.getString("FREE_OF_CHART"));
				m.setPaymentType(rst.getInt("PAYMENT_TYPE"));
				
				m.setCreditCardNo(rst.getString("CARD_NO"));
				m.setCreditCardBank(rst.getString("CARD_BANK"));
				m.setOldPriceFlag(Utils.isNull(rst.getString("OLD_PRICE_FLAG")));
				
				countRecord++;
				if(countRecord > MAX_DISP_MEMBER-1){
					nextLoop = false;
					moreMaxRecord = true;
				}
				pos.add(m);
				
			}//while
			
			resultBean.setMoreMaxRecord(moreMaxRecord);
			resultBean.setResults(pos);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
			if(ps != null){
				ps.close();ps = null;
			}
			if(rst != null){
				rst.close();rst = null;
			}
		}
		return resultBean;
	}

	public Member[] search(String join,String whereCause) throws Exception {
		List<Member> pos = super.search(TABLE_NAME, COLUMN_ID,join, whereCause, Member.class);
		if (pos.size() == 0) return null;
		Member[] array = new Member[pos.size()];
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
	public boolean save(Member member, String userCode, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (member.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			String prefix = "";
			if( Utils.isNull(member.getCode()).equals("")){
			   member.setCode(new MemberDocumentProcess().getNextDocumentNo(userCode, prefix, activeUserID, conn));
			}
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
				DateToolsUtil.convertToTimeStamp(member.getFirstDeliverlyDate()),
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
				member.getIsFreeOfChart() != null ? member.getIsFreeOfChart() : "N",
				member.getPaymentType(),
				member.getCreditCardNo(),
				member.getCreditCardBank(),
				member.getCardName(),
				Utils.isNull(member.getOldPriceFlag()),
				member.getTotalOrderQty()
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
	public boolean save(Member member, int activeUserID, Connection conn) throws Exception {
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

		Object[] values = {
				id,
				ConvertNullUtil.convertToString(member.getCode()).trim(),
				ConvertNullUtil.convertToString(member.getName()).trim(),
				ConvertNullUtil.convertToString(member.getName2()).trim(),
				ConvertNullUtil.convertToString(member.getPersonIDNo()).trim(),
				activeUserID,
				ConvertNullUtil.convertToString(member.getEmail()).trim(),
				ConvertNullUtil.convertToString(member.getTerritory()).trim(),
				member.getChrolesterol(),
				DateToolsUtil.convertToTimeStamp(member.getBirthDay()),
				member.getMonthlyIncome(),
				ConvertNullUtil.convertToString(member.getMemberType()).trim(),
				DateToolsUtil.convertToTimeStamp(member.getRegisterDate()),
				DateToolsUtil.convertToTimeStamp(member.getFirstDeliverlyDate()),
				ConvertNullUtil.convertToString(member.getRecommendedBy()).trim(),
				ConvertNullUtil.convertToString(member.getCancelReason()).trim(),
				ConvertNullUtil.convertToString(member.getMemberLevel()).trim(),
				ConvertNullUtil.convertToString(member.getCustomerType()).trim(),
				ConvertNullUtil.convertToString(member.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(member.getVatCode()).trim(),
				ConvertNullUtil.convertToString(member.getPaymentMethod()).trim(),
				null,
				member.getOrderAmountPeriod(),
				ConvertNullUtil.convertToString(member.getShippingDate()).trim(),
				ConvertNullUtil.convertToString(member.getShippingTime()).trim(),
				member.getIsActive(),
				activeUserID,
				activeUserID,
				member.getRoundTrip(),
				ConvertNullUtil.convertToString(member.getRecommendedType()).trim(),
				member.getRecommendedId(),
				member.getExpiredDate().length() > 0 ? DateToolsUtil.convertToTimeStamp(member.getExpiredDate()) : null,
				member.getAgeMonth(), ConvertNullUtil.convertToString(member.getPartyType()).trim(),
				ConvertNullUtil.convertToString(member.getDeliveryGroup()).trim(), "N", "N",
				member.getIsvip() != null ? member.getIsvip() : "N",
				ConvertNullUtil.convertToString(member.getShippingTimeTo()), member.getCreditcardExpired().trim(),
				ConvertNullUtil.convertToString(member.getOccupation()).trim(), 
				member.getIsFreeOfChart(),
				
				member.getPaymentType(),
				member.getCreditCardNo(),
				member.getCreditCardBank(),
				member.getCardName(),
				Utils.isNull(member.getOldPriceFlag())
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
	public void setMemberAgeLevel(Member member) {
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
