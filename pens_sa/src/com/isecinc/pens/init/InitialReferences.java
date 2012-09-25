package com.isecinc.pens.init;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.isecinc.core.Database;
import com.isecinc.core.bean.References;
import com.isecinc.core.init.I_Initial;

/**
 * Initial References
 * 
 * @author Atiz.b
 * @version $Id: InitialReferences.java,v 1.0 28/09/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InitialReferences extends I_Initial {

	public static final String CUSTOMER_TYPE = "CustomerType";
	public static final String TERRITORY = "Territory";
	public static final String SALES_GROUP = "SalesGroup";
	public static final String ORDER_TYPE = "OrderType";
	public static final String SHIPMENT = "Shipment";
	public static final String MEMBER_TYPE = "MemberType";
	public static final String MEMBER_STATUS = "MemberStatus";
	public static final String ROLE = "Role";
	public static final String VAT_CODE = "VatCode";
	public static final String PAYMENT_METHOD = "PaymentMethod";
	public static final String PAYMENT_TERM = "PaymentTerm";
	public static final String UNCLOSED_REASON = "UnclosedReason";
	public static final String ROLE_TT = "RoleTT";
	public static final String ROLE_VAN = "RoleVAN";
	public static final String ROLE_DD = "RoleDD";
	public static final String ACTIVE = "Active";
	public static final String ALERT_PERIOD = "AlertPeriod";
	public static final String QTY_DELIVER = "QtyDeliver";
	public static final String ROUND_DELIVER = "RoundDeliver";
	public static final String DOC_RUN = "DocRun";
	public static final String DOC_STATUS = "DocStatus";
	public static final String BANK = "Bank";
	public static final String DELIVERY_GROUP = "DeliveryGroup";
	public static final String INTERNAL_BANK = "InternalBank";

	private static Hashtable<String, List<References>> referenes = new Hashtable<String, List<References>>();

	public void init() {}

	/**
	 * init with conn
	 */
	public void init(Connection conn) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM ad_reference WHERE ISACTIVE = 'Y' ");
			List<References> refList = Database.query(sql.toString(), null, References.class, conn);
			// logger.debug(refList);
			for (References r : refList) {
				logger.debug(r);
				if (referenes.get(r.getCode()) == null) {
					List<References> tr = new ArrayList<References>();
					tr.add(r);
					referenes.put(r.getCode(), tr);
				} else {
					referenes.get(r.getCode()).add(r);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public static Hashtable<String, List<References>> getReferenes() {
		return referenes;
	}
}
