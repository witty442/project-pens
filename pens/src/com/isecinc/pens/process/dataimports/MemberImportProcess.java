package com.isecinc.pens.process.dataimports;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.dataimports.bean.MemberImport;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MOrder;

/**
 * Member Import Process
 * 
 * @author atiz.b
 * @version $Id: MemberImportProcess.java,v 1.0 17/01/2011 00:00:00 atiz.b Exp $
 * 
 */
public class MemberImportProcess {

	/**
	 * Get member Import
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 */
	public List<MemberImport> getMemberImport(String whereCause) throws Exception {
		List<MemberImport> pos = new ArrayList<MemberImport>();
		Connection conn = null;
		try {
			String w1 = " and code in (select code from i_customer) ";
			w1 += whereCause;
			w1 += " order by cast(code as unsigned) ";
			conn = new DBCPConnectionProvider().getConnection(conn);
			Member[] ms = new MMember().search(w1);
			if (ms == null) return null;
			MemberImport mi;
			MOrder mOrder = new MOrder();
			Order[] ords = null;
			for (Member m : ms) {
				mi = new MemberImport(m);
				// get order
				ords = mOrder.search(" and customer_id = " + m.getId() + " order by order_no desc");
				if (ords == null) {
					mi.setOrderId(null);
				} else {
					mi.setOrderId(Arrays.asList(ords));
				}
				pos.add(mi);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}

}
