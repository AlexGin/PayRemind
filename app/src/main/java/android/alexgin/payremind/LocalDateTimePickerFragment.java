package android.alexgin.payremind;

import android.app.AlertDialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;

public class LocalDateTimePickerFragment extends DialogFragment {
    private static final String TAG = "LocalDateTimePickerFragment";
    public static final String EXTRA_DATE_TIME =
            "android.alexgin.payremind.date_time";

    private static final String ARG_LDT = "localdatetime";

    private DatePicker mDatePicker;
    private EditText mHoursEditText;
    private EditText mMinutesEditText;

    public static LocalDateTimePickerFragment newInstance(LocalDateTime ldt) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LDT, ldt);

        LocalDateTimePickerFragment fragment = new LocalDateTimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDateTime dt = (LocalDateTime) getArguments().getSerializable(ARG_LDT);

        int year = dt.getYear();
        int month = dt.getMonthValue() - 1; // Zero-based value (January->0)
        int day = dt.getDayOfMonth();
        int hour = dt.getHour();
        int minute = dt.getMinute();

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_local_date_time, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        mHoursEditText = (EditText) v.findViewById(R.id.pay_exec_hours);
        mMinutesEditText = (EditText) v.findViewById(R.id.pay_exec_minutes);

        mHoursEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mMinutesEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Integer iHour = hour;
        Integer iMinute = minute;
        mHoursEditText.setText(iHour.toString());
        mMinutesEditText.setText(iMinute.toString());

        AlertDialog.Builder bldr = new AlertDialog.Builder(getActivity());
        bldr.setView(v);
        bldr.setTitle(R.string.time_picker_title);
        bldr.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth() + 1;
                        int day = mDatePicker.getDayOfMonth();
                        int hour = Integer.parseInt(mHoursEditText.getText().toString());
                        int minute = Integer.parseInt(mMinutesEditText.getText().toString());
                        LocalDateTime local_date_time = LocalDateTime.of(year, month, day, hour, minute, 0);
                        sendResult(Activity.RESULT_OK, local_date_time);
                    }
                });
        Dialog dlg = bldr.create();
        return dlg;
    }

    private void sendResult(int resultCode, LocalDateTime date_time) {
        if (getTargetFragment() == null) {
            Log.d(TAG, "sendResult: getTargetFragment() INVALID!");
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE_TIME, date_time);
        Log.d(TAG, "sendResult: OK!");
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
