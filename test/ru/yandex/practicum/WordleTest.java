package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundException;
import ru.yandex.practicum.exception.system.DictionaryFileNotFoundException;
import ru.yandex.practicum.exception.system.EmptyDictionaryException;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private PrintWriter log;

    @BeforeEach
    void start() {
        log = new PrintWriter(System.out, true);
    }

    @Test
    void normalizeWordTest() {
        assertEquals("елка", WordleDictionary.normalize("Ёлка"));
        assertEquals("мед", WordleDictionary.normalize("МЁД"));
    }

    @Test
    void fiveLetterWordsTest() {
        WordleDictionary dictionary = new WordleDictionary(List.of(
                "мёд", "машина", "вздор", "ежик", "шпиль"
        ));

        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("вздор"));
        assertTrue(dictionary.contains("шпиль"));
        assertFalse(dictionary.contains("машина"));
    }

    @Test
    void matchDifferentWords() throws InvalidWordException {
        assertEquals("+^-^-", WordleDictionary.match("гонец", "герой"));
    }

    @Test
    void matchSameWords() throws InvalidWordException {
        assertEquals("+++++", WordleDictionary.match("гонец", "гонец"));
    }

    @Test
    void matchAllWrong() throws InvalidWordException {
        assertEquals("-----", WordleDictionary.match("шпиль", "багет"));
    }

    @Test
    void matchWrongLengthTest() {
        assertThrows(InvalidWordException.class, () -> WordleDictionary.match("мёд", "кумыс"));
    }

    @Test
    void makeGuessWrongLengthTest() {
        WordleDictionary dictionary = new WordleDictionary((List.of("вздор", "шпиль", "кумыс")));
        WordleGame game = new WordleGame(dictionary, log);

        assertEquals(6, game.getSteps());
        assertThrows(InvalidWordException.class, () -> game.makeGuess("кот"));
        assertEquals(6, game.getSteps());
    }

    @Test
    void makeGuessUnknownWordTest() {
        WordleDictionary dictionary = new WordleDictionary((List.of("вздор", "шпиль", "кумыс")));
        WordleGame game = new WordleGame(dictionary, log);

        assertThrows(WordNotFoundException.class, () -> game.makeGuess("багет"));
        assertEquals(6, game.getSteps());
    }

    @Test
    void makeGuessCorrectWordTest() throws Exception {
        WordleDictionary dictionary = new WordleDictionary((List.of("вздор", "шпиль", "кумыс")));
        WordleGame game = new WordleGame(dictionary, log);

        String answer = game.getAnswer();
        String result = game.makeGuess(answer);

        assertEquals("+++++", result);
        assertTrue(game.isWon(answer));
        assertTrue(game.isFinished(answer));
        assertEquals(5, game.getSteps());
    }

    @Test
    void makeGuessSixWrongsTest() throws Exception {
        WordleDictionary dictionary = new WordleDictionary((List.of("вздор", "шпиль", "кумыс", "багет", "гонец", "герой", "котик")));
        WordleGame game = new WordleGame(dictionary, log);
        String answer = game.getAnswer();

        List<String> guesses = List.of("вздор", "шпиль", "кумыс", "багет", "гонец", "котик");
        String last = null;
        for (String guess : guesses) {
            if (guess.equals(answer)) guess = "герой";
            last = guess;
            game.makeGuess(guess);
        }

        assertEquals(0, game.getSteps());
        assertFalse(game.isWon(last));
        assertTrue(game.isFinished(last));
    }

    @Test
    void getHintFitsPreviousAttempts() throws Exception {
        WordleDictionary dictionary = new WordleDictionary((List.of("вздор", "шпиль", "кумыс", "багет", "гонец", "герой")));
        WordleGame game = new WordleGame(dictionary, log);
        String answer = game.getAnswer();

        String firstGuess = answer.equals("герой") ? "гонец" : "герой";
        String pattern = game.makeGuess(firstGuess);

        String hint = game.getHint();

        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(dictionary.contains(hint));
        assertEquals(pattern, WordleDictionary.match(firstGuess, hint));
        assertNotEquals(firstGuess, hint);
    }

    @Test
    void getHintTwiceReturnsDifferentWordsTest() {
        WordleDictionary dictionary = new WordleDictionary((List.of("вздор", "шпиль", "кумыс", "багет", "гонец", "герой")));
        WordleGame game = new WordleGame(dictionary, log);

        String hint1 = game.getHint();
        String hint2 = game.getHint();

        assertNotEquals(hint1, hint2);
    }

    @Test
    void loadsFromTempFileTest() throws Exception {
        Path file = Files.createTempFile("dict", ".txt");
        Files.writeString(file, "вздор\nшпиль\nдом\n");

        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        WordleDictionary dictionary = loader.load(file.toString(), log);

        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("шпиль"));

        Files.deleteIfExists(file);
    }

    @Test
    void MissingFileTest() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        assertThrows(DictionaryFileNotFoundException.class, () -> loader.load("some_file.txt", log));
    }

    @Test
    void noFiveLetterWordsTest() throws Exception {
        Path file = Files.createTempFile("dict", ".txt");
        Files.writeString(file, "дом\nмашина\nкот");

        WordleDictionaryLoader loader = new WordleDictionaryLoader();

        assertThrows(EmptyDictionaryException.class, () -> loader.load(file.toString(), log));
        Files.deleteIfExists(file);
    }
}
