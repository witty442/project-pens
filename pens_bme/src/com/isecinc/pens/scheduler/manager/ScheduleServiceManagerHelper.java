package com.isecinc.pens.scheduler.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Constants;
import com.pens.util.Utils;

public class ScheduleServiceManagerHelper {
	
	protected static Logger logger = Logger.getLogger("PENS");
	
	private void debug(ScheduleVO vo)throws Exception{
		logger.debug("********Start Debug ScheduleVO***************");
		logger.debug("vo.getJobId():"+vo.getJobId());
		logger.debug("vo.getNo():"+vo.getNo());
		logger.debug("vo.getProgramId() :"+vo.getProgramId());
		logger.debug("vo.getGroup() :"+vo.getGroupId());
		logger.debug("vo.getStatus() :"+vo.getStatus());
		logger.debug("vo.getType() :"+vo.getType());
		logger.debug("vo.getProgramName() :"+vo.getProgramName());
		logger.debug("vo.getCreateUser() :"+vo.getCreateUser());
		logger.debug("vo.getNextRunDate() :"+vo.getNextRunDate());
		logger.debug("vo.getLastRunDate() :"+vo.getLastRunDate());
		logger.debug("vo.getCrontriggerExp() :"+vo.getCrontriggerExp());
		//logger.debug(" :"+vo);
		logger.debug("********End Debug ScheduleVO***************");
	}
	 public   void createBatchTask(Connection conn,ScheduleVO param) throws Exception{
	    	PreparedStatement ps = null;
	    	int index =1;
	    	logger.debug("**Start CreateBatchTask**");
	    	try{
	    		debug(param);
	    		StringBuffer sql = new StringBuffer("");
	    		sql.append(" INSERT INTO MONITOR_SCHEDULE (\n");
	    		sql.append(" NO, PROGRAM_ID, GROUP_ID, \n"); //1-3
	    		sql.append(" STATUS,  TYPE, \n"); //4-5
	    		sql.append(" PROGRAM_NAME, CREATE_DATE, CREAT_USER, \n");//6-8
	    		sql.append(" JOB_ID,NEXT_RUN_DATE,CRONTRIGGER_EXP) \n");//9-11
	    		sql.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?) \n");
	    		ps = conn.prepareStatement(sql.toString());
	    		
	    		System.out.println("TypeSchedule:"+param.getType());
	    		String status = SchedulerConstant.STATUS_SCHEDULE;
	    		if(SchedulerConstant.SCHEDULE_TYPE_NOW.equals(param.getType())){
	    			status = SchedulerConstant.STATUS_RUNNING;
	    		}
	    		ps.setBigDecimal(index++, param.getNo());
	    		ps.setString(index++, param.getProgramId());
	    		ps.setString(index++, param.getGroupId());
	    		ps.setString(index++, status);
	    		ps.setString(index++, param.getType());
	    		ps.setString(index++, param.getProgramName());
	    		ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
	    		ps.setString(index++, param.getUserId());
	    		ps.setString(index++, param.getJobId());
	    		if(param.getNextRunDate() != null){
	    		   ps.setTimestamp(index++, new Timestamp(param.getNextRunDate().getTime()));
	    		}else{
	    		   ps.setTimestamp(index++, null);
	    		}
	    		ps.setString(index++, param.getCrontriggerExp());
	    		
	    		ps.execute();
	    		
	    	}catch(Exception e){
	    		throw e;
	    	}finally{
	    		if(ps != null){
	    		   ps.close();
	    		}
	    	}
	    }
		
	 public   void deleteBatchTask(Connection conn,ScheduleVO param) throws Exception{
	    	PreparedStatement ps = null;
	    	int index =1;
	    	logger.debug("**Delete CreateBatchTask**");
	    	try{
	    		debug(param);
	    		StringBuffer sql = new StringBuffer("");
	    		sql.append(" DELETE FROM MONITOR_SCHEDULE WHERE NO =? \n");
	    		ps = conn.prepareStatement(sql.toString());
	    		ps.setBigDecimal(index++, param.getNo());

	    		ps.execute();
	    		
	    	}catch(Exception e){
	    		throw e;
	    	}finally{
	    		if(ps != null){
	    		   ps.close();
	    		}
	    	}
	    }
	 
	 
	 
	 public  void updateBatchTaskCaseSchedule(Connection conn,ScheduleVO param) throws Exception {
	    	PreparedStatement ps = null;
	    	int index = 1;
	    	logger.debug("**Start updateBatchTaskCaseSchedule**");
	    	try{
	    		debug(param);
	    		StringBuffer sql =new StringBuffer("");
	    		sql.append(" UPDATE MONITOR_SCHEDULE  \n");
	    		sql.append(" SET STATUS = ? , \n");
	    		sql.append(" UPDATE_DATE = ?, UPDATE_USER = ? ,  \n");
	    		sql.append(" TYPE = ?  \n");
	    		if(param.getLastRunDate() != null){
		    	   sql.append(" ,LAST_RUN_DATE = ? \n");
		    	}
	    		if(param.getNextRunDate() != null){
	    		   sql.append(" ,NEXT_RUN_DATE = ? \n");
	    		}
	    		if( !Utils.isNull(param.getCrontriggerExp()).equals("")){
		    	   sql.append(" ,CRONTRIGGER_EXP = ? \n");
		    	}
	    		sql.append(" WHERE NO =?  \n");
	    		ps = conn.prepareStatement(sql.toString());
	    		
	    		ps.setString(index++, SchedulerConstant.STATUS_SCHEDULE); //status
	    		ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis())); //update_date
	    		ps.setString(index++, param.getUserId()); //update user
	    		ps.setString(index++, param.getType()); //type
	    		if(param.getLastRunDate() != null){ //lastRunDate
	    			ps.setTimestamp(index++, new Timestamp(param.getLastRunDate().getTime()));
		    	}
	    		if(param.getNextRunDate() != null){ //nextRunDate
	    			ps.setTimestamp(index++, new Timestamp(param.getNextRunDate().getTime()));
		    	}
	    		if( !Utils.isNull(param.getCrontriggerExp()).equals("")){
	    			ps.setString(index++, param.getCrontriggerExp()); //crontrigger_exp
	    		}
	    		ps.setBigDecimal(index++,param.getNo()); //no
	    		
	    		ps.execute();
	    		
	    	}catch(Exception e){
	    		throw e;
	    	}finally{
	    		if(ps != null){
	    		   ps.close();
	    		}
	    	}
	    }
	 
	 
	 public   void updateStatusBatchTask(Connection conn,ScheduleVO param) throws Exception {
	    	PreparedStatement ps = null;
	    	int index = 1;
	    	logger.debug("**Start updateBatchTask**");
	    	try{
	    		debug(param);
	    		//Case Week afer run finish update status to SCHEDULE 
	    		if(param.getType().equals(SchedulerConstant.SCHEDULE_TYPE_WEEKLY)){
	    		   param.setStatus(SchedulerConstant.STATUS_SCHEDULE);
	    		}
	    		
	    		StringBuffer sql =new StringBuffer("");
	    		sql.append(" UPDATE MONITOR_SCHEDULE  \n");
	    		sql.append(" SET STATUS = ? , \n");
	    		sql.append(" UPDATE_DATE = ?, UPDATE_USER =?,   \n");
	    		sql.append(" SIZE_OF_FILE = ? ,NO_OF_RECORD =? ,FILE_NAME =?, \n");
	    		sql.append(" BATCH_DATE = ? ,LAST_RUN_DATE =? ,NEXT_RUN_DATE =? ,  \n");
	    		sql.append(" SOURCE_PATH = ? ,DEST_PATH =? ,Message = ?  \n");
	    		sql.append(" WHERE NO =?  \n");
	    		ps = conn.prepareStatement(sql.toString());
	    		ps.setString(index++, param.getStatus());//status
	    		ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));//updateDate
	    		ps.setString(index++, param.getUserId()); //userId
	    		ps.setString(index++, param.getSizeOfFile()); //sizeOfFile
	    		ps.setBigDecimal(index++, new BigDecimal(Utils.isNullDefaultZero(param.getNoOfRecord()))); //noOfRecord
	    		ps.setString(index++, param.getFileName()); //folderName
	    		ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis())); //batchDate as CurrentDate
	    		if(param.getLastRunDate() != null){
	    		   ps.setTimestamp(index++, new Timestamp(param.getLastRunDate().getTime()));//lastRunDate
	    		}else{
	    		  ps.setTimestamp(index++, null);//lastRunDate	
	    		}
	    		if(param.getNextRunDate() != null){
	    		  ps.setTimestamp(index++, new Timestamp(param.getNextRunDate().getTime()));//nextRunDate
	    		}else{
	    		  ps.setTimestamp(index++, null);//nextRunDate
	    		}
	    		ps.setString(index++, Utils.isNull(param.getSourcePath())); //folderName
	    		ps.setString(index++, Utils.isNull(param.getDestPath())); //
	    		ps.setString(index++, Utils.isNull(param.getMessage())); //
	    		
	    		ps.setBigDecimal(index++,param.getNo()); //no
	    		ps.execute();
	    		
	    	}catch(Exception e){
	    		throw e;
	    	}finally{
	    		if(ps != null){
	    		   ps.close();
	    		}
	    	}
	    }
	 
	 
	 public  ScheduleVO findLastRunDateByProgramId(Connection conn,ScheduleVO param) throws Exception{
		 ResultSet rs2 = null;
		 PreparedStatement ps = null;
		 ScheduleVO vo = null;
		 logger.debug("**Start findLastRunDateByProgramId**");
    	 try{
            StringBuffer sql = new StringBuffer(" select no,update_date from MONITOR_SCHEDULE where program_id = ? \n");
            sql.append("and type not in('"+SchedulerConstant.SCHEDULE_TYPE_NOW+"','"+SchedulerConstant.SCHEDULE_TYPE_ONCE+"')");
            
            ps = conn.prepareStatement(sql.toString()); 
            ps.setString(1,param.getProgramId());
            rs2 = ps.executeQuery();
            if(rs2.next()){
               vo = new ScheduleVO();
               vo.setLastRunDate(rs2.getDate("update_date"));//last update_date
               vo.setNo(rs2.getBigDecimal("NO"));
            }
    		return vo;
    	 }catch(Exception e){
    		throw e;
    	 }finally{
    		if(ps != null){
    		   ps.close();
    		}
    		if(rs2 != null){
	    	   //rs2.close();
	    	}
    	}
	 }
	 public  ScheduleVO findBatchTaskByNo(Connection conn,ScheduleVO param) throws Exception{
		 ResultSet rs2 = null;
		 PreparedStatement ps = null;
		 ScheduleVO vo = null;
		 logger.debug("**Start findLastRunDateByProgramId**");
    	 try{
            StringBuffer sql = new StringBuffer(" select no,type,job_id,program_id,group_id from MONITOR_SCHEDULE where no = ? \n");
            ps = conn.prepareStatement(sql.toString()); 
            ps.setBigDecimal(1,param.getNo());
            rs2 = ps.executeQuery();
            if(rs2.next()){
               vo = new ScheduleVO();
               vo.setNo(rs2.getBigDecimal("NO"));
               vo.setJobId(rs2.getString("job_id"));
               vo.setGroupId(rs2.getString("group_id"));
               vo.setProgramId(rs2.getString("program_id"));
               vo.setType(rs2.getString("type"));
            }
    		return vo;
    	 }catch(Exception e){
    		throw e;
    	 }finally{
    		if(ps != null){
    		   ps.close();
    		}
    		if(rs2 != null){
	    	   //rs2.close();
	    	}
    	}
	 }
	 
}
