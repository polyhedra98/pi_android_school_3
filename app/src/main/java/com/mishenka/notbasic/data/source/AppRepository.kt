package com.mishenka.notbasic.data.source

class AppRepository {





    companion object {

        private var INSTANCE: AppRepository? = null

        fun getInstance() =
            INSTANCE ?: synchronized(AppRepository::class.java) {
                INSTANCE ?: AppRepository().also { INSTANCE = it }
            }

    }

}