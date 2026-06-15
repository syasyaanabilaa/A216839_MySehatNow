package com.example.a216839_wan_project2

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_project2.data.firebase.SavedTip
import com.example.a216839_wan_project2.viewmodel.CommunityTipsViewModel
import com.example.a216839_wan_project2.viewmodel.GeneratedTip
import java.text.SimpleDateFormat
import java.util.*

// ── Design tokens ─────────────────────────────────────────────────────────────
private object HealthTokens {
    val NavyDeep      = Color(0xFF080D1C)
    val NavyMid       = Color(0xFF111827)
    val VioletPrimary = Color(0xFF6C47FF)
    val VioletSoft    = Color(0xFFEDE9FF)
    val PageBg        = Color(0xFFF4F6FB)
    val CardBg        = Color.White
    val TextPrimary   = Color(0xFF111827)
    val TextSecondary = Color(0xFF6B7280)
    val TextMuted     = Color(0xFFADB5BD)
    val Divider       = Color(0xFFE5E7EB)
    val DangerSurface = Color(0xFFFFF1F1)
    val Danger        = Color(0xFFDC2626)
    val SavedChip     = Color(0xFF16A34A)
    val SavedSurface  = Color(0xFFDCFCE7)
}

// ── Category styles ───────────────────────────────────────────────────────────
private data class CategoryStyle(
    val accent : Color,
    val surface: Color,
    val label  : String
)

private fun categoryStyle(category: String): CategoryStyle = when (category) {
    "Nutrition"     -> CategoryStyle(Color(0xFF16A34A), Color(0xFFDCFCE7), "Nutrition")
    "Exercise"      -> CategoryStyle(Color(0xFF2563EB), Color(0xFFDBEAFE), "Exercise")
    "Mental Health" -> CategoryStyle(Color(0xFF7C3AED), Color(0xFFEDE9FE), "Mental Health")
    "Sleep"         -> CategoryStyle(Color(0xFF0891B2), Color(0xFFCFFAFE), "Sleep")
    "Hygiene"       -> CategoryStyle(Color(0xFF0284C7), Color(0xFFE0F2FE), "Hygiene")
    else            -> CategoryStyle(Color(0xFFB45309), Color(0xFFFEF3C7), category)
}

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTipsScreen(
    viewModel: CommunityTipsViewModel,
    onBack   : () -> Unit
) {
    val searchQuery   by viewModel.searchQuery.collectAsState()
    val isSearching   by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val hasSearched   by viewModel.hasSearched.collectAsState()
    val savedTips     by viewModel.savedTips.collectAsState()
    val message       by viewModel.message.collectAsState()
    val savingTips    by viewModel.savingTips.collectAsState()
    val savedTipTexts by viewModel.savedTipTexts.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager      = LocalFocusManager.current

    LaunchedEffect(message) {
        message?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessage() }
    }

    Scaffold(
        containerColor = HealthTokens.PageBg,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData   = data,
                    shape          = RoundedCornerShape(12.dp),
                    containerColor = HealthTokens.NavyDeep,
                    contentColor   = Color.White,
                    modifier       = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── HERO HEADER ───────────────────────────────────────────────────
            item {
                HeroHeader(
                    searchQuery   = searchQuery,
                    isSearching   = isSearching,
                    onBack        = onBack,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onSearch      = {
                        focusManager.clearFocus()
                        viewModel.searchTipsWithAI(searchQuery)
                    },
                    onClear = {
                        focusManager.clearFocus()
                        viewModel.clearSearch()
                    }
                )
            }

            // ── RESULTS AREA ──────────────────────────────────────────────────
            when {
                isSearching -> {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SearchingIndicator()
                    }
                }

                hasSearched && searchResults.isEmpty() -> {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        EmptyResultsCard()
                    }
                }

                searchResults.isNotEmpty() -> {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        ResultsHeader(count = searchResults.size, query = searchQuery)
                    }
                    items(searchResults) { tip ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            HealthTipCard(
                                tip      = tip,
                                isSaving = savingTips.contains(tip.tip),
                                isSaved  = savedTipTexts.contains(tip.tip),
                                onSave   = { viewModel.saveTip(tip) }
                            )
                        }
                    }
                }

                else -> {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SearchHintCard()
                        }
                    }
                }
            }

            // ── SAVED TIPS ────────────────────────────────────────────────────
            if (savedTips.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SavedTipsHeader(count = savedTips.size)
                }
                items(savedTips, key = { it.id }) { saved ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        SavedTipRow(
                            saved    = saved,
                            onDelete = { viewModel.deleteSavedTip(saved.id) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ── HERO HEADER ───────────────────────────────────────────────────────────────
@Composable
private fun HeroHeader(
    searchQuery  : String,
    isSearching  : Boolean,
    onBack       : () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch     : () -> Unit,
    onClear      : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(HealthTokens.NavyDeep, Color(0xFF1A1040))
                )
            )
    ) {
        // Subtle dot-grid texture overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            HealthTokens.VioletPrimary.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            // Nav row
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = Color.White,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Model badge
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = Color.White.copy(alpha = 0.10f)
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4ADE80))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text       = "Groq · Llama 3",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color.White.copy(alpha = 0.80f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Headline
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text       = "AI Health Tips",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text      = "Ask anything about your wellbeing",
                    fontSize  = 13.sp,
                    color     = Color.White.copy(alpha = 0.55f),
                    letterSpacing = 0.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search bar lifted into header
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                HeroSearchBar(
                    query         = searchQuery,
                    isSearching   = isSearching,
                    onQueryChange = onQueryChange,
                    onSearch      = onSearch,
                    onClear       = onClear
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ── HERO SEARCH BAR ───────────────────────────────────────────────────────────
@Composable
private fun HeroSearchBar(
    query        : String,
    isSearching  : Boolean,
    onQueryChange: (String) -> Unit,
    onSearch     : () -> Unit,
    onClear      : () -> Unit
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White,
        shadowElevation = 12.dp,
        tonalElevation  = 0.dp
    ) {
        Row(
            modifier          = Modifier.padding(start = 14.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.5.dp,
                    color       = HealthTokens.VioletPrimary
                )
            } else {
                Icon(
                    imageVector        = Icons.Filled.Search,
                    contentDescription = null,
                    tint               = HealthTokens.VioletPrimary,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            TextField(
                value         = query,
                onValueChange = onQueryChange,
                placeholder   = {
                    Text(
                        text     = "e.g. better sleep, reduce stress…",
                        fontSize = 14.sp,
                        color    = HealthTokens.TextMuted
                    )
                },
                modifier   = Modifier.weight(1f),
                singleLine = true,
                colors     = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle       = LocalTextStyle.current.copy(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color      = HealthTokens.TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )

            AnimatedVisibility(visible = query.isNotEmpty()) {
                IconButton(onClick = onClear, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector        = Icons.Filled.Clear,
                        contentDescription = "Clear",
                        tint               = HealthTokens.TextMuted,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = query.isNotBlank() && !isSearching) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthTokens.VioletPrimary)
                ) {
                    TextButton(
                        onClick        = onSearch,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text       = "Search",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ── RESULTS HEADER ────────────────────────────────────────────────────────────
@Composable
private fun ResultsHeader(count: Int, query: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = "$count tips",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = HealthTokens.TextPrimary
        )
        Text(
            text     = " for \"$query\"",
            fontSize = 13.sp,
            color    = HealthTokens.TextSecondary
        )
    }
}

// ── SAVED TIPS HEADER ─────────────────────────────────────────────────────────
@Composable
private fun SavedTipsHeader(count: Int) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HealthTokens.VioletSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.Bookmark,
                contentDescription = null,
                tint               = HealthTokens.VioletPrimary,
                modifier           = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text       = "Saved Tips",
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = HealthTokens.TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(99.dp),
            color = HealthTokens.VioletSoft
        ) {
            Text(
                text       = "$count",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Bold,
                color      = HealthTokens.VioletPrimary,
                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

// ── HINT CARD ─────────────────────────────────────────────────────────────────
@Composable
private fun SearchHintCard() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = HealthTokens.CardBg)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glyph
            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(HealthTokens.VioletPrimary, Color(0xFF1976D2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Search,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "What do you want to improve?",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                textAlign  = TextAlign.Center,
                color      = HealthTokens.TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text       = "Type any health topic and get 5 personalised tips powered.",
                fontSize   = 13.sp,
                textAlign  = TextAlign.Center,
                color      = HealthTokens.TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = HealthTokens.Divider, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "Try asking about",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium,
                color      = HealthTokens.TextMuted,
                modifier   = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val suggestions = listOf(
                "Sleep better", "Reduce stress", "Boost energy",
                "Improve focus", "Stay hydrated", "Healthy eating"
            )
            // Two rows of chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestions.chunked(3).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { label ->
                            Surface(
                                shape    = RoundedCornerShape(99.dp),
                                color    = HealthTokens.VioletSoft,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text       = label,
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color      = HealthTokens.VioletPrimary,
                                    textAlign  = TextAlign.Center,
                                    modifier   = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SEARCHING INDICATOR ───────────────────────────────────────────────────────
@Composable
private fun SearchingIndicator() {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors    = CardDefaults.cardColors(containerColor = HealthTokens.CardBg)
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier         = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(HealthTokens.VioletSoft),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(28.dp),
                        strokeWidth = 3.dp,
                        color       = HealthTokens.VioletPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text       = "Generating tips…",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = HealthTokens.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Groq Llama 3 is crafting your advice",
                    fontSize = 13.sp,
                    color    = HealthTokens.TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    modifier   = Modifier
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(99.dp))
                        .height(3.dp),
                    color      = HealthTokens.VioletPrimary,
                    trackColor = HealthTokens.VioletSoft
                )
            }
        }
    }
}

// ── EMPTY RESULTS ─────────────────────────────────────────────────────────────
@Composable
private fun EmptyResultsCard() {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors    = CardDefaults.cardColors(containerColor = HealthTokens.CardBg)
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = "No tips found",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = HealthTokens.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text      = "Try a different topic or check your internet connection.",
                    textAlign = TextAlign.Center,
                    fontSize  = 13.sp,
                    color     = HealthTokens.TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ── HEALTH TIP CARD ───────────────────────────────────────────────────────────
@Composable
private fun HealthTipCard(
    tip     : GeneratedTip,
    isSaving: Boolean,
    isSaved : Boolean,
    onSave  : () -> Unit
) {
    val style = categoryStyle(tip.category)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = HealthTokens.CardBg)
    ) {
        Column {
            // Top accent bar with category label inside
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(style.accent, style.accent.copy(alpha = 0.60f))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text       = style.label,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text     = "Groq · Llama 3",
                            fontSize = 10.sp,
                            color    = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Tip text with left-border
                Row {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .heightIn(min = 24.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(99.dp))
                            .background(style.accent.copy(alpha = 0.25f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text       = tip.tip,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color      = HealthTokens.TextPrimary,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = HealthTokens.Divider, thickness = 0.5.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // Save button row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isSaved) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HealthTokens.SavedSurface
                        ) {
                            Row(
                                modifier          = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector        = Icons.Filled.Bookmark,
                                    contentDescription = "Saved",
                                    tint               = HealthTokens.SavedChip,
                                    modifier           = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text       = "Saved",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = HealthTokens.SavedChip
                                )
                            }
                        }
                    } else {
                        FilledTonalButton(
                            onClick        = onSave,
                            enabled        = !isSaving,
                            modifier       = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            shape          = RoundedCornerShape(10.dp),
                            colors         = ButtonDefaults.filledTonalButtonColors(
                                containerColor = style.surface,
                                contentColor   = style.accent
                            )
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color       = style.accent
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Saving…", fontSize = 12.sp, color = style.accent)
                            } else {
                                Icon(
                                    imageVector        = Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Save",
                                    modifier           = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text       = "Save Tip",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SAVED TIP ROW ─────────────────────────────────────────────────────────────
@Composable
private fun SavedTipRow(saved: SavedTip, onDelete: () -> Unit) {
    val malaysiaTz = remember { TimeZone.getTimeZone("Asia/Kuala_Lumpur") }
    val timeStr = remember(saved.savedAt) {
        SimpleDateFormat("d MMM · h:mm a", Locale.getDefault())
            .apply { timeZone = malaysiaTz }
            .format(Date(saved.savedAt))
    }
    val style = categoryStyle(saved.category)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = HealthTokens.CardBg)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category color stripe
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(IntrinsicSize.Max)
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(style.accent)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = style.surface
                    ) {
                        Text(
                            text       = style.label,
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color      = style.accent,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text     = timeStr,
                        fontSize = 10.sp,
                        color    = HealthTokens.TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text       = saved.tip,
                    fontSize   = 13.sp,
                    color      = HealthTokens.TextPrimary,
                    maxLines   = 3,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier         = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(HealthTokens.DangerSurface),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint               = HealthTokens.Danger,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
