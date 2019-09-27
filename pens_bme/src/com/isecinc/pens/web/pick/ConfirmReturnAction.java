package com.isecinc.pens.web.pick;

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
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.ConfirmReturnWacoal;
import com.isecinc.pens.bean.ControlReturnReport;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqReturnWacoal;
import com.isecinc.pens.bean.ReturnBoxReport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ConfirmReturnDAO;
import com.isecinc.pens.dao.ReqReturnDAO;
import com.isecinc.pens.dao.ReqReturnWacoalDAO;
import com.isecinc.pens.init.InitialMessages;
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
public class ConfirmReturnAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				ConfirmReturnWacoal ad = new ConfirmReturnWacoal();
				//ad.setReturnDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());
				//aForm.setResultsSearch(ConfirmReturnDAO.searchHead(aForm.getBean(),false));
				
				conn = DBConnection.getInstance().getConnection();
				List<ConfirmReturnWacoal> items  = ConfirmReturnDAO.searchHead(conn,aForm.getBeanCriteria(),false,false,1,pageSize);
				aForm.setResultsSearch(items);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2_bk(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ConfirmReturnWacoal b = aForm.getBean();
			aForm.setBean(b);
			//aForm.setResultsSearch(ConfirmReturnDAO.searchHead(aForm.getBean(),false));
			
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
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		boolean getItems = false;
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
				aForm.setTotalRecord(ConfirmReturnDAO.searchTotalRecHead(conn,aForm.getBean()));
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
			    List<ConfirmReturnWacoal> items  = ConfirmReturnDAO.searchHead(conn,aForm.getBean(),getItems,allRec,currPage,pageSize);
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
			    List<ConfirmReturnWacoal> items  = ConfirmReturnDAO.searchHead(conn,aForm.getBean(),getItems,allRec,currPage,pageSize);
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
		return mapping.findForward("prepare2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new ConfirmReturnWacoal());
			
			ConfirmReturnWacoal ad = new ConfirmReturnWacoal();
			//ad.setReturnDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String returnNo = Utils.isNull(request.getParameter("returnNo"));
            String requestNo = Utils.isNull(request.getParameter("requestNo"));
            String mode = Utils.isNull(request.getParameter("mode"));

			logger.debug("prepare edit returnNo:"+returnNo +",requestNo:"+requestNo+",mode:"+mode);
			ConfirmReturnWacoal c = new ConfirmReturnWacoal();
			c.setRequestNo(requestNo);
			c.setReturnNo(returnNo);
			
			List<ConfirmReturnWacoal> listData = null;
			listData = ConfirmReturnDAO.searchHeadFromReqDetail(conn,c,true);
			
			
			ConfirmReturnWacoal h = null;
			if(listData != null && listData.size() >0){
			   h = (ConfirmReturnWacoal)listData.get(0);
			   h.setReturnDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
			   //Get maxQtyLimitReturn
			   h.setMaxQtyLimitReturn(ReqReturnWacoalDAO.getMaxQtyLimitReturn(conn));
			
			   logger.debug("totalQty:"+h.getTotalQty());
			   aForm.setBean(h);
			}
			
			aForm.setResults(h.getItems());
			aForm.setMode(mode);//Mode Edit
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
		ConfirmReturnWacoalForm summaryForm = (ConfirmReturnWacoalForm) form;
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
		ConfirmReturnWacoalForm orderForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ConfirmReturnWacoal aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "prepare";
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ConfirmReturnWacoal h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<ConfirmReturnWacoal> itemList = new ArrayList<ConfirmReturnWacoal>();
			//Set Item
		
			String[] boxNo = request.getParameterValues("boxNo");
			String[] jobId = request.getParameterValues("jobId");
			String[] qty = request.getParameterValues("qty");
			
			
			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
					 ConfirmReturnWacoal l = new ConfirmReturnWacoal();
					 l.setLineId(i+1);
					 l.setBoxNo(Utils.isNull(boxNo[i]));
					 l.setJobId(Utils.isNull(jobId[i]));
					 l.setQty(Utils.convertStrToInt(qty[i]));
					 l.setCreateUser(user.getUserName());
					 l.setUpdateUser(user.getUserName());
						
					 itemList.add(l);
					
				}
			}
			
			h.setItems(itemList);
			
			//Store in Session
			aForm.setResults(itemList);
			
			//Validate TotalQty no over set MAX_RETURN_LIMIT (5000) default
			int MAX_QTY_RETURN_LIMIT = ReqReturnWacoalDAO.getMaxQtyLimitReturn(conn);
			int totalQtyDBInDay = ConfirmReturnDAO.getTotalQtyByReturnDate(conn, h);
			int totalQtyInDay = h.getTotalQty()+ totalQtyDBInDay;
			
			logger.debug("MAX_QTY_RETURN_LIMIT["+MAX_QTY_RETURN_LIMIT+"] totalQtyInDay["+totalQtyInDay+"]");
			
            if(totalQtyInDay > MAX_QTY_RETURN_LIMIT){
            	request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้   ไม่สามารถคืนของได้เกิน["+MAX_QTY_RETURN_LIMIT+"]ชิ้น");
				return "search";
			}
            
			//save 
			h = ConfirmReturnDAO.save(conn,h);
			
			//Store in Session
			List<ConfirmReturnWacoal> listData = ConfirmReturnDAO.searchHeadFromReqDetail(conn,h,true);
			if(listData != null && listData.size() >0){
			   h = (ConfirmReturnWacoal)listData.get(0);
			   //Get maxQtyLimitReturn
			   h.setMaxQtyLimitReturn(MAX_QTY_RETURN_LIMIT);
			
			   logger.debug("totalQty:"+h.getTotalQty());
			  
			}
		
			aForm.setBean(h);
			aForm.setResults(h.getItems());
            aForm.setMode("edit");
            
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			conn.rollback();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
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
		return "prepare";
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		try {
			aForm.setResults(new ArrayList<ConfirmReturnWacoal>());
			
			ConfirmReturnWacoal ad = new ConfirmReturnWacoal();
			ad.setReturnDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			
			aForm.setBean(ad);
			aForm.setMode("view");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare");
	}


	/**
	 * Update Status  Return to NEW 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		ConfirmReturnWacoalForm aForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ConfirmReturnWacoal h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(ConfirmReturnDAO.STATUS_NEW);// set to NEW
			
			//save 
			h = ConfirmReturnDAO.cancelReturn(conn,h);

			//Store in Session
			List<ConfirmReturnWacoal> listData = ConfirmReturnDAO.searchHeadFromReqDetail(conn,h,true);
			if(listData != null && listData.size() >0){
			   h = (ConfirmReturnWacoal)listData.get(0);
			}
			
			//Get maxQtyLimitReturn
			h.setMaxQtyLimitReturn(ReqReturnWacoalDAO.getMaxQtyLimitReturn(conn));
			
			//set CanEdit = false after cancel confirm
			h.setCanEdit(false);
			
			aForm.setBean(h);
			aForm.setResults(h.getItems());
            aForm.setMode("edit");
	
            conn.commit();
            
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare");
	}
	
	public ActionForward printControlReturnReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("printControlReturnReport : " + this.getClass());
		ConfirmReturnWacoalForm reportForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
			String typeReport = Utils.isNull(request.getParameter("typeReport"));
	        String title = "ใบคุมการคืนสินค้า(ต้นฉบับ)";
	        if( !"org".equals(typeReport)){
	        	title = "ใบคุมการคืนสินค้า(สำเนา)";
	        }
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);
			
			conn = DBConnection.getInstance().getConnection();
			
			List<ControlReturnReport> dataList = ConfirmReturnDAO.searchItemForReportControlReturn(conn, reportForm.getBean());
			
			if(dataList != null && dataList.size()> 0){
				//Head
				parameterMap.put("p_title", title);
				parameterMap.put("p_returnNo", reportForm.getBean().getReturnNo());
				parameterMap.put("p_returnDate", reportForm.getBean().getRequestDate());
				
				parameterMap.put("p_total_box", reportForm.getBean().getTotalBox());
				parameterMap.put("p_total_qty", reportForm.getBean().getTotalQty());
				
				//Gen Report
				String fileName = "control_return_pdf_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, dataList);
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"
					+ e.getMessage());
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return null;
	}
	
	public ActionForward printReturnBoxReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("printReturnBoxReport : " + this.getClass());
		ConfirmReturnWacoalForm reportForm = (ConfirmReturnWacoalForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
			String typeReport = Utils.isNull(request.getParameter("typeReport"));
			String boxNo = Utils.isNull(request.getParameter("boxNo"));
			
	        String title = "ใบส่งสินค้าคืนจากร้านค้า(ต้นฉบับ)";
	        if( !"org".equals(typeReport)){
	        	title = "ใบส่งสินค้าคืนจากร้านค้า(สำเนา)";
	        }
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);
			
			conn = DBConnection.getInstance().getConnection();
			List<ReturnBoxReport> dataList = ConfirmReturnDAO.searchItemForReportReturnBox(conn, reportForm.getBean(),boxNo);
			
			if(dataList != null && dataList.size()> 0){
				//Head
				parameterMap.put("p_title", title);
				parameterMap.put("p_printDate",DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,Utils.local_th));
				
				//Gen Report
				String fileName = "return_box_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, dataList);
				
				//request.setAttribute("printSuccess", "printSuccess");
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"
					+ e.getMessage());
		} finally {
			try {
				if(conn !=null){
				 conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return null;
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
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportExcel : ");
		Connection conn = null;
		ConfirmReturnWacoalForm reportForm = (ConfirmReturnWacoalForm) form;
		try {
	         if(reportForm.getResults() == null || (reportForm.getResults() != null && reportForm.getResults().size() ==0)){
	        	 request.setAttribute("Message" ,"ไม่พบข้อมูล");
	         }else{
	        	conn = DBConnection.getInstance().getConnection();
	        	
	 			PickStock h = ConfirmReturnDAO.searchConfirmStockItemByReturnNo4Report(conn, reportForm.getBean());
	 			
	 			StringBuffer htmlTable = genHTML(reportForm.getBean(),h);		 
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=confirmReturnWacoal.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
	         }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	private StringBuffer genHTML(ConfirmReturnWacoal c,PickStock p){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			String title = "Return No:"+c.getReturnNo();
			
			h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='left' colspan='7'><b>"+title+" </b></td> \n");
				h.append("</tr> \n");
			h.append("</table> \n");

			if(p.getItems()!= null){
			    List<PickStock> dataList =p.getItems();
			    //Job Id	Job Name	เลขที่กล่อง		Wacoal Mat.	Group Code	Pens Item	QTY ที่เบิก

				h.append("<table border='1'> \n");
				h.append("<tr> \n");
					 h.append("<td><b>Job Id</b></td> \n");
					 h.append("<td><b>Job Name</b></td> \n");
					 h.append("<td><b>เลขที่กล่อง </b></td> \n");
					 h.append("<td><b>Wacoal Mat</b></td> \n");
					 h.append("<td><b>Group Code</b></td> \n");
					 h.append("<td><b>Pens Item </b></td> \n");
					 h.append("<td><b>QTY ที่เบิก </b></td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					PickStock s = (PickStock)dataList.get(i);
				   h.append("<tr> \n");
				   h.append("<td>"+s.getJobId()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getJobName()+"</td> \n");
				   h.append("<td>"+s.getBoxNo()+"</td> \n");
				   h.append("<td>"+s.getMaterialMaster()+"</td> \n");
				   h.append("<td>"+s.getGroupCode()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getPensItem()+"</td> \n");
				   h.append("<td>"+s.getQty()+"</td> \n");
				   h.append("</tr>");
				}//for 
				
				h.append("<tr> \n");
				h.append("<td colspan='6' align='right'><b>Total</b></td> \n");
				h.append("<td><b>"+p.getTotalQty()+"</b></td> \n");
				h.append("</tr>");	
			h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
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
