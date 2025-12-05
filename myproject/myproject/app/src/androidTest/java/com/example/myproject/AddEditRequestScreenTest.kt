package com.example.myproject.view.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myproject.viewmodel.RequestUiState
import com.example.myproject.viewmodel.RequestViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditRequestScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun fakeViewModel(): RequestViewModel {
        val vm = mockk<RequestViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(RequestUiState())
        return vm
    }

    // ── Add Mode ──────────────────────────────────────────────────────────────

    @Test
    fun addRequestScreen_showsCorrectTitle_inAddMode() {
        composeRule.setContent {
            AddEditRequestScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }
        composeRule.onNodeWithText("Add Request").assertIsDisplayed()
    }

    @Test
    fun addRequestScreen_showsAllFormFields() {
        composeRule.setContent {
            AddEditRequestScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }
        composeRule.onNodeWithText("Skill Wanted").assertIsDisplayed()
        composeRule.onNodeWithText("Description").assertIsDisplayed()
        composeRule.onNodeWithText("Budget").assertIsDisplayed()
        composeRule.onNodeWithText("Contact Info").assertIsDisplayed()
    }

    @Test
    fun addRequestScreen_showsAddButton() {
        composeRule.setContent {
            AddEditRequestScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }
        composeRule.onNodeWithText("Add Request").assertIsDisplayed()
    }

    @Test
    fun addRequestScreen_userCanTypeInAllFields() {
        composeRule.setContent {
            AddEditRequestScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }

        composeRule.onNodeWithText("Skill Wanted").performTextInput("Python")
        composeRule.onNodeWithText("Description").performTextInput("Need OOP help")
        composeRule.onNodeWithText("Budget").performTextInput("₹300/hr")
        composeRule.onNodeWithText("Contact Info").performTextInput("learner@email.com")

        composeRule.onNodeWithText("Python").assertIsDisplayed()
        composeRule.onNodeWithText("Need OOP help").assertIsDisplayed()
        composeRule.onNodeWithText("₹300/hr").assertIsDisplayed()
        composeRule.onNodeWithText("learner@email.com").assertIsDisplayed()
    }

    // ── Edit Mode ─────────────────────────────────────────────────────────────

    @Test
    fun editRequestScreen_showsCorrectTitle_inEditMode() {
        composeRule.setContent {
            AddEditRequestScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                requestId = "r1",
                initialSkillWanted = "Python",
                initialDescription = "OOP help",
                initialBudget = "₹300/hr",
                initialContactInfo = "learner@email.com",
                onBack = {}
            )
        }
        composeRule.onNodeWithText("Edit Request").assertIsDisplayed()
    }

    @Test
    fun editRequestScreen_prePopulatesFields() {
        composeRule.setContent {
            AddEditRequestScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                requestId = "r1",
                initialSkillWanted = "Python Programming",
                initialDescription = "Need help with decorators",
                initialBudget = "₹400/hr",
                initialContactInfo = "dev@email.com",
                onBack = {}
            )
        }

        composeRule.onNodeWithText("Python Programming").assertIsDisplayed()
        composeRule.onNodeWithText("Need help with decorators").assertIsDisplayed()
        composeRule.onNodeWithText("₹400/hr").assertIsDisplayed()
        composeRule.onNodeWithText("dev@email.com").assertIsDisplayed()
    }

    @Test
    fun editRequestScreen_showsUpdateButton() {
        composeRule.setContent {
            AddEditRequestScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                requestId = "r1",
                onBack = {}
            )
        }
        composeRule.onNodeWithText("Update Request").assertIsDisplayed()
    }

    @Test
    fun editRequestScreen_canModifyExistingField() {
        composeRule.setContent {
            AddEditRequestScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                requestId = "r1",
                initialSkillWanted = "Python",
                onBack = {}
            )
        }

        composeRule.onNodeWithText("Python").performTextClearance()
        composeRule.onNodeWithText("Skill Wanted").performTextInput("Kotlin")

        composeRule.onNodeWithText("Kotlin").assertIsDisplayed()
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    @Test
    fun addRequestScreen_backButton_invokesOnBack() {
        var backPressed = false

        composeRule.setContent {
            AddEditRequestScreen(
                viewModel = fakeViewModel(),
                isEditMode = false,
                onBack = { backPressed = true }
            )
        }

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue(backPressed)
    }
}