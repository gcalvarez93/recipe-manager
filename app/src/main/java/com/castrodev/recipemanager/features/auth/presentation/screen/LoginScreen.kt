// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/presentation/screen/LoginScreen.kt
package com.castrodev.recipemanager.features.auth.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.auth.presentation.components.AuthTextField
import com.castrodev.recipemanager.features.auth.presentation.components.GoogleSignInButton
import com.castrodev.recipemanager.features.auth.presentation.viewmodel.AuthState
import com.castrodev.recipemanager.features.auth.presentation.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import timber.log.Timber

private const val GOOGLE_WEB_CLIENT_ID =
    "647694841688-5u9b89na24jnnpav7o1cfecqlfqn4t2p.apps.googleusercontent.com"

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val authState         by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()
    val context           = LocalContext.current

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Credential Manager sustituye a la API GoogleSignIn, obsoleta.
    val signInWithGoogle: () -> Unit = {
        scope.launch {
            try {
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(GetSignInWithGoogleOption.Builder(GOOGLE_WEB_CLIENT_ID).build())
                    .build()
                val credential = CredentialManager.create(context).getCredential(context, request).credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    viewModel.signInWithGoogle(GoogleIdTokenCredential.createFrom(credential.data).idToken)
                } else {
                    snackbarHostState.showSnackbar("Google sign in failed")
                }
            } catch (e: GetCredentialCancellationException) {
                // El usuario cerró el selector de cuentas: no es un error.
            } catch (e: GetCredentialException) {
                Timber.w(e, "LoginScreen.signInWithGoogle")
                snackbarHostState.showSnackbar(e.message ?: "Google sign in failed")
            } catch (e: GoogleIdTokenParsingException) {
                Timber.w(e, "LoginScreen.signInWithGoogle parse")
                snackbarHostState.showSnackbar("Google sign in failed")
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> onLoginSuccess()
            is AuthState.Error   -> { snackbarHostState.showSnackbar((authState as AuthState.Error).message); viewModel.clearError() }
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.login_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(40.dp))
            AuthTextField(value = email, onValueChange = { email = it }, label = stringResource(R.string.email), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(16.dp))
            AuthTextField(value = password, onValueChange = { password = it }, label = stringResource(R.string.password), isPassword = true)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick  = { viewModel.signInWithEmail(email, password) },
                enabled  = email.isNotBlank() && password.isNotBlank() && authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (authState is AuthState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text(stringResource(R.string.sign_in))
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(stringResource(R.string.or), modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            GoogleSignInButton(onClick = signInWithGoogle)
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.no_account), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                TextButton(onClick = onNavigateToRegister) { Text(stringResource(R.string.register), color = MaterialTheme.colorScheme.primary) }
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}