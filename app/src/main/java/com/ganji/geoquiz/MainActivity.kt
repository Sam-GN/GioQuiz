package com.ganji.geoquiz


import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders


private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val ANSWERED_QUESTIONS = "anwq"
private const val GRADE = "grade"
private const val PREVIOUS_SCORE = "score"
private const val REQUEST_CODE_CHEAT = 0
private const val CHEAT_TOKEN = "cheatToken"
class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var remainingTokensTextView: TextView


private val quizViewModel: QuizViewModel by lazy{
       ViewModelProviders.of(this).get(QuizViewModel::class.java)
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX,0)?:0
        val finishedList = savedInstanceState?.getIntegerArrayList(ANSWERED_QUESTIONS)?:quizViewModel.finishedQuestions
        val grade = savedInstanceState?.getInt(GRADE,0)?:0
        val previousScore = savedInstanceState?.getInt(PREVIOUS_SCORE,0)?:0
        val cheatTokens = savedInstanceState?.getInt(CHEAT_TOKEN,3)?:3


        quizViewModel.currentIndex=currentIndex
        quizViewModel.finishedQuestions.clear()
        quizViewModel.grade = grade
        quizViewModel.previousScore = previousScore
        quizViewModel.cheatTokens = cheatTokens
        val iterator = finishedList.iterator()

        iterator.forEach {
            quizViewModel.finishedQuestions.add(it)
        }

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)
        remainingTokensTextView = findViewById(R.id.remaining_tokens_text_view)
        remainingTokensTextView.text = quizViewModel.cheatTokens.toString()


        trueButton.setOnClickListener {view: View ->
           checkAnswer(true)
            quizViewModel.addToFinishedQuestions()
            setButtonActivity()
        }



        falseButton.setOnClickListener {view: View ->
            checkAnswer(false)
            quizViewModel.addToFinishedQuestions()
            setButtonActivity()
        }

        nextButton.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener{
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        cheatButton.setOnClickListener{view ->
           // val intent = Intent(this, CheatActivity::class .java)
            if(quizViewModel.cheatTokens>0) {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                    startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                } else {
                    startActivityForResult(intent, REQUEST_CODE_CHEAT)
                }

            }
        }

        questionTextView.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK){
            return
        }
        if( requestCode== REQUEST_CODE_CHEAT){
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false)?:false
            quizViewModel.addQuestionToCheated()
            quizViewModel.useCheatToken()
            remainingTokensTextView.text = quizViewModel.cheatTokens.toString()
            if(quizViewModel.cheatTokens==0){
                cheatButton.isEnabled = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        setButtonActivity()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstaceState: Bundle){
        super.onSaveInstanceState(savedInstaceState)
        Log.i(TAG,"OnSaveInstanceState")
        savedInstaceState.putInt(KEY_INDEX,quizViewModel.currentIndex)
        savedInstaceState.putIntegerArrayList(ANSWERED_QUESTIONS,quizViewModel.finishedQuestions)
        savedInstaceState.putInt(GRADE,quizViewModel.grade)
        savedInstaceState.putInt(PREVIOUS_SCORE,quizViewModel.previousScore)
        savedInstaceState.putInt(CHEAT_TOKEN,quizViewModel.cheatTokens)
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion(){

        if(quizViewModel.isGameFinished()){
            val toast2 = Toast.makeText(this, "Score: ${quizViewModel.getPrintGrade()} percent", Toast.LENGTH_SHORT)
            toast2.setGravity(Gravity.TOP, 0, 0)
            toast2.show()
        }
        val questionTestResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTestResId)
        setButtonActivity()

    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer
        if(userAnswer == correctAnswer){
            quizViewModel.addGrade()
        }

      /*  val messageResId = if (userAnswer == correctAnswer){
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }*/
        val messageResId = when {
            quizViewModel.cheatedQuestions.contains(quizViewModel.currentIndex)->R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        val toast = Toast.makeText(applicationContext,messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()

    }

    private fun setButtonActivity(){

        if(quizViewModel.isQuestionAnswered()){
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
        else
        {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

}
