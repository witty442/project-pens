package com.isecinc.pens.inf.helper;

import org.apache.log4j.Logger;

import util.Constants;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.TableBean;

public class ExportSQL {

	protected static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * Get SQL By Special Case Export
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public static String genSpecialSQL(TableBean tableBean,User userBean) throws Exception{
		String str = "";
		try{
			 if(tableBean.getTableName().equalsIgnoreCase("pensbme_barcode_scan") ){
				str ="	select 			\n"+
				"	'H'	AS	RECORD_TYPE	,	\n"+
				"	t.DOC_NO	AS	DOC_NO  	,	\n"+
				"	t.DOC_DATE 	AS	DOC_DATE 	,	\n"+
				"	t.CUST_GROUP	AS	CUST_GROUP	,	\n"+
				"	t.REMARK	AS	REMARK	,	\n"+
				"	t.CUST_NO	AS	CUST_NO	,	\n"+
				"	t.STATUS	AS	STATUS	,	\n"+
				"	'"+tableBean.getFileFtpNameFull()+"'	AS	FILE_NAME 		\n"+

				"	FROM pensbme_barcode_scan t 	\n"+
				"	where 1=1	\n"+
				//"   and  m.user_id = "+userBean.getId()+" \n"+
				"   and  t.STATUS = '"+Constants.STATUS_CLOSE+"' \n"+
				"   and ( t.EXPORT_FLAG  = 'N' OR t.EXPORT_FLAG  IS NULL OR TRIM(t.EXPORT_FLAG) ='') \n"+
				"   ORDER BY t.DOC_NO \n";
			}
			
			return str;
		}catch(Exception e){
			throw e;
		}
	}
}
