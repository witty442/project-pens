package com.isecinc.pens.web.pick;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.ReqFinish;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ConfFinishDAO;
import com.isecinc.pens.dao.ConfirmReturnWacoalDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.ReqFinishDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.pick.process.ControlOhand;
import com.isecinc.pens.pick.process.ControlOnhandBean;
import com.isecinc.pens.pick.process.OnhandProcess;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ConfFinishAction extends I_Action {

	public static int pageSize = 60;
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
				aForm.setBean(new ReqFinish());
				
				//INIT SESSION
				List<References> billTypeList = new ArrayList<References>();
				References ref = new References("","");
				billTypeList.add(ref);
				billTypeList.addAll(ConfirmReturnWacoalDAO.getRequestStatusW2ListInPageReqFinish());
				request.getSession().setAttribute("statusReqFinishList",billTypeList);
			
				List<References> wareHouseList = new ArrayList<References>();
				References ref2 = new References("","");
				wareHouseList.add(ref2);
				wareHouseList.addAll(JobDAO.getWareHouseList());
				request.getSession().setAttribute("wareHouseList2",wareHouseList);
				
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());
				conn = DBConnection.getInstance().getConnection();
				aForm.setResultsSearch(ConfFinishDAO.searchHead(conn,aForm.getBean(),false,false,1,pageSize));
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
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(ConfFinishDAO.searchTotalHead(conn,aForm.getBean()));
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
				List<ReqFinish> items = ConfFinishDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
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
			    List<ReqFinish> items = ConfFinishDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
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
				c.setConfirmDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
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
			h.setConfirmDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Validate Can Confirm Finishing
			if( !ConfFinishDAO.canConfirmFinishing(conn, h.getRequestNo())){
				request.setAttribute("Message","ไม่สามารถ Confirm ข้อมูลได้  RequestNo["+h.getRequestNo()+"] เนื่องจากมีการ Confirm ข้อมูลไปแล้ว");
				return "search";
			}
			
			//Step 1 update status REQ_FINISHING ,REQ_FINISHING_BARCODE ,PICK_BARCODE,PICK_BARCODE_ITEM to FINISH(F)
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
			
			//Step 2 Balance onhand from REQ_FINISHING by warehouse
		    OnhandProcess.processBalanceOnhand(h.getWareHouse(),h.getRequestNo(),user.getUserName());
			
			//test delay 
			//TimeUnit.SECONDS.sleep(30);
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
