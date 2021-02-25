package com.isecinc.pens.interfaces.imports;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.pens.util.DBConnection;

public class ImportInvoiceManager {
	public static Logger logger = Logger.getLogger("PENS");
	

	public MonitorBean process(User user) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		boolean importAll = false;
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("Import Invoice");
			monitorModel.setType(BatchTaskConstants.TYPE_IMPORT);
			monitorModel.setStatus(BatchTaskConstants.STATUS_START);
			monitorModel.setCreateUser(user.getUserName());
			monitorModel.setTransactionType("TRANSACTION");
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
				

			logger.debug(" **********Start Import  Transaction Receipt All Sales  ******************");
			monitorModel = new ImportInvoiceProcess().importInvoice(monitorModel.getTransactionId(),monitorModel.getMonitorId(),monitorModel.getTransactionType(),user);
			logger.debug(" **********Result Import  Transaction Receipt All Sales :"+monitorModel.getStatus()+" ************");
		
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
