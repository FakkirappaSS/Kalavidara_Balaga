package com.example.kalavidarabalaga.ui.client

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import com.example.kalavidarabalaga.domain.model.Troupe
import com.example.kalavidarabalaga.ui.booking.BookingViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientTroupeDetailScreen(
    troupe: Troupe,
    onBack: () -> Unit,
    bookingViewModel: BookingViewModel,
    clientViewModel: com.example.kalavidarabalaga.ui.client.ClientHomeViewModel
) {
    val context = LocalContext.current
    
    // Load portfolio on launch
    LaunchedEffect(troupe.troupeId) {
        clientViewModel.incrementViewCount(troupe.troupeId)
        clientViewModel.loadTroupePortfolio(troupe.troupeId)
    }
    
    val portfolioItems by clientViewModel.selectedPortfolio.collectAsState()

    fun launchWhatsApp() {
        clientViewModel.incrementInquiryCount(troupe.troupeId)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=+91${troupe.contactPhone}&text=Hello%20${troupe.name},%20I%20would%20like%20to%20book%20your%20troupe!")
        try { context.startActivity(intent) } catch (e: Exception) { }
    }

    fun launchDialer() {
        clientViewModel.incrementInquiryCount(troupe.troupeId)
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${troupe.contactPhone}")
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(troupe.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(onClick = { launchDialer() }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Call, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Call")
                    }
                    Button(
                        onClick = { launchWhatsApp() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Text("WhatsApp")
                    }
                }
            }
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            item(span = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    if (troupe.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = troupe.photoUrl,
                            contentDescription = "Troupe Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 8.dp)
                        )
                    }
                    Text("Art Form: ${troupe.artForm}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Districts: ${troupe.districts.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("About Us", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(troupe.bio.ifEmpty { "No bio available." }, style = MaterialTheme.typography.bodyMedium)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Equipment List", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(troupe.equipmentList.joinToString(", ").ifEmpty { "None listed" }, style = MaterialTheme.typography.bodyMedium)
                    
                    if (troupe.videoLinks.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Performance Videos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        troupe.videoLinks.forEach { link ->
                            Text(
                                text = link,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(if(link.startsWith("http")) link else "https://$link"))
                                    context.startActivity(intent)
                                }.padding(vertical = 4.dp)
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Portfolio Gallery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            
            items(portfolioItems) { item ->
                var scale by remember { mutableStateOf(1f) }
                Card(elevation = CardDefaults.cardElevation(4.dp)) {
                    AsyncImage(
                        model = item.photoUrl,
                        contentDescription = item.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((150..250).random().dp) // Staggered effect
                            .transformable(
                                rememberTransformableState { zoomChange, _, _ ->
                                    scale = (scale * zoomChange).coerceIn(1f, 3f)
                                }
                            ).graphicsLayer(
                                scaleX = maxOf(1f, scale),
                                scaleY = maxOf(1f, scale)
                            )
                    )
                }
            }
        }
    }
}
