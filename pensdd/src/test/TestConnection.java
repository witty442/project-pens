package test;

import java.sql.Connection;

import util.DBCPConnectionProvider;

public class TestConnection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			if (conn != null) System.out.println("s");
			else System.out.println("f");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
