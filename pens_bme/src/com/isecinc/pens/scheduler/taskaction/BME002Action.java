package com.isecinc.pens.scheduler.taskaction;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.html.HTMLHtmlElement;

import com.isecinc.pens.bean.ExportFileBean;
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.dao.MTTBeanDAO;
import com.isecinc.pens.inf.manager.FTPManagerWacoal;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;


public class BME002Action {
	protected static Logger logger = Logger.getLogger("PENS");
	
	private static String genFileName() {
		try{
		    return DateUtil.stringValue(new Date(), DateUtil.YYYY_MM_DD_WITHOUT_SLASH)+"-SaleOut_Monthly.xls";
		}catch(Exception e){}
		return "";
	}
	
	public  ScheduleVO  execute(Connection conn ,ScheduleVO param) {
		EnvProperties env = EnvProperties.getInstance();
		String fileName = genFileName();
		String ftpFilePath = env.getProperty("path.export.wacoal");//wait
		String localTempFile = param.getLocalPath()+"/"+"temp.xls";
		String fileSize = "";
		try{
			//Get Data To Html Excel
			ExportFileBean exportFileBean = genDataExport();
            StringBuffer dataExport = exportFileBean.getDataExport();
            
            logger.debug("localTempFile:"+localTempFile);
            param.setNoOfRecord("0");
			param.setSizeOfFile("0");
			param.setSourcePath("PENS");
			param.setDestPath(ftpFilePath);
			param.setFileName(fileName);//
			
            if(dataExport != null && dataExport.length() >0){
				//write file to local
	            FileUtil.writeFile(localTempFile, dataExport,"UTF-8");
	            
	            FTPManagerWacoal ftpManager = new FTPManagerWacoal(env.getProperty("ftp.wacoal.ip.server"), env.getProperty("ftp.wacoal.username"), env.getProperty("ftp.wacoal.password"));
	            //upload file from local by sub ftp
	            ftpManager.uploadFileFromLocal(ftpFilePath+fileName, localTempFile);
	            //upload excel by FTPClient
	            //ftpManager.uploadExcelFileFromLocal(ftpFilePath,ftpFilePath+fileName, localTempFile);
	            
	            /** Set NoOfRecord and SizeOfFile */
				param.setNoOfRecord(exportFileBean.getTotalRecord()+"");
				fileSize = FileUtil.getFileSize(dataExport.toString());
				logger.debug("fileSize:"+fileSize);
				
				param.setSizeOfFile(fileSize);
            }
        
			//set status Complete
			param.setStatus(SchedulerConstant.STATUS_COMPLETE);
		}catch(Exception e){
			param.setStatus(SchedulerConstant.STATUS_FAIL);
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	public String appendCsv(String str){
		return Utils.isNull(str).replaceAll("\n","")+",";
	}
	
	private ExportFileBean genDataExport(){
		StringBuffer h = new StringBuffer("");
		String colSpan = "15";
		MTTBean bean = new MTTBean();
		ExportFileBean exportFileBean = new ExportFileBean();
		String date = "";
		int totalRecord = 0;
		try{
			//Now -1
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, -1);
			
			date = DateUtil.stringValue(now.getTime(),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>��§ҹ�����Ţ�� Sale-Out</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >�ҡ�ѹ�����:"+date+"  �֧�ѹ�����:"+date+"</td> \n");
			h.append("</tr> \n");

			h.append("</table> \n");
        	
			bean = getDataList(bean,now.getTime(),now.getTime());
			
			if(bean.getItems() != null){
			    List<MTTBean> list = (List<MTTBean>)bean.getItems();
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>No.</td> \n");
				  h.append("<td>SaleDate</td> \n");
				  h.append("<td>�ѹ��� Scan</td> \n");
				  h.append("<td>DocNo</td> \n");
				  h.append("<td>�����</td> \n");
				  h.append("<td>���ʡ����</td> \n");
				  h.append("<td>������ҹ���</td> \n");
				  h.append("<td>������ҹ���</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>GroupCode</td> \n");
				  h.append("<td>Material Master </td> \n");
				  h.append("<td>Pens Item </td> \n");
				  h.append("<td>�ӹǹ��鹷����</td> \n");
				  h.append("<td>�ҤҢ�»�ա��͹ VAT</td> \n");
				  h.append("<td>Remark</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<list.size();i++){
					MTTBean s = (MTTBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getNo()+"</td> \n");
					  h.append("<td>"+s.getSaleDate()+"</td> \n");
					  h.append("<td>"+s.getCreateDate()+"</td> \n");
					  h.append("<td>"+s.getDocNo()+"</td> \n");
					  h.append("<td>"+s.getCustGroup()+"</td> \n");
					  h.append("<td>"+s.getCustGroupName()+"</td> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td>"+s.getGroupCode()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='num'>"+s.getQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getRetailPriceBF()+"</td> \n");
					  h.append("<td>"+s.getRemark()+"</td> \n");
					h.append("</tr>");
					
					totalRecord++;
				}
				h.append("</table> \n");
			}
			exportFileBean.setDataExport(h);
			exportFileBean.setTotalRecord(totalRecord);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return exportFileBean;
	}
	
	public static MTTBean getDataList(MTTBean o,Date createDateFrom ,Date createDateTo ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MTTBean h = null;
		List<MTTBean> items = new ArrayList<MTTBean>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n  select doc_no,sale_date ,cust_group,cust_no,barcode," +
					"\n MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF ,remark,create_date" +
		            "\n ,status,count(*) as qty"+
					"\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
					"     and M.reference_code = 'Store' and M.pens_value = S.cust_no) as store_name  "+
					"\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
					"     and M.reference_code = 'Idwacoal' and M.pens_value = S.cust_group) as cust_group_name  "+
					" from PENSBME_SALES_OUT S");
			
			sql.append("\n where 1=1   \n");
			sql.append("\n and status <> '"+MTTBeanDAO.STATUS_CANCEL+"' \n");
		
			String fStr = DateUtil.stringValue(createDateFrom, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and trunc(CREATE_DATE) >= to_date('"+fStr+"','dd/mm/yyyy') ");

			String tStr = DateUtil.stringValue(createDateTo, DateUtil.DD_MM_YYYY_WITH_SLASH);
            sql.append("\n and trunc(CREATE_DATE) <= to_date('"+tStr+"','dd/mm/yyyy') ");
			
			sql.append("\n group by doc_no,sale_date ,cust_group,cust_no,barcode,MATERIAL_MASTER,GROUP_CODE,PENS_ITEM,RETAIL_PRICE_BF,status,remark,create_date");
			sql.append("\n order by doc_no desc,GROUP_CODE desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MTTBean();
			   h.setNo(r);
			   h.setDocNo(Utils.isNull(rst.getString("doc_no")));
			   h.setCustGroup(rst.getString("cust_group"));
			   h.setCustGroupName(rst.getString("cust_group_name"));
			   h.setSaleDate(DateUtil.stringValue(rst.getTimestamp("sale_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCreateDate(DateUtil.stringValue(rst.getTimestamp("create_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStoreCode(Utils.isNull(rst.getString("cust_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setRemark(Utils.isNull(rst.getString("remark")));
			   
               h.setBarcode(Utils.isNull(rst.getString("barcode")));
               h.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));
               h.setGroupCode(Utils.isNull(rst.getString("GROUP_CODE")));
               h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
               h.setRetailPriceBF(Utils.isNull(rst.getString("RETAIL_PRICE_BF")));
               h.setQty(rst.getInt("qty"));
               
               h.setStatus(Utils.isNull(rst.getString("status")));
               if("AB".equalsIgnoreCase(h.getStatus())){
            	   h.setStatusDesc("CANCEL");
            	   h.setBarcodeStyle("disableText");
   				   h.setCanCancel(false);
				   h.setCanEdit(false);
               }else{
            	   h.setStatusDesc("NEW");
            	   h.setBarcodeStyle("");  
            	   h.setCanCancel(true);
				   h.setCanEdit(true);
               }
               totalQty += h.getQty();
           
			   items.add(h);
			   r++;
			   
			}//while

			//set Result 
			o.setItems(items);
			o.setTotalQty(totalQty);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
}
