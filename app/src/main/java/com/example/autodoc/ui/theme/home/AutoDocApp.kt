package com.example.autodoc.ui.theme.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autodoc.R

@Composable
fun AutoDocApp(onScanButtonClicked: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { FabActionButton(modifier = Modifier, onScanButtonClicked) },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Title()
                SearchBar(
                    modifier = Modifier
                )
                val dummyFolders: ArrayList<String> = ArrayList()
                dummyFolders.add("Photo ID")
                dummyFolders.add("University Docs")
                dummyFolders.add("Government Docs")
                dummyFolders.add("General Docs")
                dummyFolders.add("Bills")
                dummyFolders.add("Receipts")
                FoldersList(folders = dummyFolders, modifier = Modifier)
            }
        }
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = "AutoDoc",
            modifier = modifier,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "It's smart, and safe",
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall
        )
    }

}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        var searchText by remember {
            mutableStateOf(TextFieldValue(""))
        }
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = {
                Text(text = stringResource(id = R.string.placeholder_search))
            },
            modifier = modifier
                .fillMaxWidth()
                .size(56.dp)
        )
    }
}

@Composable
fun FoldersList(folders: List<String>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        items(folders) { folder ->
            Folder(name = folder, modifier = modifier)
            Spacer(modifier = modifier.padding(bottom = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Folder(name: String, modifier: Modifier) {
    Card(
        onClick = { /*TODO*/ },
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Folder",
                modifier.padding(16.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FabActionButton(modifier: Modifier, onScanButtonClicked: () -> Unit) {
    FloatingActionButton(
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        onClick = onScanButtonClicked
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Scan Document"
            )
            Text(
                text = "Scan",
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview(widthDp = 320)
@Composable
fun PreviewHomePage() {
}