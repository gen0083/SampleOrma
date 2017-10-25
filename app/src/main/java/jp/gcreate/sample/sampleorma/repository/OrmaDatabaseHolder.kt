package jp.gcreate.sample.sampleorma.repository

import android.content.Context
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Copyright 2017 G-CREATE
 */
@Singleton
class OrmaDatabaseHolder @Inject constructor(context: Context) {
  val orma: OrmaDatabase = OrmaDatabase.builder(context)
      .build()
}
