package util;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 //convert yyyy thai to end date
		String mmyyyy ="01/07/2561";
		 String mm = mmyyyy.substring(3,5);
		 String yyyy = String.valueOf(Integer.parseInt(mmyyyy.substring(6,10))-543);
		 System.out.println(mm+":"+yyyy);
		 mmyyyy = mm+yyyy;
	}

}
