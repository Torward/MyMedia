import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class ConvertToMp3 {
    static void ConvertFileToAIFF(String inputPath,
                                  String outputPath) {
        AudioFileFormat inFileFormat;
        File inFile;
        File outFile;
        try {
            inFile = new File(inputPath);
            outFile = new File(outputPath);
        } catch (NullPointerException ex) {
            System.out.println("Error: one of the ConvertFileToAIFF" +" parameters is null!");
            return;
        }
        try {
            // запрос типа файла
            inFileFormat = AudioSystem.getAudioFileFormat(inFile);
            if (inFileFormat.getType() != AudioFileFormat.Type.AIFF)
            {
                // inFile - это не AIFF, поэтому попробуем преобразовать его.
                AudioInputStream inFileAIS =
                        AudioSystem.getAudioInputStream(inFile);
                inFileAIS.reset(); // перемотка
                if (AudioSystem.isFileTypeSupported(
                        AudioFileFormat.Type.AIFF, inFileAIS)) {
                    // inFileAIS можно конвертировать в AIFF.
                    // поэтому напишите AudioInputStream
                    // в выходной файл.
                    AudioSystem.write(inFileAIS,
                            AudioFileFormat.Type.AIFF, outFile);
                    System.out.println("Successfully made AIFF file, "
                            + outFile.getPath() + ", from "
                            + inFileFormat.getType() + " file, " +
                            inFile.getPath() + ".");
                    inFileAIS.close();
                    return; // Все сделано сейчас
                } else
                    System.out.println("Предупреждение: Mp3 преобразование в "
                            + inFile.getPath()
                            + " is not currently supported by AudioSystem.");
            } else
                System.out.println("Входной файл " + inFile.getPath() +
                        " is AIFF." + " Преобразование не требуется.");
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: " + inFile.getPath()
                    + " не поддерживается тип аудиофайла!");
            return;
        } catch (IOException e) {
            System.out.println("Ошибка: сбой при попытке чтения "
                    + inFile.getPath() + "!");
            return;
        }
    }
}

