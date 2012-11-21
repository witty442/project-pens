package com.isecinc.pens.web.moveorder;

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
import util.DBCPConnectionProvider;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MMoveOrder;
import com.isecinc.pens.model.MPriceList;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MoveOrderAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parametor By moveOrderType  
				 moveOrderForm.setMoveOrder(new MoveOrder());
				 moveOrderForm.getMoveOrder().setMoveOrderType(moveOrderType);
				 moveOrderForm.getMoveOrder().setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(moveOrderType));
				//init priceListId by User Type
				 moveOrderForm.getMoveOrder().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 moveOrderForm.setResults(null);
				 moveOrderForm.setLines(null);
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
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		try {
			logger.debug("prepare 2:"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parametor By moveOrderType  
				 moveOrderForm.setMoveOrder(new MoveOrder());
				 moveOrderForm.getMoveOrder().setMoveOrderType(moveOrderType);
				 moveOrderForm.getMoveOrder().setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(moveOrderType));
				//init priceListId by User Type
				 moveOrderForm.getMoveOrder().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 moveOrderForm.setResults(null);
				 moveOrderForm.setLines(null);
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
		MoveOrderForm mForm = (MoveOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MMoveOrder mDAO = new MMoveOrder();
			MoveOrder m = prepareCreateMoveOrder(user, mForm);
			List<MoveOrder> moveOrderList = mDAO.searchMoveOrderList(m,user);
			mForm.setResults(moveOrderList);
			if(moveOrderList != null && moveOrderList.size()==0){
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
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
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 
			 //init Parametor By moveOrderType
			 moveOrderForm.getMoveOrder().setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 moveOrderForm.getMoveOrder().setSalesCode(user.getCode());
			 moveOrderForm.getMoveOrder().setMoveOrderType(moveOrderType);
			 moveOrderForm.getMoveOrder().setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(moveOrderType));
			 moveOrderForm.getMoveOrder().setCanEdit(true);
			//init priceListId by User Type
			 moveOrderForm.getMoveOrder().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 moveOrderForm.getMoveOrder().setShowSaveBtn(true);
			 moveOrderForm.getMoveOrder().setShowCancelBtn(false);
			 moveOrderForm.getMoveOrder().setShowPrintBtn(false);
			 
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
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 //init Parametor By moveOrderType
			 moveOrderForm.getMoveOrder().setMoveOrderType(moveOrderType);
			 moveOrderForm.getMoveOrder().setRequestNumber(requestNumber);
			 
			 MoveOrder m = prepareUpdateMoveOrder(user, moveOrderForm);
			 
			 m = new MMoveOrder().searchMoveOrder(moveOrderForm.getMoveOrder(),user);
			 logger.debug("totalAmount:"+m.getTotalAmount());
			 
			 moveOrderForm.setMoveOrder(m);
			 //Set Lines
			 moveOrderForm.setLines(m.getMoveOrderLineList());
			 
			 moveOrderForm.getMoveOrder().setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(m.getMoveOrderType()));
			//init priceListId by User Type
			 moveOrderForm.getMoveOrder().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 moveOrderForm.getMoveOrder().setShowSaveBtn(true);
			 moveOrderForm.getMoveOrder().setShowCancelBtn(true);
			 moveOrderForm.getMoveOrder().setShowPrintBtn(true);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward cancelMoveOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelMoveOrder");
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		MMoveOrder mDAO = new MMoveOrder();
		try {
			 MoveOrder m = prepareCancelMoveOrder(user, moveOrderForm);
			 mDAO.updateCancelMoveOrder(m);
			 
			 /** Search **/
			 m = mDAO.searchMoveOrder(moveOrderForm.getMoveOrder(),user);
			 moveOrderForm.setMoveOrder(m);
			 //Set Lines
			 moveOrderForm.setLines(m.getMoveOrderLineList());
			 moveOrderForm.getMoveOrder().setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(m.getMoveOrderType()));
			//init priceListId by User Type
			 moveOrderForm.getMoveOrder().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 request.setAttribute("Message","ยกเลิกรายการสำเร็จ");
			 
			 //set Btn Display
			 moveOrderForm.getMoveOrder().setShowSaveBtn(true);
			 moveOrderForm.getMoveOrder().setShowCancelBtn(false);
			 moveOrderForm.getMoveOrder().setShowPrintBtn(true);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("preview");
	}
	
	public ActionForward printMoveOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printMoveOrder");
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		 MMoveOrder mDAO = new MMoveOrder();
		try {
			//Update Print Date
			 MoveOrder m = preparePrintMoveOrder(user, moveOrderForm);
			 mDAO.updatePrintMoveOrder(m);
			 
			/** Search **/
			 m = mDAO.searchMoveOrder(moveOrderForm.getMoveOrder(),user);
			 moveOrderForm.setMoveOrder(m);
			 //Set Lines
			 moveOrderForm.setLines(m.getMoveOrderLineList());
			 moveOrderForm.getMoveOrder().setMoveOrderTypeLabel(MMoveOrder.MOVE_ORDER_TYPE_MAP.get(m.getMoveOrderType()));
			//init priceListId by User Type
			 moveOrderForm.getMoveOrder().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 request.setAttribute("Message","พิมพ์รายการสำเร็จ");
			 request.setAttribute("popupPrint", "true");
			 
			 //set Btn Display
			 moveOrderForm.getMoveOrder().setShowSaveBtn(false);
			 moveOrderForm.getMoveOrder().setShowCancelBtn(false);
			 moveOrderForm.getMoveOrder().setShowPrintBtn(false);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("preview");
	}
	
	
	public ActionForward popupPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printMoveOrder");
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String moveOrderType = Utils.isNull(request.getParameter("moveOrderType"));
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 
			 moveOrderForm.getMoveOrder().setMoveOrderType(moveOrderType);
			 moveOrderForm.getMoveOrder().setRequestNumber(requestNumber);
			 moveOrderForm.getMoveOrder().setUserId(user.getId()+"");
			 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,String> parameterMap = new HashMap<String,String>();
			List<MoveOrderLine> lstData = null;
			Connection conn = null;
			String fileName = "move_order_report";
			String reportName = "";
			String reportType ="";
            String fileJasper = BeanParameter.getReportPath() + fileName;
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			MoveOrder mResult = new MMoveOrder().searchMoveOrder(moveOrderForm.getMoveOrder(),user);
			
			if(MMoveOrder.MOVE_ORDER_REQUISITION.equals(moveOrderForm.getMoveOrder().getMoveOrderType())){
				reportName = "ใบเบิกสินค้า";
				reportType = "request";
				
				parameterMap.put("p_fromCode",mResult.getPdCode());
				parameterMap.put("p_fromDesc",mResult.getPdDesc());
				parameterMap.put("p_toCode",mResult.getSalesCode());
				parameterMap.put("p_toDesc",mResult.getSalesDesc());
			}else{
				reportName = "ใบคืนสินค้า";
				reportType = "return";
				
				parameterMap.put("p_fromCode",mResult.getSalesCode());
				parameterMap.put("p_fromDesc",mResult.getSalesDesc());
				parameterMap.put("p_toCode",mResult.getPdCode());
				parameterMap.put("p_toDesc",mResult.getPdDesc());
			}
			
			//Set Parameter Map
			parameterMap.put("p_reportName",reportName);
			parameterMap.put("p_reportType",reportType);
			parameterMap.put("p_printDate",mResult.getPrintDate());
			parameterMap.put("p_requestNumber",mResult.getRequestNumber());
			parameterMap.put("p_createDate",mResult.getCreated());
			parameterMap.put("p_printNo",mResult.getPrintNo());
			parameterMap.put("p_requestDate",mResult.getRequestDate());
			
			
			if(MMoveOrder.STATUS_VOID.equals(mResult.getStatus())){
			  parameterMap.put("p_statusLabel","(ยกเลิก)");
			  parameterMap.put("p_cancelReason","เหตุผลในการยกเลิก "+mResult.getDescription());
			}else{
			  parameterMap.put("p_statusLabel","");
			  parameterMap.put("p_cancelReason",mResult.getDescription());
			}
			//Set Lines
			lstData = mResult.getMoveOrderLineList();
			
			if (lstData != null && lstData.size() > 0) {
				reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData);
			} 
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("popupPrint");
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
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
			
			MoveOrder m = prepareCreateMoveOrder(user, moveOrderForm);
			MMoveOrder mDAO = new MMoveOrder();
			
			m = mDAO.save(user,m);
			
			if("".equals(m.getRequestNumber())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}
			
			mDAO.searchMoveOrder(m,user);
			moveOrderForm.setLines(m.getMoveOrderLineList());
			
			request.setAttribute("Message",msg );
			
			 //set Btn Display
			 moveOrderForm.getMoveOrder().setShowSaveBtn(false);
			 moveOrderForm.getMoveOrder().setShowCancelBtn(false);
			 moveOrderForm.getMoveOrder().setShowPrintBtn(true);
			 
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

    private MoveOrder prepareCreateMoveOrder(User user,MoveOrderForm mForm) throws Exception {
    	MoveOrder m = mForm.getMoveOrder();
    	logger.debug("moveOrder:"+mForm.getMoveOrder().getSalesCode()+","+mForm.getMoveOrder().getPdCode()+",");
    	m.setUserId(user.getId()+"");
    	m.setCreatedBy(user.getUserName());
    	m.setUpdateBy(user.getUserName());
        
    	if("".equals(m.getRequestNumber())){
    		//create 
    		m.setCreatedLong(Utils.getCurrentTimestampLong());
    	}else{
    		//update
    		m.setUpdatedLong(Utils.getCurrentTimestampLong());
    	}
    	m.setOrganizationId(MMoveOrder.ORG_ID_DEFAULT);	//84
    	
    	m.setMoveOrderLineList(mForm.getLines());
    	
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
    
    private MoveOrder prepareUpdateMoveOrder(User user,MoveOrderForm mForm) throws Exception {
    	MoveOrder m = mForm.getMoveOrder();
    	logger.debug("moveOrder:"+mForm.getMoveOrder().getSalesCode()+","+mForm.getMoveOrder().getPdCode()+",");
    	m.setUserId(user.getId()+"");
    	m.setCreatedBy(user.getUserName());
    	m.setUpdateBy(user.getUserName());
    	m.setUpdatedLong(Utils.getCurrentTimestampLong());
    	
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
    
    private MoveOrder prepareCancelMoveOrder(User user,MoveOrderForm mForm) throws Exception {
    	MoveOrder m = mForm.getMoveOrder();
    	logger.debug("moveOrder:"+mForm.getMoveOrder().getSalesCode()+","+mForm.getMoveOrder().getPdCode()+",");
    	m.setStatus(MMoveOrder.STATUS_VOID);//Cancel MoveOrder
    	m.setUserId(user.getId()+"");
    	m.setUpdateBy(user.getUserName());
    	m.setUpdatedLong(Utils.getCurrentTimestampLong());
    	
		return m;
	}
    
    private MoveOrder preparePrintMoveOrder(User user,MoveOrderForm mForm) throws Exception {
    	MoveOrder m = mForm.getMoveOrder();
    	logger.debug("moveOrder:"+mForm.getMoveOrder().getSalesCode()+","+mForm.getMoveOrder().getPdCode()+",");
    	m.setUserId(user.getId()+"");
    	m.setPrintDateLong(Utils.getCurrentTimestampLong());
    	
		return m;
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MoveOrderForm moveOrderForm = (MoveOrderForm) form;
		try {
			//Clear Parametor 
			 moveOrderForm.getMoveOrder().setRequestDateFrom(null);
			 moveOrderForm.getMoveOrder().setRequestDateTo(null);
			 moveOrderForm.setResults(null);
			 moveOrderForm.setLines(null);
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
