package jp.gcreate.sample.sampleorma.repository

import io.reactivex.Observable
import io.reactivex.Single
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import jp.gcreate.sample.sampleorma.model.Question
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Copyright 2017 G-CREATE
 */
@Singleton
class QuestionRepository @Inject constructor(holder: OrmaDatabaseHolder) {
  private val orma: OrmaDatabase = holder.orma

  fun postQuestion(question: Question): Single<Question> {
    return orma.relationOfQuestion()
        .upsertAsSingle(question)
  }

  fun getQuestion(id: String): Single<Question> {
    return orma.selectFromQuestion()
        .idEq(id)
        .executeAsObservable()
        .singleOrError()
  }

  fun getQuestionAll(): Observable<Question> {
    return orma.selectFromQuestion()
        .executeAsObservable()
  }
}
