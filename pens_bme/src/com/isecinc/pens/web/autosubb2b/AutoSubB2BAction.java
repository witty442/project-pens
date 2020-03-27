package com.isecinc.pens.web.autosubb2b;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.AutoSubB2BDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AutoSubB2BAction extends I_Action {

	public static int pageSize = 30;
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("prepare2 action["+action+"]");
			
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				AutoSubB2BBean ad = new AutoSubB2BBean();
				//ad.setRefType("WACOAL");//default
				
				aForm.setBean(ad);
				
				//forwarder List
			    List<PopupForm> forwarderList = new ArrayList<PopupForm>();
			    PopupForm refP = new PopupForm("",""); 
			    forwarderList.add(refP);
			    forwarderList.addAll(GeneralDAO.searchForwarderList( new PopupForm()));
			    request.getSession().setAttribute("forwarderList",forwarderList);
			    
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
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
				aForm.setTotalRecord(AutoSubB2BDAO.searchTotalDataRecList(conn,aForm.getBean()));
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
				List<AutoSubB2BBean> items = AutoSubB2BDAO.searchDataList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
				List<AutoSubB2BBean> items = AutoSubB2BDAO.searchDataList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		AutoSubB2BBean bean = new AutoSubB2BBean();
		logger.debug("prepare");
		Connection conn = null;
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());

            String refNo = Utils.isNull(request.getParameter("refNo"));
            String refType = Utils.isNull(request.getParameter("refType"));
            String fromSubInv = Utils.isNull(request.getParameter("fromSubInv"));
            String fromCustGroup = Utils.isNull(request.getParameter("fromCustGroup"));
            String fromStoreCode = Utils.isNull(request.getParameter("fromStoreCode"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit refNo:"+refNo+"action:"+action+",fromCustGroup:"+fromCustGroup+",fromSubInv:"+fromSubInv);
	
			//Clear groupStoreMapError Session
			request.getSession().setAttribute("groupStoreMapError", null);
			
			//new
			if("new".equalsIgnoreCase(action)){
				bean.setCanCancel(true);
				bean.setCanSave(true);
				bean.setRefType("WACOAL");//default
				bean.setMode("new");
				aForm.setBean(bean);
				
			}else if("edit".equalsIgnoreCase(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
				
				//Find By RefNo
				bean.setRefNo(refNo);
				bean.setRefType(refType);
				bean.setFromCustGroup(fromCustGroup);
				bean.setFromStoreCode(fromStoreCode);
				bean.setFromSubInv(fromSubInv);
				// getStoreName
				bean.setFromStoreName(GeneralDAO.getStoreNameModel(conn,bean.getFromStoreCode()));
				
			
				List<AutoSubB2BBean> resultList = AutoSubB2BDAO.searchDataList(conn, bean, true, 0, pageSize);
				bean = resultList.get(0);
				bean.setMode("edit");
				bean.setCanSave(true);
				bean.setCanApprove(true);
				aForm.setBean(bean);
				
			}else if("view".equalsIgnoreCase(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
				
				//Find By RefNo
				bean.setRefNo(refNo);
				bean.setRefType(refType);
				bean.setFromCustGroup(fromCustGroup);
				bean.setFromStoreCode(fromStoreCode);
				bean.setFromSubInv(fromSubInv);
				// getStoreName
				bean.setFromStoreName(GeneralDAO.getStoreNameModel(conn,bean.getFromStoreCode()));
				
				List<AutoSubB2BBean> resultList = AutoSubB2BDAO.searchDataList(conn, bean, true, 0, pageSize);
				bean = resultList.get(0);
				bean.setMode("view");
				bean.setCanSave(false);
				bean.setCanApprove(false);
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return forward;
	}

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		User user = (User) request.getSession().getAttribute("user");
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
		try {
			aForm.setResultsSearch(null);
			AutoSubB2BBean bean = new AutoSubB2BBean();
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
		AutoSubB2BForm summaryForm = (AutoSubB2BForm) form;
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
		AutoSubB2BForm orderForm = (AutoSubB2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
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
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			request.getSession().setAttribute("groupStoreMapError",null);
			
			//init Connection
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			AutoSubB2BBean head = aForm.getBean();
			head.setStatus("");
			head.setUserName(user.getUserName());
		
			//validate can Auto Sub B2B
			boolean isExist = false;//AutoSubB2BDAO.isAutoSubTransB2BExist(head.getRefNo());
			if(isExist){
				//request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้  เนื่องจาก Order Lot No/Request No ถูกบันทึกข้อมูลไปแล้ว \n");
				//return "detail";
			}
			
			//save db
			if(head.getRefType().equalsIgnoreCase("WACOAL")){
				//Get Detiail Store From
				head = AutoSubB2BDAO.getStoreDetailByWacoal(conn, head);
			    if(head == null){
			    	request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ เนื่องจากไม่พบข้อมูล Order Lot No/Request No \n");
					return "detail";
			    }
			    //set toCustGroup
			    head.setToCustGroup(head.getToStoreCode().substring(0,6));
			    
			    AutoSubB2BDAO.saveSubTransB2BbyWacoal(conn, head);
			}else{
				//Get Detiail Store From
				head = AutoSubB2BDAO.getStoreDetailByPic(conn, head);
				 if(head == null){
			    	request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ เนื่องจากไม่พบข้อมูล Order Lot No/Request No \n");
					return "detail";
			    }
				//FROM PIC
				//set toCustGroup
				head.setToCustGroup(head.getToStoreCode().substring(0,6));
				AutoSubB2BDAO.saveSubTransB2BByPic(conn, head);
			}
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อย");
			
		    conn.commit();
		    
		    head.setCanApprove(true);
		    head.setCanSave(true);
		    
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
	public ActionForward approveAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("approveAction");
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
			
			AutoSubB2BBean head = aForm.getBean();
			head.setStatus("APPROVED");
			head.setUserName(user.getUserName());
			
			//Get toStoreNo
			StoreBean toStoreBean = GeneralDAO.getStoreInfo(conn, head.getToStoreCode());
			head.setToStoreNo(toStoreBean.getStoreNo());
			
			AutoSubB2BDAO.approveSubTransB2B(conn, head);
			
			conn.commit();
			request.setAttribute("Message", "Approve ข้อมูลเรียบร้อยแล้ว");
			
			//disable Button
			aForm.getBean().setCanApprove(false);
			aForm.getBean().setCanSave(false);
			
			//debug
			Thread.sleep(10000);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportToExcel");
		AutoSubB2BForm aForm = (AutoSubB2BForm) form;
		String fileName ="data.xls";
		StringBuffer h = new StringBuffer("");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			List<AutoSubB2BBean> items = AutoSubB2BDAO.searchDataList(conn,aForm.getBean(),true,0,pageSize);
			if(items != null && items.size() >0){
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table border='1'> \n");
				h.append("<tr  class='column_head'> \n");
				  h.append("<td>Job Id</td> \n");
				  h.append("<td>Job Name</td> \n");
				  h.append("<td>Store Code</td> \n");
				  h.append("<td>Store Name</td> \n");
				  h.append("<td>Sub Inventory</td> \n");
				  h.append("<td>Total Box</td> \n");
				  h.append("<td>Total Qty</td> \n");
				  h.append("<td>AutoCN Status</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					AutoSubB2BBean s = (AutoSubB2BBean)items.get(i);
					h.append("<tr> \n");
					 // h.append("<td class='text'>"+s.getJobId()+"</td> \n");
					  //h.append("<td class='text'>"+Utils.isNull(s.getJobName())+"</td> \n");
					/*  h.append("<td class='text'>"+Utils.isNull(s.getStoreCode())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStoreName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getSubInv())+"</td> \n");*/
					  h.append("<td class='num'>"+s.getTotalBox()+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalQty()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getStatus())+"</td> \n");
					h.append("</tr>");
				}//for
				h.append("</table> \n");
			
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
