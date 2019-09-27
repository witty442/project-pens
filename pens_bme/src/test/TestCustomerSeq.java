package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.pens.util.DBConnection;

public class TestCustomerSeq {

	private static String DELETE_C_SEQUENCE = "delete from c_customer_sequence ";

	private static String SELECT_USER = "select distinct(user_id) from m_customer where customer_type <> 'DD' ";

	private static String VIEW_MAX_SEQ = "select TERRITORY, PROVINCE, DISTRICT, max(CURRENT)+10 as CURRENT_NEXT, \r\n"
			+ " 1 as UPDATED_BY,CURRENT_TIMESTAMP as UPDATED \r\n" + "from( \r\n"
			+ " select code,left(code,1) as TERRITORY,mid(code,2,2)as PROVINCE, \r\n"
			+ "  mid(code,4,2) as DISTRICT, cast(right(code,4)as unsigned) as CURRENT \r\n"
			+ " from m_customer where user_id = ? \r\n" + " order by code \r\n" + ") A \r\n"
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

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		String sql;
		try {
			conn = DBConnection.getInstance().getConnection();
			System.out.println("---------------------------");
			System.out.println("begin!");
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			// GET USER_ID
			System.out.println("---------------------------");
			System.out.println("Get User from m_customer");
			System.out.println("---------------------------");
			sql = SELECT_USER;
			rst = stmt.executeQuery(sql);
			List<Integer> userIds = new ArrayList<Integer>();
			while (rst.next()) {
				userIds.add(rst.getInt("USER_ID"));
			}
			if (userIds.size() == 0) return;

			// DELETE C_CUSTOMER_SEQUENCE
			System.out.println("delete all c_customer_sequence");
			System.out.println("---------------------------");
			sql = DELETE_C_SEQUENCE;
			stmt.execute(sql);

			System.out.println("alter auto Increatemental to c_customer_sequence");
			System.out.println("---------------------------");
			sql = ALTER_INCREAMENTAL;
			stmt.execute(sql);

			// VIEW MAX CUSTOMER_SEQUENCE FROM CODE
			System.out.println("View max sequence from customer code");
			System.out.println("---------------------------");
			for (Integer id : userIds) {
				System.out.println(String.format("UserID:[%s]", id));
				sql = VIEW_MAX_SEQ;
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, id);
				rst = pstmt.executeQuery();
				while (rst.next()) {
					System.out.println(String.format("T[%s] P[%s] D[%s] N[%s] ", rst.getInt("TERRITORY"), rst
							.getString("PROVINCE"), rst.getString("DISTRICT"), rst.getInt("CURRENT_NEXT")));

				}
				System.out.println("---------------------------");
				System.out.println("insert to table c_customer_sequence");
				System.out.println("---------------------------");
				sql = INSERT_MAX_SEQ;
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, id);
				pstmt.execute();
			}

			System.out.println("alter not Increatemental to c_customer_sequence");
			System.out.println("---------------------------");
			sql = ALTER_NOT_INCREAMENTAL;
			stmt.execute(sql);

			System.out.println("delete c_customer_sequence from c_sequence");
			System.out.println("---------------------------");
			sql = DELETE_FROM_SEQUENCE;
			stmt.execute(sql);

			System.out.println("insert c_customer_sequence to c_sequence");
			System.out.println("---------------------------");
			sql = INSERT_TO_SEQUENCE;
			stmt.execute(sql);

			System.out.println("commit!");
			System.out.println("---------------------------");
			conn.commit();

			return;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception x) {}
			System.out.println("fail!");
			System.out.println("---------------------------");
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				rst.close();
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
