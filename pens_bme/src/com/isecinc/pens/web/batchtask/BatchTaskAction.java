package com.isecinc.pens.web.batchtask;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * 
 * 
 * @author Witty.B
 * @version $Id: ConversionAction.java,v 1.0 19/10/2010 00:00:00
 * 
 */

public class BatchTaskAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("BatchTask Prepare Form");
		String returnText = "search";
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}

	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		BatchTaskForm batchTaskForm = (BatchTaskForm) form;
		String returnText = "search";
		BatchTaskDAO dao = new BatchTaskDAO();
		String pageName = Utils.isNull(request.getParameter("pageName"));
		String[] paramArr = null;
		Map<String ,BatchTaskInfo> paramMap = new HashMap<String, BatchTaskInfo>();
		BatchTaskInfo paramItem = null;
		logger.debug("BatchTask Prepare Form No ID");
		try {
			logger.debug("pageName:"+Utils.isNull(request.getParameter("pageName")) +",pageAction:"+Utils.isNull(request.getParameter("pageAction")));
			
			if("new".equalsIgnoreCase(Utils.isNull(request.getParameter("pageAction")))){
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),pageName);
				
				//Clear Form
				batchTaskForm.setMonitorBean(new MonitorBean());
				batchTaskForm.setMonitorItemList(null);
				//Get BatchTask INIT
				BatchTaskInfo taskInfo = new BatchTaskInfo();
				String[] paramAll = getParamByTaskname(pageName).split("\\$");
				String param = paramAll[0];
				taskInfo.setButtonName(paramAll[1]);
				
				if( !Utils.isNull(param).equals("")){
					paramArr = param.split("\\,");
					for(int i=0;i<paramArr.length;i++){
					     String[] criArr = paramArr[i].split("\\|");
					     logger.debug("paramName:"+paramArr[i]);
		
					     paramItem = new BatchTaskInfo();
					     paramItem.setParamName(criArr[0]);
					     paramItem.setParamLabel(criArr[1]);
					     paramItem.setParamType(criArr[2]);
					     paramItem.setParamValue(getDefaultValue(criArr[3]));
					     paramItem.setParamValid(criArr[4]);
					     
					     paramMap.put(paramItem.getParamName(),paramItem);
					 }
				}
				taskInfo.setParamMap(paramMap);
				
				//get Script validate
				String validateScript = getValidateScriptByTaskname(pageName);
				taskInfo.setValidateScript(validateScript);
				
				batchTaskForm.setTaskInfo(taskInfo);

				//clear search results
				batchTaskForm.setResults(null);
				batchTaskForm.setMonitorBean(new MonitorBean());
				batchTaskForm.setMonitorItemList(null);
				batchTaskForm.setMonitorItem(null);
			}else{
				batchTaskForm.setMonitorBean(new MonitorBean());
				batchTaskForm.setMonitorItemList(null);
				batchTaskForm.setMonitorItem(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
		}finally{
			//conn.close();
		}
		return returnText;
	}
	
	public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		return new BatchTaskManager().runBatch(mapping,form,request,response);
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("BatchTask Search Current Action");
		BatchTaskForm batchTaskForm = (BatchTaskForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		BatchTaskDAO dao = new BatchTaskDAO();
		String pageName = "";
		try {
			String timeInUse =batchTaskForm.getMonitorBean().getTimeInUse();
			pageName = Utils.isNull(request.getParameter("pageName"));
			logger.info("TimeInUse:"+timeInUse);
			logger.debug("pageName:"+pageName);
			
			/** Set Condition Search **/
			MonitorBean[] results = dao.findMonitorListNew(user,pageName);
			
			logger.debug("results Size:"+results.length);
			if (results != null && results.length > 0) {
				// Head Monitor 
				batchTaskForm.setResults(results);
				
				//Search interfaceResult (monitorItem)
				MonitorItemBean monitorItemBean = dao.findMonitorItemBean(user,results[0]);
				batchTaskForm.setMonitorItem(monitorItemBean);
			} else {
				request.setAttribute("Message", "Data not found");
			}
				
			batchTaskForm.getMonitorBean().setTimeInUse(timeInUse);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		BatchTaskForm tripForm = (BatchTaskForm) form;
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
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}
	
	private String getParamByTaskname(String taskName){
		String param = "";
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getParam", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (String)ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private String getValidateScriptByTaskname(String taskName){
		String param = "";
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getValidateScript", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (String)ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private String getDefaultValue(String defaultValue) throws Exception{
		String r = "";
		if(defaultValue.equalsIgnoreCase("SYSDATE")){
			r = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		}
		return r;
	}
	
	@Override
	protected void setNewCriteria(ActionForm form) {
		BatchTaskForm tripForm = (BatchTaskForm) form;
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
