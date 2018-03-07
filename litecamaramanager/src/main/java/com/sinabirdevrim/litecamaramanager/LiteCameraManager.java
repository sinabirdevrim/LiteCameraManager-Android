package com.sinabirdevrim.litecamaramanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

    private Context context;
    private Activity mActivity = null;
    private Fragment mFragment = null;
    public static final int PHOTO_REQUEST_CODE = 1020;
    private static String authoritiesName;
    private String path;
    private static String fileName;
    private Uri uri;
    private PhotoFileType photoFileType;
    public Setting setting;


    public LiteCameraManager(Activity activity, String fileName, PhotoFileType photoFileType) {
        this.mActivity = activity;
        init(activity.getApplicationContext().getPackageName(), activity.getApplicationContext(), fileName, photoFileType);
    }

    public LiteCameraManager(Fragment fragment, String fileName, PhotoFileType photoFileType) {
        this.mFragment = fragment;
        init(mFragment.getActivity().getApplicationContext().getPackageName(), fragment.getActivity().getApplicationContext(), fileName, photoFileType);
    }

    private void init(String packageName, Context context, String fileName, PhotoFileType photoFileType) {
        this.context = context;
        this.photoFileType = photoFileType;
        this.fileName = fileName;
        this.authoritiesName = packageName + ".fileprovider";
        setting = new Setting();
    }

    private File getFile(String picName) {
        File photoFile = null;
        try {
            photoFile = createImageFile(picName, setting.isCreateImageFileTemp());
        } catch (IOException ex) {
            Log.e("LiteCameraManager:", ex.getMessage());
        }
        return photoFile;
    }

    public void takePicture(String picName) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            Uri photoURI = null;
            File photoFile = getFile(picName);
            uri = Uri.fromFile(photoFile);
            path = photoFile.getAbsolutePath();
            photoURI = FileProvider.getUriForFile(context,
                    authoritiesName,
                    photoFile);
            if (Build.VERSION.SDK_INT <= 21) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            if (mActivity != null) {
                mActivity.startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            } else {
                mFragment.startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            }
        }
    }

    private File createImageFile(String picName, boolean isCreateTempFile) throws IOException {
        final String imageFileName = picName;
        File storageDir = getFileStorageDir(fileName);
        storageDir.mkdirs();
        if (isCreateTempFile) {
            return File.createTempFile(imageFileName, photoFileType.getValue(), storageDir);
        } else {
            return new File(storageDir, imageFileName + photoFileType.getValue());
        }

    }


    private File getFileStorageDir(String fileName) {
        File storageDir;
        if (setting.isStoragePublic()) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory(), fileName);
        }
        return storageDir;
    }

    public BitmapFactory.Options provideCompressionBitmapFactoryOptions() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        return opt;
    }

    public static String bitmapToBase64(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    public Bitmap getResizedBitmapLessThanMaxSize(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        height = (int) Math.sqrt(maxSize * 1024 * 13 / 4 / bitmapRatio);
        width = (int) (height * bitmapRatio);
        Bitmap reduced_bitmap = Bitmap.createScaledBitmap(image, width, height, true);
        return reduced_bitmap;
    }

    public Bitmap getImage() {
        return getImage(false);
    }

    public Bitmap getImage(boolean isRotate) {
        Bitmap photo = null;
        Uri imageUri = Uri.parse(path);
        File file = new File(imageUri.getPath());
        final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        if (isRotate) {
            photo = rotateImage(getResizedBitmapLessThanMaxSize(bitmap, setting.getMaxSize()), getImageFilePath());
        } else {
            photo = getResizedBitmapLessThanMaxSize(bitmap, setting.getMaxSize());
        }
        return photo;
    }

    public String getImageFilePath() {
        Uri imageUri = Uri.parse(path);
        File file = new File(imageUri.getPath());
        return file.getPath();
    }

    private static Bitmap rotateImage(Bitmap img, String imgPath) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static String bitmapToBase64ForPath(String path, int maxSize, int quality, Bitmap.CompressFormat compressFormat) {

        Bitmap image = BitmapFactory.decodeFile(path);
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        height = (int) Math.sqrt(maxSize * 1024 * 13 / 4 / bitmapRatio);
        width = (int) (height * bitmapRatio);
        Bitmap reduced_bitmap = Bitmap.createScaledBitmap(image, width, height, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reduced_bitmap.compress(compressFormat, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
