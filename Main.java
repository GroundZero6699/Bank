package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 * 
 * Detta är program starten Vi skapar ett nytt bank objekt och ett objekt av klassen SceneView som
 * är själva det grafiska gränssnittet.
 * 
 */

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	// instans variabel
	private static ViewController controller;
	
	// metoden skapar ett nytt bank objekt och ett view objekt.
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Fjällbanken Norrbotten");
		BankLogic bank = new BankLogic();
		SceneView view = new SceneView();
		controller = new ViewController(bank, view);
		
		view.startScene(primaryStage);
	}
	// Här startar programmet
	public static void main(String[] args) {
		
		launch(args);
	}
	
	// åtkomstmetod för "kontroll klassen"
	public static ViewController getController() {
		return controller;
	}
}
