package com.isecinc.pens.web.order;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
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

import util.Constants;
import util.excel.ExcelHeader;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.LockItemOrderDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.StockLimitDAO;
import com.isecinc.pens.gendate.OrderDateUtils;
import com.isecinc.pens.inf.exception.LogisticException;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.managepath.ManagePath;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class OrderAction extends I_Action {

	public static int pageSize = 90;
	public static int reportOrderPageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public boolean chkStoreCreditLimit = false;//for chk creditLimti
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		OrderForm summaryForm = (OrderForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 logger.debug("prepare");
			 
			 //Validate Page Order can access 1 user;
			 String userLastAccessPath = ManagePath.canAccessPath(user, request.getServletPath());
			 logger.debug("userLastAccessPath:"+userLastAccessPath);
			 if( !userLastAccessPath.equals("")){
				// request.setAttribute("Message", "หน้า Order B'me to Wacoal มี  User ["+userLastAccessPath+"] กำลังใช้งานอยู่  ท่านแน่ใจที่จะทำรายการต่อไป หรือตรวจสอบกับ Userที่ใช้งานอยู่ ว่าออกจากหน้านี้หรือยัง");
			 }
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("storeList",null);
				 request.getSession().setAttribute("itemErrorMap", null);
				 
				 request.getSession().removeAttribute("totalPage");
				 request.getSession().removeAttribute("totalPage"); 
				 
				 //init store type
				 STORE_TYPE_MAP = new OrderDAO().initStoreTypeMap();
				 
				 Order order = new Order();
				 //Old OrderDate set to next billDate
				 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 
				 //new set to Current Date
				 order.setOrderDate(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 summaryForm.setOrder(order);
				 
				 ImportDAO importDAO = new ImportDAO();
				 conn = DBConnection.getInstance().getConnection();
				 
				 String notInCustCode = Constants.STORE_TYPE_FRIDAY_CODE+"," +Constants.STORE_TYPE_OSHOPPING_CODE+"," +Constants.STORE_TYPE_7CATALOG_CODE+"," +Constants.STORE_TYPE_TVD_CODE;
				 List<References> storeTypeList = importDAO.getStoreTypeList(conn,notInCustCode);
			     request.getSession().setAttribute("storeTypeList",storeTypeList);
			     
				 List<References> regionList = importDAO.getRegionList(conn);
				 request.getSession().setAttribute("regionList",regionList);
				 
				 List<References> billTypeList = importDAO.getBillTypeList();
				 request.getSession().setAttribute("billTypeList",billTypeList);
				 
				 request.getSession().setAttribute("canOrderMap", null);

			 }
			
		} catch (Exception e) {
			e.printStackTrace();
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
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		OrderDAO orderDAO = new OrderDAO();
		Connection conn = null;
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		int prevPageNumber = 0;
		List<StoreBean> storeList = null;
		String action = "";
		String tableName = "PENSBME_ONHAND_BME";
		String itemType ="LotusItem";
		Date orderDate = null;
		String barcodeInPage = "";
		String validateStore = "true";
		Map<String,String> canOrderMap = new HashMap<String,String>();
		try {
			
			Order orderCri = orderForm.getOrder();
			if(orderCri.getStoreType().equals(Constants.STORE_TYPE_FRIDAY_CODE)){
				tableName = "PENSBME_ONHAND_BME_FRIDAY";
				itemType ="FridayItem";
			}
			
			conn = DBConnection.getInstance().getConnection();
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
		
			if("save".equalsIgnoreCase(action)){
				logger.debug("Search and Save");
				storeList = (List<StoreBean>)request.getSession().getAttribute("storeList");
				String[] groupArr = request.getParameterValues("groupCode");
				String[] itemArr = request.getParameterValues("item");
				String[] onhandQtyArr = request.getParameterValues("onhandQty");
				String[] barcodeArr = request.getParameterValues("barcode");
				String[] wholePriceBFArr = request.getParameterValues("wholePriceBF");
				String[] retailPriceBFArr = request.getParameterValues("retailPriceBF");
				
				orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				  
				/** Get OrderNoInDB key by StoreCode   **/
				Map<String,OrderKeyBean> mapOrderNoByStoreMap = orderDAO.getOrderNoMap(conn, orderForm.getOrder().getStoreType(),orderDate);
	
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
		    				
		    				/*if("G".equalsIgnoreCase(orderForm.getOrder().getBillType())){
			    				o.setBillType(store.getBillType());
			    				o.setValidFrom("".equals(Utils.isNull(store.getValidFrom()))?"00000000":Utils.isNull(store.getValidFrom()));
				    			o.setValidTo("".equals(Utils.isNull(store.getValidTo()))?"00000000":Utils.isNull(store.getValidTo()));
				    			
		    				}else if("S".equalsIgnoreCase(orderForm.getOrder().getBillType())){
			    				o.setBillType(store.getBillType());
			    				o.setValidFrom("".equals(Utils.isNull(store.getValidFrom()))?"00000000":Utils.isNull(store.getValidFrom()));
				    			o.setValidTo("".equals(Utils.isNull(store.getValidTo()))?"00000000":Utils.isNull(store.getValidTo()));
			    			}else{*/
			    				o.setBillType("N");	
			    				o.setValidFrom("00000000");
				    			o.setValidTo("00000000");
			    			//}
		    				
				    		//Set For Delete Case Error
				    	   if(col==0){
				    		   barcodeInPage += "'"+barcodeArr[row]+"',";
				    	   }
				    			
	                        String keyQty = "qty_"+col+"_"+row;
	                        String qty = Utils.isNull(request.getParameter(keyQty));
	                        //System.out.println(keyQty+"["+qty+"]")3;
	                        
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
		                        	    orderNo = OrderNoGenerate.genOrderNoKEY(conn, orderDate, store.getStoreCode());
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
		                    
		                        	OrderDAO.saveOrder(conn, o);
	                        	}
	                        }else{
	                        	//update 
	                        	logger.debug("Update OrderNo:"+orderNoLine+",BarOnBoxLine:"+barOnBoxLine);
	                        	if( "".equals(o.getQty())){
	                        	  o.setQty("0");	
	                        	}
	                        	o.setOrderNo(orderNoLine);
	                        	o.setBarOnBox(barOnBoxLine);
	                        	o.setUpdateUser(user.getUserName());
	                        	
	                        	if("0".equals(o.getQty())){
	                        		logger.debug("delete");
	                        	   OrderDAO.deleteOrder(conn, o);
	                        	}else{
	                        		logger.debug("update");
	                        	   OrderDAO.updateOrder(conn, o);
	                        	}
		                        
	                        }//if
		    			}//for rows w
		        	}//for storeList 1
				}//if
	      
				logger.debug("Save and Search Success");
				
				// Validate Store Limit
				if(chkStoreCreditLimit==true){
					String msg ="";
					List<StoreBean> storeChkList = new ArrayList<StoreBean>();
					if(StockLimitDAO.isCustGroupChkCreditLimit(conn,orderCri.getStoreType())){
						if(storeList != null && storeList.size()>0){ 
				        	for(int col=0;col<storeList.size();col++){
				               StoreBean store = (StoreBean)storeList.get(col);
				               
				               if(StockLimitDAO.isStoreChkCreditLimit(conn, store.getStoreCode())){
					               if(StockLimitDAO.isStoreOverCreditLimit(conn, store.getStoreCode(), orderDate )){
					            	   store.setStoreStyle("storeError");
					            	   validateStore = "false";
					            	   msg += store.getStoreDisp()+",";
					               }else{
					            	   store.setStoreStyle("");
					               }
				               }
				               storeChkList.add(store);
				        	}
				        	
				        	if( !Utils.isNull(barcodeInPage).equals("")){
				        		barcodeInPage = barcodeInPage.substring(0,barcodeInPage.length()-1);
				        	}
				        	
				        	request.getSession().setAttribute("storeList",storeChkList);
				        	request.getSession().setAttribute("barcodeInPage",barcodeInPage);
				        	
				        	request.setAttribute("validateStore",validateStore);
				        	if( !Utils.isNull(msg).equals("")){
				        		msg = msg.substring(0,msg.length()-1);
				        		msg = "ไม่สามรถบันทึกข้อมูลได้ ["+msg +"]  เนื่องจากร้านค้า Over Credit Limit";
				        	    request.setAttribute("Message", msg);
				        	}
						}
					}
				 }
			}else{
			
				//Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				//boolean canEditOrNew = orderDAO.canEditOrNewOrder(conn, orderDate, orderForm.getOrder().getStoreType());
				logger.debug("request.getSession().totalPage["+request.getSession().getAttribute("totalPage")+"]");
				
				if(request.getSession().getAttribute("totalPage") ==null){
					totalRow = orderDAO.getTotalRowBMEItem(conn,orderForm.getOrder(),tableName);
					totalPage = (totalRow/ pageSize)+1;
					request.getSession().setAttribute("totalPage", totalPage);
					request.getSession().setAttribute("totalRow", totalRow);
				}
			}
			
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			if("newsearch".equals(action)){
				//Call runProcedureStoreLimit
				StockLimitDAO.runProcedureStoreLimit(conn, orderForm.getOrder().getStoreType(),orderForm.getOrder().getRegion());
				
				request.getSession().setAttribute("barcodeInPage","");
	        	
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
				totalRow = orderDAO.getTotalRowBMEItem(conn,orderForm.getOrder(),tableName);
				totalPage = (totalRow/ pageSize)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);
				
				
				//Load StoreCode can order Group Code
				canOrderMap = LockItemOrderDAO.getStoreCodeCanOrderGroupCode(conn);
				request.getSession().setAttribute("canOrderMap", canOrderMap);
				logger.debug("1 canOrderMap:"+canOrderMap);
			

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}
			
			//For case Error OverLimit goto PrevPage
			prevPageNumber = !Utils.isNull(request.getParameter("prevPageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("prevPageNumber")):1;
			if(validateStore.equals("false")){
				pageNumber = prevPageNumber;
			}
			request.setAttribute("prevPageNumber", prevPageNumber+"");
			
			logger.debug("pageNumber:"+pageNumber);
	
			//logger.debug("CustType["+orderForm.getOrder().getCustType()+"]");
			
            //** Search Data and Display **/
			List<Order> results = new OrderDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user,pageNumber,pageSize,tableName,itemType);
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
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
		OrderDAO orderDAO = new OrderDAO();
		String tableName = "PENSBME_ONHAND_BME";
		Date orderDate = null;
		String barcodeInPage = "";
		String validateStore = "true";
		String itemType ="LotusItem";
		try {
			Order orderCri = orderForm.getOrder();
			if(orderCri.getStoreType().equals(Constants.STORE_TYPE_FRIDAY_CODE)){
				tableName = "PENSBME_ONHAND_BME_FRIDAY";
				itemType ="FridayItem";
			}
			
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
			
			if(haveError == false){
				orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
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
		    				
		    				/*if("G".equalsIgnoreCase(orderForm.getOrder().getBillType())){
		    					o.setBillType(store.getBillType());
			    				o.setValidFrom("".equals(Utils.isNull(store.getValidFrom()))?"00000000":Utils.isNull(store.getValidFrom()));
				    			o.setValidTo("".equals(Utils.isNull(store.getValidTo()))?"00000000":Utils.isNull(store.getValidTo()));
		    				}else if("S".equalsIgnoreCase(orderForm.getOrder().getBillType())){
			    				o.setBillType(store.getBillType());
			    				o.setValidFrom("".equals(Utils.isNull(store.getValidFrom()))?"00000000":Utils.isNull(store.getValidFrom()));
				    			o.setValidTo("".equals(Utils.isNull(store.getValidTo()))?"00000000":Utils.isNull(store.getValidTo()));	
			    			}else{*/
			    				o.setBillType("N");	
			    				o.setValidFrom("00000000");
				    			o.setValidTo("00000000");
			    			//}
				    		//Set For Delete Case Error
						    if(col==0){
						       barcodeInPage += "'"+barcodeArr[row]+"',";
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
		                        	    orderNo = OrderNoGenerate.genOrderNoKEY(conn, orderDate, store.getStoreCode());
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
		                    
		                        	OrderDAO.saveOrder(conn, o);
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
	                        	   OrderDAO.deleteOrder(conn, o);
	                        	}else{
	                        	   OrderDAO.updateOrder(conn, o);
	                        	}
	                        	
	                        }//if
		    			}//for rows
		        	}//for storeList
				}//for
	
				// Validate Store Limit
				if(chkStoreCreditLimit==true){
					String msg ="";
					List<StoreBean> storeChkList = new ArrayList<StoreBean>();
					if(StockLimitDAO.isCustGroupChkCreditLimit(conn,orderForm.getOrder().getStoreType())){
						if(storeList != null && storeList.size()>0){ 
				        	for(col=0;col<storeList.size();col++){
				               StoreBean store = (StoreBean)storeList.get(col);
				               
				               if(StockLimitDAO.isStoreChkCreditLimit(conn, store.getStoreCode())){
					               if(StockLimitDAO.isStoreOverCreditLimit(conn, store.getStoreCode(), orderDate )){
					            	   store.setStoreStyle("storeError");
					            	   validateStore = "false";
					            	   msg += store.getStoreDisp()+",";
					               }else{
					            	   store.setStoreStyle("");
					               }
				               }
				               storeChkList.add(store);
				        	}
				        	
				        	if( !Utils.isNull(barcodeInPage).equals("")){
				        		barcodeInPage = barcodeInPage.substring(0,barcodeInPage.length()-1);
				        	}
				        	
				        	request.getSession().setAttribute("storeList",storeChkList);
				        	request.getSession().setAttribute("barcodeInPage",barcodeInPage);
				        	
				        	request.setAttribute("validateStore",validateStore);
				        	if( !Utils.isNull(msg).equals("")){
				        		msg = msg.substring(0,msg.length()-1);
				        		msg = "ไม่สามรถบันทึกข้อมูลได้  ["+msg +"]  เนื่องจากร้านค้า Over Credit Limit";
				        	    request.setAttribute("Message", msg);
				        	}
						}
					}
				 }
				
				
				if(validateStore.equals("false")){
					int pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
					
					request.setAttribute("prevPageNumber", pageNumber+"");
					logger.debug("pageNumber:"+pageNumber);
			
				     //** Search Data and Display **/
					List<Order> results = new OrderDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user,pageNumber,pageSize,tableName,itemType);
					if (results != null  && results.size() >0) {
						request.getSession().setAttribute("results", results);
					} else {
						request.getSession().setAttribute("results", null);
						request.setAttribute("Message", "ไม่พบข่อมูล");
					}
			        
				}else{
					logger.debug("Connection commit");
					//conn.commit();
					
					request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
					 //Research 
				    //List<Order> results = new OrderDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user);
			        //request.getSession().setAttribute("results", results);
			       // request.getSession().setAttribute("itemErrorMap", null);
					
					request.setAttribute("action", "newsearch");
					search(orderForm, request, response);
				}
			}else{
				request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้");
				 //Research 
			    List<Order> results = new OrderDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user,tableName);
		        request.getSession().setAttribute("results", results);
		        request.getSession().setAttribute("itemErrorMap", itemErrorMap);
			}
			
			 
		} catch (Exception e) {
			//conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}


	public ActionForward exportToTextAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export ToText");
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer data = new StringBuffer("");
		Connection conn = null;
		OrderDAO orderDAO = new OrderDAO();
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//Gen OrderLotNO and baronbox
			orderDAO.genOrderLotNoProcess(conn, user, orderForm.getOrder().getOrderDate());
			
			//LOTUS_OH_20130509.txt
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = Utils.stringValue(orderDate, Utils.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			String fileName = "PNBIL_"+dateFileName;

			data = orderDAO.genOrderToTextAll(conn, user, orderForm.getOrder().getOrderDate());

			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+".txt");
			response.setContentType("text/html");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"TIS-620")); 
			w.write(data.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();
		    
		    conn.commit();
		    logger.debug("Conn Commit");
		} catch(LogisticException e){
			conn.rollback();
			logger.debug("Conn Rollback");
			logger.error(e.getMessage(),e);
			
			request.setAttribute("Message","LogisticException:"+e.getMessage());
			return mapping.findForward("export");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
			return mapping.findForward("export");
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("export");
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
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = Utils.stringValue(orderDate, Utils.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			String refCode = Utils.isNull(STORE_TYPE_MAP.get(orderForm.getOrder().getStoreType()));
			String fileName = refCode+"TS_"+dateFileName;
			
			OrderDAO orderDAO = new OrderDAO();
			data = orderDAO.genOrderToExcel(conn, user, orderForm.getOrder().getStoreType(), orderForm.getOrder().getOrderDate());
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
		return mapping.findForward("exportToExcel");
	}
	
	public ActionForward exportSummaryToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Summary ToExcel");
		try {
			exportSummaryToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("export");
	}
	
	public ActionForward exportSummaryToExcelView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Summary ToExcel View");
		try {
			exportSummaryToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("exportView");
	}
	
	private void exportSummaryToExcel(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = Utils.stringValue(orderDate, Utils.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			
			String refCode = Utils.isNull(STORE_TYPE_MAP.get(orderForm.getOrder().getStoreType()))+"_";
			String fileName = refCode+"SUM_"+dateFileName;
			
			OrderDAO orderDAO = new OrderDAO();
			//data = orderDAO.genSummaryOrderToExcel(conn, user, orderForm.getOrder().getStoreType(), orderDate);
			XSSFWorkbook xssfWorkbookDta = orderDAO.genSummaryOrderToCsv(conn, user, orderForm.getOrder().getStoreType(), orderDate);
			
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
	
	public ActionForward exportSummaryAllToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Summary All ToExcel");
		OrderForm orderForm = (OrderForm) form;
		try {
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = Utils.stringValue(orderDate, Utils.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			
			String fileName = "SummaryAll_"+dateFileName+".xls";
			StringBuffer htmlTable = OrderExport.exportSummaryAll(orderForm.getOrder().getOrderDate());
		
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("export");
	}
	
	public ActionForward exportDetailToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Detail ToExcel");
		try {
			exportDetailToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("export");
	}
	
	public ActionForward exportDetailToExcelView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export Detail ToExcel View");
		try {
			exportDetailToExcel(form,request,response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("exportView");
	}
	
	private void exportDetailToExcel(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export ToExcel");
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateFileName = Utils.stringValue(orderDate, Utils.YYYY_MM_DD_WITHOUT_SLASH,Locale.US);
			String refCode = Utils.isNull(STORE_TYPE_MAP.get(orderForm.getOrder().getStoreType()))+"_";
			String fileName = refCode+"SUM_DETAIL_"+dateFileName;
			
			OrderDAO orderDAO = new OrderDAO();
			//data = orderDAO.genDetailOrderToExcel(conn, user, orderForm.getOrder().getStoreType(), orderDate);
			XSSFWorkbook xssfWorkbookDta = orderDAO.genDetailOrderToCsv(conn, user, orderForm.getOrder().getStoreType(), orderDate);
			
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
			 order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 summaryForm.setOrder(order);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	
	/**
	 * Prepare without ID
	 */
	public ActionForward prepareView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		OrderForm summaryForm = (OrderForm) form;
		Connection conn = null;
		try {
			 logger.debug("prepareView");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("storeList",null);
				 request.getSession().setAttribute("itemErrorMap", null);
				 
				 request.getSession().removeAttribute("totalPage");
				 request.getSession().removeAttribute("totalPage"); 
				 
				 //init store type
				 new OrderDAO().initStoreTypeMap();
				 
				 Order order = new Order();
				 //Old OrderDate set to next billDate
				 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 
				 //new set to Current Date
				 order.setOrderDate(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 summaryForm.setOrder(order);
				 
				 ImportDAO importDAO = new ImportDAO();
				 conn = DBConnection.getInstance().getConnection();
				 
				 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"");
				 request.getSession().setAttribute("storeTypeList",storeTypeList);
				
				 List<References> regionList = importDAO.getRegionList(conn);
				 request.getSession().setAttribute("regionList",regionList);
				 
				 List<References> billTypeList = importDAO.getBillTypeList();
				 request.getSession().setAttribute("billTypeList",billTypeList);

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
		return mapping.findForward("prepareView");
	}
	
	public ActionForward prepareHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		OrderForm summaryForm = (OrderForm) form;
		Connection conn = null;
		try {
			 logger.debug("prepareHistory");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("storeList",null);
				 request.getSession().setAttribute("itemErrorMap", null);
				 
				 request.getSession().removeAttribute("totalPage");
				 request.getSession().removeAttribute("totalPage"); 
				 
				 //init store type
				 new OrderDAO().initStoreTypeMap();
				 
				 Order order = new Order();
				 //Old OrderDate set to next billDate
				 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 
				 //new set to Current Date
				 order.setOrderDate(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 summaryForm.setOrder(order);
				 
				 ImportDAO importDAO = new ImportDAO();
				 conn = DBConnection.getInstance().getConnection();
				 
				 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"");
				 request.getSession().setAttribute("storeTypeList",storeTypeList);
				
				 List<References> regionList = importDAO.getRegionList(conn);
				 request.getSession().setAttribute("regionList",regionList);
				 
				 List<References> billTypeList = importDAO.getBillTypeList();
				 request.getSession().setAttribute("billTypeList",billTypeList);

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
		return mapping.findForward("history");
	}
	
	/**
	 * Search
	 */
	public ActionForward searchView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		OrderDAO orderDAO = new OrderDAO();
		Connection conn = null;
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		List<StoreBean> storeList = null;
		String action = "";
		String tableName = "PENSBME_ONHAND_BME";
		String itemType ="LotusItem";
		try {
			Order orderCri = orderForm.getOrder();
			if(orderCri.getStoreType().equals(Constants.STORE_TYPE_FRIDAY_CODE)){
				tableName = "PENSBME_ONHAND_BME_FRIDAY";
				itemType ="FridayItem";
			}
			
			conn = DBConnection.getInstance().getConnection();
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));
			logger.debug("searchView action:"+action);
	
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			if("newsearch".equals(action)){
				pageNumber = 1;
				request.setAttribute("action", "newsearch");
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("storeList",null);
				
				totalRow = orderDAO.getTotalRowBMEItem(conn,orderForm.getOrder(),tableName);
				totalPage = (totalRow/ pageSize)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);
				
				storeList = orderDAO.getStoreList(conn,orderForm.getOrder().getStoreType(),orderForm.getOrder().getRegion(),orderForm.getOrder().getBillType(),"''");
				request.getSession().setAttribute("storeList",storeList);
				
				if(storeList==null || (storeList != null && storeList.size()==0)){
					request.setAttribute("Message", "ไม่พบข่อมูลสาขา");
				}
				
			}else{
				totalRow = (Integer)request.getSession().getAttribute("totalRow");
				totalPage = (Integer)request.getSession().getAttribute("totalPage");
				
				storeList = (List<StoreBean>)request.getSession().getAttribute("storeList");
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}
			
			logger.debug("totalRow["+totalRow+"]totalPage["+totalPage+"]pageNumber["+pageNumber+"]");
			
            //** Search Data and Display **/
			List<Order> results = new OrderDAO().prepareNewOrder(conn,orderForm.getOrder(),storeList, user,pageNumber,pageSize,tableName,itemType);
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("searchView");
	}
	
	public ActionForward searchHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		OrderDAO orderDAO = new OrderDAO();
		Connection conn = null;
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		List<StoreBean> storeList = null;
		String action = "";
		String tableName = "PENSBME_ONHAND_BME";
		String itemType ="LotusItem";
		try {
			Order orderCri = orderForm.getOrder();
			if(orderCri.getStoreType().equals(Constants.STORE_TYPE_FRIDAY_CODE)){
				tableName = "PENSBME_ONHAND_BME_FRIDAY";
				itemType ="FridayItem";
			}
			
			conn = DBConnection.getInstance().getConnection();
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));
			logger.debug("searchHistory action:"+action);
	
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			if("newsearch".equals(action)){
				pageNumber = 1;
				request.setAttribute("action", "newsearch");
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("storeList",null);
				
				totalRow = orderDAO.getTotalRowBMEItemHistory(conn,orderForm.getOrder());
				totalPage = (totalRow/ pageSize)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);
				
				String storeCodeWhereIn = Utils.converToTextSqlIn(orderForm.getOrder().getPensCustCodeFrom());
				storeList = orderDAO.getStoreList(conn,orderForm.getOrder().getStoreType(),orderForm.getOrder().getRegion(),orderForm.getOrder().getBillType(),storeCodeWhereIn);
				request.getSession().setAttribute("storeList",storeList);
				
				if(storeList==null || (storeList != null && storeList.size()==0)){
					request.setAttribute("Message", "ไม่พบข่อมูลสาขา");
				}
				
			}else{
				totalRow = (Integer)request.getSession().getAttribute("totalRow");
				totalPage = (Integer)request.getSession().getAttribute("totalPage");
				
				storeList = (List<StoreBean>)request.getSession().getAttribute("storeList");
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}
			
			logger.debug("totalRow["+totalRow+"]totalPage["+totalPage+"]pageNumber["+pageNumber+"]");
			
            //** Search Data and Display **/
			Order orderResult = new OrderDAO().prepareNewOrderHistory(conn,orderForm.getOrder(),storeList, user,pageNumber,pageSize,tableName,itemType);
			List<Order> results = orderResult.getOrderItemList();
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("history");
	}
	
	
	public ActionForward clearView(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		OrderForm summaryForm = (OrderForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			 request.getSession().setAttribute("storeList",null);
			 request.getSession().setAttribute("itemErrorMap", null);
			 
			 Order order = new Order();
			 order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 summaryForm.setOrder(order);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clearView");
	}
	
	public ActionForward clearHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearHistory");
		OrderForm summaryForm = (OrderForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			 request.getSession().setAttribute("storeList",null);
			 request.getSession().setAttribute("itemErrorMap", null);
			 
			 Order order = new Order();
			 order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			 summaryForm.setOrder(order);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("history");
	}
	
	public ActionForward prepareReportOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		OrderForm summaryForm = (OrderForm) form;
		Connection conn = null;
		try {
			 logger.debug("prepareReportOrder");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 
				 request.getSession().removeAttribute("totalPage");
				 request.getSession().removeAttribute("totalPage"); 
				 
				 //init store type
				 new OrderDAO().initStoreTypeMap();
				 
				 Order order = new Order();
				 //Old OrderDate set to next billDate
				 // order.setOrderDate(Utils.stringValue(OrderDateUtils.getOrderDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 
				 //new set to Current Date
				 order.setOrderDate(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 summaryForm.setOrder(order);
				 
				 ImportDAO importDAO = new ImportDAO();
				 conn = DBConnection.getInstance().getConnection();
				 
				 List<References> storeTypeList = importDAO.getStoreTypeList(conn,"");
				 request.getSession().setAttribute("storeTypeList",storeTypeList);
				
				 List<References> regionList = importDAO.getRegionList(conn);
				 request.getSession().setAttribute("regionList",regionList);
				 
				 List<References> billTypeList = importDAO.getBillTypeList();
				 request.getSession().setAttribute("billTypeList",billTypeList);

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
		return mapping.findForward("reportOrder");
	}
	
	public ActionForward searchReportOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		OrderDAO orderDAO = new OrderDAO();
		Connection conn = null;
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		List<StoreBean> storeList = null;
		String action = "";
		try {
			Order orderCri = orderForm.getOrder();

			conn = DBConnection.getInstance().getConnection();
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));
			logger.debug("searchHistory action:"+action);
	
			Date orderDate = Utils.parse(orderForm.getOrder().getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			if("newsearch".equals(action)){
				pageNumber = 1;
				request.setAttribute("action", "newsearch");
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("storeList",null);
				
				totalRow = orderDAO.getTotalRowReportOrder(conn,orderForm.getOrder());
				totalPage = (totalRow/ pageSize)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);
				
			}else{
				totalRow = (Integer)request.getSession().getAttribute("totalRow");
				totalPage = (Integer)request.getSession().getAttribute("totalPage");
				
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}
			
			logger.debug("totalRow["+totalRow+"]totalPage["+totalPage+"]pageNumber["+pageNumber+"]");
			
            //** Search Data and Display **/
			Order orderResult = new OrderDAO().prepareReportOrder(conn,orderForm.getOrder(), user,pageNumber,pageSize);
			List<Order> results = orderResult.getOrderItemList();
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("reportOrder");
	}
	
	public ActionForward exportReportOrderToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportReportOrderToExcel");
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer h = new StringBuffer("");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			Order orderResult = new OrderDAO().prepareReportOrder(conn,orderForm.getOrder(),user,1,999999999);
			if(orderResult != null){
				logger.debug("header:"+ExcelHeader.EXCEL_HEADER.toString());
				
				h.append(ExcelHeader.EXCEL_HEADER.toString());
				
				h.append("<table border='1'>");
				h.append("<tr>");
				   h.append("<td>Store Code </td>");
				   h.append("<td>Order Date</td>");
				   h.append("<td>Pens Item</td>");
				   h.append("<td>Group Code </td>");
				   h.append("<td>Material Master</td>");
				   h.append("<td>Barcode</td>");
				   h.append("<td>QTY </td>");
				   h.append("<td>Whole Price</td>");
				   h.append("<td>Retail Price</td>");
	            h.append("</tr>");    
	            
	            if(orderResult.getOrderItemList() != null && orderResult.getOrderItemList().size() > 0){
	            	for(int i=0;i<orderResult.getOrderItemList().size();i++){
	            		Order o = orderResult.getOrderItemList().get(i);
	            		h.append("<tr> \n");    
		            		h.append("<td>"+o.getStoreCode()+"</td>");
		  				    h.append("<td>"+o.getOrderDate()+"</td>");
		  				    h.append("<td>"+o.getItem()+"</td>");
		  				    h.append("<td>"+o.getGroupCode()+"</td>");
		  				    h.append("<td>"+o.getMaterialMaster()+"</td>");
		  				    h.append("<td class='text'>"+o.getBarcode()+"</td>");
		  				    h.append("<td class='num'>"+o.getQty()+"</td>");
		  				    h.append("<td>"+o.getWholePriceBF()+"</td>");
		  				    h.append("<td>"+o.getRetailPriceBF()+"</td>");
	  				    h.append("</tr> "); 
	            	}
	            }
	            h.append("</table> \n"); 
	            
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(h.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("reportOrder");
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
