package es.codeurjc.mokaf.api.interfaces;

public interface FaqInterface {
    Long getId();
    void setId(Long id);
    String getQuestion();
    void setQuestion(String question);
    String getAnswer();
    void setAnswer(String answer);
}
