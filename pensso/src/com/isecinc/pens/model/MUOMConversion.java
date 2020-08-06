package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.inf.helper.Utils;

public class MUOMConversion extends I_Model<UOMConversion> {

	private static final long serialVersionUID = 3772548485733964501L;

	public static String TABLE_NAME = "pensso.m_uom_conversion";

	/**
	 * Get Current Conversion
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public UOMConversion getCurrentConversion(int productId, String uomId) throws Exception {
		//logger.debug("Find UOM conversion on Product : " + productId + " : " + uomId);
		String whereCause = "";
		whereCause += "  and product_id = " + productId;
		whereCause += "  and uom_id = '" + uomId + "' ";
		whereCause += "  and (disable_date is null or disable_date >= trunc(sysdate)) ";
		List<UOMConversion> pos = super.search(TABLE_NAME, "", whereCause, UOMConversion.class);
		if (pos != null) {
			if (pos.size() > 0) {
				return pos.get(0);
			} else {
				return new UOMConversion();
			}
		} else {
			return new UOMConversion();
		}
	}
	
	public UOMConversion getCurrentConversion(Connection conn,int productId, String uomId) throws Exception {
		//logger.debug("Find UOM conversion on Product : " + productId + " : " + uomId);
		String whereCause = "";
		whereCause += "  and product_id = " + productId;
		whereCause += "  and uom_id = '" + uomId + "' ";
		whereCause += "  and (disable_date is null or disable_date >= trunc(sysdate) ) ";
		List<UOMConversion> pos = super.search(conn,TABLE_NAME, "", whereCause, UOMConversion.class);
		if (pos != null) {
			if (pos.size() > 0) {
				return pos.get(0);
			} else {
				return new UOMConversion();
			}
		} else {
			return new UOMConversion();
		}
	}
	
	public UOMConversion getCurrentConversionNotIn(int productId, String uomId) throws Exception {
		//logger.debug("Find UOM conversion on Product : " + productId + " : " + uomId);
		String whereCause = "";
		whereCause += "  and product_id = " + productId;
		whereCause += "  and uom_id <> '" + uomId + "' ";
		whereCause += "  and (disable_date is null or disable_date >= trunc(sysdate)) ";
		List<UOMConversion> pos = super.search(TABLE_NAME, "", whereCause, UOMConversion.class);
		if (pos != null) {
			if (pos.size() > 0) {
				return pos.get(0);
			} else {
				return new UOMConversion();
			}
		} else {
			return new UOMConversion();
		}
	}
	
	/**
	 * Get Capacity
	 * 
	 * @param baseUOM
	 * @param uom
	 * @param product
	 * @return capacity of BaseUOM/UOM
	 * @throws Exception
	 */
	public double getCapacity(UOM baseUOM, UOM uom, Product product) throws Exception {
		// base uom = EA
		double baseRate = 0;
		double rate = 0;
		double capacity = 0;
		if (baseUOM == null) baseUOM = product.getUom();
		if (baseUOM != null) {
			UOMConversion baseConversion = new MUOMConversion().getCurrentConversion(product.getId(), baseUOM.getId());
			baseRate = baseConversion.getConversionRate();
			//logger.debug(baseUOM);
			//logger.debug(baseConversion);
		}
		if (uom != null) {
			UOMConversion conversion = new MUOMConversion().getCurrentConversion(product.getId(), uom.getId());
			rate = conversion.getConversionRate();
			//logger.debug(uom);
			//logger.debug(conversion);
		}
		if (rate == 0) capacity = baseRate;
		else capacity = baseRate / rate;
		return capacity;
	}
	
	
	/**
	 * 
	 * @param line
	 * @return
	 * @throws Exception
	 */
	public double getCapacity(int productId,String uom1, String uom2) throws Exception{
		double pack = 0;
		UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(productId, uom1);
	    UOMConversion  uc2 = null;
	    
	    if( !Utils.isNull(uom2).equals("")){
	    	logger.debug("case 1 uom1,uom2 not null");
	        uc2 = new MUOMConversion().getCurrentConversion(productId, uom2);
	        pack =  uc1.getConversionRate()/uc2.getConversionRate() ;
	        
	    }else{
	    	logger.debug("case 1 uom2 is null");
	    	uc2 = new MUOMConversion().getCurrentConversionNotIn(productId, uom1);
	    	
	    	if(uc2 != null && uc2.getConversionRate() != 0){
	    	   pack =  uc1.getConversionRate()/uc2.getConversionRate() ;
	    	}else{
	    	   pack = uc1.getConversionRate();
	    	}
	    }
	   // logger.debug("result calc pack["+pack+"]");
	    return pack;
	}
}
