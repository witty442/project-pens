package com.isecinc.pens.web.buds.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.model.I_PO;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderEDIBean;
import com.isecinc.pens.bean.StockOnhandBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.buds.BudsAllBean;
import com.isecinc.pens.web.buds.BudsAllForm;
import com.isecinc.pens.web.sales.InterfaceOrderProcess;
import com.pens.util.ControlCode;
import com.pens.util.DBConnectionApps;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class OrderEDIAction extends I_Action {

	public static int pageSize = 60;
	
	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		BudsAllBean bean = new BudsAllBean();
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				bean.setOrderEDIBean(new OrderEDIBean());
				aForm.setBean(bean);
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		String subPageName = aForm.getSubPageName();
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnectionApps.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.getBean().setOrderEDIBean((OrderEDIBean)request.getSession().getAttribute("_criteria"));
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				aForm.setPageSize(pageSize);
				
				//get Total Record
				aForm.setTotalRecord(OrderEDIDAO.searchTotalHead(conn,user,subPageName,aForm.getBean().getOrderEDIBean()));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
				OrderEDIBean confPickingBean = OrderEDIDAO.searchHead(conn,user,subPageName,aForm.getBean().getOrderEDIBean(),allRec,currPage,pageSize);
				if(confPickingBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setOrderEDIBean(confPickingBean);
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    OrderEDIBean confPickingBean = OrderEDIDAO.searchHead(conn,user,subPageName,aForm.getBean().getOrderEDIBean(),allRec,currPage,pageSize);
				if(confPickingBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setOrderEDIBean(confPickingBean);
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("viewDetail");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			
			aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			//save criteria
			request.getSession().setAttribute("_criteria", aForm.getBean().getOrderEDIBean());
			
			String mode = Utils.isNull(request.getParameter("mode"));
		    String orderNo = Utils.isNull(request.getParameter("orderNo"));
		    
		    logger.debug("orderNo:"+orderNo);
		    if(!"".equalsIgnoreCase(Utils.isNull(orderNo))){
		    	OrderEDIBean orderEDIHBean = new OrderEDIBean();
		    	orderEDIHBean.setOrderNo(orderNo);
		    	//Get Heade info
		    	orderEDIHBean = OrderEDIDAO.searchHead(conn,user, "", orderEDIHBean, true, 0, 0).getItemsList().get(0);
		    	
		    	//Get detail Order EDI
		    	OrderEDIBean orderEDIDBean = OrderEDIDAO.searchOrderEDIDetail(orderNo,null);
		    	orderEDIHBean.setItemsList(orderEDIDBean.getItemsList());
		       
		        //check edit
		        if(    !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_CANCEL)
		        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_PICKING)	
		        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_LOADING) ){
		        	orderEDIHBean.setCanEdit(true);
		        }
		        aForm.getBean().setOrderEDIBean(orderEDIHBean);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "budsAll";
		BudsAllForm aForm = (BudsAllForm) form;
		BudsAllBean bean = new BudsAllBean();
		Connection conn = null;
		try {
			 
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			conn.close();
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		boolean excel = false;
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			conn.close();
		}
		return "budsAll";
	}
	
	public ActionForward exportStockOnhandReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportStockOnhandReport");
		BudsAllForm aForm = (BudsAllForm) form;
		Connection conn = null;
		boolean excel = true;
		try {
			//wait
			conn = DBConnectionApps.getInstance().getConnection();
			
			aForm.getBean().getStockOnhandBean().setDataStrBuffer(null);
		    StockOnhandBean stockOnhandBean = StockOnhandDAO.searchStockOnhandReport(conn,aForm.getBean().getStockOnhandBean(),excel);
	
			if(stockOnhandBean != null && stockOnhandBean.getDataStrBuffer() != null){

				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(stockOnhandBean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    stockOnhandBean.setDataStrBuffer(null);//clear memory
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
				stockOnhandBean.setDataStrBuffer(null);//clear memory
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("budsAll");
	}

	public ActionForward saveOrderEDI(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveOrderEDI");
		User user = (User) request.getSession().getAttribute("user");
		BudsAllForm aForm = (BudsAllForm) form;
		Map<String, String> productErrorMap = null;
		String msg = "";
		try {
			OrderEDIBean orderEDIBean = aForm.getBean().getOrderEDIBean();
			orderEDIBean.setUserName(user.getUserName());
			
			List<OrderEDIBean> itemsList = new ArrayList<OrderEDIBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productId = request.getParameterValues("productId");
			String[] productCode = request.getParameterValues("productCode");
			String[] uom = request.getParameterValues("uom");
			String[] qty = request.getParameterValues("qty");
			String[] unitPrice = request.getParameterValues("unitPrice");
			String[] lineAmount = request.getParameterValues("lineAmount");
			String[] status = request.getParameterValues("status");
			
			logger.debug("productCode:"+productCode.length);
		
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					logger.debug("productCode:"+productCode[i]+",status:"+status[i]);
					logger.debug("qty:"+qty[i]);
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")
						&& (!Utils.isNull(qty[i]).equals("")  )
					 ){
						 OrderEDIBean l = new OrderEDIBean();
						 l.setHeaderId(orderEDIBean.getHeaderId());
						 l.setLineId(Utils.isNull(lineId[i]));
						 l.setProductId(Utils.isNull(productId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setUom(Utils.isNull(uom[i]));//transaction_uom
						 l.setQty(Utils.isNull(qty[i]));
						 l.setUnitPrice(Utils.isNull(unitPrice[i]));
						 l.setLineAmount(Utils.isNull(lineAmount[i]));
						 l.setStatus(Utils.isNull(status[i]));
						 l.setUserName(user.getId()+"");
						 
						 itemsList.add(l);
					}//if
				}//for
			}//if
			
			orderEDIBean.setItemsList(itemsList);
			
			//control code 
			if(ControlCode.canExecuteMethod("Stock", "checkStockEDI")){
				//before delete some row is reserver stock to delete old reserve stock
				OrderEDIBean oldOrderEDIBean = OrderEDIDAO.searchOrderEDIDetail(orderEDIBean.getOrderNo(), null);
				List<OrderEDIBean> dbLines = oldOrderEDIBean!=null?oldOrderEDIBean.getItemsList():null;
				//case status ='RESERVE' in LINE  delete reservation in stock onhand
				for (OrderEDIBean line : dbLines) {
					if(line.getReservationId() != 0){
					  logger.info("delete reserve Stock By ReservationId:"+line.getReservationId());
					  InterfaceOrderProcess.deleteStockReservation(line.getReservationId());
					}//if
				}//for
			}
			
			//save to DB
			OrderEDIDAO.saveOrderEDI(orderEDIBean);
			
			// set msg save success
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
			
	        //Search Data new refresh
			OrderEDIBean orderEDIHBean = new OrderEDIBean();
	    	orderEDIHBean.setOrderNo(orderEDIBean.getOrderNo());
	    
			//Get Header info
			orderEDIHBean = OrderEDIDAO.searchHead(user,"", orderEDIHBean, true, 0, 0).getItemsList().get(0);
	    	
	    	//Get detail Order EDI
	    	OrderEDIBean orderEDIDBean = OrderEDIDAO.searchOrderEDIDetail(orderEDIBean.getOrderNo(),null);
	    	orderEDIHBean.setItemsList(orderEDIDBean.getItemsList());
	       
	        //check edit
	        if(    !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_CANCEL)
	        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_PICKING)	
	        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_LOADING) ){
	        	orderEDIHBean.setCanEdit(true);
	        }
	        aForm.getBean().setOrderEDIBean(orderEDIHBean);
	        
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			
		}
		return mapping.findForward("budsAll");
	}
  
	public ActionForward confirmOrderEDI(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmOrderEDI");
		User user = (User) request.getSession().getAttribute("user");
		BudsAllForm aForm = (BudsAllForm) form;
		Map<String, String> productErrorMap = null;
		String msg = "บันทึกข้อมูลเรียบร้อยแล้ว";
		try {
			OrderEDIBean orderEDIBean = aForm.getBean().getOrderEDIBean();
			orderEDIBean.setUserName(user.getUserName());
			
			List<OrderEDIBean> itemsList = new ArrayList<OrderEDIBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productId = request.getParameterValues("productId");
			String[] productCode = request.getParameterValues("productCode");
			String[] uom = request.getParameterValues("uom");
			String[] qty = request.getParameterValues("qty");
			String[] unitPrice = request.getParameterValues("unitPrice");
			String[] lineAmount = request.getParameterValues("lineAmount");
			String[] status = request.getParameterValues("status");
			
			logger.debug("productCode:"+productCode.length);
			
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					logger.debug("productCode:"+productCode[i]+",status:"+status[i]);
					logger.debug("qty:"+qty[i]);
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")
						&& (!Utils.isNull(qty[i]).equals("")  )
					 ){
						 OrderEDIBean l = new OrderEDIBean();
						 l.setHeaderId(orderEDIBean.getHeaderId());
						 l.setLineId(Utils.isNull(lineId[i]));
						 l.setProductId(Utils.isNull(productId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setUom(Utils.isNull(uom[i]));//transaction_uom
						 l.setQty(Utils.isNull(qty[i]));
						 l.setUnitPrice(Utils.isNull(unitPrice[i]));
						 l.setLineAmount(Utils.isNull(lineAmount[i]));
						 l.setStatus(Utils.isNull(status[i]));
						 l.setUserName(user.getId()+"");
						 
						 itemsList.add(l);
					}//if
				}//for
			}//if
			
			orderEDIBean.setItemsList(itemsList);
			
			//control code 
			if(ControlCode.canExecuteMethod("Stock", "checkStockEDI")){
				//before reserver stock clear old reserve stock
				OrderEDIBean oldOrderEDIBean = OrderEDIDAO.searchOrderEDIDetail(orderEDIBean.getOrderNo(), null);
				List<OrderEDIBean> dbLines = oldOrderEDIBean!=null?oldOrderEDIBean.getItemsList():null;
				//case status ='RESERVE' in LINE  delete reservation in stock onhand
				for (OrderEDIBean line : dbLines) {
					if(line.getReservationId() != 0){
					  logger.info("delete reserve Stock By ReservationId:"+line.getReservationId());
					  InterfaceOrderProcess.deleteStockReservation(line.getReservationId());
					}//if
				}//for
			}
			
			//save to DB
			OrderEDIDAO.saveOrderEDI(orderEDIBean);
			
			//control code 
			if(ControlCode.canExecuteMethod("Stock", "checkStockEDI")){
				
				//Generate Interfaces Order To Oracle Temp
				//return productErrorMap cannot reserve order to display sales
				productErrorMap = InterfaceOrderProcess.reserveOrderEDI(user,orderEDIBean.getHeaderId(),orderEDIBean.getOrderNo());
				
				if(productErrorMap != null && !productErrorMap.isEmpty()){
					orderEDIBean.setDocStatus(Order.STATUS_UNAVAILABLE);
				    request.setAttribute("Message","ไม่สามารถจองสินค้าได้ โปรดตรวจสอบสต๊อกสินค้าอีกครั้ง  จากนั้น แก้ไขออเดอร์ให้ถูกต้อง " );
				}else{
				   // set msg save success
				   request.setAttribute("Message",msg );
				}
			}else{
				 // set msg save success
				 request.setAttribute("Message",msg );
			}
			
			/////////////////////////////////////////////
			//for test
			/*productErrorMap = new HashMap<String, String>();
			productErrorMap.put("815216", "815216");
			productErrorMap.put("815214", "815214");
			Connection conn = DBConnectionApps.getInstance().getConnection();
			InterfaceOrderProcess.updateStatusOrderEDI(conn, orderEDIBean.getOrderNo(), I_PO.STATUS_RESERVE);
			conn.close();*/
		    /////////////////////////////////////////////	
		
	        //Search Data new refresh
			OrderEDIBean orderEDIHBean = new OrderEDIBean();
	    	orderEDIHBean.setOrderNo(orderEDIBean.getOrderNo());
	    
			//Get Header info
			orderEDIHBean = OrderEDIDAO.searchHead(user,"", orderEDIHBean, true, 0, 0).getItemsList().get(0);
	    	
	    	//Get detail Order EDI
	    	OrderEDIBean orderEDIDBean = OrderEDIDAO.searchOrderEDIDetail(orderEDIBean.getOrderNo(),null);
	    	
	    	//display error product cannot Reserve
	    	itemsList= orderEDIDBean.getItemsList();
	    	if(productErrorMap != null && !productErrorMap.isEmpty() && itemsList != null && itemsList.size() >0){
	    		for(int i=0;i<itemsList.size();i++){
	    			OrderEDIBean itemsRow = itemsList.get(i);
	    			if(productErrorMap.get(itemsRow.getProductId()) != null){
	    				itemsRow.setRowStyle("lineError");
	    				itemsList.set(i, itemsRow);
	    			}
	    		}
	    	}
	    	orderEDIHBean.setItemsList(itemsList);
	       
	        //check edit
	        if(    !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_CANCEL)
	        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_PICKING)	
	        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_LOADING) ){
	        	orderEDIHBean.setCanEdit(true);
	        }
	        aForm.getBean().setOrderEDIBean(orderEDIHBean);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward cancelOrderEDI(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelOrderEDI");
		User user = (User) request.getSession().getAttribute("user");
		BudsAllForm aForm = (BudsAllForm) form;
		String msg = "ยกเลิกข้อมูลเรียบร้อยแล้ว";
		try {
			OrderEDIBean orderEDIBean = aForm.getBean().getOrderEDIBean();
			orderEDIBean.setUserName(user.getUserName());
			orderEDIBean.setDocStatus(I_PO.STATUS_CANCEL);
			
			//control code 
			if(ControlCode.canExecuteMethod("Stock", "checkStockEDI")){
				//before reserver stock clear old reserve stock
				OrderEDIBean oldOrderEDIBean = OrderEDIDAO.searchOrderEDIDetail(orderEDIBean.getOrderNo(), null);
				List<OrderEDIBean> dbLines = oldOrderEDIBean!=null?oldOrderEDIBean.getItemsList():null;
				//case status ='RESERVE' in LINE  delete reservation in stock onhand
				for (OrderEDIBean line : dbLines) {
					if(line.getReservationId() != 0){
					  logger.info("delete reserve Stock By ReservationId:"+line.getReservationId());
					  InterfaceOrderProcess.deleteStockReservation(line.getReservationId());
					}//if
				}//for
			}
			
			//update status to CANCEL to DB
			OrderEDIDAO.updateOrderEDIModel(orderEDIBean);
			
	        //Search Data new refresh
			OrderEDIBean orderEDIHBean = new OrderEDIBean();
	    	orderEDIHBean.setOrderNo(orderEDIBean.getOrderNo());
	    
			//Get Header info
			orderEDIHBean = OrderEDIDAO.searchHead(user,"", orderEDIHBean, true, 0, 0).getItemsList().get(0);
	    	
	    	//Get detail Order EDI
	    	OrderEDIBean orderEDIDBean = OrderEDIDAO.searchOrderEDIDetail(orderEDIBean.getOrderNo(),null);
	    	orderEDIHBean.setItemsList(orderEDIDBean.getItemsList());
	        //check edit
	        if(    !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_CANCEL)
	        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_PICKING)	
	        	&& !Utils.isNull(orderEDIHBean.getDocStatus()).equals(I_PO.STATUS_LOADING) ){
	        	orderEDIHBean.setCanEdit(true);
	        }
	        aForm.getBean().setOrderEDIBean(orderEDIHBean);
			
	        request.setAttribute("Message",msg);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Save
	 */
	public String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "budsAll";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "budsAll";
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
