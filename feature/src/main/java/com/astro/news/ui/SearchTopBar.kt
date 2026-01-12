package com.astro.news.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.astro.feature.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstroNewsTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchTriggered: () -> Unit = {},
    onSearchClosed: () -> Unit = {}
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    if (isSearchActive) {
        TopAppBar(
            title = {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(stringResource(R.string.search_articles)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    isSearchActive = false
                    onSearchClosed()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            }
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.astro_news),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                IconButton(onClick = {
                    isSearchActive = true
                    onSearchTriggered()
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_articles)
                    )
                }
            }
        )
    }
}
