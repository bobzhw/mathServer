package webTask;

import com.tsinghuabigdata.edu.mathengine.inference.database.QuestionHandler;
import com.tsinghuabigdata.edu.mathengine.inference.database.dao.Questions;
import com.tsinghuabigdata.edu.mathengine.inference.solve.SolveResult;
import com.tsinghuabigdata.edu.mathengine.inference.solve.Solver;
import com.tsinghuabigdata.edu.mathengine.nlu.mathnormalize.MathematicsNormalizeUtil;
import edu.uestc.auto.reasoning.NlpString2;
import edu.uestc.auto.reasoning.PostParam;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import static java.lang.System.out;

public class Task implements Runnable{
    private static final Semaphore semaphore = new Semaphore(1);
    private String taskId;
    private String questionId;
    private static MathMachine mathMachine = MathMachine.getInstance();
    private Solver solver = new Solver();
    private static DataBase dataBase = new DataBase("question_database");
    @Override
    public void run(){
        try {
            out.println(semaphore.toString());
            semaphore.acquire();
            out.println(semaphore.toString());
            out.println("收到任务:"+taskId+",questionid:"+questionId);
            if (taskId.equals("latex")) {
                String sql = "select stem,subStem from questions where id = '" + questionId + "'";
                ResultSet rs = dataBase.query(sql);
                if (rs.next()) {
                    String stem = rs.getString("stem");
                    String subStem = rs.getString("subStem");
                    stem = MathematicsNormalizeUtil.normalizeLineString(stem);
                    subStem = MathematicsNormalizeUtil.normalizeLineString(subStem);
                    dataBase.execute("insert into t_request(question_id,stem,sub_stem,over,request) values('"
                            + questionId + "','" + replace(stem) + "','" + replace(subStem) + "',1,0)");
                }
            } else if (taskId.equals("nlp")) {
                Questions question = QuestionHandler.getQuestion(questionId);
                SolveResult sr = solver.nlp(question.convert());
                out.println(sr);
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                        + questionId + "',1,1,'" + replace(sr.toString()) + "')"
                );
            } else if (taskId.equals("solve")) {
                Questions question = QuestionHandler.getQuestion(questionId);
                out.println("开始solve:"+questionId);
                SolveResult sr = solver.solve(question.convert());
                out.println(sr);
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                        + questionId + "',1,2,'" + replace(sr.toString()) + "')"
                );
            } else if (taskId.equals("entity")) {
               String res = entity(questionId);
                dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                   +questionId+"',1,3,'"+replace(res)+"')"
              );
                //dataBase.execute("insert into t_request(question_id,over,request,solve_result) values(')"
                        //+ questionId + "',1,3,'')");
            }
            out.println("完成");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(taskId.equals("nlp")){
                try {
                    ResultSet rs = dataBase.query("select * from t_request where question_id='"+questionId+"' and over=1 and request=1");
                    if(!rs.next())
                        dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                            + questionId + "',1,1,'" + replace("未解出") + "')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(taskId.equals("solve")){
                try {
                    ResultSet rs = dataBase.query("select * from t_request where question_id='"+questionId+"' and over=1 and request=2");
                    if(!rs.next())
                        dataBase.execute("insert into t_request(question_id,over,request,solve_result) values('"
                            + questionId + "',1,2,'" + replace("未解出") + "')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            semaphore.release();
        }
    }

    public Task( String taskId,String questionId){
        this.taskId=taskId;
        this.questionId=questionId;
    }

    private static String replace(String line){
        return line.replaceAll("\\\\","\\\\\\\\")
                .replaceAll("'","\\\\'")
                .replaceAll("\"","\\\\\"");
    }
    public static String entity(String questionId) throws Exception{

        ResultSet rs = dataBase.query("select stem2,sub_stem2,options from t_question where id = '"+questionId+"';");
        if(rs.next()){
            String stem2 = rs.getString("stem2");
            String subStem2 = rs.getString("sub_stem2");
            String option = rs.getString("options");
            List<String> options = null;
            List<String> subStems2 = null;
            if(option!=null && !option.isEmpty()){
                options = Arrays.asList(option.split("#%#"));
            }
            if(subStem2!=null && !subStem2.isEmpty()
            ){
                subStems2=Arrays.asList(subStem2.split("#%#"));
            }
            if(stem2==null && subStem2==null && option==null){
                return "";
            }
            PostParam postParam = null;
            if(stem2!=null && !stem2.isEmpty()){
                if(option!=null && !option.isEmpty()){
                    postParam = new PostParam(new PostParam.Content("1", stem2, subStems2, options));
                }
                else{
                    postParam = new PostParam(new PostParam.Content("3",stem2,subStems2, options));
                }
            }
            postParam.setChinese_type("1");
            out.println(postParam.getText_json().toString());
            NlpString2 nlpString = new NlpString2();
            out.println(nlpString.getNlpJson(postParam));
            return  nlpString.getNlpJson(postParam);
        }
        return "";
    }

//    public static void main(String[] args) {
//        Questions question = QuestionHandler.getQuestion("00AEE90A226B45918E33A26DCAED89E2");
//        SolveResult sr = solver.nlp(question.convert());
//        out.println(sr);
//    }
}
