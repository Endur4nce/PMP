package com.example.calc

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calc.ui.theme.CalcTheme
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Проверка минимальных требований к экрану
        if (!checkScreenRequirements()) {
            showUnsupportedScreenDialog()
            return
        }

        setContent {
            CalcTheme {
                val orientation = LocalConfiguration.current.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LandscapeLayout()
                } else {
                    PortraitLayout()
                }
            }
        }
    }

    private fun checkScreenRequirements(): Boolean {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels / metrics.density
        val height = metrics.heightPixels / metrics.density
        val screenSize = minOf(width, height)
        return screenSize >= 300f // Минимальный размер экрана 300dp
    }

    private fun showUnsupportedScreenDialog() {
        // Здесь можно показать диалог о неподдерживаемом устройстве
    }
}

@Composable
fun PortraitLayout() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        CalculatorScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@Composable
fun LandscapeLayout() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CalculatorScreen(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var currentInput by remember { mutableStateOf("0") }
    var history by remember { mutableStateOf(listOf<String>()) }
    var showHistory by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Ошибка") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(modifier = modifier) {
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            Column(modifier = Modifier.weight(2f)) {
                CalcButton(
                    text = if (showHistory) "Скрыть" else "Показать",
                    onClick = { showHistory = !showHistory },
                    modifier = Modifier.fillMaxWidth(),
                    buttonType = ButtonType.HISTORY
                )
                if (showHistory) {
                    HistoryList(history = history)
                }
            }
        }

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier.weight(2f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    OutlinedTextField(
                        value = currentInput,
                        onValueChange = { newText ->
                            if (newText.matches(Regex("^[\\d.+\\-*/^%()sqrt]*$"))) {
                                currentInput = newText
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFCDDC39), Color(0xFFFFC107), Color(0xFFFF5722))
                            ),
                            fontSize = 30.sp,
                            textAlign = TextAlign.End
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color(0xFFFF5722),
                            unfocusedTextColor = Color(0xFFFF5722)
                        ),
                        singleLine = true
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalcButton("C", ButtonType.OPERATOR) { currentInput = "0" }
                CalcButton("(", ButtonType.OPERATOR) { currentInput = appendToInput("(", currentInput) }
                CalcButton(")", ButtonType.OPERATOR) { currentInput = appendToInput(")", currentInput) }
                CalcButton("⌫", ButtonType.OPERATOR) {
                    currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
                }
            }
            for (i in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 1..3) {
                        val number = (6 - i * 3 + j).toString()
                        CalcButton(number, ButtonType.NUMBER) {
                            currentInput = appendToInput(number, currentInput)
                        }
                    }
                    val op_1 = when (i) {
                        0 -> "+"
                        1 -> "-"
                        else -> "×"
                    }
                    val op_2 = when (i) {
                        0 -> "^"
                        1 -> "√"
                        else -> "%"
                    }
                    CalcButton(op_1, ButtonType.OPERATOR) {
                        currentInput = appendToInput(when (op_1) {
                            "×" -> "*"
                            else -> op_1
                        }, currentInput)
                    }
                    CalcButton(op_2, ButtonType.OPERATOR) {
                        when (op_2) {
                            "√" -> currentInput = appendToInput("sqrt(", currentInput)
                            else -> currentInput = appendToInput(op_2, currentInput)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalcButton(".", ButtonType.NUMBER) { currentInput = appendToInput(".", currentInput) }
                CalcButton("0", ButtonType.NUMBER) { currentInput = appendToInput("0", currentInput) }
                CalcButton("÷", ButtonType.OPERATOR) { currentInput = appendToInput("/", currentInput) }
                CalcButton("=", ButtonType.EQUALS) {
                    try {
                        val result = evaluateExpression(currentInput)
                        history = history + "$currentInput = $result"
                        currentInput = result.toString()
                    } catch (e: Exception) {
                        errorMessage = when (e) {
                            is UnsupportedOperationException -> "Деление на ноль невозможно"
                            is IllegalArgumentException -> "Неверный ввод для квадратного корня"
                            else -> "Ошибка вычисления: ${e.message}"
                        }
                        showErrorDialog = true
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Bottom
            ) {
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { newText ->
                        if (newText.matches(Regex("^[\\d.+\\-*/^%()sqrt]*$"))) {
                            currentInput = newText
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFCDDC39), Color(0xFFFFC107), Color(0xFFFF5722))
                        ),
                        fontSize = 30.sp,
                        textAlign = TextAlign.End
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color(0xFFFF5722),
                        unfocusedTextColor = Color(0xFFFF5722)
                    ),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CalcButton("C", ButtonType.OPERATOR) { currentInput = "0" }
                    CalcButton("(", ButtonType.OPERATOR) { currentInput = appendToInput("(", currentInput) }
                    CalcButton(")", ButtonType.OPERATOR) { currentInput = appendToInput(")", currentInput) }
                    CalcButton("⌫", ButtonType.OPERATOR) {
                        currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
                    }
                }
                for (i in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (j in 1..3) {
                            val number = (6 - i * 3 + j).toString()
                            CalcButton(number, ButtonType.NUMBER) {
                                currentInput = appendToInput(number, currentInput)
                            }
                        }
                        val op = when (i) {
                            0 -> "+"
                            1 -> "-"
                            else -> "×"
                        }
                        CalcButton(op, ButtonType.OPERATOR) {
                            currentInput = appendToInput(
                                when (op) {
                                    "×" -> "*"
                                    else -> op
                                }, currentInput
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CalcButton(".", ButtonType.NUMBER) { currentInput = appendToInput(".", currentInput) }
                    CalcButton("0", ButtonType.NUMBER) { currentInput = appendToInput("0", currentInput) }
                    CalcButton("÷", ButtonType.OPERATOR) { currentInput = appendToInput("/", currentInput) }
                    CalcButton("=", ButtonType.EQUALS) {
                        try {
                            val result = evaluateExpression(currentInput)
                            history = history + "$currentInput = $result"
                            currentInput = result.toString()
                        } catch (e: Exception) {
                            errorMessage = when (e) {
                                is UnsupportedOperationException -> "Деление на ноль невозможно"
                                is IllegalArgumentException -> "Неверный ввод для квадратного корня"
                                else -> "Ошибка вычисления: ${e.message}"
                            }
                            showErrorDialog = true
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CalcButton("^", ButtonType.OPERATOR) { currentInput = appendToInput("^", currentInput) }
                    CalcButton("√", ButtonType.OPERATOR) { currentInput = appendToInput("sqrt(", currentInput) }
                    CalcButton("%", ButtonType.OPERATOR) {
                        currentInput = convertLastNumberToPercent(currentInput)
                    }
                }
            }
        }
    }
}

enum class ButtonType { NUMBER, OPERATOR, EQUALS, HISTORY }

@Composable
fun CalcButton(
    text: String,
    buttonType: ButtonType,
    modifier: Modifier = Modifier.size(60.dp),
    onClick: () -> Unit
) {
    val colors = when (buttonType) {
        ButtonType.NUMBER -> ButtonDefaults.buttonColors(
            containerColor = Color(0x00000000),
            contentColor = Color(0xFFFFFFFF)
        )
        ButtonType.OPERATOR -> ButtonDefaults.buttonColors(
            containerColor = Color(0x00000000),
            contentColor = Color(0xFFFF5722)
        )
        ButtonType.EQUALS -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5722),
            contentColor = Color(0xFF000000)
        )
        ButtonType.HISTORY -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5722),
            contentColor = Color(0xFF000000)
        )
    }

    Button(
        onClick = onClick,
        colors = colors,
        modifier = modifier
            .padding(2.dp)
            .height(60.dp)
    ) {
        Text(
            text = text,
            fontSize = when (buttonType) {
                ButtonType.HISTORY -> 20.sp
                else -> 25.sp
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HistoryList(history: List<String>) {
    val gradientColors = listOf(Color(0xFFCDDC39), Color(0xFFFFC107), Color(0xFFFF5722))
    val brush = remember {
        Brush.linearGradient(colors = gradientColors)
    }

    LazyColumn(modifier = Modifier.height(340.dp)) {
        items(history.reversed()) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x006200EE),
                    contentColor = Color(0xFFFF5722)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = item,
                        fontSize = 20.sp,
                        style = TextStyle(brush = brush),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

private fun appendToInput(value: String, current: String): String {
    return if (current == "0" || current == "Error") value else current + value
}

private fun evaluateExpression(expression: String): Double {
    // Обработка квадратных корней
    var processedExpression = expression.replace("sqrt(", "√(")

    val tokens = processedExpression.replace(" ", "").toCharArray()
    val values = Stack<Double>()
    val ops = Stack<Char>()
    var i = 0

    while (i < tokens.size) {
        when {
            // Обработка квадратного корня
            tokens[i] == '√' -> {
                i++
                if (tokens[i] != '(') {
                    throw IllegalArgumentException("После √ должна идти открывающая скобка")
                }

                // Находим соответствующую закрывающую скобку
                var balance = 1
                val start = i + 1
                var end = start
                while (end < tokens.size && balance > 0) {
                    when (tokens[end]) {
                        '(' -> balance++
                        ')' -> balance--
                    }
                    if (balance > 0) end++
                }

                if (balance != 0) {
                    throw IllegalArgumentException("Несбалансированные скобки в выражении квадратного корня")
                }

                val subExpression = String(tokens.copyOfRange(start, end))
                val subValue = evaluateExpression(subExpression)
                if (subValue < 0) {
                    throw IllegalArgumentException("Квадратный корень из отрицательного числа")
                }
                values.push(sqrt(subValue))
                i = end + 1
            }
            tokens[i] == '-' && (i == 0 || tokens[i-1] == '(' || isOperator(tokens[i-1])) -> {
                val sb = StringBuilder("-")
                i++
                while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                    sb.append(tokens[i++])
                }
                values.push(sb.toString().toDouble())
            }

            tokens[i].isDigit() || tokens[i] == '.' -> {
                val sb = StringBuilder()
                while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                    sb.append(tokens[i++])
                }
                if (i < tokens.size && tokens[i] == '%') {
                    values.push(sb.toString().toDouble() / 100)
                    i++
                } else {
                    values.push(sb.toString().toDouble())
                }
            }
            tokens[i] == '(' -> ops.push(tokens[i++])
            tokens[i] == ')' -> {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.pop()
                i++
            }
            isOperator(tokens[i]) -> {
                while (!ops.empty() && ops.peek() != '(' &&
                    hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.push(tokens[i++])
            }
        }
    }
    while (!ops.empty()) {
        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
    }

    return values.pop()
}

private fun isOperator(c: Char): Boolean {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^'
}

private fun hasPrecedence(op1: Char, op2: Char): Boolean {
    if (op2 == '(') return false

    val precedence = mapOf(
        '^' to 4,
        '*' to 2,
        '/' to 2,
        '+' to 1,
        '-' to 1
    )
    return precedence[op2]!! >= precedence[op1]!!
}

private fun applyOp(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> {
            if (b == 0.0) throw UnsupportedOperationException("Деление на ноль")
            a / b
        }
        '^' -> a.pow(b)
        else -> throw IllegalArgumentException("Неизвестная операция: $op")
    }
}

private fun convertLastNumberToPercent(expression: String): String {
    if (expression.isEmpty() || expression == "0") return "0"

    // Ищем последнее число в выражении
    val regex = Regex("""([-+]?\d*\.?\d+)([^0-9.]*)$""")
    val matchResult = regex.find(expression)

    return if (matchResult != null) {
        val (numberStr, tail) = matchResult.destructured
        try {
            val number = numberStr.toDouble() / 100
            expression.substring(0, matchResult.range.first) + number.toString() + tail
        } catch (e: NumberFormatException) {
            expression // если не удалось преобразовать, оставляем как есть
        }
    } else {
        expression // если не нашли число, оставляем как есть
    }
}