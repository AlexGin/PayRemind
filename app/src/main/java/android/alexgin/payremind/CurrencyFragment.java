package android.alexgin.payremind;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.os.Bundle;

import android.os.Parcelable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;

import android.util.Log;

public class CurrencyFragment extends Fragment {
    private static final String TAG = "CurrencyFragment";
    private EditText mInfoField;
    private EditText mNameField;
    private EditText mValueField;
    private Button mSaveCurrencyButton;
    private Button mRefreshCurrencyButton;

    public static CurrencyFragment getNewInstance(Object obj) {
        CurrencyFragment fragment = new CurrencyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_currency, container, false);

        PaymentLab pl = PaymentLab.get(getActivity());
        boolean bManualFlag = pl.getCurrncyManual();
        String strInfo = bManualFlag ?
                getString(R.string.currency_manual) :
                getString(R.string.currency_from_bank);
        String strName = pl.getCurrencyName();
        Double dbValue = pl.getCurrencyValue();
        String strValue = dbValue.toString();

        mInfoField = (EditText) v.findViewById(R.id.currency_info_text);
        mInfoField.setText(strInfo);
        mInfoField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        mNameField = (EditText) v.findViewById(R.id.currency_name_text);
        mNameField.setText(strName);
        mNameField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        mValueField = (EditText) v.findViewById(R.id.currency_value_text);
        mValueField.setText(strValue);
        mValueField.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);

        mSaveCurrencyButton = (Button) v.findViewById(R.id.saveCurrencyButton);
        mSaveCurrencyButton.setOnClickListener(saveCurrencyButtonClicked);

        mRefreshCurrencyButton = (Button) v.findViewById(R.id.refreshCurrencyButton);
        mRefreshCurrencyButton.setOnClickListener(refreshCurrencyButtonClicked);

        return v;
    }
    // responds to event generated when user edit a currency
    View.OnClickListener saveCurrencyButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mValueField.getText().toString().trim().length() != 0) {
                PaymentLab pl = PaymentLab.get(getActivity());
                pl.setCurrencyManual(true);
                saveCurrency();
            } else // required currency value is blank, so display error dialog
            {
                Log.d(TAG, "Currency value is blank");
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage("Error: Currency Value (BYN/USD) missing!");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        } // end method onClick
    } ; // end OnClickListener saveCurrencyButtonClicked

    private void saveCurrency() {
        double dbValue = Double.parseDouble(mValueField.getText().toString());
        PaymentLab pl = PaymentLab.get(getActivity());
        pl.setCurrencyValue(dbValue);

        mSaveCurrencyButton.setEnabled(false);

        String strInfo = getString(R.string.currency_manual);
        mInfoField.setText(strInfo);
    }

    // responds to event generated when user request a cool currency from the bank (NBRB)
    View.OnClickListener refreshCurrencyButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
           MainActivity ma = (MainActivity)(getActivity());
            PaymentLab pl = PaymentLab.get(ma);
            pl.setCurrencyManual(false);

           ma.ExecuteCurrncyRequest(true);
        } // end method onClick
    };
}
