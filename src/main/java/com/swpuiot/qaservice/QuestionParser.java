package com.swpuiot.qaservice;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by DELL on 2019/4/28.
 */
public class QuestionParser {
    public static void main(String[] args) {
        Map map = new HashMap();
        List list = new LinkedList();
        Map map2 = new HashMap();
        list.add("disease");
        map2.put("触电", list);
        map.put("args", map2);
        List list2 = new LinkedList();
        list2.add("disease_symptom");
        map.put("question_types",list2 );
        List sqlList = QuestionParser.parserMain(map);
        System.out.println(sqlList);
    }

    //    创建实体结点
    public static Map<String, List<String>> buildEntityDict(Map<String, List<String>> args) {
        //args是map，key是string，value是List
        Map<String, List<String>> entityDict = new HashMap<>();
        Set keySet = args.keySet();
        List<String> list = new LinkedList<>();
        int size = keySet.size();
        Collection types = args.values();
        for (int i = 0; i < size; i++) {
            for (Object type : types) {
                list.add((String) keySet.toArray()[i]);
                for (String s : ((List<String>) type)) {
                    entityDict.put(s, list);
                }
            }
        }
        System.out.println(entityDict);
        return entityDict;
    }

    public static List<Map> parserMain(Map<String, Object> resClassify) {
        Map args = (Map) resClassify.get("args");
        Map<String, List<String>> entityDict = buildEntityDict(args);
        List<String> questTypes = (List<String>) resClassify.get("question_types");
        List<Map> sqls = new LinkedList<>();
        for (String questType : questTypes) {
            Map sqlInner = new HashMap();
            sqlInner.put("question_type", questType);
            List sql = new LinkedList();
            if ("disease_symptom".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("symptom_disease".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("symptom"));
            } else if ("disease_cause".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_acompany".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_not_food".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_do_food".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("food_not_disease".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("food"));
            } else if ("disease_drug".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("drug_disease".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("drug"));
            } else if ("disease_check".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("check_disease".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("check"));
            } else if ("disease_prevent".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_lasttime".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_cureway".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_cureprob".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_easyget".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            } else if ("disease_desc".equals(questType)) {
                sql = sql_transfer(questType, entityDict.get("disease"));
            }


            if (sql != null) {
                sqlInner.put("sql", sql);
                sqls.add(sqlInner);
            }
        }
        return sqls;
    }

    public static List sql_transfer(String question_type, List<String> entities) {
        if (entities.size() == 0) {
            return new LinkedList();
        }
        List<String> sql = new LinkedList();
//            查询语句
        if ("disease_cause".equals(question_type)) {
//            查询疾病的原因
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.cause"));
        } else if ("disease_prevent".equals(question_type)) {
//            查询疾病的防御措施
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.prevent"));

        } else if ("disease_lasttime".equals(question_type)) {
//            #查询疾病的持续时间
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.cure_lasttime"));

        } else if ("disease_cureprob".equals(question_type)) {
//            #查询疾病的治愈概率
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.cured_prob"));
        } else if ("disease_cureway".equals(question_type)) {
//            #查询疾病的治疗方式
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.cure_way"));
        } else if ("disease_easyget".equals(question_type)) {
//            #查询疾病的易发人群
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.easy_get"));
        } else if ("disease_desc".equals(question_type)) {
//            #查询疾病的相关介绍
            entities.forEach(s -> sql.add("MATCH (m:Disease) where m.name = " + s + " return m.name, m.desc"));
        } else if ("disease_symptom".equals(question_type)) {
//            #查询疾病有哪些症状
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:has_symptom]->(n:Symptom) where m.name = " + s + " return m.name, r.name, n.name"));
        } else if ("symptom_disease".equals(question_type)) {
//            #查询症状会导致哪些疾病
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:has_symptom]->(n:Symptom) where n.name = " + s + " return m.name, r.name, n.name"));
        } else if ("disease_acompany".equals(question_type)) {
            //todo 测试
//            #查询疾病的并发症
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:acompany_with]->(n:Disease) where m.name = " + s + " return m.name, r.name, n.name"));
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:acompany_with]->(n:Disease) where n.name = " + s + " return m.name, r.name, n.name"));
        } else if ("disease_not_food".equals(question_type)) {
//        #查询疾病的忌口
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:no_eat]->(n:Food) where m.name = " + s + " return m.name, r.name, n.name"));

        } else if ("disease_do_food".equals(question_type)) {
            //todo 测试
//            #查询疾病建议吃的东西
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:do_eat]->(n:Food) where m.name =  " + s + " return m.name, r.name, n.name"));
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:recommand_eat]->(n:Food) where m.name = " + s + " return m.name, r.name, n.name"));
        } else if ("food_not_disease".equals(question_type)) {
//        #已知忌口查疾病
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:no_eat]->(n:Food) where n.name =  " + s + " return m.name, r.name, n.name"));

        } else if ("food_do_disease".equals(question_type)) {
//            #已知推荐查疾病
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:do_eat]->(n:Food) where n.name =  " + s + " return m.name, r.name, n.name"));
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:recommand_eat]->(n:Food) where n.name =  " + s + " return m.name, r.name, n.name"));
        } else if ("disease_drug".equals(question_type)) {
//        #查询疾病常用药品－药品别名记得扩充
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:common_drug]->(n:Drug) where m.name =  " + s + " return m.name, r.name, n.name"));
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:recommand_drug]->(n:Drug) where m.name = " + s + " return m.name, r.name, n.name"));
        } else if ("drug_disease".equals(question_type)) {
//        #已知药品查询能够治疗的疾病
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:common_drug]->(n:Drug) where n.name = " + s + " return m.name, r.name, n.name"));
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:recommand_drug]->(n:Drug) where n.name = " + s + " return m.name, r.name, n.name"));

        } else if ("disease_check".equals(question_type)) {
//        #查询疾病应该进行的检查
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:need_check]->(n:Check) where m.name =" + s + " return m.name, r.name, n.name"));
        } else if ("check_disease".equals(question_type)) {
//            #已知检查查询疾病
            entities.forEach(s -> sql.add("MATCH (m:Disease)-[r:need_check]->(n:Check) where n.name = " + s + " return m.name, r.name, n.name"));

        }
        return sql;
    }
}


