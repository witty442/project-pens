package com.isecinc.pens.web.importall.page;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.importall.ImportAllForm;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.SQLHelper;
import com.pens.util.importexcel.ImportManualExcel;

public class ImportMasterOrderREPAction {
	
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm ImportAllForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try{
			//1 
			ImportAllForm = importExcelRepMinMax(ImportAllForm, user, request);
			
			//2 
			ImportAllForm = importExcelRepPriority(ImportAllForm, user, request);
			
			//3 PENSBI.BME_CONFIG_REP
			ImportAllForm = importExcelRepMat(ImportAllForm, user, request);
			
			request.setAttribute("Message", "Import Success");
	   }catch(Exception e){
		   e.printStackTrace();
		   logger.error(e.getMessage(),e);
	   }
	   return mapping.findForward("importAll");
	}
	
	public ImportAllForm importExcelRepMinMax(ImportAllForm ImportAllForm,User user,HttpServletRequest request)  throws Exception {
		logger.debug("importExcelRepMinMax");
		String productType =EnvProperties.getInstance().getProperty("product.type");
		try {
		     InputStream is = ImportAllForm.getDataFile().getInputStream();
		     if(is !=null){
		        ImportManualExcel.importExcel("ORACLE",productType,is,ImportAllForm.getDataFile().getFileName(),6,user.getUserName());
		     }
		} catch (Exception e) {
			
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ ไม่ถูกต้อง:"+e.toString());
		}finally{
		  
		  }
		return ImportAllForm;
	}
	
	public ImportAllForm importExcelRepPriority(ImportAllForm ImportAllForm,User user,HttpServletRequest request)  throws Exception {
		logger.debug("importExcelRepPriority");
		String productType =EnvProperties.getInstance().getProperty("product.type");
		try {
		     InputStream is = ImportAllForm.getDataFile2().getInputStream();
		     if(is != null){
		        ImportManualExcel.importExcel("ORACLE",productType,is,ImportAllForm.getDataFile2().getFileName(),5,user.getUserName());
		     }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ ไม่ถูกต้อง:"+e.toString());
		}finally{
		  
		  }
		return ImportAllForm;
	}
	
	public ImportAllForm importExcelRepMat(ImportAllForm ImportAllForm,User user,HttpServletRequest request)  throws Exception {
		logger.debug("importExcelRepMat");
		Connection conn = null;
		int u = 0;
		try {
		     InputStream is = ImportAllForm.getDataFile3().getInputStream();
		     if(is != null){
		    	 conn = DBConnection.getInstance().getConnectionApps();
		    	 conn.setAutoCommit(false);
		    	 
		    	 //delete old data all
		    	 SQLHelper.excUpdate(conn,"delete from PENSBI.BME_CONFIG_REP");
		    	 
		         ImportManualExcel.importExcel(conn,is,ImportAllForm.getDataFile3().getFileName(),4,user.getUserName());
		         conn.commit();
		     }
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ ไม่ถูกต้อง:"+e.toString());
		}finally{
		  if(conn != null){
			  conn.close();
		  }
		 }
		return ImportAllForm;
	}
}
