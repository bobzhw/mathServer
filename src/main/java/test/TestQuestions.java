package test;

import opennlp.tools.parser.Cons;
import test.MessageQueue;
import webTask.DataBase;

import java.sql.ResultSet;
public class TestQuestions {
    public static void main(String[] args) {
//        String key = "sx2013%W";
        MessageQueue<String> queue = new MessageQueue<>();
//        queryTaoTi(key,queue);
//        queue.push("bj201801w");
//        queue.push("bj201802w");
//        queue.push("bj201803w");
//        queue.push("bj201804w");
//        queue.push("bj201805w");
//        queue.push("bj201806w");
//        queue.push("bj201807w");
//        queue.push("bj201808w");
//        queue.push("bj201809w");
//        queue.push("bj201810w");
//        queue.push("bj201811w");
//        queue.push("bj201812w");
//        queue.push("bj201813w");
//        queue.push("bj201814w");
//        queue.push("bj201815w");
//        queue.push("bj201816w");
//        queue.push("bj201817w");
//        queue.push("bj201818w");
//        queue.push("bj201819w");
        queue.push("bj201820w");
//        queue.push("qg2201801w");
//        queue.push("qg2201802w");
//        queue.push("qg2201803w");
//        queue.push("qg2201804w");
//        queue.push("qg2201805w");
//        queue.push("qg2201806w");
//        queue.push("qg2201807w");
//        queue.push("qg2201808w");
//        queue.push("qg2201809w");
//        queue.push("qg2201810w");
//        queue.push("qg2201811w");
//        queue.push("qg2201812w");
//        queue.push("qg2201813w");
//        queue.push("qg2201814w");
//        queue.push("qg2201815w");
//        queue.push("qg2201816w");
//        queue.push("qg2201817w");
//        queue.push("qg2201818w");
//        queue.push("qg2201819w");
//        queue.push("qg2201820w");
//        queue.push("qg2201821w");
//        queue.push("qg2201822w");
//        queue.push("qg2201823w");



//
        Consumer consumer = new Consumer(queue);
        consumer.run();
    }
    public static void queryTaoTi(String like,MessageQueue<String> queue){
        try{
            DataBase dataBase = new DataBase("autosolve_test");
            ResultSet rs = dataBase.query("select id from questions where id like '"+like+"'");
            while(rs.next()){
                String id = rs.getString("id");
                queue.push(id);
                System.out.println("生产任务："+id);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static String replace(String line){
        if(line==null)
            return null;
        return line.replaceAll("\\\\","\\\\\\\\")
                .replaceAll("'","\\\\'")
                .replaceAll("\"","\\\\\"");
    }
}
