import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class Controller implements Initializable {
    @FXML
    private Button radioOnBTN;
    @FXML
    private Button radioOffBTN;
    private File dir;
    private File[] files;
    private ArrayList<File> songs;
    private int songNumber;
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private boolean starting = false;
    private boolean prepared = false;
    private String stream = "https://maximum.hostingradio.ru/maximum96.aacp";

    @FXML
    private Label songLabel;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private Button playBTN;
    @FXML
    private Button pauseBTN;
    @FXML
    private Button rewBTN;
    @FXML
    private Button ffwBTN;
    @FXML
    private Button resetBTN;
    @FXML
    private Slider volumeSlider;

    private Media media;
    private MediaPlayer mediaPlayer;

    public void playMedia() {
        beginTimer();
        mediaPlayer.play();
    }

    public void pauseMedia() {
        mediaPlayer.pause();
    }

    public void rewriteMedia() {
        if (songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
            if (running) {
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMedia();
        } else {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();
            if (running) {
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMedia();
        }
    }

    public void forwardMedia(ActionEvent actionEvent) {
        if (songNumber < songs.size() - 1) {
            songNumber++;
            mediaPlayer.stop();
            if (running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMedia();
        } else {
            songNumber = 0;
            mediaPlayer.stop();
            if (running){
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMedia();
        }
    }

    public void resetMedia() {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0.0));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<File>();
        dir = new File("music");
        files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                songs.add(file);
                System.out.println(file);
            }
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
        songProgressBar.setStyle("-fx-background: #000000;");
        songProgressBar.setStyle("-fx-accent: #FF0000;");
//        Console con = System.console();
//        String urlString = con.readLine("Введите адрес радиопотока: ");


    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current / end);
                if (current / end == 1) {
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    public void playRadio(ActionEvent actionEvent) {
        // Европа-Плюс
        String urlString = "https://maximum.hostingradio.ru/maximum96.aacp";
        Console con = System.console();
        //String urlString = con.readLine("Введите url радио потока: ");
        try {
            URL url = new URL(urlString);
            InputStream fin = url.openStream();
            InputStream is = new BufferedInputStream(fin);

            Player player;
            player = new Player(is);
            player.play();
        }
        catch (FileNotFoundException e)
        {
            con.printf("Url %s не найден:", urlString);
        }
        catch (Exception e)
        {
            con.printf("При проигрывании с потока %s возникла следующая ошибка:", urlString);
            con.printf(e.toString());
        }

    }

    public void stopRadio(ActionEvent actionEvent) {
    }
}
