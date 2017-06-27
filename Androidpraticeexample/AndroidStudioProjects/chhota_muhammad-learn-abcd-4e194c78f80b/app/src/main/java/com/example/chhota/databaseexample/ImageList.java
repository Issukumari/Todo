package com.example.chhota.databaseexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.ArrayList;

import Adapter.AddMoreImageAdapter;
import CallBackInterface.CallbackImageList;
import DataLayer.SubtractImageList;
import Model.ImageModel;
import ProjectConstants.Constant;
import webservice.ListGetWebService;

public class ImageList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ListView listView;
    private ProgressDialog dialog;
   public static ArrayList<ImageModel> modellist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_image_list);

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


        listView=(ListView)findViewById(R.id.list1);

            dialog = ProgressDialog.show(ImageList.this, "Please wait...", "Loading list", true);
            dialog.show();
            ListGetWebService.getImageList(new CallbackImageList() {
                @Override
                public void onSuccess(ArrayList<ImageModel> modellist1) {
                    dialog.dismiss();
                    modellist=new SubtractImageList(modellist1,ImageList.this).getSubtractedImageList();
                    if(modellist.size()==0){
                        new AlertDialog.Builder(ImageList.this)
                                .setTitle("No new word")
                                .setMessage("Please try after some time")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();

                                    }

                                })
                                .show();

                    }else {
                        AddMoreImageAdapter adapter = new AddMoreImageAdapter(ImageList.this, modellist);
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    dialog.dismiss();
                    Toast.makeText(ImageList.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
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
                Toast.makeText(ImageList.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_manage) {
            SharedPreferences sharedPreferences=this.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.IsLogin, false);
            editor.commit();
            Intent intent=new Intent(ImageList.this,MainActivity.class);
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
                Toast.makeText(ImageList.this,"Send :"+temp,Toast.LENGTH_LONG).show();

                //Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Toast.makeText(ImageList.this,"Error Sending Invite",Toast.LENGTH_LONG).show();
            }
        }
    }

}
