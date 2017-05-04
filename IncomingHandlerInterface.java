package BattleChicks;

import org.json.JSONObject;

public class IncomingHandlerInterface {

	private static BattleShipGUI gui = new BattleShipGUI();

	public static void handle(JSONObject message) {
		String type = message.optString("type");
		switch (type) {
		case "login":
			System.out.println(type);
			break;

		case "application":
			JSONObject mess = message.optJSONObject("message");
			applicationHandler(mess);
			break;

		case "acknowledge":
			String ackMessage = message.optString("message");
			gui.updateTextArea(ackMessage);
			System.out.println(type + "> " + ackMessage);
			break;

		case "chat":
			String chat = message.optString("message");
			String name = message.optString("fromUser");
			String send = name + ": " + chat;
			gui.setChatMessage(send);
			System.out.println(type + "> " + chat);
			break;

		case "error":
			String error = message.optString("message");
			gui.updateTextArea(error);
			System.out.println(type + "> " + error);
			break;
		}
	}

	public static void applicationHandler(JSONObject mess) {
		if (mess.has("turn")) {
			BattleShipGUI.setTurn(mess.optBoolean("turn"));
		}
		else if (mess.has("hit")) {
			String coordinate = mess.optString("coordinate");
			BattleShipGUI.hitMiss(mess.optBoolean("hit"), coordinate);
		}
		else if (mess.has("win")) {
			String win = mess.optString("win");
			BattleShipGUI.setTurn(false);
			gui.updateTextArea(win + ": WINS!");
			gui.setChatMessage(win + ": WINS!");
		}
		else if (mess.has("reset")) {
			gui.updateTextArea("Game has ended please reset board.");
		}
	}
}
