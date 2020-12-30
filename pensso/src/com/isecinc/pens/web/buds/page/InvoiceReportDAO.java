package com.isecinc.pens.web.buds.page;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.InvoiceReportBean;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MReceipt;
import com.pens.util.ControlCode;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.NumberUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class InvoiceReportDAO {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	public static InvoiceReportBean searchInvoiceReport(InvoiceReportBean o,boolean excel,User user){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer html = null;
		InvoiceReportBean item = null;
		int r = 0;
		int countOrderAll=0;;
		double totalAmountAll=0;
		double totalVatAmountAll=0;
		double totalNetAmountAll=0;
		double totalRemainAmountAll=0;
		double totalRemainVatAmountAll=0;
		double totalRemainNetAmountAll=0;
		double remainTotalAmount= 0;
        double remainVatAmount=0;
        double remainNetAmount=0;
		List<InvoiceReportBean> itemsList = new ArrayList<InvoiceReportBean>();
		String classHeadColumn = "";
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classNumber = "td_number";
		String classNumberBold = "td_number_bold";
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			sql = genSQLInvoiceReport(o,user);
	
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new InvoiceReportBean();
				item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				item.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				item.setInvoiceDate(DateUtil.stringValueNull(rst.getDate("INVOICE_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setOrderNo(Utils.isNull(rst.getString("order_no")));
				item.setOrderDate(DateUtil.stringValueNull(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setPoNumber(Utils.isNull(rst.getString("po_number")));
				item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
				item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				item.setAlternateName(Utils.isNull(rst.getString("alternate_name")));
                item.setMobile(Utils.isNull(rst.getString("telephone")));
                item.setAmphur(Utils.isNull(rst.getString("province"))+"/"+Utils.isNull(rst.getString("amphur")));
                
                //Invoice amount
                item.setTotalAmount(Utils.decimalFormat(NumberUtil.round(rst.getDouble("total_amount"), 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
                item.setVatAmount(Utils.decimalFormat(NumberUtil.round(rst.getDouble("vat_amount"), 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
                item.setNetAmount(Utils.decimalFormat(NumberUtil.round(rst.getDouble("net_amount"), 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
                
                //remain InvoiceAmount 
            	/** 1 Invoice No Receipt **/
        		/** 2 Invoice have Receipt **/
        		/** 3 CreditNote no Apply Receipt **/
        		/** 4 CrdeitNote apply Receipt **/
                if("INV".equalsIgnoreCase(rst.getString("r_type"))){
                	/** 1 Invoice No Receipt **/
	                if(rst.getInt("receipt_line_id")==0){
	                  
	                    /** CN_APPLY_AMOUNT By invoice_no **/
	                	remainNetAmount= rst.getDouble("net_amount")+rst.getDouble("cn_apply_amount")+rst.getDouble("adjust_amount");
	                    remainVatAmount=(remainNetAmount*7)/107;
	                    remainTotalAmount=remainNetAmount-remainVatAmount;
	                   
	                    item.setRemainTotalAmount(Utils.decimalFormat(NumberUtil.round(remainTotalAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainVatAmount(Utils.decimalFormat(NumberUtil.round(remainVatAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainNetAmount(Utils.decimalFormat(NumberUtil.round(remainNetAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                
	                 /** 2 Invoice have Receipt **/
	                }else  if(rst.getInt("receipt_line_id")!=0){
	                	remainNetAmount= rst.getDouble("remain_net_amount")+rst.getDouble("adjust_amount");
	                    remainVatAmount=(remainNetAmount*7)/107;
	                    remainTotalAmount=remainNetAmount-remainVatAmount;
	                   
	                    item.setRemainTotalAmount(Utils.decimalFormat(NumberUtil.round(remainTotalAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainVatAmount(Utils.decimalFormat(NumberUtil.round(remainVatAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainNetAmount(Utils.decimalFormat(NumberUtil.round(remainNetAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    
	                }
                }else if("CN".equalsIgnoreCase(rst.getString("r_type"))){
                	/** 3 CreditNote no Apply Receipt **/
	                if(rst.getInt("receipt_line_id")==0){
	                	  /** CN_APPLY_AMOUNT By invoice_no **/
	                	remainNetAmount= rst.getDouble("net_amount")-rst.getDouble("cn_apply_amount");
	                    remainVatAmount=remainNetAmount/107;
	                    remainTotalAmount=remainNetAmount-remainVatAmount;
	                    
	                    item.setRemainTotalAmount(Utils.decimalFormat(NumberUtil.round(remainTotalAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainVatAmount(Utils.decimalFormat(NumberUtil.round(remainVatAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainNetAmount(Utils.decimalFormat(NumberUtil.round(remainNetAmount, 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                
	                /** 4 CrdeitNote apply Receipt **/
	                }else  if(rst.getInt("receipt_line_id")!=0){
	                	   
	                    item.setRemainTotalAmount(Utils.decimalFormat(NumberUtil.round(rst.getDouble("remain_total_amount"), 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainVatAmount(Utils.decimalFormat(NumberUtil.round(rst.getDouble("remain_vat_amount"), 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit));
	                    item.setRemainNetAmount(Utils.decimalFormat(NumberUtil.round(rst.getDouble("remain_net_amount"), 2, BigDecimal.ROUND_HALF_UP),Utils.format_current_2_disgit)); 
	                }
                }
                //logger.debug("before Remain_total_amountL:"+rst.getDouble("Remain_total_amount"));
                if( !Utils.isNull(o.getDispHaveRemain()).equals("")){
                   if( Utils.convertStrToDouble(item.getRemainNetAmount()) != 0){
				      itemsList.add(item);
                   }
                }else{
                	 itemsList.add(item);
                }
			}//while
			
			 //gen Head Table
			 html = new StringBuffer("");
			 if(excel){
				classHeadColumn = "colum_head_hilight";
				classText = "text";
				classTextCenter = "text_center";
				classNumber = "currency";
				classNumberBold = "currency_bold";
			    html.append(ExcelHeader.EXCEL_HEADER);
			 }
		     
			 if(itemsList != null && itemsList.size() >0){
			
				 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='table table-condensed table-striped'> \n");
				 html.append("<thead> \n");
				 html.append("<tr> \n");
				 html.append("<th class='"+classHeadColumn+"' colspan='12'></th> \n");
				 html.append("<th class='"+classHeadColumn+"' colspan='3'>ยอดขาย</th> \n");
				 html.append("<th class='"+classHeadColumn+"' colspan='3'>ยอดหนี้ คงเหลือ</th> \n");
				 html.append("</tr>\n");
				 html.append("<tr>\n");
				 html.append("<th class='"+classHeadColumn+"'>Picking List No.</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Invoice No</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Invoice Date</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Order No/CN No</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Order Date/CN Date</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Customer PO</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Sales Code</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Customer No</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Customer Name</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Reference Cust No.</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>Contact Info.</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>อำเภอ / จังหวัด</th> \n");
				 
				 html.append("<th class='"+classHeadColumn+"'>ยอดขาย (ก่อน Vat)</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>VAT</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>ยอด (รวม VAT)</th> \n");
				 
				 html.append("<th class='"+classHeadColumn+"'>ยอดหนี้ (ก่อน Vat)</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>VAT</th> \n");
				 html.append("<th class='"+classHeadColumn+"'>ยอดหนี้ (รวม VAT)</th> \n");
				 html.append("</tr></thead> \n");
				 html.append("<tbody> \n");
				//display 
				 for(int i=0;i<itemsList.size();i++){
					r++;
					item = itemsList.get(i);
	
				    //all
				    countOrderAll++;
				    totalAmountAll += Utils.convertStrToDouble(item.getTotalAmount());
				    totalVatAmountAll += Utils.convertStrToDouble(item.getVatAmount());
				    totalNetAmountAll += Utils.convertStrToDouble(item.getNetAmount());
				    
				    totalRemainAmountAll += Utils.convertStrToDouble(item.getRemainTotalAmount());
				    totalRemainVatAmountAll += Utils.convertStrToDouble(item.getRemainVatAmount());
				    totalRemainNetAmountAll += Utils.convertStrToDouble(item.getRemainNetAmount());
		
				  //Gen Row Table
					html.append("<tr class=''> \n");
					html.append("<td class='"+classTextCenter+"' width='6%'>"+item.getPickingNo()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='6%'>"+item.getInvoiceNo()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='6%'>"+item.getInvoiceDate()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='6%'>"+item.getOrderNo()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='5%'>"+item.getOrderDate()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='7%'>"+item.getPoNumber()+"</td> \n");//
					html.append("<td class='"+classTextCenter+"' width='3%'>"+item.getSalesrepCode()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='10%'>"+item.getCustomerCode()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='10%'>"+item.getCustomerName()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='10%'>"+item.getAlternateName()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='10%'>"+item.getMobile()+"</td> \n");
					html.append("<td class='"+classTextCenter+"' width='10%'>"+item.getAmphur()+"</td> \n");
					
					html.append("<td class='"+classNumber+"' width='10%'>"+item.getTotalAmount()+"</td> \n");
					html.append("<td class='"+classNumber+"' width='10%'>"+item.getVatAmount()+"</td> \n");
					html.append("<td class='"+classNumber+"' width='10%'>"+item.getNetAmount()+"</td> \n");
					
					html.append("<td class='"+classNumber+"' width='10%'>"+item.getRemainTotalAmount()+"</td> \n");
					html.append("<td class='"+classNumber+"' width='10%'>"+item.getRemainVatAmount()+"</td> \n");
					html.append("<td class='"+classNumber+"' width='10%'>"+item.getRemainNetAmount()+"</td> \n");
					html.append("</tr> \n");
					
				 }//for
				 
				//GrandTotal
				html.append("<tr class='row_hilight'> \n");
				html.append("<td class='"+classNumber+"' colspan='11' align='right'>GRAND TOTAL :</td> \n");
				html.append("<td class='"+classTextCenter+"' >"+countOrderAll+" ใบ </td> \n");
				html.append("<td class='"+classNumberBold+"' >"+Utils.decimalFormat(totalAmountAll,Utils.format_current_2_disgit)+"</td>\n");
				html.append("<td class='"+classNumberBold+"' >"+Utils.decimalFormat(totalVatAmountAll,Utils.format_current_2_disgit)+" </td>\n");
				html.append("<td class='"+classNumberBold+"' >"+Utils.decimalFormat(totalNetAmountAll,Utils.format_current_2_disgit)+"</td> \n");
				html.append("<td class='"+classNumberBold+"' >"+Utils.decimalFormat(totalRemainAmountAll,Utils.format_current_2_disgit)+"</td>\n");
				html.append("<td class='"+classNumberBold+"' >"+Utils.decimalFormat(totalRemainVatAmountAll,Utils.format_current_2_disgit)+" </td>\n");
				html.append("<td class='"+classNumberBold+"' >"+Utils.decimalFormat(totalRemainNetAmountAll,Utils.format_current_2_disgit)+"</td> \n");
				html.append("</tr> \n");
					
				//Check Execute Found data
				if(r>0){
				   html.append("<tbody> \n");
				   html.append("</table>");
				}
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
	
	private static StringBuffer genSQLInvoiceReport(InvoiceReportBean o,User user) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  /** genSQLInvoiceReport **/");
		sql.append("\n SELECT A.*");
		//sql.append("\n ,( NVL(A.remain_net_amount,0)/107) as remain_vat_amount");
		//sql.append("\n ,( NVL(A.remain_net_amount,0)-(NVL(A.remain_net_amount,0)/107) ) as remain_total_amount");
		
		sql.append("\n ,(NVL(A.remain_net_amount,0)*7/107) as remain_vat_amount");
		sql.append("\n ,( NVL(A.remain_net_amount,0) - (NVL(A.remain_net_amount,0)*7/107) )as remain_total_amount");
		
		sql.append("\n FROM (");
		sql.append("\n  SELECT M.* ");
		sql.append("\n  ,NVL(CN_APP.CN_APPLY_AMOUNT,0) as CN_APPLY_AMOUNT ");
		sql.append("\n  ,NVL(AD.ADJUST_AMOUNT,0) as ADJUST_AMOUNT ");
		sql.append("\n  FROM (");
		sql.append("\n    /** Order have Invoice  **/ ");
		sql.append("\n    SELECT 'INV' as R_TYPE ");
		sql.append("\n    ,inv.attribute12 as picking_no,inv.invoice_no,inv.invoice_date ");
		sql.append("\n    ,inv.ref_order ,o.ORDER_NO ,o.ORDER_DATE ,o.po_number ");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = c.user_id) as salesrep_code");
		sql.append("\n    ,a.alternate_name ,a.telephone,'' as ar_invoice_no");
		sql.append("\n    ,inv.total_amount ,inv.vat_amount,inv.NET_AMOUNT");
		sql.append("\n    ,( ");
		sql.append("\n      select NVL(rl.REMAIN_AMOUNT,0) as REMAIN_AMOUNT ");
		sql.append("\n      from pensso.t_receipt r,pensso.t_receipt_line rl ");
		sql.append("\n      where rl.receipt_id = r.receipt_id ");
	    sql.append("\n      and r.doc_status = '"+Receipt.DOC_SAVE + "'  ");
	    sql.append("\n      and rl.invoice_id = inv.invoice_id ");
	    sql.append("\n      and rl.receipt_line_id =(select max(receipt_line_id) from pensso.t_receipt_line srl where srl.invoice_id = inv.invoice_id) ");
		sql.append("\n    )as remain_net_amount ");
		sql.append("\n    ,( ");
		sql.append("\n      select max(rl.receipt_line_id) ");
		sql.append("\n      from pensso.t_receipt r,pensso.t_receipt_line rl ");
		sql.append("\n      where rl.receipt_id = r.receipt_id ");
	    sql.append("\n      and r.doc_status = '"+Receipt.DOC_SAVE + "'  ");
	    sql.append("\n      and rl.invoice_id = inv.invoice_id ");
		sql.append("\n    )as receipt_line_id ");
		sql.append("\n    FROM pensso.t_invoice inv");
		sql.append("\n    ,pensso.t_order o ");
		sql.append("\n    ,pensso.m_customer c");
		sql.append("\n    ,pensso.m_address a");
		sql.append("\n    WHERE 1=1 ");
		sql.append("\n    and inv.bill_to_customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND inv.ship_to_site_use_id = a.site_use_id ");
		sql.append("\n    AND inv.ORDER_TYPE = 'CR' ");
		sql.append("\n    AND o.order_no = inv.ref_order ");
		sql.append("\n    AND o.doc_status ='"+I_PO.STATUS_LOADING+"'");
		sql.append("\n    AND o.ORDER_TYPE = 'CR' ");
		/** Gen Where SQL **/
		genWhereCond(o, sql, user, "INVOICE");
		
		sql.append("\n   UNION ALL ");
		sql.append("\n   /**  Invoice No Order (ORCL) **/ ");
		sql.append("\n    SELECT 'INV' as R_TYPE ");
		sql.append("\n    ,inv.attribute12 as picking_no,inv.invoice_no,inv.invoice_date ");
		sql.append("\n    ,inv.ref_order ,'' as ORDER_NO ,null as ORDER_DATE ,'' as po_number ");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = c.user_id) as salesrep_code");
		sql.append("\n    ,a.alternate_name ,a.telephone ,'' as ar_invoice_no");
		sql.append("\n    ,inv.total_amount ,inv.vat_amount,inv.NET_AMOUNT");
		sql.append("\n    ,( ");
		sql.append("\n      select NVL(SUM(rl.REMAIN_AMOUNT),0) as REMAIN_AMOUNT ");
		sql.append("\n      from pensso.t_receipt r,pensso.t_receipt_line rl ");
		sql.append("\n      where rl.receipt_id = r.receipt_id ");
	    sql.append("\n      and r.doc_status = '"+Receipt.DOC_SAVE + "'  ");
	    sql.append("\n      and rl.invoice_id = inv.invoice_id ");
	    sql.append("\n      and rl.receipt_line_id =(select max(receipt_line_id) from pensso.t_receipt_line srl where srl.invoice_id = inv.invoice_id)");
		sql.append("\n    )as remain_net_amount ");
		sql.append("\n    ,( ");
		sql.append("\n      select max(rl.receipt_line_id) ");
		sql.append("\n      from pensso.t_receipt r,pensso.t_receipt_line rl ");
		sql.append("\n      where rl.receipt_id = r.receipt_id ");
	    sql.append("\n      and r.doc_status = '"+Receipt.DOC_SAVE + "'  ");
	    sql.append("\n      and rl.invoice_id = inv.invoice_id ");
		sql.append("\n    )as receipt_line_id ");
		sql.append("\n    FROM pensso.t_invoice inv");
		sql.append("\n    ,pensso.m_customer c");
		sql.append("\n    ,pensso.m_address a");
		sql.append("\n    WHERE 1=1 ");
		sql.append("\n    and inv.bill_to_customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND inv.ship_to_site_use_id = a.site_use_id ");
		sql.append("\n    AND inv.ORDER_TYPE = 'CR' ");
		sql.append("\n    AND ( inv.ref_order is null OR inv.ref_order not in(select order_no from pensso.t_order) ) ");//Case Invoice(ORCL) no order
		/** Gen Where SQL **/
		genWhereCond(o, sql, user, "INVOICE");
		
		sql.append("\n   UNION ALL ");
		sql.append("\n   /**  Credit Note **/ ");
		sql.append("\n    SELECT 'CN' as R_TYPE ");
		sql.append("\n    ,'' as picking_no,inv.ar_invoice_no as invoice_no ,null as invoice_date ");
		sql.append("\n    ,'' as ref_order ,inv.credit_note_no as ORDER_NO ,inv.document_date as ORDER_DATE ,'' as po_number ");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = c.user_id) as salesrep_code");
		sql.append("\n    ,a.alternate_name ,a.telephone,inv.ar_invoice_no");
		sql.append("\n    ,( NVL(inv.total_amount,0) - (NVL(inv.total_amount,0)*7/107) ) as total_amount");
		sql.append("\n    ,(NVL(inv.total_amount,0)*7/107)  as vat_amount");
		sql.append("\n    ,inv.total_amount as NET_AMOUNT");
		sql.append("\n    ,( ");
		sql.append("\n      select NVL(SUM(rl.REMAIN_AMOUNT),0) as REMAIN_AMOUNT ");
		sql.append("\n      from pensso.t_receipt_cn rl ");
		sql.append("\n      where rl.credit_note_id = inv.credit_note_id ");
		sql.append("\n    )as remain_net_amount ");
		sql.append("\n    ,( ");
		sql.append("\n      select max(rl.receipt_id) ");
		sql.append("\n      from pensso.t_receipt_cn rl ");
		sql.append("\n      where rl.credit_note_id = inv.credit_note_id ");
		sql.append("\n    )as receipt_cn_id ");
		sql.append("\n   FROM pensso.t_credit_note inv");
		sql.append("\n   ,pensso.m_customer c");
		sql.append("\n   ,(");
		sql.append("\n      select distinct a.customer_id ");
		sql.append("\n      ,a.district_id,a.province_id,a.alternate_name ,a.telephone");
		sql.append("\n      from pensso.m_address a");
		sql.append("\n      where a.purpose ='B'");
		sql.append("\n    ) a");
		sql.append("\n    WHERE 1=1 ");
		sql.append("\n    and inv.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND inv.ACTIVE ='Y' ");
		sql.append("\n    AND inv.DOC_STATUS ='SV' ");
		/** Gen Where SQL **/
		genWhereCond(o, sql, user, "CN");
		
		sql.append("\n  )M  ");
		sql.append("\n LEFT OUTER JOIN  ");
		sql.append("\n /** CN Apply Invoice 1:1   cnAmount +adjust(CN)**/ ");
		sql.append("\n ( ");
		sql.append("\n  SELECT  ar_invoice_no as invoice_no,credit_note_no ");
		sql.append("\n  ,( COALESCE(sum(total_amount),0)");
		sql.append("\n    +(COALESCE((SELECT SUM(adjust_amount) from PENSSO.t_adjust aj ");
		sql.append("\n                where aj.ar_invoice_no = c.credit_note_no),0)) ");
		sql.append("\n   ) as CN_APPLY_AMOUNT ");
		sql.append("\n  FROM PENSSO.T_CREDIT_NOTE c WHERE 1=1");
		sql.append("\n  AND ACTIVE='Y' ");
		sql.append("\n  AND DOC_STATUS ='SV' ");
		sql.append("\n  group by c.ar_invoice_no,c.credit_note_no ");
		sql.append("\n )CN_APP ");
		sql.append("\n ON M.invoice_no = CN_APP.invoice_no ");
		
		sql.append("\n LEFT OUTER JOIN  ");
		sql.append("\n /** Adjust Apply Invoice 1:1 **/ ");
		sql.append("\n ( ");
		sql.append("\n  SELECT  ar_invoice_no as invoice_no ");
		sql.append("\n  , NVL(sum(adjust_amount),0) as ADJUST_AMOUNT");
		sql.append("\n  FROM PENSSO.T_ADJUST c ");
		sql.append("\n  group by c.ar_invoice_no");
		sql.append("\n )AD ");
		sql.append("\n ON M.invoice_no = AD.invoice_no ");
		
		sql.append("\n )A ");
		
		sql.append("\n ORDER BY A.invoice_date");
		return sql;
	}
	
	public static void genWhereCond(InvoiceReportBean o,StringBuffer sql,User user,String recType) throws Exception{
		if( !user.getCode().equalsIgnoreCase("ADMIN")){
			if("CN".equalsIgnoreCase(recType)){
			   sql.append("\n    AND inv.USER_ID = " + user.getId() +" ");
			}else{
			   sql.append("\n    AND inv.primary_salesrep_id = " + user.getId() +" ");	
			}
		}
		if(!Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n    AND c.code = '" + Utils.isNull(o.getCustomerCode()) + "'");
		}
		if(!Utils.isNull(o.getCustomerName()).equals("")){
			sql.append("\n    AND c.name = '" + Utils.isNull(o.getCustomerName()) + "'");
		}
		if(!Utils.isNull(o.getTerritory()).equals("")){
			sql.append("\n    AND c.TERRITORY = '" + Utils.isNull(o.getTerritory()) + "'");
		}
		if (!"".equals(Utils.isNull(o.getSearchProvince()))) {
			sql.append("\n   AND a.province_id = " + o.getSearchProvince());
		}
		if ( !"".equals(Utils.isNull(o.getDistrict())) ){
			sql.append("\n   AND a.district_id = " + o.getDistrict() + "");
		}
		if("CN".equalsIgnoreCase(recType)){
			if ( !Utils.isNull(o.getTransactionDateFrom()).equals("") && !Utils.isNull(o.getTransactionDateTo()).equals("")) {
				sql.append("\n   AND inv.document_date  >= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') ");
				sql.append("\n   AND inv.document_date  <= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') ");
				
			}else if ( !Utils.isNull(o.getTransactionDateFrom()).equals("") && Utils.isNull(o.getTransactionDateTo()).equals("")) {
				sql.append("\n   AND inv.document_date  = to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') ");
			}
			
		}else{
			if ( !Utils.isNull(o.getTransactionDateFrom()).equals("") && !Utils.isNull(o.getTransactionDateTo()).equals("")) {
				sql.append("\n   AND inv.invoice_date  >= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') ");
				sql.append("\n   AND inv.invoice_date  <= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') ");
				
			}else if ( !Utils.isNull(o.getTransactionDateFrom()).equals("") && Utils.isNull(o.getTransactionDateTo()).equals("")) {
				sql.append("\n   AND inv.invoice_date  = to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') ");
			}
		}
	}
}
