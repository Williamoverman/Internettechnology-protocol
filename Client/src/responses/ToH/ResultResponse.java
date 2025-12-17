package responses.ToH;

public record ResultResponse(int round, String coin, String winner, Score score) {}