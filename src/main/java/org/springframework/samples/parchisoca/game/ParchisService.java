package org.springframework.samples.parchisoca.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;

import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisoca.enums.FieldType;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.samples.parchisoca.user.UserService;
import org.springframework.samples.parchisoca.user.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ParchisService {


    private static final Logger logger = LogManager.getLogger(ParchisService.class);


    @Autowired
    ParchisRepository parchisRepo;

    @Autowired
    GameService gameService;

    @Autowired
    BoardFieldRepository boardFieldRepository;

    @Autowired
    BoardFieldService boardFieldService;

    GameRepository gameRepository;

    @Autowired
    OptionService optionService;

    @Autowired
    UserService userService;

    GameBoardRepository gameBoardRepository;
    public static final String STANDARD_FILL_COLOR = "#fef9e7";
    public static final String GREEN_END = "#26ca0c";
    public static final String RED_END = "#e32908";
    public static final String BLUE_END = "#0890e3";
    public static final String YELLOW_END = "#dbe117";

    public static final Integer FIELD_WIDTH = 2;
    public static final Integer FIELD_HEIGHT = 1;

    public Optional < Parchis > findById(Integer id) {
        return parchisRepo.findById(id);
    }



    @Autowired
    public ParchisService(ParchisRepository parchisRepository,
        GameRepository gameRepository, GameBoardRepository gameBoardRepository, BoardFieldRepository boardRepo, BoardFieldService boardFieldService,
        UserService userService, OptionService optionservice, GameService gameservice) {
        this.parchisRepo = parchisRepository;
        this.gameRepository = gameRepository;
        this.gameBoardRepository = gameBoardRepository;
        this.boardFieldRepository = boardRepo;
        this.boardFieldService = boardFieldService;
        this.userService = userService;
        this.gameService = gameservice;
        this.optionService = optionservice;
    }

    public void initGameBoard(Game game) {
        //Todo: should not be hard coded
        Parchis gameBoard = new Parchis();
        gameBoard.background = "resources/images/background_board.jpg";
        gameBoard.height = 800;
        gameBoard.width = 800;

        //Create Game fields
        logger.info("creating gameFields");

        gameBoard.fields = new ArrayList < BoardField > ();
        this.createGameFields(gameBoard);
        logger.info("finished creating gameFields");

        logger.info("setting gameboard");
        gameBoard.setGame(game);
        game.setGameboard(gameBoard);



        try {
            this.gameBoardRepository.save(gameBoard);
        } catch (Exception e) {
            logger.error("ERROR: " + e.getMessage());
        }

        for (BoardField field: gameBoard.getFields()) {
            field.setBoard(gameBoard);
            boardFieldService.saveBoardField(field);
        }

        setNextFields(game.getGameboard());
        setSpecialFields(game.getGameboard());
    }
    // public Map<User,Integer> turns(Game game,Map<User,Integer> turns){
    //     switch (game.getTurn_state()) {
    //     case INIT:
    //             System.out.println("Current Player in Init: " + game.getCurrent_player().getUsername());
    //             if (game.getCurrent_player() == userService.getCurrentUser().get()) {
    //                 userService.getCurrentUser().get().setMyTurn(true);
    //                 System.out.println("The current user has been found:");
    //             }
    //     break;
    //     case ROLLDICE:
    //         game.rollDice();
    //         System.out.println("Dice Rolled: " + game.dice);
    //         turns.put(game.getCurrent_player(), game.getDice());
    //         game.setTurn_state(TurnState.CHOOSEPLAY);
    //         turns(game, turns);
    //     break;
    //     case CHOOSEPLAY:
    //         Parchis parchisOptions = (Parchis) game.getGameboard();
    //         parchisOptions.options = new ArrayList<>();
                
    //         Option option = new Option();
    //         option.setNumber(1);
    //         option.setText("Pass turn");
    //         optionService.saveOption(option);
    //         parchisOptions.options.add(option);
    //     break;
    //     case MOVE:
    //         Parchis parchisBoard = (Parchis) game.getGameboard();
                
    //         BoardField fieldSelec = boardFieldService.find(1, game.getGameboard());
    //         GamePiece selec = game.getCurrent_player().getGamePieces().get(0);
    //         for (Option opt: ((Parchis) game.getGameboard()).options) {
    //             if (opt.getChoosen()) {
    //                 System.out.println("The Choice is: " + opt.getText());
    //                 fieldSelec = boardFieldService.find(opt.getNumber(), game.getGameboard());
    //             }
    //         }
                
    //         game.setTurn_state(TurnState.NEXT);
    //         turns(game,turns);
    //     break;

    //     case NEXT:
    //         int index_last_player = game.getCurrent_players().indexOf(game.getCurrent_player());
    //         System.out.println("Index of current player" + game.getCurrent_player().getUsername() + ": " + index_last_player);
    //         System.out.println("Size of List: " + game.getCurrent_players().size());


    //         if (index_last_player == game.getCurrent_players().size() - 1) {
    //             //next player is the first one in the list
    //             Map<User,Integer> mapaOrdenado = turns.entrySet().stream()
    //                 .sorted((Map.Entry.<User,Integer>comparingByValue().reversed()))
    //                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2)->e1, LinkedHashMap::new));
    //             List<User> listaOrdenado = mapaOrdenado.keySet().stream().collect(Collectors.toList());
    //             game.setCurrent_player(listaOrdenado.get(0));
    //             System.out.println("Current player after setting if: " + game.getCurrent_player().getUsername());

    //         } else {
    //             //next player is the next one in the list
    //             game.setCurrent_player(game.getCurrent_players().get(index_last_player + 1));
    //             System.out.println("Current player after setting else: " + game.getCurrent_player().getUsername());
    //         }
    //         game.setTurn_state(TurnState.INIT);
    //         System.out.println("Current player after setting " + game.getCurrent_player().getUsername());
    //     }
    public void handleState(Game game) {
        switch (game.getTurn_state()) {
            case INIT:
                StateInit.doAction(game);
                break;
            case ROLLDICE:
                StateRollDice.doAction(game);
                break;

           
                
            
            //SPECIAL roldice FOR WHEN YOU KILL SOMEONE
            // case SPECIALROLLDICE :
            //     game.rollDice();
            //     game.setDice(20);
            //     System.out.println("Dice Rolled: " + game.dice);
            //     game.setTurn_state(TurnState.CHOOSEPLAY);
            //     handleState(game);
            //     break;
            case DIRECTPASS:
                StateDirectPass.doAction(game);
            break;
            case CHOOSEPLAY:
                
                    StateChoosePlay.doAction(game);
                
                break;
            case PASSMOVE:
                StatePassMove.doAction(game);
            break;
            case MOVE:
                
                StateMove.doAction(game);
                break;
            case CHOOSEEXTRA:
                StateChooseExtra.doAction(game);
                break;
            case EXTRA:
                StateExtra.doAction(game);
                break;
            case NEXT:
            if(game.getTurns().size()<game.getMax_player()){
                StateNext.doActionI(game);}
            else{
                StateNext.doAction(game);
            }
            
                break;
            }
        logger.info("current state: " + game.getTurn_state());
    }








    public void setNextFields(GameBoard board){
        for(BoardField field : board.getFields()){
            BoardField next = null;
            if (field.getNumber() == 68) next = boardFieldService.find(1, board);
            else if (field.getNumber() == 174 || field.getNumber() == 157 || field.getNumber() == 140 || field.getNumber() == 123) {} else next = boardFieldService.find(field.getNumber() + 1, board);
            field.setNext_field(next);
        }
    }

    private void setSpecialFields(GameBoard board){
        //special fields
        boardFieldService.find(4, board).setParchis_special(true);
        int id = 13;
        while(id < 66){
            for(int i = 0; i < 3 && id <= 68; i++){
                BoardField field = boardFieldService.find(id, board);
                field.setParchis_special(true);
                boardFieldService.saveBoardField(field);
                id += 4;
            }
            id += 5;
        }
    }

    //Calculates all the Board Field entities that are needed
    public void createGameFields(GameBoard board) {
        int id;
        int column = 7;
        int row = 0;


        // BoardField[][] field_array = new BoardField[20][20];  unfortunately this does not work with oneToMany relationship

        //create all base fields

        //ids 35 to 43 and 59 to 67
        id = 35;
        for (row = 0; row < 20; row++) {
            if (row == 9 || row == 10) {
                id = 59;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id++;
        }

        //fields 34 and 68
        column = 9;
        row = 0;
        id = 34;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
        row = 19;
        id = 68;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));


        //ids 1-9 and 25-33
        column = 11;
        id = 33;
        for (row = 0; row < 20; row++) {
            if (row == 9 || row == 10) {
                id = 9;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id--;
        }

        //ids 50 to 44 and 24 to 18
        row = 7;
        id = 50;
        for (column = 0; column < 20; column++) {
            if (column > 6 && column < 13) {
                id = 24;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id--;
        }

        //ids 52 to 58 and 10 to 16
        row = 11;
        id = 52;
        for (column = 0; column < 20; column++) {
            if (column > 6 && column < 13) {
                id = 10;
                continue;
            }
            board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id++;
        }

        //ids 51 and 17
        column = 0;
        row = 9;
        id = 51;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR,FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
        column = 19;
        id = 17;
        board.fields.add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));


        //create the end fields

        //green end fields
        row = 9;
        id = 151; //Todo: not sure what ids for the end fields
        for (column = 1; column < 8; column++) {
            board.fields.add(new BoardField(id, GREEN_END, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id++;
        }


        //blue end fields
        row = 9;
        id = 123; //Todo: not sure what ids for the end fields
        for (column = 12; column < 19; column++) {
            board.fields.add(new BoardField(id, BLUE_END, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id--;
        }


        //ids red end fields
        column = 9;
        id = 134;
        for (row = 1; row < 8; row++) {
            board.fields.add(new BoardField(id, RED_END, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id++;
        }


        //ids yellow end fields
        column = 9;
        id = 174;
        for (row = 12; row < 19; row++) {
            board.fields.add(new BoardField(id, YELLOW_END, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id--;
        }


    }

    @Transactional
    public void saveParchis(Parchis parchis) throws DataAccessException {
        parchisRepo.save(parchis);
    }





}
