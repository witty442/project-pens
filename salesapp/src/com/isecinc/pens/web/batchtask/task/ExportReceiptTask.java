package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.interfaces.exports.ExportReceiptManager;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskDispBean;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.pens.util.DBConnection;
import com.pens.util.meter.MonitorTime;

public class ExportReceiptTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	public static String PARAM_DATA_TYPE = "dataType";
   
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	/*public String getParam(){
		return "dataType|�кػ��������|LIST||VALID,dataFormFile|���͡���|FROMFILE||VALID$Import ������";
	}*/
	public String[] getParam(){
		String[] param = null;
		return param;
	}
	public String getButtonName(){
		return "Export �������Ѻ�����Թ";
	}
	public String getDescription(){
		return "Export �������Ѻ�����Թ  Receipt(Lockbox)";
	}
	public String getDevInfo(){
		return "PENSONLINE.T_RECEIPT ";
	}
	//Show detail BatchTaskResult or no
	public BatchTaskDispBean getBatchDisp(){
		BatchTaskDispBean dispBean = new BatchTaskDispBean();
		dispBean.setDispDetail(true);
		dispBean.setDispRecordFailHead(false);
		dispBean.setDispRecordFailDetail(false);
		dispBean.setDispRecordSuccessHead(false);
		dispBean.setDispRecordSuccessDetail(false);
		return dispBean;
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		
		return listAll;
	}
	
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n    return true";
		script +="\n }";
		script +="\n </script>";
		return script;
	}
	
	/** Run Process **/
	public  MonitorBean run(MonitorBean monitorModel){
		Connection conn = null;
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ExportReceiptTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(BatchTaskConstants.TRANSACTION_TYPE);
			
			/** Start process **/ 
			/** Get Parameter **/
			//String dataType =Utils.isNull(monitorModel.getBatchParamMap().get(PARAM_DATA_TYPE)) ;
			
			modelItem = runProcess(connMonitor,conn,monitorModel,modelItem);

			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== BatchTaskConstants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rolback");
				conn.rollback();
			}
			
			/** Update status Head Monitor From Monitor Item Status*/
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			monitorModel.setThName("Export �������Ѻ�����Թ ");
			
			logger.debug("errorMsg:"+monitorModel.getErrorMsg());
			/** Update Status Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);

		}catch(Exception e){
			try{
				logger.error(e.getMessage(),e);
				
				/** End process ***/
				logger.debug("Update Monitor to Fail ");
				monitorModel.setStatus(BatchTaskConstants.STATUS_FAIL);
				monitorModel.setBatchTaskStatus(BatchTaskConstants.STATUS_SUCCESS);//Thread batchTask end process
				monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
				
				dao.updateMonitorCaseError(connMonitor,monitorModel);
	
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),monitorModel.getName());
				
				if(conn != null){
				   logger.debug("Transaction Rollback");
				   conn.rollback();
				}
			}catch(Exception ee){}
		}finally{
		   try{
				if(conn != null){
					conn.setAutoCommit(true);
					conn.close();
					conn =null;
				}
				if(connMonitor != null){
					connMonitor.close();
					connMonitor=null;
				}
		   }catch(Exception ee){}
		}
		return monitorModel;
	}

	public static MonitorItemBean runProcess(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		PreparedStatement ps = null;
		StringBuffer sql  =new StringBuffer();
		int status = BatchTaskConstants.STATUS_FAIL;int dataCount=0;
		int successCount = 0;int failCount = 0;int no = 0;
		boolean passValid = false;String lineMsg = "";
		boolean foundError = false;
		String errorMsg ="";
		String salesCode = ""; //blank all salesCode
		try{
			//Get Parameter Value
			//String dataType =Utils.isNull(monitorModel.getBatchParamMap().get(PARAM_DATA_TYPE)) ;
			try{
				//Interfaces TO Oracle Txt (LockBox)
				new ExportReceiptManager().process(monitorModel.getUser(),salesCode);
				successCount++;
				
				logger.debug("result foundError:"+foundError);
				if(foundError ==false){
					status = BatchTaskConstants.STATUS_SUCCESS;
				}
			}catch(Exception ee){
				status = BatchTaskConstants.STATUS_FAIL;
				monitorItemBean.setErrorMsg(ee.getMessage());
			}
			logger.debug("result status:"+status);
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
			}catch(Exception ee){}
		}
		return monitorItemBean;	
	}
	
	
}
