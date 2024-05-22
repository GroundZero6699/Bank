package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 */
/*
 * En klass för skapande och hanterande av sparkonto specifika avlutnings eller uttags metoder
 */

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class SavingsAccount extends Account {
	
	// instans variabel
	private static final long serialVersionUID = 1L;
	private boolean freeWithdraw = true;
	
	// konstruktor
	public SavingsAccount(String personalNumber) {
		
		super(personalNumber);
	}
	
	// en överskuggande uträkning på avslutnings räntan 
	@Override
	public BigDecimal calculateClosingInterest() {
		
		return getBalance().multiply(getInterest()).divide(new BigDecimal(100));
	}
	
	/* egen uttags metod som kontrollerar att man inte försöker ta ut med negativa tal eller ta ut mer än vad
	 * som saldot är updaterar tidsstämpeln om uttaget lyckas  
	 */
	@Override
	public boolean withdraw(int amount) {
		
		if (amount <= 0) {
			
			return false;
		}
		BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
		if (!freeWithdraw) {
			withdrawAmount = withdrawAmount.multiply(new BigDecimal("1.02"));
		}
		else {
			freeWithdraw = false;
		}
		if (getBalance().compareTo(withdrawAmount) >= 0) {
			setBalance(getBalance().subtract(withdrawAmount));
			updateTime();
			transactions.add(new Transactions(withdrawAmount.negate(), getBalance(), this.transactionsTime));
			return true;
		}
		return false;
	}
	
	// metod som gör att man kan hämta kontotypen
	@Override
	public String getAccountType() {
		
		return "Sparkonto";
	}
	
	// överskuggande tostring metod som skriver ut en formaterad sträng om kontot
	@Override
	public String toString() {
			
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("sv", "SE"));
		String balanceStr = currencyFormat.format(getBalance());
		NumberFormat percentFormat = NumberFormat.getPercentInstance(new Locale("sv","SE"));
		percentFormat.setMaximumFractionDigits(1);
		String percentStr = percentFormat.format(getInterest().divide(new BigDecimal(100)));
		return String.format("Kontonummer: %d Saldo: %s %s Ränta: %s", getAccountNr(), balanceStr, getAccountType(), percentStr);
	}
	
	/* överskuggande avslunings tostring metod skriver ut en sträng med kontospecifika uppgifter vid avslutande av kontot
	 * i formaterad form 
	*/
	@Override
	public String closingString() {
		
		BigDecimal calculatedInterest = calculateInterest();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("sv", "SE"));
		String balanceStr = currencyFormat.format(getBalance());
		String interestStr = currencyFormat.format(calculatedInterest);
		return String.format("Kontonummer: %d Saldo: %s %s Saldo + ränta: %s", getAccountNr(), balanceStr, getAccountType(), interestStr);
	}
}
