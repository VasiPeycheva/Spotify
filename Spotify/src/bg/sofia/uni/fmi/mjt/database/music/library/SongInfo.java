package bg.sofia.uni.fmi.mjt.database.music.library;

public class SongInfo {
	public Integer hitRate;
	public String path;

	SongInfo(String path) {
		this.path = path;
		hitRate = 0;
	}
}
