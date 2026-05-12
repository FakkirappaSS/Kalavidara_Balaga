package com.example.kalavidarabalaga.ui.artist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kalavidarabalaga.domain.model.Troupe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroupeRegistrationScreen(
    viewModel: ArtistViewModel,
    onLogout: () -> Unit = {}
) {
    val troupe by viewModel.troupe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Troupe Registration") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Photo Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null || troupe.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = selectedImageUri ?: troupe.photoUrl,
                        contentDescription = "Troupe Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No Photo Selected")
                }
                
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                ) {
                    Text("Upload Photo")
                }
            }

            OutlinedTextField(
                value = troupe.name,
                onValueChange = { viewModel.updateTroupeInfo(troupe.copy(name = it)) },
                label = { Text("Troupe Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = troupe.artForm,
                onValueChange = { viewModel.updateTroupeInfo(troupe.copy(artForm = it)) },
                label = { Text("Art Form (e.g. Yakshagana)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = troupe.contactPhone,
                onValueChange = { viewModel.updateTroupeInfo(troupe.copy(contactPhone = it)) },
                label = { Text("Contact Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = troupe.districts.joinToString(", "),
                onValueChange = { 
                    viewModel.updateTroupeInfo(troupe.copy(districts = it.split(",").map { s -> s.trim() })) 
                },
                label = { Text("Service Districts (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = troupe.equipmentList.joinToString(", "),
                onValueChange = { 
                    viewModel.updateTroupeInfo(troupe.copy(equipmentList = it.split(",").map { s -> s.trim() })) 
                },
                label = { Text("Equipment/Instruments (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = troupe.videoLinks.joinToString(", "),
                onValueChange = { 
                    viewModel.updateTroupeInfo(troupe.copy(videoLinks = it.split(",").map { s -> s.trim() })) 
                },
                label = { Text("Video Links (comma separated URLs)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = troupe.bio,
                onValueChange = { viewModel.updateTroupeInfo(troupe.copy(bio = it)) },
                label = { Text("Troupe Bio") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            OutlinedButton(
                onClick = { viewModel.generateBio() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("✨ Auto-Generate Bio with AI")
            }

            Button(
                onClick = { viewModel.saveTroupeProfile(selectedImageUri) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Profile")
                }
            }

            val errorMessage by viewModel.errorMessage.collectAsState()
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = if (errorMessage!!.contains("Successfully")) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
