package com.example.farmyukti // <--- MAKE SURE THIS MATCHES YOUR OTHER FILES

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// --- SHARED SEARCH & FILTER COMPONENT ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterSection(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    grades: List<String>,
    selectedGrade: String,
    onGradeChange: (String) -> Unit,
    locations: List<String>,
    selectedLocation: String,
    onLocationChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 1. Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search by crop or farmer...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        // 2. Filters Row (Scrollable)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterDropdown(
                    label = "Category",
                    options = categories,
                    selected = selectedCategory,
                    onSelect = onCategoryChange
                )
            }
            item {
                FilterDropdown(
                    label = "Grade",
                    options = grades,
                    selected = selectedGrade,
                    onSelect = onGradeChange
                )
            }
            item {
                FilterDropdown(
                    label = "Location",
                    options = locations,
                    selected = selectedLocation,
                    onSelect = onLocationChange
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isSelected = selected != "All" && selected.isNotEmpty()

    Box {
        FilterChip(
            selected = isSelected,
            onClick = { expanded = true },
            label = { Text(if (isSelected) selected else label) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    leadingIcon = if (option == selected) {
                        { Icon(Icons.Default.Check, null) }
                    } else null
                )
            }
        }
    }
}