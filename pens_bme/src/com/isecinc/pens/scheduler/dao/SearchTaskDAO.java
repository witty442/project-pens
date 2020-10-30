package com.isecinc.pens.scheduler.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.scheduler.bean.BatchTaskDTO;
import com.isecinc.pens.scheduler.bean.TaskConditionDTO;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class SearchTaskDAO {

	protected static Logger logger = Logger.getLogger("PENS");
    
	 public static ArrayList<References> findJobInitail(String rerun)throws Exception{
	        Connection con = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        ArrayList<References> dataList  = new ArrayList<References>();
	        References ref = null;
	        try{            
	        	  con = DBConnection.getInstance().getConnection();
	        	  String sql ="select program_id ,program_name,type,rerun,visible from scheduler_job_initial \n";
	        	         sql +=" where visible = 'TRUE' \n";
	        	  if(!"".equals(rerun)){
	        		  sql +=" where visible = '"+rerun+"' \n";
	        	  }
	              pstmt = con.prepareStatement(sql);
	              rs = pstmt.executeQuery();
	              while (rs.next()){
	            	  ref = new References(rs.getString("program_id")+","+rs.getString("program_name"),rs.getString("program_id")+"_"+rs.getString("program_name"));
	            	  dataList.add(ref);
	              }
	              
	              return dataList;
	        }catch(Exception e){
	        	throw e;
	        }finally{
	        	if(rs != null){
	                rs.close();
	            }
	            if(pstmt != null){
	                pstmt.close();
	            }
	            if(con != null){
	                con.close();
	            }
	        }
	}
	 public static ScheduleVO getJobInit(String programId)throws Exception{
	        Connection con = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        ScheduleVO vo = new ScheduleVO();
	        try{            
	        	  con = DBConnection.getInstance().getConnection();
	        	  String sql ="select program_id ,program_name,type,rerun,visible ,param from scheduler_job_initial \n";
	        	         sql +=" where program_id = '"+programId+"' \n";
	              logger.debug("sql:"+sql.toString());
	              
	              pstmt = con.prepareStatement(sql);
	              rs = pstmt.executeQuery();
	              while (rs.next()){
	            	 vo.setProgramId(rs.getString("program_id"));
	            	 vo.setProgramName(rs.getString("program_name"));
	            	 vo.setParamRegen(rs.getString("param"));
	              }
	              
	              return vo;
	        }catch(Exception e){
	        	throw e;
	        }finally{
	        	if(rs != null){
	                rs.close();
	            }
	            if(pstmt != null){
	                pstmt.close();
	            }
	            if(con != null){
	                con.close();
	            }
	        }
	}
	 
    public static ArrayList findTaskByCondition(TaskConditionDTO dto,Connection con)throws Exception{
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{            
            boolean isDebugEnabled = logger.isDebugEnabled();  
            boolean hasId = false;
            boolean hasTaskName = false;
            boolean hasCreateDateFrom = false;
            boolean hasCreateDateTo = false;
            boolean hasAsOfDateFrom = false;
            boolean hasAsOfDateTo = false;
            boolean hasStatus = false;
            boolean hasType = false;
            boolean hasBatchDateFrom = false;
            boolean hasBatchDateTo = false;
            boolean hasEntity = false;
            boolean hasProduct = false;
            StringBuffer sql = new StringBuffer();
            
            String limitRowStr = "500";//EnvSchedulerProperties.getInstance().getProperty("row.per.page");
            int limitRow = 500; // Default
            try{
            	limitRow = Integer.parseInt(limitRowStr);
            }catch(Exception e){
            }
            
            if(logger.isDebugEnabled()){
//            	logger.debug("limitPage : "+limitPage);
            }
            
            sql.append("SELECT ( SELECT PARAM from scheduler_job_initial J where j.program_id = M.program_id) as PARAM_REGEN \n");
            sql.append(" ,M.* ");
            sql.append("FROM MONITOR_SCHEDULE M \n");
            
            /* Make serach condition */
            if(dto.getNo() != null){
                sql.append("WHERE NO = ? \n");
                hasId = true;
            }else{
            	sql.append("WHERE 1=1 \n");
                if(dto.getProgramName() != null && !"".equals(dto.getProgramName())){
                	hasTaskName = true;
                	sql.append("AND PROGRAM_NAME LIKE ?||'%' \n");
                }
                
                // Create Date                               
                if(dto.getCreateDateFrom() != null && dto.getCreateDateTo() == null){
                    hasCreateDateFrom = true;   
                    
					SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy HHmm");                    
                	String fromDateString = sd.format(dto.getCreateDateFrom());
					StringBuffer fromTSSql = new StringBuffer("TO_TIMESTAMP('");
					fromTSSql.append(fromDateString).append("','DDMMYYYY HH24MI')");
                	                	
                    sql.append("AND CREATE_DATE >= ");
                    sql.append(fromTSSql.toString()).append(" \n");;
                   
                }else if(dto.getCreateDateFrom() == null && dto.getCreateDateTo() != null){
                    hasCreateDateTo = true;
                                        
                    SimpleDateFormat sd1 = new SimpleDateFormat("HHmm");
					String hhmm = sd1.format(dto.getCreateDateTo());
					Timestamp dateTo = dto.getCreateDateTo();
					if("0000".equals(hhmm)){
						// Add one day
						dateTo = new Timestamp(dateTo.getTime()+(24*60*60*1000));						
					}else{
						// Add one minute
						dateTo = new Timestamp(dateTo.getTime()+(60*1000));
					}
					
					sql.append("AND CREATE_DATE < ");
					SimpleDateFormat sd2 = new SimpleDateFormat("ddMMyyyy HHmm");                    
                	String toDateString = sd2.format(dateTo);
					StringBuffer toTSSql = new StringBuffer("TO_TIMESTAMP('");
					toTSSql.append(toDateString).append("','DDMMYYYY HH24MI')");			
                    
                    sql.append(toTSSql.toString()).append(" \n");
                }else if(dto.getCreateDateFrom() != null && dto.getCreateDateTo() != null){
                    SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy HHmm");                    
                	String fromDateString = sd.format(dto.getCreateDateFrom());
					StringBuffer fromTSSql = new StringBuffer("TO_TIMESTAMP('");
					fromTSSql.append(fromDateString).append("','DDMMYYYY HH24MI')");
                	                	
                    sql.append("AND CREATE_DATE >= ");
                    sql.append(fromTSSql.toString()).append("\n");
                    
                    
                    SimpleDateFormat sd1 = new SimpleDateFormat("HHmm");
					String hhmm = sd1.format(dto.getCreateDateTo());
					Timestamp dateTo = dto.getCreateDateTo();
					if("0000".equals(hhmm)){						
						dateTo = new Timestamp(dateTo.getTime()+(24*60*60*1000));						
					}else{
						// add 1 sec
						dateTo = new Timestamp(dateTo.getTime()+(60*1000));						
					}
					sql.append("AND CREATE_DATE < ");
					SimpleDateFormat sd2 = new SimpleDateFormat("ddMMyyyy HHmm");                    
                	String toDateString = sd2.format(dateTo);
					StringBuffer toTSSql = new StringBuffer("TO_TIMESTAMP('");
					toTSSql.append(toDateString).append("','DDMMYYYY HH24MI')");                                        
					sql.append(toTSSql.toString()).append(" \n");
                }
                
               
                
                 // AsOfDate                               
                /*if(dto.getAsOfDateFrom() != null && dto.getAsOfDateTo() == null){                    
                    hasAsOfDateFrom = true;
                    sql.append("AND AS_OF_DATE >= ? \n");                    
                }else if(dto.getAsOfDateFrom() == null && dto.getAsOfDateTo() != null){
                    hasAsOfDateTo = true;
                    sql.append("AND AS_OF_DATE <= ? \n");
                }else if(dto.getAsOfDateFrom() != null && dto.getAsOfDateTo() != null){
                    sql.append("AND AS_OF_DATE >= ? \nAND AS_OF_DATE <= ? \n");                    
                    hasAsOfDateFrom = true;
                    hasAsOfDateTo = true;
                }*/
                
                // BatchDate                               
                if(dto.getBatchDateFrom() != null && dto.getBatchDateTo() == null){                    
                    hasBatchDateFrom = true;
                    sql.append("AND BATCH_DATE >= ? \n");                    
                }else if(dto.getBatchDateFrom() == null && dto.getBatchDateTo() != null){
                    hasBatchDateTo = true;
                    sql.append("AND BATCH_DATE <= ? \n");
                }else if(dto.getBatchDateFrom() != null && dto.getBatchDateTo() != null){
                    sql.append("AND BATCH_DATE >= ? \n AND BATCH_DATE <= ? \n");                    
                    hasBatchDateFrom = true;
                    hasBatchDateTo = true;
                }
               
                
                // Status                
                if(dto.getStatus() != null && dto.getStatus().length >0){
                    hasStatus = true;
                    sql.append("AND STATUS IN(");
                    for(int i=0; i<dto.getStatus().length; i++){
                        sql.append("?");
                        if(i != dto.getStatus().length-1){
                            sql.append(",");
                        }
                    }
                    sql.append(") \n");
                }
                
                // Type                
                if(dto.getType() != null && dto.getType().length >0){
                	hasType = true;
                    sql.append("AND TYPE IN(");
                    for(int i=0; i<dto.getType().length; i++){
                        sql.append("?");
                        if(i != dto.getType().length-1){
                            sql.append(",");
                        }
                    }
                    sql.append(") \n");
                }
                  
              /*  if(!"0".equals(Utils.isNull(dto.getEntityId())) && !"".equals(Utils.isNull(dto.getEntityId()))){
                	 hasEntity = true;
                	 sql.append(" AND ENTITY_ID = ? \n");
                }
                
                if(!"0".equals(Utils.isNull(dto.getProduct())) && !"".equals(Utils.isNull(dto.getProduct()))){  
                	 hasProduct = true;
               	     sql.append(" AND PRODUCT = ? \n");
                 }*/
                
                if(!"".equals(Utils.isNull(dto.getProgramId()))){
                	sql.append(" AND PROGRAM_ID = '"+dto.getProgramId()+"' \n");
                }
                sql.append("ORDER BY NO  \n");                
            }
           
            if(isDebugEnabled){
                System.out.println("\nSQL : "+sql.toString());
            }
            
    
           
            pstmt = con.prepareStatement(sql.toString());
            
            /* Put parameter */            
            int paramIndex = 1;
            if(hasId){
                pstmt.setBigDecimal(paramIndex++,dto.getNo());
            }
            if(hasTaskName){
                pstmt.setString(paramIndex++,dto.getProgramName());
            }
            if(hasAsOfDateFrom){
                pstmt.setDate(paramIndex++,new java.sql.Date(dto.getAsOfDateFrom().getTime()));
            }
            if(hasAsOfDateTo){
                pstmt.setDate(paramIndex++,new java.sql.Date(dto.getAsOfDateTo().getTime()));
            }
            if(hasBatchDateFrom){
                pstmt.setDate(paramIndex++,new java.sql.Date(dto.getBatchDateFrom().getTime()));
            }
            if(hasBatchDateTo){
                pstmt.setDate(paramIndex++,new java.sql.Date(dto.getBatchDateTo().getTime()));
            }
            
            if(hasStatus){
                for(int i=0; i<dto.getStatus().length; i++){
                    pstmt.setString(paramIndex++,dto.getStatus()[i]);
                }
            }
            if(hasType){
                for(int i=0; i<dto.getType().length; i++){
                    pstmt.setString(paramIndex++,dto.getType()[i]);
                }
            }
           
            if(hasEntity){
            	pstmt.setString(paramIndex++,dto.getEntityId());
            }
            if(hasProduct){
            	pstmt.setString(paramIndex++,dto.getProduct());
            }
            
            rs = pstmt.executeQuery();
            ArrayList resultList = new ArrayList();
            
            int resultCount = 0;
            while(rs.next()){
            	BatchTaskDTO batchTaskDTO = convertToBatchTaskDTO(rs);                
            	resultCount++;
            	batchTaskDTO.setId(new BigDecimal(resultCount));
            	resultList.add(batchTaskDTO);
            // if over limit => more
            	if(resultCount>=limitRow)
               		break;
            }
            
            dto.setLimitRow(limitRow);
            dto.setTotalRow(resultCount);
            
            return resultList;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }finally{
            if(rs != null){
                rs.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            
        }
    }
    
    private static BatchTaskDTO convertToBatchTaskDTO(ResultSet rs)throws Exception{
        BatchTaskDTO dto = new BatchTaskDTO();
        dto.setNo(rs.getBigDecimal("NO"));
        dto.setName(rs.getString("PROGRAM_NAME"));
        dto.setCreateDate(rs.getTimestamp("CREATE_DATE")!=null?new Date(rs.getTimestamp("CREATE_DATE").getTime()) :null);
        dto.setStatus(rs.getString("STATUS"));
        dto.setType(rs.getString("TYPE"));
        dto.setProgramId(rs.getString("PROGRAM_ID"));
        dto.setUpdateDate(rs.getDate("UPDATE_DATE"));

        dto.setNoOfRecord(rs.getString("NO_OF_RECORD"));
        dto.setSizeOfFile(rs.getString("SIZE_OF_FILE"));
        dto.setFileName(rs.getString("FILE_NAME"));
        dto.setSourcePath(rs.getString("SOURCE_PATH"));
        dto.setDestPath(rs.getString("DEST_PATH"));
        dto.setMessage(Utils.isNull(rs.getString("MESSAGE")));
        
        dto.setBatchDateTime(DateUtil.stringValue(rs.getTimestamp("batch_date"),"dd/MM/yyyy HH:mm:ss" ,Utils.local_th));
        dto.setLastRunDate(DateUtil.stringValue(rs.getTimestamp("LAST_RUN_DATE"),"dd/MM/yyyy HH:mm:ss",Utils.local_th));
        dto.setNextRunDate(DateUtil.stringValue(rs.getTimestamp("NEXT_RUN_DATE"),"dd/MM/yyyy HH:mm:ss",Utils.local_th));
        
        dto.setParamRegen(rs.getString("PARAM_REGEN"));
        
        System.out.println("NextDate:"+rs.getTimestamp("NEXT_RUN_DATE"));
        System.out.println("NextRunDate:"+dto.getNextRunDate());
        return dto;
        
    }
}
