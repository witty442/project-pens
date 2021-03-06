package com.isecinc.pens.web.general;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class GeneralDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String searchReport(HttpServletRequest request) throws Exception {
		try{
			String reportName = Utils.isNull(request.getParameter("reportName"));
			
			return "";
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	public static String searchSaleOutBME(String startDate,String endDate) throws Exception {
		StringBuilder sql = new StringBuilder();
		String result = "";
		try {
			sql.append("\n select store_type, ");
			sql.append("\n group_code,to_char(order_date,'yyyymm') as yyyymm, sum(qty) as qty ");
			sql.append("\n ,whole_price_bf as whole_price");
			sql.append("\n ,(nvl(sum(qty),0)* nvl(whole_price_bf,0)) as sum_whole_price ");
			sql.append("\n ,retail_price_bf as retail_price");
			sql.append("\n ,(nvl(sum(qty),0)* nvl(retail_price_bf,0)) as sum_retail_price ");
			sql.append("\n from pensbi.pensbme_order ");
			sql.append("\n where 1=1 ");
			//genWHereCondSql
			if( !Utils.isNull(startDate).equals("")){
				Date tDate  = DateUtil.parse(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String tDateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and ORDER_DATE >= to_date('"+tDateStr+"','dd/mm/yyyy')");
			}
			if( !Utils.isNull(endDate).equals("")){
				Date tDate  = DateUtil.parse(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String tDateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and ORDER_DATE <= to_date('"+tDateStr+"','dd/mm/yyyy')");
			}
			sql.append("\n group by store_type ,group_code,to_char(order_date,'yyyymm'),whole_price_bf,retail_price_bf ");
			sql.append("\n order by to_char(order_date,'yyyymm'),store_type,group_code ");
			logger.debug("sql:"+sql);
			
			result = SQLHelper.excQuery(sql.toString());
			
			logger.debug("result:\n"+result);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				
				
			} catch (Exception e) {}
		}
		return result;
	}
	
}
