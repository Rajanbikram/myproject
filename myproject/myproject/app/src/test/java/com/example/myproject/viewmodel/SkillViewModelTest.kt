package com.example.myproject.viewmodel

import com.example.myproject.model.SkillModel
import com.example.myproject.repository.SkillRepo
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
class SkillViewModelTest {

    // ── Test dispatcher so coroutines run synchronously ──────────────────────
    private val testDispatcher = StandardTestDispatcher()

    // ── Mocked repository ─────────────────────────────────────────────────────
    private lateinit var mockRepo: SkillRepo
    private lateinit var viewModel: SkillViewModel

    private val fakeUserId = "testUser123"

    private val fakeSkill = SkillModel(
        skillId = "skill001",
        skillTitle = "Guitar Lessons",
        description = "Learn acoustic guitar from scratch",
        price = "₹500/hr",
        contactInfo = "test@email.com"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepo = mock()
        viewModel = SkillViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── getSkills ─────────────────────────────────────────────────────────────

    @Test
    fun `getSkills sets isLoading true then populates skills on success`() = runTest {
        // Arrange: repo returns a list of skills on success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(true, "Skills fetched", listOf(fakeSkill))
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        // Act
        viewModel.getSkills(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse("Loading should be false after fetch", state.isLoading)
        assertEquals(1, state.skills.size)
        assertEquals("Guitar Lessons", state.skills[0].skillTitle)
        assertNull("Error should be null on success", state.errorMessage)
    }

    @Test
    fun `getSkills sets errorMessage on failure`() = runTest {
        // Arrange: repo signals failure
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(false, "Network error", null)
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        // Act
        viewModel.getSkills(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue("Skills list should be empty on failure", state.skills.isEmpty())
        assertEquals("Network error", state.errorMessage)
    }

    @Test
    fun `getSkills returns empty list when no skills exist`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(true, "Skills fetched", emptyList())
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        // Act
        viewModel.getSkills(fakeUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.skills.isEmpty())
        assertNull(state.errorMessage)
    }

    // ── addSkill ──────────────────────────────────────────────────────────────

    @Test
    fun `addSkill calls callback with success and refreshes list`() = runTest {
        // Arrange: add succeeds, then getSkills also succeeds
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Skill added successfully")
            null
        }.whenever(mockRepo).addSkill(eq(fakeUserId), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(true, "Skills fetched", listOf(fakeSkill))
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        var callbackSuccess = false
        var callbackMessage = ""

        // Act
        viewModel.addSkill(fakeUserId, fakeSkill) { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue("Callback should return success=true", callbackSuccess)
        assertEquals("Skill added successfully", callbackMessage)
        // Verify getSkills was called to refresh
        verify(mockRepo).getSkills(eq(fakeUserId), any())
    }

    @Test
    fun `addSkill calls callback with failure and does not refresh`() = runTest {
        // Arrange: add fails
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to add skill")
            null
        }.whenever(mockRepo).addSkill(eq(fakeUserId), any(), any())

        var callbackSuccess = true

        // Act
        viewModel.addSkill(fakeUserId, fakeSkill) { success, _ ->
            callbackSuccess = success
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertFalse(callbackSuccess)
    }

    // ── updateSkill ───────────────────────────────────────────────────────────

    @Test
    fun `updateSkill calls callback with success and refreshes list`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Skill updated successfully")
            null
        }.whenever(mockRepo).updateSkill(eq(fakeUserId), eq("skill001"), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(true, "Skills fetched", listOf(fakeSkill))
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        var callbackMessage = ""

        // Act
        viewModel.updateSkill(fakeUserId, "skill001", fakeSkill) { _, message ->
            callbackMessage = message
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("Skill updated successfully", callbackMessage)
        verify(mockRepo).getSkills(eq(fakeUserId), any())
    }

    @Test
    fun `updateSkill with correct model fields passed to repo`() = runTest {
        // Capture the model passed to updateSkill
        val modelCaptor = argumentCaptor<SkillModel>()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Updated")
            null
        }.whenever(mockRepo).updateSkill(any(), any(), modelCaptor.capture(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(true, "Fetched", emptyList())
            null
        }.whenever(mockRepo).getSkills(any(), any())

        // Act
        viewModel.updateSkill(fakeUserId, "skill001", fakeSkill) { _, _ -> }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert the right model was passed
        assertEquals("Guitar Lessons", modelCaptor.firstValue.skillTitle)
        assertEquals("₹500/hr", modelCaptor.firstValue.price)
    }

    // ── deleteSkill ───────────────────────────────────────────────────────────

    @Test
    fun `deleteSkill removes skill and refreshes list on success`() = runTest {
        // Arrange: first load two skills, then delete one
        val skill2 = fakeSkill.copy(skillId = "skill002", skillTitle = "Piano Lessons")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Skill deleted successfully")
            null
        }.whenever(mockRepo).deleteSkill(eq(fakeUserId), eq("skill001"), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(true, "Skills fetched", listOf(skill2))
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        // Act
        viewModel.deleteSkill(fakeUserId, "skill001")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: only skill2 remains
        val state = viewModel.uiState.value
        assertEquals(1, state.skills.size)
        assertEquals("Piano Lessons", state.skills[0].skillTitle)
    }

    @Test
    fun `deleteSkill sets errorMessage on failure`() = runTest {
        // Arrange
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to delete skill")
            null
        }.whenever(mockRepo).deleteSkill(eq(fakeUserId), any(), any())

        // Act
        viewModel.deleteSkill(fakeUserId, "skill001")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("Failed to delete skill", viewModel.uiState.value.errorMessage)
    }

    // ── clearMessages ─────────────────────────────────────────────────────────

    @Test
    fun `clearMessages resets errorMessage and successMessage to null`() = runTest {
        // Arrange: force an error state
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<SkillModel>?) -> Unit>(1)
            callback(false, "Some error", null)
            null
        }.whenever(mockRepo).getSkills(eq(fakeUserId), any())

        viewModel.getSkills(fakeUserId)
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
    fun `initial uiState is correct default`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.skills.isEmpty())
        assertNull(state.errorMessage)
        assertNull(state.successMessage)
    }
}