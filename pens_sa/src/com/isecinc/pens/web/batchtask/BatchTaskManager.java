package com.isecinc.pens.web.batchtask;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class BatchTaskManager {
	
protected static Logger logger = Logger.getLogger("PENS");


public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		logger.debug("runBatch");
		BatchTaskForm batchTaskForm = (BatchTaskForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		String pageName = Utils.isNull(request.getParameter("pageName"));
		try {
			//logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
			/** Import Data */

			/** insert to monitor_interface **/
			MonitorBean monitorModel = new MonitorBean();
			monitorModel.setName(pageName);
			monitorModel.setType(pageName);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			
			/** Set Param Batch Map **/
			Map<String, String> batchParamMap = new HashMap<String, String>();
			Map<String,Object> prepareMap = prepareParam(batchTaskForm, request);
			batchParamMap = (Map)prepareMap.get("batchParamMap");
			monitorModel.setBatchParamMap(batchParamMap);
			/** Case Form File **/
			logger.debug("dataFromFile:"+batchTaskForm.getDataFormFile());
			if(batchTaskForm.getDataFormFile() != null){
				monitorModel.setDataFile(batchTaskForm.getDataFormFile());
			}
			
			//Set User
			monitorModel.setUser(userLogin);
			
			//New Code
			List<BatchTaskInfo> paramListForm = (List)prepareMap.get("paramListForm");
			batchTaskForm.getTaskInfo().setParamList(paramListForm);
			
			//create Batch Task
			MonitorBean m = createBatchTask(monitorModel,userLogin,request);
		   
			/** Set for Progress Bar Opoup **/
			request.setAttribute("action", "submited");
			request.setAttribute("id", m.getTransactionId());
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	public ActionForward runBatchFromPageByPopup(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("runBatchFromPopupPage");
		User userLogin = (User) request.getSession().getAttribute("user");
		String pageName = Utils.isNull(request.getParameter("pageName"));
		try {
			/** insert to monitor_interface **/
			MonitorBean monitorModel = new MonitorBean();
			monitorModel.setName(pageName);
			monitorModel.setType(pageName);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			
			/** Set Param Batch Map **/
			//Case popup from Get from session (by page set parameter)
			Map<String,String> batchParamMap = (HashMap<String, String>)request.getSession().getAttribute("BATCH_PARAM_MAP");
			monitorModel.setBatchParamMap(batchParamMap);
			
			/** Case Form File **/
			//Case popup from Get from session 
			logger.debug("dataFromFile:"+request.getSession().getAttribute("DATA_FILE"));
			monitorModel.setDataFile((FormFile)request.getSession().getAttribute("DATA_FILE"));
			
			//clear session from (by page prepare)
			request.getSession().removeAttribute("DATA_FILE");
			request.getSession().removeAttribute("PARAM_MAP");
			
			//Set User
			monitorModel.setUser(userLogin);
		
			//create Batch Task
			MonitorBean m = createBatchTask(monitorModel,userLogin,request);
		   
			/** Set for Progress Bar popup **/
			request.setAttribute("action", "submited");
			request.setAttribute("id", m.getTransactionId());
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("batchFromPopup");
	}
	public ActionForward runBatchFromPageByPopupNoWait(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		logger.debug("runBatchFromPageByPopupNoWait");
		BatchTaskForm batchTaskForm = (BatchTaskForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		BatchTaskDAO dao = new BatchTaskDAO();
		boolean canRunBatch = false;
		EnvProperties env = EnvProperties.getInstance();
		String pageName = Utils.isNull(request.getParameter("pageName"));
		try {
			//logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
			/** Import Data */

			/** insert to monitor_interface **/
			MonitorBean monitorModel = new MonitorBean();
			monitorModel.setName(pageName);
			monitorModel.setType(pageName);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			
			/** Set Param Batch Map **/
			//Case popup from Get from session (by page set parameter)
			Map<String,String> batchParamMap = (HashMap<String, String>)request.getSession().getAttribute(BatchTaskConstants.BATCH_PARAM_MAP);
			monitorModel.setBatchParamMap(batchParamMap);
			
			/** Case Form File **/
			//Case popup from Get from session 
			logger.debug("dataFromFile:"+request.getSession().getAttribute(BatchTaskConstants.DATA_FILE));
			monitorModel.setDataFile((FormFile)request.getSession().getAttribute(BatchTaskConstants.DATA_FILE));
			
			//clear session from (by page prepare)
			request.getSession().removeAttribute("DATA_FILE");
			request.getSession().removeAttribute("PARAM_MAP");
			
			//Set User
			monitorModel.setUser(userLogin);
		
			//create Batch Task
			MonitorBean m = createBatchTask(monitorModel,userLogin,request);
		   
			/** Set for Progress Bar Popup **/
			request.setAttribute("action", "submited");
			request.setAttribute("no_wait", "no_wait");
			request.setAttribute("id", m.getTransactionId());
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("batchFromPopupNoWait");
	}
	public MonitorBean createBatchTask(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnectionApps();
			
			//Insert BatchTask
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
				
			 //start Thread
		    new BatchTaskProcessWorker(monitorModel).start();
		    
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
	    return monitorModel;
	}

	/** NEW **/
   private Map<String ,Object> prepareParam(BatchTaskForm batchTaskForm, HttpServletRequest request){
	    Map<String,Object> mapAll = new HashMap<String, Object>();
		Map<String, String> batchParamMap = new HashMap<String, String>();
		List<BatchTaskInfo> paramListForm = batchTaskForm.getTaskInfo().getParamList();
		try{
			List<BatchTaskInfo> paramList = batchTaskForm.getTaskInfo().getParamList();
			 if( paramList != null && paramList.size() >0){
				 for(int i=0;i<paramList.size();i++){
			        BatchTaskInfo task = paramList.get(i); 

			        String value = Utils.isNull(request.getParameter(task.getParamName()));
			        batchParamMap.put(task.getParamName(), value);
			        
			        //set new value from screen
			        task.setParamValue(value);
			        paramListForm.set(i, task);
				 }
			 }
			 mapAll.put("batchParamMap", batchParamMap);
			 mapAll.put("paramListForm",paramListForm);
			 
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return mapAll;
   }
   
   /** OLD **/
   private Map<String ,Map> prepareParam_OLD(BatchTaskForm batchTaskForm, HttpServletRequest request){
	    Map<String,Map> mapAll = new HashMap<String, Map>();
		Map<String, String> batchParamMap = new HashMap<String, String>();
		Map<String, BatchTaskInfo> paramMapForm = batchTaskForm.getTaskInfo().getParamMap();
		try{
			Map<String,BatchTaskInfo> paramMap = batchTaskForm.getTaskInfo().getParamMap();
			 if( !paramMap.isEmpty()){
				 Iterator its = paramMap.keySet().iterator();
				 while(its.hasNext()){
			        String key = (String)its.next();
			        BatchTaskInfo task = paramMap.get(key);    

			        String value = Utils.isNull(request.getParameter(task.getParamName()));
			        logger.debug("request.getparameter("+task.getParamName()+")value("+value+")");
			        batchParamMap.put(key, value);
			        
			        //set new value from screen
			        task.setParamValue(value);
			        paramMapForm.put(key, task);
				 }
			 }
			 mapAll.put("batchParamMap", batchParamMap);
			 mapAll.put("paramMapForm",paramMapForm);
			 
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return mapAll;
  }
}
