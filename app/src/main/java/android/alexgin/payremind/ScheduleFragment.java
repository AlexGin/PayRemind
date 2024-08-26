package android.alexgin.payremind;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.os.Parcelable;
import android.os.Bundle;

import android.util.Log;

import java.util.UUID;

public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private Schedule mSchedule;

    private TextView mTextView;

    private CheckBox mCheckBoxJAN;
    private CheckBox mCheckBoxFEB;
    private CheckBox mCheckBoxMAR;
    private CheckBox mCheckBoxAPR;
    private CheckBox mCheckBoxMAY;
    private CheckBox mCheckBoxJUN;
    private CheckBox mCheckBoxJUL;
    private CheckBox mCheckBoxAUG;
    private CheckBox mCheckBoxSEP;
    private CheckBox mCheckBoxOCT;
    private CheckBox mCheckBoxNOV;
    private CheckBox mCheckBoxDEC;

    private Button mButtonSave;

    public static ScheduleFragment getNewInstance(Object obj) {
        ScheduleFragment fragment = new ScheduleFragment();
        if (obj != null) {
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.ARG_RESULT_CODE, (Parcelable) obj);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate_0");

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            mSchedule = args.getParcelable(MainActivity.ARG_RESULT_CODE);
        }
        Log.d(TAG, "onCreate_1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        mTextView = (TextView) v.findViewById(R.id.textViewSchedule);

        mCheckBoxJAN = (CheckBox) v.findViewById(R.id.checkBoxJAN);
        mCheckBoxFEB = (CheckBox) v.findViewById(R.id.checkBoxFEB);
        mCheckBoxMAR = (CheckBox) v.findViewById(R.id.checkBoxMAR);
        mCheckBoxAPR = (CheckBox) v.findViewById(R.id.checkBoxAPR);
        mCheckBoxMAY = (CheckBox) v.findViewById(R.id.checkBoxMAY);
        mCheckBoxJUN = (CheckBox) v.findViewById(R.id.checkBoxJUN);
        mCheckBoxJUL = (CheckBox) v.findViewById(R.id.checkBoxJUL);
        mCheckBoxAUG = (CheckBox) v.findViewById(R.id.checkBoxAUG);
        mCheckBoxSEP = (CheckBox) v.findViewById(R.id.checkBoxSEP);
        mCheckBoxOCT = (CheckBox) v.findViewById(R.id.checkBoxOCT);
        mCheckBoxNOV = (CheckBox) v.findViewById(R.id.checkBoxNOV);
        mCheckBoxDEC = (CheckBox) v.findViewById(R.id.checkBoxDEC);

        PrepareCheckArray();
        PrepareTitle();

        mButtonSave = (Button) v.findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PrepareSchedule();
               SaveSchedule();
            }
        });
        return v;
    }

    private void PrepareSchedule() {
        if (mSchedule != null) {
            int[] months = new int[12];
            months[0] = mCheckBoxJAN.isChecked() ? 1 : 0;
            months[1] = mCheckBoxFEB.isChecked() ? 1 : 0;
            months[2] = mCheckBoxMAR.isChecked() ? 1 : 0;
            months[3] = mCheckBoxAPR.isChecked() ? 1 : 0;
            months[4] = mCheckBoxMAY.isChecked() ? 1 : 0;
            months[5] = mCheckBoxJUN.isChecked() ? 1 : 0;
            months[6] = mCheckBoxJUL.isChecked() ? 1 : 0;
            months[7] = mCheckBoxAUG.isChecked() ? 1 : 0;
            months[8] = mCheckBoxSEP.isChecked() ? 1 : 0;
            months[9] = mCheckBoxOCT.isChecked() ? 1 : 0;
            months[10] = mCheckBoxNOV.isChecked() ? 1 : 0;
            months[11] = mCheckBoxDEC.isChecked() ? 1 : 0;

            mSchedule.MonthInit(months);
        }
    }

    private void PrepareCheckArray() {
        if (mSchedule != null) {
            mCheckBoxJAN.setChecked(mSchedule.getJanuary() == 1);
            mCheckBoxFEB.setChecked(mSchedule.getFebruary() == 1);
            mCheckBoxMAR.setChecked(mSchedule.getMarch() == 1);
            mCheckBoxAPR.setChecked(mSchedule.getApril() == 1);
            mCheckBoxMAY.setChecked(mSchedule.getMay() == 1);
            mCheckBoxJUN.setChecked(mSchedule.getJune() == 1);
            mCheckBoxJUL.setChecked(mSchedule.getJuly() == 1);
            mCheckBoxAUG.setChecked(mSchedule.getAugust() == 1);
            mCheckBoxSEP.setChecked(mSchedule.getSeptember() == 1);
            mCheckBoxOCT.setChecked(mSchedule.getOctober() == 1);
            mCheckBoxNOV.setChecked(mSchedule.getNovember() == 1);
            mCheckBoxDEC.setChecked(mSchedule.getDecember() == 1);
        }
    }

    private void SaveSchedule() {
        PaymentLab pl = PaymentLab.get(getActivity());
        pl.deleteItemSchedule(mSchedule.getId());
        pl.addSchedule(mSchedule);
        mButtonSave.setEnabled(false);
    }

    private void PrepareTitle() {
        if (mSchedule != null) {
            UUID id = mSchedule.getId();
            PaymentLab lab = PaymentLab.get(getActivity());
            String strTitle = lab.getPay(id).getTitle();
            mTextView.setText(strTitle);
            Log.d(TAG, "PrepareTitle_0: UUID = " + id.toString());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_schedule_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_set_null_schedule:
                // DEBUG !!!
                PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.LIST_SCREEN, null);
                return true;

            case R.id.menu_item_set_all_schedule:
                // DEBUG !!!
                PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.LIST_SCREEN, null);
                return true;

            case R.id.menu_item_delete_schedule:
                // DEBUG !!!
                PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.LIST_SCREEN, null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
