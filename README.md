## Getting started
Get an API key, as described in [Getting an API key](https://developers.egnyte.com/docs/read/Getting_Started#Getting-an-API-Key).
If you need a domain for development, you can get one, as described in [Get a free Partner Domain](https://developers.egnyte.com/docs/read/Getting_Started#Get-a-Free-Partner-Domain).

## Integration

### Add archive locally

Get it from [here](https://egnyte.egnyte.com/dl/uHhbvg7kCw). 
Create 'libs' directory in your application directory (by default it's named 'app'). 
Put egnyte-android-sdk.aar there and modify your application build.gradle file so it looks like this:


```groovy
apply plugin: 'com.android.application'

repositories {
    flatDir {
        dirs 'libs'
    }
}

android {
    ...
}

dependencies {
    ...
     compile(name:'egnyte-android-sdk', ext:'aar')
    ...
}
```

## Authentication
You will find API key and Shared Secret here: https://developers.egnyte.com/apps/mykeys.
Calling this code will start EgnyteAuthActivity with UI for authentication. 
Use requestCode (int) so you can identify result coming back in onActivityResult(int, int, Intent)
```java
private static final int REQUEST_CODE_AUTH = 42;

...

EgnyteAuthRequest authRequest = new EgnyteAuthRequest.Builder(
        "your api key", "your shared secret"
).build();
EgnyteAuth.start(authRequest, activity, REQUEST_CODE_AUTH);
```

Parse result in onActivityResult(int, int, Intent) method of Activity that called EgnyteAuth.start(EgnyteAuthRequest, Context, int).
Check for requestCode value used in EgnyteAuth.start(EgnyteAuthRequest, Context, int).
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_AUTH) {
        try {
            EgnyteAuthResult authResult = EgnyteAuth.parseResult(resultCode, data);
            if (authResult != null) {
                EgnyteAuthResponseHelper.saveIntoPrefs(authResult, this);
                startMainActivity(authResult);
            }
        } catch (AuthFailedException e) {
            Toast.makeText(this, "Auth failed", Toast.LENGTH_LONG).show();
        }
    }
}
```

## Creating APIClient instance
Once you have EgnyteAuthResult object, you can create APIClient that's capable of executing requests. Note that you should use only one instance per Egnyte domain.
Find your calls per second value on https://developers.egnyte.com/apps/mykeys, under "User (Access Token) Rate Limits" section.

```java
int callsPerSecond = 2;
APIClient client = new APIClient(authResult, callsPerSecond);
```

## Executing requests
Now you can easily execute requests with APIClient instance, both asynchronously:
```java
client.enqueueAsync(new GetFolderListingRequest("/Shared"), new Callback<FolderListing>() {
    @Override
    public void onSuccess(FolderListing folderListing) {
        // handle result
    }

    @Override
    public void onError(IOException error) {
        // handle error
    }
});
```
and synchronously:
```java
try {
    FolderListing folderListing = client.enqueueSync(new GetFolderListingRequest("/Shared"));
    // handle result
} catch (IOException e) {
    // handle error
}
```

## Egnyte Folder Picker

Use EgnyteFolderPickerView to make browsing through cloud easy.

## Integration

### Add archive locally

Get it from [here](https://egnyte.egnyte.com/dl/JfNxbUOssm). 
Create 'libs' directory in your application directory (by default it's named 'app'). 
Put egnyte-android-picker.aar there and modify your application build.gradle file so it looks like this:


```groovy
apply plugin: 'com.android.application'

repositories {
    flatDir {
        dirs 'libs'
    }
}

android {
    ...
}

dependencies {
    ...
    compile('com.android.support:recyclerview-v7:25.1.0')
    compile ('com.android.support:appcompat-v7:25.1.0')
    compile(name:'egnyte-android-picker', ext:'aar')
    ...
}
```
## Usage

Now just add EgnyteFolderPickerView to your view hierarchy, like that:

```xml
<com.egnyte.androidsdk.picker.EgnyteFolderPickerView
    android:id="@+id/egnyte_folder_picker_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

and then initialize it

```java
EgnyteFolderPickerView pickerView = (EgnyteFolderPickerView) findViewById(R.id.egnyte_folder_picker_view);
pickerView.init(apiClient, new EgnyteFolderPickerView.BaseCallback() {
    @Override
    public boolean onFileClicked(EgnyteFile file) {
        //handle file click
        return true;
    }
});
```

## Copyright and License
Copyright 2017 Egnyte. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.