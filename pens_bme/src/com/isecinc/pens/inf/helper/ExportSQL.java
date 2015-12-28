package com.isecinc.pens.inf.helper;

import java.util.Map;

import org.apache.log4j.Logger;

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
	public static String genSpecialSQL(TableBean tableBean,User userBean,Map<String, String> batchParamMap) throws Exception{
		String str = "";
		try{
			if(tableBean.getTableName().equalsIgnoreCase("PENSBME_ICC_HEAD")){
				//Budish Date
				String billDateStr  = batchParamMap.get("TRANS_DATE").replaceAll("/", "");
				
				str =""+
				" SELECT \n" +
				" m.Bill_date  \n"+
				" ,m.Bill_no  \n"+
				" ,(select max(d.fact_id) from PENSBME_ICC_DLYR d where d.bill_10 = m.bill_10 group by d.bill_10) Fact_id  \n"+
				" ,m.bus_code    \n"+
				" ,m.dept_code    \n"+
				" ,m.product_code   \n"+ 
				" ,m.spd_code   \n"+
				" ,(select nvl(sum(to_number(d.cost)*to_number(d.total_qty)),0) from PENSBME_ICC_DLYR d where d.bill_10 = m.bill_10 group by d.bill_10) as Total_Amt   \n"+
				" ,(select nvl(sum(to_number(d.cost)*to_number(d.total_qty)),0) from PENSBME_ICC_DLYR d where d.bill_10 = m.bill_10 group by d.bill_10)*0.07  as Vat_Amt  \n"+
				" ,(select max(d.Wh_SyStem) from PENSBME_ICC_DLYR d where d.bill_10 = m.bill_10 group by d.bill_10) as Wh_SyStem   \n"+
				" ,m.Site_id  \n"+
				" ,'' as Discount_Baht   \n"+
				" FROM PENSBME_ICC_HEAD m"+
				" WHERE 1=1 \n"+
				" and m.bill_date = '"+billDateStr+"'"+
				" and (m.interface_icc is null or m.interface_icc = 'N')";
				
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
				" ,d.total_qty as qty  \n" +
				" ,d.cost as cost_spi   \n" +
				" ,d.cost as cost_icc  \n" + 
				" ,m.ORACLE_INVOICE_NO as Bill_Factory \n" +
				" ,'' as Bill_SPI \n" +
				" ,d.bus_code  \n" +
				" ,d.dept_code   \n" +
				" ,d.product_code  \n" + 
				" ,d.spd_code   \n" +
				" ,d.base_uom_code \n" +
				" ,d.dfb_uom_code \n" +
				" ,(to_number(d.cost)*to_number(d.total_qty) ) as cost_spi_amt  \n" +
				" ,(to_number(d.cost)*to_number(d.total_qty) ) as cost_icc_amt  \n" +
				" ,'' as spi_id \n" +
				" ,'' as sup_id \n" +
				" , (SELECT field_value FROM  PENSBME_CONFIG_INTERFACE where subject = 'itm_MMDD' and field_name = 'CREDIT_TERM') as Credit_Term \n" +
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
				" and m.bill_date = '"+billDateStr+"'"+
				" and (m.interface_icc is null or m.interface_icc = 'N')";

			}
			return str;
		}catch(Exception e){
			throw e;
		}
	}
	
	
}
