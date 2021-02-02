package com.isecinc.pens.inf.manager.process.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.Utils;

public class LockboxProcess {
	public static Logger logger = Logger.getLogger("PENS");
    public static int countTrans = 0;
	
	public TableBean exportSalesReceiptLockBoxProcess(Connection conn,TableBean tableBean,User userBean) throws Exception{
		StringBuffer dataAppend = new StringBuffer("");
		String receiptByIdTRSql = "";
		String receiptIdApplyTRAllBatch = "";
		try{
			//Gen Case TR
			List<DataBean> dataTRList = getAllReceiptByByType(conn, userBean, "TR");
			if(dataTRList != null && dataTRList.size() >0){
			for(int i=0;i<dataTRList.size();i++){
				DataBean data = dataTRList.get(i);
			    dataAppend.append(new LockboxBankTransferProcess().exportSalesReceiptLockBoxHeader(conn, tableBean, userBean
        			  , data.getReceiptId(),data.getReceiptById(),data.getBank(), data.getPaymentMethod()));
			    receiptByIdTRSql += data.getReceiptById()+",";
			  }//for
			}
			//ReceiptById TR
			if(receiptByIdTRSql.length() >1){
				receiptByIdTRSql = receiptByIdTRSql.substring(0,receiptByIdTRSql.length()-1);
			}
			
			// Get receiptIdApplyTRAllbatch
			receiptIdApplyTRAllBatch = getReceiptIdApplyTRAllBatch(conn, userBean);
			
            //Gen Case <> TR  not in (receiptByIdTRSql)
			dataAppend.append(new LockboxNormalProcess()
					.exportSalesReceiptLockBoxHeaderNormal(conn, tableBean, userBean
					, receiptByIdTRSql,receiptIdApplyTRAllBatch));
			  
            logger.debug("countTrans:"+LockboxProcess.countTrans);
  			
  			tableBean.setExportCount(LockboxProcess.countTrans);
  			tableBean.setDataStrExport(dataAppend);
  			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		}
		return tableBean;
	}
	
	
	public List<DataBean> getAllReceiptByByType(Connection conn,User userBean,String payMethod) throws Exception{
		List<DataBean> dataBeanList = new ArrayList<LockboxProcess.DataBean>();
		PreparedStatement  ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
			sql = "	select receipt_id ,receipt_by_id,payment_method,bank from t_receipt_by  \n"+
				  "	where t_receipt_by.receipt_id  in( \n"+
			      "   select t_receipt.receipt_id  \n"+
				  "	  from t_receipt ,m_customer	\n"+
				  "	  where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID \n"+
				  "   and t_receipt.DOC_STATUS = 'SV' \n"+
				  "   and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
				  "	  and m_customer.user_id =  "+userBean.getId()+"		\n"+
				  "   and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL) \n"+
			      "	 ) \n";
				  if(payMethod.equalsIgnoreCase("TR")){
			         sql +=" and payment_method ='TR' \n";
				  }else{
					 sql +=" and payment_method <>'TR' \n";
				  }
		      logger.debug("sql:"+sql);
		      
              ps = conn.prepareStatement(sql);
              rs = ps.executeQuery();
              while(rs.next()){
            	  DataBean data = new DataBean();
            	  data.setReceiptId(rs.getInt("receipt_id")+"");
            	  data.setReceiptById(rs.getInt("receipt_by_id")+"");
            	  data.setPaymentMethod(rs.getString("payment_method"));
            	  data.setBank(rs.getString("bank"));
            	  dataBeanList.add(data);
              }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
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
		return dataBeanList;
	}
	public String getReceiptIdApplyTRAllBatch(Connection conn,User userBean) throws Exception{
		String receiptIdTrAllBatch = "";
		PreparedStatement  ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
		      sql ="   select t_receipt.receipt_id  \n"+
			  "	  from t_receipt ,m_customer	\n"+
			  "	  where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID \n"+
			  "   and t_receipt.DOC_STATUS = 'SV' \n"+
			  "   and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
			  "	  and m_customer.user_id =  "+userBean.getId()+"		\n"+
			  "   and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL) \n";
			
		      logger.debug("sql:"+sql);
		      
              ps = conn.prepareStatement(sql);
              rs = ps.executeQuery();
              while(rs.next()){
            	 if(isApplyTRAllBatch(conn, rs.getString("receipt_id"))){
            		 receiptIdTrAllBatch += rs.getInt("receipt_id")+",";
            	 }
              }
              if(receiptIdTrAllBatch.length()>1){
            	  receiptIdTrAllBatch = receiptIdTrAllBatch.substring(0,receiptIdTrAllBatch.length()-1);
              }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
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
		return receiptIdTrAllBatch;
	}
	
	public boolean isApplyTRAllBatch(Connection conn,String receiptId){
		boolean applyAllBatch = true;
		PreparedStatement  ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
		      sql ="select count(*) as c from t_receipt_by where receipt_id ="+receiptId+" \n"
				  +" and write_off <> 'Y' and payment_method <> 'TR' \n";
		      logger.debug("sql:"+sql);
              ps = conn.prepareStatement(sql);
              rs= ps.executeQuery();
              if(rs.next()){
            	  if(rs.getInt("c") >0){
            		  applyAllBatch  = false;
            	  }
              }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
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
		return applyAllBatch;
	}
	
	public boolean isFoundBankTransfer(Connection conn,String receiptId){
		boolean found = false;
		PreparedStatement  ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
		      sql ="select count(*) as c \n"
				  + "from t_receipt_by where receipt_id ="+receiptId+" \n"
				  +" and write_off <> 'Y' and payment_method ='TR' \n";
		      logger.debug("sql:"+sql);
              ps = conn.prepareStatement(sql);
              rs= ps.executeQuery();
              if(rs.next()){
            	  if(rs.getInt("c") >0){
            		  found  = true;
            	  }
              }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
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
		return found;
	}
	
	private  class DataBean{
		String receiptId;
		String receiptById;
		String paymentMethod;
		String bank;
		
		
		public String getPaymentMethod() {
			return paymentMethod;
		}
		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}
		public String getBank() {
			return bank;
		}
		public void setBank(String bank) {
			this.bank = bank;
		}
		public String getReceiptId() {
			return receiptId;
		}
		public void setReceiptId(String receiptId) {
			this.receiptId = receiptId;
		}
		public String getReceiptById() {
			return receiptById;
		}
		public void setReceiptById(String receiptById) {
			this.receiptById = receiptById;
		}
		
	}
}
