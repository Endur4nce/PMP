package com.example.calc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.*

@Composable
fun GraphScreen(onBackClick: () -> Unit) {
    var functionText by remember { mutableStateOf(TextFieldValue("sin(x) + cos(x)")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var graphError by remember { mutableStateOf(false) }
    var showKeyboard by remember { mutableStateOf(false) }
    var validFunction by remember { mutableStateOf(false) }

    // Функция для проверки валидности выражения
    fun validateFunction(expr: String): Boolean {
        return try {
            if (expr.isEmpty()) return false
            // Быстрая проверка баланса скобок
            if (expr.count { it == '(' } != expr.count { it == ')' }) return false

            // Проверка, что выражение можно построить
            ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", 0.0)
                .validate()
            true
        } catch (e: Exception) {
            false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            // Поле ввода функции
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = functionText,
                    onValueChange = {
                        functionText = it
                        graphError = false
                        errorMessage = null
                        validFunction = validateFunction(it.text)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            if (functionText.text.isEmpty()) {
                                Text(
                                    "Введите функцию (например: sin(x)*x^2)",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка построения графика
            Button(
                onClick = {
                    if (validFunction) {
                        graphError = false
                    } else {
                        graphError = true
                        errorMessage = "Некорректная функция"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C),
                    contentColor = Color.White,
                ),
                enabled = validFunction
            ) {
                Text(
                    text = "Построить график",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Отображение графика или ошибки
            if (graphError) {
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Невозможно построить график",
                        color = Color.Red
                    )
                }
            } else if (validFunction) {
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                ) {
                    DrawGraph(functionText.text)
                }
            } else {
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Введите корректную функцию",
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка выхода
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C),
                    contentColor = Color.White,
                )
            ) {
                Text(
                    text = "Выход",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MathKeyboard(onSymbolClick: (String) -> Unit) {
    val mathSymbols = listOf(
        "sin(", "cos(", "tan(", "sqrt(", "log(", "exp(",
        "x", "^", "(", ")", "+", "-", "*", "/", "0", "1",
        "2", "3", "4", "5", "6", "7", "8", "9", "."
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mathSymbols.forEach { symbol ->
            Button(
                onClick = { onSymbolClick(symbol) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C),
                    contentColor = Color.White
                ),
                modifier = Modifier.sizeIn(minWidth = 40.dp)
            ) {
                Text(symbol)
            }
        }
    }
}

@Composable
fun DrawGraph(function: String) {
    if (function.isBlank()) return
    val xRange = -10f..10f
    val step = 0.1f
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2
        drawRect(color = Color.Black)
        for (i in 0 until width.toInt() step 30) {
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(i.toFloat(), 0f),
                end = Offset(i.toFloat(), height),
                strokeWidth = 1f
            )
        }
        for (i in 0 until height.toInt() step 20) {
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, i.toFloat()),
                end = Offset(width, i.toFloat()),
                strokeWidth = 1f
            )
        }
        // Ось X (белая линия с подписями)
        drawLine(
            color = Color.Black,
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 2f
        )
        // Ось Y (белая линия с подписями)
        drawLine(
            color = Color.Black,
            start = Offset(centerX, 0f),
            end = Offset(centerX, height),
            strokeWidth = 2f
        )
        // Подписи осей
        val xLabels = (-10..10 step 2)
        val yLabels = (-5..5 step 1)
        // Подписи оси X
        xLabels.forEach { x ->
            val xPos = centerX + x * (width / 20f)
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    x.toString(),
                    xPos - 10,
                    centerY + 30,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 24f
                    }
                )
            }
        }
        // Подписи оси Y
        yLabels.forEach { y ->
            if (y != 0) {
                val yPos = centerY - y * (height / 10f)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        y.toString(),
                        centerX + 10,
                        yPos + 10,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                        }
                    )
                }
            }
        }
        // Отрисовка графика
        val path = Path()
        var firstPoint = true

        for (x in xRange step step) {
            try {
                val y = evaluateFunctionSafe(function, x.toDouble())?.toFloat() ?: continue
                val xPixel = centerX + x * (width / 20f)
                val yPixel = centerY - y * (height / 10f)

                if (yPixel.isNaN() || yPixel.isInfinite()) continue

                if (firstPoint) {
                    path.moveTo(xPixel, yPixel)
                    firstPoint = false
                } else {
                    path.lineTo(xPixel, yPixel)
                }
            } catch (e: Exception) {
                firstPoint = true
            }
        }

        drawPath(
            path = path,
            color = Color(0xFFB71C1C),
            style = Stroke(width = 3f)
        )
    }
}

// Безопасная версия evaluateFunction
fun evaluateFunctionSafe(expression: String, x: Double): Double? {
    return try {
        ExpressionBuilder(expression)
            .variables("x")
            .build()
            .setVariable("x", x)
            .evaluate()
    } catch (e: Exception) {
        null
    }
}

infix fun ClosedRange<Float>.step(step: Float): Iterable<Float> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start) { previous ->
        if (previous == Float.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}