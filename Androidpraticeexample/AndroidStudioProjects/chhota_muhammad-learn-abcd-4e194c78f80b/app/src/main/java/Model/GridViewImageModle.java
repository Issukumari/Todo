package Model;

/**
 * Created by chhota on 19-02-2016.
 */
public class GridViewImageModle {
    Integer image;
    String imageName;

    public GridViewImageModle(Integer image,String imageName){
        this.image=image;
        this.imageName=imageName;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
