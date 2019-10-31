package com.isecinc.pens.web.manualstock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Job;
import com.isecinc.pens.web.mtt.MTTAction;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;
public class ManualStockDAO{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	protected static SimpleDateFormat dfUs = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	
	public static int searchHeadTotalRecList(Connection conn,ManualStockBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c  from (");
			sql.append("\n   select *" );
			sql.append("\n   from PENSBI.PENSBME_MANUAL_STOCK S");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append("   "+genWhereSearchHeadList(o).toString());
            sql.append("\n   )A ");
         
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
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
	public static ManualStockBean searchHeadTotalSummary(Connection conn,ManualStockBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ManualStockBean h = null;
		try {
			sql.append("\n  select sum(qty) as qty");;
			sql.append("\n  from PENSBI.PENSBME_MANUAL_STOCK S");
		    sql.append("\n  where 1=1 ");
		    //Where Condition
		    sql.append("   "+genWhereSearchHeadList(o).toString());
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				h = new ManualStockBean();
	            h.setQty(rst.getInt("qty"));
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
	public static List<ManualStockBean> searchHeadList(Connection conn,ManualStockBean o,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ManualStockBean h = null;
		List<ManualStockBean> items = new ArrayList<ManualStockBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n   select S.*" );
			sql.append("\n   from PENSBI.PENSBME_MANUAL_STOCK S");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append("   "+genWhereSearchHeadList(o).toString());
			sql.append("\n    order by doc_no desc ");
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
			
			//strart no by currPage
			r = Utils.calcStartNoInPage(currPage, ManualStockAction.pageSize);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ManualStockBean();
			   h.setNo(r);
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setCustGroupName(rst.getString("cust_group_name"));
			  // h.setSaleDate(Utils.stringValue(rst.getTimestamp("sale_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCreateDate(DateUtil.stringValue(rst.getTimestamp("create_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStoreCode(Utils.isNull(rst.getString("cust_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setRemark(Utils.isNull(rst.getString("remark")));
			   
               h.setBarcode(Utils.isNull(rst.getString("barcode")));
               h.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));
               h.setGroupCode(Utils.isNull(rst.getString("GROUP_CODE")));
               h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
               h.setRetailPriceBF(Utils.isNull(rst.getString("RETAIL_PRICE_BF")));
               h.setQty(rst.getInt("qty"));
               
               h.setStatus(Utils.isNull(rst.getString("status")));
               if("AB".equalsIgnoreCase(h.getStatus())){
            	   h.setStatusDesc("CANCEL");
            	   h.setBarcodeStyle("disableText");
   				   h.setCanCancel(false);
				   h.setCanEdit(false);
               }else{
            	   h.setStatusDesc("NEW");
            	   h.setBarcodeStyle("");  
            	   h.setCanCancel(true);
				   h.setCanEdit(true);
               }
               totalQty += h.getQty();
           
			   items.add(h);
			   r++;
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
	
	//OLD Method 
	public static ManualStockBean searchHead(ManualStockBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ManualStockBean h = null;
		List<ManualStockBean> items = new ArrayList<ManualStockBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n  select * from PENSBI.PENSBME_MANUAL_STOCK S");
			sql.append("\n where 1=1   \n");
		    sql.append(""+genWhereSearchHeadList(o));
			sql.append("\n order by doc_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ManualStockBean();
			   h.setNo(r);
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setCustGroupName(rst.getString("cust_group_name"));
			  // h.setSaleDate(Utils.stringValue(rst.getTimestamp("sale_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCreateDate(DateUtil.stringValue(rst.getTimestamp("create_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStoreCode(Utils.isNull(rst.getString("cust_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setRemark(Utils.isNull(rst.getString("remark")));
			   
               h.setBarcode(Utils.isNull(rst.getString("barcode")));
               h.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));
               h.setGroupCode(Utils.isNull(rst.getString("GROUP_CODE")));
               h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
               h.setRetailPriceBF(Utils.isNull(rst.getString("RETAIL_PRICE_BF")));
               h.setQty(rst.getInt("qty"));
               
               h.setStatus(Utils.isNull(rst.getString("status")));
               if("AB".equalsIgnoreCase(h.getStatus())){
            	   h.setStatusDesc("CANCEL");
            	   h.setBarcodeStyle("disableText");
   				   h.setCanCancel(false);
				   h.setCanEdit(false);
               }else{
            	   h.setStatusDesc("NEW");
            	   h.setBarcodeStyle("");  
            	   h.setCanCancel(true);
				   h.setCanEdit(true);
               }
               totalQty += h.getQty();
           
			   items.add(h);
			   r++;
			   
			}//while

			//set Result 
			o.setItems(items);
			o.setTotalQty(totalQty);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	public static StringBuffer genWhereSearchHeadList(ManualStockBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
		if( !Utils.isNull(o.getDocNo()).equals("")){
			sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
		}
		if( !Utils.isNull(o.getStoreCode()).equals("")){
			sql.append("\n and store_code = '"+Utils.isNull(o.getStoreCode())+"'  ");
		}
		if( !Utils.isNull(o.getStoreName()).equals("")){
			sql.append("\n and store_name = '"+Utils.isNull(o.getStoreName())+"'  ");
		}
		if( !Utils.isNull(o.getGroupCode()).equals("")){
			sql.append("\n and group_code = '"+Utils.isNull(o.getGroupCode())+"'  ");
		}
		
		if( !Utils.isNull(o.getTransDateFrom()).equals("")){
			Date fDate  = DateUtil.parse(o.getTransDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String fStr = DateUtil.stringValue(fDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and trunc(TRANS_DATE) >= to_date('"+fStr+"','dd/mm/yyyy') ");
		}
		
		if( !Utils.isNull(o.getTransDateTo()).equals("")){
			Date tDate  = DateUtil.parse(o.getTransDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);

			sql.append("\n and trunc(TRANS_DATE) <= to_date('"+tStr+"','dd/mm/yyyy') ");
		}

		return sql;
	}
	
	
	public static ManualStockBean searchItem(ManualStockBean o) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchItemModel(conn,o);
		}catch(Exception e){
			throw e;
		}finally{
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	
	public static ManualStockBean searchItemModel(Connection conn,ManualStockBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		
		ManualStockBean h = null;
		List<ManualStockBean> items = new ArrayList<ManualStockBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n  select line_id,doc_no,sale_date ,cust_group,cust_no,barcode," +
					"\n MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF" +
		            "\n ,status"+
					" from PENSBME_SALES_OUT ");
			
			sql.append("\n where 1=1   \n");
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			}

			sql.append("\n order by doc_no,sale_date,line_id ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ManualStockBean();
			   h.setNo(r);
			   h.setLineId(rst.getInt("line_id"));
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			  // h.setSaleDate(Utils.stringValue(rst.getTimestamp("sale_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStoreCode(Utils.isNull(rst.getString("cust_no"))); 
               h.setBarcode(Utils.isNull(rst.getString("barcode")));
               h.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));
               h.setGroupCode(Utils.isNull(rst.getString("GROUP_CODE")));
               h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
               h.setRetailPriceBF(Utils.isNull(rst.getString("RETAIL_PRICE_BF")));
               h.setStatus(Utils.isNull(rst.getString("status")));
               
               if("AB".equalsIgnoreCase(h.getStatus())){
            	   h.setStatusDesc("CANCEL");
            	   h.setBarcodeStyle("disableText");
               }else{
            	   h.setStatusDesc("NEW");
            	   h.setBarcodeStyle("");  
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
	
	public static int getMaxLineIdByDocNo(ManualStockBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
        int maxLineId= 0;
		try {
			sql.append("\n select max(line_id) as max_line_id from PENSBI.PENSBME_MANUAL_STOCK ");
			sql.append("\n where 1=1   \n");
			sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
		      maxLineId = rst.getInt("max_line_id");
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
		return maxLineId;
	}
	
	public static ManualStockBean searchReport(ManualStockBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ManualStockBean h = null;
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select i.* ,j.name  from PENSBI.PENSBME_PICK_BARCODE i INNER JOIN  \n");
			sql.append("\n ( ");
			sql.append("\n    select distinct job_id,name from PENSBME_PICK_JOB ");
			sql.append("\n ) j on i.job_id = j.job_id");
			sql.append("\n where 1=1   \n");
			//sql.append("\n and i.status in('"+PickConstants.BARCODE_ITEM_STATUS_CLOSE+"','"+PickConstants.BARCODE_ITEM_STATUS_OPEN+"')");
			
			/*if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}*/
			
			sql.append("\n order by i.job_id ,i.box_no asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
		  
			   h = new ManualStockBean();
			  // h.setNo(r);
			  /* h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setTransactionDate(Utils.stringValue(rst.getTimestamp("transaction_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   
			   h.setName(Utils.isNull(rst.getString("name"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(JobDAO.getStatusDesc(Utils.isNull(rst.getString("status")))); */
			  
			   r++;
			}//while

			if(h != null){
				//h.setItems(searchItemReport(conn,h));
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	
	
	public static ManualStockBean save(ManualStockBean h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check documentNo
			if(Utils.isNull(h.getDocNo()).equals("")){
				//Gen JobId
				h.setDocNo(genDocNo(new Date()) );
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ManualStockBean l = (ManualStockBean)h.getItems().get(i);
					   l.setDocNo(h.getDocNo());
					   l.setTransDate(h.getTransDate());
					   l.setStoreCode(h.getStoreCode());
					   l.setStoreName(h.getStoreName());
					   l.setStoreNo(h.getStoreNo());
					   l.setSubInv(h.getSubInv());
					   
				       saveModel(conn, l);
				   }
				}
			}else{
				
				//delete all item by doc no
				deleteModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ManualStockBean l = (ManualStockBean)h.getItems().get(i);
					   l.setDocNo(h.getDocNo());
					   l.setTransDate(h.getTransDate());
					   l.setCustGroup(h.getCustGroup());
					   l.setStoreCode(h.getStoreCode());
					   l.setStoreName(h.getStoreName());
					   l.setStoreNo(h.getStoreNo());
					   l.setSubInv(h.getSubInv());
					   
				       saveModel(conn, l);
				   }
				}
			}
			
			conn.commit();
			
			return h;
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	// ( Running :  yymmxxxx  เช่น 57030001 )		 yyyymmxxxx  เช่น 2014120001	
	 public static String genDocNo(Date date) throws Exception{
       String docNo = "";
       Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection();
			   String today = dfUs.format(date);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"MANUAL_STOCK","DOC_NO",date);
			   
			   docNo = new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn != null){
				   conn.close();conn=null;
			   }
		   }
		  return docNo;
	}
		 
	 
	 public static void saveModel(Connection conn,ManualStockBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO saveModel \n");
				sql.append(" (TRANS_DATE,DOC_NO, LINE_ID,   \n");
				sql.append("  STORE_CODE, STORE_NAME, STORE_NAME, \n");
				sql.append("  PENS_ITEM, MATERIAL_MASTER, GROUP_CODE,QTY ,\n");
				sql.append("  CREATE_DATE,CREATE_USER)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date saleDate = DateUtil.parse( o.getTransDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				ps.setTimestamp(c++, new java.sql.Timestamp(saleDate.getTime()));
				ps.setString(c++, o.getDocNo());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getStoreCode());
				ps.setString(c++, o.getStoreName());
				ps.setString(c++, o.getPensItem());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setDouble(c++, o.getQty());
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

		public static int deleteModel(Connection conn,ManualStockBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE FROM PENSBI.PENSBME_MANUAL_STOCK    \n");
				sql.append(" WHERE DOC_NO = ? and LINE_ID = ? \n" );

				ps = conn.prepareStatement(sql.toString());

				ps.setString(c++, Utils.isNull(o.getDocNo()));
				ps.setInt(c++, o.getLineId());
				
				c = ps.executeUpdate();
				
				return c;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		
		public static int updateMTTStatusByDocNo(Connection conn,ManualStockBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_SALES_OUT SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE DOC_NO = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setString(c++, Utils.isNull(o.getDocNo()));
				
				c = ps.executeUpdate();
				
				return c;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}

}
