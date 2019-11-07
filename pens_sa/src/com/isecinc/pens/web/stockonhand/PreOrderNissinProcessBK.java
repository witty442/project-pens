package com.isecinc.pens.web.stockonhand;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.salestarget.SalesTargetBean;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockReport;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PreOrderNissinProcessBK  {
	/** Logger */
	protected  Logger logger = Logger.getLogger("PENS");
	public static int pageSize = 99999;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			logger.debug("action:"+action);
			if("new".equals(action)){
				request.setAttribute("stockOnhandForm_RESULT",null);
				
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				//bean.setTransDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				aForm.setBean(bean);
			
			}else if("back".equals(action)){
				//clear session 
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				bean.setTransDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
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
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		StockOnhandForm aForm = (StockOnhandForm) form;
		boolean foundData = false;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			request.setAttribute("stockOnhandForm_RESULT",null);
			aForm.getBean().setItemsList(null);
			
			StockOnhandBean stockResult = searchReport(request.getContextPath(),aForm.getBean(),false);
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
			if(resultHtmlTable != null){
				 request.setAttribute("stockOnhandForm_RESULT",resultHtmlTable);
				 foundData = true;
				 aForm.getBean().setStatus(stockResult.getStatus());
			}
			if(foundData==false){
				 request.setAttribute("Message", "ไม่พบข้อมูล");
				 request.setAttribute("stockOnhandForm_RESULT",null);
				 aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
		return mapping.findForward("search");
	}
	
	public String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockOnhandForm aForm = (StockOnhandForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int u = 0;
		Connection conn = null;
		String saveType = Utils.isNull(request.getParameter("saveType"));
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			conn.setAutoCommit(false);
			
			if("saveDB".equalsIgnoreCase(saveType)){
				StockOnhandBean item = null;
				
				String[] productCode=request.getParameterValues("productCode");
				String[] productName=request.getParameterValues("productName");
				String[] beginQty=request.getParameterValues("beginQty");
				String[] salesAvg=request.getParameterValues("salesAvg");
				String[] currentQty=request.getParameterValues("currentQty");
				String[] currentPeriod=request.getParameterValues("currentPeriod");
				String[] endMonthQty=request.getParameterValues("endMonthQty");
				String[] nextQty=request.getParameterValues("nextQty");
				String[] nextPeriod=request.getParameterValues("nextPeriod");
				String[] suggestedPoQty=request.getParameterValues("suggestedPoQty");
				String[] bufferPercent=request.getParameterValues("bufferPercent");
				String[] bufferQty=request.getParameterValues("bufferQty");
				
		
				for(int i=0;i<productCode.length;i++){
					item = new StockOnhandBean();
					item.setTransDate(aForm.getBean().getTransDate());
					item.setCreateUser(user.getUserName());
					item.setUpdateUser(user.getUserName());
					item.setStatus("OPEN");
					item.setProductCode(productCode[i]);
					item.setProductName(productName[i]);
					item.setBeginQty(beginQty[i]);
					item.setSalesAvg(salesAvg[i]);
					item.setCurrentQty(currentQty[i]);
					item.setCurrentPeriod(currentPeriod[i]);
					item.setEndMonthQty(endMonthQty[i]);
					item.setNextQty(nextQty[i]);
					item.setNextPeriod(nextPeriod[i]);
					item.setBufferPercent(bufferPercent[i]);
					item.setBufferQty(bufferQty[i]);
					item.setSuggestedPoQty(suggestedPoQty[i]);
					 //update or insert
					u = updateNissinPreOrder(conn, item);
					if(u==0){
						insertNissinPreOrder(conn, item);
					}
				}//for
				
				//search 
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.getBean().setItemsList(null);
				
				StockOnhandBean stockResult = searchReport(request.getContextPath(),aForm.getBean(),false);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.setAttribute("stockOnhandForm_RESULT",resultHtmlTable);
				}
				
				request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
				conn.commit();
			}else if("saveFinishPreOrder".equalsIgnoreCase(saveType)){
				logger.debug("saveFinishPreOrder");
				StockOnhandBean item = new StockOnhandBean();
				item.setTransDate(aForm.getBean().getTransDate());
				item.setCreateUser(user.getUserName());
				item.setUpdateUser(user.getUserName());
				item.setStatus("FINISH");
				
				u = updateStatusFinishNissinPreOrder(conn, item);
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.getBean().setStatus("FINISH");
				conn.commit();
				
				StockOnhandBean stockResult = searchReport(request.getContextPath(),aForm.getBean(),false);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.setAttribute("stockOnhandForm_RESULT",resultHtmlTable);
				}
				
				request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
			}
			
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "search";
		} finally {
			try {
				conn.close();conn=null;
			} catch (Exception e2) {}
		}
		return "search";
	}
	
 public void insertNissinPreOrder(Connection conn,StockOnhandBean o) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int c =1;
		//logger.debug("insertNissionPreOrder");
		try{
			sql.append(" INSERT INTO PENSBI.NISSIN_PRE_ORDER \n");
			sql.append(" (TRANS_DATE,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC ,Begin_qty ,\n");
			sql.append(" Sales_avg,current_qty, current_period, \n");
			sql.append(" End_month_qty,next_qty,next_period,Buffer_percent,Buffer_qty ,\n");
			sql.append(" suggested_po_qty, STATUS, CREATE_USER, CREATE_DATE)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setDate(c++, new java.sql.Date(DateUtil.parse(o.getTransDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setString(c++, o.getProductCode());
			ps.setString(c++, o.getProductName());
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getBeginQty())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getSalesAvg())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getCurrentQty())));
			ps.setString(c++, o.getCurrentPeriod());
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getEndMonthQty())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getNextQty())));
			ps.setString(c++, o.getNextPeriod());
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getBufferPercent())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getBufferQty())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getSuggestedPoQty())));
			ps.setString(c++, o.getStatus());
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
 
 public int updateNissinPreOrder(Connection conn,StockOnhandBean o) throws Exception{
		PreparedStatement ps = null;
		int c =1;
		try{
		
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.NISSIN_PRE_ORDER \n");
			sql.append(" SET Begin_qty =?,Sales_avg=?,current_qty =?,current_period =? \n");
			sql.append(" ,End_month_qty =?, next_qty =? ,next_period =?,Buffer_percent=?,Buffer_qty=?  \n");
			sql.append(" ,suggested_po_qty=?,UPDATE_USER =?, UPDATE_DATE=? ,STATUS =?  \n");
			
		    sql.append(" WHERE TRANS_DATE =? AND INVENTORY_ITEM_CODE = ?\n");
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getBeginQty())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getSalesAvg())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getCurrentQty())));
			ps.setString(c++, o.getCurrentPeriod());
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getEndMonthQty())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getNextQty())));
			ps.setString(c++, o.getNextPeriod());
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getBufferPercent())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getBufferQty())));
			ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getSuggestedPoQty())));
			
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getStatus());
			
			ps.setDate(c++, new java.sql.Date(DateUtil.parse(o.getTransDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setString(c++, o.getProductCode());

			return ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
 public int updateStatusFinishNissinPreOrder(Connection conn,StockOnhandBean o) throws Exception{
		PreparedStatement ps = null;
		int c =1;
		//logger.debug("updateNissionPreOrder");
		try{
		
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.NISSIN_PRE_ORDER \n");
			sql.append(" SET UPDATE_USER =?, UPDATE_DATE=? ,STATUS =?  \n");
		    sql.append(" WHERE TRANS_DATE =? \n");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getStatus());
			
			ps.setDate(c++, new java.sql.Date(DateUtil.parse(o.getTransDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
		
			return ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	 
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("exportToExcel : ");
		StockOnhandForm aForm = (StockOnhandForm) form;
		try {
			StockOnhandBean stockResult = searchReport(request.getContextPath(),aForm.getBean(),true);
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
		    if(resultHtmlTable != null && resultHtmlTable.length() >0){
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultHtmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		   }else{
			  request.setAttribute("Message","ไม่พบข้อมูล");
			  return mapping.findForward("search");
		   }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return null;
	}
	
	public StockOnhandBean searchReport(String contextPath,StockOnhandBean o,boolean excel){
		StockOnhandBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		String transDate = "";
		String status = "OPEN";
		try{
			//create connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			//2019/10/05
			transDate = DateUtil.stringValue(DateUtil.parse(o.getTransDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th),DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select * from PENSBI.NISSIN_PRE_ORDER M where trans_date = to_date('"+transDate+"','dd/mm/yyyy') ");
			sql.append("\n ORDER BY M.INVENTORY_ITEM_CODE asc");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new StockOnhandBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 o.setCurrentPeriod(Utils.isNull(rst.getString("current_period")));
				 o.setNextPeriod(Utils.isNull(rst.getString("next_period")));
				 html.append(genHeadTable(contextPath,o,excel));
			  }
			  status = Utils.isNull(rst.getString("status"));
			  item.setStatus(status);
			  item.setProductCode(Utils.isNull(rst.getString("INVENTORY_ITEM_CODE")));
			  item.setProductName(Utils.isNull(rst.getString("INVENTORY_ITEM_DESC")));
			  item.setBeginQty(Utils.decimalFormat(rst.getDouble("begin_qty"), Utils.format_current_2_disgit,""));
			  item.setSalesAvg(Utils.decimalFormat(rst.getDouble("sales_avg"), Utils.format_current_2_disgit,"")); 
			  item.setCurrentQty(Utils.decimalFormat(rst.getDouble("current_qty"), Utils.format_current_2_disgit,""));
			  item.setEndMonthQty(Utils.decimalFormat(rst.getDouble("End_month_qty"), Utils.format_current_2_disgit,""));
			  item.setNextQty(Utils.decimalFormat(rst.getDouble("next_qty"), Utils.format_current_2_disgit,""));
			  item.setBufferPercent(Utils.decimalFormat(rst.getDouble("Buffer_percent"), Utils.format_current_no_disgit,""));
			  item.setBufferQty(Utils.decimalFormat(rst.getDouble("Buffer_qty"), Utils.format_current_2_disgit,""));
			  item.setSuggestedPoQty(Utils.decimalFormat(rst.getDouble("suggested_po_qty"), Utils.format_current_2_disgit,""));
			  item.setCurrentPeriod(Utils.isNull(rst.getString("current_period")));
			  item.setNextPeriod(Utils.isNull(rst.getString("next_period")));
			  //gen Row
			  html.append(genRowTable(o,excel,item,(r-1),true));
			}
			
			//Not found in Data Save Get From Procedure Oracle
			if(r==0){
				//input:2019/10/05
				transDate = DateUtil.stringValue(DateUtil.parse(o.getTransDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th),DateUtil.YYYY_MM_DD_WITH_SLASH);
				
                sql = new StringBuilder("");
				sql.append("\n select * from table(apps.xxpens_po_reports_xml_pkg.po_preorder('"+transDate+"')) M");
				sql.append("\n ORDER BY M.segment1 asc");
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				  item = new StockOnhandBean();
				  r++;
				  if(r==1){
					 //gen Head Table
					 html = new StringBuffer("");
					 o.setCurrentPeriod(Utils.isNull(rst.getString("current_name")));
					 o.setNextPeriod(Utils.isNull(rst.getString("next_name")));
					 html.append(genHeadTable(contextPath,o,excel));
				  }
				  item.setProductCode(Utils.isNull(rst.getString("segment1")));
				  item.setProductName(Utils.isNull(rst.getString("description")));
				  item.setBeginQty(Utils.decimalFormat(rst.getDouble("onhand_qty"), Utils.format_current_2_disgit,""));
				  item.setSalesAvg(Utils.decimalFormat(rst.getDouble("avg_qty"), Utils.format_current_2_disgit,"")); 
				  item.setCurrentQty(Utils.decimalFormat(rst.getDouble("current_qty"), Utils.format_current_2_disgit,""));
				  item.setEndMonthQty(Utils.decimalFormat(rst.getDouble("End_qty"), Utils.format_current_2_disgit,""));
				  item.setNextQty(Utils.decimalFormat(rst.getDouble("next_qty"), Utils.format_current_2_disgit,""));
				  item.setBufferPercent(Utils.decimalFormat(rst.getDouble("Buffer_percent"), Utils.format_current_no_disgit,""));
				  item.setBufferQty(Utils.decimalFormat(rst.getDouble("Buffer_qty"), Utils.format_current_2_disgit,""));
				  item.setSuggestedPoQty(Utils.decimalFormat(rst.getDouble("suggested_qty"), Utils.format_current_2_disgit,""));
				  item.setCurrentPeriod(Utils.isNull(rst.getString("current_name")));
				  item.setNextPeriod(Utils.isNull(rst.getString("next_name")));
				  //gen Row
				  html.append(genRowTable(o,excel,item,(r-1),false));
				  
				  //add to List
				  //itemList.add(item);
				 
				}//while
			}//if
			
			logger.debug("status:"+status);
			//Check Execute Found data
			if(r>0){
			  // gen end Table
			   html.append("</tbody></table>");
			}
			o.setDataStrBuffer(html);
			o.setStatus(status);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}

	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private  StringBuffer genHeadTable(String contextPath,StockOnhandBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		int colspan=8;
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1'> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+"><b> Nissin Pre-Order</b> </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <td colspan="+colspan+"> \n");
			h.append(" วันที่ทำรายการ: &nbsp;"+head.getTransDate()+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
		}
		String width="100%";
		h.append("<div class='sticky-table sticky-ltr-cells'> ");
		h.append("<table id='tblProduct' align='center' class='table table-striped' border='1' width='"+width+"' cellpadding='3' cellspacing='1' width='100%'> \n");
		h.append("<thread><tr  class='sticky-header'> \n");
		h.append(" <th bgcolor='#03A4B6' height='30px'>Item</th> \n");
		h.append(" <th bgcolor='#03A4B6' height='30px'>Item Description</th>");
		h.append(" <th bgcolor='#03A4B6' height='30px'>Stock B&W</th>");
		h.append(" <th bgcolor='#03A4B6' height='30px'>AVG Sales <br/>3 M AVG</th>");
		h.append(" <th bgcolor='#03A4B6' height='30px'>Target <br/> "+head.getCurrentPeriod()+"</th>");
		h.append(" <th bgcolor='#82E0AA' height='30px'><b>Suggested PO</b></th>");
		h.append(" <th bgcolor='#03A4B6' height='30px'>Target <br/> "+head.getNextPeriod()+"</th>");
		h.append(" <th bgcolor='#03A4B6' height='30px'>%Buffer</th>");
		h.append("</tr></thread> \n");
		h.append("<tbody> \n");
	
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private  StringBuffer genRowTable(StockOnhandBean head,boolean excel,StockOnhandBean item,int index,boolean calc) throws Exception{
		StringBuffer h = new StringBuffer("");
		String classText = "text";
		String classTextCenter = "text_center";
		String classCurrency = "currency";
		String classNum ="num";
		h.append("<tr class='lineO'> \n");
		
		//calc endMonth
		
		if( !excel){
			classText = "td_text";
			classTextCenter = "td_text_center";
			classCurrency = "td_number";
			h.append("<td class='"+classTextCenter+"' width='4%' height='25px'>");
			h.append(" <input type='text' readonly name='productCode' tabindex='-1' id='productCode' size='6' class='disableText' value='"+item.getProductCode()+"'/>");
			h.append("</td> \n");
			h.append("<td class='"+classText+"' width='12%' height='25px'>");
			h.append(" <input type='text' readonly name='productName' tabindex='-1'  id='productName' size='50' class='disableText' value='"+item.getProductName()+"'/>");
			h.append("</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
			h.append(" <input type='text' readonly name='beginQty' tabindex='-1'  id='beginQty' size='8' class='disableNumber' value='"+item.getBeginQty()+"'/>");
			h.append("</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
			h.append(" <input type='text' readonly name='salesAvg' tabindex='-1'  id='salesAvg' size='8' class='disableNumber' value='"+item.getSalesAvg()+"'/>");
			h.append("</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
			if( !"FINISH".equalsIgnoreCase(item.getStatus())){
			  h.append(" <input type='text' name='currentQty' id='currentQty' size='8' class='enableNumber'  value='"+item.getCurrentQty()+"'");
			  h.append("  onkeydown='return inputNum(event);' onblur='calcEndMonthQty("+index+")' autoComplete='off'/>");
		    }else{
		      h.append(" <input type='text' name='currentQty' id='currentQty' size='8' class='disableNumber'  value='"+item.getCurrentQty()+"'");
			  h.append(" readonly='true'/>");
		    }
			h.append(" <input type='hidden' name='currentPeriod' tabindex='-1'  id='currentPeriod'  value='"+item.getCurrentPeriod()+"'/>");
			h.append(" <input type='hidden' name='nextPeriod' tabindex='-1'  id='nextPeriod'  value='"+item.getNextPeriod()+"'/>");
			h.append(" <input type='hidden' name='endMonthQty' tabindex='-1'  id='endMonthQty'  value='"+item.getEndMonthQty()+"'/>");
			h.append("</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>");
			h.append(" <input type='text' readonly name='suggestedPoQty' tabindex='-1'  id='suggestedPoQty' size='8' class='disableNumberBold' value='"+item.getSuggestedPoQty()+"'/>");
			h.append("</td> \n");
			
			if( !"FINISH".equalsIgnoreCase(item.getStatus())){
				h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
				h.append(" <input type='text' name='nextQty' id='nextQty' size='8' class='enableNumber' value='"+item.getNextQty()+"'");
				h.append("  onkeydown='return inputNum(event);' onblur='calcBufferQty("+index+")' autoComplete='off'/>");
				h.append("</td> \n");
				h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
				h.append(" <input type='text' name='bufferPercent' id='bufferPercent' size='8' class='enableNumber' value='"+item.getBufferPercent()+"'");
				h.append("  onkeydown='return inputNum(event);' onblur='calcBufferQty("+index+")' autoComplete='off'/>");
				h.append("</td> \n");
			}else{
				h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
				h.append(" <input type='text' name='nextQty' id='nextQty' size='8' class='disableNumber' value='"+item.getNextQty()+"'");
				h.append("  readonly='true'/>");
				h.append("</td> \n");
				h.append("<td class='"+classCurrency+"' width='5%' height='25px'>");
				h.append(" <input type='text' name='bufferPercent' id='bufferPercent' size='8' class='disableNumber' value='"+item.getBufferPercent()+"'");
				h.append("  readonly='true'/>");
				h.append("</td> \n");
			}
			h.append(" <input type='hidden' name='bufferQty' tabindex='-1'  id='bufferQty' value='"+item.getBufferQty()+"'/>");
		}else{
			h.append("<td class='"+classText+"' width='4%' height='25px'>"+item.getProductCode()+"</td> \n");
			h.append("<td class='"+classText+"' width='15%' height='25px'>"+item.getProductName()+"</td> \n");
			h.append("<td class='"+classNum+"' width='5%' height='25px'>"+item.getBeginQty()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%' height='25px'>"+item.getSalesAvg()+"</td> \n");
			h.append("<td class='"+classNum+"' width='5%' height='25px'>"+item.getCurrentQty()+"</td> \n");
			h.append("<td class='"+classNum+"' width='5%' height='25px'>"+item.getSuggestedPoQty()+"</td> \n");
			h.append("<td class='"+classNum+"' width='5%' height='25px'>"+item.getNextQty()+"</td> \n");
			h.append("<td class='"+classNum+"' width='5%' height='25px'>"+item.getBufferPercent()+"</td> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	 public  List<PopupBean> searchTransDateNissinPreOrderList() throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupBean> pos = new ArrayList<PopupBean>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String transDate = "";
			try {
				sql.append("\n select DISTINCT TRANS_DATE ");
				sql.append("\n FROM PENSBI.NISSIN_PRE_ORDER ORDER BY TRANS_DATE DESC");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupBean item = new PopupBean();
					transDate = DateUtil.stringValue(rst.getDate("TRANS_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					no++;
					item.setNo(no);
					item.setKeyName(transDate);
					item.setValue(transDate);
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
}
