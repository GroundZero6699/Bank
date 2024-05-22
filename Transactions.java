package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 */

/*
 * Klass för att skapa transaktions objekt för varje trnsaktions som genomförs
 * 
 * */

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.io.Serializable;

public class Transactions implements Serializable {
	
	// instans variabler för skapande av transaktioner
	private static final long serialVersionUID = 1L;
	private LocalDateTime transactionsTime;
	private BigDecimal amount;
	private BigDecimal newBalance;
	
	// konstruktor skapar en instans "en transaktion" 
	public Transactions(BigDecimal amount, BigDecimal newBalance, LocalDateTime transactionsTime) {
		
		this.transactionsTime = transactionsTime;
		this.amount = amount;
		this.newBalance = newBalance;
	}
	
	// egen formaterad tostring metod för att skriva ut information om objektet i formaterad form
	@Override
	public String toString() {
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("sv", "SE"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		String balanceStr = currencyFormat.format(newBalance);
		String amountStr = currencyFormat.format(amount);
		String strDate = transactionsTime.format(formatter);   
		return String.format("Datum: %s Summa: %s Saldo: %s", strDate, amountStr, balanceStr);
	}
	
	// metod för att hämta den uppdaterade tiden.
	public LocalDateTime getTimeStamp() {
		return transactionsTime;
	}
	
	// uppdaterar riden 
	public void updateTime() {
		this.transactionsTime = LocalDateTime.now();
	}
}
