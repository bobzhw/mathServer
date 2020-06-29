package test;

import com.tsinghuabigdata.common.database.DBManager;
import net.didion.jwnl.data.Exc;
import webTask.DataBase;

import javax.xml.crypto.Data;

public class Question {
    private String id;
    private String stem;
    private String subStem;
    private int questionType;
    private int questionNum;
    private String year;
    private String area;

    public Question(String id, String stem, String subStem, int questionType, int questionNum, String year, String area) {
        this.id = id;
        this.stem = stem;
        this.subStem = subStem;
        this.questionType = questionType;
        this.questionNum = questionNum;
        this.year = year;
        this.area = area;
    }
    public void insertQuestion(DataBase dataBase){
        try{
            String sql = "insert into questions_copy(id,stem,subStem,questionType,questionNum,year,area)" +
                    "values('"+id+"','"+stem+"','"+subStem+"',"+questionType+","+questionNum+",'"+year+"','"+area+"');";
            dataBase.execute(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
