package CallBackInterface;

/**
 * Created by chhota on 11-02-2016.
 */
public interface CallbackDownloadImage {
    void onSuccess(byte[] result);
    void onError(String result);
}
