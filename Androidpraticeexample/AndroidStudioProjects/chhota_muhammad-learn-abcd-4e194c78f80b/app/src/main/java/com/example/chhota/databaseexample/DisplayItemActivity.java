package com.example.chhota.databaseexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.ArrayList;
import java.util.Locale;

import Adapter.GallaryAdapter;
import DataBaseConnector.DataBaseConnect;
import Model.AllImageModel;
import Model.LocalImageModel;
import ProjectConstants.AppMessage;
import ProjectConstants.Constant;
import Utilities.InternetConnection;

public class DisplayItemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        TextToSpeech.OnInitListener{
    Gallery gallery,alphabte;
    ImageView selectedImage;
    GallaryAdapter adapter;
    ArrayList<LocalImageModel> imagelist,list;
    private Cursor employees;
    DataBaseConnect db;
    TextToSpeech textToSpeech;
    TextView selectedImageName;
    public  static  final String SELECTED_ALPHBATE="SELECTED_ALPHBATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("Learn ABCD");

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        gallery = (Gallery) findViewById(R.id.gallery);
        alphabte=(Gallery)findViewById(R.id.alphabte);
        selectedImageName=(TextView)findViewById(R.id.selectedImageName);
        selectedImage=(ImageView)findViewById(R.id.imageView);

        initalizeView(getIntent().getStringExtra(SELECTED_ALPHBATE));
        getAlphbate();
        adapter=new GallaryAdapter(DisplayItemActivity.this,list);
        alphabte.setAdapter(adapter);
        alphabte.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                initalizeView(list.get(position).getImageName());

                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(list.get(position).getImageName(), TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        selectedImage.setImageBitmap(imagelist.get(0).getImageBitMap());
        selectedImageName.setText(imagelist.get(0).getImageName());
        adapter=new GallaryAdapter(DisplayItemActivity.this,imagelist);
        gallery.setAdapter(adapter);
        gallery.setSelection(1);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // show the selected Image
                if (position == imagelist.size() - 1) {
                    if (InternetConnection.checkWifiConnection(DisplayItemActivity.this)) {
                        openAddnewImagePage();
                    } else
                        new InternetConnection().noInternetAlertDialog(DisplayItemActivity.this);
                } else {
                    selectedImageName.setText(imagelist.get(position).getImageName());
                    selectedImage.setImageBitmap(imagelist.get(position).getImageBitMap());

                }

            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        initalizeView(getIntent().getStringExtra(SELECTED_ALPHBATE));


        //LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
        //new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null) {
            textToSpeech.stop();
        }

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private  void openAddnewImagePage(){
        Intent intent=new Intent(DisplayItemActivity.this,ImageList.class);
        startActivity(intent);
    }

    private void initalizeView(String alphabate){
        imagelist=new ArrayList<LocalImageModel>();
        db=new DataBaseConnect(this);
        employees = db.getWord(alphabate);
        String result="";
        if(employees.getCount()!=0){
            if(employees.moveToFirst()){
                do{
                    imagelist.add(new  LocalImageModel(employees.getString(4),getImageBitmap(employees.getBlob(5))));
                }while(employees.moveToNext());
            }

        }
        employees.close();
        imagelist.add(new LocalImageModel("More", BitmapFactory.decodeResource(getResources(), R.drawable.more)));

        gallery = (Gallery) findViewById(R.id.gallery);
        adapter=new GallaryAdapter(DisplayItemActivity.this,imagelist);
        gallery.setAdapter(adapter);
        gallery.setSelection(1);
        selectedImage.setImageBitmap(imagelist.get(0).getImageBitMap());
        selectedImageName.setText(imagelist.get(0).getImageName());
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                // show the selected Image
                if (position == imagelist.size() - 1) {
                    if (InternetConnection.checkWifiConnection(DisplayItemActivity.this)) {
                         openAddnewImagePage();
                        adapter.notifyDataSetChanged();
                    } else
                        new InternetConnection().noInternetAlertDialog(DisplayItemActivity.this);
                } else {
                    selectedImageName.setText(imagelist.get(position).getImageName());
                    selectedImage.setImageBitmap(imagelist.get(position).getImageBitMap());
                    if (textToSpeech != null) {
                        textToSpeech.stop();
                    }
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.speak(imagelist.get(position).getImageName(), TextToSpeech.QUEUE_FLUSH, null);

                }

            }
        });
    }

    public Bitmap getImageBitmap(byte imagebyte[]){
        return BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
    }


    public void getAlphbate(){
        list=new ArrayList<LocalImageModel>();
        db=new DataBaseConnect(this);
        Cursor cursor = db.getAlphbate();
        String result="";
        if(cursor.getCount()!=0){
            if(cursor.moveToFirst()){
                do{
                    list.add(new  LocalImageModel(cursor.getString(4),getImageBitmap(cursor.getBlob(5))));
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            String[] TO = {"chhota89@gmail.com"};
            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:" + TO[0]));

            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ABCD app Feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

            try {
                startActivity(emailIntent);
                finish();

            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(DisplayItemActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_manage) {
            SharedPreferences sharedPreferences=this.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.IsLogin, false);
            editor.commit();
            Intent intent=new Intent(DisplayItemActivity.this,MainActivity.class);
            startActivity(intent);
            this.finish();

        } else if (id == R.id.nav_share) {
            PackageManager pm=getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text = "My App Invite ...";

                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp");

                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, "Share with"));

            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (id == R.id.nav_send) {
            Intent intent = new AppInviteInvitation.IntentBuilder("App Invite for MyApp")
                    .setMessage("Message for invite app")
                    .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link))).build();
            startActivityForResult(intent, Constant.REQUEST_INVITE);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == Constant.REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                String temp="";
                for(String abc:ids)
                    temp+=abc+" ";
                Toast.makeText(DisplayItemActivity.this,"Send :"+temp,Toast.LENGTH_LONG).show();

                //Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Toast.makeText(DisplayItemActivity.this,"Error Sending Invite",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onInit(int status) {

    }
}
