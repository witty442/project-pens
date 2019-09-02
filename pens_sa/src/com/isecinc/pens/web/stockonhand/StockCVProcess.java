package com.isecinc.pens.web.stockonhand;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
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

import util.DBConnection;
import util.DateToolsUtil;
import util.ExcelHeader;
import util.SQLHelper;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockReport;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockCVProcess  {
	/** Logger */
	protected Logger logger = Logger.getLogger("PENS");
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
				bean.setTransDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				aForm.setBean(bean);
			}else if("back".equals(action)){
				//clear session 
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				bean.setTransDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
		Connection conn = null;
		boolean foundData = false;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			request.setAttribute("stockOnhandForm_RESULT",null);
			aForm.getBean().setItemsList(null);
			
			StockOnhandBean stockResult = searchReport(request.getContextPath(),aForm.getBean(),false);
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
			if(resultHtmlTable != null){
				 request.setAttribute("stockOnhandForm_RESULT",resultHtmlTable);
				 foundData = true;
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
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
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
		List<StockOnhandBean> itemList = new ArrayList<StockOnhandBean>();
		int r = 0;
		double dailyMeanSaleCTN = 0;
		double dailyMeanSaleTHB = 0;
		double bwStockBalCTN = 0;
		double bwStockBalTHB = 0 ;
		double stockCoverageDay = 0;
		int rowspanByBrand = 0;
		Map<String, String> mapRowSpanByBrand = new HashMap<String, String>();
		try{
			//create connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append("\n select * from table(xxpens_inv_reports_xml_pkg.stock_coverage()) M");
			sql.append("\n ORDER BY M.brand ,M.sku asc");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new StockOnhandBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel));
			  }
			  
			  item.setBrand(Utils.isNull(rst.getString("BRAND")));	
			  item.setProductCode(Utils.isNull(rst.getString("SKU")));
			  item.setDescription(Utils.isNull(rst.getString("description")));
			  item.setSale90Day(Utils.decimalFormat(rst.getDouble("qty"), Utils.format_current_2_disgit));
			  item.setUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price"), Utils.format_current_2_disgit));
			  item.setDailyMeanSaleCTN(Utils.decimalFormat(rst.getDouble("daily_qty"), Utils.format_current_2_disgit));
			  item.setDailyMeanSaleTHB(Utils.decimalFormat(rst.getDouble("daily_amt"), Utils.format_current_2_disgit)); 
			  item.setBwStockBalCTN(Utils.decimalFormat(rst.getDouble("bw_qty"), Utils.format_current_2_disgit));
			  item.setBwStockBalTHB(Utils.decimalFormat(rst.getDouble("bw_amt"), Utils.format_current_2_disgit));
			 
			  if(rst.getDouble("daily_amt")==0){
				  item.setStockCoverageDay("N/A");
			  }else{
			      item.setStockCoverageDay(Utils.decimalFormat(rst.getDouble("coverage_day"), Utils.format_current_2_disgit));
			  } 
			  if(rst.getDouble("brand_day") != 0){
			      item.setStockCoverageDayByBrand(Utils.decimalFormat(rst.getDouble("brand_day"), Utils.format_current_2_disgit));
			      rowspanByBrand =1;
			  }else{
				  item.setStockCoverageDayByBrand(""); 
				  rowspanByBrand++;
				  
				  mapRowSpanByBrand.put(item.getBrand(), rowspanByBrand+"");
			  }
			  //add to List
			  itemList.add(item);
			  
			  //Summary
			  dailyMeanSaleCTN += rst.getDouble("daily_qty");
			  dailyMeanSaleTHB += rst.getDouble("daily_amt");
			  bwStockBalCTN += rst.getDouble("bw_qty");
			  bwStockBalTHB += rst.getDouble("bw_amt");
			  stockCoverageDay += rst.getDouble("coverage_day");
			  
			}//while
			
			//Check Execute Found data
			if(r>0){
				  //Gen Row Table
				logger.debug("itemList size:"+itemList.size());
				boolean isLastRowOfBrand = false;
				StockOnhandBean nextItem = null;
				for(int i=0;i<itemList.size();i++){
					isLastRowOfBrand = false;
					item = itemList.get(i);
					if(i==itemList.size()-1){
						isLastRowOfBrand = true;
					}else{
					   nextItem = itemList.get(i+1);
					   if( !item.getBrand().equals(nextItem.getBrand())){
						   isLastRowOfBrand = true;
					   }//if
					}//if
					
				    html.append(genRowTable(o,excel,item,mapRowSpanByBrand,isLastRowOfBrand));
				 }//for
				  
				html.append("<tr class=''> \n");
				stockCoverageDay = bwStockBalTHB/dailyMeanSaleTHB;
				if(excel){
					html.append("<td class='text' width='3%'></td> \n");
					html.append("<td class='text' width='5%'></td> \n");
					html.append("<td class='text' width='10%'></td> \n");
					html.append("<td class='currency_bold' width='7%'></td> \n");
					html.append("<td class='currency_bold' width='7%'></td> \n");
					html.append("<td class='currency_bold' width='7%'>"+Utils.decimalFormat(dailyMeanSaleCTN, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='currency_bold' width='7%'>"+Utils.decimalFormat(dailyMeanSaleTHB, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='currency_bold' width='7%'>"+Utils.decimalFormat(bwStockBalCTN, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='currency_bold' width='7%'>"+Utils.decimalFormat(bwStockBalTHB, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='currency_bold' width='7%'>"+Utils.decimalFormat(stockCoverageDay, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='currency_bold' width='7%'></td> \n");
				}else{
					html.append("<td class='td_text' width='3%'></td> \n");
					html.append("<td class='td_text' width='5%'></td> \n");
					html.append("<td class='td_text' width='10%'></td> \n");
					html.append("<td class='td_number_bold' width='7%'></td> \n");
					html.append("<td class='td_number_bold' width='7%'></td> \n");
					html.append("<td class='td_number_bold' width='7%'>"+Utils.decimalFormat(dailyMeanSaleCTN, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='td_number_bold' width='7%'>"+Utils.decimalFormat(dailyMeanSaleTHB, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='td_number_bold' width='7%'>"+Utils.decimalFormat(bwStockBalCTN, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='td_number_bold' width='7%'>"+Utils.decimalFormat(bwStockBalTHB, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='td_number_bold' width='7%'>"+Utils.decimalFormat(stockCoverageDay, Utils.format_current_2_disgit)+"</td> \n");
					html.append("<td class='td_number_bold' width='7%'></td> \n");
				}
				html.append("</tr> \n");
				
			  // gen end Table
			  html.append("</table>");
			}
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
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
		int colspan=11;
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1'> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+"><b> Stock Coverage Day</b> </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <td colspan="+colspan+"> \n");
			h.append(" วันที่พิมพ์ : &nbsp;"+DateToolsUtil.getCurrentDateTime(Utils.DD_MM_YYYY_HH_MM_SS_WITH_SLASH)+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
		}
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		h.append(" <th rowspan='2'>Brand</th> \n");
		h.append(" <th rowspan='2'>SKU</th>");
		h.append(" <th rowspan='2'>Description</th>");
		h.append(" <th rowspan='2'>Sale 90 Days [CTN]</th>");
		h.append(" <th rowspan='2'>Unit Price</th>");
		h.append(" <th colspan='2'>Daily Mean Sale</th>");
		h.append(" <th colspan='2'>B&W Stock Bal.</th>");
		h.append(" <th rowspan='2'>Stock Coverage Days [Day]</th>");
		h.append(" <th rowspan='2'>Stock Coverage Days By brand [Day]</th>");
		h.append("</tr> \n");
		h.append("<tr> \n");
		h.append(" <th>[CTN]</th>");
		h.append(" <th>[THB]</th>");
		h.append(" <th>[CTN]</th>");
		h.append(" <th>[THB]</th>");
		h.append("</tr> \n");
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
	private  StringBuffer genRowTable(StockOnhandBean head,boolean excel,StockOnhandBean item
			,Map<String, String> mapRowSpanByBrand,boolean isLastRowOfBrand) throws Exception{
		StringBuffer h = new StringBuffer("");
		String classText = "text";
		String classCurrency = "currency";
		h.append("<tr> \n");
		if(excel){
			if(isLastRowOfBrand){
				classText = "text_with_bt_line";
				classCurrency = "currency_with_bt_line";
			}
			h.append("<td class='"+classText+"' width='3%'>"+item.getBrand()+"</td> \n");
			h.append("<td class='"+classText+"' width='4%'>"+item.getProductCode()+"</td> \n");
			h.append("<td class='"+classText+"' width='13%'>"+item.getDescription()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='6%'>"+item.getSale90Day()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getUnitPrice()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getDailyMeanSaleCTN()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='7%'>"+item.getDailyMeanSaleTHB()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getBwStockBalCTN()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='7%'>"+item.getBwStockBalTHB()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='7%'>"+item.getStockCoverageDay()+"</td> \n");
			if( !item.getStockCoverageDayByBrand().equals("")){
			  h.append("<td class='currency_bold_with_bt_line' width='7%' rowspan='"+mapRowSpanByBrand.get(item.getBrand())+"'>"+item.getStockCoverageDayByBrand()+"</td> \n");
			}
		}else{
			classText = "td_text";
			classCurrency = "td_number";
			if(isLastRowOfBrand){
				classText = "td_text_with_bt_line";
				classCurrency = "td_number_with_bt_line";
			}
			h.append("<td class='"+classText+"' width='3%'>"+item.getBrand()+"</td> \n");
			h.append("<td class='"+classText+"' width='4%'>"+item.getProductCode()+"</td> \n");
			h.append("<td class='"+classText+"' width='13%'>"+item.getDescription()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='6%'>"+item.getSale90Day()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getUnitPrice()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getDailyMeanSaleCTN()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='7%'>"+item.getDailyMeanSaleTHB()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='5%'>"+item.getBwStockBalCTN()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='7%'>"+item.getBwStockBalTHB()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='7%'>"+item.getStockCoverageDay()+"</td> \n");
			if( !item.getStockCoverageDayByBrand().equals("")){
			   h.append("<td class='td_number_bold_with_bt_line' width='7%' rowspan='"+mapRowSpanByBrand.get(item.getBrand())+"'>"+item.getStockCoverageDayByBrand()+"</td> \n");
			}
		}
		h.append("</tr> \n");
		
		return h;
	}
	
	
}
