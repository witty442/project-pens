package com.isecinc.pens.web.pick;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class JobAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		JobForm aForm = (JobForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				Job ad = new Job();
				//ad.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				 
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				ad.setYear(year+"");
				aForm.setJob(ad);
				
				//Set Session List
				List<References> wareHouseList = new ArrayList<References>();
				References ref = new References("","");
				wareHouseList.add(ref);
				wareHouseList.addAll(JobDAO.getWareHouseList());
				request.getSession().setAttribute("wareHouseList",wareHouseList);

				List<References> jobStatusList = new ArrayList<References>();
				ref = new References("","");
				jobStatusList.add(ref);
				jobStatusList.addAll(JobDAO.getJobStatusList());
				request.getSession().setAttribute("jobStatusList",jobStatusList);
		
				List<PopupForm> custGroupList = new ArrayList<PopupForm>();
				PopupForm refP = new PopupForm("",""); 
				custGroupList.add(refP);
				custGroupList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
				request.getSession().setAttribute("custGroupList",custGroupList);
				
				List<References> yearList = new ArrayList<References>();
				ref = new References("","");
				yearList.add(ref);
				yearList.addAll(JobDAO.getYearList());
				request.getSession().setAttribute("yearList",yearList);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		JobForm aForm = (JobForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setJob(aForm.getJobCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(JobDAO.searchJobListTotalRec(conn,aForm.getJob()));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			   
				List<Job> items = JobDAO.searchJobList(conn,aForm.getJob(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResultsSearch(null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    List<Job> items = JobDAO.searchJobList(conn,aForm.getJob(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search2");
	}
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		JobForm aForm = (JobForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setJob(new Job());
			
			Job ad = new Job();
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			ad.setYear(year+"");
			aForm.setJob(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		JobForm aForm = (JobForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setJobCriteria(aForm.getJob());
			
            String jobId = Utils.isNull(request.getParameter("jobId"));
            String mode = Utils.isNull(request.getParameter("mode"));
            logger.debug("jobId:"+jobId+",mode:"+mode);
			if( !"".equals(jobId)){
				logger.debug("prepare edit jobId:"+jobId);
				Job c = new Job();
				c.setJobId(jobId);
				Job aS = JobDAO.searchJobDetail(c);
				
				aForm.setResults(aS.getItems());
				aForm.setJob(aS);

				aForm.setMode(mode);//Mode Edit
			}else{
				logger.debug("prepare new documentNo");
				aForm.setResults(new ArrayList<Job>());
				Job ad = new Job();
				ad.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				ad.setCanEdit(true);
				
				aForm.setJob(ad);
				aForm.setMode(mode);//Mode Add new
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JobForm summaryForm = (JobForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JobForm orderForm = (JobForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			Job aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setJob(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		JobForm aForm = (JobForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Job h = aForm.getJob();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			JobDAO.save(h,user);
			
			conn.commit();

			//search
			aForm.setJob(JobDAO.searchJobDetail(h));
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}
	
	public ActionForward saveRefDoc(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveRefDoc Action");
		JobForm aForm = (JobForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Job h = aForm.getJob();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			JobDAO.updateRefDocModel(conn,h);
			
			conn.commit();

			//search
			aForm.setJob(JobDAO.searchJobDetail(h));
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel Action");
		JobForm aForm = (JobForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Job h = aForm.getJob();
			
			//verify can cancel job
			boolean canCancelJob = BarcodeDAO.canCancelJob(conn, h);
			if(canCancelJob==false){
				request.setAttribute("Message", "ไม่สามารถยกเลิกรายการได้ เนื่องจาก Job นี้มีการใช้งานอยู่");
				return mapping.findForward("search");
			}
			
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			JobDAO.cancelJob(conn,h);
 
			conn.commit();

			//search
			aForm.setJob(JobDAO.searchJobDetail(h));
			
			request.setAttribute("Message", "ยกเลิกรายการเรียบร้อยแล้ว");
			
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		JobForm aForm = (JobForm) form;
		try {
			aForm.setResults(new ArrayList<Job>());
			aForm.setJob(new Job());
			
			Job ad = new Job();
			ad.setCanEdit(true);
			ad.setOpenDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setJob(ad);
			aForm.setMode("add");
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
	
}
