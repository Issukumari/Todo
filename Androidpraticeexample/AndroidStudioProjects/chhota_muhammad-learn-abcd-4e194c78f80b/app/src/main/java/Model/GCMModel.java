package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chhota on 07-03-2016.
 */
public class GCMModel {
    @SerializedName("EmailId")
    @Expose
    private String EmailId;

    public GCMModel(String emailId, String name, String GCMid) {
        EmailId = emailId;
        Name = name;
        this.GCMid = GCMid;
    }

    @SerializedName("Name")
    @Expose

    private String Name;
    @SerializedName("GCMid")
    @Expose
    private String GCMid;

    /**
     *
     * @return
     * The EmailId
     */
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
     * The GCMid
     */
    public String getGCMid() {
        return GCMid;
    }

    /**
     *
     * @param GCMid
     * The GCMid
     */
    public void setGCMid(String GCMid) {
        this.GCMid = GCMid;
    }
}
