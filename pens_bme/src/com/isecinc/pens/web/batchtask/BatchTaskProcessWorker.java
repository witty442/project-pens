package com.isecinc.pens.web.batchtask;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.bean.MonitorBean;

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
		try {
	
            startTaskStatus(this.monitorModel.getName(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
            
            logger.debug(" **********Start Process["+this.monitorModel.getType()+"] ******************");

            Class cls = Class.forName("com.isecinc.pens.web.batchtask.task."+this.monitorModel.getName()+"Task");
    		Object obj = cls.newInstance();
    		
    		//int parameter MonitorBean
    		Class[] classParamMonitorBean = new Class[1];
    		classParamMonitorBean[0] = Class.forName("com.isecinc.pens.inf.bean.MonitorBean");
    		
    		Method method = cls.getDeclaredMethod("run", classParamMonitorBean);
    		method.invoke(obj, this.monitorModel);
    				
			//Stamp task to Success
			endTaskStatus(this.monitorModel.getName(),this.monitorModel.getTransactionId(),this.monitorModel.getMonitorId());
			
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
