package android.alexgin.payremind.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import android.alexgin.payremind.Pay;
import android.alexgin.payremind.Category;
import android.alexgin.payremind.database.PaymentDbSchema.PaymentTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
import java.util.UUID;

public class PaymentCursorWrapper extends CursorWrapper {
    public PaymentCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Pay getPay() {
        String uuidString = getString(getColumnIndex(PaymentTable.Cols.UUID));
        String title = getString(getColumnIndex(PaymentTable.Cols.TITLE));
        String bank = getString(getColumnIndex(PaymentTable.Cols.BANK));
        String descr = getString(getColumnIndex(PaymentTable.Cols.DESCR));
        String account = getString(getColumnIndex(PaymentTable.Cols.ACCID));
        long date = getLong(getColumnIndex(PaymentTable.Cols.DATE));
        int dayMnt = getInt(getColumnIndex(PaymentTable.Cols.DAYMNT));
        int period = getInt(getColumnIndex(PaymentTable.Cols.PERIOD));
        int nCateg = getInt(getColumnIndex(PaymentTable.Cols.CATEG));
        double dbTotal = getDouble(getColumnIndex(PaymentTable.Cols.TOTAL));
        int nCurrency = getInt(getColumnIndex(PaymentTable.Cols.CURRENCY));
        int isExecuted = getInt(getColumnIndex(PaymentTable.Cols.EXECUTED));
        long execdate = getLong(getColumnIndex(PaymentTable.Cols.EXECDATE));

        LocalDate localD = Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDateTime localDT = Instant.ofEpochMilli(execdate)
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        Pay pay = new Pay(UUID.fromString(uuidString));
        pay.setTitle(title);
        pay.setNameOfBank(bank);
        pay.setDescription(descr);
        pay.setAccountId(account);
        pay.setDate(localD);
        pay.setDayOfMonth(dayMnt - Pay.DM_OFFSET);
        pay.setPayPeriod(period);
        pay.setCategory((Category.values())[nCateg]);
        pay.setTotalSumm(dbTotal);
        pay.setCurrency(nCurrency);
        pay.setExecuted(isExecuted != 0);
        pay.setExecDate(localDT);

        return pay;
    }
}
