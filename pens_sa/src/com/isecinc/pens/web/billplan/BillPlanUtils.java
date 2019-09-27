package com.isecinc.pens.web.billplan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class BillPlanUtils {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static List<PopupBean> searchSalesrepListAll(String salesChannelNo,String custCatNo,String salesZone,User user) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchSalesrepListAll(conn, salesChannelNo, custCatNo,salesZone,user);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static List<PopupBean> searchSalesZoneListModel(Connection conn,User user){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.zone,S.zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and zone in('0','1','2','3','4') ");
			if(!"admin".equalsIgnoreCase(user.getUserName())){
				sql.append("\n and zone in ( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT");
				sql.append("\n   where user_name ='"+user.getUserName()+"'");
				sql.append("\n  )");
			}
			
			sql.append("\n  ORDER BY S.zone asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesZone(Utils.isNull(rst.getString("zone")));
				item.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String salesChannelNo,String custCatNo,String salesZone,User user){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.append("\n  SELECT distinct S.code as salesrep_code,S.salesrep_id from xxpens_salesreps_v S ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and S.sales_channel ='C' ");
			sql.append("\n  and S.code not like 'SN%' ");
			sql.append("\n  and S.code not like 'C%' ");
			sql.append("\n  and zone in('0','1','2','3','4') ");
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n  and s.region ='"+Utils.isNull(salesChannelNo)+"'");
			}
			if( !Utils.isNull(salesZone).equals("")){
			    sql.append("\n  and S.code in(");
			    sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			    sql.append("\n    where zone = "+Utils.isNull(salesZone) );
			    sql.append("\n  )");
			}
			if(!"admin".equalsIgnoreCase(user.getUserName())){
				sql.append("\n and zone in ( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT");
				sql.append("\n   where user_name ='"+user.getUserName()+"'");
				sql.append("\n  )");
			}
			sql.append("\n  and length(S.code) >= 3");
			sql.append("\n  and S.ISACTIVE ='Y'");
			//Not in()
			sql.append("\n  and S.code not in('V006','V008','V009','V010','V011','V013','V014','V020','V021')");
			sql.append("\n  and S.code not in('V081','V082') ");
			sql.append("\n  ORDER BY S.code asc ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 
				 //salesrepCode +=Utils.isNull(rst.getString("salesrep_code"))+",";
				 //salesrepId +=Utils.isNull(rst.getString("salesrep_id"))+",";
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				 
				 pos.add(bean);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}

	
}
