package bridgelabz.app.com.firebasedatabase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bridgelabz.app.com.firebasedatabase.base.BaseActivity;
public  class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    ProgressDialog progressDialog;
    AppCompatButton buttonCreateAccount;
    private FirebaseAuth auth;
    Pattern EmailPattern, Passwordpattern;
    Matcher mMatcher, matcher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.login);
        initView();
        if (isNetworkConnected()) {

        }else{
            new AlertDialog.Builder(this)
                    .setTitle("connection is loose")
                    .setMessage(" May be your internet connection is off. Please turn it on " +
                            " and try again ")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }

    @Override
    public void initView() {
        editTextEmailLogin = (AppCompatEditText) findViewById(R.id.editTextEmaillogin);
        editTextPasswordLogin = (AppCompatEditText) findViewById(R.id.editTextPasswordlogin);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonCreateAccount = (AppCompatButton) findViewById(R.id.buttonCreateAccount);
        setListeners();

    }

    @Override
    public void setListeners() {
        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                //validate();
                userLogin();
                break;

            case R.id.buttonCreateAccount:
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void userLogin() {
        String Email=editTextEmailLogin.getText().toString();
        String Password=editTextPasswordLogin.getText().toString();
        auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(getApplicationContext(),TodoNotesActivity.class));
                }
            }
        });
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    public boolean isNetworkConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}




   /* public void validate() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        String email_login = sharedPreferences.getString("email", Constants.values);
        String password_login = sharedPreferences.getString("password", Constants.values);
        EmailPattern = Pattern.compile(Constants.EMAIL_PATTERN);
        mMatcher = EmailPattern.matcher(editTextEmailLogin.getText().toString());

        Passwordpattern = Pattern.compile(Constants.Password_Pattern);
        matcher = Passwordpattern.matcher(editTextPasswordLogin.getText().toString());

        if (editTextEmailLogin.getText().toString().length() == 0) {
            editTextEmailLogin.setError(getString(R.string.Emailid_should_not_empty));
            editTextEmailLogin.requestFocus();
        } else if (mMatcher.matches()) {

        } else {
            editTextEmailLogin.setError(getString(R.string.invalid_Emailid));
            editTextEmailLogin.requestFocus();
        }

        if (editTextPasswordLogin.getText().toString().length() == 0) {
            editTextPasswordLogin.setError(getString(R.string.Password_should_not_empty));
            editTextPasswordLogin.requestFocus();
        } else if (matcher.matches()) {


        } else {
            editTextPasswordLogin.setError("invalid Password");
            editTextPasswordLogin.requestFocus();
        }
        if (editTextEmailLogin.getText().toString().equalsIgnoreCase(email_login) && editTextPasswordLogin.getText().toString().equalsIgnoreCase(password_login)) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("islogin", true);
            editor.commit();
            Intent intent = new Intent(LoginActivity.this, TodoHomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, R.string.details, Toast.LENGTH_LONG).show();
        }
*/



