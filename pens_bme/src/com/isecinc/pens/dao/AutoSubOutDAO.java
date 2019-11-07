package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.web.autosubin.AutoSubInBean;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class AutoSubOutDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
public static boolean isAutoSubTransOutExist(String refNo) throws Exception{
    Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	boolean exist = false;
	StringBuffer sql = new StringBuffer("");
	try{
		conn = DBConnection.getInstance().getConnection();
		
		sql.append(" select count(*) as c from PENSBI.PENSBME_AUTO_SUBTRANS_OUT where REF_NO ='"+refNo+"' \n" );
        logger.debug("sql:\n"+sql.toString());

		ps = conn.prepareStatement(sql.toString());
		rs = ps.executeQuery();
		if(rs.next()){
			if(rs.getInt("c")>0){
				exist = true;
			}
		}
		return exist;
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
		if(conn != null){
			conn.close();conn=null;
		}
	}
}
	
public static boolean canAutoSubTransOut(String custGroup) throws Exception{
    Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	boolean canAuto = false;
	StringBuffer sql = new StringBuffer("");
	try{
		conn = DBConnection.getInstance().getConnectionApps();
		
		sql.append("\n select count(*) as c FROM PENSBI.PENSBME_MST_REFERENCE WHERE reference_code = 'Customer' ");
		sql.append("\n and pens_desc6 ='Auto Sub-transfer'");
		sql.append("\n and pens_value ='"+custGroup+"'");
		
		logger.debug("sql:\n"+sql.toString());
		ps = conn.prepareStatement(sql.toString());
		rs = ps.executeQuery();
		if(rs.next()){
			if(rs.getInt("c")>0){
				canAuto = true;
			}
		}
		return canAuto;
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
		if(conn != null){
			conn.close();conn=null;
		}
	}
}

//For Page:1)Confirm Issue by Pick Dept ,2)Confirm Issue by Pick Dept(all)
 public static void saveSubTransOutStockIssue(Connection conn,AutoSubInBean head) throws Exception{
	try{
		//insert head
		insertSubTransOutHead(conn, head);
		
		//insert item
		insertAutoSubOutItemByStockIssue(conn, head);
	}catch(Exception e){
		throw e;
	}
}
 
//For Page:หน้าจอ เบิกสินค้าจากคลังขายสด-W3 (เบิกเป็นรุ่น )
 public static void saveSubTransOutPickStockByGroup(Connection conn,AutoSubInBean head) throws Exception{
	try{
		//insert head
		insertSubTransOutHead(conn, head);
		
		//insert item
		insertAutoSubOutItemByPickStockByGroup(conn, head);
	}catch(Exception e){
		throw e;
	}
}
	 
//For page:	เบิกสินค้าจากคลังขายสด-W3(เบิกทั้งกล่อง)
 public static void saveSubTransOutPickStockByAllBox(Connection conn,AutoSubInBean head) throws Exception{
	try{
		//insert head
		insertSubTransOutHead(conn, head);
		
		//insert item
		insertAutoSubOutItemByPickStockByAllBox(conn, head);
	}catch(Exception e){
		throw e;
	}
}
 public static int insertSubTransOutHead(Connection conn,AutoSubInBean head) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertSubTransOutHead");
		int c =1;int r = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_OUT \n");
			sql.append("(REF_NO, SUBINVENTORY, CUST_GROUP, STORE_CODE \n");
			sql.append(",STATUS, TOTAL_BOX, TOTAL_QTY, CREATE_DATE, CREATE_USER,Forwarder,Forwarder_box) \n");
			sql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?) \n");
			
			//logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());

			ps.setString(c++, Utils.isNull(head.getJobId()));
			ps.setString(c++, Utils.isNull(head.getSubInv())); 
			ps.setString(c++, Utils.isNull(head.getCustGroup())); 
			ps.setString(c++, Utils.isNull(head.getStoreCode())); 
			ps.setString(c++, "APPROVED"); 
			ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalBox())); 
			ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalQty())); 
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, head.getUserName());
			ps.setString(c++, Utils.isNull(head.getForwarder())); 
			ps.setDouble(c++, Utils.convertStrToDouble(head.getForwarderBox())); 
			
			r = ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
		return r;
	}
	 
 public static int insertAutoSubOutItemByStockIssue(Connection conn,AutoSubInBean head) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertAutoSubOutItemByStockIssue");
		int r = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_OUT_ITEM \n");
			sql.append(" SELECT \n");
			sql.append(" S.ISSUE_REQ_NO as REF_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, \n");
			sql.append(" S.PENS_ITEM, \n");
			sql.append("  (select unit_price from pensbi.pensbme_unit_price_v \n");
			sql.append("    where customer_code = '"+head.getStoreCode()+"' \n");
			sql.append("    and inventory_item_id = P.INVENTORY_ITEM_ID  \n");
			sql.append("    and rownum = 1 ) as unit_price , \n");
			sql.append(" NVL(SUM(S.ISSUE_QTY),0) as QTY, \n");
			sql.append(" 'APPROVED' as STATUS, \n");
			sql.append(" sysdate as CREATE_DATE, \n");
			sql.append(" '"+head.getUserName()+"' CREATE_USER, \n");
			sql.append(" null as UPDATE_DATE, \n");
			sql.append(" null as UPDATE_USER, \n");
			sql.append(" null as INTFLAG, \n");
			sql.append(" null as INTMESSAGE \n");
			sql.append(" FROM pensbi.PENSBME_STOCK_ISSUE_ITEM S \n");
			sql.append(" ,apps.xxpens_om_item_mst_v P \n");
			sql.append(" WHERE S.pens_item = P.segment1 \n");
			sql.append(" AND S.ISSUE_REQ_NO ='"+head.getJobId()+"' \n");
			sql.append(" AND S.ISSUE_QTY <> 0 ");
			sql.append(" GROUP BY S.ISSUE_REQ_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, S.PENS_ITEM \n");
			
			logger.debug("sql: \n"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			r =ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
		return r;
	}
	 
 public static int insertAutoSubOutItemByPickStockByGroup(Connection conn,AutoSubInBean head) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertAutoSubOutItemByPickStockByGroup");
		int r = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_OUT_ITEM \n");
			sql.append("SELECT \n");
			sql.append(" S.ISSUE_REQ_NO as REF_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, \n");
			sql.append(" S.PENS_ITEM, \n");
	
			sql.append("  (select unit_price from pensbi.pensbme_unit_price_v \n");
			sql.append("    where customer_code = '"+head.getStoreCode()+"' \n");
			sql.append("    and inventory_item_id = P.INVENTORY_ITEM_ID  \n");
			sql.append("    and rownum = 1 ) as unit_price , \n");
			
			sql.append(" COUNT(*) as QTY, \n");
			sql.append(" 'APPROVED' as STATUS, \n");
			sql.append(" sysdate as CREATE_DATE, \n");
			sql.append("'"+head.getUserName()+"' CREATE_USER, \n");
			sql.append(" null as UPDATE_DATE, \n");
			sql.append(" null as UPDATE_USER, \n");
			sql.append(" null as INTFLAG, \n");
			sql.append(" null as INTMESSAGE \n");
			
			sql.append(" FROM PENSBI.PENSBME_PICK_STOCK_I S \n");
			sql.append(" ,apps.xxpens_om_item_mst_v P \n");
			sql.append(" WHERE S.pens_item = P.segment1 \n");
			sql.append(" AND S.ISSUE_REQ_NO ='"+head.getJobId()+"' \n");
			sql.append(" GROUP BY S.ISSUE_REQ_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, S.PENS_ITEM \n");
			
			logger.debug("sql: \n"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			r =ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
		return r;
	}
	 
 public static int insertAutoSubOutItemByPickStockByAllBox(Connection conn,AutoSubInBean head) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertAutoSubOutItemByPickStockByAllBox");
		int r = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_OUT_ITEM \n");
			sql.append("SELECT \n");
			sql.append(" S.ISSUE_REQ_NO as REF_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, \n");
			sql.append(" S.PENS_ITEM, \n");
			sql.append("  (select unit_price from pensbi.pensbme_unit_price_v \n");
			sql.append("    where customer_code = '"+head.getStoreCode()+"' \n");
			sql.append("    and inventory_item_id = P.INVENTORY_ITEM_ID  \n");
			sql.append("    and rownum = 1 ) as unit_price , \n");
			sql.append(" COUNT(*) as QTY, \n");
			sql.append(" 'APPROVED' as STATUS, \n");
			sql.append(" sysdate as CREATE_DATE, \n");
			sql.append("'"+head.getUserName()+"' CREATE_USER, \n");
			sql.append(" null as UPDATE_DATE, \n");
			sql.append(" null as UPDATE_USER, \n");
			sql.append(" null as INTFLAG, \n");
			sql.append(" null as INTMESSAGE \n");
			
			sql.append(" FROM PENSBI.PENSBME_PICK_STOCK_I S \n");
			sql.append(" ,apps.xxpens_om_item_mst_v P \n");
			sql.append(" WHERE S.pens_item = P.segment1 \n");
			sql.append(" AND S.ISSUE_REQ_NO ='"+head.getJobId()+"' \n");
			sql.append(" GROUP BY S.ISSUE_REQ_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, S.PENS_ITEM \n");
			
			logger.debug("sql: \n"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			r =ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
		return r;
	}
}
