package org.springframework.samples.parchisoca.model.game;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.service.ParchisService;
import org.springframework.samples.parchisoca.service.TurnsService;
import org.springframework.stereotype.Component;


@Component
public class StateRollDice {



    private static final Logger logger = LogManager.getLogger(StateRollDice.class);

    private static TurnsService turnsService;
    @Autowired
    private TurnsService turnsService_;


    private static ParchisService parchisService;
    @Autowired
    private ParchisService parchisService_;

    @PostConstruct
    private void initStaticDao () {
       parchisService = this.parchisService_;
       turnsService = this.turnsService_;
    }

    public static void doAction(Game game){
        game.rollDice();
        logger.info("Dice Rolled: " + game.dice);

        if(game.getTurns().size()<game.getMax_player()){

            Turns newturn = new Turns();
            newturn.setNumber(game.dice);
            newturn.setUser_id(game.getCurrent_player());


            newturn.setUser_id(game.getCurrent_player());

            game.setTurn_state(TurnState.DIRECTPASS);
            turnsService.saveTurn(newturn);
            try {
            game.addTurn(newturn);
            }catch(Exception e){
                logger.error("ERROR: Game has not been created!");
            }


        }else{
        game.setTurn_state(TurnState.CHOOSEPLAY);}

        parchisService.handleState(game);
}

}
