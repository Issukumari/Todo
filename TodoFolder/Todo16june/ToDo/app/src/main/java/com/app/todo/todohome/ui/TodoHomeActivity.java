package com.app.todo.todohome.ui;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.app.todo.todonoteaddfragment.ui.TodoNoteaddFragment;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
import com.app.todo.utils.DownloadPic;
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
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, TodoHomeActivityInterface,
        ColorPickerDialogListener {

    private final int SELECT_PHOTO = 3;
    public TodoHomepresenter presenter;
    RecyclerView.LayoutManager layoutManager;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;
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
    DrawerLayout drawer;
    GoogleSignInOptions googleSignInOptions;
    private String userId;
    String strNotesTypes;
    private String google_first_name, google_email, google_imageUrl;
    private SharedPreferences sharedPreferences;
    private OnSearchTextChange searchTagListener;
    private String TAG;
    private Uri mPrfilefilePath;
    ReminderFragment reminderFragment;
    private SharedPreferences.Editor editor;
    private CommonUtils utils;
    private DownloadPic mDownloadImage;
    private GoogleApiClient googleApiClient;
    NotesFragment notesFragment;
    TodoNoteaddFragment todoNoteaddFragment;
    AppCompatTextView Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.navigation_drawer);
        notesFragment = new NotesFragment();
        strNotesTypes = "Notes";
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.frame, notesFragment).commit();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
       // mfirebasedatabaseref = mfirebaseDatabase.getReferenceFromUrl("https://todo-5865c.firebaseio.com/");
        //mfirebasedatabaseref.keepSynced(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        utils = new CommonUtils(this);
        todoNoteaddFragment = new TodoNoteaddFragment();
        Title = (AppCompatTextView) findViewById(R.id.edittext_title);
        mDownloadImage = new DownloadPic();
        sharedPreferences = this.getSharedPreferences(Constants.keys, MODE_PRIVATE);
        presenter = new TodoHomepresenter(this, this);
        presenter.getTodoNote(userId);
        initView();
        if (sharedPreferences.getBoolean(Constants.fbloginkeys, false)) {
            fbUser();
        } else if (sharedPreferences.getBoolean(Constants.googleloginkeys, false)) {
            googleUser();
        } else {
            circleimageview.setOnClickListener(this);
            userLogin();
        }
    }

    public void userLogin() {
        String name = sharedPreferences.getString(getString(R.string.name), "");
        String email = sharedPreferences.getString(getString(R.string.email), "");
        Navigationheadername.setText(name);
        Navigationheaderid.setText(email);
        mDownloadImage.downloadImage(String.valueOf("myProfiles/" + email.substring(0, email.indexOf("@")) + ".jpg"), new DownloadImageInterface() {
            @Override
            public void getImage(Bitmap bitmap) {
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                circleimageview.setImageBitmap(resized);
            }
        });
    }

    private void fbUser() {
        String name = sharedPreferences.getString(getString(R.string.name), "");
        String email = sharedPreferences.getString(getString(R.string.email), "");
        String imageUrl = "http://graph.facebook.com/" + sharedPreferences.getString("id", "") + "/picture?type=large";
        Navigationheadername.setText(name);
        Navigationheaderid.setText(email);
        Glide.with(this).load(imageUrl).into(circleimageview);
    }

    private void googleUser() {
        google_first_name = sharedPreferences.getString(Constants.Name, Constants.values);
        google_email = sharedPreferences.getString(Constants.Email, Constants.values);
        google_imageUrl = sharedPreferences.getString("Pic", "");
        Navigationheadername.setText(google_first_name);
        Navigationheaderid.setText(google_email);
        Glide.with(getApplicationContext()).load(google_imageUrl).into(circleimageview);
    }

    @Override
    public void initView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance().getInstance();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        circleimageview = (CircleImageView) hView.findViewById(R.id.imagenavigation);
        Navigationheadername = (AppCompatTextView) hView.findViewById(R.id.name);

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
public String  getToolbarTitle(){
   return toolbar.getTitle().toString();

}
    public void setData(TodoHomeDataModel mode) {
        recyclerAdapter.addItem(mode);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Notes:
                setTitle("Notes");
                strNotesTypes = "Notes";
                notesFragment = new NotesFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame, notesFragment).
                        addToBackStack(null) .commit();
                Toast.makeText(TodoHomeActivity.this, "notes  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
            case R.id.Logout:
                deleteAccessToken();
                break;

            case R.id.nav_Reminders:
                setTitle("Reminder");
                strNotesTypes = "Reminder";
                reminderFragment = new ReminderFragment(this);
                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                // ft1.setCustomAnimations(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
                ft1.replace(R.id.frame, reminderFragment).addToBackStack(null);
                ft1.commit();
                Toast.makeText(TodoHomeActivity.this, "reminder", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
            case R.id.nav_Archive:

                setTitle("Archive");
                ArchieveFragment archieveFragment = new ArchieveFragment(this);
                getFragmentManager()
                        .beginTransaction()
                        /*.setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_right)*/
                        .replace(R.id.frame, archieveFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "Archive  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.nav_Deleted:

                setTitle("Deleted");
                TrashFragment trashFragment = new TrashFragment(this);
                FragmentTransaction trash = getFragmentManager().beginTransaction();
               /* trash.setCustomAnimations(R.anim.anim_slide_in_from_left, R.anim.anim_slide_out_from_right);*/
                trash.replace(R.id.frame, trashFragment, "trashFragment").addToBackStack(null);
                trash.commit();
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else if (getFragmentManager().getBackStackEntryCount() == 1) {
            getSupportActionBar().show();
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
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
                if (data != null) {
                    mPrfilefilePath =data.getData();
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

        if (requestCode == 2) {
            if (data != null) {
                //addnote=true;
                Bundle bundle = data.getBundleExtra(Constants.KEY_NOTE);
                TodoHomeDataModel todohomedatamodel = new TodoHomeDataModel();
                todohomedatamodel.setTitle(bundle.getString(Constants.Titletext));
                todohomedatamodel.setDescription(bundle.getString(Constants.Desriptiontext));
                todohomedatamodel.setStartdate(bundle.getString(Constants.currentdate));
                todohomedatamodel.setReminderDate(bundle.getString(Constants.reminderKey));
                todohomedatamodel.setRemindertime(bundle.getString(Constants.remindertime));
                todohomedatamodel.setColor(bundle.getString(Constants.color));
                // notesFragment.add(todohomedatamodel);
                if(getToolbarTitle().equals("Notes"))
                {
                    notesFragment.add(todohomedatamodel);
                }
                else{
                    reminderFragment.add(todohomedatamodel);
                }
            }
        }




            if (requestCode == 3) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap cropedPic = extras.getParcelable("data");
                    utils.uploadFile(cropedPic, Navigationheaderid.getText().toString());

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

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        if (strNotesTypes.equals("Notes"))
            notesFragment.setcolor(color);
        else if (strNotesTypes.equals("Reminder")) {

            reminderFragment.setcolor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

}