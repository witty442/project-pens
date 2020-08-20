package com.isecinc.pens.web.pd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;

public class PDReceiptDAO {

	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public static Receipt[] searchPDReceiptHistory(PDReceiptForm pdForm ,User user) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		List<Receipt> receiptList = new ArrayList<Receipt>();
		Receipt[] array = null;
		try {
			String orderNoFrom = pdForm.getReceipt().getOrderNoFrom();
			String orderNoTo = pdForm.getReceipt().getOrderNoTo();
			
			String receiptDateFrom = pdForm.getReceipt().getReceiptDateFrom();
			String receiptDateTo = pdForm.getReceipt().getReceiptDateTo();
			
			String sql = " select  o.order_id as receipt_id , \n";
			       sql+= " o.ORDER_NO AS RECEIPT_NO, o.ORDER_DATE AS RECEIPT_DATE, ORDER_TYPE, o.CUSTOMER_ID, \n";
			       sql+= " (select concat(code,concat('-',name)) from m_customer m where m.customer_id = o.customer_id) as CUSTOMER_NAME,"; 
				   sql+= "  '' as BANK, '' as CHEQUE_NO, his.CHEQUE_DATE, NET_AMOUNT as RECEIPT_AMOUNT, '' as INTERFACES, DOC_STATUS,  \n";
				   sql+= " USER_ID, o.CREATED_BY, UPDATED_BY, '' as CREDIT_CARD_TYPE, '' as DESCRIPTION, '' as PREPAID, NET_AMOUNT as APPLY_AMOUNT,  \n";
				   sql+= " '' as INTERNAL_BANK ,'' as ISPDPAID, his.PDPAID_DATE,his.PD_PAYMENTMETHOD  \n";
				   
	               sql+= " FROM  t_order o, t_pd_receipt_his his \n";
	               sql+= " WHERE o.order_no = his.order_no \n";
	               sql+= " and (his.exported is null or his.exported ='N') \n";
	              // sql+= " AND o.user_id ='"+user.getId()+"' \n";
	               
	              
	               if(!StringUtils.isEmpty(orderNoFrom)){
	            	  sql+="\n  AND o.ORDER_NO >= '"+orderNoFrom+"'";
	       		   }
	       		   if(!StringUtils.isEmpty(orderNoTo)){
	       			  sql+="\n  AND o.ORDER_NO <= '"+orderNoTo+"'";
	       		   }
	       		   if(!StringUtils.isEmpty(receiptDateFrom)){
	       			  sql+="\n  AND o.ORDER_DATE >= '"+DateToolsUtil.convertToTimeStamp(receiptDateFrom)+"'";
	       		   }
	       		   if(!StringUtils.isEmpty(receiptDateTo)){
	       			  sql+="\n  AND o.ORDER_DATE <= '"+DateToolsUtil.convertToTimeStamp(receiptDateTo)+"'";
	       		   }
			     
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
				
				while(rst.next()){
					Receipt m = new Receipt();
					m.setId(rst.getLong("receipt_id"));
					m.setReceiptNo(rst.getString("RECEIPT_NO").trim());
					m.setReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("RECEIPT_DATE")));
					m.setOrderType(rst.getString("ORDER_TYPE").trim());
					m.setCustomerId(rst.getLong("CUSTOMER_ID"));
					m.setCustomerName(rst.getString("CUSTOMER_NAME").trim());
					m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PD_PAYMENTMETHOD")).trim());
					m.setBank(ConvertNullUtil.convertToString(rst.getString("BANK")).trim());
					m.setChequeNo(ConvertNullUtil.convertToString(rst.getString("CHEQUE_NO")).trim());
					m.setChequeDate("");
					if (rst.getTimestamp("CHEQUE_DATE") != null)
						m.setChequeDate(DateToolsUtil.convertToString(rst.getTimestamp("CHEQUE_DATE")));
					m.setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));
					m.setInterfaces(rst.getString("INTERFACES").trim());
					m.setDocStatus(rst.getString("DOC_STATUS").trim());
					m.setSalesRepresent(new MUser().find(rst.getString("USER_ID")));
					m.setCreditCardType(ConvertNullUtil.convertToString(rst.getString("CREDIT_CARD_TYPE")).trim());
					m.setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
					m.setPrepaid(rst.getString("PREPAID").trim());
					m.setApplyAmount(rst.getDouble("APPLY_AMOUNT"));
					m.setInternalBank(ConvertNullUtil.convertToString(rst.getString("INTERNAL_BANK")));
					if (rst.getTimestamp("PDPAID_DATE") != null)
						m.setPdPaidDate(DateToolsUtil.convertToString(rst.getTimestamp("PDPAID_DATE")));
					receiptList.add(m);
				}
				
				//convert to Obj
				if(receiptList != null && receiptList.size() >0){
					array = new Receipt[receiptList.size()];
					array = receiptList.toArray(array);
				}else{
					array = null;
				}
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e2) {}
			}
		return array;
	}
	
	public static int updateReceiptCancelPdPaid(Connection conn,Receipt r) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "\n update t_receipt ";
	               sql +="\n set ISPDPAID='N' , PDPAID_DATE = NULL ,PD_PAYMENTMETHOD ='' ,CHEQUE_DATE=null ";
	               sql +="\n where receipt_no ='"+r.getReceiptNo()+"'";
	               
			logger.debug("SQL:"+sql);
			logger.debug("update receiptNo:"+r.getReceiptNo());
			
			ps = conn.prepareStatement(sql);
			
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	public static String getPdPaid(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String pdPaid = "";
		try {
			String sql = "\n select pd_paid from ad_user where user_name <> 'admin'";
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				pdPaid = Utils.isNull(rs.getString("pd_paid"));
			}
			return pdPaid;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			rs.close();
		}
	}
	
	public static int deletePdReceiptHis(Connection conn,Receipt r) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "\n delete from t_pd_receipt_his ";
	               sql +="\n where order_no ='"+r.getReceiptNo()+"'";
	               
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql);
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	public static int deleteReceiptPdPaidNo(Connection conn,Receipt r) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "\n delete from t_receipt_pdpaid_no ";
	               sql +="\n where order_no ='"+r.getReceiptNo()+"'";
	               
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql);
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
}
