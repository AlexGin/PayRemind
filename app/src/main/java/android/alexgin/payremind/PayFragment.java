package android.alexgin.payremind;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.graphics.Bitmap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.FileProvider;

import android.os.Parcelable;
import android.text.InputType;
import android.view.Menu; // Added by AlexGin
import android.view.MenuInflater; // Added by AlexGin
import android.view.MenuItem; // Added by AlexGin
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener; // Added by AlexGin
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.provider.ContactsContract;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.database.Cursor;
import android.provider.MediaStore;
import android.content.pm.ResolveInfo;
import android.content.Context;
import android.util.Log; // Added by AlexGin
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
public class PayFragment extends Fragment {
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_TIME = 3;
    public static final String SCHEDULE_SCREEN = "schedule screen";
    private static final String TAG = "PayFragment";

    private Pay mPay;
    private EditText mTitleField;
    private EditText mDescrField;
    private EditText mBankField;
    private EditText mAccntField;
    private EditText mTotalField;
    private EditText mDateField;
    private EditText mPeriodField;
    private Button mPayLastTimestampButton;
    private Button mPaySetTimestamp;
    private Button mPayClearTimestamp;
    private Button mPayScheduleButton; // Added 23.01.2022
    private Button mLockedButton; // Added 15.02.2025
    private ImageButton mPhotoButton; // Added 18.08.2021
    private ImageView mPhotoView;     // Added 18.08.2021
    private File mPhotoFile;          // Added 18.08.2021
    private CheckBox mExecutedCheckbox;
    private FloatingActionButton mFab;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static PayFragment getNewInstance(Object obj) {
        PayFragment fragment = new PayFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.ARG_RESULT_CODE, (Parcelable)obj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if ((bundle != null) && (mPay == null)) {
            mPay = bundle.getParcelable(MainActivity.ARG_RESULT_CODE);
        }
        mPhotoFile = PaymentLab.get(getActivity()).getPhotoFile(mPay);
        if (mPhotoFile != null)
            Log.d(TAG, "onCreate_0: PhotoFile - OK!");
        else
            Log.d(TAG, "onCreate_0: PhotoFile - Invalid!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay, container, false);

        mTitleField = (EditText) v.findViewById(R.id.pay_title);
        mTitleField.setText(mPay.getTitle());
        mTitleField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        mDescrField = (EditText) v.findViewById(R.id.pay_description);
        mDescrField.setText(mPay.getDescription());
        mDescrField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        mBankField = (EditText) v.findViewById(R.id.pay_bank_name);
        mBankField.setText(mPay.getNameOfBank());
        mBankField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        mAccntField = (EditText) v.findViewById(R.id.pay_account_id);
        mAccntField.setText(mPay.getAccountId());
        mAccntField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        Double dbTotalRaw = mPay.getTotalSumm();
        StringBuilder sb = new StringBuilder(dbTotalRaw.toString());
        int iCurrency = mPay.getCurrency();
        if (iCurrency > 0) {
            sb.append(" (USD)");
            Log.d(TAG, "Currency > 0");
        }
        mTotalField = (EditText) v.findViewById(R.id.pay_total_summ);
        mTotalField.setText(sb.toString());
        mTotalField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        int nDate = mPay.getDayOfMonth();
        String strDate = String.format("День месяца (дата): %d", nDate);
        Log.d(TAG, strDate);

        // Integer iDate = mPay.getDayOfMonth();
        mDateField = (EditText) v.findViewById(R.id.pay_date_day);
        mDateField.setText(strDate);
        mDateField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        int nPeriod = mPay.getPayPeriod();
        String strPer = String.format("Период (месяцев): %d", nPeriod);
        Log.d(TAG, strPer);

        // configure FAB to hide keyboard and initiate web service request
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setOnClickListener(fabButtonClicked);

        // Integer iPeriod = mPay.getPayPeriod();
        mPeriodField = (EditText) v.findViewById(R.id.pay_period);
        mPeriodField.setText(strPer);
        mPeriodField.setInputType(InputType.TYPE_NULL); // Set read-only mode

        mPayLastTimestampButton = (Button) v.findViewById(R.id.pay_last_timestamp);
        mPayLastTimestampButton.setOnClickListener(paylastButtonClicked);

        /*mTimeButton = (Button) v.findViewById(R.id.pay_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mPay.getDate());
                dialog.setTargetFragment(PayFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });*/

        mPayScheduleButton = (Button) v.findViewById(R.id.pay_schedule);
        mPayScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uuid = mPay.getId();
                int n_period = mPay.getPayPeriod();
                Schedule shd = (n_period == 1) ? // Period of pay one month?
                        new Schedule(uuid, 1) :
                        new Schedule(uuid);
                if(n_period != 1)
                {
                    FillScheduleFromDb(shd);
                }
                PRApplication.INSTANCE.getRouter().navigateTo(SCHEDULE_SCREEN, (Object)shd);
            }
        });

        mPaySetTimestamp = (Button) v.findViewById(R.id.pay_set_timestamp);
        mPaySetTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                LocalDateTime dt = mPay.getExecDate();
                int year = dt.getYear();
                LocalDateTimePickerFragment dialog = (year != 1970) ?
                        LocalDateTimePickerFragment.newInstance(dt) :
                        LocalDateTimePickerFragment.newInstance(LocalDateTime.now());
                dialog.setTargetFragment(PayFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mPayClearTimestamp = (Button) v.findViewById(R.id.pay_clear_timestamp);
        mPayClearTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPay.clearExecDate();
                if (2 == mPay.getExecuted()) // If was locked - clear it
                    mPay.setExecuted(0);
                updatePay();
            }
        });

        mExecutedCheckbox = (CheckBox) v.findViewById(R.id.pay_executed);
        mExecutedCheckbox.setChecked(1 == mPay.getExecuted());
        mExecutedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (2 != mPay.getExecuted()) {
                    int nChecked = isChecked ? 1 : 0;
                    mPay.setExecuted(nChecked);
                    if (isChecked) {
                        mPay.setExecDate(LocalDateTime.now());
                    }
                }
                updatePay();
            };
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
           /* mSuspectButton.setEnabled(false);*/
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.pay_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && // Return to old codes: 15.12.2024
                 captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        Log.d(TAG, "onCreateView: canTakePhoto = " + canTakePhoto);
        // see:
        // https://stackoverflow.com/questions/56598480/couldnt-find-meta-data-for-provider-with-authority
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                  BuildConfig.APPLICATION_ID + ".provider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                int n_ca_size = cameraActivities.size();
                Log.d(TAG, "onCreateView: cameraActivities.size = " + n_ca_size);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mLockedButton = (Button) v.findViewById(R.id.pay_set_locked);
        mLockedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nLocked = 2;
                mPay.setExecuted(nLocked);
                mPay.setExecDate(LocalDateTime.now());
                updatePay();
                mLockedButton.setEnabled(false);
                mExecutedCheckbox.setChecked(true);
                mExecutedCheckbox.setEnabled(false);
            }
        });
        if (2 == mPay.getExecuted()) {
            mLockedButton.setEnabled(false);
            mExecutedCheckbox.setChecked(true);
            mExecutedCheckbox.setEnabled(false);
        }

        mPhotoView = (ImageView) v.findViewById(R.id.pay_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                String strPhoteFileName = mPhotoFile.getPath();
                PhotoViewFragment dialog = PhotoViewFragment.newInstance(strPhoteFileName);
                // dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });

        updatePhotoView();

        return v;
    }

    public void FillScheduleFromDb(Schedule shd) {
        PaymentLab pl = PaymentLab.get(getActivity());
        UUID uuid = shd.getId();
        Schedule schedule = pl.getSchedule(uuid);
        if (schedule != null) {
            int[] arr = schedule.getInternalArray();
            shd.MonthInit(arr);
        }
        else
        {
            Log.e(TAG, "FillScheduleFromDb: Error");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        PaymentLab pl = PaymentLab.get(getActivity());
        pl.updatePay(mPay);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (resultCode != Activity.RESULT_OK) {
        //    return;
        // }
        Log.d(TAG, "onActivityResult_0");

        if (requestCode == REQUEST_DATE) {
            LocalDateTime local_date_time =
              (LocalDateTime)data.getSerializableExtra(LocalDateTimePickerFragment.EXTRA_DATE_TIME);

            int day = local_date_time.getDayOfMonth();
            Log.d(TAG, "onActivityResult_1: day_of_month: " + day);

            mExecutedCheckbox.setChecked(true);

            mPay.setExecuted(1); // (true); // Need to set "true" before call the "setExecDate"
            mPay.setExecDate(local_date_time);
        }
    }

    private void updatePay() {
        PaymentLab.get(getActivity()).updatePay(mPay);
        // mCallbacks.onPayUpdated(mPay);
    }

    /*private void updateDate() {
        int year = mPay.getDate().getYear() + 1900; // Base year: 1900
        int month = mPay.getDate().getMonth() + 1; // Zero based month
        int day = mPay.getDate().getDate(); // Day of month
        String strDate = String.format("%02d.%02d.%d", day, month, year);
        mDateButton.setText(strDate);
    }*/

    /*private void updateTime() {
        int hours = mPay.getDate().getHours();
        int minutes = mPay.getDate().getMinutes();
        String strTime = String.format("%02d:%02d", hours, minutes);
        mTimeButton.setText(strTime);
    }*/

    // Inflate menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit_delete_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.LIST_SCREEN, null);
                return true;

            case R.id.menu_item_delete_pay:
                MainActivity ma = (MainActivity)(getActivity());
                ma.setUUID(mPay.getId());
                prepareToItemDelatePay();
                // PaymentLab paymentLab = PaymentLab.get(getActivity());
                // paymentLab.deleteItem(mPay.getId());

                //  PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.LIST_SCREEN, null);
                return true;

            case R.id.menu_item_edit_pay:
                dispalyAddEditForm(mPay);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareToItemDelatePay()
    {
        FragmentManager manager = getFragmentManager();
        // Value n_mode == 2oo - delete record in payment table of DB
        ClearDialogFragment dialog = ClearDialogFragment.newInstance(200);
        dialog.show(manager, "Dialog1");
    }

    private void dispalyAddEditForm(Pay pay)
    {
        PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.ADD_EDIT_SCREEN, (Object)pay);
    }

    private void updatePhotoView() {
        Log.d(TAG, "updatePhotoView_0");
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            Log.d(TAG, "updatePhotoView: NULL");
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
            Log.d(TAG, "updatePhotoView: file-path=" + mPhotoFile.getPath());
        }
    }

    private String LastPayTimestampAsText()
    {
        LocalDateTime localDT = mPay.getExecDate();
        int year = localDT.getYear();
        if (year > 1970) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String strText = localDT.format(formatter);
            return strText;
        }
        else
            return "";
    }

    private String CalculateTotalSumm() {
        int curr = mPay.getCurrency();
        PaymentLab pl = PaymentLab.get(getActivity());
        double dbCurrVal = pl.getCurrencyValue();
        double dbResult = (curr == 1) ?
                mPay.getTotalSumm() * dbCurrVal :
                mPay.getTotalSumm();
        String strOut = String.format("%7.2f руб", dbResult);
        return strOut;
    }

    // responds to event generated when user request a cool currency from the bank (NBRB)
    View.OnClickListener fabButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String strResult = CalculateTotalSumm();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(strResult);

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle("Полная сумма оплаты (BYN)");
            alert.show();
        } // end method onClick
    };

    View.OnClickListener paylastButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String strResult = LastPayTimestampAsText();
            if (strResult.isEmpty())
                strResult = "Не зафиксировано даты и времени";
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(strResult);

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle("Последний платёж был проведен");
            alert.show();
        } // end method onClick
    };
}
