package au.com.softclient.opus_2

import au.com.softclient.opus_2.opussrc.AudioCapture
import au.com.softclient.opus_2.opussrc.AudioPlayback
import au.com.softclient.opus_2.opussrc.AudioProcessor


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import au.com.softclient.opus_2.ui.theme.Opus_2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var audioProcessor: AudioProcessor
    private lateinit var audioCapture: AudioCapture
    private lateinit var audioPlayback: AudioPlayback

    private var isRecording by mutableStateOf(false)
    private var isPlaying by mutableStateOf(false)
    private val recordedData = mutableListOf<ByteArray>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeAudioComponents()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAudioPermission()

        setContent {
            Opus_2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AudioUI(
                        isRecording = isRecording,
                        isPlaying = isPlaying,
                        onStartRecording = { startRecording() },
                        onStopRecording = { stopRecording() },
                        onStartPlayback = { startPlayback() }
                    )
                }
            }
        }
    }

    private fun requestAudioPermission() {
        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                initializeAudioComponents()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun initializeAudioComponents() {
        Log.d("MainActivity", "Initializing audio components")
        audioProcessor = AudioProcessor()
        audioCapture = AudioCapture(this, audioProcessor, recordedData)
        audioPlayback = AudioPlayback(audioProcessor)
    }

    private fun startRecording() {
        Log.d("MainActivity", "Starting recording")
        isRecording = true
        recordedData.clear()
        audioCapture.startRecording()
    }

    private fun stopRecording() {
        Log.d("MainActivity", "Stopping recording")
        isRecording = false
        audioCapture.stopRecording()
    }

    private fun startPlayback() {
        Log.d("MainActivity", "Starting playback")
        isPlaying = true
        audioPlayback.startPlayback()

        // Play recorded audio
        recordedData.forEach { encodedData ->
            Log.d("MainActivity", "Playing encoded data of size: ${encodedData.size}")
            audioPlayback.playAudio(encodedData)
        }
        isPlaying = false
    }

    override fun onDestroy() {
        super.onDestroy()
        audioCapture.stopRecording()
        audioPlayback.stopPlayback()
    }
}

@Composable
fun AudioUI(
    isRecording: Boolean,
    isPlaying: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onStartPlayback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (isRecording) {
                    onStopRecording()
                } else {
                    onStartRecording()
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
        }

        Button(
            onClick = onStartPlayback,
            modifier = Modifier.padding(8.dp),
            enabled = !isPlaying
        ) {
            Text("Start Playback")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AudioUIPreview() {
    Opus_2Theme {
        AudioUI(
            isRecording = false,
            isPlaying = false,
            onStartRecording = {},
            onStopRecording = {},
            onStartPlayback = {}
        )
    }
}

//class MainActivity : ComponentActivity() {
//    private lateinit var audioProcessor: AudioProcessor
//    private lateinit var audioCapture: AudioCapture
//    private lateinit var audioPlayback: AudioPlayback
//
//    private var isRecording by mutableStateOf(false)
//    private var isPlaying by mutableStateOf(false)
//    private val recordedData = mutableListOf<ByteArray>()
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            initializeAudioComponents()
//        } else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requestAudioPermission()
//
//        setContent {
//            Opus_2Theme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    AudioUI(
//                        isRecording = isRecording,
//                        isPlaying = isPlaying,
//                        onStartRecording = { startRecording() },
//                        onStopRecording = { stopRecording() },
//                        onStartPlayback = { startPlayback() }
//                    )
//                }
//            }
//        }
//    }
//
//    private fun requestAudioPermission() {
//        when {
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                initializeAudioComponents()
//            }
//            else -> {
//                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//            }
//        }
//    }
//
//    private fun initializeAudioComponents() {
//        Log.d("MainActivity", "Initializing audio components")
//        audioProcessor = AudioProcessor()
//        audioCapture = AudioCapture(this, audioProcessor, recordedData)
//        audioPlayback = AudioPlayback(audioProcessor)
//    }
//
//    private fun startRecording() {
//        Log.d("MainActivity", "Starting recording")
//        isRecording = true
//        recordedData.clear()
//        audioCapture.startRecording()
//    }
//
//    private fun stopRecording() {
//        Log.d("MainActivity", "Stopping recording")
//        isRecording = false
//        audioCapture.stopRecording()
//    }
//
//    private fun startPlayback() {
//        Log.d("MainActivity", "Starting playback")
//        isPlaying = true
//        audioPlayback.startPlayback()
//
//        // Play recorded audio
//        recordedData.forEach { encodedData ->
//            Log.d("MainActivity", "Playing encoded data of size: ${encodedData.size}")
//            audioPlayback.playAudio(encodedData)
//        }
//        isPlaying = false
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        audioCapture.stopRecording()
//        audioPlayback.stopPlayback()
//    }
//}
//
//@Composable
//fun AudioUI(
//    isRecording: Boolean,
//    isPlaying: Boolean,
//    onStartRecording: () -> Unit,
//    onStopRecording: () -> Unit,
//    onStartPlayback: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(
//            onClick = {
//                if (isRecording) {
//                    onStopRecording()
//                } else {
//                    onStartRecording()
//                }
//            },
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Text(if (isRecording) "Stop Recording" else "Start Recording")
//        }
//
//        Button(
//            onClick = onStartPlayback,
//            modifier = Modifier.padding(8.dp),
//            enabled = !isPlaying
//        ) {
//            Text("Start Playback")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun AudioUIPreview() {
//    Opus_2Theme {
//        AudioUI(
//            isRecording = false,
//            isPlaying = false,
//            onStartRecording = {},
//            onStopRecording = {},
//            onStartPlayback = {}
//        )
//    }
//}



//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import au.com.softclient.opus_2.ui.theme.Opus_2Theme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            Opus_2Theme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Opus_2Theme {
//        Greeting("Android")
//    }
//}