package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.R;

public class UpdateSucessful  extends Activity {
    String fontPath = "fonts/ROCK.TTF";
    public void onCreate(Bundle savedInstanceState) {
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.updatesucessful);
        TextView txt1 = (TextView)findViewById(R.id.textView);
        txt1.setTypeface(tf);

        TextView txt2 = (TextView)findViewById(R.id.textView2);
        txt2.setTypeface(tf);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

        }
        return false;
    }

}
