package com.zipnoticiasec.ap.app.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import me.alexrs.prefs.lib.Prefs;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        TextView contract = findViewById(R.id.intro_contract);
        TextView accept = findViewById(R.id.intro_accept);
        TextView cancel = findViewById(R.id.intro_cancel);

        contract.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, BrowserActivity.class);
            intent.putExtra("source", "Términos y condiciones de privacidad");
            intent.putExtra("url", "http://meinpros.com/zipnoticiasec/terminos.php");
            startActivity(intent);
        });

        accept.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        cancel.setOnClickListener(v -> {
            Prefs.with(this).removeAll();
            finish();
        });
    }
}
