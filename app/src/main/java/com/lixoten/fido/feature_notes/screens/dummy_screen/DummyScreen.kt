package com.lixoten.fido.feature_notes.screens.dummy_screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.lixoten.fido.R
import com.lixoten.fido.feature_notes.screens._components_shared.MyTopBar
import com.lixoten.fido.navigation.NavigationDestination

object DummyScreenDestination : NavigationDestination {
    override val route = "dummy"

    @StringRes
    override val titleRes = R.string.dummy_screen_name
}

@Composable
fun DummyScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            MyTopBar(
                screenTitle = stringResource(id = DummyScreenDestination.titleRes),
                canNavigateUp = true,
                navigateUp = { navController.navigateUp() },
            )
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = stringResource(id = R.string.home_screen_button))
                }
            }
        }
    }
}