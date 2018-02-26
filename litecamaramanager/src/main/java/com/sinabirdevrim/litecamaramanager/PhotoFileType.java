package com.sinabirdevrim.litecamaramanager;

/**
 * Created by Ahmet Sina on 16.02.2018.
 */

public enum PhotoFileType {
    JPEG(".jpeg"),
    JPG(".jpg"),
    PNG(".png");
    private final String value;

    private PhotoFileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
