package com.swpuiot.qaservice;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.*;

/**
 * Created by DELL on 2019/4/28.
 */
public class Classifier {

    private static String curDir;
    private static File disease;
    private static File check;
    private static File symptom;
    private static File producer;
    private static File food;
    private static File drug;
    private static File department;
    private static File deny;
    private static Trie regionTree;
    private static Collection<String> regionWord = new HashSet<>();
    private static List<String> producerList;
    private static List<String> drugList;
    private static List<String> departmentList;
    private static List<String> checkList;
    private static List<String> diseaseList;
    private static List<String> foodList;
    private static List<String> symptomList;
    private static List<String> symptom_qwds;
    private static List<String> cause_qwds;
    private static List<String> acompany_qwds;
    private static List<String> food_qwds;
    private static List<String> drug_qwds;
    private static List<String> prevent_qwds;
    private static List<String> cureway_qwds;
    private static List<String> lasttime_qwds;
    private static List<String> cureprob_qwds;
    private static List<String> check_qwds;
    private static List<String> sbelong_qwds;
    private static List<String> easyget_qwds;

    private static List<String> cure_qwds;
    private static Map<String, List<String>> wordTyptDict;
    private static List<String> denyList;


    public static void init() throws IOException {
        Resource resource = new ClassPathResource("/dict");
        //获取到dict目录
        File curDir = resource.getFile().getAbsoluteFile();
        File[] files = curDir.listFiles();
        //获取目标文件目录
        for (int i = 0; i < files.length; i++) {
            switch (files[i].getName()) {
                case "disease.txt":
                    disease = files[i];
                    break;
                case "check.txt":
                    check = files[i];
                    break;
                case "deny.txt":
                    deny = files[i];
                    break;
                case "department.txt":
                    department = files[i];
                    break;
                case "drug.txt":
                    drug = files[i];
                    break;
                case "food.txt":
                    food = files[i];
                    break;
                case "producer.txt":
                    producer = files[i];
                    break;
                case "symptom.txt":
                    symptom = files[i];
                    break;
                default:
                    break;

            }
        }
        //加载特征词
        diseaseList = readFile(new FileInputStream(disease));
        checkList = readFile(new FileInputStream(check));
        departmentList = readFile(new FileInputStream(department));
        drugList = readFile(new FileInputStream(drug));
        foodList = readFile(new FileInputStream(food));
        producerList = readFile(new FileInputStream(producer));
        symptomList = readFile(new FileInputStream(symptom));
        denyList = readFile(new FileInputStream(deny));
        regionWord.addAll(diseaseList);
        regionWord.addAll(checkList);
        regionWord.addAll(departmentList);
        regionWord.addAll(drugList);
        regionWord.addAll(foodList);
        regionWord.addAll(producerList);
        regionWord.addAll(symptomList);
        regionWord.addAll(denyList);
        //构造AC树
        regionTree = buildActree(regionWord);
        //构建词典
        wordTyptDict = buildWordTypeDict();
        symptom_qwds = new LinkedList<>(Arrays.asList("症状", "表征", "现象", "症候", "表现"));
        cause_qwds = new LinkedList<>(Arrays.asList("原因", "成因", "为什么", "怎么会", "怎样才", "咋样才", "怎样会", "如何会", "为啥", "为何", "如何才会", "怎么才会", "会导致", "会造成"));
        acompany_qwds = new LinkedList<>(Arrays.asList("并发症", "并发", "一起发生", "一并发生", "一起出现", "一并出现", "一同发生", "一同出现", "伴随发生", "伴随", "共现"));
        food_qwds = new LinkedList<>(Arrays.asList("饮食", "饮用", "吃", "食", "伙食", "膳食", "喝", "菜", "忌口", "补品", "保健品", "食谱", "菜谱", "食用", "食物", "补品"));
        drug_qwds = new LinkedList<>(Arrays.asList("药", "药品", "用药", "胶囊", "口服液", "炎片"));
        prevent_qwds = new LinkedList<>(Arrays.asList("预防", "防范", "抵制", "抵御", "防止", "躲避", "逃避", "避开", "免得", "逃开", "避开", "避掉", "躲开", "躲掉", "绕开",
                "怎样才能不", "怎么才能不", "咋样才能不", "咋才能不", "如何才能不",
                "怎样才不", "怎么才不", "咋样才不", "咋才不", "如何才不",
                "怎样才可以不", "怎么才可以不", "咋样才可以不", "咋才可以不", "如何可以不",
                "怎样才可不", "怎么才可不", "咋样才可不", "咋才可不", "如何可不"));
        lasttime_qwds = new LinkedList<>(Arrays.asList("周期", "多久", "多长时间", "多少时间", "几天", "几年", "多少天", "多少小时", "几个小时", "多少年"));
        cureway_qwds = new LinkedList<>(Arrays.asList("怎么治疗", "如何医治", "怎么医治", "怎么治", "怎么医", "如何治", "医治方式", "疗法", "咋治", "怎么办", "咋办", "咋治"));
        cureprob_qwds = new LinkedList<>(Arrays.asList("多大概率能治好", "多大几率能治好", "治好希望大么", "几率", "几成", "比例", "可能性", "能治", "可治", "可以治", "可以医"));
        easyget_qwds = new LinkedList<>(Arrays.asList("易感人群", "容易感染", "易发人群", "什么人", "哪些人", "感染", "染上", "得上"));
        check_qwds = new LinkedList<>(Arrays.asList("检查", "检查项目", "查出", "检查", "测出", "试出"));
        sbelong_qwds = new LinkedList<>(Arrays.asList("属于什么科", "属于", "什么科", "科室"));
        cure_qwds = new LinkedList<>(Arrays.asList("治疗什么", "治啥", "治疗啥", "医治啥", "治愈啥", "主治啥", "主治什么", "有什么用", "有何用", "用处", "用途",
                "有什么好处", "有什么益处", "有何益处", "用来", "用来做啥", "用来作甚", "需要", "要"));

        System.out.println("AC树，匹配");

//        Collection<Emit> collection = trie.parseText("学习一下小儿触电与雷击");
//        System.out.println(collection.size());
//        for (Emit emit : collection) {
//            System.out.println(emit.toString());
//        }
    }

    public static Map classify(String question) {
        Map<String, Object> data = new HashMap<>();
        Map<String, List<String>> medicalDict = check_medical(question);
        if (medicalDict == null) {
            return data;
        }
        data.put("args", medicalDict);
//        收集实体类型
        List<String> types = new LinkedList<>();
        for (List<String> strings : medicalDict.values()) {
            for (String string : strings) {
                types.add(string);
            }
        }
        String questionType = "others";
        List<String> questionTypes = new LinkedList<>();
//        types.stream().flatMap(new Function<List<String>, Stream<?>>() {
//            @Override
//            public Stream<?> apply(List<String> strings) {
//
//                return null;
//            }
//        })
        //症状
        if (checkWord(symptom_qwds, question) && types.contains("disease")) {
            questionType = "disease_symptom";
            questionTypes.add(questionType);
        }
        if (checkWord(symptom_qwds, question) && types.contains("symptom")) {
            questionType = "symptom_disease";
            questionTypes.add(questionType);
        }

        //原因
        if (checkWord(cause_qwds, question) && types.contains("disease")) {
            questionType = "disease_cause";
            questionTypes.add(questionType);
        }
        //并发症
        if (checkWord(acompany_qwds, question) && types.contains("disease")) {
            questionType = "disease_acompany";
            questionTypes.add(questionType);
        }

        // 推荐食品
        if (checkWord(food_qwds, question) && types.contains("disease")) {
            boolean denyStatus;
            denyStatus = checkWord(denyList, question);
            if (denyStatus) {
                questionType = "disease_not_food";
            } else {
                questionType = "disease_do_food";
            }
            questionTypes.add(questionType);
        }

        // 已知食物找疾病
        List<String> tempQwds = new LinkedList<>();
        tempQwds.addAll(food_qwds);
        tempQwds.addAll(cure_qwds);
        if (checkWord((tempQwds), question) && types.contains("food")) {
            boolean denyStatus;
            denyStatus = checkWord(denyList, question);
            if (denyStatus) {
                questionType = "food_not_disease";
            } else {
                questionType = "food_do_disease";
            }
            questionTypes.add(questionType);
        }
        // 推荐药品
        if (checkWord(drug_qwds, question) && types.contains("disease")) {
            questionType = "disease_drug";
            questionTypes.add(questionType);
        }
        // 药品治啥病
        if (checkWord(cure_qwds, question) && types.contains("drug")) {
            questionType = "drug_disease";
            questionTypes.add(questionType);
        }
        // 疾病接受检查项目
        if (checkWord(check_qwds, question) && types.contains("disease")) {
            questionType = "disease_check";
            questionTypes.add(questionType);
        }
        // 已知检查项目查相应疾病
        tempQwds.removeAll(food_qwds);
        tempQwds.addAll(check_qwds);
        if (checkWord(tempQwds, question) && types.contains("check")) {
            questionType = "check_disease";
            questionTypes.add(questionType);
        }
        // 　症状防御
        if (checkWord(prevent_qwds, question) && types.contains("disease")) {
            questionType = "disease_prevent";
            questionTypes.add(questionType);
        }
        // 疾病医疗周期
        if (checkWord(lasttime_qwds, question) && types.contains("disease")) {
            questionType = "disease_lasttime";
            questionTypes.add(questionType);
        }
        // 疾病治疗方式
        if (checkWord(cureway_qwds, question) && types.contains("disease")) {
            questionType = "disease_cureway";
            questionTypes.add(questionType);
        }

        // 疾病治愈可能性
        if (checkWord(cureprob_qwds, question) && types.contains("disease")) {
            questionType = "disease_cureprob";
            questionTypes.add(questionType);
        }

        // 疾病易感染人群
        if (checkWord(easyget_qwds, question) && types.contains("disease")) {
            questionType = "disease_easyget";
            questionTypes.add(questionType);
        }

        // 若没有查到相关的外部查询信息，那么则将该疾病的描述信息返回
        if (questionTypes.size() == 0 && questionTypes.contains("disease")) {
            questionTypes.add("disease_desc");
        }

        // 若没有查到相关的外部查询信息，那么则将该疾病的描述信息返回
        if (questionTypes.size() == 0 && questionTypes.contains("symptom")) {
            questionTypes.add("symptom_disease");
        }
        data.put("questionTypes", questionTypes);
        return data;
    }

    public static Map<String, List<String>> buildWordTypeDict() {
        Map<String, List<String>> map = new HashMap<>();
        for (String wd : regionWord) {
            System.out.println(wd);
            List<String> list = new LinkedList<>();
            if (diseaseList.contains(wd)) {
                list.add("disease");
                map.put(wd, list);
            }
            if (departmentList.contains("department")) {
                list.add("department");
                map.put(wd, list);
            }
            if (checkList.contains("check")) {
                list.add("check");
                map.put(wd, list);
            }
            if (drugList.contains("drug")) {
                list.add("drug");
                map.put(wd, list);
            }
            if (foodList.contains("food")) {
                list.add("food");
                map.put(wd, list);
            }
            if (symptomList.contains("symptom")) {
                list.add("symptom");
                map.put(wd, list);
            }
            if (producerList.contains("producer")) {
                list.add("producer");
                map.put(wd, list);
            }
        }
        return map;
    }

    public static List<String> readFile(FileInputStream inputStream) throws IOException {
        List<String> list = new LinkedList<>();
        String str;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((str = bufferedReader.readLine()) != null) {
            list.add(str);
        }
        bufferedReader.close();
        inputStream.close();
        return list;
    }

    public static void main(String[] args) {
        try {
            Classifier.init();
            Object data = Classifier.classify("触电有什么症状");
            System.out.println(data);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Trie buildActree(Collection<String> collection) {
        return Trie.builder()
                .addKeywords(collection)
                .build();
    }

    public static boolean checkWord(List<String> words, String question) {
        for (String word : words) {
            if (question.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, List<String>> check_medical(String question) {
        List<String> regionWords = new LinkedList<>();
        Collection<Emit> Emits = regionTree.parseText(question);
        for (Emit emit : Emits) {
            regionWords.add(emit.getKeyword());
        }
        List<String> stopWords = new LinkedList<>();
        for (String wd1 : regionWords) {
            for (String wd2 : regionWords) {
                if (wd2.contains(wd1) && !wd1.equals(wd2)) {
                    stopWords.add(wd1);
                }
            }
        }
        List<String> finalWords = new LinkedList<>();
        for (String word : regionWords) {
            if (!stopWords.contains(word)) {
                finalWords.add(word);
            }
        }
        Map<String, List<String>> map = new HashMap<>();
        for (String finalWord : finalWords) {
            map.put(finalWord, wordTyptDict.get(finalWord));
        }
        return map;
    }
}
