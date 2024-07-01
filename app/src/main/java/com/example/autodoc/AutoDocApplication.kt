package com.example.autodoc

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AutoDocApplication: Application() {
    override fun onCreate() {
        super.onCreate()
//        realmConfiguration()
        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }

    /*private fun realmConfiguration() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("autodocDB.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
    }*/
}