package com.isecinc.pens.web.order;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.OrderAllDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.gendate.OrderDateUtils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class OrderAllAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String pos_fix_session ="";
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		OrderForm summaryForm = (OrderForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = "";
		try {
			 logger.debug("prepare");
			 
			 //Validate Page Order can access 1 user;
			// String userLastAccessPath = ManagePath.canAccessPath(user, request.getServletPath());
			 //logger.debug("userLastAccessPath:"+userLastAccessPath);
			// if( !userLastAccessPath.equals("")){
				// request.setAttribute("Message", "หน้า Order B'me to Wacoal มี  User ["+userLastAccessPath+"] กำลังใช้งานอยู่  ท่านแน่ใจที่จะทำรายการต่อไป หรือตรวจสอบกับ Userที่ใช้งานอยู่ ว่าออกจากหน้านี้หรือยัง");
			 //}
			 
			 pageName = Utils.isNull(request.getParameter("pageName"));
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))
					 && "OSHOPPING".equalsIgnoreCase(pageName) ){
				 
					 request.getSession().setAttribute("results"+pos_fix_session, null);
					 request.getSession().setAttribute("storeList"+pos_fix_session,null);
					 request.getSession().setAttribute("itemErrorMap"+pos_fix_session, null);
					 
					 request.getSession().removeAttribute("totalPage");
					 request.getSession().removeAttribute("totalPage"); 
					 
					 //init store type
					 STORE_TYPE_MAP = OrderAllDAO.initStoreTypeMap(Constants.STORE_TYPE_OSHOPPING_CODE);
					 
					 Order order = new Order();
					 //Old OrderDate set to next billDate
					 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 
					 //new set to Current Date
					 order.setOrderDate(DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 order.setStoreType(Constants.STORE_TYPE_OSHOPPING_CODE);
					 summaryForm.setOrder(order);
					 summaryForm.setPageName(pageName);
					 
					 ImportDAO importDAO = new ImportDAO();
					 conn = DBConnection.getInstance().getConnection();
					 
					 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"",Constants.STORE_TYPE_OSHOPPING_CODE);
					 request.getSession().setAttribute("storeTypeList",storeTypeList);
					
					 List<References> regionList = importDAO.getRegionList(conn);
					 request.getSession().setAttribute("regionList",regionList);
					 
					 List<References> billTypeList = importDAO.getBillTypeList();
					 request.getSession().setAttribute("billTypeList",billTypeList);
					 
					 request.getSession().setAttribute("canOrderMap", null);

			 }else if("new".equalsIgnoreCase(request.getParameter("action"))
						 && "7CATALOG".equalsIgnoreCase(pageName) ){
					 
					 request.getSession().setAttribute("results"+pos_fix_session, null);
					 request.getSession().setAttribute("storeList"+pos_fix_session,null);
					 request.getSession().setAttribute("itemErrorMap"+pos_fix_session, null);
					 
					 request.getSession().removeAttribute("totalPage");
					 request.getSession().removeAttribute("totalPage"); 
					 
					 //init store type
					 STORE_TYPE_MAP = OrderAllDAO.initStoreTypeMap(Constants.STORE_TYPE_PENSHOP_CODE);
					 
					 Order order = new Order();
					 //Old OrderDate set to next billDate
					 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 
					 //new set to Current Date
					 order.setOrderDate(DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 order.setStoreType(Constants.STORE_TYPE_PENSHOP_CODE);
					 summaryForm.setOrder(order);
					 summaryForm.setPageName(pageName);
					 
					 ImportDAO importDAO = new ImportDAO();
					 conn = DBConnection.getInstance().getConnection();
					 
					 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"",Constants.STORE_TYPE_PENSHOP_CODE);
					 request.getSession().setAttribute("storeTypeList",storeTypeList);
					
					 List<References> regionList = importDAO.getRegionList(conn);
					 request.getSession().setAttribute("regionList",regionList);
					 
					 List<References> billTypeList = importDAO.getBillTypeList();
					 request.getSession().setAttribute("billTypeList",billTypeList);
					 
					 request.getSession().setAttribute("canOrderMap", null);

			 }else if("new".equalsIgnoreCase(request.getParameter("action"))
					 && "TVDIRECT".equalsIgnoreCase(pageName) ){
				 
				 request.getSession().setAttribute("results"+pos_fix_session, null);
				 request.getSession().setAttribute("storeList"+pos_fix_session,null);
				 request.getSession().setAttribute("itemErrorMap"+pos_fix_session, null);
				 
				 request.getSession().removeAttribute("totalPage");
				 request.getSession().removeAttribute("totalPage"); 
				 
				 //init store type
				 STORE_TYPE_MAP = OrderAllDAO.initStoreTypeMap(Constants.STORE_TYPE_TVD_CODE);
				 
				 Order order = new Order();
				 //Old OrderDate set to next billDate
				 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 
				 //new set to Current Date
				 order.setOrderDate(DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 order.setStoreType(Constants.STORE_TYPE_TVD_CODE);
				 summaryForm.setOrder(order);
				 summaryForm.setPageName(pageName);
				 
				 ImportDAO importDAO = new ImportDAO();
				 conn = DBConnection.getInstance().getConnection();
				 
				 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"",Constants.STORE_TYPE_TVD_CODE);
				 request.getSession().setAttribute("storeTypeList",storeTypeList);
				
				 List<References> regionList = importDAO.getRegionList(conn);
				 request.getSession().setAttribute("regionList",regionList);
				 
				 List<References> billTypeList = importDAO.getBillTypeList();
				 request.getSession().setAttribute("billTypeList",billTypeList);
				 
				 request.getSession().setAttribute("canOrderMap", null);
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
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OrderForm summaryForm = (OrderForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		OrderAllDAO orderDAO = new OrderAllDAO();
		Connection conn = null;
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		List<StoreBean> storeList = null;
		String action = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			if("save".equalsIgnoreCase(action)){
				logger.debug("Search and Save");
				storeList = (List<StoreBean>)request.getSession().getAttribute("storeList"+pos_fix_session);
				String[] groupArr = request.getParameterValues("groupCode");
				String[] itemArr = request.getParameterValues("item");
				String[] onhandQtyArr = request.getParameterValues("onhandQty");
				String[] barcodeArr = request.getParameterValues("barcode");
				String[] wholePriceBFArr = request.getParameterValues("wholePriceBF");
				String[] retailPriceBFArr = request.getParameterValues("retailPriceBF");
				
				Date orderDate = DateUtil.parse(orderForm.getOrder().getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				  
                Map<String,OrderKeyBean> mapOrderNoByStoreMap = new HashMap<String, OrderKeyBean>();
				/** Get OrderNoInDB key by StoreCode   **/
                mapOrderNoByStoreMap = orderDAO.getOrderNoMap(conn, orderForm.getOrder().getStoreType(),orderDate);
                
				if(storeList != null && storeList.size()>0){ 
		        	for(int col=0;col<storeList.size();col++){
		               StoreBean store = (StoreBean)storeList.get(col);
		        		for(int row=0;row<groupArr.length;row++ ){
		        			//System.out.println("groupArr["+groupArr+"]");
		    				Order o = new Order();
		    				o.setOrderDate(orderForm.getOrder().getOrderDate());
		    				o.setGroupCode(Utils.isNull(groupArr[row]));
		    				o.setItem(Utils.isNull(itemArr[row]));
		    				o.setOnhandQty(Utils.isNull(onhandQtyArr[row]));
		    				o.setBarcode(Utils.isNull(barcodeArr[row]));
		    				o.setRetailPriceBF(Utils.isNull(retailPriceBFArr[row]));
		    				o.setWholePriceBF(Utils.isNull(wholePriceBFArr[row]));
		    				
		    				o.setExported("N");
		    				o.setStoreType(orderForm.getOrder().getStoreType());
		    				o.setStoreCode(store.getStoreCode());
		    				
		    				if("G".equalsIgnoreCase(orderForm.getOrder().getBillType())){
			    				o.setBillType(store.getBillType());
			    				o.setValidFrom("".equals(Utils.isNull(store.getValidFrom()))?"00000000":Utils.isNull(store.getValidFrom()));
				    			o.setValidTo("".equals(Utils.isNull(store.getValidTo()))?"00000000":Utils.isNull(store.getValidTo()));
			    			}else{
			    				o.setBillType("N");	
			    				o.setValidFrom("00000000");
				    			o.setValidTo("00000000");
			    			}
		    				
	                        String keyQty = "qty_"+col+"_"+row;
	                        String qty = Utils.isNull(request.getParameter(keyQty));
	                        //System.out.println(keyQty+"["+qty+"]");
	                        
	                        String keyOrderNo = "orderNo_"+col+"_"+row;
	                        String keyBarOnBox = "barOnBox_"+col+"_"+row;
	                        
	                        String orderNoLine = Utils.isNull(request.getParameter(keyOrderNo));
	                        String barOnBoxLine = Utils.isNull(request.getParameter(keyBarOnBox));
	                        //logger.debug("key:"+keyQty+"["+qty+"],"+keyOrderNo+"["+keyOrderNo+"]");
	                        
	                        o.setQty(qty);
	                        
	                        if("".equals(orderNoLine)){
	                        	//Insert Only
	                        	if( !"".equals(o.getQty()) && !"0".equals(o.getQty())){
		                        	String orderNo = "";
		                        	String barOnBox = "";
		                        	OrderKeyBean keyBean = null;
		                        	if(mapOrderNoByStoreMap.get(o.getStoreCode()) ==null){
		                        	    orderNo = OrderNoGenerate.genOrderNoKEY(orderDate, store.getStoreCode());
		                        	    keyBean= new OrderKeyBean(orderNo,barOnBox);
		                        	    
		                        	    mapOrderNoByStoreMap.put(o.getStoreCode(), keyBean);
		                        	}else{
		                        		keyBean = mapOrderNoByStoreMap.get(o.getStoreCode());
		                        		orderNo = Utils.isNull(keyBean.getOrderNo());
		                        	}
		                        	//Insert
		                        	logger.debug("Insert OrderNo:"+orderNo+"BarOnBox:"+barOnBox);
		                        	o.setCreateUser(user.getUserName());
		                        	o.setOrderNo(orderNo);
		                        	o.setBarOnBox(barOnBox);
		                    
		                        	OrderAllDAO.saveOrder(conn, o);
	                        	}
	                        }else{
	                        	//update 
	                        	logger.debug("Update OrderNo:"+orderNoLine+"BarOnBoxLine:"+barOnBoxLine);
	                        	if( "".equals(o.getQty())){
	                        	  o.setQty("0");	
	                        	}
	                        	o.setOrderNo(orderNoLine);
	                        	o.setBarOnBox(barOnBoxLine);
	                        	o.setUpdateUser(user.getUserName());
	                        	
	                        	if("0".equals(o.getQty())){
	                        	   OrderAllDAO.deleteOrder(conn, o);
	                        	}else{
	                        	   OrderAllDAO.updateOrder(conn, o);
	                        	}
		                        
	                        }//if
		    			}//for rows w
		        	}//for storeList 1
				}//if
	      
				logger.debug("Save and Search Success");
	
			}else{
			
				//Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				//boolean canEditOrNew = orderDAO.canEditOrNewOrder(conn, orderDate, orderForm.getOrder().getStoreType());
				logger.debug("request.getSession().totalPage["+request.getSession().getAttribute("totalPage")+"]");
				
				if(request.getSession().getAttribute("totalPage") ==null){
					totalRow = orderDAO.getTotalRowBMEItemByPageName(conn,orderForm.getOrder(),orderForm.getPageName());
					totalPage = (totalRow/ pageSize)+1;
					request.getSession().setAttribute("totalPage", totalPage);
					request.getSession().setAttribute("totalRow", totalRow);
				}
				
			}
			
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			if("newsearch".equals(action)){
				
				request.setAttribute("action", "newsearch");
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("storeList",null);
				
				storeList = orderDAO.getStoreList(conn,orderForm.getOrder().getStoreType(),orderForm.getOrder().getRegion(),orderForm.getOrder().getBillType(),"''");
				request.getSession().setAttribute("storeList",storeList);
				if(storeList==null || (storeList != null && storeList.size()==0)){
					request.setAttribute("Message", "ไม่พบข่อมูลสาขา");
				}
				
				//Case newsearch Recale page
				pageNumber = 1;
				totalRow = orderDAO.getTotalRowBMEItemByPageName(conn,orderForm.getOrder(),orderForm.getPageName());
				totalPage = (totalRow/ pageSize)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}
			
			//logger.debug("CustType["+orderForm.getOrder().getCustType()+"]");
			
            //** Search Data and Display **/
			List<Order> results = new OrderAllDAO().prepareNewOrderByPageName(orderForm.getPageName(),conn,orderForm.getOrder(),storeList, user,pageNumber,pageSize);
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return "search";
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int row = 0;
		int col = 0;
		boolean haveError = false;
		OrderAllDAO orderDAO = new OrderAllDAO();
		try {
			conn = DBConnection.getInstance().getConnection();
			//conn.setAutoCommit(false);
			List<StoreBean> storeList = (List<StoreBean>)request.getSession().getAttribute("storeList");
			
			String[] groupArr = request.getParameterValues("groupCode");
			String[] itemArr = request.getParameterValues("item");
			String[] onhandQtyArr = request.getParameterValues("onhandQty");
			String[] barcodeArr = request.getParameterValues("barcode");
			String[] wholePriceBFArr = request.getParameterValues("wholePriceBF");
			String[] retailPriceBFArr = request.getParameterValues("retailPriceBF");
			
			/** Validate Qty in Store not over Onhand QTY by Item **/
			Map<String,String> itemErrorMap = new HashMap<String, String>();
			/*for(row=0;row<groupArr.length;row++ ){
				BigDecimal onhandQty = new BigDecimal(onhandQtyArr[row]);
				String item = itemArr[row];
				 
				if(storeList != null && storeList.size()>0){ 
				   BigDecimal sumQtyInRow = new BigDecimal("0");
		        	for(col=0;col<storeList.size();col++){
		               //StoreBean store = (StoreBean)storeList.get(col);
	                   String keyQty = "qty_"+col+"_"+row;
	                   String qty = Utils.isNull(request.getParameter(keyQty));
	                   if( !"".equals(qty)){
	                     sumQtyInRow =  sumQtyInRow.add(new BigDecimal(qty));
	                   }
		        	}//for 2
		        	
		        	if(sumQtyInRow.longValue() > onhandQty.longValue() ){
		        		itemErrorMap.put(item, "lineError");
		        		haveError = true;
		        	}
				}
			}//for 1
*/			
			if(haveError == false){
				Date orderDate = DateUtil.parse(orderForm.getOrder().getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				Map<String,OrderKeyBean> mapOrderNoByStoreMap = new HashMap<String, OrderKeyBean>();
				/** Get OrderNoInDB key by StoreCode   **/
                mapOrderNoByStoreMap = orderDAO.getOrderNoMap(conn, orderForm.getOrder().getStoreType(),orderDate);
                
				if(storeList != null && storeList.size()>0){ 
		        	for(col=0;col<storeList.size();col++){
		               StoreBean store = (StoreBean)storeList.get(col);
		        		for(row=0;row<groupArr.length;row++ ){
		        			//System.out.println("groupArr["+groupArr+"]");
		    				Order o = new Order();
		    				o.setOrderDate(orderForm.getOrder().getOrderDate());
		    				o.setGroupCode(Utils.isNull(groupArr[row]));
		    				o.setItem(Utils.isNull(itemArr[row]));
		    				o.setOnhandQty(Utils.isNull(onhandQtyArr[row]));
		    				o.setBarcode(Utils.isNull(barcodeArr[row]));
		    				o.setRetailPriceBF(Utils.isNull(retailPriceBFArr[row]));
		    				o.setWholePriceBF(Utils.isNull(wholePriceBFArr[row]));
		    				
		    				o.setExported("N");
		    				o.setStoreType(orderForm.getOrder().getStoreType());
		    				o.setStoreCode(store.getStoreCode());
		    				
		    				if("G".equalsIgnoreCase(orderForm.getOrder().getBillType())){
		    					o.setBillType(store.getBillType());
			    				o.setValidFrom("".equals(Utils.isNull(store.getValidFrom()))?"00000000":Utils.isNull(store.getValidFrom()));
				    			o.setValidTo("".equals(Utils.isNull(store.getValidTo()))?"00000000":Utils.isNull(store.getValidTo()));
				    			
			    			}else{
			    				o.setBillType("N");	
			    				o.setValidFrom("00000000");
				    			o.setValidTo("00000000");
			    			}

	                        String keyQty = "qty_"+col+"_"+row;
	                        String qty = Utils.isNull(request.getParameter(keyQty));
	                        //System.out.println(keyQty+"["+qty+"]");
	                        
	                        String keyOrderNo = "orderNo_"+col+"_"+row;
	                        String keyBarOnBox = "barOnBox_"+col+"_"+row;
	                        
	                        String orderNoLine = Utils.isNull(request.getParameter(keyOrderNo));
	                        String barOnBoxLine = Utils.isNull(request.getParameter(keyBarOnBox));
	                        //System.out.println(keyQty+"["+qty+"],"+keyOrderNo+"["+keyOrderNo+"]");
	                        
	                        o.setQty(qty);

	                        if("".equals(orderNoLine)){
	                        	//Insert Only
	                        	if( !"".equals(o.getQty()) && !"0".equals(o.getQty())){
	                        		String orderNo = "";
		                        	String barOnBox = "";
		                        	OrderKeyBean keyBean = null;
		                        	if(mapOrderNoByStoreMap.get(o.getStoreCode()) ==null){
		                        	    orderNo = OrderNoGenerate.genOrderNoKEY(orderDate, store.getStoreCode());
		                        	    keyBean= new OrderKeyBean(orderNo,barOnBox);
		                        	    
		                        	    mapOrderNoByStoreMap.put(o.getStoreCode(), keyBean);
		                        	}else{
		                        		keyBean = mapOrderNoByStoreMap.get(o.getStoreCode());
		                        		orderNo = Utils.isNull(keyBean.getOrderNo());
		                        	}
		                        	//Insert
		                        	logger.debug("Insert OrderNo:"+orderNo+"BarOnBox:"+barOnBox);
		                        	o.setCreateUser(user.getUserName());
		                        	o.setOrderNo(orderNo);
		                        	o.setBarOnBox(barOnBox);
		                    
		                        	OrderAllDAO.saveOrder(conn, o);
	                        	}
	                        }else{
	                        	 //System.out.println(keyQty+"["+qty+"],"+keyOrderNo+"["+keyOrderNo+"]");
	                        	//update 
	                        	//System.out.println("Update OrderNo:"+orderNoLine+",barOnBoxLine["+barOnBoxLine+"]");
	                        	if( "".equals(o.getQty())){
	                        	  o.setQty("0");	
	                        	}
	                        	o.setOrderNo(orderNoLine);
	                        	o.setBarOnBox(barOnBoxLine);
	                        	o.setUpdateUser(user.getUserName());

	                        	if("0".equals(o.getQty())){
	                        	   OrderAllDAO.deleteOrder(conn, o);
	                        	}else{
	                        	   OrderAllDAO.updateOrder(conn, o);
	                        	}
	                        	
	                        }//if
		    			}//for rows
		        	}//for storeList
				}//for
	
				logger.debug("Connection commit");
				//conn.commit();
				
				request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
				 //Research 
			    //List<Order> results = new OrderAllDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user);
		        //request.getSession().setAttribute("results", results);
		       // request.getSession().setAttribute("itemErrorMap", null);
				
				request.setAttribute("action", "newsearch");
				search(orderForm, request, response);
			}else{
				request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้");
				 //Research 
			    List<Order> results = new OrderAllDAO().prepareNewOrderByPageName(orderForm.getPageName(),conn,orderForm.getOrder(),storeList, user);
		        request.getSession().setAttribute("results", results);
		        request.getSession().setAttribute("itemErrorMap", itemErrorMap);
			}
			
			 
		} catch (Exception e) {
			//conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			try {
				
			} catch (Exception e2) {}
			return "search";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export ToExcel");
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer data = new StringBuffer("");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			//LOTUS_OH_20130509.txt
			Date orderDate = DateUtil.parse(orderForm.getOrder().getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = DateUtil.stringValue(orderDate, DateUtil.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			String refCode = Utils.isNull(STORE_TYPE_MAP.get(orderForm.getOrder().getStoreType()));
			String fileName = refCode+"TS_"+dateFileName;
			
			OrderAllDAO orderDAO = new OrderAllDAO();
			data = orderDAO.genOrderToExcelByPageName(orderForm.getPageName(),conn, user, orderForm.getOrder().getStoreType(), orderForm.getOrder().getOrderDate());
			//data = genToExcel(request,orderForm,user);
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xls");
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(data.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportSummaryToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Summary ToExcel");
		try {
			exportSummaryToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportSummaryToExcelView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Summary ToExcel View");
		try {
			exportSummaryToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("search");
	}
	
	private void exportSummaryToExcel(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			Date orderDate = DateUtil.parse(orderForm.getOrder().getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = DateUtil.stringValue(orderDate, DateUtil.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			
			String refCode = Utils.isNull(STORE_TYPE_MAP.get(orderForm.getOrder().getStoreType()))+"_";
			String fileName = refCode+"SUM_"+dateFileName;
			
			OrderAllDAO orderDAO = new OrderAllDAO();
			//data = orderDAO.genSummaryOrderToExcel(conn, user, orderForm.getOrder().getStoreType(), orderDate);
			XSSFWorkbook xssfWorkbookDta = orderDAO.genSummaryOrderToCsv(conn, user, orderForm.getOrder().getStoreType(), orderDate,orderForm.getPageName());
			
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xlsx");
			response.setContentType("application/vnd.ms-excel; charset=windows-874");
			java.io.OutputStream out = response.getOutputStream();

			xssfWorkbookDta.write(out);

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
	public ActionForward exportDetailToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Detail ToExcel");
		try {
			exportDetailToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportDetailToExcelView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Detail ToExcel View");
		try {
			exportDetailToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("search");
	}
	
	private void exportDetailToExcel(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export ToExcel");
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			Date orderDate = DateUtil.parse(orderForm.getOrder().getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = DateUtil.stringValue(orderDate, DateUtil.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			String refCode = Utils.isNull(STORE_TYPE_MAP.get(orderForm.getOrder().getStoreType()))+"_";
			String fileName = refCode+"SUM_DETAIL_"+dateFileName;
			
			OrderAllDAO orderDAO = new OrderAllDAO();
			//data = orderDAO.genDetailOrderToExcel(conn, user, orderForm.getOrder().getStoreType(), orderDate);
			XSSFWorkbook xssfWorkbookDta = orderDAO.genDetailOrderToCsv(conn, user, orderForm.getOrder().getStoreType(), orderDate,orderForm.getPageName());
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xlsx");
			response.setContentType("application/vnd.ms-excel");
			
			xssfWorkbookDta.write(out);

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		OrderForm summaryForm = (OrderForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			 request.getSession().setAttribute("storeList",null);
			 request.getSession().setAttribute("itemErrorMap", null);
			 
			 Order order = new Order();
			 order.setOrderDate(DateUtil.stringValue(OrderDateUtils.getOrderDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 summaryForm.setOrder(order);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
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
