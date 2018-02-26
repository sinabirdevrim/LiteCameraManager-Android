# LiteCameraManager-Android

**Camera Helper Lib**

Basic camera libary 

### Manifest File ###
```
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

   <application
       android:allowBackup="true"
	...
	<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider_paths" />
        </provider>
   </application>
```
**don't forget runtime permission**

### Gradle ###
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
 dependencies {
	        compile 'com.github.sinabirdevrim:LiteCameraManager-Android:0.1.1'
	}
```
### Using ###

~~~~
LiteCameraManager   liteCameraManager = new LiteCameraManager(this, "Test", PhotoFileType.JPEG);
liteCameraManager.takePicture("Photo Name");


  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == liteCameraManager.PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imgPhoto.setImageBitmap(liteCameraManager.getImage());
        }

    }
~~~~

#### Get Photo File Path  ####
~~~~
cameraManager.getImageFilePath()
~~~~
#### Get Base64 Format  ####
~~~~
CameraManager.bitmapToBase64ForPath(photoInfo.getPhotoPath(), 160)
~~~~
