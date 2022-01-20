package org.springframework.samples.parchisoca.model.game;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.service.BoardFieldService;
import org.springframework.samples.parchisoca.service.ParchisService;
import org.springframework.stereotype.Component;

@Component
public class StateExtra {

    private static BoardFieldService boardFieldService;
    @Autowired
    private BoardFieldService boardFieldService_;


    private static ParchisService parchisService;
    @Autowired
    private ParchisService parchisService_;

    @PostConstruct
    private void initStaticDao () {
       boardFieldService = this.boardFieldService_;
       parchisService = this.parchisService_;
    }

    public static void doAction(Game game){
        Parchis parchisBoard = (Parchis) game.getGameboard();
        parchisBoard.setKick(false);
        if(game.getGameboard().getOptions().get(0).getText()==Option.PASS_EXTRA){

        }else{

            GamePiece selec = StateMove.getMovingPiece(game);
            Integer nextPos= 0;

            if(!parchisBoard.isExtraAction()){
                nextPos =  StateMove.calcPosition(selec, 10, game);
            }else{
                nextPos =  StateMove.calcPosition(selec, 20, game);
            }
            StateMove.kickPiece(boardFieldService.find(nextPos, game.getGameboard()), selec, game);
            StateMove.movePiece(nextPos, selec, game);
        }


        if(game.getDice()==6){
            game.setTurn_state(TurnState.INIT);
        } else if(parchisBoard.isKick()){
            game.setTurn_state(TurnState.CHOOSEEXTRA);
        } 
        else{
            game.setTurn_state(TurnState.NEXT);
        }
        parchisBoard.setExtraAction(true);
        parchisService.handleState(game);
    }



}
