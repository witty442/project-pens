package com.isecinc.pens.web.pick;

import java.sql.Connection;
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
import com.isecinc.pens.bean.Onhand;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.OnhandProcessDAO;
import com.isecinc.pens.dao.ReqPickStockDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AddItemPickStockAction extends I_Action {
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		AddItemPickStockForm aForm = (AddItemPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		//Map<String,ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
		Map<String,ReqPickStock> itemsBarcodeMap = new HashMap<String, ReqPickStock>();
		try {
            //Clear data session
			request.getSession().setAttribute("saved", null); 
			
			conn = DBConnection.getInstance().getConnection();
			
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String issueReqDate = Utils.isNull(request.getParameter("issueReqDate"));
            String status = Utils.isNull(request.getParameter("status"));
            String requestor = new String(Utils.isNull(request.getParameter("requestor")).getBytes("ISO8859_1"), "UTF-8");  
            String custGroup = Utils.isNull(request.getParameter("custGroup"));
            String needDate = Utils.isNull(request.getParameter("needDate"));
            String storeCode = Utils.isNull(request.getParameter("storeCode"));
            String subInv = Utils.isNull(request.getParameter("subInv"));
            String storeNo = Utils.isNull(request.getParameter("storeNo"));
            String remark =  new String(Utils.isNull(request.getParameter("remark")).getBytes("ISO8859_1"), "UTF-8"); 
            
            String groupCode = Utils.isNull(request.getParameter("groupCode"));
            String pensItem = Utils.isNull(request.getParameter("pensItem"));
            String wareHouse = Utils.isNull(request.getParameter("wareHouse"));
            String forwarder = Utils.isNull(request.getParameter("forwarder"));
            
            int index = Utils.convertStrToInt(Utils.isNull(request.getParameter("index")));//Row of Qty
             
            logger.debug("issueReqNo :"+issueReqNo);
            logger.debug("issueReqDate :"+issueReqDate);
            logger.debug("groupCode :"+groupCode);
            logger.debug("status :"+status);
            logger.debug("requestor :"+requestor);
            logger.debug("custGroup :"+custGroup);
            logger.debug("needDate :"+needDate);
            logger.debug("storeCode :"+storeCode);
            logger.debug("subInv :"+subInv);
            logger.debug("storeNo :"+storeNo);
            logger.debug("remark :"+remark);
            logger.debug("wareHouse :"+wareHouse);
            logger.debug("forwarder :"+forwarder);
            
			if( !"".equals(issueReqNo)){
				logger.debug("prepare edit issueReqNo:"+issueReqNo);
				request.getSession().setAttribute("resultItems", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				p.setWareHouse(wareHouse);
				
				//Search by issue_req_no
				p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
				
				p.setGroupCode(groupCode);
				p.setPensItem(pensItem);
				p.setRowIndex(index+"");
				//new search
				p.setNewReq(false);
				
				ReqPickStock pNew = ReqPickStockDAO.getItemInStockListByGroupCode(conn, p,itemsBarcodeMap);
				List<ReqPickStock> results = pNew.getItems();
				
				if (results != null  && results.size() >0) {
					request.getSession().setAttribute("resultItems", results);
				} else {
					request.getSession().setAttribute("resultItems", null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				aForm.setBean(p);
				
				logger.debug("issueReqNo:"+aForm.getBean().getIssueReqNo());
				logger.debug("groupCode:"+aForm.getBean().getGroupCode());
				
			}else{
				logger.debug("new issueReqNo");
				request.getSession().setAttribute("resultItems", null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqDate(issueReqDate);
				p.setIssueReqNo(issueReqNo);
				p.setStatus(status);
				p.setCustGroup(custGroup);
				p.setRequestor(requestor);
				p.setNeedDate(needDate);
				p.setStoreCode(storeCode);
				p.setSubInv(subInv);
				p.setStoreNo(storeNo);
				p.setRemark(remark);
				
				p.setGroupCode(groupCode);
				p.setPensItem(pensItem);
				p.setNewReq(true);
				p.setModeConfirm(false);
				p.setModeEdit(true);
				p.setRowIndex(index+"");
				p.setWareHouse(wareHouse);
				p.setForwarder(forwarder);
				
				//search by page 
				ReqPickStock pGroupCodeItems = ReqPickStockDAO.getItemInStockListByGroupCode(conn, p,itemsBarcodeMap);
                List<ReqPickStock> results = pGroupCodeItems.getItems();
				
				if (results != null  && results.size() >0) {
					request.getSession().setAttribute("resultItems", results);
				} else {
					request.getSession().setAttribute("resultItems", null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				aForm.setBean(p);
				logger.debug("issueReqNo:"+aForm.getBean().getIssueReqNo());
				logger.debug("groupCode:"+aForm.getBean().getGroupCode());
			}
		} catch (Exception e) {
			request.setAttribute("Message", "error:"+ e.getMessage());
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
		AddItemPickStockForm summaryForm = (AddItemPickStockForm) form;
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
		AddItemPickStockForm aForm = (AddItemPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
		
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		AddItemPickStockForm aForm = (AddItemPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundError = false;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			 //Data Disp in Table
		    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("resultItems"); //data per page
			String[] qty = request.getParameterValues("qty");
		
			ReqPickStock itemsBarcode = aForm.getBean();
					
			//add value to Results
			if(results != null && results.size() > 0 && qty != null){
				for(int i=0;i<results.size();i++){
					 ReqPickStock l = (ReqPickStock)results.get(i);
					 l.setIssueReqNo(itemsBarcode.getIssueReqNo());
					 l.setPensItem(itemsBarcode.getPensItem());
					 l.setWareHouse(itemsBarcode.getWareHouse());
					 
					 //validate onhand
					 int qtyScreen = Utils.convertStrToInt(qty[i]);//PICK
					 
					 if(qtyScreen > 0){
						 //Step 1 validate in Stock onhand
						 Onhand onhand = OnhandProcessDAO.getItemInStockByPKITEM(conn, l);
						 int onhandQty = onhand!=null?Utils.convertStrToInt(onhand.getOnhandQty()):0;
						   
						 logger.debug("valid masterial_master["+l.getMaterialMaster()+"]onhadnQty["+onhandQty+"]qty["+qtyScreen+"]");
						 if(qtyScreen > onhandQty){
						    foundError = true;
						    l.setLineItemStyle("lineError");
						    l.setOnhandQty(onhandQty+"");
						    l.setLineItemStyle("lineError");
						 }
					 }
					 
					 l.setQty(qty[i]);
					 l.setUpdateUser(user.getUserName());
					 l.setCreateUser(user.getUserName());
					 
					 //set data to list disp
					 results.set(i, l);

				}//for
			}
			
			//Display Data Screen
			request.getSession().setAttribute("resultItems", results);

			if(foundError == false){
				ReqPickStock reqPickSave = aForm.getBean();
				reqPickSave.setCreateUser(user.getUserName());
				reqPickSave.setUpdateUser(user.getUserName());
				
				reqPickSave.setItems(results);
				
				ReqPickStock result = ReqPickStockDAO.save(conn, reqPickSave);
				if(result.isResultProcess()){
				    conn.commit();
				    
				  //set for pass value to Main Screen
					request.getSession().setAttribute("saved", "saved");
				}else{
					request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้ พบ ERROR");
					conn.rollback();
				}

				request.getSession().setAttribute("resultItems", result.getItems());
			}else{
				request.setAttribute("Message", "ไม่สามารถเบิกยอดได้ตามจำนวนที่ต้องการ  โปรดตรวจสอบยอด onhand ");
			}
			
		}catch(Exception e){
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			conn.rollback();
		} finally {
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return "search";
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		AddItemPickStockForm aForm = (AddItemPickStockForm) form;
		try {
			ReqPickStock ad = new ReqPickStock();
			ad.setWareHouse(aForm.getBean().getWareHouse());
			aForm.setBean(ad);
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
