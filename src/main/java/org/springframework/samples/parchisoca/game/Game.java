package org.springframework.samples.parchisoca.game;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.enums.GameStatus;
import org.springframework.samples.parchisoca.enums.GameType;
import org.springframework.samples.parchisoca.enums.TurnState;
import org.springframework.samples.parchisoca.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int game_id;

    @NotEmpty
    private String name;

    @ManyToOne()
    //@JoinColumn(name = "username")
    private User creator;

    Integer dice;

    private int max_player;

    @OneToOne
    private User current_player;

    private boolean has_started = false;

    private TurnState turn_state = TurnState.INIT;

    @ManyToOne()
    // @JoinColumn(name = "won_games")
    private User winner;

    @OneToOne(mappedBy = "game")
    private GameBoard gameboard;

    @ManyToMany()
    @JoinTable(name = "game_user",
        joinColumns = {
            @JoinColumn(name = "fk_game")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "fk_user")
        })
    private List < User > other_players;

    @OneToMany
    private List < User > current_players;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private GameType type;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime endTime;

    public void addUser(User user) throws Exception {
        if (other_players == null)
            other_players = new ArrayList < > ();

        System.out.println("adding user: " + user.getUsername());
        other_players.add(user);
        current_players.add(user);
        if (checkMaxAmountPlayers() == false) {
            has_started = true;
            System.out.println("The game starts now");
        }
    }

    public boolean checks(Color color) {

        //List<User> all_players = new ArrayList<>(this.getOther_players());
        //all_players.add(this.getCreator());

        for (User user: current_players) {
            if (user.getGamePieces().get(0).getTokenColor().getRGB() == color.getRGB())
                return false;
        }

        return true;
    }

    public boolean checkMaxAmountPlayers() {
        return this.getOther_players().size() + 1 < max_player;
    }

    @Transient
    public int getNumberPlayers() {
        if (other_players != null)
            return other_players.size();
        return 0;
    }

    public void rollDice() {
        //Random rand = new Random();
        //this.dice = rand.nextInt(6) + 1;
        this.dice = 5;
    }


    public Integer getAndResetDice() {
        Integer dice_roll = this.dice;
        dice = 0;
        return dice_roll;

    }

    public void setCurrent_players(User user) {
        current_players = new ArrayList < > ();
        current_players.add(user);
    }
    //can be deleted
    public Integer getDice() {
        System.out.println("Dice number: " + dice);
        return dice;
    }
}
