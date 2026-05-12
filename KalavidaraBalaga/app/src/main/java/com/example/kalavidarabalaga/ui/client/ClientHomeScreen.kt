package com.example.kalavidarabalaga.ui.client

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kalavidarabalaga.domain.model.Troupe
import com.example.kalavidarabalaga.ui.booking.BookingRequestDialog
import com.example.kalavidarabalaga.ui.booking.BookingViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    viewModel: ClientHomeViewModel,
    bookingViewModel: BookingViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Discover", "My Bookings")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> ClientDiscoverScreen(viewModel, bookingViewModel, onLogout)
            1 -> com.example.kalavidarabalaga.ui.booking.ClientBookingsScreen(viewModel = bookingViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDiscoverScreen(
    viewModel: ClientHomeViewModel,
    bookingViewModel: BookingViewModel,
    onLogout: () -> Unit
) {
    val troupes by viewModel.troupes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchDistrict by viewModel.searchDistrict.collectAsState()
    val searchArtForm by viewModel.searchArtForm.collectAsState()

    var selectedTroupeId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Troupes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Fields
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchDistrict,
                    onValueChange = { viewModel.updateDistrictFilter(it) },
                    label = { Text("District") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = searchArtForm,
                    onValueChange = { viewModel.updateArtFormFilter(it) },
                    label = { Text("Art Form") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Results List
            if (isLoading) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) {
                        TroupeSkeletonCard()
                    }
                }
            } else if (troupes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No troupes found matching your criteria.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(troupes) { troupe ->
                        TroupeCard(
                            troupe = troupe,
                            onClick = { selectedTroupeId = troupe.troupeId }
                        )
                    }
                }
            }
        }
        
        selectedTroupeId?.let { troupeId ->
            val selectedTroupe = troupes.find { it.troupeId == troupeId }
            if (selectedTroupe != null) {
                ClientTroupeDetailScreen(
                    troupe = selectedTroupe,
                    onBack = { selectedTroupeId = null },
                    bookingViewModel = bookingViewModel,
                    clientViewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun TroupeCard(troupe: Troupe, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (troupe.photoUrl.isNotEmpty()) {
                AsyncImage(
                    model = troupe.photoUrl,
                    contentDescription = "Troupe Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Photo", style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                Text(
                    text = troupe.name.ifEmpty { "Unnamed Troupe" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = troupe.artForm.ifEmpty { "Various Arts" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = troupe.bio.ifEmpty { "No bio available." },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TroupeSkeletonCard() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = alpha))
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.height(20.dp).fillMaxWidth(0.7f).background(Color.Gray.copy(alpha = alpha)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.4f).background(Color.Gray.copy(alpha = alpha)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(14.dp).fillMaxWidth().background(Color.Gray.copy(alpha = alpha)))
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.8f).background(Color.Gray.copy(alpha = alpha)))
            }
        }
    }
}
