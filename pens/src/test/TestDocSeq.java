package test;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MDocSequence;

public class TestDocSeq {
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", new Locale("th", "TH"));

	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		} finally {}
	}

	
	
	
	private int getNextSeq(String salesCode, String docType, int activeUserId) throws Exception {
		Connection conn = null;
		boolean newSeq = false;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			InitialReferences init = new InitialReferences();
			init.init(conn);
			// get order type
			String whereCause = " AND sales_code = 'VAN001' AND doctype_id = 100 ";
			DocSequence[] seq = new MDocSequence().search(whereCause);
			if (seq == null) throw new Exception("No Sequence Initialized...");
			String orderType = seq[0].getOrderType();
			String today = df.format(new Date());
			String[] d1 = today.split("/");
			int nextValue = 0;

			// reset by DD
			if (orderType.equalsIgnoreCase("DD")) {
				if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
						&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
					newSeq = false;
				} else {
					newSeq = true;
				}
			}

			// reset by MM
			if (orderType.equalsIgnoreCase("MM")) {
				if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
						&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
					newSeq = false;
				} else {
					newSeq = true;
				}
			}

			// reset by YY
			if (orderType.equalsIgnoreCase("YY")) {
				if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())) {
					newSeq = false;
				} else {
					newSeq = true;
				}
			}

			if (newSeq) {
				// start new seq with current month/year
				seq[0].setCurrentYear(d1[0]);
				seq[0].setCurrentMonth(String.valueOf(Integer.parseInt(d1[1])));
				seq[0].setCurrentNext(seq[0].getStartNo());
			}
			nextValue = seq[0].getCurrentNext();
			seq[0].setCurrentNext(seq[0].getCurrentNext() + 1);

			// update here
			new MDocSequence().update(seq[0], activeUserId, conn);

			return nextValue;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {}
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}

	private static String processDD(DocSequence seq, int userId, Connection conn) {
		return "";
	}

	private static String processMM(DocSequence seq, int userId, Connection conn) throws Exception {
		String today = df.format(new Date());
		String[] d1 = today.split("/");
		int nextValue = 0;
		if (d1[0].equalsIgnoreCase(seq.getCurrentYear())
				&& Integer.parseInt(d1[1]) == Integer.parseInt(seq.getCurrentMonth())) {
			// use next seq

		} else {
			// start new seq with current month/year
			seq.setCurrentYear(d1[0]);
			seq.setCurrentMonth(String.valueOf(Integer.parseInt(d1[1])));
			seq.setCurrentNext(seq.getStartNo());
		}
		nextValue = seq.getCurrentNext();
		seq.setCurrentNext(seq.getCurrentNext() + 1);
		// update here
		new MDocSequence().update(seq, userId, conn);

		String format = String.format("%s%s%s%s", seq.getCurrentYear(), new DecimalFormat("00").format(Integer
				.parseInt(seq.getCurrentMonth())), seq.getSalesCode(), new DecimalFormat("0000").format(nextValue));

		return format;
	}

	private static String processYY(DocSequence seq, int userId, Connection conn) {
		String today = df.format(new Date());
		String[] d1 = today.split("/");
		if (d1[0].equalsIgnoreCase(seq.getCurrentYear())) {
			// use next seq
			System.out.println(seq.getCurrentNext() + 1);
		} else {
			// start new seq update to table
		}
		return "";
	}

}
