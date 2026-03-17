package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.Faq;

public class FaqDTO implements es.codeurjc.mokaf.api.interfaces.FaqDTO {
    private Long id;
    private String question;
    private String answer;

    public FaqDTO() {
    }

    public FaqDTO(Faq faq) {
        this.id = faq.getId();
        this.question = faq.getQuestion();
        this.answer = faq.getAnswer();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
