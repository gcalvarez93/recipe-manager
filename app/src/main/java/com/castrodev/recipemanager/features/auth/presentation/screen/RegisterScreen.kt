// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/presentation/screen/RegisterScreen.kt
package com.castrodev.recipemanager.features.auth.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.auth.presentation.components.AuthTextField
import com.castrodev.recipemanager.features.auth.presentation.viewmodel.AuthState
import com.castrodev.recipemanager.features.auth.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val authState         by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> onRegisterSuccess()
            is AuthState.Error   -> { snackbarHostState.showSnackbar((authState as AuthState.Error).message); viewModel.clearError() }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_account)) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(24.dp))
            AuthTextField(value = name, onValueChange = { name = it }, label = stringResource(R.string.name))
            Spacer(Modifier.height(16.dp))
            AuthTextField(value = email, onValueChange = { email = it }, label = stringResource(R.string.email), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(16.dp))
            AuthTextField(value = password, onValueChange = { password = it }, label = stringResource(R.string.password), isPassword = true)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick  = { viewModel.signUpWithEmail(email, password, name) },
                enabled  = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (authState is AuthState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text(stringResource(R.string.register))
            }
        }
    }
}