package android.alexgin.payremind;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
// import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.text.InputType;
import android.util.Log; // Added by AlexGin
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.UUID;

public class AddEditFragment extends Fragment {
    private static final String TAG = "AddEditFragment";
    private long rowID; // database row ID of the payment

    private Pay mPay;
    private boolean mEditMode = false; //true; // Otherwise: AddMode

    private EditText mTitleEditText;
    private Spinner mSpinnerCategory;
    private EditText mBankEditText;
    private EditText mDescrEditText;
    private EditText mAccountEditText;
    private EditText mDayEditText;
    private EditText mPeriodEditText;
    private EditText mTotalEditText;
    private CheckBox mCheckBoxUSD;
    private Button mSavePaymentButton;

    public static AddEditFragment getNewInstance(Object obj) {
        AddEditFragment fragment = new AddEditFragment();
        if (obj != null) {
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.ARG_RESULT_CODE, (Parcelable) obj);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate_0");

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            mPay = args.getParcelable(MainActivity.ARG_RESULT_CODE);
        }
        Log.d(TAG, "onCreate_1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_edit, container, false);
        // ActionBar actionBar = getSupportActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);

        mTitleEditText = (EditText) v.findViewById(R.id.titleEditText);
        mBankEditText = (EditText) v.findViewById(R.id.bankEditText);
        mDescrEditText = (EditText) v.findViewById(R.id.descrEditText);
        mAccountEditText = (EditText) v.findViewById(R.id.accountEditText);
        mDayEditText = (EditText) v.findViewById(R.id.dayEditText);
        mPeriodEditText = (EditText) v.findViewById(R.id.periodEditText);
        mTotalEditText = (EditText) v.findViewById(R.id.totalEditText);
        mCheckBoxUSD = (CheckBox) v.findViewById(R.id.checkBoxUSD);

        mDayEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPeriodEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mTotalEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getBaseContext(),
                R.array.cates_context_names,
                android.R.layout.simple_spinner_item);
        mSpinnerCategory = (Spinner) v.findViewById(R.id.categoryChooser);
        mSpinnerCategory.setAdapter(adapter);
        mSpinnerCategory.setOnItemSelectedListener(listener);

        Log.d(TAG, "onCreate before 'getIntent/getExtras'");
        /*Intent i = getIntent();
        Bundle extras = i.getExtras(); */
        if (mPay == null) {
            Log.d(TAG,"onCreate: mPay = NULL");
            mEditMode = false;
        }
        else {
            Log.d(TAG,"onCreate: mPay VALID");
            mEditMode = true;
        }

        Log.d(TAG, "onCreate after: 'Bundle' OK!");
        // mEditMode = (mPay != null);
        if (mEditMode) // Edit exist mode:
        {
            Log.d(TAG, "onCreate: Edit_mode_1");

            UUID payId = mPay.getId();
            String sId = payId.toString();
            Log.d(TAG, "onCreate: Edit_mode: UUID = " + sId);

            mTitleEditText.setText(mPay.getTitle());
            mBankEditText.setText(mPay.getNameOfBank());
            mDescrEditText.setText(mPay.getDescription());
            mAccountEditText.setText(mPay.getAccountId());
            Log.d(TAG, "onCreate: Edit_mode_2");
            Integer iDayOfMonth = mPay.getDayOfMonth();
            Integer iPayPeriod = mPay.getPayPeriod();
            Double dbTotalSumm = mPay.getTotalSumm();
            int nCurrency = mPay.getCurrency();
            mDayEditText.setText(iDayOfMonth.toString());
            mPeriodEditText.setText(iPayPeriod.toString());
            mTotalEditText.setText(dbTotalSumm.toString());
            mCheckBoxUSD.setChecked(nCurrency == 1);
            Log.d(TAG, "onCreate: Edit_mode_3");
            Category categ = mPay.getCategory();
            mSpinnerCategory.setSelection(categ.ordinal());
            Log.d(TAG, "onCreate: Edit_mode_4");
        }
        else // Create (add) new Pay-record
        {
            mPay = new Pay();
        }
        // set Save Pay-record Button's event listener
        mSavePaymentButton =
                (Button) v.findViewById(R.id.savePaymentButton);
        mSavePaymentButton.setOnClickListener(savePaymentButtonClicked);

        return v;
    }

    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView spinner, View view, int pos, long id) {
            Category categ = Category.values()[pos];
            if (mPay != null) {
                mPay.setCategory(categ);
                Log.d(TAG, "OnItemSelectedListener: CATEG = " + categ.toString());
                Toast.makeText(getActivity(),
                        spinner.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            Toast.makeText(getActivity(),
                    "Nothing selected.", Toast.LENGTH_SHORT).show();
        }
    };

    // responds to event generated when user saves a contact
    View.OnClickListener savePaymentButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mTitleEditText.getText().toString().trim().length() != 0)
            {
                savePayment();
            }
            else // required title of pay is blank, so display error dialog
            {
                Log.d(TAG, "required_pay_title");
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage("Error: Title of Pay missing!");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        } // end method onClick
    }; // end OnClickListener savePaymentButtonClicked

    // saves contact information to the database
    private void savePayment()
    {
        // mCallbacks.onPayModifyed(mPay);
        preparePayItem();
        PaymentLab pl = PaymentLab.get(getActivity());

        if (mEditMode) {
            pl.updatePay(mPay);
            // mCallbacks.onRefreshMainActivity();
        }
        else {
            pl.addPay(mPay);
            mSavePaymentButton.setEnabled(false);
        }
    }

    private void preparePayItem()
    {
        mPay.setTitle(mTitleEditText.getText().toString());
        mPay.setNameOfBank(mBankEditText.getText().toString());
        mPay.setDescription(mDescrEditText.getText().toString());
        mPay.setAccountId(mAccountEditText.getText().toString());

        mPay.setDayOfMonth(Integer.parseInt(mDayEditText.getText().toString()));
        mPay.setPayPeriod(Integer.parseInt(mPeriodEditText.getText().toString()));

        mPay.setTotalSumm(Double.parseDouble(mTotalEditText.getText().toString()));

        int nUSD = (mCheckBoxUSD.isChecked()) ? 1 : 0;
        mPay.setCurrency(nUSD);

        // Only the "category" field was setted BEFORE call the "preparePayItem"
        Category categ = mPay.getCategory();
        mSpinnerCategory.setSelection(categ.ordinal());
    }


}
