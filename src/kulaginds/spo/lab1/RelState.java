package kulaginds.spo.lab1;

/**
 * Copyright (c) Dmitry Kulagin <kulaginds@gmail.com>
 */
public class RelState {

    protected int index = Integer.MIN_VALUE;
    protected int lexeme = Integer.MIN_VALUE;

    public RelState(int index) {
        if (index != -1) { // нормальное состояние без лексемы
            this.index = index;
            this.lexeme = Integer.MIN_VALUE;
        } // иначе ошибочное состояние
    }

    public RelState(int index, int lexeme) { // состояние с лексемой
        this.index = index;
        this.lexeme = lexeme;
    }

    public RelState() { // состояние конечное
        index = Integer.MAX_VALUE;
    }

    public int getState() {
        return index;
    }

    public int getLexeme() { return lexeme; }

    public boolean isError() { return ((index == lexeme) && (index == Integer.MIN_VALUE)); }

    public boolean isEnd() {
        return (index == Integer.MAX_VALUE);
    }

    public boolean hasLexeme() { return (lexeme > Integer.MIN_VALUE); }
}
