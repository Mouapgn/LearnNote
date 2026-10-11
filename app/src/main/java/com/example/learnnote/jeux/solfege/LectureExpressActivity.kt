package com.example.learnnote.jeux.solfege

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

class LectureExpressActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                LectureExpressScreen()
            }
        }
    }
}

enum class ExpressStaffPosition(val offset: Int) {

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

data class ExpressNote(
    val label: String,
    val position: ExpressStaffPosition
)

@Composable
fun LectureExpressScreen() {

    val notes = listOf(

        ExpressNote("DO", ExpressStaffPosition.DO4),
        ExpressNote("RÉ", ExpressStaffPosition.RE4),
        ExpressNote("MI", ExpressStaffPosition.MI4),
        ExpressNote("FA", ExpressStaffPosition.FA4),
        ExpressNote("SOL", ExpressStaffPosition.SOL4),
        ExpressNote("LA", ExpressStaffPosition.LA4),
        ExpressNote("SI", ExpressStaffPosition.SI4),
        ExpressNote("DO", ExpressStaffPosition.DO5),
        ExpressNote("RÉ", ExpressStaffPosition.RE5),
        ExpressNote("MI", ExpressStaffPosition.MI5)
    )

    var currentNote by remember {
        mutableStateOf(notes.random())
    }

    var score by remember {
        mutableIntStateOf(0)
    }

    var combo by remember {
        mutableIntStateOf(0)
    }

    var selectedTime by remember {
        mutableIntStateOf(30)
    }

    var timeLeft by remember {
        mutableIntStateOf(30)
    }

    var answerState by remember {
        mutableStateOf<Boolean?>(null)
    }

    var gameFinished by remember {
        mutableStateOf(false)
    }

    fun nextQuestion() {

        currentNote = notes.random()
        answerState = null
    }

    // TIMER
    LaunchedEffect(gameFinished) {

        if (!gameFinished) {

            while (timeLeft > 0) {

                delay(1000)
                timeLeft--
            }

            gameFinished = true
        }
    }

    // FEEDBACK
    LaunchedEffect(answerState) {

        if (answerState != null) {

            if (answerState == true) {

                delay(220)

            } else {

                delay(1000)
            }

            nextQuestion()
        }
    }

    val cardTint by animateColorAsState(
        targetValue = when (answerState) {

            true -> Color(0xFFE4F3EA)
            false -> Color(0xFFF8E3E3)
            null -> Color(0xFFF6F1EA)
        },

        animationSpec = tween(180),
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

        // GLOW
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-60).dp, y = (-20).dp)
                .alpha(0.18f)
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

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Lecture Express",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353535)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Réponds le plus vite possible",
                    fontSize = 16.sp,
                    color = Color(0xFF777777)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE7B989),
                                    Color(0xFFA5BCE8)
                                )
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // INFOS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    InfoCard("Score", "$score")
                    InfoCard("Combo", "x$combo")
                    InfoCard("Temps", "${timeLeft}s")
                }

                Spacer(modifier = Modifier.height(4.dp))

                // CHOIX TEMPS
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    listOf(30, 60, 90).forEach { duration ->

                        val selected =
                            selectedTime == duration

                        Button(
                            onClick = {

                                selectedTime = duration
                                timeLeft = duration
                            },

                            shape = RoundedCornerShape(20.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selected)
                                        Color(0xFF8DB7A8)
                                    else
                                        Color.White
                            )
                        ) {

                            Text(
                                text = "${duration}s",

                                color =
                                    if (selected)
                                        Color.White
                                    else
                                        Color(0xFF444444)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // FIN DE PARTIE
                if (gameFinished) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        color = Color.Transparent
                    ) {

                        Column(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.94f),
                                            Color(0xFFF8F5F0)
                                        )
                                    ),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .padding(30.dp),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Temps écoulé",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF353535)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "$score points",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE7B989)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {

                                    score = 0
                                    combo = 0

                                    timeLeft = selectedTime

                                    gameFinished = false
                                    answerState = null

                                    nextQuestion()
                                },

                                shape = RoundedCornerShape(24.dp),

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8DB7A8)
                                )
                            ) {

                                Text(
                                    text = "Rejouer",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                } else {

                    // CARTE PRINCIPALE
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        color = Color.Transparent
                    ) {

                        Column(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            cardTint,
                                            cardTint.copy(alpha = 0.88f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .padding(20.dp),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // PORTÉE
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(145.dp)
                                    .background(
                                        Color(0xFFF1ECE4),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            ) {

                                Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {

                                    val startX = 80f
                                    val endX = size.width - 50f

                                    val topLineY = 82f
                                    val spacing = 22f

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
                                        185f,
                                        Paint().apply {
                                            textSize = 115f
                                            color = android.graphics.Color.BLACK
                                        }
                                    )

                                    val noteX = size.width / 2f

                                    val y = getNoteY(
                                        currentNote.position.offset
                                    )

                                    // LIGNE SUPPLÉMENTAIRE
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
                                            y - 14f
                                        ),
                                        size = Size(
                                            36f,
                                            24f
                                        )
                                    )

                                    val shouldStemGoDown =
                                        currentNote.position.offset >= 4

                                    if (shouldStemGoDown) {

                                        // HAMPE BAS
                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(
                                                noteX + 2f,
                                                y
                                            ),
                                            end = Offset(
                                                noteX + 2f,
                                                y + 85f
                                            ),
                                            strokeWidth = 3f
                                        )

                                    } else {

                                        // HAMPE HAUT
                                        drawLine(
                                            color = Color.Black,
                                            start = Offset(
                                                noteX + 34f,
                                                y
                                            ),
                                            end = Offset(
                                                noteX + 34f,
                                                y - 85f
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

                            Spacer(modifier = Modifier.height(20.dp))

                            val possibleAnswers = listOf(
                                "DO",
                                "RÉ",
                                "MI",
                                "FA",
                                "SOL",
                                "LA",
                                "SI"
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                possibleAnswers.chunked(2).forEach { rowAnswers ->

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {

                                        rowAnswers.forEach { answer ->

                                            val isCorrect =
                                                answer == currentNote.label

                                            Button(
                                                onClick = {

                                                    if (isCorrect) {

                                                        score++
                                                        combo++

                                                        answerState = true

                                                    } else {

                                                        combo = 0

                                                        answerState = false
                                                    }
                                                },

                                                modifier = Modifier
                                                    .width(150.dp)
                                                    .height(56.dp),

                                                shape = RoundedCornerShape(22.dp),

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
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String
) {

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent
    ) {

        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.94f),
                            Color(0xFFF8F5F0)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 13.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF353535)
            )
        }
    }
}