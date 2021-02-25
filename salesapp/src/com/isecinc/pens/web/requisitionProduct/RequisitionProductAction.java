package com.isecinc.pens.web.requisitionProduct;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.BillPlan;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.RequisitionProduct;
import com.isecinc.pens.bean.RequisitionProductLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MMoveOrder;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MRequisitionProduct;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class RequisitionProductAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		RequisitionProductForm sForm = (RequisitionProductForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parametor By moveOrderType  
				 sForm.setRequisitionProduct(new RequisitionProduct());

				//init priceListId by User Type
				 sForm.getRequisitionProduct().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 sForm.setResults(null);
				 sForm.setLines(null);
				 
				 //Clear Criteria
				 request.getSession().setAttribute("criteria_",null);
				 
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 RequisitionProduct b = (RequisitionProduct)request.getSession().getAttribute("criteria_");
				 logger.debug("b:"+b);
				 
				 sForm.getCriteria().setRequisitionProduct(b);
				 search(sForm,request,response); 
			 }
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RequisitionProductForm sForm = (RequisitionProductForm) form;
		try {
			logger.debug("prepare 2:"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				//Clear and init Parametor By moveOrderType  
				 sForm.setRequisitionProduct(new RequisitionProduct());

				//init priceListId by User Type
				 sForm.getRequisitionProduct().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 sForm.setResults(null);
				 sForm.setLines(null);
				 
				 //Clear Criteria
				 request.getSession().setAttribute("criteria_",null);
			 }
			
			 // save token
			saveToken(request);
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
		RequisitionProductForm mForm = (RequisitionProductForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MRequisitionProduct mDAO = new MRequisitionProduct();
			RequisitionProduct m = prepareCreateRequisitionProduct(user, mForm);
			List<RequisitionProduct> moveOrderList = mDAO.searchRequisitionProductList(m,user);
			mForm.setResults(moveOrderList);
			if(moveOrderList != null && moveOrderList.size()==0){
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			
			// Save Criteria
			request.getSession().setAttribute("criteria_",m);
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	public ActionForward createNewRequisitionProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("createNewRequisitionProduct");
		RequisitionProductForm moveOrderForm = (RequisitionProductForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 
			 //init Parametor By moveOrderType
			 moveOrderForm.getRequisitionProduct().setRequestDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 moveOrderForm.getRequisitionProduct().setSalesCode(user.getCode());
		
			 moveOrderForm.getRequisitionProduct().setCanEdit(true);
			//init priceListId by User Type
			 moveOrderForm.getRequisitionProduct().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 moveOrderForm.getRequisitionProduct().setShowSaveBtn(true);
			 moveOrderForm.getRequisitionProduct().setShowCancelBtn(false);
			 moveOrderForm.getRequisitionProduct().setShowPrintBtn(false);
			 
			// Save Criteria
		    request.getSession().setAttribute("criteria_",moveOrderForm.getRequisitionProduct());
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("new");
	}
	
	
	public ActionForward editRequisitionProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("editRequisitionProduct");
		RequisitionProductForm moveOrderForm = (RequisitionProductForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 //init Parametor By moveOrderType
			 moveOrderForm.getRequisitionProduct().setRequestNumber(requestNumber);
			 
			 RequisitionProduct m = prepareUpdateRequisitionProduct(user, moveOrderForm);
			 
			 m = new MRequisitionProduct().searchRequisitionProduct(moveOrderForm.getRequisitionProduct(),user);
			 logger.debug("totalAmount:"+m.getTotalAmount());
			 
			 moveOrderForm.setRequisitionProduct(m);
			 //Set Lines
			 moveOrderForm.setLines(m.getRequisitionProductLineList());
			 
			//init priceListId by User Type
			 moveOrderForm.getRequisitionProduct().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 if(m.isRequestDateDisabled()){ //Case requestDate 30-31 
			    moveOrderForm.getRequisitionProduct().setShowSaveBtn(false);
			 }else{
				moveOrderForm.getRequisitionProduct().setShowSaveBtn(true); 
			 }
			 moveOrderForm.getRequisitionProduct().setShowCancelBtn(true);
			 moveOrderForm.getRequisitionProduct().setShowPrintBtn(true);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward cancelRequisitionProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelMoveOrder");
		RequisitionProductForm moveOrderForm = (RequisitionProductForm) form;
		User user = (User) request.getSession().getAttribute("user");
		MRequisitionProduct mDAO = new MRequisitionProduct();
		try {
			 RequisitionProduct m = prepareCancelRequisitionProduct(user, moveOrderForm);
			 mDAO.updateCancelRequisitionProduct(m);
			 
			 /** Search **/
			 m = mDAO.searchRequisitionProduct(moveOrderForm.getRequisitionProduct(),user);
			 moveOrderForm.setRequisitionProduct(m);
			 //Set Lines
			 moveOrderForm.setLines(m.getRequisitionProductLineList());
			
			//init priceListId by User Type
			 moveOrderForm.getRequisitionProduct().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 request.setAttribute("Message","ยกเลิกรายการสำเร็จ");
			 
			 //set Btn Display
			 moveOrderForm.getRequisitionProduct().setShowSaveBtn(true);
			 moveOrderForm.getRequisitionProduct().setShowCancelBtn(false);
			 moveOrderForm.getRequisitionProduct().setShowPrintBtn(true);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("preview");
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequisitionProductForm moveOrderForm = (RequisitionProductForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			logger.debug("save-->");
			
			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				moveOrderForm.getLines().clear();
				return "new";
			}
			
			RequisitionProduct m = prepareCreateRequisitionProduct(user, moveOrderForm);
			MRequisitionProduct mDAO = new MRequisitionProduct();
			
			m = mDAO.save(user,m);
			
			if("".equals(m.getRequestNumber())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}
			
			mDAO.searchRequisitionProduct(m,user);
			moveOrderForm.setLines(m.getRequisitionProductLineList());
			
			request.setAttribute("Message",msg );
			
			 //set Btn Display
			 moveOrderForm.getRequisitionProduct().setShowSaveBtn(true);
			 moveOrderForm.getRequisitionProduct().setShowCancelBtn(true);
			 moveOrderForm.getRequisitionProduct().setShowPrintBtn(true);
			 
			// Save Criteria
			request.getSession().setAttribute("criteria_",moveOrderForm.getCriteria().getRequisitionProduct());
				
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "preview";
	}

    private RequisitionProduct prepareCreateRequisitionProduct(User user,RequisitionProductForm mForm) throws Exception {
    	RequisitionProduct m = mForm.getCriteria().getRequisitionProduct();
    	if(m==null){
    		m = new RequisitionProduct();
    	}
    	logger.debug("RequisitionProduct:"+mForm.getRequisitionProduct()+",User:"+user);
    	m.setUserId(user.getId()+"");
    	m.setCreatedBy(user.getUserName());
    	m.setUpdateBy(user.getUserName());
        
    	if("".equals(m.getRequestNumber())){
    		//create 
    		m.setCreatedLong(DateUtil.getCurrentTimestampLong());
    	}else{
    		//update
    		m.setUpdatedLong(DateUtil.getCurrentTimestampLong());
    	}
    	m.setOrganizationId(MMoveOrder.ORG_ID_DEFAULT);	//84
    	
    	m.setRequisitionProductLineList(mForm.getLines());
    	
    	if( !Utils.isNull(mForm.getLineNoDeleteArray()).equals("")){
    		String[] lineNoDeleteSplit = Utils.isNull(mForm.getLineNoDeleteArray()).split("\\,");
    		if(lineNoDeleteSplit != null && lineNoDeleteSplit.length > 0){
    			List<String> lineNoDeleteList = new ArrayList<String>();
    			for(int i=0;i<lineNoDeleteSplit.length;i++){
    				String lineNo = Utils.isNull(lineNoDeleteSplit[i]);
    				if( !Utils.isNull(lineNo).equals("")){
    					lineNoDeleteList.add(lineNo);
    				}
    			}
    			m.setLineNoDeleteList(lineNoDeleteList);
    		}
    	}
    	
    
		return m;
	}
    
    private RequisitionProduct prepareUpdateRequisitionProduct(User user,RequisitionProductForm mForm) throws Exception {
    	RequisitionProduct m = mForm.getRequisitionProduct();
    	logger.debug("prepareUpdateRequisitionProduct:");
    	m.setUserId(user.getId()+"");
    	m.setCreatedBy(user.getUserName());
    	m.setUpdateBy(user.getUserName());
    	m.setUpdatedLong(DateUtil.getCurrentTimestampLong());
    	
    	if( !Utils.isNull(mForm.getLineNoDeleteArray()).equals("")){
    		String[] lineNoDeleteSplit = Utils.isNull(mForm.getLineNoDeleteArray()).split("\\,");
    		if(lineNoDeleteSplit != null && lineNoDeleteSplit.length > 0){
    			List<String> lineNoDeleteList = new ArrayList<String>();
    			for(int i=0;i<lineNoDeleteSplit.length;i++){
    				String lineNo = Utils.isNull(lineNoDeleteSplit[i]);
    				if( !Utils.isNull(lineNo).equals("")){
    					lineNoDeleteList.add(lineNo);
    				}
    			}
    			m.setLineNoDeleteList(lineNoDeleteList);
    		}
    	}
		return m;
	}
    
    private RequisitionProduct prepareCancelRequisitionProduct(User user,RequisitionProductForm mForm) throws Exception {
    	RequisitionProduct m = mForm.getRequisitionProduct();
    	logger.debug("prepareCancelRequisitionProduct:");
    	m.setStatus(MMoveOrder.STATUS_VOID);//Cancel MoveOrder
    	m.setUserId(user.getId()+"");
    	m.setUpdateBy(user.getUserName());
    	m.setUpdatedLong(DateUtil.getCurrentTimestampLong());
    	
		return m;
	}
    
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		RequisitionProductForm moveOrderForm = (RequisitionProductForm) form;
		try {
			//Clear Parameter 
			 moveOrderForm.getRequisitionProduct().setRequestDateFrom(null);
			 moveOrderForm.getRequisitionProduct().setRequestDateTo(null);
			 moveOrderForm.setResults(null);
			 moveOrderForm.setLines(null);
			 
			 //Clear Criteria
			 request.getSession().setAttribute("criteria_",null);
			 
		} catch (Exception e) {
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
