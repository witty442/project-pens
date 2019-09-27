package test;

import java.sql.Connection;

import com.pens.util.DBConnection;

public class TestConnection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = new DBConnection().getInstance().getConnection();
			if (conn != null) System.out.println("s");
			else System.out.println("f");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
