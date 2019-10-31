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

import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.BahtText;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class PayDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 public static PayBean searchHead(Connection conn,PayBean o,boolean getTrans ,boolean allRec ,int currPage,int pageSize) throws Exception {
		 return searchHeadModel(conn, o,getTrans,allRec,currPage,pageSize);
		}
	   
	   public static PayBean searchHead(PayBean o ,boolean getTrans,boolean allRec ,int currPage,int pageSize) throws Exception {
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
	   
		public static int searchTotalHead(Connection conn,PayBean o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			int totalRec = 0;
			Date dateFrom=null;
			Date dateTo = null;
			try {
				sql.append("\n select count(*) as c from(");
				   sql.append("\n select h.* ");
				   sql.append("\n ,(select d.dept_name from doc_department d where d.dept_id =h.dept_id) as dept_name ");
				   sql.append("\n ,(select d.section_name from doc_section d where d.dept_id =h.dept_id and d.section_id = h.section_id) as section_name ");
				   sql.append("\n from doc_tran h where 1=1 ");
					if( !Utils.isNull(o.getCreateUser()).equals("")){
						sql.append("\n and h.create_user = '"+Utils.isNull(o.getCreateUser())+"'");
					}
					if( !Utils.isNull(o.getDocNo()).equals("")){
						sql.append("\n and h.doc_no = '"+Utils.isNull(o.getDocNo())+"'");
					}
					if( !Utils.isNull(o.getDateFrom()).equals("") && !Utils.isNull(o.getDateTo()).equals("")){
						sql.append("\n and h.doc_date >= ?");
						sql.append("\n and h.doc_date <= ?");
						
					   dateFrom  = DateUtil.parse(o.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					   dateTo  = DateUtil.parse(o.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}
					sql.append("\n order by h.doc_no desc ");
	            sql.append("\n   )A ");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				
				if( !Utils.isNull(o.getDateFrom()).equals("") && !Utils.isNull(o.getDateTo()).equals("")){
					ps.setDate(1, new java.sql.Date(dateFrom.getTime()));
					ps.setDate(2, new java.sql.Date(dateTo.getTime()));
				}
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
		
		public static PayBean searchHeadModel(Connection conn,PayBean o ,boolean getTrans,boolean allRec ,int currPage,int pageSize) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			PayBean h = null;
			List<PayBean> items = new ArrayList<PayBean>();
			int r = 1;
			Date dateFrom=null;
			Date dateTo = null;
			try {
				sql.append("\n select M.* from (");
				sql.append("\n select A.* ,rownum as r__ from (");
				   sql.append("\n select h.* ");
				   sql.append("\n ,(select d.dept_name from doc_department d where d.dept_id =h.dept_id) as dept_name ");
				   sql.append("\n ,(select d.section_name from doc_section d where d.dept_id =h.dept_id and d.section_id = h.section_id) as section_name ");
				   sql.append("\n from doc_tran h where 1=1 ");
					if( !Utils.isNull(o.getCreateUser()).equals("")){
						sql.append("\n and h.create_user = '"+Utils.isNull(o.getCreateUser())+"'");
					}
					if( !Utils.isNull(o.getDocNo()).equals("")){
						sql.append("\n and h.doc_no = '"+Utils.isNull(o.getDocNo())+"'");
					}
					if( !Utils.isNull(o.getDateFrom()).equals("") && !Utils.isNull(o.getDateTo()).equals("")){
						sql.append("\n and h.doc_date >= ?");
						sql.append("\n and h.doc_date <= ?");
						
					   dateFrom  = DateUtil.parse(o.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					   dateTo  = DateUtil.parse(o.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}
					sql.append("\n order by h.doc_no desc ");
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
				
				if( !Utils.isNull(o.getDateFrom()).equals("") && !Utils.isNull(o.getDateTo()).equals("")){
					ps.setDate(1, new java.sql.Date(dateFrom.getTime()));
					ps.setDate(2, new java.sql.Date(dateTo.getTime()));
				}
				
				rst = ps.executeQuery();
				while(rst.next()) {
				   h = new PayBean();
				   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
				   h.setDocDate(DateUtil.stringValue(rst.getDate("doc_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setPayToName(Utils.isNull(rst.getString("pay_to_name")));
				   h.setDeptId(Utils.isNull(rst.getString("dept_id")));
				   h.setDeptName(Utils.isNull(rst.getString("dept_name")));
				   h.setSectionId(Utils.isNull(rst.getString("section_id")));
				   h.setSectionName(Utils.isNull(rst.getString("section_name")));
				   h.setStatus(Utils.isNull(rst.getString("status")));
				   h.setPaymethod(Utils.isNull(rst.getString("pay_method")));
				   h.setDR_AC_NO(Utils.isNull(rst.getString("DR_AC_NO")));
				   h.setDR_DESC(Utils.isNull(rst.getString("DR_DESC")));
					if( Utils.isNull(h.getPaymethod()).equals("C")){
						h.setCashFlag("on");
					}
					
					if( Utils.isNull(h.getPaymethod()).equals("CH")){
						h.setChequeFlag("on");
					}
					
				   if(rst.getDouble("DR_AMOUNT") !=0.00){
				     h.setDR_AMOUNT(Utils.convertToCurrencyStr(Utils.isNull(rst.getString("DR_AMOUNT"))));
				   }
				   
				   if(rst.getDouble("DR_INPUT_TAX_AMOUNT") !=0.00){
				      h.setDR_INPUT_TAX_AMOUNT(Utils.convertToCurrencyStr(Utils.isNull(rst.getString("DR_INPUT_TAX_AMOUNT"))));
				   }
				   if(rst.getDouble("DR_TOTAL") !=0.00){
				      h.setDR_TOTAL(Utils.convertToCurrencyStr(Utils.isNull(rst.getString("DR_TOTAL"))));
				   }
				   
				   h.setCR_AC_NO(Utils.isNull(rst.getString("CR_AC_NO")));
				   h.setCR_DESC(Utils.isNull(rst.getString("CR_DESC")));
				   if(rst.getDouble("CR_AMOUNT") !=0.00){
				      h.setCR_AMOUNT(Utils.convertToCurrencyStr(Utils.isNull(rst.getString("CR_AMOUNT"))));
				   }
				   if(rst.getDouble("CR_ACC_WT_TAX_AMOUNT") !=0.00){
				      h.setCR_ACC_WT_TAX_AMOUNT(Utils.convertToCurrencyStr(Utils.isNull(rst.getString("CR_ACC_WT_TAX_AMOUNT"))));
				   }
				   if(rst.getDouble("CR_TOTAL") !=0.00){
				      h.setCR_TOTAL(Utils.convertToCurrencyStr(Utils.isNull(rst.getString("CR_TOTAL"))));
				   }
				   h.setCanPrint(true);
				  
	               //get Trans head
	               if(getTrans){
	            	   PayBean itemBean = searchTranDetailList(conn, h);
	       
	            	   h.setTotalAmount(Utils.convertToCurrencyStr(itemBean.getTotalAmountDouble()));
					   h.setTotalAmountLetter(new BahtText(itemBean.getTotalAmountDouble()).toString());
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
	public static PayBean searchTranDetailList(Connection conn,PayBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<PayBean> items = new ArrayList<PayBean>();
			double totalAmount = 0;
			PayBean r = new PayBean();
			try {
			   sql.append("\n select * from doc_tran_detail");
			   sql.append("\n where 1=1 ");

			   if( !Utils.isNull(o.getDocNo()).equals("")){
				 sql.append("\n and doc_no = '"+Utils.isNull(o.getDocNo())+"'");
			   }
				
				sql.append("\n order by line_id asc ");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				while(rst.next()) {
				  PayBean h = new PayBean();
				   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
				   h.setLineId(rst.getInt("line_id"));
				   h.setAccountName(Utils.isNull(rst.getString("account_name")));
				   h.setDescription(Utils.isNull(rst.getString("description")));
				   
				   if(rst.getDouble("amount") !=0.00){
				      h.setAmount(Utils.convertToCurrencyStr(rst.getString("amount")));
				   }else{
					  h.setAmount("");
				   }
				   items.add(h);
				   
				   totalAmount = totalAmount + rst.getDouble("amount");
				}//while
				r.setItems(items);
				r.setTotalAmountDouble(totalAmount);
				
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
		
	public static void insertDocTran(Connection conn,PayBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.DOC_TRAN \n");
			sql.append(" (DOC_NO, DOC_DATE, PAY_TO_NAME, DEPT_ID, SECTION_ID," +
					" PAY_METHOD, STATUS, CREATE_DATE, CREATE_USER" +
					" ,DR_AC_NO,DR_DESC,DR_AMOUNT,DR_INPUT_TAX_AMOUNT,DR_TOTAL" +
					" ,CR_AC_NO,CR_DESC,CR_AMOUNT,CR_ACC_WT_TAX_AMOUNT,CR_TOTAL) \n");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ? ,?,?,?,?,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			Date docDate = DateUtil.parse(o.getDocDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			int c =1;
			ps.setString(c++, o.getDocNo());
			ps.setTimestamp(c++, new java.sql.Timestamp(docDate.getTime()));
			
			ps.setString(c++, o.getPayToName());
			ps.setString(c++, o.getDeptId());
			ps.setString(c++, o.getSectionId());
			ps.setString(c++, o.getPaymethod());
			ps.setString(c++, Utils.isNull(o.getStatus()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			
			ps.setString(c++, Utils.isNull(o.getDR_AC_NO()));
			ps.setString(c++, Utils.isNull(o.getDR_DESC()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getDR_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getDR_INPUT_TAX_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getDR_TOTAL()));
			
			ps.setString(c++, Utils.isNull(o.getCR_AC_NO()));
			ps.setString(c++, Utils.isNull(o.getCR_DESC()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getCR_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getCR_ACC_WT_TAX_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getCR_TOTAL()));
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	 
 public static void insertDocTranDetail(Connection conn,PayBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.DOC_TRAN_DETAIL \n");
			sql.append(" (DOC_NO, LINE_ID, ACCOUNT_NAME, DESCRIPTION, AMOUNT, CREATE_DATE, CREATE_USER) \n");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ? ,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			ps.setString(c++, o.getDocNo());
			ps.setInt(c++, o.getLineId());
			ps.setString(c++, o.getAccountName());
			ps.setString(c++, o.getDescription());
			ps.setDouble(c++, Utils.convertStrToDouble(o.getAmount()));
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
	
	public static int updateDocTran(Connection conn,PayBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.DOC_TRAN SET DOC_DATE =? ,Pay_to_name = ? ,DEPT_ID=?,SECTION_ID =?,PAY_METHOD =?,  \n");
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?  " );
			sql.append(" ,DR_AC_NO = ? ,DR_DESC = ? ,DR_AMOUNT = ? ,DR_INPUT_TAX_AMOUNT = ? ,DR_TOTAL = ?  \n" );
			sql.append(" ,CR_AC_NO = ? ,CR_DESC = ? ,CR_AMOUNT = ? ,CR_ACC_WT_TAX_AMOUNT = ? ,CR_TOTAL = ?  \n");
			
			sql.append(" WHERE DOC_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
            Date docDate = DateUtil.parse(o.getDocDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

			ps.setTimestamp(c++, new java.sql.Timestamp(docDate.getTime()));
			
			ps.setString(c++, Utils.isNull(o.getPayToName()));
			ps.setString(c++, Utils.isNull(o.getDeptId()));
			ps.setString(c++, Utils.isNull(o.getSectionId()));
			ps.setString(c++, Utils.isNull(o.getPaymethod()));
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));

			ps.setString(c++, Utils.isNull(o.getDR_AC_NO()));
			ps.setString(c++, Utils.isNull(o.getDR_DESC()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getDR_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getDR_INPUT_TAX_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getDR_TOTAL()));
			
			ps.setString(c++, Utils.isNull(o.getCR_AC_NO()));
			ps.setString(c++, Utils.isNull(o.getCR_DESC()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getCR_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getCR_ACC_WT_TAX_AMOUNT()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getCR_TOTAL()));
			
			ps.setString(c++, Utils.isNull(o.getDocNo()));

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

	public static int updateDocTranDetail(Connection conn,PayBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE DOC_TRAN_DETAIL SET Account_name = ? ,Description=?, Amount=? \n");
			sql.append(" ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
			sql.append(" WHERE DOC_NO = ? and LINE_ID =?  \n" );
			logger.debug("sql:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(c++, Utils.isNull(o.getAccountName()));
			ps.setString(c++, Utils.isNull(o.getDescription()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getAmount()));
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));

			ps.setString(c++, Utils.isNull(o.getDocNo()));
			ps.setInt(c++, o.getLineId());

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
	
	 public static List<PopupForm> searchDeptList(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from DOC_DEPARTMENT M");
				
				sql.append("\n  where 1=1 ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and dept_id ="+c.getCodeSearch()+" \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and dept_name = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and dept_id LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and dept_name LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				sql.append("\n  ORDER BY dept_id asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("dept_id")));
					item.setDesc(Utils.isNull(rst.getString("dept_name")));
					pos.add(item);
					
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
			return pos;
		}
	 
	 public static List<PopupForm> searchSectionList(String deptId,String sectionId,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from DOC_SECTION M");
				
				sql.append("\n  where 1=1 ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(deptId).equals("")){
						sql.append(" and dept_id ="+deptId+" \n");
					}
					if( !Utils.isNull(sectionId).equals("")){
						sql.append(" and section_id ="+sectionId+" \n");
					}
					
				}else{
					
				}
				sql.append("\n  ORDER BY section_id asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("section_id")));
					item.setDesc(Utils.isNull(rst.getString("section_name")));
					pos.add(item);
					
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
			return pos;
		}
	 
	 //	yyyymmrrrr  เช่น 2558020001 เป็นต้น
	 public static String genDocNo(Date date) throws Exception{
	       String docNo = "";
	       Connection conn = null;
	       SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
		   try{
			   conn = DBConnection.getInstance().getConnection(); 
			   
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"DOC_TRAN");
			   
			   docNo = new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		  
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
		  return docNo;
	}
}
