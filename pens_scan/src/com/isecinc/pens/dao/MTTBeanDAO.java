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

import util.Constants;

import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class MTTBeanDAO{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	protected static SimpleDateFormat dfUs = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	
	
	public static MTTBean searchHead(MTTBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MTTBean h = null;
		List<MTTBean> items = new ArrayList<MTTBean>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n  select doc_no,doc_date ,cust_group,cust_no,status,export_flag" +
					   "\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
					   "\n     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  "+
					   "\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
					   "\n     and M.reference_code = 'Customer' and M.pens_value = S.cust_group) as cust_group_name  "+
					   "\n from PENSBME_BARCODE_SCAN S");
			
			sql.append("\n where 1=1   \n");
			//sql.append("\n and status <> '"+STATUS_CANCEL+"' \n");
			
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and cust_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'  ");
			}

			if( !Utils.isNull(o.getDocDate()).equals("")){
				Date fDate  = Utils.parse(o.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String fStr = Utils.stringValue(fDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and DOC_DATE = STR_TO_DATE('"+fStr+"','%d/%m/%Y') ");
			}

			sql.append("\n order by doc_no desc,doc_date desc ");
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
			   h.setDocDate(Utils.stringValue(rst.getTimestamp("doc_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStoreCode(Utils.isNull(rst.getString("cust_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
               h.setStatus(Utils.isNull(rst.getString("status")));
               h.setExport(Utils.isNull(rst.getString("export_flag")));
               
               
               if("AB".equalsIgnoreCase(h.getStatus())){
            	   h.setStatusDesc("CANCEL");
            	   h.setBarcodeStyle("disableTextCenter");
            	   h.setBarcodeReadonly(true);
   				   h.setCanCancel(false);
				   h.setCanEdit(false);
				   h.setStatusDesc("CANCEL");
               }else if("C".equalsIgnoreCase(h.getStatus())){
            	   h.setStatusDesc("CLOSE");
            	   h.setBarcodeStyle("disableTextCenter");
            	   h.setBarcodeReadonly(true);
            	   h.setBarcodeStyle("");  
            	   h.setCanCancel(false);
				   h.setCanEdit(false);
               }else{
            	   h.setStatusDesc("OPEN");
            	   h.setBarcodeStyle("normalTextCenter");  
            	   h.setCanCancel(true);
				   h.setCanEdit(true);
				   h.setCanClose(true);
               }
               
               if("Y".equalsIgnoreCase(h.getExport())){
            	   h.setExportDesc("ส่งข้อมูลแล้ว");
            	   h.setCanCancel(false);
				   h.setCanEdit(false);
				   h.setCanClose(false);
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
			sql.append("\n select line_id,barcode,MATERIAL_MASTER,GROUP_CODE,PENS_ITEM ,count(*) as qty" +
					" from PENSBME_BARCODE_SCAN_ITEM ");
			
			sql.append("\n where 1=1   \n");
			if( !Utils.isNull(o.getDocNo()).equals("")){
				sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			}
            sql.append("\n group by line_id,barcode,MATERIAL_MASTER,GROUP_CODE,PENS_ITEM");
			sql.append("\n order by doc_no,line_id ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MTTBean();
			   h.setNo(r);
			   h.setLineId(rst.getInt("line_id"));
               h.setBarcode(Utils.isNull(rst.getString("barcode")));
               h.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));
               h.setGroupCode(Utils.isNull(rst.getString("GROUP_CODE")));
               h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
               h.setQty(rst.getInt("qty"));
               
               totalQty += h.getQty();
               
               if(Constants.STATUS_CANCEL.equalsIgnoreCase(o.getStatus())){
            	   h.setBarcodeStyle("disableTextCenter");
            	   h.setBarcodeReadonly(true); 
               }else if(Constants.STATUS_CLOSE.equalsIgnoreCase(o.getStatus())){
            	   h.setBarcodeStyle("disableTextCenter");
            	   h.setBarcodeReadonly(true); 
               }else{
            	   h.setStatusDesc("OPEN");
            	   h.setBarcodeStyle("normalTextCenter");  
               }
               
               if("Y".equalsIgnoreCase(o.getExport())){
            	   h.setBarcodeStyle("disableTextCenter");
            	   h.setBarcodeReadonly(true); 
               }

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
		
			} catch (Exception e) {}
		}
		return o;
	}
	
	

	public static MTTBean save(MTTBean h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check documentNo
			if(Utils.isNull(h.getDocNo()).equals("")){
				//Gen DocNo
				h.setDocNo(genDocNo(conn,h.getStoreCode(),new Date()) );
				saveHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   MTTBean l = (MTTBean)h.getItems().get(i);
					   l.setDocNo(h.getDocNo());
					   l.setCreateUser(h.getCreateUser());
					   l.setUpdateUser(h.getUpdateUser());
				       saveLineModel(conn, l);
				   }
				}
			}else{
				
				//delete item by doc no
				deleteItemModel(conn, h);
				
				updateHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   MTTBean l = (MTTBean)h.getItems().get(i);
					   l.setDocNo(h.getDocNo());
					   l.setCreateUser(h.getCreateUser());
					   l.setUpdateUser(h.getUpdateUser());
					  // int update = updateLineModel(conn, l);
					  // if(update==0){
				         saveLineModel(conn, l);
					  // }
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
	
	// รหัสร้านค้า-yymm-xxx เช่น 020047-1-5803-001
	 public static String genDocNo(Connection conn,String storeCode,Date date) throws Exception{
       String docNo = "";
		   try{
			   
			   String today = dfUs.format(date);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"SCAN_DOC_NO",storeCode,date);
			   
			   docNo = storeCode+"-"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+"-"+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return docNo;
	}
		 

	 public static void saveHeadModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_BARCODE_SCAN \n");
				sql.append(" (DOC_DATE,DOC_NO, CUST_GROUP, CUST_NO, ");
				sql.append("  STATUS,EXPORT_FLAG,CREATE_DATE,CREATE_USER)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ? ,? , ?, ? ,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date saleDate = Utils.parse( o.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				ps.setTimestamp(c++, new java.sql.Timestamp(saleDate.getTime()));
				ps.setString(c++, o.getDocNo());
				ps.setString(c++, o.getCustGroup());
				ps.setString(c++, o.getStoreCode());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getExport());
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
	 
	 public static void saveLineModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_BARCODE_SCAN_ITEM \n");
				sql.append(" (DOC_NO, LINE_ID,BARCODE,MATERIAL_MASTER, ");
				sql.append("  GROUP_CODE,PENS_ITEM,CREATE_DATE,CREATE_USER)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				int c =1;

				ps.setString(c++, o.getDocNo());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getBarcode());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
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
	 
	
		public static int updateHeadModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_BARCODE_SCAN SET  \n");
				sql.append(" REMARK = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE DOC_NO = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getRemark());
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
		
		public static int updateHeadModelCaseCloseJob(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_BARCODE_SCAN SET  \n");
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
		
		public static int updateLineModelCaseCloseJob(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_BARCODE_SCAN_ITEM SET  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE DOC_NO = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
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
		
		

		public static int updateLineModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				
				sql.append(" UPDATE PENSBME_BARCODE_SCAN_ITEM SET  \n");
				sql.append(" BARCODE = ? ,MATERIAL_MASTER=?,GROUP_CODE=?,PENS_ITEM=? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE DOC_NO = ? and LINE_ID = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getBarcode());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
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

		public static void deleteItemModel(Connection conn,MTTBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE FROM PENSBME_BARCODE_SCAN_ITEM  \n");
				sql.append(" WHERE  \n" );
				sql.append(" DOC_NO =? \n" );

				ps = conn.prepareStatement(sql.toString());

				ps.setString(c++, o.getDocNo());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}

}
