package jp.gcreate.sample.sampleorma

import android.app.Application
import com.facebook.stetho.Stetho
import jp.gcreate.sample.sampleorma.di.AppComponent
import jp.gcreate.sample.sampleorma.di.DaggerAppComponent

/**
 * Copyright 2017 G-CREATE
 */
class App : Application() {
  val appComponent: AppComponent by lazy {
    DaggerAppComponent.builder()
        .bindContext(this)
        .build()
  }

  override fun onCreate() {
    super.onCreate()

    Stetho.initializeWithDefaults(this)
  }
}
