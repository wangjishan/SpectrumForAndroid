package com.sss.spectrum;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.sss.view.ColumnarView;
import com.sss.view.RotatingCircleView;
import com.sss.view.AiVoiceView;
import com.sss.view.WaveformView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Visualizer visualizer;
    private MediaPlayer player;
    private long time;

    private ColumnarView columnar;
    private WaveformView waveform;
    private RotatingCircleView rotatingCircle;
    private AiVoiceView aiVoice;

    private Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, final byte[] data, int samplingRate) {
            if (System.currentTimeMillis() - time > 1000 / AppConstant.FPS) {
                byte[] newData = new byte[AppConstant.LUMP_COUNT];
                byte abs;
                for (int i = 0; i < AppConstant.LUMP_COUNT; i++) {
                    abs = (byte) Math.abs(data[i]);
                    //Math.abs -128时越界
                    newData[i] = abs < 0 ? 127 : abs;
                }
                columnar.setWaveData(newData);
                waveform.setWaveData(newData);
                rotatingCircle.setWaveData(newData);
                aiVoice.setWaveData(newData);
                time = System.currentTimeMillis();
            }
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, final byte[] fft, int samplingRate) {
//            Log.e("SSSSS fft", fft.length + "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        columnar = findViewById(R.id.columnar);
        waveform = findViewById(R.id.waveform);
        rotatingCircle = findViewById(R.id.rotatingCircle);
        aiVoice=findViewById(R.id.aiVoice);
        XXPermissions.with(this)
                .permission("android.permission.RECORD_AUDIO")
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {

                        play();

                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
    }

    private void play() {
        player = MediaPlayer.create(MainActivity.this, R.raw.demo);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        player.setLooping(true);
        player.start();


        int mediaPlayerId = player.getAudioSessionId();
        if (visualizer == null) {
            visualizer = new Visualizer(mediaPlayerId);
        } else {
            visualizer.release();
        }
        //可视化数据的大小： getCaptureSizeRange()[0]为最小值，getCaptureSizeRange()[1]为最大值
        int captureSize = Visualizer.getCaptureSizeRange()[0];
        int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;

        visualizer.setCaptureSize(captureSize);
        visualizer.setDataCaptureListener(dataCaptureListener, captureRate, true, true);
        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        visualizer.setEnabled(true);
    }
}
