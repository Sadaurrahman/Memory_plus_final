package uk.ac.tees.mad.Q2259850.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import uk.ac.tees.mad.Q2259850.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController) {
    val viewModel = remember { SplashViewModel(navController) }

    LaunchedEffect(key1 = true) {
        delay(2000) // 2-second delay
        viewModel.onSplashScreenComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(

            painter = painterResource(id = R.drawable.baseline_photo_24 ),
            contentScale = ContentScale.FillBounds,
            contentDescription = null, // Set a content description if needed
            modifier = Modifier
                .size(200.dp)
                .wrapContentSize(align = Alignment.Center)
                .padding(16.dp)
        )

        Text(
            text = "Memory Plus",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp)
        )
    }
}
class SplashViewModel(private val navController: NavHostController) : ViewModel() {
    private val _splashScreenComplete = MutableStateFlow(false)
    val splashScreenComplete: StateFlow<Boolean> = _splashScreenComplete

    init {
        observeSplashScreenCompletion()
    }

    private fun observeSplashScreenCompletion() {
        viewModelScope.launch {
            splashScreenComplete.collect { isComplete ->
                if (isComplete) {
                    if(FirebaseAuth.getInstance().currentUser !=null){
                        navController.navigate("dashboard")
                    }else{
                        navController.navigate("Login")


                    }
                }
            }
        }
    }

    fun onSplashScreenComplete() {
        _splashScreenComplete.value = true
    }
}