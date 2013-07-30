package main.java.net.thumbtack.updateNotifierBackend;

import java.io.IOException;

import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import main.java.net.thumbtack.updateNotifierBackend.updateChecker.UpdateChecker;
import main.java.net.thumbtack.updateNotifierBackend.HTTPServer.HTTPServer;
import main.java.net.thumbtack.updateNotifierBackend.HTTPServer.UpdateNotifierRequestHandler;


public class UpdateNotifierBackend {

	private static final int PORT = 8080;
	private static DatabaseService databaseService = null;
	private static UpdateNotifierRequestHandler RESTfulService = null;
	private static UpdateChecker updateChecker = null;
	
	/**
	 * @param args not used
	 */
	public static void main(String[] args) {
		
		databaseService = new DatabaseService();
		updateChecker = new UpdateChecker(databaseService);
		updateChecker.start();
		RESTfulService = new UpdateNotifierRequestHandler(databaseService);

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
