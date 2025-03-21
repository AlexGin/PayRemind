package android.alexgin.payremind;

import android.alexgin.payremind.database.PaymentBaseHelper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import android.text.format.DateFormat;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.content.res.AssetManager;
import android.content.DialogInterface;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PayListFragment extends Fragment
    {
    private static final String ARG_PAY_ID = "payment";
    private static final String EXTRA_PAYMENT_ID =
            "android.alexgin.payremind.payment_id";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String TAG = "PayListFragment";
    private static final int REQUEST_EDIT_RESULT = 1;
    private RecyclerView mPaymentsRecyclerView;
    private ItemDivider mItemDivider;
    private PayAdapter mAdapter;
    private boolean mSubtitleVisible;

    public static List<String> gCategoryPathList; // File-name list of Category icons

    public static PayListFragment getNewInstance(Object obj) {
        PayListFragment fragment = new PayListFragment();
        // Bundle args = new Bundle();
        // args.putParcelable(MainActivity.ARG_RESULT_CODE, (Parcelable)obj);
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        gCategoryPathList = new ArrayList<>();
        PrapareCategoryList();
    }
    private void PrapareCategoryList()
    {
        AssetManager assets = getActivity().getAssets();
        gCategoryPathList.clear();
        try {
            for (Category cat : Category.values()) {
                String strCategory = cat.toString();
                String[] paths = assets.list(strCategory);
                String strFNPath = paths[0];
                String strFN = strFNPath.replace(".png", "");
                gCategoryPathList.add(strFN);
            }
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate_1");
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate_2");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pay_list, container, false);

        mPaymentsRecyclerView = (RecyclerView) view.findViewById(R.id.payment_recycler_view);
        mPaymentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
            Log.d(TAG, "onCreateView_2a");
        }

        updateUI();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_pay_list_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_pay:
                dispalyAddEditForm();
                Log.d(TAG, "onOptionsItemSelected_new_X");
                return true;

            case R.id.total_per_month:
                showTotalPerMonth();
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            case R.id.clear_execute_flags:
                prepareToClearAllExecuteFlags();
                // updateUI();
                return true;

            case R.id.clear_last_dates:
                prepareToClearAllLastDates();
                // clearAllLastDates();
                // clearAllExecuteFlags();
                // updateUI();
                return true;

            case R.id.unexecuted_table:
                /* PaymentLab.get(getActivity()).purgeTable(); */
                ShowUnexecuted();
                //updateUI();
                return true;

            case R.id.currency_info:
                ShowCurrencyInfo();
                return true;

            case R.id.application_tune:
                ShowSettingsFragment();
                return true;

            case R.id.show_app_info:
                showAppInfo();
                return true;

            // case R.id.menu_show_about:
            //    AboutBox.Show(getActivity());
            //    return true;

            case R.id.menu_exit:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowSettingsFragment()
    {
        PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.SETTINGS_SCREEN, null);
    }

    public void ShowCurrencyInfo()
    {
        // MainActivity ma = (MainActivity)getActivity();
        // ma.ProcesCurrencyInfo();
        PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.CURRENCY_SCREEN, null);
    }

    public void ShowUnexecuted()
    {
        List<UUID> listUnexec = PRApplication.INSTANCE.PrefetchUnexecutedPays();
        int nSize = listUnexec.size();
        if (nSize == 0)
        {
            String sUnexec = "Отсутствуют Неоплаченные платежи";
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(sUnexec);
            builder.setIcon(R.drawable.my_dog); // It's my dog KING :)
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    // for exit from this Activity:
                    // finish();
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("The Pay Remind Project");
            alert.show();
        }
        else if (nSize > 0) {
            displayUnexecutedList();
        }
    }

    private void prepareToClearAllExecuteFlags()
    {
        FragmentManager manager = getFragmentManager();
        // Value n_mode == 1 - clear ONLY flags (Execute-Flags)
        ClearDialogFragment dialog = ClearDialogFragment.newInstance(1);
        // dialog.setTargetFragment(MainActivity.this, REQUEST_TIME);
        dialog.show(manager, "Dialog1");
    }

    private void prepareToClearAllLastDates()
    {
        FragmentManager manager = getFragmentManager();
        // Value n_mode == 2 - clear flags (Execute-Flags) and Last-dates
        ClearDialogFragment dialog = ClearDialogFragment.newInstance(2);
        // dialog.setTargetFragment(MainActivity.this, REQUEST_TIME);
        dialog.show(manager, "Dialog1");
    }

    private void clearAllExecuteFlags()
    {
        PaymentLab lab = PaymentLab.get(getActivity());
        ArrayList<Pay> payments = (ArrayList<Pay>)PaymentLab.get(getActivity()).getPayments();
        for(Pay pay : payments)
        {
            pay.setExecuted(0); // (false);
            lab.updatePay(pay);
        }
    }

    private void clearAllLastDates()
    {
        PaymentLab lab = PaymentLab.get(getActivity());
        ArrayList<Pay> payments = (ArrayList<Pay>)PaymentLab.get(getActivity()).getPayments();
        for(Pay pay : payments)
        {
            pay.clearExecDate();
            lab.updatePay(pay);
        }
    }

    private void displayUnexecutedList()
    {
        PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.UNEXEC_SCREEN, null);
    }

    private void dispalyAddEditForm()
    {
        PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.ADD_EDIT_SCREEN, null);
    }

    private void updateSubtitle() {
        PaymentLab paymentLab = PaymentLab.get(getActivity());
        int paysCount = paymentLab.getPayments().size();
        String subtitle = getString(R.string.subtitle_format, paysCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        PaymentLab pl = PaymentLab.get(getActivity());
        ArrayList<Pay> payments = (ArrayList<Pay>) pl.getPayments();
        
        if (mItemDivider == null) {
            mItemDivider = new ItemDivider(getContext());
            mPaymentsRecyclerView.addItemDecoration(mItemDivider);
        }
        
        if (mAdapter == null) {
            mAdapter = new PayAdapter(payments);
            mPaymentsRecyclerView.setAdapter((RecyclerView.Adapter)mAdapter);
        } else {
            mAdapter.setPayments(payments);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void showTotalPerMonth()
    {
        double dbSumm = prepareTotalSummPerMonth();
        String strTotalPerMonth = String.format("Всего за месяц: %7.2f руб", dbSumm);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(strTotalPerMonth);
        // builder.setIcon(R.drawable.ic_executed);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Общая сумма оплат за месяц");
        alert.show();
    }

    private void showAppInfo() {
        String sVersion = String.format("Version: v 1.27.2.25");
        sVersion += (PaymentBaseHelper.USING_COPY_DB == 1) ?
         String.format(" DB from assets") : String.format(" Normal DB work");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(sVersion);
        builder.setIcon(R.drawable.my_dog); // It's my dog KING :)
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // for exit from this Activity:
                // finish();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("The Pay Remind Project");
        alert.show();
    }

    private double prepareTotalSummPerMonth() {
        LocalDate localDate = LocalDate.now();
        int mntCurrMonth = localDate.getMonthValue();
        int mntCurr = mntCurrMonth - 1; // Zero-based month
        Log.d(TAG, "prepareTotalSummPerMonth (Month) " + mntCurr);

        PaymentLab pl = PaymentLab.get(getActivity());
        double currency_value = pl.getCurrencyValue();
        List<Pay> payments = pl.getPayments();

        double dbTotal = 0.0;
        for(Pay pay : payments)
        {
            UUID id = pay.getId();
            boolean bValidArray = false; // Pay if array is not initialized
            boolean bPayInThisMonth = false; // Without use Schedule
            Schedule shd = pl.getSchedule(id);
            if (shd != null) {
                int[] arr = shd.getInternalArray();
                bValidArray = IsValidArray(arr);
                bPayInThisMonth = bValidArray && (arr[mntCurr] > 0);
            }
            boolean bNoLocked = (boolean)(2 != pay.getExecuted()); // No Locked record
            if (((!bValidArray) || bPayInThisMonth) && bNoLocked) {
                int n_curr = pay.getCurrency();
                double dbSumm = (n_curr == 1) ?
                        pay.getTotalSumm() * currency_value :
                        pay.getTotalSumm();
                dbTotal += dbSumm;
                // Log.d(TAG, "prepareTotalSummPerMonth: Added: " + dbSumm);
            }
        }
        return dbTotal;
    }
    private boolean IsValidArray(int[] array)
    {
        boolean bResult = false;
        for (int val : array)
        {
            if (val > 0)
                bResult = true;
        }
        return bResult;
    }

    private class PayHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Pay mPay;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mDateLastTextView;
        private ImageView mExecutedImageView;
        private ImageView mCategoryImageView;

        public PayHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_pay, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.pay_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.pay_date);
            mDateLastTextView = (TextView) itemView.findViewById(R.id.last_pay_date);
            mCategoryImageView = (ImageView) itemView.findViewById(R.id.pay_category);
            mExecutedImageView = (ImageView) itemView.findViewById(R.id.pay_executed);
        }

        public void bind(Pay pay) {
            mPay = pay;
            mTitleTextView.setText(mPay.getTitle());

            LocalDate localD = mPay.getDate();
            DateTimeFormatter formatterD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String strDateOut = (String)localD.format(formatterD);
            mDateTextView.setText(strDateOut);

            LocalDateTime localDT = mPay.getExecDate();
            int year = localDT.getYear();
            if (year > 1970) {
                DateTimeFormatter formatterDT = DateTimeFormatter.ofPattern("(yyyy-MM-dd HH:mm)");
                String strLastDateOut = (String) localDT.format(formatterDT);
                mDateLastTextView.setText(strLastDateOut);
            }
            else
                mDateLastTextView.setText("");
            int nExecuted = pay.getExecuted();
            if (nExecuted == 0) {
                mExecutedImageView.setVisibility(View.INVISIBLE); // ? View.VISIBLE : View.GONE);
            }
            else if (nExecuted == 1) {
                mExecutedImageView.setVisibility(View.VISIBLE);
                mExecutedImageView.setScaleX(1.0f);
                mExecutedImageView.setScaleY(1.0f);
            }
            else {
                // Log.d(TAG, "PayHolder.bind LOCKED - Executed: " + nExecuted);
                mExecutedImageView.setVisibility(View.VISIBLE);
                mExecutedImageView.setScaleX(0.5f);
                mExecutedImageView.setScaleY(0.5f);
            }
            // Category of Pay:
            Category cat = pay.getCategory();
            int iCategIndex = cat.ordinal();
            AssetManager assets = getActivity().getAssets();
            String strImage = PayListFragment.gCategoryPathList.get(iCategIndex);
            try (InputStream stream =
                  assets.open(cat.toString() + "/" + strImage + ".png")) {
                // load the asset as a Drawable and display on the ImageView
                Drawable categ = Drawable.createFromStream(stream, strImage);
                mCategoryImageView.setImageDrawable(categ);
            }
            catch (IOException exception) {
                Log.e(TAG, "Error loading: PayHolder.bind - strImage", exception);
            }
        }

        @Override
        public void onClick(View view) {
            PRApplication.INSTANCE.getRouter().navigateTo(MainActivity.PAYMENT_SCREEN, (Object)mPay);
        }
    }

    private class PayAdapter extends RecyclerView.Adapter<PayHolder> {

        private List<Pay> mPayments;

        public PayAdapter(List<Pay> payments)
        {
            mPayments = payments;
        }

        @Override
        public PayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PayHolder(layoutInflater, parent);
        }

        public void setPayments(List<Pay> payments) {
            mPayments = payments;
        }

        @Override
        public void onBindViewHolder(PayHolder holder, int position) {
            Pay pay = mPayments.get(position);
            holder.bind(pay);
        }

        @Override
        public int getItemCount() {
            return mPayments.size();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
    }
}
