package com.example.learnnote.cours

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnnote.ui.MenuButton

class CoursActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                CoursScreen(
                    onNoteClick = {
                        startActivity(Intent(this, NoteActivity::class.java))
                    },
                    onRythmeClick = {
                        startActivity(Intent(this, RhythmActivity::class.java))
                    },
                    onRythmeSilenceClick = {
                        startActivity(Intent(this, RhythmSilenceActivity::class.java))
                    },
                    onViolonClick = {
                        startActivity(Intent(this, ViolinNotesActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun CoursScreen(
    onNoteClick: () -> Unit,
    onRythmeClick: () -> Unit,
    onRythmeSilenceClick: () -> Unit,
    onViolonClick: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val floatingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
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

        // Glow crème
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-60).dp, y = (-30).dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF8ED),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        // Glow sauge
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = 180.dp, y = 520.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC9D8CC).copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(floatingScale)
            ) {

                Text(
                    text = "Cours de Violon",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B3B3B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC9D8CC),
                                    Color(0xFFD8C7F1)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

            PremiumCard {
                MenuButton(
                    title = "Notes",
                    color = Color(0xFF8DB7A8),
                    onClick = onNoteClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PremiumCard {
                MenuButton(
                    title = "Rythme Global",
                    color = Color(0xFFA5BCE8),
                    onClick = onRythmeClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PremiumCard {
                MenuButton(
                    title = "Rythme Silence",
                    color = Color(0xFFC7B5E8),
                    onClick = onRythmeSilenceClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PremiumCard {
                MenuButton(
                    title = "Notes sur le Violon",
                    color = Color(0xFFE7B989),
                    onClick = onViolonClick
                )
            }
        }
    }
}

@Composable
fun PremiumCard(
    content: @Composable BoxScope.() -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = RoundedCornerShape(30.dp),
        color = Color.Transparent
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.92f),
                            Color(0xFFF8F5F0)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}