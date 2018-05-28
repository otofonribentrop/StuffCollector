package com.example.tomovico.stuffcollector;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tomovico.stuffcollector.data.StuffContract;
import com.example.tomovico.stuffcollector.data.StuffDbHelper;

public class StuffProvider extends ContentProvider{

    // Kreiram DB helper objekat
    private StuffDbHelper dbHelper;

    // Kreiram UriMatcher objekt
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Definisem konstante za Uri Matcher
    private static final int STUFF_ALL = 100;
    private static final int STUFF_ID = 101;

    static {
        // Ovde dodajem sve content URI patterne koje matcher mora da prepozna
        uriMatcher.addURI(StuffContract.CONTENT_AUTHORITY, StuffContract.PATH_STUFF, STUFF_ALL);
        uriMatcher.addURI(StuffContract.CONTENT_AUTHORITY, StuffContract.PATH_STUFF + "/#", STUFF_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new StuffDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Definisem kursor koji ce vracati podatke
        Cursor cursor;

        // Dobijam od dbHelpera referencu na SQLiteDatabase objekat
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            // Slucaj kada se trazi cijela tabela
            case STUFF_ALL:
                return db.query(StuffContract.StuffEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            // Slucaj kada se trazi jedan red tabele
            case STUFF_ID:
                // Dobijam id kolone
                long idKolone = ContentUris.parseId(uri);

                // Definisem selection
                selection = "_id=?";

                // Definisem selectionArgs
                selectionArgs = new String[] {String.valueOf(idKolone)};

                // Pravim upit na bazu i vracam rezultat
                return db.query(StuffContract.StuffEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        };
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Dobijam od dbHelpera referencu na DB objekat
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.e("StuffProvider", "Usao u insert");

        switch (uriMatcher.match(uri)) {
            case STUFF_ALL:
                Log.e("StuffProvider", "Usao u case STUFF_ALL");

                // Radim insert u db i dobijam broj insertovane kolone
                long insertedRowId = db.insert(StuffContract.StuffEntry.TABLE_NAME, null, values);
                if (insertedRowId == -1) {
                    Log.e("StuffProvider","Desila se greska prilikom insert oepracije");
                }

                // Kreiram i vracam Uri za insetovanu koloni
                return ContentUris.withAppendedId(uri, insertedRowId);

            default:
                Log.e("StuffProvider","Nemoguc je insert za ovakav Uri");
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
