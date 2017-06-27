package Model;

import android.graphics.Bitmap;

/**
 * Created by chhota on 21-02-2016.
 */

public class AllImageModel {
    Bitmap image;
    String imageName;

    public AllImageModel(Bitmap image,String imageName){
        this.image=image;
        this.imageName=imageName;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
