package com.lixoten.fido.feature_notes.screens.notes_list

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.lixoten.fido.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lixoten.fido.feature_notes.data.InitialData
import com.lixoten.fido.feature_notes.model.Note
import com.lixoten.fido.feature_notes.screens._components_shared.MyTopBar
import com.lixoten.fido.feature_notes.screens.edit_note.EditNoteScreenDestination
import com.lixoten.fido.navigation.NavigationDestination
import kotlinx.coroutines.launch

object NotesScreenDestination : NavigationDestination {
    override val route = "note_list"

    @StringRes
    override val titleRes = R.string.app_name
}

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NotesViewmodel = viewModel(factory = NotesViewmodel.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()
    //works too val uiState by notesViewModel.homeUiState.collectAsState()
    //works too val xxx by notesViewModel.getFull().collectAsState(emptyList())

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyTopBar(
                screenTitle = stringResource(id = NotesScreenDestination.titleRes),
                navigateUp = { navController.navigateUp() },
                canNavigateUp = true,
                canAdd = true,
                onAddRecord = {
                    //viewModel.addDbRecord()
                    viewModel.onEvents(NotesEvents.AddDbRecord)
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(EditNoteScreenDestination.route)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        scaffoldState = scaffoldState,
        //snackbarHost = {
        //    scaffoldState.snackbarHostState
        //}
    ) { paddingValues ->
        // I do not like these 2 variables here
        val remove_note_msg = stringResource(R.string.remove_note_msg)
        val remove_note_undo_label = stringResource(R.string.remove_note_undo_label)

        //scaffoldState.drawerState
        Column(modifier = modifier.padding(paddingValues)) {
            NoteList(
                uiState = uiState,

                onCheckedNote = { note, _, _ ->
                    viewModel.onEvents(NotesEvents.UpdateDbIsCheck(note))
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            "WTF" + note.title
                        )
                    }
                },
                onRemoveNote = { note ->
                    viewModel.onEvents(NotesEvents.RemoveDbRecord(note))

                    scope.launch {
                        val result = scaffoldState.snackbarHostState.showSnackbar(
                            message = remove_note_msg,
                            actionLabel = remove_note_undo_label,
                        )
                        if(result == SnackbarResult.ActionPerformed) {
                            viewModel.onEvents(NotesEvents.RestoreDbRecord)
                        }
                    }
                },
                onNoteClick = {
                    navController.navigate(
                        EditNoteScreenDestination.route + "?id=${it.id}"
                    )
                },
                onOrderChange = {
                    viewModel.onEvents(NotesEvents.UpdateStateNoteOrderBy(it))
                    //////////.......notesViewModel.onEvents(NotesEvents.OrderLixo(it))
                },
                onToggleSection = {
                    viewModel.onEvents(NotesEvents.UpdateStateOrderSectionIsVisible)
                    //////////.......notesViewModel.onEvents(NotesEvents.ToggleOrderSection)
                },
                onPinnedNote = {
                    viewModel.onEvents(NotesEvents.UpdateDbIsUnpin(it))
                    //////////.......notesViewModel.onEvents(NotesEvents.ToggleOrderSection)
                }
            )
        }
    }
}

@Composable
fun NoteList(
    modifier: Modifier = Modifier,
    uiState: NotesUiState,
    //list: List<Note>,
    onCheckedNote: (Note, Int, Boolean) -> Unit,
    onRemoveNote: (Note) -> Unit,
    onNoteClick: (Note) -> Unit,
    onPinnedNote: (Note) -> Unit,
    onOrderChange: (NoteOrderBy) -> Unit,
    onToggleSection: () -> Unit

) {
    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.h4
            )
            IconButton(
                onClick =  onToggleSection
                //{
                    //notesViewmodel.onEvents(NotesEvents.UpdateIsOrderSectionVisible())
                    //..viewModel.onEvent(NoteListEventsWrapper.ToggleOrderSection)
                //},
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = stringResource(R.string.sort_icon_descr),
                    //modifier = Modifier.size(22.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = uiState.isOrderSectionVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            OrderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                noteOrderBy = uiState.noteOrderBy,
                onOrderChange = onOrderChange
//                {
//
//                    //danger viewModel.onEvent(NoteListEventsWrapper.Order(it))
//                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            itemsIndexed(
                items = uiState.notes,
                /**
                 * Use key param to define unique keys representing the items in a mutable list,
                 * instead of using the default key (list position). This prevents unnecessary
                 * recompositions.
                 */
                //key = { xx, note -> note.id }
            ) { index, note ->
                NoteItem(
                    note = note,
                    checked = note.isChecked,
                    onCheckedNote = { itt -> onCheckedNote(note, index, itt) },
                    onRemoveNote = { onRemoveNote(note) },
                    onNoteClick = { onNoteClick(note) },
                    //onPinnedNote = onPinnedNote
                    onPinnedNote = { onPinnedNote(note) }
                )
            }
        }

    }
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    checked: Boolean,
    onCheckedNote: (Boolean) -> Unit,
    onRemoveNote: () -> Unit,
    onPinnedNote: (Note) -> Unit,
    onNoteClick: (Note) -> Unit,
) {
    Surface(
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = 8.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(1.dp)
                .background(Color(note.color))
                .clickable {
                    onNoteClick(note)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.isPinned) {
                        IconButton(
                            onClick = { onPinnedNote(note) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pin_filled),
                                contentDescription = "pin",
                                //tint = Violet,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                    Text(
                        modifier = Modifier,
                        //.padding(start = 16.dp),
                        text = note.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h6
                    )
                }
                Text(
                    modifier = Modifier,
                        //.padding(start = 16.dp),
                    text = note.content,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1
                )
            }
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedNote
            )
            IconButton(onClick = onRemoveNote) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = stringResource(R.string.remove_note_img_desc)
                )
            }
        }
    }
}



@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    noteOrderBy: NoteOrderBy = NoteOrderBy.Title(OrderType.Descending),
    onOrderChange: (NoteOrderBy) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "Title",
                selected = noteOrderBy is NoteOrderBy.Title,
                onSelected = {
                    onOrderChange(NoteOrderBy.Title(noteOrderBy.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Date",
                selected = noteOrderBy is NoteOrderBy.Date,
                onSelected = {
                    onOrderChange(NoteOrderBy.Date(noteOrderBy.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Color",
                selected = noteOrderBy is NoteOrderBy.Color,
                onSelected = {
                    onOrderChange(NoteOrderBy.Color(noteOrderBy.orderType))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "Ascending",
                selected = noteOrderBy.orderType is OrderType.Ascending,
                onSelected = {
                    onOrderChange(noteOrderBy.copy(OrderType.Ascending))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Descending",
                selected = noteOrderBy.orderType is OrderType.Descending,
                onSelected = {
                    onOrderChange(noteOrderBy.copy(OrderType.Descending))
                }
            )
        }
    }
}

@Composable
fun DefaultRadioButton(
    text: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelected,
            colors =  RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colors.primary,
                unselectedColor = MaterialTheme.colors.onBackground
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.body1)
    }
}

@Preview
@Composable
fun NoteListPreview() {
    val mockNoteList = InitialData.getNotes().toMutableStateList()

    NoteList(
        uiState = NotesUiState(
            notes = mockNoteList,

        ),
        onCheckedNote = { _, _, _ -> },
        onRemoveNote = { },
        onNoteClick = { },
        onPinnedNote = { },
        onOrderChange = {},
        onToggleSection = { },
    )
}


@Preview
@Composable
fun NoteRowPreview() {
    val mockNote = InitialData.getNotes().toMutableStateList()[0]

    NoteItem(
        note = mockNote,
        checked = mockNote.isChecked,
        onCheckedNote = { },
        onRemoveNote = { },
        onNoteClick = { },
        onPinnedNote = { }
    )
}