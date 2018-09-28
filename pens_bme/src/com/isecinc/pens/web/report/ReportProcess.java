package com.isecinc.pens.web.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.popup.ConditionFilterBean;
import com.isecinc.pens.web.popup.DisplayBean;
import com.pens.util.Utils;

public class ReportProcess {
   
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public   static Map<String,String> MONTH_MAP = new HashMap<String,String>();
	public   static Map<String,String> DISP_COLUMN_TH_MAP = new HashMap<String,String>();

	public static final String TYPE_SEARCH_DAY = "DAY";
	public static final String TYPE_SEARCH_MONTH = "MONTH";
	public static final String TYPE_SEARCH_QUARTER = "QUARTER";
	public static final String TYPE_SEARCH_YEAR = "YEAR";
	public static final String TABLE_VIEW = "XXPENS_BI_SALES_ANALYSIS_V";
	
	//For Prefix ColumnName CALL NO DUP 
	public static final String NO_DUP_PREFIX = "ND_";
	
	public static List<String> MULTI_SELECTION_LIST = new ArrayList<String>();

	private static ReportProcess salesAnalystProcess;
	SAUtils saU = new SAUtils();
	
	public static ReportProcess getInstance(){
		
		if(salesAnalystProcess == null){
			salesAnalystProcess = new ReportProcess();
			return salesAnalystProcess;
		}
		return salesAnalystProcess;
	}

	static {
		logger.debug("Initail Param Sales Analisis");
		
		MULTI_SELECTION_LIST.add("Item");
		MULTI_SELECTION_LIST.add("Group");
		MULTI_SELECTION_LIST.add("Store");
		MULTI_SELECTION_LIST.add("Style");
		
		DISP_COLUMN_TH_MAP.put("QTY","จำนวนชิ้น");
		DISP_COLUMN_TH_MAP.put("WHOLE_PRICE_BF","จำนวนเงิน(ขายส่ง) Exc.VAT");
		DISP_COLUMN_TH_MAP.put("RETAIL_PRICE_BF","จำนวนเงิน(ขายปลีกสุทธิ) Inc.VAT");
	}
	
	public  void initSession(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("Initial Session ");
			conn = com.isecinc.pens.inf.helper.DBConnection.getInstance().getConnection();
			
			/** Display Group **/
			List<References> dispList = new ArrayList<References>();
			r = new References("disp_1","Style");
			dispList.add(r);
			r = new References("disp_2","StoreNo+StoreName");
			dispList.add(r);
			r = new References("disp_3","StoreNo+StoreName+Style");
			dispList.add(r);
			r = new References("disp_4","SalesDate+StoreNo+StoreName");
			dispList.add(r);
			r = new References("disp_5","SalesDate+StoreNo+StoreName+Style");
			dispList.add(r);
			r = new References("disp_6","SalesDate+StoreNo+StoreName+Style+รหัสสินค้า");
			dispList.add(r);
			session.setAttribute("dispList", dispList);

			/** init TypeSearch ***/
			List<References> typeSearchList = new ArrayList<References>();
			r = new References("DAY","วัน");
			typeSearchList.add(r);
			r = new References("MONTH","เดือน");
			typeSearchList.add(r);
			r = new References("YEAR","ปี");
			typeSearchList.add(r);
			session.setAttribute("typeSearchList", typeSearchList);
			
			/** init year **/
			List<References> yearL = initYearList(conn);
			session.setAttribute("yearList", yearL);
			
			MONTH_MAP.put("01", "ม.ค. ");
			MONTH_MAP.put("02", "ก.พ. ");
			MONTH_MAP.put("03", "มี.ค. ");
			MONTH_MAP.put("04", "เม.ย. ");
			MONTH_MAP.put("05", "พ.ค. ");
			MONTH_MAP.put("06", "มิ.ย. ");
			MONTH_MAP.put("07", "ก.ค. ");
			MONTH_MAP.put("08", "ส.ค. ");
			MONTH_MAP.put("09", "ก.ย. ");
			MONTH_MAP.put("10", "ต.ค. ");
			MONTH_MAP.put("11", "พ.ย. ");
			MONTH_MAP.put("12", "ธ.ค. ");
			
			/** init day **/
			List<References> dayList = new ArrayList<References>();
			for(i=1;i<=31;i++){
			   r = new References(""+i,""+i);
			   dayList.add(r);
			}
			session.setAttribute("dayList", dayList);

			/** init ConditionList **/
			List<References> conditionList = new ArrayList<References>();
			conditionList.add(new References("-1","ไม่เลือก"));
			conditionList.add(new References("Customer","กลุ่มร้านค้า"));
			conditionList.add(new References("Store","ร้านค้า"));
			conditionList.add(new References("Style","Style"));
			conditionList.add(new References("Group","Group"));
			conditionList.add(new References("Item","รหัสสินค้า"));
			session.setAttribute("conditionList", conditionList);

			
			/** Init Display Column List **/
			List<References> dispColumnList = new ArrayList<References>();
			dispColumnList.add(new References("-1","ไม่เลือก"));
			dispColumnList.add(new References("QTY","จำนวนชิ้น"));
			dispColumnList.add(new References("WHOLE_PRICE_BF","จำนวนเงิน(ขายส่ง) Exc.VAT"));
			dispColumnList.add(new References("RETAIL_PRICE_BF","จำนวนเงิน(ขายปลีกสิทธิ) Inc.VAT"));
			//dispColumnList.add(new References("TOTAL_WHOLE_PRICE_BF","จำนวนรวมเงิน(ขายปลีก)"));
			session.setAttribute("dispColumnList", dispColumnList);
			
			//Remove Session RESULT
			session.setAttribute("RESULT", null);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			 	DBConnection.getInstance().closeConn(conn, null, null);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public  List<References> initYearList(Connection conn) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> yearList = new ArrayList<References>();
		try{
			sql  = "SELECT A.* FROM ( \n";
			sql += "  SELECT DISTINCT SALES_YEAR from PENSBME_SALES_FROM_LOTUS  \n";
			sql += "  UNION \n";
			sql += "  SELECT DISTINCT SALES_YEAR from PENSBME_SALES_FROM_BIGC  \n";
			sql += ") A ORDER BY A.SALES_YEAR DESC \n";
			
			logger.debug("sql:\n"+sql.toString());
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				yearList.add(new References(rs.getString("SALES_YEAR"), (rs.getInt("SALES_YEAR")+543)+""));
			}
			
		}catch(Exception e){
			 throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return yearList;
	}
	

	public  List<DisplayBean> getConditionValueListByParent(String condType,String code ,String desc,ConditionFilterBean filterBean)throws Exception{
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		int no = 0;
		try{
			conn = DBConnection.getInstance().getConnection();

			//Customer
			if("Customer".equalsIgnoreCase(condType)){ 
				sql = "select * from PENSBME_MST_REFERENCE where reference_code ='Customer' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and PENS_DESC LIKE '%"+desc+"%' \n";
				}
				sql += "order by PENS_DESC \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("PENS_VALUE"),rs.getString("PENS_VALUE") ,rs.getString("PENS_DESC")));
				}
			}else if("Store".equalsIgnoreCase(condType)){ 
				sql = "\n select * from (select a.* ," +
					  "\n   ( CASE WHEN a.pens_value ='Dummy' THEN 0 "+
				      "\n     ELSE  CAST(REPLACE(a.pens_value,'-','0') as INTEGER) END) as store_no_order "+
					  "\n from PENSBME_MST_REFERENCE a where reference_code ='Store' \n";

				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and PENS_DESC LIKE '%"+desc+"%' \n";
				}
				/** Filter **/
				if("Customer".endsWith(filterBean.getCondType1())){
					sql += " and pens_value like '"+filterBean.getCondCode1()+"%' \n"; 
				}
				sql += " )a order by a.store_no_order \n";
				
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("PENS_VALUE"),rs.getString("PENS_VALUE") ,rs.getString("PENS_DESC")));
				}
			
            }else if("Style".equalsIgnoreCase(condType)){ 
            	
            	sql = "select * from PENSBME_MST_REFERENCE where reference_code ='Group' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and interface_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and interface_value LIKE '%"+desc+"%' \n";
				}
				sql += "order by interface_value \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("interface_value"),rs.getString("interface_value") ,rs.getString("interface_value")));
				}
            }else if("Group".equalsIgnoreCase(condType)){ 
            	sql = "select distinct pens_value from PENSBME_MST_REFERENCE where reference_code ='Group' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and pens_value LIKE '%"+desc+"%' \n";
				}
				sql += "order by pens_value \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("pens_value"),rs.getString("pens_value") ,rs.getString("pens_value")));
				}
            }else if("Item".equalsIgnoreCase(condType)){ 
            	sql = "select distinct pens_value from PENSBME_MST_REFERENCE where reference_code in('LotusItem','BigCitem') \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and pens_value LIKE '%"+desc+"%' \n";
				}
				sql += "order by pens_value \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("pens_value"),rs.getString("pens_value") ,rs.getString("pens_value")));
				}
			}
			logger.debug("SQL:"+sql);

		}catch(Exception e){
			   throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return returnList;
  }
	
	
  public  List<DisplayBean> getConditionValueList(String condType,String code ,String desc)throws Exception{	
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		int no = 0;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			//Customer
			if("Customer".equalsIgnoreCase(condType)){ 
				sql = "select * from PENSBME_MST_REFERENCE where reference_code ='Customer' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and PENS_DESC LIKE '%"+desc+"%' \n";
				}
				sql += "order by PENS_DESC \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("PENS_VALUE"),rs.getString("PENS_VALUE") ,rs.getString("PENS_DESC")));
				}
			}else if("Store".equalsIgnoreCase(condType)){ 
				sql = "\n select * from (select a.* ," +
					  "\n   ( CASE WHEN a.pens_value ='Dummy' THEN 0 "+
				      "\n     ELSE  CAST(REPLACE(a.pens_value,'-','0') as INTEGER) END) as store_no_order "+
					  "\n from PENSBME_MST_REFERENCE a where reference_code ='Store' \n";

				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and PENS_DESC LIKE '%"+desc+"%' \n";
				}
				
				sql += " )a order by a.store_no_order \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("PENS_VALUE"),rs.getString("PENS_VALUE") ,rs.getString("PENS_DESC")));
				}
            }else if("Style".equalsIgnoreCase(condType)){ 
            	
            	sql = "select * from PENSBME_MST_REFERENCE where reference_code ='Group' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and interface_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and interface_value LIKE '%"+desc+"%' \n";
				}
				sql += "order by interface_value \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("interface_value"),rs.getString("interface_value") ,rs.getString("interface_value")));
				}
            }else if("Group".equalsIgnoreCase(condType)){ 
            	sql = "select distinct pens_value from PENSBME_MST_REFERENCE where reference_code ='Group' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and pens_value LIKE '%"+desc+"%' \n";
				}
				sql += "order by pens_value \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("pens_value"),rs.getString("pens_value") ,rs.getString("pens_value")));
				}
            }else if("Item".equalsIgnoreCase(condType)){ 
            	sql = "select distinct pens_value from PENSBME_MST_REFERENCE where reference_code in('LotusItem','BigCitem') \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and pens_value like '"+code+"%' \n"; 
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and pens_value LIKE '%"+desc+"%' \n";
				}
				sql += "order by pens_value \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("pens_value"),rs.getString("pens_value") ,rs.getString("pens_value")));
				}
			}
			
		   logger.debug("SQL:"+sql);
		}catch(Exception e){
			   throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return returnList;
	}
	
  
    public String getDispColumnTH(String key){
    	return Utils.isNull(DISP_COLUMN_TH_MAP.get(key));
    }
}
