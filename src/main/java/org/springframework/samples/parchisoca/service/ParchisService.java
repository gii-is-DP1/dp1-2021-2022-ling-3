package org.springframework.samples.parchisoca.service;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisoca.enums.FieldType;
import org.springframework.samples.parchisoca.model.game.AI.AIService;
import org.springframework.samples.parchisoca.model.game.Parchis;
import org.springframework.samples.parchisoca.model.game.*;
import org.springframework.samples.parchisoca.repository.GameBoardRepository;
import org.springframework.samples.parchisoca.repository.ParchisRepository;
import org.springframework.samples.parchisoca.model.user.User;
import org.springframework.samples.parchisoca.model.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ParchisService {


    private static final Logger logger = LogManager.getLogger(ParchisService.class);


    @Autowired
    ParchisRepository parchisRepo;

    @Autowired
    AIService aiService;

    @Autowired
    BoardFieldService boardFieldService;

    @Autowired
    UserService userService;

    GameBoardRepository gameBoardRepository;


    public static final String END = "#a000000";
    public static final String STANDARD_FILL_COLOR = "#fef9e7";
    public static final String GREEN_END = "#26ca0c";
    public static final String RED_END = "#e32908";
    public static final String BLUE_END = "#0890e3";
    public static final String YELLOW_END = "#dbe117";

    public static final Integer FIELD_WIDTH = 2;
    public static final Integer FIELD_HEIGHT = 1;

    private static final Integer NUMBER_FIELDS_COURSE = 68;
    private static final Integer YELLOW_END_NUMBER = 175;
    private static final Integer BLUE_END_NUMBER = 124;
    private static final Integer RED_END_NUMBER = 141;
    private static final Integer GREEN_END_NUMBER = 158;
    private static final Integer FIRST_FIELD = 1;

    public Optional < Parchis > findById(Integer id) {
        return parchisRepo.findById(id);
    }





    @Autowired
    public ParchisService(ParchisRepository parchisRepository,
                           GameBoardRepository gameBoardRepository, BoardFieldService boardFieldService,
                          UserService userService, AIService aiService) {
        this.parchisRepo = parchisRepository;
        this.gameBoardRepository = gameBoardRepository;
        this.boardFieldService = boardFieldService;
        this.userService = userService;
        this.aiService = aiService;
    }

    public void initGameBoard(Game game) {
        //Todo: should not be hard coded
        Parchis gameBoard = new Parchis();
        gameBoard.setBackground("resources/images/background_board.jpg");
        gameBoard.setHeight(800);
        gameBoard.setWidth(800);

        //Create Game fields
        logger.info("creating gameFields");

        gameBoard.setFields(new ArrayList <BoardField> ());
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


    public void handleState(Game game) {
        switch (game.getTurn_state()) {
            case INIT:
                logger.info("Handle State Init, " + game.getCurrent_player().getFirstname());
                StateInit.doAction(game);
                break;
            case ROLLDICE:
                logger.info("Handle State ROLLEDICE, " + game.getCurrent_player().getFirstname());
                StateRollDice.doAction(game);
                User myuser = userService.getCurrentUser().get();
                myuser.setRolledDices(myuser.getRolledDices() + 1);
                break;

            case DIRECTPASS:
                StateDirectPass.doAction(game);
                if(game.getCurrent_player().getRole() == UserRole.AI){
                    aiService.choosePlay(game, this);
                }
                break;
            case CHOOSEPLAY:
                logger.info("Handle State CHOOSEPLAY,"+ game.getCurrent_player().getFirstname());
                StateChoosePlay.doAction(game);
                if(game.getCurrent_player().getRole() == UserRole.AI){
                    logger.info("AI chooses play");
                    aiService.choosePlay(game, this);
                }
                break;
            case CHOOSEEXTRA:
                logger.info("Handle State CHOOSEEXTRA,"+ game.getCurrent_player().getFirstname());

                StateChooseExtra.doAction(game);
                logger.info("ROle = " + game.getCurrent_player().getRole());
                if(game.getCurrent_player().getRole() == UserRole.AI && userService.getCurrentUser().get() == game.getCreator()){
                    logger.info("AI going to chooseextra");
                    aiService.choosePlay(game, this);
                }
                break;
            case EXTRA:
                StateExtra.doAction(game);
                break;
            case PASSMOVE:
                StatePassMove.doAction(game);
                break;
            case MOVE:
                logger.info("Handle State MOVE, " + game.getCurrent_player().getFirstname());
                StateMove.doAction(game);
                break;
            case NEXT:
                if(game.getTurns().size()<game.getMax_player()){
                    StateNext.doActionI(game);}
                else{
                    StateNext.doAction(game);
                }
                break;
            case FINISHED:
                StateFinished.doAction(game);
                break;
            }
    }


    public void setNextFields(GameBoard board){
        for(BoardField field : board.getFields()){
            BoardField next = null;
            if (field.getNumber() == NUMBER_FIELDS_COURSE) next = boardFieldService.find(FIRST_FIELD, board);
            else if (field.getNumber() == GREEN_END_NUMBER) next = boardFieldService.find(GREEN_END_NUMBER, board);
            else if (field.getNumber() == YELLOW_END_NUMBER) next = boardFieldService.find(YELLOW_END_NUMBER, board);
            else if (field.getNumber() == BLUE_END_NUMBER) next = boardFieldService.find(BLUE_END_NUMBER, board);
            else if (field.getNumber() == RED_END_NUMBER) next = boardFieldService.find(RED_END_NUMBER, board);
            else next = boardFieldService.find(field.getNumber() + 1, board);
            field.setNext_field(next);
        }
    }

    private void setSpecialFields(GameBoard board){
        //special fields
        boardFieldService.find(5, board).setParchis_special(true);
        int id = 12;
        while(id < NUMBER_FIELDS_COURSE){
            for(int i = 0; i < 3 && id <= NUMBER_FIELDS_COURSE; i++){
                BoardField field = boardFieldService.find(id, board);
                field.setParchis_special(true);
                boardFieldService.saveBoardField(field);
                id += 5;
            }
            id += 2;
        }
    }

    public void deleteSinglePiece(Game game, GamePiece piece){

        User user = piece.getUser_id();

        piece.getField().setListGamesPiecesPerBoardField(new ArrayList<GamePiece>());
        piece.setUser_id(null);
        piece.setField(null);
        user.getGamePieces().remove(piece);
        userService.saveUser(user, user.getRole());
    }

    //Calculates all the Board Field entities that are needed
    public void createGameFields(GameBoard board) {
        int id;
        int column = 7;
        int row = 0;


        //create all base fields
        //ids 35 to 43 and 59 to 67
        id = 35;
        for (row = 0; row < 20; row++) {
            if (row == 9 || row == 10) {
                id = 59;
                continue;
            }
            board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id++;
        }

        //fields 34 and 68
        column = 9;
        row = 0;
        id = 34;
        board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
        row = 19;
        id = 68;
        board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));


        //ids 1-9 and 25-33
        column = 11;
        id = 33;
        for (row = 0; row < 20; row++) {
            if (row == 9 || row == 10) {
                id = 9;
                continue;
            }
            board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
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
            board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
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
            board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id++;
        }

        //ids 51 and 17
        column = 0;
        row = 9;
        id = 51;
        board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR,FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
        column = 19;
        id = 17;
        board.getFields().add(new BoardField(id, STANDARD_FILL_COLOR, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));


        //create the end fields

        //green end fields
        row = 9;
        id = 151; //Todo: not sure what ids for the end fields
        for (column = 1; column < 8; column++) {
            board.getFields().add(new BoardField(id, GREEN_END, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id++;
        }


        //blue end fields
        row = 9;
        id = 123; //Todo: not sure what ids for the end fields
        for (column = 12; column < 19; column++) {
            board.getFields().add(new BoardField(id, BLUE_END, FieldType.VERTICAL, column, row, FIELD_HEIGHT, FIELD_WIDTH));
            id--;
        }


        //ids red end fields
        column = 9;
        id = 134;
        for (row = 1; row < 8; row++) {
            board.getFields().add(new BoardField(id, RED_END, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id++;
        }

        //ids yellow end fields
        column = 9;
        id = 174;
        for (row = 12; row < 19; row++) {
            board.getFields().add(new BoardField(id, YELLOW_END, FieldType.HORIZONTAL, column, row, FIELD_WIDTH, FIELD_HEIGHT));
            id--;
        }


        board.getFields().add(new BoardField(YELLOW_END_NUMBER, END, FieldType.HORIZONTAL, 9, 11, FIELD_WIDTH, FIELD_HEIGHT));
        board.getFields().add(new BoardField(GREEN_END_NUMBER, END, FieldType.VERTICAL, 8, 9, FIELD_HEIGHT, FIELD_WIDTH));
        board.getFields().add(new BoardField(BLUE_END_NUMBER, END, FieldType.VERTICAL, 11, 9, FIELD_HEIGHT, FIELD_WIDTH));
        board.getFields().add(new BoardField(RED_END_NUMBER, END, FieldType.HORIZONTAL, 9, 8, FIELD_WIDTH, FIELD_HEIGHT));




    }

    @Transactional
    public void saveParchis(Parchis parchis) throws DataAccessException {
        parchisRepo.save(parchis);
    }





}
