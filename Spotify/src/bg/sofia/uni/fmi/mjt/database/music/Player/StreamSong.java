package bg.sofia.uni.fmi.mjt.database.music.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

public class StreamSong implements Runnable {

	private OutputStream out;
	private Logger logger;
	private String songPath;
	private Boolean stop;

	public StreamSong(String songPath, Boolean stop, OutputStream out, Logger logger) {
		this.songPath = songPath;
		this.stop = stop;
		this.out = out;
		this.logger = logger;
	}

	@Override
	public void run() {
		File song = new File(songPath);
		try {
			AudioFormat format = AudioSystem.getAudioInputStream(song).getFormat();
			synchronized (out) {
				prepareStream(format);
				streamSong(song);
			}
		} catch (UnsupportedAudioFileException e) {
			logger.log("Error while streaming song <" + song.getName() + "> (unsupported audio format)", Level.ERROR);
		} catch (IOException e) {
			logger.log("Error while streaming song <" + song.getName() + "> (IO)", Level.ERROR);
		}

	}

	private void streamSong(File song) {
		try (InputStream in = (new FileInputStream(song))) {
			byte[] data = new byte[1024];
			int bytesRead = 0;
			while (true) {
				bytesRead = in.read(data);
				if (bytesRead == -1)
					break;
				if (stop)
					break;
				out.write(data);
			}
		} catch (FileNotFoundException e) {
			logger.log("Failed streaming song <" + song.getName() + "> (FILE NOT FOUND)", Level.ERROR);
		} catch (IOException e) {
			logger.log("Failed streaming song <" + song.getName() + "> (IOException)", Level.ERROR);
		}
	}

	private void prepareStream(AudioFormat format) {
		PrintWriter write = new PrintWriter(out, true);
		write.println("prepare stream");
		write.println(format.getEncoding());
		write.println(format.getSampleRate());
		write.println(format.getSampleSizeInBits());
		write.println(format.getChannels());
		write.println(format.getFrameSize());
		write.println(format.getFrameRate());
		write.println(format.isBigEndian());
	}

}
