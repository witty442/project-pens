package util;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 //convert yyyy thai to end date
		String mmyyyy ="201906";
		 String mm = mmyyyy.substring(4,6);
		 String yyyy = mmyyyy.substring(0,4);
		 System.out.println(mm+":"+yyyy);
		 mmyyyy = mm+yyyy;
	}

}
