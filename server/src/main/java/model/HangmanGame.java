package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HangmanGame {
    private String word = pickWord();
    private Set<Character> guesses = new HashSet<Character>();
    private int triesLeft = word.length();

    private static String pickWord() {
        try {
            List<String> words = Files.readAllLines(Paths.get("words.txt"));
            Random random = new Random();
            int index = random.nextInt(words.size());
            return words.get(index);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean tryALetter(char guess) { //gissning
        if (triesLeft == 0) {
            return false;
        }

        if (guesses.contains(guess)) {
            return false; // triesLeft--; if you wanna be mean and decrease even if you guessed the same letter
        }
        if (!word.contains(guess + "")) {
            triesLeft--;
        }
        guesses.add(guess);
        return true;

    }

    public String clue() { //ledtråden
        StringBuilder clue = new StringBuilder("");
        for (int i = 0; i < word.length(); i++) {
            if (guesses.contains(word.charAt(i))) {
                clue.append(word.charAt(i));
            } else {
                clue.append('-');
            }
        }
        return clue.toString();
    }

    public int attemptsLeft() {
        return triesLeft;

    }
}
