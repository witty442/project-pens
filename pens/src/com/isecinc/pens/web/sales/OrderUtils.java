package com.isecinc.pens.web.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import util.ControlCode;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;

public class OrderUtils {
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void mian(String[] a){
		
	}
	public static boolean canSaveCreditVan(Connection conn,User user,int customerId){
		if(ControlCode.canExecuteMethod("OrderUtils", "canSaveCreditVan")){
		   return canSaveCreditVanModel(conn, user, customerId);
		}
		return true;//default
	}
	
	public static boolean canSaveCreditVanModel(Connection conn,User user,int customerId){
		boolean canSave = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			/** Case PD_PAID =Y **/
			if( Utils.isNull(user.getPdPaid()).equalsIgnoreCase("Y")){
				sql.append("\n  SELECT  ");
				sql.append("\n   count(*) as c");
				sql.append("\n   from t_order o, m_customer cus  ");
				sql.append("\n   ,( ");
				sql.append("\n     select l.order_id  ");
				sql.append("\n     ,h.payment_method  ");
				sql.append("\n     ,h.PDPAID_DATE ");
				sql.append("\n     ,h.PD_PAYMENTMETHOD  as PAYMENT_METHOD2 ");
				sql.append("\n     from t_receipt h, t_receipt_line l ,t_receipt_match m, t_receipt_by b ");
				sql.append("\n     where h.receipt_id = l.receipt_id  ");
				sql.append("\n     and m.RECEIPT_LINE_ID = l.RECEIPT_LINE_ID  ");
				sql.append("\n     and b.RECEIPT_BY_ID = m.RECEIPT_BY_ID  ");
				sql.append("\n     and h.ISPDPAID = 'N' "); //No Pay
				sql.append("\n   ) r ");
				sql.append("\n   where 1=1 ");
				sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
				sql.append("\n   and o.order_id = r.order_id ");
				sql.append("\n   and o.DOC_STATUS ='SV' ");
				sql.append("\n   and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
				sql.append("\n   and o.USER_ID = " + user.getId());
				sql.append("\n   and o.CUSTOMER_ID = " + customerId);
			}else{
				/** Case PD_PAID =N  **/
				References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.VAN_DATE_FIX,InitialReferences.VAN_DATE_FIX);
				String  creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
				logger.debug("vanDateFix:"+creditDateFix);
				String dateCheck = "";
				if( !"".equalsIgnoreCase(creditDateFix)){
					java.util.Date d = Utils.parse(creditDateFix, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					dateCheck = "str_to_date('"+Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y')" ;
				}
				
				sql.append("\n   SELECT  ");
				sql.append("\n   count(*) as c");
				sql.append("\n   from t_order o, m_customer cus  ");
				sql.append("\n   where 1=1 ");
				sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
				sql.append("\n   and o.DOC_STATUS ='SV' ");
				sql.append("\n   and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
				sql.append("\n   and o.USER_ID = " + user.getId());
				sql.append("\n   and o.CUSTOMER_ID = " + customerId);
				sql.append("\n   and o.order_date >= " + dateCheck);
				sql.append("\n   AND order_id not in( select order_id from t_receipt_line)  ");
				sql.append("\n   AND order_no not in( select receipt_no from t_receipt)  ");
				sql.append("\n   AND order_no not in( select order_no from t_receipt_pdpaid_no)  ");
			}
			
			logger.debug("sql:"+sql.toString());
			ps =conn.prepareStatement(sql.toString());
			rs= ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
					canSave = false;
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  ps.close();
			  rs.close();
			}catch(Exception ee){}
		}
		return canSave;
	}
}
