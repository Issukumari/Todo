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
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.ArrayList;
import java.util.Locale;

import Adapter.AdapterDisplayNumber;
import Adapter.AdapterRemaningDisplay;
import DataBaseConnector.DataBaseConnect;
import Model.LocalImageModel;
import ProjectConstants.Constant;
import Utilities.NumberToWord;

public class RemaningDisplay extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {
    ArrayList<LocalImageModel> list;
    DataBaseConnect db;

    GridView gridView;
    ImageView imageView, leftArrow, rightArrow;
    TextView word;
    TextToSpeech textToSpeech;
    public static int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remaning_display);
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
        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));

        getShape();

        textToSpeech = new TextToSpeech(getApplicationContext(), this);
        gridView = (GridView) findViewById(R.id.gridview);
        leftArrow = (ImageView) findViewById(R.id.leftArrow);
        rightArrow = (ImageView) findViewById(R.id.rightArrow);
        leftArrow.setVisibility(View.GONE);
        imageView = (ImageView) findViewById(R.id.bigImage);
        word = (TextView) findViewById(R.id.numberWordBig);
        word.setText(list.get(0).getImageName());
        imageView.setImageBitmap(list.get(0).getImageBitMap());

        AdapterRemaningDisplay adapter = new AdapterRemaningDisplay(RemaningDisplay.this, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                selected = (position + 1);
                if (position == 0)
                    leftArrow.setVisibility(view.GONE);
                else
                    leftArrow.setVisibility(view.VISIBLE);

                if (position == list.size() - 1)
                    rightArrow.setVisibility(view.GONE);
                else
                    rightArrow.setVisibility(view.VISIBLE);

                imageView.setImageBitmap(list.get(position).getImageBitMap());
                word.setText(list.get(position).getImageName());

                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(list.get(position).getImageName(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected--;
                if (selected == 0) {
                    leftArrow.setVisibility(v.GONE);
                }
                rightArrow.setVisibility(v.VISIBLE);
                imageView.setImageBitmap(list.get(selected).getImageBitMap());
                word.setText(list.get(selected).getImageName());

                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(list.get(selected).getImageName(), TextToSpeech.QUEUE_FLUSH, null);

            }

        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected++;
                if (selected == list.size() - 1) {
                    rightArrow.setVisibility(v.GONE);
                }
                leftArrow.setVisibility(v.VISIBLE);
                imageView.setImageBitmap(list.get(selected).getImageBitMap());
                word.setText(list.get(selected).getImageName());

                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(list.get(selected).getImageName(), TextToSpeech.QUEUE_FLUSH, null);

            }


        });

    }

    public Bitmap getImageBitmap(byte imagebyte[]) {
        return BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
    }


    public void getShape() {
        list = new ArrayList<LocalImageModel>();
        db = new DataBaseConnect(this);
        Cursor cursor;
        switch (getIntent().getIntExtra("Position", 0)) {
            case 2:
                cursor = db.geShape();
                break;
            case 3:
                cursor = db.getColor();
                break;
            case 4:
                cursor = db.getFruits();
                break;

            case 5:
                cursor = db.getVehicles();
                break;

            case 6:
                cursor = db.getAnimals();
                break;

            default:
                cursor = db.getColor();
                break;
        }
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(new LocalImageModel(cursor.getString(4), getImageBitmap(cursor.getBlob(5))));
                } while (cursor.moveToNext());
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

            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(RemaningDisplay.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_manage) {
            SharedPreferences sharedPreferences = this.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.IsLogin, false);
            editor.commit();
            Intent intent = new Intent(RemaningDisplay.this, MainActivity.class);
            startActivity(intent);
            this.finish();

        } else if (id == R.id.nav_share) {
            PackageManager pm = getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text = "My App Invite ...";

                PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
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
                String temp = "";
                for (String abc : ids)
                    temp += abc;
                Toast.makeText(RemaningDisplay.this, "Send :" + temp, Toast.LENGTH_LONG).show();

                //Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Toast.makeText(RemaningDisplay.this, "Error Sending Invite", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onInit(int status) {

    }
}
