package uk.ac.tees.mad.Q2259850.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.Q2259850.MemoryData
import uk.ac.tees.mad.Q2259850.R
import uk.ac.tees.mad.Q2259850.StoreData
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DashboardScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var items by remember { mutableStateOf(listOf<MemoryData>()) }
    val dataStore = StoreData(context = LocalContext.current)
    var context= LocalContext.current
    val id = dataStore.getId.collectAsState(initial = "")
    val idData:String=id.value.toString()
    var database: DatabaseReference? =null
    Log.d("idddd",idData)
       try {
            database = FirebaseDatabase.getInstance().reference.child("MemoryData").child(FirebaseAuth.getInstance().currentUser!!.uid)
       }catch (e:NullPointerException){}
    val scope = rememberCoroutineScope()
    var isDialogVisible by remember { mutableStateOf(false) }

    // Read data from Firebase Realtime Database
    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.children.map {
                    Log.d("dddp", it.toString())
                    it.getValue(MemoryData::class.java)
                }
                items = data.reversed()
                isLoading=false
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }

        }
        database!!.addValueEventListener(listener)
        onDispose {
            database.removeEventListener(listener)
        }
    }

    Column (modifier = Modifier.fillMaxSize()){
        Box {
            Scaffold(
                /*topBar = {
                    TopAppBar(backgroundColor = MaterialTheme.colors.primary,
                        title = {
                            Text(text = "My Memories",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        },
                        actions = {
                            Box(modifier = Modifier) {
                                IconButton(onClick = {
                                    isDialogVisible = true
                                }) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                }
                            }
                        }
                    )
                },*/
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd),
                topBar = {
                  TopAppBar(
                        title = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Memories",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W600,
                                textAlign = TextAlign.Center
                            )
                        },

                        actions = {
                            Row {
                                IconButton(
                                    onClick = {  isDialogVisible = true },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        tint = Color.White,
                                        contentDescription = "Search"
                                    )
                                }
                            }
                        },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("AddMemory")
                        }, modifier = Modifier
                            .padding(bottom = 50.dp)
                            .size(56.dp),
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) {
                if(isLoading){
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                               .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.White) // semi-transparent background
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colors.primary)
                    }
                }else{
                if(items.size>0 ){

                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),) {
                    items(items.size) { item ->
                        DataCard(dataItem = items[item])
                    }

                }
            }
            else{
                    Box(
                        Modifier.fillMaxHeight().fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Text(text = "No Data Found")
                    }
                }
                }
                Box(Modifier.height(10.dp)){}
                if (isDialogVisible) {
                    ProfileDialog(
                        dataStore,
                        onLogoutClick = {
                            FirebaseAuth.getInstance().signOut()
//                            scope.launch {
//                                PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
//                            }
                            navController.navigate("Login")
                        },
                        onDismiss = { isDialogVisible = false }
                    )
                }
            }
        }


    }


}

@Composable
fun DataCard(dataItem: MemoryData) {
    Card(
        backgroundColor = Color.White,
        elevation = 16.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .padding(10.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(16.dp)
        ) {
            // Image
            Image(
                painter = rememberAsyncImagePainter(dataItem.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = dataItem.title,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = dataItem.description,
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = dataItem.dateTime)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = dataItem.location)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
        }


    }
}


//fun userLogin( navController: NavHostController, username: String, password: String) {
////    val firebaseDatabase = FirebaseApp.getInstance();
////    val databaseReference = firebaseDatabase.applicationContext.getDatabasePath("EmployeeInfo");
//
////    val empObj =
////        User(username, password)
//    // we are use add value event listener method
//    // which is called with database reference.
////    databaseReference.addValueEventListener(object : ValueEventListener {
////        override fun onDataChange(snapshot: DataSnapshot) {
////            // inside the method of on Data change we are setting
////            // our object class to our database reference.
////            // data base reference will sends data to firebase.
////            databaseReference.setValue(empObj)
////            // after adding this data we
////            // are showing toast message.
//////            Toast.makeText(
//////                context,
//////                "Data added to Firebase Database",
//////                Toast.LENGTH_SHORT
//////            ).show()
////        }
////
////        override fun onCancelled(error: DatabaseError) {
////            // if the data is not added or it is cancelled then
////            // we are displaying a failure toast message.
//////            Toast.makeText(
//////                context,
//////                "Fail to add data $error",
//////                Toast.LENGTH_SHORT
//////            ).show()
////        }
////    })
//     val query: Query = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("email")
//            .equalTo(username)
//
//    query.addValueEventListener(object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            for (childSnapshot in dataSnapshot.children) {
//                val signUpModel: String = childSnapshot.child("password").getValue(String::class.java)!!
////                if (signUpModel == password) {
////                    Log.d("dfff","success")
////                    navController.navigate("Signup")
////                    // Here is your desired location
//////                        AppData.userId = childSnapshot.getKey()
//////                        AppData.userType = "user"
////                    //                                AppData.phoneNo = childSnapshot.child("phoneNumber").getValue(String.class);
//////                    progressDialog!!.dismiss()
//////                    Toast.makeText(application, "Success", Toast.LENGTH_SHORT).show()
//////                    startActivity(Intent(this@uk.ac.tees.mad.Q2259850.MainActivity, MainDashboard::class.java))
//////                    finish()
////                } else {
////                    Log.d("dfff","failed")
//
////                    progressDialog!!.dismiss()
////                    Toast.makeText(this@uk.ac.tees.mad.Q2259850.MainActivity, "failed", Toast.LENGTH_SHORT).show()
////                }
//            }
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.d("dfff","success$databaseError")
//
//        }
//    })
//}
@Composable
fun ProfileDialog(dataStore: StoreData, onLogoutClick: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val dialogWidth = (240 * density).dp
    val dialogHeight = (80 * density).dp
    val username = dataStore.getUsername.collectAsState(initial = "")
    val name = dataStore.getName.collectAsState(initial = "")
    val id = dataStore.getId.collectAsState(initial = "")
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        content = {
            Surface(
                modifier = Modifier
                    .width(dialogWidth),
//                    .height(dialogHeight),
                shape = MaterialTheme.shapes.medium,
                elevation = 8.dp

            ) {
                Card(
                    modifier = Modifier
                        .width(dialogWidth),
//                        .height(dialogHeight),
                    shape = MaterialTheme.shapes.medium,
                    elevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .padding(16.dp)

                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_person_24),
//                                contentDescription = null,
//                                modifier = Modifier.size(60.dp),
//                                tint = MaterialTheme.colors.primary
//                            )
                            Image(
                                painter = painterResource(id = R.drawable.baseline_person_24), // Replace 'your_image_resource' with your actual image resource
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(MaterialTheme.colors.primary, CircleShape)
                                    .clip(CircleShape)
                                    .padding(16.dp)
                            // Add padding if needed
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            contentAlignment = Alignment.Center
                        )
                        {
                            Text(name.value.toString(), style = MaterialTheme.typography.h5, textAlign = TextAlign.Center)
                        }
                        Box(
                            contentAlignment = Alignment.Center

                        )
                        {
                            Text(
                                username.value.toString(),
                                style = MaterialTheme.typography.body1,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Logout Button
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    onLogoutClick()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    )
}

