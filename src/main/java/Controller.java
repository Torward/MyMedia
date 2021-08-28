import com.goxr3plus.streamplayer.stream.StreamPlayer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.util.*;


public class Controller extends StreamPlayer implements Initializable {
    public Label buffering;
    public TextField addresString;
    public Label currentLabel;
    public Label endLabel;
    public Button europaBTN;
    public Button piterFMBTN;
    public Button hitFMBTN;
    public Button MCBTN;
    public Button retroBTN;
    public Button recordBTN;
    public Button radio7BTN;
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
    private Thread thread;
    private String addres;


    private final String record = "https://hls-01-radiorecord.hostingradio.ru/record/playlist.m3u8";
    private final String retro = "https://hls-01-retro.emgsound.ru/12/playlist.m3u8";
    private final String relax = "https://hls-01-radio7.emgsound.ru/13/playlist.m3u8";
    private final String piterFM = "https://piterfm-hls.cdnvideo.ru/piterfm-live/piterfm.stream/playlist.m3u8";
    private final String hitFm = "http://c34.radioboss.fm:8126/mp3";
    private final String monteCarlo = "https://montecarlo.hostingradio.ru/montecarlo96.aacp";
    private final String europaPlus = "https://hls-02-europaplus.emgsound.ru/11/128/playlist.m3u8";

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
    private Player player;

    public void playMedia() {
        beginTimer();
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
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
            if (running) {
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMedia();
        } else {
            songNumber = 0;
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

    //Radio
    public boolean isRunning() {
        return thread != null;
    }

    public void playRadio() {
        thread = new Thread(() -> {
            Console con = System.console();

            String urlString = addres;
            try {
                URL url = new URL(urlString);
                media = new Media(urlString);
                url.openStream();
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void stop() {
        if (isRunning()) {
            thread.interrupt();
            thread = null;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    }

    public void stopRadio() {
        stop();
    }

    public void setEuropa(ActionEvent actionEvent) {
        stopRadio();
        addres = europaPlus;
       thread = new Thread(this::playRadio);
        thread.start();

    }

    public void setRecord(ActionEvent actionEvent) {
        stop();
        addres = record;
        thread = new Thread(this::playRadio);
        thread.start();
    }

    public void setPiterFM(ActionEvent actionEvent) {
        stop();
        addres = piterFM;
        thread = new Thread(this::playRadio);
        thread.start();
    }

    public void setHit(ActionEvent actionEvent) {
        stop();
        addres = hitFm;
        thread = new Thread(this::playRadio);
        thread.start();
    }

    public void setMonte(ActionEvent actionEvent) {
        stop();
        addres = monteCarlo;
        thread = new Thread(this::playRadio);
        thread.start();
    }

    public void setRadioSeven(ActionEvent actionEvent) {
        stop();
        addres = relax;
        thread = new Thread(this::playRadio);
        thread.start();
    }

    public void setRetroFM(ActionEvent actionEvent) {
        stop();
        addres = retro;
        thread = new Thread(this::playRadio);
        thread.start();
    }
}
