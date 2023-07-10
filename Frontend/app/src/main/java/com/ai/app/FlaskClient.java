package com.ai.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class FlaskClient extends Activity {

    private static final String TAG = FlaskClient.class.getSimpleName();
    private String postBodyString;
    private MediaType mediaType;
    private RequestBody requestBody;

    private String url = "http://172.24.128.1:5000";// *****put your URL here*********
    private String POST = "POST";
    private String GET = "GET";
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_flask_client);
//        String url = "http://127.0.0.1:5000/";
//        postRequest("your message here", url);
//        sendPostRequest();
        String text = "hi beautiful";
        /*if name text is not empty,then call the function to make the post request*/
        executorService.submit(() -> sendRequest(POST, "getname", "name", text));

    }

    private RequestBody buildRequestBody(String msg) {
        postBodyString = msg;
        mediaType = MediaType.parse("text/plain");
        requestBody = RequestBody.create(postBodyString, mediaType);
        return requestBody;
    }

    private void postRequest(String message, String URL) {
        RequestBody requestBody = buildRequestBody(message);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


//                        Toast.makeText(MainActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();


                    }
                });

            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        try {
////                            Toast.makeText(MainActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                });


            }
        });
    }

//    private void sendPostRequest() {
//        String urlString = "http://172.24.128.1:5000/"; // replace with your Flask server URL
//        String payload = "{\"message\":\"Hello, Flask server!\"}"; // replace with your JSON payload
//
//        new AsyncTask<String, Void, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                String urlString = params[0];
//                String payload = params[1];
//
//                try {
//                    URL url = new URL(urlString);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST");
//                    conn.setRequestProperty("Content-Type", "application/json");
//                    conn.setDoOutput(true);
//
//                    OutputStream os = conn.getOutputStream();
//                    os.write(payload.getBytes());
//                    os.flush();
//
//                    int statusCode = conn.getResponseCode();
//                    if (statusCode == HttpURLConnection.HTTP_OK) {
//                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                        String response = in.readLine();
//                        return response;
//                    } else {
//                        Log.e(TAG, "Request failed with error code " + statusCode);
//                        return null;
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Exception while sending POST request", e);
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                if (result != null) {
//                    Log.d(TAG, "Response received from server: " + result);
//                }
//            }
//        }.execute(urlString, payload);
//    }
void sendRequest(String type, String method, String paramname, String param) {

    /* if url is of our get request, it should not have parameters according to our implementation.
     * But our post request should have 'name' parameter. */
    String fullURL = url + "/" + method + (param == null ? "" : "/" + param);
    Request request;

    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS).build();

    /* If it is a post request, then we have to pass the parameters inside the request body*/
    if (type.equals(POST)) {
        RequestBody formBody = new FormBody.Builder()
                .add(paramname, param)
                .build();

        request = new Request.Builder()
                .url(fullURL)
                .post(formBody)
                .build();
    } else {
        /*If it's our get request, it doen't require parameters, hence just sending with the url*/
        request = new Request.Builder()
                .url(fullURL)
                .build();
    }
    /* this is how the callback get handled */
    client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {

            // Read data on the worker thread
            final String responseData = response.body().string();

            // Run view-related code back on the main thread.
            // Here we display the response message in our text view
//            FlaskClient.this.runOnUiThread(() -> textView_response.setText(responseData));
        }
    });
}

}
