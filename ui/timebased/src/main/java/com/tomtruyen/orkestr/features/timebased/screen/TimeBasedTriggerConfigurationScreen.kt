package com.tomtruyen.orkestr.features.timebased.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.features.timebased.component.TimeBasedTriggerConfigurationForm

@Composable
fun TimeBasedTriggerConfigurationScreen(
    title: String,
    description: String,
    isBeta: Boolean,
    requiredMinSdk: Int?,
    chooseDifferentLabel: String?,
    saveLabel: String,
    errors: List<String>,
    config: TimeBasedTriggerConfig,
    onFieldChanged: (String, String) -> Unit,
    onSave: () -> Unit,
    onChooseDifferent: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Card {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(saveLabel)
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AutomationDefinitionHeaderCard(
                    title = title,
                    description = description,
                    isBeta = isBeta,
                    requiredMinSdk = requiredMinSdk,
                    chooseDifferentLabel = chooseDifferentLabel,
                    onChooseDifferent = onChooseDifferent,
                )
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        TimeBasedTriggerConfigurationForm(
                            config = config,
                            onFieldChanged = onFieldChanged,
                        )
                        if (errors.isNotEmpty()) {
                            ValidationCard(errors = errors)
                        }
                    }
                }
            }
        }
    }
}
