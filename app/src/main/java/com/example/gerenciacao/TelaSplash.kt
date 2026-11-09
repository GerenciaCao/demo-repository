package com.example.gerenciacao.ui.telas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gerenciacao.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash do app (tela única do protótipo): fundo laranja que clareia para um creme na parte de baixo,
 * com o logo (patinha + casinha) e o nome "GerenciaCão" no centro.
 * O logo aparece com fade e leve zoom e, depois de ~2,4 s, chama [onConcluir] (vai para o Login).
 * O laranja do topo é o mesmo da splash do sistema (res/values/splash.xml), então a troca é contínua.
 */
@Composable
fun TelaSplash(onConcluir: () -> Unit) {
    val alfa = remember { Animatable(0f) }
    val escala = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        launch { alfa.animateTo(1f, tween(durationMillis = 700)) }
        launch { escala.animateTo(1f, tween(durationMillis = 700, easing = FastOutSlowInEasing)) }
        delay(2400) // tempo total na tela
        onConcluir()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.00f to Color(0xFFFFAD31),
                    0.70f to Color(0xFFFFAD31),
                    0.96f to Color(0xFFFFE6A7),
                    1.00f to Color(0xFFFFE6A7),
                ),
            ),
    ) {
        Image(
            painter = painterResource(R.drawable.splash_logo),
            contentDescription = "GerenciaCão",
            modifier = Modifier
                .align(BiasAlignment(0f, 0.06f)) // logo+nome ficam um pouco abaixo do centro, como no protótipo
                .width(247.dp)
                .graphicsLayer {
                    this.alpha = alfa.value
                    scaleX = escala.value
                    scaleY = escala.value
                },
        )
    }
}
