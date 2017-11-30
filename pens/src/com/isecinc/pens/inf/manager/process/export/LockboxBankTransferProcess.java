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

public class LockboxBankTransferProcess {
	
	public static Logger logger = Logger.getLogger("PENS");
	
	/*public StringBuffer exportAllReceiptBy(Connection conn,TableBean tableBean,User userBean
			,String receiptId,String receiptById) throws Exception{
		StringBuffer dataAppend = new StringBuffer("");
		PreparedStatement  ps = null;
		ResultSet rs = null;
		String sql = "";
		String payMethod = "";
		String lockBoxNumber ="";
		String internalBank = "";
		try{
		      sql =" select receipt_by_id,payment_method,bank \n"
				  +" from t_receipt_by where receipt_id ="+receiptId+" \n"
				  +" and receipt_by_id ="+receiptById+" \n"
				  +" and write_off <> 'Y' \n";
		      logger.debug("sql:"+sql);
		      
              ps = conn.prepareStatement(sql);
              rs = ps.executeQuery();
              while(rs.next()){
            	  receiptById = rs.getInt("receipt_by_id")+"";
            	  payMethod = Utils.isNull(rs.getString("payment_method"));
            	  if(payMethod.equalsIgnoreCase("TR")){
            		  internalBank = Utils.isNull(rs.getString("bank"));
            	  }
            	  dataAppend.append(exportSalesReceiptLockBoxHeader(conn, tableBean, userBean
            			  , receiptId,receiptById,internalBank, payMethod));
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
		return dataAppend;
	}*/
	
	public   StringBuffer exportSalesReceiptLockBoxHeader(Connection conn,TableBean tableBean,User userBean
			,String receiptId,String receiptById, String internalBank,String payMethod) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		String sql = "";
        int i = 0;
        int countTrans = 0;
        int lastPosition = 0;
		try{
			/** Init Config Column */
			TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_lockbox_header");
            List<ColumnBean> columnList = ExportHelper.initColumn(orderDBean);
             
			sql = "	select 			\n"+
			"	'1'	AS	Record_Identifier,	\n"+
					
			"	(select b.lockbox_name  from c_reference c ,c_lockbox b \n"+
            "    where c.reference_id = b.reference_id \n"+
            "    and c.code='TransferBank' and c.value ='"+internalBank+"') AS	Lockbox_Number,	\n"+
           
            "	(select b.bank_origination_number  from c_reference c ,c_lockbox b \n"+
            "    where c.reference_id = b.reference_id \n"+
            "    and c.code='TransferBank' and c.value ='"+internalBank+"') AS	Origination,	\n"+
			
			/** Optional **/
			"   t_receipt.receipt_id     \n"+
			"	from t_receipt ,m_customer,			\n"+
            "	(SELECT @rownum:=0) a	\n"+
			"	where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID 			\n"+
			"   and t_receipt.receipt_id = "+receiptId+" 	\n"+
			"   group by t_receipt.internal_bank \n";

            logger.debug("SQL:"+sql); 
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				lastPosition = 0;
				for(i=0;i<columnList.size();i++){
					ColumnBean colBean = (ColumnBean)columnList.get(i);				
					if(lastPosition != 0 && colBean.getStartPosition() > lastPosition){
					   // append blank by 
						dataAppend.append(ExportHelper.appendByLength("",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition()-(lastPosition+1)));
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
							dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
							dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}else{
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
						   dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
						   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}
					//check Last Position
					lastPosition = colBean.getEndPosition();
					
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				dataAppend.append(exportSalesReceiptLockBoxBatchHeader(conn,tableBean,userBean
						,rs.getString("Lockbox_Number"),internalBank,payMethod
						,receiptId,receiptById));
				   
			 /****************************/
			}//while

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
	
	/**
	 * exportSalesReceiptLockBoxBatchHeader( LEVEL 2)
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public StringBuffer exportSalesReceiptLockBoxBatchHeader(Connection conn,TableBean tableBean 
			,User userBean,String lockBoxNumber,String internalBank,String payMethod
			,String receiptId,String receiptById) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		String sql = "";
        int i = 0;
        int totalRows = 0;
        int lastPosition = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
			/** Init Config Column */
			TableBean table = new TableBean();
			table.setTableName("t_lockbox_batch_header");
            List<ColumnBean> columnList = ExportHelper.initColumn(table);

			sql = "	select 			\n"+
			"	 '2'  AS	Record_Identifier,		\n"+
			"	 '"+lockBoxNumber+"' AS	Lockbox_Number,		\n"+ // = Internal_blank
			"	 t_receipt.receipt_no AS	Batch_Name,		\n"+ //BatchName running + rownum
			"	 t_receipt.receipt_date	AS	Deposit_Date,	\n";
			
            /** new Requirement  ****/
            if(payMethod.equalsIgnoreCase("TR")){
              sql += "   (select name from c_reference  where value = '"+internalBank+"' and CODE ='TransferBank' ) AS Destination_Account,	\n"+ 
                     "   (select name from c_reference  where value = '"+internalBank+"' and CODE ='TransferBank' ) AS Remittance_Bank_Name ,	\n"+
                     "   (select name from c_reference  where value = '"+internalBank+"' and CODE ='TransferBank' ) AS Remittance_Bank_Branch_Name,	\n";
            }
			/** optional  ******/
		sql +="	t_receipt.receipt_id,	\n"+
			"	t_receipt.receipt_date,	\n"+
			"	m_customer.CODE	as customer_code,\n"+
			"   '"+userBean.getCode()+"' as sales_code, \n"+
			"   receipt_amount, \n"+
			"   ("+
			"     select count(distinct payment_method,cheque_no) from t_receipt_by where receipt_by_id in( \n"+
			"     select receipt_by_id from t_receipt r , t_receipt_match m \n"+
			"       where r.receipt_id = m.receipt_id \n"+
			"       and r.receipt_id = t_receipt.receipt_id \n"+
			"       and m.receipt_by_id = "+receiptById+" \n"+
			"     ) and write_off <> 'Y' \n"+
			"   ) count_receipt_by \n"+
			"	from t_receipt ,m_customer,	\n"+
            "	(SELECT @rownum:=0) a	\n"+
			"	where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID \n"+
			"   and t_receipt.DOC_STATUS = 'SV' \n"+
			"   and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
			"	and m_customer.user_id =  "+userBean.getId()+"		\n"+
			"   and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL) \n"+
			"   and t_receipt.receipt_id = "+receiptId+" \n"+
			"	group by t_receipt.receipt_id,	\n"+
			"	t_receipt.receipt_date,	\n"+
			"	m_customer.CODE	\n";

            logger.debug("SQL:"+sql); 
            tableBean.setPrepareSqlSelect(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				totalRows++;
				lastPosition = 0;
				for(i=0;i<columnList.size();i++){
					ColumnBean colBean = (ColumnBean)columnList.get(i);				
					if(lastPosition != 0 && colBean.getStartPosition() > lastPosition){
					   // append blank by 
						dataAppend.append(ExportHelper.appendByLength("",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition()-(lastPosition+1)));
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
							dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
							dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}else{
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
						    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
						    dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}
					//check Last Position
					lastPosition = colBean.getEndPosition();
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** add Payment Item  */
				dataAppend.append(exportSalesReceiptLockBoxBatchPayment(conn,userBean, lockBoxNumber
						, rs.getString("Batch_Name"),receiptId,receiptById,payMethod));
	            
				/** Set Data For Update Exported Flag **/
				sqlUpdateExportFlagList.add("update t_receipt set exported = 'Y' WHERE receipt_id="+rs.getString("receipt_id"));
				
			}//while
			
			/** Set Key For Update Exported Flag  **/
			if(tableBean.getSqlUpdateExportFlagList() != null){
				tableBean.getSqlUpdateExportFlagList().addAll(sqlUpdateExportFlagList);
			}else{
				tableBean.setSqlUpdateExportFlagList(sqlUpdateExportFlagList);
			}
			
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

	/**
	 * exportSalesReceiptLockBoxBatchPayment  (LEVEL 3)
	 * @param conn
	 * @param userBean
	 * @param lockBoxId
	 * @param lockBatchId
	 * @return
	 * @throws Exception
	 */
	private   StringBuffer exportSalesReceiptLockBoxBatchPayment(Connection conn,User userBean
			,String lockBoxNumber,String batchName,String receiptId
			,String receiptById,String payMethod) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		String sql = "";
        int i = 0;
        int totalRows = 0;
		try{
			/** Init Config Column */
			TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_lockbox_batch_payment");
            List<ColumnBean> columnList = ExportHelper.initColumn(orderDBean);
            
            sql="	select	\n"+
            "	'3' AS Record_Identifier,	\n"+
            "	'"+lockBoxNumber+"' AS Lockbox_Number,	\n"+
            "	'"+batchName+"' AS Batch_Name,	\n"+
            "	(CASE WHEN t_receipt_by.payment_method = 'CH' THEN  t_receipt_by.CHEQUE_NO \n"+
            "          ELSE t_receipt.receipt_no  \n"+
            "    END ) AS Payment_Number,	\n"+
            "	ifnull(t_receipt_by.receipt_amount,0) AS Remittance_Amount,	\n"+
            "	t_receipt.receipt_date AS Deposit_Date,	\n"+
            "	'THB' AS Currency_Code,	\n"+
            "	m_customer.code AS Customer_Number,	\n"+
            "	( select max(a.REFERENCE_ID) from m_address a 	\n"+
            "	  where a.customer_id = m_customer.CUSTOMER_ID	\n"+
            "	  and a.PURPOSE ='B' )  AS Billing_Location,	\n"+
            "	(@rownum:=@rownum+1) AS Item_Number,	\n"+
            "   t_receipt_by.payment_method as payment_method_code, \n"+
            "   (select name from c_reference  where value = t_receipt_by.payment_method and CODE ='PaymentMethod' )as Payment_method ,\n";
            
            /** OLD CODE **/
            // "   (select name from c_reference  where value = t_receipt_by.bank and CODE ='Bank' )  AS Attribute_1,	\n"+ //CHEQUE BANK_NAME
           
            /** NEW CODE EDIT WIT :24/03/2011 **/
            if(payMethod.equalsIgnoreCase("CH")){
	           sql += "   t_receipt_by.bank AS Attribute_1 , \n"+
	                  "   t_receipt_by.CHEQUE_NO AS Attribute_2,	\n"+ 
	                  "   t_receipt_by.CHEQUE_DATE AS Attribute_3,  	\n";
            }else{
        	 sql += "   '' AS Attribute_1 , \n"+
                    "   '' AS Attribute_2,	\n"+ 
                    "   '' AS Attribute_3,  \n";
            }
            sql +="   '"+userBean.getCode()+"' AS Attribute_4,  	\n"+ //SALES_CODE
            "   '-' AS Attribute_5,  	\n"+ //TEMP_INVOICE WAIT
            
            // Pasuwat Wang-arrayagul
            // Add Write Off Amount To First Line
            "     (SELECT SUM(IF(@rownum=1,rb.receipt_amount,0)) FROM T_RECEIPT_BY rb WHERE rb.RECEIPT_ID = "+receiptId+" AND rb.WRITE_OFF = 'Y') as WriteOff_Amt \n "+
            
            "	from t_receipt ,	\n"+
            "	m_customer, 	\n"+
            "	t_receipt_by,	\n"+
            "   (SELECT @rownum:=0) a	\n"+
            "	where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID 	\n"+
            "	and t_receipt.RECEIPT_ID = t_receipt_by.RECEIPT_ID	\n"+
            "	and t_receipt.receipt_id = "+receiptId+"	\n"+
            "	and t_receipt_by.receipt_by_id = "+receiptById+"	\n"+
            "   and t_receipt_by.write_off ='N' \n";

            logger.debug("SQL:"+sql); 
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int lastPosition = 0;
			
			while(rs.next()){
				totalRows++;
				lastPosition = 0;
				for(i=0;i<columnList.size();i++){
					ColumnBean colBean = (ColumnBean)columnList.get(i);	
					if(lastPosition != 0 && colBean.getStartPosition() > lastPosition){
					   // append blank by 
						dataAppend.append(ExportHelper.appendByLength("",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition()-(lastPosition+1)));
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
							dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
							dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}else{
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
						   dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
						   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}
					//check Last Position
					lastPosition = colBean.getEndPosition();
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** Append payment Overflow **/
				dataAppend.append(
						exportSalesReceiptLockBoxBatchPaymentOverflow(conn,userBean, lockBoxNumber, batchName, 
						                                               rs.getString("Payment_Number"),rs.getString("Item_Number")
						                                               ,rs.getString("payment_method_code"),receiptId ,receiptById));
			}//while
			
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

/**
 * exportSalesReceiptLockBoxBatchPaymentOverflow (4)
 * @param conn
 * @param userBean
 * @param lockBoxNumber
 * @param batchName
 * @param paymentNumber
 * @param receiptId
 * @return
 * @throws Exception
 */
	private   StringBuffer exportSalesReceiptLockBoxBatchPaymentOverflow(Connection conn,User userBean
			,String lockBoxNumber,String batchName,String paymentNumber
			,String itemNumber,String paymentMethod
			,String receiptId,String receiptById) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		String sql = "";
        int i = 0;
        int totalRows = 0;
		try{
			/** Init Config Column */
			TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_lockbox_batch_payment_overflow");
            List columnList = ExportHelper.initColumn(orderDBean);
            
            String appendWhere = "";
            String appendWhereCN = "";
            if(Utils.isNull(paymentMethod).equals("CH")){ //Chuque Only
            	appendWhere   = " and t_receipt_by.CHEQUE_NO = '"+paymentNumber+"' \n";
            	appendWhereCN = " and t_receipt_by.CHEQUE_NO = '"+paymentNumber+"' \n";
            }else if(Utils.isNull(paymentMethod).equals("CS")){//Cash
            	appendWhere   = " and t_receipt_by.payment_method = 'CS' \n";
            	appendWhereCN = " and t_receipt_by.payment_method = 'CS' \n";
            }else if(Utils.isNull(paymentMethod).equals("TR")){//BankTransfer
            	appendWhere   = " and t_receipt_by.payment_method = 'TR' \n";
            	appendWhereCN = " and t_receipt_by.payment_method = 'TR' \n";
            }
            
            sql ="	select	\n"+
            "	'4' AS Record_Identifier,	\n"+
            "	'"+lockBoxNumber+"' AS Lockbox_Number,	\n"+
            "	'"+paymentNumber+"' AS Payment_Number,	\n"+
            "	t_receipt_line.ar_invoice_no AS Invoice,	\n"+
            /** Witty Edit 08032011  Case One Cheque two Invoice **/
            "	round(ifnull(t_receipt_match.paid_amount,0),2) AS Amount_Applied,	\n"+
            "	@rownum:=@rownum+1  AS Overflow_Sequence,	\n"+
            "	'0' AS Overflow_Indicator,	\n"+
            "	'THB' AS Currency_Code,	\n"+
            "	'"+itemNumber+"' AS Item_Number,	\n"+
            "	'"+batchName+"' AS Batch_Name	\n"+
            "	from 	\n"+
            "	t_receipt_line ,	\n"+
            "	t_receipt_match , \n"+
            "	t_receipt_by ,  \n"+
            "   (SELECT @rownum:=0) a	\n"+
            "	where 1=1	\n"+
            "   and t_receipt_line.RECEIPT_LINE_ID = t_receipt_match.RECEIPT_LINE_ID 	\n"+
            "   and t_receipt_match.RECEIPT_BY_ID = t_receipt_by.RECEIPT_BY_ID 	\n"+
            " "+appendWhere+
            "	and t_receipt_line.receipt_id ="+receiptId+"	\n"+
            /** Witty add new Code 09/03/2011 **/
            "   and t_receipt_by.write_off ='N' \n"+
            /** Witty add new Code 27/11/2017 **/
            "   and t_receipt_by.receipt_by_id ='"+receiptById+"' \n"+
            
            " UNION ALL  \n"+
            /** CN Record  ****/
            "	select	\n"+
            "	'4' AS Record_Identifier,	\n"+
            "	'"+lockBoxNumber+"' AS Lockbox_Number,	\n"+
            "	'"+paymentNumber+"' AS Payment_Number,	\n"+
            "	t_credit_note.credit_note_no AS Invoice,	\n"+
            /** Witty Edit 08032011  Case One Cheque two Invoice **/
            "	round(ifnull(t_receipt_match_cn.paid_amount,0),2) AS Amount_Applied,	\n"+
            "	@rownum:=@rownum+1  AS Overflow_Sequence,	\n"+
            "	'0' AS Overflow_Indicator,	\n"+
            "	'THB' AS Currency_Code,	\n"+
            "	'"+itemNumber+"' AS Item_Number,	\n"+
            "	'"+batchName+"' AS Batch_Name	\n"+
            "	from 	\n" +
            "   t_receipt_cn, \n"+
            "	t_credit_note ,	\n"+
            "	t_receipt_match_cn , \n"+
            "	t_receipt_by ,  \n"+
            "   (SELECT @rownum:=0) a	\n"+
            "	where 1=1	\n"+
            "   and t_receipt_cn.CREDIT_NOTE_ID = t_credit_note.CREDIT_NOTE_ID 	\n"+
            "   and t_receipt_match_cn.receipt_cn_id = t_receipt_cn.receipt_cn_id \n"+
            "   and t_receipt_match_cn.RECEIPT_BY_ID = t_receipt_by.RECEIPT_BY_ID 	\n"+
            " "+appendWhereCN+
            "	and t_receipt_cn.receipt_id ="+receiptId+"	\n"+
            /** Witty add new Code 09/03/2011 **/
            "   and t_receipt_by.write_off ='N' \n"+
            /** Witty add new Code 27/11/2017 **/
            "   and t_receipt_by.receipt_by_id ='"+receiptById+"' \n";
           
            logger.debug("SQL:"+sql); 
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int lastPosition = 0;

			while(rs.next()){
				totalRows++;
				lastPosition = 0;
				for(i=0;i<columnList.size();i++){
					ColumnBean colBean = (ColumnBean)columnList.get(i);		
					
					//logger.debug("isLastRecord:"+rs.isLast());
					if(rs.isLast()){
						if(lastPosition != 0 && colBean.getStartPosition() > lastPosition){
						   // append blank by 
							dataAppend.append(ExportHelper.appendByLength("",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition()-(lastPosition+1)));
							if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendLeft("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}else {
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendRight("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}
						}else{
							if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendLeft("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}else {
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendLeft("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}							
							}
						}
						
					}else{
						   if(lastPosition != 0 && colBean.getStartPosition() > lastPosition){
							   // append blank by 
								dataAppend.append(ExportHelper.appendByLength("",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition()-(lastPosition+1)));
								if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
									dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else {
									dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}else{
								if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
								   dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else {
								   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}
					}
					
					//check Last Position
					lastPosition = colBean.getEndPosition();
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
			}//while
			
			/** Count Transaction By All OverFlow Payment */
			LockboxProcess.countTrans += totalRows;
			
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
