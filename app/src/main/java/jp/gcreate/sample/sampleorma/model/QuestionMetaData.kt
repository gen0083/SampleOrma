package jp.gcreate.sample.sampleorma.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table

/**
 * Copyright 2017 G-CREATE
 */
@Table(constraints = arrayOf("FOREIGN KEY (id) REFERENCES Question(id) ON DELETE CASCADE ON UPDATE CASCADE"))
data class QuestionMetaData(
    @PrimaryKey(autoincrement = false)
    @Setter("id")
    val id: String,
    @Column
    @Setter("user")
    val user: String
)
