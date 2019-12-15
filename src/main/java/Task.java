
public class Task implements Runnable{
    private String taskId;
    private String questionId;
    private static MathMachine mathMachine = MathMachine.getInstance();
    @Override
    public void run() {
        if(taskId.equals("latex")){

        }
        if(taskId.equals("nlp")){

        }
        if(taskId.equals("taskId")){

        }
    }

    public Task(String taskId,String questionId){
        this.taskId=taskId;
        this.questionId=questionId;
    }
}
