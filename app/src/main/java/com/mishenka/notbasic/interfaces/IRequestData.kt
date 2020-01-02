package com.mishenka.notbasic.interfaces

import com.mishenka.notbasic.data.DataTypes

interface IRequestData {

    val extras: IRequestDataExtras

    val ofType: DataTypes

    val fragmentId: Long

    val callback: IResponseCallback

}