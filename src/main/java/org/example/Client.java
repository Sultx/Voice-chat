package org.example;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    DatagramSocket datagramSocket;
    DatagramPacket send;
    DatagramPacket receive;
    byte[] receivedArray;
    byte[] sendArray;
    InetAddress clientAddress;
    int clientPort;
    MicAndSpeaker micAndSpeaker;
    boolean stopConnection = false;
    public Client() throws IOException, LineUnavailableException {
        InetAddress inetAddress = InetAddress.getByName("192.168.100.10");
        final int PORT = 36978;
        datagramSocket = new DatagramSocket();

        final int BUFFER_SIZE = 64;
        receivedArray = new byte[BUFFER_SIZE];
        sendArray = new byte[BUFFER_SIZE];


        send = new DatagramPacket(sendArray, BUFFER_SIZE, inetAddress, PORT);
        datagramSocket.send(send);

        receive = new DatagramPacket(receivedArray, BUFFER_SIZE);

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

