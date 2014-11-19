package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.ControlReturnReport;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqFinish;
import com.isecinc.pens.bean.ReturnBoxReport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ConfFinishDAO;
import com.isecinc.pens.dao.ReqFinishDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.pick.process.OnhandProcess;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ConfFinishAction extends I_Action {


	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ConfFinishForm aForm = (ConfFinishForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				ReqFinish ad = new ReqFinish();
				//ad.setReturnDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());
				aForm.setResultsSearch(ConfFinishDAO.searchHead(aForm.getBean(),false));
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
		ConfFinishForm aForm = (ConfFinishForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ReqFinish b = aForm.getBean();
			aForm.setBean(b);
			aForm.setResultsSearch(ConfFinishDAO.searchHead(aForm.getBean(),false));
			
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		ConfFinishForm aForm = (ConfFinishForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new ReqFinish());
			
			ReqFinish ad = new ReqFinish();
			//ad.setReturnDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
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
		ConfFinishForm aForm = (ConfFinishForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String requestNo = Utils.isNull(request.getParameter("requestNo"));
            String mode = Utils.isNull(request.getParameter("mode"));

			logger.debug("prepare edit requestNo:"+requestNo+",mode:"+mode);
			ReqFinish c = new ReqFinish();
			c.setRequestNo(requestNo);
			
			List<ReqFinish> listData = new ArrayList<ReqFinish>();
			if( !"".equals(requestNo) && !"".equals(requestNo)){
			   //get Head Detail
				c = ReqFinishDAO.searchReqFinishing(conn,c, false);
			    //Item not confirm
			    listData.addAll(ConfFinishDAO.searchItemByGroupCode(conn, c)); 
			}
			
		    aForm.setBean(c);
			aForm.setResults(listData);
			aForm.setMode(mode);//Mode Edit
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
		ConfFinishForm summaryForm = (ConfFinishForm) form;
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
		ConfFinishForm orderForm = (ConfFinishForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ReqFinish aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
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
		ConfFinishForm aForm = (ConfFinishForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqFinish h = aForm.getBean();
			h.setStatus(PickConstants.STATUS_FINISH);
			h.setConfirmDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Step 1 update status REQ_FINISHING ,REQ_FINISHING_BARCODE ,PICK_BARCODE,PICK_BARCODE_ITEM to to FINISH(F)
			ConfFinishDAO.save(conn, h);
						
			//logger.debug("returnStatusDesc:"+h.getReturnStatusDesc());
		 
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			conn.commit();
			
			//search refresh data
			h = ReqFinishDAO.searchReqFinishing(conn,h, false);
		    aForm.setBean(h);
		    if("BOX".equalsIgnoreCase(aForm.getBean().getTypeDisp())){
			    aForm.setResults(ConfFinishDAO.searchItemByBox(conn, h));
			}else{
				aForm.setResults(ConfFinishDAO.searchItemByGroupCode(conn, h));
			}
			
			//Step 2 Balance onhand from REQ_FINISHING
			OnhandProcess.processBalanceOnhand(user.getUserName());
			
		} catch (Exception e) {
			logger.error("RollBack:"+e.getMessage(),e);
			conn.rollback();
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
	

	public ActionForward viewDisp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("viewDisp");
		ConfFinishForm aForm = (ConfFinishForm) form;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//search refresh data
			ReqFinish h = ReqFinishDAO.searchReqFinishing(conn,aForm.getBean(), false);
		    aForm.setBean(h);
		    if("BOX".equalsIgnoreCase(aForm.getBean().getTypeDisp())){
		       aForm.setResults(ConfFinishDAO.searchItemByBox(conn, h));
		    }else{
			   aForm.setResults(ConfFinishDAO.searchItemByGroupCode(conn, h));
		    }
		    
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ConfFinishForm aForm = (ConfFinishForm) form;
		try {
			aForm.setResults(new ArrayList<ReqFinish>());
			
			ReqFinish ad = new ReqFinish();
			//ad.setReturnDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			
			aForm.setBean(ad);
			aForm.setMode("view");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}


	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		ConfFinishForm aForm = (ConfFinishForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			/*ReqFinish h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(ConfFinishDAO.STATUS_NEW);// set to NEW
		
			//save 
			//h = ConfFinishDAO.cancelReturn(conn,h);
			
			conn.commit();

			//Store in Session
			List<ReqFinish> listData = ConfFinishDAO.searchHead(h,true);
			if(listData != null && listData.size() >0){
			   h = (ReqFinish)listData.get(0);
			}
			   
			aForm.setBean(h);
			aForm.setResults(h.getItems());
            aForm.setMode("edit");*/
	
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
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
