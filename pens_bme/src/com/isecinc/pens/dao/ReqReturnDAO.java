package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqReturnWacoal;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class ReqReturnDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	

	public static void main(String[] s){
		try{
			String today = df.format(new Date());
			   String[] d1 = today.split("/");
			   System.out.println("d1[0]:"+d1[0]);
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   System.out.println("curYear:"+curYear);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static int searchTotalRecHead(Connection conn,ReqReturnWacoal o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqReturnWacoal h = null;
		int totalRec = 1;
		try {
			sql.append(" select count(*) as c \n");
			sql.append(" from PENSBME_PICK_RETURN i \n");
			sql.append("\n where 1=1   ");
			sql.append(genWhereSqlSearchHead(o));
			sql.append("\n order by i.request_no desc ");
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
	public static List<ReqReturnWacoal> searchHead(Connection conn,ReqReturnWacoal o,boolean getItems ,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqReturnWacoal h = null;
		List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
		int r = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			
			sql.append(" select i.* \n");
			sql.append(" ,(select NVL(count(distinct B.box_no),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = i.request_no group by B.request_no) as total_box \n");
			sql.append(" ,(select NVL(count(*),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = i.request_no group by B.request_no) as total_qty \n");
			sql.append(" from PENSBME_PICK_RETURN i \n");
			sql.append("\n where 1=1   ");
			sql.append(genWhereSqlSearchHead(o));
			sql.append("\n order by i.request_no desc ");
			
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
			//calc No.
			r = Utils.calcStartNoInPage(currPage, pageSize);
			
			while(rst.next()) {
				   h = new ReqReturnWacoal();
				   h.setNo(r);
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)); 
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setRemark(Utils.isNull(rst.getString("request_remark"))); 
				  
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_CANCEL) 
					|| Utils.isNull(rst.getString("status")).equals(STATUS_USED) 
					|| Utils.isNull(rst.getString("status")).equals(STATUS_RETURN)){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
 
				//get Items
				if(getItems){
					h.setItems(searchItem(conn, h));
				}
				
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
	
	public static StringBuffer genWhereSqlSearchHead(ReqReturnWacoal o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getRequestDate()).equals("")){
			Date tDate  = Utils.parse(o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and i.REQUEST_DATE = to_date('"+tDateStr+"','dd/mm/yyyy')");
		}
		
		if( !Utils.isNull(o.getStatus()).equals("")){
			sql.append("\n and i.status = '"+Utils.isNull(o.getStatus())+"'");
		}
		if( !Utils.isNull(o.getRequestNo()).equals("")){
			sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
		}
		return sql;
	}
	
	public static List<ReqReturnWacoal> searchItem(Connection conn,ReqReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqReturnWacoal h = null;
		int r = 1;
		int c = 1;
        List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
		try {
			sql.append("select i.job_id,i.box_no \n");
			sql.append(",(select max(name) from PENSBME_PICK_JOB j where j.job_id= i.job_id) as job_name \n");  
			sql.append(",count(*) as qty   \n");
			sql.append(" from PENSBI.PENSBME_PICK_RETURN_I i \n");
			sql.append(" where 1=1   \n");
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append(" and i.request_no = '"+Utils.isNull(o.getRequestNo())+"' \n");
			}
			sql.append("group by i.job_id,i.box_no \n");
			sql.append("order by i.box_no \n");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqReturnWacoal();
			  
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(rst.getInt("qty"));
			   h.setSelected("true");
			   
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
	
	public static int getMaxQtyLimitReturn(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        int maxQtyLimitReturn = 0;
		try {
			sql.append("\n select con_value from PENSBI.PENSBME_C_CONTROL  ");
			sql.append("\n where 1=1 and con_type ='RETURN_WACOAL' and con_code='MAX_QTY_RETURN_LIMIT'  \n");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				maxQtyLimitReturn = Utils.convertStrToInt(rst.getString("con_value"));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return maxQtyLimitReturn;
	}
	
	
	public static List<ReqReturnWacoal> searchBarcoceItemW1() throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqReturnWacoal h = null;
		int r = 1;
        List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
        Connection conn = null;
		try {
           
			sql.append("\n select i.box_no,i.job_id,j.job_name  ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h, PENSBI.PENSBME_PICK_BARCODE_ITEM i , \n");
			sql.append("\n ( select distinct job_id,warehouse ,name as job_name from PENSBME_PICK_JOB ) j");
			sql.append("\n where 1=1  ");
			sql.append("\n and h.job_id = j.job_id ");
			sql.append("\n and h.job_id = i.job_id ");
			sql.append("\n and h.box_no = i.box_no ");
			sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
			sql.append("\n and h.warehouse ='W1' ");
			sql.append("\n and ( i.status = '"+JobDAO.STATUS_CLOSE+"' OR i.status ='' OR i.status is null)");
			sql.append("\n group by i.box_no ,i.job_id ,j.job_name ");
			sql.append("\n order by i.box_no asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqReturnWacoal();
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(rst.getInt("qty"));
			
			   items.add(h);
			   r++;
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
		return items;
	}
	
	
	public static ReqReturnWacoal save(ReqReturnWacoal h) throws Exception{
		Connection conn = null;
		Map<String,String> boxNoMapAll = new HashMap<String, String>();
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check requestNo
			if(Utils.isNull(h.getRequestNo()).equals("")){
				//Gen requestNo
				h.setRequestNo(genRequestNo(new Date()) );
				h.setStatus(STATUS_NEW);
				logger.debug("RequestNO:"+h.getRequestNo());
				
				//save Head req 
			    saveHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqReturnWacoal l = (ReqReturnWacoal)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   l.setCreateUser(h.getCreateUser());
					   
					   //save item req from barcode item status =Close(c)
					   insertPickStockItem(conn, l); 
					   
					   //for validate boxNo
					   boxNoMapAll.put(l.getBoxNo(),l.getBoxNo());
				   }
				}
			}else{
				
				//update Head req 
			    updateHeadModel(conn, h);
			    
				//delete return item and update barcode item status='CLOSE'
			    deleteReturnItemAndUpdateBarcodeToClose(conn,h);
				
				//save line req
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqReturnWacoal l = (ReqReturnWacoal)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   l.setCreateUser(h.getCreateUser());
					   
					  //save item req from barcode item status =Close(c)
					   insertPickStockItem(conn, l); 
					   
					   //for validate boxNo
					   boxNoMapAll.put(l.getBoxNo(),l.getBoxNo());
				       
				   }
				}
			}
			
			//Update Head status Barcode
			calcStatusHeadBarcodeByBoxNo(conn,boxNoMapAll,h.getCreateUser());
			
			conn.commit();
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return h;
	}
	
	public static Map<String,String> updateBarcodeToCloseFromReturnItem(Connection conn,ReqReturnWacoal h) throws Exception{
		logger.debug("--cancelReturnItemAndUpdateBarcodeToClose--");
		Map<String,String> boxNoMapAll = new HashMap<String, String>();
		int no = 1;
		try{
		    List<ReqReturnWacoal> pickStockItemByGroupList = searchReturnItemList(conn, h);
			if(pickStockItemByGroupList !=null && pickStockItemByGroupList.size() >0){
			   logger.debug("pickStockItemList:"+pickStockItemByGroupList.size());
				
			   for(int k=0;k<pickStockItemByGroupList.size();k++){
				   ReqReturnWacoal p = (ReqReturnWacoal)pickStockItemByGroupList.get(k);
				   p.setCreateUser(h.getCreateUser());
				   p.setUpdateUser(h.getUpdateUser());
				   
			       //update barcode item (status = C)
			       p.setBarcodeItemStatus(PickConstants.STATUS_CLOSE);
			       updateStatusBarcodeItemModel(conn, p);
			       
			       boxNoMapAll.put(p.getBoxNo(), p.getBoxNo());
				   no++;
			   }//for 2
			}
			return boxNoMapAll;
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void calcStatusHeadBarcodeByBoxNo(Connection conn,Map<String,String> boxNoMapAll,String userName){
		/** Check all item ==RETURN and Update Head == RETURN **/
		try{
			if( !boxNoMapAll.isEmpty()){
				logger.debug("Validate Barcode Item amd update barcode Head");
				
				Iterator<String> its = boxNoMapAll.keySet().iterator();
				String boxNo = "";
				while(its.hasNext()){
				   boxNo = its.next(); 
				   processUpdateStatusBarcodeHead(conn,boxNo,userName);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void processUpdateStatusBarcodeHead(Connection conn,String boxNo,String userName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String status1 = "";
		String status2 = "";
		int c = 0;
		try{
			//Check barcode item is isssued all 
			sql.append("select distinct status from PENSBME_PICK_BARCODE_ITEM where box_no ='"+boxNo+"' and status <> '"+PickConstants.STATUS_CANCEL+"'");
			logger.debug("sql:\n"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			
			while(rst.next()){
				if(c==0){
					status1 = rst.getString("status");
				}else{
					status2 = rst.getString("status");
				}
				c++;
			}
			logger.debug("status1["+status1+"]status2["+status2+"]");
			
			if("".equalsIgnoreCase(status2)){
				 if(status1.equals(PickConstants.STATUS_RETURN)){
					 //Set barcode status = ISSUE
				     
					  Barcode b = new Barcode();
				      b.setBoxNo(boxNo);
				      b.setStatus(PickConstants.STATUS_RETURN);
				      b.setUpdateUser(userName);
				      
				      //update barcode_head DB
				      updateBarcodeHeadStatusModelByBoxNo(conn, b);
				 }else if(status1.equals(PickConstants.STATUS_CLOSE)){
				    //Check barcode item is close all update barcode head to close
					 Barcode b = new Barcode();
				     b.setBoxNo(boxNo);
				     b.setStatus(PickConstants.STATUS_CLOSE);
				     b.setUpdateUser(userName);
				      
				     //update barcode_head DB
				     updateBarcodeHeadStatusModelByBoxNo(conn, b);
				 }  
			}else{
				//Found status item = C -->Update Head = C 
				if(  status1.equals(PickConstants.STATUS_CLOSE)  || status2.equals(PickConstants.STATUS_CLOSE)){
					 //update barcode_head to close 
					 Barcode b = new Barcode();
				     b.setBoxNo(boxNo);
				     b.setStatus(PickConstants.STATUS_CLOSE);
				     b.setUpdateUser(userName);
				     
					updateBarcodeHeadStatusModelByBoxNo(conn, b);
				}else if(  status1.equals(PickConstants.STATUS_RETURN)  || status2.equals(PickConstants.STATUS_RETURN)){
					//update barcode_head to Return 
					 Barcode b = new Barcode();
				     b.setBoxNo(boxNo);
				     b.setStatus(PickConstants.STATUS_RETURN);
				     b.setUpdateUser(userName);
				     
					updateBarcodeHeadStatusModelByBoxNo(conn, b);
				}
				
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				rst.close();
				ps.close();
				//psUpdate.close();
			} catch (Exception e) {}
		}
	}
	
	public static void updateBarcodeHeadStatusModelByBoxNo(Connection conn,Barcode o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
			sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
			sql.append(" WHERE BOX_NO = ? \n" );

			logger.debug("sql:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(c++, o.getStatus());
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getBoxNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	//1-delete pickStockLine 2, update status barcode item and head
		private static void deleteReturnItemAndUpdateBarcodeToClose(Connection conn,ReqReturnWacoal h) throws Exception{
			logger.debug("--deleteReturnItemAndUpdateBarcodeToClose--");
			int no = 1;
			try{
				
			    List<ReqReturnWacoal> pickStockItemByGroupList = searchReturnItemList(conn, h);
			    
				logger.debug("pickStockItemList:"+pickStockItemByGroupList.size());
				   
				  if(pickStockItemByGroupList !=null && pickStockItemByGroupList.size() >0){
					  
					   for(int k=0;k<pickStockItemByGroupList.size();k++){
						   ReqReturnWacoal p = (ReqReturnWacoal)pickStockItemByGroupList.get(k);
						   p.setCreateUser(h.getCreateUser());
						   p.setUpdateUser(h.getUpdateUser());
						   
						   //delete pick stock line by pk issue_req_no,box_no,line_id,pens_item,material_master
						   deleteReturnLineModelByPK(conn, p);
							
						   //delete Pick Stock Line
						   logger.debug("no["+(no)+"]update barcode item:"+p.getPensItem()+",boxNo["+p.getBoxNo()+"]lineId:"+p.getLineId());

					       //update barcode item (status = C)
					       p.setBarcodeItemStatus(PickConstants.STATUS_CLOSE);
					       updateStatusBarcodeItemModel(conn, p);
					       
						   no++;
					   }//for 2
				   }//if 
						  
				
			}catch(Exception e){
				throw e;
			}
		}
		
		public static void deleteReturnLineModelByPK(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("deletePickStockLineModelByPK");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE PENSBI.PENSBME_PICK_RETURN_I \n");
				sql.append(" WHERE REQUEST_NO ='"+o.getRequestNo()+"' \n" );
				sql.append(" and JOB_ID ="+o.getJobId()+" \n" );
				sql.append(" and BOX_NO ='"+o.getBoxNo()+"' \n" );
				sql.append(" and LINE_ID ="+o.getLineId()+" \n" );
				sql.append(" and MATERIAL_MASTER ='"+o.getMaterialMaster()+"' \n" );
				sql.append(" and GROUP_CODE='"+o.getGroupCode()+"' \n" );
				sql.append(" and PENS_ITEM='"+o.getPensItem()+"' \n" );
				
				logger.debug("sql:"+sql.toString());
				
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
		
		public static List<ReqReturnWacoal> searchReturnItemList(Connection conn,ReqReturnWacoal o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			ReqReturnWacoal h = null;
			int r = 1;
	        List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
			try {

				sql.append("\n select i.* from PENSBI.PENSBME_PICK_RETURN_I i   \n");
				sql.append("\n where 1=1   \n");
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
				
				//sql.append("\n order by line_id asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();

				while(rst.next()) {
				  
				   h = new ReqReturnWacoal();
				   h.setRequestNo(rst.getString("request_no"));
				   h.setLineId(rst.getInt("line_id"));//Line_id(Barcode by boxNo)
				   h.setJobId(rst.getString("job_id"));
				   h.setBoxNo(rst.getString("box_no"));
				   
				   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
				   h.setGroupCode(rst.getString("group_code"));
				   h.setPensItem(rst.getString("pens_item"));
				   
				   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
				   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
				   
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
		
	// 	RQYYMMXXX  ( เช่น RQ5703001 )  			
	 private static String genRequestNo(Date date) throws Exception{
       String docNo = "";
       Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection();
			   
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			  // System.out.println("d1[0]:"+d1[0]);
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"RETURN_WACOAL","REQUEST_NO",date);
			   
			   docNo = "RQ"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn != null){
				   conn.close();conn=null;
			   }
		   }
		  return docNo;
	}
		 
	 private static void saveHeadModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_RETURN \n");
				sql.append(" ( REQUEST_DATE,REQUEST_NO ,CREATE_DATE ,CREATE_USER,STATUS,REQUEST_REMARK) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				Date openDate = Utils.parse( o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getRequestNo());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getRemark());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
	 public static void updateHeadModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_RETURN SET  \n");
				sql.append(" request_remark =? ,request_date =? ,update_user =?,update_date =?  \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
				Date openDate = Utils.parse( o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setString(c++, o.getRemark());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setString(c++, Utils.isNull(o.getRequestNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 

		//get barcode item status=close ,insert pick stock item ,update barcode item =issue ,and get pick_ref_key
		private static Map<String ,String> insertPickStockItem(Connection conn,ReqReturnWacoal o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			ReqReturnWacoal h = null;
			int count = 1;
	        Map<String ,String> boxNoMap = new HashMap<String, String>();
			try {
				sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
				sql.append("\n where 1=1 \n");
				sql.append("\n and  i.status ='"+PickConstants.STATUS_CLOSE+"' \n");
				if( !Utils.isNull(o.getJobId()).equals("")){
					sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
				}
				if( !Utils.isNull(o.getBoxNo()).equals("")){
					sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
				}
				
				sql.append("\n order by line_id asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();

				while(rst.next()) {
					   h = new ReqReturnWacoal();
					   h.setRequestNo(o.getRequestNo());
					   h.setLineId(rst.getInt("line_id"));
					   h.setJobId(rst.getString("job_id"));
					   h.setBoxNo(rst.getString("box_no"));
					  
					   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
					   h.setGroupCode(rst.getString("group_code"));
					   h.setPensItem(rst.getString("pens_item"));
					   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
					   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
					   
					   h.setCreateUser(o.getCreateUser());
					   h.setUpdateUser(o.getUpdateUser());
					   
					   //insert return item
					   saveItemModel(conn, h);
					   
					   //Update barcode item status=Return
				      //sql.append(" WHERE  BOX_NO = ? and  material_master =? and group_code =? and pens_item = ? and job_id = ? and line_id =? \n" );
					   
					   h.setBarcodeItemStatus(PickConstants.STATUS_RETURN);
					   h.setUpdateUser(h.getUpdateUser());
				       updateStatusBarcodeItemModel(conn, h);
					   
					  // boxNoMap.put(h.getBoxNo(), h.getBoxNo());
					   count++;
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return boxNoMap;
		}
		
	 private static void saveItemModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_RETURN_I \n");
				sql.append(" (REQUEST_NO,job_id, LINE_ID,BOX_NO,PENS_ITEM,material_Master,group_code,WHOLE_PRICE_BF,RETAIL_PRICE_BF ,CREATE_DATE,CREATE_USER,STATUS)  \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,? , ?, ?, ?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());

				ps.setString(c++, o.getRequestNo());
				ps.setString(c++, o.getJobId());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getBoxNo());
				ps.setString(c++, o.getPensItem());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setDouble(c++, Utils.convertStrToDouble(o.getWholePriceBF()));
				ps.setDouble(c++, Utils.convertStrToDouble(o.getRetailPriceBF()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getStatus());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
	 public static void updateStatusBarcodeItemModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE  BOX_NO = ? and  material_master =? and group_code =? and pens_item = ? and job_id = ? and line_id =? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getBarcodeItemStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, Utils.isNull(o.getBoxNo()));
				ps.setString(c++, Utils.isNull(o.getMaterialMaster()));
				ps.setString(c++, Utils.isNull(o.getGroupCode()));
				ps.setString(c++, Utils.isNull(o.getPensItem()));
				ps.setString(c++, Utils.isNull(o.getJobId()));
				ps.setInt(c++, o.getLineId());
				
				int r = ps.executeUpdate();
				logger.debug("Update:"+r);
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	public static void updatePickReturnStatusModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_RETURN SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setString(c++, Utils.isNull(o.getRequestNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	
	public static void updatePickReturnStatusItemModel(Connection conn,ReqReturnWacoal o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_RETURN_I SET  \n");
			sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
			sql.append(" WHERE  REQUEST_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(c++, o.getStatus());
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			
			ps.setString(c++, Utils.isNull(o.getRequestNo()));
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
		
		public static void deleteItemModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE FROM PENSBI.PENSBME_PICK_RETURN_I  \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, Utils.isNull(o.getRequestNo()));
				
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
