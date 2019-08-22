package com.galiyara.sandy.galiyara.GActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.galiyara.sandy.galiyara.GDialogs.FolderChooserDialog;
import com.galiyara.sandy.galiyara.GHelper.GAdManager;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import java.util.Objects;

import io.multimoon.colorful.CAppCompatActivity;

public class AboutActivity extends CAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about_activity_title);
        updateInfo();
        GAdManager.DisplayAdds();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else
            return super.onOptionsItemSelected(menuItem);
    }

    private void updateInfo() {
        TextView versionTextView = findViewById(R.id.aboutAppVersion);
        TextView detailsTextView = findViewById(R.id.aboutAppDetail);
        versionTextView.setText(GaliyaraConst.APP_VERSION);
        detailsTextView.setText(R.string.app_detail_msg);
    }
    public void onGithubClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(GaliyaraConst.GITHUB_LINK));
        startActivity(intent);
    }
    public void onBugReportClicked(View view) {
        //
    }
}
