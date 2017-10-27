package jp.gcreate.sample.sampleorma.repository

import android.database.sqlite.SQLiteException
import assertk.assert
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import jp.gcreate.sample.sampleorma.model.Question
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
    assert(orma.selectFromQuestion().count()).isEqualTo(0)

    sut.postQuestion(Question("a", "test", "testはうまくいきますか？"))
        .test()
        .assertNoErrors()
        .assertComplete()
        .assertValueCount(1)
        .values()[0].let {
      println(it)
      assert(it.id).isEqualTo("a")
      assert(it.title).isEqualTo("test")
      assert(it.body).isEqualTo("testはうまくいきますか？")
    }

    assert(orma.selectFromQuestion().count()).isEqualTo(1)
  }

  @Test
  fun `同じIDのデータは存在できない`() {
    orma.insertIntoQuestion(Question("a", "test", "test-a"))
    assert {
      orma.insertIntoQuestion(Question("a", "test", "test-2"))
    }.thrownError { hasClass(SQLiteException::class) }
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
      assert(it.id).isEqualTo("a")
      assert(it.title).isEqualTo("test")
      assert(it.body).isEqualTo("testだよ")
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
      assert(it[0].id).isEqualTo("a")
      assert(it[1].id).isEqualTo("b")
      assert(it[2].id).isEqualTo("c")
    }
  }
}
