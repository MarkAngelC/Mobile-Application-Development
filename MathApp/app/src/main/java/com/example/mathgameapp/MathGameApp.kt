package com.example.mathgameapp

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import kotlin.random.Random

// ---------------- COLORS ----------------

val Primary = Color(0xFF6C63FF)
val Secondary = Color(0xFF00C9A7)
val Accent = Color(0xFFFF6B6B)
val BackgroundTop = Color(0xFFEEF2FF)
val BackgroundBottom = Color(0xFFDDE7FF)

// ---------------- DATA ----------------

data class MathQuestion(val a: Int, val b: Int) {
    val answer: Int get() = a + b
}

data class GameState(
    val questions: List<MathQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0
)

sealed class Screen {
    object Start : Screen()
    object Game : Screen()
    object Result : Screen()
}

// ---------------- APP ----------------

@Composable
fun MathGameApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Start) }
    var gameState by remember { mutableStateOf(GameState()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BackgroundTop, BackgroundBottom)
                )
            )
    ) {
        AnimatedContent(targetState = screen, label = "") { current ->

            when (current) {

                Screen.Start -> StartScreen { total ->
                    val questions = List(total) {
                        MathQuestion(Random.nextInt(1, 50), Random.nextInt(1, 50))
                    }
                    gameState = GameState(questions = questions)
                    screen = Screen.Game
                }

                Screen.Game -> QuestionScreen(
                    gameState,
                    onAnswer = { answer ->
                        val q = gameState.questions[gameState.currentIndex]
                        val correct = answer == q.answer

                        val nextIndex = gameState.currentIndex + 1

                        gameState = gameState.copy(
                            correctCount = gameState.correctCount + if (correct) 1 else 0,
                            wrongCount = gameState.wrongCount + if (correct) 0 else 1,
                            currentIndex = nextIndex
                        )

                        if (nextIndex >= gameState.questions.size) {
                            screen = Screen.Result
                        }
                    },
                    onCancel = {
                        screen = Screen.Start
                        gameState = GameState()
                    }
                )

                Screen.Result -> ResultScreen(gameState) {
                    screen = Screen.Start
                    gameState = GameState()
                }
            }
        }
    }
}

// ---------------- GLASS CARD ----------------

@Composable
fun GlassCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        content()
    }
}

// ---------------- START ----------------

@Composable
fun StartScreen(onStart: (Int) -> Unit) {
    var input by remember { mutableStateOf("") }
    val count = input.toIntOrNull()?.coerceIn(1, 50)

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("🧠 Math Challenge", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Primary)
        Text("Test your brain speed", color = Color.Gray)

        Spacer(Modifier.height(40.dp))

        GlassCard {
            OutlinedTextField(
                value = input,
                onValueChange = { if (it.length <= 2) input = it },
                placeholder = { Text("Enter 1–50") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { count?.let { onStart(it) } },
            enabled = count != null,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Start", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------- QUESTION ----------------

@Composable
fun QuestionScreen(
    gameState: GameState,
    onAnswer: (Int) -> Unit,
    onCancel: () -> Unit
) {
    val total = gameState.questions.size
    val index = gameState.currentIndex
    if (index >= total) return

    val q = gameState.questions[index]
    val progress = (index + 1) / total.toFloat()

    var input by remember(index) { mutableStateOf("") }
    val answer = input.trim().toIntOrNull()

    var showDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {

        // TOP
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("✓ ${gameState.correctCount}", color = Secondary)
            Text("✗ ${gameState.wrongCount}", color = Accent)
            Text("Cancel", modifier = Modifier.clickable { showDialog = true })
        }

        Spacer(Modifier.height(16.dp))

        Text("Question ${index + 1} / $total", modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(10.dp)),
            color = Primary
        )

        Spacer(Modifier.height(40.dp))

        GlassCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${q.a} + ${q.b}", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Primary)
                Text("=", fontSize = 30.sp)
                Text("?", fontSize = 42.sp, color = Accent)
            }
        }

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("?") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { answer?.let { onAnswer(it) } },
            enabled = answer != null,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(if (index == total - 1) "Finish" else "Next", fontSize = 18.sp)
        }
    }

    // Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onCancel()
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("No") }
            },
            title = { Text("Cancel Game?") },
            text = { Text("Progress will be lost.") }
        )
    }
}

// ---------------- RESULT ----------------

@Composable
fun ResultScreen(gameState: GameState, onRestart: () -> Unit) {
    val total = gameState.questions.size
    val correct = gameState.correctCount
    val percent = if (total > 0) (correct * 100 / total) else 0

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Results", fontSize = 32.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text("$percent%", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        Text("Score: $correct / $total")

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Play Again")
        }
    }
}