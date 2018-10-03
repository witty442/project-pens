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

public class ExportReqPromotion {
	
	public static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * exportReqPromotion
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public  TableBean exportReqPromotion(Connection conn,TableBean tableBean,User userBean) throws Exception{
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
				/** add ReqPro Line Detail */
				dataAppend.append(exportReqPromotionLine(conn,rs.getString("request_no"),sqlUpdateExportFlagList));
				/** add ReqPro Cost Line Detail */
				dataAppend.append(exportReqPromotionCost(conn,rs.getString("request_no"),sqlUpdateExportFlagList));
					
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update t_req_promotion set exported ='Y' WHERE request_no = '"+rs.getString("request_no")+"'");
				
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
	 * exportReqPromotionLine
	 * @param conn
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	private   StringBuffer exportReqPromotionLine(Connection conn,String requestNo,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_req_promotion_line");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   d.REQUEST_NO  , \n"+
            	"	d.LINE_NO,	\n"+
	            "	d.PRODUCT_CODE,	\n"+
	            "	d.NEW_CTN,	\n"+
	            "	d.NEW_AMOUNT,	\n"+
	            "	d.STOCK_CTN,	\n"+
	            "	d.STOCK_QTY, \n"+
	            "	d.BORROW_CTN,	\n"+
	            "	d.BORROW_QTY, \n"+
	            "	d.BORROW_AMOUNT, \n"+
	            "   d.INVOICE_NO \n"+
	            "	FROM t_req_promotion_line d 	\n"+
	            "   WHERE d.request_no ='"+requestNo+"'\n";
  
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
	/**
	 * exportReqPromotionCost
	 * @param conn
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	private   StringBuffer exportReqPromotionCost(Connection conn,String requestNo,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("t_req_promotion_cost");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'C' AS	RECORD_TYPE, \n"+
                "   d.REQUEST_NO  , \n"+
            	"	d.LINE_NO,	\n"+
	            "	d.COST_DETAIL,	\n"+
	            "	d.COST_AMOUNT	\n"+
	            "	FROM t_req_promotion_cost d 	\n"+
	            "   WHERE d.request_no ='"+requestNo+"'\n";
  
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
