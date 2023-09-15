package com.example.shyf_.model

import android.os.Parcel
import android.os.Parcelable

class Follow_Data(var id: Int, var idUser: Int, var idUserFollow: Int, var isFollow: Int) :
    Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(idUser)
        parcel.writeInt(idUserFollow)
        parcel.writeInt(isFollow)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Follow_Data> {
        const val TABLE_NAME = "Followers"
        const val COL_ID = "id"
        const val COL_IDUSER = "idUser"
        const val COL_IDUSERFOLLOW = "idUserFollow "
        const val COL_ISFOLLOW = "isFollow"
        const val TABLE_CREAT =
            "create table ${TABLE_NAME} ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "$COL_IDUSER  INTEGER DEFAULT 0, $COL_IDUSERFOLLOW  INTEGER DEFAULT 0, $COL_ISFOLLOW INTEGER DEFAULT 0) "

        override fun createFromParcel(parcel: Parcel): Follow_Data {
            return Follow_Data(parcel)
        }

        override fun newArray(size: Int): Array<Follow_Data?> {
            return arrayOfNulls(size)
        }
    }

}