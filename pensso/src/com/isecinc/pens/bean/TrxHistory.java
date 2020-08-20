package com.isecinc.pens.bean;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MUser;
import com.pens.util.DateToolsUtil;

/**
 * Trx History
 * 
 * @author Atiz.b
 * @version $Id: TrxHistory.java,v 1.0 15/11/2010 14:00:00 Atiz.b Exp $
 */
public class TrxHistory extends I_PO {

	private static final long serialVersionUID = 2055215539014325397L;

	public static final String MOD_CUSTOMER = "Customer";
	public static final String MOD_MEMBER = "Member";
	public static final String MOD_ORDER = "SalesOrder";
	public static final String MOD_RECEIPT = "Receipt";
	public static final String MOD_VISIT = "Visit";

	public static final String TYPE_INSERT = "I";
	public static final String TYPE_UPDATE = "U";
	public static final String TYPE_DELETE = "D";

	/**
	 * Default Constructor
	 */
	public TrxHistory() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public TrxHistory(ResultSet rst) throws Exception {
		setId(rst.getInt("TRX_HIST_ID"));
		setTrxModule(rst.getString("TRX_MODULE").trim());
		setTrxType(rst.getString("TRX_TYPE").trim());
		setRecordId(rst.getLong("RECORD_ID"));
		setUser(new MUser().find(rst.getString("USER_ID")));
		setTrxDate(DateToolsUtil.convertFromTimestamp(rst.getTimestamp("TRX_DATE")));
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {

	}

	/** TRX_HIST_ID */
	private int id;

	/** TRX_MODULE */
	private String trxModule;

	/** TRX_TYPE */
	private String trxType;

	/** RECORD_ID */
	private long recordId;

	/** USER_ID */
	private User user;

	/** TRX_DATE */
	private String trxDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTrxModule() {
		return trxModule;
	}

	public void setTrxModule(String trxModule) {
		this.trxModule = trxModule;
	}

	public String getTrxType() {
		return trxType;
	}

	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public String getTrxDate() {
		return trxDate;
	}

	public void setTrxDate(String trxDate) {
		this.trxDate = trxDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
