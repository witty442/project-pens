package com.isecinc.pens.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.isecinc.core.Database;
import com.isecinc.core.bean.References;
import com.isecinc.core.init.I_Initial;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MProductCategory;

/**
 * Initial References
 * 
 * @author Atiz.b
 * @version $Id: InitialReferences.java,v 1.0 28/09/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InitialReferences extends I_Initial {

	public static final String CUSTOMER_TYPE = "CustomerType";
	public static final String TERRITORY = "Territory";
	public static final String PARTY_TYPE = "PartyType";
	public static final String SALES_GROUP = "SalesGroup";
	public static final String ORDER_TYPE = "OrderType";
	public static final String SHIPMENT = "Shipment";
	public static final String MEMBER_TYPE = "MemberType";
	public static final String MEMBER_STATUS = "MemberStatus";
	public static final String ROLE = "Role";
	public static final String VAT_CODE = "VatCode";
	public static final String PAYMENT_METHOD = "PaymentMethod";
	public static final String VAN_PAYMENT_METHOD = "VanPaymentMethod";
	public static final String PAYMENT_TERM = "PaymentTerm";
	public static final String UNCLOSED_REASON = "UnclosedReason";
	public static final String ROLE_TT = "RoleTT";
	public static final String ROLE_VAN = "RoleVAN";
	public static final String ROLE_DD = "RoleDD";
	public static final String ACTIVE = "Active";
	public static final String ALERT_PERIOD = "AlertPeriod";
	public static final String QTY_DELIVER = "QtyDeliver";
	public static final String ROUND_DELIVER = "RoundDeliver";
	public static final String DOC_RUN = "DocRun";
	public static final String DOC_STATUS = "DocStatus";
	public static final String BANK = "Bank";
	public static final String DELIVERY_GROUP = "DeliveryGroup";
	public static final String INTERNAL_BANK = "InternalBank";
	public static final String BRAND_LIST = "BrandList";
	public static final String ORGID_LIST = "OrgID"; 
	public static final String MOVEORDER = "MoveOrder"; 
	public static final String BILLPLAN = "BillPlan"; 
	public static final String BACKDATE_INVOICE = "backdateinvoice"; 
	public static final String CUST_SHOW_TRIP = "CustShowTrip"; 
	public static final String CREDIT_DATE_FIX = "CreditDateFix";
	public static final String VAN_DATE_FIX = "VanDateFix";
	public static final String TRANSFER_BANK = "TransferBank";
	public static final String ProdShowFileSize = "ProdShowFileSize";
	public static final String ADDRESS_MAYA = "ADDRESS_MAYA";
	public static final String PAYMENT_METHOD_BANK = "PaymentMethodBANK";
	public static final String CREDITCARD_TYPE = "CreditCardType";
	private static Hashtable<String, List<References>> referenes = new Hashtable<String, List<References>>();

	public void init() {}

	/**
	 * init with conn
	 */
	public void init(Connection conn) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM c_reference WHERE ISACTIVE = 'Y' AND IS_LOAD ='Y' \n");
			
			//*** WIT Edit 29/07/2554 : not Show BBL InternalBank  *******************************//
			sql.append(" AND REFERENCE_ID NOT IN(  \n");
			sql.append("	SELECT REFERENCE_ID FROM c_reference WHERE  CODE = 'InternalBank'  and VALUE = '001'   \n");
			sql.append(" )  \n");
			//*************************************************************************************//
			
			List<References> refList = Database.query(sql.toString(), null, References.class, conn);
			// logger.debug(refList);
			for (References r : refList) {
				//logger.debug(r);
				if (referenes.get(r.getCode()) == null) {
					List<References> tr = new ArrayList<References>();
					tr.add(r);
					referenes.put(r.getCode(), tr);
				} else {
					referenes.get(r.getCode()).add(r);
				}
			}
			
			//init BrandList
			MProductCategory prodC = new MProductCategory();
			referenes.put(BRAND_LIST,prodC.lookUpBrandList(null));
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public static Hashtable<String, List<References>> getReferenes() {
		return referenes;
	}
	
	public static List<References> getReferenes(String key,String value) {
		List<References> filter = new ArrayList<References>();
		try{
			List<References> refs =  referenes.get(key);
			//System.out.println("refs size:"+refs.size());
			if(refs != null && refs.size() > 0){
				for(int i=0;i<refs.size();i++){
					References r = (References)refs.get(i);
					//System.out.println("value["+value+"]key["+r.getKey()+"]code["+r.getCode()+"]");
					if(value.equals(r.getKey())){
						filter.add(r);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return filter;
	}
	
	public static References getReferenesByOne(String key,String code) {
		References re = null;
		try{
			List<References> refs =  referenes.get(key);
			//System.out.println("refs size:"+refs.size());
			if(refs != null && refs.size() > 0){
				for(int i=0;i<refs.size();i++){
					References r = (References)refs.get(i);
					logger.debug("key["+r.getKey()+"]value["+r.getCode()+"]");
					if(code.equals(r.getCode())){
						re = r;
						break;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return re;
	}
	
	public static List<References> getRef(Connection conn,String code) {
		return getRefModel(conn, code, "");
	}
	public static List<References> getRef(Connection conn,String code,String orderBy) {
		return getRefModel(conn, code, orderBy);
	}
	public static List<References> getRef(String code,String orderBy) {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getRefModel(conn, code, orderBy);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
		return null;
	}
	public static List<References> getRefModel(Connection conn,String code,String orderBy) {
		List<References> refList = new ArrayList<References>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT * FROM pens_shop.c_reference WHERE ISACTIVE = 'Y' AND CODE ='"+code+"' \n");
			if( !Utils.isNull(orderBy).equals("")){
				sql.append("ORDER BY "+orderBy +" \n");
			}
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs);
				refList.add(r);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}finally{
			try{
				if(ps != null){
					ps.close();
				}
				if(rs != null){
					rs.close();
				}
			}catch(Exception ee){}
		}
		return refList;
	}
	public static List<References> getRefBykey(Connection conn,String code,String key) {
		return getRefByKeyModel(conn, code,key, "");
	}
	public static List<References> getRefByKey(Connection conn,String code,String key,String orderBy) {
		return getRefByKeyModel(conn, code,key, orderBy);
	}
	public static List<References> getRefByKey(String code,String key,String orderBy) {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getRefByKeyModel(conn, code,key, orderBy);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
		return null;
	}
	public static List<References> getRefByKeyModel(Connection conn,String code,String key,String orderBy) {
		List<References> refList = new ArrayList<References>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT * FROM pens_shop.c_reference WHERE ISACTIVE = 'Y' AND CODE ='"+code+"' and name ='"+key+"'\n");
			if( !Utils.isNull(orderBy).equals("")){
				sql.append("ORDER BY "+orderBy +" \n");
			}
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs);
				refList.add(r);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}finally{
			try{
				if(ps != null){
					ps.close();
				}
				if(rs != null){
					rs.close();
				}
			}catch(Exception ee){}
		}
		return refList;
	}
}
