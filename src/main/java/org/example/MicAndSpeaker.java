package org.example;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;


public class MicAndSpeaker {

    SourceDataLine sourceLine;
    TargetDataLine targetLine;

    boolean micMute = false;
    boolean speakerMute = false;
    boolean isRunning = true;
    byte[] microphoneData;
    byte[] headsetData;

    public MicAndSpeaker() throws LineUnavailableException{

        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 90000, 16, 2, 4, 90000, false);

        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();


        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();

        microphoneData = new byte[64];
        headsetData = new byte[64];

    }
    public void writeToHeadsetOnce(){
        sourceLine.start();
        sourceLine.write(headsetData, 0, 64);
    }
    public void readFromMicOnce(){
        targetLine.start();
        targetLine.read(microphoneData, 0, 64);
    }

    public Runnable writeToHeadsetContinuously(){
        return () -> {
            sourceLine.start();
            while (isRunning){
                sourceLine.write(headsetData, 0, 64);
            }
            targetLine.close();
        };
    }

    public Runnable readFromMicOnceContinuously(){
        return () -> {
            targetLine.start();
            while (isRunning) {
                targetLine.read(microphoneData, 0, 64);
            }
            sourceLine.close();
        };
    }



    public  Runnable readFromMicThenWriteToHeadset(){
        return () -> {
            targetLine.start();
            sourceLine.start();
            while (isRunning) {
                targetLine.read(microphoneData, 0, 64);
                sourceLine.write(microphoneData, 0, 64);
            }
            targetLine.close();
            sourceLine.close();

        };
    }

    public void openAncCloseMic(){
        micMute = !micMute;
        if(micMute) {
            targetLine.stop();
            Arrays.fill(headsetData, (byte) 0);
        }
        else{targetLine.start();}
    }
    public void openAncCloseSpeaker(){
        speakerMute = !speakerMute;
        if(speakerMute) {
            sourceLine.stop();
        }
        else {sourceLine.start();}
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

}
