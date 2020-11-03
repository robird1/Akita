package com.ulsee.ulti_a100

import android.app.Application
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.Job
import java.io.PrintWriter
import java.io.StringWriter

class App : Application() {

    var job: Job? = null
    val TAG = "Application"

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("appv2.realm")
            .schemaVersion(1) // Must be bumped when the schema changes
//            .migration { realm, oldVersion, newVersion ->
//                if (oldVersion == 0L) {
//                    val schema = realm.schema
//                    schema.create("RealmDevice")
//                        ?.addField("mID", String.javaClass)
//                        ?.addField("mIP", String.javaClass)
//                        ?.addField("mName", String.javaClass)
//                        ?.addField("mCreatedAt", Long.javaClass)
//                }
//            } // Migration to run
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)

        initUncaughtExceptionHandler()
        listenNotification()
    }

    override fun onTerminate() {
        job?.cancel()
        super.onTerminate()
    }

    private fun initUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val report = StringBuilder()
            val result = StringWriter()
            val printWriter = PrintWriter(result)
            // Exception will write all stack trace to string builder
            e.printStackTrace(printWriter)
            report.append(result.toString())
            printWriter.close();
            Log.d("App", "uncaught exception: $report")
        }
    }
}