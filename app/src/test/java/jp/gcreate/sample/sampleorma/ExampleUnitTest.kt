package jp.gcreate.sample.sampleorma

import assertk.assert
import assertk.assertAll
import assertk.assertions.*
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
      assert { throw NumberFormatException("error") }
          .thrownError { hasClass(NumberFormatException::class) }
    }
  }

  @Test
  @DisplayName("assertkのアサーション")
  fun test1() {
    assert("hoge").contains("og")

    assert(10).isLessThan(100)

    assert(-1).isNegative()

    assert(listOf("aa", "bb", "cc"))
        .contains("bb")
  }
}
