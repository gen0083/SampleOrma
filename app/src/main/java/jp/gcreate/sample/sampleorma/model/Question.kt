package jp.gcreate.sample.sampleorma.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import java.util.*

/**
 * Copyright 2017 G-CREATE
 */
@Table
data class Question(
    @PrimaryKey(autoincrement = false)
    @Setter("id")
    val id: String = UUID.randomUUID().toString(),
    @Column
    @Setter("title")
    val title: String,
    @Column
    @Setter("body")
    val body: String
)
