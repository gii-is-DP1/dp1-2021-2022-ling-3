package org.springframework.samples.parchisoca.model.game.AI;

import org.springframework.samples.parchisoca.service.BoardFieldService;
import org.springframework.samples.parchisoca.model.game.Game;
import org.springframework.samples.parchisoca.model.game.Option;
import org.springframework.samples.parchisoca.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.awt.*;


@Service
public class AStrategy_End implements AIStrategy{


    //Todo: change numbers
    private static final Map < Color, Integer> color_end = Map.of(
        Color.RED,  141,
        Color.BLUE, 124,
        Color.GREEN, 158,
        Color.YELLOW, 175
    );


    @Override
    public Boolean checkStrategy(List<Option> options, Game game, BoardFieldService boardFieldService, OptionService optionService){
        logger.info("Testing Strategy: " + this.getStrategyName());

        for(Option option : options){
            Integer field_number = Integer.parseInt(option.getText().substring(Option.MOVE.length()));

            if(field_number + game.getDice() == color_end.get(game.getCurrent_player().getTokenColor())){
                option.setChoosen(true);
                optionService.saveOption(option);
                return true;
            }
        }
        return false;
    }

    @Override
    public StrategyName getStrategyName(){
        return StrategyName.End;
    }

}
