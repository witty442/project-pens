package com.isecinc.pens.web.reqPromotion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.RequestPromotion;
import com.isecinc.pens.bean.RequestPromotionCost;
import com.isecinc.pens.bean.RequestPromotionLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MRequestPromotion;
import com.isecinc.pens.web.externalprocess.ProcessAfterAction;
import com.pens.util.DBCPConnectionProvider;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class RequestPromotionAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		RequestPromotionForm f = (RequestPromotionForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 RequestPromotion req = new RequestPromotion();
				 req.setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
				 
				 f.setRequestPromotion(req);
				 
				 //Clear Criteria
				 request.getSession().setAttribute("criteria_",null);
				 request.getSession().setAttribute("costTableMap",null);
				 request.getSession().setAttribute("lineTableMap",null);
				 
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 RequestPromotion b = (RequestPromotion)request.getSession().getAttribute("criteria_");
				 f.setRequestPromotion(b);
				 
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
		RequestPromotionForm f = (RequestPromotionForm) form;
		try {
			logger.debug("prepare 2:"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 String RequestPromotionType = Utils.isNull(request.getParameter("RequestPromotionType"));
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
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
		RequestPromotionForm mForm = (RequestPromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MRequestPromotion mDAO = new MRequestPromotion();
			RequestPromotion m = mForm.getRequestPromotion();
			m.setUserId(String.valueOf(user.getId()));
			
			List<RequestPromotion> RequestPromotionList = mDAO.searchReqPromotionList(m,user,false);
	
			mForm.setResults(RequestPromotionList);
			mForm.setRequestPromotion(m);
			
			if(RequestPromotionList != null && RequestPromotionList.size()==0){
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

	public ActionForward createRequestPromotion(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("createRequestPromotion");
		RequestPromotionForm f = (RequestPromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
			//Case Link From Customer Search
			if (request.getParameter("shotcut_customerId") != null) {
				String customerId = Utils.isNull(request.getParameter("shotcut_customerId"));
				String fromPage = Utils.isNull(request.getParameter("fromPage"));
				
				Customer m = new MCustomer().find(customerId);
				if(m != null){
					f.getRequestPromotion().setCustomerCode(m.getCode());
					f.getRequestPromotion().setCustomerName(m.getName());
				}
				request.getSession().setAttribute("shutcut_customerId", customerId);
				request.getSession().setAttribute("fromPage", fromPage);
			}else{
				request.getSession().setAttribute("shutcut_customerId", null);
				request.getSession().setAttribute("fromPage", null);
			}
			
			
			//clear table map
			 request.getSession().setAttribute("costTableMap",null);
			 request.getSession().setAttribute("lineTableMap",null);
			 
			 // Save Old Criteria
			 request.getSession().setAttribute("criteria_",f.getRequestPromotion());
						
			 logger.info("priceListId:"+(new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId()));
			 
			 //init Parametor By RequestPromotionType
			 f.getRequestPromotion().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 f.getRequestPromotion().setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 f.getRequestPromotion().setUser(user);
			 f.getRequestPromotion().setCanEdit(true);
			 f.getRequestPromotion().setCanGenFile(false);
			 f.getRequestPromotion().setCanCancel(false);
			 
			 List<References> ref = InitialReferences.getReferenes(InitialReferences.TERRITORY,user.getTerritory());
			 String territory = ref != null && ref.size() >0?(ref.get(0).getName()):"";
			 f.getRequestPromotion().setTerritory(territory);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("new");
	}
	
	public ActionForward editRequestPromotion(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("editRequestPromotion");
		RequestPromotionForm f = (RequestPromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 
			String requestNo = Utils.isNull(request.getParameter("requestNo"));
			RequestPromotion m = new RequestPromotion();
			m.setRequestNo(requestNo);
			
			List<RequestPromotion> requestPromotionList = new MRequestPromotion().searchReqPromotionList(m,user,true);
			if(requestPromotionList != null && requestPromotionList.size() >0){
				 m = requestPromotionList.get(0);	
				 //convert to Map
				 if(m.getCostLineList() != null && m.getCostLineList().size()>0){
					 Map<String, RequestPromotionCost> costTableMap = new HashMap<String, RequestPromotionCost>();
					 for(int i=0;i<m.getCostLineList().size();i++){
						 RequestPromotionCost c = m.getCostLineList().get(i);
						 costTableMap.put(c.getLineNo()+"", c);
					 }
					 
					 request.getSession().setAttribute("costTableMap", costTableMap);
				 }
				 
				 if(m.getPromotionLineList() != null && m.getPromotionLineList().size()>0){
					 Map<String, RequestPromotionLine> costTableMap = new HashMap<String, RequestPromotionLine>();
					 for(int i=0;i<m.getPromotionLineList().size();i++){
						 RequestPromotionLine c = m.getPromotionLineList().get(i);
						 costTableMap.put(c.getLineNo()+"", c);
					 }
					 
					 request.getSession().setAttribute("lineTableMap", costTableMap);
				 } 
			}
			
			//set priceListId
			m.setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			
			f.setRequestPromotion(m);
				
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("new");
	}
	
	public ActionForward cancelRequestPromotion(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelRequestPromotion");
		RequestPromotionForm f = (RequestPromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
		 
			RequestPromotion m = f.getRequestPromotion();
			m.setUserId(user.getId()+"");
			m.setCreatedBy(user.getId()+"");
			m.setUpdateBy(user.getId()+"");
			m.setStatus(MRequestPromotion.STATUS_VOID);
			
			MRequestPromotion mDAO = new MRequestPromotion();
			m = mDAO.updateCancelRequestPromotion(m);
			
			request.setAttribute("Message","ยกเลิกรายการเรียบร้อยแล้ว" );
			
			//Search
			List<RequestPromotion> requestPromotionList = new MRequestPromotion().searchReqPromotionList(m,user,true);
			if(requestPromotionList != null && requestPromotionList.size() >0){
				 m = requestPromotionList.get(0);	
				 //convert to Map
				 if(m.getCostLineList() != null && m.getCostLineList().size()>0){
					 Map<String, RequestPromotionCost> costTableMap = new HashMap<String, RequestPromotionCost>();
					 for(int i=0;i<m.getCostLineList().size();i++){
						 RequestPromotionCost c = m.getCostLineList().get(i);
						 costTableMap.put(c.getLineNo()+"", c);
					 }
					 
					 request.getSession().setAttribute("costTableMap", costTableMap);
				 }
				 
				 if(m.getPromotionLineList() != null && m.getPromotionLineList().size()>0){
					 Map<String, RequestPromotionLine> costTableMap = new HashMap<String, RequestPromotionLine>();
					 for(int i=0;i<m.getPromotionLineList().size();i++){
						 RequestPromotionLine c = m.getPromotionLineList().get(i);
						 costTableMap.put(c.getLineNo()+"", c);
					 }
					 
					 request.getSession().setAttribute("lineTableMap", costTableMap);
				 }
				 
			}
			
			f.setRequestPromotion(m);
				
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("new");
	}
	
	public ActionForward popupPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printRequestPromotion");
		RequestPromotionForm f = (RequestPromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try { 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
			List<RequestPromotionLine> lstData = null;
	
			//Update DB PrintDate
			new MRequestPromotion().updatePrintDateRequestPromotion(f.getRequestPromotion());
			
			String fileName = "request_promotion_report";
            String fileJasper = BeanParameter.getReportPath() + fileName;
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			List<RequestPromotion> mResultList = new MRequestPromotion().searchReqPromotionList(f.getRequestPromotion(), user, true);

			if(mResultList != null && mResultList.size()>0){
				RequestPromotion p = mResultList.get(0);
				
				String logopath =   context.getRealPath("/images/pens_logo_fit.jpg");//
				//logger.debug("logoPath:"+logopath);
	            
				parameterMap.put("pens_logo_fit",logopath);
				parameterMap.put("requestDate",p.getRequestDate());
				parameterMap.put("requestNo",p.getRequestNo());
				parameterMap.put("printDate",Utils.isNull(p.getPrintDate()));
				parameterMap.put("userPrint",user.getName());
				
				//Set Parameter Map
				List<References> ref = InitialReferences.getReferenes(InitialReferences.TERRITORY,user.getTerritory());
				String territoryStr = ref != null && ref.size() >0?(ref.get(0).getName()):"";
				
				parameterMap.put("productCatagory",p.getProductCatagory()+"-"+p.getProductCatagoryDesc());
				parameterMap.put("productType",p.getProductType());
				parameterMap.put("salesCode",user.getCode());
				parameterMap.put("salesName",user.getName());
				parameterMap.put("territory",territoryStr);
				parameterMap.put("customerCode",p.getCustomerCode());
				parameterMap.put("customerName",p.getCustomerName());
				parameterMap.put("phone",p.getPhone());
				
				parameterMap.put("promotionStartDate",p.getPromotionStartDate());
				parameterMap.put("promotionEndDate",p.getPromotionEndDate());
				parameterMap.put("name",p.getName());
				parameterMap.put("remark",p.getRemark());
				
				//set cost Table
				if(p.getCostLineList() != null && p.getCostLineList().size()>0){
					 Map<String, RequestPromotionCost> costTableMap = new HashMap<String, RequestPromotionCost>();
					 for(int i=0;i<p.getCostLineList().size();i++){
						 RequestPromotionCost c = p.getCostLineList().get(i);
						 //logger.debug("lineNo["+c.getLineNo()+"]");
						 
						 costTableMap.put(c.getLineNo()+"", c);
					 }
	                 logger.debug("CostDetail1:"+costTableMap.get("1").getCostDetail());
					 
					parameterMap.put("costDetail1",Utils.isNull(costTableMap.get("1").getCostDetail()));
					parameterMap.put("costAmount1",costTableMap.get("1").getCostAmount());
					parameterMap.put("costDetail2",Utils.isNull(costTableMap.get("2").getCostDetail()));
					parameterMap.put("costAmount2",costTableMap.get("2").getCostAmount());
					parameterMap.put("costDetail3",Utils.isNull(costTableMap.get("3").getCostDetail()));
					parameterMap.put("costAmount3",costTableMap.get("3").getCostAmount());
					parameterMap.put("costDetail4",Utils.isNull(costTableMap.get("4").getCostDetail()));
					parameterMap.put("costAmount4",costTableMap.get("4").getCostAmount());
					parameterMap.put("costDetail5",Utils.isNull(costTableMap.get("5").getCostDetail()));
					parameterMap.put("costAmount5",costTableMap.get("5").getCostAmount());
						
				 }
				
				String fileNameExport = p.getRequestNo()+"_"+p.getPrintDate()+".pdf";
				//Set Lines
				lstData = p.getPromotionLineList();
				
				reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData,fileNameExport);
				
			}else{
				request.setAttribute("Message","Data not found");
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
		RequestPromotionForm f = (RequestPromotionForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			logger.debug("save-->");
			
			// check Token
			/*if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				return "new";
			}*/
			
			RequestPromotion m = f.getRequestPromotion();
			m.setUserId(user.getId()+"");
			m.setCreatedBy(user.getId()+"");
			m.setUpdateBy(user.getId()+"");
			m.setStatus(MRequestPromotion.STATUS_SAVE);
			
			//Cost Table
			List<RequestPromotionCost> costList = new ArrayList<RequestPromotionCost>();
			Map<String, RequestPromotionCost> costMap = new HashMap<String, RequestPromotionCost>();
			
			String[] costDetail = request.getParameterValues("costDetail");
			String[] costAmount = request.getParameterValues("costAmount");
			String[] lineCost = request.getParameterValues("lineCost");
			
			for(int i=0;i<5;i++){
				logger.debug("i["+i+"]costAmount["+costAmount[i]+"]costDetail["+costDetail[i]+"]");
				RequestPromotionCost c = new RequestPromotionCost();
				c.setLineNo(Integer.parseInt(lineCost[i]));
				c.setCostDetail(Utils.isNull(costDetail[i]));
				if( !Utils.isNull(costAmount[i]).equals("")){
				  c.setCostAmount(new BigDecimal(Utils.convertStrToDouble(costAmount[i])));
				}
				
				costList.add(c);
				costMap.put(c.getLineNo()+"", c);
			}
			
			request.getSession().setAttribute("costTableMap", costMap);
			m.setCostLineList(costList);
			
			//Line Table 
			List<RequestPromotionLine> lineList = new ArrayList<RequestPromotionLine>();
			Map<String, RequestPromotionLine> lineMap = new HashMap<String, RequestPromotionLine>();
			
			String[] lineNo = request.getParameterValues("lineNo");
			String[] productCode= request.getParameterValues("productCode");
			String[] productName= request.getParameterValues("productName");
			String[] productId= request.getParameterValues("productId");
			String[] uom1= request.getParameterValues("uom1");
			String[] uom2= request.getParameterValues("uom2");
			String[] price1= request.getParameterValues("price1");
			String[] price2= request.getParameterValues("price2");
			
			String[] newCtn= request.getParameterValues("newCtn");
			String[] newAmount= request.getParameterValues("newAmount");
			
			String[] stockCtn= request.getParameterValues("stockCtn");
			String[] stockQty= request.getParameterValues("stockQty");
			
			String[] borrowCtn= request.getParameterValues("borrowCtn");
			String[] borrowQty= request.getParameterValues("borrowQty");
			String[] borrowAmount= request.getParameterValues("borrowAmount");
			
			String[] invoiceNo= request.getParameterValues("invoiceNo");
			
			for(int i=0;i<7;i++){
				logger.debug("i["+i+"]productCode["+productCode[i]+"]lineNo["+lineNo[i]+"]");
				RequestPromotionLine line = new RequestPromotionLine();
				line.setLineNo(Integer.parseInt(lineNo[i]));
				line.setProductCode(Utils.isNull(productCode[i]));
				
				line.setNewCtn(Utils.convertStrToBig(newCtn[i]));
				line.setNewAmount(Utils.convertStrToBig(newAmount[i]));
				line.setStockCtn(Utils.convertStrToBig(stockCtn[i]));
				line.setStockQty(Utils.convertStrToBig(stockQty[i]));
			    line.setBorrowCtn(Utils.convertStrToBig(borrowCtn[i]));
				line.setBorrowQty(Utils.convertStrToBig(borrowQty[i]));
				line.setBorrowAmount(Utils.convertStrToBig(borrowAmount[i]));

				line.setProductName(Utils.isNull(productName[i]));
				line.setProductId(Utils.isNull(productId[i]));
				line.setUom1(Utils.isNull(uom1[i]));
				line.setUom2(Utils.isNull(uom2[i]));
				line.setPrice1(Utils.isNull(price1[i]));
				line.setPrice2(Utils.isNull(price2[i]));
				line.setInvoiceNo(invoiceNo[i]);
				
				lineList.add(line);
				lineMap.put(line.getLineNo()+"", line);
				
			}
			
			request.getSession().setAttribute("lineTableMap", lineMap);
			m.setPromotionLineList(lineList);
			
			MRequestPromotion mDAO = new MRequestPromotion();
			m = mDAO.save(user,m);
			
			if("".equals(m.getRequestNo())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}

			request.setAttribute("Message",msg );
			
			//Search
			List<RequestPromotion> requestPromotionList = new MRequestPromotion().searchReqPromotionList(m,user,true);
			if(requestPromotionList != null && requestPromotionList.size() >0){
				 m = requestPromotionList.get(0);	
				 //convert to Map
				 if(m.getCostLineList() != null && m.getCostLineList().size()>0){
					 Map<String, RequestPromotionCost> costTableMap = new HashMap<String, RequestPromotionCost>();
					 for(int i=0;i<m.getCostLineList().size();i++){
						 RequestPromotionCost c = m.getCostLineList().get(i);
						 costTableMap.put(c.getLineNo()+"", c);
					 }
					 
					 request.getSession().setAttribute("costTableMap", costTableMap);
				 }
				 
				 if(m.getPromotionLineList() != null && m.getPromotionLineList().size()>0){
					 Map<String, RequestPromotionLine> costTableMap = new HashMap<String, RequestPromotionLine>();
					 for(int i=0;i<m.getPromotionLineList().size();i++){
						 RequestPromotionLine c = m.getPromotionLineList().get(i);
						 costTableMap.put(c.getLineNo()+"", c);
					 }
					 
					 request.getSession().setAttribute("lineTableMap", costTableMap);
				 }
				 
			}
			f.setRequestPromotion(m);
			
			
			// save token
			saveToken(request);
			
			/** 
			* Process run after this action 
			* get sql manual script from 'c_after_action_sql' 
			* and run script by action name 
			**/
			ProcessAfterAction.processAfterAction(ProcessAfterAction.SAVE_REQ_PROMOTION,m.getRequestNo());

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "preview";
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		RequestPromotionForm f = (RequestPromotionForm) form;
		try {
			//Clear Parameter 
			 f.getRequestPromotion().setRequestDateFrom(null);
			 f.getRequestPromotion().setRequestDateTo(null);
			
			 //Clear Criteria
			 request.getSession().setAttribute("criteria_",null);
			 request.getSession().setAttribute("costTableMap",null);
			 request.getSession().setAttribute("lineTableMap",null);
			 
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
