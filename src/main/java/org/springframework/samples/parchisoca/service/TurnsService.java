package org.springframework.samples.parchisoca.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisoca.repository.TurnsRepository;
import org.springframework.samples.parchisoca.model.game.Turns;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TurnsService {

    @Autowired
    TurnsRepository turnsRepo;

    @Autowired
    public TurnsService(TurnsRepository turnsrepo) {
        this.turnsRepo = turnsrepo;
    }

    @Transactional
    public void saveTurn(Turns turns) throws DataAccessException {
        turnsRepo.save(turns);
    }

    public Optional<Turns> findTurn(int id) {
        return turnsRepo.findById(id);
    }

}
