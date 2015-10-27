package ticTacMinMax.stream;

import static ticTacMinMax.board.twoDimensional.Board2D.PLAYER_1_TOKEN;
import static ticTacMinMax.board.twoDimensional.Board2D.PLAYER_2_TOKEN;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.jar.JarFile;

import ticTacMinMax.stream.files.Configuration;
import ticTacMinMax.stream.files.Log;
import ticTacMinMax.stream.files.SymbolImage;

public class StreamManager {
	// TODO This is by far the messiest class I have ever written. I apologize
	// to anyone reading this, please message me if you have a better idea for
	// how to organize this according to OOD best practice.
	// https://github.com/SiliciousReese.

	private static final StreamManager INSTANCE = new StreamManager();

	/**
	 * True if the program is running from inside a jar file. This is used to
	 * make sure the compressed data is handled correctly.
	 */
	private boolean runFromJar;

	// The folder where resource files are located inside the source directory.
	private Path runningFromFolder;
	// The folder to store files from the game.
	private Path gameFolder;

	private static final String configFileName = "DefaultConfig.txt";

	// TODO Program log.
	@SuppressWarnings("unused")
	private Log logFile;
	private Configuration config;
	private SymbolImage symbols;

	private HashSet<Closeable> streams;

	private StreamManager() {
		gameFolder =
				Paths.get(System.getProperties().getProperty("user.dir"))
						.resolve("src");

		runningFromFolder =
				Paths.get(System.getProperties().getProperty("java.class.path"))
						.toAbsolutePath();
		runFromJar = isJar(runningFromFolder);

		streams = new HashSet<Closeable>();

		logFile = getLog();

		Path configLocation = gameFolder.resolve("res").resolve(configFileName);
		try {
			config = getConfig(configLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}

		symbols = getSymbolImage();

		try {
			verifyGameData();
		} catch (IOException e) {
			// TODO Handle IO exception.
			e.printStackTrace();
		}
	}

	public static StreamManager getInstance() {
		return INSTANCE;
	}

	/**
	 * @param classPath
	 *            Path to file to test.
	 * @return
	 * 		True if the file looks like a jar. It "looks" like a jar if the
	 *         file does ends with ".jar" OR the content probe detects that it
	 *         is a java archive.
	 */
	private boolean isJar(Path classPath) {
		// TODO Improve and test jar checking algorithm
		boolean jar = false;

		String type = null;
		try {
			type = Files.probeContentType(classPath);
			if (type == null) {
				System.err.format("'%s' has an" + " unknown filetype.%n",
						classPath);
			}
		} catch (IOException e) {
			System.err.println(e);
		}

		// If the type contains the string "java-archive" or the filename ends
		// with jar, the file is assumed to be a jar.
		jar = type.contains("java-archive") || classPath.endsWith(".jar");

		return jar;
	}

	private Log getLog() {
		// TODO Program getLog
		Log log = null;

		return log;
	}

	private Configuration getConfig(Path pathToConfig) throws IOException {
		// TODO Separate default and user configuration.
		InputStream defaultConfig = Files.newInputStream(pathToConfig);
		streams.add(defaultConfig);

		InputStream userConfig = defaultConfig;
		streams.add(userConfig);

		Configuration config = new Configuration(defaultConfig, userConfig);

		return config;
	}

	public String getSetting(String settingName) {
		String setting = config.getProperty(settingName);
		return setting;
	}

	/**
	 * @param characterImage
	 *            The character representing which part of the symbol image to
	 *            return. This should either be the x or o character
	 * @return
	 * 		The part of the symbol image specified by item.
	 */
	public BufferedImage getSubimage(char characterImage) {
		BufferedImage image = null;

		if (characterImage == PLAYER_1_TOKEN)
			image = symbols.getXImage();
		else if (characterImage == PLAYER_2_TOKEN)
			image = symbols.getOImage();
		else
			throw new Error();

		return image;
	}

	private SymbolImage getSymbolImage() {
		SymbolImage image =
				new SymbolImage(
						gameFolder.resolve("res").resolve("symbols.png"));

		return image;
	}

	/**
	 * Make sure all the streams are closed.
	 * 
	 * @throws IOException
	 *             if the streams fail to close. There is no guarantee the
	 *             streams will all be closed if this occurs.
	 */
	public void closeAllStreams() throws IOException {
		while (streams.iterator().hasNext()) {
			Closeable item = streams.iterator().next();
			item.close();
			streams.remove(item);
		}
		streams = null;
	}

	/**
	 * Check the use data directory and make sure all data is valid. If the
	 * directory does not contain valid data, move resources from res folder to
	 * game directory.
	 */
	private void verifyGameData() throws IOException {
		// TODO Check user directory for config

		// TODO Check that the config file is valid

		// TODO If in jar extract needed files to user data directory
		// Else just copy the files that don't exist
		// Only extract files that aren't already there.

		if (runFromJar) {
			// TODO Copy files from jar
			JarFile self = new JarFile(gameFolder.toFile());
			self.close();
		} else {

		}
	}
}