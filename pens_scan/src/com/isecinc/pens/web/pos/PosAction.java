package com.isecinc.pens.web.pos;

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

import util.BundleUtil;
import util.Constants;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.MTTBeanDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PosAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MTTBean ad = new MTTBean();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(MTTBeanDAO.searchHead(aForm.getBeanCriteria()));
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
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MTTBean b = aForm.getBean();
			aForm.setBean(MTTBeanDAO.searchHead(aForm.getBean()));
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
	
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			htmlTable = genExportReport(request,aForm,user);
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("report");
	}
	
	
	private StringBuffer genExportReport(HttpServletRequest request,PosForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		try{
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='6'>Scan Barcode</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='6' >วันที่ทำรายการ:"+form.getBean().getDocDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='6' >กลุ่มร้านค้า:"+form.getBean().getCustGroup()+"&nbsp;"+Utils.isNull(form.getBean().getCustGroupName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='6' >รหัสร้านค้า:"+form.getBean().getStoreCode()+"&nbsp;"+Utils.isNull(form.getBean().getStoreName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResultsSearch() != null){
			    List<MTTBean> list = (List<MTTBean>)form.getResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>No.</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Wacoal Mat.</td> \n");
				  h.append("<td>GroupCode</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Whole Proce BF</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					MTTBean s = (MTTBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getNo()+"</td> \n");
					  
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td>"+s.getGroupCode()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='num'>"+s.getWholePriceBF()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		PosForm aForm = (PosForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new MTTBean());
			
			MTTBean ad = new MTTBean();
			ad.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
		String forward = "mttDetail";
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
		
            String docNo = Utils.isNull(request.getParameter("docNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
            String pageType = Utils.isNull(request.getParameter("pageType"));
           
            
			if( !"".equals(docNo) && !"".equals(docNo)){
				logger.debug("prepare edit docNo:"+docNo);
				MTTBean c = new MTTBean();
				c.setDocNo(docNo);
				
				MTTBean bean = MTTBeanDAO.searchHead(c).getItems().get(0);
				
				//search item
				MTTBean items = MTTBeanDAO.searchItem( bean);
				aForm.setResults(items.getItems());
				
				aForm.setBean(bean);
				aForm.setMode(mode);//Mode Edit
				aForm.setPageType(pageType);
			}else{
				
				logger.debug("prepare new docNo");
				aForm.setResults(new ArrayList<MTTBean>());
				MTTBean ad = new MTTBean();
				ad.setCanEdit(true);
				ad.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				aForm.setBean(ad);
				
				aForm.setMode(mode);//Mode Add new
				aForm.setPageType(pageType);
			}

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
		PosForm summaryForm = (PosForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "mttDetail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PosForm orderForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MTTBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
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
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
            //save for search back criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MTTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(Constants.STATUS_OPEN);
			
			List<MTTBean> itemList = new ArrayList<MTTBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] barcode = request.getParameterValues("barcode");
			String[] materialMaster = request.getParameterValues("materialMaster");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] pensItem = request.getParameterValues("pensItem");
			String[] wholePriceBF = request.getParameterValues("wholePriceBF");
			
			logger.debug("barcode:"+barcode.length);
			int maxLineId = 0;

			//add value to Results
			if(barcode != null && barcode.length > 0){
				for(int i=0;i<barcode.length;i++){
					if(    !Utils.isNull(barcode[i]).equals("") 
						&& !Utils.isNull(materialMaster[i]).equals("")
						){
						
						 MTTBean l = new MTTBean();
						 maxLineId++;
						 l.setLineId(maxLineId);
						 
						 l.setBarcode(Utils.isNull(barcode[i]));
						 l.setMaterialMaster(Utils.isNull(materialMaster[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setPensItem(Utils.isNull(pensItem[i]));
						 l.setWholePriceBF(Utils.isNull(wholePriceBF[i]));
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			//Store in Session
			aForm.setResults(itemList);
			
			//save
			MTTBeanDAO.save(h);
			
			//commit
			conn.commit();

			//search
			h = MTTBeanDAO.searchHead(h).getItems().get(0);
			aForm.setBean(h);
			//get Item
			aForm.setResults(MTTBeanDAO.searchItem(h).getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//Set Criteria for search
			//aForm.getBeanCriteria().setJobId(h.getJobId());
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "mttDetail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "mttDetail";
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PosForm aForm = (PosForm) form;
		try {
			aForm.setResults(new ArrayList<MTTBean>());
			
			MTTBean ad = new MTTBean();
			ad.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("mttDetail");
	}


	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MTTBean h = aForm.getBean();
			h.setUpdateUser(user.getUserName());
			h.setStatus(Constants.STATUS_CANCEL);
			
			MTTBeanDAO.updateStatusByDocNo(conn, h);
			
			conn.commit();
			
			MTTBean items = MTTBeanDAO.searchItem(h);
			aForm.setResults(items.getItems());
			aForm.setBean(h);
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
		return mapping.findForward("mttDetail");
	}
	
	public ActionForward closeJob(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("closeJob");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MTTBean h = aForm.getBean();
			h.setUpdateUser(user.getUserName());
			h.setStatus(Constants.STATUS_CLOSE);
			
			MTTBeanDAO.updateHeadModelCaseCloseJob(conn, h);
			MTTBeanDAO.updateLineModelCaseCloseJob(conn, h);
			
			conn.commit();
			
			//search
			h = MTTBeanDAO.searchHead(h).getItems().get(0);
			aForm.setBean(h);
			//search Item
			aForm.setResults(MTTBeanDAO.searchItem(h).getItems());
		
			request.setAttribute("Message", "ปิดงาน  เรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("mttDetail");
	}
	
	public ActionForward prepareScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareScanReport");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MTTBean ad = new MTTBean();	
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("scanReport");
	}
	
	public ActionForward searchScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MTTBean b = aForm.getBean();
			aForm.setBean(MTTBeanDAO.searchScanReport(aForm.getBean()));
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
		return mapping.findForward("scanReport");
	}
	
	public ActionForward exportScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportScanReport");
		PosForm aForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			htmlTable = genExportScanReport(request,aForm,user);
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("scanReport");
	}
	
	
	private StringBuffer genExportScanReport(HttpServletRequest request,PosForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		int colSpan =7;
		try{
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			 if("barcode".equalsIgnoreCase(form.getBean().getDispType())){
				 colSpan = 9;
			 }
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงาน ผล Scan ยอดสต๊อกคงเหลือ</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >กลุ่มร้านค้า:"+form.getBean().getCustGroup()+"&nbsp;"+Utils.isNull(form.getBean().getCustGroupName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getBean().getStoreCode()+"&nbsp;"+Utils.isNull(form.getBean().getStoreName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ทำรายการ:"+form.getBean().getDocDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Group Code:"+form.getBean().getGroupCode()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >แสดงผลตาม:"+form.getBean().getDispType()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResultsSearch() != null){
			    List<MTTBean> list = (List<MTTBean>)form.getResultsSearch();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				 h.append("<td>เลขที่เอกสาร</td> \n");
				 h.append("<td>วันที่ทำรายการ</td> \n");
				 h.append("<td>Cust Group</td> \n");
				 h.append("<td>Cust No</td> \n");
				 h.append("<td>GroupCode</td> \n");
				 h.append("<td>Pens Item </td> \n");
				 if("barcode".equalsIgnoreCase(form.getBean().getDispType())){
				   h.append("<td>Material Master </td> \n");
				   h.append("<td>Barcode </td> \n");
				 }
				 h.append("<td>QTY</td> \n");
				
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					MTTBean s = (MTTBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getDocNo()+"</td> \n");
					  h.append("<td>"+s.getDocDate()+"</td> \n");
					  h.append("<td>"+s.getCustGroup()+"</td> \n");
					  h.append("<td>"+s.getStoreCode()+"&nbsp;"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroupCode()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  if("barcode".equalsIgnoreCase(form.getBean().getDispType())){
						  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
						  h.append("<td class='num'>"+s.getBarcode()+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getQty()+"</td> \n");
					
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public ActionForward clearScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearScanReport");
		PosForm aForm = (PosForm) form;
		try {
			aForm.setResults(new ArrayList<MTTBean>());
			
			MTTBean ad = new MTTBean();
			ad.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("scanReport");
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
		
		logger.debug("Search for report : " + this.getClass());
		PosForm reportForm = (PosForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
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
