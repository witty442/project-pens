package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.TransferBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.SequenceProcess;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;


public class MTransfer {

	private Logger logger = Logger.getLogger("PENS"); 
	public static String STATUS_SAVE = "SV";//Active
	public static String STATUS_VOID = "VO";//cancel
	
	public static String STATUS_EXPORTED = "Y";//Active
	public static String STATUS_NO_EXPORTED = "N";//cancel
	
    
	public TransferBean save(User user,TransferBean head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//delete line id (user check box delete)
			if( !Utils.isNull(head.getLineIdDelete()).equals("")){
			   boolean result = deleteTransfer(conn, head.getLineIdDelete());
			}
			
			if(head.getItems() != null && head.getItems().size() > 0){
				// Process normal
				for(int i =0;i< head.getItems().size();i++){
					TransferBean line = (TransferBean)head.getItems().get(i);
					//check row is can edit
					if(line.isCanEdit()){
						if(line.getLineId() ==0){
						   insertTransfer(conn,line);
						}else{
						   updateTransfer(conn,line);
						}
					}
				}//for
			}
			conn.commit();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			conn.rollback();
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return head;
	}
	
	private boolean deleteTransfer(Connection conn ,String lineIdDelete) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from t_bank_transfer where line_id in("+Utils.converToTextSqlIn(lineIdDelete)+") \n");
			logger.debug("SQL:"+sql);

			ps = conn.prepareStatement(sql.toString());
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("delete record:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	private TransferBean updateTransfer(Connection conn ,TransferBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			
			StringBuffer sql = new StringBuffer("update t_bank_transfer set \n");
			sql.append(" transfer_date =? , transfer_type =?, \n");
			sql.append(" transfer_bank =?, transfer_time=?, amount=?, \n");
			sql.append(" cheque_no=?, cheque_date=?,UPDATED =? ,UPDATED_BY =? \n");
			sql.append(" where create_date =? and line_id =? \n");

			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getTransferDate()).getTime()));
			ps.setString(++index, Utils.isNull(model.getTransferType()));
			ps.setString(++index, Utils.isNull(model.getTransferBank()));
			ps.setString(++index, Utils.isNull(model.getTransferTime()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getAmount()));
			ps.setString(++index, Utils.isNull(model.getChequeNo()));
			if( !Utils.isNull(model.getChequeDate()).equals("")){
			  ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getChequeDate()).getTime()));
			}else{
			  ps.setDate(++index, null);
			}
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, model.getUpdateBy());//9
			
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getCreateDate()).getTime()));
			ps.setInt(++index, model.getLineId());
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return model;
	}
	
	private TransferBean insertTransfer(Connection conn ,TransferBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("INSERT INTO t_bank_transfer( \n");
			sql.append(" line_id, transfer_date, transfer_type, \n");
			sql.append(" transfer_bank, transfer_time, amount, \n");
			sql.append(" cheque_no, cheque_date, create_date, \n");
			sql.append(" status,exported, USER_ID, CREATED,CREATED_BY) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			
			ps.setInt(++index, SequenceProcess.getNextValue("t_bank_transfer"));
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getTransferDate()).getTime()));
			ps.setString(++index, Utils.isNull(model.getTransferType()));
			ps.setString(++index, Utils.isNull(model.getTransferBank()));
			ps.setString(++index, Utils.isNull(model.getTransferTime()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getAmount()));
			ps.setString(++index, Utils.isNull(model.getChequeNo()));
			if( !Utils.isNull(model.getChequeDate()).equals("")){
			  ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getChequeDate()).getTime()));
			}else{
			  ps.setDate(++index,null);
			}
			ps.setDate(++index, new java.sql.Date(DateToolsUtil.convertToTimeStamp(model.getCreateDate()).getTime()));
			
			ps.setString(++index, STATUS_SAVE);//status
			ps.setString(++index, STATUS_NO_EXPORTED);//exported
			ps.setString(++index, model.getUserId());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, model.getCreatedBy());//14
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return model;
	}
	public int searchTotalRecTransferList(Connection conn,TransferBean mCriteria) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n  SELECT count(*) as c  ");
			sql.append("\n  from t_bank_transfer h ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
			if( !Utils.isNull(mCriteria.getCreateDate()).equals("")){
				sql.append(" and h.create_date = str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getCreateDate(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
			}
			if( !Utils.isNull(mCriteria.getTransferDateFrom()).equals("")
				&&	!Utils.isNull(mCriteria.getTransferDateTo()).equals("")	){
				  sql.append(" and h.transfer_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getTransferDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				  sql.append(" and h.transfer_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getTransferDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
			}else if( !Utils.isNull(mCriteria.getTransferDateFrom()).equals("")
					&&	Utils.isNull(mCriteria.getTransferDateTo()).equals("")	){
				 sql.append(" and h.transfer_date = str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getTransferDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
			}

			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()) {
				totalRec = rst.getInt("c");
			}//while
		
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return totalRec;
}

	 public List<TransferBean> searchTransferList(Connection conn ,TransferBean mCriteria
			 ,boolean allRec ,int currPage,int pageSize) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<TransferBean> list = new ArrayList<TransferBean>();
			StringBuilder sql = new StringBuilder();
			int start=0;int end =0;
			int no=0;
			try {
				//Calc start ,end for select sql
				currPage=currPage==0?1:currPage;
				start = (currPage-1)*pageSize;
				end =  (currPage*pageSize);
				
				//start no 
				no = Utils.calcStartNoInPage(currPage, pageSize);
				
				sql.append("\n select A.* from (");
				sql.append("\n  SELECT h.*   ");
				sql.append("\n  ,( SELECT r.name  from c_reference r");
				sql.append("\n     where R.CODE='"+InitialReferences.TRANSFER_BANK_VAN+"'" );
				sql.append("\n     and r.value = h.transfer_bank) as transfer_bank_label ");
				sql.append("\n  from t_bank_transfer h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
				if( !Utils.isNull(mCriteria.getCreateDate()).equals("")){
					sql.append(" and h.create_date = str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getCreateDate(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				if( !Utils.isNull(mCriteria.getTransferDateFrom()).equals("")
					&&	!Utils.isNull(mCriteria.getTransferDateTo()).equals("")	){
					  sql.append(" and h.transfer_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getTransferDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.transfer_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getTransferDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}else if( !Utils.isNull(mCriteria.getTransferDateFrom()).equals("")
						&&	Utils.isNull(mCriteria.getTransferDateTo()).equals("")	){
					 sql.append(" and h.transfer_date = str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getTransferDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  ORDER BY h.create_date desc \n");
			    sql.append("\n   )A ");
	         
			    if( !allRec){
				  sql.append("\n  limit "+start+","+end);
			    }
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				  TransferBean m = new TransferBean();
				  m.setNo(no+"");
				  m.setLineId(rst.getInt("line_id"));
				  m.setTransferType(Utils.isNull(rst.getString("transfer_type")));
				  if("CS".equalsIgnoreCase(m.getTransferType())){
					  m.setTransferTypeLabel("เงินสด");
				  }else{
					  m.setTransferTypeLabel("เช็ค"); 
				  }
				  m.setTransferBank(Utils.isNull(rst.getString("transfer_bank")));
				  m.setTransferBankLabel(Utils.isNull(rst.getString("transfer_bank_label")));
				  m.setTransferDate(Utils.stringValue(rst.getDate("transfer_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setTransferTime(Utils.isNull(rst.getString("transfer_time")));
				  
				  m.setAmount(Utils.decimalFormat(rst.getDouble("amount"),Utils.format_current_2_disgit));
				  m.setChequeNo(Utils.isNull(rst.getString("cheque_no")));
				  m.setChequeDate(Utils.stringValueNull(rst.getDate("cheque_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  m.setCreateDate(Utils.stringValue(rst.getDate("create_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  
				  m.setStatus(rst.getString("status"));
				  m.setStatusLabel(STATUS_VOID.equals(m.getStatus())?"ยกเลิก":"ใช้งาน");
				  m.setExported(rst.getString("exported"));
				  m.setExportedLabel(STATUS_EXPORTED.equals(m.getExported())?"ส่งข้อมูลแล้ว":"ยังไม่ส่งข้อมูล");
				  
				  m.setUserId(rst.getString("user_id")); 
				  m.setCreatedBy(rst.getString("created_by"));
				  m.setUpdateBy(rst.getString("updated_by"));
				 
				  //Check canEdit
				  if((STATUS_SAVE.equals(m.getStatus()) && STATUS_NO_EXPORTED.equals(m.getExported()) ) 
					){
					  m.setCanEdit(true);
				  }
				  no++;
				  list.add(m);
				}//while
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return list;
	}
	 public TransferBean searchSummaryTransfer(Connection conn ,TransferBean mCriteria) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			TransferBean m = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT sum(amount) as amount from t_bank_transfer h ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and  h.user_id ='"+mCriteria.getUserId()+"'");
				if( !Utils.isNull(mCriteria.getCreateDate()).equals("")){
					sql.append(" and h.create_date = str_to_date('"+Utils.format(Utils.parseToBudishDate(mCriteria.getCreateDate(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				  m = new TransferBean();
				  m.setAmount(Utils.decimalFormat(rst.getDouble("amount"),Utils.format_current_2_disgit));
				}//while
			
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return m;
	}
 
}
