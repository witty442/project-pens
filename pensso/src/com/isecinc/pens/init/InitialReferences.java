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
import com.isecinc.pens.model.MProductCategory;
import com.pens.util.DBConnection;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

/**
 * Initial References
 * 
 * @author witty
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
	public static final String ROLE_NISSIN = "RoleNissin";
	public static final String ACTIVE = "Active";
	public static final String ALERT_PERIOD = "AlertPeriod";
	public static final String QTY_DELIVER = "QtyDeliver";
	public static final String ROUND_DELIVER = "RoundDeliver";
	public static final String DOC_RUN = "DocRun";
	public static final String DOC_STATUS = "DocStatus";
	public static final String RECEIPT_DOC_STATUS = "ReceiptDocStatus";
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
	public static final String TRANSFER_BANK_VAN = "TransferBankVAN";
	public static final String BACKDATE_INVOICE_STOCKRETURN = "backDateInvStkReturn"; 
	public static final String BACKDATE_INVOICE_STOCKDISCOUNT = "backDateInvStkDis"; 
	public static final String VATRATE_INVOICE_STOCKDISCOUNT = "vatRateInvStkDis"; 
	public static final String REASON_RETURN = "ReasonReturn"; 

	
	private static Hashtable<String, List<References>> referenes = new Hashtable<String, List<References>>();

	public void init() {}

	/**
	 * init with conn
	 */
	public void init(Connection conn) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM c_reference WHERE ISACTIVE = 'Y' and is_load ='Y' \n");
		
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
	
	/** keyAll is null get all record by code **/
	public static List<References>  getReferenesByManual(String code,String keyAll) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		List<References> refList = new ArrayList<References>();
		try {
			conn = DBConnection.getInstance().getConnection();
			
			sql.append("SELECT * FROM pensso.c_reference \n");
			sql.append("WHERE ISACTIVE = 'Y' and code ='"+code+"' \n");
			if( !Utils.isNull(keyAll).equals("")){
			  sql.append("AND value in("+SQLHelper.converToTextSqlIn(keyAll)+") \n");
			}
			sql.append(" order by reference_id asc");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			//(String code, String key, String name)
			while(rs.next()){
			   References r = new References();//(Utils.isNull(rs.getString("code")),Utils.isNull(rs.getString("value")),Utils.isNull(rs.getString("name")));
				
			    r.setCode(rs.getString("CODE"));
				r.setKey(rs.getString("VALUE"));
				r.setName(rs.getString("NAME"));
				r.setDesc(rs.getString("DESCRIPTION"));
			   
			   refList.add(r);
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}finally{
			try{
				if(conn !=null){
					conn.close();
				}
			}catch(Exception ee){}
		}
		return refList;
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
	public static List<References> getReferenceListByCode(String code) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getReferenceListByCode(conn, code);
		}catch(Exception e){
		  throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}

	}
	
	public static List<References> getReferenceListByCode(Connection conn,String code) {
		List<References> refList = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM c_reference WHERE ISACTIVE = 'Y' and code ='"+code+"' \n");
			refList = Database.query(sql.toString(), null, References.class, conn);
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return refList;
	}

}
