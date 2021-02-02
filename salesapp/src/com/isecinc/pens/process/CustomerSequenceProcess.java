package com.isecinc.pens.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CustomerSequence;
import com.isecinc.pens.model.MCustomerSequence;
import com.pens.util.DBCPConnectionProvider;

/**
 * CustomerSequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: CustomerSequenceProcess.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class CustomerSequenceProcess {

	private static Logger logger = Logger.getLogger("PENS");

	private static String DELETE_C_SEQUENCE = "delete from c_customer_sequence ";

	private static String SELECT_USER = "select distinct(user_id) from m_customer where customer_type = 'CV' ";

	private static String VIEW_MAX_SEQ = "select TERRITORY, PROVINCE, DISTRICT, max(CURRENT)+10 as CURRENT_NEXT, \r\n"
			+ " 1 as UPDATED_BY,CURRENT_TIMESTAMP as UPDATED \r\n" + "from( \r\n"
			+ " select code,left(code,1) as TERRITORY,mid(code,2,2)as PROVINCE, \r\n"
			+ "  mid(code,4,2) as DISTRICT, cast(right(code,4)as unsigned) as CURRENT \r\n"
			+ " from m_customer where user_id = ? \r\n" + " and code not in('V203') order by code \r\n" + ") A \r\n"
			+ " group by TERRITORY, PROVINCE, DISTRICT \r\n";

	private static String INSERT_MAX_SEQ = "INSERT INTO c_customer_sequence \r\n"
			+ "(TERRITORY, PROVINCE, DISTRICT, CURRENT_NEXT, UPDATED_BY, UPDATED) \r\n"
			+ "select TERRITORY, PROVINCE, DISTRICT, max(CURRENT)+10 as CURRENT_NEXT, \r\n"
			+ "  1 as UPDATED_BY,CURRENT_TIMESTAMP as UPDATED \r\n" + "from( \r\n"
			+ "select code,left(code,1) as TERRITORY,mid(code,2,2)as PROVINCE, \r\n"
			+ "  mid(code,4,2) as DISTRICT, cast(right(code,4)as unsigned) as CURRENT \r\n"
			+ "from m_customer where user_id = ? \r\n" + "order by code \r\n" + ") A \r\n"
			+ "group by TERRITORY, PROVINCE, DISTRICT ";

	private static String ALTER_INCREAMENTAL = "ALTER TABLE c_customer_sequence CHANGE CUSTOMER_SEQUENCE_ID CUSTOMER_SEQUENCE_ID INT(11) AUTO_INCREMENT NOT NULL";

	private static String ALTER_NOT_INCREAMENTAL = "ALTER TABLE c_customer_sequence CHANGE CUSTOMER_SEQUENCE_ID CUSTOMER_SEQUENCE_ID INT(11) NOT NULL";

	private static String DELETE_FROM_SEQUENCE = "delete from c_sequence where name = 'c_customer_sequence' ";

	private static String INSERT_TO_SEQUENCE = "insert into c_sequence(name,active,startno,nextvalue) "
			+ "values('c_customer_sequence','Y',1,(select max(customer_sequence_id)+10  from c_customer_sequence))";

	public List<CustomerSequence> updateSequence() throws Exception {
		List<CustomerSequence> pos = new ArrayList<CustomerSequence>();
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		String sql;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);

			logger.debug("---------------------------");
			logger.debug("begin!");
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			// GET USER_ID
			logger.debug("---------------------------");
			logger.debug("Get User from m_customer");
			logger.debug("---------------------------");
			sql = SELECT_USER;
			rst = stmt.executeQuery(sql);
			List<Integer> userIds = new ArrayList<Integer>();
			while (rst.next()) {
				userIds.add(rst.getInt("USER_ID"));
			}
			if (userIds.size() == 0) return null;

			// DELETE C_CUSTOMER_SEQUENCE
			logger.debug("delete all c_customer_sequence");
			logger.debug("---------------------------");
			sql = DELETE_C_SEQUENCE;
			stmt.execute(sql);

			logger.debug("alter auto Increatemental to c_customer_sequence");
			logger.debug("---------------------------");
			sql = ALTER_INCREAMENTAL;
			stmt.execute(sql);

			// VIEW MAX CUSTOMER_SEQUENCE FROM CODE
			logger.debug("View max sequence from customer code");
			logger.debug("---------------------------");
			for (Integer id : userIds) {
				logger.debug(String.format("UserID:[%s]", id));
				sql = VIEW_MAX_SEQ;
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, id);
				rst = pstmt.executeQuery();
				while (rst.next()) {
					logger.debug(String.format("T[%s] P[%s] D[%s] N[%s] ", rst.getInt("TERRITORY"), rst
							.getString("PROVINCE"), rst.getString("DISTRICT"), rst.getInt("CURRENT_NEXT")));

				}
				logger.debug("---------------------------");
				logger.debug("insert to table c_customer_sequence");
				logger.debug("---------------------------");
				sql = INSERT_MAX_SEQ;
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, id);
				pstmt.execute();
			}

			logger.debug("alter not Increatemental to c_customer_sequence");
			logger.debug("---------------------------");
			sql = ALTER_NOT_INCREAMENTAL;
			stmt.execute(sql);

			logger.debug("delete c_customer_sequence from c_sequence");
			logger.debug("---------------------------");
			sql = DELETE_FROM_SEQUENCE;
			stmt.execute(sql);

			logger.debug("insert c_customer_sequence to c_sequence");
			logger.debug("---------------------------");
			sql = INSERT_TO_SEQUENCE;
			stmt.execute(sql);

			logger.debug("commit!");
			logger.debug("---------------------------");
			conn.commit();

			logger.debug("get Result!");
			logger.debug("---------------------------");
			// get c_customer_sequence
			CustomerSequence[] cs = new MCustomerSequence().search(" order by " + MCustomerSequence.COLUMN_ID);
			pos = Arrays.asList(cs);
			return pos;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception x) {}
			logger.error("fail!");
			logger.error("---------------------------");
			logger.error(e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				pstmt.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
}
