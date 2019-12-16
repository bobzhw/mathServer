import com.google.common.collect.Lists;
import com.tsinghuabigdata.edu.mathengine.data.common.Question;
import com.tsinghuabigdata.edu.mathengine.inference.database.QuestionHandler;
import com.tsinghuabigdata.edu.mathengine.inference.database.dao.Questions;
import com.tsinghuabigdata.edu.mathengine.inference.solve.SolveResult;
import com.tsinghuabigdata.edu.mathengine.inference.solve.Solver;
import com.tsinghuabigdata.edu.mathengine.nlu.mathnormalize.MathematicsNormalizeUtil;
import edu.uestc.auto.reasoning.NlpString2;
import edu.uestc.auto.reasoning.PostParam;
import pri.tsinghuabigdata.edu.mathengine.wordproblem.segmentengine.process.Methods.MathematicsNormalizeMethod;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

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
                            +questionId+"','"+replace(stem)+"','"+replace(subStem)+"',1,0)");
                }
            }
            else if(taskId.equals("nlp")){
                Questions question = QuestionHandler.getQuestion(questionId);
                SolveResult sr = solver.nlp(question.convert());
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                    +questionId+"',1,1,'"+replace(sr.toString())+"')"
                );
            }
            else if(taskId.equals("solve")){
                Questions question = QuestionHandler.getQuestion(questionId);
                SolveResult sr = solver.solve(question.convert());
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                        +questionId+"',1,2,'"+replace(sr.toString())+"')"
                );
            }
            else if(taskId.equals("entity")){
                String res = entity(questionId);
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                    +questionId+"',1,3,'"+replace(res)+"')"
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

    private static String replace(String line){
        return line.replaceAll("\\\\","\\\\\\\\")
                .replaceAll("'","\\\\'")
                .replaceAll("\"","\\\\\"");
    }
    public static String entity(String questionId){

        Questions question = QuestionHandler.getQuestion(questionId);

        String stem = question.getStem();
        String subStem = question.getSubStem();
        String options[] = subStem.split("#%#");
        PostParam postParam = null;
        if(options.length==4){
            postParam = new PostParam(new PostParam.Content("1", stem, null, Arrays.asList(options)));
        }
        else if(options.length==0){
            postParam = new PostParam(new PostParam.Content("3",stem,null,null));
        }
        else{
             postParam = new PostParam(new PostParam.Content("2",stem,Arrays.asList(options),null));
        }
        postParam.setChinese_type("1");
        out.println(postParam.getText_json().toString());
        NlpString2 nlpString = new NlpString2();
        out.println(nlpString.getNlpJson(postParam));
        return  nlpString.getNlpJson(postParam);
    }

    public static void main(String[] args) {
        entity("bj201702w");
    }
}
