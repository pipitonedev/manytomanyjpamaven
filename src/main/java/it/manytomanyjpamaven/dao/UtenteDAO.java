package it.manytomanyjpamaven.dao;

import java.util.List;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteDAO extends IBaseDAO<Utente> {

	public List<Utente> findAllByRuolo(Ruolo ruoloInput);

	public Utente findByIdFetchingRuoli(Long id);

	public List<Utente> findByUtenteCreated();

	public Long findByAdmin();

	public List<String> findByRuoloDescrizione();

	public List<Utente> findByPasswordLength();

	public boolean findByDisableAdmin();

}
