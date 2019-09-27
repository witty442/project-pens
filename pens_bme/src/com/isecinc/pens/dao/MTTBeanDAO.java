package com.isecinc.pens.dao;

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
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.web.mtt.MTTAction;
import com.isecinc.pens.web.shop.ShopBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;
public class MTTBeanDAO{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	protected static SimpleDateFormat dfUs = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	
	public static String STATUS_NEW ="N";
	public static String STATUS_CANCEL ="AB";
	
	public static int searchHeadTotalRecList(Connection conn,MTTBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c  from (");
			sql.append("\n   select doc_no,sale_date ,cust_group,cust_no,barcode," );
			sql.append("\n   MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF ,remark,create_date");
			sql.append("\n   ,status,count(*) as qty");
			sql.append("\n   ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1 ");
			sql.append("\n     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  ");
			sql.append("\n   ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  ");
			sql.append("\n     and M.reference_code = 'Idwacoal' and M.pens_value = S.cust_group) as cust_group_name ");
			sql.append("\n   from PENSBME_SALES_OUT S");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append("   "+genWhereSearchHeadList(o).toString());
			sql.append("\n    group by doc_no,sale_date ,cust_group,cust_no,barcode,MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF,status,remark,create_date");
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
	public static MTTBean searchHeadTotalSummary(Connection conn,MTTBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		MTTBean h = null;
		try {
			sql.append("\n  select count(*) as qty");;
			sql.append("\n  from PENSBME_SALES_OUT S");
		    sql.append("\n  where 1=1 ");
		    //Where Condition
		    sql.append("   "+genWhereSearchHeadList(o).toString());
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				h = new MTTBean();
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
	public static List<MTTBean> searchHeadList(Connection conn,MTTBean o,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		MTTBean h = null;
		List<MTTBean> items = new ArrayList<MTTBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n   select doc_no,sale_date ,cust_group,cust_no,barcode," );
			sql.append("\n   MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF ,remark,create_date");
			sql.append("\n   ,status,count(*) as qty");
			sql.append("\n   ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1 ");
			sql.append("\n     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  ");
			sql.append("\n   ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  ");
			sql.append("\n     and M.reference_code = 'Idwacoal' and M.pens_value = S.cust_group) as cust_group_name ");
			sql.append("\n   from PENSBME_SALES_OUT S");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append("   "+genWhereSearchHeadList(o).toString());
			sql.append("\n    group by doc_no,sale_date ,cust_group,cust_no,barcode,MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF,status,remark,create_date");
			sql.append("\n    order by doc_no desc,GROUP_CODE desc ");
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
			r = Utils.calcStartNoInPage(currPage, MTTAction.pageSize);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new MTTBean();
			   h.setNo(r);
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setCustGroupName(rst.getString("cust_group_name"));
			   h.setSaleDate(DateUtil.stringValue(rst.getTimestamp("sale_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
	public static MTTBean searchHead(MTTBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MTTBean h = null;
		List<MTTBean> items = new ArrayList<MTTBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n  select doc_no,sale_date ,cust_group,cust_no,barcode," +
					"\n MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF ,remark,create_date" +
		            "\n ,status,count(*) as qty"+
					"\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
					"     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  "+
					"\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
					"     and M.reference_code = 'Idwacoal' and M.pens_value = S.cust_group) as cust_group_name  "+
					" from PENSBME_SALES_OUT S");
			
			sql.append("\n where 1=1   \n");
			sql.append("\n and status <> '"+STATUS_CANCEL+"' \n");
			
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and cust_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and group_code = '"+Utils.isNull(o.getGroupCode())+"'  ");
			}
			
			if( !Utils.isNull(o.getSaleDateFrom()).equals("")){
				Date fDate  = DateUtil.parse(o.getSaleDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String fStr = DateUtil.stringValue(fDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and trunc(SALE_DATE) >= to_date('"+fStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getSaleDateTo()).equals("")){
				Date tDate  = DateUtil.parse(o.getSaleDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String tStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);

				sql.append("\n and trunc(SALE_DATE) <= to_date('"+tStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getCreateDateFrom()).equals("")){
				Date fDate  = DateUtil.parse(o.getCreateDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String fStr = DateUtil.stringValue(fDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and trunc(CREATE_DATE) >= to_date('"+fStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getCreateDateTo()).equals("")){
				Date tDate  = DateUtil.parse(o.getCreateDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String tStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);

				sql.append("\n and trunc(CREATE_DATE) <= to_date('"+tStr+"','dd/mm/yyyy') ");
			}
			
			sql.append("\n group by doc_no,sale_date ,cust_group,cust_no,barcode,MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF,status,remark,create_date");
			sql.append("\n order by doc_no desc,GROUP_CODE desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MTTBean();
			   h.setNo(r);
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setCustGroupName(rst.getString("cust_group_name"));
			   h.setSaleDate(DateUtil.stringValue(rst.getTimestamp("sale_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
	public static StringBuffer genWhereSearchHeadList(MTTBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		sql.append("\n and status <> '"+STATUS_CANCEL+"' \n");
		
		if( !Utils.isNull(o.getDocNo()).equals("")){
			sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
		}
		if( !Utils.isNull(o.getCustGroup()).equals("")){
			sql.append("\n and cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
		}
		if( !Utils.isNull(o.getStoreCode()).equals("")){
			sql.append("\n and cust_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
		}
		if( !Utils.isNull(o.getGroupCode()).equals("")){
			sql.append("\n and group_code = '"+Utils.isNull(o.getGroupCode())+"'  ");
		}
		
		if( !Utils.isNull(o.getSaleDateFrom()).equals("")){
			Date fDate  = DateUtil.parse(o.getSaleDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String fStr = DateUtil.stringValue(fDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and trunc(SALE_DATE) >= to_date('"+fStr+"','dd/mm/yyyy') ");
		}
		
		if( !Utils.isNull(o.getSaleDateTo()).equals("")){
			Date tDate  = DateUtil.parse(o.getSaleDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);

			sql.append("\n and trunc(SALE_DATE) <= to_date('"+tStr+"','dd/mm/yyyy') ");
		}
		
		if( !Utils.isNull(o.getCreateDateFrom()).equals("")){
			Date fDate  = DateUtil.parse(o.getCreateDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String fStr = DateUtil.stringValue(fDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and trunc(CREATE_DATE) >= to_date('"+fStr+"','dd/mm/yyyy') ");
		}
		
		if( !Utils.isNull(o.getCreateDateTo()).equals("")){
			Date tDate  = DateUtil.parse(o.getCreateDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);

			sql.append("\n and trunc(CREATE_DATE) <= to_date('"+tStr+"','dd/mm/yyyy') ");
		}
		return sql;
	}
	
	public static MTTBean searchScanReport(MTTBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MTTBean h = null;
		List<MTTBean> items = new ArrayList<MTTBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			if("groupCode".equalsIgnoreCase(o.getDispType())){
				sql.append("\n  select S.doc_no,S.doc_date ,S.cust_group,S.cust_no,I.GROUP_CODE, I.PENS_ITEM " +
			               "\n ,count(*) as qty"+
						   "\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
						   "     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  "+
						   "\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
						   "     and M.reference_code = 'Customer' and M.pens_value = S.cust_group) as cust_group_name  "+
						   " from PENSBME_BARCODE_SCAN S ,PENSBME_BARCODE_SCAN_ITEM I");
			}else{
				sql.append("\n  select S.doc_no,S.doc_date ,S.cust_group,S.cust_no,I.GROUP_CODE, I.PENS_ITEM,MATERIAL_MASTER,BARCODE " +
			               "\n ,count(*) as qty"+
						   "\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
						   "     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  "+
						   "\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
						   "     and M.reference_code = 'Customer' and M.pens_value = S.cust_group) as cust_group_name  "+
						   " from PENSBME_BARCODE_SCAN S ,PENSBME_BARCODE_SCAN_ITEM I");
			}
			
			sql.append("\n where 1=1   \n");
			sql.append("\n and S.doc_no = I.doc_no \n");
			//sql.append("\n and status <> '"+STATUS_CANCEL+"' \n");
			
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and S.doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and S.cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and S.cust_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and I.group_code = '"+Utils.isNull(o.getGroupCode())+"'  ");
			}
			
			if( !Utils.isNull(o.getDocDate()).equals("")){
				Date fDate  = DateUtil.parse(o.getDocDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String fStr = DateUtil.stringValue(fDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and trunc(DOC_DATE) = to_date('"+fStr+"','dd/mm/yyyy') ");
			}
			
			if("groupCode".equalsIgnoreCase(o.getDispType())){
				sql.append("\n group by S.doc_no,S.doc_date,S.cust_group,S.cust_no,I.GROUP_CODE,I.PENS_ITEM");
				sql.append("\n order by S.doc_no,S.doc_date,S.cust_group,S.cust_no,I.GROUP_CODE,I.PENS_ITEM ");
			}else{
				sql.append("\n group by S.doc_no,S.doc_date,S.cust_group,S.cust_no,I.GROUP_CODE,I.PENS_ITEM ,I.PENS_ITEM,MATERIAL_MASTER,BARCODE");
				sql.append("\n order by S.doc_no,S.doc_date,S.cust_group,S.cust_no,I.GROUP_CODE,I.PENS_ITEM,I.PENS_ITEM,MATERIAL_MASTER,BARCODE ");
			}
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MTTBean();
			   h.setNo(r);
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setCustGroupName(rst.getString("cust_group_name"));
			   h.setDocDate(DateUtil.stringValue(rst.getTimestamp("doc_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStoreCode(Utils.isNull(rst.getString("cust_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
               h.setGroupCode(Utils.isNull(rst.getString("GROUP_CODE")));
               h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
               if("barcode".equalsIgnoreCase(o.getDispType())){
            	 h.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));  
            	 h.setBarcode(Utils.isNull(rst.getString("BARCODE")));  
               }
               h.setQty(rst.getInt("qty"));
             
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
	
	public static MTTBean searchItem(MTTBean o) throws Exception {
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
	
	public static MTTBean searchItemModel(Connection conn,MTTBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		
		MTTBean h = null;
		List<MTTBean> items = new ArrayList<MTTBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n  select line_id,doc_no,sale_date ,cust_group,cust_no,barcode," +
					"\n MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF" +
		            "\n ,status"+
					" from PENSBME_SALES_OUT ");
			
			sql.append("\n where 1=1   \n");
			sql.append("\n and status <> '"+STATUS_CANCEL+"' \n");
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			}

			sql.append("\n order by doc_no,sale_date,line_id ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MTTBean();
			   h.setNo(r);
			   h.setLineId(rst.getInt("line_id"));
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setSaleDate(DateUtil.stringValue(rst.getTimestamp("sale_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
	
	public static int getMaxLineIdFromMTTByDocNo(MTTBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
        int maxLineId= 0;
		try {
			sql.append("\n select max(line_id) as max_line_id from PENSBME_SALES_OUT ");
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
	
	public static MTTBean searchReport(MTTBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MTTBean h = null;
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
		  
			   h = new MTTBean();
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
	
	
	/**
	 * 
	 * @param conn
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static boolean  canCancelJob(Connection conn,Job o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean canCancelJob = true;
		try {

			sql.append("\n select count(*) as c from PENSBI.PENSBME_PICK_BARCODE i   \n");
			sql.append("\n where 1=1  and status <> '"+STATUS_CANCEL+"' \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  int c = rst.getInt("c");
			  if(c>0){
				  canCancelJob = false;
			  }
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return canCancelJob;
	}
	
	public static MTTBean save(MTTBean h) throws Exception{
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
					   MTTBean l = (MTTBean)h.getItems().get(i);
					   l.setDocNo(h.getDocNo());
					   l.setStatus(STATUS_NEW);
					   l.setSaleDate(h.getSaleDate());
					   l.setCustGroup(h.getCustGroup());
					   l.setStoreCode(h.getStoreCode());
					   l.setRemark(h.getRemark());
					   
				       saveModel(conn, l);
				   }
				}
			}else{
				
				//delete item by doc no
				//deleteModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   MTTBean l = (MTTBean)h.getItems().get(i);
					   l.setDocNo(h.getDocNo());
					   l.setSaleDate(h.getSaleDate());
					   l.setCustGroup(h.getCustGroup());
					   l.setStoreCode(h.getStoreCode());
					   l.setRemark(h.getRemark());
					   
					   int update = updateModel(conn, l);
					   if(update==0){
				         saveModel(conn, l);
					   }
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
			   int seq = SequenceProcess.getNextValue(conn,"SalesMTT","DOC_NO",date);
			   
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
		 
	 
	 public static void saveModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_SALES_OUT \n");
				sql.append(" (SALE_DATE,DOC_NO, LINE_ID,   \n");
				sql.append("  CUST_GROUP, CUST_NO, BARCODE,MATERIAL_MASTER, ");
				sql.append("  GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF,STATUS,CREATE_DATE,CREATE_USER,REMARK)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date saleDate = DateUtil.parse( o.getSaleDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				ps.setTimestamp(c++, new java.sql.Timestamp(saleDate.getTime()));
				ps.setString(c++, o.getDocNo());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getCustGroup());
				ps.setString(c++, o.getStoreCode());
				ps.setString(c++, o.getBarcode());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
				ps.setDouble(c++, Utils.convertStrToDouble(o.getRetailPriceBF()));
				ps.setString(c++, o.getStatus());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
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

		public static int updateModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_SALES_OUT SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ? ,REMARK =?   \n");
				sql.append(" WHERE DOC_NO = ? and LINE_ID = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, Utils.isNull(o.getRemark()));
				
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
		
		
		public static int updateMTTStatusByDocNo(Connection conn,MTTBean o) throws Exception{
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
