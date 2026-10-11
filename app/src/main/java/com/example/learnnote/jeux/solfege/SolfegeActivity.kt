package com.example.learnnote.jeux.solfege

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SolfegeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                QuizSolfegeScreen(

                    onZenClick = {
                        startActivity(
                            Intent(
                                this,
                                SolfegeZenActivity::class.java
                            )
                        )
                    },

                    onLectureExpressClick = {
                        startActivity(
                            Intent(
                                this,
                                LectureExpressActivity::class.java
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun QuizSolfegeScreen(
    onZenClick: () -> Unit,
    onLectureExpressClick: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse
        ),
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

        // Glow haut
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-70).dp, y = (-20).dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        // Glow bas
        Box(
            modifier = Modifier
                .size(230.dp)
                .offset(x = 170.dp, y = 620.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD8C7F1).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Spacer(modifier = Modifier.height(78.dp))

                HeaderSection()

                Spacer(modifier = Modifier.height(50.dp))

                QuizModeCard(
                    title = "Mode Zen",
                    subtitle = "Apprendre sans pression",
                    color = Color(0xFF8DB7A8),
                    onClick = onZenClick
                )

                Spacer(modifier = Modifier.height(22.dp))

                QuizModeCard(
                    title = "Lecture Express",
                    subtitle = "Reconnais les notes rapidement",
                    color = Color(0xFFA5BCE8),
                    onClick = onLectureExpressClick
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun QuizModeCard(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = ""
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale)
            .clickable {

                pressed = true
                onClick()
                pressed = false
            },

        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.94f),
                            Color(0xFFF8F5F0)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 24.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(8.dp)
                        .background(
                            color,
                            shape = RoundedCornerShape(100.dp)
                        )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353535)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    fontSize = 15.sp,
                    color = Color(0xFF777777)
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val floatingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(floatingScale)
    ) {

        Text(
            text = "Quiz Solfège",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A3A3A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Choisis ton mode d’apprentissage",
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
                    shape = RoundedCornerShape(50)
                )
        )
    }
}