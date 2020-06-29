package test;

import net.didion.jwnl.data.Exc;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.zookeeper.server.DataNode;
import webTask.DataBase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static test.ExcelControl.createExcelXls;

public class getAnswer {
    public static void main(String[] args) throws Exception {
        String key = "BJ2018%W";
        List<Answer> answers = getTaoTiAnswer(key);
        String fileDir = "北京2018文科.xls";
        List<String> sheetName = new ArrayList<>();

        sheetName.add("北京2018文科");

        System.out.println(sheetName);

        String[] title = {"questionID","divideId","answer","score"};
        createExcelXls(fileDir, sheetName, title);
        List<Map<String,String>> userList = new ArrayList<Map<String,String>>();
        for(Answer answer : answers){
            Map<String,String> map=new HashMap<String,String>();
            boolean flag = false;
            for(Map<String,String> maps : userList){
                if(maps.get("questionID").equals(answer.questionId)){
                    maps.put("divideId",maps.get("divideId")+" "+answer.divideId);
                    maps.put("answer", maps.get("answer")+" "+answer.context);
                    flag = true;
                    break;
                }
            }
            if(!flag){
                map.put("questionID", answer.questionId);
                map.put("divideId",answer.divideId);
                map.put("answer", answer.context);
                map.put("score", "0");
                userList.add(map);
            }

        }

        createExcelXls(fileDir, sheetName, title);
        try {
            ExcelControl.writeToExcelXls(fileDir, sheetName.get(0), userList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static List<Answer> getTaoTiAnswer(String like) throws Exception{
        DataBase dataBase = new DataBase("autosolve_test");
        ResultSet rs = dataBase.query("select context,questionId,divideId from stdanswer where questionId like '"+like+"'");
        List<Answer> answerList = new ArrayList<>();
        while(rs.next()){
            Answer answer = new Answer(rs.getString("context"),rs.getString("questionId"),rs.getString("divideId"));
            answerList.add(answer);
        }
        return answerList;
    }
}
