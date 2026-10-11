package com.example.learnnote.partitions

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.io.File
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

import kotlinx.coroutines.launch
import android.media.SoundPool
import android.media.AudioAttributes
import kotlinx.coroutines.CoroutineScope
import com.example.learnnote.R
class LecturePartitionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val partitionName =
            intent.getStringExtra("partition") ?: "partition"

        setContent {

            MaterialTheme {

                LecturePartitionScreen(
                    partitionName = partitionName
                )
            }
        }
    }
}
data class PianoSounds(

    val do4: Int,
    val re4: Int,
    val mi4: Int,
    val fa4: Int,
    val sol4: Int,
    val la4: Int,
    val si4: Int,

    val do5: Int,
    val re5: Int,
    val mi5: Int,
    val fa5: Int,
    val sol5: Int,
    val la5: Int,
    val si5: Int,

    val sol3: Int,
    val la3: Int,
    val si3: Int
)

data class MusicNote(

    val type: NoteType,

    val x: Float,

    val step: Int,

    val isRest: Boolean = false
)
// ======================================================
// SCREEN
// ======================================================
fun getNoteY(
    step: Int,
    startY: Float,
    spacing: Float
): Float {

    val bottomLineY =
        startY + spacing * 4

    return bottomLineY - step * (spacing / 2f)
}
@Composable
fun LecturePartitionScreen(
    partitionName: String
) {
    val context = LocalContext.current

    val soundPool = remember {

        val attributes =
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

        SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(attributes)
            .build()
    }

    val metroClick =
        soundPool.load(context, R.raw.click, 1)
    val metroClickHigh =
        soundPool.load(context, R.raw.click_high, 1)

    val sounds = remember {

        PianoSounds(

            do4 = soundPool.load(context, R.raw.do4, 1),
            re4 = soundPool.load(context, R.raw.re4, 1),
            mi4 = soundPool.load(context, R.raw.mi4, 1),
            fa4 = soundPool.load(context, R.raw.fa4, 1),
            sol4 = soundPool.load(context, R.raw.sol4, 1),
            la4 = soundPool.load(context, R.raw.la4, 1),
            si4 = soundPool.load(context, R.raw.si4, 1),

            do5 = soundPool.load(context, R.raw.do5, 1),
            re5 = soundPool.load(context, R.raw.re5, 1),
            mi5 = soundPool.load(context, R.raw.mi5, 1),
            fa5 = soundPool.load(context, R.raw.fa5, 1),
            sol5 = soundPool.load(context, R.raw.sol5, 1),
            la5 = soundPool.load(context, R.raw.la5, 1),
            si5 = soundPool.load(context, R.raw.si5, 1),

            sol3 = soundPool.load(context, R.raw.sol3, 1),
            la3 = soundPool.load(context, R.raw.la3, 1),
            si3 = soundPool.load(context, R.raw.si3, 1)
        )
    }

    val notes = remember {
        mutableStateListOf<MusicNote>()
    }

    var beatsPerMeasure by remember {
        mutableIntStateOf(4)
    }
    var countdown by remember {
        mutableStateOf<Int?>(null)
    }
    var metronomeEnabled by remember {
        mutableStateOf(true)
    }

    var melodyEnabled by remember {
        mutableStateOf(true)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var bpm by remember {
        mutableIntStateOf(90)
    }

    var playbackX by remember {
        mutableFloatStateOf(420f)
    }

    val scrollState =
        rememberScrollState()
    val scope = rememberCoroutineScope()


    // ======================================================
    // STAFF SETTINGS
    // ======================================================

    val startY = 163f
    val spacing = 42f

    // ======================================================
    // LOAD JSON
    // ======================================================

    LaunchedEffect(Unit) {

        val file =
            File(
                context.filesDir,
                "$partitionName.json"
            )

        if (file.exists()) {

            val json =
                file.readText()

            val obj =
                JSONObject(json)

            beatsPerMeasure =
                obj.getInt("beatsPerMeasure")

            val notesArray =
                obj.getJSONArray("notes")

            notes.clear()

            for (i in 0 until notesArray.length()) {

                val noteObj =
                    notesArray.getJSONObject(i)

                notes.add(

                    MusicNote(

                        type =
                            NoteType.valueOf(
                                noteObj.getString("type")
                            ),

                        x =
                            noteObj.getDouble("x")
                                .toFloat(),

                        step =
                            noteObj.getInt("step"),

                        isRest =
                            noteObj.optBoolean("isRest", false)
                    )
                )

            }
        }
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // ======================================================
            // TOP BAR
            // ======================================================

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = partitionName,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    PlayerButton(
                        text =
                            if (isPlaying) "⏸"
                            else "▶",

                        size = 82.dp,

                        fontSize = 34.sp,

                        onClick = {

                            if (isPlaying) {

                                isPlaying = false

                            } else {

                                scope.launch {

                                    countdown = 4

                                    while (countdown != null && countdown!! > 0) {

                                        if (metronomeEnabled) {

                                            val clickSound =
                                                if (countdown == 4)
                                                    metroClickHigh
                                                else
                                                    metroClick

                                            soundPool.play(
                                                clickSound,
                                                2f,
                                                2f,
                                                1,
                                                0,
                                                1f
                                            )
                                        }

                                        delay((60000f / bpm).toLong())

                                        countdown = countdown!! - 1
                                    }

                                    countdown = null

                                    isPlaying = true
                                }
                            }
                        }
                    )
                    Spacer(
                        modifier = Modifier.width(24.dp)
                    )

                    Column {

                        Text(
                            text = "BPM : $bpm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Row {

                            PlayerButton(
                                text = "-",
                                size = 46.dp,
                                fontSize = 22.sp
                            ) {

                                bpm =
                                    maxOf(40, bpm - 5)
                            }

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            PlayerButton(
                                text = "+",
                                size = 46.dp,
                                fontSize = 22.sp
                            ) {

                                bpm =
                                    minOf(240, bpm + 5)
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    PlayerButton(
                        text = "■",
                        size = 74.dp,
                        fontSize = 28.sp,
                        onClick = {

                            isPlaying = false
                            playbackX = 420f

                            scope.launch {

                                scrollState.animateScrollTo(0)
                            }
                        }
                    )

                    Spacer(
                        modifier = Modifier.width(30.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Métronome",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Switch(
                                checked = metronomeEnabled,

                                onCheckedChange = {
                                    metronomeEnabled = it
                                }
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Mélodie",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Switch(
                                checked = melodyEnabled,

                                onCheckedChange = {
                                    melodyEnabled = it
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // ======================================================
            // STAFF CARD
            // ======================================================

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                shape =
                    RoundedCornerShape(40.dp),

                color =
                    Color.White.copy(alpha = 0.94f),

                shadowElevation = 6.dp
            ) {
                // ======================================================
// METRONOME + PLAYBACK
// ======================================================

                LaunchedEffect(isPlaying, bpm, notes.size) {

                    scope.launch {

                        var beat = 1
                        while (isPlaying) {

                            if (metronomeEnabled) {

                                val clickSound =
                                    if (beat == 1)
                                        metroClickHigh
                                    else
                                        metroClick

                                soundPool.play(
                                    clickSound,
                                    2f,
                                    2f,
                                    1,
                                    0,
                                    1f
                                )

                                beat++

                                if (beat > beatsPerMeasure) {
                                    beat = 1
                                }
                            }

                            delay((60000f / bpm).toLong())
                        }
                    }
                    val sortedNotes =
                        notes.sortedBy { it.x }

                    if (sortedNotes.isEmpty())
                        return@LaunchedEffect

                    playbackX =
                        sortedNotes.first().x * 1.35f

                    for (i in sortedNotes.indices) {

                        if (!isPlaying)
                            break

                        val current = sortedNotes[i]

                        val startX =
                            current.x * 1.35f

                        val endX =
                            if (i < sortedNotes.lastIndex)
                                sortedNotes[i + 1].x * 1.35f
                            else
                                startX + 180f



                        // =====================================
                        // PLAY NOTE ONLY IF NOT A REST
                        // =====================================


                        if (
                            melodyEnabled &&
                            !current.type.isRest
                        )

                        {

                            playNote(
                                note = current,
                                bpm = bpm,
                                soundPool = soundPool,
                                sounds = sounds,
                                scope = scope
                            )
                        }
                        val durationMs =
                            ((60000f / bpm) * current.type.duration)
                                .toLong()

                        val startTime =
                            System.currentTimeMillis()

                        while (true) {

                            val elapsed =
                                System.currentTimeMillis() - startTime

                            val progress =
                                (elapsed.toFloat() / durationMs)
                                    .coerceIn(0f, 1f)

                            playbackX =
                                startX + (endX - startX) * progress

                            scrollState.scrollTo(
                                maxOf(
                                    0,
                                    (playbackX - 500f).toInt()
                                )
                            )

                            if (progress >= 1f)
                                break

                            delay(16L)
                        }
                    }

                    isPlaying = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(scrollState)
                    ) {

                        val dynamicWidth =
                            maxOf(
                                7000f,
                                notes.size * 220f
                            )

                        Canvas(
                            modifier = Modifier
                                .width(dynamicWidth.dp)
                                .fillMaxSize()
                        ) {

                            // ======================================================
                            // STAFF LINES
                            // ======================================================

                            repeat(5) { index ->

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        120f,
                                        startY + index * spacing
                                    ),

                                    end = Offset(
                                        dynamicWidth,
                                        startY + index * spacing
                                    ),

                                    strokeWidth = 5f
                                )
                            }

                            // ======================================================
                            // CLEF
                            // ======================================================

                            drawContext.canvas.nativeCanvas.drawText(
                                "𝄞",
                                120f,
                                startY + 135f,
                                android.graphics.Paint().apply {

                                    textSize = 240f
                                    isAntiAlias = true
                                }
                            )

                            // ======================================================
                            // TIME SIGNATURE
                            // ======================================================

                            drawContext.canvas.nativeCanvas.apply {

                                drawText(
                                    beatsPerMeasure.toString(),
                                    330f,
                                    startY + 65f,
                                    android.graphics.Paint().apply {

                                        textSize = 95f
                                        isAntiAlias = true
                                    }
                                )

                                drawText(
                                    "4",
                                    330f,
                                    startY + 165f,
                                    android.graphics.Paint().apply {

                                        textSize = 95f
                                        isAntiAlias = true
                                    }
                                )
                            }

                            // ======================================================
                            // MEASURE BARS
                            // ======================================================

                            var beats = 0f

                            val measureBars =
                                mutableListOf<Float>()

                            notes
                                .sortedBy { it.x }
                                .forEach { note ->

                                    beats += note.type.duration

                                    if (beats >= beatsPerMeasure) {

                                        val extraSpacing =
                                            when (note.type) {

                                                NoteType.RONDE,
                                                NoteType.RONDE_POINTEE -> 170f

                                                NoteType.BLANCHE,
                                                NoteType.BLANCHE_POINTEE -> 130f

                                                else -> 90f
                                            }

                                        val barX =
                                            note.x * 1.35f + extraSpacing

                                        measureBars.add(barX)

                                        beats = 0f
                                    }
                                }
                            drawContext.canvas.nativeCanvas.drawText(
                                "1",
                                430f,
                                startY - 40f,
                                android.graphics.Paint().apply {

                                    textSize = 42f
                                    isAntiAlias = true
                                }
                            )
                            measureBars.forEachIndexed { index, barX ->

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        barX,
                                        startY - 10f
                                    ),

                                    end = Offset(
                                        barX,
                                        startY + spacing * 4 + 10f
                                    ),

                                    strokeWidth = 4f
                                )

                                drawContext.canvas.nativeCanvas.drawText(
                                    (index + 2).toString(),
                                    barX - 25f,
                                    startY - 40f,
                                    android.graphics.Paint().apply {

                                        textSize = 42f
                                        isAntiAlias = true
                                    }
                                )
                            }

                            // ======================================================
// BUILD TRUE MUSICAL BEAM GROUPS
// ======================================================

                            val beamGroups =
                                mutableListOf<List<MusicNote>>()

                            val sortedNotes =
                                notes.sortedBy { it.x }

                            var currentGroup =
                                mutableListOf<MusicNote>()

                            var measureBeat = 0f

                            sortedNotes.forEachIndexed { index, note ->

                                val isBeamable =
                                    !note.isRest &&
                                            (
                                                    note.type == NoteType.CROCHE ||
                                                            note.type == NoteType.CROCHE_POINTEE ||
                                                            note.type == NoteType.DOUBLE_CROCHE
                                                    )

                                // ======================================================
                                // NEW MEASURE
                                // ======================================================

                                if (measureBeat >= beatsPerMeasure) {

                                    if (currentGroup.size >= 2) {
                                        beamGroups.add(currentGroup.toList())
                                    }

                                    currentGroup.clear()

                                    measureBeat = 0f
                                }

                                // ======================================================
                                // NON BEAMABLE NOTE
                                // ======================================================

                                if (!isBeamable) {

                                    if (currentGroup.size >= 2) {
                                        beamGroups.add(currentGroup.toList())
                                    }

                                    currentGroup.clear()

                                    measureBeat += note.type.duration

                                    return@forEachIndexed
                                }

                                // ======================================================
                                // LARGE GAP => NEW GROUP
                                // ======================================================

                                if (currentGroup.isNotEmpty()) {

                                    val previous =
                                        currentGroup.last()

                                    val gap =
                                        note.x - previous.x

                                    if (gap > 180f) {

                                        if (currentGroup.size >= 2) {
                                            beamGroups.add(currentGroup.toList())
                                        }

                                        currentGroup = mutableListOf()
                                    }
                                }

                                currentGroup.add(note)

                                measureBeat += note.type.duration
                            }

                            if (currentGroup.size >= 2) {

                                beamGroups.add(currentGroup.toList())
                            }
                            // ======================================================
// PLAYBACK BAR
// ======================================================



                            drawLine(
                                color = Color.Red,

                                start = Offset(
                                    playbackX,
                                    startY - 80f
                                ),

                                end = Offset(
                                    playbackX,
                                    startY + spacing * 4 + 80f
                                ),

                                strokeWidth = 6f
                            )
                            // ======================================================
                            // NOTES
                            // ======================================================

                            notes.forEach { note ->
                                if (note.type.isRest) {

                                    val centerX =
                                        note.x * 1.35f

                                    val centerY =
                                        startY + spacing * 2

                                    when (note.type) {

                                        // =====================================
                                        // PAUSE (silence ronde)
                                        // =====================================

                                        NoteType.PAUSE -> {

                                            drawRect(
                                                color = Color.Black,

                                                topLeft = Offset(
                                                    centerX - 28f,
                                                    startY + spacing
                                                ),

                                                size = Size(
                                                    56f,
                                                    18f
                                                )
                                            )
                                        }

                                        // =====================================
                                        // DEMI-PAUSE (silence blanche)
                                        // =====================================

                                        NoteType.DEMI_PAUSE -> {

                                            drawRect(
                                                color = Color.Black,

                                                topLeft = Offset(
                                                    centerX - 28f,
                                                    startY + spacing * 2 - 18f
                                                ),

                                                size = Size(
                                                    56f,
                                                    18f
                                                )
                                            )
                                        }

                                        // =====================================
                                        // SOUPIR
                                        // =====================================

                                        NoteType.SOUPIR -> {

                                            drawContext.canvas.nativeCanvas.drawText(
                                                "𝄽",
                                                centerX - 26f,
                                                startY + spacing * 2 + 55f,
                                                android.graphics.Paint().apply {

                                                    textSize = 125f
                                                    isAntiAlias = true
                                                }
                                            )
                                        }

                                        // =====================================
                                        // DEMI-SOUPIR
                                        // =====================================

                                        NoteType.DEMI_SOUPIR -> {

                                            drawContext.canvas.nativeCanvas.drawText(
                                                "𝄾",
                                                centerX - 24f,
                                                startY + spacing * 2 + 55f,
                                                android.graphics.Paint().apply {

                                                    textSize = 125f
                                                    isAntiAlias = true
                                                }
                                            )
                                        }

                                        // =====================================
                                        // SILENCE DOUBLE CROCHE
                                        // =====================================

                                        NoteType.SILENCE_DOUBLE_CROCHE -> {

                                            drawContext.canvas.nativeCanvas.drawText(
                                                "𝄿",
                                                centerX - 24f,
                                                startY + spacing * 2 + 55f,
                                                android.graphics.Paint().apply {

                                                    textSize = 125f
                                                    isAntiAlias = true
                                                }
                                            )
                                        }

                                        else -> {}
                                    }

                                    return@forEach
                                }
                                val centerX =
                                    note.x * 1.35f

                                val centerY =
                                    getNoteY(
                                        note.step,
                                        startY,
                                        spacing
                                    )
                                // ======================================================
                                // LEDGER LINES
                                // ======================================================

                                val top =
                                    startY

                                val bottom =
                                    startY + spacing * 4

                                if (centerY < top) {

                                    var y =
                                        top - spacing

                                    while (y >= centerY) {

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                centerX - 42f,
                                                y
                                            ),

                                            end = Offset(
                                                centerX + 42f,
                                                y
                                            ),

                                            strokeWidth = 4f
                                        )

                                        y -= spacing
                                    }
                                }

                                if (centerY > bottom) {

                                    var y =
                                        bottom + spacing

                                    while (y <= centerY) {

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                centerX - 42f,
                                                y
                                            ),

                                            end = Offset(
                                                centerX + 42f,
                                                y
                                            ),

                                            strokeWidth = 4f
                                        )

                                        y += spacing
                                    }
                                }

                                // ======================================================
                                // NOTE HEAD
                                // ======================================================

                                drawOval(
                                    color =
                                        if (note.type.filled)
                                            Color.Black
                                        else
                                            Color.White,

                                    topLeft = Offset(
                                        centerX - 28f,
                                        centerY - 20f
                                    ),

                                    size = Size(
                                        56f,
                                        42f
                                    )
                                )

                                drawOval(
                                    color = Color.Black,

                                    topLeft = Offset(
                                        centerX - 28f,
                                        centerY - 20f
                                    ),

                                    size = Size(
                                        56f,
                                        42f
                                    ),

                                    style = Stroke(width = 4f)
                                )
                                // ======================================================
// POINTED NOTES
// ======================================================

                                val isPointed =
                                    note.type == NoteType.RONDE_POINTEE ||
                                            note.type == NoteType.BLANCHE_POINTEE ||
                                            note.type == NoteType.NOIRE_POINTEE ||
                                            note.type == NoteType.CROCHE_POINTEE

                                if (isPointed) {

                                    drawCircle(
                                        color = Color.Black,
                                        radius = 5f,

                                        center = Offset(
                                            centerX + 42f,
                                            centerY
                                        )
                                    )
                                }
                                // ======================================================
                                // STEMS (NON-BEAMED NOTES)
                                // ======================================================

                                val isBeamed =
                                    beamGroups.any { group ->
                                        note in group
                                    }

                                if (
                                    note.type != NoteType.RONDE &&
                                    note.type != NoteType.RONDE_POINTEE &&
                                    !isBeamed
                                ) {

                                    val stemUp =
                                        centerY >
                                                startY +
                                                spacing * 2

                                    if (stemUp) {

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                centerX + 24f,
                                                centerY
                                            ),

                                            end = Offset(
                                                centerX + 24f,
                                                centerY - 130f
                                            ),

                                            strokeWidth = 6f,

                                            cap = StrokeCap.Round
                                        )

                                    } else {

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                centerX - 24f,
                                                centerY
                                            ),

                                            end = Offset(
                                                centerX - 24f,
                                                centerY + 130f
                                            ),

                                            strokeWidth = 6f,

                                            cap = StrokeCap.Round
                                        )
                                    }
                                    if (
                                        note.type == NoteType.CROCHE ||
                                        note.type == NoteType.CROCHE_POINTEE
                                    ) {

                                        if (stemUp) {

                                            drawArc(
                                                color = Color.Black,

                                                startAngle = 0f,
                                                sweepAngle = 180f,
                                                useCenter = false,

                                                topLeft = Offset(
                                                    centerX + 8f,
                                                    centerY - 130f
                                                ),

                                                size = Size(42f, 55f),

                                                style = Stroke(width = 8f)
                                            )

                                        } else {

                                            drawArc(
                                                color = Color.Black,

                                                startAngle = 180f,
                                                sweepAngle = 180f,
                                                useCenter = false,

                                                topLeft = Offset(
                                                    centerX - 50f,
                                                    centerY + 80f
                                                ),

                                                size = Size(42f, 55f),

                                                style = Stroke(width = 8f)
                                            )
                                        }
                                    }
                                    if (note.type == NoteType.DOUBLE_CROCHE) {

                                        val firstOffset =
                                            if (stemUp) -130f else 80f

                                        val secondOffset =
                                            if (stemUp) -105f else 105f

                                        repeat(2) { index ->

                                            val yOffset =
                                                if (index == 0)
                                                    firstOffset
                                                else
                                                    secondOffset

                                            if (stemUp) {

                                                drawArc(
                                                    color = Color.Black,

                                                    startAngle = 0f,
                                                    sweepAngle = 180f,
                                                    useCenter = false,

                                                    topLeft = Offset(
                                                        centerX + 8f,
                                                        centerY + yOffset
                                                    ),

                                                    size = Size(42f, 55f),

                                                    style = Stroke(width = 8f)
                                                )

                                            } else {

                                                drawArc(
                                                    color = Color.Black,

                                                    startAngle = 180f,
                                                    sweepAngle = 180f,
                                                    useCenter = false,

                                                    topLeft = Offset(
                                                        centerX - 50f,
                                                        centerY + yOffset
                                                    ),

                                                    size = Size(42f, 55f),

                                                    style = Stroke(width = 8f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // ======================================================
// DRAW BEAMS
// ======================================================

                            beamGroups.forEach { group ->

                                if (group.size < 2)
                                    return@forEach

                                val averageY =
                                    group.map {
                                        getNoteY(it.step, startY, spacing) - 12f
                                    }
                                        .average()
                                        .toFloat()

                                val middleLineY =
                                    startY + spacing * 2

                                // ======================================================
                                // STEM DIRECTION
                                // ======================================================

                                val stemUp =
                                    averageY > middleLineY

                                val first =
                                    group.first()

                                val last =
                                    group.last()

                                val firstX =
                                    first.x * 1.35f

                                val lastX =
                                    last.x * 1.35f

                                val totalWidth =
                                    maxOf(1f, lastX - firstX)

                                // ======================================================
                                // BEAM SLOPE
                                // ======================================================

                                val firstY =
                                    getNoteY(first.step, startY, spacing) - 12f

                                val lastY =
                                    getNoteY(last.step, startY, spacing) - 12f

                                // ======================================================
// NATURAL BEAM SLOPE
// ======================================================

                                val idealSlope =
                                    ((lastY - firstY) * 0.25f)
                                        .coerceIn(-25f, 25f)

                                val beamStartY =
                                    if (stemUp)
                                        averageY - 150f
                                    else
                                        averageY + 150f

                                val beamEndY =
                                    beamStartY + idealSlope
                                // ======================================================
                                // HELPER
                                // ======================================================

                                fun beamYAt(x: Float): Float {

                                    val progress =
                                        (x - firstX) / totalWidth

                                    return beamStartY +
                                            (beamEndY - beamStartY) * progress
                                }

                                // ======================================================
                                // STEMS
                                // ======================================================

                                group.forEach { note ->

                                    val noteX =
                                        note.x * 1.35f

                                    val noteY =
                                        getNoteY(note.step, startY, spacing) - 12f

                                    val stemX =
                                        if (stemUp)
                                            noteX + 24f
                                        else
                                            noteX - 24f

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            stemX,
                                            noteY
                                        ),

                                        end = Offset(
                                            stemX,
                                            beamYAt(noteX)
                                        ),

                                        strokeWidth = 6f,

                                        cap = StrokeCap.Round
                                    )
                                }

                                // ======================================================
                                // MAIN BEAM
                                // ======================================================

                                val firstStemX =
                                    if (stemUp)
                                        firstX + 24f
                                    else
                                        firstX - 24f

                                val lastStemX =
                                    if (stemUp)
                                        lastX + 24f
                                    else
                                        lastX - 24f

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        firstStemX,
                                        beamYAt(firstX)
                                    ),

                                    end = Offset(
                                        lastStemX,
                                        beamYAt(lastX)
                                    ),

                                    strokeWidth = 12f
                                )

                                // ======================================================
                                // SECONDARY BEAMS
                                // ======================================================

                                group.windowed(2).forEach { pair ->

                                    val left =
                                        pair[0]

                                    val right =
                                        pair[1]

                                    val leftDouble =
                                        left.type == NoteType.DOUBLE_CROCHE

                                    val rightDouble =
                                        right.type == NoteType.DOUBLE_CROCHE

                                    // ======================================================
                                    // CASE 1
                                    // DOUBLE + DOUBLE
                                    // ======================================================

                                    if (leftDouble && rightDouble) {

                                        val leftX =
                                            left.x * 1.35f

                                        val rightX =
                                            right.x * 1.35f

                                        val leftStemX =
                                            if (stemUp)
                                                leftX + 24f
                                            else
                                                leftX - 24f

                                        val rightStemX =
                                            if (stemUp)
                                                rightX + 24f
                                            else
                                                rightX - 24f

                                        val offset =
                                            if (stemUp)
                                                20f
                                            else
                                                -20f

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                leftStemX,
                                                beamYAt(leftX) + offset
                                            ),

                                            end = Offset(
                                                rightStemX,
                                                beamYAt(rightX) + offset
                                            ),

                                            strokeWidth = 10f
                                        )
                                    }

                                    // ======================================================
                                    // CASE 2
                                    // DOUBLE + CROCHE
                                    // => LEFT HOOK
                                    // ======================================================

                                    else if (leftDouble && !rightDouble) {

                                        val leftX =
                                            left.x * 1.35f

                                        val leftStemX =
                                            if (stemUp)
                                                leftX + 24f
                                            else
                                                leftX - 24f

                                        val offset =
                                            if (stemUp)
                                                20f
                                            else
                                                -20f

                                        val hookLength = 55f

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                leftStemX,
                                                beamYAt(leftX) + offset
                                            ),

                                            end = Offset(
                                                leftStemX + hookLength,
                                                beamYAt(leftX) + offset
                                            ),

                                            strokeWidth = 10f
                                        )
                                    }

                                    // ======================================================
                                    // CASE 3
                                    // CROCHE + DOUBLE
                                    // => RIGHT HOOK
                                    // ======================================================

                                    else if (!leftDouble && rightDouble) {

                                        val rightX =
                                            right.x * 1.35f

                                        val rightStemX =
                                            if (stemUp)
                                                rightX + 24f
                                            else
                                                rightX - 24f

                                        val offset =
                                            if (stemUp)
                                                20f
                                            else
                                                -20f

                                        val hookLength = 40f

                                        drawLine(
                                            color = Color.Black,

                                            start = Offset(
                                                rightStemX - hookLength,
                                                beamYAt(rightX) + offset
                                            ),

                                            end = Offset(
                                                rightStemX,
                                                beamYAt(rightX) + offset
                                            ),

                                            strokeWidth = 10f
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        countdown?.let { value ->

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = value.toString(),

                    fontSize = 140.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color.Black.copy(alpha = 0.75f)
                )
            }
        }
    }
}

fun playNote(
    note: MusicNote,
    bpm: Int,
    soundPool: SoundPool,
    sounds: PianoSounds,
    scope: CoroutineScope
) {

    // =====================================================
    // STEP -> SOUND
    // =====================================================

    val soundId = when (note.step) {

        // =========================
        // BASSES
        // =========================


        -5 -> sounds.sol3
        -4 -> sounds.la3
        -3 -> sounds.si3
        -2 -> sounds.do4
        -1 -> sounds.re4

        // =========================
        // PORTÉE
        // =========================

        0 -> sounds.mi4
        1 -> sounds.fa4
        2 -> sounds.sol4
        3 -> sounds.la4
        4 -> sounds.si4
        5 -> sounds.do5
        6 -> sounds.re5
        7 -> sounds.mi5
        8 -> sounds.fa5
        9 -> sounds.sol5
        10 -> sounds.la5
        11 -> sounds.si5

        else -> sounds.mi4
    }

    // =====================================================
    // NOTE DURATION
    // =====================================================

    val quarterDuration =
        60000f / bpm

    val durationMs =
        quarterDuration * note.type.duration

    // =====================================================
    // SMALL SILENCE BEFORE NOTE
    // =====================================================

    scope.launch {

        delay(10L)

        val streamId =
            soundPool.play(
                soundId,
                1f,
                1f,
                1,
                0,
                1f
            )

        delay(durationMs.toLong())

        soundPool.stop(streamId)
    }
}
// ======================================================
// PLAYER BUTTON
// ======================================================

@Composable
fun PlayerButton(
    text: String,
    size: androidx.compose.ui.unit.Dp,
    fontSize: TextUnit,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .size(size)
            .clickable {

                onClick()
            },

        shape = CircleShape,

        color = Color(0xFFD8C7F1)
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = text,
                fontSize = fontSize,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
