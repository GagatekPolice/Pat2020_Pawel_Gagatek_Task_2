package loginflow.app.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String TAG="DatabaseHelper";

    private static String TABLE_NAME="users_table";
    public static String COLUMN_ID="id";
    public static String COLUMN_LOGIN="login";
    public static String COLUMN_EMAIL="email";
    public static String COLUMN_PASSWORD="password";
    public static String COLUMN_SESSION="session";

    private static final int DEFAULT_USER_ID=1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable =
                "CREATE TABLE " + TABLE_NAME +"("+
                        COLUMN_ID + " integer primary key, " +
                        COLUMN_LOGIN + " text," +
                        COLUMN_EMAIL + " text," +
                        COLUMN_PASSWORD + " text," +
                        COLUMN_SESSION +  " INTEGER  DEFAULT 0)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertUser (String login, String email, String password,int session) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOGIN, login);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_SESSION, session);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void updateUser(String login, String email, String password,int session){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues values= new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_SESSION, session);

        sqLiteDatabase.update(TABLE_NAME, values, COLUMN_ID+"="+1, null);
    }

    public Cursor getUserData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery( "select * from "+TABLE_NAME+" where "+COLUMN_ID+"="+DEFAULT_USER_ID, null );
    }

    public int numberOfRows(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, TABLE_NAME);
    }

    public boolean isEmpty(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor userData = sqLiteDatabase.rawQuery( "select count(*) from "+TABLE_NAME, null );
        boolean isEmpty=userData.getCount()>0;
        if (!userData.isClosed())  {
            userData.close();
        }
        return !isEmpty;
    }

    public boolean isUserIsLoggedIn(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor userData = sqLiteDatabase.rawQuery( "select * from "+TABLE_NAME+" where "+COLUMN_ID+"="+DEFAULT_USER_ID, null );

        if (userData.getCount()<1){
            if (!userData.isClosed())  {
                userData.close();
            }
            return false;
        }

        userData.moveToFirst();
        boolean isLogged = userData.getInt(userData.getColumnIndex(DatabaseHelper.COLUMN_SESSION)) == 1;
        if (!userData.isClosed())  {
            userData.close();
        }
        return isLogged;
    }

    public void changeUserSession(int session){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues values= new ContentValues();
        values.put(COLUMN_SESSION, session);

        sqLiteDatabase.update(TABLE_NAME, values, COLUMN_ID+"="+1, null);
    }










    @Deprecated
    public int whichUserIsLoggedIn(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor countSessions = sqLiteDatabase.rawQuery( "select count(*) from "+TABLE_NAME+" where "+COLUMN_SESSION+"=1", null );
        countSessions.moveToFirst();
        int count =countSessions.getInt(0);
        countSessions.close();
        if(count==0) return -1;
        if(count==1) {
            Cursor userData = sqLiteDatabase.rawQuery( "select * from "+TABLE_NAME+" where "+COLUMN_SESSION+"=1", null );
            int id =Integer.valueOf(userData.getInt(userData.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            userData.close();
            return id;
        }
        else return -2;
    }

    @Deprecated
    public void updateSession(int id, int session){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
//        Cursor countSessions = sqLiteDatabase.rawQuery( "UPDATE "+TABLE_NAME+" SET "+COLUMN_SESSION+"="+1+" WHERE "+COLUMN_ID+"="+id, null );

        ContentValues values= new ContentValues();
        values.put(COLUMN_SESSION, session);


        sqLiteDatabase.update(TABLE_NAME, values, COLUMN_ID+"="+id, null);

//        countSessions.close();
    }
}
