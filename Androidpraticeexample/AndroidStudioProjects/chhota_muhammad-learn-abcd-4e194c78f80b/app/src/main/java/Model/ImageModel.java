package Model;

/**
 * Created by chhota on 10-02-2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ImageModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("ImageName")
    @Expose
    private String ImageName;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((ImageModel)obj).getId());
    }

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("imagepath")
    @Expose
    private String imagepath;

    public ImageModel(String id){
        this.id=id;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The ImageName
     */
    public String getImageName() {
        return ImageName;
    }

    /**
     *
     * @param ImageName
     * The ImageName
     */
    public void setImageName(String ImageName) {
        this.ImageName = ImageName;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     * The imagepath
     */
    public String getImagepath() {
        return imagepath;
    }

    /**
     *
     * @param imagepath
     * The imagepath
     */
    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
