package com.app.todo.todohome.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.archievefragment.ui.ArchieveFragment;
import com.app.todo.base.BaseActivity;
import com.app.todo.database.NoteDatabase;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.notes.ui.NotesFragment;
import com.app.todo.notes.ui.OnSearchTextChange;
import com.app.todo.reminderfragment.ui.ReminderFragment;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.presenter.TodoHomepresenter;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, TodoHomeActivityInterface {

    private final int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2, SELECT_PHOTO = 3;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();

    DrawerLayout drawer;
    NoteDatabase databse;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    AppCompatTextView Navigationheaderid;
    AppCompatTextView Navigationheadername;
    View hView;
    FirebaseAuth auth;
    CircleImageView circleimageview;
    DatabaseReference mfirebasedatabaseref;
    FirebaseDatabase mfirebaseDatabase;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    private String userId;
    private String google_first_name, google_email, google_imageUrl;
    private SharedPreferences sharedPreferences;
    private List<TodoHomeDataModel> allnotes;
    private TodoHomepresenter presenter;
    private OnSearchTextChange searchTagListener;
    private FloatingActionButton fabbutton;
    private int result_load_img;
    private String TAG;
    private Uri mPrfilefilePath;
    private SharedPreferences.Editor editor;
    private CommonUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new NotesFragment()).addToBackStack(null).commit();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mfirebasedatabaseref = mfirebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        utils=new CommonUtils(this);
        sharedPreferences = this.getSharedPreferences(Constants.keys, MODE_PRIVATE);
        presenter = new TodoHomepresenter(this, this);
        presenter.getTodoNote(userId);
        initView();
        if (sharedPreferences.getBoolean(Constants.fbloginkeys, false)) {
            fbuser();
        } else if (sharedPreferences.getBoolean(Constants.googleloginkeys, false)) {
            googleuser();
        }
    }

    private void fbuser() {
        String name = sharedPreferences.getString(getString(R.string.name), "");
        String email = sharedPreferences.getString(getString(R.string.email), "");
        String imageUrl = "http://graph.facebook.com/" + sharedPreferences.getString("id", "") + "/picture?type=large";
        Navigationheadername.setText(name);
        Navigationheaderid.setText(email);
        Glide.with(this).load(imageUrl).into(circleimageview);
    }

    private void googleuser() {
        google_first_name = sharedPreferences.getString(Constants.Name, Constants.values);
        google_email = sharedPreferences.getString(Constants.Email, Constants.values);
        google_imageUrl = sharedPreferences.getString("Pic", "");
        Navigationheadername.setText(google_first_name);
        Navigationheaderid.setText(google_email);
        Glide.with(getApplicationContext()).load(google_imageUrl).into(circleimageview);
    }

    @Override
    public void initView() {
        fabbutton = (FloatingActionButton) findViewById(R.id.fabbutton);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance().getInstance();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        circleimageview = (CircleImageView) hView.findViewById(R.id.imagenavigation);
        Navigationheadername = (AppCompatTextView) hView.findViewById(R.id.name);
        circleimageview.setOnClickListener(this);
        Navigationheaderid = (AppCompatTextView) hView.findViewById(R.id.Navigationheaderid);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open_navigation_drawer, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public void setListeners() {

    }


    public void setData(TodoHomeDataModel mode) {
        recyclerAdapter.addItem(mode);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Notes:
                setTitle("Notes");
                NotesFragment notefragment = new NotesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, notefragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "notes  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.Logout:
                deleteAccessToken();
                break;

            case R.id.nav_Reminders:
                getSupportFragmentManager().popBackStackImmediate();
                setTitle("Reminder");
                ReminderFragment reminderFragment = new ReminderFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, reminderFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "reminder", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
            case R.id.nav_Archive:

                getSupportFragmentManager().popBackStackImmediate();
                setTitle("Archive");
                ArchieveFragment archieveFragment = new ArchieveFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, archieveFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "Archive  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

        }

        return true;
    }

    private void deleteAccessToken() {
        auth.signOut();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putBoolean(Constants.fbloginkeys, false);
        editor.putBoolean(Constants.googleloginkeys, false);
        editor.apply();
        Intent intent = new Intent(TodoHomeActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void getNoteFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showProgressDialog(String message) {
        if (!isFinishing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (!isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void getNotesListSuccess(List<TodoHomeDataModel> modelList) {

    }

    @Override
    public void deleteTodoHomeaModelSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLongClick(TodoHomeDataModel datmodels) {

    }

    public void setSearchTagListener(OnSearchTextChange searchTagListener) {
        this.searchTagListener = searchTagListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imagenavigation:
                Intent picker = new Intent();
                picker.setType("image/*");
                picker.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(picker, String.valueOf(R.string.select_pick)), SELECT_PHOTO);
                break;
        }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_PHOTO) {
                    // Get the url from data
                    if (data != null) {
                        mPrfilefilePath = data.getData();
                        if (null != mPrfilefilePath) {
                            Log.i(TAG, "onActivityResult: " + mPrfilefilePath);
                            cropCapturedImage(mPrfilefilePath);
                            editor = sharedPreferences.edit();
                            editor.putString(Constants.USER_PROFILE_LOCAL, String.valueOf(mPrfilefilePath));
                            editor.putString(Constants.USER_PROFILE_SERVER, getString(R.string.flag_true));
                            editor.commit();

                        }
                    }
                }
            }
        if (requestCode == 3) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap cropedPic = extras.getParcelable("data");
                utils.uploadFile(cropedPic, Navigationheaderid.getText().toString());
                // Bitmap scaled = Bitmap.createScaledBitmap(cropedPic, 100, 100, true);
                // Bitmap conv_bm = getRoundedRectBitmap(scaled, 3000);
                circleimageview.setImageBitmap(cropedPic);

            }
        }
        }
    private void cropCapturedImage(Uri prfilefilePath) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(prfilefilePath, "image/*");
        cropIntent.putExtra("crop", getString(R.string.flag_true));
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, 3);
    }

}
    /*   public void image(int requestCode,int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == PICK_FROM_CAMERA)
            onSelectFromGalleryResult(data);
        else if (requestCode == PICK_FROM_GALLERY)
            onCaptureImageResult(data);
        super.onActivityResult(requestCode, resultCode, data);

    }
}*/

    /*private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        circleimageview.setImageBitmap(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        circleimageview.setImageBitmap(bitmap);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imagenavigation:

                Imagecapture();


                break;

        }
    }

    void Imagecapture() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"camera", " Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("camera")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } else if (options[item].equals("Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_FROM_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission fail", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {    super.onActivityResult(requestCode, resultCode, data);
        String Title = "";
        String contentData = "";
        String recentTimeData = "";
        Log.i(TAG, "onActivityResult: " + requestCode + "  " + resultCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_FROM_GALLERY) {
                uri = data.getData();
                ImageCropFunction();
            }
            else if (requestCode == PICK_FROM_CAMERA) {
                uri = data.getData();
                ImageCropFunction();        }
            else if (requestCode == CROP_IMAGE) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                }
            }}}}*/