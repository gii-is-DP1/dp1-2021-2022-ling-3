package org.springframework.samples.parchisoca.game;

import lombok.Getter;
import lombok.Setter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.springframework.samples.parchisoca.enums.FieldType;
import org.springframework.samples.parchisoca.user.User;


import javax.persistence.*;

import java.util.List;
import java.util.*;
import java.awt.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
@Entity
@Table(name = "gamePieces")
public class GamePiece {

    @Transient
    private static final Logger logger = LogManager.getLogger(GamePiece.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer gamePiece_id;

    //should add some constraint here
    private Color tokenColor;

    //this should not be like this the game piece
    //should not be connected to both Game and GameBoard

    //@ManyToOne
    //GameBoard board;

    @ManyToOne
    @JoinColumn(name = "field")
    BoardField field;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user_id;

    //int xPosition;
    //int yPosition;


    private static final Map < Color, List < Pair < Double, Double >>> color_position_map = Map.of(
        Color.RED,  Arrays.asList(Pair.with(3.0, 3.0), Pair.with(3.0, 4.0), Pair.with(4.0, 3.0), Pair.with(4.0, 4.0)), //3.5|3.5
        Color.BLUE, Arrays.asList(Pair.with(16.0, 3.0), Pair.with(17.0, 3.0), Pair.with(16.0, 4.0), Pair.with(17.0, 4.0)), //16.5|3.5
        Color.GREEN, Arrays.asList(Pair.with(3.0, 16.0), Pair.with(4.0, 16.0), Pair.with(3.0, 17.0), Pair.with(4.0, 17.0)), //3.5|16.5
        Color.YELLOW, Arrays.asList(Pair.with(16.0, 16.0), Pair.with(16.0, 17.0), Pair.with(17.0, 16.0), Pair.with(17.0, 17.0)) //16.5, 16.5
    );

    private static final Map < Color, Pair < Double, Double >> oca_piece_position_map = Map.of(
        Color.RED, Pair.with(1.0, 1.0),
        Color.BLUE, Pair.with(2.0, 1.0),
        Color.GREEN, Pair.with(3.0, 1.0),
        Color.YELLOW, Pair.with(4.0, 1.0)
    );

    //TODO probably the work of Service??
    public Integer getPositionXInPixels(Integer size) {
        logger.info("size: " + size);
        Double pos_percentage = 0.0;
        if (field == null) {
            //piece is standing in base
            //find out position in base
            List < GamePiece > piece_list = new ArrayList < GamePiece > (user_id.getGamePieces());
            Collections.sort(piece_list, Comparator.comparingLong(GamePiece::getGamePiece_id));

            int index = piece_list.indexOf(this);

            pos_percentage = color_position_map.get(this.tokenColor).get(index).getValue0();
            pos_percentage *= size;
        } else {
            int dividor = ((field.getType() == FieldType.HORIZONTAL) ? 4 : 2);
            pos_percentage = field.getPositionXluInPixels(size) + Double.valueOf(field.getPositionXrbInPixels(size)) / dividor;
            System.out.println(Math.round(pos_percentage));


        }

        return (int) Math.round((pos_percentage));
    }

    public Integer getPositionXInPixelsOca(Integer size)
    {
        Double pos_percentage = 0.0d;
        if(field == null)
            pos_percentage = (double) BoardField.height /2;

    	return (int) Math.round((pos_percentage));
    }
    public Integer getPositionYInPixelsOca(Integer size) {

        Double pos_percentage = 0.0d;
        if(field == null)
            pos_percentage = (double) GameBoard.boardsize - (BoardField.height / 2);


    	return (int) Math.round(pos_percentage);
    }


    public Integer getPositionYInPixels(Integer size) {
        Double pos_percentage = 0.0;
        if (field == null) {
            //piece is standing in base
            //get the number of pieces that are already in the base
            List < GamePiece > piece_list = new ArrayList < GamePiece > (user_id.getGamePieces());
            Collections.sort(piece_list, Comparator.comparingLong(GamePiece::getGamePiece_id));

            int index = piece_list.indexOf(this);

            pos_percentage = color_position_map.get(this.tokenColor).get(index).getValue1();
            pos_percentage *= size;
        } else {
            //piece is standing on a game field
            //Calculates the middle of a board field
            int dividor = ((field.getType() == FieldType.VERTICAL) ? 4 : 2);
            pos_percentage = field.getPositionYluInPixels(size) + Double.valueOf(field.getPositionYrbInPixels(size))/dividor ;

            //int dividorOca = ((field.getType()==FieldType.SQUARE)?
            //pos_oca = field.get
        }

        return (int) Math.round(pos_percentage);
    }

    //Returns the tokenColor in String-Hex Format
    public String getColorInHex() {
        return String.format("#%06x", Integer.valueOf(this.tokenColor.getRGB() & 0x00FFFFFF));
    }
}
