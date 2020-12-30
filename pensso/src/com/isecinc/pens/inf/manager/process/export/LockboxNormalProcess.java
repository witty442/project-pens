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

public class LockboxNormalProcess {
	
	public static Logger logger = Logger.getLogger("PENS");
	
	/**For User TT ONLY 
	 * exportSalesReceiptLockBoxHeader (LEVEL 1)
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public  StringBuffer exportSalesReceiptLockBoxHeaderNormal(Connection conn,TableBean tableBean
			,User userBean,String receiptByIdTRSql,String receiptIdApplyTRAllBatch) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
		String sql = "";
        int i = 0;
        int lastPosition = 0;
		try{
			/** Init Config Column */
			TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_lockbox_header");
            List<ColumnBean> columnList = ExportHelper.initColumn(orderDBean);
             
			sql = "	select 			\n"+
			"	'1'	AS	Record_Identifier,	\n"+
			"	CONCAT('LCK',SUBSTR(t_receipt.internal_bank,2,2)) AS	Lockbox_Number,	\n"+
			"	'123'	AS	Origination,	\n"+
			"   t_receipt.internal_bank     \n"+
			"	from pensso.t_receipt ,pensso.m_customer \n"+
            //"	,(SELECT @rownum:=0) a	\n"+
			"	where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID 			\n"+
			"   and t_receipt.DOC_STATUS = 'SV' 	\n"+
			"   and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
			"	and  m_customer.user_id =  "+userBean.getId()+"		\n"+
			"   and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL) \n";
			
			// Case normal not read receipt is apply TR all batch
			if( !Utils.isNull(receiptIdApplyTRAllBatch).equals("")){
				sql +="   and t_receipt.receipt_id not in("+receiptIdApplyTRAllBatch+")";
			}
			sql +="   group by t_receipt.internal_bank \n";

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
							dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
							dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}else{
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
						   dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
						   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}
					//check Last Position
					lastPosition = colBean.getEndPosition();
					
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** Add LockBox Batch Header  */
				dataAppend.append(new LockboxNormalProcess().exportSalesReceiptLockBoxBatchHeader(
						conn,tableBean,userBean
						,rs.getString("Lockbox_Number"),rs.getString("internal_bank")
						,receiptByIdTRSql,receiptIdApplyTRAllBatch));
				   
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
	public  StringBuffer exportSalesReceiptLockBoxBatchHeader(Connection conn,TableBean tableBean 
			,User userBean,String lockBoxNumber,String internalBank
			,String receiptByIdTRSql,String receiptIdApplyTRAllBatch) throws Exception{
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

			sql = "	select 	distinct \n"+
			"	 '2'  AS	Record_Identifier,		\n"+
			"	 '"+lockBoxNumber+"' AS	Lockbox_Number,		\n"+ // = Internal_blank
			"	 t_receipt.receipt_no AS	Batch_Name,		\n"+ //BatchName running + rownum
			"	 t_receipt.receipt_date	AS	Deposit_Date,	\n"+

			"   (select description from c_reference  where value = t_receipt.internal_bank and CODE ='InternalBank' ) AS Destination_Account,	\n"+ 
            "   (select description from c_reference  where value = t_receipt.internal_bank and CODE ='InternalBank' ) AS Remittance_Bank_Name ,	\n"+
            "   (select description from c_reference  where value = t_receipt.internal_bank and CODE ='InternalBank' ) AS Remittance_Bank_Branch_Name,	\n"+
            
			/** optional  ******/
			"	t_receipt.receipt_id,	\n"+
			"	t_receipt.receipt_date,	\n"+
			"	m_customer.CODE	as customer_code,\n"+
			"   '"+userBean.getCode()+"' as sales_code, \n"+
			"   receipt_amount \n"+
			"	from t_receipt ,m_customer	\n"+
			"	where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID \n"+
			"   and t_receipt.DOC_STATUS = 'SV' \n"+
			"   and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
			"	and m_customer.user_id =  "+userBean.getId()+"		\n"+
			"   and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL) \n"+
			"   and t_receipt.internal_bank = '"+internalBank+"' \n";
			
			// Case normal not read receipt is apply TR all batch
			if( !Utils.isNull(receiptIdApplyTRAllBatch).equals("")){
				sql +="   and t_receipt.receipt_id not in("+receiptIdApplyTRAllBatch+")";
			}
           // logger.debug("SQL:"+sql); 
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
							dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
							dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}else{
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
						    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
						    dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}
					//check Last Position
					lastPosition = colBean.getEndPosition();
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** add Payment Item  */
				dataAppend.append(exportSalesReceiptLockBoxBatchPayment(conn,userBean, lockBoxNumber
						, rs.getString("Batch_Name"),rs.getInt("receipt_id")+"",receiptByIdTRSql));
	            
				/** Set Data For Update Exported Flag **/
				sqlUpdateExportFlagList.add("update pensso.t_receipt set exported = 'Y' WHERE receipt_id="+rs.getString("receipt_id"));
				
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
	private   StringBuffer exportSalesReceiptLockBoxBatchPayment(Connection conn,User userBean,String lockBoxNumber
			,String batchName,String receiptId,String receiptByIdTRSql) throws Exception{
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
            "	(CASE WHEN t_receipt_by.payment_method <> 'CS' THEN  t_receipt_by.CHEQUE_NO \n"+
            "         ELSE t_receipt.receipt_no  \n"+
            "    END ) AS Payment_Number,	\n"+
            "	NVL(t_receipt_by.receipt_amount,0) AS Remittance_Amount, \n"+
            "	t_receipt.receipt_date AS Deposit_Date,	\n"+
            "	'THB' AS Currency_Code,	\n"+
            "	m_customer.code AS Customer_Number,	\n"+
            
            //"	t_receipt.bill_to_address_id AS Billing_Location, \n"+ //new code 
            
            "   (select max(address_id) from PENSSO.m_address "+
            "     where t_receipt.customer_id= m_address.customer_id "+
            "     and purpose ='B' "+
            "   )AS Billing_Location,"+
            
            "	rownum AS Item_Number,	\n"+
            "   t_receipt_by.payment_method as payment_method_code, \n"+
            "   (select name from c_reference  where value = t_receipt_by.payment_method \n"+
            "    and CODE ='PaymentMethod' )as Payment_method ,\n"+
            "   t_receipt_by.bank AS Attribute_1 , \n"+
            "   t_receipt_by.CHEQUE_NO AS Attribute_2,	\n"+ 
            "   t_receipt_by.CHEQUE_DATE AS Attribute_3,  	\n"+ 
            "   '"+userBean.getCode()+"' AS Attribute_4,  	\n"+ //SALES_CODE
            "   '-' AS Attribute_5 ,  	\n"+ //TEMP_INVOICE WAIT
            
            "   (SELECT SUM(Case when rownum=1 then rb.receipt_amount else 0 end) \n"+
            "    FROM PENSSO.T_RECEIPT_BY rb WHERE rb.RECEIPT_ID = "+receiptId+
            "    AND rb.WRITE_OFF = 'Y') as WriteOff_Amt , \n "+

            "   t_receipt.receipt_id \n"+
            "	from PENSSO.t_receipt ,	\n"+
            "	PENSSO.m_customer, 	\n"+
            "	PENSSO.t_receipt_by	\n"+
            "	where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID 	\n"+
            "	and t_receipt.RECEIPT_ID = t_receipt_by.RECEIPT_ID	\n"+
            "	and t_receipt.receipt_id = "+receiptId+"	\n"+
            "   and t_receipt_by.write_off ='N' \n";
            
            //not get receipt_by_id(TR)
            if( !Utils.isNull(receiptByIdTRSql).equals("")){
             sql += "  and t_receipt_by.receipt_by_id not in("+receiptByIdTRSql+") \n";
            }

            logger.debug("XX SQL:"+sql); 
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
							dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
							dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}else{
						if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
						   dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}else {
						   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
						}
					}
					//check Last Position
					lastPosition = colBean.getEndPosition();
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				
				/** Append payment Overflow **/
				dataAppend.append(
				exportSalesReceiptLockBoxBatchPaymentOverflow(
						conn, userBean, lockBoxNumber, batchName, 
						rs.getString("Payment_Number"),rs.getString("Item_Number"), 
						receiptId,rs.getString("payment_method_code"),receiptByIdTRSql ));
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
	private   StringBuffer exportSalesReceiptLockBoxBatchPaymentOverflow(Connection conn,User userBean,String lockBoxNumber
			,String batchName,String paymentNumber,String itemNumber,String receiptId
			,String paymentMethod,String receiptByIdTRSql) throws Exception{
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
            }
            
            sql ="	select	\n"+
            "	'4' AS Record_Identifier,	\n"+
            "	'"+lockBoxNumber+"' AS Lockbox_Number,	\n"+
            "	'"+paymentNumber+"' AS Payment_Number,	\n"+
            "	t_receipt_line.ar_invoice_no AS Invoice,	\n"+
            /** Witty Edit 08032011  Case One Cheque two Invoice **/
            "	round(NVL(t_receipt_match.paid_amount,0),2) AS Amount_Applied,	\n"+
            "	rownum  AS Overflow_Sequence,	\n"+
            "	'0' AS Overflow_Indicator,	\n"+
            "	'THB' AS Currency_Code,	\n"+
            "	'"+itemNumber+"' AS Item_Number,	\n"+
            "	'"+batchName+"' AS Batch_Name	\n"+
            "	from 	\n"+
            "	t_receipt_line ,	\n"+
            "	t_receipt_match , \n"+
            "	t_receipt_by   \n"+
            "	where 1=1	\n"+
            "   and t_receipt_line.RECEIPT_LINE_ID = t_receipt_match.RECEIPT_LINE_ID 	\n"+
            "   and t_receipt_match.RECEIPT_BY_ID = t_receipt_by.RECEIPT_BY_ID 	\n"+
            " "+appendWhere+
            "	and t_receipt_line.receipt_id ="+receiptId+"	\n"+
            /** Witty add new Code 09/03/2011 **/
            "   and t_receipt_by.write_off ='N' \n";

            //not get receipt_by_id(TR)
            if( !Utils.isNull(receiptByIdTRSql).equals("")){
             sql += "  and t_receipt_by.receipt_by_id not in("+receiptByIdTRSql+") \n";
            }
            
            sql +=" UNION ALL  \n"+
            /** CN Record  ****/
            "	select	\n"+
            "	'4' AS Record_Identifier,	\n"+
            "	'"+lockBoxNumber+"' AS Lockbox_Number,	\n"+
            "	'"+paymentNumber+"' AS Payment_Number,	\n"+
            "	t_credit_note.credit_note_no AS Invoice,	\n"+
            /** Witty Edit 08032011  Case One Cheque two Invoice **/
            "	round(NVL(t_receipt_match_cn.paid_amount,0),2) AS Amount_Applied,	\n"+
            "	rownum  AS Overflow_Sequence,	\n"+
            "	'0' AS Overflow_Indicator,	\n"+
            "	'THB' AS Currency_Code,	\n"+
            "	'"+itemNumber+"' AS Item_Number,	\n"+
            "	'"+batchName+"' AS Batch_Name	\n"+
            "	from 	\n" +
            "   t_receipt_cn, \n"+
            "	t_credit_note ,	\n"+
            "	t_receipt_match_cn , \n"+
            "	t_receipt_by   \n"+
            "	where 1=1	\n"+
            "   and t_receipt_cn.CREDIT_NOTE_ID = t_credit_note.CREDIT_NOTE_ID 	\n"+
            "   and t_receipt_match_cn.receipt_cn_id = t_receipt_cn.receipt_cn_id \n"+
            "   and t_receipt_match_cn.RECEIPT_BY_ID = t_receipt_by.RECEIPT_BY_ID 	\n"+
            " "+appendWhereCN+
            "	and t_receipt_cn.receipt_id ="+receiptId+"	\n"+
            "   and t_receipt_by.write_off ='N' \n";
            
            //not get receipt_by_id(TR)
            if( !Utils.isNull(receiptByIdTRSql).equals("")){
             sql += "  and t_receipt_by.receipt_by_id not in("+receiptByIdTRSql+") \n";
            }
            
            //logger.debug("SQL:"+sql); 
			ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
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
								    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}else {
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendRight("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}
						}else{
							if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendLeft("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}else {
								if(colBean.getColumnName().equalsIgnoreCase("Overflow_Indicator")){
									dataAppend.append(ExportHelper.appendLeft("9",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else{
								    dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}							
							}
						}
						
					}else{
						   if(lastPosition != 0 && colBean.getStartPosition() > lastPosition){
							   // append blank by 
								dataAppend.append(ExportHelper.appendByLength("",Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition()-(lastPosition+1)));
								if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
									dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else {
									dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}
							}else{
								if(colBean.getExternalFunction().equalsIgnoreCase("LEFT")){
								   dataAppend.append(ExportHelper.appendLeft(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
								}else {
								   dataAppend.append(ExportHelper.appendRight(ExportHelper.covertToFormatExport(conn,colBean,rs),Constants.INSERT_STR_DEFAULT_BLANK,colBean.getStartPosition(),colBean.getEndPosition()));
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
