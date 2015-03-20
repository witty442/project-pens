package com.isecinc.pens.web.pay;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.Constants;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PayDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PayAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		PayForm aForm = (PayForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				PayBean ad = new PayBean();
				ad.setCreateUser(user.getUserName());
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				PayBean cri  =aForm.getBeanCriteria();
				cri.setDocNo("");
				aForm.setBean(PayDAO.searchHead(cri,false));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		PayForm aForm = (PayForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			PayBean b = aForm.getBean();
			aForm.setBean(PayDAO.searchHead(aForm.getBean(),false));
			aForm.setResultsSearch(aForm.getBean().getItems());

			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		PayForm aForm = (PayForm) form;
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
		PayForm aForm = (PayForm) form;
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
				PayBean bean = PayDAO.searchHead(c,true).getItems().get(0);
				bean.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
				  PayBean bean = PayDAO.searchHead(c,true).getItems().get(0);
				  aForm.setBean(bean);
				}else{
				   c.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
		PayForm summaryForm = (PayForm) form;
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
		PayForm orderForm = (PayForm) form;
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
		PayForm aForm = (PayForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PayBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(STATUS_SAVE);
			
			if( !Utils.isNull(h.getDocNo()).equals("")){
				int update = PayDAO.updateDocTran(conn, h);
				if(update==0){
				   PayDAO.insertDocTran(conn, h);
				}
			}else{
				//Get DocNo
				h.setDocNo(PayDAO.genDocNo(new Date()));
				PayDAO.insertDocTran(conn, h);
			}
			
			//Items
			String[] lineId =request.getParameterValues("lineId");
			String[] accountName =request.getParameterValues("accountName");
			String[] description =request.getParameterValues("description");
			String[] amount =request.getParameterValues("amount");

			for(int i=0;i<4;i++){
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
					PayDAO.insertDocTranDetail(conn, item);
				}else{
					//update
					PayDAO.updateDocTranDetail(conn, item);
				}
				
			}
			
			//Search Again
			PayBean bean = PayDAO.searchHead(conn,h,true).getItems().get(0);
		
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
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
		PayForm aForm = (PayForm) form;
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
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report ");
		PayForm reportForm = (PayForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		 
		try {
			conn = DBConnection.getInstance().getConnection();
			PayForm aForm = (PayForm) form;
			String fileType = SystemElements.PRINTER;//SystemElements.PDF;//request.getParameter("fileType");
			//String fileType =SystemElements.PDF;
			logger.debug("fileType:"+fileType);
			
			//Search Again
			String docNo = Utils.isNull(request.getParameter("docNo"));
			PayBean cri = new PayBean();
			cri.setDocNo(docNo);
			
			PayBean h = PayDAO.searchHead(conn,cri,true).getItems().get(0);
			logger.debug("result:"+h.getDocNo());
			
			if(h != null){
				//Head
				if("CH".equalsIgnoreCase(h.getPaymethod())){
				   parameterMap.put("cheque", h.getPaymethod());
				}else{
				   parameterMap.put("cash", h.getPaymethod());
				}
				// 22/10/2558
			    String dd = h.getDocDate().substring(0,2);
			    String MM = h.getDocDate().substring(3,5);
			    String yyyy = h.getDocDate().substring(6,10);
			    
				parameterMap.put("docDateDD", Utils.isNull(dd));
				parameterMap.put("docDateMM", Utils.isNull(MM));
				parameterMap.put("docDateYYYY", Utils.isNull(yyyy));
				
				parameterMap.put("payInName", Utils.isNull(h.getPayToName()));
				parameterMap.put("deptName", Utils.isNull(h.getDeptName()));
				parameterMap.put("sectionName", Utils.isNull(h.getSectionName()));
				parameterMap.put("totalAmountLetter", Utils.isNull(h.getTotalAmountLetter()));
				parameterMap.put("createUser", Utils.isNull(h.getCreateUser()));
				
				logger.debug("totalAmountLetter["+Utils.isNull(h.getTotalAmountLetter())+"]");
				
				String[] aa = Utils.isNull(h.getTotalAmount()).split("\\.");
				parameterMap.put("totalAmount", Utils.isNull(aa[0]));
				if(aa.length > 1){
					parameterMap.put("totalAmount2Digit", Utils.isNull(aa[1]));
				}else{
					parameterMap.put("totalAmount2Digit", "");
				}
				
				//Items
				if(h.getItems() != null &&  h.getItems().size() >0){
					for(int i=0;i<h.getItems().size();i++){
						PayBean item = h.getItems().get(i);
						if(i==0){
							parameterMap.put("accountName1", Utils.isNull(item.getAccountName()));
							parameterMap.put("description1", Utils.isNull(item.getDescription()));
		
							logger.debug("amount:"+item.getAmount());
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount1", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							
							if(a.length > 1){
								parameterMap.put("amount2Digit1", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
								   parameterMap.put("amount2Digit1", "00");
							}
						}
						if(i==1){
							parameterMap.put("accountName2", Utils.isNull(item.getAccountName()));
							parameterMap.put("description2", Utils.isNull(item.getDescription()));
		
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount2", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							
							if(a.length > 1){
								parameterMap.put("amount2Digit2", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
								   parameterMap.put("amount2Digit2", "00");
							}
						}
						if(i==2){
							parameterMap.put("accountName3", Utils.isNull(item.getAccountName()));
							parameterMap.put("description3", Utils.isNull(item.getDescription()));
		
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount3", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							if(a.length > 1){
							   parameterMap.put("amount2Digit3", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
							       parameterMap.put("amount2Digit3", "00");
							}
						}
						if(i==3){
							parameterMap.put("accountName4", Utils.isNull(item.getAccountName()));
							parameterMap.put("description4", Utils.isNull(item.getDescription()));
		
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount4", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							if(a.length > 1){
							   parameterMap.put("amount2Digit4", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
							      parameterMap.put("amount2Digit4", "00");
							}
						}
					}
				}
				
				//Gen Report
				String fileName = "pay_in_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				logger.debug("start report");
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,h.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
				return  mapping.findForward("detail");
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
		// return null;
		return null;
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
