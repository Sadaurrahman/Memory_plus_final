package uk.ac.tees.mad.Q2259850.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import androidx.navigation.NavHostController
import uk.ac.tees.mad.Q2259850.UsersData
import uk.ac.tees.mad.Q2259850.theme.Purple700
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Composable
fun SignupScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    var newItem by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(CollectionUtils.listOf<UsersData>()) }
    val database = FirebaseDatabase.getInstance().reference.child("Users")

    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Login here"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {
                navController.navigate("Login")
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple700
            )
        )

        // Wrap the Column inside a Box and align it to the center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val name = remember { mutableStateOf(TextFieldValue()) }
                val username = remember { mutableStateOf(TextFieldValue()) }
                val password = remember { mutableStateOf(TextFieldValue()) }

                Text(text = "Sign Up", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive))
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Name") },
                    value = name.value,
                    onValueChange = { name.value = it })
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Email") },
                    value = username.value,
                    onValueChange = { username.value = it })
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Password") },
                    value = password.value,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { password.value = it })
                if (showDialog) {
                    Dialog(
                        onDismissRequest = { showDialog = false },
                        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            if (username.value.text.isNotEmpty() && password.value.text.isNotEmpty() && name.value.text.isNotEmpty()) {
                                showDialog = true
                                signUp(
                                    username.value.text,
                                    password.value.text,
                                    name.value.text,
                                    onResult = { dialog, success ->
                                        showDialog = dialog
                                        if (success) {
                                            navController.navigate("Login")
                                        } else {

                                        }
                                    })

                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Sign Up")
                    }
                }
            }
        }
    }
}


fun signUp(email: String, password: String,name:String, onResult: (dialog:Boolean,success:Boolean) -> Unit) {
     val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val database = Firebase.database
                val myRef = database.getReference("Users")
                val userData = UsersData(email,password,name)
                myRef.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userData)
                onResult(false,true)

            } else {
                // Sign-up failed
                onResult(false,false)

            }
        }
}

