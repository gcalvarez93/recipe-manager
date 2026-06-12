// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/presentation/components/GoogleSignInButton.kt
package com.castrodev.recipemanager.features.auth.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.castrodev.recipemanager.R

@Composable
fun GoogleSignInButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick  = onClick,
        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
        shape    = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter           = painterResource(R.drawable.ic_google),
                contentDescription = null,
                tint              = Color.Unspecified,
                modifier          = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(R.string.sign_in_google), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}