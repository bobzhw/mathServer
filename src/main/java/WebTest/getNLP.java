package WebTest;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.uestc.auto.reasoning.NlpString2;
import edu.uestc.auto.reasoning.PostParam;
import net.didion.jwnl.data.Exc;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class getNLP extends Thread{
    //以Socket为成员变量
    private Socket socket;
    public getNLP(Socket socket){
        try{
            this.socket=socket;
            if(this.socket!=null){
                writer = new PrintWriter(this.socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private PrintWriter writer;
    private BufferedReader in;

    @Override
    public void run(){
        try{
            for(;;){
                if(!socket.isInputShutdown())
                {
                    String res = read();
                    RuleData ruleData = new RuleData();
                    List<RuleData.Method> methods = new ArrayList<>();
                    methods.add(new RuleData.Method(1,"method1",1));
                    methods.add(new RuleData.Method(2,"method2",2));
                    ruleData.setMethod(methods);
                    List<RuleData.Tripples> tripplesArrayList1= new ArrayList<>();
                    RuleData.Tripple tripple1 = new RuleData.Tripple(0,"tripple1",1);
                    RuleData.Tripple tripple2 = new RuleData.Tripple(0,"tripple2",2);
                    RuleData.Tripple tripple3 = new RuleData.Tripple(1,"tripple3",1);
                    RuleData.Tripple tripple4 = new RuleData.Tripple(1,"tripple4",2);
                    List<RuleData.Tripple> trippleList1 = new ArrayList<>();
                    trippleList1.add(tripple1);
                    trippleList1.add(tripple2);
                    List<RuleData.Tripple> trippleList2 = new ArrayList<>();
                    trippleList2.add(tripple3);
                    trippleList2.add(tripple4);
                    tripplesArrayList1.add(new RuleData.Tripples(0,trippleList1));
                    tripplesArrayList1.add(new RuleData.Tripples(1,trippleList2));
                    ruleData.setTripples(tripplesArrayList1);
                    List<RuleData.Parameter> parameters = new ArrayList<>();
                    RuleData.Parameter parameter1 = new RuleData.Parameter("p1","1","1");
                    RuleData.Parameter parameter2 = new RuleData.Parameter("p2","2","2");
                    RuleData.Parameter parameter3 = new RuleData.Parameter("p3","3","3");
                    parameters.add(parameter1);
                    parameters.add(parameter2);
                    parameters.add(parameter3);
                    ruleData.setParameters(parameters);
                    write(JSON.toJSONString(ruleData));
                    out.println("发送完毕");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String read(){
        StringBuffer res = new StringBuffer() ;
        try{
            char[] buf = new char[1024];
            int size = 0;
            while (res.toString().isEmpty() || !res.toString().contains("eof")) {
                size = in.read(buf,0,1024);
                res.append(new String(buf,0,size));
                System.out.println(res);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res.toString();
    }

    public void write(String data){
        writer.println(data+"eof");
        // 将从系统标准输入读入的字符串输出到Server
        writer.flush();
    }
}


