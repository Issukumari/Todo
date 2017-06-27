package fundoohr.com.todoapp1.utils;

import android.util.Patterns;

/**
 * Created by bridgeit on 25/3/17.
 */
public class Utility {
    public static boolean isValidEmail(String email_id) {
        return Patterns.EMAIL_ADDRESS.matcher(email_id).matches();
    }
}
