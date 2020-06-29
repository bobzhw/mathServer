import com.tsinghuabigdata.edu.mathengine.data.common.QuestionType;
import com.tsinghuabigdata.edu.mathengine.inference.solve.SolveResult;
import com.tsinghuabigdata.edu.mathengine.inference.solve.Solver;
import webTask.TextReader;
import webTask.TextWriter;

import java.util.ArrayList;
import java.util.List;

public class testQuestion {

    public static void func()
    {
        TextReader reader = new TextReader("C:\\Users\\10733\\IdeaProjects\\mathServer\\src\\main\\resources\\input_3");
        TextWriter writer = new TextWriter("C:\\Users\\10733\\IdeaProjects\\mathServer\\src\\main\\resources\\output4",true);
        Solver solver = new Solver();
        int count =0;
        for(String line : reader.ReadLines()) {
            count++;
            String stem = line;
            SolveResult solveResult = solver.solve(stem, "", "", QuestionType.SOLUTION);
            writer.write(count+":"+solveResult.toString());
        }
        writer.close();
    }
    public static void main(String[] args) {
        func();
//        TextReader question = new TextReader("C:\\Users\\10733\\IdeaProjects\\mathServer\\src\\main\\resources\\input_2");
//        TextReader answer = new TextReader("C:\\Users\\10733\\IdeaProjects\\mathServer\\src\\main\\resources\\output2");
//        TextWriter writer = new TextWriter("C:\\Users\\10733\\IdeaProjects\\mathServer\\src\\main\\resources\\总输出",true);
//        List<String> questions = new ArrayList<>();
//        for(String line : question.ReadLines())
//        {
//            if(!line.equals(""))
//                questions.add(line.trim());
//        }
//        List<String> answers = new ArrayList<>();
//        int index=0;
//        for(String line : answer.ReadLines())
//        {
//            if(line.equals(""))
//                continue;
//            if(line.contains("#%#%#"))
//            {
//                if(!answers.isEmpty())
//                {
//                    String d = "第"+(index+35)+"道题目:\n";
//                    writer.writeLines(d+questions.get(index++));
//                    for(String s : answers)
//                    {
//                        writer.writeLines(s);
//                    }
//                }
//                answers.clear();
//                answers.add(line.replace("#%#%#","").trim());
//            }
//            else
//            {
//                answers.add(line.trim());
//            }
//        }
//        writer.close();
//        question.Close();
//        answer.Close();

    }
}
