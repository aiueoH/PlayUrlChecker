package com.dowob.marketurlchecker;

import android.app.usage.NetworkStats;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText urlEt;
    private Button openBtn;
    private Button resetBtn;
    private Button pasteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlEt = (EditText) findViewById(R.id.et_url);
        openBtn = (Button) findViewById(R.id.btn_open);
        resetBtn = (Button) findViewById(R.id.btn_reset);
        pasteBtn = (Button)  findViewById(R.id.btn_paste);

        openBtn.setOnClickListener(this::onClickOpenBtn);
        resetBtn.setOnClickListener(this::onClickResetBtn);
        pasteBtn.setOnClickListener(this::onClickPasteBtn);

    }

    private void onClickOpenBtn(View v) {
        String url = urlEt.getText().toString();
        open(url);
    }

    private void onClickResetBtn(View v) {
        urlEt.setText("");
    }

    private void onClickPasteBtn(View v) {
        ClipboardManager plaster = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = plaster.getPrimaryClip();
        if (clipData.getItemCount() < 1) return;
        CharSequence s = clipData.getItemAt(0).getText();
        urlEt.setText(s);
    }

    private void open(String url) {
        if (!isNetworkOk()) {
            Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            String pattern = "market://details";
            if (url.length() < pattern.length()) {
                // wrong length
                Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show();
                return;
            }
            String urlHead = url.substring(0, pattern.length());
            if (!urlHead.equals(pattern)) {
                // invalid url
                Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Cannot find Google Play on this device", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkOk() {
        ConnectivityManager connectivityMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
        if (networkInfo == null) return false;
        return networkInfo.isConnected();
    }
}
