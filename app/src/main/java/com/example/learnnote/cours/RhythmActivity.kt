package com.example.learnnote.cours

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnnote.R
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class RhythmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                RhythmScreen()
            }
        }
    }
}

// =========================================
// DATA
// =========================================

data class RhythmNote(
    val symbol: String,
    val title: String,
    val duration: String,
    val description: String,
    val color: Color,
    val clicks: Int,
    val speedMultiplier: Float,
    val notePositions: List<Float>
)

// =========================================
// SOUND PLAYER
// =========================================

class RhythmPlayer(
    context: Context
) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setAudioAttributes(audioAttributes)
        .setMaxStreams(8)
        .build()

    private val clickSound = soundPool.load(
        context,
        R.raw.click,
        1
    )

    fun playClick() {

        soundPool.play(
            clickSound,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    fun release() {

        soundPool.release()
    }
}

// =========================================
// SCREEN
// =========================================

@Composable
fun RhythmScreen() {

    val context = LocalContext.current

    val rhythmPlayer = remember {

        RhythmPlayer(context)
    }

    DisposableEffect(Unit) {

        onDispose {

            rhythmPlayer.release()
        }
    }

    var selectedIndex by remember {
        mutableIntStateOf(-1)
    }

    var displayedRhythm by remember {
        mutableStateOf("")
    }

    var bpm by remember {
        mutableFloatStateOf(80f)
    }

    var pulseBeat by remember {
        mutableStateOf(false)
    }

    var progress by remember {
        mutableFloatStateOf(0f)
    }

    val pulseScale by animateFloatAsState(
        targetValue = if (pulseBeat) 1.18f else 1f,

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),

        label = ""
    )

    // =====================================
    // RESET LABEL
    // =====================================

    LaunchedEffect(displayedRhythm) {

        if (displayedRhythm.isNotEmpty()) {

            delay(1800)

            displayedRhythm = ""
        }
    }

    // =====================================
    // RHYTHMS
    // =====================================

    val rhythms = listOf(

        RhythmNote(
            symbol = "𝅝",
            title = "Ronde",
            duration = "4 temps",
            description = "La note la plus longue.",
            color = Color(0xFF8E24AA),
            clicks = 1,
            speedMultiplier = 4f,
            notePositions = listOf(0.08f)
        ),

        RhythmNote(
            symbol = "𝅗𝅥",
            title = "Blanche",
            duration = "2 temps",
            description = "Vaut deux temps.",
            color = Color(0xFF3949AB),
            clicks = 2,
            speedMultiplier = 2f,
            notePositions = listOf(
                0.25f,
                0.75f
            )
        ),

        RhythmNote(
            symbol = "♩",
            title = "Noire",
            duration = "1 temps",
            description = "Le rythme de base.",
            color = Color(0xFF1E88E5),
            clicks = 4,
            speedMultiplier = 1f,
            notePositions = listOf(
                0.12f,
                0.37f,
                0.62f,
                0.87f
            )
        ),

        RhythmNote(
            symbol = "♪",
            title = "Croche",
            duration = "1/2 temps",
            description = "Deux croches = une noire.",
            color = Color(0xFF00897B),
            clicks = 8,
            speedMultiplier = 0.5f,
            notePositions = listOf(
                0.06f,
                0.18f,
                0.31f,
                0.43f,
                0.56f,
                0.68f,
                0.81f,
                0.93f
            )
        ),

        RhythmNote(
            symbol = "♬",
            title = "Double croche",
            duration = "1/4 temps",
            description = "Très rapide.",
            color = Color(0xFFF4511E),
            clicks = 16,
            speedMultiplier = 0.25f,
            notePositions = listOf(
                0.03f, 0.09f,
                0.15f, 0.21f,
                0.27f, 0.33f,
                0.39f, 0.45f,
                0.51f, 0.57f,
                0.63f, 0.69f,
                0.75f, 0.81f,
                0.87f, 0.93f
            )
        )
    )

    // =====================================
    // UI
    // =====================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8F2),
                        Color.White
                    )
                )
            )
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),

            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Rythme",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Découvre les figures rythmiques",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(28.dp))

                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {

                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {

                        Text(
                            text = "Tempo",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${bpm.roundToInt()} BPM",
                            fontSize = 18.sp,
                            color = Color(0xFF1E88E5),
                            fontWeight = FontWeight.SemiBold
                        )

                        Slider(
                            value = bpm,
                            onValueChange = {
                                bpm = it
                            },
                            valueRange = 40f..200f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(pulseScale)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1E88E5),
                                        Color(0x551E88E5)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // =====================================
            // CARDS
            // =====================================

            itemsIndexed(rhythms) { index, rhythm ->

                RhythmCard(
                    rhythm = rhythm,
                    isSelected = selectedIndex == index,
                    progress = if (selectedIndex == index) progress else 0f,

                    onClick = {

                        selectedIndex = index

                        displayedRhythm =
                            "${rhythm.title} • ${rhythm.duration}"

                        val beatMs = (60000 / bpm).toLong()

                        CoroutineScope(Dispatchers.Main).launch {

                            progress = 0f

                            val totalDuration =
                                beatMs *
                                        rhythm.speedMultiplier *
                                        rhythm.clicks

                            // =====================================
                            // PROGRESS BAR
                            // =====================================

                            val progressJob = launch {

                                val startTime =
                                    System.currentTimeMillis()

                                while (true) {

                                    val elapsed =
                                        System.currentTimeMillis() -
                                                startTime

                                    progress =
                                        (elapsed.toFloat() / totalDuration.toFloat())
                                            .coerceIn(0f, 1f)

                                    if (progress >= 1f) break

                                    delay(16)
                                }
                            }

                            // =====================================
                            // SYNCHRONIZED NOTES
                            // =====================================

                            val playedNotes =
                                mutableSetOf<Int>()

                            while (progress < 1f) {

                                rhythm.notePositions.forEachIndexed { noteIndex, notePos ->

                                    if (
                                        progress >= notePos &&
                                        !playedNotes.contains(noteIndex)
                                    ) {

                                        playedNotes.add(noteIndex)

                                        pulseBeat = true

                                        rhythmPlayer.playClick()

                                        launch {

                                            delay(80)

                                            pulseBeat = false
                                        }
                                    }
                                }

                                delay(8)
                            }

                            progressJob.join()

                            delay(200)

                            progress = 0f

                            delay(400)

                            selectedIndex = -1
                        }
                    }
                )
            }

            item {

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // =====================================
        // LABEL
        // =====================================

        AnimatedVisibility(
            visible = displayedRhythm.isNotEmpty(),

            enter = fadeIn(),

            exit = fadeOut(),

            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 92.dp)
        ) {

            Surface(
                shape = RoundedCornerShape(30.dp),
                color = Color.Black.copy(alpha = 0.78f),
                shadowElevation = 12.dp
            ) {

                Text(
                    text = displayedRhythm,

                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 12.dp
                    ),

                    color = Color.White,

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// =========================================
// CARD
// =========================================

@Composable
fun RhythmCard(
    rhythm: RhythmNote,
    isSelected: Boolean,
    progress: Float,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),

        label = ""
    )

    val symbolSize by animateFloatAsState(
        targetValue = if (isSelected) 52f else 42f,
        label = ""
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {

                onClick()
            },

        shape = RoundedCornerShape(28.dp),

        shadowElevation = if (isSelected) 18.dp else 8.dp,

        color = Color.White
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            rhythm.color.copy(alpha = 0.16f),
                            Color.White
                        )
                    )
                )
                .padding(22.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(
                        rhythm.color.copy(alpha = 0.18f),
                        RoundedCornerShape(24.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = rhythm.symbol,
                    fontSize = symbolSize.sp,
                    color = rhythm.color
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = rhythm.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = rhythm.duration,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = rhythm.color
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = rhythm.description,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(18.dp))

                MusicalStaff(
                    rhythm = rhythm,
                    progress = progress
                )
            }
        }
    }
}

// =========================================
// MUSICAL STAFF
// =========================================

@Composable
fun MusicalStaff(
    rhythm: RhythmNote,
    progress: Float
) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,

        animationSpec = tween(
            durationMillis = 50,
            easing = LinearEasing
        ),

        label = ""
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Mesure 4/4",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            val staffWidth = maxWidth

            val barPosition =
                staffWidth * animatedProgress

            // =====================================
            // STAFF LINES
            // =====================================

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                repeat(5) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Color.Black.copy(alpha = 0.22f)
                            )
                    )
                }
            }

            // =====================================
            // START BAR
            // =====================================

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(
                        Color.Black.copy(alpha = 0.4f)
                    )
            )

            // =====================================
            // END BAR
            // =====================================

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(
                        Color.Black.copy(alpha = 0.4f)
                    )
            )

            // =====================================
            // NOTES
            // =====================================

            rhythm.notePositions.forEach {

                Box(
                    modifier = Modifier
                        .offset(
                            x = staffWidth * it
                        )
                        .align(Alignment.CenterStart)
                ) {

                    if (rhythm.title == "Double croche") {

                        DoubleCrocheNote(
                            color = rhythm.color
                        )

                    } else {

                        StaffNote(
                            symbol = rhythm.symbol,
                            color = rhythm.color,

                            size = when (rhythm.title) {

                                "Ronde" -> 58.sp
                                "Blanche" -> 52.sp
                                "Noire" -> 48.sp
                                "Croche" -> 46.sp
                                else -> 44.sp
                            }
                        )
                    }
                }
            }

            // =====================================
            // MOVING BAR
            // =====================================

            Box(
                modifier = Modifier
                    .offset(x = barPosition - 3.dp)
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        rhythm.color,
                        RoundedCornerShape(20.dp)
                    )
            )
        }
    }
}

// =========================================
// STAFF NOTE
// =========================================

@Composable
fun StaffNote(
    symbol: String,
    color: Color,
    size: TextUnit
) {

    Text(
        text = symbol,
        fontSize = size,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

// =========================================
// DOUBLE CROCHE
// =========================================

@Composable
fun DoubleCrocheNote(
    color: Color
) {

    Text(
        text = "♬",
        fontSize = 42.sp,
        color = color,
        fontWeight = FontWeight.Bold
    )
}