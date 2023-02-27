package com.lixoten.fido.features.feature_notes.presentation.home

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
import com.lixoten.fido.features.feature_notes.presentation.dummy_screen.DummyScreenDestination
import com.lixoten.fido.features.feature_notes.presentation._components_shared.MyTopBar
import com.lixoten.fido.features.feature_notes.presentation.notes_list.NotesScreenDestination
import com.lixoten.fido.navigation.NavigationDestination

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
    @StringRes
    override val titleRes = R.string.home_screen_name
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            MyTopBar(
                screenTitle = stringResource(id = HomeScreenDestination.titleRes),
                canNavigateUp = false,
                navigateUp = { navController.navigateUp() },
            )
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Button(onClick = { navController.navigate(NotesScreenDestination.route) }) {
                    Text(text = stringResource(id = R.string.notes_screen_button))
                }
                Button(onClick = { navController.navigate(DummyScreenDestination.route) }) {
                    Text(text = stringResource(id = R.string.dummy_screen_name))
                }
            }
        }
    }
}