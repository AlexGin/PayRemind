package android.alexgin.payremind;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.Runnable;
import java.net.URL;
import java.net.HttpURLConnection;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

public class CurrencyExecutor {
    private static final String TAG = "CurrencyExecutor";

    private AppCompatActivity mAct;
    private JSONObject mJSONResult;
    private Callbacks mCallbacks;

    CurrencyExecutor(Context context, AppCompatActivity act)
    {
        this.mCallbacks = (Callbacks)context;
        this.mAct = act;
    }

    public interface Callbacks {
        void onNotifyResultReady();
        void onNotifyConnectError();
        void onNotifyReadError();
    }

    public JSONObject GetJsonResult() {
        return mJSONResult;
    }

    public void Execute(URL url) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                mAct.runOnUiThread (new Runnable() {
                    @Override
                    public void run() {
                        // onPreExecute method
                        long thr_id = Utils.getThreadId();
                        Log.d(TAG, "onPreExecute: thread-id =" + thr_id);
                    }
                });
                // doInBackground method
                HttpURLConnection connection = null;
                long thread_id = Utils.getThreadId();
                Log.d(TAG, "doInBackground_0: thread-id =" + thread_id);
                try {
                    Log.d(TAG, "doInBackground_1");

                    connection = (HttpURLConnection) url.openConnection();
                    if (connection == null)
                        Log.d(TAG, "doInBackground_2: NULL");
                    else
                        Log.d(TAG, "doInBackground_2: OK");

                    int response = connection.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "doInBackground_2a: OK");
                    } else {
                        Log.d(TAG, "doInBackground_2a: ERROR");
                    }
                    if (response == HttpURLConnection.HTTP_OK) {
                        StringBuilder builder = new StringBuilder();

                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()))) {
                    /*try{
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));*/
                            Log.d(TAG, "doInBackground_3");
                            String line;

                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "ERROR: Read_Error");
                            mJSONResult = null;
                            mCallbacks.onNotifyReadError();
                            e.printStackTrace();
                        }

                        mJSONResult = new JSONObject(builder.toString());
                    } else {
                        mJSONResult = null;
                        mCallbacks.onNotifyConnectError();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "ERROR: Connect_Error");
                    mJSONResult = null;
                    mCallbacks.onNotifyConnectError();
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect(); // close the HttpURLConnection
                }

                mAct.runOnUiThread (new Runnable() {
                    @Override
                    public void run() {
                        // onPostExecute method
                        long thr_id = Utils.getThreadId();
                        Log.d(TAG, "onPostExecute: thread-id =" + thr_id);

                        mCallbacks.onNotifyResultReady();
                    }
                });
            }
        });
    }
}
