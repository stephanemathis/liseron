package fr.rocknscrum.liseronmobile.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Generate the config file for ORMLite.
 * Should be launch every time a modification is done on a class.
 * @author Mathis
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	public static void main(String[] args) throws Exception {
	writeConfigFile("ormlite_config.txt");
	}
}