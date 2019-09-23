package com.ganji.geoquiz


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val EXTRA_ANSWER_IS_TRUE = "com.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.answer_shown"
private const val   CHEAT_INDEX = "cheatIndex"
private const val   CHEAT_TEXT = "cheatText"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var apiTextView: TextView




    private val quizViewModel: QuizViewModel by lazy{
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerTextView = findViewById(R.id.answer_text_view)
        apiTextView = findViewById(R.id.api_text_view)
        var answerIsTrue = savedInstanceState?.getBoolean(CHEAT_INDEX,false)?:false

        if(savedInstanceState!=null){
            var text = savedInstanceState.getString(CHEAT_TEXT,"")
            answerTextView.text = text
            setAnswerShownResult(true)
        }

        quizViewModel.answerIsTrue = answerIsTrue

        quizViewModel.answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false)

        showAnswerButton = findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener{
            val answerText = when {
                answerIsTrue->R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }

        apiTextView.text = "API Level "+android.os.Build.VERSION.SDK_INT.toString()
    }

    override fun onSaveInstanceState(savedInstaceState: Bundle){
        super.onSaveInstanceState(savedInstaceState)

        savedInstaceState.putBoolean(CHEAT_INDEX,quizViewModel.answerIsTrue)
        savedInstaceState.putString(CHEAT_TEXT,answerTextView.text.toString())

    }

    private fun setAnswerShownResult(isAnswerShown: Boolean){
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown)
        }
        setResult(Activity.RESULT_OK,data)
    }

    companion object{
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue)
            }
        }
    }
}
