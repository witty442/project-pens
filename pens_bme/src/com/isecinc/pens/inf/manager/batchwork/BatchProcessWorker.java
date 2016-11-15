package com.isecinc.pens.inf.manager.batchwork;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.ProcessManager;
import com.isecinc.pens.inf.manager.process.ExternalProcess;

/**
 * @author WITTY
 *
 */
public class BatchProcessWorker extends BatchWorker {
	public static Logger logger = Logger.getLogger("PENS");
	private BigDecimal reGenTransactionId;
    private MonitorBean monitorModel;
	private User user;
    private HttpServletRequest request;
    
	public BatchProcessWorker(MonitorBean monitorModel,User user,HttpServletRequest request) {
	   this.setMonitorModel(monitorModel);
	   this.setUser(user);
	   this.setRequest(request);
	}
	
	@Override
	public void run() {
		System.out.println("Start Thread:" + Thread.currentThread().getName());
		try {
			/** Process run Script Before Import **/
			new ExternalProcess().processImportBefore(request, user);
			
            startTaskStatus(this.monitorModel.getType(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
            
            logger.debug(" **********Start Process["+this.monitorModel.getType()+"] ******************");
            
            this.monitorModel = new ProcessManager().mainProcess(this.monitorModel,user,request);
            
           /* if(this.monitorModel.getType().equals(Constants.TYPE_GEN_HISHER)){
                monitorModel = (new ProcessManager()).processGenerateHisHerTxt(this.monitorModel, user,request);
                
            }else if(this.monitorModel.getType().equals(Constants.TYPE_IMPORT_BILL_ICC)){
            	monitorModel = (new ProcessManager()).processImportBillICC(this.monitorModel, user,request);
            	
            }else if(this.monitorModel.getType().equals(Constants.TYPE_EXPORT_BILL_ICC)){
            	monitorModel = (new ProcessManager()).processExportBillICC(this.monitorModel, user,request);
            	
            }else if(this.monitorModel.getType().equals(Constants.TYPE_GEN_ORDER_EXCEL)){
            	monitorModel = (new ProcessManager()).processGenerateOrderExcel(this.monitorModel, user,request);
            	
            }else if(this.monitorModel.getType().equals(Constants.TYPE_GEN_ITEM_MASTER_HISHER)){
            	monitorModel = (new ProcessManager()).processGenerateItemMasterHisHerTxt(this.monitorModel, user,request);
            	
            }else if(this.monitorModel.getType().equals(Constants.TYPE_IMPORT_TRANSACTION_LOTUS)){
            	monitorModel = (new ProcessManager()).processImportTransactionLotus(this.monitorModel, user,request);
            	
            }else if(this.monitorModel.getType().equals(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS)){
            	monitorModel = (new ProcessManager()).processGenStockEndDateLotus(this.monitorModel, user,request);
            	
            }else if(this.monitorModel.getType().equals(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS)){
            	monitorModel = (new ProcessManager()).processGenStockReportEndDateLotus(this.monitorModel, user,request);
            	
            }*/
            
            logger.debug(" **********Result Process["+this.monitorModel.getType()+"] :"+this.monitorModel.getStatus()+" ******************");
			logger.debug(" **********Result Process["+this.monitorModel.getType()+"] ErrorCode:"+Utils.isNull(this.monitorModel.getErrorCode()));
            
			/** Process Run Script After Import **/
			new ExternalProcess().processImportAfter(request,user);
			
			//Stamp task to Success
			endTaskStatus(this.monitorModel.getType(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	
	public MonitorBean getMonitorModel() {
		return monitorModel;
	}


	public void setMonitorModel(MonitorBean monitorModel) {
		this.monitorModel = monitorModel;
	}


	public BigDecimal getReGenTransactionId() {
		return reGenTransactionId;
	}

	public void setReGenTransactionId(BigDecimal reGenTransactionId) {
		this.reGenTransactionId = reGenTransactionId;
	}

	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	
	
}
