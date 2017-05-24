package com.app.todo.todohome.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.archievefragment.ui.ArchieveFragment;
import com.app.todo.base.BaseActivity;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.notes.ui.NotesFragment;
import com.app.todo.notes.ui.OnSearchTextChange;
import com.app.todo.reminderfragment.ui.ReminderFragment;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.presenter.TodoHomepresenter;
import com.app.todo.trash.ui.TrashFragment;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.app.todo.utils.DownloadImageInterface;
import com.app.todo.utils.downloadPic;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, TodoHomeActivityInterface {

    private final int SELECT_PHOTO = 3;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    //   List<TodoHomeDataModel> datamodels = new ArrayList<>();


    // NoteDatabase databse;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    AppCompatTextView Navigationheaderid;
    AppCompatTextView Navigationheadername;
    View hView;
    FirebaseAuth firebaseAuth;
    CircleImageView circleimageview;
    DatabaseReference mfirebasedatabaseref;
    FirebaseDatabase mfirebaseDatabase;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    // String emailid;
    DrawerLayout drawer;
    GoogleSignInOptions googleSignInOptions;
    private String userId;
    private String google_first_name, google_email, google_imageUrl;
    private SharedPreferences sharedPreferences;
    private List<TodoHomeDataModel> allnotes;
    public TodoHomepresenter presenter;
    private OnSearchTextChange searchTagListener;
    private FloatingActionButton fabbutton;
    private String TAG;
    private Uri mPrfilefilePath;
    private SharedPreferences.Editor editor;
    private CommonUtils utils;
    private downloadPic DownloadImage;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new NotesFragment()).commit();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mfirebasedatabaseref = mfirebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        utils = new CommonUtils(this);

        DownloadImage = new downloadPic();
        sharedPreferences = this.getSharedPreferences(Constants.keys, MODE_PRIVATE);
        presenter = new TodoHomepresenter(this, this);
        presenter.getTodoNote(userId);
        initView();
        if (sharedPreferences.getBoolean(Constants.fbloginkeys, false)) {
            fbuser();
        } else if (sharedPreferences.getBoolean(Constants.googleloginkeys, false)) {
            googleuser();
        } else {
            userlogin();
        }
    }

    private void userlogin() {
        String email = sharedPreferences.getString(getString(R.string.email), "");
        String name = sharedPreferences.getString(getString(R.string.name), "");
        Navigationheaderid.setText(email);
        Navigationheadername.setText(name);
        DownloadImage.downloadImage(String.valueOf("myProfiles/" + email.substring(0, email.indexOf("@")) + ".jpg"), new DownloadImageInterface() {
            @Override
            public void getImage(Bitmap bitmap) {
                circleimageview.setImageBitmap(bitmap);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                circleimageview.setImageBitmap(resized);
            }
        });
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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance().getInstance();
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
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    public void setListeners() {

    }

    public void setTitle(String title) {

        toolbar.setTitle(title);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, notefragment).addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "notes  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.Logout:
                deleteAccessToken();
                break;

            case R.id.nav_Reminders:
                setTitle("Reminder");
                ReminderFragment reminderFragment = new ReminderFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, reminderFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "reminder", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
            case R.id.nav_Archive:

                setTitle("Archive");
                ArchieveFragment archieveFragment = new ArchieveFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, archieveFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "Archive  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.nav_Deleted:

                setTitle("Archive");
                TrashFragment trashFragment = new TrashFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, trashFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "trash  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
        }

        return true;
    }

    private void deleteAccessToken() {
        LoginManager.getInstance().logOut();
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                finish();
                Toast.makeText(TodoHomeActivity.this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();

            }

        });

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

        int fragmentCount = getFragmentManager().getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentCount == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }


    @Override
    public void getNoteFailure(String message) {
        // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

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
        }
    }

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
