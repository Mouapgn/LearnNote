package com.example.learnnote.cours

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnnote.R

class RhythmSilenceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                RhythmSilenceScreen()
            }
        }
    }
}

// =========================================
// SCREEN
// =========================================

@Composable
fun RhythmSilenceScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4EC))
            .verticalScroll(rememberScrollState())
    ) {

        // =====================================
        // IMAGE PRINCIPALE
        // =====================================

        Image(
            painter = painterResource(id = R.drawable.silence_solfege),
            contentDescription = "Les silences en musique",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        // =====================================
        // COMPARAISON NOTES / SILENCES
        // =====================================

        NotesComparisonSection()
    }
}

// =========================================
// SECTION COMPARAISON
// =========================================

@Composable
fun NotesComparisonSection() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            )
    ) {

        Text(
            text = "Comparaison avec les notes",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF2EEE4))
                .padding(18.dp)
        ) {

            ComparisonRow(
                note = "Ronde",
                silence = "Pause",
                duration = "4 temps",
                noteSymbol = "𝅝",
                silenceSymbol = "▬"
            )

            ComparisonRow(
                note = "Blanche",
                silence = "Demi-pause",
                duration = "2 temps",
                noteSymbol = "𝅗𝅥",
                silenceSymbol = "▬"
            )

            ComparisonRow(
                note = "Noire",
                silence = "Soupir",
                duration = "1 temps",
                noteSymbol = "♩",
                silenceSymbol = "𝄽"
            )

            ComparisonRow(
                note = "Croche",
                silence = "Demi-soupir",
                duration = "1/2 temps",
                noteSymbol = "♪",
                silenceSymbol = "𝄾"
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// =========================================
// LIGNE COMPARAISON
// =========================================

@Composable
fun ComparisonRow(
    note: String,
    silence: String,
    duration: String,
    noteSymbol: String,
    silenceSymbol: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // =====================================
        // NOTE
        // =====================================

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = noteSymbol,
                fontSize = 36.sp,
                color = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // =====================================
        // ÉGAL
        // =====================================

        Text(
            text = "=",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        // =====================================
        // SILENCE
        // =====================================

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = silenceSymbol,
                fontSize = 36.sp,
                color = Color(0xFF8E44AD)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = silence,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        // =====================================
        // DURÉE
        // =====================================

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = duration,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
        }
    }
}