package com.isecinc.pens.interfaces.imports;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.isecinc.pens.bean.Invoice;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.TransactionHelper;
import com.isecinc.pens.model.MInvoice;
import com.pens.rest.api.client.InvoiceAPIClient;
import com.pens.rest.api.mapping.InvoiceAPIBean;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;

public class ImportInvoiceProcess {

	public static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * importFile
	 * @param monitorId
	 * @param transType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean importInvoice(BigDecimal transactionId ,BigDecimal monitorId,String transType,User userLogin ) throws Exception{
		Connection connMonitor = null;
		Connection conn = null;
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		TransactionHelper helper = new TransactionHelper();
		int dataCount = 0;
		int successCount = 0;
		String errorMsg="";
		String errorCode = "";
		String invoiceIdArr = "";
		try{
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			
			logger.debug("importFile Type:"+transType);
			/** Initial Monitor ***/
			monitorModel.setTransactionId(transactionId);
			monitorModel.setMonitorId(monitorId);
			monitorModel.setTransactionType(transType);

			conn = DBConnection.getInstance().getConnection();
			
			//Call API Rest
			/****************Process*************************************************/
			// Get InvoiceList By API
			List<Invoice> invoiceList = new InvoiceAPIClient().getInvoiceListToSalesApp();
			
			if(invoiceList != null && invoiceList.size() >0){
				//insert invoice to DB
				boolean r = new MInvoice().insertInvoice(invoiceList);
				
				//put update export_to_sale =Y
				if(r){
					for(int i=0;i<invoiceList.size();i++){
						invoiceIdArr +=invoiceList.get(i).getInvoiceId()+",";
					}
					invoiceIdArr = invoiceIdArr.substring(0,invoiceIdArr.length()-1);
	
				    Invoice invResult = new InvoiceAPIClient().updateExportToSalesInvoice(invoiceIdArr);
				    logger.debug("invResult Message:"+invResult.getStatusMessage());
				}
			}else {
				errorCode = "DataNotFoundException";
			}
		
			/*************************************************************************/

			/** End process ***/
			logger.debug("Update Monitor to Success:"+taskStatusInt);
			monitorModel.setStatus(taskStatusInt);
			monitorModel.setFileCount(0);
			monitorModel.setTransactionType(transType);
			monitorModel.setErrorMsg("");
			monitorModel.setErrorCode(errorCode);
			
			dao.updateMonitor(connMonitor,monitorModel);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setFileCount(0);
			monitorModel.setTransactionType(transType);
			monitorModel.setErrorMsg(e.getMessage());
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			dao.updateMonitor(connMonitor,monitorModel);

			if(conn != null){
				logger.debug("Transaction Rolback");
				conn.rollback();
			}
		}finally{
			if(conn != null){
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
		return monitorModel;
	}
	

}
