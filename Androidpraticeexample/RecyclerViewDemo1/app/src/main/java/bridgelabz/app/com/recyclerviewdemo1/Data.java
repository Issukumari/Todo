package bridgelabz.app.com.recyclerviewdemo1;

/**
 * Created by bridgeit on 28/3/17.
 */
public class Data {

    String name;
    String version;
    int id_;
    int image;

    public Data(String name, String version, int id_, int image) {
        this.name = name;
        this.version = version;
        this.id_ = id_;
        this.image=image;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int getImage() {
        return image;
    }

    public int getId() {
        return id_;
    }
}