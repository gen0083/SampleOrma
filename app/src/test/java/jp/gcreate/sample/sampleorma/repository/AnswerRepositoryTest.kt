package jp.gcreate.sample.sampleorma.repository

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.gcreate.sample.sampleorma.model.Answer
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import jp.gcreate.sample.sampleorma.model.Question
import org.amshove.kluent.shouldEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Copyright 2017 G-CREATE
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AnswerRepositoryTest {
  private lateinit var sut: AnswerRepository
  private val orma: OrmaDatabase by lazy {
    OrmaDatabase.builder(RuntimeEnvironment.application)
        .name(null)
        .build()
  }
  private val holder: OrmaDatabaseHolder = mock { on { orma } doReturn orma }

  @Before
  fun setUp() {
    sut = AnswerRepository(holder)
  }

  @After
  fun tearDown() {
    orma.deleteAll()
  }

  @Test
  fun `Questionに対するAnswerが登録できる`() {
    orma.insertIntoQuestion(Question("a", "aaa", "aaa"))
    orma.selectFromAnswer().count() shouldEqualTo 0
    orma.selectFromQuestionRelation().count() shouldEqualTo 0

    sut.answerForQuestion("a", Answer("test", "answer", "hoge"))
        .test()
        .assertNoErrors()
        .assertComplete()

    orma.selectFromAnswer()
        .idEq("test")[0].let {
      println(it)
      it.id shouldEqualTo "test"
      it.answer shouldEqualTo "answer"
      it.user shouldEqualTo "hoge"
    }

    orma.selectFromQuestionRelation()[0].let {
      println(it)
      it.question.id shouldEqualTo "a"
      it.answer.id shouldEqualTo "test"
    }
  }
}
