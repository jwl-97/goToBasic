package com.ljw.myapplication

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val visualizerView: SoundVisualizerView by lazy {
        findViewById<SoundVisualizerView>(R.id.visualizerView)
    }

    private val recordButton: RecordButton by lazy {
        findViewById<RecordButton>(R.id.recodeButton)
    }

    private val recordTimeTextView: CountUpView by lazy {
        findViewById<CountUpView>(R.id.recordTimeTextView)
    }

    private val resetButton: Button by lazy {
        findViewById<Button>(R.id.resetButton)
    }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private val recordingFilePath: String by lazy {
        externalCacheDir?.absolutePath + "/recording.3gp"
    }

    private var state: State = State.BEFORE_RECORDING
        set(value) {
            /** */
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initView()
        initState()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            finish()
        }
    }

    private fun initView() {
        recordButton.updateIconWithState(state)
        visualizerView.onRequestCurrentAmplitude = { //SoundVisualizerView에 현재 maxAmplitude값을 전달
            recorder?.maxAmplitude ?: 0
        }
    }

    private fun initState() {
        state = State.BEFORE_RECORDING
    }

    //////////////////////////////////////////////////////////////////////////////

    private fun startRecording() {
        recorder = MediaRecorder()
            .apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(recordingFilePath)
                prepare()
            }

        recorder?.start()
        state = State.ON_RECORDING
        visualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }

        recorder = null
        state = State.AFTER_RECORDING
        visualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }

        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }

        player?.start()
        state = State.ON_PLAYING
        visualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        state = State.AFTER_RECORDING
        visualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
    }

    //////////////////////////////////////////////////////////////////////////////

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.recodeButton -> {
                when (state) {
                    State.BEFORE_RECORDING -> startRecording()
                    State.ON_RECORDING -> stopRecording()

                    State.AFTER_RECORDING -> startPlaying()
                    State.ON_PLAYING -> stopPlaying()
                }
            }

            R.id.resetButton -> {
                stopPlaying()
                visualizerView.clearVisualizing()
                recordTimeTextView.clearCountUp()

                state = State.BEFORE_RECORDING
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 100
    }
}