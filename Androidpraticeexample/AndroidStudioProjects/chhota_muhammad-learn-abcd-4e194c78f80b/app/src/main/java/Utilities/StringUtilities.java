package Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chhota on 10-02-2016.
 */
public class StringUtilities {

    static Pattern pattern;
    static final String USERNAME_PATTERN = "^[a-z ]{3,15}$";
    static final String MOBILE_PATTERN = "^[0-9]{10,12}$";
   public  static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static  boolean validateuserName(String name){

        Matcher matcher;
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static  boolean validateMobileNo(String name){

        Matcher matcher;
        pattern = Pattern.compile(MOBILE_PATTERN);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

}
