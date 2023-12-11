package uk.ac.tees.mad.Q2259850.screen

import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.Q2259850.BuildConfig
import uk.ac.tees.mad.Q2259850.MemoryData
import uk.ac.tees.mad.Q2259850.R
import uk.ac.tees.mad.Q2259850.StoreData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddMemory(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
//            .statusBarsPadding()
//            .systemBarsPadding()
    ) {
        // Top App Bar
        TopAppBar(
//            modifier = Modifier.align(c),
            title = {
                Text(text = "Add Memory")
            },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White

        )

        // Add Memory Form
        AddMemoryForm(navController)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImagePicker(onImageSelected: (Uri) -> Unit) {
    val context = LocalContext.current
//    val launcher = rememberLauncherForImagePicker { uri ->
//        onImageSelected(uri)
//    }

    // Button to pick an image
    Button(
        onClick = {
//            launcher.launch(Unit)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(16.dp)
    ) {
        Text("Pick Image")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun AddMemoryForm(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(selectedDate.time)
        )
    }
    var locationValue by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // This state variable is used to force recomposition when the date is selected
    var dateSelected by remember { mutableStateOf(false) }
    var isUploaded by remember { mutableStateOf(false) }


    val imageUploadProgress by remember { mutableStateOf(0) }

    val cameraPermissionState = rememberPermissionState(READ_EXTERNAL_STORAGE)
    val dataStore = StoreData(context = LocalContext.current)
    val id = dataStore.getId.collectAsState(initial = "")

    val context = LocalContext.current
//    val storage = Firebase.database
    val database: DatabaseReference = Firebase.database.reference
    val storage = Firebase.database
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    var isGranted by remember { mutableStateOf(false) }
    var isGranted2 by remember { mutableStateOf(false) }
    var isDatePickerVisible by remember { mutableStateOf(false) }

    val launcher1 = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted = it
    }
    val launcher2 = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted2 = it
    }

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
            print(capturedImageUri)
            Log.d("ddd", capturedImageUri.toString())

        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var latititude by remember { mutableStateOf<Double?>(0.0) }
    var longitude by remember { mutableStateOf<Double?>(0.0) }
    ProvideLocation { location1 ->
        currentLocation = location1
        Log.d("ddd", currentLocation.toString())
        latititude = currentLocation!!.latitude
        longitude = currentLocation!!.longitude

    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), //PickVisualMedia
        onResult = { uri -> selectedImageUri = uri }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        // Image Upload
        if (capturedImageUri.path?.isNotEmpty() == true) {
            AsyncImage(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp)
                    .clip(MaterialTheme.shapes.medium),
                model = capturedImageUri,
                contentDescription = null
            )
        } else {
            Image(
                painter = if (imageUri != null) rememberAsyncImagePainter(imageUri!!) else painterResource(
                    id = R.drawable.ic_launcher_foreground
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Select Image Button
            Button(onClick = {
                if (isGranted) {

                    launcher.launch("image/*")
                    capturedImageUri = Uri.EMPTY
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        launcher1.launch(READ_MEDIA_IMAGES)
                    else
                        launcher1.launch(READ_EXTERNAL_STORAGE)

                }

            }) {
                Text(" Get From Gallery")
            }
            Button(onClick = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    // Request a permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
//                if (isGranted) {
//                    val file = createImageFile()
//                    val uri = FileProvider.getUriForFile(
//                        Objects.requireNonNull(context),
//                        BuildConfig.APPLICATION_ID + ".provider", file
//                    )
//
//
//                   // launcher.launch("image/*")
//
//                } else {
//
//                    launcher1.launch(Camera)
//                }

            }) {
                Text("Get From Camera")
            }
            // Upload Image Progress
            if (imageUploadProgress > 0) {
                CircularProgressIndicator(
                    progress = imageUploadProgress / 100f,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = eventDate,
            onValueChange = { eventDate = it },
            label = { Text("Event Date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                },
            trailingIcon = {
                IconButton(onClick = {
//                    isDatePickerVisible = true
                }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Picker")
                }
            },
//            readOnly = ,
//            keyboardOptions = KeyboardOptions.Default.copy(
//                keyboardType = KeyboardType.Decimal
//            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
//        if (isDatePickerVisible) {
//            DatePicker(
//                date = selectedDate,
//                onDateChange = {
//                    selectedDate = it
//                    isDatePickerVisible = false
//                }
//            )
//        }
        // Location
        OutlinedTextField(
            value = locationValue,
            onValueChange = { locationValue = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = {
            Log.d("location1", locationValue)

            if (isGranted2) {

                try {
                    Log.d("location2", latititude.toString())
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address>? =
                        geocoder.getFromLocation(latititude!!, longitude!!, 1)
                    val cityName: String = addresses!![0].getAddressLine(0)
//                    val stateName: String = addresses[0].getAddressLine(1)
//                    val countryName: String = addresses[0].getAddressLine(2)
                    locationValue = "$cityName"
                } catch (e: Exception) {
                    e.toString()
                }
                // Get the location
            } else {
                Log.d("location3", locationValue)
                launcher2.launch(ACCESS_FINE_LOCATION)
            }

        }) {
            Text("Get Current Location")
        }

        // Upload Image Progress
        if (imageUploadProgress > 0) {
            CircularProgressIndicator(
                progress = imageUploadProgress / 100f,
                modifier = Modifier.size(24.dp)
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Image Upload Button

        // Save Button
        if (!isUploaded) {
            Button(
                onClick = {
                    // Validate and save memory
                    if (title.isNotEmpty() && locationValue.isNotEmpty() && (imageUri != null || capturedImageUri.path?.isNotEmpty() == true) && description.isNotEmpty()) {
                        isUploaded = true
                        if (capturedImageUri.path?.isNotEmpty() != true) {
                            uploadImageToStorage(
                                uri = imageUri!!,
                                context = context,
                                title = title,
                                location = locationValue,
                                dateTime = eventDate,
                                description = description,
                                onResult = {
                                    if (it) {
                                        navController.navigate("dashboard")
                                    } else {
                                        isUploaded = false
                                        Toast.makeText(
                                            context,
                                            "Something went wrong please try again",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                })
                        } else {
                            uploadImageToStorage(
                                uri = capturedImageUri,
                                context = context,
                                title = title,
                                location = locationValue,
                                dateTime = eventDate,
                                description = description,
                                onResult = {
                                    if (it) {
                                        navController.navigate("dashboard")
                                    } else {
                                        isUploaded = false
                                        Toast.makeText(
                                            context,
                                            "Something went wrong please try again",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                })
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save Memory")
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // semi-transparent background
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.primary)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}


@Composable
fun ProvideLocation(callback: (Location) -> Unit) {
    val context = LocalContext.current
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    var location by remember { mutableStateOf<Location?>(null) }

    DisposableEffect(context) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    location = it
                    callback(it)
                }
            }
        }

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval = 1000
        }

        if (context.checkSelfPermission(ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        onDispose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // You can use the 'location' variable wherever you need the current location
}

@SuppressLint("SuspiciousIndentation")
fun uploadImageToStorage(
    uri: Uri,
    context: Context,
    title: String,
    description: String,
    location: String,
    dateTime: String,
    onResult: (success: Boolean) -> Unit
) {
    val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    val uniqueID = UUID.randomUUID().toString()
    val imageRef = storageRef.child("images/$uniqueID.jpg")
    val uploadTask = imageRef.putFile(uri)
    uploadTask.addOnSuccessListener {
        // Image upload successful
        Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()

    }.addOnFailureListener { e ->
        // Image upload failed
        Toast.makeText(context, "Image upload failed: $e", Toast.LENGTH_SHORT).show()
    }
    imageRef.putFile(uri)
        .addOnSuccessListener {
            // Image uploaded successfully, now get the download URL
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Save the download URL in the Realtime Database
                val dataStore = StoreData(context = context)

                val newMemory =
                    MemoryData(location, dateTime, description, downloadUrl.toString(), title)
                saveMemoryToDatabase(newMemory, onResult = { success ->
                    onResult(success)
                })

//                currentDate?.let { it1 ->
//                    saveImageData(
//                        downloadUrl.toString(),
//                        it1,
//                        etTitle.text.toString(),
//                        etDescription.text.toString()
//                    )
            }
//                    saveImageUrlToDatabase(downloadUrl.toString())
        }
        .addOnFailureListener { exception ->
            // Handle any errors that occurred during the upload
            // e.g., Toast.makeText(this, "Image upload failed: $exception", Toast.LENGTH_LONG).show()
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(
    date: Calendar,
    onDateChange: (Calendar) -> Unit
) {
    MaterialTheme {
        DatePicker(
            date = date,
            onDateChange = onDateChange,
//            modifier = Modifier.padding(16.dp)
        )
    }
}

fun saveMemoryToDatabase(memory: MemoryData, onResult: (success: Boolean) -> Unit) {
    // Save memory to Firebase Realtime Database
    val database: DatabaseReference = Firebase.database.reference

    val newMemoryKey = database.child("MemoryData").child(FirebaseAuth.getInstance().currentUser!!.uid).push().key
    if (newMemoryKey != null) {
        database.child("MemoryData").child(FirebaseAuth.getInstance().currentUser!!.uid).child(newMemoryKey).setValue(memory)
        onResult(true)
    } else {
        onResult(false)
    }
}

