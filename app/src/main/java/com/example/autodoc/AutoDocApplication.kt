package com.example.autodoc

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class AutoDocApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        realmConfiguration()
    }

    private fun realmConfiguration() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("autodocDB.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
    }
}