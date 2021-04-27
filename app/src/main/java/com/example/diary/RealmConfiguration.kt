package com.example.diary

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmConfiguration.*
fun realmInit(context: Context): Realm {
    Realm.init(context)
    val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()
    Realm.setDefaultConfiguration(config)
    return Realm.getInstance(config)
}
