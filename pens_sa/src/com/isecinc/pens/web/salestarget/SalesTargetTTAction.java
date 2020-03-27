package com.isecinc.pens.web.salestarget;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.DataDuplicateException;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SalesTargetTTAction  {
	protected static Logger logger = Logger.getLogger("PENS");

	/**
	 * Save
	 */
	protected String saveTTByMKT(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			h.setStatus(SalesTargetConstants.STATUS_OPEN);
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<SalesTargetBean> itemList = new ArrayList<SalesTargetBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] itemCode = request.getParameterValues("itemCode");
			String[] itemId = request.getParameterValues("itemId");
			String[] targetQty = request.getParameterValues("targetQty");
			String[] targetAmount = request.getParameterValues("targetAmount");
			String[] price = request.getParameterValues("price");
			String[] orderAmt12Month = request.getParameterValues("orderAmt12Month");
			String[] orderAmt3Month = request.getParameterValues("orderAmt3Month");
			String[] remark = request.getParameterValues("remark");
			String[] rejectReason = request.getParameterValues("rejectReason");
			String[] status = request.getParameterValues("status");
			
			logger.debug("itemCode:"+itemCode.length);
			
			//add value to Results
			if(itemCode != null && itemCode.length > 0){
				for(int i=0;i<itemCode.length;i++){
					if( !Utils.isNull(itemCode[i]).equals("") && !Utils.isNull(price[i]).equals("")){
						 SalesTargetBean l = new SalesTargetBean();
						 logger.debug("itemCode["+Utils.isNull(itemCode[i])+"]lineId:"+lineId[i]);
						 
						 l.setLineId(Utils.convertStrToLong(lineId[i],0));
						 l.setItemCode(Utils.isNull(itemCode[i]));
						 l.setItemId(Utils.isNull(itemId[i]));
						 l.setTargetQty(Utils.isNull(targetQty[i]));
						 l.setTargetAmount(Utils.isNull(targetAmount[i]));
						 l.setPrice(Utils.isNull(price[i]));
						 l.setOrderAmt12Month(Utils.isNull(orderAmt12Month[i]));
						 l.setOrderAmt3Month(Utils.isNull(orderAmt3Month[i]));
						 l.setRemark(Utils.isNull(remark[i]));
						 l.setRejectReason(Utils.isNull(rejectReason[i]));
						 l.setStatus(Utils.isNull(status[i]));
						
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 itemList.add(l);
					}//if
				}//for
			}//if
			//set items list
			h.setItems(itemList);
			
			//save
			SalesTargetTTDAO.save(conn,h,user);
			
			/** after save case: MKT(reject from ttsupper) delete some product but TTSUPER is save product 
			 *  check is product not in(TT) and delete 
			 */
			SalesTargetTTDAO.deleteProductByMKTDel(conn, h, user);
			
			//commit
			conn.commit();

			//search
			boolean getItems = true;
			h = SalesTargetTTDAO.searchSalesTargetTT(conn, h,getItems, user,aForm.getPageName());
			//get PriceListId
			h.setPriceListId(SalesTargetTTUtils.getPriceListId(conn, h.getSalesZone(), h.getCustCatNo(),user));
			
			aForm.setBean(h);
			aForm.setResults(h.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "detailTTMKT";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detailTTMKT";
	}
	
	protected String saveTTSUPER(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SalesTargetBean l = null;
		List<SalesTargetBean> salesrepDataSaveList = new ArrayList<SalesTargetBean>();
		List<SalesTargetBean> productDataSaveListBySalesrep = new ArrayList<SalesTargetBean>();
		boolean checkFoundInsertQty = false;
		SalesTargetBean h = null;
		String sessionId = request.getSession().getId();
		boolean dataValid = true;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean bean = aForm.getBean();
           
			
			//save Item target_temp
			List<SalesTargetBean> productMKTList =(List<SalesTargetBean>)request.getSession().getAttribute("productMKTList");
			List<SalesTargetBean> salesrepList = (List<SalesTargetBean>)request.getSession().getAttribute("salesrepList");
			
			if(productMKTList != null && productMKTList.size()>0){ 
				for(int row=0;row<salesrepList.size();row++ ){
        			SalesTargetBean salesrepBean = (SalesTargetBean)salesrepList.get(row);
        			productDataSaveListBySalesrep = new ArrayList<SalesTargetBean>();
        			checkFoundInsertQty = false;
        			
	        	    for(int col=0;col<productMKTList.size();col++){
	                    SalesTargetBean productBean = (SalesTargetBean)productMKTList.get(col);
	        		
	        			String keyTargetQty = "target_qty_"+productBean.getItemCode()+"_"+salesrepBean.getSalesrepCode();
	        			String keyTargetAmount = "target_amount_"+productBean.getItemCode()+"_"+salesrepBean.getSalesrepCode();
	        			
	        			String targetQty = Utils.isNull(request.getParameter(keyTargetQty));
	                    String targetAmount = Utils.isNull(request.getParameter(keyTargetAmount));
	                   // logger.debug("keyTargetQty["+keyTargetQty+"],targetQty["+targetQty+"]");
	                    
                    	logger.debug("keyTargetQty["+keyTargetQty+"],targetQty["+targetQty+"]");
                    	
                    	checkFoundInsertQty = true;
	                    String id = Utils.isNull(request.getParameter("id_"+productBean.getItemCode()+"_"+salesrepBean.getSalesrepCode()));
	                    String lineId = Utils.isNull(request.getParameter("line_id_"+productBean.getItemCode()+"_"+salesrepBean.getSalesrepCode()));
	                    
	                    l = new SalesTargetBean();
	  
	                    logger.debug("itemId:"+productBean.getItemId());
	                    l.setId(Utils.convertStrToLong(id,0));
						l.setLineId(Utils.convertStrToLong(lineId,0));
						l.setItemCode(productBean.getItemCode());
						l.setItemId(productBean.getItemId());
						l.setTargetQty(targetQty);
						l.setTargetAmount(targetAmount);
						l.setPrice(productBean.getPrice());
						l.setOrderAmt12Month(productBean.getOrderAmt12Month());
						l.setOrderAmt3Month(productBean.getOrderAmt3Month());
						l.setRemark("");
						l.setRejectReason("");
						l.setCreateUser(user.getUserName());
						l.setUpdateUser(user.getUserName());
						l.setSessionId(sessionId);
						
							
	                    //add product
						productDataSaveListBySalesrep.add(l);
			
						//validate head id vs input(screen) id
	                    if(salesrepBean.getId() != l.getId()){
	                    	dataValid = false;
	                    	break;
	                    }//if
	        		}//for 2
	        	    
	        	   //set header property
        	    	h = new SalesTargetBean();
        	    	h.setStartDate(bean.getStartDate());
	        	    h.setPeriod(bean.getPeriod());
	        	    h.setCustCatNo(bean.getCustCatNo());
	        	    h.setBrand(bean.getBrand());
	        	    h.setBrandGroup(bean.getBrandGroup());
        	    	h.setId(salesrepBean.getId());
	        	    h.setSalesrepCode(salesrepBean.getSalesrepCode());
	        	    h.setSalesrepId(salesrepBean.getSalesrepId());
	        	    h.setSalesChannelNo(salesrepBean.getSalesChannelNo());
	        	    h.setDivision(SalesTargetTTUtils.getDivision(conn,h.getSalesChannelNo()));
	    			//set productList to salesrepCode
	        	    h.setItems(productDataSaveListBySalesrep);
	        		h.setStatus(SalesTargetConstants.STATUS_POST);
	    			h.setCreateUser(user.getUserName());
	    			h.setUpdateUser(user.getUserName());
	    			h.setSessionId(sessionId);
	    			h.setUserInputId(Utils.convertStrToLong(request.getParameter("userInputId")));
					logger.debug("userInputId:"+h.getUserInputId());
					
	        	    logger.debug("** Prepare head *****");
	        	    logger.debug("salesrepCode:"+h.getSalesrepCode()+",id:"+h.getId());
	        	    
	        	    salesrepDataSaveList.add(h);
	        	}//for 1
			}//if
			
			//validate duplicate
			if(dataValid==false){
				request.setAttribute("Message", "!!!ไม่สามารถบันทึกข้อมูลได้ พบข้อมูลไม่ถูกต้อง  กลับไปที่หน้าค้นหาหลัก แล้วทำการบันทึกข้อมูลใหม่  หากไม่ได้ กรุณาติดต่อ ฝ่ายไอที");
				return "detailTTSUPER";
			}
		
			//save Head target_temp
			SalesTargetTTDAO.saveModelByTTSUPER_TT(conn, salesrepDataSaveList);

			//commit
			conn.commit();

			//Get target save By Sales(XXPENS_BI_SALES_TARGET_TEMP) to MAP
			//KEY store_code+itemCode
			List<Map> dataList = SalesTargetTTDAO.searchSalesTargetTempToMap(conn, aForm.getBean(), user);
			Map<String, String> rowMap = dataList.get(0);
			Map<String, SalesTargetBean> dataMap = dataList.get(1);
		
			//Get SalesList
		    salesrepList = SalesTargetTTDAO.searchSalesrepListByTTSUPER(conn, aForm.getBean(), user,rowMap);
	
			request.getSession().setAttribute("salesrepList", salesrepList);
			request.getSession().setAttribute("dataMap", dataMap);
			
			//get PriceListId
			bean.setPriceListId(SalesTargetTTUtils.getPriceListId(conn, h.getSalesZone(), h.getCustCatNo(),user));
			
			aForm.setBean(bean);
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//unlock page 
			request.getSession().removeAttribute("TTSUPER_LOCKPAGE");
			
		} catch (DataDuplicateException e) {
			conn.rollback();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้  พบข้อมูลซ้ำ  <br/> กรุณาตรวจสอบว่าไม่ได้เปิด 2 หน้าจอพร้อมกัน <br/> หากไม่ได้ กรุณาแจ้งไอที  \n");
			try {
				
			} catch (Exception e2) {}
			return "detailTTSUPER";
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "detailTTSUPER";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detailTTSUPER";
	}
	
	public ActionForward copyFromLastMonthTTByMKT(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("copyFromLastMonthTTByMKT");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			SalesTargetBean bean = aForm.getBean();
			bean.setCreateUser(user.getUserName());
			bean.setUpdateUser(user.getUserName());
			
			String errorCode = SalesTargetTTCopy.copyFromLastMonthByMKT_TT(user, bean,aForm.getPageName());
			if(errorCode.equalsIgnoreCase("DATA_CUR_EXIST_EXCEPTION")){
				request.setAttribute("Message","ไม่สามารถ Copy ได้ เนื่องจากมีการบันทึกข้อมูลบางส่วนไปแล้ว");
			}else if(errorCode.equalsIgnoreCase("DATA_PREV_NOT_FOUND")){
				request.setAttribute("Message","ไม่พบข้อมูลเดือนที่แล้ว");
			}else{
			   request.setAttribute("Message","Copy ข้อมูลเรียบร้อยแล้ว");
			   
			   //Search Data
			   SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByMKT_TT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() > 0){
				  request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
			   }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward copyFromLastMonthByTTSUPER(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("copyFromLastMonthByTTSUPER FROM XXPENS_BI_SALES_TARGET_TEMP");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			SalesTargetBean bean = aForm.getBean();
			bean.setCreateUser(user.getUserName());
			bean.setUpdateUser(user.getUserName());
			bean.setSessionId(request.getSession().getId());
			
			String errorCode = SalesTargetTTSUPERCopy.copyFromLastMonthByTTSUPER(user, bean,aForm.getPageName());
			
			if(errorCode.equalsIgnoreCase("DATA_CUR_EXIST_EXCEPTION")){
				request.setAttribute("Message","ไม่สามารถ Copy ได้ เนื่องจากมีการบันทึกข้อมูลบางส่วนไปแล้ว");
			}else if(errorCode.equalsIgnoreCase("DATA_MKT_NOT_POST_FOUND")){
				request.setAttribute("Message","ไม่พบข้อมูล Maketing Post To Sales");
			}else if(errorCode.equalsIgnoreCase("DATA_PREV_NOT_FOUND")){
				request.setAttribute("Message","ไม่พบข้อมูลเดือนที่แล้ว");
			}else{
			    request.setAttribute("Message","Copy ข้อมูลเรียบร้อยแล้ว");
			   
			    //Search Data
			    SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMKT(aForm.getBean(),user,pageName);
			    aForm.setBean(salesReuslt);
			    if(salesReuslt.getItems() != null && salesReuslt.getItems().size() > 0){
				   request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
			    }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	public ActionForward copyBrandFromLastMonthByTTSUPER(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("copyBrandFromLastMonthByTTSUPER ");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			SalesTargetBean paramBean = new SalesTargetBean();
			paramBean.setPeriod(Utils.isNull(request.getParameter("period")));
			paramBean.setStartDate(Utils.isNull(request.getParameter("startDate")));
			paramBean.setBrand(Utils.isNull(request.getParameter("brand")));
			paramBean.setCustCatNo(Utils.isNull(request.getParameter("custCatNo")));
			paramBean.setSalesZone(Utils.isNull(request.getParameter("salesZone")));
			paramBean.setCreateUser(user.getUserName());
			paramBean.setUpdateUser(user.getUserName());
			paramBean.setSessionId(request.getSession().getId());
			
			String errorCode = SalesTargetTTSUPERCopyByBrand.copyBrandFromLastMonthByTTSUPER(user, paramBean,aForm.getPageName());
			if(errorCode.equalsIgnoreCase("DATA_CUR_EXIST_EXCEPTION")){
				request.setAttribute("Message","ไม่สามารถ Copy ได้ เนื่องจากมีการบันทึกข้อมูลบางส่วนไปแล้ว");
			}else if(errorCode.equalsIgnoreCase("DATA_MKT_NOT_POST_FOUND")){
				request.setAttribute("Message","ไม่พบข้อมูล Maketing Post To Sales");
			}else if(errorCode.equalsIgnoreCase("DATA_PREV_NOT_FOUND")){
				request.setAttribute("Message","ไม่พบข้อมูลเดือนที่แล้ว");
			}else{
			    request.setAttribute("Message","Copy ข้อมูลเรียบร้อยแล้ว");
			    
			   //search
			   SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTSUPER_TT(aForm.getBean(),user,pageName);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
				    request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTSUPER(request,aForm.getBean(),user));
			   }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward deleteAllTT(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Delete All By Marketing");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			
			//delete all;
			SalesTargetTTDAO.deleteAllByMKT_TT(conn, h);

			request.setAttribute("Message","ลบข้อมูล เรียบร้อยแล้ว");
			
			conn.commit();
			//reset
			h.setId(0);
			h.setTotalOrderAmt12Month("");
			h.setTotalOrderAmt3Month("");
			h.setTotalTargetAmount("");
			h.setTotalTargetQty("");
			h.setItems(null);
			h.setItemsList(null);
			logger.debug("priceListId:"+h.getPriceListId());
			aForm.setResults(new ArrayList<SalesTargetBean>());
			aForm.setBean(h);
			
		} catch (Exception e) {
			conn.rollback();
			
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailTTMKT");
	}
	
	public ActionForward updateStatusManualTT(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("updateStatusManual set status to:"+Utils.isNull(request.getParameter("status")));
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			//Update status head
			h.setStatus(Utils.isNull(request.getParameter("status")));
			h.setUpdateUser("GOD");
			SalesTargetTTDAO.updateStatusHeadByManual(conn, h);

			request.setAttribute("Message","อัพเดตข้อมูล Status to:"+Utils.isNull(request.getParameter("status"))+" เรียบร้อยแล้ว");
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward changeStatusTTByAdmin(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("changeStatusTTByAdmin");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setPeriod(aForm.getBean().getPeriod());
			h.setStartDate(aForm.getBean().getStartDate());
			h.setBrand(aForm.getBean().getBrand());
			h.setCustCatNo(aForm.getBean().getCustCatNo());
			h.setSalesZone(aForm.getBean().getSalesZone());
			
			//get parameter
			String[] brandChange = request.getParameterValues("brand_change");
			String[] statusChange = request.getParameterValues("status_change");
			
			if(brandChange.length>0){
				for(int i=0;i<brandChange.length;i++){
					if( !Utils.isNull(statusChange[i]).equals("")){
						
						h.setStatus(Utils.isNull(statusChange[i]));
						h.setBrand(Utils.isNull(brandChange[i]));
						h.setUpdateUser(user.getUserName());
						
						//update status TT
						SalesTargetTTDAO.updateStatusHead_TTByAdmin(conn, h);
						SalesTargetTTDAO.updateStatusItem_TTByAdmin(conn, h);
						
						//update status TEMP
						SalesTargetTTDAO.updateStatusHead_TEMPByTTADMIN(conn, h);
						SalesTargetTTDAO.updateStatusItem_TEMPByTTADMIN(conn, h);
					}
					
				}//for
			}
			conn.commit();
			
			request.setAttribute("Message","Change Status  เรียบร้อยแล้ว");
			
			SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTADMIN_TT(aForm.getBean(),user,aForm.getPageName());
			aForm.setBean(salesReuslt);
			if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
				 request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTADMIN(request,aForm.getBean(),user));
			}
		
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward postToSalesTT(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("postToSales By Marketing TT");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			h.setStatus(SalesTargetConstants.STATUS_POST);
			h.setUpdateUser(user.getUserName());
			
			//update status POST in TT
			SalesTargetTTDAO.updateStatusHead_TTByMKT(conn, h);
			SalesTargetTTDAO.updateStatusItem_TTByMKT(conn, h);
			
			//update status POST in TEMP
			SalesTargetTTDAO.updateStatusHead_TEMPByTTSUPER(conn, h);
			SalesTargetTTDAO.updateStatusItem_TEMPByTTSUPER(conn, h);
			
			request.setAttribute("Message","Post To Sales เรียบร้อยแล้ว");
			
			conn.commit();
			
			//search
			boolean getItems = true;
			h = SalesTargetTTDAO.searchSalesTargetTT(conn, h,getItems, user,aForm.getPageName());
			
			aForm.setBean(h);
			aForm.setResults(h.getItems());
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward salesAcceptToSalesManagerTT(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("salesAcceptToSalesManager By TTSUPER(Sales)");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			h.setStatus(SalesTargetConstants.STATUS_ACCEPT);
			h.setUpdateUser(user.getUserName());
			
			//update  (XXPENS_BI_SALES_TARGET_TEMP ,XXPENS_BI_SALES_TARGET_TT )  TABLE status POST to ACCEPT
			SalesTargetTTDAO.updateStatusHead_TEMPByTTSUPER(conn, h);
			SalesTargetTTDAO.updateStatusItem_TEMPByTTSUPER(conn, h);
	
			request.setAttribute("Message","ได้ทำการ Accept เป้าหมายขาย เรียบร้อยแล้ว");
			conn.commit();
			
			//search again
			//List<SalesTargetBean> productMKTList =(List<SalesTargetBean>)request.getSession().getAttribute("productMKTList");
			List<SalesTargetBean> salesrepList = (List<SalesTargetBean>)request.getSession().getAttribute("salesrepList");
			
			//Get target save By Sales(XXPENS_BI_SALES_TARGET_TEMP) to MAP
			//KEY store_code+itemCode
			List<Map> dataList = SalesTargetTTDAO.searchSalesTargetTempToMap(conn, aForm.getBean(), user);
			Map<String, String> rowMap = dataList.get(0);
			Map<String, SalesTargetBean> dataMap = dataList.get(1);
		
			//Get SalesList
		    salesrepList = SalesTargetTTDAO.searchSalesrepListByTTSUPER(conn, aForm.getBean(), user,rowMap);
	
			request.getSession().setAttribute("salesrepList", salesrepList);
			request.getSession().setAttribute("dataMap", dataMap);
			
			//get PriceListId
			h.setPriceListId(SalesTargetTTUtils.getPriceListId(conn, h.getSalesZone(), h.getCustCatNo(),user));
			
			//disable canSet and canAccept
			h.setCanSet(false);
			h.setCanAccept(false);
			
			aForm.setBean(h);
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailTTSUPER");
	}
	
	public ActionForward salesManagerFinishByTTMGR_TT(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("salesManagerFinish By TTMGR Manager");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean cri = new SalesTargetBean();
			cri.setPeriod(aForm.getBean().getPeriod());
			cri.setStartDate(aForm.getBean().getStartDate());
			cri.setStatus(SalesTargetConstants.STATUS_FINISH);
			cri.setUpdateUser(user.getUserName());
			
			//get parameter from input
			String[] custCatNo = request.getParameterValues("custCatNo");
			String[] brand = request.getParameterValues("brand");
			String[] zone = request.getParameterValues("zone");
			
			for(int i =0;i<custCatNo.length;i++){
				cri.setCustCatNo(custCatNo[i]);
				cri.setBrand(brand[i]);
				cri.setSalesZone(zone[i]);
				//update status FINISH
				//loop
				SalesTargetTTDAO.updateStatusHead_TEMPByTTMGR(conn, cri);
				SalesTargetTTDAO.updateStatusItem_TEMPByTTMGR(conn, cri);
				
				//TT for MKT
				SalesTargetTTDAO.updateStatusHead_TTByMKT(conn, cri);
			
			}
			request.setAttribute("Message","ได้ทำการ อนุมัติเป้าหมายขาย เรียบร้อยแล้ว");
			
			conn.commit();
			
			//search head
			SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTMGR_TT(aForm.getBean(),user,aForm.getPageName());
			aForm.setBean(salesReuslt);
		    request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTMGR(request,aForm.getBean(),user));

		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	

	
}
