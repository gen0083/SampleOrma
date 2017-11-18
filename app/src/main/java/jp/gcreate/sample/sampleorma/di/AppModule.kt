package jp.gcreate.sample.sampleorma.di

import android.content.Context
import dagger.*
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import javax.inject.Singleton

/**
 * Copyright 2017 G-CREATE
 */
@Module
class AppModule {
  @Provides
  @Singleton
  fun provideOrmaDatabase(context: Context): OrmaDatabase = OrmaDatabase.builder(context)
      .build()
}
