package org.example;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {

            new Server();


        }catch (IOException | LineUnavailableException e){
            e.printStackTrace();
        }

    }
}