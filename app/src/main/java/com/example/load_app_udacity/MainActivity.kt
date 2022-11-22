package com.example.load_app_udacity
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.load_app_udacity.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

class MainActivity : AppCompatActivity() {
    private var repo: String? = null
    private lateinit var notiManager: NotificationManager
    private var DLID: Long = 0
    private var fileName: String? = null

    private lateinit var loadingbtn: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        registerReceiver(rec, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        setSupportActionBar(binding.toolbar)
        loadingbtn = binding.included.downloadButton
        loadingbtn.setOnClickListener {downloadTheRepo()}
        loadingbtn.setState(ButtonState.Completed)
        binding.included.retrofitRadioButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) {
                repo = "https://github.com/square/retrofit"
                fileName = getString(R.string.retrofit_title)
            }
        }

        binding.included.loadAppRadioButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) {
                repo =
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                fileName = getString(R.string.load_app_title)
            }
        }
        binding.included.glideRadioButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) {
                repo = "https://github.com/bumptech/glide"
                fileName = getString(R.string.glide_title)
            }
        }

    }

    private val rec = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -11)
            if (DLID == id) {
                if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
                    val manager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val cursorColumnIndex =
                                cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            val columnIndex = if (cursorColumnIndex > -1){
                                cursorColumnIndex
                            } else {0}
                            val status = cursor.getInt(columnIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                notiManager.sendNotification(
                                    applicationContext,
                                    fileName.toString(),
                                    fileName.toString()+" downloaded"
                                    ,
                                    "Downloaded Successfully"
                                )
                                loadingbtn.setState(ButtonState.Completed)
                            } else {
                                notiManager.sendNotification(
                                    applicationContext,
                                    fileName.toString(),
                                    fileName.toString(),
                                    "Failed try again"
                                )
                                loadingbtn.setState(ButtonState.Completed)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun downloadTheRepo() {
        loadingbtn.setState(ButtonState.Clicked)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (repo != null) {
                    val fl =
                        File(getExternalFilesDir(null), "github_repo")
                    if (!fl.exists()) {
                        fl.mkdirs()
                    }
                    loadingbtn.setState(ButtonState.Loading)
                    notiManager = ContextCompat.getSystemService(
                        applicationContext,
                        NotificationManager::class.java
                    ) as NotificationManager
                    createChannel(
                        getString(R.string.notification_channel_id),
                        getString(R.string.notification_channel_name)
                    )
                    val path = "github_repo/${fileName?.split(' ')?.get(0)}.zip"
                    val request =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            DownloadManager.Request(Uri.parse(repo))
                                .setTitle(getString(R.string.app_name)).setDescription(getString(R.string.app_description))
                                .setAllowedOverMetered(true)
                                .setAllowedOverRoaming(true)
                                .setRequiresCharging(false)
                                .setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS,
                                    path
                                )
                        } else {
                            DownloadManager.Request(Uri.parse(repo))
                                .setTitle(getString(R.string.app_name))
                                .setAllowedOverMetered(true)
                                .setAllowedOverRoaming(true)
                                .setDescription(getString(R.string.app_description))
                                .setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS,
                                    path
                                )
                        }

                    val dManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    DLID = dManager.enqueue(request)
                } else {
                    Toast.makeText(this, "select any file to continue", Toast.LENGTH_SHORT)
                        .show()
                    loadingbtn.setState(ButtonState.Completed)
                }
            },
            250
        )


    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.notification_channel_description)
            val notiManager = getSystemService(NotificationManager::class.java)
            notiManager.createNotificationChannel(notificationChannel)
        }
    }
}
