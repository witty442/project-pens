package com.isecinc.pens.web.shop.sub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class CHINAStockOnhandAction {
 private static Logger logger = Logger.getLogger("PENS");
 private static int pageSize =50;
 
 public static ShopForm search(HttpServletRequest request, ShopForm f,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<ShopBean> pos = new ArrayList<ShopBean>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		int currPage = 1;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			if("newsearch".equalsIgnoreCase(action)){
				conn = DBConnection.getInstance().getConnectionApps();
				
				//Get Init date
				Date initDate = new SummaryDAO().searchInitDateMTT(conn,f.getBean().getCustGroup());
				//gen sql
				sql = genSQL(conn,f,initDate);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					ShopBean item = new ShopBean();
					item.setGroupCode(Utils.isNull(rst.getString("group_type")));
					item.setPensItem(Utils.isNull(rst.getString("pens_item")));
					item.setBarcode(rst.getString("barcode"));
					item.setStyle(rst.getString("material_master"));
			
					item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("INIT_SALE_QTY"),Utils.format_current_2_disgit));
					item.setTransInQty(Utils.decimalFormat(rst.getDouble("TRANS_IN_QTY"),Utils.format_current_2_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("SALE_OUT_QTY"),Utils.format_current_2_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("SALE_RETURN_QTY"),Utils.format_current_2_disgit));
					item.setAdjustQty("0");//wait for spec
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("ONHAND_QTY"),Utils.format_current_2_disgit));
					
					pos.add(item);
				}//while
	
				if(pos != null && pos.size() >0){
					f.setResults(pos);
					//request.getSession().setAttribute("summary" ,item);
					//calc Paging
					f.setTotalRecord(f.getResults().size());
					f.setTotalPage(Utils.calcTotalPage(f.getTotalRecord(), pageSize));
					f.setPageSize(pageSize);
					f.setCurrPage(1);
					//calc startRec endRec
					int startRec = ((currPage-1)*pageSize);
					int endRec = (currPage * pageSize);
				    if(endRec > f.getTotalRecord()){
					   endRec = f.getTotalRecord();
				    }
				    f.setStartRec(startRec);
				    f.setEndRec(endRec);
				}else{
					f.setResults(null);
					request.getSession().setAttribute("summary" ,null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}else{
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				f.setCurrPage(currPage);
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize);
				int endRec = (currPage * pageSize);
			    if(endRec > f.getTotalRecord()){
				   endRec = f.getTotalRecord();
			    }
			    f.setStartRec(startRec);
			    f.setEndRec(endRec);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return f;
    }
 
 public static StringBuffer exportToExcel(HttpServletRequest request, ShopForm form,User user,List<ShopBean> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="10";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			/*if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="11";
			}*/
			//Header
			h.append("<table border='1'> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"'><b>รายงาน Stock Onhand at I'm CHINA</b></td> \n");
			h.append(" </tr> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"' ><b>วันที่ขาย (As Of Date):"+form.getBean().getAsOfDate()+"</b></td> \n");
			h.append(" </tr> \n");
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>Group</th> \n");
				  h.append("<th>Pens Item</th> \n");
				  h.append("<th>Material Master</th> \n");
				  h.append("<th>Barcode</th> \n");
				  h.append("<th>Initial Stock</th> \n");
				  h.append("<th>Trans In Qty</th> \n");
				  h.append("<th>Sale Out Qty</th> \n");
				  h.append("<th>Return Qty</th> \n");
				  h.append("<th>Adjust Qty</th> \n");
				  h.append("<th>Onhand Qty</th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					ShopBean s = (ShopBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getGroupCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getStyle()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getTransInQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				/** Summary **/
				ShopBean s = (ShopBean)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				 
				 /* if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='currency_bold'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
				h.append("</tr>");*/
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
 
 public static StringBuilder genSQL(Connection conn,ShopForm f,Date initDate) throws Exception{
		StringBuilder sql = new StringBuilder();
		String storeType ="SHOP";
		ShopBean c = f.getBean();//Get Criteria
		try {
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getAsOfDate()).equals("")){
				Date d = DateUtil.parse(c.getAsOfDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = DateUtil.stringValue(d, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			String initDateStr ="";
			if( initDate != null){
				initDateStr = DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			
			sql.append("\n SELECT A.* FROM(");
			sql.append("\n SELECT M.*");
			sql.append("\n , NVL(INIT_MTT.INIT_SALE_QTY,0) AS INIT_SALE_QTY");
			sql.append("\n , NVL(TRANS_IN.TRANS_IN_QTY,0) AS TRANS_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(TRANS_IN.TRANS_IN_QTY,0)) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
			       /** INIT MTT */
					sql.append("\n SELECT DISTINCT");
				    sql.append("\n L.GROUP_CODE as group_type,L.PENS_ITEM,L.MATERIAL_MASTER ,L.BARCODE ");
					sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBI.PENSBME_MTT_ONHAND_INIT_STK L");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
					sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   where M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
					sql.append("\n   AND M.pens_value ='"+f.getBean().getCustGroup()+"' ");
			        sql.append("\n  ) M ");
					sql.append("\n WHERE H.cust_no = L.cust_no  ");
					sql.append("\n AND M.customer_code = H.cust_no  ");
					sql.append("\n AND H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
					if( !Utils.isNull(initDateStr).equals("")){
						 sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
						sql.append("\n AND L.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND L.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND L.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					
					sql.append("\n UNION ");
					/** ORDER BME (TRANS_IN)*/
					sql.append("\n SELECT DISTINCT");
					sql.append("\n L.GROUP_CODE as group_type,L.ITEM AS PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE ");
					sql.append("\n FROM PENSBI.PENSBME_ORDER L");
					sql.append("\n ,(" );
					sql.append("\n   SELECT DISTINCT MP.PENS_VALUE as PENS_ITEM ");
					sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
					sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
					sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
					sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
					sql.append("\n ) MP ");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND L.BARCODE = MP.BARCODE ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.store_code"));
					sql.append("\n AND L.store_code ='"+f.getBean().getCustGroup()+"' ");
					if(initDate != null){
						 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
						sql.append("\n AND L.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					sql.append("\n UNION ");
					/** Pick Stock **/
					sql.append("\n SELECT DISTINCT I.group_code as group_type,I.PENS_ITEM,I.material_master");
					sql.append("\n ,(select max(X.interface_desc) from PENSBI.PENSBME_MST_REFERENCE X ");
					sql.append("\n   WHERE X.interface_value=I.material_master ");
					sql.append("\n   AND REFERENCE_CODE in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"') ");
					sql.append("\n   AND X.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
					sql.append("\n  ) as barcode ");
					sql.append("\n FROM PENSBI.PENSBME_PICK_STOCK M ,PENSBI.PENSBME_PICK_STOCK_I I ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND M.issue_req_no = I.issue_req_no ");
					sql.append("\n AND M.issue_req_status ='"+PickConstants.STATUS_ISSUED+"'");
					sql.append("\n AND M.store_code ='"+f.getBean().getCustGroup()+"' ");
					if(initDate != null){
						 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND I.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND I.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					sql.append("\n UNION ");
					/** Pick Stock Issue **/
					sql.append("\n SELECT DISTINCT I.group_code as group_type,I.PENS_ITEM,I.material_master");
					sql.append("\n ,(select max(X.interface_desc) from PENSBI.PENSBME_MST_REFERENCE X ");
					sql.append("\n   WHERE X.interface_value=I.material_master ");
					sql.append("\n   AND REFERENCE_CODE in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"') ");
					sql.append("\n   AND X.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
					sql.append("\n  ) as barcode ");
					sql.append("\n FROM PENSBI.PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND M.issue_req_no = I.issue_req_no ");
					sql.append("\n AND M.status ='"+PickConstants.STATUS_ISSUED+"'");
					sql.append("\n AND M.CUSTOMER_NO ='"+f.getBean().getCustGroup()+"' ");
					if(initDate != null){
						 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND I.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND I.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					
					sql.append("\n UNION");
					
					/** ORDER ORACLE(SALEOUT) */
					sql.append("\n SELECT DISTINCT");
					sql.append("\n MP.GROUP_CODE as group_type,MP.PENS_ITEM ,MP.MATERIAL_MASTER,MP.BARCODE ");
					sql.append("\n FROM APPS.XXPENS_OM_SHOP_ORDER_MST L,APPS.XXPENS_OM_SHOP_ORDER_DT D ");
					sql.append("\n ,(" );
					sql.append("\n   SELECT DISTINCT I.inventory_item_id as product_id");
					sql.append("\n   ,MP.pens_desc2 as group_code ");
					sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
					sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
					sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,APPS.XXPENS_OM_ITEM_MST_V I");
					sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
					sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
					sql.append("\n   AND MP.pens_value =I.segment1 ");
					sql.append("\n ) MP ");
					sql.append("\n WHERE L.ORDER_NUMBER = D.ORDER_NUMBER ");
					sql.append("\n AND D.product_id = MP.product_id ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUSTOMER_NUMBER"));
					sql.append("\n AND L.CUSTOMER_NUMBER ='"+f.getBean().getCustGroup()+"' ");
					if(initDate != null){
						 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
						sql.append("\n AND MP.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND MP.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND MP.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
				
					sql.append("\n UNION");
					
					/** Return **/
					sql.append("\n SELECT DISTINCT");
					sql.append("\n D.GROUP_CODE as group_type,D.PENS_ITEM ,D.MATERIAL_MASTER,D.BARCODE ");
					sql.append("\n FROM PENSBI.PENSBME_PICK_JOB J,PENSBI.PENSBME_PICK_BARCODE L,PENSBI.PENSBME_PICK_BARCODE_ITEM D");
					sql.append("\n WHERE J.JOB_ID = L.JOB_ID ");
					sql.append("\n AND L.BOX_NO = D.BOX_NO ");
					sql.append("\n AND L.JOB_ID = D.JOB_ID ");
					sql.append("\n AND J.STATUS ='"+PickConstants.STATUS_CLOSE+"'");
					sql.append("\n AND L.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
					sql.append("\n AND D.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "J.STORE_CODE"));
					sql.append("\n AND J.STORE_CODE ='"+f.getBean().getCustGroup()+"' ");
					if(initDate != null){
						 sql.append("\n AND J.close_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
						sql.append("\n AND D.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND D.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND D.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND D.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND D.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					sql.append("\n )AA");
        sql.append("\n )M ");
        sql.append("\n LEFT OUTER JOIN(	 ");
	       /**** INIT MTT STOCK *****************/
   		    sql.append("\n SELECT L.GROUP_CODE as group_type ,L.pens_item ,L.MATERIAL_MASTER ,L.BARCODE");
			sql.append("\n ,SUM(QTY) AS INIT_SALE_QTY ");
			sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBI.PENSBME_MTT_ONHAND_INIT_STK L");
			sql.append("\n ,( ");
			sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
			sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
			sql.append("\n   WHERE M.reference_code ='Store' ");
			//Filter By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			sql.append("\n   AND M.pens_value ='"+f.getBean().getCustGroup()+"' ");
	        sql.append("\n  ) M ");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n and H.cust_no = L.cust_no  ");
			sql.append("\n AND M.customer_code = H.cust_no  ");
			sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
			if( !Utils.isNull(initDateStr).equals("")){
				 sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
				sql.append("\n AND L.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
			}
			if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
				sql.append("\n AND L.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
				sql.append("\n AND L.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			sql.append("\n  GROUP BY L.GROUP_CODE,L.pens_item,L.MATERIAL_MASTER ,L.BARCODE");
			sql.append("\n )INIT_MTT ");
			sql.append("\n ON M.pens_item = INIT_MTT.pens_item ");	 
			sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
			
		   sql.append("\n LEFT OUTER JOIN(	 ");
		   
		   /******** TRANS_IN *****************/
		   sql.append("\n SELECT T.group_type,T.PENS_ITEM ,T.MATERIAL_MASTER,T.BARCODE");
	        sql.append("\n ,NVL(SUM(T.TRANS_IN_QTY),0) AS TRANS_IN_QTY FROM(");
		    /** ORDER **/
		    sql.append("\n SELECT L.GROUP_CODE as group_type,L.ITEM AS PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE");
	        sql.append("\n ,NVL(SUM(L.QTY),0) AS TRANS_IN_QTY ");
	        sql.append("\n FROM PENSBI.PENSBME_ORDER L");
			sql.append("\n ,(" );
			sql.append("\n   SELECT DISTINCT MP.PENS_VALUE as PENS_ITEM ");
			sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
			sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
			sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
			sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
			sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
			sql.append("\n ) MP ");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n AND L.BARCODE = MP.BARCODE ");
			//Filter By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.store_code"));
			sql.append("\n AND L.store_code ='"+f.getBean().getCustGroup()+"' ");
			if(initDate != null){
				 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}else{
				 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
				sql.append("\n AND L.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
			}
			if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
				sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
				sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			sql.append("\n  GROUP BY L.GROUP_CODE,L.ITEM ,MP.MATERIAL_MASTER,L.BARCODE");
			
			sql.append("\n  UNION ALL");
			
			/** Pick Stock **/
			sql.append("\n SELECT I.group_code as group_type,I.PENS_ITEM,I.material_master");
			sql.append("\n ,(select max(X.interface_desc) from PENSBI.PENSBME_MST_REFERENCE X ");
			sql.append("\n   WHERE X.interface_value=I.material_master ");
			sql.append("\n   AND REFERENCE_CODE in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"') ");
			sql.append("\n   AND X.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
			sql.append("\n  ) as barcode ");
			sql.append("\n ,NVL(COUNT(*),0) AS TRANS_IN_QTY ");
			sql.append("\n FROM PENSBI.PENSBME_PICK_STOCK M ,PENSBI.PENSBME_PICK_STOCK_I I ");
			sql.append("\n WHERE 1=1   ");
			sql.append("\n AND M.issue_req_no = I.issue_req_no ");
			sql.append("\n AND M.issue_req_status ='"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n AND M.store_code ='"+f.getBean().getCustGroup()+"' ");
			if(initDate != null){
				 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}else{
				 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
				sql.append("\n AND I.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
				sql.append("\n AND I.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			sql.append("\n GROUP BY  I.group_code,I.PENS_ITEM,I.material_master");
			
			sql.append("\n UNION ALL");
			
			/** Pick Stock Issue **/
			sql.append("\n SELECT I.group_code as group_type,I.PENS_ITEM,I.material_master");
			sql.append("\n ,(select max(X.interface_desc) from PENSBI.PENSBME_MST_REFERENCE X ");
			sql.append("\n   WHERE X.interface_value=I.material_master ");
			sql.append("\n   AND REFERENCE_CODE in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"') ");
			sql.append("\n   AND X.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
			sql.append("\n  ) as barcode ");
			sql.append("\n ,NVL(SUM(I.issue_qty),0) AS TRANS_IN_QTY ");
			sql.append("\n FROM PENSBI.PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
			sql.append("\n WHERE 1=1   ");
			sql.append("\n AND M.issue_req_no = I.issue_req_no ");
			sql.append("\n AND M.status ='"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n AND M.CUSTOMER_NO ='"+f.getBean().getCustGroup()+"' ");
			if(initDate != null){
				 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}else{
				 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
				sql.append("\n AND I.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
				sql.append("\n AND I.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			sql.append("\n    GROUP BY  I.group_code,I.PENS_ITEM,I.material_master");
			sql.append("\n  )T GROUP BY T.group_type,T.PENS_ITEM ,T.MATERIAL_MASTER,T.BARCODE");
			sql.append("\n )TRANS_IN ");
			sql.append("\n ON  M.pens_item = TRANS_IN.pens_item ");	 
			sql.append("\n AND M.group_type = TRANS_IN.group_type ");
	
			sql.append("\n LEFT OUTER JOIN( ");
			 /******** SALE_OUT *****************/
				sql.append("\n SELECT");
				sql.append("\n MP.GROUP_CODE as group_type,MP.PENS_ITEM ,MP.MATERIAL_MASTER,MP.BARCODE ");
				sql.append("\n ,NVL(SUM(D.ORDERED_QUANTITY),0) as SALE_OUT_QTY");
				sql.append("\n FROM APPS.XXPENS_OM_SHOP_ORDER_MST L,APPS.XXPENS_OM_SHOP_ORDER_DT D ");
				sql.append("\n ,(" );
				sql.append("\n   SELECT DISTINCT I.inventory_item_id as product_id");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,APPS.XXPENS_OM_ITEM_MST_V I");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
				sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21','IMCHINA') ");
				sql.append("\n   AND MP.pens_value =I.segment1 ");
				sql.append("\n ) MP ");
				sql.append("\n WHERE L.ORDER_NUMBER = D.ORDER_NUMBER ");
				sql.append("\n AND D.product_id = MP.product_id ");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUSTOMER_NUMBER"));
				sql.append("\n AND L.CUSTOMER_NUMBER ='"+f.getBean().getCustGroup()+"' ");
				if(initDate != null){
					 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
					sql.append("\n AND MP.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
				}
				if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
					sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
					sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND MP.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND MP.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				sql.append("\n  GROUP BY MP.GROUP_CODE,MP.PENS_ITEM ,MP.MATERIAL_MASTER,MP.BARCODE");
			sql.append("\n )SALE_OUT ");
			sql.append("\n ON M.pens_item = SALE_OUT.pens_item ");
			sql.append("\n AND M.group_type  = SALE_OUT.group_type ");
			sql.append("\n LEFT OUTER JOIN ( ");
			 /******** SALE_RETURN *****************/
				sql.append("\n SELECT ");
				sql.append("\n D.GROUP_CODE as group_type,D.PENS_ITEM ,D.MATERIAL_MASTER,D.BARCODE ");
				sql.append("\n ,COUNT(*) as SALE_RETURN_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_PICK_JOB J,PENSBI.PENSBME_PICK_BARCODE L,PENSBI.PENSBME_PICK_BARCODE_ITEM D");
				sql.append("\n WHERE J.JOB_ID = L.JOB_ID ");
				sql.append("\n AND L.BOX_NO = D.BOX_NO ");
				sql.append("\n AND L.JOB_ID = D.JOB_ID ");
				sql.append("\n AND J.STATUS ='"+PickConstants.STATUS_CLOSE+"'");
				sql.append("\n AND L.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
				sql.append("\n AND D.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "J.STORE_CODE"));
				sql.append("\n AND J.STORE_CODE ='"+f.getBean().getCustGroup()+"' ");
				if(initDate != null){
					 sql.append("\n AND J.close_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getGroupCodeFrom()).equals("") ){
					sql.append("\n AND D.GROUP_CODE LIKE '"+Utils.isNull(c.getGroupCodeFrom())+"%' ");
				}
				if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
					sql.append("\n AND D.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
					sql.append("\n AND D.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND D.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND D.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
			   sql.append("\n GROUP BY D.GROUP_CODE,D.PENS_ITEM ,D.MATERIAL_MASTER,D.BARCODE" );
			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type");
			sql.append("\n ) A ");
			
			sql.append("\n ORDER BY A.MATERIAL_MASTER asc ");
			
			//logger.debug("sql:"+sql);
			
			//debug write sql to file
			if(logger.isDebugEnabled()){
			   FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
			}
		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
}
	
}
