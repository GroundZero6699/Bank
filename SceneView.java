package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 * 
 * Detta är klassen som skapar det grafiska gränssnittet till applikationen BankApp 
 * 
 * 
 */

import java.math.BigDecimal;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SceneView {
	
	// Instans variablar
	private Button submit;
	private Button clear;
	private Button search;
	private Button create;
	private Button overView;
	private Button start;
	private Button open;
	private Button deposit;
	private Button withdraw;
	private Button transactions;
	private Button saveTransaction;
	private CheckBox savingsAccount;
	private CheckBox creditAccount;
	private Button erase;
	private Label error;
	private Label success;
	private TextField name;
	private TextField surname;
	private TextField pNo;
	private TextField amountField;
	private ListView<String> customerList;
	private Text information;
	
	// konstruktor
	public SceneView() {
		submit = new Button("Ändra");
		clear = new Button("Rensa");
		search = new Button("Sök");
		start = new Button("Start sida");
		create = new Button("Skapa");
		overView = new Button("Kund lista");
		open = new Button("Öppna konto");
		transactions = new Button("Transaktioner");
		savingsAccount = new CheckBox("Sparkonto");
		creditAccount = new CheckBox("Kreditkonto");
		erase = new Button("Avsluta");
		error = new Label("Ett fel uppstod\nKontrollera uppgifterna!");
		error.setId("error");
		success = new Label();
		success.setId("success");
		name = new TextField();
		name.setPromptText("Förnamn");
		surname = new TextField();
		surname.setPromptText("Efternamn");
		amountField = new TextField();
		pNo = new TextField();
		pNo.setPromptText("ÅÅÅÅMMDD");
		customerList = new ListView<>();
		deposit = new Button("Insättning");
		withdraw = new Button("Uttag");
		saveTransaction = new Button("Spara transaktioner");
		information = new Text();
		information.setId("information");
	}
	
	// Start fönstret 
	public void startScene(Stage primaryStage) {
		// sätter ikonen i aktivitetfältet 
		primaryStage.getIcons().add(new Image("dollar.jpg"));
		MenuBar menu = new MenuBar();
		menu.setId("top");
		BorderPane root = new BorderPane();
		VBox center = new VBox();
		center.setId("center");
		VBox right = new VBox();
		right.setId("right");
		
		Label pNr = new Label("Sök kund");
		pNr.setVisible(false);
		search.setVisible(false);
		pNo.setVisible(false);
		Button startAccount = new Button("Gå till Konton");
		startAccount.setVisible(false);
		information.setText("Välj att skapa nya kunder\ni menyn alternativt\nladda in en befintlig fil med kunder\n"
				+ "Eller spara ner alla kunder till lokal fil\n"
				+ "med knapparna till vänster.");
		
		error.setVisible(false);
		success.setVisible(false);
		
		// Menyn med olika flikar 
		Menu customer = new Menu("Kund");
		MenuItem newCustomer = new MenuItem("Ny kund");					
		MenuItem nameChange = new MenuItem("Ändra namn");
		MenuItem customerOverView = new MenuItem("kunder / konton");
		MenuItem cancelCustomer = new MenuItem("Avsluta kund");
		
		customer.getItems().addAll(newCustomer, nameChange, customerOverView, cancelCustomer);
		
		Menu account = new Menu("Konto");
		MenuItem newAccount = new MenuItem("Öppna konto");
		MenuItem transactions = new MenuItem("Insättning / Uttag");
		MenuItem closeAccount = new MenuItem("Avsluta konto");
		
		account.getItems().addAll(newAccount, transactions, closeAccount);
		
		Menu file = new Menu("Arkiv");
		MenuItem save = new MenuItem("Spara");
		MenuItem load = new MenuItem("Hämta");
		
		file.getItems().addAll(save, load);
		
		Menu exit = new Menu("Avsluta");
		MenuItem exitItem = new MenuItem("Avsluta");
		
		exit.getItems().add(exitItem);
		
		menu.getMenus().addAll(customer, account, file, exit);
		
		customerList.setVisible(false);
		overView.setVisible(false);
		
		// sparar kunder och konton till fil
		save.setOnAction(e -> {
			Main.getController().saveBank();
		});
		
		// Hämtar kunder och konton från fil
		load.setOnAction(e -> {
			Main.getController().loadBank();
		});
		
		// Händelse vid tryck på avsluta i menyn.
		exitItem.setOnAction(e -> {
			Alert exitAlert = new Alert(AlertType.CONFIRMATION);
			exitAlert.setTitle("Avsluta");
			exitAlert.setHeaderText("Programmet avslutas!");
			ButtonType yesButton = new ButtonType("Avsluta", ButtonData.YES);
			ButtonType noButton = new ButtonType("Ångra", ButtonData.NO);
			exitAlert.getButtonTypes().setAll(yesButton, noButton);
			
			Optional<ButtonType> choose = exitAlert.showAndWait();
			if (choose.isPresent() && choose.get() == yesButton) {
				Platform.exit();
				// program slut;
			}
		});
		
		// Händelse vid tryck på kunder / konton i menyn
		customerOverView.setOnAction(e -> {
			search.setVisible(true);
			overView.setVisible(true);
			right.setVisible(false);
			startAccount.setVisible(true);
			
			// Händelse som dirigerar till konto fönster vid tryck på "gå till konto" knapp
			startAccount.setOnAction(event -> accountWindow(primaryStage, true, false));
			
			pNr.setVisible(true);
			pNo.setVisible(true);
			
			// Händelse vid tryck på sök knapp skickar personnummer till viewController 
			search.setOnAction(event -> {
				String inPno = pNo.getText();
				Main.getController().getMyCustomer(inPno);
				customerList.setVisible(true);
			});
			
			// Händelse som hämtar samtliga kunder och visar i en listview.
			overView.setOnAction(event -> {
				Main.getController().allCustomers();
				customerList.setVisible(true);
			});
		});
		
		// Dirigerar om till vyer
		newCustomer.setOnAction(e -> customerWindow(primaryStage, false, false));
		
		cancelCustomer.setOnAction(e -> customerWindow(primaryStage, true, false));
		
		nameChange.setOnAction(e -> customerWindow(primaryStage, false, true));
		
		newAccount.setOnAction(e -> accountWindow(primaryStage, true, false));
		
		transactions.setOnAction(e -> accountWindow(primaryStage, false, true));
		
		closeAccount.setOnAction(e -> accountWindow(primaryStage, false, false));
		
		Insets inset = new Insets(5);
		
		BorderPane.setMargin(menu, inset);
		BorderPane.setMargin(center, inset);
		BorderPane.setMargin(right, inset);
		
		// lägger till noder till fönstret
		center.getChildren().addAll(startAccount, overView, search, pNr, pNo, customerList, success, error);
		right.getChildren().addAll(information);
		
		// placerar noderna på sina platser i fönstret
		root.setTop(menu);
		root.setCenter(center);
		root.setRight(right);
		
		// skapar en scen med samtliga noder på.
		Scene scene = new Scene(root, 900, 800);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/* metod som skapar en vy tillägnad kund hantering som tar 3 parametrar varav 2 är bool för att avgöra
	   vilka noder som ska synas*/
	public void customerWindow(Stage primaryStage, boolean isEraseCustomer, boolean isNameChange) {
		primaryStage.setTitle("Kund");
		BorderPane layout = new BorderPane();
		VBox pane = new VBox();
		pane.setId("center");
		HBox box = new HBox();
		box.setId("top");
		GridPane grid = new GridPane();
		grid.setId("left");
		
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(30);
		
		Label fName = new Label("Förnamn");
		Label lName = new Label("Efternamn");
		Label personNr = new Label("Personnummer");
		Label info = new Label("Hantera kunder");
		info.setId("info");
		
		
		// kontrollerar vilket manyval som gjorts i startvyn för att gömma eller visa noder
		if (isEraseCustomer) {
			name.setVisible(false);
			surname.setVisible(false);
			search.setVisible(true);
			create.setVisible(false);
			fName.setVisible(false);
			lName.setVisible(false);
			erase.setVisible(true);
			submit.setVisible(false);
			pNo.setVisible(true);
			information.setText("Avsluta kund med att söka\nmed personnummer eller välj i listan\n"
					+ "tryck sedan på knappen 'avsluta'.");
		}
		else if (isNameChange) {
			name.setVisible(true);
			surname.setVisible(true);
			pNo.setVisible(true);
			fName.setVisible(true);
			lName.setVisible(true);
			submit.setVisible(true);
			erase.setVisible(false);
			information.setText("Byt namn på kund skriv in\nför och/eller efternamn\ntryck sen 'Ändra'.");
		}
		else {
			name.setVisible(true);
			surname.setVisible(true);
			pNo.setVisible(true);
			submit.setVisible(false);
			search.setVisible(false);
			create.setVisible(true);
			erase.setVisible(false);
			information.setText("Skapa ny kund genom att skriva in\nkundens uppgifter tryck sedan på 'Skapa'.");
		}
		
		// sätter meddelande och kundlistans synlighet till falskt så dom inte syns
		success.setVisible(false);
		error.setVisible(false);
		customerList.setVisible(false);
		
		// lägger till noder till lådan i mitten
		grid.getChildren().addAll(fName, lName, personNr, name, surname, pNo, create, 
								  clear, search, erase, submit);
		
		// lägger till noder i top fältet
		box.getChildren().addAll(start, info);
		
		// positionerar ut komponenter i mitten lådan som är ett rutnät av rader och kolumner
		GridPane.setConstraints(erase, 3, 7);
		
		GridPane.setConstraints(fName, 1, 0);
		
		GridPane.setConstraints(name, 1, 1);
		
		GridPane.setConstraints(lName, 1, 2);
		
		GridPane.setConstraints(surname, 1, 3);
		
		GridPane.setConstraints(personNr, 1, 4);
		
		GridPane.setConstraints(pNo, 1, 5);
		
		GridPane.setConstraints(create, 1, 7);
		
		GridPane.setConstraints(clear, 1, 8);
		
		GridPane.setConstraints(start, 2, 7);
		
		GridPane.setConstraints(search, 1, 7);
		
		GridPane.setConstraints(submit,  1, 7);
		
		// sätter in lådorna med komponenter i den större lådan för att positionera allt.
		layout.setLeft(grid);
		layout.setTop(box);
		layout.setCenter(pane);
		layout.setRight(information);
		
		// Händelse som raderar kund från banken vid markering i listview och sen tryck på avsluta knappen
		erase.setOnAction(e -> {
			String selectedCustomer = customerList.getSelectionModel().getSelectedItem();
			Main.getController().cancelCustomer(selectedCustomer);
		});
		
		// Händerlse som skapar ny kund skickar namn efternamn och personnumer till viewController
		create.setOnAction(e -> {
			String inName = name.getText();
			String inSurname = surname.getText();
			String inPno = pNo.getText();
			Main.getController().createCustomer(inPno, inName, inSurname);
		});
		
		// Händelse som tömmer textfälten.
		clear.setOnAction(e -> {
			error.setVisible(false);
			success.setVisible(false);
			customerList.setVisible(false);
			name.clear();
			surname.clear();
			pNo.clear();
		});
		
		// utför en sökning på personnummer
		search.setOnAction(e -> {
			String inPno = pNo.getText();
			Main.getController().getMyCustomer(inPno);
		});
		
		// byter namn eller efternamn eller båda på kund skickar uppgifterna till viewController som kontrollerar personnummer 
		submit.setOnAction(e -> {
			String inName = name.getText();
			String inSurname = surname.getText();
			String inPno = pNo.getText();
			Main.getController().changeName(inName, inSurname, inPno);
		});
		
		// återgår till startvyn
		start.setOnAction(e -> startScene(primaryStage));
		
		Insets inset = new Insets(5);
		
		BorderPane.setMargin(box, inset);
		BorderPane.setMargin(grid, inset);
		BorderPane.setMargin(information, inset);
		BorderPane.setMargin(pane, inset);
		
		pane.getChildren().addAll(customerList, success, error);
		Scene scene = new Scene(layout, 900, 800);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/* Metoden skapar upp en vy där man hanterar konton och transaktioner till konton såsom uttag eller insättning*/
	public void accountWindow(Stage primaryStage, boolean isNewAccount, boolean isTransaction) {
		primaryStage.setTitle("Konto");
		
		Label pNr = new Label("Personnummer");
		Button list = new Button("Välj från lista");
		Label amountLabel = new Label("Summa");
		
		Label info = new Label("Konto Hantering");
		
		list.setVisible(false);
		success.setVisible(false);
		error.setVisible(false);
		
		VBox right = new VBox();
		right.setId("right");
		HBox top = new HBox();
		top.setId("top");
		
		BorderPane layout = new BorderPane();
		
		VBox left = new VBox();
		left.setId("left");
		
		VBox center = new VBox();
		center.setId("center");
		
		info.setId("info");
		
		Insets inset = new Insets(5);
		
		// kontrollerar vilken menyflik som blivit valt.
		if (isNewAccount) {
			pNr.setVisible(true);
			pNo.setVisible(true);
			customerList.setVisible(false);
			deposit.setVisible(false);
			withdraw.setVisible(false);
			amountLabel.setVisible(false);
			amountField.setVisible(false);
			savingsAccount.setVisible(true);
			creditAccount.setVisible(true);
			search.setVisible(true);
			open.setVisible(true);
			information.setText("Skapa nytt konto\ngenom att skriva in\npersonnummer och sen\nmarkera en av\n"
					+ "bockrutorna ovanför\ntryck sedan 'Öppna konto'.");
		}
		else if (isTransaction){
			pNr.setVisible(true);
			pNo.setVisible(true);
			customerList.setVisible(false);
			deposit.setVisible(false);
			withdraw.setVisible(false);
			open.setVisible(false);
			search.setVisible(true);
			amountLabel.setVisible(true);
			savingsAccount.setVisible(false);
			creditAccount.setVisible(false);
			erase.setVisible(false);
			amountField.setVisible(false);
			amountLabel.setVisible(false);
			information.setText("Gör transaktioner genom\natt söka fram\nkunden med personnummer\n"
					+ "markera sedan kontot\nsom transaktionen gäller\n"
					+ "fyll i summan och tryck\nantingen 'insättning' eller 'uttag'.");
		}
		else {
			pNr.setVisible(true);
			pNo.setVisible(true);
			customerList.setVisible(false);
			deposit.setVisible(false);
			withdraw.setVisible(false);
			open.setVisible(false);
			search.setVisible(true);
			amountLabel.setVisible(false);
			savingsAccount.setVisible(false);
			creditAccount.setVisible(false);
			erase.setVisible(true);
			amountField.setVisible(false);
			amountLabel.setVisible(false);
			information.setText("Avsluta konto genom att\nsöka fram kundens konton\n"
					+ "med personnummer\nmarkera sedan kontot\n"
					+ "och tryck på 'Avsluta'.");
		}
		
		// lägger till noder till fönstret
		left.getChildren().addAll(open, clear, search, erase, transactions, saveTransaction);
		top.getChildren().addAll(start, info);
		right.getChildren().addAll(savingsAccount, creditAccount, information, success, error);
		center.getChildren().addAll(pNr, pNo, list, amountLabel, deposit, withdraw, amountField, customerList);
		
		// skapar lite utrymme mellan "lådorna"
		BorderPane.setMargin(top, inset);
		BorderPane.setMargin(left, inset);
		BorderPane.setMargin(right, inset);
		BorderPane.setMargin(center, inset);
		
		// lägger till "lådor" med noder till scenen
		layout.setCenter(center);
		layout.setTop(top);
		layout.setRight(right);
		layout.setLeft(left);
		
		// återgår till startvyn
		start.setOnAction(e -> startScene(primaryStage));
		
		// tömmer textfälten
		clear.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			pNo.clear();
			amountField.clear();
			customerList.setVisible(false);
			amountLabel.setVisible(false);
			amountField.setVisible(false);
			deposit.setVisible(false);
			withdraw.setVisible(false);
			savingsAccount.setSelected(false);
			creditAccount.setSelected(false);
		});
		
		// Händelse som skapar nya konton 
		open.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			String inPno = pNo.getText();
			Main.getController().openAccount(inPno, isSavingsAccount(), isCreditAccount());
		});
		
		// händelse som söker kund på personnummer
		search.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			String inPno = pNo.getText();
			Main.getController().getMyCustomer(inPno);
			amountLabel.setVisible(true);
			amountField.setVisible(true);
			deposit.setVisible(true);
			withdraw.setVisible(true);
		});
		
		// händelse som utför insättningar på konto som blivit valt i listan 
		deposit.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			String inPno = pNo.getText();
			String amountText = amountField.getText();
			if (!amountText.isEmpty()) {
				int amount = new BigDecimal(amountField.getText()).intValue();
				String selection = customerList.getSelectionModel().getSelectedItem();
				Main.getController().doDeposit(inPno, selection, amount);
			}
		});
		
		// händelse som utför uttag från konto som blivit valt i listan.
		withdraw.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			String inPno = pNo.getText();
			String amountText = amountField.getText();
			if (!amountText.isEmpty()) {
				int amount = new BigDecimal(amountField.getText()).intValue();
				String selection = customerList.getSelectionModel().getSelectedItem();
				Main.getController().doWithdraw(inPno, selection, amount);
			}
		});
		
		// händelse som raderar konto som blivit valt i listan
		erase.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			String inPno = pNo.getText();
			String selection = customerList.getSelectionModel().getSelectedItem();
			Main.getController().cancelAccount(inPno, selection);
		});
		
		// Händelse som visar gjorda transaktioner i lista
		transactions.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			customerList.setVisible(true);
			String inPno = pNo.getText();
			String selection = customerList.getSelectionModel().getSelectedItem();
			Main.getController().getTransactions(inPno, selection);
		});
		
		// 
		saveTransaction.setOnAction(e -> {
			success.setVisible(false);
			error.setVisible(false);
			String inPno = pNo.getText();
			String selection = customerList.getSelectionModel().getSelectedItem();
			Main.getController().transactionsToFile(inPno, selection);
		});
		// skapar upp scenen för kontohanteringen
		Scene scene = new Scene(layout, 900, 800);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	// nedanför finns åtkomstmetoder för dom olika komponenterna som används genom programmet.
	public Button transactions() {
		return transactions;
	}
	
	public Button saveTransaction() {
		return saveTransaction;
	}
	
	public Button erase() {
		return erase;
	}
	
	public TextField amount() {
		return amountField;
	}
	
	public Button deposit() {
		return deposit;
	}
	
	public Button withdraw() {
		return withdraw;
	}
	
	public String selection() {
		return customerList.getSelectionModel().getSelectedItem();
	}
	
	public boolean isCreditAccount() {
		return creditAccount.isSelected();
	}
	
	public boolean isSavingsAccount() {
		return savingsAccount.isSelected();
	}
	
	public Button open() {
		return open;
	}
	
	public Button eraseCustomer() {
		return erase;
	}
	
	public Button submit() {
		return submit;
	}
	
	public Button search() {
		return search;
	}
	
	public Button create() {
		return create;
	}
	
	public ListView<String> customerList(){
		return customerList;
	}
	
	public Button overView() {
		return overView;
	}
	
	public TextField getName() {
		return name;
	}
	
	public TextField getSurname() {
		return surname;
	}
	
	public TextField getPno() {
		return pNo;
	}
	
	public Label errorLabel() {
		return error;
	}
	
	public Label successLabel() {
		return success;
	}
}
