package jp.gcreate.sample.sampleorma.repository

import android.database.sqlite.SQLiteException
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import jp.gcreate.sample.sampleorma.model.Question
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldThrow
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
class QuestionRepositoryTest {
  private lateinit var sut: QuestionRepository
  private val orma: OrmaDatabase by lazy {
    OrmaDatabase.builder(RuntimeEnvironment.application)
        .name(null)
        .build()
  }
  private val holder: OrmaDatabaseHolder = mock { on { orma } doReturn orma }

  @Before
  fun setUp() {
    sut = QuestionRepository(holder)
  }

  @After
  fun tearDown() {
    orma.deleteAll()
  }

  @Test
  fun `Questionが保存できる`() {
    orma.selectFromQuestion().count() shouldEqualTo 0

    sut.postQuestion(Question("a", "test", "testはうまくいきますか？"))
        .test()
        .assertNoErrors()
        .assertComplete()
        .assertValueCount(1)
        .values()[0].let {
      println(it)
      it.id shouldEqualTo "a"
      it.title shouldEqualTo "test"
      it.body shouldEqualTo "testはうまくいきますか？"
    }

    orma.selectFromQuestion().count() shouldEqualTo 1
  }

  @Test
  fun `同じIDのデータは存在できない`() {
    orma.insertIntoQuestion(Question("a", "test", "test-a"))
    val throwsFunction = {
      orma.insertIntoQuestion(Question("a", "test", "test-2"))
    }
    throwsFunction shouldThrow SQLiteException::class
  }

  @Test
  fun `指定したIDのデータを取得できる`() {
    orma.insertIntoQuestion(Question("a", "test", "testだよ"))

    sut.getQuestion("a")
        .test()
        .assertNoErrors()
        .assertComplete()
        .assertValueCount(1)
        .values()[0].let {
      println(it)
      it.id shouldEqualTo "a"
      it.title shouldEqualTo "test"
      it.body shouldEqualTo "testだよ"
    }
  }

  @Test
  fun `指定したIDのデータがない場合はNoSuchElementException`() {
    sut.getQuestion("test")
        .test()
        .assertTerminated()
        .assertError(NoSuchElementException::class.java)
  }

  @Test
  fun `登録されているQuestionの一覧が取得できる`() {
    orma.relationOfQuestion()
        .inserter()
        .executeAll(listOf(
            Question("a", "aaa", "aaaaa"),
            Question("b", "bbb", "bbbbb"),
            Question("c", "ccc", "ccccc")
        ))

    sut.getQuestionAll()
        .test()
        .assertNoErrors()
        .assertComplete()
        .assertValueCount(3)
        .values().let {
      it[0].id shouldEqualTo "a"
      it[1].id shouldEqualTo "b"
      it[2].id shouldEqualTo "c"
    }
  }
}
