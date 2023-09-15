package com.example.finalproject_kotlin.Data_Base

import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.shyf_.model.Follow_Data

class Data_Base_Holder(var activity: Activity) : SQLiteOpenHelper(activity, "Notes_App", null, 1) {

    private var dbd = writableDatabase


    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(Follow_Data.TABLE_CREAT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE ${Follow_Data.TABLE_NAME}")
        onCreate(db)
    }

    //............................... Follwers Table
    fun insert_Follwers(idUser: Int, idUserFollow: Int, isFollow: Int): Boolean {
        val cv = ContentValues()
        cv.put(Follow_Data.COL_IDUSER, idUser)
        cv.put(Follow_Data.COL_IDUSERFOLLOW, idUserFollow)
        cv.put(Follow_Data.COL_ISFOLLOW, isFollow)
        return dbd.insert(Follow_Data.TABLE_NAME, null, cv) > 0
    }

//    fun get_idUser(title: String): Int {
//        val c = dbd.rawQuery(
//            "SELECT ${Notes_Data.COL_ID} FROM ${Notes_Data.TABLE_NAME} WHERE ${Notes_Data.COL_TITLE} = '$title' ",
//            null
//        )
//        val bol = c.moveToFirst()
//        if (bol) return c.getInt(0)
//        else return -1
//    }

//    fun edit_Data(id: Int, title: String, note: String): Boolean {
//        val c = dbd.rawQuery(
//            "UPDATE ${Notes_Data.TABLE_NAME} SET ${Notes_Data.COL_NOTE} = '$note' , ${Notes_Data.COL_TITLE} = '$title'" +
//                    "WHERE ${Notes_Data.COL_ID} = $id", null
//        )
//        return c.count > 0
//    }

    fun get_IsFollowing(idUser: Int, idUserFollow: Int): Int {
        var followers: Int = 0
        val c = dbd.rawQuery(
            "SELECT ${Follow_Data.COL_ISFOLLOW} FROM ${Follow_Data.TABLE_NAME}  WHERE ${Follow_Data.COL_IDUSER} = $idUser AND ${Follow_Data.COL_IDUSERFOLLOW} = $idUserFollow",
            null
        )
        val bol = c.moveToFirst()
        if (bol) return c.getInt(0)
        else
            return followers
    }

    fun get_Id(idUser: Int, idUserFollow: Int): Int {
        var followers: Int = 0
        val c = dbd.rawQuery(
            "SELECT ${Follow_Data.COL_ID} FROM ${Follow_Data.TABLE_NAME}  WHERE ${Follow_Data.COL_IDUSER} = $idUser AND ${Follow_Data.COL_IDUSERFOLLOW} = $idUserFollow",
            null
        )
        val bol = c.moveToFirst()
        if (bol) return c.getInt(0)
        else
            return followers
    }


    fun delete_follower(idUser: Int, idUserFollow: Int): Boolean {
        val c = dbd.rawQuery(
            "DELETE FROM ${Follow_Data.TABLE_NAME} WHERE ${Follow_Data.COL_IDUSER} = $idUser and ${Follow_Data.COL_IDUSERFOLLOW} = $idUserFollow",
            null
        )
        return c.count > 0
    }

}