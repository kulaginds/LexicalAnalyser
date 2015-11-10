package kulaginds.spo.lab1;

/**
 * Copyright (c) Dmitry Kulagin <kulaginds@gmail.com>
 */
public class RelRow {

    protected RelState[] elements;

    public RelRow(int states_count) {
        elements = new RelState[states_count];
    }

    public void addElement(int index, RelState element) {
        elements[index] = element;
    }

    public RelState getElement(int index) {
        return elements[index];
    }
}
