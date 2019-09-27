package com.isecinc.pens.web.moveorder;

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
import com.pens.util.CConstants;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MoveOrderAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		MoveOrderForm aForm = (MoveOrderForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("new".equals(action)){
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				MoveOrderBean bean = new MoveOrderBean();
				aForm.setBean(bean);
				bean.setPeriodType("month");
				bean.setCustCatNo("V");
				bean.setMoveDay(Utils.convertStrToInt(CConstants.getConstants(conn, CConstants.MOVEORDER_REF_CODE, CConstants.MOVEORDER_MAX_MOVEDAY).getValue()));//default
				bean.setDispCheckMoveDay("true");
				//prepare Session List
				prepareSearchData(request, conn, user);
				
			}else if("back".equals(action)){
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				MoveOrderBean bean = new MoveOrderBean();
				bean.setPeriodType("month");
				bean.setCustCatNo("V");
				bean.setMoveDay(Utils.convertStrToInt(CConstants.getConstants(conn, CConstants.MOVEORDER_REF_CODE, CConstants.MOVEORDER_MAX_MOVEDAY).getValue()));//default
				bean.setDispCheckMoveDay("true");
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
	
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user){
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", MoveOrderUtils.initPeriod(conn));
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = MoveOrderUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("V");
			item.setCustCatDesc("VAN SALES");
			custCatNoList.add(item);
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			//SALESREP_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = MoveOrderUtils.searchSalesrepListAll(conn,"","C","");
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);

			List<PopupBean> salesZoneList_s = MoveOrderUtils.searchSalesZoneListModel(conn);
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		MoveOrderForm aForm = (MoveOrderForm) form;
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
				aForm.setTotalRecord(MoveOrderDAO.searchMoveOrderListTotalRec(conn,aForm.getBean()));
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
			   
				List<MoveOrderBean> items = MoveOrderDAO.searchMoveOrderList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
			    List<MoveOrderBean> items = MoveOrderDAO.searchMoveOrderList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
		MoveOrderForm aForm = (MoveOrderForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		MoveOrderForm aForm = (MoveOrderForm) form;
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
		MoveOrderForm aForm = (MoveOrderForm) form;
		StringBuffer resultTable = null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			List<MoveOrderBean> items = MoveOrderDAO.searchMoveOrderList(conn,aForm.getBean(),true,0,pageSize);
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
	
	private StringBuffer genExcelHTMLTable(List<MoveOrderBean> items) throws Exception{
	 StringBuffer h = new StringBuffer("");
	 int no = 0;
	 h.append(ExcelHeader.EXCEL_HEADER);
	 
	 h.append("<table border='1'> \n");
	 h.append("<tr><td colspan='17'><b>รายงาน  ข้อมูลการส่งเอกสารใบเบิก-ใบคืน</b></td></tr> \n");
	 h.append("</table> \n");
	 h.append("<table border='1'> \n");
	 h.append("<tr> \n");
	 h.append("<th rowspan='2'>No.</th>\n");
	 h.append("<th rowspan='2'>ประเภทเอกสาร</th>\n");
	 h.append("<th rowspan='2'>เลขที่เอกสาร</th>\n");
	 h.append("<th rowspan='2'>วันที่เอกสาร</th>\n");
	 h.append("<th rowspan='2'>รหัสพนักงานขาย</th>\n");
	 h.append("<th rowspan='2'>ชื่อพนักงานขาย</th>\n");
	 h.append("<th rowspan='2'>PD</th>\n");
	 h.append("<th rowspan='2'>ชื่อ PD</th>\n");
	 h.append("<th rowspan='2'>รวมหีบ</th>\n");
	 h.append("<th rowspan='2'>รวมเศษ</th>\n");
	 h.append("<th rowspan='2'>ยอดเงินรวมของเอกสาร</th>\n");
	 h.append("<th rowspan='2'>จำนวนวันเดินทางของเอกสาร</th>\n");
	 h.append("<th rowspan='2'>วันที่รับเอกสารจากเลขา</th>\n");
	 h.append("<th rowspan='2'>เหตุผล</th>\n");
	 h.append("<th rowspan='2'>หมายเหตุ</th>\n");
	 h.append("<th colspan='2'>ตามเอกสารจริง</th>\n");
	 h.append("</tr> \n");
	 h.append("<tr> \n");
	 h.append("<th>รวมหีบ</th>\n");
	 h.append("<th>รวมเศษ</th>\n");
	 h.append("</tr> \n");
	 for(int i=0;i<items.size();i++){
		 MoveOrderBean p = items.get(i);
		 no++;
		 h.append("<tr> \n");
		 h.append("<td class='text'> "+no+"</td>\n");
		 h.append("<td class='text'> "+p.getDocType()+"</td>\n");
		 h.append("<td class='text'> "+p.getRequestNumber()+"</td>\n");
		 h.append("<td class='text'> "+p.getRequestDate()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepCode()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepName()+"</td>\n");
		 h.append("<td class='text'> "+p.getPdCode()+"</td>\n");
		 h.append("<td class='text'> "+p.getPdName()+"</td>\n");
		 h.append("<td class='num'> "+p.getCtnQty()+"</td>\n");
		 h.append("<td class='num'> "+p.getPcsQty()+"</td>\n");
		 h.append("<td class='currentcy'> "+p.getAmount()+"</td>\n");
		 h.append("<td class='num'> "+p.getMoveDay()+"</td>\n");
		 h.append("<td class='text'> "+p.getStatusDate()+"</td>\n");
		 h.append("<td class='text'> "+p.getReason()+"</td>\n");
		 h.append("<td class='text'> "+p.getRemark()+"</td>\n");
		 h.append("<td class='num'> "+p.getRealCtnQty()+"</td>\n");
		 h.append("<td class='num'> "+p.getRealPcsQty()+"</td>\n");
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
		MoveOrderForm orderForm = (MoveOrderForm) form;
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
