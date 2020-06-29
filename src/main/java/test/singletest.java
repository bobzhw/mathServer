package test;

import com.tsinghuabigdata.edu.mathengine.inference.database.QuestionHandler;
import com.tsinghuabigdata.edu.mathengine.inference.solve.Solver;

public class singletest {
    public static void main (String[] args) {
        Solver solver = new Solver();
        solver.solve(QuestionHandler.getQuestion("test201720").convert());
    }
}
