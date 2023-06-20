package org.example;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    DatagramSocket datagramSocket;
    DatagramPacket send;
    DatagramPacket receive;
    byte[] receivedArray;
    byte[] sendArray;
    InetAddress clientAddress;
    int clientPort;
    MicAndSpeaker micAndSpeaker;
    boolean stopConnection = false;

    public Server() throws IOException, LineUnavailableException {

        datagramSocket = new DatagramSocket(36978);
        receivedArray = new byte[64];
        sendArray = new byte[64];

        receive = new DatagramPacket(receivedArray,64);
        datagramSocket.receive(receive);

        clientAddress = receive.getAddress();
        clientPort = receive.getPort();

        send = new DatagramPacket(sendArray,64,receive.getAddress(),receive.getPort());

        micAndSpeaker = new MicAndSpeaker();
        new Thread(handleVoice()).start();

    }

    public Runnable handleVoice() {
        return ()-> {
            micAndSpeaker.targetLine.start();

            try {

                while (!stopConnection){

                    micAndSpeaker.targetLine.read(sendArray,0,64);
                    datagramSocket.send(send);

                    datagramSocket.receive(receive);
                    micAndSpeaker.sourceLine.write(receivedArray,0,64);
                }

            }catch (IOException e){e.printStackTrace();}
        };
    }

}


