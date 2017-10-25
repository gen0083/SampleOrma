package jp.gcreate.sample.sampleorma.model

import com.github.gfx.android.orma.annotation.*

/**
 * Copyright 2017 G-CREATE
 */
@Table(
    indexes = arrayOf(Index(value = *arrayOf("question", "answer"), unique = true))
)
data class QuestionRelation(
    @PrimaryKey
    @Setter("id")
    val id: Long,
    @Column
    @Setter("question")
    val question: Question,
    @Column
    @Setter("answer")
    val answer: Answer
)
