package mazegame.logic;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// Created by wael
public class musicPlayer {

    private Clip backgroundClip;
    private FloatControl volumeControl;

    public void playBackgroundMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);

//            volumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.VOLUME);

            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
        } catch (Exception e) {
            //handle wrong paths
            System.out.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playSoundEffect(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            //handle wrong paths bardo
            System.out.println("Error playing sound effect: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        backgroundClip.stop();
        backgroundClip.close();
    }
    //set the volume (0.0 to 1.0)
    public void setVolume(float volume) {
        //assume that i'll put 0 - 1f values
        volumeControl.setValue(volume);
    }
}