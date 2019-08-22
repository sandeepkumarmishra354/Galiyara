package com.galiyara.sandy.galiyara.GActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.galiyara.sandy.galiyara.GDialogs.LockDisableDialog;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GDatabase.GDBCrud;
import com.galiyara.sandy.galiyara.GDatabase.GeneralSettingEntity;
import com.galiyara.sandy.galiyara.GDatabase.SecuritySettingEntity;
import com.galiyara.sandy.galiyara.GDialogs.ColorChooserDialog;
import com.galiyara.sandy.galiyara.GDialogs.LanguageDialog;
import com.galiyara.sandy.galiyara.GDialogs.SetupLockDialog;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import java.util.Objects;

import io.multimoon.colorful.CAppCompatActivity;

public class SettingActivity extends CAppCompatActivity {

    private SetupLockDialog setupLockDialog;
    private ColorChooserDialog chooseThemeDialog;
    private LanguageDialog languageDialog;
    private LockDisableDialog lockDisableDialog;
    private boolean mHidden,mHqmode,mTrash,mDarkMode;
    private Switch appLockSwitchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting_activity_title);
        initBools();
        initOptions();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    private void initBools() {
        mHidden = GSettings.getGeneralSetting().getShowHidden();
        mHqmode = GSettings.getGeneralSetting().getShowHq();
        mTrash = GSettings.getGeneralSetting().getMoveTrash();
        mDarkMode = ThemeManager.isUseDarkTheme();
    }

    private void initOptions() {
        initSecurityOptions();
        initGeneralOptions();
    }

    private void initSecurityOptions() {
        setHeading(R.id.sec_heading,R.string.heading_security);
        initOption(R.id.appLockOption,R.drawable.ic_fingerprint_grey,
                R.string.item_app_lock,R.string.item_app_lock_subtitle);
    }
    private void initGeneralOptions() {
        setHeading(R.id.gen_heading,R.string.heading_general);
        initOption(R.id.showHiddenOption,R.drawable.ic_visibility_grey,
                R.string.item_hidden,R.string.item_hidden_subtitle);
        initOption(R.id.showHdImageOption,R.drawable.ic_high_quality_grey,
                R.string.item_hq_mode,R.string.item_hq_mode_subtitle);
        initOption(R.id.enableTrashOption,R.drawable.ic_delete_grey,
                R.string.item_move_trash,R.string.item_move_trash_subtitle);
        initOption(R.id.enableDarkOption,R.drawable.ic_dark_grey,
                R.string.item_enable_dark,R.string.item_enable_dark_subtitle);
        initOption(R.id.choosePrimaryColorOption,R.drawable.ic_primary_color_grey,
                R.string.item_primary_color,R.string.item_primary_color_subtitle);
        initOption(R.id.chooseAccentColorOption,R.drawable.ic_accent_color_grey,
                R.string.item_accent_color,R.string.item_accent_color_subtitle);
        initOption(R.id.chooseLanguageOption,R.drawable.ic_language_grey,
                R.string.item_choose_lang,R.string.item_choose_lang_subtitle);
    }

    private void setHeading(int id,int heading) {
        View vh = findViewById(id);
        TextView mainHeading;
        mainHeading = vh.findViewById(R.id.settingCardHeading);
        mainHeading.setText(heading);
    }

    private void initOption(int op,int icon,int title,int subtitle) {
        View view = findViewById(op);
        ImageView iconView;
        TextView itemTitle,itemSubtitle;
        Switch switchButton;

        iconView = view.findViewById(R.id.settingItemIcon);
        itemTitle = view.findViewById(R.id.settingItemOptionTitle);
        itemSubtitle = view.findViewById(R.id.settingItemOptionSubtitle);

        iconView.setImageResource(icon);
        itemTitle.setText(title);
        itemSubtitle.setText(subtitle);

        if(ThemeManager.isUseDarkTheme()) {
            if(Build.VERSION.SDK_INT < 23)
                iconView.setColorFilter(ContextCompat.getColor(this, R.color.whiteColor));
            else
                iconView.setColorFilter(getColor(R.color.whiteColor));
        }

        if(op == R.id.enableDarkOption || op == R.id.showHiddenOption ||
                op == R.id.showHdImageOption || op == R.id.enableTrashOption ||
                op == R.id.appLockOption) {
            switchButton = view.findViewById(R.id.switchButton);
            if(op == R.id.showHiddenOption) {
                switchButton.setChecked(mHidden);
                initSwitchButton(switchButton,GaliyaraConst.ENABLE_HIDDEN_OPTION);
            }
            if(op == R.id.showHdImageOption) {
                switchButton.setChecked(mHqmode);
                initSwitchButton(switchButton,GaliyaraConst.ENABLE_HQ_OPTION);
            }
            if(op == R.id.enableTrashOption) {
                switchButton.setChecked(mTrash);
                initSwitchButton(switchButton,GaliyaraConst.ENABLE_TRASH_OPTION);
            }
            if(op == R.id.enableDarkOption) {
                switchButton.setChecked(mDarkMode);
                initSwitchButton(switchButton,GaliyaraConst.ENABLE_DARK_THEME);
            }
            if(op == R.id.appLockOption) {
                switchButton.setClickable(false);
                switchButton.setChecked(GSettings.getSecuritySetting().isAppLockEnabled());
                appLockSwitchButton = switchButton;
            }
            if(op != R.id.appLockOption)
                view.setOnClickListener(v -> switchButton.setChecked(!switchButton.isChecked()));
            else
                view.setOnClickListener(v -> {
                    checkForLock(switchButton);
                });
        }
        else {
            view.setOnClickListener(this::showDialog);
        }
    }

    private void showDialog(View v) {
        if(v.getId() == R.id.appLockOption)
            showPasswordDialog();
        if(v.getId() == R.id.choosePrimaryColorOption)
            showPrimaryColorChooser();
        if(v.getId() == R.id.chooseAccentColorOption)
            showAccentColorChooser();
        if(v.getId() == R.id.chooseLanguageOption)
            showLanguageDialog();
    }

    private void showLanguageDialog() {
        if(languageDialog == null) {
            languageDialog = new LanguageDialog(this, new DialogActionListener() {
                @Override
                public void onActionDismissed() {
                    languageDialog = null;}
                @Override
                public void onLangChanged(@NonNull String lang) {
                    GaliyaraHelper.updateLang(SettingActivity.this,lang);
                }
            });
        }
        languageDialog.show();
    }
    private void showPrimaryColorChooser() {
        if(chooseThemeDialog == null) {
            chooseThemeDialog = new ColorChooserDialog(this, new DialogActionListener() {
                @Override
                public void onActionDismissed() {
                    chooseThemeDialog = null;
                }
                @Override
                public void onActionConfirmed() {
                    refreshActivity();
                }
            },GaliyaraConst.SET_PRIMARY_COLOR);
        }
        chooseThemeDialog.show();
    }
    private void showAccentColorChooser() {
        if(chooseThemeDialog == null) {
            chooseThemeDialog = new ColorChooserDialog(this, new DialogActionListener() {
                @Override
                public void onActionDismissed() {
                    chooseThemeDialog = null;
                }
                @Override
                public void onActionConfirmed() {
                    refreshActivity();
                }
            },GaliyaraConst.SET_ACCENT_COLOR);
        }
        chooseThemeDialog.show();
    }

    private void showPasswordDialog() {
        if(setupLockDialog == null)
            setupLockDialog = new SetupLockDialog(this, new DialogActionListener() {
                @Override
                public void onActionDenied() {
                    SecuritySettingEntity entity = new SecuritySettingEntity();
                    entity.setAppLockEnabled(false);
                    new GDBCrud(SettingActivity.this).updateData(entity,GaliyaraConst.ENABLE_LOCK_OPTION);
                    setupLockDialog = null;
                    appLockSwitchButton.setChecked(true);
                }
                @Override
                public void onActionConfirmed(String str,boolean flag) {
                    SecuritySettingEntity entity = new SecuritySettingEntity();
                    try {
                        entity.setPassword(str);
                        entity.setUseBiometric(flag);
                        new GDBCrud(SettingActivity.this).updateData(entity,
                                GaliyaraConst.SET_PASSWORD&GaliyaraConst.ENABLE_BIOMETRIC_OPTION);
                        appLockSwitchButton.setChecked(true);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        GaliyaraConst.showToast("PASSS ERROR");
                    }
                    setupLockDialog = null;
                }
                @Override
                public void onActionDismissed() {
                    setupLockDialog = null;
                }
            });
        setupLockDialog.show();
    }

    private void showDisableDialog() {
        if(lockDisableDialog == null)
            lockDisableDialog = new LockDisableDialog(this, new DialogActionListener() {
                @Override
                public void onActionConfirmed() {
                    SecuritySettingEntity entity = new SecuritySettingEntity();
                    entity.setAppLockEnabled(false);
                    new GDBCrud(SettingActivity.this).updateData(entity,GaliyaraConst.ENABLE_LOCK_OPTION);
                    appLockSwitchButton.setChecked(false);
                    lockDisableDialog = null;
                }
                @Override
                public void onActionDenied() {
                    lockDisableDialog = null;
                }
            });
        lockDisableDialog.show();
    }

    private void initSwitchButton(Switch switchButton,int op) {

        if(op == GaliyaraConst.ENABLE_DARK_THEME) {
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mDarkMode = isChecked;
                ThemeManager.setUseDarkTheme(isChecked);
                ThemeManager.saveSettings(this.getApplicationContext(),GaliyaraConst.ENABLE_DARK_THEME);
                refreshActivity();
            });
        } else {
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                GDBCrud gdbCrud = new GDBCrud(this);
                GeneralSettingEntity generalSettingEntity = new GeneralSettingEntity();
                if(op == GaliyaraConst.ENABLE_HIDDEN_OPTION) {
                    generalSettingEntity.setShowHidden(isChecked);
                    mHidden = isChecked;
                }
                if(op == GaliyaraConst.ENABLE_HQ_OPTION) {
                    generalSettingEntity.setShowHq(isChecked);
                    mHqmode = isChecked;
                }
                if(op == GaliyaraConst.ENABLE_TRASH_OPTION) {
                    generalSettingEntity.setMoveTrash(isChecked);
                    mTrash = isChecked;
                }
                gdbCrud.updateData(generalSettingEntity, op);
            });
        }
    }

    private void refreshActivity() {
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
        finish();
    }
    private void checkForLock(@NonNull Switch switchButton) {
        if(!switchButton.isChecked())
            showPasswordDialog();
        else
            showDisableDialog();
    }
}
