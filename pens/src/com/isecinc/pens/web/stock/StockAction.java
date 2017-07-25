package com.isecinc.pens.web.stock;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
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

import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.StockLine;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MStock;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockAction extends I_Action {

	private int MAX_ROW_PAGE = 50;
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		StockForm stockForm = (StockForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 if("searchStock".equalsIgnoreCase(request.getParameter("action"))){
				String customerId = Utils.isNull(request.getParameter("customer_id"));
				
				MStock mDAO = new MStock();
				Stock mCri = new Stock();
				mCri.setUserId(user.getId()+"");
				mCri.setCustomerId(Utils.convertStrToInt(customerId));
				
				List<Stock> stockList = mDAO.searchStockList(mCri,user);
				stockForm.setResults(stockList);
					
				//Set cri session for Back page
				request.getSession().setAttribute("criteria_",mCri);
				
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 Stock b = (Stock)request.getSession().getAttribute("criteria_");
				 stockForm.getCriteria().setBean(b);
				 
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
	
	public ActionForward prepareCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		StockForm stockForm = (StockForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parametor By moveOrderType  
				 stockForm.setCustomer(new Customer());
				 stockForm.setResultsCust(null);
				 stockForm.setLines(null);
				 
				 //Clear Criteria
				 request.getSession().setAttribute("criteria_",null);
				 
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 Customer b = (Customer)request.getSession().getAttribute("criteria_cust");
				 stockForm.getCriteria().setCustomer(b);
				 
				 searchCustomer(mapping,stockForm,request,response); 
			 }
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return mapping.findForward("prepareCustomer");
	}
	
	public ActionForward stockReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		StockForm stockForm = (StockForm) form;
		StringBuffer html = null;
		boolean excel = false;
		 User user = (User) request.getSession(true).getAttribute("user");
		try {
			 logger.debug("prepare :"+request.getParameter("action"));

			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 //Clear and init Parametor   
				 stockForm.setCustomer(new Customer());
				 request.getSession().setAttribute("RESULTS",null);
				 
			 }else if("exportToExcel".equalsIgnoreCase(request.getParameter("action"))){
				 excel = true;
				 html = StockReport.genStockReportToHTML(request, stockForm,excel);
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
				 html = StockReport.genStockReportToHTML(request, stockForm,excel);
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
		StockForm stockForm = (StockForm) form;
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

	public ActionForward searchCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Customer Search");
		StockForm stockForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
        int currPage = 1;
        int totalRow = 0;
        int totalPage = 0;
        int start = 0;
        int end = 50;
		try {
		
			String whereCause = "";
			if (stockForm.getCustomer().getTerritory() != null
					&& !stockForm.getCustomer().getTerritory().trim().equals("")) {
				whereCause += "\n AND m_customer.TERRITORY = '" + stockForm.getCustomer().getTerritory().trim() + "'";
			}
			if (stockForm.getCustomer().getCode() != null && !stockForm.getCustomer().getCode().trim().equals("")) {
				whereCause += "\n AND m_customer.CODE LIKE '%"
						+ stockForm.getCustomer().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (stockForm.getCustomer().getName() != null && !stockForm.getCustomer().getName().trim().equals("")) {
				whereCause += "\n AND m_customer.NAME LIKE '%"
						+ stockForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (stockForm.getCustomer().getIsActive() != null
					&& !stockForm.getCustomer().getIsActive().equals("")) {
				whereCause += "\n AND m_customer.ISACTIVE = '" + stockForm.getCustomer().getIsActive() + "'";
			}
			// WIT EDIT :04/08/2554 
			if(!User.ADMIN.equals(user.getType())){
			   whereCause += "\n AND m_customer.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
			   whereCause += "\n AND m_customer.USER_ID = " + user.getId();
			}
			
			if ( !"".equals(Utils.isNull(stockForm.getCustomer().getDistrict())) && !"0".equals(Utils.isNull(stockForm.getCustomer().getDistrict())) ){
				whereCause += " AND m_address.district_id = " + stockForm.getCustomer().getDistrict() + "";
			}
			
			if (stockForm.getCustomer().getSearchProvince() != 0) {
				whereCause += "\n AND m_customer.CUSTOMER_ID IN (select customer_id ";
				whereCause += "\n from m_address where province_id = " + stockForm.getCustomer().getSearchProvince()
						 + "\n)";
			}
			
			if( !Utils.isNull(stockForm.getBean().getHaveStock()).equals("")){
				whereCause += "\n AND EXISTS (SELECT customer_id from t_stock where t_stock.customer_id = m_customer.customer_id )";
			}
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//** get From Session **/
			if(Utils.isNull(request.getParameter("search")).equalsIgnoreCase("new")){
				currPage = 1;
				totalRow = 0;
			}else{
		      currPage = stockForm.getCurPage();
			  totalRow = stockForm.getTotalRow();
			}
			/** Get TotalRow **/
		    totalRow = new MCustomer().getTotalRowCustomer(conn, whereCause, user);
		    if(totalRow > 0){
		    	double t = new Double(totalRow)/new Double(MAX_ROW_PAGE);
		    	logger.debug("t:"+t);
		    	BigDecimal totalPageB = new BigDecimal(t);
		    	totalPageB = totalPageB.setScale(0,BigDecimal.ROUND_UP);
		    	
		    	logger.debug("totalPageB:"+totalPageB);
		    	
			    totalPage = totalPageB.intValue();
		    }
		    
			logger.debug("totalRow:"+totalRow);
			logger.debug("totalPage:"+totalPage);
			logger.debug("currPage:"+currPage);
			
			start = (currPage-1)*MAX_ROW_PAGE;
			end =  MAX_ROW_PAGE;
			whereCause +="\n limit "+start+","+end;
			
			Customer[] results = new MCustomer().searchOptForStockCustomer(conn,whereCause,user,start);//new method optimize
			stockForm.setResultsCust(results);
			stockForm.setTotalPage(totalPage);
			stockForm.setTotalRow(totalRow);
			stockForm.setCurPage(currPage);
			
			Customer customer = stockForm.getCustomer();
			logger.debug("customer getDistrict:"+customer.getDistrict());
			
			//customer.setDistrict(district)
			// Save Criteria customer
			request.getSession().setAttribute("criteria_cust",customer);
						
			if (results != null) {
				stockForm.getCriteria().setSearchResult(results.length);
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

		return mapping.findForward("prepareCustomer");
	}
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockForm mForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MStock mDAO = new MStock();
			Stock m = mForm.getCriteria().getBean();
			List<Stock> stockList = mDAO.searchStockList(m,user);
			mForm.setResults(stockList);
			mForm.setBean(m);
			
			if(stockList != null && stockList.size()==0){
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

	public ActionForward createNewStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("createNewStock");
		StockForm stockForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 //Clear session item in page
			request.getSession().setAttribute("ITEM_IN_PAGE", null);
			
			 Stock bean  = new Stock(); 
			 //Get Detail Customer by customerId
			 Customer customer = new MCustomer().find(Utils.isNull(request.getParameter("customer_id")));
			 bean.setCustomerId(customer.getId());
			 bean.setCustomerName(customer.getName()+" "+customer.getName2());
			 
			 bean.setRequestDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 bean.setCanEdit(true);
			//init priceListId by User Type
			 bean.setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 bean.setShowSaveBtn(true);
			 bean.setShowCancelBtn(false);
			 
			 stockForm.setBean(bean);
			 
			// Save Criteria
			request.getSession().setAttribute("criteria_",stockForm.getBean());
				 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("new");
	}
	
	
	public ActionForward editStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("editStock");
		StockForm stockForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Clear session check dup item in page
			 request.getSession().setAttribute("ITEM_IN_PAGE",null);
			 
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 //init Parametor 
			 stockForm.getBean().setRequestNumber(requestNumber);
			 Stock m = prepareUpdateStock(user, stockForm);
			 
			 m = new MStock().searchStock(m,user);

			//Get Detail Customer by customerId
			 Customer customer = new MCustomer().find(m.getCustomerId()+"");
			 m.setCustomerId(customer.getId());
			 m.setCustomerName(customer.getName()+" "+customer.getName2());
			 
			 stockForm.setBean(m);
			 //Set Lines
			 stockForm.setLines(m.getLineList());
			//init priceListId by User Type
			 stockForm.getBean().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			//init itemCode in session for check sup
				if(m.getLineList() != null && m.getLineList().size() >0){
					 Map<String,String> map = new HashMap<String,String>();
					 for(int i=0;i<m.getLineList().size();i++){
						 StockLine item = m.getLineList().get(i);
						 map.put(item.getProductCode(), item.getProductCode());
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
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward viewStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("viewStock");
		StockForm stockForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
			 //init Parametor 
			 stockForm.getBean().setRequestNumber(requestNumber);
			 Stock m = prepareUpdateStock(user, stockForm);
			 
			 m = new MStock().searchStock(m,user);

			//Get Detail Customer by customerId
			 Customer customer = new MCustomer().find(m.getCustomerId()+"");
			 m.setCustomerId(customer.getId());
			 m.setCustomerName(customer.getName()+" "+customer.getName2());
			 
			 stockForm.setBean(m);
			 //Set Lines
			 stockForm.setLines(m.getLineList());
			//init priceListId by User Type
			 stockForm.getBean().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
			 //set Btn Display
			 stockForm.getBean().setShowSaveBtn(false);
			 stockForm.getBean().setShowCancelBtn(false);
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward cancelStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelStock");
		StockForm stockForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		MStock mDAO = new MStock();
		try {
			String backPage = Utils.isNull(request.getParameter("backPage"));
			request.getSession().setAttribute("backPage", backPage);
			
			 Stock m = prepareCancelStock(user, stockForm);
			 mDAO.updateCancelStock(m);
			 
			 /** Search **/
			 m = mDAO.searchStock(stockForm.getBean(),user);
			 stockForm.setBean(m);
			 //Set Lines
			 stockForm.setLines(m.getLineList());
			//init priceListId by User Type
			 stockForm.getBean().setPriceListId((new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId())+"");
			 
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
		StockForm stockForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		String uomId= "";
		try {
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
			
			Stock m = stockForm.getBean();
			m.setUserId(user.getId()+"");
			m.setCreatedBy(user.getUserName());
			m.setUpdateBy(user.getUserName());
			 
			List<StockLine> itemList = new ArrayList<StockLine>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productCode = request.getParameterValues("productCode");
			String[] inventoryItemId = request.getParameterValues("inventoryItemId");
			String[] status = request.getParameterValues("status");
			String[] fullUom = request.getParameterValues("fullUom");
			
			String[] qty = request.getParameterValues("qty");
			String[] qty2 = request.getParameterValues("qty2");
			String[] qty3 = request.getParameterValues("qty3");
			
			String[] sub = request.getParameterValues("sub");
			String[] sub2 = request.getParameterValues("sub2");
			String[] sub3 = request.getParameterValues("sub3");
			
			String[] expireDate = request.getParameterValues("expireDate");
			String[] expireDate2 = request.getParameterValues("expireDate2");
			String[] expireDate3 = request.getParameterValues("expireDate3");
			
			logger.debug("itemCode:"+productCode.length);
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")){
						StockLine l = new StockLine();
						 logger.debug("lineId:"+lineId[i]);
						 
						 l.setLineId(Utils.convertStrToInt(lineId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setInventoryItemId(Utils.isNull(inventoryItemId[i]));
						 l.setFullUom(fullUom[i]);
						 
						 l.setQty(qty[i]);
						 l.setSub(sub[i]);
						 l.setExpireDate(Utils.isNull(expireDate[i]));
						 
						 l.setQty2(qty2[i]);
						 l.setSub2(sub2[i]);
						 l.setExpireDate2(Utils.isNull(expireDate2[i]));
						 
						 l.setQty3(qty3[i]);
						 l.setSub3(sub3[i]);
						 l.setExpireDate3(Utils.isNull(expireDate3[i]));
					      
						 l.setCreatedBy(user.getUserName());
						 l.setUpdateBy(user.getUserName());
						 itemList.add(l);
					}//if
				}//for
			}//if
			//set items list
			m.setLineList(itemList);
			
			MStock mDAO = new MStock();
			
			m = mDAO.save(user,m);
			
			logger.debug("requestNumber:"+m.getRequestNumber());
			
			if("".equals(m.getRequestNumber())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}
			
			m = mDAO.searchStock(m,user);
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
			// save token
			saveToken(request);
		} catch (Exception e) {
			e.printStackTrace();
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "preview";
	}

   
    private Stock prepareUpdateStock(User user,StockForm mForm) throws Exception {
    	Stock m = mForm.getBean();
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
    
    private Stock prepareCancelStock(User user,StockForm mForm) throws Exception {
    	Stock m = mForm.getBean();
    	//logger.debug("moveOrder:"+mForm.getBean().getSalesCode()+","+mForm.getBean().getPdCode()+",");
    	m.setStatus(MStock.STATUS_VOID);//Cancel MoveOrder
    	m.setUserId(user.getId()+"");
    	m.setUpdateBy(user.getUserName());
		return m;
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StockForm stockForm = (StockForm) form;
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
