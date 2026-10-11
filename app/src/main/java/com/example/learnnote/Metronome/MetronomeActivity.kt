package com.example.learnnote.Metronome

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import com.example.learnnote.R

class MetronomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                MetronomeScreen()
            }
        }
    }
}

// ========================================
// SOUND
// ========================================

class MetronomePlayer(
    context: Context
) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setAudioAttributes(audioAttributes)
        .setMaxStreams(4)
        .build()

    private val tickSound = soundPool.load(
        context,
        R.raw.click,
        1
    )

    fun playTick() {

        soundPool.play(
            tickSound,
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

// ========================================
// SCREEN
// ========================================

@Composable
fun MetronomeScreen() {

    val context = LocalContext.current

    val metronomePlayer = remember {

        MetronomePlayer(context)
    }

    DisposableEffect(Unit) {

        onDispose {

            metronomePlayer.release()
        }
    }

    var bpm by remember {

        mutableFloatStateOf(80f)
    }

    var isPlaying by remember {

        mutableStateOf(false)
    }

    var beatIndex by remember {

        mutableIntStateOf(0)
    }

    var beatsPerMeasure by remember {

        mutableIntStateOf(4)
    }

    var pulse by remember {

        mutableStateOf(false)
    }

    val pulseScale by animateFloatAsState(
        targetValue = if (pulse) 1.14f else 1f,

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),

        label = ""
    )

    // ========================================
    // METRONOME LOOP
    // ========================================

    LaunchedEffect(
        isPlaying,
        bpm,
        beatsPerMeasure
    ) {

        if (isPlaying) {

            while (true) {

                pulse = true

                metronomePlayer.playTick()

                beatIndex =
                    (beatIndex + 1) % beatsPerMeasure

                delay(80)

                pulse = false

                delay((60000 / bpm).toLong() - 80)
            }
        }
    }

    // ========================================
    // UI
    // ========================================

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
                .size(240.dp)
                .offset(x = (-70).dp, y = (-20).dp)
                .alpha(0.18f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Glow bas
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 180.dp, y = 620.dp)
                .alpha(0.22f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC9D8CC),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(34.dp))

            // ========================================
            // HEADER
            // ========================================

            Text(
                text = "Métronome",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B3B3B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFC9D8CC),
                                Color(0xFFD8C7F1)
                            )
                        ),
                        RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ========================================
            // BPM CARD
            // ========================================

            Surface(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(26.dp),

                color = Color.White.copy(alpha = 0.84f),

                shadowElevation = 6.dp
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Tempo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B3B3B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${bpm.roundToInt()} BPM",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8DB7A8)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Slider(
                        value = bpm,

                        onValueChange = {

                            bpm = it
                        },

                        valueRange = 40f..220f
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ========================================
            // BEATS PER MEASURE
            // ========================================

            Surface(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(26.dp),

                color = Color.White.copy(alpha = 0.84f),

                shadowElevation = 5.dp
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Pulsations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B3B3B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        listOf(2, 3, 4, 5, 6,7,8).forEach { value ->

                            val selected =
                                beatsPerMeasure == value

                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {

                                        beatsPerMeasure = value
                                        beatIndex = 0
                                    },

                                shape = CircleShape,

                                color =
                                    if (selected)
                                        Color(0xFFD8C7F1)
                                    else
                                        Color(0xFFF2EEE7),

                                shadowElevation =
                                    if (selected) 6.dp
                                    else 1.dp
                            ) {

                                Box(
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = value.toString(),

                                        fontSize = 16.sp,

                                        fontWeight = FontWeight.Bold,

                                        color =
                                            if (selected)
                                                Color.White
                                            else
                                                Color(0xFF6B6B6B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ========================================
            // METRONOME PULSE
            // ========================================

            Box(
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(173.dp)
                        .scale(pulseScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8DB7A8),
                                    Color(0x338DB7A8)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Text(
                    text = "${beatIndex + 1}",

                    fontSize = 46.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ========================================
            // PLAY BUTTON
            // ========================================

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),

                shape = RoundedCornerShape(24.dp),

                shadowElevation = 6.dp,

                color = if (isPlaying)
                    Color(0xFFE7B989)
                else
                    Color(0xFF8DB7A8),

                onClick = {

                    isPlaying = !isPlaying
                }
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text =
                            if (isPlaying)
                                "Arrêter"
                            else
                                "Démarrer",

                        fontSize = 20.sp,

                        fontWeight = FontWeight.Bold,

                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ========================================
            // BEAT INDICATORS
            // ========================================

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                repeat(beatsPerMeasure) { index ->

                    val active =
                        beatIndex == index && isPlaying

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
                                        Color(0xFFCFCFCF),

                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}