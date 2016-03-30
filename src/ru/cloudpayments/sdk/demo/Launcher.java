package ru.cloudpayments.sdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.cloudpayments.sdk.demo.buildIn.BuildInActivity;
import ru.cloudpayments.sdk.demo.custom.CustomActivity;


public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }

    public void onBuildInForm(View view) {
        startActivity(new Intent(Launcher.this, BuildInActivity.class));
    }

    public void onCustomForm(View view) {
        startActivity(new Intent(Launcher.this, CustomActivity.class));
    }
}
