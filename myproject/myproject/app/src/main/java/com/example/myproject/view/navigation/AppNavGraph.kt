package com.example.myproject.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myproject.repository.RequestRepoImpl
import com.example.myproject.repository.SkillRepoImpl
import com.example.myproject.view.screens.AddEditRequestScreen
import com.example.myproject.view.screens.AddEditSkillScreen
import com.example.myproject.view.screens.DashboardScreen
import com.example.myproject.view.screens.RequestsScreen
import com.example.myproject.view.screens.SkillsScreen
import com.example.myproject.viewmodel.RequestViewModel
import com.example.myproject.viewmodel.SkillViewModel

// All route name constants in one place
object Routes {
    const val DASHBOARD = "dashboard"
    const val SKILLS = "skills"
    const val ADD_SKILL = "add_skill"
    const val EDIT_SKILL = "edit_skill/{skillId}/{skillTitle}/{description}/{price}/{contactInfo}"
    const val REQUESTS = "requests"
    const val ADD_REQUEST = "add_request"
    const val EDIT_REQUEST = "edit_request/{requestId}/{skillWanted}/{description}/{budget}/{contactInfo}"
}

@Composable
fun AppNavGraph(navController: NavHostController) {

    // Create ViewModels here so they survive screen navigation within the same activity
    val skillViewModel = SkillViewModel(SkillRepoImpl())
    val requestViewModel = RequestViewModel(RequestRepoImpl())

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {

        // ── Dashboard ──────────────────────────────────────────────────────────
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onTeachClick = { navController.navigate(Routes.SKILLS) },
                onLearnClick = { navController.navigate(Routes.REQUESTS) }
            )
        }

        // ── Skills List ────────────────────────────────────────────────────────
        composable(Routes.SKILLS) {
            SkillsScreen(
                viewModel = skillViewModel,
                onAddClick = { navController.navigate(Routes.ADD_SKILL) },
                onEditClick = { skill ->
                    // Encode values in the route to pass as nav arguments
                    navController.navigate(
                        "edit_skill/${skill.skillId}/${skill.skillTitle}/${skill.description}/${skill.price}/${skill.contactInfo}"
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Add Skill ──────────────────────────────────────────────────────────
        composable(Routes.ADD_SKILL) {
            AddEditSkillScreen(
                viewModel = skillViewModel,
                isEditMode = false,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Edit Skill ─────────────────────────────────────────────────────────
        composable(
            route = Routes.EDIT_SKILL,
            arguments = listOf(
                navArgument("skillId") { type = NavType.StringType },
                navArgument("skillTitle") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType },
                navArgument("contactInfo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddEditSkillScreen(
                viewModel = skillViewModel,
                isEditMode = true,
                skillId = backStackEntry.arguments?.getString("skillId") ?: "",
                initialTitle = backStackEntry.arguments?.getString("skillTitle") ?: "",
                initialDescription = backStackEntry.arguments?.getString("description") ?: "",
                initialPrice = backStackEntry.arguments?.getString("price") ?: "",
                initialContactInfo = backStackEntry.arguments?.getString("contactInfo") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        // ── Requests List ──────────────────────────────────────────────────────
        composable(Routes.REQUESTS) {
            RequestsScreen(
                viewModel = requestViewModel,
                onAddClick = { navController.navigate(Routes.ADD_REQUEST) },
                onEditClick = { request ->
                    navController.navigate(
                        "edit_request/${request.requestId}/${request.skillWanted}/${request.description}/${request.budget}/${request.contactInfo}"
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Add Request ────────────────────────────────────────────────────────
        composable(Routes.ADD_REQUEST) {
            AddEditRequestScreen(
                viewModel = requestViewModel,
                isEditMode = false,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Edit Request ───────────────────────────────────────────────────────
        composable(
            route = Routes.EDIT_REQUEST,
            arguments = listOf(
                navArgument("requestId") { type = NavType.StringType },
                navArgument("skillWanted") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("budget") { type = NavType.StringType },
                navArgument("contactInfo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddEditRequestScreen(
                viewModel = requestViewModel,
                isEditMode = true,
                requestId = backStackEntry.arguments?.getString("requestId") ?: "",
                initialSkillWanted = backStackEntry.arguments?.getString("skillWanted") ?: "",
                initialDescription = backStackEntry.arguments?.getString("description") ?: "",
                initialBudget = backStackEntry.arguments?.getString("budget") ?: "",
                initialContactInfo = backStackEntry.arguments?.getString("contactInfo") ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}