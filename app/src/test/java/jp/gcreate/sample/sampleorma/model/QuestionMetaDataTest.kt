package jp.gcreate.sample.sampleorma.model

import android.database.sqlite.SQLiteConstraintException
import assertk.assert
import assertk.assertions.contains
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Copyright 2017 G-CREATE
 */
@RunWith(RobolectricTestRunner::class)
class QuestionMetaDataTest {
  private val orma: OrmaDatabase = OrmaDatabase.builder(RuntimeEnvironment.application)
      .name(null)
      .build()

  @Before
  fun setUp() {
    orma.insertIntoQuestion(Question("test", "test", "test"))
  }

  @After
  fun tearDown() {
    orma.deleteAll()
  }

  @Test
  fun `存在するQuizのMetaDataが作成できる`() {
    assert(orma.selectFromQuestionMetaData().count())
        .isEqualTo(0)

    orma.insertIntoQuestionMetaData(QuestionMetaData("test", "user"))

    assert(orma.selectFromQuestionMetaData().count())
        .isEqualTo(1)
  }

  @Test

  fun `存在しないQuestionのMetaDataは作成できない`() {
    assert(orma.selectFromQuestionMetaData().count())
        .isEqualTo(0)

    assert {
      orma.insertIntoQuestionMetaData(QuestionMetaData("ghost", "abc"))
    }
        .thrownError {
          hasClass(SQLiteConstraintException::class)
          assert(actual.cause?.message)
              .isNotNull {
                it.contains("foreign key constraint failed")
              }
        }

    assert(orma.selectFromQuestionMetaData().count())
        .isEqualTo(0)
  }

  @Test
  fun `MetaDataが存在するQuestionを更新できる`() {
    orma.insertIntoQuestionMetaData(QuestionMetaData("test", "hoge"))
    assert(orma.selectFromQuestion().idEq("test")[0].body)
        .isEqualTo("test")

    orma.updateQuestion()
        .idEq("test")
        .body("updated")
        .execute()

    assert(orma.selectFromQuestion().idEq("test")[0].body)
        .isEqualTo("updated")
  }

  @Test
  fun `主キーの更新`() {
    orma.insertIntoQuestionMetaData(QuestionMetaData("test", "hoge"))
    assert(orma.selectFromQuestion().count())
        .isEqualTo(1)
    assert(orma.selectFromQuestion().idEq("test").count())
        .isEqualTo(1)

    orma.updateQuestion()
        .idEq("test")
        .id("new")
        .execute()

    assert(orma.selectFromQuestion().count())
        .isEqualTo(1)
    assert(orma.selectFromQuestion().idEq("test").count())
        .isEqualTo(0)
    assert(orma.selectFromQuestion().idEq("new").count())
        .isEqualTo(1)
  }
}
