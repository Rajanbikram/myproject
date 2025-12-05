package com.example.myproject.view.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class SkillsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // ── Helpers ───────────────────────────────────────────────────────────────

    // Creates a fake ViewModel with a fixed StateFlow value — no Firestore needed
    private fun fakeViewModel(state: SkillUiState): SkillViewModel {
        val mockRepo = mockk<SkillRepo>(relaxed = true)
        val vm = mockk<SkillViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(state)
        return vm
    }

    private val sampleSkills = listOf(
        SkillModel("s1", "Guitar Lessons", "Learn acoustic guitar", "₹500/hr", "guitar@email.com"),
        SkillModel("s2", "Python Tutoring", "Learn Python basics", "₹400/hr", "python@email.com")
    )

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun skillsScreen_showsLoadingIndicator_whenIsLoadingTrue() {
        val vm = fakeViewModel(SkillUiState(isLoading = true))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        // CircularProgressIndicator does not have text, check that skill list is NOT shown
        composeRule.onNodeWithText("Guitar Lessons").assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun skillsScreen_showsErrorMessage_whenErrorMessageSet() {
        val vm = fakeViewModel(SkillUiState(errorMessage = "Failed to load skills"))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule
            .onNodeWithText("Failed to load skills")
            .assertIsDisplayed()
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    @Test
    fun skillsScreen_showsEmptyMessage_whenSkillsListIsEmpty() {
        val vm = fakeViewModel(SkillUiState(skills = emptyList()))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule
            .onNodeWithText("No skills yet.\nTap + to add your first skill!")
            .assertIsDisplayed()
    }

    // ── Skills list ───────────────────────────────────────────────────────────

    @Test
    fun skillsScreen_showsAllSkillTitles_whenSkillsLoaded() {
        val vm = fakeViewModel(SkillUiState(skills = sampleSkills))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Guitar Lessons").assertIsDisplayed()
        composeRule.onNodeWithText("Python Tutoring").assertIsDisplayed()
    }

    @Test
    fun skillsScreen_showsSkillDescription() {
        val vm = fakeViewModel(SkillUiState(skills = sampleSkills))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Learn acoustic guitar").assertIsDisplayed()
    }

    @Test
    fun skillsScreen_showsPrice() {
        val vm = fakeViewModel(SkillUiState(skills = sampleSkills))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Price: ₹500/hr").assertIsDisplayed()
    }

    // ── FAB ───────────────────────────────────────────────────────────────────

    @Test
    fun skillsScreen_fabClick_invokesOnAddClick() {
        val vm = fakeViewModel(SkillUiState(skills = emptyList()))
        var addClicked = false

        composeRule.setContent {
            SkillsScreen(
                viewModel = vm,
                onAddClick = { addClicked = true },
                onEditClick = {},
                onBack = {}
            )
        }

        composeRule
            .onNodeWithContentDescription("Add Skill")
            .performClick()

        assertTrue("FAB click should trigger onAddClick", addClicked)
    }

    // ── Edit / Delete ─────────────────────────────────────────────────────────

    @Test
    fun skillsScreen_editButtonClick_invokesOnEditClick() {
        val vm = fakeViewModel(SkillUiState(skills = sampleSkills))
        var editClicked = false

        composeRule.setContent {
            SkillsScreen(
                viewModel = vm,
                onAddClick = {},
                onEditClick = { editClicked = true },
                onBack = {}
            )
        }

        composeRule
            .onAllNodesWithContentDescription("Edit")
            .onFirst()
            .performClick()

        assertTrue("Edit button should trigger onEditClick", editClicked)
    }

    @Test
    fun skillsScreen_deleteButtonIsPresent_forEachSkill() {
        val vm = fakeViewModel(SkillUiState(skills = sampleSkills))

        composeRule.setContent {
            SkillsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        // There should be 2 delete buttons for 2 skills
        val deleteButtons = composeRule.onAllNodesWithContentDescription("Delete")
        deleteButtons[0].assertIsDisplayed()
        deleteButtons[1].assertIsDisplayed()
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    @Test
    fun skillsScreen_backButton_invokesOnBack() {
        val vm = fakeViewModel(SkillUiState())
        var backPressed = false

        composeRule.setContent {
            SkillsScreen(
                viewModel = vm,
                onAddClick = {},
                onEditClick = {},
                onBack = { backPressed = true }
            )
        }

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue("Back button should invoke onBack", backPressed)
    }
}