import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mysql.jdbc.Connection;

//import javax.persistence.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 试卷内容类
public class Paper {
    String[] paragraphs;
    String title;
    String subject;
    List<Question> questionsList;

    private final static String IMAGE_DIR;

    static {
        if (System.getProperty("os.name").startsWith("Windows")) {
            IMAGE_DIR = "D:\\WordImages";
        } else {
            IMAGE_DIR = "/home/images";
        }
    }

    public String[] getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(String[] paragraphs) {
        this.paragraphs = paragraphs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void addQuestion(Question question) {
        this.questionsList.add(question);
    }

    public List<Question> getQuestionsList() {
        return questionsList;
    }

    public Question getQuestionByIndex(int index) {
        for (Question q : questionsList) {
            if (q.getIndex() == index) {
                return q;
            }
        }
        return null;
    }

    public void setQuestionsList(List<Question> questionsList) {
        this.questionsList = questionsList;
    }

    public Paper() {
        this.questionsList = new ArrayList<>(50);
    }

    public void dumpAllImages() {
        File dir = new File(IMAGE_DIR);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        for (Question question : questionsList) {
            List<String> stemImgsPath = new ArrayList<>();
            List<String> subStemImgsPath = new ArrayList<>();
            List<String> answer1ImgsPath = new ArrayList<>();
            List<String> answer2ImgsPath = new ArrayList<>();
            List<String> answer3ImgsPath = new ArrayList<>();
            List<String> analysis1ImgsPath = new ArrayList<>();
            List<String> analysis2ImgsPath = new ArrayList<>();
            List<String> analysis3ImgsPath = new ArrayList<>();

            for (QuestionImage image : question.getStemImgs()) {
                image.store();
                stemImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getSubStemImgs()) {
                image.store();
                subStemImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getAnswer1Imgs()) {
                image.store();
                answer1ImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getAnswer2Imgs()) {
                image.store();
                answer2ImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getAnswer3Imgs()) {
                image.store();
                answer3ImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getAnalysis1Imgs()) {
                image.store();
                analysis1ImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getAnalysis2Imgs()) {
                image.store();
                analysis2ImgsPath.add(image.getStorePath());
            }
            for (QuestionImage image : question.getAnalysis3Imgs()) {
                image.store();
                analysis3ImgsPath.add(image.getStorePath());
            }
            question.addStemImgsPath(stemImgsPath);
            question.addSubStemImgsPath(subStemImgsPath);
            question.addAnswer1ImgsPath(answer1ImgsPath);
            question.addAnswer2ImgsPath(answer2ImgsPath);
            question.addAnswer3ImgsPath(answer3ImgsPath);
            question.addAnalysis1ImgsPath(answer1ImgsPath);
            question.addAnalysis2ImgsPath(answer2ImgsPath);
            question.addAnalysis3ImgsPath(answer3ImgsPath);

        }
    }

    public void dump2db(String firstName) {

        //    private final static String url = "jdbc:mysql://120.24.56.197:3306/deepeducation?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
        //    private final static String username = "root";
        //    private final static String password = "Test123456";

//        final String url = "jdbc:mysql://120.24.56.197/deepeducation?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
        final String url = "jdbc:mysql://192.168.1.9/zdy_web?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
        final String username = "root";
//        final String password = "Test123456";
        final String password = "woaizxl";

        String driver = "com.mysql.cj.jdbc.Driver";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = (Connection) DriverManager.getConnection(url, username, password);
            List<Question> questions = getQuestionsList();
            String SQL = "INSERT INTO shs_question (questionid,stem,sub_stem,question_type,field,category) VALUES(?,?,?,?,?,?);";
            for (int i = 1;i<=questions.size();i++) {
                Question question = questions.get(i-1);
                String stem = question.getStem();
                List<String> stemImgPathList = new ArrayList<>();
                for (QuestionImage image : question.getStemImgs()) {
                    stemImgPathList.add(image.getStorePath());
                }
                String stemImgPath = JSON.toJSONString(stemImgPathList);

                String subStem = question.getSubStem();
                List<String> subStemImgPathList = new ArrayList<>();
                for (QuestionImage image : question.getSubStemImgs()) {
                    subStemImgPathList.add(image.getStorePath());
                }
                String subStemImgPath = JSON.toJSONString(subStemImgPathList);

                String answer = question.getAnswer1();
                List<String> answerPathList = new ArrayList<>();
                for (QuestionImage image : question.getAnswer1Imgs()) {
                    answerPathList.add(image.getStorePath());
                }
                String answerImgPath = JSON.toJSONString(answerPathList);

                String analysis = question.getAnalysis1();
                List<String> analysisPathList = new ArrayList<>();
                for (QuestionImage image : question.getAnalysis1Imgs()) {
                    analysisPathList.add(image.getStorePath());
                }
                String analysisImgPath = JSON.toJSONString(analysisPathList);

                String type;
                switch (question.getType()) {
                    case SELECTION:
                        type = "0";
                        break;
                    case FILLING:
                        type = "1";
                        break;
                    case SOLVING:
                        type = "2";
                        break;
                    case OTHER:
                        type = "3";
                        break;
                    default:
                        new ReadWordException("错误的题目类型").printStackTrace();
                        return;
                }
                PreparedStatement ps = conn.prepareStatement(SQL);
                if(i<=9){
                    ps.setString(1, firstName+"0"+i);
                }
                else{
                    ps.setString(1,firstName+i);
                }
                ps.setString(2, stem);
                ps.setString(3, subStem);
                ps.setString(5, "高中");
                ps.setString(4, type);
                ps.setString(6, "0");
                ps.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class Question {
        public static enum QuestionType {
            SELECTION {
                @Override
                public String getInfo() {
                    return "单选";
                }
            },
            MULTI_SELECTION {
                @Override
                public String getInfo() {
                    return "多选";
                }
            },
            JUDGEMENT {
                @Override
                public String getInfo() {
                    return "判断";
                }
            },
            FILLING {
                @Override
                public String getInfo() {
                    return "填空";
                }
            },
            SOLVING {
                @Override
                public String getInfo() {
                    return "解答";
                }
            },
            OTHER {
                @Override
                public String getInfo() {
                    return "其他";
                }
            };

            public abstract String getInfo();

            public static String getNameByOrder(int order) {
                if (order < 0 || order > QuestionType.values().length) {
                    return "异常的序号";
                }
                return QuestionType.values()[order].getInfo();
            }

        }

        // 题号
        private Long id;

        private Integer index;

        // 题型
        private QuestionType type;

        // 出题区域
        private String area;

        // 年份
        private String year;

        // 题目分类
        private String category;

        // 来源
        private String origin;

        // 难易度
        private Double difficulty;


        // 题目分析
        private String questionAnalysis;

        // 章节
        private String chapter;

        // 教材
        private String textbook;

        // 注解
        private String annotation;


        // 创建人ID
        private Long adminId;

        // 知识点ID
        private Long knowledgePointId;

        // 关键词
        private String keyword;

        //创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time = LocalDateTime.now();

        // 题干文字
        private String stem;

        // 题干图片
        private List<QuestionImage> stemImgs;

        // 题干图片路径
        private List<String> stemImgsPath;

        // 小问文字
        private String subStem;

        // 小问图片
        private List<QuestionImage> subStemImgs;

        // 小问图片路径
        private List<String> subStemImgsPath;


        // 答案
        private String answer1;

        // 答案1图片
        private List<QuestionImage> answer1Imgs;

        // 答案1图片路径
        private List<String> answer1ImgsPath;

        // 答案
        private String answer2;

        // 答案2图片
        private List<QuestionImage> answer2Imgs;

        // 答案2图片路径
        private List<String> answer2ImgsPath;

        // 答案
        private String answer3;

        // 答案3图片
        private List<QuestionImage> answer3Imgs;

        // 答案3图片路径
        private List<String> answer3ImgsPath;

        // 解析1文字
        private String analysis1;

        // 解析1图片
        private List<QuestionImage> analysis1Imgs;

        // 解析1图片路径
        private List<String> analysis1ImgsPath;

        // 解析2文字
        private String analysis2;

        // 解析2图片
        private List<QuestionImage> analysis2Imgs;

        // 解析2图片路径
        private List<String> analysis2ImgsPath;

        // 解析3文字
        private String analysis3;

        // 解析3图片
        private List<QuestionImage> analysis3Imgs;

        // 解析3图片路径
        private List<String> analysis3ImgsPath;


        public Question() {
            this.stemImgs = new ArrayList<>(4);
            this.stemImgsPath = new ArrayList<>(4);
            this.subStemImgs = new ArrayList<>(4);
            this.subStemImgsPath = new ArrayList<>(4);
            this.answer1Imgs = new ArrayList<>(4);
            this.answer2Imgs = new ArrayList<>(4);
            this.answer3Imgs = new ArrayList<>(4);
            this.answer1ImgsPath = new ArrayList<>(4);
            this.answer2ImgsPath = new ArrayList<>(4);
            this.answer3ImgsPath = new ArrayList<>(4);
            this.analysis1Imgs = new ArrayList<>(4);
            this.analysis2Imgs = new ArrayList<>(4);
            this.analysis3Imgs = new ArrayList<>(4);
            this.analysis1ImgsPath = new ArrayList<>(4);
            this.analysis2ImgsPath = new ArrayList<>(4);
            this.analysis3ImgsPath = new ArrayList<>(4);
        }


        public QuestionType getType() {
            return type;
        }

        public String getCategory() {
            return category;
        }

        public String getArea() {
            return area;
        }

        public String getYear() {
            return year;
        }

        public String getOrigin() {
            return origin;
        }

        public Double getDifficulty() {
            return difficulty;
        }

        public String getQuestionAnalysis() {
            return questionAnalysis;
        }

        public String getChapter() {
            return chapter;
        }

        public String getTextbook() {
            return textbook;
        }

        public String getAnnotation() {
            return annotation;
        }

        public Long getAdminId() {
            return adminId;
        }

        public Long getKnowledgePointId() {
            return knowledgePointId;
        }

        public String getKeyword() {
            return keyword;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public String getStem() {
            return stem;
        }

        public List<QuestionImage> getStemImgs() {
            return stemImgs;
        }

        public List<String> getStemImgsPath() {
            return stemImgsPath;
        }

        public String getSubStem() {
            return subStem;
        }

        public List<QuestionImage> getSubStemImgs() {
            return subStemImgs;
        }

        public List<String> getSubStemImgsPath() {
            return subStemImgsPath;
        }

        public String getAnswer1() {
            return answer1;
        }

        public List<QuestionImage> getAnswer1Imgs() {
            return answer1Imgs;
        }

        public List<String> getAnswer1ImgsPath() {
            return answer1ImgsPath;
        }

        public String getAnswer2() {
            return answer2;
        }

        public List<QuestionImage> getAnswer2Imgs() {
            return answer2Imgs;
        }

        public List<String> getAnswer2ImgsPath() {
            return answer2ImgsPath;
        }

        public String getAnswer3() {
            return answer3;
        }

        public List<QuestionImage> getAnswer3Imgs() {
            return answer3Imgs;
        }

        public List<String> getAnswer3ImgsPath() {
            return answer3ImgsPath;
        }

        public Long getId() {
            return id;
        }

        public Integer getIndex() {
            return index;
        }

        public String getAnalysis1() {
            return analysis1;
        }

        public List<QuestionImage> getAnalysis1Imgs() {
            return analysis1Imgs;
        }

        public List<String> getAnalysis1ImgsPath() {
            return analysis1ImgsPath;
        }

        public String getAnalysis2() {
            return analysis2;
        }

        public List<QuestionImage> getAnalysis2Imgs() {
            return analysis2Imgs;
        }

        public List<String> getAnalysis2ImgsPath() {
            return analysis2ImgsPath;
        }

        public String getAnalysis3() {
            return analysis3;
        }

        public List<QuestionImage> getAnalysis3Imgs() {
            return analysis3Imgs;
        }

        public List<String> getAnalysis3ImgsPath() {
            return analysis3ImgsPath;
        }


        public void setType(QuestionType type) {
            this.type = type;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setStem(String stem) {
            this.stem = stem;
        }

        public void setSubStem(String subStem) {
            this.subStem = subStem;
        }

        public void setAnswer1(String answer1) {
            this.answer1 = answer1;
        }

        public void setAnswer2(String answer2) {
            this.answer2 = answer2;
        }

        public void setAnswer3(String answer3) {
            this.answer3 = answer3;
        }

        public void setAnalysis1(String analysis1) {
            this.analysis1 = analysis1;
        }

        public void setAnalysis2(String analysis2) {
            this.analysis2 = analysis2;
        }

        public void setAnalysis3(String analysis3) {
            this.analysis3 = analysis3;
        }


        public void setArea(String area) {
            this.area = area;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public void setDifficulty(Double difficulty) {
            this.difficulty = difficulty;
        }

        public void setQuestionAnalysis(String questionAnalysis) {
            this.questionAnalysis = questionAnalysis;
        }

        public void setChapter(String chapter) {
            this.chapter = chapter;
        }

        public void setTextbook(String textbook) {
            this.textbook = textbook;
        }

        public void setAnnotation(String annotation) {
            this.annotation = annotation;
        }

        public void setAdminId(Long adminId) {
            this.adminId = adminId;
        }

        public void setKnowledgePointId(Long knowledgePointId) {
            this.knowledgePointId = knowledgePointId;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }

        public void addStemImgs(List<QuestionImage> stemImgs) {
            this.stemImgs.addAll(stemImgs);
        }

        public void addStemImgsPath(List<String> stemImgsPath) {
            this.stemImgsPath.addAll(stemImgsPath);
        }

        public void addSubStemImgs(List<QuestionImage> subStemImgs) {
            this.subStemImgs.addAll(subStemImgs);
        }

        public void addSubStemImgsPath(List<String> subStemImgsPath) {
            this.subStemImgsPath.addAll(subStemImgsPath);
        }


        public void addAnswer1Imgs(List<QuestionImage> answer1Imgs) {
            this.answer1Imgs.addAll(answer1Imgs);
        }

        public void addAnswer2Imgs(List<QuestionImage> answer2Imgs) {
            this.answer2Imgs.addAll(answer2Imgs);
        }

        public void addAnswer3Imgs(List<QuestionImage> answer3Imgs) {
            this.answer3Imgs.addAll(answer3Imgs);
        }

        public void addAnswer1ImgsPath(List<String> answer1ImgsPath) {
            this.answer1ImgsPath.addAll(answer1ImgsPath);
        }

        public void addAnswer2ImgsPath(List<String> answer2ImgsPath) {
            this.answer2ImgsPath.addAll(answer2ImgsPath);
        }

        public void addAnswer3ImgsPath(List<String> answer3ImgsPath) {
            this.answer3ImgsPath.addAll(answer3ImgsPath);
        }


        public void addAnalysis1Imgs(List<QuestionImage> analysis1Imgs) {
            this.analysis1Imgs.addAll(analysis1Imgs);
        }

        public void addAnalysis2Imgs(List<QuestionImage> analysis2Imgs) {
            this.analysis2Imgs.addAll(analysis2Imgs);
        }

        public void addAnalysis3Imgs(List<QuestionImage> analysis3Imgs) {
            this.analysis3Imgs.addAll(analysis3Imgs);
        }

        public void addAnalysis1ImgsPath(List<String> analysis1ImgsPath) {
            this.analysis1ImgsPath.addAll(analysis1ImgsPath);
        }

        public void addAnalysis2ImgsPath(List<String> analysis2ImgsPath) {
            this.analysis2ImgsPath.addAll(analysis2ImgsPath);
        }

        public void addAnalysis3ImgsPath(List<String> analysis3ImgsPath) {
            this.analysis3ImgsPath.addAll(analysis3ImgsPath);
        }


    }

    // 题目图片
    public static class QuestionImage {
        private String title;
        private byte[] imageByts;
        private String type;
        private String info;
        private String id;
        private String storePath;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStorePath() {
            return storePath;
        }

        public void setStorePath(String storePath) {
            this.storePath = storePath;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public byte[] getImageByts() {
            return imageByts;
        }

        public void setImageByts(byte[] imageByts) {
            this.imageByts = imageByts;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public void fillStorePath() {
            String uuid = System.currentTimeMillis() + UUID.randomUUID().toString() + "." + getType();
            String path = IMAGE_DIR + File.separator + uuid;
            setStorePath(path);
        }

        public void store() {
            String path = getStorePath();
            if (path == null) {
                return;
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(path);
                outputStream.write(getImageByts());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class ReadWordException extends Exception {
        public ReadWordException() {
        }

        public ReadWordException(String message) {
            super(message);
        }
    }
}

