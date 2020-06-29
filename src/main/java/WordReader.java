
//Code is far away from bugs with the god animal protecting
//    I love animals. They taste delicious.
//             ┏┓   ┏┓
//            ┏┛┻━━━┛┻┓
//            ┃       ┃
//            ┃┳┛  ┗┳ ┃
//            ┃┻    ┻ ┃
//            ┗━┓   ┏━┛
//              ┃   ┗━━━┓
//              ┃神兽保佑 ┣┓
//              ┃永无BUG ┏┛
//              ┗┓┓┏━┳┓┏┛
//               ┃┫┫ ┃┫┫
//               ┗┻┛ ┗┻┛

// author: duolaoa
// date: 2019.06.13
// time: 13:35


import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordReader {


    private final static Set<String> IGNORE_TYPES = new HashSet<>();
    // 2000 byte
    private final static int MINIMUM_SIZE = 2000;

    static {
        IGNORE_TYPES.add(".wmf");
    }


    private String[] readDocText(String path) {
        InputStream is = null;
        WordExtractor we = null;
        try {
            is = new FileInputStream(new File(path));
            we = new WordExtractor(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (we != null) {
            return we.getParagraphText();
        } else {
            return null;
        }
    }

    private String[] readDocxText(String path) {
        FileInputStream inputStream = null;
        XWPFDocument xDocument = null;
        String[] result = null;
        try {
            inputStream = new FileInputStream(path);
            xDocument = new XWPFDocument(inputStream);
            List<XWPFParagraph> paragraphs = xDocument.getParagraphs();
            String[] temp = new String[paragraphs.size()];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = paragraphs.get(i).getText();
            }
            result = temp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    private Map<String, Paper.QuestionImage> readDocxImage(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        Map<String, Paper.QuestionImage> map = new HashMap<>();
        try {
            XWPFDocument xDocument = new XWPFDocument(inputStream);
            List<XWPFPictureData> pictures = xDocument.getAllPictures();
            for (XWPFPictureData picture : pictures) {

                String id = picture.getParent().getRelationId(picture);
                String rawName = picture.getFileName();
                String imageId = rawName.substring(rawName.indexOf("image") + "image".length(), rawName.indexOf("."));
                byte[] bytes = picture.getData();
                String fileExt = rawName.substring(rawName.lastIndexOf("."));
                if (IGNORE_TYPES.contains(fileExt) || bytes.length < MINIMUM_SIZE) {
                    continue;
                }

                Paper.QuestionImage image = new Paper.QuestionImage();
                image.setId(id);
                image.setImageByts(bytes);
                image.setTitle(rawName);
                image.setType(fileExt.substring(fileExt.lastIndexOf(".") + 1));
                map.put(id, image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    private String matchTitle(String str) {
        String titlePatternStr = "\\d{4}年\\d{2}月\\d{2}日.{0,30}学校高中数学试卷";
        Pattern titlePattern = Pattern.compile(titlePatternStr);
        Matcher matcher = titlePattern.matcher(str);
        if (matcher.find() /*&& matcher.groupCount() == 1*/) {
            return matcher.group();
        } else {
            return null;
        }
    }

    private boolean matchTypeMark(String para, String typeMark) {
        return para.lastIndexOf(typeMark) >= 0;
    }

    private List<String> matchImage(String str) {
        String imagePatternStr = "<img src='(rId\\d+)'/>";
        Pattern pattern = Pattern.compile(imagePatternStr);
        Matcher matcher = pattern.matcher(str);
        List<String> ids = new ArrayList<>();
        while (matcher.find()) {
            String id = matcher.group(1);
            ids.add(id);
        }
        return ids;
    }

    private List<String> matchImageTag(String str) throws Paper.ReadWordException {
        String imageTagStr = "<wp\\:docPr[^>]{0,100}id=\"(\\d+)\"\\s[^>]{0,50}>";
        String embedTagSTr = "embed=\\\"(rId\\d+)\\\"";
        Pattern imageTagPattern = Pattern.compile(imageTagStr);
        Pattern embedTagPattern = Pattern.compile(embedTagSTr);
        Matcher imageMatcher = imageTagPattern.matcher(str);

        List<String> ids = new ArrayList<>();
        while (imageMatcher.find()) {
            String imageTag = imageMatcher.group(0);
            int start = str.indexOf(imageTag) + imageTag.length();
            Matcher embedMatcer = embedTagPattern.matcher(str.substring(start));
            if (embedMatcer.find()) {
                ids.add(embedMatcer.group(1));
            } else {
                throw new Paper.ReadWordException("图片映射错误");
            }

        }
        return ids;
    }


    private void setEntity(Paper paper, String[] filled_paragraphs, Map<String, Paper.QuestionImage> imageMap) {
        final String[] typeMarks = {"一、选择题", "二、填空题", "三、解答题", "四、??"};
        Map<String, Paper.Question.QuestionType> map = new HashMap<>();
        map.put("一、选择题", Paper.Question.QuestionType.SELECTION);
        map.put("二、填空题", Paper.Question.QuestionType.FILLING);
        map.put("三、解答题", Paper.Question.QuestionType.SOLVING);
        map.put("四、??", Paper.Question.QuestionType.OTHER);
        int curLine = 0;
        int MAX_LINE_NUM = filled_paragraphs.length;
        while (curLine < MAX_LINE_NUM) {
            String title = matchTitle(filled_paragraphs[curLine]);
            curLine++;
            if (title != null) {
                paper.setTitle(title);
                break;
            }
        }
        if (curLine >= MAX_LINE_NUM) {
            new Paper.ReadWordException("没有找到title").printStackTrace();
            return;
        }
        int typeMarkCount = -1;
        String typeMark = typeMarks[++typeMarkCount];
        int problemID = 0;
        String prefix = "%d.";
        String nextPrefix = String.format(prefix, ++problemID);
        while (curLine < MAX_LINE_NUM) {
            String para = filled_paragraphs[curLine];
            // 空行跳过
            if (para.isEmpty()) {
                curLine++;
                continue;
            }
            // 遇到题型信息跳过
            if (matchTypeMark(para, typeMark)) {
                // 更新下一个预期的题型标记
                typeMark = typeMarks[++typeMarkCount];
                curLine++;
                continue;
            }
            // 没找到预期题号的行跳过
            if (!para.startsWith(nextPrefix)) {
                curLine++;
                continue;
            } else {
                // 找到nextPrefix预期题号的行
                StringBuilder stem = new StringBuilder();
                StringBuilder subStem = new StringBuilder();
                StringBuilder answer = new StringBuilder();
                StringBuilder analysis = new StringBuilder();

                // 题干添加
                stem.append(para.substring(para.indexOf(nextPrefix) + nextPrefix.length()));
                curLine++;

                //更新 curPrefix 预期题号前缀
                nextPrefix = String.format(prefix, ++problemID);

                String subPrefix = String.format(prefix, 1);

                while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith(subPrefix) && !filled_paragraphs[curLine].startsWith("答案：") && !filled_paragraphs[curLine].startsWith(nextPrefix)) {
                    if (!filled_paragraphs[curLine].isEmpty()) {
                        stem.append(filled_paragraphs[curLine]);
                    }
                    curLine++;
                }
                // 直到最后一行没有找到小问或者答案或者下一题
                if (curLine >= MAX_LINE_NUM) {
                    System.err.println(String.format("第%d题没有找到小问或答案", problemID - 1));
                    return;
                }
                // 找到小问
                if (filled_paragraphs[curLine].startsWith(subPrefix)) {
                    while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith("答案：") && !filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        if (!filled_paragraphs[curLine].isEmpty()) {
                            subStem.append(filled_paragraphs[curLine]);
                        }
                        curLine++;
                    }
                    // 直到最后一行没有找到答案或者下一题
                    if (curLine >= MAX_LINE_NUM) {
                        System.err.println(String.format("第%d题没有找到\"答案：\"", problemID - 1));
                    }
                    // 提前找到下一题
                    if (filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        System.err.println(String.format("第%d题没有找到\"答案：\"", problemID - 1));
                        continue;
                    }
                }
                // 找到答案
                if (filled_paragraphs[curLine].startsWith("答案：")) {
                    answer.append(filled_paragraphs[curLine].substring(filled_paragraphs[curLine].indexOf("答案：") + "答案：".length()));
                    curLine++;
                    while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith("解析：") && !filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        if (!filled_paragraphs[curLine].isEmpty()) {
                            answer.append(filled_paragraphs[curLine]);
                        }
                        curLine++;
                    }
                    // 直到最后一行没有找到解析
                    if (curLine >= MAX_LINE_NUM) {
                        System.err.println(String.format("第%d题没有找到\"解析：\"", problemID - 1));
                        return;
                    }
                    // 提前找到下一题
                    if (filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        System.err.println(String.format("第%d题没有找到\"解析：\"", problemID - 1));
                        continue;
                    }
                } else {
                    // 没有匹配上答案
                    new Paper.ReadWordException("控制流错误,没有匹配答案").printStackTrace();
                    return;
                }
                if (filled_paragraphs[curLine].startsWith("解析：")) {
                    analysis.append(filled_paragraphs[curLine].substring(filled_paragraphs[curLine].indexOf("解析：") + "解析：".length()));
                    curLine++;
                    while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith(nextPrefix) && !filled_paragraphs[curLine].startsWith(typeMark)) {
                        if (!filled_paragraphs[curLine].isEmpty()) {
                            analysis.append(filled_paragraphs[curLine]);
                        }
                        curLine++;
                    }
                    if (curLine >= MAX_LINE_NUM) {
                        ;
                    } else if (filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        ;
                    } else if (filled_paragraphs[curLine].startsWith(typeMark)) {
                        ;
                    }
                } else {
                    new Paper.ReadWordException("控制流错误,没有匹配解析").printStackTrace();
                    return;
                }
                Paper.Question question = new Paper.Question();
                question.setType(map.get(typeMarks[typeMarkCount - 1]));
                List<Paper.QuestionImage> stemImgs = new ArrayList<>();
                List<Paper.QuestionImage> subStemImgs = new ArrayList<>();
                List<Paper.QuestionImage> answerImgs = new ArrayList<>();
                List<Paper.QuestionImage> analysisImgs = new ArrayList<>();


                String stemStr = stem.toString();
                String subStemStr = subStem.toString();
                String answerStr = answer.toString();
                String analysisStr = analysis.toString();
                for (String id : matchImage(stemStr)) {
                    if (imageMap.get(id) != null) {
                        Paper.QuestionImage img = imageMap.get(id);
                        stemImgs.add(img);
                        stemStr = stemStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                    }
                }
                for (String id : matchImage(subStemStr)) {
                    if (imageMap.get(id) != null) {
                        Paper.QuestionImage img = imageMap.get(id);
                        subStemImgs.add(img);
                        subStemStr = subStemStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                    }
                }
                for (String id : matchImage(answerStr)) {
                    if (imageMap.get(id) != null) {
                        Paper.QuestionImage img = imageMap.get(id);
                        answerImgs.add(img);
                        answerStr = answerStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                    }
                }
                for (String id : matchImage(analysisStr)) {
                    if (imageMap.get(id) != null) {
                        Paper.QuestionImage img = imageMap.get(id);
                        analysisImgs.add(img);
                        analysisStr = analysisStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                    }
                }
                question.addStemImgs(stemImgs);
                question.addSubStemImgs(subStemImgs);
                question.addAnswer1Imgs(answerImgs);
                question.addAnalysis1Imgs(analysisImgs);

                question.setStem(stemStr);
                question.setSubStem(subStemStr);
                question.setAnswer1(answerStr);
                question.setAnalysis1(analysisStr);

                question.setIndex(problemID - 1);
                paper.addQuestion(question);
            }
        }
    }

    private void setEntity2(Paper paper, String[] filled_paragraphs, Map<String, Paper.QuestionImage> imageMap) {
        final String[] typeMarks = {"一、选择题", "二、填空题", "三、解答题", "参考答案"};
        Map<String, Paper.Question.QuestionType> map = new HashMap<>();
        map.put("一、选择题", Paper.Question.QuestionType.SELECTION);
        map.put("二、填空题", Paper.Question.QuestionType.FILLING);
        map.put("三、解答题", Paper.Question.QuestionType.SOLVING);
        map.put("参考答案", Paper.Question.QuestionType.OTHER);
        int curLine = 0;
        int MAX_LINE_NUM = filled_paragraphs.length;
        while (curLine < MAX_LINE_NUM) {
            String title = matchTitle(filled_paragraphs[curLine]);
            curLine++;
            if (title != null) {
                paper.setTitle(title);
                break;
            }
        }
        if (curLine >= MAX_LINE_NUM) {
            new Paper.ReadWordException("没有找到title").printStackTrace();
            return;
        }
        int typeMarkCount = -1;
        String typeMark = typeMarks[++typeMarkCount];
        int problemID = 0;
        String prefix = "%d.";
        String nextPrefix = String.format(prefix, ++problemID);
        while (curLine < MAX_LINE_NUM) {
            String para = filled_paragraphs[curLine];
            // 空行跳过
            if (para.isEmpty()) {
                curLine++;
                continue;
            }
            // 遇到题型信息跳过
            if (matchTypeMark(para, typeMark)) {
                // 找到 "参考答案"
                if (typeMark.equals(typeMarks[typeMarks.length - 1])) {
                    break;
                }
                // 更新下一个预期的题型标记
                typeMark = typeMarks[++typeMarkCount];
                curLine++;
                continue;
            }
            // 没找到预期题号的行跳过
            if (!para.startsWith(nextPrefix)) {
                curLine++;
                continue;
            } else {
                // 找到nextPrefix预期题号的行
                StringBuilder stem = new StringBuilder();
                StringBuilder subStem = new StringBuilder();


                // 题干添加
                stem.append(para.substring(para.indexOf(nextPrefix) + nextPrefix.length()));
                curLine++;

                //更新 curPrefix 预期题号前缀
                nextPrefix = String.format(prefix, ++problemID);

                String subPrefix = String.format(prefix, 1);

                while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith(subPrefix) && !filled_paragraphs[curLine].startsWith(nextPrefix) && !filled_paragraphs[curLine].startsWith(typeMark)) {
                    if (!filled_paragraphs[curLine].isEmpty()) {
                        stem.append(filled_paragraphs[curLine]);
                    }
                    curLine++;
                }
                // 找到小问
                if (filled_paragraphs[curLine].startsWith(subPrefix)) {
                    while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith(nextPrefix) && !filled_paragraphs[curLine].startsWith(typeMark)) {
                        if (!filled_paragraphs[curLine].isEmpty()) {
                            subStem.append(filled_paragraphs[curLine]);
                        }
                        curLine++;
                    }
                }
                // 小问结束
                if (filled_paragraphs[curLine].startsWith(nextPrefix) || filled_paragraphs[curLine].startsWith(typeMark)) {
                    Paper.Question question = new Paper.Question();
                    question.setType(map.get(typeMarks[typeMarkCount - 1]));
                    List<Paper.QuestionImage> stemImgs = new ArrayList<>();
                    List<Paper.QuestionImage> subStemImgs = new ArrayList<>();
                    String stemStr = stem.toString();
                    String subStemStr = subStem.toString();

                    for (String id : matchImage(stemStr)) {
                        if (imageMap.get(id) != null) {
                            Paper.QuestionImage img = imageMap.get(id);
                            stemImgs.add(img);
                            stemStr = stemStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                        }
                    }
                    for (String id : matchImage(subStemStr)) {
                        if (imageMap.get(id) != null) {
                            Paper.QuestionImage img = imageMap.get(id);
                            subStemImgs.add(img);
                            subStemStr = subStemStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                        }
                    }

                    question.addStemImgs(stemImgs);
                    question.addSubStemImgs(subStemImgs);

                    question.setStem(stemStr);
                    question.setSubStem(subStemStr);

                    question.setIndex(problemID - 1);
                    paper.addQuestion(question);
                }
            }
        }

        // 重置状态量
        typeMarkCount = -1;
        typeMark = typeMarks[++typeMarkCount];
        problemID = 0;
        prefix = "%d.答案：";
        nextPrefix = String.format(prefix, ++problemID);

        while (curLine < MAX_LINE_NUM) {
            String para = filled_paragraphs[curLine];
            // 空行跳过
            if (para.isEmpty()) {
                curLine++;
                continue;
            }
            // 遇到题型信息跳过
            if (matchTypeMark(para, typeMark)) {
                // 找到 "参考答案"
                if (typeMark.equals(typeMarks[3])) {
                    break;
                }
                // 更新下一个预期的题型标记
                typeMark = typeMarks[++typeMarkCount];
                curLine++;
                continue;
            }
            // 没找到预期题号的行跳过
            if (!para.startsWith(nextPrefix)) {
                curLine++;
                continue;
            } else {
                // 更新 nextPrefix
                String curPrefix = nextPrefix;
                nextPrefix = String.format(prefix, ++problemID);

                // 找到答案
                StringBuilder answer = new StringBuilder();
                StringBuilder analysis = new StringBuilder();
                if (filled_paragraphs[curLine].startsWith(curPrefix)) {
                    answer.append(filled_paragraphs[curLine].substring(filled_paragraphs[curLine].indexOf(curPrefix) + curPrefix.length()));
                    curLine++;
                    while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith("解析：") && !filled_paragraphs[curLine].startsWith(nextPrefix) && !filled_paragraphs[curLine].startsWith(typeMark)) {
                        if (!filled_paragraphs[curLine].isEmpty()) {
                            answer.append(filled_paragraphs[curLine]);
                        }
                        curLine++;
                    }
                    // 直到最后一行没有找到解析
                    if (curLine >= MAX_LINE_NUM) {
                        System.err.println(String.format("第%d题没有找到\"解析：\"", problemID - 1));
                        return;
                    }
                    // 提前找到下一题
                    if (filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        System.err.println(String.format("第%d题没有找到\"解析：\"", problemID - 1));
                        continue;
                    }
                    if (filled_paragraphs[curLine].startsWith(typeMark)) {
                        System.err.println(String.format("第%d题没有找到\"解析：\"", problemID - 1));
                        continue;
                    }
                } else {
                    // 没有匹配上答案
                    new Paper.ReadWordException("控制流错误,没有匹配答案").printStackTrace();
                    return;
                }
                if (filled_paragraphs[curLine].startsWith("解析：")) {
                    analysis.append(filled_paragraphs[curLine].substring(filled_paragraphs[curLine].indexOf("解析：") + "解析：".length()));
                    curLine++;
                    while (curLine < MAX_LINE_NUM && !filled_paragraphs[curLine].startsWith(nextPrefix) && !filled_paragraphs[curLine].startsWith(typeMark)) {
                        if (!filled_paragraphs[curLine].isEmpty()) {
                            analysis.append(filled_paragraphs[curLine]);
                        }
                        curLine++;
                    }
                    if (curLine >= MAX_LINE_NUM) {
                        ;
                    } else if (filled_paragraphs[curLine].startsWith(nextPrefix)) {
                        ;
                    } else if (filled_paragraphs[curLine].startsWith(typeMark)) {
                        ;
                    }
                } else {
                    new Paper.ReadWordException("控制流错误,没有匹配解析").printStackTrace();
                    return;
                }
                Paper.Question question = paper.getQuestionByIndex(problemID - 1);

                List<Paper.QuestionImage> answerImgs = new ArrayList<>();
                List<Paper.QuestionImage> analysisImgs = new ArrayList<>();


                String answerStr = answer.toString();
                String analysisStr = analysis.toString();
                for (String id : matchImage(answerStr)) {
                    if (imageMap.get(id) != null) {
                        Paper.QuestionImage img = imageMap.get(id);
                        answerImgs.add(img);
                        answerStr = answerStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                    }
                }
                for (String id : matchImage(analysisStr)) {
                    if (imageMap.get(id) != null) {
                        Paper.QuestionImage img = imageMap.get(id);
                        analysisImgs.add(img);
                        analysisStr = analysisStr.replace(String.format("<img src='%s'/>", id), String.format("<img src='%s'/>", img.getStorePath()));
                    }
                }

                question.addAnswer1Imgs(answerImgs);
                question.addAnalysis1Imgs(analysisImgs);

                question.setAnswer1(answerStr);
                question.setAnalysis1(analysisStr);

            }
        }

    }

    private String[] getFilledParagraphs(Paper paper, InputStream inputStream, Map<String, Paper.QuestionImage> map) {
        String[] result = null;
        if (inputStream == null) {
            return null;
        }
        try {
            XWPFDocument xDocument = new XWPFDocument(inputStream);
            List<XWPFParagraph> paragraphs = xDocument.getParagraphs();
            result = new String[paragraphs.size()];
            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                //System.out.println(paragraph.getParagraphText());
                String text = "";

                text = text + paragraph.getParagraphText().trim();
                String xml = paragraph.getCTP().xmlText();
                for (String id : matchImageTag(xml)) {
                    Paper.QuestionImage im = map.get(id);
                    if (im == null) {
                        continue;
                    }
                    im.fillStorePath();
//                    String path = im.getStorePath() != null ? im.getStorePath() : "无";
                    text = text + String.format("<img src='%s'/>", id);
                }

//                List<XWPFRun> runs = paragraph.getRuns();
//                for (XWPFRun run : runs) {
//                    if (run.getCTR().xmlText().contains("<w:drawing>")) {
//                        String runXmlText = run.getCTR().xmlText();
//                        int rIdIndex = runXmlText.indexOf("r:embed");
//                        int rIdEndIndex = runXmlText.indexOf("/>", rIdIndex);
//                        String rIdText = runXmlText.substring(rIdIndex, rIdEndIndex);
//                        String id = rIdText.split("\"")[1];
//                        if (map.containsKey(id)) {
//                            text = text + "<img src = '" + map.get(id).getId() + "'/>";
//                        }
//                    } else {
//                        text = text + run;
//                    }
//                }
                result[i] = text.trim();
            }
        } catch (IOException | Paper.ReadWordException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public Paper readWord(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
        int count = 0;
        byte[] buff = new byte[4096];
        try {
            while ((count = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] paragraphs = null;
        Map<String, Paper.QuestionImage> imageMap = null;
        String[] filled_paragraphs = null;
        Paper paper = new Paper();
        imageMap = readDocxImage(new ByteArrayInputStream(outputStream.toByteArray()));
        filled_paragraphs = getFilledParagraphs(paper, new ByteArrayInputStream(outputStream.toByteArray()), imageMap);
        setEntity(paper, filled_paragraphs, imageMap);
        paper.dumpAllImages();
        return paper;
    }

    public Paper readWord2(InputStream inputStream,String firstName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
        int count = 0;
        byte[] buff = new byte[4096];
        try {
            while ((count = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] paragraphs = null;
        Map<String, Paper.QuestionImage> imageMap = null;
        String[] filled_paragraphs = null;
        Paper paper = new Paper();
        imageMap = readDocxImage(new ByteArrayInputStream(outputStream.toByteArray()));
        filled_paragraphs = getFilledParagraphs(paper, new ByteArrayInputStream(outputStream.toByteArray()), imageMap);
        setEntity2(paper, filled_paragraphs, imageMap);
        paper.dump2db(firstName);
        return paper;
    }

    public static void main(String[] args) throws FileNotFoundException {
        String folderPath="C:\\Users\\10733\\Documents\\WeChat Files\\zw1073394859\\FileStorage\\File\\2020-05\\404套";
        File dir = new File(folderPath);
        if (dir.isDirectory()) {
            File next[] = dir.listFiles();
            for (int i = 0; i < next.length; i++) {
                StringBuffer firstName = new StringBuffer("TT404");
                System.out.println(next[i].getName());
                WordReader wordReader = new WordReader();
                if(i<10){
                    firstName.append("0").append(i);
                }
                else{
                    firstName.append(i);
                }
                Paper paper = wordReader.readWord2(new FileInputStream(folderPath+"\\"+next[i].getName()),firstName.toString());
                for (Paper.Question question : paper.getQuestionsList()) {
                    System.out.println("题号:" + question.getIndex());
                    System.out.println("题干:" + question.getStem());
                    System.out.println("小问:" + question.getSubStem());
                    System.out.println("答案:" + question.getAnswer1());
                    System.out.println("解析:" + question.getAnalysis1());
                    System.out.println("题干图片数:" + question.getStemImgs().size());
                    for (Paper.QuestionImage image : question.getStemImgs()) {
                        System.out.println(image.getStorePath());
                    }
                    System.out.println("小问图片数:" + question.getSubStemImgs().size());
                    for (Paper.QuestionImage image : question.getSubStemImgs()) {
                        System.out.println(image.getStorePath());
                    }
                    System.out.println("答案图片数:" + question.getAnswer1Imgs().size());
                    for (Paper.QuestionImage image : question.getAnswer1Imgs()) {
                        System.out.println(image.getStorePath());
                    }
                    System.out.println("解析图片数:" + question.getAnalysis1Imgs().size());
                    for (Paper.QuestionImage image : question.getAnalysis1Imgs()) {
                        System.out.println(image.getStorePath());
                    }
                    System.out.println();
                }
            }
        }
    }
}

