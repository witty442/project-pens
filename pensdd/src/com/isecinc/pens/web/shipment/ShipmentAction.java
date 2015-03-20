package com.isecinc.pens.web.shipment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.ShipmentConfirm;
import com.isecinc.pens.bean.ShipmentSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MShipmentConfirm;

public class ShipmentAction extends I_Action {

	protected String prepare(String id, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		return "prepare";
	}

	protected String search(ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		ShipmentForm shipmentForm = (ShipmentForm)form;
		
		// Get Paramter From UI
		String memberCode = shipmentForm.getShipment().getMemberCode();
		String shipDate = shipmentForm.getShipment().getShipDate();
		
		MShipmentConfirm mShipment = MShipmentConfirm.getInstance();
		
		ShipmentConfirm shipment = mShipment.getShipmentList(memberCode, shipDate,"N",false);
		List<ShipmentConfirm> shipmentL = shipment.getShipmentConfirmList();
		
		ShipmentConfirm[] results = null;
		if(shipmentL.size() > 0){
			results = new ShipmentConfirm[shipmentL.size()];
			shipmentL.toArray(results);
		}
		shipmentForm.setTotalGroup(shipment.getTotalGroup());
		shipmentForm.getCriteria().setSearchResult(shipmentL.size());
		shipmentForm.setResults(results);
		
		shipmentForm.setConfirmDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
		
		if (results != null) {
			shipmentForm.getShipmentCriteria().setSearchResult(results.length);
		} 
		else{
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
		}
		
		log.info("Forward To Prepare");
		
		return "prepare";
	}

	protected String save(ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		try{
			ShipmentForm shipmentForm = (ShipmentForm)form;
			List<ShipmentConfirm> confirmList = shipmentForm.getConfirms();
			String confirmDate = shipmentForm.getConfirmDate();
			User user = (User) request.getSession(true).getAttribute("user");
			
			MShipmentConfirm mShipment = MShipmentConfirm.getInstance();
			
			ShipmentSummary summary = mShipment.confirmShipments(confirmList,confirmDate,user);
			summary.setNo_of_confirm(shipmentForm.getTotalGroup());//Total Group
			shipmentForm.setSummary(summary);
			
		}catch(Exception e){
			request.setAttribute("Message",e.getMessage());
		}
		return "summary";
	}
	

	public ActionForward saveCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) 
	{
		logger.debug("Save Cancel ");
		try{
			ShipmentForm shipmentForm = (ShipmentForm)form;
			List<ShipmentConfirm> confirmList = shipmentForm.getConfirms();
			User user = (User) request.getSession(true).getAttribute("user");
			
			MShipmentConfirm mShipment = MShipmentConfirm.getInstance();
			
			ShipmentSummary summary = mShipment.cancelConfirmShipments(confirmList,user);
			summary.setNo_of_confirm(shipmentForm.getTotalGroup());//Total Group
			shipmentForm.setSummary(summary);
			
		}catch(Exception e){
			request.setAttribute("Message",e.getMessage());
		}
		return mapping.findForward("summaryCancel");
	}
	

	protected String changeActive(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentForm shipForm = (ShipmentForm) form;
		
		shipForm.getShipmentCriteria().getShipment().setMemberCode("");
		shipForm.getShipmentCriteria().getShipment().setShipDate("");
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		saveToken(request);
		return mapping.findForward("cancel");
	}
	
	public ActionForward clearCancelForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		logger.debug("Clear Form " + this.getClass());
		String searchKey = (String) request.getSession(true).getAttribute(this.getClass().toString());
		if (searchKey != null) {
			request.getSession(true).removeAttribute(searchKey);
			request.removeAttribute(this.getClass().toString());
		}
		// Clear Criteria
		try {
			setNewCriteria(form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("clearCancelForm");
	}
	
	public ActionForward searchCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		ShipmentForm shipmentForm = (ShipmentForm)form;
		
		try {
			// Get Paramter From UI
			String memberCode = shipmentForm.getShipment().getMemberCode();
			String shipDate = shipmentForm.getShipment().getShipDate();
			
			MShipmentConfirm mShipment = MShipmentConfirm.getInstance();
			
			logger.debug("memberCode:"+memberCode);
			logger.debug("shipDate:"+shipDate);
			
			ShipmentConfirm shipment = mShipment.getShipmentList(memberCode, shipDate,"Y",true);
			List<ShipmentConfirm> shipmentL =shipment.getShipmentConfirmList();
			ShipmentConfirm[] results = null;
			if(shipmentL.size() > 0){
				results = new ShipmentConfirm[shipmentL.size()];
				shipmentL.toArray(results);
			}
			
			shipmentForm.getCriteria().setSearchResult(shipmentL.size());
			shipmentForm.setResults(results);
			
			//shipmentForm.setConfirmDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
			
			if (results != null) {
				shipmentForm.getShipmentCriteria().setSearchResult(results.length);
			} 
			else{
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			
			saveToken(request);
			
			log.info("Forward To Prepare");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return mapping.findForward("searchCancel");
	}
	
	public ActionForward saveCancelLine(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		ShipmentForm shipmentForm = (ShipmentForm)form;
		String lineId = request.getParameter("lineid");
		log.debug("Save Cancel Line Id "+lineId);
		
		saveToken(request);
		
		return searchCancel(mapping,shipmentForm,request,response);
	}
}
