package com.example.a30daysapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.a30daysapp.ui.theme.DataSource
import com.example.a30daysapp.ui.theme.Destination
import com.example.a30daysapp.ui.theme._30DaysAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _30DaysAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("🌍 30 Days of Travel") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(DataSource.destinations) { destination ->
                            DestinationCard(destination)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationCard(destination: Destination) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Day ${destination.day}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = destination.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = destination.country,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(destination.imageRes),
                contentDescription = destination.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = destination.description,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}