package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chhota on 18-02-2016.
 */
public class RegisterModel {
    @SerializedName("EmailId")
    @Expose
    private String EmailId;
    @SerializedName("Password")
    @Expose
    private String Password;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("MobileNo")
    @Expose
    private String MobileNo;

    /**
     *
     * @return
     * The EmailId
     */
    public  RegisterModel(String emailId,String password,String name,String mobileNo)
    {
        this.EmailId=emailId;
        this.Password=password;
        this.Name=name;
        this.MobileNo=mobileNo;
    }
    public String getEmailId() {
        return EmailId;
    }

    /**
     *
     * @param EmailId
     * The EmailId
     */
    public void setEmailId(String EmailId) {
        this.EmailId = EmailId;
    }

    /**
     *
     * @return
     * The Password
     */
    public String getPassword() {
        return Password;
    }

    /**
     *
     * @param Password
     * The Password
     */
    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     *
     * @return
     * The Name
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param Name
     * The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     *
     * @return
     * The MobileNo
     */
    public String getMobileNo() {
        return MobileNo;
    }

    /**
     *
     * @param MobileNo
     * The MobileNo
     */
    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
    }
}
