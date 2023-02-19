package com.lixoten.fido.feature_notes.screens.edit_note

import androidx.annotation.StringRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lixoten.fido.R
import com.lixoten.fido.feature_notes.model.Note
import com.lixoten.fido.feature_notes.screens._components_shared.MyTextField
import com.lixoten.fido.feature_notes.screens._components_shared.MyTopBar
import com.lixoten.fido.navigation.NavigationDestination
import com.lixoten.fido.ui.theme.Violet
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object EditNoteScreenDestination : NavigationDestination {
    override val route = "edit_note"

    @StringRes
    override val titleRes = R.string.edit_add_note_screen_name

    //const val routeArg = "id"
    const val routeArg = "id="
    val routeWithArgs = "$route{$routeArg}"

}

@Composable
fun EditNoteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddEditNoteViewmodel = viewModel(factory = AddEditNoteViewmodel.Factory),
) {
    // addEditNoteViewModel.setCode(code)
    val uiState by viewModel.uiState.collectAsState()


    val noteColor = -1
    val noteBackgroundAnimatable = remember {
        Animatable(
            //Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
            Color(uiState.note.color)
        )
    }
    val scope = rememberCoroutineScope()

    val scaffoldState: ScaffoldState = rememberScaffoldState()



    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewmodel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                //is AddEditNoteViewmodel.UiEvent.SaveNote -> {
                //    navController.navigateUp()
                //}
            }
        }
    }




    Scaffold(
        topBar = {
            MyTopBar(
                screenTitle = stringResource(id = EditNoteScreenDestination.titleRes),
                canNavigateUp = true,
                navigateUp = { navController.navigateUp() },
//                canDelete = uiState.note.id != -1,
//                deleteRecord = {
//                    viewModel.removeDbNote(viewModel.noteModel)
//                    navController.popBackStack()
//                },
            )
        },
        floatingActionButton =
        if (uiState.dataHasChanged && !uiState.titleError) {
            {
                FloatingActionButton(
                    modifier = Modifier,
                    onClick = {
                        viewModel.updateDbNote()
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.save_note_img_desc)
                    )
                }
            }
        } else {
            {
                FloatingActionButton(
                    modifier = Modifier,
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Add Task")
                }
            }
        },
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        }

    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
        ) {
            //val mContext = LocalContext.current
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
//                val v: ImageVector
//                val cd: String
//                val tin: Color
//                if (uiState.isChecked) {
//                    v = Icons.Default.DoneOutline
//                    cd = "Done"
//                    tin = Color.Red
//                } else {
//                    v = Icons.Default.Done
//                    cd = "Done"
//                    tin = Color.Red
//                }
//                IconButton(
//                    enabled = uiState.title.isNotEmpty(),
//                    onClick = {
//                        viewModel.updateStateCheck(!uiState.isChecked)
//                    }
//                ) {
//                    Icon(
//                        imageVector = v,
//                        contentDescription = cd,
//                        tint = tin
//                    )
//                }
                IconButton(
                    onClick = {
                        viewModel.updateStatePinned()
                    }
                ) {
                    Icon(
                        painter = if (uiState.note.isPinned) painterResource(id = R.drawable.ic_pin_filled)
                        else painterResource(id = R.drawable.ic_pin),
                        contentDescription = "pin",
                        tint = Violet,
                        modifier = Modifier.size(24.dp),
                    )
                }
                IconButton(
                    onClick = {
                        if (uiState.note.id > 0) {
                            viewModel.removeDbNote(viewModel.uiState.value.note)
                        }
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.LightGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Status :")
                    Checkbox(
                        checked = uiState.note.isChecked,
                        onCheckedChange = {
                            viewModel.updateStateCheck(it)
                        }
                    )
                }
            }
            MyTextField(
                value = uiState.note.title,
                onValueChange = {

                    viewModel.updateStateTitle(it)
                    //if (it.length <= 15) editNoteViewModel.updateStateTitle(it)
                    //else Toast.makeText(mContext, "Cannot be more than 5 Characters", Toast.LENGTH_SHORT).show()

                },
                onDone = { focusManager.moveFocus(FocusDirection.Down) },
                labelResId = if (uiState.titleError) R.string.add_input_label_error else R.string.add_input_label,
                placeHolderResId = R.string.add_input_placeholder,
                error = uiState.titleError,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(uiState.note.color))
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyTextField(
                value = uiState.note.content,
                onValueChange = {

                    viewModel.updateStateContent(it)
                    //if (it.length <= 15) editNoteViewModel.updateStateTitle(it)
                    //else Toast.makeText(mContext, "Cannot be more than 5 Characters", Toast.LENGTH_SHORT).show()


                },
                labelResId = R.string.add_content_label,
                placeHolderResId = R.string.add_content_placeholder,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f)
                    .background(color = Color(uiState.note.color))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Note.noteColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Surface(
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(color)
                                .clip(CutCornerShape(12.dp))
                                .shadow(15.dp, CutCornerShape(23.dp))
                                .border(
                                    width = 4.dp,
                                    color = if (uiState.note.color == colorInt) {
                                        Color.Black
                                    } else Color.Transparent,
                                    shape = CutCornerShape(20.dp)
                                )
                                .clickable {
                                    scope.launch {
                                        noteBackgroundAnimatable.animateTo(
                                            targetValue = Color(colorInt),
                                            animationSpec = tween(
                                                durationMillis = 500
                                            )
                                        )
                                    }
                                    viewModel.updateStateColor(colorInt)
                                }
                        )
                    }
                }
            }
        }
    }
}