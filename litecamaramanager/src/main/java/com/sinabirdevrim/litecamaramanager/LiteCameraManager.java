package com.sinabirdevrim.litecamaramanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class LiteCameraManager {

    private Activity mActivity;
    public static final int PHOTO_REQUEST_CODE = 102;
    private static String authoritiesName;
    public String path;
    private static String fileName;
    private Uri uri;
    private PhotoFileType photoFileType;
    private int MaxSize = 160;
    private int Width = 640;
    private int Height = 480;

    public int getMaxSize() {
        return MaxSize;
    }

    public void setMaxSize(int maxSize) {
        MaxSize = maxSize;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public LiteCameraManager(Activity activity, String fileName, PhotoFileType photoFileType) {
        this.mActivity = activity;
        this.fileName = fileName;
        this.photoFileType = photoFileType;
        this.authoritiesName = activity.getApplicationContext().getPackageName() + ".fileprovider";
    }

    public void takePicture(String picName) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            Uri photoURI = null;
            try {
                File photoFile = createImageFileWith(picName);
                uri = Uri.fromFile(photoFile);
                path = photoFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(mActivity,
                        authoritiesName,
                        photoFile);

            } catch (IOException ex) {
                Log.e("TakePicture", ex.getMessage());
            }
            if (Build.VERSION.SDK_INT <= 21) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            mActivity.startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
        }
    }

    public File createImageFileWith(String picName) throws IOException {
        final String imageFileName = picName;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        storageDir.mkdirs();
        return File.createTempFile(imageFileName, photoFileType.getValue(), storageDir);
    }

    public BitmapFactory.Options provideCompressionBitmapFactoryOptions() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        return opt;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    public Bitmap getResizedBitmapLessThanMaxSize(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        height = (int) Math.sqrt(maxSize * 1024 * 13 / 4 / bitmapRatio);
        width = (int) (height * bitmapRatio);
        Bitmap reduced_bitmap = Bitmap.createScaledBitmap(image, getWidth(), getHeight(), true);
        return reduced_bitmap;
    }

    public Bitmap getImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Uri imageUri = Uri.parse(path);
        File file = new File(imageUri.getPath());
        final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        return getResizedBitmapLessThanMaxSize(bitmap, getMaxSize());
    }

    public String getImageFilePath() {
        Uri imageUri = Uri.parse(path);
        File file = new File(imageUri.getPath());
        return file.getPath();
    }


    public static String bitmapToBase64ForPath(String path, int maxSize, int quality) {

        Bitmap image = BitmapFactory.decodeFile(path);
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        height = (int) Math.sqrt(maxSize * 1024 * 13 / 4 / bitmapRatio);
        width = (int) (height * bitmapRatio);
        Bitmap reduced_bitmap = Bitmap.createScaledBitmap(image, width, height, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reduced_bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
