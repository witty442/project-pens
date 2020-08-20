package com.isecinc.pens.web.stockpd;

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

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.BillPlan;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.StockPD;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MMoveOrder;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MStockPD;
import com.pens.util.BeanParameter;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.ReportUtilServlet;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockPDAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		StockPDForm f = (StockPDForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user"); 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parametor By moveOrderType  
				 f.setStockPD(new StockPD());
				//init priceListId by User Type
				 f.getStockPD().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 f.setResults(null);
				 f.setLines(null);
				 
				 //Clear Criteria
				 request.getSession().setAttribute("criteria_",null);
				 
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 StockPD b = (StockPD)request.getSession().getAttribute("criteria_");
				 f.getCriteria().setStockPD(b);
				 search(f,request,response); 
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
		StockPDForm f = (StockPDForm) form;
		try {
			 logger.debug("prepare 2:"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				//Clear and init Parametor By moveOrderType  
				 f.setStockPD(new StockPD());
				//init priceListId by User Type
				 f.getStockPD().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 f.setResults(null);
				 f.setLines(null);
				 
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
		StockPDForm mForm = (StockPDForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MStockPD mDAO = new MStockPD();
			StockPD m = prepareCreateStockPD(user, mForm);
			List<StockPD> moveOrderList = mDAO.searchStockPDList(m,user);
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

	public ActionForward createNewMoveOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("createNewMoveOrder");
		StockPDForm f = (StockPDForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 
			 //init Parametor By moveOrderType
			 f.getStockPD().setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 f.getStockPD().setSalesCode(user.getCode());
			 f.getStockPD().setCanEdit(true);
			//init priceListId by User Type
			 f.getStockPD().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 f.getStockPD().setShowSaveBtn(true);
			 f.getStockPD().setShowCancelBtn(false);
			 f.getStockPD().setShowPrintBtn(false);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("new");
	}
	
	
	public ActionForward editMoveOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("editMoveOrder");
		StockPDForm f = (StockPDForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 //init Parametor By moveOrderType
			 f.getStockPD().setRequestNumber(requestNumber);
			 
			 StockPD m = prepareUpdateStockPD(user, f);
			 
			 m = new MStockPD().searchStockPD(f.getStockPD(),user);
			 logger.debug("totalAmount:"+m.getTotalAmount());
			 
			 f.setStockPD(m);
			 //Set Lines
			 f.setLines(m.getStockPDLineList());
			//init priceListId by User Type
			 f.getStockPD().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 if(m.isRequestDateDisabled()){ //Case requestDate 30-31 
			    f.getStockPD().setShowSaveBtn(false);
			 }else{
				f.getStockPD().setShowSaveBtn(true); 
			 }
			 f.getStockPD().setShowCancelBtn(true);
			 f.getStockPD().setShowPrintBtn(true);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("edit");
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockPDForm f = (StockPDForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			logger.debug("save-->");
			
			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				f.getLines().clear();
				return "new";
			}
			
			StockPD m = prepareCreateStockPD(user, f);
			MStockPD mDAO = new MStockPD();
			
			m = mDAO.save(user,m);
			
			if("".equals(m.getRequestNumber())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}
			
			mDAO.searchStockPD(m,user);
			f.setLines(m.getStockPDLineList());
			
			request.setAttribute("Message",msg );
			
			 //set Btn Display
			 f.getStockPD().setShowSaveBtn(false);
			 f.getStockPD().setShowCancelBtn(false);
			 f.getStockPD().setShowPrintBtn(true);
			 
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

    private StockPD prepareCreateStockPD(User user,StockPDForm mForm) throws Exception {
    	StockPD m = mForm.getStockPD();
    	//logger.debug("moveOrder:"+mForm.getMoveOrder().getSalesCode()+","+mForm.getMoveOrder().getPdCode()+",");
    	m.setUserId(user.getId()+"");
    	m.setCreatedBy(user.getUserName());
    	m.setUpdateBy(user.getUserName());
       
    	m.setOrganizationId(MMoveOrder.ORG_ID_DEFAULT);	//84
    	
    	m.setStockPDLineList(mForm.getLines());
    	
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
    
    private StockPD prepareUpdateStockPD(User user,StockPDForm mForm) throws Exception {
    	StockPD m = mForm.getStockPD();
    	//logger.debug("moveOrder:"+mForm.getMoveOrder().getSalesCode()+","+mForm.getMoveOrder().getPdCode()+",");
    	m.setUserId(user.getId()+"");
    	m.setCreatedBy(user.getUserName());
    	m.setUpdateBy(user.getUserName());
    	
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
    
 
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StockPDForm f = (StockPDForm) form;
		try {
			//Clear Parameter 
			 f.getStockPD().setRequestDateFrom(null);
			 f.getStockPD().setRequestDateTo(null);
			 f.setResults(null);
			 f.setLines(null);
			 
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
