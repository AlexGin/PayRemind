package android.alexgin.payremind;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.android.SupportFragmentNavigator;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
  implements CurrencyExecutor.Callbacks,
  ClearDialogFragment.OnDialogChoiceListener {
    private static final String TAG = "MainActivity";
    public static final String NOTIFICATIONS = "pref_notifications";
    public static final String FILTER_USD = "pref_filter_usd";
    public static final String PRENOTIFY = "pref_pre_notifcations"; // Preview of notifications
    public static final String PRENOTDAYS = "pref_pre_notif_days";  // Preview days
    public static final String ARG_RESULT_CODE = "arg_result_code";
    public static final String LIST_SCREEN = "list screen";
    public static final String PAYMENT_SCREEN = "payment screen";
    public static final String ADD_EDIT_SCREEN = "add edit screen";
    public static final String SCHEDULE_SCREEN = "schedule screen";
    public static final String UNEXEC_SCREEN = "unexec screen";
    public static final String CURRENCY_SCREEN = "currency screen";
    public static final String SETTINGS_SCREEN = "settings screen";
    private CurrencyExecutor mCE;
    private boolean mFlagShowCurrencyScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        mCE = new CurrencyExecutor(this, this);
        if (mCE != null)
        {
            ExecuteCurrncyRequest(false);
        }
    }

    public void ExecuteCurrncyRequest(boolean flagShowCurrencyScreen) {
        mFlagShowCurrencyScreen = flagShowCurrencyScreen;
        URL url = createURL();
        if (url != null) {
            mCE.Execute(url);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PRApplication.INSTANCE.getRouter().navigateTo(LIST_SCREEN, null);
        Log.d(TAG, "onStart");
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        PRApplication.INSTANCE.getNavigatorHolder().setNavigator(navigator);
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        PRApplication.INSTANCE.getNavigatorHolder().removeNavigator();
        Log.d(TAG, "onPause");
    }

    private URL createURL() {
        String baseUrl = getString(R.string.web_service_url);
        Log.d(TAG, "createURL: Base URL=" + baseUrl);
        try {
            return new URL(baseUrl);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null; // URL was malformed
    }

    public void onDialogCanceled()
    {
        int messageResId = R.string.cancel_erase;
        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.show();
    }

    public void onDialogConfirmed(int n_mode)
    {
        if (n_mode == 1) {
            clearAllExecuteFlags();
        } else if (n_mode == 2) {
            clearAllLastDates();
            clearAllExecuteFlags();
        }
        int messageResId = (n_mode == 1) ? R.string.erase : R.string.erase_all;
        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.show();
        PRApplication.INSTANCE.getRouter().navigateTo(LIST_SCREEN, null);
    }

    private void clearAllExecuteFlags()
    {
        PaymentLab lab = PaymentLab.get(this);
        ArrayList<Pay> payments = (ArrayList<Pay>)PaymentLab.get(this).getPayments();
        for(Pay pay : payments)
        {
            pay.setExecuted(false);
            lab.updatePay(pay);
        }
    }

    private void clearAllLastDates()
    {
        PaymentLab lab = PaymentLab.get(this);
        ArrayList<Pay> payments = (ArrayList<Pay>)PaymentLab.get(this).getPayments();
        for(Pay pay : payments)
        {
            pay.clearExecDate();
            lab.updatePay(pay);
        }
    }

    public void ProcesCurrencyInfo()
    {
        URL url = createURL();
        if (url != null) {
            mCE.Execute(url);
        }
    }

    public void onNotifyResultReady() {
        JSONObject objResult = mCE.GetJsonResult();
        if (objResult != null) {
            // String strOut = objResult.toString();
            // Log.d(TAG, "JSON-result: " + strOut);
            try {
                String strName = objResult.get("Cur_Name").toString();
                double dbValue = objResult.getDouble("Cur_OfficialRate");
                PaymentLab pl = PaymentLab.get(this);
                pl.setCurrencyName(strName);
                pl.setCurrencyValue(dbValue);
                Log.d(TAG, "JSON-Result: " + strName + " " + dbValue);
                if (mFlagShowCurrencyScreen) {
                    PRApplication.INSTANCE.getRouter().navigateTo(CURRENCY_SCREEN, null);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                }
        }
    }

    public void onNotifyConnectError() {
        Log.d(TAG, "JSON-result: onNotifyConnectError");
        Integer nConnectErrorCode = R.string.connect_error;
        setContentView(R.layout.activity_main);
        // Toast.makeText(getApplicationContext(),
        //        R.string.connect_error, Toast.LENGTH_LONG).show();
     }
    public void onNotifyReadError() {
        Log.d(TAG, "JSON-result: onNotifyReadError");
        Integer nReadErrorCode = R.string.read_error;
        setContentView(R.layout.activity_main);
        // Toast.makeText(getApplicationContext(),
        //        R.string.read_error, Toast.LENGTH_LONG).show();
    }

    private SupportFragmentNavigator navigator = new SupportFragmentNavigator(getSupportFragmentManager(),
            R.id.main_container) {
        @Override
        protected Fragment createFragment(String screenKey, Object data) {
            switch(screenKey) {
                case LIST_SCREEN:
                    return PayListFragment.getNewInstance(data);
                case PAYMENT_SCREEN:
                    return PayFragment.getNewInstance(data);
                case ADD_EDIT_SCREEN:
                    return AddEditFragment.getNewInstance(data);
                case SCHEDULE_SCREEN:
                    return ScheduleFragment.getNewInstance(data);
                case UNEXEC_SCREEN:
                    return UnexecListFragment.getNewInstance(null);
                case CURRENCY_SCREEN:
                    return CurrencyFragment.getNewInstance(null);
                case SETTINGS_SCREEN:
                    return SettingsFragment.getNewInstance(null);
                default:
                    throw new RuntimeException("Unknown screen key!");
            }
        }

        @Override
        protected void showSystemMessage(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void exit() {
            finish();
        }
    };

    @Override
    public void onBackPressed() {
      Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
      if ((fragment instanceof PayFragment) ||
          (fragment instanceof AddEditFragment) ||
          (fragment instanceof ScheduleFragment) ||
          (fragment instanceof UnexecListFragment) ||
          (fragment instanceof CurrencyFragment) ||
          (fragment instanceof SettingsFragment) )  {
          // PRApplication.INSTANCE.getRouter().backTo(LIST_SCREEN); // It's not work!
          PRApplication.INSTANCE.getRouter().navigateTo(LIST_SCREEN, null);
      }
      else {
          finish();
      }
    }
}