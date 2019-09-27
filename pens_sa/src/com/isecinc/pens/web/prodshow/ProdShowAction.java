package com.isecinc.pens.web.prodshow;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.isecinc.pens.web.promotion.PromotionUtils;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ProdShowAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		ProdShowForm aForm = (ProdShowForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if("new".equals(action)){
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				ProdShowBean bean = new ProdShowBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				bean.setPeriodType("month");
				bean.setCustCatNo("VAN".equalsIgnoreCase(pageName)?"V":"S");
				aForm.setBean(bean);
				
				//prepare Session List
				prepareSearchData(request, conn, user,pageName);
				
			}else if("back".equals(action)){
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				ProdShowBean bean = new ProdShowBean();
				bean.setPeriodType("month");
				bean.setCustCatNo("V");
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", ProdShowUtils.initPeriod(conn));
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = ProdShowUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			if("VAN".equalsIgnoreCase(pageName)){
				item = new PopupBean();
				item.setCustCatNo("V");
				item.setCustCatDesc("VAN SALES");
			}else{
				item = new PopupBean();
				item.setCustCatNo("S");
				item.setCustCatDesc("CREDIT SALES");
			}
			custCatNoList.add(item);
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			
			//SALESREP_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = ProdShowUtils.searchSalesrepListAll(conn,"","C","");
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);
			
			List<PopupBean> salesZoneList_s = ProdShowUtils.searchSalesZoneListModel(conn);
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		ProdShowForm aForm = (ProdShowForm) form;
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
				aForm.setTotalRecord(ProdShowDAO.searchProdShowListTotalRec(conn,aForm.getBean()));
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
			   
				List<ProdShowBean> items = ProdShowDAO.searchProdShowList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
			    List<ProdShowBean> items = ProdShowDAO.searchProdShowList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
		ProdShowForm aForm = (ProdShowForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ProdShowForm aForm = (ProdShowForm) form;
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
		ProdShowForm aForm = (ProdShowForm) form;
		StringBuffer resultTable = null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			List<ProdShowBean> items = ProdShowDAO.searchProdShowList(conn,aForm.getBean(),true,0,pageSize);
		    if(items!= null && items.size() >0){
		    	resultTable = genExcelHTMLTable(items);
				
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
	
	private StringBuffer genExcelHTMLTable(List<ProdShowBean> items) throws Exception{
	 StringBuffer h = new StringBuffer("");
	 int no = 0;
	 h.append(ExcelHeader.EXCEL_HEADER);
	 
	 h.append("<table border='1'> \n");
	 h.append("<tr><td colspan='10'><b>รายงาน  ข้อมูลตั้งกองโชว์ </b></td></tr> \n");
	 h.append("</table> \n");
	 h.append("<table border='1'> \n");
	 h.append("<tr> \n");
	 h.append("<th>No.</th>\n");
	 h.append("<th>ภาคการขาย</th>\n");
	 h.append("<th>พนักงานขาย</th>\n");
	 h.append("<th>รหัสร้านค้า</th>\n");
	 h.append("<th>ชื่อร้านค้า</th>\n");
	 h.append("<th>เลขที่ ตั้งกอง</th>\n");
	 h.append("<th>วันที่ทำรายการ</th>\n");
	 h.append("<th>แบรนด์</th>\n");
	 h.append("<th>รูปที่ 1</th>\n");
	 h.append("<th>รูปที่ 2</th>\n");
	 h.append("<th>รูปที่ 3</th>\n");
	 h.append("</tr> \n");
	 for(int i=0;i<items.size();i++){
		 ProdShowBean p = items.get(i);
		 no++;
		 h.append("<tr> \n");
		 h.append("<td class='text'> "+no+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesChannelName()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepCode()+"-"+p.getSalesrepName()+"</td>\n");
		 h.append("<td class='text'> "+p.getCustomerCode()+"</td>\n");
		 h.append("<td class='text'> "+p.getCustomerName()+"</td>\n");
		 h.append("<td class='text'> "+p.getOrderNo()+"</td>\n");
		 h.append("<td class='text'> "+p.getShowDate()+"</td>\n");
		 h.append("<td class='text'> "+p.getBrand()+"-"+p.getBrandName()+"</td>\n");
		 h.append("<td class='text'> "+p.getPic1()+"</td>\n");
		 h.append("<td class='text'> "+p.getPic2()+"</td>\n");
		 h.append("<td class='text'> "+p.getPic3()+"</td>\n");
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
		ProdShowForm orderForm = (ProdShowForm) form;
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
