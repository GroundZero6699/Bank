package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 */

/*
 * klass konto hanterar skapandet av konton med instans variablerna 
 * vi har även en variabel som ger löpande nytt konto nr som blir
 * unikt.
 * räntan är satt till en konstant för att inte ändras.
 * vi har även två stycken toString metoder den ena skriver ut med kontonr saldo sparkonto ränteprocent
 * den andra skriver ut kontonr saldo sparkonto saldo efter ränteuträkning
 */

import java.io.Serializable;
import java.math.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

abstract class Account implements Serializable {

	// instans variabler för skapande av konto
	private static final long serialVersionUID = 1L;
	protected LocalDateTime transactionsTime;
	private BigDecimal balance;
	private final BigDecimal interest;
	protected List<Transactions> transactions = new ArrayList<>();
	private int accountNr;
	private static int assignedAccountNr = 1000;
	
	// konstruktor för skapande av konto
	public Account(String personalNumber) {
		
		this.transactionsTime = LocalDateTime.now();
		this.balance = BigDecimal.ZERO;
		this.interest = new BigDecimal("1.2");
		this.accountNr = ++assignedAccountNr;
	}
	
	public static void setAssignedAccountNr(int assignedAccountNr) {
		Account.assignedAccountNr = assignedAccountNr;
	}
	
	// abstrakt metod som tvingar subklasserna att implementera sina specifika uträknings metoder
	public abstract BigDecimal calculateClosingInterest();
	
	// metoden skapar ett nytt objekt av tidsstämpel klassen för uppdatering av tidsstämpeln 
	protected void updateTime() {
		
		this.transactionsTime = LocalDateTime.now();
	}
	
	// metoden samlar tidsstämplarna från transaktioner
	public List<String> getTransactions(){
		
		
		return transactions.stream().map(Transactions::toString).collect(Collectors.toList());
	}
	
	// abstrakt metod som tvingar subklassen att implementera sin egen tostring 
	public abstract String toString();
	
	// abstrakt avslutnings string tvingar subklassen att implementera sin egen avslutnings string
	public abstract String closingString();
	
	// Denna metod räknar ut vad räntan blir när kontot avslutas och returnerar räntan
	public BigDecimal calculateInterest() {
		
		return balance.multiply(interest).divide(new BigDecimal(100));
	}
	
	// Meod som returnerar variabeln balance som är saldot på kontot
	public BigDecimal getBalance() {
		
		return balance;
	}
	
	// Metod som kalkylerar ränta och returnerar saldo med ränta.
	public BigDecimal getInterest() {
		
		return interest;
	}
	
	// Metoden returnerar konto nummret
	public int getAccountNr() {
		
		return accountNr;
	}
	
	// abstrakt metod tvingar subklassen att implementera sin egna metod 
	public abstract String getAccountType();
	
	/* Metoden kalkylerar det nya saldot efter insättning av pengar och returnerar nya saldot
	 * uppdaterar sedan tidsstämpeln vid transaktion
	 */
	public String deposit(int amount) {
		
		BigDecimal depositAmount = BigDecimal.valueOf(amount);
		balance = balance.add(depositAmount);
		this.transactionsTime = LocalDateTime.now();
		transactions.add(new Transactions(depositAmount, balance, this.transactionsTime));
		return balance.toString();
	}
	
	/* metoden kalkylerar saldo vid uttag av pengar och returnerar nytt saldo
	 * uppdaterar sedan tidsstämpeln vid transaktion
	 */
	public boolean withdraw(int amount) {
		
		BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
		balance = balance.subtract(withdrawAmount);
		this.transactionsTime = LocalDateTime.now();
		transactions.add(new Transactions(withdrawAmount.negate(), balance, this.transactionsTime));
		return true;
	}
	
	// setter metod som uppdaerar saldo variabeln.
	protected void setBalance(BigDecimal newBalance) {
		
		this.balance = newBalance;
	}
}