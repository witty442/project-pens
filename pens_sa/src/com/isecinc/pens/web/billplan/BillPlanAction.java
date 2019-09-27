package com.isecinc.pens.web.billplan;

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
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.moveorder.MoveOrderUtils;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * BillTAction
 * 
 * @author WITTY
 * 
 */
public class BillPlanAction extends I_Action {

	public static int pageSize = 60;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchXX");
		BillPlanForm aForm = (BillPlanForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("new".equals(action)){
				logger.debug("Prepare NEW");
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				BillPlanBean bean = new BillPlanBean();
				aForm.setBean(bean);
				
				//prepare Session List
				prepareSearchData(request, conn, user);
			}else if("back".equals(action)){
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				BillPlanBean bean = new BillPlanBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user){
		try{
			//SALESREP_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = BillPlanUtils.searchSalesrepListAll(conn,"","C","",user);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);

			List<PopupBean> salesZoneList_s = BillPlanUtils.searchSalesZoneListModel(conn,user);
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		BillPlanForm aForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(BillPlanDAO.searchBillTListTotalRec(conn,aForm.getBean()));
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
				List<BillPlanBean> items = BillPlanDAO.searchBillTList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
			    List<BillPlanBean> items = BillPlanDAO.searchBillTList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
		User user = (User) request.getSession().getAttribute("user");
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		BillPlanForm aForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
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
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		BillPlanForm aForm = (BillPlanForm) form;
		StringBuffer resultTable = null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			List<BillPlanBean> items = BillPlanDAO.searchBillTList(conn,aForm.getBean(),true,0,pageSize);
		    if(items!= null && items.size() >0){
		    	resultTable = genExcelHTMLTable(aForm.getBean(), items);
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		   }else{
			  request.setAttribute("Message","ไม่พบข้อมูล");
		   }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	private StringBuffer genExcelHTMLTable(BillPlanBean o, List<BillPlanBean> items) throws Exception{
	 StringBuffer h = new StringBuffer("");
	 String colSpan = "8";
	 if("summary".equalsIgnoreCase(o.getDispType())){
		 colSpan = "6";
	 }
	 h.append(ExcelHeader.EXCEL_HEADER);
	 
	 h.append("<table border='1'> \n");
	 h.append("<tr><td colspan='"+colSpan+"'><b>รายงาน   Bill-T ที่ยังไม่ได้รับเข้าระบบ</b></td></tr> \n");
	 h.append("</table> \n");
	 h.append("<table border='1'> \n");
	 h.append("<tr> \n");
	 h.append("<th >ภาคการดูแล</th>\n");
	 h.append("<th >รหัสพนักงานขาย</th>\n");
	 h.append("<th >ชื่อพนักงานขาย</th>\n");
	 h.append("<th >เลขที่ Bill-T</th>\n");
	 h.append("<th >วันที่ Bill-T</th>\n");
	 if("detail".equalsIgnoreCase(o.getDispType())){
	  h.append("<th >รหัสสินค้า</th>\n");
	  h.append("<th >ชื่อสินค้า</th>\n");
	 }
	 h.append("<th >จำนวนที่ Plan ไป</th>\n");
	 h.append("</tr> \n");
	 for(int i=0;i<items.size();i++){
		 BillPlanBean p = items.get(i);
		 h.append("<tr> \n");
		 h.append("<td class='text'> "+p.getSalesZone()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepCode()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepName()+"</td>\n");
		 h.append("<td class='text'> "+p.getBillTNo()+"</td>\n");
		 h.append("<td class='text'> "+p.getBillTDate()+"</td>\n");
		 if("detail".equalsIgnoreCase(o.getDispType())){
		   h.append("<td class='text'> "+p.getItem()+"</td>\n");
		   h.append("<td class='text'> "+p.getItemName()+"</td>\n");
		 }
		 h.append("<td class='num'> "+p.getPlanQty()+"</td>\n");
		 h.append("</tr> \n");
	 }
	 h.append("</table> \n");
	 return h;
	}
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BillPlanForm aForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
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
