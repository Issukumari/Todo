package com.example.chhota.databaseexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.Locale;

import Adapter.AdapterDisplayNumber;
import ProjectConstants.Constant;
import Utilities.NumberToWord;

public class DisplayNumber extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,TextToSpeech.OnInitListener {
    GridView gridView;
    TextToSpeech textToSpeech;

    ImageView leftArrow, rightArrow;
    TextView number, word;
    public static int selected = 0;
    public int size=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_number);
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

        gridView = (GridView) findViewById(R.id.gridview);
        leftArrow = (ImageView) findViewById(R.id.leftArrow);
        rightArrow = (ImageView) findViewById(R.id.rightArrow);

        leftArrow.setVisibility(View.GONE);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView number, word;
                number = (TextView) findViewById(R.id.numberBig);
                word = (TextView) findViewById(R.id.numberWordBig);
                selected--;
                if (selected == 1) {
                    leftArrow.setVisibility(v.GONE);
                }
                rightArrow.setVisibility(v.VISIBLE);
                number.setText("" + selected);
                word.setText(new NumberToWord().convertNumberToWords(selected));

                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(new NumberToWord().convertNumberToWords(selected), TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView number, word;
                number = (TextView) findViewById(R.id.numberBig);
                word = (TextView) findViewById(R.id.numberWordBig);
                selected++;
                if (selected == size) {
                    rightArrow.setVisibility(v.GONE);
                }

                leftArrow.setVisibility(v.VISIBLE);
                number.setText("" + selected);
                word.setText(new NumberToWord().convertNumberToWords(selected));
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(new NumberToWord().convertNumberToWords(selected), TextToSpeech.QUEUE_FLUSH, null);

            }


        });
        number = (TextView) findViewById(R.id.numberBig);
        word = (TextView) findViewById(R.id.numberWordBig);
        number.setText("" + 1);
        word.setText(new NumberToWord().convertNumberToWords(1));

        int number[] = new int[size];
        for (int i = 0; i < number.length; i++)
            number[i] = i + 1;
        AdapterDisplayNumber adapter = new AdapterDisplayNumber(DisplayNumber.this, number);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position==0)
                    leftArrow.setVisibility(view.GONE);
                else
                    leftArrow.setVisibility(view.VISIBLE);

                if(position==size-1)
                    rightArrow.setVisibility(view.GONE);
                else
                    rightArrow.setVisibility(view.VISIBLE);

                TextView number, word;
                number = (TextView) findViewById(R.id.numberBig);
                word = (TextView) findViewById(R.id.numberWordBig);
                number.setText("" + ((position) + 1));
                selected = (position + 1);
                word.setText(new NumberToWord().convertNumberToWords(position + 1));
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech.setLanguage(Locale.UK);
                textToSpeech.speak(new NumberToWord().convertNumberToWords(position + 1), TextToSpeech.QUEUE_FLUSH, null);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        selected = 1;
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        selected = 1;
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }


    @Override
    public void onBackPressed() {
        selected = 1;
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_number, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                Toast.makeText(DisplayNumber.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_manage) {
            SharedPreferences sharedPreferences = this.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.IsLogin, false);
            editor.commit();
            Intent intent = new Intent(DisplayNumber.this, MainActivity.class);
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
                Toast.makeText(DisplayNumber.this, "Send :" + temp, Toast.LENGTH_LONG).show();

                //Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Toast.makeText(DisplayNumber.this, "Error Sending Invite", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onInit(int status) {

    }
}
