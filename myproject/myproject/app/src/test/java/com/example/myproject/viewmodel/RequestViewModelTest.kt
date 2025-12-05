package com.example.myproject.viewmodel

import com.example.myproject.model.RequestModel
import com.example.myproject.repository.RequestRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class RequestViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRepo: RequestRepo
    private lateinit var viewModel: RequestViewModel

    private val fakeUserId = "testUser123"

    private val fakeRequest = RequestModel(
        requestId = "req001",
        skillWanted = "Python Programming",
        description = "Need help with basic Python and OOP",
        budget = "₹300/hr",
        contactInfo = "learner@email.com"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepo = mock()
        viewModel = RequestViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── getRequests ───────────────────────────────────────────────────────────

    @Test
    fun `getRequests populates requests list on success`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(true, "Requests fetched", listOf(fakeRequest))
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        // Act
        viewModel.getRequests(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.requests.size)
        assertEquals("Python Programming", state.requests[0].skillWanted)
        assertNull(state.errorMessage)
    }

    @Test
    fun `getRequests sets errorMessage on failure`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(false, "Firestore unavailable", null)
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        // Act
        viewModel.getRequests(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.requests.isEmpty())
        assertEquals("Firestore unavailable", state.errorMessage)
    }

    @Test
    fun `getRequests handles null list gracefully`() = runTest {
        // Arrange: repo returns success but null list
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(true, "Fetched", null)
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        // Act
        viewModel.getRequests(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: should fall back to empty list, not crash
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.requests.isEmpty())
    }

    // ── addRequest ────────────────────────────────────────────────────────────

    @Test
    fun `addRequest calls callback with success and triggers refresh`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Request added successfully")
            null
        }.whenever(mockRepo).addRequest(eq(fakeUserId), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(true, "Fetched", listOf(fakeRequest))
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        var callbackSuccess = false
        var callbackMessage = ""

        // Act
        viewModel.addRequest(fakeUserId, fakeRequest) { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(callbackSuccess)
        assertEquals("Request added successfully", callbackMessage)
        verify(mockRepo).getRequests(eq(fakeUserId), any())
    }

    @Test
    fun `addRequest does not refresh list on failure`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Add failed")
            null
        }.whenever(mockRepo).addRequest(eq(fakeUserId), any(), any())

        var callbackSuccess = true

        // Act
        viewModel.addRequest(fakeUserId, fakeRequest) { success, _ ->
            callbackSuccess = success
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertFalse(callbackSuccess)
    }

    // ── updateRequest ─────────────────────────────────────────────────────────

    @Test
    fun `updateRequest passes correct fields to repo`() = runTest {
        val modelCaptor = argumentCaptor<RequestModel>()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Updated")
            null
        }.whenever(mockRepo).updateRequest(any(), any(), modelCaptor.capture(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(true, "Fetched", emptyList())
            null
        }.whenever(mockRepo).getRequests(any(), any())

        // Act
        viewModel.updateRequest(fakeUserId, "req001", fakeRequest) { _, _ -> }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert fields captured correctly
        assertEquals("Python Programming", modelCaptor.firstValue.skillWanted)
        assertEquals("₹300/hr", modelCaptor.firstValue.budget)
        assertEquals("learner@email.com", modelCaptor.firstValue.contactInfo)
    }

    @Test
    fun `updateRequest invokes callback with success message`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Request updated successfully")
            null
        }.whenever(mockRepo).updateRequest(eq(fakeUserId), eq("req001"), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(true, "Fetched", emptyList())
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        var resultMessage = ""

        // Act
        viewModel.updateRequest(fakeUserId, "req001", fakeRequest) { _, message ->
            resultMessage = message
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("Request updated successfully", resultMessage)
    }

    // ── deleteRequest ─────────────────────────────────────────────────────────

    @Test
    fun `deleteRequest removes item and refreshes list`() = runTest {
        val req2 = fakeRequest.copy(requestId = "req002", skillWanted = "Graphic Design")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Request deleted successfully")
            null
        }.whenever(mockRepo).deleteRequest(eq(fakeUserId), eq("req001"), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(true, "Fetched", listOf(req2))
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        // Act
        viewModel.deleteRequest(fakeUserId, "req001")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: only req2 remains after deletion
        val state = viewModel.uiState.value
        assertEquals(1, state.requests.size)
        assertEquals("Graphic Design", state.requests[0].skillWanted)
    }

    @Test
    fun `deleteRequest sets errorMessage on failure`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Delete failed")
            null
        }.whenever(mockRepo).deleteRequest(eq(fakeUserId), any(), any())

        // Act
        viewModel.deleteRequest(fakeUserId, "req001")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("Delete failed", viewModel.uiState.value.errorMessage)
    }

    // ── clearMessages ─────────────────────────────────────────────────────────

    @Test
    fun `clearMessages resets error and success to null`() = runTest {
        // Arrange: trigger an error
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<RequestModel>?) -> Unit>(1)
            callback(false, "Some error", null)
            null
        }.whenever(mockRepo).getRequests(eq(fakeUserId), any())

        viewModel.getRequests(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Act
        viewModel.clearMessages()

        // Assert
        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.successMessage)
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial uiState has correct defaults`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.requests.isEmpty())
        assertNull(state.errorMessage)
        assertNull(state.successMessage)
    }
}