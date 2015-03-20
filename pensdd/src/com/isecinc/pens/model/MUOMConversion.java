package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.UOMConversion;

public class MUOMConversion extends I_Model<UOMConversion> {

	private static final long serialVersionUID = 3772548485733964501L;

	public static String TABLE_NAME = "m_uom_conversion";

	/**
	 * Get Current Conversion
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public UOMConversion getCurrentConversion(int productId, String uomId) throws Exception {
		logger.debug("Find UOM conversion on Product : " + productId + " : " + uomId);
		String whereCause = "";
		whereCause += "  and product_id = " + productId;
		whereCause += "  and uom_id = '" + uomId + "' ";
		whereCause += "  and (disable_date is null or date_format(disable_date,'%Y%m%d') >= date_format(current_timestamp,'%Y%m%d')) ";
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
			logger.debug(baseUOM);
			logger.debug(baseConversion);
		}
		if (uom != null) {
			UOMConversion conversion = new MUOMConversion().getCurrentConversion(product.getId(), uom.getId());
			rate = conversion.getConversionRate();
			logger.debug(uom);
			logger.debug(conversion);
		}
		if (rate == 0) capacity = baseRate;
		else capacity = baseRate / rate;
		return capacity;
	}
}
