package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ExcelHeader;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.JobDAO;
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
public class BarcodeAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		BarcodeForm aForm = (BarcodeForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				Barcode ad = new Barcode();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setJob(ad);
			}else if("back".equals(action)){
				aForm.setJob(BarcodeDAO.searchHead(aForm.getJobCriteria()));
				aForm.setResultsSearch(aForm.getJob().getItems());
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
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		BarcodeForm aForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			Barcode b = aForm.getJob();
			if(b.getJobId().equals("")){
				b.setName("");
			}
			aForm.setJob(BarcodeDAO.searchHead(aForm.getJob()));
			aForm.setResultsSearch(aForm.getJob().getItems());
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
		BarcodeForm aForm = (BarcodeForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setJob(new Barcode());
			
			Barcode ad = new Barcode();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setJob(ad);
			
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
		BarcodeForm aForm = (BarcodeForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setJobCriteria(aForm.getJob());
			
            String jobId = Utils.isNull(request.getParameter("jobId"));
            String boxNo = Utils.isNull(request.getParameter("boxNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(jobId) && !"".equals(boxNo)){
				logger.debug("prepare edit jobId:"+jobId +",boxNo:"+boxNo);
				Barcode c = new Barcode();
				c.setJobId(jobId);
				c.setBoxNo(boxNo);
				
				Barcode aS = BarcodeDAO.search(c);
				
				aForm.setResults(aS.getItems());
				aForm.setJob(aS);

				aForm.setMode(mode);//Mode Edit
			}else{
				
				logger.debug("prepare new documentNo");
				aForm.setResults(new ArrayList<Barcode>());
				Barcode ad = new Barcode();
				ad.setCanEdit(true);
				ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				aForm.setJob(ad);
				
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
		BarcodeForm summaryForm = (BarcodeForm) form;
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
		BarcodeForm orderForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			Barcode aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setJob(aS);
			
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
		BarcodeForm aForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {

			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Barcode h = aForm.getJob();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<Barcode> itemList = new ArrayList<Barcode>();
			//Set Item
			String[] barcode = request.getParameterValues("barcode");
			String[] materialMaster = request.getParameterValues("materialMaster");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] pensItem = request.getParameterValues("pensItem");
			String[] wholePriceBF = request.getParameterValues("wholePriceBF");
			String[] retailPriceBF = request.getParameterValues("retailPriceBF");
			
			logger.debug("barcode:"+barcode.length);
			
			//add value to Results
			if(barcode != null && barcode.length > 0){
				for(int i=0;i<barcode.length;i++){
					if( !Utils.isNull(barcode[i]).equals("") && !Utils.isNull(materialMaster[i]).equals("")){
						 Barcode l = new Barcode();
						 l.setLineId(i+1);
						 l.setBarcode(Utils.isNull(barcode[i]));
						 l.setMaterialMaster(Utils.isNull(materialMaster[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setPensItem(Utils.isNull(pensItem[i]));
						 l.setWholePriceBF(Utils.isNull(wholePriceBF[i]));
						 l.setRetailPriceBF(Utils.isNull(retailPriceBF[i]));
						
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			//Store in Session
			aForm.setResults(itemList);
			
			//Validate Job in not close
			Job job = new Job();
			job.setJobId(aForm.getJob().getJobId());
			job = JobDAO.search(job);
			
			if(job != null && job.getStatus().equals(JobDAO.STATUS_CANCEL)){
				request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้  Job["+job.getJobId()+"-"+job.getName()+"] มีสถานะเป็น CANCEL");
				return "search";
			}
			
            if(job != null && job.getStatus().equals(JobDAO.STATUS_CLOSE)){
            	request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลได้  Job["+job.getJobId()+"-"+job.getName()+"] มีสถานะเป็น  CLOSE");
				return "search";
			}
            
			//save
			BarcodeDAO.save(h);
			
			//commit
			conn.commit();

			//search
			h = BarcodeDAO.search(h);
			logger.debug("h:"+h+",items:"+h.getItems().size());
			
			aForm.setJob(h);
			aForm.setResults(h.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//Set Criteria for search
			aForm.getJobCriteria().setJobId(h.getJobId());
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
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
		return "search";
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		BarcodeForm aForm = (BarcodeForm) form;
		try {
			aForm.setResults(new ArrayList<Barcode>());
			
			Barcode ad = new Barcode();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setJob(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	public ActionForward newBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("newBox");
		BarcodeForm aForm = (BarcodeForm) form;
		try {
			aForm.setResults(new ArrayList<Barcode>());
			Barcode ad = new Barcode();
			ad.setJobId(aForm.getJob().getJobId());
			ad.setName(aForm.getJob().getName());
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
			ad.setStoreCode(aForm.getJob().getStoreCode());
			ad.setStoreName(aForm.getJob().getStoreName());
			ad.setStoreNo(aForm.getJob().getStoreNo());
			ad.setSubInv(aForm.getJob().getSubInv());
			ad.setRemark(aForm.getJob().getRemark());
			ad.setWareHouse(aForm.getJob().getWareHouse());
			ad.setWareHouseDesc(aForm.getJob().getWareHouseDesc());
			
			ad.setCanEdit(true);
			
			aForm.setJob(ad);
			aForm.setMode("add");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		BarcodeForm aForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Barcode h = aForm.getJob();
			h.setUpdateUser(user.getUserName());
			h.setStatus(JobDAO.STATUS_CANCEL);
			
			BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, h);
			BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, h);
			
			conn.commit();
			aForm.setJob(h);
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
		
		logger.debug("Search for report : " + this.getClass());
		BarcodeForm reportForm = (BarcodeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			Barcode h = BarcodeDAO.searchReport(reportForm.getJob());
			if(h != null){
				//Head
				parameterMap.put("p_boxno", h.getBoxNo());
				parameterMap.put("p_jobname", h.getJobId()+"-"+h.getName());
				parameterMap.put("p_remark", Utils.isNull(h.getRemark()));
	
				//Gen Report
				String fileName = "boxno_pdf_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, h.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
				return  mapping.findForward("prepare");
			}
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
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportExcel : ");
		BarcodeForm reportForm = (BarcodeForm) form;
		try {
		    StringBuffer htmlTable = genFromPickBarcode(reportForm.getJob());	 
			
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
	
	public static StringBuffer genFromPickBarcode(Barcode o) throws Exception{
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
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and h.TRANSACTION_DATE = ? ");
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
			   sql.append("\n and l.status ='"+o.getStatus()+"'");//RESERVE
			}
			sql.append("\n group by h.warehouse,j.job_id,h.create_user,j.name ,l.box_no,l.line_id, " );
			sql.append("\n          l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ");
			
			logger.debug("sql:"+sql.toString());

			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
	
		
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = Utils.parse(o.getTransactionDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(1,new java.sql.Date(tDate.getTime()));
			}
			
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
					h.append("<td align='left' colspan='"+colSpan+"' >Transaction Date:"+o.getTransactionDate()+"</td> \n");
					h.append("</tr> \n");
	
					/*h.append("<tr> \n");
					h.append("<td align='left' colspan='"+colSpan+"' >Job Id:"+o.getJobId()+":"+Utils.isNull(rs.getString("job_name"))+"</td> \n");
					h.append("</tr> \n");*/
					
					h.append("<tr> \n");
					h.append("<td align='left' colspan='"+colSpan+"' class='text'>เลขที่ Job "+o.getJobId()+" "+Utils.isNull(rs.getString("job_name"))+"</td> \n");
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
