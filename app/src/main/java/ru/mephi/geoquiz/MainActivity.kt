package ru.mephi.geoquiz


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider


private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val SCORE_INDEX = "score"


class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            quizViewModel.isCheater( it.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false)
            if (quizViewModel.isCheater){
                if (quizViewModel.cheatsLeft > 0)
                    quizViewModel.cheatsLeft--
            }
        }
    }


    private val quizViewModel: QuizViewModel by lazy {
        val factory = QuizViewModelFactory()
        ViewModelProvider(this@MainActivity, factory).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val score = savedInstanceState?.getInt(SCORE_INDEX, 0)?: 0
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0)?: 0
        quizViewModel.currentIndex = currentIndex
        quizViewModel.score = score

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton=findViewById(R.id.previous_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)


        trueButton.setOnClickListener {
            checkAnswer(true)
            Toast.makeText(this, "Your score: ${quizViewModel.score}", Toast.LENGTH_SHORT).show()

        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            Toast.makeText(this, "Your score: ${quizViewModel.score}", Toast.LENGTH_SHORT).show()

        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            isAnswered(quizViewModel.currentIndex)
            previousButton.isEnabled = true
        }

        previousButton.setOnClickListener{
            quizViewModel.moveToPrevious()
            updateQuestion()
            isAnswered(quizViewModel.currentIndex)
            if (quizViewModel.currentIndex == 0)
                previousButton.isEnabled = false
        }

        cheatButton.setOnClickListener {
            val intent = CheatActivity.newIntent(this@MainActivity, quizViewModel.currentQuestionAnswer)
            val options = ActivityOptionsCompat.makeScaleUpAnimation(it, 0, 0, it.width, it.height)
            getResult.launch(intent, options)
            Toast.makeText(this, "Cheats left: ${quizViewModel.cheatsLeft-1}", Toast.LENGTH_SHORT).show()

        }

        updateQuestion()
        previousButton.isEnabled = false

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(SCORE_INDEX, quizViewModel.score)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }



    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){
        trueButton.isEnabled = false
        falseButton.isEnabled = false
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId: Int
        when {
            quizViewModel.isCheater -> {
                messageResId = R.string.judgment_toast
                if (quizViewModel.cheatsLeft == 0)
                quizViewModel.score--
            }
            else -> {
                when (userAnswer) {
                    correctAnswer -> {
                        messageResId = R.string.correct_toast
                        quizViewModel.score++
                    }
                    else -> {
                        messageResId = R.string.incorrect_toast
                    }
                }
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        if (quizViewModel.currentIndex == quizViewModel.questionBankSize-1)
            Toast.makeText(this, "Ваши очки - ${quizViewModel.score}", Toast.LENGTH_LONG).show()
    }

    private fun isAnswered (index: Int){
        val isQuestionAnswered = quizViewModel.currentQuestionAnswered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

}