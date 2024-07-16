package com.example.chattingApp.viewModel

import androidx.lifecycle.ViewModel
import com.example.chattingApp.data.repository.UserServiceRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserServiceRepositoryImpl
) : ViewModel() {


}