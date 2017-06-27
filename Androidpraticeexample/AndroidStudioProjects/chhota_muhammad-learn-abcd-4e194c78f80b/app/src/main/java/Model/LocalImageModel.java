package Model;

import android.graphics.Bitmap;

/**
 * Created by chhota on 13-02-2016.
 */
public class LocalImageModel {
    private String ImageName;
    private Bitmap ImageBitMap;
    Character character;

    public LocalImageModel(Character character,String ImageName,Bitmap ImageBitmap){
        this.character=character;
        this.ImageName=ImageName;
        this.ImageBitMap=ImageBitmap;
    }

    public LocalImageModel(String ImageName,Bitmap ImageBitmap){
        this.ImageName=ImageName;
        this.ImageBitMap=ImageBitmap;
    }

    public Character getCharacter(){
        return character;
    }

    public String getImageName(){
        return ImageName;
    }

    public Bitmap getImageBitMap(){
        return ImageBitMap;
    }
}
