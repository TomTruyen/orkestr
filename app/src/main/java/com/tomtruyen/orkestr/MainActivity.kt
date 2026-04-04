package com.tomtruyen.orkestr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.tomtruyen.orkestr.ui.automation.AutomationRulesRoute
import com.tomtruyen.orkestr.ui.automation.AutomationRulesViewModel
import com.tomtruyen.orkestr.ui.theme.OrkestrTheme
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AutomationRulesViewModel> {
        AutomationRulesViewModel.factory(
            repository = GlobalContext.get().get(),
            definitions = GlobalContext.get().get()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OrkestrTheme {
                AutomationRulesRoute(viewModel = viewModel)
            }
        }
    }
}
