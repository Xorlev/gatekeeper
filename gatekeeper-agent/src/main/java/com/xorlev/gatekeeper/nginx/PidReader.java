package com.xorlev.gatekeeper.nginx;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class PidReader {
	 private static final Logger log = LoggerFactory.getLogger(PidReader.class);
	 
    private final File pidFile;

    public PidReader(String filename) throws FileNotFoundException {
        File file = new File(filename);

        if (file.exists()) {
            pidFile = file;
        } else {
            throw new FileNotFoundException("Pidfile not found: " + file.getAbsolutePath());
        }
    }

    public int getPid() {
    	BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(pidFile));
            String line = reader.readLine();

            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            throw new InvalidPidException("Could not parse pid", e);
        } catch (IOException e) {
            throw new InvalidPidException("Could not read pid, " + e.getMessage(), e);
        } finally {
        	if ( reader != null ) {
        		try {
        			reader.close();
        		} catch(IOException ioe) {
        			log.error("File reader close error = " + ioe.getMessage(), ioe);
        		}
        	}
        }
    }
}
