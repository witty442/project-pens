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
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

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
		Map<String,ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
		Map<String,ReqPickStock> itemsBarcodeMap = new HashMap<String, ReqPickStock>();
		Map<String,ReqPickStock> itemsBarcodeErrorMap = new HashMap<String, ReqPickStock>();
		try {
            //Clear data session
			request.getSession().setAttribute("saved", null); 
			
			conn = DBConnection.getInstance().getConnection();
			
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String groupCode = Utils.isNull(request.getParameter("groupCode"));
            String pensItem = Utils.isNull(request.getParameter("pensItem"));
            int index = Utils.convertStrToInt(Utils.isNull(request.getParameter("index")));//Row of Qty
            
          //Get itemBarcodeMap old key = groupCode
			if(request.getSession().getAttribute("groupCodeMap") != null){
			   groupCodeMap = (Map)request.getSession().getAttribute("groupCodeMap");
			   String key  = groupCode;
			   ReqPickStock itemBarcode = groupCodeMap.get(key);
			   logger.debug("groupCode["+groupCode+"]");
			   
			   if(itemBarcode != null && itemBarcode.getItemsBarcodeMap() !=null){
				   logger.debug("groupCode:"+itemBarcode.getGroupCode());
			       itemsBarcodeMap = itemBarcode.getItemsBarcodeMap();
			       logger.debug("itemsBarcodeMap:"+itemsBarcodeMap);
			   }
			}
			
			if(request.getSession().getAttribute("itemsBarcodeErrorMap") !=null){
				itemsBarcodeErrorMap = (Map) request.getSession().getAttribute("itemsBarcodeErrorMap");
			}
			
			if( !"".equals(issueReqNo)){
				logger.debug("prepare edit issueReqNo:"+issueReqNo);
				request.getSession().setAttribute("resultItems", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				//Search by issue_req_no
				p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
				
				p.setGroupCode(groupCode);
				p.setPensItem(pensItem);
				p.setRowIndex(index+"");
				//new search
				p.setNewReq(false);
				
				ReqPickStock pNew = ReqPickStockDAO.getItemInStockListByGroupCode(conn, p,itemsBarcodeMap,itemsBarcodeErrorMap);
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
				p.setGroupCode(groupCode);
				p.setPensItem(pensItem);
				p.setNewReq(true);
				p.setModeConfirm(false);
				p.setModeEdit(true);
				p.setRowIndex(index+"");
				
				//search by page 
				ReqPickStock pGroupCodeItems = ReqPickStockDAO.getItemInStockListByGroupCode(conn, p,itemsBarcodeMap,itemsBarcodeErrorMap);
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
		AddItemPickStockForm aForm = (AddItemPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Map<String,ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
		Map<String,ReqPickStock> itemsBarcodeMap = new HashMap<String, ReqPickStock>();
		boolean foundError =false;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			 //Data Disp in Table
		    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("resultItems"); //data per page
		    
			// add itemBarcode to Session Map groupCodeMap
			if(request.getSession().getAttribute("groupCodeMap") != null){
				groupCodeMap = (Map)request.getSession().getAttribute("groupCodeMap");
				
				String[] qty = request.getParameterValues("qty");
			
				ReqPickStock itemsBarcode = aForm.getBean();
						
				//add value to Results
				if(results != null && results.size() > 0 && qty != null){
					for(int i=0;i<results.size();i++){
						 ReqPickStock l = (ReqPickStock)results.get(i);
						 l.setIssueReqNo(itemsBarcode.getIssueReqNo());
						 l.setPensItem(itemsBarcode.getPensItem());
						 
						 //validate onhand
						 int qtyScreen = Utils.convertStrToInt(qty[i]);//PICK
						   
						 //Step 1 validate in Stock onhand
						 Onhand onhand = OnhandProcessDAO.getItemInStockByPKITEM(conn, l);
						 int onhandQty = onhand!=null?Utils.convertStrToInt(onhand.getOnhandQty()):0;
						   
						 logger.debug("valid masterial_master["+l.getMaterialMaster()+"]onhadnQty["+onhandQty+"]qty["+qtyScreen+"]");
						 if(qtyScreen > onhandQty){
						    foundError = true;
						    l.setLineItemStyle("lineError");
						    l.setOnhandQty(onhandQty+"");
						 }
						 
						 l.setQty(qty[i]);
						 l.setUpdateUser(user.getUserName());
						 l.setCreateUser(user.getUserName());
						 
						 //set data to list disp
						 results.set(i, l);
						 
						 if(foundError==false){
							 logger.debug("GroupCode["+itemsBarcode.getGroupCode()+"]matMaster["+l.getMaterialMaster()+"]qty["+l.getQty()+"]");
							 //Key Map  
							 String key = l.getBarcode();
							 //check pens_items is old record
							 if(itemsBarcodeMap.get(key) != null){
								 itemsBarcodeMap.put(key, l);
							 }else{
								 if( !Utils.isNull(l.getQty()).equals("")){
									 itemsBarcodeMap.put(key, l);
								 }
							 }
						 }//if 
					}//for
				}
				
				//set Line Item to Map(key=GroupCode)
				itemsBarcode.setItemsBarcodeMap(itemsBarcodeMap);
				//set line item to GroupCode Map
				groupCodeMap.put(itemsBarcode.getGroupCode(), itemsBarcode);
			}
			
			if(foundError == false){
				//Put Date Save Session
				request.getSession().setAttribute("groupCodeMap", groupCodeMap);
				//Display Data Screen
				request.getSession().setAttribute("resultItems", results);
				
				//set for pass value to Main Screen
				request.getSession().setAttribute("saved", "saved");
			}else{
				request.setAttribute("Message", "ไม่สามารถเบิกยอดได้ตามจำนวนที่ต้องการ  โปรดตรวจสอบยอด onhand ");
			}
			
			//clear error item by barcode
			 request.getSession().removeAttribute("itemsBarcodeErrorMap");
			 
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
