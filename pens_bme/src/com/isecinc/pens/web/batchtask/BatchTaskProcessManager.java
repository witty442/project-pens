package com.isecinc.pens.web.batchtask;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 * @author WITTY
 *
 */
public class BatchTaskProcessManager {
   
	public static Logger logger = Logger.getLogger("PENS");

	public MonitorBean createBatchTask(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
		    //start Thread
			new BatchTaskProcessWorker(monitorModel).start();
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
	    return monitorModel;
	}
}
