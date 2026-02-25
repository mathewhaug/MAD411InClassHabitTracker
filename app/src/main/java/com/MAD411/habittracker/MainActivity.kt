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


//Add a stable unique id so LazyColumn keys are never duplicated
//This prevents crashes when two habits have the same title
data class Habit(
    val id: Long = System.currentTimeMillis(),
    val title: String
)

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
    //observable list that LazyColumn can react to
    val habits = remember { mutableStateListOf<Habit>() }

    // Input state stays in the parent (this is the "source of truth")
    //keep input state here so HabitInput can remain stateless.
    var habitTitle by rememberSaveable { mutableStateOf("") }

    var hasTriedSubmit by rememberSaveable { mutableStateOf(false) }

    var validationMessage by remember { mutableStateOf<String?>(null) }

    val trimmedTitle = habitTitle.trim()

    val isDuplicate = habits.any { it.title.equals(trimmedTitle, ignoreCase = true) }

    val showError = hasTriedSubmit && (trimmedTitle.isEmpty() || isDuplicate)

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
                onTitleChange = {
                    habitTitle = it
                    validationMessage = null
                },
                isError = showError,
                errorMessage = validationMessage ?: "",
                isAddEnabled = true,
                onAddHabit = {
                    hasTriedSubmit = true

                    when {
                        trimmedTitle.isEmpty() -> {
                            validationMessage = "Habit title cannot be empty"
                        }
                        isDuplicate -> {
                            validationMessage = "That habit already exists"
                        }
                        else -> {
                            habits.add(Habit(title = trimmedTitle))
                            habitTitle = ""
                            validationMessage = null
                            hasTriedSubmit = false
                        }
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
    isError: Boolean,
    errorMessage: String,
    isAddEnabled: Boolean,
    onAddHabit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("New habit") },
                isError = isError,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAddHabit,
                enabled = isAddEnabled,
                modifier = Modifier.heightIn(min = 56.dp)
            ) {
                Text("Add")
            }
        }

        if (isError && errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
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
            key = { habit -> habit.id }
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