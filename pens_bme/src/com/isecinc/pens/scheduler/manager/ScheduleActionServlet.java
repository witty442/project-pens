package com.isecinc.pens.scheduler.manager;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.scheduler.dao.SearchTaskDAO;
import com.isecinc.pens.scheduler.forms.ScheduleForm;
import com.isecinc.pens.scheduler.utils.JobUtils;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Servlet implementation class ScheduleActionServlet
 */
public class ScheduleActionServlet extends HttpServlet {
	protected static Logger logger = Logger.getLogger("PENS");
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScheduleActionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		regen(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		regen(request,response);
	}
	
	public void regen(HttpServletRequest request,HttpServletResponse response)  {
		User user = (User) request.getSession().getAttribute("user");
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
			 
		}catch(Exception e){
			 e.printStackTrace();
		}finally{
			try{
			if(conn != null){
				conn.close();
			}
			}catch(Exception ee){}
		}
	}
	

}
