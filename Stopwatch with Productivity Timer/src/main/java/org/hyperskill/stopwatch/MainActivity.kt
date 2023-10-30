package org.hyperskill.stopwatch
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlin.random.Random
const val CHANNEL_ID = "org.hyperskill"

class MainActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var settingsButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notification: Notification
    private val handler = Handler()
    private var isRunning = false
    private var elapsedTime: Long = 0
    private var upperLimit = Int.MAX_VALUE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTextView = findViewById(R.id.textView)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)
        settingsButton = findViewById(R.id.settingsButton)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        startButton.setOnClickListener {
            startTimer()
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        settingsButton.setOnClickListener {
            openAlert()

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification"
            val descriptionText = "Time exceeded"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager=
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notificationBuilder = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Stopwatch")
                .setContentText("Time Exceeded")
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
            notification = notificationBuilder.build()



        }

    }


    private fun startTimer() {
        if (!isRunning) {

                progressBar.visibility = View.VISIBLE
                settingsButton.isEnabled = false
                isRunning = true
                handler.postDelayed(updateTime, 1000)
                handler.postDelayed(updateColor,1000)
//        startButton.text = "Stop"

        }
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTime)
        startButton.text = "Start"
        isRunning = false
    }

    private fun resetTimer() {
        elapsedTime = 0
        updateTimerText(elapsedTime)
        progressBar.visibility = View.INVISIBLE
        settingsButton.isEnabled = true
        timeTextView.setTextColor(Color.GRAY)
        handler.removeCallbacks(updateTime)
        isRunning = false
    }

    private val updateTime = object : Runnable {
        override fun run() {
            elapsedTime++

            updateTimerText(elapsedTime)
            if (elapsedTime >= upperLimit && upperLimit > 0) {
                timeTextView.setTextColor(Color.RED)
                notificationManager.notify(393939, notification.apply { flags += Notification.FLAG_INSISTENT })


            }
            handler.postDelayed(this, 1000)
        }
    }

    private val updateColor = object : Runnable {
        override fun run() {
            val color = Random.nextInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.indeterminateTintList = ColorStateList.valueOf(color)
            }
            handler.postDelayed(this, 1000)
        }
    }


    private fun updateTimerText(timeInSeconds: Long) {
        val minutes = timeInSeconds / 60
        val seconds = timeInSeconds % 60
        timeTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun openAlert() {
        val contentView = LayoutInflater.from(this).inflate(R.layout.dialog_main, null, false)
        AlertDialog.Builder(this)
            .setTitle("Set upper limit in seconds")
            .setView(contentView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                Toast.makeText(this, editText.text, Toast.LENGTH_SHORT).show()
                if (editText.text.isNotEmpty()){
                    val newLimit = editText.text.toString()
                    upperLimit = newLimit.toInt()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}