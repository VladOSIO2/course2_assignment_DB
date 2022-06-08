package gui.chooseQuiz;

import javafx.util.Pair;

public class SelectedQuiz {
    private static Pair<Integer, String> selectedQuiz;
    static {
        selectedQuiz = new Pair<>(-1, "");
    }

    public static void set(Pair<Integer, String> selectedQuiz) {
        SelectedQuiz.selectedQuiz = selectedQuiz;
    }

    public static Pair<Integer, String> get() {
        Pair<Integer, String> q = selectedQuiz;
        selectedQuiz = new Pair<>(-1, "");
        return q;
    }
}
