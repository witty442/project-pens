package com.isecinc.pens.report.detailedsales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.Constants;
import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class DetailedSalesReportProcess extends I_ReportProcess<DetailedSalesReport>{

	/**
	 * Search for report.
	 */
	public List<DetailedSalesReport> doReport(DetailedSalesReport t, User user,
			Connection conn) throws Exception {
		
		List<DetailedSalesReport> lstData = new ArrayList<DetailedSalesReport>();
		DetailedSalesReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		int no = 0;
		try {
			sql.delete(0, sql.length());
			
			// Witty 21/01/2018
			sql.append("\n SELECT t.* FROM ( ");
			sql.append("\n SELECT o.ORDER_DATE, o.ORDER_NO, o.customer_bill_name as NAME, c.NAME2, o.IsCash , ");
			sql.append("\n SUM(o.NET_AMOUNT) AS NET_AMOUNT, ");
			sql.append("\n o.PAYMENT, o.INTERFACES, o.EXPORTED, o.DOC_STATUS,O.payment_method ");
			sql.append("\n FROM t_order o ");
			sql.append("\n LEFT JOIN m_customer c ON o.CUSTOMER_ID = c.CUSTOMER_ID ");
			sql.append("\n WHERE c.CUSTOMER_TYPE = 'CV' ");
			
			sql.append("\n AND o.USER_ID = " + user.getId());	
			sql.append("\n AND o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n AND o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			
			if( Utils.isNull(t.getOrderType()).equals(Constants.PAYMT_CASH)){
				sql.append("\n AND o.payment_method in( 'CS','ALI','WE','GOV','QR') ");
				
			}else if( Utils.isNull(t.getOrderType()).equals(Constants.PAYMT_CREDITCARD)){
				sql.append("\n AND o.payment_method = '"+Constants.PAYMT_CREDITCARD+"' ");
			}
			//Aneak.t 21/01/2011
			sql.append("\n GROUP BY o.ORDER_NO ");
			
			switch (t.getSortType()) {
			case 1:
				sql.append("\n ORDER BY o.IsCASH, o.ORDER_DATE, o.ORDER_NO, c.CODE ");
				break;
			case 2:
				sql.append("\n ORDER BY o.IsCASH,o.ORDER_NO, o.ORDER_DATE ");
				break;
			case 3:
				sql.append("\n ORDER BY o.IsCASH,c.CODE, o.ORDER_DATE, o.ORDER_NO ");
				break;
			default:
				sql.append("\n ORDER BY o.IsCASH ");
				break;
			}
			
			sql.append(") t ");
		
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			
			while(rs.next()){
				no++;
				detailedSales = new DetailedSalesReport();
				detailedSales.setNo(no+"");
				detailedSales.setOrderDate(DateToolsUtil.convertToString(rs.getTimestamp("ORDER_DATE")));
				detailedSales.setOrderNo(rs.getString("ORDER_NO"));
				detailedSales.setName(rs.getString("NAME"));
				detailedSales.setName2("");
				detailedSales.setTotalAmount(rs.getDouble("NET_AMOUNT"));
				detailedSales.setPayment(rs.getString("PAYMENT"));
				detailedSales.setInterfaces(rs.getString("INTERFACES"));
				detailedSales.setExported(rs.getString("EXPORTED"));
				detailedSales.setDocStatus(rs.getString("DOC_STATUS"));
				detailedSales.setPaymentMethod(rs.getString("payment_method"));
				if(  Utils.isNull(detailedSales.getPaymentMethod()).equals("CS")
				   ||Utils.isNull(detailedSales.getPaymentMethod()).equals("ALI")
				   ||Utils.isNull(detailedSales.getPaymentMethod()).equals("WE")
				   ||Utils.isNull(detailedSales.getPaymentMethod()).equals("GOV")
				   ||Utils.isNull(detailedSales.getPaymentMethod()).equals("QR")
						){
				   detailedSales.setIsCash("Y");
				}else{
				   detailedSales.setIsCash("N");
				}
				
				if(  Utils.isNull(detailedSales.getPaymentMethod()).equals("CS")){
					detailedSales.setPaymentMethodDesc("เงินสด");
				}else if(Utils.isNull(detailedSales.getPaymentMethod()).equals("ALI")){
					detailedSales.setPaymentMethodDesc("Alipay");
			    }else if(Utils.isNull(detailedSales.getPaymentMethod()).equals("WE")){
			    	detailedSales.setPaymentMethodDesc("WeChat");
		        }else if(Utils.isNull(detailedSales.getPaymentMethod()).equals("GOV")){
		        	detailedSales.setPaymentMethodDesc("GOV");
			    }else if(Utils.isNull(detailedSales.getPaymentMethod()).equals("QR")){
			    	detailedSales.setPaymentMethodDesc("QRCODE");
			    }else if(Utils.isNull(detailedSales.getPaymentMethod()).equals("CR")){
			    	detailedSales.setPaymentMethodDesc("บัตรเครดิต");
                }
				//detailedSales.setIsPDPaid(rs.getString("IsPDPaid"));
				
				lstData.add(detailedSales);
			}
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return lstData;
	}
}
