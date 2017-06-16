package com.app.todo.utils;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.Patterns;
import android.widget.DatePicker;

import com.app.todo.todohome.ui.TodoHomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

/**
 * Created by bridgeit on 22/4/17.
 */
public class CommonUtils {
    private static final String TAG ="Tag" ;
    ProgressDialog mProgressDialog;
    TodoHomeActivity mContext;
    private Bitmap bitmap;

    public CommonUtils(TodoHomeActivity context) {
        this.mContext=context;
        mProgressDialog=new ProgressDialog(mContext);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void showDatePicker(Context context, final AppCompatTextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = "Date" + String.valueOf(year) + "-" + String.valueOf(monthOfYear)
                        + "-" + String.valueOf(dayOfMonth);
                textView.setText(date);
            }
        }, yy, mm, dd);
        datePicker.show();
    }


    public void uploadFile(Bitmap bitmap, String navigationheaderid) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Title", null);

        if (Uri.parse(path) != null) {
            //displaying a progress dialog while upload is going on
            //mProgressDialog.showProgress("Uploading");

            StorageReference riversRef = FirebaseStorage.getInstance().getReference();
            StorageReference ref=riversRef.child("myProfiles/"+navigationheaderid.substring(0,navigationheaderid.indexOf("@"))+".jpg");
            ref.putFile(Uri.parse(path))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //hiding the progress dialog
                           mContext.userLogin();

                            mProgressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //hiding the progress dialog
                          //  mProgressDialog.dismissProgress();
                            mProgressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //displaying percentage in progress dialog
                           // mProgressDialog.showProgress("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            Log.i(TAG, "uploadFile: ");  //you can display an error toast
        }
    }



    }

