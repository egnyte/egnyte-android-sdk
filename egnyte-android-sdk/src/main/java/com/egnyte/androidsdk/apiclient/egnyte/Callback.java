package com.egnyte.androidsdk.apiclient.egnyte;

import java.io.IOException;

/**
 * Callback used by {@link com.egnyte.androidsdk.apiclient.egnyte.client.APIClient} in asynchronous requests
 * @param <Result> class representing successfull response
 */
public interface Callback<Result> {

    void onSuccess(Result result);

    void onError(IOException error);
}
