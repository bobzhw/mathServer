package test;

import webTask.DataBase;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetSolveResult {
    public static void main(String[] args) throws Exception{
        String key = "BJ2018%w";
        List<SolveResult> answers = getTaoTiSolveResult(key);
        for(SolveResult solveResult : answers){
            System.out.println(solveResult);
        }
    }

    public static List<SolveResult>getTaoTiSolveResult(String key)throws Exception{
        DataBase dataBase = new DataBase("autosolve_test");
        ResultSet rs = dataBase.query("select questionID,result from results where questionID like '"+key+"'");
        List<SolveResult> answerList = new ArrayList<>();
        while(rs.next()){
            SolveResult solveResult = new SolveResult(rs.getString("questionID"),rs.getString("result"));
            answerList.add(solveResult);
        }
        return answerList;
    }
}

class SolveResult{
    public String questionId;
    public String context;
    public SolveResult(String questionId,String context){
        this.questionId= questionId;
        this.context=context;
    }

    @Override
    public String toString() {
        return "SolveResult{" +
                "questionId='" + questionId + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}