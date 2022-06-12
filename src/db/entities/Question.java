package db.entities;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class Question {
    private int questionID;
    private int authorID;
    private int themeID;
    private int gradeID;
    private String text;
    private List<String> answers;
    private String rightAnswer;

    public Question(
            int questionID, int authorID, int themeID, int gradeID,
            String text, List<String> answers, String rightAnswer) {
        this.questionID = questionID;
        this.authorID = authorID;
        this.themeID = themeID;
        this.gradeID = gradeID;
        this.text = text;
        this.answers = answers;
        this.rightAnswer = rightAnswer;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getThemeID() {
        return themeID;
    }

    public void setThemeID(int themeID) {
        this.themeID = themeID;
    }

    public int getGradeID() {
        return gradeID;
    }

    public void setGradeID(int gradeID) {
        this.gradeID = gradeID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }
}
