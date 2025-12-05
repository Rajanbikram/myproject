package com.example.myproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myproject.model.RequestModel
import com.example.myproject.view.screens.RequestsScreen
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
class RequestsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun fakeViewModel(state: RequestUiState): RequestViewModel {
        val vm = mockk<RequestViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(state)
        return vm
    }

    private val sampleRequests = listOf(
        RequestModel("r1", "Python Programming", "Help with OOP basics", "₹300/hr", "learn@email.com"),
        RequestModel("r2", "Graphic Design", "Need Photoshop basics", "₹250/hr", "design@email.com")
    )

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun requestsScreen_hidesContent_whenLoading() {
        val vm = fakeViewModel(RequestUiState(isLoading = true))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Python Programming").assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun requestsScreen_showsErrorMessage() {
        val vm = fakeViewModel(RequestUiState(errorMessage = "Connection failed"))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Connection failed").assertIsDisplayed()
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    @Test
    fun requestsScreen_showsEmptyMessage_whenNoRequests() {
        val vm = fakeViewModel(RequestUiState(requests = emptyList()))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule
            .onNodeWithText("No requests yet.\nTap + to request a skill!")
            .assertIsDisplayed()
    }

    // ── Requests list ─────────────────────────────────────────────────────────

    @Test
    fun requestsScreen_showsAllRequestTitles() {
        val vm = fakeViewModel(RequestUiState(requests = sampleRequests))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Python Programming").assertIsDisplayed()
        composeRule.onNodeWithText("Graphic Design").assertIsDisplayed()
    }

    @Test
    fun requestsScreen_showsDescription() {
        val vm = fakeViewModel(RequestUiState(requests = sampleRequests))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Help with OOP basics").assertIsDisplayed()
    }

    @Test
    fun requestsScreen_showsBudget() {
        val vm = fakeViewModel(RequestUiState(requests = sampleRequests))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("Budget: ₹300/hr").assertIsDisplayed()
    }

    @Test
    fun requestsScreen_showsContactInfo() {
        val vm = fakeViewModel(RequestUiState(requests = sampleRequests))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("learn@email.com").assertIsDisplayed()
    }

    // ── FAB ───────────────────────────────────────────────────────────────────

    @Test
    fun requestsScreen_fabClick_invokesOnAddClick() {
        val vm = fakeViewModel(RequestUiState(requests = emptyList()))
        var addClicked = false

        composeRule.setContent {
            RequestsScreen(
                viewModel = vm,
                onAddClick = { addClicked = true },
                onEditClick = {},
                onBack = {}
            )
        }

        composeRule
            .onNodeWithContentDescription("Add Request")
            .performClick()

        assertTrue("FAB click should trigger onAddClick", addClicked)
    }

    // ── Edit / Delete ─────────────────────────────────────────────────────────

    @Test
    fun requestsScreen_editButtonClick_invokesOnEditClick() {
        val vm = fakeViewModel(RequestUiState(requests = sampleRequests))
        var editClicked = false

        composeRule.setContent {
            RequestsScreen(
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

        assertTrue("Edit click should trigger onEditClick", editClicked)
    }

    @Test
    fun requestsScreen_deleteButtonsPresent_forAllRequests() {
        val vm = fakeViewModel(RequestUiState(requests = sampleRequests))

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        val deleteButtons = composeRule.onAllNodesWithContentDescription("Delete")
        deleteButtons[0].assertIsDisplayed()
        deleteButtons[1].assertIsDisplayed()
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    @Test
    fun requestsScreen_backButton_invokesOnBack() {
        val vm = fakeViewModel(RequestUiState())
        var backPressed = false

        composeRule.setContent {
            RequestsScreen(
                viewModel = vm,
                onAddClick = {},
                onEditClick = {},
                onBack = { backPressed = true }
            )
        }

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue(backPressed)
    }

    // ── TopAppBar title ───────────────────────────────────────────────────────

    @Test
    fun requestsScreen_showsCorrectTopBarTitle() {
        val vm = fakeViewModel(RequestUiState())

        composeRule.setContent {
            RequestsScreen(viewModel = vm, onAddClick = {}, onEditClick = {}, onBack = {})
        }

        composeRule.onNodeWithText("My Skill Requests").assertIsDisplayed()
    }
}
