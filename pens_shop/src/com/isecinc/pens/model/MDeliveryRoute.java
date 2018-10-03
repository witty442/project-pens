package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.DeliveryRoute;

public class MDeliveryRoute extends I_Model<DeliveryRoute> {

	private static final long serialVersionUID = -5364947064917996010L;

	public static String TABLE_NAME = "m_delivery_route";
	public static String COLUMN_ID = "DELIVERY_ROUTE_ID";

	/**
	 * Lookup with district
	 * 
	 * @param districtId
	 * @return
	 * @throws Exception
	 */
	public List<DeliveryRoute> lookUp(int districtId) throws Exception {
		String whereCause = "  AND DISTRICT_ID = " + districtId;
		return search(TABLE_NAME, COLUMN_ID, whereCause, DeliveryRoute.class);
	}
}
