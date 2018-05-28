package com.example.tomovico.stuffcollector.data;


import android.net.Uri;
import android.provider.BaseColumns;

public final class StuffContract {

    // Definicija ContetnAuthority imena
    public static final String CONTENT_AUTHORITY = "com.example.tomovico.stuffcollector";

    // Osnovi Uri za ContentAuthority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Ime tabele
    public static final String PATH_STUFF = "stuff";

    // Definicija tabele Stuff u bazi
    public static final class StuffEntry implements BaseColumns {

        // Definicija Urija za pristup tabeli
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STUFF);

        // Definicija konstante za ime tabele
        public static final String TABLE_NAME = "stuff";

        // Definicija konstanti za imena kolona
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_STUFF_NAME = "product_name";
        public static final String COLUMN_STUFF_PRODUCER = "product_producer";
        public static final String COLUMN_STUFF_CIJENA = "product_price";
        public static final String COLUMN_STUFF_QUANTITY = "product_quantity";
        //public static final String COLUMN_STUFF_PICTURE = "product_picture";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";

    }
}
