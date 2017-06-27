package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chhota on 16-02-2016.
 */
public class CustomMessageModel {


    @SerializedName("Status")
    @Expose
    private Integer Status;
    @SerializedName("Success")
    @Expose
    private Boolean Success;

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public Boolean getSuccess() {
        return Success;
    }

    public void setSuccess(Boolean success) {
        Success = success;
    }
}
