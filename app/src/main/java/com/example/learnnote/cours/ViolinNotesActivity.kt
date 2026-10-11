package com.example.learnnote.cours

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnnote.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViolinNotesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                ViolinNotesScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

// =========================================
// NOTE CLIQUABLE
// =========================================

data class ClickableNote(
    val xPercent: Float,
    val yPercent: Float,
    val label: String,
    val soundRes: Int
)

// =========================================
// SCREEN
// =========================================

@Composable
fun ViolinNotesScreen() {

    val context = LocalContext.current
    val density = LocalDensity.current


    // =====================================
    // DEBUG MODE
    // =====================================

    val debugMode = false

    // =====================================
    // NOTE SÉLECTIONNÉE
    // =====================================

    var selectedIndex by remember {
        mutableIntStateOf(-1)
    }

    // =====================================
    // NOM NOTE AFFICHÉ
    // =====================================

    var displayedNote by remember {
        mutableStateOf("")
    }

    // =====================================
    // ANTI MULTITAP
    // =====================================

    var canPlay by remember {
        mutableStateOf(true)
    }

    // =====================================
    // PLAYER UNIQUE
    // =====================================

    var currentPlayer by remember {
        mutableStateOf<MediaPlayer?>(null)
    }

    // =====================================
    // RESET COULEUR
    // =====================================

    LaunchedEffect(selectedIndex) {

        if (selectedIndex != -1) {

            delay(700)

            selectedIndex = -1
        }
    }

    // =====================================
    // RESET NOM NOTE
    // =====================================

    LaunchedEffect(displayedNote) {

        if (displayedNote.isNotEmpty()) {

            delay(1500)

            displayedNote = ""
        }
    }

    // =====================================
    // NOTES RESPONSIVE
    // =====================================

    val notes = listOf(

        // =====================================
        // CORDE SOL
        // =====================================

        ClickableNote(0.337f, 0.425f, "SOL3", R.raw.sol3),
        ClickableNote(0.462f, 0.425f, "LA3", R.raw.la3),
        ClickableNote(0.582f, 0.425f, "SI3", R.raw.si3),
        ClickableNote(0.71f, 0.425f, "DO4", R.raw.do4),
        ClickableNote(0.836f, 0.425f, "RÉ4", R.raw.re4),

        // =====================================
        // CORDE RÉ
        // =====================================

        ClickableNote(0.336f, 0.39f, "RÉ4", R.raw.re4),
        ClickableNote(0.462f, 0.39f, "MI4", R.raw.mi4),
        ClickableNote(0.581f, 0.39f, "FA4", R.raw.fa4),
        ClickableNote(0.71f, 0.39f, "SOL4", R.raw.sol4),
        ClickableNote(0.837f, 0.39f, "LA4", R.raw.la4),

        // =====================================
        // CORDE LA
        // =====================================

        ClickableNote(0.338f, 0.358f, "LA4", R.raw.la4),
        ClickableNote(0.464f, 0.358f, "SI4", R.raw.si4),
        ClickableNote(0.58f, 0.356f, "DO5", R.raw.do5),
        ClickableNote(0.709f, 0.356f, "RÉ5", R.raw.re5),
        ClickableNote(0.836f, 0.355f, "MI5", R.raw.mi5),

        // =====================================
        // CORDE MI
        // =====================================

        ClickableNote(0.337f, 0.3255f, "MI5", R.raw.mi5),
        ClickableNote(0.462f, 0.324f, "FA5", R.raw.fa5),
        ClickableNote(0.581f, 0.322f, "SOL5", R.raw.sol5),
        ClickableNote(0.709f, 0.321f, "LA5", R.raw.la5),
        ClickableNote(0.8338f, 0.3185f, "SI5", R.raw.si5)
    )

    // =====================================
    // LAYOUT RESPONSIVE
    // =====================================

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        val screenWidth = constraints.maxWidth
        val screenHeight = constraints.maxHeight

        // =====================================
        // IMAGE + AMBIANCE
        // =====================================

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.violin_notes),
                contentDescription = "Notes sur le violon",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x11FF9800)
                            )
                        )
                    )
            )
        }

        // =====================================
        // NOM NOTE EN HAUT
        // =====================================

        AnimatedVisibility(
            visible = displayedNote.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 170.dp)
        ) {

            Text(
                text = "♫ $displayedNote",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color(0xFFFF9800)
            )
        }

        // =====================================
        // SURFACE CLIQUABLE
        // =====================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {

                    detectTapGestures { offset ->

                        // =====================================
                        // ANTI MULTITAP
                        // =====================================

                        if (!canPlay) return@detectTapGestures

                        // =====================================
                        // DEBUG COORDONNÉES
                        // =====================================

                        val xPercent =
                            offset.x / screenWidth.toFloat()

                        val yPercent =
                            offset.y / screenHeight.toFloat()

                        if (debugMode) {

                            Toast
                                .makeText(
                                    context,
                                    "x=${"%.2f".format(xPercent)} | y=${"%.2f".format(yPercent)}",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }

                        // =====================================
                        // DÉTECTION NOTES
                        // =====================================

                        notes.forEachIndexed { index, note ->

                            val noteX =
                                screenWidth * note.xPercent

                            val noteY =
                                screenHeight * note.yPercent

                            val radius = 85f

                            val isInside =
                                offset.x in (noteX - radius)..(noteX + radius) &&
                                        offset.y in (noteY - radius)..(noteY + radius)

                            if (isInside) {

                                canPlay = false

                                selectedIndex = index

                                displayedNote = note.label

                                // =============================
                                // STOP ANCIEN SON
                                // =============================

                                try {
                                    currentPlayer?.release()
                                } catch (_: Exception) {
                                }

                                // =============================
                                // NOUVEAU SON
                                // =============================

                                currentPlayer = MediaPlayer.create(
                                    context,
                                    note.soundRes
                                )

                                currentPlayer?.start()

                                currentPlayer?.setOnCompletionListener {
                                    it.release()
                                }

                                // =============================
                                // RESET MULTITAP
                                // =============================

                                CoroutineScope(Dispatchers.Main).launch {

                                    delay(120)

                                    canPlay = true
                                }

                                return@forEachIndexed
                            }
                        }
                    }
                }
        )

        // =====================================
// FEEDBACK VISUEL PREMIUM
// =====================================

        notes.forEachIndexed { index, note ->

            val isSelected = selectedIndex == index

            Box(
                modifier = Modifier
                    .offset {

                        with(density) {

                            IntOffset(
                                ((screenWidth * note.xPercent) - 35.dp.toPx()).toInt(),
                                ((screenHeight * note.yPercent) - 35.dp.toPx()).toInt()
                            )
                        }
                    }
                    .size(70.dp),
                contentAlignment = Alignment.Center
            ) {

                // =====================================
                // GLOW PREMIUM
                // =====================================

                if (isSelected) {

                    // Glow externe
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(
                                Color(0xFFFF9800).copy(alpha = 0.10f)
                            )
                    )

                    // Glow moyen
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Color(0xFFFF9800).copy(alpha = 0.18f)
                            )
                    )

                    // Glow interne
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Color(0xFFFF9800).copy(alpha = 0.28f)
                            )
                    )
                }

                // =====================================
                // CERCLE CENTRAL
                // =====================================

                Box(
                    modifier = Modifier
                        .size(
                            if (isSelected) 34.dp else 0.dp
                        )
                        .clip(CircleShape)
                        .background(
                            Color(0xFFFF9800).copy(alpha = 0.75f)
                        )
                )
            }
        }
    }
}