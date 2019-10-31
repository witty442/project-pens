package com.isecinc.pens.web.itmanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.BahtText;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class ITStockDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 public static ITManageBean searchHead(Connection conn,ITManageBean o,boolean getTrans ,boolean allRec ,int currPage,int pageSize) throws Exception {
		 return searchHeadModel(conn, o,getTrans,allRec,currPage,pageSize);
		}
	   
	   public static ITManageBean searchHead(ITManageBean o ,boolean getTrans,boolean allRec ,int currPage,int pageSize) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchHeadModel(conn, o,getTrans,allRec,currPage,pageSize);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
	   public static List<ITManageBean> initItemList() throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			List<ITManageBean> itemList = new ArrayList<ITManageBean>();
			ITManageBean bean = null;
			try {
			    sql.append("\n select * from PENSBI.IT_STOCK_MASTER ");
				sql.append("\n order by seq asc ");
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				while(rst.next()) {
					bean = new ITManageBean();
					bean.setItemName(rst.getString("ITEM_NAME"));
					bean.setSeq(rst.getInt("seq"));
					itemList.add(bean);
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					conn.close();
				} catch (Exception e) {}
			}
			return itemList;
		}
	   
		public static int searchTotalHead(Connection conn,ITManageBean o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			int totalRec = 0;
			try {
				sql.append("\n select count(*) as c from(");
			    sql.append("\n select h.* ");
			    sql.append("\n from PENSBI.IT_STOCK h where 1=1 ");
				if( !Utils.isNull(o.getSalesrepCode()).equals("")){
					sql.append("\n and h.salesrep_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
				}
				if(!Utils.isNull(o.getDocDate()).equals("")){
					Date date = DateUtil.parse(o.getDocDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String dateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH);
					sql.append("\n and h.doc_date = to_date('"+dateStr+"','dd/mm/yyyy')");
				}
				sql.append("\n order by h.id desc ");
	            sql.append("\n   )A ");
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
		
		public static ITManageBean searchHeadModel(Connection conn,ITManageBean o ,boolean getTrans,boolean allRec ,int currPage,int pageSize) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			ITManageBean h = null;
			List<ITManageBean> items = new ArrayList<ITManageBean>();
			int r = 1;
			try {
				sql.append("\n select M.*");
				sql.append("\n ,(select distinct zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE Z where Z.zone = M.zone) as zone_name");
				sql.append("\n from (");
				sql.append("\n select A.* ,rownum as r__ from (");
				   sql.append("\n select h.* ");
				   sql.append("\n from PENSBI.IT_STOCK  h where 1=1 ");
				   if(o.getId() != 0){
					  sql.append("\n and h.id ="+o.getId()+"");
				   }
				   if( !Utils.isNull(o.getSalesrepCode()).equals("")){
					  sql.append("\n and h.salesrep_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
				   }
				   if(!Utils.isNull(o.getDocDate()).equals("")){
						Date date = DateUtil.parse(o.getDocDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						String dateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH);
						sql.append("\n and h.doc_date = to_date('"+dateStr+"','dd/mm/yyyy')");
				   }
				   sql.append("\n order by h.id desc ");
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
				   h = new ITManageBean();
				   h.setDocType(Utils.isNull(rst.getString("doc_type")));
				   h.setDocDate(DateUtil.stringValue(rst.getDate("doc_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				   h.setSalesrepFullName(Utils.isNull(rst.getString("salesrep_full_name")));
				   h.setId(rst.getInt("id"));
				   h.setZone(Utils.isNull(rst.getString("zone")));
				   h.setZoneName(Utils.isNull(rst.getString("zone_name")));
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   h.setCanPrint(true);
				  
	               //get Trans head
	               if(getTrans){
	            	   ITManageBean itemBean = searchITStockItemList(conn, o);
	            	   h.setItems(itemBean.getItems());
	               }
	               
				   items.add(h);
				   r++;
				}//while
				
				//set Result 
				o.setItems(items);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return o;
	}
	public static ITManageBean searchITStockItemList(Connection conn,ITManageBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<ITManageBean> items = new ArrayList<ITManageBean>();
			ITManageBean r = new ITManageBean();
			try {
			   sql.append("\n select * from PENSBI.IT_STOCK_ITEM");
			   sql.append("\n where 1=1 ");
			   sql.append("\n and id = "+Utils.isNull(o.getId())+"");
				sql.append("\n order by line_id asc ");
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				while(rst.next()) {
				   ITManageBean h = new ITManageBean();
				   h.setId(rst.getInt("id"));
				   h.setLineId(rst.getInt("line_id"));
				   h.setItemName(Utils.isNull(rst.getString("item_name")));
				   h.setSerialNo(Utils.isNull(rst.getString("serial_no")));
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   
				   if(rst.getDouble("qty") !=0.00){
				      h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
				   }else{
					  h.setQty("");
				   }
				   items.add(h);
				  
				}//while
				r.setItems(items);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return r;
	}
		
	public static void insertITStock(Connection conn,ITManageBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		int c=1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.IT_STOCK(ID,DOC_DATE,DOC_TYPE, \n");
			sql.append(" SALESREP_CODE ,SALESREP_FULL_NAME ,ZONE, \n" );
			sql.append(" UPDATE_USER ,UPDATE_DATE,REMARK )  \n" );
			sql.append(" VALUES(?,?,?,?,?,?,?,?,?)  \n" );
			ps = conn.prepareStatement(sql.toString());
			
            Date docDate = DateUtil.parse(o.getDocDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
            
            ps.setString(c++, Utils.isNull(o.getId()));
			ps.setTimestamp(c++, new java.sql.Timestamp(docDate.getTime()));
			ps.setString(c++, Utils.isNull(o.getDocType()));
			ps.setString(c++, Utils.isNull(o.getSalesrepCode()));
			ps.setString(c++, Utils.isNull(o.getSalesrepFullName()));
			ps.setString(c++, Utils.isNull(o.getZone()));
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getRemark()));
		   
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	 
 public static void insertITStockItem(Connection conn,ITManageBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.IT_STOCK_ITEM \n");
			sql.append(" (ID, LINE_ID, ITEM_NAME, SERIAL_NO, QTY , REMARK, CREATE_DATE, CREATE_USER) \n");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ? ,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			ps.setInt(c++, o.getId());
			ps.setInt(c++, o.getLineId());
			ps.setString(c++, o.getItemName());
			ps.setString(c++, o.getSerialNo());
			ps.setDouble(c++, Utils.convertStrToDouble(o.getQty()));
			ps.setString(c++, o.getRemark());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
		
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static int updateITStock(Connection conn,ITManageBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.IT_STOCK SET \n");
			sql.append(" DOC_DATE =?  ,DOC_TYPE =?, ");
			sql.append(" SALESREP_CODE =? ,SALESREP_FULL_NAME = ? ,ZONE =?, " );
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?  ,REMARK =?" );
			sql.append(" WHERE ID = ?  \n" );
			ps = conn.prepareStatement(sql.toString());
			
            Date docDate = DateUtil.parse(o.getDocDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

			ps.setTimestamp(c++, new java.sql.Timestamp(docDate.getTime()));
			ps.setString(c++, Utils.isNull(o.getDocType()));
			ps.setString(c++, Utils.isNull(o.getSalesrepCode()));
			ps.setString(c++, Utils.isNull(o.getSalesrepFullName()));
			ps.setString(c++, Utils.isNull(o.getZone()));
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getRemark()));
		    ps.setString(c++, Utils.isNull(o.getId()));

			int r =ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

	public static int deleteITStock(Connection conn,int id) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE FROM PENSBI.IT_STOCK \n");
			sql.append(" WHERE ID = ?  \n" );
			logger.debug("sql:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			ps.setInt(c++, id);

			int r =ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static int deleteITStockItem(Connection conn,int id) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE FROM PENSBI.IT_STOCK_ITEM \n");
			sql.append(" WHERE ID = ?  \n" );
			logger.debug("sql:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			ps.setInt(c++, id);

			int r =ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void insertITMaster(Connection conn,ITManageBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertITMaster");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.IT_STOCK_MASTER \n");
			sql.append(" (ITEM_NAME, SEQ) \n");
			sql.append(" VALUES ('"+o.getItemName()+"', "+o.getSeq()+") \n");
			logger.debug("sql:\n"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static int updateITMaster(Connection conn,String itemName, ITManageBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateITMaster");
		int r =0 ;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.IT_STOCK_MASTER \n");
			sql.append(" SET ITEM_NAME= '"+o.getItemName()+"', SEQ ="+o.getSeq()+" \n");
			sql.append(" WHERE ITEM_NAME = '"+itemName+"' \n");
			logger.debug("sql:\n"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			r= ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

}
