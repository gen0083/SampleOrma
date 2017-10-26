package jp.gcreate.sample.sampleorma

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.gcreate.sample.sampleorma.model.Answer
import jp.gcreate.sample.sampleorma.model.Question
import jp.gcreate.sample.sampleorma.repository.AnswerRepository
import jp.gcreate.sample.sampleorma.repository.QuestionRepository
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
  @Inject lateinit var questionRepository: QuestionRepository
  @Inject lateinit var answerRepository: AnswerRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    (application as App).appComponent.inject(this)

    questionRepository.postQuestion(Question("a", "hoge", "fuga"))
        .flatMapCompletable { answerRepository.answerForQuestion(it.id, Answer("b", "answer-hoge", "some")) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          Log.d("test", "complete")
        }, {
          Log.e("test", "error $it", it)
        })
  }
}
