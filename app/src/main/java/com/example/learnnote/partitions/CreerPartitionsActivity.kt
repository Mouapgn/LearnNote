package com.example.learnnote.partitions

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import org.json.JSONArray
import org.json.JSONObject
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton

class CreerPartitionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {

            MaterialTheme {

                CreerPartitionScreen()
            }
        }
    }
}

// ======================================================
// DATA
// ======================================================

enum class NoteType(
    val duration: Float,
    val filled: Boolean,
    val label: String,
    val isRest: Boolean = false
) {

    RONDE(4f, false, "Ronde"),

    RONDE_POINTEE(6f, false, "Ronde."),

    BLANCHE(2f, false, "Blanche"),

    BLANCHE_POINTEE(3f, false, "Blanche."),

    NOIRE(1f, true, "Noire"),

    NOIRE_POINTEE(1.5f, true, "Noire."),

    CROCHE(0.5f, true, "Croche"),

    CROCHE_POINTEE(0.75f, true, "Croche."),

    DOUBLE_CROCHE(0.25f, true, "Double croche"),

    PAUSE(
        duration = 4f,
        filled = false,
        label = "Pause",
        isRest = true
    ),

    DEMI_PAUSE(
        duration = 2f,
        filled = false,
        label = "Demi-pause",
        isRest = true
    ),

    SOUPIR(
        duration = 1f,
        filled = false,
        label = "Soupir",
        isRest = true
    ),

    DEMI_SOUPIR(
        duration = 0.5f,
        filled = false,
        label = "Demi-soupir",
        isRest = true
    ),

    SILENCE_DOUBLE_CROCHE(
        duration = 0.25f,
        filled = false,
        label = "Silence double croche",
        isRest = true
    )
}

fun NoteType.isBeamNote(): Boolean {

    return this == NoteType.CROCHE ||
            this == NoteType.CROCHE_POINTEE ||
            this == NoteType.DOUBLE_CROCHE
}

// ======================================================
// SCREEN
// ======================================================

@Composable
fun CreerPartitionScreen() {

    var beatsPerMeasure by remember {

        mutableIntStateOf(4)
    }

    val notes = remember {

        mutableStateListOf<MusicNote>()
    }
    // ======================================================
// SAVE
// ======================================================

    val context = LocalContext.current

    fun savePartition() {

        val notesArray = JSONArray()

        notes.forEach { note ->

            val obj = JSONObject()

            obj.put("type", note.type.name)
            obj.put("x", note.x)
            obj.put("step", note.step)

            notesArray.put(obj)
        }

        val partitionObject = JSONObject()

        partitionObject.put(
            "beatsPerMeasure",
            beatsPerMeasure
        )

        partitionObject.put(
            "notes",
            notesArray
        )

        val fileName =
            "partition_${System.currentTimeMillis()}.json"

        context.openFileOutput(
            fileName,
            Context.MODE_PRIVATE
        ).use { file ->

            file.write(
                partitionObject
                    .toString()
                    .toByteArray()
            )
        }

        Toast.makeText(
            context,
            "Partition enregistrée",
            Toast.LENGTH_SHORT
        ).show()

    }
    var selectedType by remember {

        mutableStateOf(NoteType.NOIRE)
    }

    var showSaveDialog by remember {

        mutableStateOf(false)
    }

    var partitionName by remember {

        mutableStateOf("")
    }

    val scrollState = rememberScrollState()

    val startY = 240f
    val spacing = 30f
    val saveLauncher =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts.CreateDocument(
                    "application/json"
                )

        ) { uri: Uri? ->

            uri?.let {

                val notesArray = JSONArray()

                notes.forEach { note ->

                    val obj = JSONObject()

                    obj.put("type", note.type.name)
                    obj.put("x", note.x)
                    obj.put("step", note.step)
                    obj.put("isRest", note.type.isRest)
                    notesArray.put(obj)
                }

                val partitionObject = JSONObject()

                partitionObject.put(
                    "title",
                    partitionName
                )

                partitionObject.put(
                    "beatsPerMeasure",
                    beatsPerMeasure
                )

                partitionObject.put(
                    "notes",
                    notesArray
                )

                context.contentResolver
                    .openOutputStream(uri)
                    ?.use { output ->

                        output.write(
                            partitionObject
                                .toString(4)
                                .toByteArray()
                        )
                    }

// ======================================================
// AJOUT DANS partitions.json
// ======================================================

                val prefs =
                    context.getSharedPreferences(
                        "learnnote_partitions",
                        Context.MODE_PRIVATE
                    )

                val existing =
                    prefs.getString(
                        "partitions_list",
                        "[]"
                    )

                val partitionsArray =
                    JSONArray(existing)

                val newPartition = JSONObject()

                newPartition.put(
                    "title",
                    partitionName
                )

                newPartition.put(
                    "uri",
                    uri.toString()
                )

                partitionsArray.put(newPartition)

                prefs.edit()
                    .putString(
                        "partitions_list",
                        partitionsArray.toString()
                    )
                    .apply()
                Toast.makeText(
                    context,
                    "Partition enregistrée",
                    Toast.LENGTH_SHORT
                ).show()
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

        Column {

            // ======================================================
            // HEADER
            // ======================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 28.dp,
                        vertical = 18.dp
                    ),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Créer Partition",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Surface(

                        modifier = Modifier
                            .pointerInput(Unit) {

                                detectTapGestures {

                                    showSaveDialog = true
                                }
                            },

                        shape = RoundedCornerShape(18.dp),

                        color = Color(0xFFD8C7F1)
                    ) {

                        Box(
                            modifier = Modifier.padding(
                                horizontal = 22.dp,
                                vertical = 12.dp
                            )
                        ) {

                            Text(
                                text = "Enregistrer",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Mesure",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    listOf(2, 3, 4, 6).forEach { beat ->

                        val selected =
                            beatsPerMeasure == beat

                        Surface(
                            modifier = Modifier
                                .size(52.dp)
                                .padding(horizontal = 4.dp)
                                .pointerInput(Unit) {

                                    detectTapGestures {

                                        beatsPerMeasure = beat
                                    }
                                },

                            shape = CircleShape,

                            color =
                                if (selected)
                                    Color(0xFFD8C7F1)
                                else
                                    Color.White
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text = beat.toString(),
                                    fontWeight = FontWeight.Bold,

                                    color =
                                        if (selected)
                                            Color.White
                                        else
                                            Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "/4",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ======================================================
            // STAFF
            // ======================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 22.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(34.dp)
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(scrollState)
                ) {

                    val dynamicWidth =
                        maxOf(
                            6000f,
                            notes.size * 170f
                        )

                    Canvas(
                        modifier = Modifier
                            .width(dynamicWidth.dp)
                            .fillMaxHeight()
                            .pointerInput(selectedType, notes.size) {

                                detectTapGestures(


                                onTap = { offset ->

                                    val stepSize = spacing / 2f

                                    val snapped =
                                        ((startY + spacing * 4 - offset.y) / (spacing / 2f))
                                            .roundToInt()

                                    val sorted =
                                        notes.sortedBy { it.x }

                                    // ======================================================
                                    // POSITION X MUSICALE
                                    // ======================================================

                                    var nextX = 360f

                                    if (sorted.isNotEmpty()) {

                                        val last =
                                            sorted.last()

                                        nextX =
                                            last.x + when (last.type) {

                                                NoteType.RONDE,
                                                NoteType.RONDE_POINTEE -> 260f

                                                NoteType.BLANCHE,
                                                NoteType.BLANCHE_POINTEE -> 210f

                                                NoteType.NOIRE,
                                                NoteType.NOIRE_POINTEE -> 170f

                                                NoteType.CROCHE,
                                                NoteType.CROCHE_POINTEE -> 130f

                                                NoteType.DOUBLE_CROCHE -> 105f

                                                NoteType.PAUSE -> 220f

                                                NoteType.DEMI_PAUSE -> 180f

                                                NoteType.SOUPIR -> 160f

                                                NoteType.DEMI_SOUPIR -> 130f

                                                NoteType.SILENCE_DOUBLE_CROCHE -> 110f
                                            }
                                    }

                                    notes.add(
                                        MusicNote(
                                            type = selectedType,
                                            x = nextX,
                                            step = snapped
                                        )
                                    )
                                },

                                    onLongPress = { offset ->

                                        val clicked =
                                            notes.findLast {

                                                val noteY =
                                                    getNoteY(it.step, startY, spacing)

                                                abs(it.x - offset.x) < 35f &&
                                                        abs(noteY - offset.y) < 35f
                                            }

                                        clicked?.let {

                                            notes.remove(it)
                                        }
                                    }
                                )
                            }
                    ) {

                        // ======================================================
                        // STAFF LINES
                        // ======================================================

                        repeat(5) { index ->

                            drawLine(
                                color = Color.Black,

                                start = Offset(
                                    80f,
                                    startY + index * spacing
                                ),

                                end = Offset(
                                    dynamicWidth,
                                    startY + index * spacing
                                ),

                                strokeWidth = 3f
                            )
                        }

                        // ======================================================
                        // CLEF
                        // ======================================================

                        drawContext.canvas.nativeCanvas.drawText(
                            "𝄞",
                            90f,
                            startY + 95f,
                            android.graphics.Paint().apply {

                                textSize = 170f
                                isAntiAlias = true
                            }
                        )

                        // ======================================================
                        // SIGNATURE
                        // ======================================================

                        drawContext.canvas.nativeCanvas.apply {

                            drawText(
                                beatsPerMeasure.toString(),
                                230f,
                                startY + 45f,
                                android.graphics.Paint().apply {

                                    textSize = 70f
                                    isAntiAlias = true
                                }
                            )

                            drawText(
                                "4",
                                230f,
                                startY + 120f,
                                android.graphics.Paint().apply {

                                    textSize = 70f
                                    isAntiAlias = true
                                }
                            )
                        }

                        // ======================================================
                        // MEASURES
                        // ======================================================

                        var beats = 0f
                        val measureBars =
                            mutableListOf<Float>()

                        notes
                            .sortedBy { it.x }
                            .forEach { note ->

                                beats += note.type.duration

                                if (beats >= beatsPerMeasure) {

                                    val barX =
                                        note.x + 80f

                                    measureBars.add(barX)

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            barX,
                                            startY - 12f
                                        ),

                                        end = Offset(
                                            barX,
                                            startY +
                                                    spacing * 4 +
                                                    12f
                                        ),

                                        strokeWidth = 4f
                                    )

                                    beats = 0f
                                }
                            }

                        // ======================================================
                        // NUMBERS
                        // ======================================================

                        var previousX = 260f

                        measureBars.forEachIndexed { index, barX ->

                            val centerX =
                                (previousX + barX) / 2f

                            drawContext.canvas.nativeCanvas.drawText(
                                "${index + 1}",
                                centerX,
                                startY - 40f,
                                android.graphics.Paint().apply {

                                    textSize = 34f
                                    isAntiAlias = true

                                    textAlign =
                                        android.graphics.Paint.Align.CENTER
                                }
                            )

                            previousX = barX
                        }

                        // ======================================================
                        // BEAMS PRO
                        // ======================================================

                        val sortedNotes =
                            notes.sortedBy { it.x }

                        val beamGroups =
                            mutableListOf<List<MusicNote>>()

                        var currentGroup =
                            mutableListOf<MusicNote>()

                        sortedNotes.forEachIndexed { index, note ->

                            if (!note.type.isBeamNote()) {

                                if (currentGroup.size >= 2) {

                                    beamGroups.add(currentGroup.toList())
                                }

                                currentGroup = mutableListOf()

                                return@forEachIndexed
                            }

                            if (currentGroup.isEmpty()) {

                                currentGroup.add(note)

                            } else {

                                val previous =
                                    currentGroup.last()

                                val closeEnough =
                                    note.x - previous.x < 160f

                                if (closeEnough) {

                                    currentGroup.add(note)

                                } else {

                                    if (currentGroup.size >= 2) {

                                        beamGroups.add(
                                            currentGroup.toList()
                                        )
                                    }

                                    currentGroup =
                                        mutableListOf(note)
                                }
                            }

                            if (
                                index == sortedNotes.lastIndex &&
                                currentGroup.size >= 2
                            ) {

                                beamGroups.add(
                                    currentGroup.toList()
                                )
                            }
                        }

                        beamGroups.forEach { group ->

                            val averageY =
                                group.map {
                                    getNoteY(it.step, startY, spacing)
                                }.average().toFloat()

                            val stemUp =
                                averageY > startY + spacing * 2

                            val beamThickness = 12f
                            val secondBeamOffset = 20f

                            val first =
                                group.first()

                            val last =
                                group.last()

                            val beamY =
                                if (stemUp)
                                    group.minOf {
                                        getNoteY(it.step, startY, spacing)
                                    } - 90f
                                else
                                    group.maxOf {
                                        getNoteY(it.step, startY, spacing)
                                    } + 90f

                            // STEMS

                            group.forEach { note ->

                                val stemX =
                                    if (stemUp)
                                        note.x + 13f
                                    else
                                        note.x - 13f

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        stemX,
                                        getNoteY(note.step, startY, spacing)
                                    ),

                                    end = Offset(
                                        stemX,
                                        beamY
                                    ),

                                    strokeWidth = 4f,

                                    cap = StrokeCap.Round
                                )
                            }

                            // MAIN BEAM

                            val firstStemX =
                                if (stemUp)
                                    first.x + 13f
                                else
                                    first.x - 13f

                            val lastStemX =
                                if (stemUp)
                                    last.x + 13f
                                else
                                    last.x - 13f

                            drawLine(
                                color = Color.Black,

                                start = Offset(
                                    firstStemX,
                                    beamY
                                ),

                                end = Offset(
                                    lastStemX,
                                    beamY
                                ),

                                strokeWidth = beamThickness
                            )

                            // SECONDARY BEAMS

                            group.windowed(2).forEach { pair ->

                                val left = pair[0]
                                val right = pair[1]

                                val leftDouble =
                                    left.type == NoteType.DOUBLE_CROCHE

                                val rightDouble =
                                    right.type == NoteType.DOUBLE_CROCHE

                                val leftStemX =
                                    if (stemUp)
                                        left.x + 13f
                                    else
                                        left.x - 13f

                                val rightStemX =
                                    if (stemUp)
                                        right.x + 13f
                                    else
                                        right.x - 13f

                                val secondBeamY =
                                    if (stemUp)
                                        beamY + secondBeamOffset
                                    else
                                        beamY - secondBeamOffset

                                // DOUBLE + DOUBLE

                                if (leftDouble && rightDouble) {

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            leftStemX,
                                            secondBeamY
                                        ),

                                        end = Offset(
                                            rightStemX,
                                            secondBeamY
                                        ),

                                        strokeWidth = 10f
                                    )
                                }

                                // DOUBLE + CROCHE

                                else if (leftDouble && !rightDouble) {

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            leftStemX,
                                            secondBeamY
                                        ),

                                        end = Offset(
                                            leftStemX + 45f,
                                            secondBeamY
                                        ),

                                        strokeWidth = 10f
                                    )
                                }

                                // CROCHE + DOUBLE

                                else if (!leftDouble && rightDouble) {

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            rightStemX - 45f,
                                            secondBeamY
                                        ),

                                        end = Offset(
                                            rightStemX,
                                            secondBeamY
                                        ),

                                        strokeWidth = 10f
                                    )
                                }
                            }
                        }

                        // ======================================================
                        // NOTES
                        // ======================================================

                        notes.forEach { note ->

                            val centerX = note.x
                            val centerY = getNoteY(note.step, startY, spacing)

                            // LEDGER LINES

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
                                            centerX - 24f,
                                            y
                                        ),

                                        end = Offset(
                                            centerX + 24f,
                                            y
                                        ),

                                        strokeWidth = 3f
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
                                            centerX - 24f,
                                            y
                                        ),

                                        end = Offset(
                                            centerX + 24f,
                                            y
                                        ),

                                        strokeWidth = 3f
                                    )

                                    y += spacing
                                }
                            }

                            // ======================================================
// SILENCES PROFESSIONNELS
// ======================================================
// PAUSE (ronde)
// suspendue sous la 2e ligne
// ======================================================

                            if (note.type == NoteType.PAUSE) {

                                drawRect(
                                    color = Color.Black,

                                    topLeft = Offset(
                                        centerX - 18f,
                                        startY + spacing + 2f
                                    ),

                                    size = Size(
                                        36f,
                                        12f
                                    )
                                )

                                return@forEach
                            }

// ======================================================
// DEMI-PAUSE (blanche)
// posée sur la 3e ligne
// ======================================================

                            if (note.type == NoteType.DEMI_PAUSE) {

                                drawRect(
                                    color = Color.Black,

                                    topLeft = Offset(
                                        centerX - 18f,
                                        startY + spacing * 2 - 12f
                                    ),

                                    size = Size(
                                        36f,
                                        12f
                                    )
                                )

                                return@forEach
                            }

// ======================================================
// SOUPIR (noire silence)
// ======================================================

                            // ======================================================
// SILENCES
// ======================================================

                            if (note.type == NoteType.SOUPIR) {

                                drawLine(
                                    color = Color.Black,
                                    start = Offset(centerX, centerY - 20f),
                                    end = Offset(centerX + 12f, centerY),
                                    strokeWidth = 5f
                                )

                                drawLine(
                                    color = Color.Black,
                                    start = Offset(centerX + 12f, centerY),
                                    end = Offset(centerX - 8f, centerY + 25f),
                                    strokeWidth = 5f
                                )

                                drawLine(
                                    color = Color.Black,
                                    start = Offset(centerX - 8f, centerY + 25f),
                                    end = Offset(centerX + 10f, centerY + 45f),
                                    strokeWidth = 5f
                                )

                                return@forEach
                            }

                            // ======================================================


                            if (note.type == NoteType.DEMI_SOUPIR) {

                                drawCircle(
                                    color = Color.Black,
                                    radius = 6f,

                                    center = Offset(
                                        centerX,
                                        centerY + 8f
                                    )
                                )

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        centerX,
                                        centerY - 28f
                                    ),

                                    end = Offset(
                                        centerX,
                                        centerY + 8f
                                    ),

                                    strokeWidth = 4f
                                )

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        centerX,
                                        centerY - 28f
                                    ),

                                    end = Offset(
                                        centerX + 16f,
                                        centerY - 12f
                                    ),

                                    strokeWidth = 4f
                                )

                                return@forEach
                            }

                            if (note.type == NoteType.SILENCE_DOUBLE_CROCHE) {

                                drawCircle(
                                    color = Color.Black,
                                    radius = 6f,

                                    center = Offset(
                                        centerX,
                                        centerY + 10f
                                    )
                                )

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        centerX,
                                        centerY - 35f
                                    ),

                                    end = Offset(
                                        centerX,
                                        centerY + 10f
                                    ),

                                    strokeWidth = 4f
                                )

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        centerX,
                                        centerY - 35f
                                    ),

                                    end = Offset(
                                        centerX + 18f,
                                        centerY - 18f
                                    ),

                                    strokeWidth = 4f
                                )

                                drawLine(
                                    color = Color.Black,

                                    start = Offset(
                                        centerX,
                                        centerY - 15f
                                    ),

                                    end = Offset(
                                        centerX + 18f,
                                        centerY + 2f
                                    ),

                                    strokeWidth = 4f
                                )

                                return@forEach
                            }
                            // HEAD

                            drawOval(
                                color =
                                    if (note.type.filled)
                                        Color.Black
                                    else
                                        Color.White,

                                topLeft = Offset(
                                    centerX - 15f,
                                    centerY - 10f
                                ),

                                size = Size(
                                    30f,
                                    20f
                                )
                            )

                            drawOval(
                                color = Color.Black,

                                topLeft = Offset(
                                    centerX - 15f,
                                    centerY - 10f
                                ),

                                size = Size(
                                    30f,
                                    20f
                                ),

                                style = Stroke(width = 3f)
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
                                    radius = 4f,

                                    center = Offset(
                                        centerX + 26f,
                                        centerY
                                    )
                                )
                            }
                            // STEM

                            if (
                                note.type != NoteType.RONDE &&
                                note.type != NoteType.RONDE_POINTEE &&
                                !note.type.isBeamNote()
                            ) {

                                val stemUp =
                                    centerY >
                                            startY +
                                            spacing * 2

                                if (stemUp) {

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            centerX + 13f,
                                            centerY
                                        ),

                                        end = Offset(
                                            centerX + 13f,
                                            centerY - 90f
                                        ),

                                        strokeWidth = 4f,

                                        cap = StrokeCap.Round
                                    )

                                } else {

                                    drawLine(
                                        color = Color.Black,

                                        start = Offset(
                                            centerX - 13f,
                                            centerY
                                        ),

                                        end = Offset(
                                            centerX - 13f,
                                            centerY + 90f
                                        ),

                                        strokeWidth = 4f,

                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ======================================================
            // TOOLBAR
            // ======================================================

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(14.dp),

                contentPadding =
                    PaddingValues(horizontal = 20.dp)
            ) {

                items(NoteType.entries) { type ->

                    val selected =
                        selectedType == type

                    Surface(
                        modifier = Modifier
                            .size(
                                width = 130.dp,
                                height = 82.dp
                            )
                            .pointerInput(Unit) {

                                detectTapGestures {

                                    selectedType = type
                                }
                            },

                        shape = RoundedCornerShape(26.dp),

                        color =
                            if (selected)
                                Color(0xFFD8C7F1)
                            else
                                Color.White
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = type.label,

                                fontSize = 16.sp,

                                fontWeight = FontWeight.Bold,

                                color =
                                    if (selected)
                                        Color.White
                                    else
                                        Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
    if (showSaveDialog) {

        AlertDialog(

            onDismissRequest = {

                showSaveDialog = false
            },

            title = {

                Text("Nom de la partition")
            },

            text = {

                OutlinedTextField(

                    value = partitionName,

                    onValueChange = {

                        partitionName = it
                    },

                    label = {

                        Text("Nom")
                    }
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        val finalName =
                            if (partitionName.isBlank())
                                "partition"
                            else
                                partitionName

                        val notesArray = JSONArray()

                        notes.forEach { note ->

                            val obj = JSONObject()

                            obj.put("type", note.type.name)
                            obj.put("x", note.x)
                            obj.put("step", note.step)

                            notesArray.put(obj)
                        }

                        val partitionObject = JSONObject()

                        partitionObject.put(
                            "beatsPerMeasure",
                            beatsPerMeasure
                        )

                        partitionObject.put(
                            "notes",
                            notesArray
                        )

                        // ======================================================
                        // SAVE APP STORAGE
                        // ======================================================

                        context.openFileOutput(
                            "$finalName.json",
                            Context.MODE_PRIVATE
                        ).use { output ->

                            output.write(
                                partitionObject
                                    .toString(4)
                                    .toByteArray()
                            )
                        }

                        // ======================================================
                        // EXPORT PHONE STORAGE
                        // ======================================================

                        saveLauncher.launch(
                            "$finalName.json"
                        )

                        showSaveDialog = false
                    }
                ) {

                    Text("Enregistrer")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showSaveDialog = false
                    }
                ) {

                    Text("Annuler")
                }
            }
        )
    }
}

// ======================================================
// SPACING
// ======================================================

fun getSpacing(type: NoteType): Float {

    return when (type) {

        NoteType.RONDE -> 240f
        NoteType.BLANCHE -> 180f
        NoteType.NOIRE -> 140f
        NoteType.CROCHE -> 110f
        NoteType.DOUBLE_CROCHE -> 90f
        NoteType.PAUSE -> 170f
        NoteType.DEMI_PAUSE -> 150f
        NoteType.SOUPIR -> 130f
        NoteType.DEMI_SOUPIR -> 120f
        NoteType.SILENCE_DOUBLE_CROCHE -> 100f
        NoteType.RONDE_POINTEE -> 260f
        NoteType.BLANCHE_POINTEE -> 200f
        NoteType.NOIRE_POINTEE -> 160f
        NoteType.CROCHE_POINTEE -> 130f
    }
}
