package android.alexgin.payremind;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class
Pay implements Parcelable {
    private static final String TAG = "Pay";
    public static final int DM_OFFSET = 100;
    private UUID mId;
    private String mTitle;        // Title (Name of Payment)
    private String mNameOfBank;   // Bank's name (or company's name)
    private String mDescription;  // Description (comment to this paymrnt)
    private String mAccountId;    // Number (or identifier) of account
    private LocalDate mDate;           // Last Payment date (01.01.1970 - if unknown)
    private int mDayOfMonth;      // Payment: day of Month
    private int mPayPeriod;       // Period (in month) of this Payment
    private Category mCategory;   // Category of this Payment
    private double mTotalSumm;    // Total Summ of Payment (0.0 - if unknown)
    private int mCurrency;        // 0->BYN; 1->USD
    private boolean mExecuted;    // Current Pay (in current month) executed

    private LocalDateTime mExecDate;  // Last Payment date (01.01.1970 - if unknown)

    public Pay()
    {
        mId = UUID.randomUUID();
        mDate = LocalDate.now();
        mDayOfMonth = DM_OFFSET;
        mPayPeriod = 1; // By default: one month
        mTotalSumm = 0.0; // Total Summ: 0.0 - unknown
        mCategory = Category.UNKNOWN;
        mCurrency = 0;
        mExecDate = LocalDateTime.of(1970, Month.JANUARY, 1, 3, 0, 0);
    }

    public Pay(UUID id) {
        mId = id;
        mDate = LocalDate.now();
        mDayOfMonth = DM_OFFSET;
        mPayPeriod = 1; // By default: one month
        mTotalSumm = 0.0; // Total Summ: 0.0 - unknown
        mCategory = Category.UNKNOWN;
        mCurrency = 0;
        mExecDate = LocalDateTime.of(1970, Month.JANUARY, 1, 3, 0, 0);
    }

    public Pay(int n) { // DEBUG !!!
        if (n == 42) {
            mId = UUID.randomUUID();
            mDate = LocalDate.now();
            mDayOfMonth = DM_OFFSET;
            mPayPeriod = 1; // DEBUG !!!
            mDayOfMonth = 3; // DEBUG !!!
            mTotalSumm = 1234.56; // DEBUG !!!
            mCurrency = 0;
            mCategory = Category.HOUSE;
            mExecDate = LocalDateTime.now();
            mTitle = "Test";
            mNameOfBank = "Banc-test";
            mDescription = "Testing";
            mAccountId = "12345T";
        }
    }

    public Pay(Parcel in)
    {
        mId = (UUID)in.readValue(null);
 
        mDate = (LocalDate)in.readSerializable();
        String[] data = new String[4];
        in.readStringArray(data);
        mTitle = data[0];
        mNameOfBank = data[1];
        mDescription = data[2];
        mAccountId = data[3];

        int[] ints = new int[4];
        in.readIntArray(ints);
        mDayOfMonth = ints[0];
        mPayPeriod = ints[1];
        int n_categ = ints[2];
        mCurrency = ints[3];

        mCategory = (Category.values())[0]; //[n_categ];
        mTotalSumm = in.readDouble();
        mExecuted = in.readBoolean();
    }

    public boolean isExecuted() {
        return mExecuted;
    }

    public void setExecuted(boolean Executed) {
        this.mExecuted = Executed;
    }

    public LocalDate getDate() {
        return mDate;
    }

    public void setDate(LocalDate mDate) {
        this.mDate = mDate;
    }

    public LocalDateTime getExecDate() {
        return mExecDate;
    }
    public void clearExecDate() {
        LocalDateTime ldt = LocalDateTime.of(1970, Month.JANUARY, 1, 3, 0, 0);
        this.mExecDate = ldt;
    }
    public void setExecDate(LocalDateTime mExecDate) {this.mExecDate = mExecDate; }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public UUID getId() {
        return mId;
    }

    public Category getCategory() { return mCategory; }

    public void setCategory(Category categ) { this.mCategory = categ; }

    public String getDescription() { return mDescription; }

    public void setDescription(String description) { mDescription = description; }

    public String getNameOfBank() {
        return mNameOfBank;
    }

    public void setNameOfBank(String nameOfBank) {
        this.mNameOfBank = nameOfBank;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountid) {
        this.mAccountId = accountid;
    }

    public int getDayOfMonth() {
        Log.d(TAG, "getDayOfMonth = " + mDayOfMonth);
        return mDayOfMonth;
    }

    public void setDayOfMonth(int dayofmonth) {
        this.mDayOfMonth = dayofmonth;
    }

    public int getPayPeriod() {
        return mPayPeriod;
    }

    public void setPayPeriod(int payperiod) {
        this.mPayPeriod = payperiod;
    }

    public double getTotalSumm() {
        return mTotalSumm;
    }

    public void setTotalSumm(double totalsumm) {
        this.mTotalSumm = totalsumm;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public int getCurrency() { return mCurrency; }

    public void setCurrency(int n_currency) { this.mCurrency = n_currency; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeStringArray(new String[]{mTitle, mNameOfBank, mDescription, mAccountId});
        dest.writeSerializable(mDate);
        int n_categ = mCategory.ordinal();
        dest.writeIntArray(new int[]{mDayOfMonth, mPayPeriod, n_categ, mCurrency});
        dest.writeDouble(mTotalSumm);
        dest.writeBoolean(mExecuted);
        dest.writeSerializable(mExecDate);
    }

    public static final Creator<Pay> CREATOR = new Creator<Pay>() {
        @Override
        public Pay[] newArray(int size) {
            return new Pay[size];
        }

        @Override
        public Pay createFromParcel(Parcel source) {
            return new Pay(source);
        }
    };
}
