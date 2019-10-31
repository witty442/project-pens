package com.isecinc.pens.web.batchtask;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.pens.util.Utils;


/**
 * @author WITTY
 *
 */
public class BatchTaskProcessWorker extends BatchTaskWorker {
	public static Logger logger = Logger.getLogger("PENS");
    private MonitorBean monitorModel;

	public BatchTaskProcessWorker(MonitorBean monitorModel) {
	   this.setMonitorModel(monitorModel);
	}
	
	@Override
	public void run() {
		logger.debug("Start Thread:" + Thread.currentThread().getName());
		BatchTaskDAO dao = new BatchTaskDAO();
		try {
			/** Validate Check TaskName can Run **/
			boolean canRunBatch = dao.canRunBatchTask(monitorModel.getName());
			logger.info("TaskName["+monitorModel.getName()+"]canRunBatch["+canRunBatch+"]");
		
			if( !canRunBatch){
				startTaskStatusCaseFail(this.monitorModel.getName(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
				monitorModel.setErrorMsg("โปรแกรมนี้ กำลังทำรายการอยู่ ไม่สามารถทำรายการพร้อมกันได้ กรุณารอสักครู่ แล้วค่อยทำรายการใหม่");
				
				monitorModel.setStatus(-1);//fail
				monitorModel.setChannel("-1");//fail
				monitorModel = dao.updateMonitor(monitorModel);
				
				//Stamp task to Success
				endTaskStatusCaseFail(this.monitorModel.getName(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
			}else{
				startTaskStatus(this.monitorModel.getName(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
		        logger.debug(" **********Start Process["+this.monitorModel.getType()+"] ******************");
		        
	            Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+this.monitorModel.getName()+"Task");
	    		Object obj = cls.newInstance();
	    		
	    		//int parameter MonitorBean
	    		Class[] classParamMonitorBean = new Class[1];
	    		classParamMonitorBean[0] = Class.forName("com.isecinc.pens.bean.MonitorBean");
	    		
	    		Method method = cls.getDeclaredMethod("run", classParamMonitorBean);
	    		method.invoke(obj, this.monitorModel);
	    		
	    		//Stamp task to Success
				endTaskStatus(this.monitorModel.getName(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
			}
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


	
}
