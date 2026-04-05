package com.tomtruyen.orkestr.common.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalNavigationSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope> {
    error("Navigation shared transition scope was not provided")
}
