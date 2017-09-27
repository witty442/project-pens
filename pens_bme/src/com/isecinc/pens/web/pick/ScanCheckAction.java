package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ReportUtilServlet;
import util.excel.ExcelHeader;
import util.pdf.StampBoxNoReportPdf;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.ScanCheckDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ScanCheckAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ScanCheckForm aForm = (ScanCheckForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			request.getSession().setAttribute("ITEM_MAP",null);
			
			logger.debug("action:"+action);
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				ScanCheckBean ad = new ScanCheckBean();
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(ScanCheckDAO.searchHead(aForm.getBeanCriteria()));
				aForm.setResultsSearch(aForm.getBean().getItems());
				
				//logger.debug("resultSearch prev size:"+aForm.getResultsSearchPrev().size());
				//aForm.setBean(aForm.getBeanCriteria());
				//aForm.setResultsSearch(aForm.getResultsSearchPrev());
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		ScanCheckForm aForm = (ScanCheckForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ScanCheckBean b = aForm.getBean();
			aForm.setBean(ScanCheckDAO.searchHead(aForm.getBean()));
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
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		ScanCheckForm aForm = (ScanCheckForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new ScanCheckBean());
			
			ScanCheckBean ad = new ScanCheckBean();
			ad.setCheckOutDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ScanCheckForm aForm = (ScanCheckForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//clear session
			request.getSession().setAttribute("ITEM_MAP",null);
			
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			//logger.debug("resultSearch size:"+aForm.getResultsSearch().size());
			//aForm.setResultsSearchPrev(aForm.getResultsSearch());
			
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String boxNo = Utils.isNull(request.getParameter("boxNo"));
            String warehouse = Utils.isNull(request.getParameter("warehouse"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(issueReqNo) && !"".equals(boxNo)){
				logger.debug("prepare edit issueReqNo:"+issueReqNo +",boxNo:"+boxNo);
				ScanCheckBean c = new ScanCheckBean();
				c.setIssueReqNo(issueReqNo);
				c.setBoxNo(boxNo);
				c.setWareHouse(warehouse);
				
				//INIT SESSION ITEM_MAP;
				conn = DBConnection.getInstance().getConnection();
				Map<String, String> ITEM_MAP  = ScanCheckDAO.initItemMap(conn,c);
				request.getSession().setAttribute("ITEM_MAP",ITEM_MAP);
				
				ScanCheckBean aS = ScanCheckDAO.searchDetail(c);
				
				aForm.setResults(aS.getItems());
				aForm.setBean(aS);

				aForm.setMode(mode);//Mode Edit
			}else{
				
				logger.debug("prepare new documentNo");
				aForm.setResults(new ArrayList<ScanCheckBean>());
				ScanCheckBean ad = new ScanCheckBean();
				ad.setWareHouse("");
				ad.setIssueReqNo("");
				ad.setCanEdit(true);
				ad.setCheckOutDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
				aForm.setBean(ad);
				
				aForm.setMode(mode);//Mode Add new
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
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
		ScanCheckForm summaryForm = (ScanCheckForm) form;
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
		ScanCheckForm orderForm = (ScanCheckForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			ScanCheckBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
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
		ScanCheckForm aForm = (ScanCheckForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
            saveDB(form, request);
		
            //search refresh data
            ScanCheckBean aS = ScanCheckDAO.searchDetail(aForm.getBean());
			aForm.setResults(aS.getItems());
			aForm.setBean(aS);
			aForm.setMode("edit");
			
			//Set Criteria for search
			//aForm.getBeanCriteria().setJobId(h.getJobId());
		} catch (Exception e) {
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "prepare";
		} finally {
			
		}
		return "search";
	}
	
	private String saveDB(ActionForm form, HttpServletRequest request) throws Exception {
		Connection conn = null;
		ScanCheckForm aForm = (ScanCheckForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String boxNo = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ScanCheckBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<ScanCheckBean> itemList = new ArrayList<ScanCheckBean>();
			//Set Item
			String[] barcode = request.getParameterValues("barcode");
			String[] materialMaster = request.getParameterValues("materialMaster");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] pensItem = request.getParameterValues("pensItem");
			String[] status = request.getParameterValues("status");
			
			logger.debug("barcode:"+barcode.length);
			
			//add value to Results
			if(barcode != null && barcode.length > 0){
				for(int i=0;i<barcode.length;i++){
					if( !Utils.isNull(barcode[i]).equals("") && !Utils.isNull(materialMaster[i]).equals("") 
						&& !Utils.isNull(status[i]).equals("AB") ){
						 ScanCheckBean l = new ScanCheckBean();
						 l.setLineId(i+1);
						 l.setBarcode(Utils.isNull(barcode[i]));
						 l.setMaterialMaster(Utils.isNull(materialMaster[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setPensItem(Utils.isNull(pensItem[i]));
						
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
            ScanCheckBean r = ScanCheckDAO.save(h);
			
			//commit
			conn.commit();
		
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			return r.getBoxNo();
		} catch (Exception e) {
			conn.rollback();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
            throw e;
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		
	}
	
	public ActionForward newBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("newBox");
		ScanCheckForm aForm = (ScanCheckForm) form;
		try {
			//clear session ITEM_MAP for check remain qty
			request.getSession().setAttribute("ITEM_MAP",null);
			
			//save prev boxNo
			String boxNo = saveDB(form, request);
			
			request.setAttribute("Message", "บันทึกข้อมูล กล่องที่ "+boxNo+"เรียบร้อยแล้ว   ,เริ่มบันทึกกล่องใหม่");
			aForm.getBean().setBoxNo("");
			aForm.setResults(new ArrayList<ScanCheckBean>());
			aForm.setMode("add")
			;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ScanCheckForm aForm = (ScanCheckForm) form;
		try {
			aForm.setResults(new ArrayList<ScanCheckBean>());
			ScanCheckBean ad = new ScanCheckBean();
			ad.setWareHouse("");
			ad.setIssueReqNo("");
			ad.setCanEdit(true);
			ad.setCheckOutDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
			aForm.setMode("add");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	@Deprecated
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		ScanCheckForm aForm = (ScanCheckForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ScanCheckBean h = aForm.getBean();
			h.setUpdateUser(user.getUserName());
			h.setStatus(JobDAO.STATUS_CANCEL);
			
			//ScanCheckDAO.updateBarcodeHeadStatusModelByPK(conn, h);
			//ScanCheckDAO.updateBarcodeLineStatusModelByPK(conn, h);
			
			conn.commit();
			aForm.setBean(h);
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
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
		ScanCheckForm reportForm = (ScanCheckForm) form;
		try {
			reportForm.getBean().setBoxNo("");
			InputStream in= StampBoxNoReportPdf.generate(request,reportForm.getBean());// 
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.pdf");
			response.setContentType("application/vnd.ms-excel");
			
			IOUtils.copy(in,out);

		    out.flush();
		    out.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
			
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}
	
	public ActionForward printReport1Box(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ScanCheckForm reportForm = (ScanCheckForm) form;
		try {
			
			InputStream in= StampBoxNoReportPdf.generate(request,reportForm.getBean());// 
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.pdf");
			response.setContentType("application/vnd.ms-excel");
			
			IOUtils.copy(in,out);

		    out.flush();
		    out.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
			
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}
	
	@Deprecated
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportExcel : ");
		ScanCheckForm reportForm = (ScanCheckForm) form;
		try {
		    StringBuffer htmlTable = genFromPickBarcode(reportForm.getBean());	 
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.xls");
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();
         
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}
	
	@Deprecated
	public static StringBuffer genFromPickBarcode(ScanCheckBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer h = new StringBuffer("");
		int colSpan = 11;
		String title = "Stock Pick Query By Detail";
		int totalQty = 0;
		int no = 0;
		String jobName = "";
		try{
			sql.append("\n select h.warehouse,j.job_id,h.create_user,j.name as job_name ,l.box_no, l.material_master ,l.group_code," );
			sql.append("\n l.pens_item,l.barcode,l.status ,count(*) as qty,'' as remark ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h,PENSBI.PENSBME_PICK_BARCODE_ITEM l ,PENSBME_PICK_JOB j ");
			sql.append("\n where h.job_id = l.job_id and h.box_no = l.box_no ");
			sql.append("\n and  h.job_id = j.job_id");

			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and h.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			if( !Utils.isNull(o.getCreateUser()).equals("")){
				sql.append("\n and h.create_user LIKE '%"+Utils.isNull(o.getCreateUser())+"%'");
			}
			
			/*if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and h.TRANSACTION_DATE = ? ");
			}*/
			if( !Utils.isNull(o.getStatus()).equals("")){
			   sql.append("\n and l.status ='"+o.getStatus()+"'");//RESERVE
			}
			sql.append("\n group by h.warehouse,j.job_id,h.create_user,j.name ,l.box_no,l.line_id, " );
			sql.append("\n          l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ");
			
			logger.debug("sql:"+sql.toString());

			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
	
		
			/*if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = Utils.parse(o.getTransactionDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(1,new java.sql.Date(tDate.getTime()));
			}
			*/
			rs = ps.executeQuery();
			
			h.append(ExcelHeader.EXCEL_HEADER);
			
			
			
			while(rs.next()){
			   no++;
			   if(no==1){
					h.append("<table border='1'> \n");
					h.append("<tr> \n");
					h.append("<td align='left' colspan='"+colSpan+"'>"+title+" </td> \n");
					h.append("</tr> \n");
					
					h.append("<tr> \n");
					//h.append("<td align='left' colspan='"+colSpan+"' >Transaction Date:"+o.getTransactionDate()+"</td> \n");
					h.append("</tr> \n");
	
					/*h.append("<tr> \n");
					h.append("<td align='left' colspan='"+colSpan+"' >Job Id:"+o.getJobId()+":"+Utils.isNull(rs.getString("job_name"))+"</td> \n");
					h.append("</tr> \n");*/
					
					h.append("<tr> \n");
					//h.append("<td align='left' colspan='"+colSpan+"' class='text'>เลขที่ Job "+o.getJobId()+" "+Utils.isNull(rs.getString("job_name"))+"</td> \n");
					h.append("</tr> \n");
					
					h.append("<tr> \n");
					h.append("<td align='left' colspan='"+colSpan+"' >สถานะ :"+PickConstants.getStatusDesc(o.getStatus())+" </td>\n");
					h.append("</tr> \n");
	
				    h.append("</table> \n");
	
					h.append("<table border='1'> \n");
					h.append("<tr> \n");
					h.append("<td>No.</td> \n");
					h.append("<td>WareHouse</td> \n");
					h.append("<td>Group Code</td> \n");
					h.append("<td>Pens Item</td> \n");
					h.append("<td>Wacoal Mat.</td> \n");
					h.append("<td>Barcode</td> \n");
					h.append("<td>เลขที่กล่อง</td> \n");
					h.append("<td>Job Id</td> \n");
					h.append("<td>รับคืนจาก</td> \n");
					h.append("<td>Status</td> \n");
					h.append("<td>User Created</td> \n");
					h.append("</tr>");
			   }
			   
			   h.append("<tr> \n");
			   h.append("<td>"+no+"</td> \n");
			   h.append("<td>"+Utils.isNull(rs.getString("warehouse"))+"</td> \n");
			   h.append("<td>"+Utils.isNull(rs.getString("group_code"))+"</td> \n");
			   h.append("<td>"+Utils.isNull(rs.getString("pens_item"))+"</td> \n");
			   h.append("<td>"+Utils.isNull(rs.getString("material_master"))+"</td> \n");
			   h.append("<td class='text'>"+Utils.isNull(rs.getString("barcode"))+"</td> \n");
			   h.append("<td class='text'>"+Utils.isNull(rs.getString("box_no"))+"</td> \n");
			   h.append("<td class='text'>"+Utils.isNull(rs.getString("job_id"))+"</td> \n");
			   h.append("<td>"+Utils.isNull(rs.getString("job_name"))+"</td> \n");
			   h.append("<td>"+PickConstants.getStatusDesc(Utils.isNull(rs.getString("status")))+"</td> \n");
			   h.append("<td>"+Utils.isNull(rs.getString("create_user"))+"</td> \n");
			   h.append("</tr>");
			}
			
			 h.append("<tr> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td></td> \n");
			 h.append("<td>Total QTY</td> \n");
			 h.append("<td>"+no+"</td> \n");
			 h.append("</tr>");
			 
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
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
