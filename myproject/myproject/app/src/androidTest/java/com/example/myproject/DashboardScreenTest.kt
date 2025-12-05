package com.example.myproject.view.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // ── Visibility ────────────────────────────────────────────────────────────

    @Test
    fun dashboardScreen_showsAppBarTitle() {
        composeRule.setContent {
            DashboardScreen(onTeachClick = {}, onLearnClick = {})
        }
        composeRule
            .onNodeWithText("SkillConnect")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsSubtitleText() {
        composeRule.setContent {
            DashboardScreen(onTeachClick = {}, onLearnClick = {})
        }
        composeRule
            .onNodeWithText("What would you like to do?")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsTeachSkillCard() {
        composeRule.setContent {
            DashboardScreen(onTeachClick = {}, onLearnClick = {})
        }
        composeRule
            .onNodeWithText("Teach a Skill")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsLearnSkillCard() {
        composeRule.setContent {
            DashboardScreen(onTeachClick = {}, onLearnClick = {})
        }
        composeRule
            .onNodeWithText("Learn a Skill")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsTeachCardSubtitle() {
        composeRule.setContent {
            DashboardScreen(onTeachClick = {}, onLearnClick = {})
        }
        composeRule
            .onNodeWithText("Offer your expertise to others")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsLearnCardSubtitle() {
        composeRule.setContent {
            DashboardScreen(onTeachClick = {}, onLearnClick = {})
        }
        composeRule
            .onNodeWithText("Request skills you want to learn")
            .assertIsDisplayed()
    }

    // ── Click callbacks ───────────────────────────────────────────────────────

    @Test
    fun dashboardScreen_teachCardClick_invokesCallback() {
        var teachClicked = false

        composeRule.setContent {
            DashboardScreen(
                onTeachClick = { teachClicked = true },
                onLearnClick = {}
            )
        }

        composeRule
            .onNodeWithText("Teach a Skill")
            .performClick()

        assertTrue("onTeachClick should be called", teachClicked)
    }

    @Test
    fun dashboardScreen_learnCardClick_invokesCallback() {
        var learnClicked = false

        composeRule.setContent {
            DashboardScreen(
                onTeachClick = {},
                onLearnClick = { learnClicked = true }
            )
        }

        composeRule
            .onNodeWithText("Learn a Skill")
            .performClick()

        assertTrue("onLearnClick should be called", learnClicked)
    }

    @Test
    fun dashboardScreen_teachAndLearnCallbacks_areIndependent() {
        var teachClicked = false
        var learnClicked = false

        composeRule.setContent {
            DashboardScreen(
                onTeachClick = { teachClicked = true },
                onLearnClick = { learnClicked = true }
            )
        }

        // Click Teach only
        composeRule.onNodeWithText("Teach a Skill").performClick()

        assertTrue(teachClicked)
        assertTrue("Learn should NOT have been clicked", !learnClicked)
    }
}