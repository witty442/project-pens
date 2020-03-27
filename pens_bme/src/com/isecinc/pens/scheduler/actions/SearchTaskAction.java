package com.isecinc.pens.scheduler.actions;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.scheduler.bean.TaskConditionDTO;
import com.isecinc.pens.scheduler.dao.SearchTaskDAO;
import com.isecinc.pens.scheduler.forms.SearchTaskForm;
import com.isecinc.pens.scheduler.utils.DateUtil;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class SearchTaskAction extends  I_Action
{

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("SearchTaskAction Prepare Form");
		SearchTaskForm importForm = (SearchTaskForm) form;
		String returnText = "prepare";
		try {
			
			
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
		SearchTaskForm f = (SearchTaskForm) form;
		String returnText = "Success";
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
        	
        	logger.debug("action:"+action);
        	 
        	if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
				 request.getSession().setAttribute("dataList",null);
				 
				 //init JobList
				 List<References> jobList = SearchTaskDAO.findJobInitail("");
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
		SearchTaskForm f = (SearchTaskForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "Success";
		ArrayList dataList = new ArrayList();
	    Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
	        TaskConditionDTO dto = getSearchCondition(f);
		    dataList = SearchTaskDAO.findTaskByCondition(dto,conn);
		    
		    if(dataList != null && dataList.size() >0){
		    }else{
		    	request.setAttribute("Message", "ไม่พบข้อมูล");
		    }

		    request.getSession().setAttribute("dataList",dataList);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
		return returnText;
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SearchTaskForm tripForm = (SearchTaskForm) form;
		try {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {

		}
		return "re-search";
	}

	
	@Override
	protected void setNewCriteria(ActionForm form) {
		
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

    private TaskConditionDTO getSearchCondition(SearchTaskForm dynaForm)throws Exception{
        String dateFormat = "dd/MM/yyyy" ;//Constant.DATE_FORMAT_ddMMYYYY_WITH_SLASH;
        String dateFormatWithTime = "dd/MM/yyyy HH:mm";// Constant.DATE_FORMAT_ddMMYYYY_WITH_SLASH_WITH_TIME;
        String programId = Utils.isNull(dynaForm.getProgramId()).equals("")?"": dynaForm.getProgramId().split("\\,")[0];
        String no = dynaForm.getSearchId();//(String) dynaForm.get("searchId");
        String taskName = dynaForm.getSearchTaskName();// (String) dynaForm.get("searchTaskName");		
		String createDateFromStr = dynaForm.getSearchCreateDateFrom();//(String) dynaForm.get("searchCreateDateFrom");
		String createDateToStr = dynaForm.getSearchCreateDateTo();//(String) dynaForm.get("searchCreateDateTo");;
		String tranDateSchFromStr = dynaForm.getTranDateSchFrom();//(String) dynaForm.get("tranDateSchFrom");
		String tranDateSchToStr = dynaForm.getTranDateSchTo();//(String) dynaForm.get("tranDateSchTo");
		String createTimeFromHourStr = dynaForm.getSearchTimeFromHour();// (String) dynaForm.get("searchTimeFromHour");
		String createTimeFromMinStr = dynaForm.getSearchTimeFromMinute();//(String) dynaForm.get("searchTimeFromMinute");
		String createTimeToHourStr = dynaForm.getSearchTimeToHour();//(String) dynaForm.get("searchTimeToHour");
		String createTimeToMinStr = dynaForm.getSearchTimeToMinute();//(String) dynaForm.get("searchTimeToMinute");
		
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
		System.out.println("programId:"+programId);
		
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
		dto.setProgramId(programId);
		return dto;
    }
    
   
}

