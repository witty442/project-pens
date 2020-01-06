package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.autosubb2b.AutoSubB2BBean;
import com.isecinc.pens.web.autosubin.AutoSubInBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class AutoSubB2BDAO extends PickConstants{
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	public static List<AutoSubB2BBean> searchDataList(Connection conn,AutoSubB2BBean o,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		AutoSubB2BBean h = null;
		List<AutoSubB2BBean> items = new ArrayList<AutoSubB2BBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n  select S.* from (");
            sql.append("\n   select j.ref_no,j.ref_type");
			sql.append("\n   ,j.from_store_code ,j.from_cust_group");
			sql.append("\n   ,j.from_store_name ,j.from_SUBINVENTORY");
			sql.append("\n   ,j.to_store_code ,j.to_cust_group");
			sql.append("\n   ,j.to_store_name ,j.to_SUBINVENTORY");
			sql.append("\n   ,j.total_box ,j.total_qty  ,j.Forwarder,j.Forwarder_box,j.reason ");
		    sql.append("\n   ,(select nvl(sum(qty),0) as c from PENSBI.PENSBME_AUTO_SUBTRANS_B2B_ITEM bi ");
		    sql.append("\n    where bi.ref_no = j.ref_no group by ref_no) as total_save_qty ");
		    
		    sql.append("\n    ,J.status ,J.intflag ,J.intmessage ");
		    sql.append("\n   from PENSBI.PENSBME_AUTO_SUBTRANS_B2B j  ");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append(genWhereSearchList(o).toString());
		    
            sql.append("\n     order by j.create_date desc");
            sql.append("\n   )S WHERE 1=1 ");
            //Where Status Condition
		    sql.append(genWhereSearchListStatus(o).toString());
            sql.append("\n   )A ");
            
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new AutoSubB2BBean();
			   h.setRefNo(Utils.isNull(rst.getString("ref_no")));
			   h.setRefType(Utils.isNull(rst.getString("ref_type")));
			   h.setRefTypeDesc("WACOAL".equalsIgnoreCase(h.getRefType())?"เปิดจากโรงงาน":"เบิกจาก PIC");
			   
			   h.setFromCustGroup(Utils.isNull(rst.getString("from_cust_group")));
			   h.setFromSubInv(Utils.isNull(rst.getString("from_SUBINVENTORY")));
			   h.setFromStoreCode(Utils.isNull(rst.getString("from_store_code")));
			   h.setFromStoreName(Utils.isNull(rst.getString("from_store_name")));
			   
			   h.setToCustGroup(Utils.isNull(rst.getString("to_cust_group")));
			   h.setToSubInv(Utils.isNull(rst.getString("to_SUBINVENTORY")));
			   h.setToStoreCode(Utils.isNull(rst.getString("to_store_code")));
			   h.setToStoreName(Utils.isNull(rst.getString("to_store_name")));
			   
			   h.setTotalBox(Utils.decimalFormat(rst.getDouble("total_box") ,Utils.format_current_no_disgit));
			   h.setTotalQty(Utils.decimalFormat(rst.getDouble("total_qty"),Utils.format_current_no_disgit));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   h.setForwarder(Utils.isNull(rst.getString("Forwarder")));
			   h.setForwarderBox(Utils.decimalFormat(rst.getDouble("forwarder_box") ,Utils.format_current_no_disgit,""));
			   h.setReason(Utils.isNull(rst.getString("reason")));
			   h.setIntFlag(Utils.isNull(rst.getString("intflag")));
			   h.setIntMessage(Utils.isNull(rst.getString("intmessage")));
			   //e ,s
			   if( !Utils.isNull(rst.getString("status")).equals("")){
				   if( Utils.isNull(rst.getString("status")).equalsIgnoreCase("s")){
					   h.setStatus("SUCCESS");
					   h.setCanSave(false);
					   h.setCanApprove(false);
				   }else if( Utils.isNull(rst.getString("status")).equalsIgnoreCase("e")){
					   h.setStatus("ERROR");
					   h.setCanSave(false);
					   h.setCanApprove(false);
				   }else{
					   //aproved no intflag
					   h.setStatus("APPROVED");
					   h.setCanSave(false);
					   h.setCanApprove(false);
				   }
			   }else{
				   h.setCanSave(true);
				   h.setCanApprove(true);
			   }
			   items.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static int searchTotalDataRecList(Connection conn,AutoSubB2BBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
	    int totalRec = 0;
		try {
            sql.append("\n select count(*) as c from(");
            sql.append("\n select S.* from( ");
    	    sql.append("\n select j.status ");
		    sql.append("\n from PENSBI.PENSBME_AUTO_SUBTRANS_B2B j  ");
		    sql.append("\n where 1=1 ");
		    //Where Condition
		    sql.append(genWhereSearchList(o).toString());
		   
		    sql.append("\n ) S where 1=1");
		    //Where Status Condition
		    sql.append(genWhereSearchListStatus(o).toString());
		    
		    sql.append("\n ) A ");
		    
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRec = rst.getInt("c");
			}//while
 
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRec;
	}
	public static StringBuffer genWhereSearchList(AutoSubB2BBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
	    if( !Utils.isNull(o.getRefNo()).equals("")){
	       sql.append("\n and j.ref_no = '"+Utils.isNull(o.getRefNo())+"' ");
	    }
	    if( !Utils.isNull(o.getRefType()).equals("")){
	       sql.append("\n and j.ref_type = '"+Utils.isNull(o.getRefType())+"' ");
	    }
	  
		 return sql;
	}
	public static StringBuffer genWhereSearchListStatus(AutoSubB2BBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if(Utils.isNull(o.getStatus()).equals("")){
	    	//show all
	    }else if(Utils.isNull(o.getStatus()).equals("APPROVED")){
	    	//"" and APPROVED
	    	sql.append("\n   AND S.status ='APPROVED'");
	    	
	    }else  if(Utils.isNull(o.getStatus()).equals("SUCCESS")){
	    	sql.append("\n   AND (S.intflag ='S')");
	    }else  if(Utils.isNull(o.getStatus()).equals("ERROR")){
	    	sql.append("\n   AND (S.intflag ='E')");
	    }
		return sql;
	}
	
	public static boolean isAutoSubTransB2BExist(String refNo) throws Exception{
	    Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean exist = false;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnection();
			
			sql.append(" select count(*) as c from PENSBI.PENSBME_AUTO_SUBTRANS_B2B where REF_NO ='"+refNo+"' \n" );
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
	
	public static void saveSubTransB2BbyWacoal(Connection conn,AutoSubB2BBean head) throws Exception{
		try{
			//delete head
			deleteB2BHeadByPK(conn, head);
			deleteB2BItemByPK(conn, head);//delete item
			
			//insert head
			insertB2BHeadModel(conn, head);
			
			//insert item
			insertAutoSubB2BItemByWacoal(conn, head);
		}catch(Exception e){
			throw e;
		}
	}
	 
	//For Page:1)Confirm Issue by Pick Dept ,2)Confirm Issue by Pick Dept(all)
	 public static void saveSubTransB2BByPic(Connection conn,AutoSubB2BBean head) throws Exception{
		try{
			//delete head
			deleteB2BHeadByPK(conn, head);
			deleteB2BItemByPK(conn, head);//delete item
			
			//insert head
			insertB2BHeadModel(conn, head);
			
			//insert item
			insertAutoSubB2BItemByPic(conn, head);
		}catch(Exception e){
			throw e;
		}
	}
	 
	 public static void approveSubTransB2B(Connection conn,AutoSubB2BBean head) throws Exception{
			try{
				updateStatusB2BHeadModel(conn, head);
				updateStatusB2BDetailModel(conn, head);
				
				if(head.getRefType().equalsIgnoreCase("WACOAL")){
					updateNewStoreByWacoal(conn, head);
				}else{
					updateNewStoreByPic(conn, head);
				}
			}catch(Exception e){
				throw e;
			}
		}
	 
	 public static int insertAutoSubB2BItemByWacoal(Connection conn,AutoSubB2BBean head) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertAutoSubOutItemByBMEOrder");
		int r = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_B2B_ITEM \n");
			sql.append(" SELECT \n");
			sql.append(" S.ORDER_LOT_NO as REF_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, \n");
			sql.append(" S.ITEM, \n");
			sql.append("  (select unit_price from pensbi.pensbme_unit_price_v \n");
			sql.append("    where customer_code = '"+head.getToStoreCode()+"' \n");
			sql.append("    and inventory_item_id = P.INVENTORY_ITEM_ID  \n");
			sql.append("    and rownum = 1 ) as unit_price , \n");
			sql.append(" NVL(SUM(S.QTY),0) as QTY, \n");
			sql.append(" '' as STATUS, \n");
			sql.append(" sysdate as CREATE_DATE, \n");
			sql.append(" '"+head.getUserName()+"' CREATE_USER, \n");
			sql.append(" null as UPDATE_DATE, \n");
			sql.append(" null as UPDATE_USER, \n");
			sql.append(" null as INTFLAG, \n");
			sql.append(" null as INTMESSAGE \n");
			sql.append(" FROM pensbi.PENSBME_ORDER S \n");
			sql.append(" ,apps.xxpens_om_item_mst_v P \n");
			sql.append(" WHERE S.item = P.segment1 \n");
			sql.append(" AND S.ORDER_LOT_NO ='"+head.getRefNo()+"' \n");
			sql.append(" GROUP BY S.ORDER_LOT_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, S.ITEM \n");
			
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
	 
	public static AutoSubB2BBean getStoreDetailByWacoal(AutoSubB2BBean bean) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return getStoreDetailByWacoal(conn,bean);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
 public static AutoSubB2BBean getStoreDetailByWacoal(Connection conn,AutoSubB2BBean cri) throws Exception{
	PreparedStatement ps = null;
	ResultSet rs = null;
	logger.debug("getStoreDetailByWacoal");
	StringBuffer sql = new StringBuffer("");
	AutoSubB2BBean bean = null;
	try{
		sql.append(" SELECT \n");
		sql.append(" S.STORE_TYPE,S.STORE_CODE \n");
		//GET 
		sql.append(" ,(SELECT B.TOTAL_BOX FROM PENSBI.PENSBME_BOX_BYWACOAL B \n");
		sql.append("   WHERE B.STORE_CODE = S.STORE_CODE \n");
		sql.append("   AND B.LOT_NO = S.ORDER_LOT_NO ) as TOTAL_BOX \n");
		sql.append(" ,NVL(SUM(S.QTY),0) as QTY \n");
		sql.append(" FROM pensbi.PENSBME_ORDER S \n");
		sql.append(" WHERE S.ORDER_LOT_NO ='"+cri.getRefNo()+"' \n");
		//Filter storeCode can AutoSub 
		sql.append(" AND S.STORE_TYPE IN(  \n");
		sql.append("    SELECT pens_value  \n");
		sql.append("    FROM  PENSBI.PENSBME_MST_REFERENCE  \n");
		sql.append("    where reference_code = 'Customer'  \n");
		sql.append("    and  pens_desc6 = 'Auto Sub-transfer' \n");
		sql.append(" ) \n");
		
		sql.append(" GROUP BY S.STORE_TYPE,S.STORE_CODE ,S.ORDER_LOT_NO  \n");
		logger.debug("sql: \n"+sql.toString());
		
		ps = conn.prepareStatement(sql.toString());
		rs = ps.executeQuery();
		if(rs.next()){
			//get detail from store
			bean = cri;
			bean.setFromCustGroup(Utils.isNull(rs.getString("STORE_TYPE")));
			bean.setFromStoreCode(Utils.isNull(rs.getString("STORE_CODE")));
			bean.setFromSubInv(GeneralDAO.getSubInvModel(conn, bean.getFromStoreCode()));
			// getStoreName
			bean.setFromStoreName(GeneralDAO.getStoreNameModel(conn,bean.getFromStoreCode()));
			bean.setTotalQty(Utils.decimalFormat(rs.getDouble("QTY"),Utils.format_current_no_disgit));
			bean.setTotalBox(Utils.decimalFormat(rs.getDouble("TOTAL_BOX"),Utils.format_current_no_disgit));
		}
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
	}
	return bean;
}
 
 public static int insertAutoSubB2BItemByPic(Connection conn,AutoSubB2BBean head) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertAutoSubOutItemByPic");
		int r = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_B2B_ITEM \n");
			//STOCK_ISSUE
			sql.append(" SELECT \n");
			sql.append(" S.ISSUE_REQ_NO as REF_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, \n");
			sql.append(" S.PENS_ITEM, \n");
			sql.append("  (select unit_price from pensbi.pensbme_unit_price_v \n");
			sql.append("    where customer_code = '"+head.getToStoreCode()+"' \n");
			sql.append("    and inventory_item_id = P.INVENTORY_ITEM_ID  \n");
			sql.append("    and rownum = 1 ) as unit_price , \n");
			sql.append(" NVL(SUM(S.ISSUE_QTY),0) as QTY, \n");
			sql.append(" '' as STATUS, \n");
			sql.append(" sysdate as CREATE_DATE, \n");
			sql.append(" '"+head.getUserName()+"' CREATE_USER, \n");
			sql.append(" null as UPDATE_DATE, \n");
			sql.append(" null as UPDATE_USER, \n");
			sql.append(" null as INTFLAG, \n");
			sql.append(" null as INTMESSAGE \n");
			sql.append(" FROM pensbi.PENSBME_STOCK_ISSUE_ITEM S \n");
			sql.append(" ,apps.xxpens_om_item_mst_v P \n");
			sql.append(" WHERE S.pens_item = P.segment1 \n");
			sql.append(" AND S.ISSUE_REQ_NO ='"+head.getRefNo()+"' \n");
			sql.append(" AND S.ISSUE_QTY <> 0 ");
			sql.append(" GROUP BY S.ISSUE_REQ_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, S.PENS_ITEM \n");
			
			sql.append(" UNION ALL \n");
			
			sql.append("SELECT \n");
			sql.append(" S.ISSUE_REQ_NO as REF_NO, \n");
			sql.append(" P.INVENTORY_ITEM_ID, \n");
			sql.append(" S.PENS_ITEM, \n");
	
			sql.append("  (select unit_price from pensbi.pensbme_unit_price_v \n");
			sql.append("    where customer_code = '"+head.getToStoreCode()+"' \n");
			sql.append("    and inventory_item_id = P.INVENTORY_ITEM_ID  \n");
			sql.append("    and rownum = 1 ) as unit_price , \n");
			
			sql.append(" COUNT(*) as QTY, \n");
			sql.append(" '' as STATUS, \n");
			sql.append(" sysdate as CREATE_DATE, \n");
			sql.append("'"+head.getUserName()+"' CREATE_USER, \n");
			sql.append(" null as UPDATE_DATE, \n");
			sql.append(" null as UPDATE_USER, \n");
			sql.append(" null as INTFLAG, \n");
			sql.append(" null as INTMESSAGE \n");
			
			sql.append(" FROM PENSBI.PENSBME_PICK_STOCK_I S \n");
			sql.append(" ,apps.xxpens_om_item_mst_v P \n");
			sql.append(" WHERE S.pens_item = P.segment1 \n");
			sql.append(" AND S.ISSUE_REQ_NO ='"+head.getRefNo()+"' \n");
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
 public static AutoSubB2BBean getStoreDetailByPic(AutoSubB2BBean bean) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return getStoreDetailByPic(conn,bean);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
 public static AutoSubB2BBean getStoreDetailByPic(Connection conn,AutoSubB2BBean cri) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		logger.debug("getStoreDetailByPic");
		StringBuffer sql = new StringBuffer("");
		AutoSubB2BBean  bean = null;
		try{
			//STOCK ISSUE
			sql.append(" SELECT \n");
			sql.append(" S.CUST_GROUP,S.CUSTOMER_NO AS STORE_CODE ,S.SUB_INV,S.TOTAL_CTN as TOTAL_BOX \n");
			sql.append(" ,(SELECT NVL(SUM(ISSUE_QTY),0) FROM pensbi.PENSBME_STOCK_ISSUE_ITEM I \n");
			sql.append("   WHERE I.ISSUE_REQ_NO=S.ISSUE_REQ_NO \n");
			sql.append("   AND I.STATUS ='I' \n");
			sql.append("   GROUP BY I.ISSUE_REQ_NO \n");
			sql.append(" ) as QTY \n");
			sql.append(" FROM pensbi.PENSBME_STOCK_ISSUE S \n");
			sql.append(" WHERE S.ISSUE_REQ_NO ='"+cri.getRefNo()+"' \n");
			sql.append(" AND S.STATUS ='I' \n");
			//Filter storeCode can AutoSub 
			sql.append(" AND S.CUST_GROUP IN(  \n");
			sql.append("    SELECT pens_value  \n");
			sql.append("    FROM  PENSBI.PENSBME_MST_REFERENCE  \n");
			sql.append("    where reference_code = 'Customer'  \n");
			sql.append("    and  pens_desc6 = 'Auto Sub-transfer' \n");
			sql.append(" ) \n");
			
			sql.append(" UNION ALL \n");
			//PICK_STOCK
			sql.append(" SELECT \n");
			sql.append(" S.CUST_GROUP,S.STORE_CODE ,S.SUB_INV,S.TOTAL_BOX \n");
			sql.append(" ,(SELECT NVL(COUNT(*),0) FROM pensbi.PENSBME_PICK_STOCK_I I \n");
			sql.append("   WHERE I.ISSUE_REQ_NO=S.ISSUE_REQ_NO \n");
			sql.append("   GROUP BY I.ISSUE_REQ_NO \n");
			sql.append(" ) as QTY \n");
			sql.append(" FROM pensbi.PENSBME_PICK_STOCK S \n");
			sql.append(" WHERE S.ISSUE_REQ_NO ='"+cri.getRefNo()+"' \n");
			sql.append(" AND S.ISSUE_REQ_STATUS ='I' \n");
			//Filter storeCode can AutoSub 
			sql.append(" AND S.CUST_GROUP IN(  \n");
			sql.append("    SELECT pens_value  \n");
			sql.append("    FROM  PENSBI.PENSBME_MST_REFERENCE  \n");
			sql.append("    where reference_code = 'Customer'  \n");
			sql.append("    and  pens_desc6 = 'Auto Sub-transfer' \n");
			sql.append(" ) \n");
			
			logger.debug("sql: \n"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				//get detail from store
				bean = cri;
				bean.setFromCustGroup(Utils.isNull(rs.getString("CUST_GROUP")));
				bean.setFromStoreCode(Utils.isNull(rs.getString("STORE_CODE")));
				bean.setFromSubInv(Utils.isNull(rs.getString("SUB_INV")));
				// getStoreName
				bean.setFromStoreName(GeneralDAO.getStoreNameModel(conn,bean.getFromStoreCode()));
				bean.setTotalQty(Utils.decimalFormat(rs.getDouble("QTY"),Utils.format_current_no_disgit));
				bean.setTotalBox(Utils.decimalFormat(rs.getDouble("TOTAL_BOX"),Utils.format_current_no_disgit));
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
		return bean;
	}
	 public static int insertB2BHeadModel(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("INSERT INTO PENSBI.PENSBME_AUTO_SUBTRANS_B2B \n");
				sql.append("(REF_NO,REF_TYPE \n");
				sql.append(", FROM_SUBINVENTORY, FROM_CUST_GROUP, FROM_STORE_CODE ,FROM_STORE_NAME \n");
				sql.append(", TO_SUBINVENTORY, TO_CUST_GROUP, TO_STORE_CODE ,TO_STORE_NAME \n");
				sql.append(",STATUS, TOTAL_BOX, TOTAL_QTY, CREATE_DATE, CREATE_USER,Forwarder,Forwarder_box ,reason) \n");
				sql.append("VALUES(?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?) \n");//18
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(head.getRefNo()));
				ps.setString(c++, Utils.isNull(head.getRefType()));
				
				ps.setString(c++, Utils.isNull(head.getFromSubInv())); 
				ps.setString(c++, Utils.isNull(head.getFromCustGroup())); 
				ps.setString(c++, Utils.isNull(head.getFromStoreCode())); 
				ps.setString(c++, Utils.isNull(head.getFromStoreName())); 
				
				ps.setString(c++, Utils.isNull(head.getToSubInv())); 
				ps.setString(c++, Utils.isNull(head.getToCustGroup())); 
				ps.setString(c++, Utils.isNull(head.getToStoreCode())); 
				ps.setString(c++, Utils.isNull(head.getToStoreName())); 
				
				ps.setString(c++, Utils.isNull(head.getStatus())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalBox())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalQty())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(head.getForwarder())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getForwarderBox())); 
				ps.setString(c++, Utils.isNull(head.getReason())); 
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
	 
	 public static int updateHeadModel(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_AUTO_SUBTRANS_B2B \n");
				sql.append(" SET TOTAL_BOX = ? , TOTAL_QTY =?, INTFLAG='' , CREATE_DATE =? \n");
				sql.append(",UPDATE_DATE = ?, UPDATE_USER = ? ,Forwarder=? , Forwarder_box=? \n");
				sql.append(" WHERE REF_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalBox())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalQty())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(head.getForwarder())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getForwarderBox())); 
				
				ps.setString(c++, Utils.isNull(head.getRefNo()));

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
	 
	 public static int updateStatusB2BHeadModel(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusB2BHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_AUTO_SUBTRANS_B2B \n");
				sql.append(" SET  STATUS =? ,UPDATE_DATE = ?, UPDATE_USER = ?  \n");
				sql.append(" WHERE REF_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, head.getStatus()); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				
				ps.setString(c++, Utils.isNull(head.getRefNo()));

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
	 
	 public static int updateStatusB2BDetailModel(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusB2BDetailModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_AUTO_SUBTRANS_B2B_ITEM \n");
				sql.append(" SET  STATUS =? ,UPDATE_DATE = ?, UPDATE_USER = ?  \n");
				sql.append(" WHERE REF_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, head.getStatus()); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				
				ps.setString(c++, Utils.isNull(head.getRefNo()));

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
	 public static int deleteB2BHeadByPK(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("DELETE FROM PENSBI.PENSBME_AUTO_SUBTRANS_B2B \n");
				sql.append("WHERE REF_NO = ? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(head.getRefNo()));
				
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
	 public static int deleteB2BItemByPK(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("DELETE FROM PENSBI.PENSBME_AUTO_SUBTRANS_B2B_ITEM \n");
				sql.append("WHERE REF_NO = ? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(head.getRefNo()));
				
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
	
	  public static AutoSubB2BBean searchProductByPensItem(PopupForm c,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			AutoSubB2BBean b = null;
			String itemType = Constants.STORE_TYPE_BIGC_ITEM;
			try {
				sql.append("\n select MS.pens_value as pens_item ");
				sql.append("\n ,M.description as item_name,M.INVENTORY_ITEM_ID ");
			
				sql.append("\n  ,(select unit_price from pensbme_unit_price_v ");
				sql.append("\n    where customer_code = '"+storeCode+"' ");
				sql.append("\n    and inventory_item_id = M.INVENTORY_ITEM_ID  ");
				sql.append("\n    and rownum = 1 ) as unit_price");
				
				/*sql.append("\n  ,(SELECT max(P.price) from xxpens_bi_mst_price_list P " );
				sql.append("\n    where P.product_id =M.INVENTORY_ITEM_ID " );
				sql.append("\n    and P.primary_uom_code ='Y' " );
				sql.append("\n    and P.pricelist_id ="+priceListId+") as unit_price");
				*/
				
				sql.append("\n from PENSBI.PENSBME_MST_REFERENCE MS ,PENSBI.xxpens_om_item_mst_v M ");
	            sql.append("\n where 1=1 ");
		        sql.append("\n and M.segment1 = MS.pens_value ");
				sql.append("\n and MS.reference_code ='"+itemType+"' ");
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and MS.pens_value= '"+c.getCodeSearch()+"' \n");
				}
				if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
					sql.append("\n and MS.interface_value ='"+c.getMatCodeSearch()+"'");
				}
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					b = new AutoSubB2BBean();
					b.setPensItem(Utils.isNull(rst.getString("pens_item")));
					b.setItemName(Utils.isNull(rst.getString("item_name")));
					b.setInventoryItemId(Utils.isNull(rst.getString("INVENTORY_ITEM_ID")));
					b.setUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price") ,Utils.format_current_2_disgit));
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return b;
		}
	  
	  public static int updateNewStoreByWacoal(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateNewStoreByWacoal");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_ORDER \n");
				sql.append(" SET  STORE_TYPE  =?, STORE_CODE =? ,UPDATE_DATE = ?, UPDATE_USER = ?  \n");
				sql.append(" WHERE ORDER_LOT_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, head.getToCustGroup()); 
				ps.setString(c++, head.getToStoreCode()); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				
				ps.setString(c++, Utils.isNull(head.getRefNo()));

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
	  public static int updateNewStoreByPic(Connection conn,AutoSubB2BBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateNewStoreByPic");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				/**Stock_issue **/
				sql.append("UPDATE PENSBI.PENSBME_STOCK_ISSUE \n");
				sql.append(" SET  CUST_GROUP  =?, CUSTOMER_NO =?,STORE_NO =? , SUB_INV =? ,UPDATE_DATE = ?, UPDATE_USER = ?  \n");
				sql.append(" WHERE ISSUE_REQ_NO =? \n");
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, head.getToCustGroup()); 
				ps.setString(c++, head.getToStoreCode()); 
				ps.setString(c++, head.getToStoreNo()); 
				ps.setString(c++, head.getToSubInv()); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(head.getRefNo()));
				r =ps.executeUpdate();
				
				/**Stock_PICK **/
				c =1;
				sql = new StringBuffer("");
				sql.append("UPDATE PENSBI.PENSBME_PICK_STOCK \n");
				sql.append(" SET  CUST_GROUP  =?, STORE_CODE =?,STORE_NO =? , SUB_INV =? ,UPDATE_DATE = ?, UPDATE_USER = ?  \n");
				sql.append(" WHERE ISSUE_REQ_NO =? \n");
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, head.getToCustGroup()); 
				ps.setString(c++, head.getToStoreCode()); 
				ps.setString(c++, head.getToStoreNo()); 
				ps.setString(c++, head.getToSubInv()); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(head.getRefNo()));
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
