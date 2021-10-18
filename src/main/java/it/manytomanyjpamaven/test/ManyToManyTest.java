package it.manytomanyjpamaven.test;

import java.util.Date;
import java.util.List;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testRimuoviRuolo(ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testUtenteDiGiugno(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCercaAdmin(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testLunghezzaPassword(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testDescrizioneDistintaRuoliUtenti(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testAlmenoUnAdmin(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "manuvoTio1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testRimuoviRuolo(RuoloService ruoloService) throws Exception {

		System.out.println("-------Inizio Test Rimuovi Ruolo----------");

		Ruolo nuovoRuolo = new Ruolo("Utente Speciale", "ROLE_SPECIAL_USER");
		ruoloService.inserisciNuovo(nuovoRuolo);
		if (nuovoRuolo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");
		ruoloService.rimuovi(nuovoRuolo);

		System.out.println(".......Test Rimuovi Automobile Concuso: TUTTO OKAY!.............");

	}

	// DAMMI LA LISTA DI UTENTI CHE SONO STATI CREATI A GIUGNO

	private static void testUtenteDiGiugno(UtenteService utenteService) throws Exception {

		System.out.println("-----Inizio Test Utente creato a Giugno---------");

		List<Utente> listaUtenti = null;
		listaUtenti = utenteService.caricaUtenteCreatoAGiugno();

		if (listaUtenti == null)
			throw new RuntimeException("test Utente Creato a Giugno fallito: Query non eseguita ");

		System.out.println("------Test Utente Creato a Giugno Concluso: TUTTO OKAY!------");
	}

	// DAMMI IL NUMERO DI UTENTI CHE HANNO IL RUOLO DI ADMIN

	private static void testCercaAdmin(UtenteService utenteService) throws Exception {

		System.out.println("-------Test Cerca Admin--------");

		Long listaAdmin = null;
		listaAdmin = utenteService.cercaAdmin();

		if (listaAdmin == null)
			throw new RuntimeException("test Cerca Admin: Query non eseguita ");

		System.out.println("------Test Cerca Admin Concluso: TUTTO OKAY!");

	}

	// DAMMI LA LISTA DI UTENTI CON LA LUNGHEZZA PASSWORD INFERIORE A 8

	private static void testLunghezzaPassword(UtenteService utenteService) throws Exception {

		System.out.println("------Inizio Test Lunghezza Password");

		List<Utente> listaUtenti = null;
		listaUtenti = utenteService.cercaPasswordConLunghezzaMinore();

		if (listaUtenti == null)
			throw new RuntimeException("test Utente Creato a Giugno fallito: Query non eseguita ");

		System.out.println("------Fine Test Cerca Password con lunghezza minore di 8 concluso: TUTTO OKAY!------");

	}

	// DAMMI LA LISTA DI DESCRIZIONI DISTINTE DEI RUOLI CON UTENTI ASSOCIATI

	private static void testDescrizioneDistintaRuoliUtenti(UtenteService utenteService) throws Exception {

		System.out.println(".......Inizio Test descrizione distinta ruoli utenti.............");

		List<String> ruoli = null;
		ruoli = utenteService.cercaDescrizioneUtenti();

		if (ruoli == null)
			throw new RuntimeException(
					"Test Descrizione utenti fallito: c'è stato un errore nell'esecuzione della query");

		System.out.println(".......Test Descrizione Distinta Ruoli Utenti conclusa: TUTTO OKAY!.............");
	}

	// CONTROLLA SE CE' ALMENO UN ADMIN TRA GLI UTENTI DISABILITATI

	private static void testAlmenoUnAdmin(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {

		System.out.println("------Inizio Test Almeno un Admin--------");

		Ruolo ruoloEsistente = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");

		if (ruoloEsistente == null)
			throw new RuntimeException("Test Almeno un Admin fallito: ruolo non presente nel database! ");

		Utente utenteNuovo = new Utente("rocchui", "fgt5rde", "epoinb", "lepres", new Date());

		utenteNuovo.setStato(StatoUtente.DISABILITATO);

		utenteServiceInstance.inserisciNuovo(utenteNuovo);

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistente);

		System.out.println("---------Test almeno un Admin Concluso: TUTTO OKAY!-------");

	}

}
