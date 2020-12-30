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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PayWhiteDAO;
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
public class PayWhiteAction extends I_Action {

	public static int pageSize = 30;
	public static String STATUS_SAVE ="SV";
	public static int maxRow = 16;
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		PayWhiteForm aForm = (PayWhiteForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				//Set User by Login User 
				PayBean ad = new PayBean();
				ad.setCreateUser(user.getUserName());
				aForm.setBean(ad);
				
				//init session ListBox
				List<PopupForm> billTypeList = new ArrayList<PopupForm>();
				PopupForm ref = new PopupForm("",""); 
				billTypeList.add(ref);
				billTypeList.addAll(PayWhiteDAO.searchDeptList(new PopupForm(),""));
				request.getSession().setAttribute("deptList",billTypeList);

			}else if("back".equals(action)){
				conn = DBConnection.getInstance().getConnection();
				
				PayBean cri  =aForm.getBeanCriteria();
				cri.setDocNo("");
				aForm.setBean(PayWhiteDAO.searchHead(conn,cri,false,false,1,pageSize));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		PayWhiteForm aForm = (PayWhiteForm) form;
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
				aForm.setPageSize(pageSize);
				
				//get Total Record
				aForm.setTotalRecord(PayWhiteDAO.searchTotalHead(conn,aForm.getBean()));
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
				PayBean paybean = PayWhiteDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
				List<PayBean> items = paybean.getItems();
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "��辺������");
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
			    PayBean paybean = PayWhiteDAO.searchHead(conn,aForm.getBean(),false,allRec,currPage,pageSize);
				List<PayBean> items = paybean.getItems();
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
		PayWhiteForm aForm = (PayWhiteForm) form;
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
		PayWhiteForm aForm = (PayWhiteForm) form;
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
				PayBean bean = PayWhiteDAO.searchHead(c,true,false,1,pageSize).getItems().get(0);
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
				  PayBean bean = PayWhiteDAO.searchHead(c,true,false,1,pageSize).getItems().get(0);
				  aForm.setBean(bean);
				}else{
				   c.setDocDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   aForm.setBean(c);
				}
			}
			aForm.setMode(mode);//Mode Edit
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
		PayWhiteForm summaryForm = (PayWhiteForm) form;
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
		PayWhiteForm orderForm = (PayWhiteForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			PayBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			//orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
			request.setAttribute("Message", msg);
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
		Connection conn = null;
		PayWhiteForm aForm = (PayWhiteForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PayBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(STATUS_SAVE);
			
			logger.debug("cashFlag ="+Utils.isNull(h.getCashFlag()));
			logger.debug("chequeFlag ="+Utils.isNull(h.getChequeFlag()));
			
			if( !Utils.isNull(h.getCashFlag()).equals("")){
				h.setPaymethod("C");
			}
			
			if( !Utils.isNull(h.getChequeFlag()).equals("")){
				h.setPaymethod("CH");
			}
			
			if( !Utils.isNull(h.getDocNo()).equals("")){
				int update = PayWhiteDAO.updateDocTran(conn, h);
				if(update==0){
				   PayWhiteDAO.insertDocTran(conn, h);
				}
			}else{
				//Get DocNo
				h.setDocNo(PayWhiteDAO.genDocNo(new Date()));
				PayWhiteDAO.insertDocTran(conn, h);
			}
			
			//Items
			String[] lineId =request.getParameterValues("lineId");
			String[] accountName =request.getParameterValues("accountName");
			String[] description =request.getParameterValues("description");
			String[] amount =request.getParameterValues("amount");

			for(int i=0;i<maxRow;i++){
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
					PayWhiteDAO.insertDocTranDetail(conn, item);
				}else{
					//update
					PayWhiteDAO.updateDocTranDetail(conn, item);
				}
				
			}
			
			//Search Again
			//PayBean bean = PayWhiteDAO.searchHead(conn,h,true).getItems().get(0);
			PayBean bean = PayWhiteDAO.searchHead(conn,h,true,false,1,pageSize).getItems().get(0);
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "�ѹ�֡���������º��������");
			
			String actionFlag = Utils.isNull(request.getParameter("actionFlag"));
			if(actionFlag.equals("saveAndPrint")){
				request.setAttribute("saveAndPrint", "saveAndPrint");
			}
		} catch (Exception e) {
			conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
		PayWhiteForm aForm = (PayWhiteForm) form;
		try {
			aForm.setResults(new ArrayList<PayBean>());
			
			PayBean ad = new PayBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//ad.setCanEdit(true);
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
		
		logger.debug(" Print PayIn report ");
		PayWhiteForm reportForm = (PayWhiteForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		String sectionName= "";
		try {
			//Choose Printer By User Case Printer default offline
			String printerName = Utils.isNull(request.getParameter("printerName"));
			if( !Utils.isNull(printerName).equals("")){
				logger.info("Printer Choose by user:"+printerName);
				user.setPrinterName(printerName);
				request.getSession().setAttribute("user",user);
			}
			logger.info("Print["+user.getUserName()+"] Date["+new Date()+"]printer["+user.getPrinterName()+"]");
			
			conn = DBConnection.getInstance().getConnection();
			PayWhiteForm aForm = (PayWhiteForm) form;
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);
			
			//Search Again
			String docNo = Utils.isNull(request.getParameter("docNo"));
			PayBean cri = new PayBean();
			cri.setDocNo(docNo);
			
			PayBean h = PayWhiteDAO.searchHead(conn,cri,true,false,1,pageSize).getItems().get(0);
			logger.debug("result:"+h.getDocNo());
			
			if(h != null){
				
				parameterMap.put("companyName","����ѷ ྐྵ�� �����絵�� �͹�� ��ʷ�Ժ�Ǫ�� ��ҡѴ");
				parameterMap.put("docNo",h.getDocNo());
				parameterMap.put("remark",h.getRemark());
				// 22/10/2558
			    String dd = h.getDocDate().substring(0,2);
			    String MM = h.getDocDate().substring(3,5);
			    String yyyy = h.getDocDate().substring(6,10);
			    
			    
			    		
				parameterMap.put("docDateDD", Utils.isNull(dd));
				parameterMap.put("docDateMM", Utils.isNull(MM));
				parameterMap.put("docDateYYYY", Utils.isNull(yyyy));
				
				 //Case Print White Doc Replace word "Ἱ�"
				if( !Utils.isNull(h.getSectionName()).equals("")){
				   sectionName = Utils.isNull(Utils.isNull(h.getSectionName()));
				   sectionName = Utils.isNull(sectionName.replaceAll("Ἱ�", ""));   
				 }
				parameterMap.put("payInName", Utils.isNull(h.getPayToName()));
				parameterMap.put("deptName", sectionName+"  /  "+Utils.isNull(h.getDeptName()));
				parameterMap.put("sectionName",sectionName );
				parameterMap.put("totalAmountLetter", Utils.isNull(h.getTotalAmountLetter()));
				parameterMap.put("createUser", Utils.isNull(h.getCreateUser()));
				
				logger.debug("totalAmountLetter["+Utils.isNull(h.getTotalAmountLetter())+"]");
			
				parameterMap.put("totalAmount", h.getTotalAmount());
				//*********************DR***************************************//
				parameterMap.put("DR_AC_NO", Utils.isNull(h.getDR_AC_NO()));
				parameterMap.put("DR_DESC", Utils.isNull(h.getDR_DESC()));
				
				String[] temp = Utils.isNull(h.getDR_AMOUNT()).split("\\.");
				parameterMap.put("DR_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("DR_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("DR_AMOUNT_DIGIT", "");
				}
				
				temp = Utils.isNull(h.getDR_INPUT_TAX_AMOUNT()).split("\\.");
				parameterMap.put("DR_INPUT_TAX_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("DR_INPUT_TAX_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("DR_INPUT_TAX_AMOUNT_DIGIT", "");
				}
				temp = Utils.isNull(h.getDR_TOTAL()).split("\\.");
				parameterMap.put("DR_TOTAL", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("DR_TOTAL_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("DR_TOTAL_DIGIT", "");
				}
				//********************CR*************************************//
				parameterMap.put("CR_AC_NO", Utils.isNull(h.getCR_AC_NO()));
				parameterMap.put("CR_DESC", Utils.isNull(h.getCR_DESC()));
				
				temp = Utils.isNull(h.getCR_AMOUNT()).split("\\.");
				parameterMap.put("CR_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("CR_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("CR_AMOUNT_DIGIT", "");
				}
				
				temp = Utils.isNull(h.getCR_ACC_WT_TAX_AMOUNT()).split("\\.");
				parameterMap.put("CR_ACC_WT_TAX_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("CR_ACC_WT_TAX_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("CR_ACC_WT_TAX_AMOUNT_DIGIT", "");
				}
				temp = Utils.isNull(h.getCR_TOTAL()).split("\\.");
				parameterMap.put("CR_TOTAL", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("CR_TOTAL_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("CR_TOTAL_DIGIT", "");
				}
			    
				//add blank to MaxRow
				List<PayBean> rowItemList = h.getItems();
				if(rowItemList.size() < maxRow){
					int diff = maxRow-rowItemList.size();
					for(int i=0;i<diff;i++){
						PayBean rowBlank = new PayBean();
						rowBlank.setAccountName("");
						rowBlank.setDescription("");
						rowBlank.setTotalAmount("");
						rowItemList.add(rowBlank);
					}
				}
				logger.debug("maxrow["+maxRow+"]ItemList Size["+rowItemList.size()+"]");
				//Gen Report
				String fileName = "pay_in_white_pdf_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				logger.debug("start report");
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,rowItemList);
				
				//set printer success
				request.setAttribute("printerSuccess", "printerSuccess");
				
				logger.info("Print report PayInWhite Success");
			}
		} catch (Exception e) {
			logger.info("Print report PayInWhite Error");
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
