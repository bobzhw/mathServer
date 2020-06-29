package test;

import com.tsinghuabigdata.common.database.DBManager;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
import webTask.DataBase;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.List;

public class TestXml {
    public static void main(String[] args) throws Exception{
        String filepath = "C:\\Users\\10733\\IdeaProjects\\mathServer\\src\\main\\resources\\2018-讯飞专家验收测试-文档\\2017高中数学试题（A卷）.xml";
        SAXReader reader = new SAXReader();
        DataBase dataBase = new DataBase("autosolve_test");
        File file = new File(filepath);
        Document document = reader.read(file);
        Element root = document.getRootElement();
        List<Element> childElements = root.elements();
        int questionNum = 0;
        for(int i = 0;i<3;i++) {
            List<Element> c2 = childElements.get(i).elements();
            //年份
            String year = "2020-4-6";
            //省份
            String area = "uestc";
            //选择题
            if (i == 0) {
                //题目类型。填空选择解答
                int questionType = 0;
                for (int j = 1; j <= c2.size(); j++) {
                    List<Element> questions = c2.get(j - 1).elements();
                    for (Element question : questions) {
                        //题号
                        String id = question.attribute(0).getValue();
                        //题目序号
                        questionNum++;

                        Element e = question.element("text");
                        List<Text> lst = e.content();
                        //正文
                        String stem = lst.get(0).getText();
                        System.out.println(stem);
                        Element e2 = question.element("select");
                        List<Element> lst2 = e2.elements();
                        StringBuffer subStem = new StringBuffer();
                        for (Element choice : lst2) {
                            lst = choice.content();
                            subStem.append(choice.attribute(0).getValue()).append("、");
                            subStem.append(lst.get(0).getText()).append("#%#");
                        }
                        subStem.delete(subStem.length() - 3, subStem.length());
                        System.out.println(subStem.toString());
                        Question q = new Question(id, stem, subStem.toString(), questionType, questionNum, year, area);
                        q.insertQuestion(dataBase);
                        System.out.println(1);
                    }
                }
            } else if (i == 1) {
                //题目类型。填空选择解答
                int questionType = 1;
                for (int j = 1; j <= c2.size(); j++) {
                    List<Element> questions = c2.get(j - 1).elements();
                    for (Element question : questions) {
                        //题号
                        String id = question.attribute(0).getValue();
                        //题目序号
                        questionNum++;
                        Element e = question.element("blank");
                        List<Text> lst = e.content();
                        //正文
                        String stem = lst.get(0).getText();
                        System.out.println(stem);
                        Question q = new Question(id, stem, "", questionType, questionNum, year, area);
                         q.insertQuestion(dataBase);
                    }
                }
            } else if (i == 2) {
                //题目类型。填空选择解答
                int questionType = 2;
                for (int j = 1; j <= c2.size(); j++) {
                    List<Element> questions = c2.get(j - 1).elements();
                    List<Text> lst = questions.get(0).content();
                    String stem = lst.get(0).getText();
                    System.out.println(stem);
                    for (int k = 1; k < questions.size(); k++) {
                        questionNum++;
                        String id = questions.get(k).attribute("id").getValue();
                        List<Element> e1 = questions.get(k).elements();
                        lst = e1.get(0).content();
                        String subStem = lst.get(0).getText();
                        System.out.println(subStem);
                        Question q = new Question(id,stem,subStem,questionType,questionNum,year,area);
                        q.insertQuestion(dataBase);
                    }
                }
            }
        }
        System.out.println(1);
    }
}
