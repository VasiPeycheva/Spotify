package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import bg.sofia.uni.fmi.mjt.logger.Level;
import bg.sofia.uni.fmi.mjt.logger.Logger;

/**
 * Listen for Server response
 * 
 * @author Vasi Peycheva
 *
 */
public class ServerReader implements Runnable {

	private BufferedReader input;
	private InputStream stream;
	private Logger logger;
	private Socket socket;

	public ServerReader(Socket socket, Logger logger) {
		this.socket = socket;
		try {
			stream = socket.getInputStream();
		} catch (IOException e) {
			this.logger.log("Failed getting socket input stream (Server Reader)", Level.ERROR);
		}
		input = new BufferedReader(new InputStreamReader(stream));
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			String response = null;
			while (!socket.isClosed() && ((response = input.readLine()) != null)) {
				if (response.equals("prepare stream")) {
					play();
				} else {
					System.out.println(response);
				}
			}
		} catch (IOException e) {
			logger.log("Unable to get server response", Level.ERROR);
		}
	}

	/**
	 * Play client requested song
	 */
	// TODO : REPAIR
	private void play() {

		try {
			Encoding encoding = new Encoding(input.readLine());
			float sampleRate = Float.parseFloat(input.readLine());
			int sampleSizeInBits = Integer.parseInt(input.readLine());
			int channels = Integer.parseInt(input.readLine());
			int frameSize = Integer.parseInt(input.readLine());
			float frameRate = Float.parseFloat(input.readLine());
			boolean bigEndian = Boolean.parseBoolean(input.readLine());

			AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate,
					bigEndian);

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open();
			dataLine.start();

			byte[] data = new byte[1024];
			int bytesRead = 0;

			while (true) {
				bytesRead = stream.read(data);
				if (stream.available() == 0)
					break;
				dataLine.write(data, 0, bytesRead);
			}
			dataLine.drain();
			dataLine.stop();
			dataLine.close();
		} catch (IOException e) {
			logger.log("Failed streaming song (IOException) ", Level.ERROR);
		} catch (LineUnavailableException e) {
			logger.log("Failed streaming song (unavailable line)", Level.ERROR);
		}
	}

}
