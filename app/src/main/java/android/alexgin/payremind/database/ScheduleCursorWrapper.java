package android.alexgin.payremind.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import android.alexgin.payremind.Schedule;
import android.alexgin.payremind.Category;
import android.alexgin.payremind.database.PaymentDbSchema.ScheduleTable;

import java.util.UUID;

public class ScheduleCursorWrapper extends CursorWrapper {
    public ScheduleCursorWrapper(Cursor cursor) { super(cursor); }

    public Schedule getSchedule() {
        String uuidString = getString(getColumnIndex(ScheduleTable.Cols.UUID));
        int[] months = new int[12];
        months[0] = getInt(getColumnIndex(ScheduleTable.Cols.JANUARY));
        months[1] = getInt(getColumnIndex(ScheduleTable.Cols.FEBRUARY));
        months[2] = getInt(getColumnIndex(ScheduleTable.Cols.MARCH));
        months[3] = getInt(getColumnIndex(ScheduleTable.Cols.APRIL));
        months[4] = getInt(getColumnIndex(ScheduleTable.Cols.MAY));
        months[5] = getInt(getColumnIndex(ScheduleTable.Cols.JUNE));
        months[6] = getInt(getColumnIndex(ScheduleTable.Cols.JULY));
        months[7] = getInt(getColumnIndex(ScheduleTable.Cols.AUGUST));
        months[8] = getInt(getColumnIndex(ScheduleTable.Cols.SEPTEMBER));
        months[9] = getInt(getColumnIndex(ScheduleTable.Cols.OCTOBER));
        months[10]= getInt(getColumnIndex(ScheduleTable.Cols.NOVEMBER));
        months[11]= getInt(getColumnIndex(ScheduleTable.Cols.DECEMBER));
        Schedule shd = new Schedule(UUID.fromString(uuidString), months);
        return shd;
    }
}
