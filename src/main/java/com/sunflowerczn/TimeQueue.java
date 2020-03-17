package com.sunflowerczn;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import javax.print.Doc;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: mongoToExcle
 * @description:
 * @author: chenzhennan
 * @create: 2020-02-25 17:27
 **/
public class TimeQueue {

    /**
     * 正常数据条数
     */
    private static final Integer NORMAL_RECORDS = 20;
    /**
     * 设置查询时间间隔,默认15分钟
     */
    private static final Integer TIME_LIMIT = 15;

    private Queue<Document> queue = new LinkedBlockingQueue<>();

    private ArrayList<Document> documents = null;

    public ArrayList<Document> getAllDocument() {
        return documents;
    }

    public boolean put(Document doc) {
        Object time = doc.get("time");
        if (time == null) {
            return true;
        }
        //System.out.println("---------------current time--------------"+doc.get("time").toString());
        //1.先取出第一个元素
        if (queue.isEmpty()) {
            queue.add(doc);
            return true;
        }
        Document firstElement = queue.peek();
        String firstTime = firstElement.get("time").toString();

        //System.out.println("---------------firstTime time--------------"+firstTime);
        String nextTime = doc.get("time").toString();
        //2.比较时间是否小于15Min
        if (compareTime(firstTime, nextTime)) {
            queue.add(doc);
        } else {
            if (queue.size() >= NORMAL_RECORDS) {
                //异常数据
                documents = new ArrayList<Document>();
                //队列中所有元素放入List返回
                while (!queue.isEmpty()) {
                    Document element = queue.poll();
                    documents.add(element);
                }

                //System.out.println("---------------find exception data--------------");
                return false;
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //计算doc时间减去15Min=time1
                try {
                    Date currentDate = format.parse(doc.get("time").toString());
                    currentDate.setMinutes(currentDate.getMinutes() - TIME_LIMIT);
                    do {
                        Document element = queue.peek();
                        if (element == null) {
                            break;
                        }

                        Date elementDate = format.parse(element.get("time").toString());
                        if (elementDate.before(currentDate)) {
                            queue.poll();
                            // System.out.println("---------------remove element--------------"+element.get("time").toString());
                        } else {
                            break;
                        }
                    } while (true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //队列中删除时间早于time1的元素
                queue.add(doc);
            }
        }
        return true;
    }

    /**
     * 判断字符串时间是否在15分钟内
     *
     * @param startTimeStamp
     * @param endTimeStamp
     * @return
     */
    public boolean compareTime(String startTimeStamp, String endTimeStamp) {
        boolean initValue = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(startTimeStamp) && StringUtils.isNotBlank(endTimeStamp)) {
            long result = 0;
            try {
                result = (format.parse(endTimeStamp).getTime() - format.parse(startTimeStamp).getTime()) / (1000 * 60);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (result <= TIME_LIMIT) {
                initValue = true;
            }

        }
        return initValue;
    }
}



