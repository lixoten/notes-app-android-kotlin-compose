package com.lixoten.fido.feature_notes.presentation._components_shared

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lixoten.fido.R

@Composable
fun MyTopBar(
    screenTitle: String,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit = { },
    canAdd: Boolean = false,
    onAddRecord: () -> Unit = { },
//    canDelete: Boolean = false,
//    deleteRecord: () -> Unit = { },
//    canPin: Boolean = false,
//    pinRecord: () -> Unit = { },
//    isPinned: Boolean = false
) {
    if (canNavigateUp) {
        TopAppBar(
            title = { Text(text = screenTitle) },
            navigationIcon = {
                IconButton(
                    onClick = navigateUp
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_arrow)
                    )
                }
            },
            actions = {
//                if (canDelete) {
//                    IconButton(
//                        onClick = pinRecord
//                    ) {
//                        Icon(
//                            painter = if (isPinned) painterResource(id = R.drawable.ic_pin_filled)
//                            else painterResource(id = R.drawable.ic_pin),
//                            contentDescription = stringResource(R.string.add_input_label),
//                            modifier = Modifier.size(24.dp),
//                            tint = Violet
//                        )
//                    }
//                }
                if (canAdd) {
                    IconButton(
                        onClick = onAddRecord
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_note_icon_descr),
                            //tint = Color.Red
                            //modifier = Modifier.size(24.dp),
                        )
                    }
                }
            },

            elevation = 8.dp
        )
    } else {
        TopAppBar(
            title = { Text(text = screenTitle) },
            elevation = 8.dp
        )
    }
}