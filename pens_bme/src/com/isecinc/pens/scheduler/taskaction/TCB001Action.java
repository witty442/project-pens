package com.isecinc.pens.scheduler.taskaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.scheduler.bean.FileBean;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;
import com.isecinc.pens.scheduler.utils.Constants;
import com.isecinc.pens.scheduler.utils.JobUtils;
import com.pens.util.Utils;


public class TCB001Action {
	protected static Logger logger = Logger.getLogger("PENS");
	public  ScheduleVO  execute(Connection conn ,ScheduleVO param) {
		try{
		/*	FileBean fileBean  = getDateForExport(conn, param);
			fileBean.setPath((String)EnvProperties.getInstance().getProperty("trex.export.path"));
			fileBean.setFolderName1(JobUtils.genName(param,Constants.TYPE_GENFOLDER_BY_CURRENTDATE));
			fileBean.setFolderName2(JobUtils.genName(param,Constants.TYPE_GENFOLDER_BY_ENTITY));
			
			fileBean.setFileName(JobUtils.genName(param,Constants.TYPE_GENFOLDER_BY_PRODUCT));*/
			
		    /** create Csv File */
			//CsvExportType csv = new CsvExportType();
		//	fileBean = csv.generateCsv(fileBean);
			/** create Excel File */
			//ExcelExportType excel = new ExcelExportType();
			//fileBean = excel.generateExcel(fileBean);
			
			/** Set NoOfRecord and SizeOfFile */
			param.setNoOfRecord("0");
			param.setSizeOfFile("0");
			
			//set status Complete
			param.setStatus(SchedulerConstant.STATUS_COMPLETE);
		}catch(Exception e){
			param.setStatus(SchedulerConstant.STATUS_FAIL);
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private  FileBean getDateForExport(Connection conn,ScheduleVO param) throws Exception {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	StringBuffer str = new StringBuffer("");
    	FileBean fileBean = new FileBean();
    	int  i = 0;
    	try{
    		/** find Last Date Export By Entity and Product */
    		String transDate ="20090201";//for test
    		
	    	StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT A.* FROM TCB_LOG_ACCUM A,TCB_PRODUCT C  \n");
			buffer.append("WHERE 1=1 \n");
		    buffer.append("AND A.TCB_TRANSACTION_DATE = '" + transDate+ "'\n");
			
			buffer.append("AND C.PRODUCT = A.PRODUCT_CODE \n"); 
			//buffer.append("AND A.CONSENT_TYPE_ID= '" + consent + "'\n"); 
			if( !"0".equals(Utils.isNull(param.getEntity()))){
			  buffer.append("AND C.ENTITY_ID = '" + param.getEntity() + "'\n"); 
			}
			if( !"0".equals(Utils.isNull(param.getProduct()))){
			  buffer.append("AND A.PRODUCT_CODE IN( SELECT PRODUCT FROM TCB_PRODUCT WHERE PRODUCT_NAME ='"+param.getProduct()+"') \n");
			}
			
			buffer.append("ORDER BY C.ENTITY_ID,C.PRODUCT ");
    	    
			logger.debug("sql:"+buffer.toString());
			
    	    ps = conn.prepareStatement(buffer.toString());

    	    rs = ps.executeQuery();
            HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row     = sheet.createRow((short)0); 
			/** header Column */
			row.createCell((short)0).setCellValue("ID"); 
			row.createCell((short)1).setCellValue("PRODUCT_CODE");
			row.createCell((short)2).setCellValue("TCB_USER_ID");
			row.createCell((short)3).setCellValue("APPLICATION_No");
			row.createCell((short)4).setCellValue("TCB_FIRST_NAME");
			row.createCell((short)5).setCellValue("TCB_LAST_NAME");
			row.createCell((short)6).setCellValue("TCB_ID_No");
			row.createCell((short)7).setCellValue("TCB_TRANSACTION_DATE");
			row.createCell((short)8).setCellValue("TCB_TIME");
			row.createCell((short)9).setCellValue("URL_IMAGE_CL");
			row.createCell((short)10).setCellValue("URL_IMAGE_ID");
			row.createCell((short)11).setCellValue("APP_IN");
			row.createCell((short)12).setCellValue("CONSENT_TYPE_ID");
    		while(rs.next()){
    			i++;
    			str.append(appendCsv(rs.getString("ID")));
    			str.append(appendCsv(rs.getString("PRODUCT_CODE")));
    			str.append(appendCsv(rs.getString("TCB_USER_ID")));
    			str.append(appendCsv(rs.getString("APPLICATION_No")));
    			str.append(appendCsv(rs.getString("TCB_FIRST_NAME")));
    			str.append(appendCsv(rs.getString("TCB_LAST_NAME")));
    			str.append(appendCsv(rs.getString("TCB_ID_No")));
    			str.append(appendCsv(rs.getString("TCB_TRANSACTION_DATE")));
    			str.append(appendCsv(rs.getString("TCB_TIME")));
    			str.append(appendCsv(rs.getString("URL_IMAGE_CL")));
    			str.append(appendCsv(rs.getString("URL_IMAGE_ID")));
    			str.append(appendCsv(rs.getString("APP_IN")));
    			str.append(appendCsv(rs.getString("CONSENT_TYPE_ID")));
    			str.append("\n");
    			//creatExcel
    			row   = sheet.createRow(i); 
    			/** header Column */
    			row.createCell((short)0).setCellValue(rs.getString("ID")); 
    			row.createCell((short)1).setCellValue(rs.getString("PRODUCT_CODE"));
    			row.createCell((short)2).setCellValue(rs.getString("TCB_USER_ID"));
    			row.createCell((short)3).setCellValue(rs.getString("APPLICATION_No"));
    			row.createCell((short)4).setCellValue(rs.getString("TCB_FIRST_NAME"));
    			row.createCell((short)5).setCellValue(rs.getString("TCB_LAST_NAME"));
    			row.createCell((short)6).setCellValue(rs.getString("TCB_ID_No"));
    			row.createCell((short)7).setCellValue(rs.getString("TCB_TRANSACTION_DATE"));
    			row.createCell((short)8).setCellValue(rs.getString("TCB_TIME"));
    			row.createCell((short)9).setCellValue(rs.getString("URL_IMAGE_CL"));
    			row.createCell((short)10).setCellValue(rs.getString("URL_IMAGE_ID"));
    			row.createCell((short)11).setCellValue(rs.getString("APP_IN"));
    			row.createCell((short)12).setCellValue(rs.getString("CONSENT_TYPE_ID"));
    		}
    		fileBean.setBookExcel(wb);
    		fileBean.setData(str);
    		fileBean.setNoOfRecord(i+"");
    		return fileBean;
    	}catch(Exception e){
    		throw e;
    	}finally{
    		if(ps != null){
    		   ps.close();
    		}
    	}
    }

	public String appendCsv(String str){
		return Utils.isNull(str).replaceAll("\n","")+",";
	}
	
}
