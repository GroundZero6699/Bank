package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 * 
 * 
 * Denna klassen hanterar logiken i "banken" och skickar parametrar till respektive metod för att
 * skapa kund/kunder skapa konton.
 * kunderna sparas i en arraylist sedan används en hashmap för att linka
 * konton till respektive kund.
 * i denna klass finns flertalet publika metoder som hanterar anrop till kund klass eller konto klass
 * det finns även några privata metoder som hanterar sökningar utav kund och kontolistan.
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;

public class BankLogic {
	
	// En arraylist för att lagra kundobject
	private ArrayList<Customer> customers = new ArrayList<Customer>();
	
	// En hashmap för att linka kunder till respektive konto
	private HashMap<Customer, List<Account>> accounts = new HashMap<>();
	
	// Privat metod för att söka genom efter en specifik kund.
	private Customer findCustomer(String pNo) {
		
		for (Customer customer : customers) {
			if (customer.getPno().equals(pNo)) {
				return customer;
			}
		}
		return null;
	}
	
	// Link mellan kund och kontona relaterade till kunden
	private List<Account> getCustomerAccounts(String pNo){
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> customerAccounts = accounts.get(customer);
			if (customerAccounts == null) {
				customerAccounts = new ArrayList<>();
			}
			return customerAccounts;
		}
		return new ArrayList<>();
	}
	
	// Metoden skapar kund objekt
	public boolean createCustomer(String name, String surname, String pNo) {
		
		Customer customer = findCustomer(pNo);
		if (customer == null) {
			Customer newCustomer = new Customer(name, surname, pNo);
			customers.add(newCustomer);
			return true;
		}
		return false;
	}
	
	// Metoden skapar en lista som fylls med kundobjekten och sedan returneras
	public List<String> getAllCustomers(){
		
		List<String> allCustomers = new ArrayList<>();
		for(Customer customer : customers) {
			allCustomers.add(customer.toString());
		}
		return allCustomers;
	}
	
	// Metoden skapar en lista med kundobjektet som har ett specifikt personnr
	public List<String> getCustomer(String pNo){
		
		List<String> requestedCustomer = new ArrayList<>();
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			requestedCustomer.add(customer.toString());
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			for (Account account : customerAccounts) {
				requestedCustomer.add(account.toString());
			}
			return requestedCustomer;
		}
		return null;
	}
	
	// metod som hämtar transaktioner för erhållet personnummer och kontonummer
	public List<String> getTransactions(String pNo, int accountId){
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			for (Account account : customerAccounts) {
				if (account.getAccountNr() == accountId) {
					return account.getTransactions();
					}
				}	
		}
		return null;
	}
	
	// Skapar nytt sparkonto om kund finns
	public int createSavingsAccount(String pNo) {
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			Account newAccount = new SavingsAccount(pNo);
			List<Account> customerAccounts = accounts.get(customer);
			if (customerAccounts == null) {
				customerAccounts = new ArrayList<>();
				accounts.put(customer, customerAccounts);
			}
			customerAccounts.add(newAccount);
			return newAccount.getAccountNr();
		}
		return -1;
	}
	
	// metoden skapar kreditkonto om kund existerar
	public int createCreditAccount(String pNo) {
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			CreditAccount newCreditAccount = new CreditAccount(pNo);
			List<Account> customerAccounts = accounts.get(customer);
			if (customerAccounts == null) {
				customerAccounts = new ArrayList<>();
				accounts.put(customer, customerAccounts);
			}
			customerAccounts.add(newCreditAccount);
			return newCreditAccount.getAccountNr();
		}
		return -1;
	}
	
	// Skriver ut konto med speciellt kontonr
	public String getAccount(String pNo, int accountId) {
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			for (Account account : customerAccounts) {
				if (account.getAccountNr() == accountId) {
						return account.toString();
				}
			}
		}
		return null;
	}
	
	// Gör insättning på kontot till kund med ett specifikt personnr och ett specifikt kontonr
	public boolean deposit(String pNo, int accountId, int amount) {
		
		BigDecimal depositAmount = BigDecimal.valueOf(amount);
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			for (Account account : customerAccounts) {
				if (account.getAccountNr() == accountId) {
					if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
						account.deposit(depositAmount.intValue());
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// Gör uttag på konto med specifikt personnr och kontonr
	public boolean withdraw(String pNo, int accountId, int amount) {
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			for (Account account : customerAccounts) {
				if (account.getAccountNr() == accountId) {
					return account.withdraw(amount);
				}
			}
		}
		return false;
	}
	
	// Skapar en lista med borttagen kund och returnerar den kontrollerar först om kund finns.
	public List<String> deleteCustomer(String pNo) {
		
		List<String> deletedCustomer = new ArrayList<>();
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			String erasedCustomer = customer.toString();
			deletedCustomer.add(erasedCustomer);
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			for (Account account : customerAccounts) {
				deletedCustomer.add(account.closingString());
			}
			customers.remove(customer);
			accounts.remove(customer);
			return deletedCustomer;
		}
		return deletedCustomer.isEmpty() ? null : deletedCustomer;
	}
	
	// Stänger konto med konronr till kund med personnr 
	public String closeAccount(String pNo, int accountId) {
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			List<Account> customerAccounts = getCustomerAccounts(pNo);
			Account removeAccount = null;
			for (Account account : customerAccounts) {
				if (account.getAccountNr() == accountId) {
					removeAccount = account;
					break;
				}
			}
			if (removeAccount != null) {
				customerAccounts.remove(removeAccount);
				accounts.put(customer,  customerAccounts);
				return removeAccount.closingString();
			}
		}
		return null;
	}
	
	// Byter namn på kund 
	public boolean changeCustomerName(String name, String surname, String pNo) {
		
		Customer customer = findCustomer(pNo);
		if (customer != null) {
			boolean changed = false;
			if (name != null && !name.isEmpty() && !customer.getName().equals(name)) {
				customer.setName(name);
				changed = true;
			}
			if (surname != null && !surname.isEmpty() && !customer.getSurname().equals(surname)) {
				customer.setSurname(surname);
				changed = true;
			}
			return changed;
		}
		return false;
	}
	
	// sparar ner alla kunder och deras konton till en fil.
	public boolean saveToFile(String chrwii3_files) throws IOException {
		String path = "chrwii3_files" + File.separator + "bank.dat";
		try (ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(path))){
			save.writeObject(customers);
			save.writeObject(accounts);
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	// Hämtar kunder och konton från fil.
	public boolean loadFromFile(String chrwii3_files) throws IOException {
		String path = "chrwii3_files" + File.separator + "bank.dat";
		try (ObjectInputStream load = new ObjectInputStream(new FileInputStream(path))){
			Object customerObject = load.readObject();
			Object accountObject = load.readObject();
			if (customerObject instanceof ArrayList && accountObject instanceof HashMap) {
				customers = (ArrayList<Customer>) customerObject;
				accounts = (HashMap<Customer, List<Account>>) accountObject;
				int lastAccountNr = accounts.values().stream()
									.flatMap(List::stream)
									.mapToInt(Account::getAccountNr)
									.max()
									.orElse(1000);
				Account.setAssignedAccountNr(lastAccountNr);
				return true;
			}
			else {
				return false;
			}
			
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
}
