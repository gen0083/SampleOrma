package jp.gcreate.sample.sampleorma.repository

import io.reactivex.*
import jp.gcreate.sample.sampleorma.model.*
import javax.inject.*

/**
 * Copyright 2017 G-CREATE
 */
@Singleton
class QuestionRepository @Inject constructor(private val orma: OrmaDatabase) {

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
