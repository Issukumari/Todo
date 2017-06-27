package com.example.chhota.customized;

/**
 * Created by chhota on 17-02-2016.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class ListenerEditText extends EditText {

    private KeyImeChange keyImeChangeListener;

    public ListenerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event){
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}