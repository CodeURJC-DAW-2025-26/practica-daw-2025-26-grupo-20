package es.codeurjc.mokaf.api.interfaces;

public interface FaqDTO {
    Long getId();
    void setId(Long id);
    String getQuestion();
    void setQuestion(String question);
    String getAnswer();
    void setAnswer(String answer);
}
