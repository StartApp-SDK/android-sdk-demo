package com.example.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressLint("SetTextI18n")
public class OtherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);

        // TODO this activity does nothing here
        //      it's designed as an example for interstitial ad

        FrameLayout root = new FrameLayout(this);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;

        Button button = new Button(this);
        button.setLayoutParams(lp);
        button.setText("Back");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        root.addView(button);

        setContentView(root);
    }
}
