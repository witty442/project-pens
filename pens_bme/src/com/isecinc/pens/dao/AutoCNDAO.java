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

import util.Constants;
import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.LockItemOrderBean;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcessAll;
import com.isecinc.pens.web.autocn.AutoCNBean;
import com.isecinc.pens.web.popup.PopupForm;

public class AutoCNDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	static String priceListId = "10012";//Fix
	
	public static List<AutoCNBean> searchJobList(Connection conn,AutoCNBean o,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		AutoCNBean h = null;
		List<AutoCNBean> items = new ArrayList<AutoCNBean>();
		int no=1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
            sql.append("\n   select ");
			sql.append("\n   j.job_id ,j.name ");
			sql.append("\n   ,j.store_code ,j.cust_group");
			sql.append("\n   ,(select m.pens_desc from  pensbme_mst_reference m ");
			sql.append("\n      where m.reference_code='Store' and pens_value =j.store_code ) as store_name ");
			sql.append("\n   ,j.rtn_no ");
			sql.append("\n   ,(select count(*) as c from pensbme_pick_barcode b where b.status not in ('O','AB') and b.job_id = j.job_id group by job_id) as total_box ");
		    sql.append("\n   ,(select count(*) as c from pensbme_pick_barcode_item bi where bi.status not in ('O','AB') and bi.job_id = j.job_id group by job_id) as total_qty ");
		    sql.append("\n   ,(select a.status from PENSBME_APPROVE_TO_AUTOCN a ");
		    sql.append("\n     where a.job_id = j.job_id and a.rtn_no = j.rtn_no");
		    sql.append("\n   ) as status ");
		    sql.append("\n   ,(select a.intflag from PENSBME_APPROVE_TO_AUTOCN a ");
		    sql.append("\n     where a.job_id = j.job_id and a.rtn_no = j.rtn_no");
		    sql.append("\n   ) as status_interface ");
		    sql.append("\n   ,(select nvl(sum(qty),0) as c from PENSBME_APPROVE_TO_AUTOCN_ITEM bi where bi.job_id = j.job_id group by job_id) as total_save_qty ");
		    sql.append("\n   from pensbme_pick_job j  ");
		    sql.append("\n   where 1=1 ");
		    sql.append("\n   and j.status not in ('O','AB') ");
		    //Where Condition
		    sql.append(genWhereSearchJobList(o).toString());
            sql.append("\n    order by j.job_id desc");
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
			   h = new AutoCNBean();
			   h.setJobId(Utils.isNull(rst.getString("job_id")));
			   h.setJobName(Utils.isNull(rst.getString("name")));
			   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setTotalBox(Utils.decimalFormat(rst.getDouble("total_box") ,Utils.format_current_no_disgit));
			   if(rst.getInt("total_save_qty") >0){
				   h.setTotalQty(Utils.decimalFormat(rst.getDouble("total_save_qty"),Utils.format_current_no_disgit));  
			   }else{
			      h.setTotalQty(Utils.decimalFormat(rst.getDouble("total_qty"),Utils.format_current_no_disgit));
			   }
			   h.setRtnNo(Utils.isNull(rst.getString("rtn_no")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   //e ,s
			   if( !Utils.isNull(rst.getString("status_interface")).equals("")){
				   if( Utils.isNull(rst.getString("status_interface")).equalsIgnoreCase("s")){
					   h.setStatus("SUCCESS");
					   h.setCanSave(false);
				   }else{
					   h.setStatus("ERROR");
					   h.setCanSave(true);
				   }
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
	public static AutoCNBean searchItemListCaseNew(Connection conn,AutoCNBean h) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try {
            sql.append("\n   select ");
			sql.append("\n   nvl(count(*),0) as total_box");
		    sql.append("\n  ,(select count(*) as c from pensbme_pick_barcode_item bi where bi.status not in ('O','AB') and bi.job_id = j.job_id group by job_id) as total_qty ");
		    sql.append("\n   from pensbme_pick_barcode j ");
		    sql.append("\n   where 1=1 ");
		    sql.append("\n   and j.status not in ('O','AB') ");
		    sql.append("\n   and j.job_id="+Utils.isNull(h.getJobId()));
            sql.append("\n   group by  j.job_id");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   h.setTotalQty(Utils.decimalFormat(rst.getDouble("total_qty"),Utils.format_current_no_disgit));
			   h.setTotalBox(Utils.decimalFormat(rst.getDouble("total_box") ,Utils.format_current_no_disgit));
			   h.setItems(searchItemDetailListCaseNew(conn, h));
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static List<AutoCNBean> searchItemDetailListCaseNew(Connection conn,AutoCNBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		AutoCNBean h = null;
		List<AutoCNBean> items = new ArrayList<AutoCNBean>();
		try {
			sql.append("\n select A.* ");
			sql.append("\n ,nvl((A.qty * A.unit_price),0) as amount");
			sql.append("\n from (");
            sql.append("\n    select ");
			sql.append("\n    j.pens_item");
			sql.append("\n  , M.INVENTORY_ITEM_DESC as item_name,M.INVENTORY_ITEM_ID ");
			sql.append("\n  ,(select unit_price from pensbme_unit_price_v ");
			sql.append("\n    where customer_code = '"+o.getStoreCode()+"' ");
			sql.append("\n    and inventory_item_id = M.INVENTORY_ITEM_ID  ");
			sql.append("\n    and rownum = 1 ) as unit_price");
			
			/*sql.append("\n  , (SELECT max(P.price) from xxpens_bi_mst_price_list P " );
			sql.append("\n     where P.product_id =M.INVENTORY_ITEM_ID " );
			sql.append("\n     and P.primary_uom_code ='Y' " );
			sql.append("\n     and P.pricelist_id ="+priceListId+") as unit_price");*/
			
			sql.append("\n  , nvl(count(*),0) as qty");
		    sql.append("\n   from pensbme_pick_barcode_item j ,XXPENS_BI_MST_ITEM M ");
		    sql.append("\n   where 1=1 ");
			sql.append("\n   and M.INVENTORY_ITEM_CODE =j.pens_item  ");
			sql.append("\n   and j.status not in('O','AB') ");
		    //Where Condition
		    if( !Utils.isNull(o.getJobId()).equals("")){
		    	 sql.append("\n  and j.job_id="+Utils.isNull(o.getJobId()));
		    }
            sql.append("\n    group by  j.pens_item, M.INVENTORY_ITEM_ID,M.INVENTORY_ITEM_DESC ");
            sql.append("\n    order by j.pens_item");
            sql.append("\n )A ");
            
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new AutoCNBean();
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setInventoryItemId(Utils.isNull(rst.getString("INVENTORY_ITEM_ID")));
			   h.setItemName(Utils.isNull(rst.getString("item_name")));
			   h.setUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price") ,Utils.format_current_2_disgit));
			   h.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
			   h.setAmount(Utils.decimalFormat(rst.getDouble("amount") ,Utils.format_current_2_disgit));
               h.setKeyData("");
               
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
	
	public static AutoCNBean searchItemListCaseView(Connection conn,AutoCNBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		AutoCNBean h = null;
		List<AutoCNBean> items = new ArrayList<AutoCNBean>();
		double totalQty = 0;
		double totalAmount = 0;
		String status = "";
		try {
            sql.append("\n  select ");
            sql.append("\n  h.status ,h.intflag as status_interface ");
			sql.append("\n  ,j.pens_item");
			sql.append("\n  ,M.INVENTORY_ITEM_DESC as item_name, M.INVENTORY_ITEM_ID ");
			sql.append("\n  ,j.unit_price");
			sql.append("\n  ,j.qty");
			sql.append("\n  ,(j.unit_price*j.qty) as amount");
		    sql.append("\n  from PENSBME_APPROVE_TO_AUTOCN h, PENSBME_APPROVE_TO_AUTOCN_ITEM j ");
		    sql.append("\n  ,XXPENS_BI_MST_ITEM M ");
		    sql.append("\n  where 1=1 ");
		    sql.append("\n  and h.job_id = j.job_id and h.rtn_no = j.rtn_no ");
			sql.append("\n  and M.INVENTORY_ITEM_CODE =j.pens_item  ");
		    //Where Condition
		    if( !Utils.isNull(o.getJobId()).equals("")){
		    	 sql.append("\n  and j.job_id="+Utils.isNull(o.getJobId()));
		    }
            sql.append("\n   order by j.pens_item");
            
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new AutoCNBean();
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setInventoryItemId(Utils.isNull(rst.getString("INVENTORY_ITEM_ID")));
			   h.setItemName(Utils.isNull(rst.getString("item_name")));
			   h.setUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price") ,Utils.format_current_2_disgit));
			   h.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
			   h.setAmount(Utils.decimalFormat(rst.getDouble("amount") ,Utils.format_current_2_disgit));
			  
			   h.setKeyData(h.getPensItem());
			   totalQty += rst.getDouble("qty");
			   totalAmount += rst.getDouble("amount");
			   items.add(h);
			   
			   status = Utils.isNull(rst.getString("status"));
			   
			   if( !Utils.isNull(rst.getString("status_interface")).equals("")){
				   if( Utils.isNull(rst.getString("status_interface")).equalsIgnoreCase("s")){
					   h.setStatus("SUCCESS");
					   h.setCanSave(false);
					   status ="SUCCESS";
				   }else{
					   h.setStatus("ERROR");
					   h.setCanSave(true);
					   status ="ERROR";
				   }
			   }
			}//while
			o.setStatus(status);
			o.setItems(items);
			o.setTotalQty(Utils.decimalFormat(totalQty,Utils.format_current_no_disgit));
		    o.setTotalAmount(Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit));
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
	
	public static AutoCNBean searchItemListCaseViewCN(Connection conn,AutoCNBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		AutoCNBean h = null;
		List<AutoCNBean> items = new ArrayList<AutoCNBean>();
		try {
            sql.append("\n  select ");
            sql.append("\n   h.rma_order ,h.cn_no,h.cn_date");
			sql.append("\n  ,h.ref_inv ,h.line_number ");
			sql.append("\n  ,h.segment1 as pens_item,h.description as item_name");
			sql.append("\n  ,h.ordered_quantity as qty ,h.unit_list_price as unit_price,h.amount");
		    sql.append("\n  from xxpens_om_rtn_order_ref_v h");
		    sql.append("\n  where 1=1 ");
		    sql.append("\n  and h.rtn_no ='"+o.getRtnNo()+"'");
            sql.append("\n  order by h.rma_order");
            
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new AutoCNBean();
			   h.setSeq(rst.getInt("line_number")+"");
			   h.setRmaOrder(Utils.isNull(rst.getString("rma_order")));
			   h.setCnNo(Utils.isNull(rst.getString("cn_no")));
			   h.setCnDate(Utils.stringValueNull(rst.getDate("cn_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setRefInv(Utils.isNull(rst.getString("ref_inv")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setItemName(Utils.isNull(rst.getString("item_name")));
			   h.setUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price") ,Utils.format_current_2_disgit));
			   h.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
			   h.setAmount(Utils.decimalFormat(rst.getDouble("amount") ,Utils.format_current_2_disgit));
			   
			   items.add(h);
			 
			}//while
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
	public static int searchTotalRecJobList(Connection conn,AutoCNBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		AutoCNBean h = null;
	    int totalRec = 0;
		try {
            sql.append("\n select count(*) as c ");
		    sql.append("\n from pensbme_pick_job j  ");
		    sql.append("\n where 1=1 ");
		    sql.append("\n and j.status not in ('O','AB')");
		    //Where Condition
		    sql.append(genWhereSearchJobList(o).toString());
         
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
	public static StringBuffer genWhereSearchJobList(AutoCNBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		sql.append("\n and j.rtn_no is not null ");
		sql.append("\n and j.store_code is not null ");
	    if( !Utils.isNull(o.getCustGroup()).equals("")){
	       sql.append("\n and j.cust_group = '"+Utils.isNull(o.getCustGroup())+"' ");
	    }
	    if( !Utils.isNull(o.getStoreCode()).equals("")){
	       sql.append("\n and j.store_code = '"+Utils.isNull(o.getStoreCode())+"' ");
	    }
	    if( !Utils.isNull(o.getJobId()).equals("")){
		       sql.append("\n and j.job_id = "+Utils.isNull(o.getJobId())+" ");
		    }
	    if( !Utils.isNull(o.getRtnNo()).equals("")){
	       sql.append("\n and j.rtn_no = '"+Utils.isNull(o.getRtnNo())+"' ");
	    }
	    if( !Utils.isNull(o.getCuttOffDate()).equals("")){
	       Date date = Utils.parse(Utils.isNull(o.getCuttOffDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	       sql.append("\n and j.close_date >= to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
	    }
		 return sql;
	}
	
	public static void save(Connection conn,AutoCNBean head) throws Exception{
		int i=0;
		AutoCNBean item = null;
		try{
			//insert head case edit
			if( !isHeadExist(conn,head) ){
			   insertHeadModel(conn, head);
			}else{
			  updateHeadModel(conn, head);
			}
			
			//delete item by job_id ,rtn_no
			deleteItemByPK(conn, head, item);
			
			//insert item
		    if(head.getItems() != null && head.getItems().size() >0){
		    	for(i=0;i<head.getItems().size();i++){
		    		item = head.getItems().get(i);
		    		
		    		//insert item
		    		insertItemModel(conn, head, item);
		    	}
		    }
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void cancel(Connection conn,AutoCNBean head) throws Exception{
		try{
			//update status head and item
			//updateStatusHeadModel(conn, head);
			//updateStatusItemModel(conn, head);
			
		}catch(Exception e){
			throw e;
		}
	}
	
	 public static int insertHeadModel(Connection conn,AutoCNBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("INSERT INTO PENSBI.PENSBME_APPROVE_TO_AUTOCN \n");
				sql.append("(JOB_ID, RTN_NO, CUST_GROUP, STORE_CODE \n");
				sql.append(",STATUS, TOTAL_BOX, TOTAL_QTY, CREATE_DATE, CREATE_USER) \n");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?) \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(head.getJobId()));
				ps.setString(c++, Utils.isNull(head.getRtnNo())); 
				ps.setString(c++, Utils.isNull(head.getCustGroup())); 
				ps.setString(c++, Utils.isNull(head.getStoreCode())); 
				ps.setString(c++, Utils.isNull(head.getStatus())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalBox())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalQty())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				
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
	 
	 public static int updateHeadModel(Connection conn,AutoCNBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.PENSBME_APPROVE_TO_AUTOCN \n");
				sql.append("SET TOTAL_BOX = ? , TOTAL_QTY =? , UPDATE_DATE = ?, UPDATE_USER = ? \n");
				sql.append("WHERE JOB_ID =? AND RTN_NO =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalBox())); 
				ps.setDouble(c++, Utils.convertStrToDouble(head.getTotalQty())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(head.getJobId()));
				ps.setString(c++, Utils.isNull(head.getRtnNo())); 
				
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
	 public static boolean isHeadExist(Connection conn,AutoCNBean head) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("isHeadExist");
			boolean r =false;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("select count(*) as c from PENSBI.PENSBME_APPROVE_TO_AUTOCN \n");
				sql.append("WHERE job_id = "+Utils.isNull(head.getJobId())+" and rtn_no = '"+Utils.isNull(head.getRtnNo())+"'  \n");
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
	 
	 public static int insertItemModel(Connection conn,AutoCNBean head,AutoCNBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertItemModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("INSERT INTO PENSBI.PENSBME_APPROVE_TO_AUTOCN_ITEM \n");
				sql.append("(JOB_ID, RTN_NO, PENS_ITEM, UNIT_PRICE \n");
				sql.append(",QTY , STATUS, CREATE_DATE, CREATE_USER,INVENTORY_ITEM_ID) \n");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?) \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(head.getJobId()));
				ps.setString(c++, Utils.isNull(head.getRtnNo())); 
				ps.setString(c++, Utils.isNull(item.getPensItem())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getUnitPrice())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getQty())); 
				ps.setString(c++, Utils.isNull(head.getStatus())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(item.getInventoryItemId())); 
				
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
	 public static int updateStatusItemModel(Connection conn,AutoCNBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("update PENSBI.PENSBME_APPROVE_TO_AUTOCN_ITEM \n");
				sql.append("SET status = ? ,UPDATE_DATE = ? ,UPDATE_USER = ? \n");
				sql.append("WHERE job_id = ? and rtn_no = ?  \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(head.getStatus()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setString(c++, Utils.isNull(head.getJobId()));
				ps.setString(c++, Utils.isNull(head.getRtnNo())); 
	
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
	 public static int deleteItemByPK(Connection conn,AutoCNBean head,AutoCNBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("DELETE FROM PENSBI.PENSBME_APPROVE_TO_AUTOCN_ITEM \n");
				sql.append("WHERE JOB_ID = ? AND RTN_NO = ?  \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(head.getJobId()));
				ps.setString(c++, Utils.isNull(head.getRtnNo())); 
				
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
	
	  public static AutoCNBean searchProductByPensItem(PopupForm c,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			AutoCNBean b = null;
			String itemType = "LotusItem";
			
			try {
				if(storeCode.startsWith(Constants.STORE_TYPE_FRIDAY_CODE)){
					itemType ="FridayItem";
				}
				sql.append("\n select MS.pens_value as pens_item ");
				sql.append("\n ,M.INVENTORY_ITEM_DESC as item_name,M.INVENTORY_ITEM_ID ");
				
				/*sql.append("\n  ,(SELECT max(P.price) from xxpens_bi_mst_price_list P " );
				sql.append("\n    where P.product_id =M.INVENTORY_ITEM_ID " );
				sql.append("\n    and P.primary_uom_code ='Y' " );
				sql.append("\n    and P.pricelist_id ="+priceListId+") as unit_price");
				*/
				sql.append("\n  ,(select unit_price from pensbme_unit_price_v ");
				sql.append("\n    where customer_code = '"+storeCode+"' ");
				sql.append("\n    and inventory_item_id = M.INVENTORY_ITEM_ID  ");
				sql.append("\n    and rownum = 1 ) as unit_price");
				
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE MS ,PENSBI.XXPENS_BI_MST_ITEM M ");
	            sql.append("\n where 1=1 ");
		        sql.append("\n and M.INVENTORY_ITEM_CODE = MS.pens_value ");
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
					b = new AutoCNBean();
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
	  
	
	    
}
