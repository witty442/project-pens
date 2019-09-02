package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CConstantsBean;

public class CConstants {
	private static Logger logger = Logger.getLogger("PENS");
	
	//Group Of C_CONSTANTS
	public static final String SPIDER_REF_CODE ="SPIDER";
	public static final String MOVEORDER_REF_CODE ="MOVEORDER";
	public static final String STOCKCREDIT_CODE ="StockCredit";
	
	//Type of C_CONSTANTS ,Van Or Credit
	public static final String MAX_DISTANCE ="MaxDistance"; 
	public static final String MIN_VISIT_BY_TRIP ="MinVisitByTrip"; 
	public static final String MIN_VISIT_BY_REAL ="MinVisitByReal"; 
	public static final String MAX_NOT_EQUALS_TRIP ="MaxNotEqualsTrip"; 
	public static final String MAX_NOT_EQUALS_MASLOC ="MaxNotEqualsMasloc";
	
	public static final String MOVEORDER_MAX_MOVEDAY ="MoveOrderMaxMoveDay"; 
	public static final String STOCK_CALLC_CREDIT_START_DATE ="StockCallCCreditStartDate"; 
	
	 public static CConstantsBean getConstants(Connection conn,String refCode,String code) throws Exception {
		 return getConstantsModel(conn,refCode, code);
	 }
	 
	 public static CConstantsBean getConstants(String refCode,String code) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnectionApps();
			 return getConstantsModel(conn,refCode, code);
		 } finally {
			conn.close();	
		}
	 }
	 public static CConstantsBean getConstantsModel(Connection conn,String refCode,String code) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			CConstantsBean value = null;
			try {
				sql.append("\n select * from PENSBI.C_CONSTANTS ");
				sql.append("\n where ref_code ='"+refCode+"'");
				sql.append("\n and code ='"+code+"'");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					value = new CConstantsBean();
					value.setRefCode(Utils.isNull(rst.getString("ref_code")));
					value.setRefDesc(Utils.isNull(rst.getString("ref_desc")));
					value.setCode(Utils.isNull(rst.getString("code")));
					value.setValue(Utils.isNull(rst.getString("value")));
					value.setValue2(Utils.isNull(rst.getString("value_2")));
				    value.setIsactive(Utils.isNull(rst.getString("isactive")));
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return value;
		}
	 
	 public static Map<String, CConstantsBean> getConstantsList(Connection conn,String refCode) throws Exception {
		 return getConstantsListModel(conn,refCode);
	 }
	 
	 public static Map<String, CConstantsBean> getConstantsList(String refCode) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnectionApps();
			 return getConstantsListModel(conn,refCode);
		 } finally {
			conn.close();	
		}
	 }
	 public static Map<String, CConstantsBean> getConstantsListModel(Connection conn,String refCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			CConstantsBean value = null;
			Map<String, CConstantsBean> constantsMap = new HashMap<String, CConstantsBean>();
			try {
				sql.append("\n select * from PENSBI.C_CONSTANTS ");
				sql.append("\n where ref_code ='"+refCode+"'");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					value = new CConstantsBean();
					value.setRefCode(Utils.isNull(rst.getString("ref_code")));
					value.setRefDesc(Utils.isNull(rst.getString("ref_desc")));
					value.setCode(Utils.isNull(rst.getString("code")));
					value.setValue(Utils.isNull(rst.getString("value")));
					value.setValue2(Utils.isNull(rst.getString("value_2")));
				    value.setIsactive(Utils.isNull(rst.getString("isactive")));
				    
				    constantsMap.put(value.getCode(), value);
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return constantsMap;
		}
}
