package com.example.voicenotes.presentation.vm

import androidx.lifecycle.ViewModel
import com.example.domain.usecase.SignInFirebaseUseCase
import com.example.domain.usecase.SignUpFirebaseUseCase
import javax.inject.Inject

class AuthorizationViewModel @Inject constructor(
    private val signUpFirebaseUseCase: SignUpFirebaseUseCase,
    private val signInFirebaseUseCase: SignInFirebaseUseCase
): ViewModel() {

}