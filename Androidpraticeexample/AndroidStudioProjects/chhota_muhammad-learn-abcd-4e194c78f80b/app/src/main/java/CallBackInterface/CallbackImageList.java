package CallBackInterface;

import java.util.ArrayList;

import Model.ImageModel;

/**
 * Created by chhota on 10-02-2016.
 */
public interface CallbackImageList {
     void onSuccess(ArrayList<ImageModel> modelList);
     void onError(String errorMessage);
}
