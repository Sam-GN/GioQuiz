package com.ganji.geoquiz

import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
  /*  init {
        Log.d(TAG,"ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"ViewModel instance about to be destroyed")
    }
    */

    private val questionBank = listOf(Question(R.string.question_australia,true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas,true),
        Question(R.string.question_asia,true))
     var currentIndex = 0
     val finishedQuestions = ArrayList<Int>()
     var grade = 0
     var previousScore = 0;

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext(){
        currentIndex = (currentIndex+1) % questionBank.size
    }

    fun moveToPrev(){
        currentIndex = (currentIndex+questionBank.size-1) % questionBank.size
    }

    fun addToFinishedQuestions(){
       finishedQuestions.add(currentIndex)
    }

    fun isGameFinished(): Boolean{
        if(questionBank.size==finishedQuestions.size){
            previousScore = grade*100/questionBank.size
            grade = 0
            finishedQuestions.clear()
            return true
        } else {
            return false
        }
    }

    fun getPrintGrade(): Int {
        return  previousScore
    }

    fun addGrade(){
        grade++
    }

    fun isQuestionAnswered(): Boolean {
        return finishedQuestions.contains(currentIndex)
    }


}