package com.example.calc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calc.Calculator.ButtonType

@Composable
fun MainScreen(onCalculatorClick: () -> Unit, onExitClick: () -> Unit) {
    Scaffold(
        containerColor = Color.White // Светлый фон
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onCalculatorClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F), // Красная кнопка
                    contentColor = Color.White // Белый текст
                )
            ) {
                Text(
                    text = "Калькулятор",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Пока disabled */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFCDD2), // Светло-красная
                    contentColor = Color(0xFFD32F2F),   // Тёмно-красный текст
                    disabledContainerColor = Color(0xFFF8BBD0), // Бледно-розовая
                    disabledContentColor = Color.Gray
                ),
                enabled = false
            ) {
                Text(
                    text = "Графики",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onExitClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F), // Красная кнопка
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Выход",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
