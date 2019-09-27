package com.isecinc.pens.web.interfaces;

import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.MonitorItemDetailBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Constants;
import com.pens.util.EnvProperties;

/**
 * ConversionAction Class
 * 
 * @author Witty.B
 * @version $Id: ConversionAction.java,v 1.0 19/10/2010 00:00:00
 * 
 */

public class MonitorInterfacesAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Trip Prepare Form");
		InterfacesForm conversionForm = (InterfacesForm) form;
	
		String returnText = "prepare";
		try {
			//for test
			/*User user = new User();
			com.isecinc.core.bean.References ref = new com.isecinc.core.bean.References("AD","AD","AD");
			user.setUserName("ADMIN");
			user.setRole(ref);
			request.getSession().setAttribute("user",user);*/
			
			conversionForm.setMonitorBean(new MonitorBean());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		
		return returnText;
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Trip Prepare Form without ID");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		String returnText = "prepare";
		Connection conn = null;
		try {
			interfacesForm.setResults(null);
			//for test
			/*
			 *  SOMBOON   VAN
				WANCHAI   DD
				SOMCHAI   TT
			 */
			/*User user = new User();
		
			user.setUserName("SOMCHAI");
	        user.setPassword("1234");
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			user = new LoginProcess().login(user.getUserName(), user.getPassword(), conn);
			
			request.getSession().setAttribute("user",user);*/
			
			interfacesForm.setMonitorBean(new MonitorBean());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
		}finally{
			//conn.close();
		}
		return returnText;
	}

	
	
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Interfaces Search Current Action");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		InterfaceDAO dao = new InterfaceDAO();
		try {
			InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
			if(request.getAttribute("searchKey") != null){
				criteria.setSearchKey((String)request.getAttribute("searchKey"));
			}
			//interfacesForm.setCriteria(criteria);
			/** Set Condition Search **/
			logger.debug("name:"+interfacesForm.getMonitorBean().getName());
			
			MonitorBean[] results = dao.findMonitorDetailList(user,interfacesForm.getMonitorBean(),"monitor.submit_date");
			
			if (results != null && results.length > 0) {
				interfacesForm.getCriteria().setSearchResult(results.length);
				interfacesForm.setResults(results);
				//criteria.setMonitorBean((MonitorBean)results[0]);
				interfacesForm.setCriteria(criteria);
			} else {
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
			request.getSession().setAttribute("results", results);
		} catch (Exception e) {
			e.printStackTrace();
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}

	
	
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("getLog");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i = 0;
		EnvProperties env = EnvProperties.getInstance();
		String pathFull = "";
		try {
			 String fileName = request.getParameter("fileName")+"-"+request.getParameter("userName")+".log";
			 String transType = request.getParameter("transType");
			 if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.master.sales.in.result")+fileName;
			 }else if(Constants.TRANSACTION_TRANS_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.transaction.sales.in.result")+fileName;
			 }else if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.transaction.sales.in.result")+fileName;
			 }
			 logger.debug("fileName:"+fileName);
			 logger.debug("transaType:"+transType);
			 logger.debug("pathFull:"+pathFull);
			 
			 FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		     
			 //DISPALY  CSV FILE
			 //ftpManager.getDownloadFTPFileByName(pathFull,response);

			 String dataLog = "";//ftpManager.getDownloadFTPFileByName(pathFull);
			// logger.debug("dataLog:"+dataLog);
			 String htmlCode = "";
			 if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
				 htmlCode = MonitorExport.genHTMLCodeTransaction(dataLog).toString();
			 }else{
				 htmlCode = MonitorExport.genHTMLCode(dataLog).toString(); 
			 }
			 //logger.debug("htmlCode:"+htmlCode);
		     request.setAttribute("DATA_LOGS", htmlCode);
		     request.setAttribute("LOGS_NAME",fileName );
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("getLog");
	}
	
	
	public ActionForward downloadLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("getLog");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i = 0;
		EnvProperties env = EnvProperties.getInstance();
		String pathFull = "";
		try {
			 String fileName = request.getParameter("fileName");
			 String transType = request.getParameter("transType");
			 if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.master.sales.in.result")+fileName;
			 }else if(Constants.TRANSACTION_TRANS_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.transaction.sales.in.result")+fileName;
			 }else if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.transaction.sales.in.result")+fileName;
			 }
			 logger.debug("fileName:"+fileName);
			 logger.debug("transaType:"+transType);
			 logger.debug("pathFull:"+pathFull);
			 
			 //FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		    // ftpManager.getDownloadFTPFileByName(pathFull,response);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("getLog");
	}
	
	/**
	 * Search
	 */
	public ActionForward showItemExport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("ShowItemExport");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		InterfaceDAO dao = new InterfaceDAO();
		try {
			logger.debug("tableName:"+request.getParameter("tableName"));
			MonitorItemDetailBean[] results = dao.findMonitorItemDetailBeanList(new BigDecimal(request.getParameter("monitorItemId")));
			
			interfacesForm.setResultsItemDetail(results);
		    MonitorBean m = new MonitorBean();
		    MonitorItemBean i = new MonitorItemBean();
		    i.setTableName(request.getParameter("tableName"));
		    m.setMonitorItemBean(i);
		    interfacesForm.setMonitorBean(m);
		} catch (Exception e) {
			e.printStackTrace();
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("showItemExport");
	}
	
	/**
	 * getTextFile
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getTextFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("getTextFile");
		EnvProperties env = EnvProperties.getInstance();
		String pathFull = "";
		try {
			 String fileName = request.getParameter("fileName");;
			 String transType = request.getParameter("transType");
			 String type = request.getParameter("type");
			 String status = request.getParameter("status");
			 if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
				 if(type.equals(Constants.TYPE_EXPORT)){
					 if(fileName.indexOf("TRIP") != -1 
					   || fileName.indexOf("AUTHEN") != -1
					   || fileName.indexOf("SALES_INVENTORY") != -1){
						 pathFull =  env.getProperty("path.master.sales.in")+fileName;
					 }else{
				         pathFull =  env.getProperty("path.master.sales.out")+fileName;
					 }
				 }else{
					 //import 
					 if(fileName.indexOf("TRIP") != -1 
					   || fileName.indexOf("CUST") != -1
					   || fileName.indexOf("CUSTADDR") != -1
					   || fileName.indexOf("CUSTCONTACT") != -1
							 ){
						 if(status.equals("1")){ //success
							 pathFull =  env.getProperty("path.master.sales.in.processed")+fileName; 
						 }else{
							 pathFull =  env.getProperty("path.master.sales.in")+fileName; 
						 }
					 }else{
					    pathFull =  env.getProperty("path.master.sales.in")+fileName; 
					 }
				 }
			 }else if(Constants.TRANSACTION_TRANS_TYPE.equals(transType)){
				 if(type.equals(Constants.TYPE_EXPORT)){
					 pathFull =  env.getProperty("path.transaction.sales.out")+fileName; 
				 }else{
					 if((Constants.STATUS_SUCCESS+"").equals(status)){
					     pathFull =  env.getProperty("path.transaction.sales.in.processed")+fileName;
					 }else{
						 pathFull =  env.getProperty("path.transaction.sales.in")+fileName; 
					 }
				 }
			 }else if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
				 if(type.equals(Constants.TYPE_EXPORT)){
					 pathFull =  env.getProperty("path.transaction.sales.out")+fileName; 
				 }else{
					 if((Constants.STATUS_SUCCESS+"").equals(status)){
					     pathFull =  env.getProperty("path.transaction.sales.in.processed")+fileName;
					 }else{
						 pathFull =  env.getProperty("path.transaction.sales.in")+fileName; 
					 }
				 }
			 }
			 logger.debug("fileName:"+fileName);
			 logger.debug("transaType:"+transType);
			 logger.debug("pathFull:"+pathFull);
			 
			 FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		     //DISPLAY CSV FILE 
			// ftpManager.getDownloadFTPFileByName(pathFull,response);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("getLog");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		InterfacesForm tripForm = (InterfacesForm) form;
		
		try {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			request.setAttribute("searchKey", tripForm.getCriteria().getSearchKey());
			
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}

	
	@Override
	protected void setNewCriteria(ActionForm form) {
		InterfacesForm tripForm = (InterfacesForm) form;
		tripForm.setCriteria(new InterfacesCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
