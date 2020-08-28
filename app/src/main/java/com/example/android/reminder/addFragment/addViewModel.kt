package com.example.android.reminder.addFragment

import android.app.Application
import androidx.lifecycle.*
import com.example.android.reminder.network.Cook
import com.example.android.reminder.network.FirebaseDatabase
import kotlinx.coroutines.*

class AddViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AddViewModel(application: Application): AndroidViewModel(application){

    //========================================= Init stuff

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var cookName = MutableLiveData<String>()

    val result = FirebaseDatabase.result


    //========================================= event control stuff

    private val _hide = MutableLiveData<Boolean>()
    val hide: LiveData<Boolean>
        get() = _hide
    fun hide(){
        _hide.value = true
    }
    fun hideEnd(){
        _hide.value = false
    }

    //========================================= event of pressing the add button with empty editText
    private val _displayEmptyFieldMessage = MutableLiveData<Boolean>()
    val displayEmptyFieldMessage: LiveData<Boolean>
        get() = _displayEmptyFieldMessage
    fun displayEmptyFieldMessage(){
        _displayEmptyFieldMessage.value = true
    }
    fun displayEmptyFieldMessageEnd(){
        _displayEmptyFieldMessage.value = false
    }

    //========================================= Init

    init {
        cookName.value = ""
        _displayEmptyFieldMessage.value =false
        _hide.value = false
    }

    //========================================= Functions

    fun onAddButtonClicked(userId: String){
        uiScope.launch {
            val _cookName = cookName.value
            if (_cookName != null) {
                if(_cookName.isNotEmpty()){
                    val cook = Cook(name = _cookName)
                    addNewCook(cook, userId)
                    hide()
                }else{
                    displayEmptyFieldMessage()
                }
            }
        }
    }

    private suspend fun addNewCook(cook: Cook, userId: String) {
        withContext(Dispatchers.IO){
            FirebaseDatabase.addNewCook(cook,userId)
        }
    }

    //=========================================

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}