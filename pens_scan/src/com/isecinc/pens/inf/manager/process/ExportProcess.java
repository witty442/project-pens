package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.Utils;

public class ExportProcess {

	public static Logger logger = Logger.getLogger("PENS");

	/**
	 * readTableDataDB
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 * Case : Normal
	 */
	public   TableBean exportDataDB(Connection conn,TableBean tableBean,User userBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
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
				dataAppend.append(Constants.newLine);//new line
			}//while
			
			//set TotalRows
			if( !Utils.isNull(dataAppend.toString()).equals("")){
			   tableBean.setExportCount(dataAppend.toString().split("\n").length);
			}
			
			/** Set Text Data To Export By Table ****/
			tableBean.setDataStrExport(dataAppend);
			
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
	  * updateInterfaceCustomer
	  * @param conn
	  * @param model
	  * @return
	  * @throws Exception
	  */
	 public int updateExportFlag(Connection conn,User userBean,TableBean tableBean) throws Exception {
			Statement st = null;
			String sql= "";
			int countUpdate =0;
			try {
				st = conn.createStatement();
				for (int i = 0;i<tableBean.getSqlUpdateExportFlagList().size() ; i++) {
				    sql = (String)tableBean.getSqlUpdateExportFlagList().get(i);
					logger.debug("updateSQL:"+sql);
					st.addBatch(sql);
			    }
				
			    countUpdate = st.executeBatch().length;
			    logger.debug("Result Update:"+countUpdate);
			    
			} catch (Exception ex) {
				throw ex;
			} finally {
				if(st != null){
					st.close();st = null;
				}
			}
			return countUpdate;
		}


		/**
	 * 
	 * @param conn
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public   TableBean exportBarcodeScan(Connection conn,TableBean tableBean,User userBean) throws Exception{
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
				dataAppend.append(exportBarcodeScanItem(conn,rs.getString("doc_no"),sqlUpdateExportFlagList));
				
				/** Set Data For Update InterfacesFlag **/
				sqlUpdateExportFlagList.add("update pensbme_barcode_scan set export_flag ='Y' WHERE doc_no = '"+rs.getString("doc_no")+"'");
				
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
	private   StringBuffer exportBarcodeScanItem(Connection conn,String docNo,List<String> sqlUpdateExportFlagList) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        String lastAppen = Constants.delimeterPipeStr;
        int totalRows = 0;
		try{
            /** Get Column Detail Order **/
            TableBean orderDBean = new TableBean();
            orderDBean.setTableName("pensbme_barcode_scan_item");
            List colOrderList = ExportHelper.initColumn(orderDBean);
            String sql = "select 'D' AS	RECORD_TYPE, \n"+
                "   d.DOC_NO	AS	DOC_NO, \n"+
            	"	d.LINE_ID	AS	LINE_ID,	\n"+
	            "	d.BARCODE	AS	BARCODE,	\n"+
	            "	d.Material_master	AS	Material_master,	\n"+
	            "	d.Group_code	AS	Group_code,	\n"+
	            "	d.PENS_ITEM	AS	PENS_ITEM	\n"+
	            "   FROM pensbme_barcode_scan_item d WHERE d.doc_no ='"+docNo+"'";
  
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
				//sqlUpdateExportFlagList.add("UPDATE t_order_line set exported ='Y' WHERE ORDER_LINE_ID ="+rs.getString("ORDER_LINE_ID"));
				
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
