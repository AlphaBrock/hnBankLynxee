package com.sino.hnbank.screen.quartz;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sino.hnbank.screen.MyApplicationRunner;
import com.sino.hnbank.screen.pojo.TransDayData;

import com.sino.hnbank.screen.splunk.SimpleSearch;
import org.apache.tomcat.jni.Time;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedReader;

/**
 * Created by llb on 18/12/28.
 */
@Component
public class ScreenData {

    private static final Logger logger = LoggerFactory.getLogger(ScreenData.class);
    private static int i =0;

    /**
     * SPL配置文件名称
     */
    private static final String CONFIG_NAME = "spl.conf";

    /**
     * SPL信息读取标记
     */
    private static final String SECTION_SPL = "spl";

    /**
     * 分支机构信息读取标记
     */
    private static final String SECTION_BRANCH = "branch";

    /**
     * 运行基础信息
     */
    private static final String SECTION_RUN_INFO = "runInfo";

    /**
     * 读取信息分支机构排序KEY值
     */
    private static final String SECTION_BRANCH_KEY_ORDER = "branchOrder";

    /**
     * 读取信息分支机构编码KEY值
     */
    private static final String SECTION_BRANCH_KEY_CODE = "branchCode";

    /**
     * 读取信息分支机构名称KEY值
     */
    private static final String SECTION_BRANCH_KEY_NAME = "branchName";


    private static final String SECTION_TIME_INTERVAL = "timeInterval";

    /**
     * POJO字段分支机构排序字段名称
     */
    private static final String POJO_FIELD_BRANCH_ORDER = "sortNum";

    /**
     * POJO字段分支机构名称字段名称
     */
    private static final String POJO_FIELD_BRANCH_NAME = "branchName";

    /**
     * 数据存储日期的格式
     */
    private static final String DATA_DATE_FORMAT = "yyyyMMdd";

    /**
     * SPL语句执行间隔
     */
    private static long TIME_INTERVAL = 10L;

    /**
     * json解析对象
     */
    static Gson gson = new Gson();


    /**
     * SPL语句缓存数据
     */
    static List<Profile.Section> splList = new ArrayList<Profile.Section>();

    /**
     * 分支机构编码与排序缓存数据
     */
    static Map<String, Integer> branchOrderMap = new HashMap<String, Integer>();

    /**
     * 分支机构编码和名称缓存数据
     */
    static Map<String, String> branchNameMap = new HashMap<String, String>();

    /**
     * 最终数据结果集,缓存数据,对象类型(TransDayData)
     */
    static Map<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();

    /**
     * 最终数据结果集,缓存数据,list类型
     */
    static Map<String, JsonArray> listJson = new HashMap<String, JsonArray>();

    /**
     * 最终数据结果集,缓存数据,map类型
     */
    public static Map<String, JsonObject> mapJson = new HashMap<String, JsonObject>();

    public static Map<String, Long> allBank = new HashMap<String, Long>();


    /**
     * 初始化数据
     *
     * @param args
     */
    public static void init(String args) {
        Config cfg = new Config();
        // 生成配置文件的URL
        //URL url = Resources.getResource(args);
        File file = new File(args);
        // 设置Section允许出现重复
        cfg.setMultiSection(true);
        Ini ini = new Ini();
        ini.setConfig(cfg);
        try {
            // 加载配置文件
            ini.load(file);
            //读取基本信息
            Profile.Section runInfo = ini.get(SECTION_RUN_INFO);
            TIME_INTERVAL = Long.parseLong(runInfo.get(SECTION_TIME_INTERVAL));
            //分支机构信息
            List<Profile.Section> branchList = ini.getAll(SECTION_BRANCH);
            for (Profile.Section section : branchList) {
                if (null != section.get(SECTION_BRANCH_KEY_ORDER)) {
                    branchOrderMap.put(section.get(SECTION_BRANCH_KEY_CODE), Integer.valueOf(section.get(SECTION_BRANCH_KEY_ORDER)));
                }
                branchNameMap.put(section.get(SECTION_BRANCH_KEY_CODE), section.get(SECTION_BRANCH_KEY_NAME));
            }
            // 读取SPL语句配置
            splList = ini.getAll(SECTION_SPL);
        } catch (Exception e) {
            logger.error("init data error", e);
        }
        ScreenData.setDefaultData();
        logger.info("init data success");
    }

    /**
     * 测试用main函数
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ScreenData d = new ScreenData();
        init(args[0]);
        //第一步:读取各类配置文件
        //各支行信息
        //语句执行间隔
        //如果文件中存在数据,则写入数据,如果数据为空,则补0
        //如果文件不存在则各项数据补0
        ScreenData.run();
        //最后数据写入文件
        System.out.println("========>>>>map:" + map.toString());
        List<TransDayData> list = d.buildBranchAllMetric();
        System.out.println("========>>>>list:" + list.toString());
    }

    /**
     * 按顺序执行SPL
     */
    public static void run() {
        for (Profile.Section section : splList) {
            logger.debug("spl" + " : " + section.get("spl"));
            logger.debug("spl_field" + " : " + section.get("spl_field"));
            logger.debug("pojo_field" + " : " + section.get("pojo_field"));

            logger.info("000000======>>>>>>spl" + " : " + section.get("spl"));
            logger.info("000000======>>>>>>spl_field" + " : " + section.get("spl_field"));
            logger.info("000000======>>>>>>pojo_field" + " : " + section.get("pojo_field"));

            String splField = section.get("spl_field");
            String pojoField = section.get("pojo_field");
            if (splField==null || pojoField==null) {
                logger.error("spl_field and pojo_field cannot be empty string. spla="+section.get("spl"));
                return;
            }

            Map<String, String> pojoMap = buildPojoMap(splField, pojoField);
            Map<String, String> splFieldMap = buildSplFieldMap(splField, pojoField);

            try {
                String[] args = new String[1];
                args[0] = section.get("spl");
                InputStream stream = SimpleSearch.getInstance().search(args);
                //InputStream stream = null;
                if (stream != null) {
                    List<Map<String, String>> splResultList = buidSplResultValue(stream);
                    logger.debug(splResultList.toString());
                    logger.info("000000======>>>>>>splResultList"+splResultList.toString());

                    if ("object".equals(section.get("type"))) {
                        buidObjectData(pojoMap, splFieldMap, splResultList);
                    } else if ("list".equals(section.get("type"))) {
                        buidListData(section.get("key"), splFieldMap, splResultList);
                    } else if ("map".equals(section.get("type"))) {
                        buidMapData(section.get("key"), splFieldMap, splResultList);
                    }
                }
            } catch (Exception e) {
                logger.error("excute run error!", e);
            }
            try {
                Thread.sleep(1000 * TIME_INTERVAL);
            } catch (InterruptedException e) {
                logger.error("excute run error", e);
            }

        }
        //将map写入文件

    }

    /**
     * 构建对象型缓存数据
     *
     * @param pojoMap
     * @param splFieldMap
     * @param splResultList
     * @throws Exception
     */
    private static void buidObjectData(Map<String, String> pojoMap, Map<String, String> splFieldMap, List<Map<String, String>> splResultList) throws Exception {
        //获取数据结束,开始处理数据
        String splKeyField = pojoMap.get("branchCode");
        //如果执行了查询10分钟的他行来往金额数据，则先清空
        if (pojoMap.toString().contains("otherBankOutAmountNew")) {
            Set<Map.Entry<String, HashMap<String, String>>> entrySet = map.entrySet();
            for (Map.Entry<String, HashMap<String, String>> entry : entrySet) {
                entry.getValue().put("otherBankOutAmountNew", "0");
            }
        }
        if (pojoMap.toString().contains("otherBankInAmountNew")) {
            Set<Map.Entry<String, HashMap<String, String>>> entrySet = map.entrySet();
            for (Map.Entry<String, HashMap<String, String>> entry : entrySet) {
                entry.getValue().put("otherBankInAmountNew", "0");
            }
        }
        for (Map<String, String> splResultValueMap : splResultList) {
            String branchCode = splResultValueMap.get(splKeyField);
            if (branchOrderMap.get(branchCode) == null) {
                continue;
            }
            HashMap<String, String> dataMap = map.get(branchCode);
            if (dataMap == null) {
                dataMap = new HashMap<String, String>();
            }
            Iterator<Map.Entry<String, String>> itsplMap = splResultValueMap.entrySet().iterator();
            while (itsplMap.hasNext()) {
                Map.Entry<String, String> entry = itsplMap.next();
                logger.debug("=================>>>>>>key= " + entry.getKey() + " and value= " + entry.getValue());
                dataMap.put(splFieldMap.get(entry.getKey()), entry.getValue());
            }
            dataMap.put(POJO_FIELD_BRANCH_ORDER, branchOrderMap.get(branchCode) == null ? "" : branchOrderMap.get(branchCode).toString());
            dataMap.put(POJO_FIELD_BRANCH_NAME, branchNameMap.get(branchCode));

            logger.debug(branchCode);
            logger.debug(dataMap.toString());
            map.put(branchCode, dataMap);
        }
    }

    /**
     * 构建list缓存数据
     *
     * @param key
     * @param splFieldMap
     * @param splResultList
     * @throws Exception
     */
    private static void buidListData(String key, Map<String, String> splFieldMap, List<Map<String, String>> splResultList) throws Exception {
        JsonArray array = new JsonArray();
        for (Map<String, String> splResultValueMap : splResultList) {
            JsonObject element = new JsonObject();
            Iterator<Map.Entry<String, String>> it = splResultValueMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if (splFieldMap.get(entry.getKey()) != null) {
                    element.addProperty(splFieldMap.get(entry.getKey()), entry.getValue());
                }
            }
            array.add(element);
        }
        listJson.put(key, array);
    }

    /**
     * 构建Map类型缓存数据
     *
     * @param key
     * @param splFieldMap
     * @param splResultList
     * @throws Exception
     */
    private static void buidMapData(String key, Map<String, String> splFieldMap, List<Map<String, String>> splResultList) throws Exception {
        JsonObject object = new JsonObject();
        for (Map<String, String> splResultValueMap : splResultList) {
            Iterator<Map.Entry<String, String>> it = splResultValueMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if (splFieldMap.get(entry.getKey()) != null) {
                    object.addProperty(splFieldMap.get(entry.getKey()), entry.getValue());
                }
            }
        }
        mapJson.put(key, object);
    }

    /**
     * 构建SPL查询数据
     *
     * @throws Exception
     */
    private static List<Map<String, String>> buidSplResultValue(InputStream stream) throws Exception {
        List<Map<String, String>> splResultList = new ArrayList<Map<String, String>>();
        // 创建saxReader对象
        SAXReader reader = new SAXReader();

        /*if(i ==0 || i ==1){
            File file = new File("E:\\Develop\\hnb_screen\\src\\main\\resources\\test.txt");
            stream = new FileInputStream(file);
            i ++;
        }else {
            File file = new File("E:\\Develop\\hnb_screen\\src\\main\\resources\\test2.txt");
            stream = new FileInputStream(file);
        }*/


        /*BufferedInputStream bf = new BufferedInputStream(stream);
        BufferedReader r = new BufferedReader(new InputStreamReader(bf));
        String line;
        while ((line = r.readLine()) != null) {
            logger.warn(line);
        }*/

        // 通过read方法读取一个文件 转换成Document对象
        Document document = reader.read(stream);

        //获取根节点元素对象
        Element node = document.getRootElement();
        //遍历所有的元素节点
        // 当前节点下面子节点迭代器
        Iterator<Element> it = node.elementIterator();
        // 遍历
        while (it.hasNext()) {
            // 获取某个子节点对象
            Element e = it.next();
            //如果是result节点
            if ("result".equals(e.getName())) {
                //新建实体类
                Map<String, String> splResultValueMap = new HashMap<String, String>();
                //遍历子节点
                Iterator<Element> resultIt = e.elementIterator();
                while (resultIt.hasNext()) {
                    //获取子节点
                    Element field = resultIt.next();
                    //将field节点的属性名和节点的内容加入map
                    if ("field".equals(field.getName())) {
                        List<Attribute> attributes = field.attributes();
                        for (Attribute attribute : attributes) {
                            splResultValueMap.put(attribute.getValue(), field.getStringValue().trim());
                        }
                    }
                }
                splResultList.add(splResultValueMap);
            }
        }
        return splResultList;
    }

    /**
     * 构建POJO字段与SPL字段对应关系
     *
     * @param splField
     * @param pojoField
     * @return
     */
    private static Map<String, String> buildPojoMap(String splField, String pojoField) {
        Map<String, String> _map = new HashMap<String, String>();
        String[] s = splField.split(",");
        String[] p = pojoField.split(",");
        for (int i = 0; i < p.length; i++) {
            if (s.length >= p.length) {
                _map.put(p[i], s[i]);
            } else {
                logger.error("字段个数不匹配");
            }
        }
        return _map;
    }

    /**
     * 构建SPL字段与POJO字段对应关系
     *
     * @param splField
     * @param pojoField
     * @return
     */
    private static Map<String, String> buildSplFieldMap(String splField, String pojoField) {
        Map<String, String> _map = new HashMap<String, String>();
        String[] s = splField.split(",");
        String[] p = pojoField.split(",");
        for (int i = 0; i < p.length; i++) {
            if (p.length >= s.length) {
                _map.put(s[i], p[i]);
            } else {
                logger.info("字段个数不匹配");
            }
        }
        return _map;
    }

    /**
     * 把Map按顺序转换为List对象
     *
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public List<TransDayData> buildTransDayDataList() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<TransDayData> list = new ArrayList<TransDayData>();
        Iterator<Map.Entry<String, HashMap<String, String>>> it = map.entrySet().iterator();
        Date t = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(DATA_DATE_FORMAT);
        String dataDate = sdf.format(t);
        while (it.hasNext()) {
            Map.Entry<String, HashMap<String, String>> entry = it.next();
            HashMap _map = entry.getValue();
            TransDayData transDayData = new TransDayData();
            transDayData.setDataDate(dataDate);
            transDayData.setLastUpdateTime(t);
            setFieldValue(transDayData, _map);
            list.add(transDayData);
        }
        return list;
    }


    /**
     * set属性的值到Bean
     *
     * @param bean
     * @param valMap
     */
    private static void setFieldValue(Object bean, Map<String, String> valMap) {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            try {
                String fieldSetName = parSetName(field.getName());
                if (!checkSetMet(methods, fieldSetName)) {
                    continue;
                }
                Method fieldSetMet = cls.getMethod(fieldSetName,
                        field.getType());
//				String fieldKeyName = parKeyName(field.getName());
                String fieldKeyName = field.getName();
                String value = valMap.get(fieldKeyName);
                if (null != value && !"".equals(value)) {
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equals(fieldType)) {
                        fieldSetMet.invoke(bean, value);
                    } else if ("Date".equals(fieldType)) {
                        Date temp = parseDate(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("Integer".equals(fieldType)
                            || "int".equals(fieldType)) {
                        Integer intval = Integer.parseInt(value);
                        fieldSetMet.invoke(bean, intval);
                    } else if ("Long".equalsIgnoreCase(fieldType)) {
                        Long temp = Long.parseLong(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("Double".equalsIgnoreCase(fieldType)) {
                        Double temp = Double.parseDouble(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                        Boolean temp = Boolean.parseBoolean(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
                        BigDecimal temp = new BigDecimal(value);
                        fieldSetMet.invoke(bean, temp);
                    } else {
                        logger.info("not supper type" + fieldType);
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
    }


    /**
     * 判断是否存在某属性的 set方法
     *
     * @param methods
     * @param fieldSetMet
     * @return boolean
     */
    public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
        for (Method met : methods) {
            if (fieldSetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }


    /**
     * 首字母转大写
     *
     * @param s
     * @return
     */
    private String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 格式化string为Date
     *
     * @param datestr
     * @return date
     */
    public static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 对象类型数据,把Map按顺序转换为List对象
     */
    public static void setDefaultData() {
        Class<?> cls = TransDayData.class;
        // 取出bean里的所有方法
        Field[] fields = cls.getDeclaredFields();
        Iterator<Map.Entry<String, String>> it = branchNameMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            HashMap<String, String> _map = new HashMap();

            for (Field field : fields) {
                try {
                    String fieldKeyName = field.getName();
                    if ("id".equals(fieldKeyName)) {
                        continue;
                    }
                    String fieldType = field.getType().getSimpleName();
                    if ("Integer".equals(fieldType)
                            || "int".equals(fieldType)) {
                        _map.put(fieldKeyName, "0");
                    } else if ("Long".equalsIgnoreCase(fieldType)) {
                        _map.put(fieldKeyName, "0");
                    } else if ("Double".equalsIgnoreCase(fieldType)) {
                        _map.put(fieldKeyName, "0");
                    } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                        _map.put(fieldKeyName, "0");
                    } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
                        _map.put(fieldKeyName, "0");
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            _map.put("branchCode", entry.getKey());
            _map.put("branchName", branchNameMap.get(entry.getKey()));
            _map.put("sortNum", branchOrderMap.get(entry.getKey()).toString());
            map.put(entry.getKey(), _map);
        }
        //logger.info(map);
        //凌晨重置数据  by liulb 2020-12-9
        MyApplicationRunner.setDefalut();
        logger.info("======>>>>>>每日凌晨重置数据成功");
    }

    //TODO 定时任务把数据存储到数据库?

    //===========我是分割线=========下面的代码是数据获取的代码===============

    public Map<String, Long> buildCustomerNum() {
        Map<String, Long> customerMap = new HashMap<>();
        long customerToPulicNum = 0;
        long customerToPrivateNum = 0;
        try {
            customerToPulicNum = mapJson.get("customerToPulicNum").get("customerToPulicNum").getAsBigDecimal().longValue();

        } catch (Exception e) {
            logger.info("the customer to public number is empty");
        }
        try {
            customerToPrivateNum = mapJson.get("customerToPrivateNum").get("customerToPrivateNum").getAsBigDecimal().longValue();
        } catch (Exception e) {
            logger.info("the customer to private number is empty");
        }
        customerMap.put("customerToPulicNum", customerToPulicNum);
        customerMap.put("customerToPrivateNum", customerToPrivateNum);
        return customerMap;
    }

    /**
     * 获取全部指标数据
     *
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<TransDayData> buildBranchAllMetric() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<TransDayData> list = buildTransDayDataList();
        Collections.sort(list, new Comparator<TransDayData>() {
            @Override
            public int compare(TransDayData t1, TransDayData t2) {
                int diff = (t1.getSortNum() == null ? 0 : t1.getSortNum()) - (t2.getSortNum() == null ? 0 : t2.getSortNum());
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0; //相等为0
            }
        });
        return list;
    }

    /**
     * 获取开户数TOPN
     *
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<TransDayData> buildBranchAccountOpenNum(int topN) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<TransDayData> list = buildTransDayDataList();
        for (TransDayData transDayData : list) {
            if ("461998".equals(transDayData.getBranchCode())) {
                list.remove(transDayData);
                break;
            }
        }
        Collections.sort(list, new Comparator<TransDayData>() {
            @Override
            public int compare(TransDayData t1, TransDayData t2) {
                int diff = (t2.getAccountOpenNumPri().toBigInteger().intValue() + t2.getAccountOpenNumPub().toBigInteger().intValue()) - (t1.getAccountOpenNumPri().toBigInteger().intValue() + t1.getAccountOpenNumPub().toBigInteger().intValue());
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0; //相等为0
            }
        });
        if (list.size() >= topN) {
            return list.subList(0, topN);
        }
        return list;
    }

    /**
     * 构建业务交易笔数TOPN
     *
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<TransDayData> buildBranchTransCountTotal(int topN) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<TransDayData> list = buildTransDayDataList();
        for (TransDayData transDayData : list) {
            if ("461998".equals(transDayData.getBranchCode())) {
                list.remove(transDayData);
                break;
            }
        }
        Collections.sort(list, new Comparator<TransDayData>() {
            @Override
            public int compare(TransDayData t1, TransDayData t2) {
                int diff = (t2.getTransCountTotal().toBigInteger().intValue()) - (t1.getTransCountTotal().toBigInteger().intValue());
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0; //相等为0
            }
        });
        if (list.size() >= topN) {
            return list.subList(0, topN);
        }
        return list;
    }

    /**
     * 构建全行交易业务金额和业务笔数数据
     *
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String, BigDecimal> buildAllBankMetric() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<String, BigDecimal> _map = new HashMap<String, BigDecimal>();
        //获取交易总额
        BigDecimal totalTransAmount = BigDecimal.valueOf(0);
        //获取总业务笔数
        BigDecimal totalTransCount = BigDecimal.valueOf(0);
        List<TransDayData> list = buildTransDayDataList();
        for (TransDayData transDayData : list) {
            totalTransAmount = totalTransAmount.add(transDayData.getTransInAmount()).add(transDayData.getTransOutAmount());
            totalTransCount = totalTransCount.add(transDayData.getTransCountTotal());
        }
        _map.put("totalTransAmount", totalTransAmount);
        _map.put("totalTransCount", totalTransCount);
        return _map;
    }

    /**
     * 构建与他行往来转出\转入数据
     *
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String, BigDecimal> buildAllBankToOtherBank() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<String, BigDecimal> _map = new HashMap<String, BigDecimal>();
        //获取他行收入总额
        BigDecimal otherBankInAmount = BigDecimal.valueOf(0);
        //获取他行支出总额
        BigDecimal otherBankOutAmount = BigDecimal.valueOf(0);
        List<TransDayData> list = buildTransDayDataList();
        for (TransDayData transDayData : list) {
            otherBankInAmount = otherBankInAmount.add(transDayData.getOtherBankInAmount());
            otherBankOutAmount = otherBankOutAmount.add(transDayData.getOtherBankOutAmount());
        }
        _map.put("otherBankInAmount", otherBankInAmount);
        _map.put("otherBankOutAmount", otherBankOutAmount);
        return _map;
    }

    /**
     * 根据key返回splunk的表数据（多条）
     *
     * @param key
     * @return
     */
    public JsonArray buildListJson(String key) {
        JsonArray jsonElements = listJson.get(key);
        return jsonElements;
    }

    /**
     * 根据key返回splunk的表数据（只有一条）
     *
     * @param key
     * @return
     */
    public JsonObject buildMapJson(String key) {
        return mapJson.get(key);
    }

}
