// Path: app/src/main/java/com/castrodev/recipemanager/core/ui/ErrorMessages.kt
package com.castrodev.recipemanager.core.ui

import androidx.annotation.StringRes
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.core.error.AppError
import com.castrodev.recipemanager.core.util.toAppError

/** Texto localizado, apto para el usuario, de cada error. Nunca incluye detalle técnico. */
@StringRes
fun AppError.messageRes(): Int = when (this) {
    is AppError.Network -> R.string.error_network
    is AppError.Server -> R.string.error_server
    is AppError.Timeout -> R.string.error_timeout
    is AppError.Unauthorized -> R.string.error_unauthorized
    is AppError.Forbidden -> R.string.error_forbidden
    is AppError.NotFound -> R.string.error_not_found
    is AppError.TooManyRequests -> R.string.error_too_many_requests
    is AppError.InvalidCredentials -> R.string.error_invalid_credentials
    is AppError.InvalidEmail -> R.string.error_invalid_email
    is AppError.EmailAlreadyInUse -> R.string.error_email_in_use
    is AppError.WeakPassword -> R.string.error_weak_password
    is AppError.Validation -> when (reason) {
        AppError.Validation.Reason.EmailAndPasswordRequired -> R.string.validation_email_password_required
        AppError.Validation.Reason.AllFieldsRequired -> R.string.validation_all_fields_required
        AppError.Validation.Reason.PasswordTooShort -> R.string.validation_password_too_short
        AppError.Validation.Reason.RecipeNameRequired -> R.string.validation_recipe_name_required
    }
    is AppError.Unknown -> R.string.error_unknown
}

/** Atajo para los ViewModels: cualquier fallo, incluso uno que no sea [AppError], tiene mensaje seguro. */
@StringRes
fun Throwable.toMessageRes(): Int = toAppError().messageRes()