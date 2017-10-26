package jp.gcreate.sample.sampleorma

import assertk.assert
import assertk.assertAll
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
  @Nested
  @DisplayName("assertkを使ったアサーション")
  inner class AssertkTest {
    @Test
    @DisplayName("通常のアサーション")
    fun test1() {
      val a = "hoge"
      assert(a).isEqualTo("hoge")
      assertAll {
        assert(true).isEqualTo(true)
        assert("abc").isEqualTo("abc")
        assert("abc".length).isEqualTo(3)
      }
    }

    @Test
    @DisplayName("例外のアサーション")
    fun test2() {
      assert { throw NoSuchElementException("test") }
          .thrownError { isInstanceOf(NoSuchElementException::class) }

      assert { throw NumberFormatException("error") }
          .thrownError { isInstanceOf(RuntimeException::class) }
      // NumberFormatExceptionはRuntimeExceptionを継承しているのでinstanceOfでは一致していると判断される
    }
  }

  @Nested
  @DisplayName("kluentを使ったアサーション")
  inner class KluentTest {
    @Test
    @DisplayName("通常のアサーション")
    fun test1() {
      val a = "hoge"
      a shouldEqualTo "hoge"

      // kluentにはassertAllみたいなマルチアサーションはない
    }

    @Test
    @DisplayName("例外のアサーション")
    fun test2() {
      // この書き方では特定のメソッドを処理することができない
      { throw IllegalArgumentException("bad") } shouldThrow IllegalArgumentException::class

      // その場合、一旦変数で受け取る書き方をすると処理できる
      val throwsFunction = { throw NumberFormatException("fail") }
      throwsFunction shouldThrow RuntimeException::class
    }
  }
}
