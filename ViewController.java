package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 * 
 * Denna klassen hanterar logiken från gränssnittet och hanterar dom olika händelserna 
 * såsom knapptryck och liknande.
 * metoderan får parametrar från gränssnittet och skickar dom till BankLogic för att utföra modifieringar
 * eller skapande utav olika objekt som kunder, konton och liknande.
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ViewController {

	// instans variabler utan banken och vyn
	private BankLogic bank;
	private SceneView view;
	
	// konstruktor för banken och vyn
	public ViewController(BankLogic bank, SceneView view) {
		this.bank = bank;
		this.view = view;
		
		// skickar till metoden för att hantera händelser
		setupHandlers();
	}
	
	// metoden hanterar händelser vid knapptryck och liknande 
	private void setupHandlers() {
		view.overView().setOnAction(event -> allCustomers());
		view.create().setOnAction(event -> createCustomer(view.getPno().getText(), view.getName().getText(), view.getSurname().getText()));
		view.search().setOnAction(event -> getMyCustomer(view.getPno().getText()));
		view.submit().setOnAction(event -> changeName(view.getPno().getText(), view.getName().getText(), view.getSurname().getText()));
		view.eraseCustomer().setOnAction(event -> cancelCustomer(view.getPno().getText()));
		view.open().setOnAction(event -> openAccount(view.getPno().getText(), view.isSavingsAccount(), view.isCreditAccount()));
		view.deposit().setOnAction(event -> doDeposit(view.getPno().getText(), view.selection(),
													  Integer.parseInt(view.amount().getText())));
		view.withdraw().setOnAction(event -> doWithdraw(view.getPno().getText(), view.selection(),
													  Integer.parseInt(view.amount().getText())));
		view.erase().setOnAction(event -> cancelAccount(view.getPno().getText(), view.selection()));
		view.transactions().setOnAction(event -> getTransactions(view.getPno().getText(), view.selection()));
		view.saveTransaction().setOnAction(event -> transactionsToFile(view.getPno().getText(), view.selection()));
	}
	
	/* Denna metoden kontrollerar personnummret om kunden finns så delar den textsträngen som blir returnerad och
	 * lyfter ut kontonummret för att sedan hämta uppgifter om transaktioner som gjorts mot det kontot
	 * annars visas ett felmeddelande
	 * tar emot 2 parametrar "personnummer, retursträngen från kund sökningen som delas*/
	public void getTransactions(String pNo, String selection) {
		if (!pNo.isEmpty() && selection != null) {
			String[] part = selection.split(" ");
			if (part.length > 2 && !part[1].isEmpty() && part[1].matches("\\d+")) {
				int accountId = Integer.parseInt(part[1]);
				if (pNo.matches("\\d{8}")) {
					List<String> transactionList = bank.getTransactions(pNo, accountId);
					if (transactionList != null) {
						view.customerList().setVisible(true);
						view.errorLabel().setVisible(false);
						view.successLabel().setVisible(false);
						view.customerList().getItems().clear();
						view.customerList().getItems().addAll(transactionList);
					}
					else {
						view.errorLabel().setVisible(true);
						view.errorLabel().setText("Kontrollera uppgifterna");
						view.customerList().setVisible(false);
						view.successLabel().setVisible(false);
					}
				}
				else {
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Felaktigt format på personnummer!");
					view.customerList().setVisible(false);
					view.successLabel().setVisible(false);
				}
			}
			else {
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Välj ett konto i listan!");
				view.successLabel().setVisible(false);
			}
		}
		else {
			view.customerList().setVisible(false);
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Kontrollera uppgifterna");
			view.successLabel().setVisible(false);
		}
	}
	 /* Denna metoden kontrollerar personnummer och delat retur strängen för att lyfta ut kontonummer
	  * som sedan används för att avsluta kontot sedan uppdateras vyn med dom nya uppgifterna
	  * Tar emot 2 parametrar "personnummer, retursträngen från kund sökningen som delas*/
	public void cancelAccount(String pNo, String selection) {
		if (!pNo.isEmpty() && selection != null) {
			String[] part = selection.split(" ");
			if (part.length > 2 && !part[1].isEmpty() && part[1].matches("\\d+")) {
				int accountId = Integer.parseInt(part[1]);
				if (pNo.matches("\\d{8}")) {
					String closedAccount = bank.closeAccount(pNo, accountId);
					if (closedAccount != null) {
						view.successLabel().setVisible(true);
						view.successLabel().setText("Konto avslutat");
						view.customerList().getItems().clear();
						view.customerList().getItems().add(closedAccount);
						view.errorLabel().setVisible(false);
						getMyCustomer(pNo);
					}
					else {
						view.errorLabel().setVisible(true);
						view.errorLabel().setText("Kontrollera uppgifterna");
						view.successLabel().setVisible(false);
					}
				}
				else {
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Felaktigt format på personnummer!");
					view.successLabel().setVisible(false);
				}
			}
			else {
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Välj ett konto i listan!");
				view.successLabel().setVisible(false);
			}	
		}
		else {
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Kontrollera uppgifterna");
			view.successLabel().setVisible(false);
		}
	}
	
	/* Metod för att göra insättningar Kontrollerar personnummer och delar strängen för att plocka ut kontonummer 
	 * som sedan används för att göra insättningar till kontot
	 * tar emot 3 parametrar "personnummer, retursträngen från kund sökningen som delas, och summan*/
	public void doDeposit(String pNo, String selection, int amount) {
		if (!pNo.isEmpty() && selection != null && amount > 0) {
			String[] part = selection.split(" ");
			if (part.length > 2 && !part[1].isEmpty() && part[1].matches("\\d+")) {
				int accountId = Integer.parseInt(part[1]);
				if (pNo.matches("\\d{8}")) {
					boolean deposited = bank.deposit(pNo, accountId, amount);
					if (deposited) {
						view.successLabel().setVisible(true);
						view.errorLabel().setVisible(false);
						view.successLabel().setText("Insättning lyckades!");
						getMyCustomer(pNo);
					}
					else {
						view.errorLabel().setVisible(true);
						view.errorLabel().setText("Kontrollera uppgifterna");
						view.successLabel().setVisible(false);
					}
				}
				else {
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Felaktigt format på personnummer!");
					view.successLabel().setVisible(false);
				}
			}
			else {
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Välj ett konto i listan!");
				view.successLabel().setVisible(false);
			}
		}
		else {
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Kontrollera uppgifterna");
			view.successLabel().setVisible(false);
		}
	}
	
	/* Metod för att göra uttag kontrollerar personnummer delar sedan strängen för att plocka ut kontonummer
	 * som sedan används för att göra uttag på kontot
	 * tar emot 3 parametrar "personnummer, retursträngen från kund sökningen som delas, och summan*/
	public void doWithdraw(String pNo, String selection, int amount) {
		if (!pNo.isEmpty() && selection != null && amount > 0) {
			String[] part = selection.split(" ");
			if (part.length > 2 && !part[1].isEmpty() && part[1].matches("\\d+")) {
				int accountId = Integer.parseInt(part[1]);
				if (pNo.matches("\\d{8}")) {
					boolean withdrawed = bank.withdraw(pNo, accountId, amount);
					if (withdrawed) {
						view.successLabel().setVisible(true);
						view.errorLabel().setVisible(false);
						view.successLabel().setText("Uttag lyckades!");
						getMyCustomer(pNo);
					}
					else {
						view.errorLabel().setVisible(true);
						view.errorLabel().setText("Konto saldo för lågt!!");
						view.successLabel().setVisible(false);
					}
				}
				else {
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Felaktigt format på personnummer!");
					view.successLabel().setVisible(false);
				}
			}
			else {
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Välj ett konto i listan!");
				view.successLabel().setVisible(false);
			}
		}
		else {
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Kontrollera uppgifterna");
			view.successLabel().setVisible(false);
		}
	}
	
	/* metoden tar emot 3 parametrar personnummer som kontrolleras
	 * sedan en parameter som berättar om sparkonto blivit valt i SceneView klassen
	 * den sista kontrollerar om kreditkonto blivit valt*/
	public void openAccount(String pNo, boolean isSavingsAccount, boolean isCreditAccount) {
		if (pNo.matches("\\d{8}")) {
			if (isSavingsAccount && isCreditAccount) {
				view.errorLabel().setVisible(true);
				view.successLabel().setVisible(false);
				view.errorLabel().setText("Välj EN kontotyp!");
			}
			else if (!isSavingsAccount && !isCreditAccount) {
				view.errorLabel().setVisible(true);
				view.successLabel().setVisible(false);
				view.errorLabel().setText("Välj EN kontoTyp!");
			}
			else {
				if (isSavingsAccount) {
					int openSaving = bank.createSavingsAccount(pNo);
					if (openSaving != -1) {
						view.successLabel().setVisible(true);
						view.errorLabel().setVisible(false);
						view.successLabel().setText("Sparkonto Skapat\nKontonr: " + openSaving);
						getMyCustomer(pNo);
					}
					else {
						view.errorLabel().setVisible(true);
						view.successLabel().setVisible(false);
						view.errorLabel().setText("Ett fel uppstod vänligen\n kontrollera uppgifterna!");
					}
				}
				else if (isCreditAccount) {
					int openCredit = bank.createCreditAccount(pNo);
					if (openCredit != -1) {
						view.successLabel().setVisible(true);
						view.errorLabel().setVisible(false);
						view.successLabel().setText("Kreditkonto skapat\nKontonr: " + openCredit);
						getMyCustomer(pNo);
					}
					else {
						view.errorLabel().setVisible(true);
						view.successLabel().setVisible(false);
						view.errorLabel().setText("Ett fel uppstod vänligen\n kontrollera uppgifterna!");
					}
				}
				else {
					view.errorLabel().setVisible(true);
					view.successLabel().setVisible(false);
					view.errorLabel().setText("Ett fel uppstod vänligen\n kontrollera uppgifterna!");
				}
			}
		}
		else {
			view.errorLabel().setVisible(true);
			view.successLabel().setVisible(false);
			view.errorLabel().setText("Ett fel uppstod vänligen\n kontrollera uppgifterna!");
		}
	}
	
	/* Metoden tar emot 1 parameter som är en kund som är vald i lista. denna sträng delas sedan för att plocka ut personnumer 
	 * som sedan kontrolleras sedan kommer ett varnings fönster öppnas där man väljer ja eller nej sedan avslutas kunden
	 * och listan uppdateras med kundens info och alla kundens konton som blir avslutade*/
	public void cancelCustomer(String selectedCustomer) {
		if (selectedCustomer != null) {
			String[] spliting = selectedCustomer.split(" ");
			String pNo = spliting[0];
			if (pNo.matches("\\d{8}")) {
				List<String> canceledCustomer = bank.deleteCustomer(pNo);
				ObservableList<String> customerData = FXCollections.observableArrayList();
				if (!canceledCustomer.isEmpty()) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Avsluta Kund!");
					alert.setContentText("Vill du ta bort kunden och all tillhörande information?");
					ButtonType yes = new ButtonType("Ja");
					ButtonType no = new ButtonType("Nej");
					alert.getButtonTypes().clear();
					alert.getButtonTypes().addAll(yes, no);
					Optional<ButtonType> choice = alert.showAndWait();
					
					if (choice.isPresent() && choice.get() == yes) {
						customerData.addAll(canceledCustomer);
						view.customerList().setVisible(true);
						view.errorLabel().setVisible(false);
						view.customerList().setItems(customerData);
					}
				}
				else {
					view.customerList().setVisible(false);
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
				}
			}
			else {
				view.customerList().setVisible(false);
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
			}
		}
		else {
			view.customerList().setVisible(false);
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
		}	
	}
	
	/* denna metoden tar 3 parametrar name som är förnamnet surname som är efternamnet och pNo som är personnummer 
	 * personnummer kontrolleras och sedan skickas dom andra vidare för att utföra ändring därefter uppdateras listan*/
	public void changeName(String name, String surname, String pNo) {
		if (pNo.matches("\\d{8}")) {
			boolean change = bank.changeCustomerName(name, surname, pNo);
			if (change == true) {
				view.successLabel().setVisible(true);
				view.errorLabel().setVisible(false);
				view.customerList().setVisible(true);
				view.successLabel().setText("Ändring utförd");
				getMyCustomer(pNo);
			}
			else {
				view.successLabel().setVisible(false);
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
			}
		}
		else {
			view.successLabel().setVisible(false);
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
		}
	}
	
	/* Metoden söker ut en kund på personnummer och tar 1 parameter
	 * uppdaterar listan om kunden finns*/
	public void getMyCustomer(String pNo) {
		if (!pNo.isEmpty()) {
			if (pNo.matches("\\d{8}")) {
				List<String> customer = bank.getCustomer(pNo);
				ObservableList<String> customerData = FXCollections.observableArrayList();
				if (customer != null) {
					customerData.addAll(customer);
					view.customerList().setVisible(true);
					view.errorLabel().setVisible(false);
					view.customerList().setItems(customerData);
				}
				else {
					view.customerList().setVisible(false);
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
				}
			}
			else {
				view.customerList().setVisible(false);
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
			}
		}
		else {
			view.customerList().setVisible(false);
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Ett fel uppstod\nkontrollera uppgifterna");
		}
	}
	
	/* Metoden hämtar samtliga kunder och visar i en lista*/
	public void allCustomers() {
		List<String> customers = bank.getAllCustomers();
		ObservableList<String> customerData = FXCollections.observableArrayList();
		
		customerData.addAll(customers);
		view.customerList().setItems(customerData);    
	}
	
	/* Metoden skapar nya kunder tar 3 parametrar som personnummer, förnamn, efternamn
	 * som kontrolleras att som inte är null samt att personnummer är 8 siffror */
	public void createCustomer(String pNr, String fName, String lName) {
		if (!fName.isEmpty() && !lName.isEmpty()) {
			if (pNr.matches("\\d{8}")) {
				boolean newCustomer = bank.createCustomer(fName, lName, pNr);
				if (newCustomer == true) {
					view.successLabel().setVisible(true);
					view.errorLabel().setVisible(false);
					view.customerList().setVisible(true);
					view.successLabel().setText("Ny kund Tillagd");
					getMyCustomer(pNr);
				}
				else {
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("kund finns redan\nKontrollera uppgifterna");
					view.successLabel().setVisible(false);
				}	
			}
			else {
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Kontrollera personnummret\nFormat ÅÅÅÅMMDD med siffror.");
				view.successLabel().setVisible(false);
			}
		}
		else {
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Vänligen ange namn\npå kunden");
			view.successLabel().setVisible(false);
		}
	}
	
	// Sparar ner kunder och deras konton till en fil som hamnar i mappen chrwii_files med namnet bank.dat
	public void saveBank() {
		String path = "chrwii3_files" + File.separator + "bank.dat";
		try {
			boolean saveSuccess = bank.saveToFile(path);
			if (saveSuccess == true) {
				view.successLabel().setVisible(true);
				view.errorLabel().setVisible(false);
				view.successLabel().setText("Fil sparad");
			}
			else {
				view.successLabel().setVisible(false);
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("ett fel uppstod vid sparning av fil");
			}
		}
		catch (IOException e) {
			view.errorLabel().setVisible(true);
			view.successLabel().setVisible(false);
			view.errorLabel().setText("ett fel uppstod vid sparning av fil");
		}
	}
	
	// Hämtar filen som blivit sparad med kunder och konton
	public void loadBank() {
		String path = "chrwii3_files" + File.separator + "bank.dat";
		try {
			boolean loadSuccess = bank.loadFromFile(path);
			if (loadSuccess == true) {
				view.successLabel().setVisible(true);
				view.errorLabel().setVisible(false);
				view.successLabel().setText("Läsning av fil lyckades");
			}
			else {
				view.successLabel().setVisible(false);
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Ett fel uppstod vid läsning av fil");
			}
		}
		// om filen inte hittas eller är korrupt visas felmeddelande
		catch (IOException e){
			view.successLabel().setVisible(false);
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Ett fel uppstod vid läsning av fil");
		}
	}
	
	// sparar transaktioner till en textfil i samma mapp som "bank" filen. textfilen får namnet utav kontonummret.txt 
	public void transactionsToFile(String pNo, String selection) {
		if (!pNo.isEmpty() && selection != null) {
			String[] part = selection.split(" ");
			if (part.length > 2 && !part[1].isEmpty() && part[1].matches("\\d+")) {
				int accountId = Integer.parseInt(part[1]);
				String fileName = "chrwii3_files" + File.separator + accountId + ".txt";
				if (pNo.matches("\\d{8}")) {
					List<String> transactionList = bank.getTransactions(pNo, accountId);
					if (transactionList != null) {
						view.customerList().setVisible(true);
						view.errorLabel().setVisible(false);
						view.successLabel().setVisible(false);
						BufferedWriter file = null;
						try {
							file = new BufferedWriter(new FileWriter(fileName, true));
							for (String transaction : transactionList) {
								file.write(transaction);
								file.newLine();
							}
							view.successLabel().setText("Transaktioner sparade!");
							view.successLabel().setVisible(true);
						}
						catch (IOException e) {
							view.errorLabel().setVisible(true);
							view.successLabel().setVisible(false);
							view.errorLabel().setText("Ett fel uppstod");
							
						}
						finally {
							try {
								if (file != null) {
									file.close();
								}
							}
							catch (IOException e) {
								view.errorLabel().setVisible(true);
								view.successLabel().setVisible(false);
								view.errorLabel().setText("Ett fel uppstod");
							}
						}
					}
					else {
						view.errorLabel().setVisible(true);
						view.errorLabel().setText("Kontrollera uppgifterna");
						view.customerList().setVisible(false);
						view.successLabel().setVisible(false);
					}
				}
				else {
					view.errorLabel().setVisible(true);
					view.errorLabel().setText("Felaktigt format på personnummer!");
					view.customerList().setVisible(false);
					view.successLabel().setVisible(false);
				}
			}
			else {
				view.errorLabel().setVisible(true);
				view.errorLabel().setText("Välj ett konto i listan!");
				view.customerList().setVisible(false);
				view.successLabel().setVisible(false);
			}
		}
		else {
			view.customerList().setVisible(false);
			view.errorLabel().setVisible(true);
			view.errorLabel().setText("Kontrollera uppgifterna");
			view.successLabel().setVisible(false);
		}
	}
}
