package com.isecinc.pens.report.salestargetsummary;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MSalesTargetNew;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MSalesTargetPeriod;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUOMConversion;
import com.pens.util.DateToolsUtil;
import com.pens.util.NumberToolsUtil;


/**
 * SalesTargetSummaryReportProcess Report
 * 
 * @author WITTY
 * @version $Id:$
 * 
 */

public class SalesTargetSummaryReportProcess extends I_ReportProcess<SalesTargetSummaryReport> {
	/**
	 * Search for performance report.
	 */
   public static String codeControl = "1";
	
	public static String getMethodCalcTargetControl(Connection conn){
		String method = "2";//default new code
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps =conn.prepareStatement("select value from c_reference where ISACTIVE ='Y' and REFERENCE_ID=2403");
			rs = ps.executeQuery();
			if(rs.next()){
				method = Utils.isNull(rs.getString("value")).equals("")?"2":Utils.isNull(rs.getString("value"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(ps != null){
				   ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return method;
	}
	
	public List<SalesTargetSummaryReport> doReport(SalesTargetSummaryReport report, User user, Connection conn) throws Exception {
		SalesTargetNew[] stns = null;
		codeControl = getMethodCalcTargetControl(conn);
		int i =0;
		List<SalesTargetSummaryReport> pos = new ArrayList<SalesTargetSummaryReport>();
		// Get Sales Target Match With Sales Order
		String p_dateFrom =report.getDateFrom();
		String p_dateTo = report.getDateTo();
		String p_productCodeFrom = report.getProductCodeFrom();
		String p_productCodeTo = report.getProductCodeTo();
		Calendar c = Calendar.getInstance(Locale.US);
		c.setTime(Utils.parse(p_dateFrom,Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
	
		int month = c.get(Calendar.MONTH)+1;
		int year = c.get(Calendar.YEAR);
		logger.debug("month:"+month+",year:"+year);
		
		//New Requirement 30/03/2563 get period sales order from Config Table(pens.m_sales_target_period)
		/*SalesTargetNew salesPeriod = MSalesTargetPeriod.getSalesPeriodDate(conn, String.valueOf(month), String.valueOf(year));
		if(salesPeriod ==null){
			//default by Target period date
			salesPeriod = new SalesTargetNew();
			salesPeriod.setMonth(String.valueOf(month));
			salesPeriod.setYear(String.valueOf(year));
			salesPeriod.setSalesStartDate(p_dateFrom);
			salesPeriod.setSalesEndDate(p_dateTo);
		}*/
		
		SalesTargetNew salesPeriod = new SalesTargetNew();
		salesPeriod.setSalesStartDate(p_dateFrom);
		salesPeriod.setSalesEndDate(p_dateTo);
		
		StringBuffer whereClause = new StringBuffer();
		whereClause.append(" AND User_ID = "+user.getId());
		whereClause.append(" AND MONTH(TARGET_FROM) = "+month);
		whereClause.append(" AND YEAR(TARGET_FROM) = "+year);
		if(p_productCodeFrom != null && p_productCodeFrom.length() >0)
			whereClause.append(" AND ProductCode >= "+p_productCodeFrom);
		
		if(p_productCodeTo != null && p_productCodeTo.length() >0 )
			whereClause.append(" AND ProductCode <= "+p_productCodeTo);
		
		MSalesTargetNew sales = new MSalesTargetNew();
		sales.TABLE_NAME = "M_Sales_Target_New_v";
		
		stns = new MSalesTargetNew().search(whereClause.toString());
		
		logger.debug("whereClause:"+whereClause.toString());
		if(stns != null){
			new MOrderLine().compareSalesTarget(conn,stns,salesPeriod);
			
			for(SalesTargetNew stn : stns){
				SalesTargetSummaryReport result = new SalesTargetSummaryReport();
				
				result.setDateFrom(p_dateFrom);
				result.setDateTo(p_dateTo);
				result.settDateFrom(DateToolsUtil.convertToTimeStamp(p_dateFrom));
				result.settDateTo(DateToolsUtil.convertToTimeStamp(p_dateTo));
				result.setProductCode(stn.getProduct().getCode());
				result.setProductName(stn.getProduct().getName());
				result.setUomId(stn.getUom().getId());
				result.setUomName(stn.getUom().getName());
				result.setSalesName(user.getName());
				result.setSalesCode(user.getCode());
				result.setTargetAmt(BigDecimal.valueOf(stn.getTargetAmount()));
				result.setTargetQty(stn.getTargetQty()+"/0");
				
				//Test 
				//result.setTargetQty(stn.getBaseQty()+"/"+stn.getSubQty());
				
				//logger.debug("Qty1:"+stn.getBaseQty()+",qty2:"+stn.getSubQty());
				
				result.setSalesQty(setQtyStr(stn.getProduct().getId()+"", stn.getBaseQty(), stn.getSubQty()) );
				
				result.setSalesAmt(BigDecimal.valueOf(stn.getSoldAmount()));
				
				result.setSalesCompareTargetPct(BigDecimal.valueOf(stn.getPercentCompare()));
				
				pos.add(result);
			}
		}
		
		// Include Order that don't have Sales target
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pd.product_id,pd.code as ProductCode , pd.Name as ProductName ,uom.UOM_ID , uom.Name as UOM_NAME \n")
			.append(", SUM(IF(pd.UOM_ID=odl.UOM_ID,IF(odl.promotion ='N',odl.Qty,0),0)) as BaseQty \n")
			.append(", SUM(IF(pd.UOM_ID<>odl.UOM_ID,IF(odl.promotion ='N',odl.Qty,0),0)) as SecondQty \n" )
			
			.append(", SUM(IF(pd.UOM_ID=odl.UOM_ID,odl.Line_Amount,0)) as BaseLineAmt \n")
			.append(", SUM(IF(pd.UOM_ID<>odl.UOM_ID,odl.Line_Amount,0)) as SecondLineAmt \n ")
			.append("FROM T_Order od \n")
			.append("INNER JOIN T_Order_Line odl ON (od.Order_ID = odl.Order_ID) \n")
			.append("INNER JOIN M_Product pd ON (pd.Product_Id = odl.Product_ID) \n")
			.append("INNER JOIN M_UOM uom ON (uom.UOM_ID = pd.UOM_ID) \n")
			.append("WHERE od.User_ID = ? \n")
			.append("AND od.order_date >= ? \n")
			.append("AND od.order_date <= ? \n")
			.append("AND odl.IsCancel = 'N' \n")// FIXED : Not Include Cancel Line To Calculate
			.append("AND od.Doc_STATUS = 'SV' \n")// FIXED : Not Include Order was Void To Calculate
			
			//.append("AND od.ar_invoice_no is not null \n")
			.append("AND odl.Product_ID NOT IN \n")
			.append("(SELECT stn.Product_ID FROM M_Sales_Target_New_v stn "
					+ "WHERE stn.User_ID = ? AND month(stn.Target_From) = ? AND year(stn.Target_From) ="+year+" ) \n");

			if(p_productCodeFrom != null && p_productCodeFrom.length() >0)
				sql.append(" AND pd.Code >= ? \n");
		
			if(p_productCodeTo != null && p_productCodeTo.length() > 0)
				sql.append(" AND pd.Code <= ? \n");
			
			sql.append("GROUP BY pd.product_id,pd.code , pd.Name ,pd.UOM_ID \n");
		
	    logger.debug("sql:"+sql.toString());
	    
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		
		ppstmt.setInt(1, user.getId());
		ppstmt.setTimestamp(2,DateToolsUtil.convertToTimeStamp(p_dateFrom));
		ppstmt.setTimestamp(3, DateToolsUtil.convertToTimeStamp(p_dateTo));
		ppstmt.setInt(4, user.getId());
		ppstmt.setInt(5, month);
		
		int curr_param_idx = 5;
		if(p_productCodeFrom != null && p_productCodeFrom.length() >0){
			curr_param_idx++;
			ppstmt.setString(curr_param_idx, p_productCodeFrom);
		}
		
		if(p_productCodeTo != null && p_productCodeTo.length() >0){
			curr_param_idx++;
			ppstmt.setString(curr_param_idx, p_productCodeTo);
		}
		
		ResultSet rs = ppstmt.executeQuery();
		while(rs.next()){
			SalesTargetSummaryReport result = new SalesTargetSummaryReport();
			result.setDateFrom(p_dateFrom);
			result.setDateTo(p_dateTo);
			result.settDateFrom(DateToolsUtil.convertToTimeStamp(p_dateFrom));
			result.settDateTo(DateToolsUtil.convertToTimeStamp(p_dateTo));
			result.setProductCode(rs.getString("ProductCode"));
			result.setProductName(rs.getString("ProductName"));
			result.setUomId(rs.getString("UOM_ID"));
			result.setUomName(rs.getString("UOM_NAME"));
			result.setSalesName(user.getName());
			result.setSalesCode(user.getCode());
			result.setTargetAmt(BigDecimal.ZERO);
			result.setTargetQty("0");
			
			BigDecimal baseQty = rs.getBigDecimal("BaseQty");
			BigDecimal secondQty = rs.getBigDecimal("SecondQty");
			BigDecimal totalSalesAmt = rs.getBigDecimal("BaseLineAmt").add(rs.getBigDecimal("SecondLineAmt"));
			
			//OlD CODE
			//result.setSalesQty(baseQty.setScale(0,2).toString()+"/"+secondQty.setScale(0,2).toString());
		
			//NEW CODE
			result.setSalesQty(setQtyStr(rs.getString("product_id"), baseQty.setScale(0,2).intValue(), secondQty.setScale(0,2).intValue()) );
			
			//Test
			//result.setTargetQty(baseQty.setScale(0,2).toString()+"/"+secondQty.setScale(0,2).toString());
			
			result.setSalesAmt(totalSalesAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
			result.setSalesCompareTargetPct(BigDecimal.ZERO);
			BigDecimal.valueOf(100d);
			pos.add(result);
		}
		
		if(pos.size() >0)
			Collections.sort(pos);
		
		return pos;
	}
	
	 private String setQtyStr(String productId,int qty1,int qty2) throws Exception{

		 String  qtyStr = qty1+"/"+qty2 +""; //default
		 if("1".equalsIgnoreCase(codeControl)){//default control code 1
			 return qtyStr;
	    }
		 
		 if( !"".equals(qty2) && !"0".equals(qty2)){
			UOMConversion uc2 =  new MUOMConversion().getCurrentConversionNotIn(Integer.parseInt(productId),"CTN");
			if(uc2 != null){
				double[] newQty = calcCTNQty(productId,qty1,qty2,uc2);
					
				String qtyTemp1 = NumberToolsUtil.decimalFormat(newQty[0],NumberToolsUtil.format_current_no_disgit)+" ";
				String qtyTemp2 = " "+NumberToolsUtil.decimalFormat(newQty[1],NumberToolsUtil.format_current_no_disgit);
	
				qtyStr = qtyTemp1 +"/"+qtyTemp2+"";
			}
		 }
		 return qtyStr;
	 }
	 
	 private double[] calcCTNQty(String productId,int qty1,int qty2,UOMConversion  uc2) throws Exception{
		    //logger.debug("CalcCTN QTY productId["+productId+"]");
		    String ctnUomId = "CTN";
			double[] priQty = new double[2];
			UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(Integer.parseInt(productId),ctnUomId);
		    
		    if( uc2 != null){
		       // logger.debug("qty2["+qty2+"]( rate1["+uc1.getConversionRate()+"]/rate2["+uc2.getConversionRate()+"])");
		        
		        if(uc2.getConversionRate() > 0){
		        	double qty2Temp = qty2 / (uc1.getConversionRate()/uc2.getConversionRate()) ;
		        	//logger.debug("result divide["+qty2Temp+"]");
		        
					double pcsQty = new Double(qty2Temp).intValue();
		        	priQty[0] = qty1  +pcsQty;
		        	
		        	//‡»…
		        	double qty2Temp2 = qty2 % (uc1.getConversionRate()/uc2.getConversionRate()) ;
		        	//logger.debug("result mod["+qty2Temp2+"]");
					priQty[1] = qty2Temp2;
					
		        }else{
		        	priQty[0] = qty1;
		        }
		    }else{
		    	//No Qty2 ,UOM2
			    priQty[0] = qty1;
		    }
		   // logger.debug("result calc qty["+priQty+"]");
		    return priQty;
		}
	 
	
}
