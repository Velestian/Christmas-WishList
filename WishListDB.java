package com.gretchen.christmaswishlist;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WishListDB {

    // database constants
    public static final String DB_NAME = "wishlist.db";
    public static final int    DB_VERSION = 1;

    // list table constants
    public static final String LIST_TABLE = "list";
    
    public static final String LIST_ID = "_id";
    public static final int    LIST_ID_COL = 0;

    public static final String LIST_NAME = "list_name";
    public static final int    LIST_NAME_COL = 1;

    // wish item table constants
    public static final String ITEM_TABLE = "items";

    public static final String ITEM_ID = "_id";
    public static final int    ITEM_ID_COL = 0;

    public static final String ITEM_LIST_ID = "list_id";
    public static final int    ITEM_LIST_ID_COL = 1;
    
    public static final String ITEM_NAME = "item";
    public static final int    ITEM_NAME_COL = 2;
    
    public static final String ITEM_DESCRIPTION = "description";
    public static final int    ITEM_DESCRIPTION_COL = 3;
    
    public static final String ITEM_PRIORITY = "priority";
    public static final int    ITEM_PRIORITY_COL = 4;

    public static final String ITEM_COST = "cost";
    public static final int    ITEM_COST_COL = 5;

    public static final String ITEM_COLOR = "color";
    public static final int     ITEM_COLOR_COL = 6;

    public static final String ITEM_HIDDEN = "hidden";
    public static final int    ITEM_HIDDEN_COL = 7;
    
    // CREATE and DROP TABLE statements
    public static final String CREATE_LIST_TABLE = 
            "CREATE TABLE " + LIST_TABLE + " (" + 
            LIST_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
            LIST_NAME + " TEXT    UNIQUE)";
    
    public static final String CREATE_WISHITEM_TABLE =
            "CREATE TABLE " + ITEM_TABLE + " (" +
            ITEM_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ITEM_LIST_ID    + " INTEGER, " +
            ITEM_NAME       + " TEXT, " +
            ITEM_DESCRIPTION      + " TEXT, " +
            ITEM_PRIORITY  + " INTEGER, " +
            ITEM_COST       + " INTEGER, " +
            ITEM_COLOR + " INTEGER, " +
            ITEM_HIDDEN     + " TEXT)";

    public static final String DROP_LIST_TABLE = 
            "DROP TABLE IF EXISTS " + LIST_TABLE;

    public static final String DROP_WISHITEM_TABLE =
            "DROP TABLE IF EXISTS " + "_TABLE";
    
    public static final String WISHITEM_MODIFIED =
            "com.gretchen.christmaswishlist.ITEM_MODIFIED";
    
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, 
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_LIST_TABLE);
            db.execSQL(CREATE_WISHITEM_TABLE);
            
            // insert lists
            db.execSQL("INSERT INTO list VALUES (1, 'MOM')");
            db.execSQL("INSERT INTO list VALUES (2, 'DAD')");
            
            // insert sample tasks
            db.execSQL("INSERT INTO items VALUES (1, 1, 'Diamond Ring', " +
                    "'white gold only', '2', '3', '0', '0')");
            db.execSQL("INSERT INTO items VALUES (2, 1, 'Socks', " +
                    "'', '0', '0', '0', '0')");
            db.execSQL("INSERT INTO items VALUES (3, 2, 'Golf Clubs', " +
                    "'white gold only', '2', '3', '0', '0')");
            db.execSQL("INSERT INTO items VALUES (4, 2, 'Tie', " +
                    "'', '0', '0', '0', '0')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, 
                int oldVersion, int newVersion) {

            Log.d("Item list", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);
            
            Log.d("Item list", "Deleting all data!");
            db.execSQL(WishListDB.DROP_LIST_TABLE);
            db.execSQL(WishListDB.DROP_WISHITEM_TABLE);
            onCreate(db);
        }
    }
    
    // database object and database helper object
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Context context;
    
    // constructor
    public WishListDB(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }
    
    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }
    
    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }
    
    private void closeDB() {
        if (db != null)
            db.close();
    }
    
    private void broadcastItemModified() {
        Intent intent = new Intent(WISHITEM_MODIFIED);
        context.sendBroadcast(intent);
    }

    // public methods
    public ArrayList<List> getLists() {
        ArrayList<List> lists = new ArrayList<List>();
        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE, 
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
             List list = new List();
             list.setId(cursor.getInt(LIST_ID_COL));
             list.setName(cursor.getString(LIST_NAME_COL));
             
             lists.add(list);
        }
        cursor.close();
        closeDB();
        return lists;
    }
    
    public List getList(String name) {
        String where = LIST_NAME + "= ?";
        String[] whereArgs = { name };

        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE, null, 
                where, whereArgs, null, null, null);
        List list = null;
        cursor.moveToFirst();

        list = new List(cursor.getInt(LIST_ID_COL),
                        cursor.getString(LIST_NAME_COL));
        cursor.close();
        this.closeDB();
        
        return list;
    }

    public long addList(String name) {
        ContentValues cv = new ContentValues();
        cv.put(LIST_NAME, name);


        this.openWriteableDB();
        long rowID = db.insert(LIST_TABLE, null, cv);
        this.closeDB();

        broadcastItemModified();

        return rowID;
    }

    public int deleteList(String name) {
        String where = LIST_NAME + "= ?";
        String[] whereArgs = { name };

        this.openWriteableDB();
        int rowCount = db.delete(LIST_TABLE, where, whereArgs);
        this.closeDB();

        broadcastItemModified();

        return rowCount;
    }

    public ArrayList<WishListItem> getItems(String listName, String sortCriteria ) {
        String where = 
                ITEM_LIST_ID + "= ? AND " +
                ITEM_HIDDEN + "!='1'";
        String orderBy = sortCriteria;

        long listID = getList(listName).getId();
        String[] whereArgs = { Long.toString(listID) };

        this.openReadableDB();
        Cursor cursor = db.query(ITEM_TABLE, null,
                where, whereArgs, 
                null, null, sortCriteria);
        ArrayList<WishListItem> items = new ArrayList<WishListItem>();
        while (cursor.moveToNext()) {
             items.add(getItemFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();
        return items;
    }

    
    public WishListItem getItem(long id) {
        String where = ITEM_ID + "= ?";
        String[] whereArgs = { Long.toString(id) };

        this.openReadableDB();        
        Cursor cursor = db.query(ITEM_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        WishListItem item = getItemFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();
        
        return item;
    }    
    
    private static WishListItem getItemFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                WishListItem item = new WishListItem(
                    cursor.getInt(ITEM_ID_COL),
                    cursor.getInt(ITEM_LIST_ID_COL),
                    cursor.getString(ITEM_NAME_COL),
                    cursor.getString(ITEM_DESCRIPTION_COL),
                    cursor.getInt(ITEM_PRIORITY_COL),
                    cursor.getInt(ITEM_COST_COL),
                    cursor.getInt(ITEM_COLOR_COL),
                    cursor.getString(ITEM_HIDDEN_COL));
                return item;
            }
            catch(Exception e) {
                return null;
            }
        }
    }
    
    public long insertItem(WishListItem item) {
        Log.d("insert", item.getItem());
        ContentValues cv = new ContentValues();
        cv.put(ITEM_LIST_ID, item.getListId());
        cv.put(ITEM_NAME, item.getItem());
        cv.put(ITEM_DESCRIPTION, item.getDescription());
        cv.put(ITEM_PRIORITY, item.getPriority());
        cv.put(ITEM_COST, item.getCost());
        cv.put(ITEM_COLOR, item.getColor());
        cv.put(ITEM_HIDDEN, item.getHidden());
        
        this.openWriteableDB();
        long rowID = db.insert(ITEM_TABLE, null, cv);
        this.closeDB();
        
        broadcastItemModified();
        
        return rowID;
    }    
    
    public int updateItem(WishListItem item) {
        ContentValues cv = new ContentValues();
        cv.put(ITEM_LIST_ID, item.getListId());
        cv.put(ITEM_NAME, item.getItem());
        cv.put(ITEM_DESCRIPTION, item.getDescription());
        cv.put(ITEM_PRIORITY, item.getPriority());
        cv.put(ITEM_COST, item.getCost());
        cv.put(ITEM_COLOR, item.getColor());
        cv.put(ITEM_HIDDEN, item.getHidden());
        
        String where = ITEM_ID + "= ?";
        String[] whereArgs = { String.valueOf(item.getId()) };

        this.openWriteableDB();
        int rowCount = db.update(ITEM_TABLE, cv, where, whereArgs);
        this.closeDB();
        
        broadcastItemModified();
        
        return rowCount;
    }    
    
    public int deleteItem(long id) {
        String where = ITEM_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openWriteableDB();
        int rowCount = db.delete(ITEM_TABLE, where, whereArgs);
        this.closeDB();

        broadcastItemModified();
        
        return rowCount;
    }

    
    /*
     * Methods for content provider
     * NOTE: You don't close the DB connection after executing
     * a query, insert, update, or delete operation
     */
    public Cursor genericQuery(String[] projection, String where,
            String[] whereArgs, String orderBy) {
        this.openReadableDB();
        return db.query(ITEM_TABLE, projection, where, whereArgs, null, null, orderBy);
    }

    public long genericInsert(ContentValues values) {
        this.openWriteableDB();
        return db.insert(ITEM_TABLE, null, values);
    }

    public int genericUpdate(ContentValues values, String where,
            String[] whereArgs) {
        this.openWriteableDB();
        return db.update(ITEM_TABLE, values, where, whereArgs);
    }

    public int genericDelete(String where, String[] whereArgs) {
        this.openWriteableDB();
        return db.delete(ITEM_TABLE, where, whereArgs);
    }
}