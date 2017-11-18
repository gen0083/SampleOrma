package jp.gcreate.sample.sampleorma.repository

import io.reactivex.Completable
import jp.gcreate.sample.sampleorma.model.*
import javax.inject.*

/**
 * Copyright 2017 G-CREATE
 */
@Singleton
class AnswerRepository @Inject constructor(private val orma: OrmaDatabase) {

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
