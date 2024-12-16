@file:OptIn(UiToolingDataApi::class)

package com.example.locationapp
import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme
import androidx.activity.viewModels
import androidx.compose.ui.tooling.data.UiToolingDataApi
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                 MyApp(viewModel)
                }
            }
        }
    }
}
@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, context =context, viewModel )
}

@Composable
fun LocationDisplay(locationUtils: LocationUtils, context: Context, viewModel: LocationViewModel){


    val location = viewModel.location.value

    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
      onResult = {permissions ->
          if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
          && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
              //I have acces to location
              locationUtils.requestLocationUpdates(viewModel)

              locationUtils.requestLocationUpdates(viewModel = viewModel)
          }else{
               //Ask =or permission
              val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                  context as MainActivity, // from this we ask don't oprn new screen for permission do it on mainactivity screen
                  Manifest.permission.ACCESS_FINE_LOCATION
              ) ||  ActivityCompat.shouldShowRequestPermissionRationale(
                  context as MainActivity,
                  Manifest.permission.ACCESS_COARSE_LOCATION
              )

              if(rationaleRequired){
                  Toast.makeText(context, "Location Permission is required for this feature to work",
                      Toast.LENGTH_LONG).show()
              }
              else{
                  Toast.makeText(context, "Location Permission is required . please enables in the settings",
                      Toast.LENGTH_LONG).show()
              }
          }
        } )


    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        )
    {
        if (location!= null) {
       Text("Address : ${location.latitude}  ${location.longitude} \n $address")
        }else{
Text("Location Not Available")}
        //Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
//permission alread granted

            }
            else{
requestPermissionLauncher.launch(
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
)
            }
        }) {
            Text("Get Location")
        }
    }
}