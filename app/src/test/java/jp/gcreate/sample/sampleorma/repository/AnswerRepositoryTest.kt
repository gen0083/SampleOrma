package jp.gcreate.sample.sampleorma.repository

import android.database.sqlite.SQLiteConstraintException
import assertk.assert
import assertk.assertAll
import assertk.assertions.contains
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.gcreate.sample.sampleorma.model.Answer
import jp.gcreate.sample.sampleorma.model.OrmaDatabase
import jp.gcreate.sample.sampleorma.model.Question
import jp.gcreate.sample.sampleorma.model.QuestionRelation
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
    assert(orma.selectFromAnswer().count())
        .isEqualTo(0)
    assert(orma.selectFromQuestionRelation().count())
        .isEqualTo(0)

    sut.answerForQuestion("a", Answer("test", "answer", "hoge"))
        .test()
        .assertNoErrors()
        .assertComplete()

    orma.selectFromAnswer()
        .idEq("test")[0].let {
      println(it)
      assert(it.id).isEqualTo("test")
      assert(it.answer).isEqualTo("answer")
      assert(it.user).isEqualTo("hoge")
    }

    orma.selectFromQuestionRelation()[0].let {
      println(it)
      assert(it.question.id).isEqualTo("a")
      assert(it.answer.id).isEqualTo("test")
    }
  }

  @Test
  fun `Relationにデータを挿入するには事前に対象データを保存しておく必要がある`() {
    assert(orma.selectFromQuestionRelation().count()).isEqualTo(0)

    assert {
      try {
        orma.insertIntoQuestionRelation(QuestionRelation(0L, Question("test", "test-t", "test-b"), Answer("a", "aa", "au")))
      } catch (e: Exception) {
//        e.printStackTrace()
        assert(e.cause?.cause?.message)
            .isNotNull {
              it.contains("foreign key constraint failed")
            }
        throw e
      }
    }.thrownError {
      hasClass(SQLiteConstraintException::class)
    }
    // 外部キー制約があるので、先にQuizとAnswerそれぞれを登録してから出ないと、QuestionRelationにはデータが入らない

    assert(orma.selectFromQuestionRelation().count())
        .isEqualTo(0)
  }

  @Test
  fun `Relationに同一QuizIDとAnswerIDのデータは重複して登録できない`() {
    val q = Question("a", "a-title", "a-body")
    orma.insertIntoQuestion(q)
    val a = Answer("b", "bb", "bbb")
    orma.insertIntoAnswer(a)
    assert(orma.selectFromQuestionRelation().count())
        .isEqualTo(0)

    orma.insertIntoQuestionRelation(QuestionRelation(0L, q, a))
    assert(orma.selectFromQuestionRelation().count())
        .isEqualTo(1)

    assert {
      try {
        orma.insertIntoQuestionRelation(QuestionRelation(0L, q, a))
      } catch (e: Exception) {
        e.printStackTrace()
        assert(e.cause?.cause?.message)
            .isNotNull {
              it.contains("columns question, answer are not unique")
            }
        throw e
      }
    }.thrownError {
      hasClass(SQLiteConstraintException::class)
    }
    assert(orma.selectFromQuestionRelation().count())
        .isEqualTo(1)
  }

  @Test
  fun `Relationに存在する元となるQuestionを削除すると、Relation中の該当データも削除される`() {
    val q = Question("1", "2", "3")
    val a = Answer("a", "b", "c")
    orma.insertIntoQuestion(q)
    orma.insertIntoAnswer(a)
    orma.insertIntoQuestionRelation(QuestionRelation(0L, q, a))
    assert(orma.selectFromQuestion().count()).isEqualTo(1)
    assert(orma.selectFromAnswer().count()).isEqualTo(1)
    assert(orma.selectFromQuestionRelation().count()).isEqualTo(1)

    orma.selectFromQuestionRelation()
        .questionEq("1")[0]
        .let {
          println(it)
          assert(it.question.id).isEqualTo("1")
          assert(it.answer.id).isEqualTo("a")
        }

    orma.deleteFromQuestion().idEq("1").execute()

    // Questionが削除されたので、そのQuestionに外部キーとして依存しているQuestionRelationのデータも一緒に削除される
    assert(orma.selectFromQuestion().count()).isEqualTo(0)
    assert(orma.selectFromQuestionRelation().count()).isEqualTo(0)
    // ただしそれは該当のQuestionが存在しなくなったからQuestionRelationから消えただけで
    // 該当データのAnswerは削除されない
    assert(orma.selectFromAnswer().count()).isEqualTo(1)
  }

  @Test
  fun `Relationを削除しても依存データへは影響がない`() {
    val q = Question("question", "b", "c")
    val a = Answer("answer", "x", "y")
    orma.insertIntoQuestion(q)
    orma.insertIntoAnswer(a)
    orma.insertIntoQuestionRelation(QuestionRelation(0L, q, a))
    assert(orma.selectFromQuestion().count()).isEqualTo(1)
    assert(orma.selectFromAnswer().count()).isEqualTo(1)
    assert(orma.selectFromQuestionRelation().count()).isEqualTo(1)

    orma.deleteFromQuestionRelation()
        .answerEq("answer")
        .execute()

    assert(orma.selectFromQuestionRelation().count()).isEqualTo(0)
    assert(orma.selectFromQuestion().count()).isEqualTo(1)
    assert(orma.selectFromAnswer().count()).isEqualTo(1)
  }

  @Test
  fun `Questionに対するAnswerを取得できる`() {
    val q1 = Question("q1", "q1-title", "q1-body")
    val q2 = Question("q2", "q2-title", "q2-body")
    orma.prepareInsertIntoQuestion()
        .executeAll(listOf(q1, q2))
    val a1 = Answer("a1-1", "a1", "user")
    val a2 = Answer("a1-2", "a2", "user2")
    val a3 = Answer("a2-1", "a3", "user")
    val a4 = Answer("a2-2", "a4", "user3")
    val a5 = Answer("a1-3", "a5", "user4")
    orma.prepareInsertIntoAnswer()
        .executeAll(listOf(a1, a2, a3, a4, a5))
    orma.prepareInsertIntoQuestionRelation()
        .executeAll(listOf(
            QuestionRelation(0L, q1, a1),
            QuestionRelation(0L, q1, a2),
            QuestionRelation(0L, q2, a3),
            QuestionRelation(0L, q2, a4),
            QuestionRelation(0L, q1, a5)
        ))
    assert(orma.selectFromQuestion().count()).isEqualTo(2)
    assert(orma.selectFromAnswer().count()).isEqualTo(5)
    assert(orma.selectFromQuestionRelation().count()).isEqualTo(5)

    orma.selectFromQuestionRelation()
        .questionEq(q2.id)
        .let {
          assertAll {
            assert(it.count()).isEqualTo(2)
            assert(it[0].question).isEqualTo(q2)
            assert(it[0].answer).isEqualTo(a3)
            assert(it[1].answer).isEqualTo(a4)
          }
          println(it[0])
          println(it[1])
        }
  }
}
