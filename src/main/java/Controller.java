import com.goxr3plus.streamplayer.stream.StreamPlayer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javazoom.jl.player.Player;


import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;


public class Controller extends StreamPlayer implements Initializable {

    public Label currentLabel;
    public Label endLabel;
    public Button europaBTN;
    public Button piterFMBTN;
    public Button retroBTN;
    public Button recordBTN;
    public Button radio7BTN;
    @FXML
    private Button fileChooserBTN;
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
    private final String primWaive = "http://inhold.org:8000/primvolna-nhk";
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
        stop();
        if (songs.size() == 0){
            chooseMedia();
        }
        beginTimer();
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(this::forwardMedia);
        mediaPlayer.play();
    }

    public void pauseMedia() {
        mediaPlayer.pause();
    }

    public void rewriteMedia() {
        if (songNumber > 0) {
            songNumber--;
            stopPlaying();
        } else {
            songNumber = songs.size() - 1;
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mediaPlayer.stop();
        if (running) {
            cancelTimer();
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());
        playMedia();
    }

    public void forwardMedia() {
        if (songNumber < songs.size() - 1) {
            songNumber++;
            stopPlaying();
        } else {
            songNumber = 0;
            stopPlaying();
        }
    }

    public void resetMedia() {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0.0));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<>();
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                running = true;
//                if (mediaPlayer.getCurrentTime() == null){
//                    chooseMedia();
//                }
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                String currentDuration = String.valueOf(new DecimalFormat("##.##").format(mediaPlayer.getCurrentTime().toMinutes()));
                String endDuration = String.valueOf(new DecimalFormat("##.##").format(media.getDuration().toMinutes()));
                songProgressBar.setProgress(current / end);
                Platform.runLater(() -> {
//                    currentLabel.setText(currentDuration);
                    endLabel.setText(currentDuration + "/" + endDuration);
                    if (media.getMetadata().get("title") != null || media.getMetadata().get("artist") != null) {
                        String title = media.getMetadata().get("title").toString();
                        String artist = media.getMetadata().get("artist").toString();
                        songLabel.setText(artist + "-" + title);
                    }
                    if (media.getMetadata().get("title") == null || media.getMetadata().get("artist") == null) {
                        songLabel.setText("Отредактируйте метаданные файла");
                    }

                });
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
            try {
                media = new Media(addres);
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
            if (this.mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    }

    public void stopRadio() {
        stop();
    }

    public void setWave(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        switch (button.getText()) {
            case "Приморская Волна":
                stop();
                startPlayRadio(primWaive);
                break;
            case "EUROPA+":
                stop();
                startPlayRadio(europaPlus);
                break;
            case "RECORD":
                stop();
                startPlayRadio(record);
                break;
            case "Радио 7":
                stop();
                startPlayRadio(relax);
                break;
            case "Ретро FM":
                stop();
                startPlayRadio(retro);
                break;
            default:
                stop();
        }

    }

    private void startPlayRadio(String station) {
        addres = station;
        thread = new Thread(this::playRadio);
        thread.start();
    }

    public void chooseMedia() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            dir = directoryChooser.showDialog(new Stage());
            if(dir != null){
                files = dir.listFiles();
                    for (File file : files) {
                        songs.add(file);
                        System.out.println(file);
                    }
                    media = new Media(songs.get(songNumber).toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
//            currentLabel.setText(mediaPlayer.getCurrentTime().toString());
                    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> mediaPlayer.setVolume(volumeSlider.getValue() * 0.01));
                    songProgressBar.setStyle("-fx-background-color: #000000;");
                    songProgressBar.setStyle("-fx-accent: #FF0000;");

            }

        }catch (NullPointerException | MediaException e) {
            Platform.runLater(() -> songLabel.setText("Выберите папку с музыкой"));
            e.printStackTrace();
        }
    }
}
