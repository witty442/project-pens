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

public class ExportReceipt {
	public static Logger logger = Logger.getLogger("PENS");
	/** User Normal
	 * readTableSalesReceiptHeader
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportSalesReceiptHeaderMaya(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        int countTrans = 0;
        String lastAppen = Constants.delimeterPipeStr;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>(); 
		try{
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				//add Receipt Line Detail
				dataAppend.append(exportSalesReceiptItem(conn,countTrans,rs.getString("receipt_id"),rs.getString("receipt_no")));
				
				/** Set Data For Update ExportFlag **/
				sqlUpdateExportFlagList.add("update t_receipt set exported = 'Y' WHERE receipt_id="+rs.getString("receipt_id"));
			}//while		
			
			tableBean.setExportCount(countTrans);
			tableBean.setDataStrExport(dataAppend);
			tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	/**
	 * readTableSaleReceiptItem
	 * @param conn
	 * @param receiptId
	 * @return
	 * @throws Exception
	 */
	private   StringBuffer exportSalesReceiptItem(Connection conn,int countTrans,String receiptId,String receiptNo) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        int no = 0;
		try{
            /** Get Column Detail Receipt **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_receipt_line");
            List colReceiptLineList = ExportHelper.initColumn(orderDBean);
   
			// Create Credit Note Line Sent To Oracle
            String sql ="	Select 		\n"+
            "	   'D'	AS	RECORD_TYPE	,	\n"+
            "		@rownum:=@rownum+1  	AS	LINE_NO ,	\n"+
            "		'"+receiptNo+"'	AS	RECEIPT_NUMBER,		\n"+
            "		t_credit_note.credit_note_no	AS	AR_INVOICE_NO	,	\n"+
            "		null	AS	SALES_ORDER_NO	,		\n"+
            "		t_credit_note.total_amount	AS	INVOICE_AMOUNT	,	\n"+
            "		t_receipt_cn.CREDIT_AMOUNT	AS	CREDIT_AMOUNT	,	\n"+
            "		t_receipt_match_cn.PAID_AMOUNT	AS	PAID_AMOUNT	,	\n"+
            "		t_receipt_cn.REMAIN_AMOUNT	AS	REMAIN_AMOUNT	,	\n"+
            "		null	AS	DESCRIPTION	,	\n"+
            "	    (select max(value) from c_reference where code ='OrgID') AS ORG_ID, 		\n"+
            "	    null as order_number, \n"+
            
            "	    t_receipt_by.PAYMENT_METHOD  AS PAYMENT_METHOD ,\n"+
            
            "       /* CASE WHEN t_receipt_by.PAYMENT_METHOD ='CS' THEN 'N' ELSE '' END AS CASH_FLAG, */ \n"+
            "       t_receipt_by.WRITE_OFF AS WRITE_OFF, \n"+ 
            /******** new Requirement ************************************/
            "       t_receipt_by.bank AS	BANK, \n"+	
            "       ''	AS	BANK_BRANCH, \n"+	
            "       t_receipt_by.CREDIT_CARD_NO, \n"+	
            "       t_receipt_by.CREDITCARD_EXPIRED as CREDIT_CARD_EXPIRE_DATE, \n"+	
            "       t_receipt_by.CREDIT_CARD_TYPE ,	 \n"+
            /******** new Requirement ************************************/
            /** WIT Add 07/03/2011  **/
            "       '' AS ORDER_LINE_ID \n"+
            
            "		FROM 	\n"+
            "		t_receipt_cn ,	\n"+
            "		t_receipt_match_cn, 	\n"+
            "		t_receipt_by,		\n"+
            "	    t_credit_note,            \n"+
            "       t_receipt ,			\n"+	
            "		ad_user,			\n"+
            "	    (SELECT @rownum:=0) rowsnum    \n"+
            "		where 1=1		    \n"+
            "		and t_receipt.receipt_id = t_receipt_cn.receipt_id and t_receipt.user_id = ad_user.user_id \n"+
            "		and t_receipt_match_cn.receipt_cn_ID = t_receipt_cn.receipt_cn_ID	\n"+
            "	    and t_receipt_match_cn.receipt_by_ID = t_receipt_by.receipt_by_ID		\n"+
            "       and t_credit_note.credit_note_id = t_receipt_cn.credit_note_id  \n"+
            "	    and t_receipt_cn.receipt_id ="+receiptId;
			
			logger.debug("SQL for Credit Note(CN):"+sql);
            
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colReceiptLineList.size();i++){
					no++;
					ColumnBean colBean = (ColumnBean)colReceiptLineList.get(i);
					if(i==colReceiptLineList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
					
				}//for
				
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
			}//while
			
            sql ="	Select 		\n"+
            "	   'D'	AS	RECORD_TYPE	,	\n"+
            "		@rownum:=@rownum+1  	AS	LINE_NO ,	\n"+
            "		'"+receiptNo+"'	AS	RECEIPT_NUMBER,		\n"+
            "		t_receipt_line.AR_INVOICE_NO	AS	AR_INVOICE_NO	,	\n"+
            "		t_receipt_line.SALES_ORDER_NO	AS	SALES_ORDER_NO	,	\n"+
            "		t_receipt_line.INVOICE_AMOUNT	AS	INVOICE_AMOUNT	,	\n"+
            "		t_receipt_line.CREDIT_AMOUNT	AS	CREDIT_AMOUNT	,	\n"+
            "		t_receipt_match.PAID_AMOUNT	AS	PAID_AMOUNT	,	\n"+
            "		t_receipt_line.REMAIN_AMOUNT	AS	REMAIN_AMOUNT	,	\n"+
            "		t_receipt_line.DESCRIPTION	AS	DESCRIPTION	, 	\n"+
            "	    (select max(value) from c_reference where code ='OrgID') AS ORG_ID, 		\n"+
            "	    t_order.order_no as order_number, \n"+
           
            "	    t_receipt_by.PAYMENT_METHOD ,\n"+
            
            "       /* CASE WHEN t_receipt_by.PAYMENT_METHOD ='CS' THEN 'N' ELSE '' END AS CASH_FLAG, */ \n"+
            "       t_receipt_by.WRITE_OFF AS WRITE_OFF, \n"+ 
            /******** new Requirement ************************************/
            "       t_receipt_by.bank AS BANK, \n"+	
            "       ''	AS	BANK_BRANCH, \n"+	
            "       t_receipt_by.CREDIT_CARD_NO, \n"+	
            "       t_receipt_by.CREDITCARD_EXPIRED as CREDIT_CARD_EXPIRE_DATE, \n"+	
            "       t_receipt_by.CREDIT_CARD_TYPE ,	 \n"+
            /******** new Requirement ************************************/
            /** WIT Add 07/03/2011  **/
            "       '' AS ORDER_LINE_ID \n"+
            "		FROM 		\n"+
            "		t_receipt_line ,	\n"+
            "		t_receipt_match, 	\n"+
            "		t_receipt_by,		\n"+
            "	    t_order,             \n"+
            "       t_receipt ,			\n"+	
            "		ad_user,			\n"+
            "	    (SELECT @rownum:= "+totalRows+") rowsnum    \n"+
            "		where 1=1		    \n"+
            "		and t_receipt.receipt_id = t_receipt_line.receipt_id and t_receipt.user_id = ad_user.user_id \n"+
            "		and t_receipt_match.RECEIPT_LINE_ID = t_receipt_line.RECEIPT_LINE_ID	\n"+
            "	    and t_receipt_match.RECEIPT_BY_ID = t_receipt_by.RECEIPT_BY_ID		\n"+
            "       and t_receipt_line.order_id = t_order.order_id  \n"+
            "	    and t_receipt_line.receipt_id ="+receiptId;

            logger.debug("SQL Receipt:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colReceiptLineList.size();i++){
					no++;
					ColumnBean colBean = (ColumnBean)colReceiptLineList.get(i);
					if(i==colReceiptLineList.size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
					
				}//for
				
				/** Gen New Line **/
				dataAppend.append(Constants.newLine);
				
			}//while
			
			countTrans += totalRows;
			logger.debug("countTrans:"+countTrans);	
			
			return dataAppend;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
}
