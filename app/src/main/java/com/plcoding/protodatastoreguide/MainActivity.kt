package com.plcoding.protodatastoreguide

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import com.plcoding.protodatastoreguide.ui.theme.ProtoDataStoreGuideTheme
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.launch

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProtoDataStoreGuideTheme {
                val appSettings = dataStore.data.collectAsState(
                    initial = AppSettings()
                ).value
                val scope = rememberCoroutineScope()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Select Language", modifier = Modifier.padding(bottom = 16.dp))

                    // Display language options
                    Language.values().forEach { language ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = language == appSettings.language,
                                onClick = {
                                    scope.launch {
                                        setLanguage(language)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = language.toString())
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Display known locations
                    val locationsDataList = appSettings.knownLocations
                    Text(
                        text = "PersistentList contains data:",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (locationsDataList.isNotEmpty()) {
                        Text(text = locationsDataList.joinToString { "(${it.lat}, ${it.lng})" })
                    } else {
                        Text(text = "PersistentList is currently empty")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Display theme settings
                    val themeSettingsDataMap = appSettings.themeSettingsMap
                    Text(
                        text = "PersistentMap contains data:",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (themeSettingsDataMap.isNotEmpty()) {
                        Text(text = themeSettingsDataMap.entries.joinToString { "${it.key}: ${it.value}" })
                    } else {
                        Text(text = "PersistentMap is currently empty")
                    }
                }
            }
        }
    }

    private suspend fun setLanguage(language: Language) {
        dataStore.updateData {
            it.copy(
                language = language,
                knownLocations = it.knownLocations.mutate {
                    it.clear()
                    it.add(Location(Math.random(), Math.random()))
                },
                themeSettingsMap = it.themeSettingsMap.mutate {
                    it["ProtoDSGuide"] = PersistentMapSettings(
                        "DoesItWork?", "MapSerialization", true
                    )
                }
            )
        }
    }
}
