package android.alexgin.payremind;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";

    private CheckBox mCheckBoxNotify;
    private CheckBox mCheckBoxPrenotify;
    private CheckBox mCheckBoxFilterUSD;
    private RadioGroup mRadioGroup;
    private int n_id_rBtn1;
    private Button mSaveSettings;

    public static SettingsFragment getNewInstance(Object obj) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mCheckBoxNotify = (CheckBox) v.findViewById(R.id.checkBoxNotify);
        mCheckBoxFilterUSD = (CheckBox) v.findViewById(R.id.checkBoxFilterUSD);
        mCheckBoxPrenotify = (CheckBox) v.findViewById(R.id.checkBoxPrenotify);

        mRadioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        n_id_rBtn1 = R.id.rBtn1day;

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean bNotif = shp.getBoolean(MainActivity.NOTIFICATIONS, true);
        boolean bFilter = shp.getBoolean(MainActivity.FILTER_USD, true);
        boolean bPrenotif = shp.getBoolean(MainActivity.PRENOTIFY, true);
        int m_n_days = shp.getInt(MainActivity.PRENOTDAYS, 1);
        int n_r_btn_id = n_id_rBtn1 + m_n_days - 1;
        mRadioGroup.check(n_r_btn_id);
        Log.d(TAG, "Load days = " + m_n_days);

        mCheckBoxNotify.setChecked(bNotif);
        mCheckBoxFilterUSD.setChecked(bFilter);
        mCheckBoxPrenotify.setChecked(bPrenotif);

        mSaveSettings = (Button) v.findViewById(R.id.buttonSaveSettings);
        mSaveSettings.setOnClickListener(saveSettingsButtonClicked);

        return v;
    }

    // responds to event generated when user press button "save settings"
    View.OnClickListener saveSettingsButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            boolean bNotify = mCheckBoxNotify.isChecked();
            boolean bFilterUSD = mCheckBoxFilterUSD.isChecked();
            boolean bPreNotify = mCheckBoxPrenotify.isChecked();
            int n_id = mRadioGroup.getCheckedRadioButtonId();
            int n_days = n_id - n_id_rBtn1 + 1;
            MainActivity ma = (MainActivity)(getActivity());
            Log.d(TAG, "Settings-save button - pressed");
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ma);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(MainActivity.NOTIFICATIONS, bNotify);
            editor.putBoolean(MainActivity.FILTER_USD, bFilterUSD);
            editor.putBoolean(MainActivity.PRENOTIFY, bPreNotify);
            editor.putInt(MainActivity.PRENOTDAYS, n_days);
            Log.d(TAG, "Settings-save button - save days = " + n_days);
            editor.commit(); //   apply();
            mSaveSettings.setEnabled(false);
        } // end method onClick
    };
}
