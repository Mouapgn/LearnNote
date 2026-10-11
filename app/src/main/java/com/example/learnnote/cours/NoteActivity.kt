package com.example.learnnote.cours

import android.media.MediaPlayer
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.learnnote.R

import kotlinx.coroutines.delay
class NoteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {

                    ViolinLearningPage()
                }
            }
        }
    }
}

// =========================================
// POSITIONS MUSICALES
// =========================================

enum class StaffPosition(val offset: Int) {

    SOL3(-5),
    LA3(-4),
    SI3(-3),
    DO4(-2),
    RE4(-1),

    MI4(0),
    FA4(1),
    SOL4(2),
    LA4(3),
    SI4(4),

    DO5(5),
    RE5(6),
    MI5(7),
    FA5(8),

    SOL5(9),
    LA5(10),
    SI5(11)
}

// =========================================
// NOTE
// =========================================

data class MusicNote(
    val label: String,
    val finger: String,
    val position: StaffPosition,
    val x: Float,
    val soundRes: Int
)

// =========================================
// PAGE PRINCIPALE
// =========================================

@Composable
fun ViolinLearningPage() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 18.dp)
    ) {

        // =====================================
        // CORDE SOL
        // =====================================

        StaffBlock(
            title = "CORDE SOL",
            notes = listOf(
                MusicNote("SOL", "0", StaffPosition.SOL3, 250f, R.raw.sol3),
                MusicNote("la", "1", StaffPosition.LA3, 420f, R.raw.la3),
                MusicNote("si", "2", StaffPosition.SI3, 590f, R.raw.si3),
                MusicNote("do", "3", StaffPosition.DO4, 760f, R.raw.do4),
            )
        )

        // =====================================
        // CORDE RÉ
        // =====================================

        StaffBlock(
            title = "CORDE RÉ",
            notes = listOf(
                MusicNote("RÉ", "0", StaffPosition.RE4, 250f, R.raw.re4),
                MusicNote("mi", "1", StaffPosition.MI4, 420f, R.raw.mi4),
                MusicNote("fa", "2", StaffPosition.FA4, 590f, R.raw.fa4),
                MusicNote("sol", "3", StaffPosition.SOL4, 760f, R.raw.sol4),
            )
        )

        // =====================================
        // CORDE LA
        // =====================================

        StaffBlock(
            title = "CORDE LA",
            notes = listOf(
                MusicNote("LA", "0", StaffPosition.LA4, 250f, R.raw.la4),
                MusicNote("si", "1", StaffPosition.SI4, 420f, R.raw.si4),
                MusicNote("do", "2", StaffPosition.DO5, 590f, R.raw.do5),
                MusicNote("ré", "3", StaffPosition.RE5, 760f, R.raw.re5),
            )
        )

        // =====================================
        // CORDE MI
        // =====================================

        StaffBlock(
            title = "CORDE MI",
            notes = listOf(
                MusicNote("MI", "0", StaffPosition.MI5, 250f, R.raw.mi5),
                MusicNote("fa", "1", StaffPosition.FA5, 420f, R.raw.fa5),
                MusicNote("sol", "2", StaffPosition.SOL5, 590f, R.raw.sol5),
                MusicNote("la", "3", StaffPosition.LA5, 760f, R.raw.la5),
                MusicNote("si", "4", StaffPosition.SI5, 930f, R.raw.si5),
            )
        )
    }
}

// =========================================
// PORTÉE
// =========================================

@Composable
fun StaffBlock(
    title: String,
    notes: List<MusicNote>
) {

    val context = LocalContext.current

    var selectedNote by remember {
        mutableStateOf<String?>(null)
    }

    var triggerReset by remember {
        mutableIntStateOf(0)
    }

    Box {

        LaunchedEffect(triggerReset) {

            if (selectedNote != null) {

                delay(2000)

                selectedNote = null
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                )
                .pointerInput(Unit) {

                    detectTapGestures { tapOffset ->

                        val topLineY = 120f
                        val spacing = 22f

                        fun getNoteY(position: Int): Float {

                            val bottomLineY = topLineY + spacing * 4

                            return bottomLineY - (position * (spacing / 2f))
                        }

                        notes.forEach { note ->

                            val y = getNoteY(note.position.offset)

                            val noteLeft = note.x - 35f
                            val noteRight = note.x + 70f

                            val noteTop = y - 55f
                            val noteBottom = y + 55f

                            if (
                                tapOffset.x in noteLeft..noteRight &&
                                tapOffset.y in noteTop..noteBottom
                            ) {

                                selectedNote = note.label
                                triggerReset++

                                val mediaPlayer = MediaPlayer.create(
                                    context,
                                    note.soundRes
                                )

                                mediaPlayer.start()

                                mediaPlayer.setOnCompletionListener {
                                    it.release()
                                }
                            }
                        }
                    }
                }
        ) {

            // =========================================
            // CONFIG
            // =========================================

            val startX = 30f
            val endX = size.width - 20f

            val topLineY = 120f
            val spacing = 22f

            val noteWidth = 28f
            val noteHeight = 18f

            // =========================================
            // POSITION Y
            // =========================================

            fun getNoteY(position: Int): Float {

                val bottomLineY = topLineY + spacing * 4

                return bottomLineY - (position * (spacing / 2f))
            }

            // =========================================
            // TITRE
            // =========================================

            drawContext.canvas.nativeCanvas.drawText(
                title,
                60f,
                40f,
                Paint().apply {
                    textSize = 30f
                    isFakeBoldText = true
                    color = android.graphics.Color.BLACK
                }
            )

            // =========================================
            // PORTÉE
            // =========================================

            for (i in 0..4) {

                val y = topLineY + i * spacing

                drawLine(
                    color = Color.Black,
                    start = Offset(startX, y),
                    end = Offset(endX, y),
                    strokeWidth = 2f
                )
            }

            // =========================================
            // CLÉ DE SOL
            // =========================================

            drawContext.canvas.nativeCanvas.drawText(
                "𝄞",
                10f,
                210f,
                Paint().apply {
                    textSize = 120f
                    color = android.graphics.Color.BLACK
                }
            )

            // =========================================
            // SIGNATURE 4/4
            // =========================================

            drawContext.canvas.nativeCanvas.drawText(
                "4",
                90f,
                140f,
                Paint().apply {
                    textSize = 34f
                    isFakeBoldText = true
                }
            )

            drawContext.canvas.nativeCanvas.drawText(
                "4",
                90f,
                180f,
                Paint().apply {
                    textSize = 34f
                    isFakeBoldText = true
                }
            )

            // =========================================
            // MESURES
            // =========================================

            val bars = listOf(
                390f,
                560f,
                730f,
                900f
            )

            bars.forEach { x ->

                drawLine(
                    color = Color.Black,
                    start = Offset(x, topLineY),
                    end = Offset(x, topLineY + spacing * 4),
                    strokeWidth = 2f
                )
            }

            // =========================================
            // DOUBLE BARRE FINALE
            // =========================================

            drawLine(
                color = Color.Black,
                start = Offset(endX - 10f, topLineY),
                end = Offset(endX - 10f, topLineY + spacing * 4),
                strokeWidth = 2f
            )

            drawLine(
                color = Color.Black,
                start = Offset(endX, topLineY),
                end = Offset(endX, topLineY + spacing * 4),
                strokeWidth = 5f
            )

            // =========================================
            // NOTES
            // =========================================

            notes.forEach { note ->

                val y = getNoteY(note.position.offset)

                // =====================================
                // LIGNES SUPPLÉMENTAIRES BASSES
                // =====================================

                if (note.position.offset <= -2) {

                    var current = -2

                    while (current >= note.position.offset) {

                        val ledgerY = getNoteY(current)

                        drawLine(
                            color = Color.Black,
                            start = Offset(note.x - 10f, ledgerY),
                            end = Offset(note.x + 40f, ledgerY),
                            strokeWidth = 2f
                        )

                        current -= 2
                    }
                }

                // =====================================
                // LIGNES SUPPLÉMENTAIRES HAUTES
                // =====================================

                if (note.position.offset >= 10) {

                    var current = 10

                    while (current <= note.position.offset) {

                        val ledgerY = getNoteY(current)

                        drawLine(
                            color = Color.Black,
                            start = Offset(note.x - 10f, ledgerY),
                            end = Offset(note.x + 40f, ledgerY),
                            strokeWidth = 2f
                        )

                        current += 2
                    }
                }

                // =====================================
                // DOIGTÉS
                // =====================================

                drawContext.canvas.nativeCanvas.drawText(
                    note.finger,
                    note.x + 5f,
                    95f,
                    Paint().apply {
                        textSize = 28f
                        color = android.graphics.Color.BLACK
                    }
                )

                // =====================================
                // COULEUR NOTE SÉLECTIONNÉE
                // =====================================

                val noteColor =
                    if (selectedNote == note.label) {
                        Color.Red
                    } else {
                        Color.Black
                    }

                // =====================================
                // TÊTE DE NOTE
                // =====================================

                drawOval(
                    color = noteColor,
                    topLeft = Offset(
                        note.x,
                        y - noteHeight / 2f
                    ),
                    size = Size(
                        noteWidth,
                        noteHeight
                    )
                )

                // =====================================
                // HAMPE
                // =====================================

                val stemX = note.x + noteWidth - 2f

                if (note.position.offset >= 4) {

                    drawLine(
                        color = noteColor,
                        start = Offset(stemX, y),
                        end = Offset(stemX, y + 48f),
                        strokeWidth = 2f
                    )

                } else {

                    drawLine(
                        color = noteColor,
                        start = Offset(stemX, y),
                        end = Offset(stemX, y - 48f),
                        strokeWidth = 2f
                    )
                }

                // =====================================
                // NOM NOTE
                // =====================================

                val labelY = if (title == "CORDE SOL") {
                    305f
                } else {
                    255f
                }

                drawContext.canvas.nativeCanvas.drawText(
                    note.label,
                    note.x - 10f,
                    labelY,
                    Paint().apply {
                        textSize = 32f
                        color = android.graphics.Color.rgb(40, 90, 35)
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
}