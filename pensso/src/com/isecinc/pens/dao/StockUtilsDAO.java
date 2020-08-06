package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import util.NumberToolsUtil;

import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MUOMConversion;
import com.pens.util.DBConnectionApps;

public class StockUtilsDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static void main1(String[] a){
		Connection conn = null;
		try{
			conn =DBConnectionApps.getInstance().getConnection();
			String uomFrom ="CUP";
			String uomTo ="CTN";
			String subQty = StockUtilsDAO.convertStockQty(conn, "753157", uomFrom, uomTo, "20");
			
			System.out.println("subQty:"+subQty);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] a){
		Connection conn = null;
		try{
			conn =DBConnectionApps.getInstance().getConnection();
			String uom1 ="CTN";
			String uom2 ="CUP";
			String subQty = StockUtilsDAO.convertSecQtyToPriQty(conn, "753157", uom1, uom2, "20");
			
			System.out.println("subQty:"+subQty);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param conn
	 * @param productId
	 * @param uomId
	 * @param qty
	 * @return  priQty
	 * @throws Exception
	 */
	 public static double calcPriQty(Connection conn,String productId,String uomId,int qty) throws Exception{
		double priQty = 0;
		if("CTN".equalsIgnoreCase(uomId)){
			priQty = qty;
		}else{
			UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), "CTN");//default to CTN
		    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), uomId);
	        logger.debug("("+uc1.getConversionRate()+"/"+uc2.getConversionRate()+")");
	        
	        if(uc2.getConversionRate() > 0){
	        	double qty2Temp = qty/ (uc1.getConversionRate()/uc2.getConversionRate()) ;
	        	//convert to Str 1.666667
				String qty2Str5Digit = NumberToolsUtil.decimalFormat(qty2Temp, NumberToolsUtil.format_current_6_digit);
				//substr remove digit no 6 :"7" -> 1.66666
				qty2Str5Digit = qty2Str5Digit.substring(0,qty2Str5Digit.length()-1);
				
				double pcsQty = Double.parseDouble((qty2Str5Digit));
	        	priQty = pcsQty;
	        }
	    }
	    logger.debug("result calc qty["+priQty+"]");
	    return priQty;
	}
	 
	 public static double calcPriQtyToSubQty(Connection conn,String productId,String uom2,double subQty) throws Exception{
		double calcSubQty = 0;
		UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), "CTN");//default to CTN
		UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), uom2);
	    logger.debug("uom2:"+uom2);
		logger.debug("("+uc1.getConversionRate()+"/"+uc2.getConversionRate()+")");
	        
	     if(uc2.getConversionRate() > 0){
        	double qty2Temp = subQty * uc1.getConversionRate() ;
        	calcSubQty = qty2Temp/uc2.getConversionRate();
	      }
	     logger.debug("result calcSubQty["+calcSubQty+"]");
	    return calcSubQty;
	}
	 
	 /**
	  * convert secQty to Primary_qty (ex 1.8)
	  * @param conn
	  * @param productId
	  * @param uom1
	  * @param uom2
	  * @param secQtyIn
	  * @return
	  * @throws Exception
	  */
	 public static String convertSecQtyToPriQty(Connection conn,String productId
			 ,String uom1,String uom2,String secQtyIn) throws Exception{
		double priQty = 0;
		UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), uom1);//default to CTN
	    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), uom2);
        logger.debug("("+uc1.getConversionRate()+"/"+uc2 !=null?uc2.getConversionRate():""+")");
        
        if(uc2.getConversionRate() > 0){
        	double qty2Temp = Utils.convertStrToDouble(secQtyIn) / (uc1.getConversionRate()/uc2.getConversionRate()) ;
        	//convert to Str 1.666667
        	logger.debug("qty2Temp:"+qty2Temp);
			String qty2Str5Digit = NumberToolsUtil.decimalFormat(qty2Temp, NumberToolsUtil.format_current_6_digit);
			//substr remove digit no 6 :"7" -> 1.66666
			qty2Str5Digit = qty2Str5Digit.substring(0,qty2Str5Digit.length()-1);
			logger.debug("qty2Str5Digit:"+qty2Str5Digit);
			
			double pcsQty = Double.parseDouble((qty2Str5Digit));
        	priQty = pcsQty;
        }
	    logger.debug("result calc qty["+priQty+"]");
	    return String.valueOf(priQty);
	}
	 
	 public static String convertStockQty(Connection conn,String productId,String uomFrom,String uomTo,String subQtyIn) throws Exception{
			PreparedStatement  ps = null;
			ResultSet rs = null;
			String subQtyOut = "";
			try{
				/**
				 *   inv_convert.inv_um_convert(item_id       => 3, --inventory_item_id
	                                  precision     => 5, --decimal
	                                  from_quantity => 15, --qty
	                                  from_unit     => 'CUP', -- uom from
	                                  to_unit       => 'CTN', -- uom to
	                                  from_name     => null,
	                                  to_name       => null)
				 */
				logger.debug("productId["+productId+"]subQtyIn["+subQtyIn+"]uomFrom["+uomFrom+"]uomTo["+uomTo+"]");
				StringBuffer sql = new StringBuffer("");
				sql.append("select inv_convert.inv_um_convert(?,?,?,?,?,?,?) as sub_qty from dual");
			
				ps = conn.prepareStatement(sql.toString());
				ps.setString(1,productId);
				ps.setInt(2, 5);
				ps.setString(3,subQtyIn);
				ps.setString(4,uomFrom);
				ps.setString(5,uomTo);
				ps.setString(6,null);
				ps.setString(7,null);
				rs = ps.executeQuery();
				if(rs.next()){
					subQtyOut = rs.getString("sub_qty");
				}
				return subQtyOut;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
}
