package com.isecinc.pens.web.itmanage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ITStockAction extends I_Action {

	public static int pageSize = 30;
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ITManageForm aForm = (ITManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				ITManageBean ad = new ITManageBean();
				ad.setCreateUser(user.getUserName());
				aForm.setBean(ad);
			    
				/*//init itemNameList
				request.getSession().setAttribute("ITEM_NAME_LIST", ITStockDAO.initItemList());*/
			}else if("back".equals(action)){
				conn = DBConnection.getInstance().getConnection();
				
				ITManageBean cri  =aForm.getBeanCriteria();
				//cri.setDocNo("");
				aForm.setBean(ITStockDAO.searchHead(conn,cri,false,false,1,pageSize));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		ITManageForm aForm = (ITManageForm) form;
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
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(ITStockDAO.searchTotalHead(conn,aForm.getBean()));
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
				ITManageBean paybean = ITStockDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
				List<ITManageBean> items = paybean.getItems();
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
			    ITManageBean paybean = ITStockDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
				List<ITManageBean> items = paybean.getItems();
				aForm.setResultsSearch(items);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
		ITManageForm aForm = (ITManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new ITManageBean());
			
			ITManageBean ad = new ITManageBean();
			ad.setCreateUser(user.getUserName());
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
		String forward = "detailITStockItem";
		ITManageForm aForm = (ITManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String id = Utils.isNull(request.getParameter("id"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare edit id:"+id +",mode:"+mode);
			if("copy".equalsIgnoreCase(mode)){
				//copy data 
				ITManageBean c = new ITManageBean();
				c.setId(Utils.convertStrToInt(id));
				ITManageBean bean = ITStockDAO.searchHead(c,true,false,1,pageSize).getItems().get(0);
				
				bean.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setId(0);;
				
				List<ITManageBean> items = new ArrayList<ITManageBean>();
				for(int i=0;i<bean.getItems().size();i++){
					ITManageBean item = bean.getItems().get(i);
					item.setLineId(0);
					items.add(item);
				}
				bean.setItems(items);
				aForm.setBean(bean);
			}else{
				ITManageBean c = new ITManageBean();
				c.setId(Utils.convertStrToInt(id));
				if( Utils.convertStrToInt(id) != 0){
				   ITManageBean bean = ITStockDAO.searchHead(c,true,false,1,pageSize).getItems().get(0);
				   aForm.setBean(bean);
				}else{
				   c.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   aForm.setBean(c);
				}
			}
			aForm.setMode(mode);//Mode Edit
			
			saveToken(request);	

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ITManageForm summaryForm = (ITManageForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "detail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ITManageForm orderForm = (ITManageForm) form;
		try {
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ITManageForm aForm = (ITManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			// check Token in Save Action
			if (!isTokenValid(request)) {
			    logger.debug("Token invalid");
			    request.setAttribute("Message", "ไม่สามารถทำรายการต่อได้  เนื่องจากมีการกด Back กรุณากดเข้าหน้า เมนูหลัก แล้วเข้าทำรายใหม่อีกครั้ง");
			    return "detailITStockItem";
			}
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ITManageBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			if( h.getId() != 0){
				logger.debug("update Id:"+h.getId());
				ITStockDAO.updateITStock(conn, h);
			}else{
				//Get NEW ID
				h.setId(SequenceProcess.getNextValue("IT_MANAGE_ITEM"));
				logger.debug("insert Id:"+h.getId());
				ITStockDAO.insertITStock(conn, h);
			}
			
			//Items
			String[] lineId =request.getParameterValues("lineId");
			String[] itemName =request.getParameterValues("itemName");
			String[] serialNo =request.getParameterValues("serialNo");
			String[] qty =request.getParameterValues("qty");
			String[] remark =request.getParameterValues("remark");
			
			//delete all item by head ID
			ITStockDAO.deleteITStockItem(conn, h.getId());
			
			for(int i=0;i<5;i++){
				ITManageBean item = new ITManageBean();

				item.setLineId(Utils.convertStrToInt(lineId[i]));
				item.setItemName(Utils.isNull(itemName[i]));
				item.setSerialNo(Utils.isNull(serialNo[i]));
				item.setQty(Utils.isNull(qty[i]));
				item.setRemark(Utils.isNull(remark[i]));
				
				item.setId(h.getId());
				item.setCreateUser(user.getUserName());
				item.setUpdateUser(user.getUserName());
				
				if( !Utils.isNull(item.getItemName()).equals("")){
					//insert
					item.setLineId(i+1);
					ITStockDAO.insertITStockItem(conn, item);
				}
			}
			
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//Search Again
			ITManageBean bean = ITStockDAO.searchHead(conn,h,true,false,1,pageSize).getItems().get(0);
		    aForm.setBean(bean);
			
			String actionFlag = Utils.isNull(request.getParameter("actionFlag"));
			if(actionFlag.equals("saveAndPrint")){
			   request.setAttribute("saveAndPrint", "saveAndPrint");
			}
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detailITStockItem";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detailITStockItem";
	}
	
	public ActionForward deleteAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
			logger.debug(" deleteAction");
			Connection conn = null;
			ITManageForm aForm = (ITManageForm) form;
			try {
				String id = Utils.isNull(request.getParameter("ids"));
				logger.debug("id:"+id);
				if(Utils.convertToInt(id) != 0){
					 conn = DBConnection.getInstance().getConnection();
					
					 ITStockDAO.deleteITStock(conn, Utils.convertToInt(id));
					 ITStockDAO.deleteITStockItem(conn, Utils.convertToInt(id));
					
					 request.setAttribute("Message", "ลบรายการเรียบร้อยแล้ว");
				    
					 //set for new insert
					 ITManageBean c = new ITManageBean();
					 c.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 aForm.setBean(c);
				     aForm.setMode("add");//Mode Edit
				
				     saveToken(request);	
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("Message", e.getMessage());
			} finally {
				try {
					 conn.close();
				} catch (Exception e2) {
					
				}
			}
			return  mapping.findForward("detailITStockItem");
	}
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		
		logger.debug(" Print report ");
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		Connection conn = null;
		try {
	
			conn = DBConnection.getInstance().getConnectionApps();
			ITManageForm aForm = (ITManageForm) form;
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);
			
			//Search Again
			String docNo = Utils.isNull(request.getParameter("docNo"));
			ITManageBean cri = new ITManageBean();
			cri.setId(aForm.getBean().getId());
			ITManageBean h = ITStockDAO.searchHead(conn,cri,true,true,1,pageSize).getItems().get(0);
			logger.debug("result:"+h.getId());
			
			if(h != null){
				parameterMap.put("docDate", h.getDocDate());				
				parameterMap.put("docType", h.getDocType());	
				parameterMap.put("salesrepCode", h.getSalesrepCode());	
				parameterMap.put("salesrepFullName", h.getSalesrepFullName());	
				parameterMap.put("zoneName", h.getZoneName());	
				
				if(h.getItems() != null && h.getItems().size() <5){
					ITManageBean bean = h.getItems().get(h.getItems().size()-1);
					int lineId = bean.getLineId();
					int diff = (5-h.getItems().size());
					logger.debug("diff:"+diff);
					for(int i=0;i<diff;i++){
						 bean = new ITManageBean();
						 lineId++;
						 bean.setLineId(lineId);
						 bean.setItemName("");
						 bean.setSerialNo("");
						 bean.setQty("");
						 bean.setRemark("");
						 h.getItems().add(bean);
					}
				}

				//Gen Report
				String fileName = "it_stock_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				logger.debug("start report");
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,h.getItems());
				
				//set printer success
				request.setAttribute("printerSuccess", "printerSuccess");
				logger.info("Print report PayIn Success");
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล ");
				return  mapping.findForward("detail");
			}
		} catch (Exception e) {
			logger.info("Print report Error");
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {
				
			}
		}
		return  mapping.findForward("printPayPopup");
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
