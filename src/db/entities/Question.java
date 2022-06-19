package db.entities;

import java.util.List;

public class Question {
    private int questionID;
    private final int authorID;
    private final int themeID;
    private final int gradeID;
    private String text;
    private final List<String> answers;
    private final String rightAnswer;

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

    public int getThemeID() {
        return themeID;
    }

    public int getGradeID() {
        return gradeID;
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

    public String getRightAnswer() {
        return rightAnswer;
    }

}
