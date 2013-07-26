package main.java.net.thumbtack.updateNotifierBackend;

import java.io.IOException;

import main.java.net.thumbtack.updateNotifierBackend.accountManagment.AccountManager;
import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import main.java.net.thumbtack.updateNotifierBackend.updateChecker.UpdateChecker;
import main.java.net.thumbtack.updateNotifierBackend.HTTPServer.HTTPServer;
import main.java.net.thumbtack.updateNotifierBackend.IOService.RESTfulService;


public class UpdateNotifierBackend {

	private static final String EXIT_STRING = "exit";
	private static final int PORT = 8080;
	private static DatabaseService databaseService = null;
	private static AccountManager accountManager = null;
	private static RESTfulService RESTfulService = null;
	private static UpdateChecker updateChecker = null;
	
	/**
	 * @param args not used
	 */
	public static void main(String[] args) {
		
		databaseService = new DatabaseService();
		updateChecker = new UpdateChecker(databaseService);
		updateChecker.start();
		accountManager = new AccountManager(databaseService);
		RESTfulService = new RESTfulService(accountManager, databaseService);

        Thread server = null;
		try {
			server = new HTTPServer(PORT, RESTfulService);
			
	        server.setDaemon(false);
	        server.start();

			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateChecker.stop();
		server.interrupt();
	}

}
