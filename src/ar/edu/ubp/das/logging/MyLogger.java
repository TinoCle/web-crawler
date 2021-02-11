package ar.edu.ubp.das.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
	private Logger logger;
	public static final String INFO = "INFO";
	public static final String WARNING = "WARN";
	public static final String ERROR = "ERROR";
	
	public MyLogger(String className) {
		this.logger = LogManager.getLogger(className);
	}

	public void log(String level, String message) {
		if (level == null) {
			level = "INFO";
		}
		logger.log(Level.getLevel(level), message);
	}
}
