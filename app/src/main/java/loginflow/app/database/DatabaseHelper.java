package loginflow.app.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "users_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SESSION = "session";

    private static final int USER_IS_LOGGED = 1;

    private static final int DEFAULT_USER_ID = 1;

    private static final String USER_DATA_DATABASE_QUERY = String.format(Locale.ENGLISH, "select %1$s from %2$s where %3$s=%4$d", "*", TABLE_NAME, COLUMN_ID, DEFAULT_USER_ID);


    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        COLUMN_ID + " integer primary key, " +
                        COLUMN_LOGIN + " text," +
                        COLUMN_EMAIL + " text," +
                        COLUMN_PASSWORD + " text," +
                        COLUMN_SESSION + " INTEGER  DEFAULT 0)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertUser(String login, String email, String password, int session) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOGIN, login);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_SESSION, session);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void updateUser(String login, String email, String password, int session) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_SESSION, session);

        sqLiteDatabase.update(TABLE_NAME, values, COLUMN_ID + "=" + DEFAULT_USER_ID, null);
    }

    public Cursor getUserData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery(USER_DATA_DATABASE_QUERY, null);
    }


    private int numberOfRows() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, TABLE_NAME);
    }

    public boolean isEmpty() {
        return numberOfRows() <= 0;
    }

    public boolean isUserIsLoggedIn() {
        if (isEmpty()) {
            return false;
        }
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor userData = sqLiteDatabase.rawQuery(USER_DATA_DATABASE_QUERY, null);

        userData.moveToFirst();
        boolean isLogged = userData.getInt(userData.getColumnIndex(DatabaseHelper.COLUMN_SESSION)) == USER_IS_LOGGED;
        if (!userData.isClosed()) {
            userData.close();
        }
        return isLogged;
    }

    public void changeUserSession(int session) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION, session);

        sqLiteDatabase.update(TABLE_NAME, values, COLUMN_ID + "=" + DEFAULT_USER_ID, null);
    }


    public String getEmail() {
        if (isEmpty()) {
            return "";
        }
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor userData = sqLiteDatabase.rawQuery(USER_DATA_DATABASE_QUERY, null);
        userData.moveToFirst();
        String email = userData.getString(userData.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
        if (!userData.isClosed()) {
            userData.close();
        }
        return email;
    }

}
