package uk.ac.tees.mad.Q2259850.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.Q2259850.Routes

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenMain(){
    val navController = rememberNavController()

//    if(FirebaseAuth.getInstance().currentUser ==null) {
        NavHost(navController = navController, startDestination = Routes.SplashScreen.route) {
            composable(Routes.SplashScreen.route) {
                SplashScreen(navController = navController)
            }
            composable(Routes.Login.route) {
                LoginScreen(navController = navController)
            }

            composable(Routes.Signup.route) {
                SignupScreen(navController = navController)
            }

            composable(Routes.Dashboard.route) {
                DashboardScreen(navController = navController)
            }
            composable(Routes.AddMemory.route) {
                AddMemory(navController = navController)
            }
            composable(Routes.SplashScreen.route) {
                SplashScreen(navController = navController)
            }
        }
//    }else{
//        NavHost(navController = navController, startDestination = Routes.SplashScreen.route) {
//            composable(Routes.SplashScreen.route) {
//                SplashScreen(navController = navController)
//            }
//            composable(Routes.Login.route) {
//                LoginScreen(navController = navController)
//            }
//
//            composable(Routes.Signup.route) {
//                SignupScreen(navController = navController)
//            }
//
//            composable(Routes.Dashboard.route) {
//                DashboardScreen(navController = navController)
//            }
//            composable(Routes.AddMemory.route) {
//                AddMemory(navController = navController)
//            }
//        }
//    }
}