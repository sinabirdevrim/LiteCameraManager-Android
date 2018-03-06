package com.sinabirdevrim.litecameraexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sinabirdevrim.litecamaramanager.LiteCameraManager;
import com.sinabirdevrim.litecamaramanager.PhotoFileType;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    public LiteCameraManager liteCameraManager;
    public ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        liteCameraManager = new LiteCameraManager(this, "Test1", PhotoFileType.JPEG);
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {
                    // do you work now
                    liteCameraManager.takePicture(count + "_Photo");
                }
                //liteCameraManager.takePicture(count + "_Photo");
                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // permission is denied permenantly, navigate user to app settings
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread()
                .check();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == liteCameraManager.PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imgPhoto.setImageBitmap(liteCameraManager.getImage());
        }

    }
}
