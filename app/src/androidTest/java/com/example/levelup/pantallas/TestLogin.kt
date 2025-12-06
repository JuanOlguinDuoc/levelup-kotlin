package com.example.levelup.pantallas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performTextInput
import com.example.levelup.model.ApiResult
import com.example.levelup.model.LoginResponse
import com.example.levelup.ui.theme.auth.LoginScreen
import com.example.levelup.repository.LevelUpRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class TestLogin {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `login screen shows main texts and allows input`() {
        // Create a fake repository to avoid network calls
        val fakeRepo = mockk<LevelUpRepository>(relaxed = true)
        coEvery { fakeRepo.login(any(), any()) } returns ApiResult.Success(
            LoginResponse(token = "token123", email = "test@example.com", message = "OK")
        )

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                testLogoPainter = ColorPainter(Color.LightGray),
                testRepository = fakeRepo
            )
        }

        composeTestRule.onNodeWithText("Inicio de Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tu plataforma de gestión de ecommerce").assertIsDisplayed()

        // Enter email and password
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password")

        // Ensure inputs are displayed (fields exist)
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
    }
}
