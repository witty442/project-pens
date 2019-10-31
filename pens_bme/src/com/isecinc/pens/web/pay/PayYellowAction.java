package com.isecinc.pens.web.pay;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PayYellowDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PayYellowAction extends I_Action {

	public static int pageSize = 30;
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		PayYellowForm aForm = (PayYellowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				PayBean ad = new PayBean();
				ad.setCreateUser(user.getUserName());
				aForm.setBean(ad);
				
				//init session ListBox
				List<PopupForm> billTypeList = new ArrayList<PopupForm>();
				PopupForm ref = new PopupForm("",""); 
				billTypeList.add(ref);
				billTypeList.addAll(PayYellowDAO.searchDeptList(new PopupForm(),""));
				request.getSession().setAttribute("deptList",billTypeList);
				
			}else if("back".equals(action)){
				conn = DBConnection.getInstance().getConnection();
				
				PayBean cri  =aForm.getBeanCriteria();
				cri.setDocNo("");
				aForm.setBean(PayYellowDAO.searchHead(conn,cri,false,false,1,pageSize));
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
		PayYellowForm aForm = (PayYellowForm) form;
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
				aForm.setTotalRecord(PayYellowDAO.searchTotalHead(conn,aForm.getBean()));
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
				PayBean paybean = PayYellowDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
				List<PayBean> items = paybean.getItems();
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
			    PayBean paybean = PayYellowDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
				List<PayBean> items = paybean.getItems();
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
		PayYellowForm aForm = (PayYellowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new PayBean());
			
			PayBean ad = new PayBean();
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
		String forward = "detail";
		PayYellowForm aForm = (PayYellowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String docNo = Utils.isNull(request.getParameter("docNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare edit docNo:"+docNo +",mode:"+mode);
			if("copy".equalsIgnoreCase(mode)){
				//copy data 
				PayBean c = new PayBean();
				c.setDocNo(docNo);
				//PayBean bean = PayWhiteDAO.searchHead(c,true).getItems().get(0);
				PayBean bean = PayYellowDAO.searchHead(c,true,false,1,pageSize).getItems().get(0);
				
				bean.setDocDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setDocNo("");
				
				List<PayBean> items = new ArrayList<PayBean>();
				for(int i=0;i<bean.getItems().size();i++){
					PayBean item = bean.getItems().get(i);
					item.setLineId(0);
					items.add(item);
				}
				bean.setItems(items);
				aForm.setBean(bean);
			}else{
				PayBean c = new PayBean();
				c.setDocNo(docNo);
				if( !Utils.isNull(docNo).equals("")){
				  //PayBean bean = PayWhiteDAO.searchHead(c,true).getItems().get(0);
				  PayBean bean = PayYellowDAO.searchHead(c,true,false,1,pageSize).getItems().get(0);
				  aForm.setBean(bean);
				}else{
				   c.setDocDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   aForm.setBean(c);
				}
			}
			aForm.setMode(mode);//Mode Edit
			
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
		PayYellowForm summaryForm = (PayYellowForm) form;
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
		PayYellowForm orderForm = (PayYellowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			PayBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			//orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
			request.setAttribute("Message", msg);
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
		PayYellowForm aForm = (PayYellowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PayBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(STATUS_SAVE);
			if( !Utils.isNull(h.getDocNo()).equals("")){
				int update = PayYellowDAO.updateDocTran(conn, h);
				if(update==0){
				   PayYellowDAO.insertDocTran(conn, h);
				}
			}else{
				//Get DocNo
				h.setDocNo(PayYellowDAO.genDocNo(new Date()));
				PayYellowDAO.insertDocTran(conn, h);
			}
			
			//Items
			String[] lineId =request.getParameterValues("lineId");
			String[] accountName =request.getParameterValues("accountName");
			String[] description =request.getParameterValues("description");
			String[] amount =request.getParameterValues("amount");

			for(int i=0;i<6;i++){
				PayBean item = new PayBean();

				item.setLineId(Utils.convertStrToInt(lineId[i]));
				item.setAccountName(Utils.isNull(accountName[i]));
				item.setDescription(Utils.isNull(description[i]));
				item.setAmount(Utils.isNull(amount[i]));
				
				item.setDocNo(h.getDocNo());
				item.setCreateUser(user.getUserName());
				item.setUpdateUser(user.getUserName());
				
				if(item.getLineId()==0){
					//insert
					item.setLineId(i+1);
					PayYellowDAO.insertDocTranDetail(conn, item);
				}else{
					//update
					PayYellowDAO.updateDocTranDetail(conn, item);
				}
				
			}
			
			//Search Again
			PayBean bean = PayYellowDAO.searchHead(conn,h,true,false,1,pageSize).getItems().get(0);
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
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
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PayYellowForm aForm = (PayYellowForm) form;
		try {
			aForm.setResults(new ArrayList<PayBean>());
			PayBean ad = new PayBean();
			ad.setDocDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
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
		
		logger.debug(" Print PayIn Yellow Report ");
		PayYellowForm reportForm = (PayYellowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
			String docType ="copy".equalsIgnoreCase(Utils.isNull(request.getParameter("docType")))?"(สำเนา)":"(ต้นฉบับ)";
			
			//Choose Printer By User Case Printer default offline
			String printerName = Utils.isNull(request.getParameter("printerName"));
			if( !Utils.isNull(printerName).equals("")){
				logger.info("Printer Choose by user:"+printerName);
				user.setPrinterName(printerName);
				request.getSession().setAttribute("user",user);
			}
			logger.info("Print["+user.getUserName()+"] Date["+new Date()+"]printer["+user.getPrinterName()+"]");
			
			conn = DBConnection.getInstance().getConnection();
			PayYellowForm aForm = (PayYellowForm) form;
			String fileType = SystemElements.PRINTER;
			logger.debug("fileType:"+fileType);
			
			//Search Again
			String docNo = Utils.isNull(request.getParameter("docNo"));
			PayBean cri = new PayBean();
			cri.setDocNo(docNo);
			
			PayBean h = PayYellowDAO.searchHead(conn,cri,true,false,1,pageSize).getItems().get(0);
			logger.debug("result:"+h.getDocNo());
			
			if(h != null){
				
				parameterMap.put("docDate", h.getDocDate());
				parameterMap.put("docNo", h.getDocNo());
				parameterMap.put("docType", docType);
				
				parameterMap.put("employeeName", Utils.isNull(h.getEmployeeName()));
				parameterMap.put("deptName", Utils.isNull(h.getDeptName()));
				parameterMap.put("sectionName", Utils.isNull(h.getSectionName()));
				parameterMap.put("totalAmountLetter", Utils.isNull(h.getTotalAmountLetter()));
				parameterMap.put("createUser", Utils.isNull(h.getCreateUser()));
				
				logger.debug("totalAmountLetter["+Utils.isNull(h.getTotalAmountLetter())+"]");
				parameterMap.put("totalAmount", h.getTotalAmount());
				
				//Items
				if(h.getItems() != null &&  h.getItems().size() >0){
					for(int i=0;i<h.getItems().size();i++){
						PayBean item = h.getItems().get(i);
						parameterMap.put("accountName"+(i+1), Utils.isNull(item.getAccountName()));
						parameterMap.put("description"+(i+1), Utils.isNull(item.getDescription()));
						parameterMap.put("amount"+(i+1), Utils.isNull(item.getAmount()) );
					}//for
				}//if
				
				//Gen Report
				String fileName = "pay_in_yellow_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				//for test
				//fileType = "PDF";
				
				logger.debug("start report Real 1");
				parameterMap.put("docType", "(ต้นฉบับ)");
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,h.getItems());
				
				logger.debug("start report Copy 2");
				parameterMap.put("docType", "(สำเนา)");
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,h.getItems());
				//new 
				request.setAttribute("printCopy", "printSuccess");
				
				/*
				//set printer success
				if("".equalsIgnoreCase(Utils.isNull(request.getParameter("docType")))){
					//logger.debug("Print Report PayIn Main 1");
					request.setAttribute("printCopy", "printCopy");
				}else{
					//logger.debug("Print Report PayIn Copy 2");
					request.setAttribute("printCopy", "printSuccess");
				}*/
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
				return  mapping.findForward("detail");
			}
		} catch (Exception e) {
			logger.info("Print report PayIn Error");
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
