package android.alexgin.payremind.database;

public class PaymentDbSchema {
    public static final class PaymentTable {
        public static final String NAME = "payments";

    public static final class Cols {
        public static final String UUID = "uuid";
        public static final String TITLE = "title";
        public static final String BANK = "bank";
        public static final String DESCR = "description";
        public static final String ACCID = "accountid";
        public static final String DATE = "date";
        public static final String DAYMNT = "daymnt";
        public static final String PERIOD = "period";
        public static final String CATEG = "category";
        public static final String TOTAL = "totalsumm";
        public static final String CURRENCY = "currency";
        public static final String EXECUTED = "executed";
        public static final String EXECDATE = "execdate";
        }
    }
    public static final class ScheduleTable {
        public static final String NAME = "schedule";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String JANUARY = "january";
            public static final String FEBRUARY = "february";
            public static final String MARCH = "march";
            public static final String APRIL = "april";
            public static final String MAY = "may";
            public static final String JUNE = "june";
            public static final String JULY = "july";
            public static final String AUGUST = "august";
            public static final String SEPTEMBER = "september";
            public static final String OCTOBER = "october";
            public static final String NOVEMBER = "november";
            public static final String DECEMBER = "december";
        }
    }
}
