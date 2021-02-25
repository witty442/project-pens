package com.isecinc.pens.interfaces.imports;

import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.seq.SequenceProcessAll;

/**
 * @author WITTY
 *
 */
public class ImportMasterManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	//public static LoggerUtils logger = new LoggerUtils("UpdateSalesManager");
	
	public static Object IMPORT_Q = new Object();
	public static String  PATH_CONTROL = "inf-config/table-mapping-transaction/";
	public static String  FILE_CONTROL_NAME = "control_import_receipt.csv";
	public static String  PATH_IMPORT = EnvProperties.getInstance().getProperty("path.transaction.sales.in");
	
    public static boolean debug = true;
    
    public static void main(String[] a){
		try{
			User user = new User();
			user.setId(100000047);
			user.setCode("S001");
			user.setName("XXXX");
			user.setUserName("S001");
			References ref = new References("ROLE","TT","CreditSales");
			user.setRole(ref);
			//process(user,2,"RS00163070002");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public static MonitorBean process(User user) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		boolean importAll = false;
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			BigDecimal transactionId = SequenceProcessAll.getIns().getNextValue("monitor.transaction_id");
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("Import Master Table");
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(user.getUserName());
			monitorModel.setTransactionType("MASTER");
			monitorModel.setTransactionId(transactionId);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			monitorModel = new ImportMasterProcess().importMasterFileToDB(monitorModel.getTransactionId(),monitorModel.getMonitorId(), monitorModel.getTransactionType(),user,importAll);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(connMonitor != null){
				connMonitor.close();connMonitor=null;
			}
		}
		return monitorModel;
	}
	
}
