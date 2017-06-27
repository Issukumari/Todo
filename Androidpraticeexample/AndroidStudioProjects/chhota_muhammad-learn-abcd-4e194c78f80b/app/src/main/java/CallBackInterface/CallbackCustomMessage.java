package CallBackInterface;

import Model.CustomMessageModel;

/**
 * Created by chhota on 16-02-2016.
 */
public interface CallbackCustomMessage {
    void onSuccess(CustomMessageModel message);
    void onError(String error);
}
