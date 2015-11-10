package kulaginds.spo.lab1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) Dmitry Kulagin <kulaginds@gmail.com>
 */

class M { // сообщения
    static String HELLO = "Программа \"Лексический анализатор\"";
    static String NO_CONFIG = "Нет конфигурационного файла!";
    static String FILE_NOT_FOUND = "Файл не найден!";
    static String IO_ERROR = "Произошла ошибка ввода/вывода: %s\n";
    static String LOADING_CONFIG = "Загрузка конфигурационного файла.";
    static String CONFIG_LOADED = "Конфигурационный файл загружен.";
    static String PARSE_TABLE = "Происходит разбор ТПиВ.";
    static String UNKNOWN_STATE = "Неизвестное состояние! %s: %s\n";
    static String TABLE_PARSED = "ТПиВ разобрана.";
    static String GET_ANALYSE_STR = "Введите строку для анализа:";
    static String STR_ANALYSE = "Анализируем строку:\n";
    static String UNKNOWN_CHAR = "Введен неизвестный символ!";
    static String STR_END = "\nКонец строки";
    static String ERROR_STATE = "%s\nОшибочное состояние\n";
    static String LEX_STATE = "%s L%s\n";
    static String UNKNOWN_END = "\nНепредвиденный конец строки";
}

public class LexicalAnalyser {

    protected Map<Character, RelRow> table;

    public static void main(String[] args) {
        new LexicalAnalyser(args);
    }

    public LexicalAnalyser(String[] args) {
        System.out.println(M.HELLO);

        if (args == null || args.length == 0) {
            System.err.println(M.NO_CONFIG);
            System.exit(-1);
        }

        try {
            loadTable(args[0]);
            readAndAnalyse();
        } catch (FileNotFoundException e) {
            System.err.println(M.FILE_NOT_FOUND);
            System.exit(-1);
        } catch (IOException e) {
            System.err.printf(M.IO_ERROR, e.getMessage());
            System.exit(-1);
        }
    }

    protected void loadTable(String url) throws IOException {
        System.out.println(M.LOADING_CONFIG);
        BufferedReader br = new BufferedReader(new FileReader(url));
        System.out.println(M.CONFIG_LOADED);
        System.out.println(M.PARSE_TABLE);

        String line, cell;
        String[] row, cells, parts;
        RelRow r;
        boolean loaded_line_count = false;
        int cols_count = 0, state, lexeme;
        char c;

        table = new HashMap<>();

        while ((line = br.readLine()) != null) {
            if (line.length() == 0)
                continue;
            if (!loaded_line_count) {
                cols_count = Integer.parseInt(line) + 1;
                loaded_line_count = true;
                continue;
            }
            row = line.split(":");
            c = row[0].charAt(0);
            cells = row[1].split("\\|");
            r = new RelRow(cols_count);
            Pattern state_p = Pattern.compile("S[0-9]");
            Matcher state_m;

            for (int i = 0; i < cols_count; i++) {
                cell = cells[i];
                if (!cell.contains(",")) {
                    // переход в состояние
                    state_m = state_p.matcher(cell);
                    if (state_m.matches()) {
                        state = Integer.parseInt(cell.substring(1));
                        if (state <= 0) {
                            System.err.printf(M.UNKNOWN_STATE, row[1], cell);
                            System.exit(-1);
                        }
                        r.addElement(i, new RelState(state));
                    } else {
                        switch (cell) {
                            case "K":
                                r.addElement(i, new RelState());
                                break;
                            case "E":
                                r.addElement(i, new RelState(-1));
                                break;
                            case "H":
                                r.addElement(i, new RelState(0));
                                break;
                            default:
                                System.err.printf(M.UNKNOWN_STATE, row[1], cell);
                                System.exit(-1);
                                break;
                        }
                    }
                } else {
                    // определение лексемы, переход в начало
                    parts = cell.split(",");
                    if(parts.length != 2) {
                        System.err.printf(M.UNKNOWN_STATE, row[1], cell);
                        System.exit(-1);
                    }
                    state = (parts[0].length() != 1)?Integer.parseInt(parts[0].substring(1)):0;
                    lexeme = Integer.parseInt(parts[1].substring(1));
                    r.addElement(i, new RelState(state, lexeme));
                }
            }
            table.put(c, r);
        }
        br.close();
        System.out.println(M.TABLE_PARSED);
    }

    protected void readAndAnalyse() throws IOException {
        System.out.println(M.GET_ANALYSE_STR);
        Scanner sc = new Scanner(System.in);
        String input = sc.next();
        sc.close();
        analyze(input);
    }

    protected void analyze(String input) {
        char c;
        int state = 0;
        RelRow r;
        RelState cell;

        System.out.println(M.STR_ANALYSE);

        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            r = table.get(c);
            if (r == null) {
                System.err.println(M.UNKNOWN_CHAR);
                System.exit(-1);
            }
            cell = r.getElement(state);
            if (cell.isEnd()) {
                System.out.println(M.STR_END);
                return;
            } else if (cell.isError()) {
                System.out.printf(M.ERROR_STATE, c);
                return;
            } else if (cell.hasLexeme()) {
                System.out.printf(M.LEX_STATE, c, cell.getLexeme());
            } else
                System.out.print(c);
            state = cell.getState();
        }
        System.err.println(M.UNKNOWN_END);
    }
}
