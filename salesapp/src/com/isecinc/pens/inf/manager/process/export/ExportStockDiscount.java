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

public class ExportStockDiscount {
public static Logger logger = Logger.getLogger("PENS");

public static String getSqlPrepareSelect(TableBean tableBean,User userBean){
	String sqlSelect  ="";
	sqlSelect = " SELECT 'H' AS	RECORD_TYPE	\n"+
		    " ,REQUEST_NUMBER \n"+
		    " ,REQUEST_DATE \n"+
		    " ,C.CODE as CUSTOMER_CODE \n"+
		    " ,M.DESCRIPTION \n"+
		    " ,M.line_amount \n"+
		    " ,M.vat_amount \n"+
		    " ,M.net_amount \n"+
		    " ,'"+userBean.getId()+"' as salesrep_id \n"+
			" ,'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME	\n"+
			" FROM t_stock_discount M  \n"+
			" ,m_customer C 	\n"+
			" WHERE M.CUSTOMER_ID = C.CUSTOMER_ID 	\n"+
			" AND   M.STATUS = 'SV' \n"+
			" AND   M.user_id = "+userBean.getId() +"\n"+
			" AND (M.EXPORTED  ='N' OR M.EXPORTED IS NULL OR TRIM(M.EXPORTED) ='') \n";
	
	return sqlSelect;
}
	/**
	 * readTableSaleOrder
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportStockDiscount(Connection conn,TableBean tableBean,User userBean) throws Exception{
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
				dataAppend.append(exportStockDiscountLine(conn,rs.getString("request_number"),sqlUpdateExportFlagList));

				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_stock_discount set exported ='Y' WHERE request_number = '"+rs.getString("request_number")+"'");
				
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
	private   StringBuffer exportStockDiscountLine(Connection conn,String requestNumber,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_stock_discount_line");
            List<ColumnBean> colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   d.REQUEST_NUMBER, \n"+
            	"	d.LINE_NUMBER,	\n"+
	            "	d.INVENTORY_ITEM_ID, \n"+
	            "	d.PRODUCT_NAME, \n"+
	            "	d.AR_INVOICE_NO, \n"+
	            "	d.pri_qty, \n"+
	            "	COALESCE(d.uom1_qty,0,d.uom1_qty) as uom1_qty, \n"+
	            "	COALESCE(d.uom2_qty,0,d.uom2_qty) as uom2_qty, \n"+
	            "	'CTN' as uom1, \n"+
	            "	d.uom2, \n"+
	            "	d.uom1_pac, \n"+
	            "	d.uom2_pac, \n"+
	            "	d.uom1_conv_rate, \n"+
	            "	d.uom2_conv_rate, \n"+
	            "	d.uom1_price, \n"+
	            "	d.line_amount, \n"+
	            "	d.vat_amount, \n"+
	            "	d.net_amount \n"+
	            "	FROM t_stock_discount h,t_stock_discount_line d 	\n"+
	            "   WHERE d.request_number = h.request_number \n"+
                "   AND d.request_number ='"+requestNumber+"' \n";
            logger.debug("sql: \n"+sql);
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
