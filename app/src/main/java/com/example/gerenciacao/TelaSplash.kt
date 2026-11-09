package com.example.gerenciacao.ui.telas

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gerenciacao.R
import com.example.gerenciacao.ui.Cores
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash do app em duas etapas:
 *  1) fundo laranja com o logo (continuação da splash do sistema, res/values/splash.xml);
 *  2) fundo creme com o logo laranja e o nome "GerenciaCão".
 * Ao terminar, chama [onConcluir] (vai para o Login).
 */
@Composable
fun TelaSplash(onConcluir: () -> Unit) {
    var etapa by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1300) // tempo na etapa 1
        etapa = 1
        delay(1900) // tempo na etapa 2
        onConcluir()
    }

    Crossfade(targetState = etapa, animationSpec = tween(500), label = "splash") { atual ->
        if (atual == 0) {
            EtapaSplash(fundo = Cores.Laranja, logo = R.drawable.logo_splash, largura = 182.dp)
        } else {
            // O PNG da etapa 2 tem o mesmo fundo creme da tela, então a emenda não aparece
            EtapaSplash(fundo = Color(0xFFFFF8EE), logo = R.drawable.logo_splash2, largura = 247.dp)
        }
    }
}

@Composable
private fun EtapaSplash(fundo: Color, @DrawableRes logo: Int, largura: Dp) {
    val alfa = remember { Animatable(0f) }
    val escala = remember { Animatable(0.88f) }

    LaunchedEffect(Unit) {
        launch { alfa.animateTo(1f, tween(durationMillis = 600)) }
        launch { escala.animateTo(1f, tween(durationMillis = 600, easing = FastOutSlowInEasing)) }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(fundo),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(logo),
            contentDescription = "GerenciaCão",
            modifier = Modifier
                .width(largura)
                .graphicsLayer {
                    this.alpha = alfa.value
                    scaleX = escala.value
                    scaleY = escala.value
                },
        )
    }
}
