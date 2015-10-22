package com.isecinc.pens.web.pick;

import java.sql.Connection;
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
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.ReqReturnWacoal;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.OnhandDAO;
import com.isecinc.pens.dao.ReqReturnWacoalDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReqReturnWacoalAction extends I_Action {


	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				ReqReturnWacoal ad = new ReqReturnWacoal();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());
				aForm.setResultsSearch(ReqReturnWacoalDAO.searchHead(aForm.getBean(),false));
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
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ReqReturnWacoal b = aForm.getBean();
			aForm.setBean(b);
			aForm.setResultsSearch(ReqReturnWacoalDAO.searchHead(aForm.getBean(),false));
			
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
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new ReqReturnWacoal());
			
			ReqReturnWacoal ad = new ReqReturnWacoal();
			ad.setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String requestDate = Utils.isNull(request.getParameter("requestDate"));
            String requestNo = Utils.isNull(request.getParameter("requestNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(requestDate) && !"".equals(requestNo)){
				List<ReqReturnWacoal> allList = new ArrayList<ReqReturnWacoal>();	
				
				logger.debug("prepare edit requestDate:"+requestDate +",requestNo:"+requestNo+",mode:"+mode);
				ReqReturnWacoal c = new ReqReturnWacoal();
				c.setRequestDate(requestDate);
				c.setRequestNo(requestNo);
				
				List<ReqReturnWacoal> listData = ReqReturnWacoalDAO.searchHead(c,true);
				ReqReturnWacoal h = null;
				if(listData != null && listData.size() >0){
				   h = (ReqReturnWacoal)listData.get(0);
				   aForm.setBean(h);
				}
 
				if(!"view".equalsIgnoreCase(mode)){
					// All barcode status CLOSE
					List<ReqReturnWacoal> allData = ReqReturnWacoalDAO.searchBarcoceItemW1();
					allList.addAll(h.getItems());
					allList.addAll(allData);
				}else{
					allList.addAll(h.getItems());
				}
				
				aForm.setResults(allList);
				aForm.setMode(mode);//Mode Edit
			}else{
				
				logger.debug("prepare new requestNo");
				aForm.setResults(new ArrayList<ReqReturnWacoal>());
				ReqReturnWacoal ad = new ReqReturnWacoal();
				ad.setCanEdit(true);
				ad.setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setResults(ReqReturnWacoalDAO.searchBarcoceItemW1());
				aForm.setBean(ad);
				
				aForm.setMode(mode);//Mode Add new
				
			}
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
		ReqReturnWacoalForm summaryForm = (ReqReturnWacoalForm) form;
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
		ReqReturnWacoalForm orderForm = (ReqReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ReqReturnWacoal aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
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
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqReturnWacoal h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<ReqReturnWacoal> itemList = new ArrayList<ReqReturnWacoal>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] boxNo = request.getParameterValues("boxNo");
			String[] jobId = request.getParameterValues("jobId");
			String[] qty = request.getParameterValues("qty");
			
			logger.debug("lineId:"+lineId.length);
			
			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
					if( !Utils.isNull(lineId[i]).equals("")){
						 ReqReturnWacoal l = new ReqReturnWacoal();
						 l.setLineId(i+1);
						 l.setBoxNo(Utils.isNull(boxNo[i]));
						 l.setJobId(Utils.isNull(jobId[i]));
						 l.setQty(Utils.convertStrToInt(qty[i]));
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 
						 itemList.add(l);
	 
					}
				}
			}
			
			h.setItems(itemList);
			
			//Store in Session
			aForm.setResults(itemList);
			
			//Validate TotalQty no over set MAX_RETURN_LIMIT (5000) default
			int MAX_QTY_RETURN_LIMIT = ReqReturnWacoalDAO.getMaxQtyLimitReturn(conn);
			logger.debug("MAX_QTY_RETURN_LIMIT["+MAX_QTY_RETURN_LIMIT+"] totalQty["+h.getTotalQty()+"]");
			
            if(h.getTotalQty() > MAX_QTY_RETURN_LIMIT){
            	request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้   ไม่สามารถคืนของได้เกิน["+MAX_QTY_RETURN_LIMIT+"]ชิ้น");
				return "search";
			}
			
			ReqReturnWacoalDAO.save(h);
			
			conn.commit();
			
			List<ReqReturnWacoal> allList = new ArrayList<ReqReturnWacoal>();
			//search data
			List<ReqReturnWacoal> saveData = ReqReturnWacoalDAO.searchHead(h,true);
			h = (ReqReturnWacoal)saveData.get(0);
			   
			// All barcode status CLOSE
			List<ReqReturnWacoal> allData = ReqReturnWacoalDAO.searchBarcoceItemW1();
			  
			allList.addAll(h.getItems());
			allList.addAll(allData);
			
			aForm.setResults(allList);
            aForm.setBean(h);
            aForm.setMode("edit");
            
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
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		try {
			aForm.setResults(new ArrayList<ReqReturnWacoal>());
			
			ReqReturnWacoal ad = new ReqReturnWacoal();
			ad.setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
		ReqReturnWacoalForm aForm = (ReqReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		String whereInBoxNoSql = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqReturnWacoal h = aForm.getBean();
			
			//update status barcode to close (old status)
			List<ReqReturnWacoal> saveData = ReqReturnWacoalDAO.searchHead(h,true);
			if(saveData != null && saveData.size()>0){
			   ReqReturnWacoal oldReq = (ReqReturnWacoal)saveData.get(0);
			   for(int i=0;i<oldReq.getItems().size();i++){
				   ReqReturnWacoal l = (ReqReturnWacoal)oldReq.getItems().get(i);
				   
				   //Set barcode status = return
			       Barcode b = new Barcode();
			       b.setJobId(l.getJobId());
			       b.setBoxNo(l.getBoxNo());
			       b.setStatus(JobDAO.STATUS_CLOSE);
			       
			       logger.debug("update barcode job_id["+b.getJobId()+"]boxNo["+b.getBoxNo()+"]");
			       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
			       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
			       
			     //For Onhand
					 whereInBoxNoSql +="'"+l.getBoxNo()+"',";
			   }
			}
			
			//Set Data to Cancel
			h.setUpdateUser(user.getUserName());
			h.setStatus(ReqReturnWacoalDAO.STATUS_CANCEL);
			h.setStatusDesc(ReqReturnWacoalDAO.getStatusDesc(ReqReturnWacoalDAO.STATUS_CANCEL));
			
			//update Req to cancel
			ReqReturnWacoalDAO.updateStatusModel(conn,h);
			//conn commit
			conn.commit();
			
			//Process Onhand  by pens_item By BoxNo
			if(whereInBoxNoSql.length() > 0){
				whereInBoxNoSql = whereInBoxNoSql.substring(0,whereInBoxNoSql.length()-1);
			}
		//	OnhandDAO.processBanlanceOnhandFromBarcodeByBoxNo(conn,user.getUserName(),PickConstants.STATUS_CLOSE,whereInBoxNoSql);
			
			
			//Search Data
			List<ReqReturnWacoal> listData = ReqReturnWacoalDAO.searchHead(h,true);
			ReqReturnWacoal beanSearch = null;
			if(listData != null && listData.size() >0){
				beanSearch = (ReqReturnWacoal)listData.get(0);
			    aForm.setResults(beanSearch.getItems());
			}
			//Set CanEdit
			h.setCanEdit(false);
			
			aForm.setBean(h);
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
