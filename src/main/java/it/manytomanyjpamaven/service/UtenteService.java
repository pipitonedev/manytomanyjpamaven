package it.manytomanyjpamaven.service;

import java.util.List;

import it.manytomanyjpamaven.dao.UtenteDAO;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteService {

	public List<Utente> listAll() throws Exception;

	public Utente caricaSingoloElemento(Long id) throws Exception;

	public void aggiorna(Utente utenteInstance) throws Exception;

	public void inserisciNuovo(Utente utenteInstance) throws Exception;

	public void rimuovi(Utente utenteInstance) throws Exception;

	public void aggiungiRuolo(Utente utenteEsistente, Ruolo ruoloInstance) throws Exception;

	public Utente caricaUtenteSingoloConRuoli(Long id) throws Exception;

	public List<Utente> caricaUtenteCreatoAGiugno() throws Exception;

	public Long cercaAdmin() throws Exception;

	public List<Utente> cercaPasswordConLunghezzaMinore() throws Exception;

	public List<String> cercaDescrizioneUtenti() throws Exception;
	
	public boolean adminDisabilitati() throws Exception;

	// per injection
	public void setUtenteDAO(UtenteDAO utenteDAO);

}
