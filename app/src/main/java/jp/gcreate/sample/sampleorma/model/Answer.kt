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
data class Answer(
    @PrimaryKey(autoincrement = false)
    @Setter("id")
    val id: String = UUID.randomUUID().toString(),
    @Column
    @Setter("answer")
    val answer: String,
    @Column
    @Setter("user")
    val user: String
)
