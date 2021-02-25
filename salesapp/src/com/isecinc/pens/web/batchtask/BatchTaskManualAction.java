package com.isecinc.pens.web.batchtask;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class BatchTaskManualAction {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public String runBatchTask(User user,String pageName){
		String returnText = "search";
		BatchTaskDAO dao = new BatchTaskDAO();
		BatchTaskForm  batchTaskForm = new BatchTaskForm();
		String[] paramArr = null;
		Map<String ,BatchTaskInfo> paramMap = new HashMap<String, BatchTaskInfo>();
		List<BatchTaskInfo> btnList = new ArrayList<BatchTaskInfo>();
		List<BatchTaskInfo> paramList = new ArrayList<BatchTaskInfo>();
		BatchTaskInfo paramItem = null;
		logger.debug("BatchTask Prepare Form No ID");
		try {
			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),pageName);
			
			//Clear Form
			batchTaskForm.setMonitorBean(new MonitorBean());
			batchTaskForm.setMonitorItemList(null);
			//Get BatchTask INIT
			BatchTaskInfo taskInfo = new BatchTaskInfo();
			//getDescription 
			taskInfo.setDescription(getDescriptionByTaskname(pageName));
			//getDevInfo
			taskInfo.setDevInfo(getDevInfoByTaskname(pageName));

			//prepare BatchTask ParameterMap
			Map<String, String> batchParamMap = new HashMap<String, String>();
			
			//start BatchTask
			new BatchTaskManager().runBatchFromManualNoWait(user, pageName, batchTaskForm, batchParamMap);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			//conn.close();
		}
		return returnText;
	}
	private String[] getParamByTaskname(String taskName){
		String[] param = null;
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getParam", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (String[])ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private String getButtonNameByTaskname(String taskName){
		String param = "";
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getButtonName", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (String)ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private String getDescriptionByTaskname(String taskName){
		String param = "";
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getDescription", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (String)ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private String getDevInfoByTaskname(String taskName){
		String param = "";
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getDevInfo", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (String)ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	private List<BatchTaskListBean> getParamListBoxByTaskname(String taskName){
		List<BatchTaskListBean> listBoxBean = new ArrayList<BatchTaskListBean>();
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getParamListBox", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   listBoxBean = (List<BatchTaskListBean>)ob;
		   logger.debug("return:"+ob);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return listBoxBean;
	}
	public  BatchTaskDispBean getBatchDispByTaskname(String taskName){
		BatchTaskDispBean param = null;
		try{
		   Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+taskName+"Task");
   		   Object obj = cls.newInstance();
   		   
   		  //no paramater
   		   Class noparams[] = {};
   		
   		   Method method = cls.getDeclaredMethod("getBatchDisp", noparams);
		   Object ob =  method.invoke(obj, null);
		   
		   param = (BatchTaskDispBean)ob;
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
	
	private String getValidateScriptByTaskname(String taskName,String processName){
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
			r = DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
		}
		return r;
	}
}
