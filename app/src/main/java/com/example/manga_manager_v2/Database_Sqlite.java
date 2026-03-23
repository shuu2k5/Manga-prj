package com.example.manga_manager_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database_Sqlite extends SQLiteOpenHelper {
    //Định nghĩa tên kho và phiên bản
    private static final String Database_Name = "KhoTruyenCache.db";
    private static final int Data_Version = 1;

    // thành phần của kho
    private static final String Table_Name = "Manga";
    private static final String MaHeThong = "maHeThong";
    private static final String MaHienThi = "maHienThi";
    private static final String TenTruyen = "tenTruyen";
    private static final String TenTacGia = "tenTacGia";
    private static final String TapSo = "tapSo";
    private static final String GiaTien = "giaTien";

    public Database_Sqlite (Context context) {
        super(context, Database_Name, null, Data_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " +Table_Name + " ( "
                + MaHeThong + " TEXT PRIMARY KEY , "
                + MaHienThi + " TEXT , "
                + TenTruyen + " TEXT , "
                + TenTacGia + " TEXT , "
                + TapSo + " INTEGER , "
                + GiaTien + " INTEGER )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    public void DongboTruyenFirebase (Truyen truyen ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MaHeThong, truyen.maHeThong);
        values.put(MaHienThi, truyen.maTruyenHienThi);
        values.put(TenTruyen, truyen.tenTruyen);
        values.put(TenTacGia, truyen.tacGia);
        values.put(TapSo, truyen.tapSo);
        values.put(GiaTien, truyen.giaTien);

        db.insertWithOnConflict(Table_Name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    public void DeleteCache(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table_Name);
        db.close();
    }
    public ArrayList<Truyen> laytruyen(){
        ArrayList<Truyen> danhsach = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Table_Name
                + " ORDER BY " + MaHienThi + " ASC", null);
        while (cursor.moveToNext()) {
            Truyen t = new Truyen();
            int indexMaHeThong = cursor.getColumnIndexOrThrow(MaHeThong);
            int indexMaHienThi = cursor.getColumnIndexOrThrow(MaHienThi);
            int indexTenTruyen = cursor.getColumnIndexOrThrow(TenTruyen);
            int indexTacGia = cursor.getColumnIndexOrThrow(TenTacGia);
            int indexTapSo = cursor.getColumnIndexOrThrow(TapSo);
            int indexGiaTien = cursor.getColumnIndexOrThrow(GiaTien);

            t.maHeThong = cursor.getString(indexMaHeThong);
            t.maTruyenHienThi = cursor.getString(indexMaHienThi);
            t.tenTruyen = cursor.getString(indexTenTruyen);
            t.tacGia = cursor.getString(indexTacGia);
            t.tapSo = cursor.getInt(indexTapSo);
            t.giaTien = cursor.getInt(indexGiaTien);
            danhsach.add(t);
        }
        cursor.close();
        db.close();
        return danhsach;
    }
}
