package com.app.todo.utils;

/**
 * Created by bridgeit on 20/5/17.
 */
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.os.Debug;

    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;

        public class DownloadPic {
        public static final String TAG = "DownloadImage";
        public static Bitmap mBitmap;
            private static DownloadImageInterface mImage;
            public DownloadPic() {

            }

            public static void downloadImage(String url, final DownloadImageInterface image){
                mImage=image;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReference().child(url);

            final long IMAGE_SIZE = 1024*1024;
            reference.getActiveDownloadTasks();
            reference.getBytes(IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    if(mImage!=null){
                        mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.getImage(mBitmap);
                        mImage=null;
                    }

                }
            });

        }
    }

