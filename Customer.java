package chrwii3;

/*
 * Namn: Christoffer Wiik
 * 
 * Användarnamn LTU: chrwii3
 */
/*
 * Denna klass är för skapande av kunder med matoder för att byta namn och ta bort kund.
 * vi har instansvariabler varav pNo är personnummer som blir en konstant för att det inte
 * ska kunna ändras.
 * sedan har vi set metoder som gör att man kan ändra namn och efternamn
 * det finns metoder för att hämta namn efternamn och personnummer
 * det finns även en tostring metod som skrivet ut en formaterad sträng med personnummer namn och efternamn
 */

import java.io.Serializable;

public class Customer implements Serializable {
	
	// instans variabler
	private static final long serialVersionUID = 1L;
	private String customerName;
	private String customerSurname;
	private final String pNo;
	
	// kontsruktor med parametrar
	public Customer(String customerName, String customerSurname, String pNo) {
		
		this.customerName = customerName;
		this.customerSurname = customerSurname;
		this.pNo = pNo;
	}
	
	// metoden ändrar förnamnet
	public void setName(String name) {
		
		this.customerName = name;
	}
	
	// metoden ändrar efternamnet
	public void setSurname(String surname) {
		
		this.customerSurname = surname;
	}
	
	// metod för att hämta kundens förnamn.
	public String getName() {

		return customerName;
	}
	
	// metod för att hämta efternamn
	public String getSurname() {
		
		return customerSurname;
	}
	
	// metod för att hämta personnummer
	public String getPno() {
		
		return pNo;
	}
	
	// metoden returnerar info om kunden som förnamn efternamn och personnummer
	public String toString() {
		
		return  pNo + " " + customerName + " " + customerSurname;
	}
}
