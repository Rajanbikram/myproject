package com.example.myproject.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillModelTest {

    // ── Default constructor ───────────────────────────────────────────────────

    @Test
    fun `default constructor creates model with empty strings`() {
        val skill = SkillModel()
        assertEquals("", skill.skillId)
        assertEquals("", skill.skillTitle)
        assertEquals("", skill.description)
        assertEquals("", skill.price)
        assertEquals("", skill.contactInfo)
    }

    // ── toMap ─────────────────────────────────────────────────────────────────

    @Test
    fun `toMap returns map with all correct keys`() {
        val skill = SkillModel(
            skillId = "abc123",
            skillTitle = "Guitar Lessons",
            description = "Learn guitar",
            price = "₹500/hr",
            contactInfo = "test@email.com"
        )
        val map = skill.toMap()

        assertTrue("Map must contain 'skillId'", map.containsKey("skillId"))
        assertTrue("Map must contain 'skillTitle'", map.containsKey("skillTitle"))
        assertTrue("Map must contain 'description'", map.containsKey("description"))
        assertTrue("Map must contain 'price'", map.containsKey("price"))
        assertTrue("Map must contain 'contactInfo'", map.containsKey("contactInfo"))
    }

    @Test
    fun `toMap returns correct values`() {
        val skill = SkillModel(
            skillId = "abc123",
            skillTitle = "Guitar Lessons",
            description = "Learn guitar",
            price = "₹500/hr",
            contactInfo = "test@email.com"
        )
        val map = skill.toMap()

        assertEquals("abc123", map["skillId"])
        assertEquals("Guitar Lessons", map["skillTitle"])
        assertEquals("Learn guitar", map["description"])
        assertEquals("₹500/hr", map["price"])
        assertEquals("test@email.com", map["contactInfo"])
    }

    @Test
    fun `toMap is not null`() {
        val map = SkillModel().toMap()
        assertNotNull(map)
    }

    @Test
    fun `toMap has exactly 5 keys`() {
        val map = SkillModel().toMap()
        assertEquals(5, map.size)
    }

    // ── copy ──────────────────────────────────────────────────────────────────

    @Test
    fun `copy creates independent copy with updated field`() {
        val original = SkillModel(skillId = "id1", skillTitle = "Old Title")
        val copy = original.copy(skillTitle = "New Title")

        assertEquals("Old Title", original.skillTitle)  // original unchanged
        assertEquals("New Title", copy.skillTitle)       // copy updated
        assertEquals("id1", copy.skillId)               // other fields preserved
    }

    // ── equality ─────────────────────────────────────────────────────────────

    @Test
    fun `two models with same data are equal`() {
        val skill1 = SkillModel("id1", "Guitar", "Desc", "₹500", "email@test.com")
        val skill2 = SkillModel("id1", "Guitar", "Desc", "₹500", "email@test.com")
        assertEquals(skill1, skill2)
    }
}