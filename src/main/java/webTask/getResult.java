package webTask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class getResult {
    public static void main(String[] args) throws Exception {
        DataBase dataBase = new DataBase("autosolve_test");
        ResultSet rs = dataBase.query(" select questionID,result from results");

        TextWriter writer =new TextWriter("solveResult",false);
        List<qr> ls = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("questionID");
            String result = rs.getString("result");
            ls.add(new qr(id,result));
        }
        for(qr q : ls){
            String id = q.id;
            String sql ="select questionType from questions where id ='"+id+"'";
            ResultSet res = dataBase.query(sql);
            while(res.next()){
                int c =  res.getInt("questionType");
                if(c==2){
                    writer.write(id+"\t");
                    writer.write(q.result);
                }
            }
        }
        writer.close();
    }
    static class qr{
        public String id;
        public String result;
        public qr(String id,String result){
            this.id = id;
            this.result=result;
        }
    }
}
