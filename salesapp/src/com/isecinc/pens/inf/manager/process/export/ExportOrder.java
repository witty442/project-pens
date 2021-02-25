package com.isecinc.pens.inf.manager.process.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ColumnBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExportHelper;

public class ExportOrder {
	public static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * readTableSaleOrder
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportSaleOrder(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
        List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					if(i==tableBean.getColumnBeanList().size()-1){
						lastAppen = "";
					}else{
						lastAppen = Constants.delimeterPipeStr;
					}
					//logger.debug("colName["+colBean.getColumnName()+"]");
					if(colBean.getColumnName().equalsIgnoreCase("RECORD_TYPE")){
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs));
					}else{
						dataAppend.append(ExportHelper.covertToFormatExport(colBean,rs)).append(lastAppen);
					}	
				}//for
				/** Add New Line **/
				dataAppend.append(Constants.newLine);//new line
				/** add Order Line Detail */
				dataAppend.append(exportSalesOrderLine(conn,rs.getString("order_id"),sqlUpdateExportFlagList));
				
				/** add Order Payment Detail   Case DD Only */
				if(User.DD.equalsIgnoreCase(userBean.getType())){
				  dataAppend.append(exportSalesOrderPayment(conn,userBean,rs.getString("order_id")));
				}
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_order set exported ='Y' WHERE order_id = "+rs.getString("order_id"));
				
			}//while
			logger.debug("totalRows:"+totalRows);

			tableBean.setExportCount(totalRows);
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
	 * readTableSaleOrderLine
	 * @param conn
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	private   StringBuffer exportSalesOrderLine(Connection conn,String orderId,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_order_line");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   h.ORDER_NO	AS	ORDER_NUMBER, \n"+
            	"	d.LINE_NO	AS	LINE_NO,	\n"+
	            "	d.PRODUCT_ID	AS	PRODUCT_ID,	\n"+
	            "	d.UOM_ID	AS	UOM_ID,	\n"+
	            "	d.QTY	AS	QTY,	\n"+
	            "	d.PRICE	AS	PRICE,	\n"+
	            "	d.LINE_AMOUNT	AS	LINE_AMOUNT, \n"+
	            "	d.DISCOUNT	AS	DISCOUNT,	\n"+
	            "	d.TOTAL_AMOUNT	AS	TOTAL_AMOUNT, \n"+
	            "	(select max(value) from c_reference where code ='OrgID') AS ORG_ID,	\n"+
	            "	''	AS	WAREHOUSE,	\n"+
	            "	a.CODE	AS	SUBINVENTORY,	\n"+
	            "	d.REQUEST_DATE	AS	REQUEST_DATE,	\n"+
	            "	d.SHIPPING_DATE	AS	SHIPPING_DATE,	\n"+
	            "	d.PROMOTION	AS	PROMOTION,	\n"+
	            "	d.VAT_AMOUNT	AS	VAT_AMOUNT,	\n"+
	            "   d.ORDER_LINE_ID AS ORDER_LINE_ID, \n"+
	            "   d.ORG AS ORG , \n"+
	            "   d.SUB_INV AS SUB_INV \n"+
	            "	FROM t_order_line d 	\n"+
	            "	inner join t_order h	\n"+
	            "	on d.ORDER_ID = h.ORDER_ID	\n"+
	            "	left outer join ad_user a	\n"+
	            "	on h.USER_ID = a.USER_ID	\n"+
	            "   WHERE d.order_id ="+orderId+
	            "   and d.promotion = 'N' \n";
  
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
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
				
				/** Set Data For Update Exported Flag ='Y' **/
				sqlUpdateExportFlagList.add("UPDATE t_order_line set exported ='Y' WHERE ORDER_LINE_ID ="+rs.getString("ORDER_LINE_ID"));
				
			}//while
			logger.debug("totalRows:"+totalRows);	
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
	 * readTableSaleOrderPayment
	 * @param conn
	 * @param orderId
	 * @return
	 * @throws Exception
	 * WAIT : Spec
	 */
	private   StringBuffer exportSalesOrderPayment(Connection conn,User user,String orderId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_order_payment");
            List colOrderList = ExportHelper.initColumn(orderDBean);

            String sql  ="	select 			\n"+
            "	'P'	AS	RECORD_TYPE ,	\n"+
            "	o.ORDER_NO	AS	ORDER_NUMBER,	\n"+
            "	''	AS	ORDER_LINE_NO ,	\n"+
            "	@rownum:=@rownum+1  AS	PAYMENT_NUMBER,		\n"+
            "	'ORDER'	AS	PAYMENT_LEVEL_CODE,	\n"+
            "	'PREPAY'	AS	PAYMENT_COLLECTION_EVENT,	\n"+
            "	rb.PAYMENT_METHOD	AS	PAYMENT_TYPE_CODE,	\n"+
            "	rm.PAID_AMOUNT	AS	PREPAID_AMOUNT,	\n"+
            "	rb.CHEQUE_NO AS	CHECK_NUMBER,		\n"+
            "	'' AS	RECEIPT_METHOD_ID 		\n"+
            "	from			\n"+
            "	t_receipt_match rm, 			\n"+
            "	t_receipt_by rb, 			\n"+
            "	t_receipt_line rl, 			\n"+
            "	t_receipt r,			\n"+
            "	t_order o ,    			\n"+
            "  (SELECT @rownum:=0) a \n"+
            "	where	1=1		\n"+
            "	and r.DOC_STATUS = 'SV'			\n"+
            "	and rm.RECEIPT_ID = r.RECEIPT_ID			\n"+
            "	and rm.RECEIPT_BY_ID = rb.RECEIPT_BY_ID			\n"+
            "	and rm.RECEIPT_LINE_ID = rl.RECEIPT_LINE_ID			\n"+
            "	and rl.ORDER_ID = o.ORDER_ID			\n"+
            "	and r.ORDER_TYPE = '"+ExportHelper.getOrderType(user)+"' \n"+
            "   and o.ORDER_ID ="+orderId;
            
            logger.debug("SQL:"+sql);
            
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				for(i=0;i<colOrderList.size();i++){
					ColumnBean colBean = (ColumnBean)colOrderList.get(i);
					if(i==colOrderList.size()-1){
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
			logger.debug("totalRows:"+totalRows);	
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
