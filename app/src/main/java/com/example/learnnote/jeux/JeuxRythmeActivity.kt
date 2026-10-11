package com.example.learnnote.jeux

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnnote.R
import kotlinx.coroutines.delay

class JeuxRythmeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                JeuxRythmeScreen()
            }
        }
    }
}

// ======================================================
// SOUND PLAYER
// ======================================================

class RhythmPlayer(
    context: Context
) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setAudioAttributes(audioAttributes)
        .setMaxStreams(6)
        .build()

    private val beatSound = soundPool.load(
        context,
        R.raw.click,
        1
    )

    private val successSound = soundPool.load(
        context,
        R.raw.click,
        1
    )

    fun playBeat(
        accent: Boolean = false
    ) {

        soundPool.play(
            beatSound,
            if (accent) 1f else 0.7f,
            if (accent) 1f else 0.7f,
            1,
            0,
            if (accent) 0.92f else 1.08f
        )
    }

    fun playSuccess() {

        soundPool.play(
            successSound,
            0.55f,
            0.55f,
            1,
            0,
            1.25f
        )
    }

    fun release() {

        soundPool.release()
    }
}

// ======================================================
// SCREEN
// ======================================================

@Composable
fun JeuxRythmeScreen() {

    val context = LocalContext.current

    val rhythmPlayer = remember {
        RhythmPlayer(context)
    }

    DisposableEffect(Unit) {

        onDispose {

            rhythmPlayer.release()
        }
    }

    var bpm by remember {
        mutableIntStateOf(80)
    }

    var score by remember {
        mutableIntStateOf(0)
    }

    var combo by remember {
        mutableIntStateOf(0)
    }

    var beatActive by remember {
        mutableStateOf(false)
    }

    var currentBeat by remember {
        mutableIntStateOf(0)
    }

    var feedbackText by remember {
        mutableStateOf("")
    }

    var feedbackColor by remember {
        mutableStateOf(Color.Transparent)
    }

    val beatDuration = (60000 / bpm).toLong()

    // ======================================================
    // RHYTHM LOOP
    // ======================================================

    LaunchedEffect(bpm) {

        while (true) {

            val accentBeat = currentBeat == 0

            beatActive = true

            rhythmPlayer.playBeat(accentBeat)

            currentBeat = (currentBeat + 1) % 4

            delay(140)

            beatActive = false

            delay(beatDuration - 140)
        }
    }

    // ======================================================
    // ANIMATIONS
    // ======================================================

    val pulseScale by animateFloatAsState(
        targetValue =
            if (beatActive) 1.08f else 1f,

        animationSpec = tween(120),
        label = ""
    )

    val pulseColor by animateColorAsState(
        targetValue =
            if (beatActive)
                Color(0xFFE7D2B6)
            else
                Color(0xFFF4F0EA),

        animationSpec = tween(120),
        label = ""
    )

    // ======================================================
    // UI
    // ======================================================

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

        // GLOW HAUT
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-70).dp, y = (-20).dp)
                .alpha(0.16f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent
                        )
                    )
                )
        )

        // GLOW BAS
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 180.dp, y = 620.dp)
                .alpha(0.18f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC9D8CC),
                            Color.Transparent
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Spacer(modifier = Modifier.height(26.dp))

                // ======================================================
                // HEADER
                // ======================================================

                Text(
                    text = "Jeu de Rythme",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353535)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ressens la pulsation",
                    fontSize = 16.sp,
                    color = Color(0xFF777777)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE7B989),
                                    Color(0xFFD8C7F1)
                                )
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ======================================================
                // HUD
                // ======================================================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    RhythmInfoCard(
                        title = "Score",
                        value = "$score"
                    )

                    RhythmInfoCard(
                        title = "Combo",
                        value = "x$combo"
                    )

                    RhythmInfoCard(
                        title = "BPM",
                        value = "$bpm"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ======================================================
                // BPM CARD
                // ======================================================

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
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .padding(22.dp)
                    ) {

                        Text(
                            text = "Tempo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF353535)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = bpm.toFloat(),
                            onValueChange = {
                                bpm = it.toInt()
                            },
                            valueRange = 50f..160f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ======================================================
                // RHYTHM CARD
                // ======================================================

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),

                    shape = RoundedCornerShape(34.dp),
                    color = Color.Transparent
                ) {

                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        pulseColor,
                                        pulseColor.copy(alpha = 0.95f)
                                    )
                                ),
                                shape = RoundedCornerShape(34.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(34.dp)
                            )
                            .padding(24.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // PULSE
                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(170.dp)
                                    .scale(pulseScale)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFE7B989),
                                                Color(0xFFFFF5EB)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            Text(
                                text = "${currentBeat + 1}",
                                fontSize = 46.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5A4A3A)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // TAP BUTTON
                        Button(
                            onClick = {

                                if (beatActive) {

                                    score++
                                    combo++

                                    feedbackText = "Parfait"
                                    feedbackColor =
                                        Color(0xFF8DB7A8)

                                    rhythmPlayer.playSuccess()

                                } else {

                                    combo = 0

                                    feedbackText = "Raté"
                                    feedbackColor =
                                        Color(0xFFD96C6C)
                                }
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp),

                            shape = RoundedCornerShape(24.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            )
                        ) {

                            Text(
                                text = "TAPER",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF444444)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // FEEDBACK
                        Text(
                            text = feedbackText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = feedbackColor
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // BEAT INDICATORS
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            repeat(4) { index ->

                                val active =
                                    currentBeat == index

                                Box(
                                    modifier = Modifier
                                        .size(
                                            if (active) 18.dp
                                            else 13.dp
                                        )
                                        .background(
                                            color =
                                                if (active)
                                                    Color(0xFFD8C7F1)
                                                else
                                                    Color(0xFFD6D1CB),

                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(42.dp))
            }
        }
    }
}

@Composable
fun RhythmInfoCard(
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