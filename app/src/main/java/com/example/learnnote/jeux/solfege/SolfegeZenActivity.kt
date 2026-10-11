package com.example.learnnote.jeux.solfege

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class SolfegeZenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                SolfegeZenScreen()
            }
        }
    }
}

enum class ZenStaffPosition(val offset: Int) {

    DO4(-2),
    RE4(-1),
    MI4(0),
    FA4(1),
    SOL4(2),
    LA4(3),
    SI4(4),
    DO5(5),
    RE5(6),
    MI5(7)
}

data class ZenNote(
    val label: String,
    val position: ZenStaffPosition
)

@Composable
fun SolfegeZenScreen() {

    val notes = listOf(

        ZenNote("DO", ZenStaffPosition.DO4),
        ZenNote("RÉ", ZenStaffPosition.RE4),
        ZenNote("MI", ZenStaffPosition.MI4),
        ZenNote("FA", ZenStaffPosition.FA4),
        ZenNote("SOL", ZenStaffPosition.SOL4),
        ZenNote("LA", ZenStaffPosition.LA4),
        ZenNote("SI", ZenStaffPosition.SI4),
        ZenNote("DO", ZenStaffPosition.DO5),
        ZenNote("RÉ", ZenStaffPosition.RE5),
        ZenNote("MI", ZenStaffPosition.MI5)
    )

    var currentNote by remember {
        mutableStateOf(notes.random())
    }

    var answerState by remember {
        mutableStateOf<Boolean?>(null)
    }

    var score by remember {
        mutableIntStateOf(0)
    }

    var showPlusOne by remember {
        mutableStateOf(false)
    }

    fun nextQuestion() {

        currentNote = notes.random()
        answerState = null
    }

    LaunchedEffect(answerState) {

        if (answerState != null) {

            if (answerState == true) {

                delay(350)
                showPlusOne = false
                delay(250)

            } else {

                delay(1000)
            }

            nextQuestion()
        }
    }

    val backgroundGlow by animateColorAsState(
        targetValue = when (answerState) {

            true -> Color(0xFFE4F3EA)
            false -> Color(0xFFF8E3E3)
            null -> Color(0xFFF4EFE8)
        },

        animationSpec = tween(500),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF7F4EF),
                        Color(0xFFF3EFE8),
                        Color(0xFFEDE7DE)
                    )
                )
            )
    ) {

        // Glow décoratif
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-60).dp, y = (-20).dp)
                .alpha(0.20f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Spacer(modifier = Modifier.height(38.dp))

                // HEADER
                Text(
                    text = "Mode Zen",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353535)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Reconnais la note affichée",
                    fontSize = 16.sp,
                    color = Color(0xFF777777)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF8DB7A8),
                                    Color(0xFFD8C7F1)
                                )
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // CARTE PRINCIPALE
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.Transparent
                ) {

                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(

                                        when (answerState) {

                                            true -> Color(0xFFE4F3EA)
                                            false -> Color(0xFFF8E3E3)
                                            null -> Color(0xFFF8F5F0)
                                        },

                                        when (answerState) {

                                            true -> Color(0xFFD8ECE0)
                                            false -> Color(0xFFF3D6D6)
                                            null -> Color(0xFFF2ECE4)
                                        }
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .padding(20.dp)
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // SCORE
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Score : $score",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF5B5B5B)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                AnimatedVisibility(
                                    visible = showPlusOne
                                ) {

                                    Text(
                                        text = "+1",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE7B989)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // ZONE PORTÉE
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(
                                        backgroundGlow.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            ) {

                                Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {

                                    val startX = 80f
                                    val endX = size.width - 50f

                                    val topLineY = 90f
                                    val spacing = 26f

                                    fun getNoteY(position: Int): Float {

                                        val bottomLineY =
                                            topLineY + spacing * 4

                                        return bottomLineY -
                                                (position * (spacing / 2f))
                                    }

                                    // PORTÉE
                                    for (i in 0..4) {

                                        val y = topLineY + i * spacing

                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(startX, y),
                                            end = Offset(endX, y),
                                            strokeWidth = 3f
                                        )
                                    }

                                    // CLÉ DE SOL
                                    drawContext.canvas.nativeCanvas.drawText(
                                        "𝄞",
                                        15f,
                                        210f,
                                        Paint().apply {
                                            textSize = 130f
                                            color = android.graphics.Color.BLACK
                                        }
                                    )

                                    // POSITION NOTE
                                    val noteX = size.width / 2f

                                    val y = getNoteY(
                                        currentNote.position.offset
                                    )

                                    // LIGNE SUPPLÉMENTAIRE BASSE
                                    if (currentNote.position.offset <= -2) {

                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(noteX - 30f, y),
                                            end = Offset(noteX + 40f, y),
                                            strokeWidth = 3f
                                        )
                                    }

                                    // NOTE
                                    drawOval(
                                        color = Color(0xFF2F2F2F),
                                        topLeft = Offset(
                                            noteX,
                                            y - 16f
                                        ),
                                        size = Size(
                                            38f,
                                            26f
                                        )
                                    )

                                    // HAMPE
                                    val shouldStemGoDown =
                                        currentNote.position.offset >= 4

                                    if (shouldStemGoDown) {

                                        // HAMPE À GAUCHE VERS LE BAS
                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(
                                                noteX + 2f,
                                                y
                                            ),
                                            end = Offset(
                                                noteX + 2f,
                                                y + 90f
                                            ),
                                            strokeWidth = 3f
                                        )

                                    } else {

                                        // HAMPE À DROITE VERS LE HAUT
                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(
                                                noteX + 36f,
                                                y
                                            ),
                                            end = Offset(
                                                noteX + 36f,
                                                y - 90f
                                            ),
                                            strokeWidth = 3f
                                        )
                                    }
                                }

                                // BONNE NOTE SI ERREUR
                                if (answerState == false) {

                                    Text(
                                        text = currentNote.label,

                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 10.dp),

                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD96C6C)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // RÉPONSES
                val possibleAnswers = listOf(
                    "DO",
                    "RÉ",
                    "MI",
                    "FA",
                    "SOL",
                    "LA",
                    "SI"
                ).shuffled()

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    possibleAnswers.chunked(2).forEach { rowAnswers ->

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            rowAnswers.forEach { answer ->

                                val isCorrect =
                                    answer == currentNote.label

                                Button(
                                    onClick = {

                                        if (isCorrect) {

                                            score++
                                            answerState = true
                                            showPlusOne = true

                                        } else {

                                            answerState = false
                                        }
                                    },

                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(62.dp),

                                    shape = RoundedCornerShape(24.dp),

                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    )
                                ) {

                                    Text(
                                        text = answer,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF444444)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}