package android.alexgin.payremind;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Schedule implements Parcelable {
    private UUID mId;
    private int mJanuary;
    private int mFebruary;
    private int mMarch;
    private int mApril;
    private int mMay;
    private int mJune;
    private int mJuly;
    private int mAugust;
    private int mSeptember;
    private int mOctober;
    private int mNovember;
    private int mDecember;

    public Schedule(UUID id)
    {
        mId = id;
        int[] months = new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        MonthInit(months);
    }

    public Schedule(UUID id, int n_flag)
    {
        mId = id;
        int[] months = new int[12];
        for (int i = 0; i < 12; ++i)
        {
            months[i] = n_flag;
        }
        MonthInit(months);
    }

    public Schedule(UUID id, int[] mnts)
    {
        mId = id;
        MonthInit(mnts);
    }

    public Schedule(Parcel in) {
        mId = (UUID) in.readValue(null);
        int[] months = new int[12];
        in.readIntArray(months);
        MonthInit(months);
    }

    public void MonthInit(int[] mnts) {
        mJanuary = mnts[0];
        mFebruary= mnts[1];
        mMarch = mnts[2];
        mApril = mnts[3];
        mMay = mnts[4];
        mJune = mnts[5];
        mJuly = mnts[6];
        mAugust = mnts[7];
        mSeptember = mnts[8];
        mOctober = mnts[9];
        mNovember = mnts[10];
        mDecember = mnts[11];
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public int getJanuary() {
        return mJanuary;
    }

    public void setJanuary(int mJanuary) {
        this.mJanuary = mJanuary;
    }

    public int getFebruary() {
        return mFebruary;
    }

    public void setFebruary(int mFebruary) {
        this.mFebruary = mFebruary;
    }

    public int getMarch() {
        return mMarch;
    }

    public void setMarch(int mMarch) {
        this.mMarch = mMarch;
    }

    public int getApril() {
        return mApril;
    }

    public void setApril(int mApril) {
        this.mApril = mApril;
    }

    public int getMay() {
        return mMay;
    }

    public void setMay(int mMay) {
        this.mMay = mMay;
    }

    public int getJune() {
        return mJune;
    }

    public void setJune(int mJune) {
        this.mJune = mJune;
    }

    public int getJuly() {
        return mJuly;
    }

    public void setJulj(int mJuly) {
        this.mJuly = mJuly;
    }

    public int getAugust() {
        return mAugust;
    }

    public void setAugust(int mAugust) {
        this.mAugust = mAugust;
    }

    public int getSeptember() {
        return mSeptember;
    }

    public void setSeptember(int mSeptember) {
        this.mSeptember = mSeptember;
    }

    public int getOctober() {
        return mOctober;
    }

    public void setOctober(int mOctober) {
        this.mOctober = mOctober;
    }

    public int getNovember() {
        return mNovember;
    }

    public void setNovember(int mNovember) {
        this.mNovember = mNovember;
    }

    public int getDecember() {
        return mDecember;
    }

    public int[] getInternalArray() {
        int[] months = new int[12];
        months[0] = mJanuary;
        months[1] = mFebruary;
        months[2] = mMarch;
        months[3] = mApril;
        months[4] = mMay;
        months[5] = mJune;
        months[6] = mJuly;
        months[7] = mAugust;
        months[8] = mSeptember;
        months[9] = mOctober;
        months[10]= mNovember;
        months[11]= mDecember;
        return months;
    }

    public void setDecember(int mDecember) {
        this.mDecember = mDecember;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        int[] months = getInternalArray();
        dest.writeIntArray(months);
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }

        @Override
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }
    };
}
