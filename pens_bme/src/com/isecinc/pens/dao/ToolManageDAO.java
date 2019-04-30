package com.isecinc.pens.dao;

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

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.LockItemOrderBean;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.autocn.AutoCNBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.toolmanage.ToolManageBean;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;
import com.pens.util.helper.SequenceProcessAll;

public class ToolManageDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	static String priceListId = "10012";//Fix
	
	public static void main(String[] a){
		try{
		  System.out.println(""+genDocNo(new Date()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static List<ToolManageBean> searchHeadList(Connection conn,ToolManageBean o,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
		List<ToolManageBean> items = new ArrayList<ToolManageBean>();
		int no=1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n  select S.* from (");
            sql.append("\n   select ");
			sql.append("\n   j.doc_no ,j.doc_date ,j.status");
			sql.append("\n   ,j.store_code ,j.cust_group ,j.ref_rtn ,j.remark");
			sql.append("\n   ,(select m.pens_desc from  pensbme_mst_reference m ");
			sql.append("\n      where m.reference_code='Store' and pens_value =j.store_code ) as store_name ");
		 	sql.append("\n   from PENSBME_ITEM_INOUT j  ");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append(genWhereSearchList(o).toString());
            sql.append("\n     order by j.doc_no desc");
            sql.append("\n   )S WHERE 1=1 ");
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
			   h = new ToolManageBean();
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setDocDate(Utils.stringValue(rst.getDate("doc_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setRemark(Utils.isNull(rst.getString("remark")));
			   h.setRefRtn(Utils.isNull(rst.getString("ref_rtn")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   //O=OPEN ,P =POST
			   if( Utils.isNull(rst.getString("status")).equalsIgnoreCase("POST")){
				   h.setCanSave(false);
			   }else{
				   h.setCanSave(true);
			   }
			   items.add(h);
			   no++;
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
	
	public static int searchTotalRecList(Connection conn,ToolManageBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
	    int totalRec = 0;
		try {
            sql.append("\n select count(*) as c from(");
            sql.append("\n select S.* from( ");
		    sql.append("\n  select * from PENSBME_ITEM_INOUT j  ");
		    sql.append("\n  where 1=1 ");
		    //Where Condition
		    sql.append(genWhereSearchList(o).toString());
		    sql.append("\n ) S where 1=1"); 
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
	
	
	public static List<ToolManageBean> searchItemList(Connection conn,ToolManageBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
		List<ToolManageBean> items = new ArrayList<ToolManageBean>();
		try {
            sql.append("\n  select d.id, d.item, d.qty");
            sql.append("\n  ,(select M.item_name from PENSBME_ITEM_MASTER M where M.item = d.item) as item_name");
		    sql.append("\n  from PENSBME_ITEM_INOUT_DETAIL d");
		    sql.append("\n  where 1=1 ");
		    //Where Condition
		    if( !Utils.isNull(o.getDocNo()).equals("")){
		    	 sql.append("\n  and d.doc_no='"+Utils.isNull(o.getDocNo())+"'");
		    }
            sql.append("\n   order by d.item");
            
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ToolManageBean();
			   h.setId(rst.getLong("id"));
			   h.setItem(Utils.isNull(rst.getString("item")));
			   h.setItemName(Utils.isNull(rst.getString("item_name")));
			   //h.setItemType(Utils.isNull(rst.getString("item_type")));
			   h.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
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
	
	public static StringBuffer genWhereSearchList(ToolManageBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getDocType()).equals("")){
		       sql.append("\n and j.doc_type = '"+Utils.isNull(o.getDocType())+"' ");
		}
	    if( !Utils.isNull(o.getCustGroup()).equals("")){
	       sql.append("\n and j.cust_group = '"+Utils.isNull(o.getCustGroup())+"' ");
	    }
	    if( !Utils.isNull(o.getStoreCode()).equals("")){
	       sql.append("\n and j.store_code = '"+Utils.isNull(o.getStoreCode())+"' ");
	    }
	    if( !Utils.isNull(o.getDocNo()).equals("")){
		     sql.append("\n and j.doc_no = '"+Utils.isNull(o.getDocNo())+"' ");
		}
	    
	    if( !Utils.isNull(o.getDocDate()).equals("")){
	       Date date = Utils.parse(Utils.isNull(o.getDocDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	       sql.append("\n and j.doc_date = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
	    }
		 return sql;
	}
	// ( Running :  Pyymmxxxx  เช่น P61030001 )			
		 public static String genDocNo(Date date) throws Exception{
	       String docNo = "";
	       Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection(); 
			   String today = df.format(date);
			   //logger.debug("today:"+today);
			   
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			  
			   //.debug("curYear:"+curYear);
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"DOC_NO","DOC_NO",date); 
			   docNo = "P"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
		  return docNo;
	}
		 
	public static void save(Connection conn,ToolManageBean head) throws Exception{
		int i=0;
		long id = 0;
		ToolManageBean item = null;
		try{
			//insert head case add
			if( Utils.isNull(head.getDocNo()).equals("")){
			   //gen Doc No
			   head.setDocNo(genDocNo(new Date()));
			   //insert to db
			   insertHeadModel(conn, head);
			   
			  //insert item
			    if(head.getItems() != null && head.getItems().size() >0){
			    	for(i=0;i<head.getItems().size();i++){
			    		item = head.getItems().get(i);
			    		id++;
			    		item.setId(id);
			    		item.setDocNo(head.getDocNo());
			    		item.setCreateUser(head.getCreateUser());
			    		//insert item
			    		insertItemModel(conn, item);
			    	}
			    }
			}else{
			   //update head
			   updateHeadModel(conn, head);
			   
			  //insert or update item
			  //get max id by doc_no
			   id = getMaxIdItemDetail(conn, head);
			    if(head.getItems() != null && head.getItems().size() >0){
			    	for(i=0;i<head.getItems().size();i++){
			    		item = head.getItems().get(i);
			    		item.setDocNo(head.getDocNo());
			    		item.setCreateUser(head.getCreateUser());
			    		item.setUpdateUser(head.getUpdateUser());
			    		
			    		logger.debug("id["+item.getId()+"]statusRow["+item.getStatus()+"]");
			    		if(item.getId() != 0 && !item.getStatus().equals("CANCEL") ){
			    		   //update item
			    		   upadteItemByPK(conn, item);
			    		}else if(item.getId() != 0 && item.getStatus().equals("CANCEL") ){
				    	   //update item
				    	   deleteItemByPK(conn, item);
			    		}else{
			    	       //insert item
			    		   id++;
			    		   item.setId(id);
			    		   insertItemModel(conn, item);
			    		}
			    	}
			    }
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	 public static int insertHeadModel(Connection conn,ToolManageBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("INSERT INTO PENSBI.PENSBME_ITEM_INOUT \n");
				sql.append("(Doc_no, doc_date,doc_type,ref_rtn, CUST_GROUP, STORE_CODE \n");
				sql.append(",STATUS,remark, CREATE_DATE, CREATE_USER) \n");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?,?) \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(head.getDocNo()));
				ps.setDate(c++, new java.sql.Date(Utils.parse(head.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime())); 
				ps.setString(c++, Utils.isNull(head.getDocType())); 
				ps.setString(c++, Utils.isNull(head.getRefRtn())); 
				ps.setString(c++, Utils.isNull(head.getCustGroup())); 
				ps.setString(c++, Utils.isNull(head.getStoreCode())); 
				ps.setString(c++, Utils.isNull(head.getStatus())); 
				ps.setString(c++, Utils.isNull(head.getRemark())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getCreateUser());
				
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
	 
	 public static int updateHeadModel(Connection conn,ToolManageBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_ITEM_INOUT \n");
				sql.append("SET DOC_DATE = ? , REF_RTN =?, CUST_GROUP= ?");
				sql.append(",STORE_CODE = ? , REMARK = ?");
				sql.append(",UPDATE_DATE = ?, UPDATE_USER = ? \n");
				sql.append("WHERE DOC_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setDate(c++, new java.sql.Date(Utils.parse(head.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime())); 
				ps.setString(c++, Utils.isNull(head.getRefRtn())); 
				ps.setString(c++, Utils.isNull(head.getCustGroup()));
				ps.setString(c++, Utils.isNull(head.getStoreCode())); 
				ps.setString(c++, Utils.isNull(head.getRemark()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUpdateUser());
				ps.setString(c++, Utils.isNull(head.getDocNo())); 
				
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
	 public static int updatePostStatus(Connection conn,ToolManageBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_ITEM_INOUT \n");
				sql.append("SET STATUS = ? ,UPDATE_DATE = ?, UPDATE_USER = ? \n");
				sql.append("WHERE DOC_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString()); 
				ps.setString(c++, Utils.isNull(head.getStatus()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUpdateUser());
				ps.setString(c++, Utils.isNull(head.getDocNo())); 
				
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
	 public static boolean isHeadExist(Connection conn,ToolManageBean head) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("isHeadExist");
			boolean r =false;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("select count(*) as c from PENSBI.PENSBME_ITEM_INOUT \n");
				sql.append("WHERE doc_no = '"+Utils.isNull(head.getDocNo())+"'  \n");
				logger.debug("sql:"+sql.toString());
	
		        ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					if(rs.getInt("c") >0){
						r = true;
					}
				}
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs !=null){
					rs.close();rs =null;
				}
			}
			return r;
		}
	 public static long getMaxIdItemDetail(Connection conn,ToolManageBean head) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("getMaxIdItem");
			long maxId = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("select max(id) as c from PENSBI.PENSBME_ITEM_INOUT_DETAIL \n");
				sql.append("WHERE doc_no = '"+Utils.isNull(head.getDocNo())+"'  \n");
				logger.debug("sql:"+sql.toString());
	
		        ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					maxId = rs.getLong("c") ;
				}
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs !=null){
					rs.close();rs =null;
				}
			}
			return maxId;
	}
	 public static int insertItemModel(Connection conn,ToolManageBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertItemModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("INSERT INTO PENSBI.PENSBME_ITEM_INOUT_DETAIL \n");
				sql.append("(ID,DOC_NO, ITEM, QTY ,CREATE_DATE, CREATE_USER) \n");
				sql.append("VALUES(?,?,?,?,?,?) \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	            ps.setLong(c++, item.getId());
				ps.setString(c++, Utils.isNull(item.getDocNo()));
				ps.setString(c++, Utils.isNull(item.getItem()));   
				ps.setDouble(c++, Utils.convertStrToDouble(item.getQty())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, item.getCreateUser());
				
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
	 public static int upadteItemByPK(Connection conn,ToolManageBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("upadteItemByPK");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_ITEM_INOUT_DETAIL \n");
				sql.append("SET ITEM =?  ,QTY = ? ,UPDATE_DATE =?,UPDATE_USER = ? \n");
				sql.append("WHERE DOC_NO = ? AND ID = ?  \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(item.getItem()));      
				ps.setDouble(c++, Utils.convertStrToDouble(item.getQty())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, item.getUpdateUser());
				ps.setString(c++, Utils.isNull(item.getDocNo())); 
				ps.setLong(c++, item.getId());
				
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
	 
	 public static int deleteItemByPK(Connection conn,ToolManageBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("deleteItemByPK");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("DELETE FROM PENSBI.PENSBME_ITEM_INOUT_DETAIL \n");
				sql.append("WHERE DOC_NO = ? AND ID = ?  \n");
			
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(item.getDocNo())); 
				ps.setLong(c++, item.getId());
				
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
	 
	  public static ToolManageBean searchItemMaster(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			ToolManageBean b = null;
			try {
				sql.append("\n select * FROM PENSBI.PENSBME_ITEM_MASTER M ");
	            sql.append("\n where 1=1 ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and M.item= '"+c.getCodeSearch()+"' \n");
				}
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					b = new ToolManageBean();
					b.setItem(Utils.isNull(rst.getString("item")));
					b.setItemName(Utils.isNull(rst.getString("item_name")));
					
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
  
	  public static List<ToolManageBean> searchItemMasterList(Connection conn) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			ToolManageBean b = null;
			List<ToolManageBean> itemList = new ArrayList<ToolManageBean>();
			try {
				sql.append("\n select * FROM PENSBI.PENSBME_ITEM_MASTER M ");
	            sql.append("\n where 1=1 order by item");
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while(rst.next()) {
					b = new ToolManageBean();
					b.setItem(Utils.isNull(rst.getString("item")));
					b.setItemName(Utils.isNull(rst.getString("item_name")));
					itemList.add(b);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return itemList;
		}
}
