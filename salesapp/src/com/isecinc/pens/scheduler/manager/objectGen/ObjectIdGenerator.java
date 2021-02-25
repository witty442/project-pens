package com.isecinc.pens.scheduler.manager.objectGen;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;




/**
 * Generate ID for ... something
 * 
 * @author Witty
 *  
 */
public class ObjectIdGenerator {
   
//	private static Logger logger = Logger.getLogger(ObjectIdGenerator.class);
	private static ObjectIdGenerator instance = new ObjectIdGenerator();
	private Map objectIdCache;
	/** Logger */
	private Logger logger = Logger.getLogger("PENS");
	
	private ObjectIdGenerator() {
		initial();
	}

	/**
	 * Returns the instance of ObjectIdGenerator.
	 * 
	 * @return ObjectIdGenerator
	 *  
	 */
	public static ObjectIdGenerator getInstance() {
		return instance;
	}
	/**
	 * 
	 * @param table
	 * @param primaryField
	 * @return long
	 * @throws Exception
	 */
	public synchronized BigDecimal getNextSequenceId(Connection con) throws Exception {
		PreparedStatement ps = null;
        ResultSet rs = null;
    	BigDecimal id = new BigDecimal("0");
        try{
        	//logger.debug("conn:"+con);
        	
	        ps = con.prepareStatement("select max(no) as max_id from PENSONLINE.MONITOR_SCHEDULE "); 
	        rs = ps.executeQuery();
			if(rs.next()){
				BigDecimal bb = rs.getBigDecimal("max_id")==null?new BigDecimal("0"):rs.getBigDecimal("max_id");
				id = bb.add(new BigDecimal("1"));
			}
	        return id;
        }catch(Exception e){
            throw e;
        }finally{
        	if(ps != null){
        		ps.close();
        	}
        	if(rs != null){
        	//	rs.close();
        	}
        }
    
	}
	
	
	
	public void initial() {
		objectIdCache = Collections.synchronizedMap(new HashMap());
	}

// for Reloadable Interface (reload singleton object + clear cache)
	public void reload(Date timeStamp) {
		
	}
	
	
	
}