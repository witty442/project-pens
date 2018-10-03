package com.isecinc.pens.model;

import java.util.Calendar;
import java.util.List;


import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.PriceList;

/**
 * MPriceList Class
 * 
 * @author Aneak.t
 * @version $Id: MPriceList.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MPriceList extends I_Model<PriceList> {

	private static final long serialVersionUID = 8984201463664967119L;

	public static String TABLE_NAME = "m_pricelist";
	public static String COLUMN_ID = "PriceList_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PriceList find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, PriceList.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public PriceList[] search(String whereCause) throws Exception {
		List<PriceList> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, PriceList.class);
		if (pos.size() == 0) return null;
		PriceList[] array = new PriceList[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Get Current PriceList
	 * 
	 * @return
	 * @throws Exception
	 */
	public PriceList getCurrentPriceList(String priceListType) throws Exception {
		String whereCause = "and isactive = 'Y' ";
		whereCause += "  and price_list_type = '" + priceListType + "' ";
		logger.info("xx:"+whereCause);
		PriceList[] pls = search(whereCause);
		if (pls != null) {
			if (pls.length > 0) {
				for (PriceList pl : pls) {
					if (DateToolsUtil.checkStartEnd(pl.getEffectiveDate(), pl.getEffectiveToDate())) return pl;
				}
				return new PriceList();
			} else {
				return new PriceList();
			}
		} else {
			return new PriceList();
		}
	}
	
	public PriceList getMayaPriceList() throws Exception {
		String whereCause = "and isactive = 'Y' ";
		whereCause += "  and name = 'MAYA Pricelist' ";
		logger.info("xx:"+whereCause);
		PriceList[] pls = search(whereCause);
		if (pls != null) {
			if (pls.length > 0) {
				for (PriceList pl : pls) {
					if (DateToolsUtil.checkStartEnd(pl.getEffectiveDate(), pl.getEffectiveToDate())) return pl;
				}
				return new PriceList();
			} else {
				return new PriceList();
			}
		} else {
			return new PriceList();
		}
	}
	
	public PriceList getCurrentPriceListByCustomer(String priceListType,String registerDate) throws Exception {

		String whereCause = "and isactive = 'Y' ";
		whereCause += "  and price_list_type = '" + priceListType + "' ";
		//whereCause += "  and isactive = 'Y' ";
		whereCause += "  AND (register_from_date <= '"+ registerDate +"' AND register_to_date >= '"+ registerDate + "' ";
		whereCause += "  OR  register_from_date <= '"+ registerDate +"' AND register_to_date is null) ";
		
		PriceList[] pls = search(whereCause);
		if (pls != null) {
			if (pls.length > 0) {
				for (PriceList pl : pls) {
					if (DateToolsUtil.checkStartEnd(pl.getEffectiveDate(), pl.getEffectiveToDate())) return pl;
				}
				return new PriceList();
			} else {
				return new PriceList();
			}
		} else {
			return new PriceList();
		}
	}

	/**
	 * Get Current PriceList
	 * 
	 * @return
	 * @throws Exception
	 */
	public PriceList getPriceList(String priceListType, String dateFrom, String dateTo) throws Exception {
		String whereCause = "and isactive = 'Y' ";
		whereCause += "  and price_list_type = '" + priceListType + "' ";
		whereCause += "  and isactive = 'Y' ";

		Calendar plFirst = null;
		Calendar plLast = null;
		Calendar first = dateFrom.length() > 0 ? DateToolsUtil.toCalendar(dateFrom) : null;
		Calendar last = dateTo.length() > 0 ? DateToolsUtil.toCalendar(dateTo) : null;

		boolean usePl = false;
		PriceList[] pls = search(whereCause);
		if (pls != null) {
			if (pls.length > 0) {
				for (PriceList pl : pls) {
					usePl = false;
					if (pl.getEffectiveDate().length() > 0) {
						plFirst = DateToolsUtil.toCalendar(pl.getEffectiveDate());
					} else {
						plFirst = null;
					}
					if (pl.getEffectiveToDate().length() > 0) {
						plLast = DateToolsUtil.toCalendar(pl.getEffectiveToDate());
					} else {
						plLast = null;
					}
					// Check
					if (plFirst != null && plLast == null) {
						if (first != null && first.getTimeInMillis() <= plFirst.getTimeInMillis() && last != null
								&& last.getTimeInMillis() >= plFirst.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() >= plFirst.getTimeInMillis() && last != null
								&& last.getTimeInMillis() >= plFirst.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() >= plFirst.getTimeInMillis() && last == null) {
							usePl = true;
						}
						if (first == null && last != null && last.getTimeInMillis() >= plFirst.getTimeInMillis()) {
							usePl = true;
						}
					}
					if (plFirst == null && plLast != null) {
						if (first != null && first.getTimeInMillis() <= plLast.getTimeInMillis() && last != null
								&& last.getTimeInMillis() <= plLast.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() <= plLast.getTimeInMillis() && last != null
								&& last.getTimeInMillis() >= plLast.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() <= plLast.getTimeInMillis() && last == null) {
							usePl = true;
						}
						if (first == null && last != null && last.getTimeInMillis() <= plLast.getTimeInMillis()) {
							usePl = true;
						}
					}
					if (plFirst != null && plLast != null) {
						if (first != null && first.getTimeInMillis() <= plFirst.getTimeInMillis() && last != null
								&& last.getTimeInMillis() <= plLast.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() <= plFirst.getTimeInMillis() && last != null
								&& last.getTimeInMillis() >= plLast.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() >= plFirst.getTimeInMillis() && last != null
								&& last.getTimeInMillis() >= plLast.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() >= plFirst.getTimeInMillis() && last != null
								&& last.getTimeInMillis() <= plLast.getTimeInMillis()) {
							usePl = true;
						}
						if (first != null && first.getTimeInMillis() >= plFirst.getTimeInMillis()
								&& first.getTimeInMillis() <= plLast.getTimeInMillis() && last != null) {
							usePl = true;
						}
						if (first == null && last != null && last.getTimeInMillis() >= plFirst.getTimeInMillis()
								&& last.getTimeInMillis() <= plLast.getTimeInMillis()) {
							usePl = true;
						}
					}
					if (plFirst == null && plLast == null) usePl = true;
					if (usePl) return pl;
				}
				return new PriceList();
			} else {
				return new PriceList();
			}
		} else {
			return new PriceList();
		}
	}
}
