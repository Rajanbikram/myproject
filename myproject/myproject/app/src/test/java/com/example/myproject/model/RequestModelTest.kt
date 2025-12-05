package com.example.myproject.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestModelTest {

    // ── Default constructor ───────────────────────────────────────────────────

    @Test
    fun `default constructor creates model with empty strings`() {
        val request = RequestModel()
        assertEquals("", request.requestId)
        assertEquals("", request.skillWanted)
        assertEquals("", request.description)
        assertEquals("", request.budget)
        assertEquals("", request.contactInfo)
    }

    // ── toMap ─────────────────────────────────────────────────────────────────

    @Test
    fun `toMap returns map with all correct keys`() {
        val request = RequestModel(
            requestId = "req001",
            skillWanted = "Python",
            description = "OOP basics",
            budget = "₹300/hr",
            contactInfo = "learner@email.com"
        )
        val map = request.toMap()

        assertTrue(map.containsKey("requestId"))
        assertTrue(map.containsKey("skillWanted"))
        assertTrue(map.containsKey("description"))
        assertTrue(map.containsKey("budget"))
        assertTrue(map.containsKey("contactInfo"))
    }

    @Test
    fun `toMap returns correct values`() {
        val request = RequestModel(
            requestId = "req001",
            skillWanted = "Python",
            description = "OOP basics",
            budget = "₹300/hr",
            contactInfo = "learner@email.com"
        )
        val map = request.toMap()

        assertEquals("req001", map["requestId"])
        assertEquals("Python", map["skillWanted"])
        assertEquals("OOP basics", map["description"])
        assertEquals("₹300/hr", map["budget"])
        assertEquals("learner@email.com", map["contactInfo"])
    }

    @Test
    fun `toMap is not null`() {
        assertNotNull(RequestModel().toMap())
    }

    @Test
    fun `toMap has exactly 5 keys`() {
        assertEquals(5, RequestModel().toMap().size)
    }

    // ── copy ──────────────────────────────────────────────────────────────────

    @Test
    fun `copy preserves unchanged fields and updates specified field`() {
        val original = RequestModel(requestId = "r1", skillWanted = "Old Skill")
        val updated = original.copy(skillWanted = "New Skill")

        assertEquals("Old Skill", original.skillWanted) // original unchanged
        assertEquals("New Skill", updated.skillWanted)  // copy updated
        assertEquals("r1", updated.requestId)           // ID preserved
    }

    // ── equality ─────────────────────────────────────────────────────────────

    @Test
    fun `two requests with same data are equal`() {
        val r1 = RequestModel("id1", "Python", "Desc", "₹300", "e@mail.com")
        val r2 = RequestModel("id1", "Python", "Desc", "₹300", "e@mail.com")
        assertEquals(r1, r2)
    }
}