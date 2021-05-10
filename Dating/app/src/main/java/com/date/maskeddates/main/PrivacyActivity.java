package com.date.maskeddates.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.date.maskeddates.R;

public class PrivacyActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        textView = (TextView)findViewById(R.id.text);

        String text = textView.getText().toString();

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(text, TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable)textView.getText();
        ClickableSpan myClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                /* do something */
                Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        };

        mySpannable.setSpan(myClickableSpan, textView.getText().toString().length() - 5, textView.getText().toString().length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
