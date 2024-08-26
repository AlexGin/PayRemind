package android.alexgin.payremind;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.UUID;

import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.NavigatorHolder;

// Need to add line:
// android:name=".PRApplication"
// into the AndroidManifest.xml file!

public class PRApplication extends Application {
    private static final String TAG = "PRApplication";
    public static PRApplication INSTANCE;
    private Cicerone<Router> cicerone;

    public PRApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        cicerone = Cicerone.create();
        Log.d(TAG, "onCreate (PRApplication)");

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        boolean bNotif = shp.getBoolean(MainActivity.NOTIFICATIONS, true);
        if (!bNotif) {
            Log.d(TAG, "onCreate (PRApplication) - without PrefetchUnexecutedPays");
            return;
        }
        boolean bFilterUSD = shp.getBoolean(MainActivity.FILTER_USD, true);
        boolean bPrenotif = shp.getBoolean(MainActivity.PRENOTIFY, true);
        int m_n_preview_days = shp.getInt(MainActivity.PRENOTDAYS, 1);

        List<UUID> listUnexecId = (!bPrenotif) ?
                PrefetchUnexecutedPays(bFilterUSD) :
                PrefetchUnexecutedPays(m_n_preview_days, bFilterUSD);

        CallMessageService(listUnexecId);
    }

    public NavigatorHolder getNavigatorHolder() {
        Log.d(TAG, "getNavigatorHolder (PRApplication)");
        return cicerone.getNavigatorHolder();
    }

    public Router getRouter() {
        Log.d(TAG, "getRouter (PRApplication)");
        return cicerone.getRouter();
    }
    // Notification support:
    public List<UUID> PrefetchUnexecutedPays(int n_pre_days, boolean b_notify_filter)
    {
        Log.d(TAG, "PrefetchUnexecutedPays n_pre_days: " + n_pre_days);
        LocalDateTime dateNow = LocalDateTime.now();
        long n_now = dateNow.toEpochSecond(ZoneOffset.UTC);
        long n_preview_date = n_now + (86400L * (long)(n_pre_days)); // Value 86400 - seconds per one day
        LocalDateTime datePreview = LocalDateTime.ofEpochSecond(n_preview_date, 0, ZoneOffset.UTC);
        return PrefetchUnexecutedPays(datePreview, b_notify_filter);
    }

    public List<UUID> PrefetchUnexecutedPays(boolean b_notify_filter) {
        LocalDateTime dateNow = LocalDateTime.now();
        return PrefetchUnexecutedPays(dateNow, b_notify_filter);
    }

    public List<UUID> PrefetchUnexecutedPays() {
        return PrefetchUnexecutedPays(false);
    }

    public List<UUID> PrefetchUnexecutedPays(LocalDateTime date, boolean b_notify_filter) {
        int dayCurr = date.getDayOfMonth();
        int mntCurr1 = date.getMonth().getValue();
        int mntCurr = mntCurr1 - 1; // Zero-based month
        Log.d(TAG, "PrefetchPays (Day) " + dayCurr);
        Log.d(TAG, "PrefetchPays (Month) " + mntCurr);
        PaymentLab lab = PaymentLab.get(this);
        List<Schedule> listShds = lab.getScheduleItems();
        List<Pay> listPayments = lab.getPayments();
        List<UUID> listUnexecId = new ArrayList<>();
        for (Pay p : listPayments)
        {
            int n_period = p.getPayPeriod();
            if (n_period != 1) {
                UUID id = p.getId();
                boolean b_flag = RetrievePrefetchPayFlag(listShds, id, mntCurr);
                if (!b_flag) {
                    Log.d(TAG, "PrefetchPays (Notify-exclude)");
                    continue;
                }
            }
            int nDate = p.getDayOfMonth();
            boolean bExec = p.isExecuted();
            if ((dayCurr >= nDate) && !bExec)
            {
                if (!b_notify_filter) {
                    UUID id_unexec = p.getId();
                    listUnexecId.add(id_unexec);
                    // CallMessageService(p);
                }
                else {
                    int n_cur = p.getCurrency();
                    if (n_cur != 1) { // Notify ONLY about local valute pays!
                        UUID id_unexec = p.getId();
                        listUnexecId.add(id_unexec);
                        // CallMessageService(p);
                    }
                }
            }
        }
        return listUnexecId;
    }

    private void CallMessageService(List<UUID> listIds)
    {
        int nSize = listIds.size();
        for (int index = 0; index < nSize; ++index) {
            UUID id = listIds.get(index);
            Log.d(TAG, "CallMessageService: prepare to startService N" + index);
            Intent intent = new Intent(this, PRMessageService.class);
            intent.putExtra(PRMessageService.EXTRA_PAY_INFO, id);
            startService(intent);
            Log.d(TAG, "CallMessageService: executed startService");
        }
    }

    private boolean RetrievePrefetchPayFlag(List<Schedule> listShds, UUID id, int n_mnt)
    {
        boolean b_return_flag = true;
        for (Schedule shd : listShds)
        {
            UUID uuid = shd.getId();
            if (uuid.equals(id)) // Do NOT using the "==" in this point!
            {
                // Log.d(TAG, "RetrievePrefetchPayFlag (uuid == id)");
                int[] arr= shd.getInternalArray();
                if (arr[n_mnt] != 1) {
                    b_return_flag = false;
                    Log.d(TAG, "RetrievePrefetchPayFlag (FALSE)");
                }
            }
        }
        return b_return_flag;
    }
}
