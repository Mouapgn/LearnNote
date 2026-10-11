package com.example.learnnote

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.example.learnnote.Metronome.MetronomeActivity
import com.example.learnnote.cours.CoursActivity
import com.example.learnnote.jeux.JeuxActivity
import com.example.learnnote.partitions.PartitionsActivity
import com.example.learnnote.ui.MenuButton

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                MainMenu(
                    onCoursClick = {
                        startActivity(Intent(this, CoursActivity::class.java))
                    },

                    onJeuxClick = {
                        startActivity(Intent(this, JeuxActivity::class.java))
                    },

                    onMetronomeClick = {
                        startActivity(Intent(this, MetronomeActivity::class.java))
                    },

                    onPartitionsClick = {
                        startActivity(Intent(this, PartitionsActivity::class.java))
                    }
                )
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MainMenu(
    onCoursClick: () -> Unit,
    onJeuxClick: () -> Unit,
    onMetronomeClick: () -> Unit,
    onPartitionsClick: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
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
            animation = tween(4500),
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
                        Color(0xFFF2EEE7),
                        Color(0xFFEDE7DE)
                    )
                )
            )
    ) {

        // Glow haut gauche
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-70).dp, y = (-20).dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF8EF),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        // Glow bas droite
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 180.dp, y = 620.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC9D8CC).copy(alpha = 0.45f),
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

            Spacer(modifier = Modifier.height(85.dp))

            // HEADER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(floatingScale)
            ) {

                Text(
                    text = "LearnNote",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3A3A3A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Parce que pourquoi pas ?",
                    fontSize = 16.sp,
                    color = Color(0xFF7A7A7A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC9D8CC),
                                    Color(0xFFDCCDF4)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

            PremiumMenuCard {
                MenuButton(
                    title = "Cours",
                    color = Color(0xFF8DB7A8),
                    onClick = onCoursClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PremiumMenuCard {
                MenuButton(
                    title = "Jeux",
                    color = Color(0xFFA5BCE8),
                    onClick = onJeuxClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PremiumMenuCard {
                MenuButton(
                    title = "Métronome",
                    color = Color(0xFFE7B989),
                    onClick = onMetronomeClick
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PremiumMenuCard {
                MenuButton(
                    title = "Partitions",
                    color = Color(0xFFC7B5E8),
                    onClick = onPartitionsClick
                )
            }
        }
    }
}

@Composable
fun PremiumMenuCard(
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