package com.isecinc.pens.model;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Adjust;
import com.isecinc.pens.bean.CreditNote;

/**
 * Credit Note Model
 * 
 * @author atiz.b
 * @version $Id: MCreditNote.java,v 1.0 15/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MAdjust extends I_Model<Adjust> {

	private static final long serialVersionUID = -4202991323191680273L;
	public static String TABLE_NAME = "t_adjust";
	public static String COLUMN_ID = "ADJUST_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Adjust find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Adjust.class);
	}

	public double getTotalAdjustAmt (String invoiceNo){
		double totalAdjustAmt = 0;
		List<Adjust> pos = new ArrayList<Adjust>();
		String whereClause = "AND AR_Invoice_No = '"+invoiceNo+"'";
		try {
			pos = super.search(TABLE_NAME, COLUMN_ID, whereClause, Adjust.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Sum Total Amount
		for(Adjust adjusts : pos){
			totalAdjustAmt = totalAdjustAmt + adjusts.getAdjustAmount();
		}
		
		return totalAdjustAmt;
	}
}
