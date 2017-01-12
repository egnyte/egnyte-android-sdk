package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.CallsPerSecondQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.DailyQuotaExceeded;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ErrorParserTest {

    @Test(expected = DailyQuotaExceeded.class)
    public void readDeveloperOverDailyQuota() throws Exception {
        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
        Mockito.when(connection.getHeaderField("X-Mashery-Error-Code")).thenReturn("ERR_403_DEVELOPER_OVER_RATE");
        Mockito.when(connection.getInputStream()).thenThrow(new IOException());
        BaseRequest.getInputStream(connection, new LetItGo(), null, new APIClient.ErrorParser());
    }

    @Test(expected = CallsPerSecondQuotaExceeded.class)
    public void readDeveloperOverQPSException() throws Exception {
        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
        Mockito.when(connection.getHeaderField("X-Mashery-Error-Code")).thenReturn("ERR_403_DEVELOPER_OVER_QPS");
        Mockito.when(connection.getInputStream()).thenThrow(new IOException());
        BaseRequest.getInputStream(connection, new LetItGo(), null, new APIClient.ErrorParser());
    }
}