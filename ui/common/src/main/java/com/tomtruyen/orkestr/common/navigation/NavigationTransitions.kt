package com.tomtruyen.orkestr.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import kotlin.math.roundToInt

private const val NAVIGATION_SLIDE_DISTANCE_FRACTION = 0.08f

fun <S> AnimatedContentTransitionScope<S>.premiumForwardTransition(): ContentTransform {
    val distance = { fullWidth: Int -> (fullWidth * NAVIGATION_SLIDE_DISTANCE_FRACTION).roundToInt() }
    return (
        slideInHorizontally(
            animationSpec = tween(durationMillis = 320),
            initialOffsetX = distance,
        ) + fadeIn(
            animationSpec = tween(durationMillis = 220, delayMillis = 40),
        ) + scaleIn(
            animationSpec = tween(durationMillis = 320),
            initialScale = 0.985f,
        )
        ).togetherWith(
        slideOutHorizontally(
            animationSpec = tween(durationMillis = 260),
            targetOffsetX = { fullWidth -> -(distance(fullWidth) / 2) },
        ) + fadeOut(
            animationSpec = tween(durationMillis = 180),
        ) + scaleOut(
            animationSpec = tween(durationMillis = 260),
            targetScale = 0.992f,
        ),
    )
}

fun <S> AnimatedContentTransitionScope<S>.premiumBackwardTransition(): ContentTransform {
    val distance = { fullWidth: Int -> (fullWidth * NAVIGATION_SLIDE_DISTANCE_FRACTION).roundToInt() }
    return (
        slideInHorizontally(
            animationSpec = tween(durationMillis = 320),
            initialOffsetX = { fullWidth -> -distance(fullWidth) / 2 },
        ) + fadeIn(
            animationSpec = tween(durationMillis = 220, delayMillis = 40),
        ) + scaleIn(
            animationSpec = tween(durationMillis = 320),
            initialScale = 0.992f,
        )
        ).togetherWith(
        slideOutHorizontally(
            animationSpec = tween(durationMillis = 260),
            targetOffsetX = distance,
        ) + fadeOut(
            animationSpec = tween(durationMillis = 180),
        ) + scaleOut(
            animationSpec = tween(durationMillis = 260),
            targetScale = 0.985f,
        ),
    )
}
