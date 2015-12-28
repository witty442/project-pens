package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.InterfaceUtils;

public class ExportICCProcess {
  
	public static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * importToDB
	 * @param conn
	 * @param initConfigMap
	 * @param tableBean
	 * @param dataTextLineArr
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	
	public  TableBean exportBillICC(Connection conn,TableBean tableBean,User userBean,Map<String, String> batchParamMap) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAll = new StringBuffer("");
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        int totalRows = 0;
        String lineStr = "";
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				lineStr = "";
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					
					lineStr += ExportHelper.covertToFormatExport(colBean,rs);
					
				}//for
				/** Add New Line **/
				logger.debug("Line length:"+lineStr.length());
				lineStr += Constants.newLine;//new line
				dataAppend.append(lineStr);
			}//while
			
			logger.debug("totalRows:"+totalRows);
			//Add Total record
			if(totalRows >0){
			  dataAll.append(InterfaceUtils.appendNumLeft(String.valueOf(totalRows),"0",10)+"\n");
			   dataAll.append(dataAppend);
			}
			
			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAll);
			
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
	
	public int validateFileExport(Connection connMonitor,MonitorBean monitorModel,LinkedHashMap<String,TableBean> controlTableMap,Map<String, String> batchParamMap) {
		String fileNameFixByDate = "";
		InterfaceDAO dao = new InterfaceDAO();
		int taskStatusInt = Constants.STATUS_SUCCESS;
		try{
			String billDateStr  = batchParamMap.get("TRANS_DATE").replaceAll("/", "");
			
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int j = 1; it.hasNext(); j++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				fileNameFixByDate = InterfaceUtils.genExportNameICC(tableName, batchParamMap);
				
				MonitorItemBean modelItem = new MonitorItemBean();
				modelItem.setMonitorId(monitorModel.getMonitorId());
				modelItem.setSource(tableBean.getSource());
				modelItem.setDestination(tableBean.getDestination());
				modelItem.setTableName(tableBean.getTableName());
				modelItem.setFileName(fileNameFixByDate);
				modelItem.setDataCount(0);
				modelItem.setFileSize("");
				modelItem.setSubmitDate(new Date());
				
				if(isExportByTransDate(connMonitor, tableName, billDateStr)){
					modelItem.setStatus(Constants.STATUS_FAIL);
					modelItem.setSuccessCount(0);
					modelItem.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก มีการ Exportไปแล้ว ");
					modelItem.setErrorCode("DuplicateExportFileException");
					
					dao.insertMonitorItem(connMonitor,modelItem);
					
					taskStatusInt = Constants.STATUS_FAIL;
				}

			}
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return taskStatusInt;
	}
	
	public boolean isExportByTransDate(Connection conn,String tableName,String transDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean exist = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from pensbme_icc_head where bill_date ='"+transDate+"' and interface_icc ='Y' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
				   exist = true;
				}
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return exist;
	} 
	
	public int validateOrcaleInvoice(Connection connMonitor,MonitorBean monitorModel,LinkedHashMap<String,TableBean> controlTableMap,Map<String, String> batchParamMap) {
		String fileNameFixByDate = "";
		InterfaceDAO dao = new InterfaceDAO();
		int taskStatusInt = Constants.STATUS_SUCCESS;
		try{
			String billDateStr  = batchParamMap.get("TRANS_DATE").replaceAll("/", "");
			
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int j = 1; it.hasNext(); j++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				fileNameFixByDate = InterfaceUtils.genExportNameICC(tableName, batchParamMap);
				
				MonitorItemBean modelItem = new MonitorItemBean();
				modelItem.setMonitorId(monitorModel.getMonitorId());
				modelItem.setSource(tableBean.getSource());
				modelItem.setDestination(tableBean.getDestination());
				modelItem.setTableName(tableBean.getTableName());
				modelItem.setFileName(fileNameFixByDate);
				modelItem.setDataCount(0);
				modelItem.setFileSize("");
				modelItem.setSubmitDate(new Date());
				
				if(isFoundOracleInvoiceNoEmty(connMonitor, tableName, billDateStr)){
					modelItem.setStatus(Constants.STATUS_FAIL);
					modelItem.setSuccessCount(0);
					modelItem.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก ยังไม่มีการบันทึก oracle invoice no");
					modelItem.setErrorCode("OracleInvoiceNoEmtpyException");
					
					dao.insertMonitorItem(connMonitor,modelItem);
					
					taskStatusInt = Constants.STATUS_FAIL;
				}

			}
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return taskStatusInt;
	}
	
	public boolean isFoundOracleInvoiceNoEmty(Connection conn,String tableName,String transDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean exist = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from pensbme_icc_head where bill_date ='"+transDate+"' and (oracle_invoice_no is null or trim(oracle_invoice_no) ='') \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
				   exist = true;
				}
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return exist;
	} 
}
