package com.isecinc.pens.web.sa;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
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
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.SAEmpBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ConfPickStockDAO;
import com.isecinc.pens.dao.SAEmpDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.pick.ConfPickStockForm;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SAEmpAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SAEmpBean ad = new SAEmpBean();
				//Can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
					ad.setCanEdit(true);
				}
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				SAEmpBean oldCri = aForm.getBeanCriteria();
				oldCri.setEmpRefId("");
				SAEmpBean bean = SAEmpDAO.searchHead(oldCri,"");
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
					bean.setCanEdit(true);
				}
				aForm.setBean(bean);
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
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setBean(SAEmpDAO.searchHead(aForm.getBean(),""));
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
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SAEmpBean bean = new SAEmpBean();
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String empRefId = Utils.isNull(request.getParameter("empRefId"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit empRefId:"+empRefId+",action:"+action);
			if("add".equalsIgnoreCase(action)){
				//init default value
				bean.setRewardBme(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","RewardBME").get(0).getCode());
				bean.setRewardWacoal(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","RewardWACOAL").get(0).getCode());
				bean.setSuretyBond(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","SuretyBond").get(0).getCode());
			}else{
				SAEmpBean c = new SAEmpBean();
				c.setEmpRefId(empRefId);
				bean = SAEmpDAO.searchHead(c,"edit").getItems().get(0);
				
			}
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);
			aForm.setMode(action);//Mode Edit ,Add
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SAEmpBean ad = new SAEmpBean();
				
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			SAEmpBean b = aForm.getBean();
			//aForm.setBean(MCBeanDAO.searchHead(aForm.getBean()));
			//aForm.setResultsSearch(aForm.getBean().getItems());
			
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
		return mapping.findForward("detail");
	}
	
   public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("exportToExcel: ");
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		int colSpan = 23;
		double totalRewardBme = 0;
		double totalRewardWacoal = 0;
		double totalDamage = 0;
		double totalPayment = 0;
		double totalDelayPayment = 0;
		try {
			SAEmpBean bean = aForm.getBean();
			
			 if( !Utils.isNull(bean.getDispDamage()).equals("")){
				 colSpan =26;
			 }
			
			if(bean != null){
				h.append(ExcelHeader.EXCEL_HEADER);
				
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("  <td align='left' colspan='"+colSpan+"'>ข้อมูลพนักงาน SA</td> \n");
				h.append("</tr> \n");
				h.append("<tr> \n");
				h.append("  <td align='left' colspan='"+colSpan+"'></td> \n");
				h.append("</tr> \n");
				/*h.append("<tr> \n");
				h.append("  <td align='left' colspan='"+colSpan+"'></td> \n");
				h.append("</tr> \n");*/
			    h.append("</table> \n");
			    
				List<SAEmpBean> items = SAEmpDAO.searchHead(aForm.getBean(),"").getItems();

				if(items != null && items.size() >0){
					h.append("<table border='1'> \n");
					h.append("<tr> \n");
			            h.append("<td>Employee ID</th><!-- 1 --> \n");
						h.append("<td>Name</th><!-- 3 --> \n");
						h.append("<td>Surname</th><!-- 4 --> \n");
						h.append("<td>รหัสใน Oracle</th><!-- 5 --> \n");
						h.append("<td>Type</th><!-- 6 --> \n");
						h.append("<td>Region</th><!-- 7 --> \n");
						h.append("<td>Group Store</th><!-- 8 --> \n");
						h.append("<td>Branch</th><!-- 9 --> \n");
						h.append("<td>ยอดสะสม ค่าเฝ้าตู้ Bme</td><!-- 9 --> \n");
						h.append("<td>ยอดสะสม ค่าเฝ้าตู้ Wacoal</td><!-- 9 --> \n");
						if( !Utils.isNull(bean.getDispDamage()).equals("")){
						 h.append("<td>ค่าความเสียหาย </th><!-- 9 --> \n");
						 h.append("<td>ยอดชำระแล้ว </th><!-- 9 --> \n");
					     h.append("<td>ยอดค้างชำระ </th><!-- 9 --> \n");
						}
						h.append("<td>Mobile No </th><!-- 10 --> \n");
						h.append("<td>Email</th><!-- 11 --> \n");
						h.append("<td>Bank Account</th><!-- 12 --> \n");
						h.append("<td>ID Card</th><!-- 13 --> \n");
						h.append("<td>Start Working Date</th><!-- 14 --> \n");
						h.append("<td>Leave Date</th><!-- 15 --> \n");
						h.append("<td>Leave Reason </th><!-- 16 --> \n");
						h.append("<td>ค่าเเฝ้าตู้ BME</th><!-- 17 --> \n");
						h.append("<td>วันที่เริ่มให้ค่าเเฝ้าตู้ BME</th><!-- 18 --> \n");
						h.append("<td>ค่าเเฝ้าตู้ Wacoal</th><!-- 1 9--> \n");
						h.append("<td>วันที่เริ่มให้ค่าเเฝ้าตู้ Wacoal</th><!-- 20 --> \n");
						h.append("<td>Surety Bond</th><!-- 21 --> \n");
						h.append("<td>วันที่เริ่มให้ Surety Bond</th><!-- 22 --> \n");
					h.append("</tr>");
					for(int i=0 ;i<items.size();i++){
						SAEmpBean item = items.get(i);
						h.append("<tr> \n");
							h.append("<td class='text'>"+item.getEmpId()+"</th><!-- 1 --> \n");
							h.append("<td>"+item.getName()+"</th><!-- 3 --> \n");
							h.append("<td>"+item.getSurName()+"</th><!-- 4 --> \n");
							h.append("<td class='text'>"+item.getOracleRefId()+"</th><!-- 5 --> \n");
							h.append("<td>"+item.getEmpType()+"</th><!-- 6 --> \n");
							h.append("<td>"+item.getRegionDesc()+"</th><!-- 7 --> \n");
							h.append("<td>"+item.getGroupStore()+"</th><!-- 8 --> \n");
							h.append("<td>"+item.getBranch()+"</th><!-- 9 --> \n");
							h.append("<td class='currency'>"+item.getTotalRewardBme()+" </th><!-- 9 --> \n");
							h.append("<td class='currency'>"+item.getTotalRewardWacoal()+" </th><!-- 9 --> \n");
							if( !Utils.isNull(bean.getDispDamage()).equals("")){
								totalRewardBme += Utils.convertStrToDouble(item.getTotalRewardBme());
								totalRewardWacoal += Utils.convertStrToDouble(item.getTotalRewardWacoal());
								totalDamage += Utils.convertStrToDouble(item.getTotalDamage());
								totalPayment += Utils.convertStrToDouble(item.getTotalPayment());
								totalDelayPayment += Utils.convertStrToDouble(item.getTotalDelayPayment());
								
								h.append("<td class='currency'>"+item.getTotalDamage()+" </th><!-- 9 --> \n");
							    h.append("<td class='currency'>"+item.getTotalPayment()+"</th><!-- 9 --> \n");
							    h.append("<td class='currency'>"+item.getTotalDelayPayment()+" </th><!-- 9 --> \n");
							}
							h.append("<td class='text'>"+item.getMobile()+"</th><!-- 10 --> \n");
							h.append("<td>"+item.getEmail()+"</th><!-- 11 --> \n");
							h.append("<td class='text'>"+item.getBankAccount()+"</th><!-- 12 --> \n");
							h.append("<td class='text'>"+item.getIdCard()+"</th><!-- 13 --> \n");
							h.append("<td>"+item.getStartDate()+"</th><!-- 14 --> \n");
							h.append("<td>"+item.getLeaveDate()+"</th><!-- 15 --> \n");
							h.append("<td>"+item.getLeaveDate()+"</th><!-- 16 --> \n");
							h.append("<td class='text'>"+item.getRewardBme()+"</th><!-- 17 --> \n");
							h.append("<td>"+item.getStartRewardBmeDate()+"</th><!-- 18 --> \n");
							h.append("<td class='text'>"+item.getRewardWacoal()+"</th><!-- 1 9--> \n");
							h.append("<td>"+item.getStartRewardWacoalDate()+"</th><!-- 20 --> \n");
							h.append("<td class='text'>"+item.getSuretyBond()+"</th><!-- 21 --> \n");
							h.append("<td>"+item.getStartSuretyBondDate()+"</th><!-- 22 --> \n");
					    h.append("</tr>");
					}
					
					/** Summary **/
					h.append("<tr> \n");
					h.append("<td class='text'></th><!-- 1 --> \n");
					h.append("<td></th><!-- 3 --> \n");
					h.append("<td></th><!-- 4 --> \n");
					h.append("<td class='text'></th><!-- 5 --> \n");
					h.append("<td></th><!-- 6 --> \n");
					h.append("<td></th><!-- 7 --> \n");
					h.append("<td></th><!-- 8 --> \n");
					h.append("<td><b>รวม</b></th><!-- 9 --> \n");
					if( !Utils.isNull(bean.getDispDamage()).equals("")){
						h.append("<td class='currency_bold'>"+totalRewardBme+" </th><!-- 9 --> \n");
						h.append("<td class='currency_bold'>"+totalRewardWacoal+" </th><!-- 9 --> \n");
						h.append("<td class='currency_bold'>"+totalDamage+" </th><!-- 9 --> \n");
					    h.append("<td class='currency_bold'>"+totalPayment+"</th><!-- 9 --> \n");
					    h.append("<td class='currency_bold'>"+totalDelayPayment+" </th><!-- 9 --> \n");
					}
					h.append("<td class='text'></th><!-- 10 --> \n");
					h.append("<td></th><!-- 11 --> \n");
					h.append("<td class='text'></th><!-- 12 --> \n");
					h.append("<td class='text'></th><!-- 13 --> \n");
					h.append("<td></th><!-- 14 --> \n");
					h.append("<td></th><!-- 15 --> \n");
					h.append("<td></th><!-- 16 --> \n");
					h.append("<td class='text'></th><!-- 17 --> \n");
					h.append("<td></th><!-- 18 --> \n");
					h.append("<td class='text'></th><!-- 1 9--> \n");
					h.append("<td></th><!-- 20 --> \n");
					h.append("<td class='text'></th><!-- 21 --> \n");
					h.append("<td></th><!-- 22 --> \n");
			        h.append("</tr>");
					
					java.io.OutputStream out = response.getOutputStream();
					response.setHeader("Content-Disposition", "attachment; filename=data.xls");
					response.setContentType("application/vnd.ms-excel");
					
					Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
					w.write(h.toString());
				    w.flush();
				    w.close();
	
				    out.flush();
				    out.close();
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล  ");
					return  mapping.findForward("search");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return null;
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
			SAEmpBean bean = new SAEmpBean();
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SAEmpForm summaryForm = (SAEmpForm) form;
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
		SAEmpForm orderForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
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
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SAEmpBean h = aForm.getBean();
			logger.debug("mode:"+aForm.getMode());
			//head 
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Validate StaffId duplicate
			if("add".equalsIgnoreCase((aForm.getMode()))){
				logger.debug("insert:");
				if(!Utils.isNull(h.getEmpId()).equals("")){
					if( SAEmpDAO.isDuplicateEmployeeId(conn, h.getEmpId())){
						request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้  ข้อมูล รหัสพนักงาน ซ้ำ");
						h.setEmpId("");
					    aForm.setBean(h);
						return  "detail";
					}
				}
				
				if(!Utils.isNull(h.getOracleRefId()).equals("")){
					if( SAEmpDAO.isDuplicateOracleRefId(conn,"", h.getOracleRefId())){
						request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้  ข้อมูล รหัสร้านค้าใน Oracle ซ้ำ");
						h.setEmpId("");
					    aForm.setBean(h);
						return  "detail";
					}
				}
				
			    h = SAEmpDAO.insertModel(conn, h);
			}else{
				logger.debug("update:");
				if(!Utils.isNull(h.getOracleRefId()).equals("")){
					if( SAEmpDAO.isDuplicateOracleRefId(conn,h.getEmpId(), h.getOracleRefId())){
						request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้  ข้อมูล รหัสร้านค้าใน Oracle ซ้ำ");
						h.setEmpId("");
					    aForm.setBean(h);
						return  "detail";
					}
				}
				
				SAEmpDAO.updateModel(conn, h);  
			}
			//Search Again
			SAEmpBean bean = SAEmpDAO.searchHead(conn,h).getItems().get(0);
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
		    aForm.setBean(bean);
		    aForm.setMode("edit");
		    
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
		SAEmpForm aForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResults(new ArrayList<SAEmpBean>());
			
			SAEmpBean bean = new SAEmpBean();
			//init default value
			bean.setRewardBme(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","RewardBME").get(0).getCode());
			bean.setRewardWacoal(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","RewardWACOAL").get(0).getCode());
			bean.setSuretyBond(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","SuretyBond").get(0).getCode());
			
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
			aForm.setMode("add");
			aForm.setBean(bean);
			
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
		
		logger.debug("Search for report : " + this.getClass());
		SAEmpForm reportForm = (SAEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			SAEmpBean h = null;// MCBeanDAO.searchReport(reportForm.getBean());
			if(h != null){
				//Head
				//parameterMap.put("p_boxno", h.getBoxNo());
				//parameterMap.put("p_jobname", h.getJobId()+"-"+h.getName());
				//parameterMap.put("p_remark", Utils.isNull(h.getRemark()));
	
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
