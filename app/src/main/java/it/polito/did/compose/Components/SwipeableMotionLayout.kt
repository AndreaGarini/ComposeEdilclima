package it.polito.did.compose.Components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.*
import kotlinx.coroutines.channels.Channel
import java.util.*

@OptIn(ExperimentalMotionApi::class)
@Composable
inline fun SwipeableMotionLayout(
    motionScene: MotionScene,
    constraintSetName: String? = null,
    targetProgress : Float,
    animationSpec: AnimationSpec<Float> = tween<Float>(),
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = 1,
    noinline finishedAnimationListener: (() -> Unit)? = null,
    crossinline content: @Composable (MotionLayoutScope.() -> Unit)
) {
    SwipeableMotionLayoutCore(
        motionScene = motionScene,
        constraintSetName = constraintSetName,
        targetProgress = targetProgress,
        animationSpec = animationSpec,
        debug = debug,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        finishedAnimationListener = finishedAnimationListener,
        content = content
    )
}

@OptIn(ExperimentalMotionApi::class)
@PublishedApi
@Composable
internal inline fun SwipeableMotionLayoutCore(
    motionScene: MotionScene,
    constraintSetName: String? = null,
    targetProgress : Float,
    animationSpec: AnimationSpec<Float> = tween<Float>(),
    debug: EnumSet<MotionLayoutDebugFlags> = EnumSet.of(MotionLayoutDebugFlags.NONE),
    modifier: Modifier = Modifier,
    optimizationLevel: Int = 1,
    noinline finishedAnimationListener: (() -> Unit)? = null,
    crossinline content: @Composable (MotionLayoutScope.() -> Unit)
) {
    val needsUpdate = remember {
        mutableStateOf(0L)
    }
    motionScene.setUpdateFlag(needsUpdate)

    var usedDebugMode = debug
    if (motionScene.getForcedDrawDebug() != MotionLayoutDebugFlags.UNKNOWN) {
        usedDebugMode = EnumSet.of(motionScene.getForcedDrawDebug())
    }

    val transitionContent = remember(motionScene, needsUpdate.value) {
        motionScene.getTransition("default")
    }

    val transition: androidx.constraintlayout.compose.Transition? =
        transitionContent?.let { Transition(it) }

    val startId = transition?.getStartConstraintSetId() ?: "start"
    val endId = transition?.getEndConstraintSetId() ?: "end"

    val startContent = remember(motionScene, needsUpdate.value) {
        motionScene.getConstraintSet(startId) ?: motionScene.getConstraintSet(0)
    }
    val endContent = remember(motionScene, needsUpdate.value) {
        motionScene.getConstraintSet(endId) ?: motionScene.getConstraintSet(1)
    }

    val targetEndContent = remember(motionScene, constraintSetName) {
        constraintSetName?.let { motionScene.getConstraintSet(constraintSetName) }
    }

    if (startContent == null || endContent == null) {
        return
    }

    var start: ConstraintSet by remember(motionScene) { mutableStateOf(ConstraintSet(jsonContent = startContent)) }
    var end: ConstraintSet by remember(motionScene) { mutableStateOf(ConstraintSet(jsonContent = endContent)) }
    val targetConstraintSet = targetEndContent?.let { ConstraintSet(jsonContent = targetEndContent) }

    val progress = remember { Animatable(0f) }

    var animateToEnd by remember(motionScene) { mutableStateOf(true) }

    val channel = remember { Channel<ConstraintSet>(Channel.CONFLATED) }

    if (targetConstraintSet != null) {
        SideEffect {
            channel.trySend(targetConstraintSet)
        }

        LaunchedEffect(motionScene, channel, ) {
            for (constraints in channel) {
                val newConstraintSet = channel.tryReceive().getOrNull() ?: constraints
                val animTargetValue = if (animateToEnd) targetProgress else 0f
                val currentSet = if (animateToEnd) start else end
                if (newConstraintSet != currentSet) {
                    if (animateToEnd) {
                        end = newConstraintSet
                    } else {
                        start = newConstraintSet
                    }
                    progress.animateTo(animTargetValue, animationSpec)
                    animateToEnd = !animateToEnd

                    if (targetProgress==1f)
                    finishedAnimationListener?.invoke()
                }
            }
        }
    }

    val lastOutsideProgress = remember { mutableStateOf(0f) }
    val forcedProgress = motionScene.getForcedProgress()

    val currentProgress =
        if (!forcedProgress.isNaN() && lastOutsideProgress.value == progress.value) {
            forcedProgress
        } else {
            motionScene.resetForcedProgress()
            progress.value
        }

    lastOutsideProgress.value = progress.value

    MotionLayout(
        start = start,
        end = end,
        transition = transition,
        progress = currentProgress,
        debug = debug,
        informationReceiver = null,
        modifier = modifier,
        optimizationLevel = optimizationLevel,
        content = content
    )
}