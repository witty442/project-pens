package com.isecinc.pens.web.summary.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.sql.ReportOnhandAsOfKingSQL;
import com.isecinc.pens.sql.ReportOnhandAsOfRobinsonSQL;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;


public class OpenBillRobinsonReportAction {
	private static Logger logger = Logger.getLogger("PENS");
	

public static SummaryForm process(HttpServletRequest request,User user,SummaryForm summaryForm,String typeExport) throws Exception{
	Connection conn = null;
	OnhandSummary cri = summaryForm.getBean();
	Statement stmt = null;
	ResultSet rst = null;
	Map<String, OnhandSummary> sumByCustMap = new HashMap<String, OnhandSummary>();
	Map<String, String> splitByCustMap = new HashMap<String, String>();
 	double totalQtyByGP = 0;
	double totalAmountByGP = 0;
	OnhandSummary mainGroupItem = null;
	OnhandSummary subGroupItem = null;
	StringBuffer h = new StringBuffer("");
	String orderLotNoAll = "";
	boolean genGrandTotal = false;
	 String text_center = "td_text_center";
	 String text = "td_text";
	 String number = "td_number";
	 String number_bold = "td_number_bold";
	 String currency = "td_number";
	 String currency_bold = "td_number_bold";
	 String border ="0";
	try{
		//set Page Name
		summaryForm.setPage("openBillRobinsonReport");
		//Init Connection
		conn = DBConnection.getInstance().getConnection();
		
		OnhandSummary bean = searchOpenBillRobinsonAll(conn, cri, user);
		//validate Error 
		if(bean != null && bean.getErrorList() != null && bean.getErrorList().size() >0){
			bean.setAsOfDate(cri.getAsOfDate());
			summaryForm.setBean(bean);
			return summaryForm;
		}else{
			 /** Case Export to Excel **/
			 logger.debug("typeExport:"+typeExport);
			 if(bean.getItemsList() != null && bean.getItemsList().size() >0){
				 if("EXCEL".equalsIgnoreCase(typeExport)){
				    h.append(ExcelHeader.EXCEL_HEADER);//excel style
				  
				    //Gen Header report
				    h.append("<table id='tblProductHead' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
					h.append("<tr> \n");
					h.append("<td colspan='9' align='center'><font size='3'><b>ใบแจ้งเปิดบิล ของโรบินสัน</b> </font></td> \n");
					h.append("</tr> \n");
					h.append("<tr> \n");
					h.append("<td colspan='9'><b>วันที่เปิด Order โรงงาน / วันที่ Issue จาก PIC  :  "+cri.getAsOfDate()+"</b></td> \n");
					h.append("</tr> \n");
					h.append("</table> \n");
					
					border ="1";
				    text_center = "text";
				    text = "text";
				    number = "num";
				    number_bold = "num_bold";
				    currency = "currency";
				    currency_bold = "currency_bold";
				 }
			 }
		}
		
		if(bean.getItemsList() != null && bean.getItemsList().size() >0){
			h.append("<table id='tblProduct' align='center' border='"+border+"' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr> \n");
			h.append("<th>Customer Code</th> \n");
			h.append("<th>Customer Name</th> \n");
			h.append("<th>GP(%)</th> \n");
			h.append("<th>Order Lot No/Request No</th> \n");
			h.append("<th>Group Code</th> \n");
			h.append("<th>Pens Item Desc</th> \n");
			h.append("<th>Total</th> \n");
			h.append("<th>ราคาส่ง</th> \n");
			h.append("<th>รวม</th> \n");
			h.append("</tr> \n");
			for(int i=0;i<bean.getItemsList().size();i++){
				mainGroupItem =bean.getItemsList().get(i);
			    
				if(mainGroupItem.getSubList() != null && mainGroupItem.getSubList().size() > 0){
					//reset by Group
					totalQtyByGP = 0;
					totalAmountByGP =0;
					
					orderLotNoAll = getOrderLotNoFromList(mainGroupItem.getSubList());
					
					//merg product and sum- N..1 in 1 
					mainGroupItem.setSubList(mergeProductInSubList(mainGroupItem.getSubList()));
					
					for(int s=0;s<mainGroupItem.getSubList().size();s++){
						subGroupItem = mainGroupItem.getSubList().get(s);
						h.append("<tr class='lineE'> \n");
						if(s==0){
							h.append("<td class='"+text_center+"' width='8%'>"+mainGroupItem.getStoreCode()+"</td> \n");
							h.append("<td class='"+text+"' width='17%'>"+mainGroupItem.getStoreName()+"</td> \n");
							h.append("<td class='"+text_center+"' width='10%'>"+mainGroupItem.getGp()+"</td> \n");
							h.append("<td class='"+text+"' width='15%'>"+orderLotNoAll+"</td> \n");
						}else{
							h.append("<td  width='8%'></td> \n");
							h.append("<td width='17%'></td> \n");
							h.append("<td width='10%'></td> \n");
							h.append("<td width='15%'></td> \n");
						}
						
						h.append("<td class='"+text_center+"' width='10%'>"+subGroupItem.getGroup()+"</td> \n");
						h.append("<td class='"+text_center+"' width='10%'>"+subGroupItem.getPensItem()+"</td> \n");
						h.append("<td class='"+number+"' width='10%'>"+subGroupItem.getSaleInQty()+"</td> \n");
						h.append("<td class='"+currency+"' width='10%'>"+subGroupItem.getWholePriceVat()+"</td> \n");
						h.append("<td class='"+currency+"' width='10%'>"+subGroupItem.getTotalSaleAmount()+"</td> \n");
						h.append("</tr> \n");
						
						//summary all
						totalQtyByGP += Utils.convertStrToInt(subGroupItem.getSaleInQty());
						totalAmountByGP += Utils.convertStrToDouble(subGroupItem.getTotalSaleAmount());
						
						//sum by cust
						if(sumByCustMap.get(subGroupItem.getStoreCode()) ==null){
							OnhandSummary sumByCust = new OnhandSummary();
							sumByCust.setTotalQtyByCust(Utils.convertStrToInt(subGroupItem.getSaleInQty()));
							sumByCust.setTotalAmountByCust(Utils.convertStrToDouble(subGroupItem.getTotalSaleAmount()));
							
							sumByCustMap.put(subGroupItem.getStoreCode(), sumByCust);
						}else{
							OnhandSummary sumByCust = sumByCustMap.get(subGroupItem.getStoreCode());
							sumByCust.setTotalQtyByCust(sumByCust.getTotalQtyByCust()+Utils.convertStrToInt(subGroupItem.getSaleInQty()));
							sumByCust.setTotalAmountByCust(sumByCust.getTotalAmountByCust()+Utils.convertStrToDouble(subGroupItem.getTotalSaleAmount()));
							
							sumByCustMap.put(subGroupItem.getStoreCode(), sumByCust);
						}
						
					}//for subList
					
					//summary by group customerCode,gp
					h.append("<tr class='row_hilight'> \n");
					/*h.append("<td></td> \n");
					h.append("<td></td> \n");
					h.append("<td></td> \n");*/
					h.append("<td  colspan='4' align='right'>Total by GP : "+mainGroupItem.getGp()+"</td> \n");
					h.append("<td></td> \n");
					h.append("<td></td> \n");
					h.append("<td class='"+number_bold+"'>"+Utils.decimalFormat(totalQtyByGP,Utils.format_current_no_disgit)+"</td> \n");
					h.append("<td></td> \n");
					h.append("<td class='"+currency_bold+"'>"+Utils.decimalFormat(totalAmountByGP,Utils.format_current_2_disgit)+"</td> \n");
					h.append("</tr> \n");
				}//if subList is not null
				
				//summary By Cust
				genGrandTotal = false;
				
				if(i == bean.getItemsList().size()-1){//last row 
					genGrandTotal = true;
				}else if(i <= bean.getItemsList().size()-2){
				   OnhandSummary nextCustBean = bean.getItemsList().get(i+1);
				   logger.debug("compare:i["+(i+1)+"]");
				   logger.debug("compare:"+mainGroupItem.getStoreCode()+":"+nextCustBean.getStoreCode());
				   
				   if( !mainGroupItem.getStoreCode().equalsIgnoreCase(nextCustBean.getStoreCode())){
					   genGrandTotal = true;
				   }
				}
				
				if(genGrandTotal){
					h.append("<tr class='row_hilight'> \n");
					/*h.append("<td></td> \n");
					h.append("<td></td> \n");*/
					h.append("<td colspan='3' align='right'>Grand Total</td> \n");
					h.append("<td></td> \n");
					h.append("<td></td> \n");
					h.append("<td></td> \n");
					h.append("<td  class='"+number_bold+"'>"+Utils.decimalFormat(sumByCustMap.get(mainGroupItem.getStoreCode()).getTotalQtyByCust(),Utils.format_current_no_disgit)+"</td> \n");
					h.append("<td></td> \n");
					h.append("<td  class='"+currency_bold+"'>"+Utils.decimalFormat(sumByCustMap.get(mainGroupItem.getStoreCode()).getTotalAmountByCust(),Utils.format_current_2_disgit)+"</td> \n");
					h.append("</tr> \n");
				}
				
			}//for mainList
			h.append("</table> \n");
		}
		
		//debug 
		//FileUtil.writeFile("d://temp/data.html", h.toString(), "UTF-8");
		
		summaryForm.setDataHTML(h);
	}catch(Exception e){
		logger.error(e.getMessage(),e);
	}finally{
		if(stmt != null){
			stmt.close();stmt=null;
		}
		if(rst != null){
			rst.close();rst=null;
		}
		if(conn != null){
			conn.close();conn=null;
		}
	}
	return summaryForm;
 }
	
	public static OnhandSummary searchOpenBillRobinsonAll(Connection conn,OnhandSummary cri,User user) throws Exception{
		    Statement stmt = null;
			ResultSet rst = null;
			int mainIndexList = -1;
			List<OnhandSummary> mainList = new ArrayList<OnhandSummary>();
			List<OnhandSummary> subList = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			List<OnhandSummary> errorList = new ArrayList<OnhandSummary>();
			Map<String, String> pensItemErrorMap = new HashMap<String, String>();
			//key storeCode+orderLotNo+gp
			String keyMain = "";
			Map<String, List<OnhandSummary>> mainGroupMap = new HashMap<String, List<OnhandSummary>>();
			OnhandSummary bean = new OnhandSummary();
			try {
				sql.append("SELECT A.* ");
				sql.append(",(select max(interface_value) from pensbi.pensbme_mst_reference M \n");
				sql.append("  where reference_code ='Store' \n");
				sql.append("  and M.pens_value = A.store_code) as store_code_oracle \n");
				sql.append(",(select max(M.pens_desc) from pensbi.pensbme_mst_reference M \n");
				sql.append("   where M.reference_code ='Store' and M.pens_value=A.store_code) as store_name \n");
				sql.append("FROM ( \n");
				
				//PENSBME ORDER
				sql.append(" select O.store_code,O.order_lot_no,O.group_code,O.item as m_pens_item,\n");
				sql.append(" GP.gp,GP.pens_item,GP.whole_price_vat ,nvl(sum(O.qty),0) as qty \n");
				sql.append(" FROM PENSBI.pensbme_order O  \n");
				sql.append(" LEFT OUTER JOIN PENSBI.PENSBME_ITEMBY_GP GP \n");
				sql.append(" ON O.item = GP.pens_item \n");
				sql.append(" WHERE 1=1 \n");
				//robinson only
				sql.append(" and O.store_type = '100002' \n");
				if( !Utils.isNull(cri.getAsOfDate()).equals("")){
					Date dateObj = Utils.parse(cri.getAsOfDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					sql.append(" and O.order_date = to_date('"+Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') ");
				}
				
				//for test 2 orderLotNo 
				/*sql.append("and O.order_date = to_date('13/02/2019','dd/mm/yyyy') \n");
				sql.append("and O.store_code ='020047-71' \n");*/
				
				sql.append(" Group by O.store_code,O.order_lot_no,O.group_code,O.item \n");
				sql.append("         ,GP.gp,GP.pens_item,GP.whole_price_vat \n");
				
				sql.append(" UNION ALL \n");
				
				//PENSBME_PICK_STOCK
				sql.append(" select H.store_code,H.issue_req_no as order_lot_no,O.group_code,O.pens_item as m_pens_item,\n");
				sql.append(" GP.gp,GP.pens_item,GP.whole_price_vat ,nvl(count(*),0) as qty \n");
				sql.append(" FROM PENSBI.PENSBME_PICK_STOCK H,\n");
				sql.append(" PENSBI.PENSBME_PICK_STOCK_I O \n");
				sql.append(" LEFT OUTER JOIN PENSBI.PENSBME_ITEMBY_GP GP \n");
				sql.append("  ON O.pens_item = GP.pens_item \n");
				sql.append(" WHERE 1=1 \n");
				sql.append(" AND O.issue_req_no = H.issue_req_no \n");
				sql.append(" AND H.issue_req_status ='I' \n");
				//robinson only
				sql.append(" and H.cust_group = '100002' \n");
				if( !Utils.isNull(cri.getAsOfDate()).equals("")){
					Date dateObj = Utils.parse(cri.getAsOfDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					sql.append(" and H.confirm_issue_date = to_date('"+Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') ");
				}
				
				sql.append(" Group by H.store_code,H.issue_req_no,O.group_code,O.pens_item  \n");
				sql.append("         ,GP.gp,GP.pens_item,GP.whole_price_vat \n");
				
                sql.append(" UNION ALL \n");
				
				//PENSBME_STOCK_ISSUE
				sql.append(" select H.customer_no as store_code,H.issue_req_no as order_lot_no,O.group_code,O.pens_item as m_pens_item,\n");
				sql.append(" GP.gp,GP.pens_item,GP.whole_price_vat ,nvl(sum(O.issue_qty),0) as qty \n");
				sql.append(" FROM PENSBI.PENSBME_STOCK_ISSUE H,\n");
				sql.append(" PENSBI.PENSBME_STOCK_ISSUE_ITEM O \n");
				sql.append(" LEFT OUTER JOIN PENSBI.PENSBME_ITEMBY_GP GP\n");
				sql.append(" ON O.pens_item = GP.pens_item \n");
				sql.append(" WHERE  O.issue_req_no = H.issue_req_no \n");
				sql.append(" AND H.status ='I' \n");
				//robinson only
				sql.append(" and H.cust_group = '100002' \n");
				if( !Utils.isNull(cri.getAsOfDate()).equals("")){
					Date dateObj = Utils.parse(cri.getAsOfDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					sql.append(" and H.status_date = to_date('"+Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') ");
				}
				
				sql.append(" Group by H.customer_no,H.issue_req_no,O.group_code,O.pens_item \n");
				sql.append("         ,GP.gp,GP.pens_item,GP.whole_price_vat \n");
				
				sql.append(")A \n");
				
				sql.append(" order by to_number(REPLACE(A.store_code,'-')),A.gp, A.order_lot_no ,A.group_code,A.pens_item asc \n");
				
				logger.debug("sql:"+sql.toString());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					item.setStoreCode(Utils.isNull(rst.getString("store_code_oracle")));
					item.setStoreName(Utils.isNull(rst.getString("store_name")));
					item.setGp(Utils.isNull(rst.getString("gp")));
					
					item.setOrderLotNo(Utils.isNull(rst.getString("order_lot_no")));
					item.setGroup(Utils.isNull(rst.getString("group_code")));
					item.setPensItem(Utils.isNull(rst.getString("pens_item")));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
					item.setWholePriceVat(Utils.decimalFormat(rst.getDouble("whole_price_vat"),Utils.format_current_2_disgit));
					item.setTotalSaleAmount(Utils.decimalFormat(rst.getDouble("whole_price_vat")*rst.getDouble("qty"),Utils.format_current_2_disgit));
					
					//key storeCode+gp
					keyMain = item.getStoreCode()+"_"+item.getGp();
					if(mainGroupMap.get(keyMain) != null){
						//add subGroupList from prevList
						subList = mainGroupMap.get(keyMain);
						
						subList.add(item);//add
						item.setSubList(subList);
						
						//logger.debug("mainIndexLsit:"+mainIndexList);
						mainList.set(mainIndexList,item);
						
						//set for check group
						mainGroupMap.put(keyMain, subList);
					}else{
						//add new subGroupList
						subList = new ArrayList<OnhandSummary>();
						subList.add(item);
						item.setSubList(subList);
						
						mainList.add(item);
						
						//set for check group
						mainGroupMap.put(keyMain, subList);
						//count by group (custCode,gp)
						mainIndexList++;
					}
					
					//check error
					if(Utils.isNull(rst.getString("pens_item")).equals("")){
						if(pensItemErrorMap.get(Utils.isNull(rst.getString("m_pens_item"))) ==null){
						item.setPensItem(Utils.isNull(rst.getString("m_pens_item")));
						   errorList.add(item);
						}
						pensItemErrorMap.put(Utils.isNull(rst.getString("m_pens_item")),Utils.isNull(rst.getString("m_pens_item")));
					}
					
				}//while
				bean.setItemsList(mainList);
				bean.setErrorList(errorList);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
	    }
	
	public static StringBuffer genErrorListTable(List<OnhandSummary> errorList) throws Exception{
		StringBuffer h = new StringBuffer();
		try{
			 //Gen Header report
			if(errorList != null && errorList.size() >0){
			    h.append("<table id='tblProductHead' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth' width='50%'> \n");
			    h.append("<tr> \n");
			    //h.append(" <td align='center' ><b>ไม่พบข้อมูล Pens Item ใน MASTER TABLE PENSBME_ITEMBY_GP</b> </td>");
			    h.append(" <th align='center'>OrderLotNo/RequestNo</th>");
			    h.append(" <th align='center'>GroupCode</th>");
			    h.append(" <th align='center'>Pens Item</th>");
			    h.append("</tr> \n");
			   
			    for(int i=0;i<errorList.size();i++){
					OnhandSummary s = errorList.get(i);
					h.append("<tr> \n");
					h.append(" <td align='center'>"+s.getOrderLotNo()+"</td> \n");
					h.append(" <td align='center'>"+s.getGroup()+"</td> \n");
					h.append(" <td align='center'>"+s.getPensItem()+"</td> \n");
				    h.append("</tr> \n");
				}
			    
				h.append("</table> \n");
			}
			return h;
		}catch(Exception e){
			throw e;
		}
	}
	private static String getOrderLotNoFromList(List<OnhandSummary> subList) throws Exception{
		String orderLotNoAll = "";
		Map<String, String> orderLotNoDup = new HashMap<String, String>();
		if(subList != null && subList.size() > 0){
			orderLotNoAll = "";
			for(int s=0;s<subList.size();s++){
				if(orderLotNoDup.get(subList.get(s).getOrderLotNo()) ==null){
					orderLotNoAll += subList.get(s).getOrderLotNo()+",";
				}
				orderLotNoDup.put(subList.get(s).getOrderLotNo(), subList.get(s).getOrderLotNo());
			}
			if(orderLotNoAll.length()>0){
				orderLotNoAll = orderLotNoAll.substring(0,orderLotNoAll.length()-1);
			}
		}
		return orderLotNoAll;
	}
	//wait
	private static List<OnhandSummary> mergeProductInSubList(List<OnhandSummary> subList) throws Exception{
		List<OnhandSummary> newSubList = new ArrayList<OnhandSummary>();
		Map<String, Integer> productDup = new HashMap<String, Integer>();
		int index = -1;
		if(subList != null && subList.size() > 0){
			for(int s=0;s<subList.size();s++){
				OnhandSummary currItem = subList.get(s);
				if(productDup.get(currItem.getGroup()+currItem.getPensItem()) ==null){
					newSubList.add(currItem);
					index++;
					productDup.put(currItem.getGroup()+currItem.getPensItem(),index);
				}else{
					int prevIndex= productDup.get(currItem.getGroup()+currItem.getPensItem());
					OnhandSummary prevItem =subList.get(prevIndex); 
					//sum new Line to prevItem
					int saleInQty = (Utils.convertStrToInt(prevItem.getSaleInQty())+ Utils.convertStrToInt(currItem.getSaleInQty()));
					double totalAmountQty = (Utils.convertStrToDouble(prevItem.getTotalSaleAmount())+ Utils.convertStrToDouble(currItem.getTotalSaleAmount()));
					
					prevItem.setSaleInQty(Utils.decimalFormat(saleInQty,Utils.format_current_no_disgit) );
					prevItem.setTotalSaleAmount(Utils.decimalFormat(totalAmountQty,Utils.format_current_no_disgit) );
					
					newSubList.set(prevIndex,prevItem);
				}
			}//for
		}
		return newSubList;
	}
}
