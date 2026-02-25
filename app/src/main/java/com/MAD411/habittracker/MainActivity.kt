package com.MAD411.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Habit(val title: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HabitTrackerApp()
            }
        }
    }
}

@Composable
fun HabitTrackerApp() {
    // Observable list that LazyColumn can react to
    val habits = remember { mutableStateListOf<Habit>() }

    // Input state (use rememberSaveable so rotation keeps the typed text)
    var habitTitle by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { HabitHeader() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            HabitInput(
                title = habitTitle,
                onTitleChange = { habitTitle = it },
                onAddHabit = {
                    val trimmed = habitTitle.trim()
                    if (trimmed.isNotEmpty()) {
                        habits.add(Habit(trimmed))
                        habitTitle = ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HabitList(habits = habits)

            Spacer(modifier = Modifier.height(12.dp))

            HabitFooter(total = habits.size)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitHeader() {
    TopAppBar(
        title = { Text("Student Habit Tracker") }
    )
}

@Composable
fun HabitInput(
    title: String,
    onTitleChange: (String) -> Unit,
    onAddHabit: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("New habit") },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAddHabit,
            modifier = Modifier.heightIn(min = 56.dp)
        ) {
            Text("Add")
        }
    }
}

@Composable
fun HabitList(habits: List<Habit>) {
    if (habits.isEmpty()) {
        Text("No habits yet. Add one above.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = habits,
            key = { habit -> habit.title } // good habit (pun intended) for list stability
        ) { habit ->
            HabitRow(habit = habit)
        }
    }
}

@Composable
fun HabitRow(habit: Habit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = habit.title,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun HabitFooter(total: Int) {
    Text("Total habits: $total")
}