package jp.gcreate.sample.sampleorma.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table

/**
 * Copyright 2017 G-CREATE
 */
@Table(
    constraints = arrayOf("unique (question, answer)")
)
data class QuestionRelation(
    @PrimaryKey
    @Setter("id")
    val id: Long,
    @Column(indexed = true)
    @Setter("question")
    val question: Question,
    @Column(indexed = true)
    @Setter("answer")
    val answer: Answer
)
