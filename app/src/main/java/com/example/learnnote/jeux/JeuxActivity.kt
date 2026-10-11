package com.example.learnnote.jeux


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.learnnote.jeux.solfege.SolfegeActivity

class JeuxActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                JeuxScreen(
                    onGameClick = { game ->

                        when (game) {

                            "Quiz Solfège" -> {

                                startActivity(
                                    Intent(
                                        this,
                                        SolfegeActivity::class.java
                                    )
                                )
                            }
                            "Rythme" -> {

                                startActivity(
                                    Intent(
                                        this,
                                        JeuxRythmeActivity::class.java
                                    )
                                )
                            }
                            "Reconnaissance de notes" -> {

                                startActivity(
                                    Intent(
                                        this,
                                        ReconnaitreNoteActivity::class.java
                                    )
                                )
                            }
                            else -> {

                                Toast.makeText(
                                    this,
                                    "Ouverture : $game",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

data class GameItem(
    val title: String,
    val subtitle: String,
    val color: Color
)

@Composable
fun JeuxScreen(
    onGameClick: (String) -> Unit
) {

    val games = listOf(

        GameItem(
            title = "Quiz Solfège",
            subtitle = "Lecture de notes et rapidité",
            color = Color(0xFFA5BCE8)
        ),

        GameItem(
            title = "Rythme",
            subtitle = "Travaille ton tempo et ta précision",
            color = Color(0xFFE7B989)
        ),

        GameItem(
            title = "Reconnaissance de notes",
            subtitle = "Écoute et retrouve la bonne note",
            color = Color(0xFF8DB7A8)
        )
    )

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

        // GLOW HAUT GAUCHE
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

        // GLOW BAS DROITE
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = 180.dp, y = 620.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC9D8CC).copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Spacer(modifier = Modifier.height(72.dp))

                JeuxHeaderSection()

                Spacer(modifier = Modifier.height(42.dp))
            }

            items(games) { game ->

                GameCard(
                    game = game,
                    onClick = {
                        onGameClick(game.title)
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun JeuxHeaderSection() {

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
            text = "Jeux Musicaux",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A3A3A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Apprendre en jouant",
            fontSize = 16.sp,
            color = Color(0xFF777777)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .width(110.dp)
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

@Composable
fun GameCard(
    game: GameItem,
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
            .height(118.dp)
            .scale(scale)
            .clickable {

                pressed = true
                onClick()
                pressed = false
            },

        shape = RoundedCornerShape(30.dp),
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
                    shape = RoundedCornerShape(30.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 22.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(70.dp)
                        .background(
                            game.color,
                            shape = RoundedCornerShape(100.dp)
                        )
                )

                Spacer(modifier = Modifier.width(18.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = game.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF353535)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = game.subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF7A7A7A)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            game.color.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(100.dp)
                        )
                )
            }
        }
    }
}
