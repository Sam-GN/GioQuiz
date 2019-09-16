package com.ganji.geoquiz


import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders


private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val ANSWERED_QUESTIONS = "anwq"
private const val GRADE = "grade"
private const val PREVIOUS_SCORE = "score"
class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

   /* private val questionBank = listOf(Question(R.string.question_australia,true),
                                      Question(R.string.question_oceans,true),
                                      Question(R.string.question_mideast,false),
                                      Question(R.string.question_africa,false),
                                      Question(R.string.question_americas,true),
                                      Question(R.string.question_asia,true))
    private var currentIndex = 0
    private val finishedQuestions = ArrayList<Int>()
    private var grade = 0*/
private val quizViewModel: QuizViewModel by lazy{
       ViewModelProviders.of(this).get(QuizViewModel::class.java)
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

      /*  val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")*/

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX,0)?:0
        val finishedList = savedInstanceState?.getIntegerArrayList(ANSWERED_QUESTIONS)?:quizViewModel.finishedQuestions
        val grade = savedInstanceState?.getInt(GRADE,0)?:0
        val previousScore = savedInstanceState?.getInt(PREVIOUS_SCORE,0)?:0

        quizViewModel.currentIndex=currentIndex
        quizViewModel.finishedQuestions.clear()
        quizViewModel.grade = grade
        quizViewModel.previousScore = previousScore
        val iterator = finishedList.iterator()



        iterator.forEach {
            quizViewModel.finishedQuestions.add(it)
        }
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)


        trueButton.setOnClickListener {view: View ->
           checkAnswer(true)
          // finishedQuestions.add(currentIndex)
            quizViewModel.addToFinishedQuestions()
            setButtonActivity()
        }



        falseButton.setOnClickListener {view: View ->
            checkAnswer(false)
          //  finishedQuestions.add(currentIndex)
            quizViewModel.addToFinishedQuestions()
            setButtonActivity()
        }

        nextButton.setOnClickListener{
         //   currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener{
            //currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        questionTextView.setOnClickListener{
          //  currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
            updateQuestion()
        }
        updateQuestion()
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

    override fun onSaveInstanceState(savedInstaceState: Bundle){
        super.onSaveInstanceState(savedInstaceState)
        Log.i(TAG,"OnSaveInstanceState")
        savedInstaceState.putInt(KEY_INDEX,quizViewModel.currentIndex)
        savedInstaceState.putIntegerArrayList(ANSWERED_QUESTIONS,quizViewModel.finishedQuestions)
        savedInstaceState.putInt(GRADE,quizViewModel.grade)
        savedInstaceState.putInt(PREVIOUS_SCORE,quizViewModel.previousScore)
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
      /*  if(finishedQuestions.size==questionBank.size){
            gradeQuiz()
            finishedQuestions.clear()
            grade=0
        }*/
     //   val questionTestResId = questionBank[currentIndex].textResId

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
      //  val correctAnswer = questionBank[currentIndex].answer
        val correctAnswer = quizViewModel.currentQuestionAnswer
        if(userAnswer == correctAnswer){
            //grade++
            quizViewModel.addGrade()
        }

        val messageResId = if (userAnswer == correctAnswer){
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }
        val toast = Toast.makeText(applicationContext,messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()

    }

    private fun setButtonActivity(){
        //if(finishedQuestions.contains(currentIndex)){
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
