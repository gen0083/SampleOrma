package jp.gcreate.sample.sampleorma.repository

import io.reactivex.Completable
import jp.gcreate.sample.sampleorma.model.Answer
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import jp.gcreate.sample.sampleorma.model.QuestionRelation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Copyright 2017 G-CREATE
 */
@Singleton
class AnswerRepository @Inject constructor(holder: OrmaDatabaseHolder) {
  private val orma: OrmaDatabase = holder.orma

  fun answerForQuestion(questionId: String, answer: Answer): Completable {
    return orma.transactionAsCompletable {
      val q = orma.selectFromQuestion()
          .idEq(questionId)
          .executeAsObservable()
          .singleOrError()
          .blockingGet()

      val a = orma.relationOfAnswer()
          .upsertAsSingle(answer)
          .blockingGet()

      orma.insertIntoQuestionRelation(QuestionRelation(0L, q, a))
    }
  }
}
