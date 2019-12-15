public class MathMachine {
    private static MathMachine instance = new MathMachine();
    private MathMachine(){

    }

    public static MathMachine getInstance(){
        return instance;
    }
    public String latexTask(String questionid){
        return"";
    }
    public String nlpTask(String questionid){
        return"";
    }
    public String solveTask(String questionid){
        return"";
    }
}
