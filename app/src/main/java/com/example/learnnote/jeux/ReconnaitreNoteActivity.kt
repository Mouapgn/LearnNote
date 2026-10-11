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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnnote.R
import kotlinx.coroutines.delay

class ReconnaitreNoteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                ReconnaitreNoteScreen()
            }
        }
    }
}

// ======================================================
// MODES
// ======================================================

enum class NoteMode(
    val displayName: String,
    val notes: List<String>
) {

    FACILE(
        "Facile",
        listOf(
            "DO",
            "RE",
            "MI"
        )
    ),

    MOYEN(
        "Moyen",
        listOf(
            "DO",
            "RE",
            "MI",
            "FA",
            "SOL"
        )
    ),

    VIOLON(
        "Violon",
        listOf(
            "DO",
            "RE",
            "MI",
            "FA",
            "SOL",
            "LA",
            "SI"
        )
    )
}

// ======================================================
// PLAYER
// ======================================================

class NotesPlayer(
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

    private val noteSounds = mapOf(

        "DO" to soundPool.load(context, R.raw.do4, 1),
        "RE" to soundPool.load(context, R.raw.re4, 1),
        "MI" to soundPool.load(context, R.raw.mi4, 1),
        "FA" to soundPool.load(context, R.raw.fa4, 1),
        "SOL" to soundPool.load(context, R.raw.sol4, 1),
        "LA" to soundPool.load(context, R.raw.la4, 1),
        "SI" to soundPool.load(context, R.raw.si4, 1)
    )

    fun playNote(note: String) {

        noteSounds[note]?.let {

            soundPool.play(
                it,
                1f,
                1f,
                1,
                0,
                1f
            )
        }
    }

    fun release() {

        soundPool.release()
    }
}

// ======================================================
// SCREEN
// ======================================================

@Composable
fun ReconnaitreNoteScreen() {

    val context = LocalContext.current

    val notesPlayer = remember {

        NotesPlayer(context)
    }

    DisposableEffect(Unit) {

        onDispose {

            notesPlayer.release()
        }
    }

    var selectedMode by remember {

        mutableStateOf(NoteMode.FACILE)
    }

    var currentNote by remember {

        mutableStateOf("DO")
    }

    var feedback by remember {

        mutableStateOf("")
    }

    var feedbackColor by remember {

        mutableStateOf(Color(0xFFE7B989))
    }

    var revealNote by remember {

        mutableStateOf<String?>(null)
    }

    var cardTint by remember {

        mutableStateOf(Color(0xFFF2F0EB))
    }

    fun newNote(
        playSound: Boolean = false
    ) {

        currentNote =
            selectedMode.notes.random()

        if (playSound) {

            notesPlayer.playNote(currentNote)
        }
    }

    LaunchedEffect(selectedMode) {

        delay(250)

        newNote(false)
    }

    LaunchedEffect(feedback) {

        if (feedback.isNotEmpty()) {

            delay(700)

            feedback = ""
        }
    }

    LaunchedEffect(revealNote) {

        if (revealNote != null) {

            delay(1000)

            revealNote = null

            cardTint = Color(0xFFF2F0EB)

            newNote(false)
        }
    }

    val animatedCardColor by animateColorAsState(
        targetValue = cardTint,
        animationSpec = tween(250),
        label = ""
    )

    val infiniteTransition =
        rememberInfiniteTransition(label = "")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.22f,
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

        // Glow
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-60).dp, y = (-30).dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
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

            Spacer(modifier = Modifier.height(42.dp))

            // ======================================================
            // HEADER
            // ======================================================

            Text(
                text = "Reconnaissance",
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF353535)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8DB7A8),
                                Color(0xFFD8C7F1)
                            )
                        ),
                        RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ======================================================
            // MODES
            // ======================================================

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                NoteMode.entries.forEach { mode ->

                    val selected =
                        selectedMode == mode

                    Surface(
                        modifier = Modifier
                            .height(42.dp)
                            .clickable {

                                selectedMode = mode
                            },

                        shape = RoundedCornerShape(20.dp),

                        color =
                            if (selected)
                                Color(0xFFD8C7F1)
                            else
                                Color.White.copy(alpha = 0.75f)
                    ) {

                        Box(
                            modifier = Modifier.padding(
                                horizontal = 18.dp
                            ),

                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = mode.displayName,
                                fontWeight = FontWeight.Bold,
                                color =
                                    if (selected)
                                        Color.White
                                    else
                                        Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ======================================================
            // MAIN CARD
            // ======================================================

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                shape = RoundedCornerShape(30.dp),

                color = animatedCardColor,

                shadowElevation = 4.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 22.dp,
                            vertical = 18.dp
                        ),

                    horizontalAlignment = Alignment.CenterHorizontally,

                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Écoute attentivement",
                        fontSize = 17.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // ======================================================
                    // PLAY BUTTON
                    // ======================================================

                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ) {

                                notesPlayer.playNote(
                                    currentNote
                                )
                            },

                        shape = CircleShape,

                        color = Color(0xFFFFF8F0),

                        shadowElevation = 3.dp
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "▶",
                                fontSize = 58.sp,
                                color = Color(0xFFE7B989),
                                modifier = Modifier.offset(
                                    x = 6.dp,
                                    y = (-3).dp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ======================================================
                    // FIXED FEEDBACK SPACE
                    // ======================================================

                    Box(
                        modifier = Modifier.height(42.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        when {

                            feedback.isNotEmpty() -> {

                                Text(
                                    text = feedback,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = feedbackColor
                                )
                            }

                            revealNote != null -> {

                                Text(
                                    text = revealNote!!,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE57373)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ======================================================
            // NOTE BUTTONS
            // ======================================================

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                val rows = listOf(
                    listOf("DO", "RE", "MI"),
                    listOf("FA", "SOL", "LA"),
                    listOf("SI")
                )

                rows.forEach { row ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp,
                                Alignment.CenterHorizontally
                            )
                    ) {

                        row.forEach { note ->

                            var pressed by remember {
                                mutableStateOf(false)
                            }

                            val scale by animateFloatAsState(
                                targetValue =
                                    if (pressed) 0.96f
                                    else 1f,

                                animationSpec = tween(90),
                                label = ""
                            )

                            Surface(
                                modifier = Modifier
                                    .size(
                                        width = 96.dp,
                                        height = 60.dp
                                    )
                                    .graphicsLayer {

                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        }
                                    ) {

                                        pressed = true

                                        if (note == currentNote) {

                                            feedback = "Bien !"

                                            feedbackColor =
                                                Color(0xFFE7B989)

                                            revealNote = null

                                            cardTint =
                                                Color(0xFFEAF7EF)

                                            newNote(false)

                                        } else {

                                            revealNote =
                                                currentNote

                                            cardTint =
                                                Color(0xFFFFECEC)
                                        }

                                        pressed = false
                                    },

                                shape = RoundedCornerShape(24.dp),

                                color = Color.Transparent,

                                shadowElevation = 4.dp
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.96f),
                                                    Color(0xFFF7F2EC)
                                                )
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        ),

                                    contentAlignment = Alignment.Center
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(1.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.15f),
                                                RoundedCornerShape(24.dp)
                                            )
                                    )

                                    Text(
                                        text = note,

                                        fontSize = 20.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = Color(0xFF5A5A5A),

                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}