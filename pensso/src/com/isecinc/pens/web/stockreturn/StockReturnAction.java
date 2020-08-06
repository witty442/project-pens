package com.isecinc.pens.web.stockreturn;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.RequestPromotion;
import com.isecinc.pens.bean.RequestPromotionCost;
import com.isecinc.pens.bean.RequestPromotionLine;
import com.isecinc.pens.bean.StockReturn;
import com.isecinc.pens.bean.StockReturnLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MRequestPromotion;
import com.isecinc.pens.model.MStockReturn;
import com.isecinc.pens.web.externalprocess.ProcessAfterAction;
import com.isecinc.pens.web.reqPromotion.RequestPromotionForm;
import com.pens.util.DBCPConnectionProvider;

/**
 * Stock Return Action
 * 
 * @author WITTY
 * 
 */
public class StockReturnAction extends I_Action {

	private int MAX_ROW_PAGE = 50;
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		StockReturnForm stockForm = (StockReturnForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
			    request.getSession().removeAttribute("_CRITERIA");
				stockForm.setResults(null);
				stockForm.setBean(new StockReturn());
				
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 if(request.getSession().getAttribute("_CRITERIA") != null){
				     stockForm.getCriteria().setBean((StockReturn)request.getSession().getAttribute("_CRITERIA"));
				 }else{
					 stockForm.getCriteria().setBean(new StockReturn());
				 }
				 search(stockForm,request,response); 
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
	
	public ActionForward stockReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		StockReturnForm stockForm = (StockReturnForm) form;
		StringBuffer html = null;
		boolean excel = false;
		// User user = (User) request.getSession(true).getAttribute("user");
		try {
			 logger.debug("stockReport :"+request.getParameter("action"));

			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parameter   
				 request.getSession().setAttribute("RESULTS",null);
				
				 StockReturn s = new StockReturn();
				 s.setHaveStock("true");
				 stockForm.setBean(s);

			 }else if("exportToExcel".equalsIgnoreCase(request.getParameter("action"))){
				 excel = true;
				 html = StockReturnReport.genStockReportToHTML(request, stockForm,excel);
				 request.getSession().setAttribute("RESULTS",html);
				 
			     if(html ==null){
				    request.setAttribute("Message","ไม่พบข้อมูล");
			     }else{
			        //Export To Excel
				    java.io.OutputStream out = response.getOutputStream();
					response.setHeader("Content-Disposition", "attachment; filename=data.xls");
					response.setContentType("application/vnd.ms-excel");
					
					Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
					w.write(html.toString());
				    w.flush();
				    w.close();
	
				    out.flush();
				    out.close();
			     }
			 }else {
				//search Report 
				 html = StockReturnReport.genStockReportToHTML(request, stockForm,excel);
				 request.getSession().setAttribute("RESULTS",html);
				 if(html ==null){
					 request.setAttribute("Message","ไม่พบข้อมูล");
				 }
			 }
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return mapping.findForward("stockReport");
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StockReturnForm stockForm = (StockReturnForm) form;
		try {
			logger.debug("prepare 2:"+request.getParameter("action"));
			
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
		logger.debug("searchStockReturn");
		StockReturnForm stockForm = (StockReturnForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
        int currPage = 1;
        int totalRow = 0;
        int totalPage = 0;
        int start = 0;
        int end = 50;
        String whereCause = "";
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//** get From Session **/
			if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")
				|| Utils.isNull(request.getParameter("action")).equalsIgnoreCase("back")){
				currPage = 1;
				totalRow = 0;
				
				/** Get TotalRow **/
			    totalRow = new MStockReturn().getTotalRowStockReturn(conn, stockForm.getBean(), user);
			    if(totalRow > 0){
			    	double t = new Double(totalRow)/new Double(MAX_ROW_PAGE);
			    	logger.debug("t:"+t);
			    	BigDecimal totalPageB = new BigDecimal(t);
			    	totalPageB = totalPageB.setScale(0,BigDecimal.ROUND_UP);
			    	
			    	logger.debug("totalPageB:"+totalPageB);
			    	
				    totalPage = totalPageB.intValue();
			    }
			}else{
		       currPage = stockForm.getCurPage();
			   totalRow = stockForm.getTotalRow();
			   if(totalRow > 0){
			    	double t = new Double(totalRow)/new Double(MAX_ROW_PAGE);
			    	logger.debug("t:"+t);
			    	BigDecimal totalPageB = new BigDecimal(t);
			    	totalPageB = totalPageB.setScale(0,BigDecimal.ROUND_UP);
			    	
			    	logger.debug("totalPageB:"+totalPageB);
			    	
				    totalPage = totalPageB.intValue();
			    }
			}
		    
			logger.debug("totalRow:"+totalRow);
			logger.debug("totalPage:"+totalPage);
			logger.debug("currPage:"+currPage);
			
			start = (currPage-1)*MAX_ROW_PAGE;
			end =  MAX_ROW_PAGE;
			whereCause +="\n limit "+start+","+end;
			
			List<StockReturn> results= new MStockReturn().searchStockReturnList(conn,stockForm.getBean(),user,whereCause,start);//new method optimize
			stockForm.setResults(results);
			stockForm.setTotalPage(totalPage);
			stockForm.setTotalRow(totalRow);
			stockForm.setCurPage(currPage);
			
			request.getSession().setAttribute("criteria_stock",stockForm.getBean());
						
			if (results != null) {
				stockForm.getCriteria().setSearchResult(results.size());
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return "search";
	}

	public ActionForward createNewStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("createNewStock");
		StockReturnForm stockForm = (StockReturnForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//Clear session item in page
			request.getSession().setAttribute("ITEM_IN_PAGE", null);
			
			StockReturn bean  = new StockReturn(); 
			// get back_date for get data ,and save for check case edit (insert database only)
		    String backDate = null;
			List<References> backDateInvoiceStockReturnList = InitialReferences.getReferenceListByCode(conn,InitialReferences.BACKDATE_INVOICE_STOCKRETURN);
			if(backDateInvoiceStockReturnList != null ){
			   References refbackDate =  backDateInvoiceStockReturnList.get(0);
			   Calendar curdate = Calendar.getInstance();
			   curdate.add(Calendar.MONTH, -1*Integer.parseInt(refbackDate.getKey()));
			   
			   logger.debug("DateStart:"+curdate.getTime());
			   backDate = Utils.stringValue(curdate.getTime(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			   //set to 01/mm/yyyy
			   backDate = "01/"+backDate.substring(3,backDate.length());
			   logger.debug("backDate:"+backDate);
			}
			bean.setBackDate(backDate);
			
			//init reasonList
			List<References> reasonReturnList = InitialReferences.getReferenceListByCode(conn,InitialReferences.REASON_RETURN);
			request.getSession().setAttribute("reasonReturnList", reasonReturnList);
			
			bean.setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			bean.setCanEdit(true);
		
			//set Btn Display
			bean.setShowSaveBtn(true);
			bean.setShowCancelBtn(false);
			 
			//for test
			//bean.setCustomerCode("26202001");
			//bean.setCustomerId(1299);
			//bean.setCustomerName("ลานหอยซุปเปอร์");
			 
			stockForm.setBean(bean);
			 
			// Save Criteria
			request.getSession().setAttribute("criteria_",stockForm.getBean());
				 
			// save token
			saveToken(request);
				
			//Set cri session for Back page
			request.getSession().setAttribute("criteria_stock",bean);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
		return mapping.findForward("detail");
	}
	
	
	public ActionForward viewStockReturn(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("viewStockReturn");
		StockReturnForm stockForm = (StockReturnForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//save search page criteria 
			request.getSession().setAttribute("_CRITERIA",stockForm.getBean());
			
			//Clear session check dup item in page
			 request.getSession().setAttribute("ITEM_IN_PAGE",null);
			 
			//init reasonList
			List<References> reasonReturnList = InitialReferences.getReferenceListByCode(conn,InitialReferences.REASON_RETURN);
			request.getSession().setAttribute("reasonReturnList", reasonReturnList);
				
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 //init Parameter 
			 stockForm.getBean().setRequestNumber(requestNumber);
			 StockReturn m = prepareUpdateStockReturn(user, stockForm);
			 
			 m = new MStockReturn().searchStockReturn(m,user);
             m.setShowPrintBtn(true);
			 stockForm.setBean(m);
			 //Set Lines
			 stockForm.setLines(m.getLineList());
			 
			//init itemCode in session for check sup
				if(m.getLineList() != null && m.getLineList().size() >0){
					 Map<String,String> map = new HashMap<String,String>();
					 for(int i=0;i<m.getLineList().size();i++){
						 StockReturnLine item = m.getLineList().get(i);
						 map.put(item.getProductCode()+"_"+item.getRequestNumber(), item.getProductCode()+"_"+item.getRequestNumber());
					 }
			    	 request.getSession().setAttribute("ITEM_IN_PAGE",map);
				}
				
			 //set Btn Display
			 if("Y".equalsIgnoreCase(m.getExported())){ 
			    stockForm.getBean().setShowSaveBtn(false);
			    stockForm.getBean().setShowCancelBtn(false);
			 }else{
				 stockForm.getBean().setShowSaveBtn(true);
				 stockForm.getBean().setShowCancelBtn(true);
			 }
			 
			// save token
			saveToken(request);
			
			//Set cri session for Back page
			request.getSession().setAttribute("criteria_stock",m);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("detail");
	}
	
	
	public ActionForward cancelStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelStock");
		StockReturnForm stockForm = (StockReturnForm) form;
		User user = (User) request.getSession().getAttribute("user");
		MStockReturn mDAO = new MStockReturn();
		try {
			String backPage = Utils.isNull(request.getParameter("backPage"));
			request.getSession().setAttribute("backPage", backPage);
			
			StockReturn m = prepareCancelStockReturn(user, stockForm);
			mDAO.updateCancelStockReturn(m);
			 
			 /** Search **/
			 m = mDAO.searchStockReturn(stockForm.getBean(),user);
			 stockForm.setBean(m);
			 //Set Lines
			 stockForm.setLines(m.getLineList());
			 
			 request.setAttribute("Message","ยกเลิกรายการสำเร็จ");
			 
			 //set Btn Display
			 stockForm.getBean().setShowSaveBtn(false);
			 stockForm.getBean().setShowCancelBtn(false);
			 
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
		StockReturnForm stockForm = (StockReturnForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		MStockReturn mDAO = new MStockReturn();
		try {
			conn = DBConnection.getInstance().getConnection();
			
			logger.debug("save-->");
			String backPage = Utils.isNull(request.getParameter("backPage"));
			logger.debug("backPage:"+backPage);
			request.getSession().setAttribute("backPage", backPage);
			
			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				stockForm.getLines().clear();
				return "new";
			}
			
			StockReturn m = stockForm.getBean();
			m.setUserId(user.getId()+"");
			m.setCreatedBy(user.getUserName());
			m.setUpdateBy(user.getUserName());
			 
			List<StockReturnLine> itemList = new ArrayList<StockReturnLine>();
			List<String> itemDeleteList = new ArrayList<String>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productCode = request.getParameterValues("productCode");
			String[] inventoryItemId = request.getParameterValues("inventoryItemId");
			String[] status = request.getParameterValues("status");
			String[] arInvoiceNo = request.getParameterValues("arInvoiceNo");
			//user insert
			String[] priQty = request.getParameterValues("priQty");
			String[] uom1Qty = request.getParameterValues("uom1Qty");
			String[] uom2Qty = request.getParameterValues("uom2Qty");
			//calc from order
			/*String[] remainPriAllQty = request.getParameterValues("remainPriAllQty");
			String[] remainPriQty = request.getParameterValues("remainPriQty");
			String[] remainSubQty = request.getParameterValues("remainSubQty");*/
			
			String[] uom2 = request.getParameterValues("uom2");
			String[] uom1Pac = request.getParameterValues("uom1Pac");
			String[] uom2Pac = request.getParameterValues("uom2Pac");
			
			String[] uom1Price = request.getParameterValues("uom1Price");
			String[] discount = request.getParameterValues("discount");
			String[] totalAmount = request.getParameterValues("totalAmount");
			
			String[] uom1ConvRate = request.getParameterValues("uom1ConvRate");
			String[] uom2ConvRate = request.getParameterValues("uom2ConvRate");
			
			String[] reason = request.getParameterValues("reason");
			String[] expireDate = request.getParameterValues("expireDate");
			
			logger.debug("itemCode:"+productCode.length);
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")){
						StockReturnLine l = new StockReturnLine();
						 logger.debug("lineId:"+lineId[i]);
						 
						 l.setLineId(Utils.convertStrToInt(lineId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setInventoryItemId(Utils.isNull(inventoryItemId[i]));
						 l.setArInvoiceNo(arInvoiceNo[i]);
						 l.setPriQty(Utils.isNull(priQty[i]));//ctn
						 l.setUom1Qty(Utils.isNull(uom1Qty[i]));//ctn
						 l.setUom2Qty(Utils.isNull(uom2Qty[i]));//sub
						 
						 //calc from order
						/* l.setRemainPriAllQty(Utils.isNull(remainPriAllQty[i]));//ctn
						 l.setRemainPriQty(Utils.isNull(remainPriQty[i]));//ctn
						 l.setRemainSubQty(Utils.isNull(remainSubQty[i]));//sub
*/						 
						 //set uomconvRate
						 l.setUom1ConvRate(Utils.isNull(uom1ConvRate[i]));//uom1ConvRate
						 l.setUom2ConvRate(Utils.isNull(uom2ConvRate[i]));//uom2ConvRate
						 
						 l.setUom2(uom2[i]);
						 l.setUom1Pac(uom1Pac[i]);
						 l.setUom2Pac(uom2Pac[i]);
						 l.setUom1Price(uom1Price[i]);//ctn /price
						 l.setDiscount(discount[i]);
						 l.setTotalAmount(totalAmount[i]);
						 l.setReason(Utils.isNull(reason[i]));
						 l.setExpireDate(Utils.isNull(expireDate[i]));
						 l.setCreatedBy(user.getUserName());
						 l.setUpdateBy(user.getUserName());
						 itemList.add(l);
					}else if( !Utils.isNull(productCode[i]).equals("") 
						    && Utils.isNull(status[i]).equals("DELETE") 
						    && Utils.convertStrToInt(lineId[i]) != 0){
						 itemDeleteList.add(lineId[i]);
					}//if
				}//for
			}//if
			//set items list
			m.setLineList(itemList);
			m.setLineNoDeleteList(itemDeleteList);
	
			/** save to DB **/
			m = mDAO.save(user,m);
			
			logger.debug("requestNumber:"+m.getRequestNumber());
			
			if("".equals(m.getRequestNumber())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				
				//ReCalc Update total in Head 
				mDAO.reCalcAmountInHead(conn, m);
				
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}
			
			m = mDAO.searchStockReturn(m,user);
			m.setShowPrintBtn(true);
			stockForm.setBean(m);
			stockForm.setLines(m.getLineList());
			
			request.setAttribute("Message",msg );
			
			 //set Btn Display
			if("Y".equalsIgnoreCase(m.getExported())){
			  stockForm.getBean().setShowSaveBtn(false);
			  stockForm.getBean().setShowCancelBtn(false);
			}else{
			  stockForm.getBean().setShowSaveBtn(true);
			  stockForm.getBean().setShowCancelBtn(true);
			}
			
			/** 
			 * Process run after this action 
			 * get sql manual script from 'c_after_action_sql' 
			 * and run script by action name 
			 **/
			ProcessAfterAction.processAfterAction(ProcessAfterAction.SAVE_STOCK_RETURN,stockForm.getBean().getRequestNumber());
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "detail";
		} finally {
			try {
				if(conn !=null){
					conn.close();conn=null; 
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}

   
    private StockReturn prepareUpdateStockReturn(User user,StockReturnForm mForm) throws Exception {
    	StockReturn m = mForm.getBean();
    	//logger.debug("moveOrder:"+mForm.getBean().getSalesCode()+","+mForm.getBean().getPdCode()+",");
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
    
    private StockReturn prepareCancelStockReturn(User user,StockReturnForm mForm) throws Exception {
    	StockReturn m = mForm.getBean();
    	//logger.debug("moveOrder:"+mForm.getBean().getSalesCode()+","+mForm.getBean().getPdCode()+",");
    	m.setStatus(MStockReturn.STATUS_VOID);//Cancel MoveOrder
    	m.setUserId(user.getId()+"");
    	m.setUpdateBy(user.getUserName());
		return m;
	}
    
    public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printReport");
		StockReturnForm f = (StockReturnForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		StockReturn p  =null;
		String fileNameExport  = "StockReturn.pdf";
		try { 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
	
			String fileName = "stock_return_mainreport";
            String fileJasper = BeanParameter.getReportPath() + fileName;
          
            //init connection 
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//head
			String logopath =   context.getRealPath("/images/pens_logo_fit.jpg");//
			logger.debug("requestNumber:"+Utils.isNull(request.getParameter("requestNumber")));
			logger.debug("reportType:"+Utils.isNull(request.getParameter("reportType")));
			
			//get parameter
			f.getBean().setRequestNumber(Utils.isNull(request.getParameter("requestNumber")));
        	f.getBean().setUserId(user.getId()+"");
        	  
            //get head detail
            p = new MStockReturn().searchStockReturn(f.getBean(), user);
			
            logger.debug("request_number: "+p.getRequestNumber());
            //detail
			List<StockReturnLine> mResultList = new MStockReturn().searchStockReturnLine(conn,user, f.getBean());
			//fix row detail =12
			if(mResultList != null && mResultList.size() >0){
				int diff = 12-mResultList.size();
				int no = mResultList.size();
			    for(int i=0;i<diff;i++){
			    	StockReturnLine line = new StockReturnLine();
			    	no++;
			    	line.setNo(no); 
			    	mResultList.add(line);
				}//for
			}//if
			
			//subReport1
			HashMap<String,Object> subParameterMap1 = new HashMap<String,Object>();
			subParameterMap1.put("reportName","ใบอนุมัติให้คืนคลัง PENS รอทำลาย");
			subParameterMap1.put("reportName2","(ต้นฉบับ)");  	
			subParameterMap1.put("reportName3","(สำหรับบัญชีลูกหนี้)");  	
			subParameterMap1.put("pens_logo_fit",logopath);
			subParameterMap1.put("request_date",p.getRequestDate());
			subParameterMap1.put("request_number",p.getRequestNumber());
			subParameterMap1.put("customer_code",p.getCustomerCode());
			subParameterMap1.put("customer_name",p.getCustomerName());
			subParameterMap1.put("description",p.getDescription());
			subParameterMap1.put("totalAllAmount",p.getTotalAllAmount());
			subParameterMap1.put("totalAllVatAmount",p.getTotalAllVatAmount());
			subParameterMap1.put("totalAllNonVatAmount",p.getTotalAllNonVatAmount());
			subParameterMap1.put("sales_code",user.getCode());
			subParameterMap1.put("sales_name",user.getName());
			subParameterMap1.put("printDate",Utils.isNull(p.getPrintDate()));
			subParameterMap1.put("userPrint",user.getName());
			
			Address address = new MAddress().findAddressByCustomerId(conn, p.getCustomerId()+"","B");
			if(address != null){
				//subParameterMap1.put("address",address.getLine1()+" "+address.getLine2());
				//subParameterMap1.put("address2",address.getLine3()+" "+address.getProvince().getName()+" "+address.getPostalCode());
				subParameterMap1.put("address",address.getLine1()+" "+address.getLine2()+" "+address.getLine3()+" "+address.getProvince().getName()+" "+address.getPostalCode());
			}
			subParameterMap1.put("parameter_subreport1",subParameterMap1);
			
			//subRepport2
			HashMap<String,Object> subParameterMap2 = new HashMap<String,Object>();
			subParameterMap2.put("reportName","ใบอนุมัติให้คืนคลัง PENS รอทำลาย");
			subParameterMap2.put("reportName2","(สำเนา)");  
			subParameterMap2.put("reportName3","(สำหรับคลังสินค้า)");  
			subParameterMap2.put("pens_logo_fit",logopath);
			subParameterMap2.put("request_date",p.getRequestDate());
			subParameterMap2.put("request_number",p.getRequestNumber());
			subParameterMap2.put("customer_code",p.getCustomerCode());
			subParameterMap2.put("customer_name",p.getCustomerName());
			subParameterMap2.put("description",p.getDescription());
			subParameterMap2.put("totalAllAmount",p.getTotalAllAmount());
			subParameterMap2.put("totalAllVatAmount",p.getTotalAllVatAmount());
			subParameterMap2.put("totalAllNonVatAmount",p.getTotalAllNonVatAmount());
			subParameterMap2.put("sales_code",user.getCode());
			subParameterMap2.put("sales_name",user.getName());
			subParameterMap2.put("printDate",Utils.isNull(p.getPrintDate()));
			subParameterMap2.put("userPrint",user.getName());
			if(address != null){
				subParameterMap2.put("address",address.getLine1()+" "+address.getLine2()+" "+address.getLine3()+" "+address.getProvince().getName()+" "+address.getPostalCode());
			}
			subParameterMap2.put("parameter_subreport2",subParameterMap2);
			
			//set main parameter
            parameterMap.put("found_data_subreport","found");
            parameterMap.put("subDataList", mResultList);
            parameterMap.put("SUBREPORT_DIR",BeanParameter.getReportPath());
            parameterMap.put("parameter_subreport1",subParameterMap1);
            parameterMap.put("parameter_subreport2",subParameterMap2);
            
			if(mResultList != null && mResultList.size()>0){
				//set for display report
				List<StockReturnLine> showList = new ArrayList<StockReturnLine>();
				showList.add(new StockReturnLine());
				
				reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName,showList ,fileNameExport);
			    
			}else{
				request.setAttribute("Message","Data not found");
			}
			// save token
			//saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return null;//mapping.findForward("printPopup");
	}
    
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StockReturnForm stockForm = (StockReturnForm) form;
		try {
			//Clear Parameter 
			 stockForm.getBean().setRequestDateFrom(null);
			 stockForm.getBean().setRequestDateTo(null);
			 stockForm.setResults(null);
			 stockForm.setLines(null);
			 
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
