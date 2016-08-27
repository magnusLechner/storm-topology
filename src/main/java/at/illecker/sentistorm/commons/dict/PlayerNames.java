package at.illecker.sentistorm.commons.dict;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Configuration;
import at.illecker.sentistorm.commons.util.io.FileUtils;
import at.illecker.sentistorm.commons.util.io.IOUtils;
import at.illecker.sentistorm.commons.util.io.SerializationUtils;

public class PlayerNames {

	private static final Logger LOG = LoggerFactory.getLogger(FirstNames.class);
	private static final PlayerNames INSTANCE = new PlayerNames();

	private Set<String> m_playerNames = null;

	@SuppressWarnings("unchecked")
	private PlayerNames() {
		for (String file : Configuration.getPlayerNames()) {
			// Try deserialization of file
			String serializationFile = file + ".ser";
			if (IOUtils.exists(serializationFile)) {
				LOG.info("Deserialize PlayerNames from: " + serializationFile);
				if (m_playerNames == null) {
					m_playerNames = SerializationUtils.deserialize(serializationFile);
				} else {
					m_playerNames.addAll((Set<String>) SerializationUtils.deserialize(serializationFile));
				}
			} else {
				LOG.info("Load PlayerNames from: " + file);
				if (m_playerNames == null) {
					m_playerNames = FileUtils.readFile(file, true);
					SerializationUtils.serializeCollection(m_playerNames, serializationFile);
				} else {
					Set<String> twitchEmoticons = FileUtils.readFile(file, true);
					SerializationUtils.serializeCollection(twitchEmoticons, serializationFile);
					m_playerNames.addAll(twitchEmoticons);
				}
			}
		}
	}

	public static PlayerNames getInstance() {
		return INSTANCE;
	}

	public boolean isPlayerName(String value) {
		return m_playerNames.contains(value.toLowerCase());
	}

	public Set<String> getPlayerNames() {
		return m_playerNames;
	}

	public static void main(String[] args) {
		PlayerNames playerNames = PlayerNames.getInstance();
		String[] testPlayerNames = new String[] { "dodo8", "f0rest", "JW", "olofmeister", "NAVI", "get_right" };
		for (String s : testPlayerNames) {
			System.out.println("isPlayerName(" + s + "): " + playerNames.isPlayerName(s));
		}
	}

}
