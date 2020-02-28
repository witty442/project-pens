package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportOrderAutoSubFromWacoalTask extends BatchTask implements BatchTaskInterface{

	private static String PARAM_AS_OF_DATE = "AS_OF_DATE";
	private static String PARAM_CUST_GROUP = "custGroup";
	
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate,xxxx|||$Button Name
	 */
	/*public String getParam(){
		//return "AS_OF_DATE|วันที่เปิด Order จากโรงงาน Wacoal|DATE|SYSDATE|VALID$Import ข้อมูล";
		return "custGroup|กลุ่มร้านค้า|LIST||VALID,AS_OF_DATE|วันที่เปิด Order จากโรงงาน Wacoal|DATE|SYSDATE|VALID$Import ข้อมูล";
	}*/
	public String[] getParam(){
		String[] param = new String[2];
		param[0] = "custGroup|กลุ่มร้านค้า|LIST||VALID";
		param[1] = "AS_OF_DATE|วันที่เปิด Order จากโรงงาน Wacoal|DATE|SYSDATE|VALID";
		return param;
	}
	public String getButtonName(){
		return "Import ข้อมูล";
	}
	
	public String getDescription(){
		return "เฉพาะข้อมูล Order ของกลุ่มร้านค้าที่เป็นลักษณะ การโอนสินค้า ระหว่าง Sub Inventory เช่น BigC,Shop ต่างๆ";
	}
	public String getDevInfo(){
		return "Import Order(PENSBI.PENSBME_ORDER) Customer in (PENSBI.PENSBME_MST_REFERENCE reference_code = 'AutoPOtoORACLE') To Oracle "
				+ "<br/> (apps.XXPENS_PO_ORDER_IMPORT_MST,apps.XXPENS_PO_ORDER_IMPORT_DT) ";
	}
	public boolean isDispDetail(){
		return true;
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		try{
			List<Master> custGroupList= GeneralDAO.getCustGroupAutoPOToOracleList();
			if(custGroupList != null && custGroupList.size() >0){
				//LIST 1
				BatchTaskListBean listHeadBean = new BatchTaskListBean();
				List<BatchTaskListBean> listBoxData = new ArrayList<BatchTaskListBean>();
				//listBoxData.add(new BatchTaskListBean("",""));
				for(int i=0;i<custGroupList.size();i++){
				   Master m = custGroupList.get(i);
				   listBoxData.add(new BatchTaskListBean(m.getPensValue(),m.getPensDesc()));
				}
				
				listHeadBean.setListBoxName("LIST");
				listHeadBean.setListBoxData(listBoxData);
				
				listAll.add(listHeadBean);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return listAll;
	}
	public String getValidateScript(){
		String script ="";
		
		script +="\n <script>";
		script +="\n function validate(){";
		script +="\n  var form = document.batchTaskForm;";
		script +="\n  if(form.AS_OF_DATE.value ==''){";
		script +="\n    alert('กรุณาระบุ วันที่เปิด Order Big-C จากโรงงาน Wacoal');";
		script +="\n    form.AS_OF_DATE.focus();";
		script +="\n    return false;";
		script +="\n  }";
		script +="\n  return true";
		script +="\n }";
		script +="\n </script>";
		
		return script;
	}
	public  MonitorBean run(MonitorBean monitorModel){
		Connection conn = null;
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		EnvProperties env = EnvProperties.getInstance();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item Store Auto Sub Transfer PO ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			//Validate Duplication
			if(isImportExist(conn,monitorModel)){
				 modelItem.setStatus(Constants.STATUS_FAIL);
				 modelItem.setErrorMsg("ได้เคย Import ข้อมูลวันที่ระบุ ไปก่อนหน้านี้แล้ว");
				 modelItem.setErrorCode("ImportException");
			}else{
				//Process Generate 
				modelItem = runProcess(conn,monitorModel,modelItem);
				
				/** validate data **/
	            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
	 			    modelItem.setErrorMsg("ไม่พบข้อมูลการเปิด Order ในวันที่ระบุ");
	 			    modelItem.setErrorCode("DataNotFoundException");
	            }else{
	            	modelItem.setErrorCode("SuccessException"); 
	            }
			}
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rolback");
				conn.rollback();
			}
			
			/** Update status Head Monitor */
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			/** Update Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);

		}catch(Exception e){
			try{
				logger.error(e.getMessage(),e);
				
				/** End process ***/
				logger.debug("Update Monitor to Fail ");
				monitorModel.setStatus(Constants.STATUS_FAIL);
				monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
				monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
				
				dao.updateMonitorCaseError(connMonitor,monitorModel);
	
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),monitorModel.getName());
				
				if(conn != null){
				   logger.debug("Transaction Rolback");
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
	
	public static MonitorItemBean prepareMonitorItemBean(MonitorBean monitorModel) throws Exception{
		MonitorItemBean modelItem = new MonitorItemBean();
		modelItem.setMonitorId(monitorModel.getMonitorId());
		modelItem.setSource("BME");
		modelItem.setDestination("ORACLE");
		modelItem.setStatus(Constants.STATUS_START);
		modelItem.setSubmitDate(new Date());
		modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
		
		return modelItem;
	}
	
	public static boolean isImportExist(Connection conn,MonitorBean monitorModel) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		boolean exist = false;
		Map<String, String> batchParamMap = monitorModel.getBatchParamMap();
		try{
			String custGroup = Utils.isNull(batchParamMap.get(PARAM_CUST_GROUP));
			String asOfDate =DateUtil.stringValue(DateUtil.parse(batchParamMap.get(PARAM_AS_OF_DATE),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th),DateUtil.DD_MM_YYYY_WITH_SLASH) ;

			sql.append("\n SELECT count(*) as c");
			sql.append("\n FROM PENSBME_ORDER O");
			sql.append("\n WHERE O.ORDER_DATE  = TO_DATE('"+asOfDate+"','dd/mm/yyyy')");
			sql.append("\n AND O.STORE_TYPE ='"+custGroup+"'");
			sql.append("\n AND ( O.ORDER_LOT_NO IS  NOT NULL OR ORDER_LOT_NO <> '')");
			sql.append("\n AND O.ORDER_LOT_NO IN(  ");
			sql.append("\n   SELECT ORDER_NUMBER FROM XXPENS_PO_ORDER_IMPORT_MST ");
			sql.append("\n  ) ");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());

			rs = ps.executeQuery();
			if(rs.next()){
			   if(rs.getInt("c") >0){
				   exist = true;
			   }
			}//while	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return exist;	
	}
	
	public static MonitorItemBean runProcess(Connection conn,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		PreparedStatement psIns =null;
		StringBuffer sql = new StringBuffer("");
		int status = Constants.STATUS_FAIL;
		boolean foundData = false;
		int successCount = 0;
		int failCount = 0;
		int index = 0;
		BigDecimal headId = null;
		Map<String, String> batchParamMap = monitorModel.getBatchParamMap();
		try{
			String custGroup = Utils.isNull(batchParamMap.get(PARAM_CUST_GROUP));
			String asOfDate = DateUtil.stringValue(DateUtil.parse(batchParamMap.get(PARAM_AS_OF_DATE),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th),DateUtil.DD_MM_YYYY_WITH_SLASH) ;

			sql.append("\n SELECT DISTINCT ");
			sql.append("\n  O.STORE_CODE");
			sql.append("\n ,O.ORDER_DATE");
			sql.append("\n ,(SELECT INTERFACE_DESC FROM PENSBI.PENSBME_MST_REFERENCE M where reference_code = 'SubInv' and M.pens_value = O.store_code) as SUBINV");
			sql.append("\n ,(SELECT PENS_DESC FROM PENSBI.PENSBME_MST_REFERENCE M where reference_code = 'Store' and M.pens_value = O.store_code) as STORE_NAME");
			sql.append("\n ,(SELECT max(PENS_DESC5) FROM PENSBI.PENSBME_MST_REFERENCE M where reference_code = 'AutoPOtoORACLE' ");
			sql.append("\n   and M.pens_value = O.store_type) as forwarder");
			sql.append("\n ,O.ORDER_LOT_NO");
			//Get transporter_qty from 
			sql.append("\n ,(SELECT TOTAL_BOX FROM PENSBI.PENSBME_BOX_BYWACOAL M where M.LOT_NO = O.ORDER_LOT_NO) as transporter_qty ");
			sql.append("\n FROM PENSBI.PENSBME_ORDER O");
			sql.append("\n WHERE O.ORDER_DATE  = TO_DATE('"+asOfDate+"','dd/mm/yyyy')");
			sql.append("\n AND O.STORE_TYPE ='"+custGroup+"'");
			sql.append("\n AND ( O.ORDER_LOT_NO IS  NOT NULL OR ORDER_LOT_NO <> '')");
			sql.append("\n AND O.STORE_TYPE IN (");
			sql.append("\n   select pens_value from PENSBI.PENSBME_MST_REFERENCE");
			sql.append("\n   where reference_code = 'AutoPOtoORACLE'");
			sql.append("\n )");
			sql.append("\n AND O.ORDER_LOT_NO NOT IN(  ");
			sql.append("\n   SELECT ORDER_NUMBER FROM XXPENS_PO_ORDER_IMPORT_MST ");
			sql.append("\n  ) ");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			
			String sqlIn = "insert into PENSBI.XXPENS_PO_ORDER_IMPORT_MST \n";
			sqlIn +=" ( created_by, creation_date,last_updated_by, last_update_date,last_update_login, \n";
			sqlIn +=" header_id ,account_number,ordered_date , \n ";
			sqlIn +=" subinventory , int_flag,order_number,comments ,transporter_name,transporter_qty) \n";
			sqlIn +=" values(?,?,?,?, ?,?,?,? ,?,?,?,?,?,?) \n";
			
			psIns = conn.prepareStatement(sqlIn);
			rs = ps.executeQuery();
			while(rs.next()){
				foundData = true;
				index = 0;
				psIns.setInt(++index, 2408);
				psIns.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIns.setInt(++index, 2408);
				psIns.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIns.setDouble(++index, -1);
				
				headId = SequenceProcess.getNextValueBig("PO_ORDER_BIGC");//gen next head_id
				psIns.setBigDecimal(++index, headId);//headId
				psIns.setString(++index, Utils.isNull(rs.getString("store_code")));//account_number
				psIns.setDate(++index, rs.getDate("order_date"));
				psIns.setString(++index, Utils.isNull(rs.getString("subinv")));
				psIns.setString(++index,"N");
				psIns.setString(++index, Utils.isNull(rs.getString("order_lot_no")));
				psIns.setString(++index, Utils.isNull(rs.getString("STORE_NAME")));
				psIns.setString(++index, Utils.isNull(rs.getString("forwarder")));
				psIns.setInt(++index, rs.getInt("transporter_qty"));
				
				psIns.executeUpdate();
				
				//insert Line
				successCount += insertLine(conn,headId,rs.getString("order_lot_no"));
			}//while
			
			if(foundData){
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return monitorItemBean;	
	}
	
	public static int insertLine(Connection conn,BigDecimal headId, String orderLotNo) throws Exception{
		ResultSet rs = null;
		PreparedStatement ps =null;
		PreparedStatement psIns =null;
		StringBuffer sql = new StringBuffer("");
		int successCount = 0;
		int index = 0;
		int lineId = 0;	
		try{
			String sqlIn = " insert into XXPENS_PO_ORDER_IMPORT_DT \n";
			sqlIn +=" ( created_by, creation_date,last_updated_by, last_update_date,last_update_login, \n";
			sqlIn +="   header_id,line_id,item_no , qty,group_code )\n ";
			sqlIn += " values(?,?,?,? ,?,?,?,? ,?,?)";
			psIns = conn.prepareStatement(sqlIn);
			
			sql.append("\n SELECT  ");
			sql.append("\n O.ORDER_LOT_NO,O.ITEM ,O.GROUP_CODE");
			sql.append("\n ,NVL(SUM(O.QTY),0) as QTY");
			sql.append("\n FROM PENSBME_ORDER O");
			sql.append("\n WHERE O.ORDER_LOT_NO ='"+orderLotNo+"'");
			sql.append("\n GROUP BY O.ORDER_LOT_NO,O.ITEM,O.GROUP_CODE ");
			sql.append("\n ORDER BY O.GROUP_CODE ,O.ITEM");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				index = 0;
				lineId++;
				
				psIns.setInt(++index, 2408);
				psIns.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIns.setInt(++index, 2408);
				psIns.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIns.setDouble(++index, -1);
				
				psIns.setBigDecimal(++index, headId);//headId
				psIns.setInt(++index, lineId);//lineId
				psIns.setString(++index, Utils.isNull(rs.getString("item")));
				psIns.setInt(++index, rs.getInt("qty"));
				psIns.setString(++index, Utils.isNull(rs.getString("group_code")));
				
				psIns.executeUpdate();
				successCount++;
			}//while

		}catch(Exception e){
		  throw e;
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return successCount;	
	}
}
