package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 */

/*
 * denna klass skapar och hanterar kreditkonton som ärver från klassen account 
 * den har metoder för att räkna ut avslutnings ränta och göra uttag relevanta för 
 * denna typ av konto.
 */

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CreditAccount extends Account {

	// insatans variabler
	private static final long serialVersionUID = 1L;
	private static final BigDecimal limit = new BigDecimal("-5000");
	private static final BigDecimal interest = new BigDecimal("7");
	private static final BigDecimal balanceInterest = new BigDecimal("0.5");
	
	// konstruktor
	public CreditAccount(String personalNumber) {
		
		super(personalNumber);
	}
	
	// metod som räknar ut räntan vid avslutande av kontot
	@Override
	public BigDecimal calculateClosingInterest() {
		
		if (getBalance().compareTo(BigDecimal.ZERO) < 0) {
			return getBalance().multiply(interest).divide(new BigDecimal(100));
		}
		else {
			return getBalance().multiply(balanceInterest).divide(new BigDecimal(100));
		}
	}
	
	/* en överskuggad uttags metod för att kunna göra uttag som är större än vad som existerar på kontot
	 * dock inte mer än vad gränsen tillåter.
	 */
	@Override
	public boolean withdraw(int amount) {
		
		BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
		if (getBalance().subtract(withdrawAmount).compareTo(limit) >= 0) {
			setBalance(getBalance().subtract(withdrawAmount));
			updateTime();
			transactions.add(new Transactions(withdrawAmount.negate(), getBalance(), this.transactionsTime));
			return true;
		}
		else {
			return false;
		}
		
	}
	
	// metod som returnerar en string som berättar vilken typ av konto det är 
	public String getAccountType() {
		
		return "Kreditkonto";
	}
	
	// egen tostring som överskuggar account klassens tostring.
	@Override
	public String toString() {
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("sv", "SE"));
		String balanceStr = currencyFormat.format(getBalance());
		NumberFormat percentFormat = NumberFormat.getPercentInstance(new Locale("sv","SE"));
		percentFormat.setMaximumFractionDigits(1);
		BigDecimal dependingInterest = getBalance().compareTo(BigDecimal.ZERO) < 0 ? interest : balanceInterest;
		String percentStr = percentFormat.format(dependingInterest.divide(new BigDecimal(100)));
		return String.format("Kontonummer: %d Saldo: %s %s Ränta: %s", getAccountNr(), balanceStr, getAccountType(), percentStr);
	}

	// egen kontoavslutnings tostring metod som överskuggar account klassens avslutnings tostring.
	@Override
	public String closingString() {
		
		BigDecimal calculateInterest = calculateClosingInterest();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("sv", "SE"));
		String balanceStr = currencyFormat.format(getBalance());
		String interestStr = currencyFormat.format(calculateInterest);
		return String.format("Kontonummer: %d Saldo: %s %s Saldo + ränta: %s", getAccountNr(), balanceStr, getAccountType(), interestStr);
	}
}
