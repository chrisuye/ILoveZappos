package com.christian.seyoum.ilovezappos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object UserContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "foods"
        const val COLUMN_NAME_PRICE = "user_price"
        const val COLUMN_NAME_DELETED = "deleted"
    }
}

private const val CREATE_USER_TABEL = "CREATE TABLE ${UserContract.UserEntry.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "${UserContract.UserEntry.COLUMN_NAME_PRICE} TEXT, " +
        "${UserContract.UserEntry.COLUMN_NAME_DELETED} BOOL DEFAULT 0" +
        ")"

interface IUserDataBase{
    fun getFoods(): MutableList<User>
    fun addFood(user: User)
}

private const val DELETE_USER_TABLE = "DROP TABLE IF EXISTS ${UserContract.UserEntry.TABLE_NAME}"

class UserDataBase (ctx:Context) :IUserDataBase {

    class UserDbHelper (ctx: Context) : SQLiteOpenHelper (ctx, DATABASE_NAME,null, DATABASE_VERSION){

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_USER_TABEL)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL(DELETE_USER_TABLE)
        }

        companion object{
            val DATABASE_NAME = "users.db"
            val DATABASE_VERSION = 1
        }
    }

    private val db: SQLiteDatabase

    init {
        db = UserDbHelper(ctx).writableDatabase
    }

    override fun getFoods(): MutableList<User> {
        val project = arrayOf(BaseColumns._ID, UserContract.UserEntry.COLUMN_NAME_PRICE)
        val sortorder = "${BaseColumns._ID} ASC"
        val selection = "${UserContract.UserEntry.COLUMN_NAME_DELETED} = ?"
        val selectionArg = arrayOf("0")

        val cursor = db.query(
            UserContract.UserEntry.TABLE_NAME,
            project,
            selection,
            selectionArg,
            null,
            null,
            sortorder
        )
        val prices = mutableListOf<User>()
        with(cursor){
            while (cursor.moveToNext()){

                val id = getInt(getColumnIndex(BaseColumns._ID))
                val price = getString(getColumnIndex(UserContract.UserEntry.COLUMN_NAME_PRICE))

                val pri = User(id,price)

                prices.add(pri)

            }
        }
        return prices
    }

    override fun addFood(user: User) {
        val cvs = toContentValues(user)
        db.insert(UserContract.UserEntry.TABLE_NAME, null, cvs)
    }

    private fun toContentValues(user: User): ContentValues {
        val cv = ContentValues()
        cv.put(UserContract.UserEntry.COLUMN_NAME_PRICE, user.price)
        return cv
    }
}