package test;

public class Answer{
    public String questionId;
    public String divideId;
    public String context;

    public Answer(String context,String questionId,String divideId){
        this.context=context;
        this.questionId=questionId;
        this.divideId=divideId;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "questionId='" + questionId + '\'' +
                ", divideId='" + divideId + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
