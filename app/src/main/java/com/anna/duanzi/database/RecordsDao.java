package com.anna.duanzi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class RecordsDao {

    public static RecordsDao instance;

    public static RecordsDao getInstance(Context context) {
        if (instance == null) {
            synchronized (RecordsDao.class) {
                if (instance == null) {
                    instance = new RecordsDao(context);
                }
            }
        }
        return instance;
    }

    RecordSQLiteOpenHelper recordHelper;

    SQLiteDatabase recordsDb;

    public RecordsDao(Context context) {
        recordHelper = new RecordSQLiteOpenHelper(context);
    }

    //添加搜索记录
    public void addRecords(String record) {
        if (!isHasRecord(record)) {
            recordsDb = recordHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", record);
            //添加
            recordsDb.insert("records", null, values);
            //关闭
            recordsDb.close();
        }
    }

    //判断是否含有该搜索记录
    public boolean isHasRecord(String record) {
        boolean isHasRecord = false;
        recordsDb = recordHelper.getReadableDatabase();
        Cursor cursor = recordsDb.query("records", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (record.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))) {
                isHasRecord = true;
            }
        }
        //关闭数据库
        recordsDb.close();
        return isHasRecord;
    }

    //获取全部搜索记录
    public List<String> getRecordsList() {
        List recordsList = new ArrayList<>();
        recordsDb = recordHelper.getReadableDatabase();
        Cursor cursor = recordsDb.query("records", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            recordsList.add(name);
        }
        //关闭数据库
        recordsDb.close();
        return recordsList;
    }

    //删除搜索记录
    public void deleteRecords(String record) {
        recordsDb = recordHelper.getWritableDatabase();
        recordsDb.execSQL("delete from records where name='" + record + "'");
        recordsDb.close();
    }
}
