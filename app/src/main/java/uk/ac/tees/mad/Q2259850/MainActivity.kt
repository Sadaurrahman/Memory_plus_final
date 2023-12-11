package uk.ac.tees.mad.Q2259850

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import uk.ac.tees.mad.Q2259850.screen.ScreenMain
import uk.ac.tees.mad.Q2259850.theme.Memory_plusTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      
        WindowCompat.setDecorFitsSystemWindows(window,false)
        FirebaseApp.getApps(applicationContext)
        setContent {
            Memory_plusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =  Color.White
                ) {
                    ScreenMain()

                }
            }
        }
    }
}