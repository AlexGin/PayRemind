package android.alexgin.payremind.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
// In the next line: the "*" was added 22.01.2022:
import android.alexgin.payremind.database.PaymentDbSchema.*;

public class PaymentBaseHelper extends SQLiteOpenHelper {
    public  static final int USING_COPY_DB = 0; // Allowed values: 0 & 1
    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "paymentBase.db";

    public PaymentBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (USING_COPY_DB == 0) {
            db.execSQL("create table " + PaymentTable.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    PaymentTable.Cols.UUID + ", " +
                    PaymentTable.Cols.TITLE + ", " +
                    PaymentTable.Cols.BANK + ", " +
                    PaymentTable.Cols.DESCR + ", " +
                    PaymentTable.Cols.ACCID + ", " +
                    PaymentTable.Cols.DATE + ", " +
                    PaymentTable.Cols.DAYMNT + ", " +
                    PaymentTable.Cols.PERIOD + ", " +
                    PaymentTable.Cols.CATEG + ", " +
                    PaymentTable.Cols.TOTAL + ", " +
                    PaymentTable.Cols.CURRENCY + ", " +
                    PaymentTable.Cols.EXECUTED + ", " +
                    PaymentTable.Cols.EXECDATE +
                    ")"
            );

            db.execSQL("create table " + ScheduleTable.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    ScheduleTable.Cols.UUID + ", " +
                    ScheduleTable.Cols.JANUARY + ", " +
                    ScheduleTable.Cols.FEBRUARY + ", " +
                    ScheduleTable.Cols.MARCH + ", " +
                    ScheduleTable.Cols.APRIL + ", " +
                    ScheduleTable.Cols.MAY + ", " +
                    ScheduleTable.Cols.JUNE + ", " +
                    ScheduleTable.Cols.JULY + ", " +
                    ScheduleTable.Cols.AUGUST + ", " +
                    ScheduleTable.Cols.SEPTEMBER + ", " +
                    ScheduleTable.Cols.OCTOBER + ", " +
                    ScheduleTable.Cols.NOVEMBER + ", " +
                    ScheduleTable.Cols.DECEMBER +
                    ")"
            );
        }
    }

    public void onPurgeTable()
    {
        /*
        db.execSQL("DROP TABLE IF EXISTS " + PaymentTable.NAME);
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (USING_COPY_DB == 0) {
            db.execSQL("DROP TABLE IF EXISTS " + PaymentTable.NAME);
            onCreate(db);
        }
    }
}
