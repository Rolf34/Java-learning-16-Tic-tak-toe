import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class american {
    private static Scanner arbuz = new Scanner(System.in);
    private static int razmerDoski = 3;
    private static String player1Name = "Player 1";
    private static String player2Name = "Player 2";
    private static final String CONFIG_FILE = "config.txt";
    private static final String STATS_FILE = "statistics.txt";
    
    public static void main(String[] args) {
        loadConfiguration();
        mainGameLoop();
        arbuz.close();
        System.out.println("Vihid z programi...");
    }

    private static void mainGameLoop() {
        boolean codeisrunning = true;
        while (codeisrunning) {
            displayMainMenu();
            if (!arbuz.hasNextLine()) {
                System.out.println("Nepravilniy vvid.");
                continue;
            }
            
            String input = arbuz.nextLine();
            if (input.isEmpty()) {
                System.out.println("Pomilka v zapiti, spobuyte she raz.");
                continue;
            }
            
            char choice = input.charAt(0);
            switch (choice) {
                case '1' -> handleGameMenu();
                case '2' -> handleSettingsMenu();
                case '3' -> showStatistics();
                case '4' -> codeisrunning = handleExitMenu();
                default -> System.out.println("Pomilka v zapiti, spobuyte she raz.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("""
                ===Welcome to Main Menu===
                 ==Vas vitae Pes Patron==
                1. Start game
                2. Settings
                3. Statistics
                4. Exit""");
    }

    private static char[][] createGameBoard(int rows, int cols) {
        char[][] displayBoard = new char[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                displayBoard[i][j] = ' ';
            }
        }
        
        for (int i = 0; i < razmerDoski; i++) {
            displayBoard[0][i * 4 + 2] = (char) ('1' + i);
            displayBoard[i * 2 + 2][0] = (char) ('1' + i);
        }
        
        for (int i = 1; i < rows; i += 2) {
            for (int j = 1; j < cols; j++) {
                displayBoard[i][j] = '-';
            }
        }
        
        for (int i = 0; i < rows; i++) {
            for (int j = 4; j < cols; j += 4) {
                displayBoard[i][j - 1] = '|';
            }
        }
        
        return displayBoard;
    }

    private static void displayBoard(char[][] board) {
        for (char[] rowArray : board) {
            System.out.println(rowArray);
        }
    }

    private static boolean isValidMove(int row, int col, char[][] board) {
        return row >= 1 && row <= razmerDoski && 
               col >= 1 && col <= razmerDoski && 
               board[(row - 1) * 2 + 2][(col - 1) * 4 + 2] == ' ';
    }

    private static boolean checkWin(char[][] board, char igrok, int rows, int cols) {
        for (int i = 2; i < rows; i += 2) {
            int count = 0;
            for (int j = 2; j < cols; j += 4) {
                if (board[i][j] == igrok) {
                    count++;
                    if (count == razmerDoski) return true;
                } else {
                    count = 0;
                }
            }
        }
        for (int j = 2; j < cols; j += 4) {
            int count = 0;
            for (int i = 2; i < rows; i += 2) {
                if (board[i][j] == igrok) {
                    count++;
                    if (count == razmerDoski) return true;
                } else {
                    count = 0;
                }
            }
        }
        int count = 0;
        for (int i = 0; i < razmerDoski; i++) {
            if (board[i * 2 + 2][i * 4 + 2] == igrok) {
                count++;
                if (count == razmerDoski) return true;
            } else {
                count = 0;
            }
        }

        count = 0;
        for (int i = 0; i < razmerDoski; i++) {
            if (board[i * 2 + 2][(razmerDoski - 1 - i) * 4 + 2] == igrok) {
                count++;
                if (count == razmerDoski) return true;
            } else {
                count = 0;
            }
        }

        return false;
    }

    private static boolean checkDraw(char[][] board, int rows, int cols) {
        for (int i = 2; i < rows; i += 2) {
            for (int j = 2; j < cols; j += 4) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    private static void handleGameMenu() {
        boolean inGameMenu = true;
        while (inGameMenu) {
            System.out.println(": Board size: " + razmerDoski + "x" + razmerDoski);
            System.out.println("Players: " + player1Name + " (X) vs " + player2Name + " (O)");
            System.out.println("Are you ready?(1) Yes! (2)Go back to main menu");
            
            String input = arbuz.nextLine();
            if (input.isEmpty()) {
                System.out.println("Pomilka v zapiti, spobuyte she raz.");
                continue;
            }
            
            char choice = input.charAt(0);
            if (choice == '2') {
                inGameMenu = false;
            } else if (choice == '1') {
                playGame();
                inGameMenu = false;
            } else {
                System.out.println("Pomilka v zapiti, spobuyte she raz.");
            }
        }
    }

    private static void playGame() {
        int rows = razmerDoski * 2 + 1;
        int cols = razmerDoski * 4 - 1;
        char[][] board = createGameBoard(rows, cols);
        char currentPlayer = 'X';
        boolean isGameOver = false;
        String winner = null;
        String currentPlayerName = player1Name;

        while (!isGameOver) {
            System.out.println("\nZaraz shturmuye: " + currentPlayerName + " (" + currentPlayer + ")");
            displayBoard(board);

            int[] move = getPlayerMove(board);
            if (move[0] == 0) {
                break;
            }

            int displayRow = (move[0] - 1) * 2 + 2;
            int displayCol = (move[1] - 1) * 4 + 2;
            board[displayRow][displayCol] = currentPlayer;

            if (checkWin(board, currentPlayer, rows, cols)) {
                System.out.println("POTUZHNA PEREMOGA " + currentPlayerName + " (" + currentPlayer + ") !!!");
                winner = currentPlayerName;
                isGameOver = true;
            } else if (checkDraw(board, rows, cols)) {
                System.out.println("Nichya nachalnika!");
                winner = "Draw";
                isGameOver = true;
            }

            if (currentPlayer == 'X') {
                currentPlayer = 'O';
                currentPlayerName = player2Name;
            } else {
                currentPlayer = 'X';
                currentPlayerName = player1Name;
            }
        }

        System.out.println("\nItogove pole:");
        displayBoard(board);
        
        if (winner != null) {
            saveGameStatistics(winner);
        }
    }

    private static int[] getPlayerMove(char[][] board) { 
        while (true) {
            System.out.println("Vvedit ryad (1-" + razmerDoski + ", or 0 to exit):");
            String input = arbuz.nextLine();
            if (input.isEmpty()) continue;
            int row = input.charAt(0) - '0';
            
            if (row == 0) return new int[]{0, 0};

            System.out.println("Vvedit colonku (1-" + razmerDoski + "):");
            input = arbuz.nextLine();
            if (input.isEmpty()) continue;
            int col = input.charAt(0) - '0';

            if (isValidMove(row, col, board)) { 
                return new int[]{row, col};
            }
            System.out.println("Kabinka zaynyata! Sprobuyte she raz."); 
        }
    }

    private static void handleSettingsMenu() {
        boolean inSettingsMenu = true;
        while (inSettingsMenu) {
            System.out.println("""
                ===Settings Menu===
                1. Change board size
                2. Change player names
                0. Go back to main menu""");
            
            String input = arbuz.nextLine();
            if (input.isEmpty()) {
                System.out.println("Pomilka v zapiti, spobuyte she raz.");
                continue;
            }
            
            char choice = input.charAt(0);
            switch (choice) {
                case '1' -> changeBoardSize();
                case '2' -> changePlayerNames();
                case '0' -> inSettingsMenu = false;
                default -> System.out.println("Pomilka v zapiti, spobuyte she raz.");
            }
            
            if (choice == '1' || choice == '2') {
                saveConfiguration();
            }
        }
    }
    
    private static void changeBoardSize() {
        System.out.println("""
            Vibir rozmiru doshki:
            1. 3x3
            2. 5x5
            3. 7x7
            4. 9x9
            0. Cancel""");
        
        String input = arbuz.nextLine();
        if (input.isEmpty()) {
            System.out.println("Pomilka v zapiti, spobuyte she raz.");
            return;
        }
        
        char choice = input.charAt(0);
        switch (choice) {
            case '1' -> razmerDoski = 3;
            case '2' -> razmerDoski = 5;
            case '3' -> razmerDoski = 7;
            case '4' -> razmerDoski = 9;
            case '0' -> { return; }
            default -> {
                System.out.println("Pomilka v zapiti, spobuyte she raz.");
                return;
            }
        }
        System.out.println("Vstanovleno rozmir " + razmerDoski + "x" + razmerDoski);
    }
    
    private static void changePlayerNames() {
        System.out.println("Enter name for Player 1 (X): ");
        String input = arbuz.nextLine();
        if (!input.isEmpty()) {
            player1Name = input;
        }
        
        System.out.println("Enter name for Player 2 (O): ");
        input = arbuz.nextLine();
        if (!input.isEmpty()) {
            player2Name = input;
        }
        
        System.out.println("Player names updated: " + player1Name + " (X) vs " + player2Name + " (O)");
    }

    private static boolean handleExitMenu() {
        System.out.println("Are you sure bra? ( enter 1(Yep) or 2(NUH UH)");
        String input = arbuz.nextLine();
        if (input.isEmpty()) {
            System.out.println("Pomilka v zapiti, spobuyte she raz.");
            return true;
        }
        
        char choice = input.charAt(0);
        if (choice == '1') {
            return false;
        } else if (choice == '2') {
            System.out.println("Deltuyemo v golovne menu.");
            return true;
        } else {
            System.out.println("Pomilka v zapiti, spobuyte she raz.");
            return true;
        }
    }
    
    private static void saveConfiguration() {
        try {
            Path path = Paths.get(CONFIG_FILE);
            String content = "boardSize=" + razmerDoski + "\n" +
                             "player1=" + player1Name + "\n" +
                             "player2=" + player2Name + "\n";
            Files.writeString(path, content);
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }
    
    private static void loadConfiguration() {
        Path path = Paths.get(CONFIG_FILE);
        if (!Files.exists(path)) {
            System.out.println("No configuration file found. Using default settings.");
            return;
        }
        
        try {
            BufferedReader reader = Files.newBufferedReader(path);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    
                    if (key.equals("boardSize")) {
                        razmerDoski = Integer.parseInt(value);
                    } else if (key.equals("player1")) {
                        player1Name = value;
                    } else if (key.equals("player2")) {
                        player2Name = value;
                    }
                }
            }
            reader.close();
            System.out.println("Configuration loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading configuration: " + e.getMessage());
        }
    }
    
    private static void saveGameStatistics(String winner) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            
            Path path = Paths.get(STATS_FILE);
            String content = "Date: " + timestamp + "\n" +
                            "Board Size: " + razmerDoski + "x" + razmerDoski + "\n" +
                            "Player 1: " + player1Name + " (X)\n" +
                            "Player 2: " + player2Name + " (O)\n" +
                            "Winner: " + winner + "\n" +
                            "------------------------\n";
            
            if (!Files.exists(path)) {
                Files.writeString(path, content);
            } else {
                Files.writeString(path, content, StandardOpenOption.APPEND);
            }
            
            System.out.println("Game statistics saved.");
        } catch (IOException e) {
            System.out.println("Error saving game statistics: " + e.getMessage());
        }
    }
    
    private static void showStatistics() {
        Path path = Paths.get(STATS_FILE);
        if (!Files.exists(path)) {
            System.out.println("No statistics file found. Play some games first!");
            return;
        }
        
        System.out.println("\n===Game Statistics===");
        try {
            String content = Files.readString(path);
            System.out.println(content);
            
            System.out.println("\nPress Enter to continue...");
            arbuz.nextLine();
        } catch (IOException e) {
            System.out.println("Error reading statistics: " + e.getMessage());
        }
    }
}
