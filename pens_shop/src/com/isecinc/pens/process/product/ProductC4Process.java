package com.isecinc.pens.process.product;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.PriceList;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductC4;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MUOMConversion;
import com.isecinc.pens.process.modifier.ModifierProcess;

/**
 * Product C4 Process Class
 * 
 * @author Atiz.b
 * @version $Id: ProductC4Process.java,v 1.0 28/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ProductC4Process {

	// private Logger logger = Logger.getLogger("PENS");

	/**
	 * Get All Item
	 * 
	 * @param user
	 * @param custmerId
	 * @return
	 * @throws Exception
	 */
	public List<ProductC4> getAllItem(User user, String custmerId) throws Exception {
		List<ProductC4> pos = new ArrayList<ProductC4>();
		UOM baseUOM = null;
		UOM uom = null;
		// UOMConversion baseConversion = null;
		// UOMConversion conversion = null;
		double capacity = 0;
		// double baseRate = 0;
		// double rate = 0;
		Customer customer = null;
		if (custmerId.length() > 0) customer = new MCustomer().find(custmerId);
		try {
			String whereCause = "  and isactive = 'Y' "
					+ "and code in(select product_code from m_barcode )"
					+ "order by code, name ";
			Product[] ps = new MProduct().search(whereCause);
			PriceList pl = new MPriceList().getMayaPriceList(user);
			List<ProductPrice> pps;
			MProductPrice mProductPrice = new MProductPrice();
			ProductC4 c4;
			// MUOMConversion muomConversion = new MUOMConversion();
			ModifierProcess modifierProcess = new ModifierProcess(customer != null ? customer.getTerritory() : "");
			for (Product p : ps) {
				baseUOM = null;
				uom = null;
				// baseConversion = null;
				// conversion = null;
				capacity = 0;

				c4 = new ProductC4(p);
				pps = mProductPrice.lookUp(p.getId(), pl.getId());
				for (ProductPrice pp : pps) {
					if (pp.getUom().getId().equalsIgnoreCase(p.getUom().getId())) {
						c4.setBaseUnitPrice(pp.getPrice());
						baseUOM = pp.getUom();
					} else {
						c4.setSubUnitPrice(pp.getPrice());
						uom = pp.getUom();
					}
				}
				// base uom = EA
				// baseRate = 0;
				// rate = 0;
				// if (baseUOM != null) {
				// baseConversion = muomConversion.getCurrentConversion(c4.getProduct().getId(), baseUOM.getId());
				// baseRate = baseConversion.getConversionRate();
				// logger.debug(baseUOM);
				// logger.debug(baseConversion);
				// }
				// if (uom != null) {
				// conversion = muomConversion.getCurrentConversion(c4.getProduct().getId(), uom.getId());
				// rate = conversion.getConversionRate();
				// logger.debug(uom);
				// logger.debug(conversion);
				// }
				// if (rate == 0) capacity = baseRate;
				// else capacity = baseRate / rate;

				// get UOM Capacity
				capacity = new MUOMConversion().getCapacity(baseUOM, uom, c4.getProduct());

				c4.setCapacity(new Double(capacity).intValue());
				if (c4.getBaseUnitPrice() != 0 || c4.getSubUnitPrice() != 0) pos.add(c4);
				modifierProcess.getModifierLineDescription(c4, user);
				if (c4.getListDescription().size() == 0) {
					c4.setDescriptionSize(1);
					c4.setListDescription(null);
				} else {
					c4.setDescriptionSize(c4.getListDescription().size());
				}
			}
			return pos;
		} catch (Exception e) {
			throw e;
		} finally {}
	}

}
