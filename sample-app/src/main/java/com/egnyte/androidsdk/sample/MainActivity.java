package com.egnyte.androidsdk.sample;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.egnyte.androidsdk.apiclient.egnyte.client.APIClient;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.EgnyteException;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthResponseHelper;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthResult;
import com.egnyte.androidsdk.entities.CreateLinkResult;
import com.egnyte.androidsdk.entities.EgnyteFile;
import com.egnyte.androidsdk.entities.UploadResult;
import com.egnyte.androidsdk.picker.EgnyteFolderPickerView;
import com.egnyte.androidsdk.picker.FilesystemItem;
import com.egnyte.androidsdk.picker.PickerItem;
import com.egnyte.androidsdk.picker.ViewHoldersFactoryImpl;
import com.egnyte.androidsdk.requests.CreateFileLinkRequestBuilder;
import com.egnyte.androidsdk.requests.CreateFolderLinkRequestBuilder;
import com.egnyte.androidsdk.requests.CreateFolderRequest;
import com.egnyte.androidsdk.requests.CreateLinkRequest;
import com.egnyte.androidsdk.requests.DeleteRequest;
import com.egnyte.androidsdk.requests.DownloadFileRequest;
import com.egnyte.androidsdk.requests.InputStreamProvider;
import com.egnyte.androidsdk.requests.ProgressListener;
import com.egnyte.androidsdk.requests.UploadRequest;
import com.egnyte.androidsdk.sample.auth.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_AUTH_RESULT = "EgnyteAuthResult";

    private static final int CODE_DOWNLOAD = 1;
    private static final int REQUEST_CODE_PICK = 2;
    private static final int CODE_OPEN = 3;
    private static final int REQUEST_READ_PERM = 4;

    private APIClient client;
    private EgnyteFolderPickerView pickerView;
    private OngoingRequest currentRequest;
    private ErrorPresenter errorPresenter = new ErrorPresenter() {
        @Override
        public void present(Exception error) {
            String message = "Something went wrong";
            if (error instanceof EgnyteException && ((EgnyteException) error).getApiExceptionMessage() != null) {
                message = ((EgnyteException) error).getApiExceptionMessage();
            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }
    };

    private EgnyteFile downloadMeIfYouCan;
    private EgnyteFile openMeIfYouCan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EgnyteAuthResult authResponse = getIntent().getParcelableExtra(KEY_AUTH_RESULT);
        getSupportActionBar().setTitle(authResponse.getEgnyteDomainURL().toString());
        pickerView = (EgnyteFolderPickerView) findViewById(R.id.egnyte_folder_picker_view);
        client = new APIClient(authResponse, 2);
        pickerView.init(client, new EgnyteFolderPickerView.BaseCallback() {
            @Override
            public boolean onFileClicked(EgnyteFile file) {
                download(file, CODE_OPEN);
                return true;
            }
        }, new CustomViewHoldersFactory(pickerView));
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(pickerView.getCurrentPath())) {
            pickerView.goUp();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                upload();
                return true;
            case R.id.create_new_folder:
                createNewFolder();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewFolder() {
        final EditText editText = new EditText(this);
        int padding = (int) getResources().getDimension(R.dimen.picker_padding);
        editText.setPadding(padding, padding, padding, padding);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        editText.setHint("New folder's name");
        new AlertDialog.Builder(this)
                .setTitle("Create New Folder")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() != 0) {
                            createNewFolder(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void upload() {
        try {
            startActivityForResult(new Intent(Intent.ACTION_PICK), REQUEST_CODE_PICK);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no apps that can provide file to upload.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        EgnyteAuthResponseHelper.logout(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        FilesystemItem chosenItem = pickerView.getItemAtPosition(item.getGroupId());
        if (item.getItemId() == CustomViewHoldersFactory.MENU_DELETE) {
            currentRequest = OngoingRequest.start(client, new DeleteRequest(chosenItem.getPath()), OngoingRequest.createDialog(this, "Deleting", true), new SuccessCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    pickerView.reload();
                }
            }, errorPresenter);
            return true;
        } else if (item.getItemId() == CustomViewHoldersFactory.MENU_SHARE) {
            CreateLinkRequest createLinkRequest;
            if (chosenItem.isFolder()) {
                createLinkRequest = new CreateFolderLinkRequestBuilder(chosenItem.getPath(), CreateLinkRequest.Accessibility.ANYONE).build();
            } else {
                createLinkRequest = new CreateFileLinkRequestBuilder(chosenItem.getPath(), CreateLinkRequest.Accessibility.ANYONE).build();
            }
            currentRequest = OngoingRequest.start(client, createLinkRequest, OngoingRequest.createDialog(this, "Creating link", true), new SuccessCallback<CreateLinkResult>() {
                @Override
                public void onSuccess(CreateLinkResult result) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, result.links.get(0).url);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            }, errorPresenter);
            return true;
        } else if (item.getItemId() == CustomViewHoldersFactory.MENU_DOWNLOAD) {
            if (chosenItem.getFile() != null) {
                download(chosenItem.getFile(), CODE_DOWNLOAD);
            }
            return true;
        }
        Toast.makeText(this, item.getTitle() + " " + item.getGroupId() + " " + item.getItemId(), Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }

    private void download(final EgnyteFile chosenItem, int code) {
        if (!checkWritePermission(chosenItem, code)) {
            return;
        }
        File dest = getDownloadDestFile(chosenItem);
        final ProgressDialog progressDialog = OngoingRequest.createDialog(this, "Downloading", false);
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesTotal) {
                progressDialog.setProgress((int) ((double) bytesTotal / chosenItem.size * 100));
            }
        };
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(chosenItem.path, null, progressListener, dest);
        OngoingRequest.start(client, downloadFileRequest, progressDialog, successCallbackForCode(chosenItem, code), errorPresenter);
    }

    private File getDownloadDestFile(EgnyteFile chosenItem) {
        File parentDest = new File(Environment.getExternalStorageDirectory(), "egnyte-downloads");
        parentDest.mkdirs();
        return new File(parentDest, chosenItem.name);
    }

    private SuccessCallback<Void> successCallbackForCode(final EgnyteFile egnyteFile, int code) {
        if (code == CODE_DOWNLOAD) {
            return new SuccessCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(MainActivity.this, "Download succeeded", Toast.LENGTH_SHORT).show();
                }
            };
        } else if (code == CODE_OPEN) {
            return new SuccessCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    openFile(egnyteFile);
                }
            };
        }
        return null;
    }

    private void openFile(EgnyteFile egnyteFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = "application/octet-stream";
        int dotIndex = egnyteFile.name.lastIndexOf(".");
        if (dotIndex != -1) {
            String extension = egnyteFile.name.substring(dotIndex + 1, egnyteFile.name.length());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", getDownloadDestFile(egnyteFile));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimeType);
        startActivity(intent);
    }

    private boolean checkWritePermission(EgnyteFile chosenItem, int code) {
        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            downloadMeIfYouCan = chosenItem;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentRequest != null) {
            currentRequest.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK) {
            if (data != null && data.getData() != null) {
                final Uri dataUri = data.getData();
                final ProgressDialog progressDialog = OngoingRequest.createDialog(this, "Uploading", true);
                Cursor cursor = getContentResolver().query(dataUri, new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE}, null, null, null);
                if (cursor != null && cursor.moveToFirst() && cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) != -1) {
                    String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    String path = pickerView.getCurrentPath() + "/" + displayName;
                    UploadRequest uploadRequest = new UploadRequest(path, new InputStreamProvider() {
                        @Override
                        public InputStream provideInputStream() throws IOException {
                            return getContentResolver().openInputStream(dataUri);
                        }
                    }, null, null, null);
                    currentRequest = OngoingRequest.start(client, uploadRequest, progressDialog, new SuccessCallback<UploadResult>() {
                        @Override
                        public void onSuccess(UploadResult result) {
                            pickerView.reload();
                        }
                    }, errorPresenter);
                }
            }
        }
    }

    private void createNewFolder(String folderName) {
        final String destPath = pickerView.getCurrentPath() + "/" + folderName;

        currentRequest = OngoingRequest.start(
                client,
                new CreateFolderRequest(destPath),
                OngoingRequest.createDialog(this, "Creating new folder", true),
                new SuccessCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        pickerView.openPath(destPath);
                    }
                },
                errorPresenter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_DOWNLOAD || requestCode == CODE_OPEN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && downloadMeIfYouCan != null) {
                download(downloadMeIfYouCan, requestCode);
            }
            downloadMeIfYouCan = null;
        } else if (requestCode == REQUEST_READ_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && downloadMeIfYouCan != null) {
                openFile(openMeIfYouCan);
            }
            openMeIfYouCan = null;
        }
    }

    static class CustomViewHoldersFactory extends ViewHoldersFactoryImpl {

        private static final int MENU_DELETE = 0;
        private static final int MENU_SHARE = 1;
        private static final int MENU_DOWNLOAD = 2;

        public CustomViewHoldersFactory(EgnyteFolderPickerView egnyteFolderPickerView) {
            super(LayoutInflater.from(new ContextThemeWrapper(egnyteFolderPickerView.getContext(), R.style.CustomEgnytePickerStyle)), egnyteFolderPickerView);
        }

        @Override
        public EgnyteFolderPickerView.ViewHolder onCreateViewHolder(ViewGroup parent, PickerItem.Type viewType) {
            final EgnyteFolderPickerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
            if (viewType == PickerItem.Type.FILE) {
                holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        applyMenuItemsForFile(menu, holder.getAdapterPosition());

                    }
                });
            } else if (viewType == PickerItem.Type.FOLDER) {
                holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        applyMenuItemsForFolder(menu, holder.getAdapterPosition());
                    }
                });
            }
            return holder;
        }

        private void applyMenuItemsForFile(ContextMenu menu, int adapterPosition) {
            applyMenuItemsForFolder(menu, adapterPosition);
            menu.add(adapterPosition, MENU_DOWNLOAD, Menu.NONE, "Download");
        }

        private void applyMenuItemsForFolder(ContextMenu menu, int adapterPosition) {
            menu.add(adapterPosition, MENU_DELETE, Menu.NONE, "Delete");
            menu.add(adapterPosition, MENU_SHARE, Menu.NONE, "Share");
        }
    }
}
