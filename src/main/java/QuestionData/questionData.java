package QuestionData;

import test.TestQuestions;
import webTask.DataBase;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
/*
* +--------------+--------------+------+-----+---------+-------+
| Field        | Type         | Null | Key | Default | Extra |
+--------------+--------------+------+-----+---------+-------+
| id           | varchar(255) | NO   | PRI | NULL    |       |
| stem         | text         | YES  |     | NULL    |       |
| subStem      | text         | YES  |     | NULL    |       |
| config       | text         | YES  |     | NULL    |       |
| tableInfo    | text         | YES  |     | NULL    |       |
| year         | varchar(10)  | YES  |     | NULL    |       |
| area         | varchar(10)  | YES  |     | NULL    |       |
| type         | varchar(10)  | YES  |     | NULL    |       |
| questionNum  | int(2)       | YES  |     | NULL    |       |
| questionType | int(2)       | YES  |     | 0       |       |
| solved       | bit(1)       | YES  |     | b'0'    |       |
| templateJson | text         | YES  |     | NULL    |       |
| category     | tinyint(3)   | YES  |     | NULL    |       |
+--------------+--------------+------+-----+---------+-------+
* */
/*
*  Field         | Type         | Null | Key | Default | Extra |
+---------------+--------------+------+-----+---------+-------+
| questionid    | varchar(255) | NO   | PRI | NULL    |       |
| stem          | text         | YES  |     | NULL    |       |
| sub_stem      | text         | YES  |     | NULL    |       |
| config        | text         | YES  |     | NULL    |       |
| table_info    | text         | YES  |     | NULL    |       |
| year          | varchar(255) | YES  |     | NULL    |       |
| area          | varchar(255) | YES  |     | NULL    |       |
| type          | varchar(255) | YES  |     | NULL    |       |
| question_type | varchar(255) | YES  |     | NULL    |       |
| options       | varchar(255) | YES  |     | NULL    |       |
| stem2         | text         | YES  |     | NULL    |       |
| sub_stem2     | text         | YES  |     | NULL    |       |
| category      | int(11)      | NO   |     | NULL    |       |
| field         | varchar(255) | YES  |     | NULL    |       |
+---------------+--------------+------+-----+---------+-------+
* */
public class questionData {
    private static String[] areas={"初中","101","yqzy","doudou","MT"};
    public static void questions() throws Exception{
        DataBase dataBase = new DataBase("863");
        DataBase question_database = new DataBase("question_database");
        List<String> arealist = Arrays.asList(areas);
        ResultSet rs = dataBase.query("select * from questions");
        int count=0;
        while (rs.next()){
            try{
                count++;
                String area = rs.getString("area");
                if(area!=null && arealist.contains(area)){
                    String sql ="insert into shs_question(questionid,stem,subStem,category,config,table_info,type,questionType,field) values('"
                            +TestQuestions.replace(rs.getString("id"))+"','"+TestQuestions.replace(rs.getString("stem"))+"','"
                            +TestQuestions.replace(rs.getString("subStem"))+"',"+rs.getInt("category")+",'"
                            +TestQuestions.replace(rs.getString("config"))+"','"
                            +TestQuestions.replace(rs.getString("tableInfo"))+"','"
                            +TestQuestions.replace(rs.getString("type"))+"','"+TestQuestions.replace(rs.getString("questionType"))+"','初中')";
                    question_database.execute((sql));
                    System.out.println(count);
                }
                else{
                    String sql = "insert into shs_question(questionid,stem,subStem,category,config,table_info,year,area,type,questionType,field) values('"
                            +TestQuestions.replace(rs.getString("id"))+"','"+TestQuestions.replace(rs.getString("stem"))+"','"
                            +TestQuestions.replace(rs.getString("subStem"))+"',"+rs.getInt("category")+",'"
                            +TestQuestions.replace(rs.getString("config"))+"','"+TestQuestions.replace(rs.getString("tableInfo"))+"','"
                            +TestQuestions.replace(rs.getString("year"))+"','"+TestQuestions.replace(rs.getString("area"))+"','"
                            +TestQuestions.replace(rs.getString("type"))+"','"+TestQuestions.replace(rs.getString("questionType"))+"','高中')";
                    question_database.execute(sql);
                    System.out.println(count);
                }
            }
            catch (Exception e){
//                e.printStackTrace();
                continue;
            }

        }

    }

    public static void question() throws Exception{
        DataBase dataBase = new DataBase("autosolve_test");
        DataBase question_database = new DataBase("question_database");
        ResultSet rs = dataBase.query("select * from question");
        int count=0;
        while(rs.next()) {
            try {
                count++;
                String sql = "insert into shs_question(questionid,stem,subStem,category,config,table_info,type,questionType,field) values('"
                        + TestQuestions.replace(rs.getString("questionId")) + "','" + TestQuestions.replace(rs.getString("stem")) + "','"
                        + TestQuestions.replace(rs.getString("subStem")) + "',0,'"
                        + TestQuestions.replace(rs.getString("geoInfo")) + "','','";
                if (rs.getString("algOrgeo") == null) {
                    sql = sql + "','";

                } else if (rs.getString("algOrgeo").equals("alg")) {
                    sql = sql + "代数" + "','";
                } else if (rs.getString("algOrgeo").equals("geo")) {
                    sql = sql + "几何" + "','";
                } else if (rs.getString("algOrgeo").equals("algGeo")) {
                    sql = sql + "代数几何" + "','";
                }
                if (rs.getString("questionType") == null) {
                    sql = sql + "2','";
                } else if (rs.getString("questionType").equals("choice")) {
                    sql = sql + "0','";
                } else if (rs.getString("questionType").equals("fill_in")) {
                    sql = sql + "1','";
                } else {
                    sql = sql + "2','";
                }
                sql = sql+"初中')";
                question_database.execute(sql);
                System.out.println(count);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public static void shs2questions() throws Exception{
        DataBase dataBase = new DataBase("question_database");
        ResultSet rs = dataBase.query("select * from shs_question");
        int count=0;
        while(rs.next()){
            try{
                String id = rs.getString("questionid");
                String stem = rs.getString("stem");
                String subStem = rs.getString("sub_stem");
                String config = rs.getString("config");
                String tableInfo = rs.getString("table_info");
                String year = rs.getString("year");
                String area = rs.getString("area");
                String type = rs.getString("type");
                String questionType = rs.getString("question_type");
                int category = rs.getInt("category");
                count++;
                String sql = "insert into questions(id,stem,subStem,questionNum,config,tableInfo,year,area,type,questionType,category)" +
                        "values('"+id+"','"+TestQuestions.replace(stem)+"','"+TestQuestions.replace(subStem)+"',0,'"+
                        TestQuestions.replace(config)+"','"+TestQuestions.replace(tableInfo)+"',";
                if(year!=null){
                    sql+="'"+year+"',";
                }else{
                    sql+="NULL,";
                }
                if(area!=null){
                    sql+="'"+area+"',";
                }
                else{
                    sql+="NULL,";
                }
                if(type!=null){
                    sql+="'"+type+"',";
                }
                else{
                    sql+="NULL,";
                }
                sql = sql + questionType+","+category+")";
                System.out.println(count);
                dataBase.execute(sql);
            }
            catch (Exception e){
                continue;
            }
        }
    }

    public static void main(String[] args) throws Exception{
        shs2questions();
    }
}
