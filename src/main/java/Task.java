import com.tsinghuabigdata.edu.mathengine.data.common.Question;
import com.tsinghuabigdata.edu.mathengine.inference.database.QuestionHandler;
import com.tsinghuabigdata.edu.mathengine.inference.database.dao.Questions;
import com.tsinghuabigdata.edu.mathengine.inference.solve.SolveResult;
import com.tsinghuabigdata.edu.mathengine.inference.solve.Solver;
import com.tsinghuabigdata.edu.mathengine.nlu.mathnormalize.MathematicsNormalizeUtil;
import pri.tsinghuabigdata.edu.mathengine.wordproblem.segmentengine.process.Methods.MathematicsNormalizeMethod;

import java.sql.ResultSet;

public class Task implements Runnable{
    private String taskId;
    private String questionId;
    private static MathMachine mathMachine = MathMachine.getInstance();
    private static Solver solver = new Solver();
    private static DataBase dataBase = DataBase.getInstance();
    @Override
    public void run(){
        try {
            if(taskId.equals("latex")){
                String sql = "select stem,subStem from questions where id = '"+questionId+"'";
                ResultSet rs = dataBase.query(sql);
                if(rs.next()){
                    String stem = rs.getString("stem");
                    String subStem = rs.getString("subStem");
                    stem = MathematicsNormalizeUtil.normalizeLineString(stem);
                    subStem = MathematicsNormalizeUtil.normalizeLineString(subStem);
                    dataBase.execute("insert into t_request(question_id,stem,sub_stem,over,request) values('"
                            +questionId+"','"+stem+"','"+subStem+"',1,0)");
                }
            }
            else if(taskId.equals("nlp")){
                Questions question = QuestionHandler.getQuestion(questionId);
                SolveResult sr = solver.nlp(question.convert());
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                    +questionId+"',1,1,'"+sr.toString()+"')"
                );
            }
            else if(taskId.equals("solve")){
                Questions question = QuestionHandler.getQuestion(questionId);
                SolveResult sr = solver.solve(question.convert());
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                        +questionId+"',1,2,'"+sr.toString()+"')"
                );
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public Task(String taskId,String questionId){
        this.taskId=taskId;
        this.questionId=questionId;
    }
}
