package com.androhub.workmanagerexample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.androhub.workmanagerexample.ui.theme.WorkManagerExampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkManagerExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }

        listenWorkerUpdate()
    }

    private fun listenWorkerUpdate() {
        lifecycleScope.launch {
            WorkManager.getInstance(this@MainActivity)
                .getWorkInfoByIdFlow(MyWorker.oneTimeWorkRequest.id)
                .collect { info ->
                    if (info?.state == WorkInfo.State.RUNNING) {
                        Toast.makeText(this@MainActivity, "Work is Running", Toast.LENGTH_SHORT)
                            .show()
                    } else if (info?.state == WorkInfo.State.SUCCEEDED) {
                        Toast.makeText(this@MainActivity, "Work is Completed", Toast.LENGTH_SHORT)
                            .show()
                    } else if (info?.state == WorkInfo.State.CANCELLED) {
                        Toast.makeText(this@MainActivity, "Work is Cancelled", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

            WorkManager.getInstance(this@MainActivity)
                .getWorkInfoByIdFlow(MyWorker.periodicWorkRequest.id)
                .collect { info ->
                    if (info?.state == WorkInfo.State.RUNNING) {
                        Toast.makeText(this@MainActivity, "Work is Running", Toast.LENGTH_SHORT)
                            .show()
                    } else if (info?.state == WorkInfo.State.SUCCEEDED) {
                        Toast.makeText(this@MainActivity, "Work is Completed", Toast.LENGTH_SHORT)
                            .show()
                    } else if (info?.state == WorkInfo.State.CANCELLED) {
                        Toast.makeText(this@MainActivity, "Work is Cancelled", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column {
        ElevatedButton(onClick = {
            MyWorker.executeOneTimeWorker(context)
        }) {
            Text(text = "Start One Time Worker")
        }

        ElevatedButton(onClick = {
            MyWorker.cancelOneTimeWorker(context)
        }) {
            Text(text = "Cancel One Time Worker")
        }

        ElevatedButton(onClick = {
            MyWorker.executePeriodicWorker(context)
        }) {
            Text(text = "Start Periodic Worker")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    WorkManagerExampleTheme {
        MyApp()
    }
}