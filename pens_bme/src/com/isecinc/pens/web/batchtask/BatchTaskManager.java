package com.isecinc.pens.web.batchtask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

public class BatchTaskManager {
	
protected static Logger logger = Logger.getLogger("PENS");


public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		logger.debug("runBatch");
		BatchTaskForm batchTaskForm = (BatchTaskForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		BatchTaskProcessManager processManager =  new BatchTaskProcessManager();
		BatchTaskDAO dao = new BatchTaskDAO();
		boolean canRunBatch = false;
		EnvProperties env = EnvProperties.getInstance();
		String pageName = Utils.isNull(request.getParameter("pageName"));
		try {
				String status = dao.findControlMonitor(pageName);
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName(pageName);
					monitorModel.setType(pageName);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					Map<String,Map> prepareMap = prepareParam(batchTaskForm, request);
					batchParamMap = prepareMap.get("batchParamMap");
					monitorModel.setBatchParamMap(batchParamMap);
					
					//Set User
					monitorModel.setUser(userLogin);
					
					//set param BatchtaskInfo on screen
					Map<String, BatchTaskInfo> paramMapForm = prepareMap.get("paramMapForm");
					batchTaskForm.getTaskInfo().setParamMap(paramMapForm);

					//create Batch Task
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());

				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

   private Map<String ,Map> prepareParam(BatchTaskForm batchTaskForm, HttpServletRequest request){
	    Map<String,Map> map = new HashMap<String, Map>();
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
			        
			        batchParamMap.put(key, value);
			        
			        //set new value from screen
			        task.setParamValue(value);
			        paramMapForm.put(key, task);
				 }
			 }
			map.put("batchParamMap", batchParamMap);
			map.put("paramMapForm",paramMapForm);
			 
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return map;
   }
}
