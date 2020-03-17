package com.sunflowerczn;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.apache.commons.lang3.StringUtils;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: mongoToExcle
 * @description: MongoToExcle
 * @author: chenzhennan
 * @create: 2020-02-24 11:26
 **/
public class AttrCalc {

    /**
     * 端口号
     */
    private static Integer PORT = 27017;
    /**
     * IP
     */
    private static String IP = "localhost";
    /**
     * 数据库名
     */
    private static String DATABASE = "mydatabaseName";
    /**
     * 文档即集合
     */
    private static String COLLECTION = "DevAttrRecord";

    /**
     * 异常数据详情文件路径
     */
    private static String DETAILS_FILE_PATH = "D:\\details\\";

    /**
     * 数据汇总文件路径
     */
    private static String TOTAL_COUNT_FILE_PATH = "D:\\countData.txt";

    private static AtomicInteger recordCount = new AtomicInteger(0);

    private static AtomicInteger fileCount = new AtomicInteger(0);

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static HashMap<String, TimeQueue> TimeQueueMap = new HashMap<String, TimeQueue>();

    public static void main(String[] args) {
        long start_time = System.currentTimeMillis();
        Date startDate = new Date(start_time);
        System.out.println("程序开始执行时间==> " + formatter.format(startDate));
        try {
            //连接到 mongodb 服务
            MongoClient mongoClient = new MongoClient(IP, PORT);

            // 连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE);
            // 连接文档
            MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION);

            // 获取所有文档
            FindIterable<Document> findIterable = collection.find().sort(Sorts.orderBy(Sorts.ascending("time")));
            long total = collection.count();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            long count = 0;
            while (mongoCursor.hasNext()) {
                count++;
                if (count % 10000 == 0 || total == count) {
                    System.out.println("当前进度:" + count * 100.0f / total + "%");
                }
                Document document = mongoCursor.next();
                final String devId = document.get("devId").toString();
                final String attr = document.get("attr").toString();
                final String key = devId + "_" + attr;
                if (TimeQueueMap.containsKey(key)) {
                    TimeQueue timeQueue = TimeQueueMap.get(key);
                    if (timeQueue.put(document) == false) {
                        final ArrayList<Document> documents = timeQueue.getAllDocument();
                        //数据汇总
                        List<String> time = new ArrayList<>();

                        for (Document it : documents) {
                            time.add(it.get("time").toString());
                        }
                        Collections.sort(time);
                        final String startTime = time.get(0);
                        String endTime = time.get(time.size() - 1);

                        FileWriter fw = null;
                        PrintWriter p = null;
                        try {
                            //如果文件存在，则追加内容；如果文件不存在，则创建文件
                            File f = new File(TOTAL_COUNT_FILE_PATH);
                            fw = new FileWriter(f, true);
                            p = new PrintWriter(fw);
                            p.println(devId + "," + attr + "," + startTime + "," + endTime + "," + documents.size());
                            p.flush();
                            recordCount.incrementAndGet();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fw.flush();
                                p.close();
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //保存异常数据

                        pool.submit(new Runnable() {
                            @Override
                            public void run() {
                                FileWriter fw = null;
                                PrintWriter pw = null;
                                try {
                                    //如果文件存在，则追加内容；如果文件不存在，则创建文件
                                    File f = new File(DETAILS_FILE_PATH + devId + "-" + attr + "-" + startTime.replace(":", "") + ".txt");
                                    fw = new FileWriter(f, true);
                                    fileCount.incrementAndGet();
                                    for (Document item : documents) {
                                        pw = new PrintWriter(fw);
                                        if (f.length() == 0) {
                                            pw.println("devId,attr,houseIeee,time");
                                        }
                                        pw.println(item.get("devId") + "," + item.get("attr") + "," + item.get("houseIeee") + "," + item.get("time"));
                                        fw.flush();
                                    }
                                    //System.out.println("-----");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        pw.close();
                                        fw.close();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }


                            }
                        });


                    }
                } else {
                    TimeQueue timeQueue = new TimeQueue();
                    timeQueue.put(document);
                    TimeQueueMap.put(key, timeQueue);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                break;
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        long end_time = System.currentTimeMillis();
        Date endDate = new Date(end_time);
        long totalTime = end_time - start_time;
        System.out.println("程序执行结束时间==> " + formatter.format(endDate));
        System.out.println("共计耗时:" + totalTime / (1000 * 60 * 1.0) + "分钟");
        System.out.println("异常数量:" + recordCount.get());
        System.out.println("文件数量:" + fileCount.get());
        System.out.println("执行检查:" + (recordCount.get() == fileCount.get()));
    }

}