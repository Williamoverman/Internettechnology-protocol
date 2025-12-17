package protocol.commands.ToHGame;

import connection.ClientConnection;
import games.ToH;
import managers.ToHManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;
import requests.ToHGame.ChoiceRequest;

import java.util.Random;

public record ChoiceCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("TOH_RESP", 67);
            return;
        }

        String username = registry.getUsername(connection);
        ToHManager gameManager = ToHManager.getInstance();

        ToH game = gameManager.getGame(username);
        if (game == null) {
            messenger.sendError("TOH_RESP", 10005);
            return;
        }

        ChoiceRequest request = gson.fromJson(jsonBody, ChoiceRequest.class);
        if (request == null || (!request.choice().equals("heads") && !request.choice().equals("tails"))) {
            messenger.sendError("TOH_RESP", 10004);
            return;
        }

        game.setChoice(username, request.choice());
        if (!game.bothPlayersChosen())
            return;

        processRound(game);
    }

    private void processRound(ToH game) {
        String playerOne = game.getPlayerOne();
        String playerTwo = game.getPlayerTwo();

        String choiceOne = game.getChoicePlayerOne();
        String choiceTwo = game.getChoicePlayerTwo();

        if (choiceOne.equals(choiceTwo)) {
            handleTie(game, playerOne, playerTwo);
            return;
        }

        Random random = new Random();
        String coinResult = random.nextBoolean() ? "heads" : "tails";

        String winner = choiceOne.equals(coinResult) ? playerOne : playerTwo;
        String loser = winner.equals(playerOne) ? playerTwo : playerOne;

        game.incrementScore(winner);

        int scoreWinner = winner.equals(playerOne)
                ? game.getScorePlayerOne()
                : game.getScorePlayerTwo();
        int scoreLoser = loser.equals(playerOne)
                ? game.getScorePlayerOne()
                : game.getScorePlayerTwo();

        sendResult(winner, loser, coinResult, scoreWinner, scoreLoser, game.getRoundNumber());

        if (scoreWinner >= 3)
            handleGameEnd(game, winner, loser, scoreWinner, scoreLoser);
        else {
            game.incrementRoundNumber();
            game.clearChoices();
        }
    }

    private void handleTie(ToH game, String playerOne, String playerTwo) {
        int round = game.getRoundNumber();

        game.clearChoices();

        String tieMsg = MessageFormatter.createTie(round);

        ClientConnection playerOneConnection = registry.getConnection(playerOne);
        ClientConnection playerTwoConnection = registry.getConnection(playerTwo);

        if (playerOneConnection != null)
            ClientMessenger.sendTo(playerOneConnection, tieMsg);
        if (playerTwoConnection != null)
            ClientMessenger.sendTo(playerTwoConnection, tieMsg);
    }

    private void sendResult(String winner, String loser, String coinResult, int scoreWinner, int scoreLoser, int roundNumber) {
        ClientConnection winnerConnection = registry.getConnection(winner);
        if (winnerConnection != null) {
            String message = MessageFormatter.createRoundResult(roundNumber, coinResult, winner, scoreWinner, scoreLoser);
            ClientMessenger.sendTo(winnerConnection, message);
        }

        ClientConnection loserConnection = registry.getConnection(loser);
        if (loserConnection != null) {
            String message = MessageFormatter.createRoundResult(roundNumber, coinResult, winner, scoreLoser, scoreWinner);
            ClientMessenger.sendTo(loserConnection, message);
        }
    }

    private void handleGameEnd(ToH game, String winner, String loser, int scoreWinner, int scoreLoser) {
        ClientConnection winnerConnection = registry.getConnection(winner);
        if (winnerConnection != null) {
            String message = MessageFormatter.createEndResult(winner, scoreWinner, scoreLoser);
            ClientMessenger.sendTo(winnerConnection, message);
        }

        ClientConnection loserConnection = registry.getConnection(loser);
        if (loserConnection != null) {
            String message = MessageFormatter.createEndResult(winner, scoreLoser, scoreWinner);
            ClientMessenger.sendTo(loserConnection, message);
        }

        ToHManager.getInstance().endGame(game.getPlayerOne(), game.getPlayerTwo());
    }
}
