package fr.rocknscrum.liseronmobile.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.regex.Pattern;

public class ToolsString {

	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
			"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
					"\\@" +
					"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
					"(" +
					"\\." +
					"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
					")+"
			);

	public static boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}


	/**
	 * @author Mathis
	 * @param s : input string to test
	 * @return boolean : true if the input string is null or empty, false otherwise
	 */
	public static boolean isNullOrempty(String s)
	{
		if(s == null || s.compareTo("")==0)
			return true;
		else return false;		
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e){}
		return false;
	}

	public static boolean isFloat(String str) {
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException e){}
		return false;
	}

	/**
	 * @author Mathis
	 * @param s : string to modify
	 * @return String : the input string with the first letter in uppercase
	 */
	public static String toFirstUpperCase(String s)
	{
		if(isNullOrempty(s))
			return s;
		else {			
			return s.substring(0,1).toUpperCase()+s.substring(1);
		}
	}

	public static String hashPassword(String data)
	{
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(data.getBytes());
			byte byteData[] = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			return data;
		}
	}

	public static String getTwoDigitNumber(int i) {

		if(i<10)
			return "0"+(i);
		else return i+"";

	}

	public static String getCurrentDateString() {

		Calendar c = Calendar.getInstance();
		return ToolsString.getTwoDigitNumber(c.get(Calendar.DAY_OF_MONTH))+"/"+ToolsString.getTwoDigitNumber(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR)+"-"+ToolsString.getTwoDigitNumber(c.get(Calendar.HOUR_OF_DAY))+":"+ToolsString.getTwoDigitNumber(c.get(Calendar.MINUTE))+":"+ToolsString.getTwoDigitNumber(c.get(Calendar.SECOND));
	}




}
