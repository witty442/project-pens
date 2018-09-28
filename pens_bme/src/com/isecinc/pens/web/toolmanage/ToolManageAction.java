package com.isecinc.pens.web.toolmanage;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ToolManageDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ToolManageAction extends I_Action {

	public static int pageSize = 25;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ToolManageForm aForm = (ToolManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		ToolManageBean ad = null;
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			String action = Utils.isNull(request.getParameter("action"));
			
			logger.debug("prepareSearch action["+action+"]");
			
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				ad = new ToolManageBean();
				ad.setDocDate("");
				ad.setPageName(pageName);
				//set docType
				if("ToolManageOut".equalsIgnoreCase(pageName)){
					ad.setDocType("OUT");
				}else if("ToolManageIn".equalsIgnoreCase(pageName)){
					ad.setDocType("IN");
				}
				aForm.setBean(ad);
				
				List<Master> custGroupList = new ArrayList<Master>();
				Master refP = new Master(); 
				custGroupList.add(refP);
				custGroupList.addAll(GeneralDAO.getCustGroupList(""));
				request.getSession().setAttribute("custGroupList",custGroupList);
				
				//init itemList
				conn  =DBConnection.getInstance().getConnection();
				List<ToolManageBean> itemList = new ArrayList<ToolManageBean>();
				ToolManageBean refP1 = new ToolManageBean(); 
				refP1.setItem("");
				refP1.setItemName("");
				itemList.add(refP1);
				itemList.addAll(ToolManageDAO.searchItemMasterList(conn));
				request.getSession().setAttribute("itemList",itemList);
			}else if("clear".equals(action)){
				if("ToolManageReport".equalsIgnoreCase(aForm.getBean().getPageName())){
					aForm.setResultsSearch(null);
					request.getSession().removeAttribute("resultReport"); 
					ad = new ToolManageBean();
					ad.setPageName(aForm.getBean().getPageName());
					aForm.setBean(ad);
				}else{
					aForm.setResultsSearch(null);
					ad = new ToolManageBean();
					ad.setPageName(aForm.getBean().getPageName());
					aForm.setBean(ad);
				}
			}else if("back".equals(action)){
				//search head again
				conn  =DBConnection.getInstance().getConnection();
				aForm.setResultsSearch(ToolManageDAO.searchHeadList(conn, aForm.getBeanCriteria(), false, 1, pageSize));
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
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		ToolManageForm aForm = (ToolManageForm) form;
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
				request.getSession().setAttribute("resultReport", null); 
				
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(ToolManageDAO.searchTotalRecList(conn,aForm.getBean()));
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
				List<ToolManageBean> items = ToolManageDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
				List<ToolManageBean> items = ToolManageDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		ToolManageForm aForm = (ToolManageForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		ToolManageBean bean = new ToolManageBean();
		logger.debug("prepareDetail");
		Connection conn = null;
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());

            String docNo = Utils.isNull(request.getParameter("docNo"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit docNo:"+docNo+"action:"+action);
	
			//Verify
			if("edit".equalsIgnoreCase(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
	
				//Find By DocNo
				bean.setDocNo(docNo);
			    bean = ToolManageDAO.searchHeadList(conn, bean, true, 0, pageSize).get(0);
			    //set PageName
				bean.setPageName(aForm.getBean().getPageName());
				bean.setDocType(aForm.getBean().getDocType());
				bean.setMode(action);
				//search ItemList
				bean.setItems(ToolManageDAO.searchItemList(conn,bean));
				aForm.setBean(bean);
			}else if("view".equalsIgnoreCase(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
	
				//Find By DocNo
				bean.setDocNo(docNo);
			    bean = ToolManageDAO.searchHeadList(conn, bean, true, 0, pageSize).get(0);
			    //set PageName
				bean.setPageName(aForm.getBean().getPageName());
				bean.setDocType(aForm.getBean().getDocType());
				bean.setMode(action);
				//search ItemList
				bean.setItems(ToolManageDAO.searchItemList(conn,bean));
				aForm.setBean(bean);
			}else if("add".equalsIgnoreCase(action)){
				bean = new ToolManageBean();
				//set PageName
				bean.setPageName(aForm.getBean().getPageName());
				bean.setDocType(aForm.getBean().getDocType());
				bean.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setCanSave(true);
				bean.setMode(action);
	
				aForm.setBean(bean);
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

	public ActionForward clearHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearHead");
		User user = (User) request.getSession().getAttribute("user");
		ToolManageForm aForm = (ToolManageForm) form;
		try {
			aForm.setResultsSearch(null);
			ToolManageBean bean = new ToolManageBean();
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
		ToolManageForm aForm = (ToolManageForm) form;
		try {
			logger.debug("prepare Clear Action");
			ToolManageBean bean = new ToolManageBean();
			//set PageName
			bean.setPageName(aForm.getBean().getPageName());
			bean.setDocType(aForm.getBean().getDocType());
			bean.setCanSave(true);
			bean.setMode("add");

			aForm.setBean(bean);
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
		ToolManageForm orderForm = (ToolManageForm) form;
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
		ToolManageForm aForm = (ToolManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i= 0;
		ToolManageBean item = null;
		List<ToolManageBean> items = new ArrayList<ToolManageBean>();
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			ToolManageBean head = aForm.getBean();
			head.setStatus("OPEN");
			head.setCreateUser(user.getUserName());
			head.setUpdateUser(user.getUserName());
			
			//get Parametervalues
			String[] ids = request.getParameterValues("id");
			String[] statusRows = request.getParameterValues("statusRow");
			String[] itemData = request.getParameterValues("item");
			String[] qtys = request.getParameterValues("qty");
			
			logger.debug("statusRows length:"+statusRows.length);
			
			for(i=0;i<statusRows.length;i++){
				logger.debug("statusRows:"+statusRows[i]);
				if( !Utils.isNull(itemData[i]).equals("") ){
				   item = new ToolManageBean();
				   item.setId(Long.parseLong(ids[i]));
				   item.setStatus(Utils.isNull(statusRows[i]));
				   item.setItem(itemData[i]);
				   item.setQty(qtys[i]);
				   item.setCreateUser(user.getUserName());
				   item.setUpdateUser(user.getUserName());
	
				   items.add(item);
				}
			}
			head.setItems(items);
			
			//save db
			ToolManageDAO.save(conn, head);
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อย");
			
		    conn.commit();
		    
		    //search new 
			head.setItems(ToolManageDAO.searchItemList(conn,aForm.getBean()));
            aForm.setBean(head);
		} catch (Exception e) {
			logger.debug("Conn Rollback");
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
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
	
	public ActionForward postAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		Connection conn = null;
		ToolManageForm aForm = (ToolManageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			ToolManageBean head = aForm.getBean();
			head.setStatus("POST");
			head.setCreateUser(user.getUserName());
			head.setUpdateUser(user.getUserName());
			head.setMode("view");
			
			//save db
			ToolManageDAO.updatePostStatus(conn, head);
			//disable can save
			head.setCanSave(false);
			request.setAttribute("Message", "POST ข้อมูลเรียบร้อย");
			
		    conn.commit();
		    
		    //search new 
            aForm.setBean(head);
		} catch (Exception e) {
			logger.debug("Conn Rollback");
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถ POST ข้อมูลได้ \n"+ e.getMessage());
			return mapping.findForward("detail");
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportToExcel");
		ToolManageForm aForm = (ToolManageForm) form;
		String fileName ="data.xls";
		StringBuffer h = new StringBuffer("");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			List<ToolManageBean> items = ToolManageDAO.searchHeadList(conn,aForm.getBean(),true,0,pageSize);
			if(items != null && items.size() >0){
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>เลขที่เอกสาร</td> \n");
				  h.append("<td>วันที่</td> \n");
				  h.append("<td>รหัสร้านค้า</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>อ้างอิง RTN</td> \n");
				  h.append("<td>หมายเหตุ</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					ToolManageBean s = (ToolManageBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getDocNo()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getDocDate())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreCode())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getRefRtn())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getRemark())+"</td> \n");
					h.append("</tr> \n");
				}//for
				h.append("</table> \n");
			    
				//logger.debug("table str: \n"+h.toString());
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(h.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		ToolManageForm aForm = (ToolManageForm) form;
		String fileName ="data.xls";
		StringBuffer data = null;;
		Connection conn = null;
		try {
			 conn = DBConnection.getInstance().getConnection();
			 String action = Utils.isNull(request.getParameter("action"));
			 logger.debug("action:"+action);
			 data = ToolManageReport.genReport(conn, aForm.getBean(),action);
			 if(data != null){
				 if(data.toString().equalsIgnoreCase("AsOfDate Invalid")){
					 request.setAttribute("Message","วันที่เบิกคืน (As of date) ที่คีย์เข้ามา ต้องมากกว่า  วันที่ max(count_stk_date) ของร้านนั้น ๆ ");
					 aForm.getBean().setDocDate("");
				 }else{ 
					  if("ExportToExcel".equalsIgnoreCase(action)){
						java.io.OutputStream out = response.getOutputStream();
						response.setHeader("Content-Disposition", "attachment; filename="+fileName);
						response.setContentType("application/vnd.ms-excel");
						
						Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
						w.write(data.toString());
					    w.flush();
					    w.close();
		
					    out.flush();
					    out.close();
					  }else{
						 request.getSession().setAttribute("resultReport", data); 
				      }//if2
			     }//if1
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
				request.getSession().setAttribute("resultReport", null); 
			}//if
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
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
