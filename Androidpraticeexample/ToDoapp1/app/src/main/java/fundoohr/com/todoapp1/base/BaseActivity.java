package fundoohr.com.todoapp1.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bridgeit on 25/3/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setMessage("");
    }

    public abstract void initView();
    public abstract void setClicklistener();


    public void showProgressDialog(String message){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(BaseActivity.this);
        }
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    public void hideProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
