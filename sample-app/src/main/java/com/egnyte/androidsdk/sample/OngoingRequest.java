package com.egnyte.androidsdk.sample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.egnyte.androidsdk.apiclient.egnyte.Callback;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.APIClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.BaseRequest;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class OngoingRequest {

    private final Dialog dialog;

    public OngoingRequest(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public void cancel() {
        dialog.cancel();
    }

    public static ProgressDialog createDialog(Context context, String message, boolean indeterminate) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat("");
        return progressDialog;
    }

    public static <T> OngoingRequest start(APIClient apiCLient,
                                              BaseRequest<T> request, ProgressDialog dialog,
                                              SuccessCallback<T> onSuccess, ErrorPresenter errorPresenter) {

        CancelledState cancelledState = new CancelledState();
        final WeakReference<CancelledState> cancelledStateRef = new WeakReference<CancelledState>(cancelledState);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancelledStateRef.get() != null) {
                    cancelledStateRef.get().setCancelled();
                }
            }
        });
        final WeakReference<ProgressDialog> dialogRef = new WeakReference<ProgressDialog>(dialog);
        final WeakReference<SuccessCallback<T>> successCallbackRef = new WeakReference<SuccessCallback<T>>(onSuccess);
        final WeakReference<ErrorPresenter> errorPresenterRef = new WeakReference<ErrorPresenter>(errorPresenter);

        Callback<T> callback = new Callback<T>() {
            @Override
            public void onSuccess(T result) {
                if (dialogRef.get() != null) {
                    dialogRef.get().dismiss();
                }
                if (successCallbackRef.get() != null) {
                    successCallbackRef.get().onSuccess(result);
                }
            }

            @Override
            public void onError(IOException error) {
                if (dialogRef.get() != null) {
                    dialogRef.get().dismiss();
                }
                if (errorPresenterRef.get() != null) {
                    errorPresenterRef.get().present(error);
                }
            }
        };
        apiCLient.enqueueAsync(request, callback, cancelledState);
        dialog.show();
        return new OngoingRequest(dialog);
    }
}
