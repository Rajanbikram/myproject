package com.example.myproject.view.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myproject.model.SkillModel
import com.example.myproject.repository.SkillRepo
import com.example.myproject.viewmodel.SkillUiState
import com.example.myproject.viewmodel.SkillViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditSkillScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun fakeViewModel(): SkillViewModel {
        val vm = mockk<SkillViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(SkillUiState())
        return vm
    }

    // ── Add Mode ──────────────────────────────────────────────────────────────

    @Test
    fun addSkillScreen_showsCorrectTitle_inAddMode() {
        composeRule.setContent {
            AddEditSkillScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }
        composeRule.onNodeWithText("Add Skill").assertIsDisplayed()
    }

    @Test
    fun addSkillScreen_showsAllFormFields() {
        composeRule.setContent {
            AddEditSkillScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }
        composeRule.onNodeWithText("Skill Title").assertIsDisplayed()
        composeRule.onNodeWithText("Description").assertIsDisplayed()
        composeRule.onNodeWithText("Price").assertIsDisplayed()
        composeRule.onNodeWithText("Contact Info").assertIsDisplayed()
    }

    @Test
    fun addSkillScreen_showsAddButton() {
        composeRule.setContent {
            AddEditSkillScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }
        composeRule.onNodeWithText("Add Skill").assertIsDisplayed()
    }

    @Test
    fun addSkillScreen_userCanTypeInAllFields() {
        composeRule.setContent {
            AddEditSkillScreen(viewModel = fakeViewModel(), isEditMode = false, onBack = {})
        }

        composeRule.onNodeWithText("Skill Title").performTextInput("Guitar")
        composeRule.onNodeWithText("Description").performTextInput("Learn guitar basics")
        composeRule.onNodeWithText("Price").performTextInput("₹500/hr")
        composeRule.onNodeWithText("Contact Info").performTextInput("test@email.com")

        composeRule.onNodeWithText("Guitar").assertIsDisplayed()
        composeRule.onNodeWithText("Learn guitar basics").assertIsDisplayed()
        composeRule.onNodeWithText("₹500/hr").assertIsDisplayed()
        composeRule.onNodeWithText("test@email.com").assertIsDisplayed()
    }

    // ── Edit Mode ─────────────────────────────────────────────────────────────

    @Test
    fun editSkillScreen_showsCorrectTitle_inEditMode() {
        composeRule.setContent {
            AddEditSkillScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                skillId = "s1",
                initialTitle = "Guitar Lessons",
                initialDescription = "Learn guitar",
                initialPrice = "₹500/hr",
                initialContactInfo = "guitar@email.com",
                onBack = {}
            )
        }
        composeRule.onNodeWithText("Edit Skill").assertIsDisplayed()
    }

    @Test
    fun editSkillScreen_prePopulatesFields_withInitialValues() {
        composeRule.setContent {
            AddEditSkillScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                skillId = "s1",
                initialTitle = "Guitar Lessons",
                initialDescription = "Learn guitar from scratch",
                initialPrice = "₹500/hr",
                initialContactInfo = "guitar@email.com",
                onBack = {}
            )
        }

        composeRule.onNodeWithText("Guitar Lessons").assertIsDisplayed()
        composeRule.onNodeWithText("Learn guitar from scratch").assertIsDisplayed()
        composeRule.onNodeWithText("₹500/hr").assertIsDisplayed()
        composeRule.onNodeWithText("guitar@email.com").assertIsDisplayed()
    }

    @Test
    fun editSkillScreen_showsUpdateButton() {
        composeRule.setContent {
            AddEditSkillScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                skillId = "s1",
                onBack = {}
            )
        }
        composeRule.onNodeWithText("Update Skill").assertIsDisplayed()
    }

    @Test
    fun editSkillScreen_canEditExistingField() {
        composeRule.setContent {
            AddEditSkillScreen(
                viewModel = fakeViewModel(),
                isEditMode = true,
                skillId = "s1",
                initialTitle = "Guitar Lessons",
                onBack = {}
            )
        }

        // Clear and retype
        composeRule.onNodeWithText("Guitar Lessons").performTextClearance()
        composeRule.onNodeWithText("Skill Title").performTextInput("Piano Lessons")

        composeRule.onNodeWithText("Piano Lessons").assertIsDisplayed()
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    @Test
    fun addSkillScreen_backButton_invokesOnBack() {
        var backPressed = false

        composeRule.setContent {
            AddEditSkillScreen(
                viewModel = fakeViewModel(),
                isEditMode = false,
                onBack = { backPressed = true }
            )
        }

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue("Back button should invoke onBack", backPressed)
    }
}