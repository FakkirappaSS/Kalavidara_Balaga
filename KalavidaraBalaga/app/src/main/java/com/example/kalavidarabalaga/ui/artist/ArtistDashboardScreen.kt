package com.example.kalavidarabalaga.ui.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kalavidarabalaga.ui.booking.ArtistBookingsScreen
import com.example.kalavidarabalaga.ui.booking.BookingViewModel

@Composable
fun ArtistDashboardScreen(
    artistViewModel: ArtistViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("My Profile", "Bookings", "Portfolio")

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
            0 -> TroupeRegistrationScreen(viewModel = artistViewModel, onLogout = onLogout)
            1 -> ArtistBookingsScreen(viewModel = bookingViewModel)
            2 -> com.example.kalavidarabalaga.ui.artist.PortfolioScreen(viewModel = artistViewModel)
        }
    }
}
