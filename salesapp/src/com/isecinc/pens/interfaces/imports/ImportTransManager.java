package com.isecinc.pens.interfaces.imports;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.pens.util.DBConnection;

public class ImportTransManager {
	public static Logger logger = Logger.getLogger("PENS");
	
	/** t_bill_plan **/
	/** t_adjust **/

	public MonitorBean process(User userLogin) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		boolean importAll = false;
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("Import Transaction");
			monitorModel.setType(BatchTaskConstants.TYPE_IMPORT);
			monitorModel.setStatus(BatchTaskConstants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType("UPDATE-TRANS-SALES");
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
				
			logger.debug(" **********Start Import  Transaction  All Sales  ******************");
			monitorModel = new ImportTransProcess().initImportFile(monitorModel.getTransactionId(),monitorModel.getMonitorId(),monitorModel.getTransactionType(), userLogin, importAll);
			logger.debug(" **********Result Import  Transaction  All Sales :"+monitorModel.getStatus()+" ******************");
		
			//Stamp Task to Success
			logger.info("startTaskStatus transactionId["+monitorModel.getTransactionId()+"]monitorId["+monitorModel.getMonitorId()+"]");
			dao.updateTaskStatus(monitorModel.getTransactionId(),monitorModel.getMonitorId(),"0"); //Start BatchTask = 0;
			
	
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
