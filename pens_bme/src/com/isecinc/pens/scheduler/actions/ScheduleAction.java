package com.isecinc.pens.scheduler.actions;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.scheduler.bean.TaskConditionDTO;
import com.isecinc.pens.scheduler.dao.SearchTaskDAO;
import com.isecinc.pens.scheduler.forms.ScheduleForm;
import com.isecinc.pens.scheduler.manager.ScheduleServiceManager;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;
import com.isecinc.pens.scheduler.utils.DateUtil;
import com.isecinc.pens.scheduler.utils.JobUtils;
import com.isecinc.pens.scheduler.utils.SecurityUtils;
import com.isecinc.pens.web.imports.ImportCriteria;
import com.isecinc.pens.web.imports.ImportForm;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class ScheduleAction extends  I_Action
{
	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Interfaces Prepare Form");
		ImportForm importForm = (ImportForm) form;
		String returnText = "prepare";
		try {
			importForm.setPage(Utils.isNull(request.getParameter("page")));
			
			if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
				 request.getSession().setAttribute("dataList",null);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}
		return returnText;
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Prepare Form without ID");
		ScheduleForm f = (ScheduleForm) form;
		String returnText = "Success";
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
        	
        	System.out.println("action:"+action);
        	 
        	if("new".equalsIgnoreCase(action)){
        		 ArrayList jobList = SearchTaskDAO.findJobInitail("");
        		 request.getSession().setAttribute("jobList",jobList);
        	
        		 //init hourList
        		 List<References> hourList = new ArrayList<References>();
        		 String key = "";
        		 for(int i=0;i<24;i++){
        			 key = String.valueOf(i).length()==1?"0"+i:String.valueOf(i);
        			 hourList.add(new References(key, key)) ;
        		 }
        		 request.getSession().setAttribute("hourList",hourList);
        		 
        		//init minuteList
        		 ArrayList<References> minuteList = new ArrayList<References>();
        		 for(int i=0;i<60;i++){
        			 key = String.valueOf(i).length()==1?"0"+i:String.valueOf(i);
        			 minuteList.add(new References(key, key)) ;
        		 }
        		 request.getSession().setAttribute("minuteList",minuteList);
        		 
        	}
       
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null)conn.close();
		}
		return returnText;
	}
	
	/**
	 * Search
	*/
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Interfaces Search Current Action");
		ScheduleForm f = (ScheduleForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "Success";
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
	        TaskConditionDTO dto = getSearchCondition(f);
		    dataList = SearchTaskDAO.findTaskByCondition(dto,conn);

		    request.getSession().setAttribute("dataList",dataList);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
		return returnText;
	}
	
	public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ScheduleForm f = (ScheduleForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
	    logger.debug("Action Run Batch");
		try{
			// Job & trigger information
		     ScheduleVO param = covertToScheduleVO(f,request);
			 conn = DBConnection.getInstance().getConnection();
			 
			 logger.debug("1.groupid:"+param.getGroupId());
			 //run job
			 ScheduleServiceManager service = new  ScheduleServiceManager();
			 service.runJob(param,conn);
			 
			 //search task 
			 TaskConditionDTO dto = getSearchCondition(f);
	         dataList = SearchTaskDAO.findTaskByCondition(dto,conn);
	         
	         request.getSession().setAttribute("dataList",dataList);
		
		}catch(Exception e){
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
        return mapping.findForward("Success");
	}
	
	public ActionForward regen(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ScheduleForm f = (ScheduleForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
	    logger.debug("Action Regen Batch");
	    Map<String, String> paramMap = new HashMap<String, String>();
		try{
		     //Get Param From Job_Init
			 String programId = Utils.isNull(request.getParameter("programId"));
			 ScheduleVO param = SearchTaskDAO.getJobInit(programId);
			 
			 //Get Parameter Regen\
			 String paramQtr = param.getParamRegen();
			 if( !"".equals(paramQtr)){
			    String[] paramArr = paramQtr.split("\\,");
			    if(paramArr.length >0){ 
					 for(int i=0;i<paramArr.length;i++){
					   String[] paramTemp = paramArr[i].split("\\|");
					   String paramName = paramTemp[0];
					   String paramType = paramTemp[1];
					   String paramValue = Utils.isNull(request.getParameter(paramName));
					   logger.debug(paramName+":"+paramValue);
					   
					   paramMap.put(paramName, paramValue);
					 }
			    }
			 }
			 param.setProgramName(param.getProgramName()+"(regen)");
			 param.setGroupId(Utils.isNull(SchedulerConstant.SCHEDULE_TYPE_NOW));// group by type schedule
			 param.setType(Utils.isNull(SchedulerConstant.SCHEDULE_TYPE_NOW));
			 //Gen JobName
			 param.setJobId(JobUtils.genRegenJobId(param));
			 //set localPath
			 param.setLocalPath(request.getRealPath("temps"));
			 param.setParamMap(paramMap);
			 
			 //get Connection 
			 conn = DBConnection.getInstance().getConnection();
			 
			 //run job
			 ScheduleServiceManager service = new  ScheduleServiceManager();
			 service.runTypeScheduleNowRegen(conn,param);
			 
			 request.setAttribute("regenSuccess", "Success");
			 
		}catch(Exception e){
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
        return mapping.findForward("Regen");
	}
	
	public ActionForward deleteBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ScheduleForm f = (ScheduleForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
	    logger.debug("Action Delete Batch");
		try{
			// Job & trigger information
		     ScheduleVO param = new ScheduleVO();
		     param.setNo(new BigDecimal(Utils.isNullDefaultZero(request.getParameter("selectNoDelete"))));
			 conn = DBConnection.getInstance().getConnection();
			 
			 //run job
			 ScheduleServiceManager service = new  ScheduleServiceManager();
			 service.deleteJob(param,conn);
			 
			 //search task 
			 TaskConditionDTO dto = getSearchCondition(f);
	         dataList = SearchTaskDAO.findTaskByCondition(dto,conn);
	         
	         request.getSession().setAttribute("dataList",dataList);
		
		}catch(Exception e){
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
        return mapping.findForward("Success");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ImportForm tripForm = (ImportForm) form;
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
		return "Success";
	}

	
	@Override
	protected void setNewCriteria(ActionForm form) {
		ImportForm tripForm = (ImportForm) form;
		tripForm.setCriteria(new ImportCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    
    private ScheduleVO covertToScheduleVO(ScheduleForm f,HttpServletRequest request) throws Exception{
  
    	String taskName = Utils.isNull(f.getTaskName());//dynaForm.get("taskName"));
		String hour = Utils.isNull(f.getHour());//dynaForm.get("hour"));
		String minute = Utils.isNull(f.getMinute());//dynaForm.get("minute"));
		String nDay = Utils.isNull(f.getNDay());//dynaForm.get("nDay"));
		String nMonth = Utils.isNull(f.getNMonth());//dynaForm.get("nMonth"));
		String nthDay = Utils.isNull(f.getNDay());//dynaForm.get("nthDay"));
		String[] months = Utils.isNullArray(f.getMonth());//dynaForm.get("month"));
		
		String programNames = f.getProgramId();
		String progs[] = programNames.split(",");
		String programId = Utils.isNull(progs[0]);//dynaForm.get("programId"));
		String programName = Utils.isNull(progs[1]);//dynaForm.get("programId"));
		
		logger.debug("run:"+f.getRun());
		
		// prepare ScheduleParameter
		 ScheduleVO param = new ScheduleVO();
		 param.setGroupId(Utils.isNull(f.getRun()));// group by type schedule
		 param.setProgramId(programId);
		 param.setProgramName(programName);
		 param.setType(Utils.isNull(f.getRun()));
		 param.setDays(Utils.isNullArray(f.getDay()));
		 param.setStartDate(Utils.isNull(f.getStartDate()));
		 param.setStartHour(Utils.isNull(f.getStartHour()));
		 param.setStartMinute(Utils.isNull(f.getStartMinute()));
		 param.setUserId(SecurityUtils.getInstance().getUserId(request));
		 param.setEveryDay(f.getEveryDay());
		 param.setNDay(f.getNDay());
		 
		 //Gen JobName
		 param.setJobId(JobUtils.genJobId(param));
		 
		 //set localPath
		 param.setLocalPath(request.getRealPath("temps"));
		
    	return param;
    }

    private TaskConditionDTO getSearchCondition(ScheduleForm dynaForm)throws Exception{
        String dateFormat = "dd/MM/yyyy" ;//Constant.DATE_FORMAT_ddMMYYYY_WITH_SLASH;
        String dateFormatWithTime = "dd/MM/yyyy HH:mm";// Constant.DATE_FORMAT_ddMMYYYY_WITH_SLASH_WITH_TIME;
        
        String no = "";//dynaForm.getSearchId();//(String) dynaForm.get("searchId");
        String taskName = "";// dynaForm.getSearchTaskName();// (String) dynaForm.get("searchTaskName");		
		String createDateFromStr = "";//dynaForm.getSearchCreateDateFrom();//(String) dynaForm.get("searchCreateDateFrom");
		String createDateToStr = "";//dynaForm.getSearchCreateDateTo();//(String) dynaForm.get("searchCreateDateTo");;
		String tranDateSchFromStr = "";//dynaForm.getTranDateSchFrom();//(String) dynaForm.get("tranDateSchFrom");
		String tranDateSchToStr = "";//dynaForm.getTranDateSchTo();//(String) dynaForm.get("tranDateSchTo");
		String createTimeFromHourStr ="";// dynaForm.getSearchTimeFromHour();// (String) dynaForm.get("searchTimeFromHour");
		String createTimeFromMinStr ="";// dynaForm.getSearchTimeFromMinute();//(String) dynaForm.get("searchTimeFromMinute");
		String createTimeToHourStr ="";// dynaForm.getSearchTimeToHour();//(String) dynaForm.get("searchTimeToHour");
		String createTimeToMinStr ="";// dynaForm.getSearchTimeToMinute();//(String) dynaForm.get("searchTimeToMinute");
		String entity = "";//dynaForm.getEntitySch();//(String) dynaForm.get("entitySch");
		String product ="";// dynaForm.getProductSch();//(String) dynaForm.get("productSch");
		
		Timestamp createDateFrom = null;
		boolean createDateFromWithTime = false;
		if (createDateFromStr != null && !"".equals(createDateFromStr)) {
		    
		    if (createTimeFromHourStr != null && !"".equals(createTimeFromHourStr) &&
		        createTimeFromMinStr != null && !"".equals(createTimeFromMinStr)) {
		        SimpleDateFormat format = new SimpleDateFormat(dateFormatWithTime);
		        
		        createDateFrom = new Timestamp(format.parse(createDateFromStr + " " + createTimeFromHourStr + ":" + createTimeFromMinStr).getTime());
		        createDateFromWithTime = true;
		        
		    } else {	// No time
			    createDateFrom = new Timestamp(DateUtil.parse(createDateFromStr,dateFormat).getTime());
		    }
		}
		
		Timestamp createDateTo = null;
		boolean createDateToWithTime = false;
		if (createDateToStr != null && !"".equals(createDateToStr)) {
		    
		    if (createTimeToHourStr != null && !"".equals(createTimeToHourStr) &&
			    createTimeToMinStr != null && !"".equals(createTimeToMinStr)) {
			    SimpleDateFormat format = new SimpleDateFormat(dateFormatWithTime);
			    createDateTo = new Timestamp(format.parse(createDateToStr + " " + createTimeToHourStr + ":" + createTimeToMinStr).getTime());
			    createDateToWithTime = true;
			} else {	// No time
			    createDateTo = new Timestamp(DateUtil.parse(createDateToStr,dateFormat).getTime());
			}		    
		}
		
		System.out.println("BatchFrom:"+tranDateSchFromStr);
		System.out.println("BatchTO:"+tranDateSchToStr);
		
		Date tranDateSchFrom = (tranDateSchFromStr != null && !"".equals(tranDateSchFromStr))
			?DateUtil.parse(tranDateSchFromStr,dateFormat) :null;
		Date tranDateSchTo = (tranDateSchToStr != null && !"".equals(tranDateSchToStr))
			?DateUtil.parse(tranDateSchToStr,dateFormat) :null;
		
		TaskConditionDTO dto = new TaskConditionDTO();
		dto.setNo((no != null && !"".equals(no))?new BigDecimal(no) :null);
		dto.setProgramName(taskName);
		dto.setCreateDateFrom(createDateFrom);
		dto.setCreateDateTo(createDateTo);
		//dto.setAsOfDateFrom(asOfDateFrom);
		//dto.setAsOfDateTo(asOfDateTo);
		dto.setCreateDateFromWithTime(createDateFromWithTime);
		dto.setCreateDateToWithTime(createDateToWithTime);
		dto.setBatchDateFrom(tranDateSchFrom);
		dto.setBatchDateTo(tranDateSchTo);
		dto.setEntityId(entity);
		dto.setProduct(product);
		
		String programNames = dynaForm.getProgramId();
		String progs[] = programNames.split(",");
		String programId = Utils.isNull(progs[0]);//dynaForm.get("programId"));
		String programName = Utils.isNull(progs[1]);//dynaForm.get("programId"));
		dto.setProgramId(programId);
		
		return dto;
    }
   
}

