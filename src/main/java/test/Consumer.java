package test;

import com.tsinghuabigdata.edu.mathengine.inference.database.QuestionHandler;
import com.tsinghuabigdata.edu.mathengine.inference.solve.SolveResult;
import com.tsinghuabigdata.edu.mathengine.inference.solve.Solver;
import webTask.DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Consumer implements Runnable{
    private MessageQueue<String> queue;
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 2,
            3000, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    public Consumer(MessageQueue<String> queue){
        this.queue=queue;
    }

    @Override
    public void run() {
        for(;;){
            if(!queue.empty()){
                String id = queue.peek();
                System.out.println("消费任务："+id);
                queue.pop();
                executorService.execute(new solveTask(id));
            }
        }
    }
}

class solveTask implements Runnable{
    private static Solver solver= new Solver();
    private static DataBase dataBase = new DataBase("autosolve_test");
    private String id;

    public solveTask(String id){
        this.id = id;
    }

    @Override
    public void run() {
        try{
            System.out.println("开始测试题目:"+id);
            SolveResult result = solver.solve(QuestionHandler.getQuestion(id).convert());
            Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
            dataBase.execute("insert into results(questionID,IP,result) values('"
                    +id+"','"+sd+"','"+TestQuestions.replace(result.toString()) + "')");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
