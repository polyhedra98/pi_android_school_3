package com.mishenka.notbasic.interfaces

import com.mishenka.notbasic.data.DataTypes

interface IDataRequest {

    val extras: IDataExtras

    val ofType: DataTypes

    val fragmentId: Long?

}