package com.isecinc.pens.web.stockinv;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.web.autokeypress.AutoKeypressDAO;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockInvAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();

	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockInvForm aForm = (StockInvForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				aForm.setResults(null);
				StockInvBean ad = new StockInvBean();
				ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));//default Current date
				
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		StockInvForm aForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
					aForm.getBean().setHeaderId(0);
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(StockInvDAO.getTotalRecList(conn,aForm.getBean()));
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
			    List<StockInvBean> items = StockInvDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResultsSearch(null);
				}
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
			    List<StockInvBean> items = StockInvDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
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
		return mapping.findForward("search");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		StockInvForm aForm = (StockInvForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new StockInvBean());
			
			StockInvBean ad = new StockInvBean();
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		StockInvForm aForm = (StockInvForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	public ActionForward prepareInitStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareInitStock");
		StockInvForm aForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			aForm.setBean(new StockInvBean());//clear
			
		    String mode = Utils.isNull(request.getParameter("mode"));
		    String headerId = Utils.isNull(request.getParameter("headerId"));
		    String transType = Utils.isNull(request.getParameter("transType"));
		    if("add".equalsIgnoreCase(mode)){
		    	aForm.getBean().setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
		    	aForm.getBean().setCanEdit(true);
		    	aForm.getBean().setHeaderId(0);
		    	aForm.getBean().setTransType(transType);
		    	aForm.setResults(new ArrayList<StockInvBean>());
		    }else  if("edit".equalsIgnoreCase(mode)){
		    	aForm.getBean().setCanEdit(true);
		    	aForm.getBean().setHeaderId(Utils.convertToLong(headerId));
		    	aForm.getBean().setTransType(transType);
		    	
				StockInvBean r = StockInvDAO.searchInitDetailList(conn, aForm.getBean());
				List<StockInvBean> results = r.getItems();
			
				aForm.setBean(r);
		    	aForm.setResults(results);
		    	
		    	logger.debug("canEdit:"+aForm.getBean().isCanEdit());
		    }else  if("view".equalsIgnoreCase(mode)){
		    	aForm.getBean().setHeaderId(Utils.convertToLong(headerId));
		    	aForm.getBean().setTransType(transType);
		    	
		    	StockInvBean r = StockInvDAO.searchInitDetailList(conn, aForm.getBean());
		    	List<StockInvBean> results = r.getItems();
		    	aForm.setBean(r);
		    	aForm.getBean().setCanEdit(false);
		    	aForm.setResults(results);
		    }
		    aForm.setMode(mode);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
		return mapping.findForward("stockInvDetail");
	}
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StockInvForm summaryForm = (StockInvForm) form;
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
		StockInvForm orderForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockInvForm aForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {

		} catch (Exception e) {
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "prepare";
		} finally {
		}
		return "search";
	}
	
	public ActionForward saveInitStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveInitStock");
		StockInvForm aForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			
			StockInvBean h = aForm.getBean();
			logger.debug("SCreen headerId:"+h.getHeaderId());
			h.setCreateUser(user.getId()+"");
			h.setUpdateUser(user.getId()+"");
			
			List<StockInvBean> itemList = new ArrayList<StockInvBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productId = request.getParameterValues("productId");
			String[] productCode = request.getParameterValues("productCode");
			String[] uom1 = request.getParameterValues("uom1");
			String[] uom2 = request.getParameterValues("uom2");
			String[] qty1 = request.getParameterValues("qty1");
			String[] qty2 = request.getParameterValues("qty2");
			String[] status = request.getParameterValues("status");
			
			logger.debug("productCode:"+productCode.length);
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					logger.debug("productCode:"+productCode[i]+",status:"+status[i]);
					logger.debug("qty1:"+qty1[i]+",qty2:"+qty2[i]);
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")
						&& (!Utils.isNull(qty1[i]).equals("") || !Utils.isNull(qty2[i]).equals("") )
					 ){
						 StockInvBean l = new StockInvBean();
						 l.setLineId(Utils.convertToLong(lineId[i]));
						 l.setProductId(Utils.isNull(productId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setUom(Utils.isNull(uom1[i]));//transaction_uom
						 l.setUom1(Utils.isNull(uom1[i]));
						 l.setUom2(Utils.isNull(uom2[i]));
						 l.setQty1(Utils.isNull(qty1[i]));
						 l.setQty2(Utils.isNull(qty2[i]));
						 l.setStatus(Utils.isNull(status[i]));
						 l.setCreateUser(user.getId()+"");
						 l.setUpdateUser(user.getId()+"");
						 itemList.add(l);
					}//if
				}//for
			}//if
			
			h.setItems(itemList);
	
			//save
			long id = StockInvDAO.saveInitStock(h);
			h.setHeaderId(id);
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//search
			h = StockInvDAO.searchInitDetailList(conn, h);
			List<StockInvBean> results = h.getItems();
			h.setCanConfirm(true);
			//logger.debug("h:"+h+",items:"+results.size());
			
			aForm.setBean(h);
			aForm.setResults(results);
			
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return mapping.findForward("stockInvDetail");
		} finally {
			conn.close();
		}
		return mapping.findForward("stockInvDetail");
	}
	
	public ActionForward confirmInitStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmInitStock");
		StockInvForm aForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			//Save
			StockInvBean h = aForm.getBean();
			logger.debug("SCreen headerId:"+h.getHeaderId());
			h.setCreateUser(user.getId()+"");
			h.setUpdateUser(user.getId()+"");
			
			List<StockInvBean> itemList = new ArrayList<StockInvBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productId = request.getParameterValues("productId");
			String[] productCode = request.getParameterValues("productCode");
			String[] uom1 = request.getParameterValues("uom1");
			String[] uom2 = request.getParameterValues("uom2");
			String[] qty1 = request.getParameterValues("qty1");
			String[] qty2 = request.getParameterValues("qty2");
			String[] status = request.getParameterValues("status");
			
			logger.debug("productCode:"+productCode.length);
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")
						&& (!Utils.isNull(qty1[i]).equals("") || !Utils.isNull(qty2[i]).equals("") )
					){
						 StockInvBean l = new StockInvBean();
						 l.setLineId(Utils.convertToLong(lineId[i]));
						 l.setProductId(Utils.isNull(productId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setUom(Utils.isNull(uom1[i]));//transaction_uom
						 l.setUom1(Utils.isNull(uom1[i]));
						 l.setUom2(Utils.isNull(uom2[i]));
						 l.setQty1(Utils.isNull(qty1[i]));
						 l.setQty2(Utils.isNull(qty2[i]));
						 l.setStatus(Utils.isNull(status[i]));
						 l.setCreateUser(user.getId()+"");
						 l.setUpdateUser(user.getId()+"");
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			//Store in Session
			aForm.setResults(itemList);
			//save
			long id = StockInvDAO.saveInitStock(conn,h);
			h.setHeaderId(id);
			
			//clear intflag
			StockInvDAO.clearStatusStockInitModel(conn,aForm.getBean().getHeaderId());
			
			//confirm Init Stock
			StockInvDAO.confirmInitStock(conn,aForm.getBean().getHeaderId());
			
			request.setAttribute("msgList", null);
			//check stock init status
			List<String> msgList = StockInvDAO.checkStockInitStatus(conn, aForm.getBean());
			if(msgList != null && msgList.size() >0){
				request.setAttribute("Message", "ไม่สามารถ Confirm Initial Stock กรุณาแก้รายการ แล้วทำการ Confirm ใหม่");
				request.setAttribute("msgList", msgList);
			}else{
				request.setAttribute("Message" ,"ยืนยัน ข้อมูลเรียบร้อบแล้ว");
			}
			
			//search
			StockInvBean r = StockInvDAO.searchInitDetailList(conn, h);
			List<StockInvBean> results = r.getItems();
			h.setCanConfirm(true);
			//logger.debug("h:"+h+",items:"+results.size());
			
			aForm.setBean(h);
			aForm.setResults(results);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
		return mapping.findForward("stockInvDetail");
	}
	public ActionForward initProductTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("initProductTable");
		StockInvForm aForm = (StockInvForm) form;
		Map<String, String> productMap = new HashMap<String, String>();
		int index = 0;
		Connection conn = null;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			
			//Save Old data in screen 
			List<StockInvBean> itemList = new ArrayList<StockInvBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] productId = request.getParameterValues("productId");
			String[] productCode = request.getParameterValues("productCode");
			String[] productName = request.getParameterValues("productName");
			String[] uom = request.getParameterValues("uom");
			String[] uom1 = request.getParameterValues("uom1");
			String[] uom2 = request.getParameterValues("uom2");
			String[] qty1 = request.getParameterValues("qty1");
			String[] qty2 = request.getParameterValues("qty2");
			String[] status = request.getParameterValues("status");
			logger.debug("productCode:"+productCode.length);
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					if( !Utils.isNull(productCode[i]).equals("") && !Utils.isNull(status[i]).equals("DELETE")){
						 StockInvBean l = new StockInvBean();
						 l.setLineId(Utils.convertToLong(lineId[i]));
						 l.setProductId(Utils.isNull(productId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setProductName(Utils.isNull(productName[i]));
						 l.setUom(Utils.isNull(uom[i]));
						 l.setUom1(Utils.isNull(uom1[i]));
						 l.setUom2(Utils.isNull(uom2[i]));
						 l.setQty1(Utils.isNull(qty1[i]));
						 l.setQty2(Utils.isNull(qty2[i]));
						 l.setStatus(Utils.isNull(status[i]));
						 if(Utils.isNull(l.getStatus()).equals("SUCCESS")){
						    productMap.put(l.getProductCode(), "SUCCESS"); //Exception no merge
						    l.setQty1Readonly("readonly");
							l.setQty1Style("disableNumber");
							l.setQty2Readonly("readonly");
							l.setQty2Style("disableNumber");
						 }else{
							productMap.put(l.getProductCode(), index+""); 
							l.setQty1Readonly("");
							l.setQty1Style("enableNumber");
							l.setQty2Readonly("");
							l.setQty2Style("enableNumber");
							if(Utils.isNull(l.getUom2()).equals("")){
								l.setQty2Readonly("readonly");
								l.setQty2Style("disableNumber");
							}
						 }
						 itemList.add(l);
						 index++;
					}//if
				}//for
			}//if
			String productCodeSelect[] = Utils.isNull(request.getParameter("productCodeSelect")).split("\\,");
			logger.debug("productCodeSelect:"+productCodeSelect);
			//merg to prevDataTable
			String indexS = "";
			StockInvBean newLine = null;
			if(productCodeSelect != null && productCodeSelect.length >0){
				for(int i=0;i<productCodeSelect.length;i++){
					if( !Utils.isNull(productCodeSelect[i]).equals("") 
						&& productMap.get(Utils.isNull(productCodeSelect[i])) == null){
						
						newLine = new StockInvBean();
						//set criteria
						PopupBean criteriaForm = new PopupBean();
						criteriaForm.setCodeSearch(Utils.isNull(productCodeSelect[i]));
						PopupBean p = AutoKeypressDAO.searchProduct(criteriaForm);
						if(p != null){
							newLine.setNo(index);
							newLine.setProductId(Utils.isNull(p.getProductId()));
							newLine.setProductCode(Utils.isNull(p.getCode()));
							newLine.setUom(Utils.isNull(p.getUom1())+"/"+Utils.isNull(p.getUom2()));
							newLine.setUom1(Utils.isNull(p.getUom1()));
							newLine.setUom2(Utils.isNull(p.getUom2()));
							newLine.setProductName(p.getDesc());
							newLine.setQty1Readonly("");
							newLine.setQty1Style("enableNumber");
							if(Utils.isNull(newLine.getUom2()).equals("")){
								newLine.setQty2Readonly("readonly");
								newLine.setQty2Style("disableNumber");
							}
							itemList.add(newLine);
							index++;
						}
					}//if
				}//for
			}//if
			aForm.setResults(itemList);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
		return mapping.findForward("stockInvDetail");
	}
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		StockInvForm aForm = (StockInvForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			StringBuffer dataOut = StockInvDAO.genExportStockInitDetail(aForm.getBean(),user);
			if(dataOut != null){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(dataOut.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("stockInvDetail");
	}
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StockInvForm aForm = (StockInvForm) form;
		try {
			aForm.setResults(new ArrayList<StockInvBean>());
			
			StockInvBean ad = new StockInvBean();
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward clearInitStock(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearInitStock");
		StockInvForm aForm = (StockInvForm) form;
		try {
			aForm.setResults(new ArrayList<StockInvBean>());
			StockInvBean ad = new StockInvBean();
			ad.setCanEdit(true);
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
			aForm.setMode("add");//Mode Add new
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("stockInvDetail");
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
