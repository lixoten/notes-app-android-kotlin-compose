package com.lixoten.fido.feature_notes.presentation.notes_list

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.lixoten.fido.DrawerBody
import com.lixoten.fido.feature_notes.data.InitialData
import com.lixoten.fido.feature_notes.model.Note
import com.lixoten.fido.feature_notes.presentation._components_shared.MyTopBar
import com.lixoten.fido.feature_notes.presentation.edit_note.EditNoteScreenDestination
import com.lixoten.fido.navigation.NavDrawerItem
import com.lixoten.fido.navigation.NavigationDestination
import com.lixoten.fido.navigation.doMe
import kotlinx.coroutines.launch

object NotesScreenDestination : NavigationDestination {
    override val route = "note_list"

    @StringRes
    override val titleRes = R.string.notes_screen_name
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
                canNavigateUp = false,
                canAdd = true,
                hasMenu = true,
                isGridLayout = uiState.isGridLayout,
                onAddRecord = {
                    //viewModel.addDbRecord()
                    viewModel.onEvents(NotesEvents.AddDbRecord)
                },
                onToggleLayout = {
                    viewModel.onEvents(NotesEvents.ToggleLayout)
                },
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                onToggleSection = {
                    viewModel.onEvents(NotesEvents.UpdateStateOrderSectionIsVisible)
                    //////////.......notesViewModel.onEvents(NotesEvents.ToggleOrderSection)
                },
                onToggleSearch = {
                    viewModel.onEvents(NotesEvents.ToggleSearch)
                    //////////.......notesViewModel.onEvents(NotesEvents.ToggleOrderSection)
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
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerBody(
                items = listOf(
                    NavDrawerItem.Home,
                    NavDrawerItem.Dummy,
                ),
                onItemClick = {
                    //println("Clicked on ${it.title}")
                    doMe(
                        scope = scope,
                        drawerState = scaffoldState.drawerState,
                        navController = navController,
                        id = it.id
                    )
                }
            )
        },
        scaffoldState = scaffoldState,
        //snackbarHost = {
        //    scaffoldState.snackbarHostState
        //}
    ) { paddingValues ->
        // I do not like these 2 variables here
        val remove_note_msg = stringResource(R.string.remove_note_msg)
        val remove_note_undo_label = stringResource(R.string.remove_note_undo_label)

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
                        if (result == SnackbarResult.ActionPerformed) {
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
//                onToggleSection = {
//                    viewModel.onEvents(NotesEvents.UpdateStateOrderSectionIsVisible)
//                    //////////.......notesViewModel.onEvents(NotesEvents.ToggleOrderSection)
//                },
                onPinnedNote = {
                    viewModel.onEvents(NotesEvents.UpdateDbIsUnpin(it))
                    //////////.......notesViewModel.onEvents(NotesEvents.ToggleOrderSection)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
    //onToggleSection: () -> Unit

) {
    Column() {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
////            Text(
////                text = uiState.isGridLayout.toString(),
////                style = MaterialTheme.typography.h4
////            )
////            Text(
////                text = "Notes",
////                style = MaterialTheme.typography.h4
////            )
//            IconButton(
//                onClick = onToggleSection
//                //{
//                //notesViewmodel.onEvents(NotesEvents.UpdateIsOrderSectionVisible())
//                //..viewModel.onEvent(NoteListEventsWrapper.ToggleOrderSection)
//                //},
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Sort,
//                    contentDescription = stringResource(R.string.sort_icon_descr),
//                    //modifier = Modifier.size(22.dp)
//                )
//            }
//        }
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

        //Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.isSearchVisible) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "", onValueChange = {}
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        if (uiState.isGridLayout) {
            LazyVerticalStaggeredGrid(
                //columns = GridCells.Fixed(2)
                columns = StaggeredGridCells.Fixed(2),
                //horizontalArrangement = Arrangement.spacedBy(0.dp),
                content = {
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
                            onPinnedNote = { onPinnedNote(note) },
                            isGridLayout = uiState.isGridLayout,
                        )
                    }

                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                        onPinnedNote = { onPinnedNote(note) },
                        isGridLayout = uiState.isGridLayout,
                    )
                }
            }
        }

        //}


    }
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    checked: Boolean,
    isGridLayout: Boolean,
    onCheckedNote: (Boolean) -> Unit,
    onRemoveNote: () -> Unit,
    onPinnedNote: (Note) -> Unit,
    onNoteClick: (Note) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable {
                onNoteClick(note)
            },
        //backgroundColor = Color(note.color),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = 8.dp
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(1.dp)
                .background(Color(note.color))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                //.fillMaxWidth()
                //.padding(1.dp)
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
                        maxLines = 12,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body1
                    )
                }
                if (!isGridLayout) {
                    Row {
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
            if (isGridLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
            colors = RadioButtonDefaults.colors(
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
        //onToggleSection = { },
    )
}


@Preview
@Composable
fun NoteRowPreview() {
    val mockNote =
        InitialData.getNotes().toMutableStateList()[0]

    NoteItem(
        note = mockNote,
        checked = mockNote.isChecked,
        onCheckedNote = { },
        onRemoveNote = { },
        onNoteClick = { },
        onPinnedNote = { },
        isGridLayout = false
    )
}