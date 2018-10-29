package com.isecinc.pens.web.maya.sub;

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

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.web.maya.MayaBean;
import com.isecinc.pens.web.maya.MayaForm;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class MayaStockOnhandAction {
 private static Logger logger = Logger.getLogger("PENS");
	
 public static MayaForm search(HttpServletRequest request, MayaForm f,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<MayaBean> pos = new ArrayList<MayaBean>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			//Get Init date
			Date initDate = new SummaryDAO().searchInitDateMTT(conn,f.getBean().getCustGroup());
			//gen sql
			sql = genSQL(conn,f,initDate);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				MayaBean item = new MayaBean();
				item.setOrderDate(Utils.stringValue(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				item.setOrderNo(Utils.isNull(rst.getString("ORDER_NUMBER")));
				item.setPensItem(Utils.isNull(rst.getString("pens_item")));
				item.setBarcode(rst.getString("barcode"));
				item.setStyle(rst.getString("material_master"));
				item.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
				item.setUnitPrice(Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit));
				item.setLineAmount(Utils.decimalFormat(rst.getDouble("line_amount"),Utils.format_current_2_disgit));
				item.setDiscount(Utils.decimalFormat(rst.getDouble("discount"),Utils.format_current_2_disgit));
				item.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
				item.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
				
				if("S".equalsIgnoreCase(Utils.isNull(rst.getString("promotion")))){
				  item.setFreeItem("YES");
				}else{
				  item.setFreeItem("");
				}
				pos.add(item);
			}//while

			if(pos != null && pos.size() >0){
				f.setResults(pos);
				//request.getSession().setAttribute("summary" ,item);
			}else{
				f.setResults(null);
				request.getSession().setAttribute("summary" ,null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
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
 
 public static StringBuffer exportToExcel(HttpServletRequest request, MayaForm form,User user,List<MayaBean> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="12";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="11";
			}
			//Header
			h.append("<table border='1'> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"'>รายงานรายละเอียดการขาย ที่ MAYA Shop</td> \n");
			h.append(" </tr> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getBean().getStartDate()+"-"+form.getBean().getEndDate()+"</td> \n");
			h.append(" </tr> \n");
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>Sales Date</th> \n");
				  h.append("<th>Order No</th> \n");
				  h.append("<th>Pens Item</th> \n");
				  h.append("<th>Barcode</th> \n");
				  h.append("<th>Style</th> \n");
				  h.append("<th>Qty </th> \n");
				  h.append("<th>Free Item</th> \n");
				  h.append("<th>Unit Price</th> \n");
				  h.append("<th>Line Amount</th> \n");
				  h.append("<th>Discount</th> \n");
				  h.append("<th>Vat Amount</th> \n");
				  h.append("<th>Total Line Amount</th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					MayaBean s = (MayaBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getOrderDate()+"</td> \n");
					  h.append("<td class='text'>"+s.getOrderNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStyle()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getQty()+"</td> \n");
					  h.append("<td class='text'>"+s.getFreeItem()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getUnitPrice()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getLineAmount()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getDiscount()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getVatAmount()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getTotalAmount()+"</td> \n");
					h.append("</tr>");
				}
				/** Summary **/
				MayaBean s = (MayaBean)request.getSession().getAttribute("summary");
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
 
 public static StringBuilder genSQL(Connection conn,MayaForm f,Date initDate) throws Exception{
		StringBuilder sql = new StringBuilder();
		String storeType ="SHOP";
		MayaBean c = f.getBean();//Get Criteria
		try {
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getAsOfDate()).equals("")){
				Date d = Utils.parse(c.getAsOfDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			String initDateStr ="";
			if( initDate != null){
				initDateStr = Utils.stringValue(initDate, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			
			sql.append("\n SELECT A.* FROM(");
			sql.append("\n SELECT M.*");
			sql.append("\n , NVL(INIT_MTT.INIT_SALE_QTY,0) AS INIT_SALE_QTY");
			sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0)) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
			       /** INIT MTT */
					sql.append("\n SELECT DISTINCT");
				    sql.append("\n L.GROUP_CODE as group_type ,L.MATERIAL_MASTER ,L.BARCODE ");
					sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBI.PENSBME_MTT_ONHAND_INIT_STK L");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
					sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   where M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			        sql.append("\n  ) M ");
					sql.append("\n WHERE H.cust_no = L.cust_no  ");
					sql.append("\n AND M.customer_code = H.cust_no  ");
					sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
					if( !Utils.isNull(initDateStr).equals("")){
						 sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
						sql.append("\n AND L.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
						sql.append("\n AND L.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND L.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND L.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					
					sql.append("\n UNION ALL");
					/** ORDER BME (TRANS_IN)*/
					sql.append("\n SELECT DISTINCT");
					sql.append("\n L.GROUP_CODE as group_type,L.ITEM AS PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE ");
					sql.append("\n FROM PENSBI.PENSBME_ORDER L");
					sql.append("\n ,(" );
					sql.append("\n   SELECT,MP.PENS_VALUE as PENS_ITEM ");
					sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
					sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
					sql.append("\n   WHERE reference_code ='"+Constants.STORE_TYPE_7CATALOG_ITEM+"'");
					sql.append("\n ) MP ");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND L.BARCODE = MP.BARCODE ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.store_code"));
					if(initDate != null){
						 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
						sql.append("\n AND L.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
						sql.append("\n AND L.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					sql.append("\n UNION ALL");
					/** ORDER ORACLE(SALEOUT) */
					sql.append("\n SELECT DISTINCT");
					sql.append("\n MP.GROUP_CODE as group_type,MP.PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE ");
					sql.append("\n FROM APPS.XXPENS_OM_SHOP_ORDER_MST L,APPS.XXPENS_OM_SHOP_ORDER_DT D ");
					sql.append("\n ,(" );
					sql.append("\n   SELECT I.inventory_item_id as product_id");
					sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
					sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
					sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,APPS.XXPENS_OM_ITEM_MST_V I");
					sql.append("\n   WHERE reference_code ='"+Constants.STORE_TYPE_7CATALOG_ITEM+"'");
					sql.append("\n   AND MP.pens_value =I.segment1 ");
					sql.append("\n ) MP ");
					sql.append("\n WHERE L.ORDER_NUMBER = D.ORDER_NUMBER ");
					sql.append("\n AND D.product_id = MP.product_id ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUSTOMER_NUMBER"));
					if(initDate != null){
						 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
						sql.append("\n AND MP.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
						sql.append("\n AND MP.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
					}
					if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
						sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
						sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND MP.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND MP.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
				
					sql.append("\n UNION ALL");
					
					/** Return **/
					sql.append("\n SELECT DISTINCT");
					sql.append("\n D.GROUP_CODE as group_type,D.PENS_ITEM ,D.MATERIAL_MASTER,D.BARCODE ");
					sql.append("\n FROM PENSBI.PENSBME_PICK_JOB J L,PENSBI.PENSBME_PICK_BARCODE L,PENSBI.PENSBME_PICK_BARCODE_ITEM D");
					sql.append("\n WHERE J.JOB_ID = L.JOB_ID ");
					sql.append("\n AND L.ISSUE_REQ_NO = D.ISSUE_REQ_NO ");
					sql.append("\n AND J.STATUS ='"+PickConstants.STATUS_CLOSE+"'");
					sql.append("\n AND L.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
					sql.append("\n AND D.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "J.STORE_CODE"));
					
					if(initDate != null){
						 sql.append("\n AND J.close_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
						sql.append("\n AND D.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
						sql.append("\n AND D.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
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
   		    sql.append("\n SELECT L.GROUP_CODE as group_type ,L.MATERIAL_MASTER ,L.BARCODE");
			sql.append("\n ,SUM(QTY) AS INIT_SALE_QTY ");
			sql.append("\n FROM PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
			sql.append("\n ,( ");
			sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
			sql.append("\n   PENSBME_MST_REFERENCE M ");
			sql.append("\n   WHERE M.reference_code ='Store' ");
			//Filter By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
	        sql.append("\n  ) M ");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n and H.cust_no = L.cust_no  ");
			sql.append("\n AND M.customer_code = H.cust_no  ");
			sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
			if( !Utils.isNull(initDateStr).equals("")){
				 sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
				sql.append("\n AND L.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
				sql.append("\n AND L.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
			}
			if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
				sql.append("\n AND L.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
				sql.append("\n AND L.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			sql.append("\n  GROUP BY L.GROUP_CODE as group_type ,L.MATERIAL_MASTER ,L.BARCODE");
			sql.append("\n )INIT_MTT ");
			sql.append("\n ON M.pens_item = INIT_MTT.pens_item ");	 
			sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
			
		   sql.append("\n LEFT OUTER JOIN(	 ");
		   
		   /******** TRANS_IN *****************/
		    sql.append("\n SELECT L.GROUP_CODE as group_type,L.ITEM AS PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE");
	        sql.append("\n ,NVL(SUM(L.QTY),0) AS TRANS_IN_QTY ");
	        sql.append("\n FROM PENSBI.PENSBME_ORDER L");
			sql.append("\n ,(" );
			sql.append("\n   SELECT,MP.PENS_VALUE as PENS_ITEM ");
			sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
			sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
			sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
			sql.append("\n   WHERE reference_code ='"+Constants.STORE_TYPE_7CATALOG_ITEM+"'");
			sql.append("\n ) MP ");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n AND L.BARCODE = MP.BARCODE ");
			//Filter By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.store_code"));
			if(initDate != null){
				 sql.append("\n AND L.sale_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}else{
				 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
				sql.append("\n AND L.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
				sql.append("\n AND L.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
			}
			if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
				sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
				sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			sql.append("\n  GROUP BY L.GROUP_CODE as group_type,L.ITEM AS PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE");
			sql.append("\n )TRANS_IN ");
			sql.append("\n ON  M.pens_item = TRANS_IN.pens_item ");	 
			sql.append("\n AND M.group_type = TRANS_IN.group_type ");
	
			sql.append("\n LEFT OUTER JOIN( ");
			 /******** SALE_OUT *****************/
				sql.append("\n SELECT");
				sql.append("\n MP.GROUP_CODE as group_type,MP.PENS_ITEM ,MP.MATERIAL_MASTER,L.BARCODE ");
				sql.append("\n ,NVL(SUM(D.ORDERED_QUANTITY)) as SALE_OUT_QTY");
				sql.append("\n FROM APPS.XXPENS_OM_SHOP_ORDER_MST L,APPS.XXPENS_OM_SHOP_ORDER_DT D ");
				sql.append("\n ,(" );
				sql.append("\n   SELECT I.inventory_item_id as product_id");
				sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,APPS.XXPENS_OM_ITEM_MST_V I");
				sql.append("\n   WHERE reference_code ='"+Constants.STORE_TYPE_7CATALOG_ITEM+"'");
				sql.append("\n   AND MP.pens_value =I.segment1 ");
				sql.append("\n ) MP ");
				sql.append("\n WHERE L.ORDER_NUMBER = D.ORDER_NUMBER ");
				sql.append("\n AND D.product_id = MP.product_id ");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUSTOMER_NUMBER"));
				
				if(initDate != null){
					 sql.append("\n AND L.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
					sql.append("\n AND MP.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
					sql.append("\n AND MP.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
				}
				if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
					sql.append("\n AND MP.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
					sql.append("\n AND MP.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND MP.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND MP.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
			sql.append("\n )SALE_OUT ");
			sql.append("\n ON  M.customer_code = SALE_IN.customer_code and M.pens_item = SALE_IN.pens_item ");
			sql.append("\n AND M.group_type  = SALE_IN.group_type ");
			sql.append("\n LEFT OUTER JOIN ( ");
			 /******** SALE_RETURN *****************/
				sql.append("\n SELECT ");
				sql.append("\n D.GROUP_CODE as group_type,D.PENS_ITEM ,D.MATERIAL_MASTER,D.BARCODE ");
				sql.append("\n ,COUNT(*) as SALE_RETURN_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_PICK_JOB J L,PENSBI.PENSBME_PICK_BARCODE L,PENSBI.PENSBME_PICK_BARCODE_ITEM D");
				sql.append("\n WHERE J.JOB_ID = L.JOB_ID ");
				sql.append("\n AND L.ISSUE_REQ_NO = D.ISSUE_REQ_NO ");
				sql.append("\n AND J.STATUS ='"+PickConstants.STATUS_CLOSE+"'");
				sql.append("\n AND L.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
				sql.append("\n AND D.STATUS <> '"+PickConstants.STATUS_CANCEL+"'");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "J.STORE_CODE"));
				
				if(initDate != null){
					 sql.append("\n AND J.close_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getGroupCodeFrom()).equals("") && !Utils.isNull(c.getGroupCodeTo()).equals("")){
					sql.append("\n AND D.GROUP_CODE >='"+Utils.isNull(c.getGroupCodeFrom())+"' ");
					sql.append("\n AND D.GROUP_CODE <='"+Utils.isNull(c.getGroupCodeTo())+"' ");
				}
				if( !Utils.isNull(c.getStyleFrom()).equals("") && !Utils.isNull(c.getStyleTo()).equals("")){
					sql.append("\n AND D.MATERIAL_MASTER  >='"+Utils.isNull(c.getStyleFrom())+"' ");
					sql.append("\n AND D.MATERIAL_MASTER  <='"+Utils.isNull(c.getStyleTo())+"' ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND D.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND D.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
			   sql.append("\n GROUP BY D.GROUP_CODE as group_type,D.PENS_ITEM ,D.MATERIAL_MASTER,D.BARCODE" );
			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type");
			sql.append("\n ) A ");
			
			sql.append("\n ORDER BY A.MATERIAL_MASTER asc ");
			
			//logger.debug("sql:"+sql);
			
			//debug write sql to file
			if(logger.isDebugEnabled()){
			   FileUtil.writeFile("d:/temp/sql.sql", sql.toString());
			}
		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
}
	
}
