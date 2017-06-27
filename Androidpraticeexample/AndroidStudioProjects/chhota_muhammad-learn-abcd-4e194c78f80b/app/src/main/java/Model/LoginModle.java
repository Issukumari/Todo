package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chhota on 16-02-2016.
 */
public class LoginModle {

    @SerializedName("EmailId")
    @Expose
    private String EmailId;
    @SerializedName("Password")

    @Expose
    private String Password;

    public LoginModle(String EmailId,String Password){
        this.EmailId=EmailId;
        this.Password=Password;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }


}
