package android.alexgin.payremind;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.File;

import android.alexgin.payremind.database.PaymentDbSchema;
import android.alexgin.payremind.database.PaymentBaseHelper;
import android.alexgin.payremind.database.PaymentCursorWrapper;
import android.alexgin.payremind.database.ScheduleCursorWrapper;

public class PaymentLab {
    private static final String TAG = "PaymentLab";
    private static final String DB_PATH="/data/data/android.alexgin.payremind/databases/";
    private static final String DB_NAME="paymentBase";
    private static PaymentLab sPaymentLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private Pay mPay;
    private String mCurrencyName;
    private double mCurrencyValue = 0.0f;
    private boolean mFlagCurrencyManual = false;

    public static PaymentLab get(Context context) {
        if (sPaymentLab == null) {
            sPaymentLab = new PaymentLab(context);
        }

        return sPaymentLab;
    }

    private PaymentLab(Context context) {
        mContext = context.getApplicationContext();
        if (PaymentBaseHelper.USING_COPY_DB == 1) {
            try {
                copyDB(mContext);
            } catch (IOException ex) {
                Log.e(TAG, "PaymentLab - copy DB Error");
            }
        }

        mDatabase = new PaymentBaseHelper(mContext).getWritableDatabase();
        String strDbPath = DB_PATH; //mDatabase.getPath();
        int nDbVer = mDatabase.getVersion();
        String strDbOut = String.format("SQLite v %d, (path %s)", nDbVer, strDbPath);
        Log.d(TAG, "PaymentLab c-tor: " + strDbOut);
    }


    public List<Schedule> getScheduleItems() {
        List<Schedule> scheduleList = new ArrayList<>();
        ScheduleCursorWrapper cursor = querySchedule();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Schedule shd = cursor.getSchedule();
                UUID id = shd.getId();
                Log.d(TAG, "getScheduleItems (while) UUID = " + id.toString());

                scheduleList.add(shd);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return scheduleList;
    }

    public void setCurrencyValue(double currency) {
        mCurrencyValue = currency;
    }

    public void setCurrencyName(String currencyName) {
        mCurrencyName = currencyName;
    }

    public void setCurrencyManual(boolean flagCurrencyManual) {
        mFlagCurrencyManual = flagCurrencyManual;
    }

    public double getCurrencyValue() {
        return mCurrencyValue;
    }

    public String getCurrencyName() {
        return mCurrencyName;
    }

    public boolean getCurrncyManual() { return mFlagCurrencyManual; }

    public List<Pay> getPayments() {
        List<Pay> payments = new ArrayList<>();
        PaymentCursorWrapper cursor = queryPays();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Pay p = cursor.getPay();
                // Log.d(TAG, "getPayments (while) p.Title = " + p.getTitle());
                // Log.d(TAG, "getPayments (while) p.Bank = " + p.getNameOfBank());
                // Log.d(TAG, "getPayments (while) p.AccntId = " + p.getAccountId());
                // Log.d(TAG, "getPayments (while) p.TotalSumm = " + p.getTotalSumm());
                // Log.d(TAG, "getCurrency (while) p.Currency = " + p.getCurrency());
                payments.add(p);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return payments;
    }

    public Pay getPay(UUID id) {
        if (mPay != null) { // DEBUG !!!
           if (id == mPay.getId())
               return mPay;
        }
        PaymentCursorWrapper cursor = queryPays(
                 PaymentDbSchema.PaymentTable.Cols.UUID + " = ?",
                 new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getPay();
        }
        finally {
            cursor.close();
        }
    }

    public Pay getPay(int n_id) { // DEBUG !!!
        mPay = new Pay(n_id);
        return mPay;
    }

    public Pay getPay() { // DEBUG !!!
        return mPay;
    }

    public Schedule getSchedule(UUID id)
    {
        ScheduleCursorWrapper cursor = querySchedule( "uuid = " +"'"+ id.toString() +"'", null
               // PaymentDbSchema.ScheduleTable.Cols.UUID + " = ?",
               // new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getSchedule();
        }
        finally {
            cursor.close();
        }
    }

    public void purgeTable()
    {
        mDatabase.delete(PaymentDbSchema.PaymentTable.NAME, "", null);
    }

    private ScheduleCursorWrapper querySchedule() {
        Cursor cursor =
         mDatabase.rawQuery("SELECT * FROM schedule order by _id asc", null);

        return new ScheduleCursorWrapper(cursor);
    }

    private ScheduleCursorWrapper querySchedule(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PaymentDbSchema.ScheduleTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                "_id asc" // orderBy
        );
        return new ScheduleCursorWrapper(cursor);
    }

    public void addPay(Pay p)
    {
        UUID id = p.getId();
        String strId = p.getId().toString();
        ContentValues values = getContentValues(p);
        mDatabase.insert(PaymentDbSchema.PaymentTable.NAME, null, values);
        Log.d(TAG, "PaymentLab: (PaymentTable) INSERT Id=" + strId);
    }

    public void addSchedule(Schedule schedule) {
        UUID id = schedule.getId();
        String strId = schedule.getId().toString();
        ContentValues values = getContentValues(schedule);
        mDatabase.insert(PaymentDbSchema.ScheduleTable.NAME, null, values);
        Log.d(TAG, "PaymentLab: (ScheduleTable) INSERT Id=" + strId);
    }

    public File getPhotoFile(Pay pay) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, pay.getPhotoFilename());
    }

    public File getPhotoFile(String str_file_name) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, str_file_name);
    }

    public void updatePay(Pay pay) {
        Log.d(TAG, "updatePay pay.Title = " + pay.getTitle());
        Log.d(TAG, "updatePay pay.Bank = " + pay.getNameOfBank());
        Log.d(TAG, "updatePay pay.AccntId = " + pay.getAccountId());
        String uuidString = pay.getId().toString();
        ContentValues values = getContentValues(pay);
        mDatabase.update(PaymentDbSchema.PaymentTable.NAME, values,
                PaymentDbSchema.PaymentTable.Cols.UUID + " = ?",
                new String[]{uuidString});
        Log.d(TAG, "updatePay (UPDATE)");
    }

    public void deleteItem(UUID payID) {
        String whereClause = PaymentDbSchema.PaymentTable.Cols.UUID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(payID.toString()) };
        mDatabase.delete(PaymentDbSchema.PaymentTable.NAME, whereClause, whereArgs);
    }

    public void deleteItemSchedule(UUID payID) {
        String whereClause = PaymentDbSchema.ScheduleTable.Cols.UUID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(payID.toString()) };
        mDatabase.delete(PaymentDbSchema.ScheduleTable.NAME, whereClause, whereArgs);
    }

    private PaymentCursorWrapper queryPays(String whereClause, String[] whereArgs) {
        Log.e(TAG, "queryPays_1: mDatabase.query");
        Cursor cursor = mDatabase.query(
                PaymentDbSchema.PaymentTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                "daymnt asc" //PaymentDbSchema.PaymentTable.Cols.DAYMNT // orderBy
        );
        return new PaymentCursorWrapper(cursor);
    }

    private PaymentCursorWrapper queryPays() {
        Log.e(TAG, "queryPays_2: SELECT request");
        Cursor cursor =
         mDatabase.rawQuery("SELECT * FROM payments order by daymnt asc", null);
         //"SELECT _id, uuid, title, bank, description, accountid, date, daymnt, period, category, totalsumm, currency, executed FROM payments order by daymnt asc",
         // null);

        return new PaymentCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Pay p) {
        UUID id = p.getId();
        // String strId = p.getId().toString();
        String strTitle = p.getTitle();
        String strNameOfBank = p.getNameOfBank();
        String strDescription = p.getDescription();
        String strAccountId = p.getAccountId();
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instantDate = p.getDate().atStartOfDay(zoneId).toInstant();
        long nDate = instantDate.toEpochMilli();
        int nDayOfMonth = p.getDayOfMonth();
        int nPayPeriod = p.getPayPeriod();
        int nCategory = p.getCategory().ordinal();
        double dbTotalSumm = p.getTotalSumm();
        int nCurrency = p.getCurrency();
        int nExecuted = p.isExecuted() ? 1 : 0;
        Instant instantExecDate = p.getExecDate().atZone(zoneId).toInstant();
        long nExecDate = instantExecDate.toEpochMilli();

        ContentValues values = new ContentValues();
        values.put(PaymentDbSchema.PaymentTable.Cols.UUID, id.toString());
        values.put(PaymentDbSchema.PaymentTable.Cols.TITLE, strTitle);
        values.put(PaymentDbSchema.PaymentTable.Cols.BANK, strNameOfBank);
        values.put(PaymentDbSchema.PaymentTable.Cols.DESCR, strDescription);
        values.put(PaymentDbSchema.PaymentTable.Cols.ACCID, strAccountId);
        values.put(PaymentDbSchema.PaymentTable.Cols.DATE, nDate);
        values.put(PaymentDbSchema.PaymentTable.Cols.DAYMNT, nDayOfMonth + Pay.DM_OFFSET);
        values.put(PaymentDbSchema.PaymentTable.Cols.PERIOD, nPayPeriod);
        values.put(PaymentDbSchema.PaymentTable.Cols.CATEG, nCategory);
        values.put(PaymentDbSchema.PaymentTable.Cols.TOTAL, dbTotalSumm);
        values.put(PaymentDbSchema.PaymentTable.Cols.CURRENCY, nCurrency);
        values.put(PaymentDbSchema.PaymentTable.Cols.EXECUTED, nExecuted);
        values.put(PaymentDbSchema.PaymentTable.Cols.EXECDATE, nExecDate);

        return values;
    }

    private static ContentValues getContentValues(Schedule shd) {
        UUID id = shd.getId();

        ContentValues values = new ContentValues();
        values.put(PaymentDbSchema.ScheduleTable.Cols.UUID, id.toString());
        values.put(PaymentDbSchema.ScheduleTable.Cols.JANUARY, shd.getJanuary());
        values.put(PaymentDbSchema.ScheduleTable.Cols.FEBRUARY, shd.getFebruary());
        values.put(PaymentDbSchema.ScheduleTable.Cols.MARCH, shd.getMarch());
        values.put(PaymentDbSchema.ScheduleTable.Cols.APRIL, shd.getApril());
        values.put(PaymentDbSchema.ScheduleTable.Cols.MAY, shd.getMay());
        values.put(PaymentDbSchema.ScheduleTable.Cols.JUNE, shd.getJune());
        values.put(PaymentDbSchema.ScheduleTable.Cols.JULY, shd.getJuly());
        values.put(PaymentDbSchema.ScheduleTable.Cols.AUGUST, shd.getAugust());
        values.put(PaymentDbSchema.ScheduleTable.Cols.SEPTEMBER, shd.getSeptember());
        values.put(PaymentDbSchema.ScheduleTable.Cols.OCTOBER, shd.getOctober());
        values.put(PaymentDbSchema.ScheduleTable.Cols.NOVEMBER, shd.getNovember());
        values.put(PaymentDbSchema.ScheduleTable.Cols.DECEMBER, shd.getDecember());

        return values;
    }

    public static void copyDB(Context context) throws IOException{
        try {
            InputStream ip =  context.getAssets().open(DB_NAME+".db");
            Log.d(TAG,"copyDB");
            String op=  DB_PATH  +  DB_NAME +".db";
            Log.d(TAG,"copyDB: path: " + op);
            OutputStream output = new FileOutputStream( op);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = ip.read(buffer))>0){
                output.write(buffer, 0, length);
                Log.d(TAG,"CopyDB: len = " + length);
            }
            output.flush();
            output.close();
            ip.close();
        }
        catch (IOException err) {
            Log.e(TAG, "ERROR CopyDB:"+err.toString());
        }
    }
}
