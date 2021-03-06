package org.springframework.samples.parchisoca.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.parchisoca.enums.GameStatus;
import org.springframework.samples.parchisoca.model.game.Game;

import java.util.List;
import java.util.Optional;


public interface GameRepository extends CrudRepository<Game, Integer> {


    List<Game> findByStatus(@Param("status") GameStatus status) throws DataAccessException;

    List<Game> findAll();

    Optional<Game> findByName(@Param("name") String name) throws DataAccessException;

    boolean existsByName(@Param("name") String name) throws DataAccessException;



}
