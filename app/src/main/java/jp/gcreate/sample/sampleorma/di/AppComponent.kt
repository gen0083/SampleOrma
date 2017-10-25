package jp.gcreate.sample.sampleorma.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import jp.gcreate.sample.sampleorma.MainActivity
import javax.inject.Singleton

/**
 * Copyright 2017 G-CREATE
 */
@Singleton
@Component
interface AppComponent {
  fun inject(activity: MainActivity)

  @Component.Builder
  interface Builder {
    fun build(): AppComponent

    @BindsInstance
    @Singleton
    fun bindContext(context: Context): Builder

  }
}
