package com.pranks.picasa.album.slideshow.picasaalbumslideshow;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.AccountPicker;
import com.google.gdata.client.photos.PicasawebService;
import com.pranks.picasa.album.slideshow.picasaalbumslideshow.imagemanager.ImageManager;

public class LoginActivity extends Activity {

    //TODO Add the nav bar on the login activity.
    public static final int PICK_ACCOUNT_REQUEST = 1;
    public static final int REQUEST_AUTHENTICATE = 2;
    private static final String TAG = LoginActivity.class.getCanonicalName();
    private PicasawebService mPicasaService;
    private Button mSelectAccount;
    private AccountManager mAccountManager;
    private Account[] mAccountListOnDevice;
    private Account mSelectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSelectAccount = (Button) findViewById(R.id.selectAccount);
        fetchAccountsPermission();
        mAccountManager = AccountManager.get(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case PICK_ACCOUNT_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    handleAccountSelection();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //TODO something to be done here. Say bye to user etc.
                }
                return;
            }
        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent
            data) {

        switch (requestCode) {
            case PICK_ACCOUNT_REQUEST:
                if (resultCode == RESULT_OK) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    selectAccount(accountName);
                    handleRequest();
                }
                break;
            case REQUEST_AUTHENTICATE:
                if (resultCode == RESULT_OK) {
                    handleRequest();
                }
                break;
        }
    }

    private void handleRequest() {
        ImageManager imageManager = new ImageManager(this, mAccountManager, mSelectedAccount);

        mAccountManager.getAuthToken
                (mSelectedAccount,"lh2", null, this, imageManager, null);
    }

    private void selectAccount(String accountName) {
        mSelectedAccount = null;
        for (Account account : mAccountListOnDevice) {
            if (account.name.equals(accountName)) {
                mSelectedAccount = account;

                Log.d(TAG, account.name);
                break;
            }
        }
    }

    private void fetchAccountsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .GET_ACCOUNTS}, PICK_ACCOUNT_REQUEST);
        } else {
            handleAccountSelection();
        }
    }

    private void handleAccountSelection() {
        mSelectAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAccountListOnDevice = mAccountManager.getAccounts();
                    for (Account a : mAccountListOnDevice) {
                        Log.d(TAG, " account details: " + a.name + a.type);
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "didn't accept the permission");
                }

                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new
                        String[]{"com.google"}, false, null, null, null, null);
                startActivityForResult(intent, PICK_ACCOUNT_REQUEST);
            }
        });
    }
}
