package com.example.madassignment

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class CommunityViewModel : ViewModel() {
    val groupName = MutableLiveData<String>()
    val groupCode = MutableLiveData<String>()

    // Method to check if the user has joined a community
    fun isJoinedCommunity(): Boolean {
        return groupName.value != null && groupCode.value != null
    }

    fun observeCommunityDetails(owner: LifecycleOwner, observer: Observer<String>) {
        groupName.observe(owner, observer)
        groupCode.observe(owner, observer)
    }

    // Method to set community details in the ViewModel
    fun notifyObservers() {
        groupName.value = groupName.value // This triggers the observer
        groupCode.value = groupCode.value // This triggers the observer
    }
}
