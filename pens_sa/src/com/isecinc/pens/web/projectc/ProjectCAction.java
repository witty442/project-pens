package com.isecinc.pens.web.projectc;

import java.sql.Connection;
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
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupDAO;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ProjectCAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		ProjectCForm aForm = (ProjectCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if("new".equals(action)){
				//clear Message all
				request.getSession().removeAttribute("Message");
				request.getSession().removeAttribute("ERROR_Message");
		    	request.getSession().removeAttribute("oracleCustNo");
		    	
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				aForm.setBranchList(null);
				//prepare bean
				ProjectCBean bean = new ProjectCBean();
				
				//bean.setStoreCode("21209002");//for test
				aForm.setBean(bean);
				
				
			}else if("back".equals(action)){
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				ProjectCBean bean = aForm.getBeanCriteria();
				aForm.setResultsSearch(aForm.getBranchList());
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}

	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		ProjectCForm aForm = (ProjectCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = true;
		Connection conn = null;
		try {
			aForm.setResultsSearch(null);
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			//get Items Show by Page Size
			List<ProjectCBean> items = ProjectCDAO.searchBranchList(conn,aForm.getBean(),allRec,currPage,pageSize,user);
		    if(items != null && items.size() >0){
			   aForm.setResultsSearch(items);
			   aForm.setBranchList(aForm.getResultsSearch());
		    }else{
		    	request.setAttribute("Message", "ไม่พบข้อมูล");
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
		return mapping.findForward("search");
	}
	public ActionForward prepareCheckStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareCheckStock");
		ProjectCForm aForm = (ProjectCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		int currPage = 1;
		boolean allRec = false;
		boolean getItems = false;
		try {
			//save criteria Main Search
			aForm.setBeanCriteria(aForm.getBean());
			
			request.getSession().removeAttribute("projectCBeanServlet");//clear session servlet 
			conn = DBConnection.getInstance().getConnectionApps();
			
			String action = Utils.isNull(request.getParameter("action"));
			String storeCode = Utils.isNull(request.getParameter("storeCode"));
			String branchId = Utils.isNull(request.getParameter("branchId"));
			
			//criteria for get customerName And BranchName
			PopupForm cri = new PopupForm();
			Map<String, String> criMap = new HashMap<String, String>();
			
			ProjectCBean bean = new ProjectCBean();
			bean.setCheckDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			
			//Get CustomerName
			bean.setStoreCode(storeCode);
			cri.setCodeSearch(bean.getStoreCode());
			bean.setStoreName(PopupDAO.searchCustomerCreditSalesProjectCList(request, cri).get(0).getDesc());
			
			//Get BranchName
			bean.setBranchId(branchId);
			criMap.put("storeCode", bean.getStoreCode());
			cri.setCodeSearch(bean.getBranchId());
			bean.setBranchName(PopupDAO.searchCustomerBranchCreditSalesProjectCList(request, cri).get(0).getDesc());
			
			aForm.setBean(bean);
			aForm.setResultsSearch(null);
		
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//debug
				logger.debug("aForm.bean:"+aForm.getBean());
				logger.debug("aForm.bean.id:"+aForm.getBean().getId());
				
				//get Total Record
				//search By StoreCode and branchId
				ProjectCBean criSearch = new ProjectCBean();
				criSearch.setStoreCode(aForm.getBean().getStoreCode());
				criSearch.setBranchId(aForm.getBean().getBranchId());
				aForm.setTotalRecord(ProjectCDAO.searchCheckStockListTotalRec(conn,criSearch));
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
			    getItems=false;
				List<ProjectCBean> items = ProjectCDAO.searchCheckStockList(conn,criSearch,allRec,currPage,pageSize,getItems);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   //request.setAttribute("Message", "ไม่พบข้อมูล");
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
			    //search By StoreCode and branchId
				ProjectCBean criSearch = new ProjectCBean();
				criSearch.setStoreCode(aForm.getBean().getStoreCode());
				criSearch.setBranchId(aForm.getBean().getBranchId());
				
			    getItems=false;
			    List<ProjectCBean> items = ProjectCDAO.searchCheckStockList(conn,criSearch,allRec,currPage,pageSize,getItems);
				aForm.setResultsSearch(items);
			}//if
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
		return mapping.findForward("checkStockSearch");
	}
	
	public ActionForward prepareCheckStockDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareStockDetail");
		ProjectCForm aForm = (ProjectCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		boolean getItems = false;
		Connection conn = null;
		try {
			//clear Message all
			request.getSession().removeAttribute("Message");
			request.getSession().removeAttribute("ERROR_Message");
	    	request.getSession().removeAttribute("oracleCustNo");
			request.getSession().removeAttribute("projectCBeanServlet");//clear session servlet 
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			String action = Utils.isNull(request.getParameter("action"));
			String id = Utils.isNull(request.getParameter("idx"));
			String storeCode = Utils.isNull(request.getParameter("storeCode"));
			String branchId = Utils.isNull(request.getParameter("branchId"));
			String checkDate = Utils.isNull(request.getParameter("checkDate"));
			logger.debug("action:"+action);
			
			//criteria for get customerName And BranchName
			PopupForm cri = new PopupForm();
			Map<String, String> criMap = new HashMap<String, String>();
			
			ProjectCBean bean = new ProjectCBean();
			bean.setCheckDate(checkDate);
			//Get CustomerName
			bean.setStoreCode(storeCode);
			cri.setCodeSearch(bean.getStoreCode());
			bean.setStoreName(PopupDAO.searchCustomerCreditSalesProjectCList(request, cri).get(0).getDesc());
			
			//Get BranchName
			bean.setBranchId(branchId);
			criMap.put("storeCode", bean.getStoreCode());
			cri.setCodeSearch(bean.getBranchId());
			bean.setBranchName(PopupDAO.searchCustomerBranchCreditSalesProjectCList(request, cri).get(0).getDesc());
			
			bean.setId(id);
			bean.setCheckUser(user.getName());//default user
			aForm.setBean(bean);
			
			//Clear session from ProjectCServlet after save(checkStockDetail) to display
			request.getSession().removeAttribute("projectCBean");
			request.getSession().removeAttribute("Message");
			
			if("view".equalsIgnoreCase(action)){
				 allRec  = true;getItems = true;
				 List<ProjectCBean> items = ProjectCDAO.searchCheckStockList(conn,bean,allRec,currPage,pageSize,getItems);
				 if(items != null && items.size() >0){
					 ProjectCBean beanSearch = items.get(0);
					 beanSearch.setStoreName(bean.getStoreName());
					 beanSearch.setBranchName(bean.getBranchName());
					 beanSearch.setMode(action);
					
					 //Check CanSave checkDate < sysdate cannot not save
					 beanSearch.setCanSave(true);
					 if(DateUtil.compareWithToday(beanSearch.getCheckDate()) ==1){ //sysdate more than
						 beanSearch.setCanSave(false);
					 }
					 
					 aForm.setBean(beanSearch);
				 }
			}else if("new".equalsIgnoreCase(action)){
				/** check_date is Exist DB load old data to show **/
				 allRec  = true;getItems = true;
				 List<ProjectCBean> items = ProjectCDAO.searchCheckStockList(conn,bean,allRec,currPage,pageSize,getItems);
				 if(items != null && items.size() >0){
					 ProjectCBean beanSearch = items.get(0);
					 beanSearch.setStoreName(bean.getStoreName());
					 beanSearch.setBranchName(bean.getBranchName());
					 beanSearch.setMode(action);
					
					 //Check CanSave checkDate < sysdate cannot not save
					 beanSearch.setCanSave(true);
					 if(DateUtil.compareWithToday(beanSearch.getCheckDate()) ==-1){ //sysdate more than
						 beanSearch.setCanSave(false);
					 }
					 aForm.setBean(beanSearch);
				 }else{
					bean= ProjectCDAO.searchCheckStockListCaseNew(conn,bean);
					bean.setCanSave(true);
					bean.setMode(action);
					aForm.setBean(bean);
				 }
			}else if("edit".equalsIgnoreCase(action)){
				
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
		return mapping.findForward("checkStockDetail");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		ProjectCForm aForm = (ProjectCForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save CheckStock Detail
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ProjectCForm aForm = (ProjectCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ProjectCForm orderForm = (ProjectCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
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
