package com.example.learnnote.partitions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.InputStream

class PartitionsActivity : ComponentActivity() {

    private val partitions =
        mutableStateListOf<PartitionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        loadPartitions()

        val importLauncher =
            registerForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->

                uri?.let {

                    try {

                        val inputStream: InputStream? =
                            contentResolver.openInputStream(uri)

                        val originalName =

                            uri.lastPathSegment
                                ?.substringAfterLast("/")
                                ?.substringBeforeLast(".")

                        val safeName =
                            originalName ?: "partition"

                        val fileName =
                            "$safeName.json"

                        val outputFile =
                            File(filesDir, fileName)

                        inputStream?.use { input ->

                            outputFile.outputStream().use { output ->

                                input.copyTo(output)
                            }
                        }

                        loadPartitions()



                    } catch (e: Exception) {

                        Toast
                            .makeText(
                                this,
                                "Erreur importation",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            }

        setContent {

            MaterialTheme {

                PartitionsScreen(

                    partitions = partitions,

                    onCreatePartition = {

                        startActivity(
                            Intent(
                                this,
                                CreerPartitionActivity::class.java
                            )
                        )
                    },

                    onImportPartition = {

                        importLauncher.launch(
                            arrayOf("application/json")
                        )
                    },

                    onReadPartition = { partition ->

                        startActivity(

                            Intent(
                                this,
                                LecturePartitionActivity::class.java
                            ).apply {

                                putExtra(
                                    "partition",
                                    partition
                                )
                            }
                        )
                    },
                    onRenamePartition = { oldName, newName ->

                        val oldFile =
                            File(
                                filesDir,
                                "$oldName.json"
                            )

                        val newFile =
                            File(
                                filesDir,
                                "$newName.json"
                            )

                        if (oldFile.exists()) {

                            oldFile.renameTo(newFile)
                        }

                        loadPartitions()

                    },

                    onDeletePartition = { partition ->

                        val file =
                            File(
                                filesDir,
                                "$partition.json"
                            )

                        if (file.exists()) {

                            file.delete()
                        }

                        loadPartitions()

                    }
                )
            }
        }
    }

    override fun onResume() {

        super.onResume()

        loadPartitions()
    }

    private fun loadPartitions() {

        partitions.clear()

        val files = filesDir.listFiles()

        files?.forEach { file ->

            if (file.extension == "json") {

                partitions.add(
                    PartitionItem(
                        title = file.nameWithoutExtension
                    )
                )
            }
        }
    }
}

// ======================================================
// DATA
// ======================================================

data class PartitionItem(
    val title: String
)

// ======================================================
// SCREEN
// ======================================================

@Composable
fun PartitionsScreen(
    partitions: List<PartitionItem>,
    onCreatePartition: () -> Unit,
    onImportPartition: () -> Unit,
    onReadPartition: (String) -> Unit,
    onRenamePartition: (String, String) -> Unit,
    onDeletePartition: (String) -> Unit
) {

    val infiniteTransition =
        rememberInfiniteTransition(label = "")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.24f,

        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse
        ),

        label = ""
    )

    var selectedPartition by remember {
        mutableStateOf<PartitionItem?>(null)
    }

    var showOptionsDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showRenameDialog by remember {
        mutableStateOf(false)
    }

    var renameText by remember {
        mutableStateOf("")
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

        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-70).dp, y = (-20).dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 170.dp, y = 620.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD8C7F1).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {

            Spacer(modifier = Modifier.height(42.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Partitions",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3A3A3A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(95.dp)
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC9D8CC),
                                    Color(0xFFDCCDF4)
                                )
                            ),
                            RoundedCornerShape(50)
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            PartitionActionButton(
                title = "Créer partition",
                color = Color(0xFF8DB7A8),
                onClick = onCreatePartition
            )

            Spacer(modifier = Modifier.height(14.dp))

            PartitionActionButton(
                title = "Importer partition",
                color = Color(0xFFA5BCE8),
                onClick = onImportPartition
            )

            Spacer(modifier = Modifier.height(28.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),

                verticalArrangement =
                    Arrangement.spacedBy(18.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(18.dp),

                modifier = Modifier.fillMaxSize()
            ) {

                if (partitions.isEmpty()) {

                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),

                            shape = RoundedCornerShape(30.dp),

                            color = Color.White.copy(alpha = 0.78f),

                            shadowElevation = 3.dp
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 24.dp,
                                        vertical = 42.dp
                                    ),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "♫",
                                    fontSize = 46.sp,
                                    color = Color(0xFFD8C7F1)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Aucune partition",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF555555)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text =
                                        "Crée ou importe\n ta première partition",

                                    fontSize = 15.sp,

                                    lineHeight = 22.sp,

                                    textAlign = TextAlign.Center,

                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }
                }

                else {

                    items(partitions) { partition ->

                        PartitionCard(

                            title = partition.title,

                            onClick = {

                                selectedPartition = partition
                                showOptionsDialog = true
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }

    // ======================================================
    // OPTIONS DIALOG
    // ======================================================

    if (showOptionsDialog && selectedPartition != null) {

        AlertDialog(

            onDismissRequest = {

                showOptionsDialog = false
            },

            title = {

                Text(selectedPartition!!.title)
            },

            text = {

                Column {

                    TextButton(

                        onClick = {

                            onReadPartition(
                                selectedPartition!!.title
                            )

                            showOptionsDialog = false
                        }
                    ) {

                        Text("Lire")
                    }

                    TextButton(

                        onClick = {

                            renameText =
                                selectedPartition!!.title

                            showOptionsDialog = false
                            showRenameDialog = true
                        }
                    ) {

                        Text("Renommer")
                    }

                    TextButton(

                        onClick = {

                            showOptionsDialog = false
                            showDeleteDialog = true
                        }
                    ) {

                        Text(
                            "Supprimer",
                            color = Color.Red
                        )
                    }
                }
            },

            confirmButton = {}
        )
    }

    // ======================================================
    // RENAME DIALOG
    // ======================================================

    if (showRenameDialog && selectedPartition != null) {

        AlertDialog(

            onDismissRequest = {

                showRenameDialog = false
            },

            title = {

                Text("Renommer la partition")
            },

            text = {

                OutlinedTextField(

                    value = renameText,

                    onValueChange = {

                        renameText = it
                    },

                    label = {

                        Text("Nom")
                    }
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        onRenamePartition(
                            selectedPartition!!.title,
                            renameText
                        )

                        showRenameDialog = false
                    }
                ) {

                    Text("Renommer")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showRenameDialog = false
                    }
                ) {

                    Text("Annuler")
                }
            }
        )
    }

    // ======================================================
    // DELETE DIALOG
    // ======================================================

    if (showDeleteDialog && selectedPartition != null) {

        AlertDialog(

            onDismissRequest = {

                showDeleteDialog = false
            },

            title = {

                Text("Supprimer la partition")
            },

            text = {

                Text(
                    "Confirmer la suppression de \"${selectedPartition!!.title}\" ?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        onDeletePartition(
                            selectedPartition!!.title
                        )

                        showDeleteDialog = false
                    }
                ) {

                    Text("Supprimer")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDeleteDialog = false
                    }
                ) {

                    Text("Annuler")
                }
            }
        )
    }
}

// ======================================================
// ACTION BUTTON
// ======================================================

@Composable
fun PartitionActionButton(
    title: String,
    color: Color,
    onClick: () -> Unit
) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue =
            if (pressed) 0.98f
            else 1f,

        animationSpec = tween(100),

        label = ""
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .clickable {

                pressed = true

                onClick()

                pressed = false
            },

        shape = RoundedCornerShape(26.dp),

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
                    RoundedCornerShape(26.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(26.dp)
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A3A3A)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .width(7.dp)
                    .height(42.dp)
                    .background(
                        color,
                        RoundedCornerShape(100.dp)
                    )
            )
        }
    }
}

// ======================================================
// PARTITION CARD
// ======================================================

@Composable
fun PartitionCard(
    title: String,
    onClick: () -> Unit
) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue =
            if (pressed) 0.97f
            else 1f,

        animationSpec = tween(100),

        label = ""
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
            .scale(scale)
            .clickable {

                pressed = true

                onClick()

                pressed = false
            },

        shape = RoundedCornerShape(24.dp),

        color = Color.Transparent
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.92f),
                            Color(0xFFF6F2EC)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(14.dp)
        ) {

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF444444),
                textAlign = TextAlign.Center,

                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}