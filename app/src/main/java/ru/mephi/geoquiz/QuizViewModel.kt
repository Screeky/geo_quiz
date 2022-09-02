package ru.mephi.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel
private const val TAG = "QuizViewModel"

class QuizViewModel: ViewModel() {
    var currentIndex = 0
    var score = 0
    var cheatsLeft = 4
    private val questionBank = listOf(Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    val currentQuestionAnswer: Boolean
    get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
    get() = questionBank[currentIndex].textResId

    val currentQuestionAnswered: Boolean
    get() = questionBank[currentIndex].answered

    val questionBankSize = questionBank.size

    val isCheater: Boolean
    get() = questionBank[currentIndex].isCheated

    fun moveToNext(){
        currentIndex = (currentIndex+1) % questionBank.size
    }

    fun moveToPrevious(){
        currentIndex = if (currentIndex > 0)
            (currentIndex-1) % questionBank.size
        else questionBank.size-1
    }

    fun isCheater(value :Boolean){
        questionBank[currentIndex].isCheated = value
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
}