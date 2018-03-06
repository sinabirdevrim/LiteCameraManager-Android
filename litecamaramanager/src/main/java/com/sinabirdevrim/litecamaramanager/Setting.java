package com.sinabirdevrim.litecamaramanager;

/**
 * Created by Ahmet on 26.02.2018.
 */

public class Setting {

    private int MaxSize = 160;
    private int Width = 640;
    private int Height = 480;
    private boolean isStoragePublic = false;
    private boolean isCreateImageFileTemp = true;

    public boolean isCreateImageFileTemp() {
        return isCreateImageFileTemp;
    }

    public void setCreateImageFileTemp(boolean createImageFileTemp) {
        isCreateImageFileTemp = createImageFileTemp;
    }

    public boolean isStoragePublic() {
        return isStoragePublic;
    }

    public void setStoragePublic(boolean storagePublic) {
        isStoragePublic = storagePublic;
    }

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
}
