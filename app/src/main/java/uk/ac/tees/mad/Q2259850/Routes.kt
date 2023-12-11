package uk.ac.tees.mad.Q2259850

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object Signup : Routes("Signup")
    object Dashboard : Routes("dashboard")
    object AddMemory : Routes("AddMemory")
    object SplashScreen : Routes("SplashScreen")
}