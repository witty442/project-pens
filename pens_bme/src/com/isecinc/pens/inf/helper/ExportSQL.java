package com.isecinc.pens.inf.helper;

import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.manager.process.ExportOrderToICC;
import com.pens.util.Utils;

public class ExportSQL {

	protected static Logger logger = Logger.getLogger("PENS");
	
	
	
	/**
	 * Get SQL By Special Case Export
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public static String genSpecialSQL(TableBean tableBean,User userBean,Map<String, String> batchParamMap) throws Exception{
		String str = "";
		try{
			String productType = Utils.isNull(batchParamMap.get(ExportOrderToICC.PARAM_PRODUCT_TYPE));
			
			if(tableBean.getTableName().equalsIgnoreCase("PENSBME_ICC_HEAD")){
				//Budish Date
				String billDateStr  = batchParamMap.get("TRANS_DATE").replaceAll("/", "");
				
				str =""+
				"SELECT "+
				"  A.Bill_date  \n"+
				" ,A.Bill_no  \n"+
				" ,A.Fact_id  \n"+
				" ,A.bus_code    \n"+
				" ,A.dept_code    \n"+
				" ,A.product_code   \n"+ 
				" ,A.spd_code   \n"+
				" ,NVL(A.Total_Amt,0) as Total_Amt   \n"+
				" ,NVL(A.Vat_Amt,0) as Vat_Amt  \n"+
				" ,A.Wh_SyStem   \n"+
				" ,A.Site_id  \n"+
				" ,A.Discount_Baht   \n"+
			    "FROM (" +
					"  SELECT \n" +
					"  m.Bill_date  \n"+
					" ,m.oracle_invoice_no as Bill_no  \n"+
					" ,(select max(d.fact_id) from PENSBME_ICC_DLYR d where d.bill_10 = m.bill_10 and d.bus_code = m.bus_code and d.dept_code = m.dept_code and d.product_code = m.product_code group by d.bill_10) Fact_id  \n"+
					" ,m.bus_code    \n"+
					" ,m.dept_code    \n"+
					" ,m.product_code   \n"+ 
					" ,m.spd_code   \n"+
					" ,NVL(D.Total_Amt,0) as Total_Amt   \n"+
					" ,NVL(D.Total_Amt*0.07,0)  as Vat_Amt  \n"+
					" ,(select max(d.Wh_SyStem) from PENSBME_ICC_DLYR d where d.bill_10 = m.bill_10 and d.bus_code = m.bus_code and d.dept_code = m.dept_code and d.product_code = m.product_code group by d.bill_10) as Wh_SyStem   \n"+
					" ,m.Site_id  \n"+
					" ,'0000000000000' as Discount_Baht   \n"+
					" FROM PENSBME_ICC_HEAD m \n"+
					" INNER JOIN ( \n"+
					"   select h.oracle_invoice_no ,nvl(sum(to_number(d.cost)*to_number(d.total_qty)),0) as Total_Amt \n" +
					"   from PENSBME_ICC_HEAD h , PENSBME_ICC_DLYR d \n" +
					"   WHERE 1=1 \n"+
					"   and h.bill_10 = d.bill_10 \n"+
					"   and d.bus_code = h.bus_code \n"+
					"   and d.dept_code = h.dept_code \n"+
					"   and d.product_code = h.product_code \n "+
					"   and h.bill_date = '"+billDateStr+"' \n"+
					"   and (h.interface_icc is null or h.interface_icc = 'N') \n"+
					"   group by h.oracle_invoice_no \n" +
					"  )D "+
					" ON m.oracle_invoice_no = D.oracle_invoice_no \n"+
					" WHERE m.bill_date = '"+billDateStr+"' \n"+
					" and (m.interface_icc is null or m.interface_icc = 'N') \n"+
					" group by  m.Bill_date \n"+  
					 " ,m.oracle_invoice_no  \n"+
					 " ,m.bill_10 \n"+
					 " ,m.bus_code  \n"+
					 " ,m.dept_code \n"+  
					 " ,m.product_code  \n"+  
					 " ,m.spd_code \n"+  
					 " ,m.Site_id  \n"+  
					 " ,D.Total_Amt  \n"+  
				") A"+
				" group by \n"+  
				 "    A.Bill_date  \n"+  
				 " 	 ,A.Bill_no  \n"+  
				 " 	 ,A.Fact_id  \n"+  
				 " 	 ,A.bus_code   \n"+   
				 " 	 ,A.dept_code  \n"+    
				 " 	 ,A.product_code \n"+    
				 " 	 ,A.spd_code  \n"+  
				 " 	 ,A.Wh_SyStem   \n"+  
				 " 	 ,A.Site_id  \n"+  
				 " 	 ,A.Discount_Baht  \n"+
				 " 	 ,A.Total_Amt \n"+ 
				 " 	 ,A.Vat_Amt  \n"; 
				
			}else if(tableBean.getTableName().equalsIgnoreCase("PENSBME_ICC_DLYR")){
				//Budish Date
				String billDateStr  = batchParamMap.get("TRANS_DATE").replaceAll("/", "");
				
				str =""+
				"  SELECT \n" +
				"  d.wh_system \n" +
				" ,m.bill_date as Bill_Fact_date \n" +
				" ,d.item_id   \n" +
				" ,d.size_code  \n" + 
				" ,d.color_code \n" + 
				" ,NVL(SUM(d.total_qty),0) as qty  \n" +
				" ,d.cost as cost_spi   \n" +
				" ,d.cost as cost_icc  \n" + 
				" ,m.ORACLE_INVOICE_NO as Bill_Factory \n" +
				" ,m.ORACLE_INVOICE_NO as Bill_SPI \n" +
				" ,d.bus_code  \n" +
				" ,d.dept_code   \n" +
				" ,d.product_code  \n" + 
				" ,d.spd_code   \n" +
				" ,d.base_uom_code \n" +
				" ,d.dfb_uom_code \n" +
				" ,NVL(SUM((to_number(d.cost)*to_number(d.total_qty) )),0) as cost_spi_amt  \n" +
				" ,NVL(SUM((to_number(d.cost)*to_number(d.total_qty) )),0) as cost_icc_amt  \n" +
				" ,'' as spi_id \n" +
				" ,'' as sup_id \n" +
				" , (SELECT field_value FROM  PENSBME_CONFIG_INTERFACE where product='"+productType+"' and subject = 'itm_MMDD' and field_name = 'CREDIT_TERM') as Credit_Term \n" +
				" ,m.Site_id \n" +
				" ,'ICC' as Cust_Fact \n" +
				" ,'ICC' as Sell_Cust \n" +
				" ,'' as Lot_no \n" +
				" ,'' as Serial_PreFix_Code \n" +
				" ,'' as Serial_Start_no \n" +
				" ,'' as Serial_End_no \n"+
				" FROM PENSBME_ICC_HEAD m \n " +
				" ,PENSBME_ICC_DLYR d \n"+
				" WHERE 1=1 \n"+
				" and m.bill_10 = d.bill_10 \n"+
				" and d.bus_code = m.bus_code \n"+
				" and d.dept_code = m.dept_code \n"+
				" and d.product_code = m.product_code \n "+
				" and m.bill_date = '"+billDateStr+"'"+
				" and (m.interface_icc is null or m.interface_icc = 'N')"+
				
				" group by d.wh_system  \n"+
				"  ,m.oracle_invoice_no \n"+
				"  ,m.bill_date \n"+
				"  ,d.item_id  \n"+
				"  ,d.size_code  \n"+
				"  ,d.color_code  \n"+
				"  ,d.cost \n"+
				"  ,d.bus_code   \n"+
				"  ,d.dept_code    \n"+
				"  ,d.product_code    \n"+
				"  ,d.spd_code    \n"+
				"  ,d.base_uom_code  \n"+
				"  ,d.dfb_uom_code  \n"+
				"  ,m.Site_id ";

			}
			return str;
		}catch(Exception e){
			throw e;
		}
	}
	
	
}
