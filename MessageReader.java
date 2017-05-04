package BattleChicks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.JSONObject;

public class MessageReader implements Runnable {

	private BufferedReader reader;
	private IncomingHandlerInterface handler;

	public MessageReader(Socket socket, BattleShipGUI gui) throws IOException {
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		handler = new IncomingHandlerInterface(gui);
	}

	@Override
	public void run() {

		while (true) {
			try {
				String read = reader.readLine();
				System.out.println("incoming: " + read);
				JSONObject message = new JSONObject(read);
				handler.handle(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
