package com.payegis.applib.sdk.web.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ctc.wstx.util.DataUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.payegis.applib.sdk.web.common.CommConstants;
import com.payegis.applib.sdk.web.pojo.AttackType;
import com.payegis.applib.sdk.web.pojo.Result;
import com.payegis.applib.sdk.web.pojo.RiskType;
import com.payegis.applib.sdk.web.service.IntegratedQueryService;
import com.payegis.applib.sdk.web.util.DateUtil;
import com.payegis.applib.sdk.web.util.JsonUtil;
import com.payegis.applib.sdk.web.util.MongoDBUtil;
import com.payegis.applib.sdk.web.util.ResultUtil;
import com.payegis.applib.sdk.web.util.StringUtil;

@Service
public class IntegratedQueryServiceImpl implements IntegratedQueryService,CommConstants {

    private static Logger logger = Logger.getLogger(IntegratedQueryServiceImpl.class);

    private static Integer limit = 10;
    
    private @Value("${new.device.deadline}") Integer deadline;

    @Autowired
    MongoDBUtil mongoDBUtil;

    @Override
    public Map<String, Object> getEnvironmentMonitorData(Integer userId ,JSONObject jsonObject) {
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> result = null;
        DBCollection newApp = mongoDBUtil.getDBCollection(ENVIRONMENTAL_SATETY_INSPECTION);
        DBObject query = new BasicDBObject();
        DBObject sort = new BasicDBObject();
        DBObject timeZone = new BasicDBObject();
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        String appVersion = jsonObject.get("appVersion") == null ? null : jsonObject.getString("appVersion");
        String attribution = jsonObject.get("attribution") == null ? null : jsonObject.getString("attribution");
        String riskType = jsonObject.get("riskType") == null ? null : jsonObject.getString("riskType");
        Integer pageNow = jsonObject.getInt("pageNow");
        if(jsonObject.get("appId")!=null&&StringUtils.isNotEmpty(jsonObject.getString("appId"))){
            query.put("appId", jsonObject.getString("appId"));
        }else{
            data.put("pageCount", 0);
            data.put("totalData", 0);
            data.put("pageNow", pageNow);
            data.put("data", new ArrayList<Map<String, Object>>());
            return data;
        }
        if(StringUtils.isNotEmpty(startTime) && startTime.equals(endTime)){
            query.put("detectionTime", startTime);
            timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00"); 
            timeZone.put("$lte", DateUtil.getCurrentDateString(startTime)+" 23:59:59");
            query.put("detectionTime", timeZone);
        }else{
            if(StringUtils.isNotEmpty(startTime)){
                timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00");  
                query.put("detectionTime", timeZone);  
            }
            if(StringUtils.isNotEmpty(endTime)){
                timeZone.put("$lte", DateUtil.getCurrentDateString(endTime)+" 23:59:59");  
                query.put("detectionTime", timeZone);  
            }
        }
        if(StringUtils.isNotEmpty(appVersion)){
            query.put("appVersion", appVersion);
        }
        if(StringUtils.isNotEmpty(attribution)){
            query.put("attribution", attribution);
        }
        //组装风险类型数据
        if(StringUtils.isNotEmpty(riskType)){
            query = getRiskType(query,riskType);
        }
        //query.put("userId", userId.toString());
        sort.put("detectionTime", -1);
        Integer skipSize = (pageNow-1)*10;//上一页的最大值
        //不分页情况下查询改用户下的所有
        List<DBObject> results = mongoDBUtil.pageList(newApp, query, sort, skipSize, limit);
        result = formatEnvironmentMonitorResult(userId,results);
        long totalData = newApp.getCount(query);
        data.put("pageCount", totalData%10 == 0 ? totalData/10 : (totalData/10)+1);
        data.put("totalData", totalData);
        data.put("pageNow", pageNow);
        data.put("data", result);
        return data;
    }

    private DBObject getRiskType(DBObject query, String riskType) {
        DBObject riskQuery = new BasicDBObject();
        if("isRoot".equals(riskType)){
            query.put("isRoot", true);
            return query;
        }else if("isEmulator".equals(riskType)){
            query.put("isEmulator", true);
            return query;
        }else if("modifierFrame".equals(riskType)){
            riskQuery.put("$ne", null);
            query.put("modifierFrame", riskQuery);
            riskQuery.put("$exists", true);
            return query;
        }else if("virusApps".equals(riskType)){
            riskQuery.put("$ne", null);
            query.put("virusApps", riskQuery);
            riskQuery.put("$exists", true);
            return query;
        }else if("piracyApps".equals(riskType)){
            riskQuery.put("$ne", null);
            query.put("piracyApps", riskQuery);
            riskQuery.put("$exists", true);
            return query;
        }else if("unknownApps".equals(riskType)){
            riskQuery.put("$ne", null);
            riskQuery.put("$exists", true);
            query.put("unknownApps", riskQuery);
            return query;
        }else if("cheatApps".equals(riskType)){
            riskQuery.put("$ne", null);
            riskQuery.put("$exists", true);
            query.put("cheatApps", riskQuery);
            return query;
        }else if("hostCheat".equals(riskType)){
            query.put("hostCheat", true);
            return query;
        }
        else if("locationCheat".equals(riskType)){
            query.put("locationCheat", true);
            return query;
        }
        else if("otherApps".equals(riskType)){
            riskQuery.put("$ne", "");
            riskQuery.put("$exists", true);
            query.put("otherApps", riskQuery);
            return query;
        }
        return query;
    }

    private List<Map<String, Object>> formatEnvironmentMonitorResult(Integer userId, List<DBObject> data) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (DBObject dbObj : data) {
            try {
                Map<String, Object>  map = new HashMap<String, Object>();
                List<String> riskTypes = new ArrayList<String>();
                map.put("detectionTime", dbObj.get("detectionTime") == null ? "" : (String) dbObj.get("detectionTime"));
                map.put("phoneNumber", dbObj.get("phoneNumber") == null ? "" : (String) dbObj.get("phoneNumber"));
                map.put("deviceId", dbObj.get("deviceId") == null ? "" : (String) dbObj.get("deviceId"));
                map.put("ip", dbObj.get("ip") == null ? "" : (String) dbObj.get("ip"));
                map.put("isRoot",dbObj.get("isRoot").equals(true) ? "Y" : "N");
                map.put("isEmulator", dbObj.get("isEmulator").equals(true)? "Y" : "N");
                map.put("modifierFrame", dbObj.get("modifierFrame") == null ? "N" : "Y");
                map.put("virusApps", StringUtil.isEmpty(dbObj.get("virusApps")) ? "N" : "Y");
                map.put("piracyApps", StringUtil.isEmpty(dbObj.get("piracyApps")) ? "N" : "Y");
                map.put("unknownApps", StringUtil.isEmpty(dbObj.get("unknownApps")) ? "N" : "Y");
                map.put("cheatApps", StringUtil.isEmpty(dbObj.get("cheatApps")) ? "N" : "Y");
                map.put("appVersion", dbObj.get("appVersion") == null ? "" : (String) dbObj.get("appVersion"));
                map.put("attribution", dbObj.get("attribution") == null ? "未知" : (String) dbObj.get("attribution"));
                //组装环境类型数据
                if(dbObj.get("isRoot") != null && dbObj.get("isRoot").equals(true)){
                    riskTypes.add(RiskType.getNameByValue("isRoot"));
                }
                if(dbObj.get("isEmulator") != null && dbObj.get("isEmulator").equals(true)){
                    riskTypes.add(RiskType.getNameByValue("isEmulator"));
                }
                if(dbObj.get("locationCheat") != null &&dbObj.get("locationCheat").equals(true)){
                    riskTypes.add(RiskType.getNameByValue("locationCheat"));
                }
                if(dbObj.get("hostCheat") != null && dbObj.get("hostCheat").equals(true)){
                    riskTypes.add(RiskType.getNameByValue("hostCheat"));
                }
                if(StringUtil.isNotEmpty(dbObj.get("modifierFrame"))){
                    riskTypes.add(RiskType.getNameByValue("modifierFrame"));
                }
                if(StringUtil.isNotEmpty(dbObj.get("virusApps"))){
                    riskTypes.add(RiskType.getNameByValue("virusApps"));
                }
                if(StringUtil.isNotEmpty(dbObj.get("piracyApps"))){
                    riskTypes.add(RiskType.getNameByValue("piracyApps"));
                }
                if(StringUtil.isNotEmpty(dbObj.get("unknownApps"))){
                    riskTypes.add(RiskType.getNameByValue("unknownApps"));
                }
                if(StringUtil.isNotEmpty(dbObj.get("cheatApps"))){
                    riskTypes.add(RiskType.getNameByValue("cheatApps"));
                }
                if(StringUtil.isNotEmpty(dbObj.get("otherApps"))){
                    riskTypes.add(RiskType.getNameByValue("otherApps"));
                }
                riskTypes.removeAll(Collections.singleton(null));
                map.put("riskTypes",riskTypes);
                ObjectId _id = (ObjectId) dbObj.get("_id");
                map.put("_id", _id.toString());
                result.add(map);
            } catch (Exception e) {
                logger.error("format result error !",e);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getAttackTypeData(Integer userId,JSONObject jsonObject)  {
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> result = null;
        DBCollection newApp = mongoDBUtil.getDBCollection(ATTACK_INFO);
        DBObject query = new BasicDBObject();
        DBObject sort = new BasicDBObject();
        DBObject timeZone = new BasicDBObject();
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime"); 
        String appVersion = jsonObject.get("appVersion") == null ? null : jsonObject.getString("appVersion");
        String attribution = jsonObject.get("attribution") == null ? null : jsonObject.getString("attribution");
        String attackType = jsonObject.get("attackType") == null ? null : jsonObject.getString("attackType");
        Integer pageNow = jsonObject.getInt("pageNow");
        if(jsonObject.get("appId")!=null&&StringUtils.isNotEmpty(jsonObject.getString("appId"))){
            query.put("appId", jsonObject.getString("appId"));
        }else{
            data.put("pageCount", 0);
            data.put("totalData", 0);
            data.put("pageNow", pageNow);
            data.put("data", new ArrayList<Map<String, Object>>());
            return data;
        }
        if(StringUtils.isNotEmpty(startTime) && startTime.equals(endTime)){
            query.put("attackTime", startTime);
            timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00"); 
            timeZone.put("$lte", DateUtil.getCurrentDateString(startTime)+" 23:59:59");
            query.put("attackTime", timeZone);
        }else{
            if(StringUtils.isNotEmpty(startTime)){
                timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00");  
                query.put("attackTime", timeZone);  
            }
            if(StringUtils.isNotEmpty(endTime)){
                timeZone.put("$lte", DateUtil.getCurrentDateString(endTime)+" 23:59:59");  
                query.put("attackTime", timeZone);  
            }
        }
        if(StringUtils.isNotEmpty(appVersion)){
            query.put("appVersion", appVersion);
        }
        if(StringUtils.isNotEmpty(attribution)){
            query.put("attribution", attribution);
        }
        //组装攻击类型
        if(StringUtils.isNotEmpty(attackType)){
            query.put("attackType", attackType);
        }
        sort.put("attackTime", -1);
        Integer skipSize = (pageNow-1)*10;//上一页的最大值
        List<DBObject> results = mongoDBUtil.pageList(newApp, query, sort, skipSize, limit);
        List<DBObject> flagList = mongoDBUtil.pageList(newApp, query, 1000, "_id", null);
        result = formatAttackTypeResult(userId,results);
        long totalData = flagList.size() > 1000 ? 1000 : flagList.size();
        data.put("pageCount", totalData%10 == 0 ? totalData/10 : (totalData/10)+1);
        data.put("totalData", totalData);
        data.put("pageNow", pageNow);
        data.put("data", result);
        return data;
    }

    private List<Map<String, Object>> formatAttackTypeResult(Integer userId, List<DBObject> data) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (DBObject dbObj : data) {
            try {
                Map<String, Object>  map = new HashMap<String, Object>();
                map.put("attackTime", dbObj.get("attackTime") == null ? "" : (String) dbObj.get("attackTime"));
                map.put("phoneNumber", dbObj.get("phoneNumber") == null ? "" : (String) dbObj.get("phoneNumber"));
                map.put("deviceId", dbObj.get("deviceId") == null ? "" : (String) dbObj.get("deviceId"));
                map.put("ip", dbObj.get("ip") == null ? "" : (String) dbObj.get("ip"));
                map.put("attribution", dbObj.get("attribution") == null ? "未知" : (String) dbObj.get("attribution"));
                map.put("operatingSystem", dbObj.get("operatingSystem") == null ? "" : (String) dbObj.get("operatingSystem"));
                map.put("appName", dbObj.get("appName") == null ? "" : (String) dbObj.get("appName"));
                map.put("appVersion", dbObj.get("appVersion") == null ? "" : (String) dbObj.get("appVersion"));
                map.put("systemVersion", dbObj.get("systemVersion") == null ? "" : (String) dbObj.get("systemVersion"));
                map.put("attackType", dbObj.get("attackType") == null ? "" : AttackType.getNameByValue((String) dbObj.get("attackType")));
                map.put("model", dbObj.get("model") == null ? "" : (String) dbObj.get("model"));
                ObjectId _id = (ObjectId) dbObj.get("_id");
                map.put("_id", _id.toString());
                result.add(map);
            } catch (Exception e) {
                logger.error("format result error !",e);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getAttackTypeData(Integer userId) {
        Map<String, Object> data = new HashMap<String, Object>();
        List<String> appNames = new ArrayList<String>();
        DBCollection newApp = mongoDBUtil.getDBCollection(ATTACK_INFO);
        //聚合查询统计appname;
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        appNames = newApp.distinct("appName",query);
        data.put("appNames", appNames);
        return data;
    }

    @Override
    public Map<String, Object> getEnvironmentMonitorData(Integer userId) {
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> result = null;
        DBCollection newApp = mongoDBUtil.getDBCollection(ENVIRONMENTAL_SATETY_INSPECTION);
        DBObject query = new BasicDBObject();
        DBObject sort = new BasicDBObject();
        query.put("userId", userId);
        sort.put("detectionTime", -1);
        //首次进入跳转获取第一页的数据
        List<DBObject> results = mongoDBUtil.pageList(newApp, query, sort, 0, limit);
        List<DBObject> flagList = mongoDBUtil.pageList(newApp, query, 1000, "_id", null);
        result = formatEnvironmentMonitorResult(userId,results);
        long totalData = flagList.size() > 1000 ? 1000 : flagList.size();
        data.put("pageCount", totalData%10 == 0 ? totalData/10 : (totalData/10)+1);
        data.put("totalData", totalData);
        data.put("data", result);
        return data;
    }

    @Override
    public Map<String, Object> getDetectionDetail(String _id) {
        Map<String, Object> result = new HashMap<String, Object>();
        ObjectId objectId = new ObjectId(_id);
        DBCollection newApp = mongoDBUtil.getDBCollection(ENVIRONMENTAL_SATETY_INSPECTION);
        DBObject query = new BasicDBObject();
        query.put("_id", objectId);
        DBObject data = mongoDBUtil.getDocumentByQuery(newApp, query);
        for (String key : data.keySet()) {
            try {
                if("location".equals(key)){
                    result.put(key,parseLoaction(data.get(key)));
                }else{
                    result.put(key, ("isRoot".equals(key)) ? ((boolean)data.get(key) ? "Y": "N"): ("isEmulator".equals(key)) ? ((boolean)data.get(key) ? "Y": "N"): ("hostCheat".equals(key)) ? ((boolean)data.get(key) ? "Y": "N"):("locationCheat".equals(key)) ? ((boolean)data.get(key) ? "Y": "N"): data.get(key));
                }
            } catch (Exception e) {
                logger.info("parse result error!",e);
            }
        }
        String deviceId = (String) data.get("deviceId");
        String appId = (String) data.get("appId");
        String userId = (String) data.get("userId");
        //DBCollection attackInfo = mongoDBUtil.getDBCollection(ATTACK_INFO);
        DBObject attackQuery = new BasicDBObject();
        attackQuery.put("deviceId", deviceId);
        attackQuery.put("appId", appId);
        // attackQuery.put("userId", userId);
        /*DBObject attackData = mongoDBUtil.getDocumentByQuery(attackInfo, attackQuery);
        if(null != attackData){
            result.put("appName",StringUtil.isEmpty(attackData.get("appName")) ? "" : attackData.get("appName"));
            result.put("packageName",StringUtil.isEmpty(attackData.get("packageName")) ? "" : attackData.get("packageName"));
            result.put("appMd5",StringUtil.isEmpty(attackData.get("appMd5")) ? "" : attackData.get("appMd5"));
            result.put("installTime",StringUtil.isEmpty(attackData.get("installTime")) ? "" : attackData.get("installTime"));
            result.put("updateTime",StringUtil.isEmpty(attackData.get("updateTime")) ? "" : attackData.get("updateTime"));
            result.put("attackTime",StringUtil.isEmpty(attackData.get("attackTime")) ? "" : attackData.get("attackTime"));
            result.put("issuer", StringUtil.isEmpty(attackData.get("issuer")) ? "" : attackData.get("issuer"));
            result.put("signName", StringUtil.isEmpty(attackData.get("signName")) ? "" : attackData.get("signName"));
        }*/
        
        DBCollection startInfo = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_NORMAL);
        DBObject startData = mongoDBUtil.getDocumentByQuery(startInfo, attackQuery);
        if(null != startData){
            result.put("startTime",startData.get("startTime") == null ? "" : startData.get("startTime"));
        }
        return result;
    }

    /**
     * description 处理经纬度
     * @param object
     * @return
     */
    private String parseLoaction(Object object) {
        if (StringUtils.isBlank((String) object)) {
            return "";
        }
        try {
            String[] data = StringUtils.trim((String) object).split("\\n");
            String[] result = new String[2];
            int index = 0;
            for (String str : data) {
                String[] arr = str.split("：");
                BigDecimal bd = new BigDecimal(Double.parseDouble(arr[1]));
                bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP);
                System.out.println(bd);
                result[index] = arr[0] + "：" + (bd.toString());
                index++;
            }
            return result[0] + "\n" + result[1];
        } catch (Exception e) {
            return object.toString();
        }

    }

    @Override
    public Map<String, Object> getAttackDetail(String _id) {
        Map<String, Object> result = new HashMap<String, Object>();
        ObjectId objectId = new ObjectId(_id);
        DBCollection newApp = mongoDBUtil.getDBCollection(ATTACK_INFO);
        DBObject query = new BasicDBObject();
        query.put("_id", objectId);
        DBObject data = mongoDBUtil.getDocumentByQuery(newApp, query);
        if(data == null){
            return result;
        }
        for (String key : data.keySet()) {
            if("location".equals(key)){
                result.put(key,parseLoaction(data.get(key)));
            }else{
                result.put(key, ("attackType".equals(key)) ? AttackType.getNameByValue((String)data.get(key)): data.get(key));  
            }
        }
        String deviceId = (String) data.get("deviceId");
        String appId = (String) data.get("appId");
        String userId = (String) data.get("userId");
        DBCollection envInfo = mongoDBUtil.getDBCollection(ENVIRONMENTAL_SATETY_INSPECTION);
        DBObject envQuery = new BasicDBObject();
        envQuery.put("deviceId", deviceId);
        envQuery.put("appId", appId);
        envQuery.put("userId", userId);
        DBObject envData = mongoDBUtil.getDocumentByQuery(envInfo, envQuery);
        if(null != envData){
            result.put("isRoot",envData.get("isRoot").equals(true) ? "Y" : "N");
            result.put("isEmulator", envData.get("isEmulator").equals(true)? "Y" : "N");
            result.put("modifierFrame", envData.get("modifierFrame") == null ? "N" : "Y");
            result.put("virusApps", StringUtil.isEmpty(envData.get("virusApps")) ? "" : envData.get("virusApps").toString().replace(",", "&nbsp;&nbsp;&nbsp;&nbsp;"));
            result.put("piracyApps", StringUtil.isEmpty(envData.get("piracyApps")) ? "" : envData.get("piracyApps").toString().replace(",", "&nbsp;&nbsp;&nbsp;&nbsp;"));
            result.put("unknownApps", StringUtil.isEmpty(envData.get("unknownApps")) ? "" : envData.get("unknownApps").toString().replace(",", "&nbsp;&nbsp;&nbsp;&nbsp;"));
            result.put("cheatApps", StringUtil.isEmpty(envData.get("cheatApps")) ? "" : envData.get("cheatApps").toString().replace(",", "&nbsp;&nbsp;&nbsp;&nbsp;"));
//          result.put("issuer", StringUtil.isEmpty(envData.get("issuer")) ? "" : envData.get("issuer"));
//          result.put("signName", StringUtil.isEmpty(envData.get("signName")) ? "" : envData.get("signName"));
        }
        
        DBCollection startInfo = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_NORMAL);
        DBObject startData = mongoDBUtil.getDocumentByQuery(startInfo, envQuery);
        if(null != startData){
            result.put("startTime",startData.get("startTime") == null ? "" : startData.get("startTime"));
        }

        return result;
    }

    @Override
    public Map<String, Object> getOperationData(Integer userId,
            JSONObject jsonObject) {
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> result = null;
        DBCollection newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_NORMAL);
        DBObject query = new BasicDBObject();
        DBObject sort = new BasicDBObject();
        DBObject count = new BasicDBObject();
        DBObject timeZone = new BasicDBObject();
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        String appVersion = jsonObject.getString("appVersion");
        String attribution = jsonObject.getString("attribution");
        String isNewDevice = jsonObject.getString("isNewDevice");
        Integer pageNow = jsonObject.getInt("pageNow");
        if(jsonObject.get("appId")!=null&&StringUtils.isNotEmpty(jsonObject.getString("appId"))){
            query.put("appId", jsonObject.getString("appId"));
        }else{
            data.put("pageCount", 0);
            data.put("totalData", 0);
            data.put("pageNow", pageNow);
            data.put("data", new ArrayList<Map<String, Object>>());
            return data;
        }
        if(StringUtils.isNotEmpty(startTime) && startTime.equals(endTime)){
            query.put("startTime", startTime);
            timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00"); 
            timeZone.put("$lte", DateUtil.getCurrentDateString(startTime)+" 23:59:59");
            query.put("startTime", timeZone);
        }else{
            if(StringUtils.isNotEmpty(startTime)){
                timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00");  
                query.put("startTime", timeZone);  
            }
            if(StringUtils.isNotEmpty(endTime)){
                timeZone.put("$lte", DateUtil.getCurrentDateString(endTime)+" 23:59:59");  
                query.put("startTime", timeZone);  
            }
        }
        if(StringUtils.isNotEmpty(appVersion)){
            query.put("appVersion", appVersion);
        }
        if(StringUtils.isNotEmpty(attribution)){
            query.put("attribution", attribution);
        }
        if("Y".equalsIgnoreCase(isNewDevice)){
            DBObject deadlineTime = new BasicDBObject();
            String time = DateUtil.getCurrentDateString().split(" ")[0]+" 00:00:00";
            deadlineTime.put("$gte", time);  
            query.put("deviceCreateTime", deadlineTime);  
        }
        DBObject timeObj = new BasicDBObject();
        timeObj.put("$gte", 0);
        //      query.put("userId", userId.toString());
        query.put("runningTime", timeObj);
        //      count.put("userId", userId.toString());
        sort.put("startTime", -1);
        Integer skipSize = (pageNow-1)*10;//上一页的最大值
        List<DBObject> results = mongoDBUtil.pageList(newApp, query, sort, skipSize, limit);
        List<DBObject> flagList = mongoDBUtil.pageList(newApp, query, 1000, "_id", null);
        result = formatCollapseAndRunningResult(userId,results);
        long totalData = flagList.size() > 1000 ? 1000 : flagList.size();
        data.put("pageCount", totalData%10 == 0 ? totalData/10 : (totalData/10)+1);
        data.put("totalData", totalData);
        data.put("pageNow", pageNow);
        data.put("data", result);
        return data;
    }

    @Override
    public Map<String, Object> getCollapseData(Integer userId,
            JSONObject jsonObject) {
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> result = null;
        DBCollection newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_COLLAPSE);
        DBObject query = new BasicDBObject();
        DBObject sort = new BasicDBObject();
        DBObject count = new BasicDBObject();
        DBObject timeZone = new BasicDBObject();
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime"); 
        String appVersion = jsonObject.getString("appVersion");
        String attribution = jsonObject.getString("attribution");
        Integer pageNow = jsonObject.getInt("pageNow");
        if(jsonObject.get("appId")!=null&&StringUtils.isNotEmpty(jsonObject.getString("appId"))){
            query.put("appId", jsonObject.getString("appId"));
        }else{
            data.put("pageCount", 0);
            data.put("totalData", 0);
            data.put("pageNow", pageNow);
            data.put("data", new ArrayList<Map<String, Object>>());
            return data;
        }
        if(StringUtils.isNotEmpty(startTime) && startTime.equals(endTime)){
            query.put("collapseTime", startTime);
            timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00"); 
            timeZone.put("$lte", DateUtil.getCurrentDateString(startTime)+" 23:59:59");
            query.put("collapseTime", timeZone);
        }else{
            if(StringUtils.isNotEmpty(startTime)){
                timeZone.put("$gte", DateUtil.getCurrentDateString(startTime)+" 00:00:00");  
                query.put("collapseTime", timeZone);  
            }
            if(StringUtils.isNotEmpty(endTime)){
                timeZone.put("$lte", DateUtil.getCurrentDateString(endTime)+" 23:59:59");  
                query.put("collapseTime", timeZone);  
            }
        }
        if(StringUtils.isNotEmpty(appVersion)){
            query.put("appVersion", appVersion);
        }
        if(StringUtils.isNotEmpty(attribution)){
            query.put("attribution", attribution);
        }
        sort.put("collapseTime", -1);
        Integer skipSize = (pageNow-1)*10;//上一页的最大值
        List<DBObject> results = mongoDBUtil.pageList(newApp, query, sort, skipSize, limit);
        List<DBObject> flagList = mongoDBUtil.pageList(newApp, query, 1000, "_id", null);
        result = formatCollapseAndRunningResult(userId,results);
        long totalData = flagList.size() > 1000 ? 1000 : flagList.size();
        data.put("pageCount", totalData%10 == 0 ? totalData/10 : (totalData/10)+1);
        data.put("totalData", totalData);
        data.put("pageNow", pageNow);
        data.put("data", result);
        return data;
    }

    private List<Map<String, Object>> formatCollapseAndRunningResult(Integer userId,
            List<DBObject> data) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (DBObject dbObj : data) {
            try {
                Map<String, Object>  map = new HashMap<String, Object>();
                map.put("collapseTime", dbObj.get("collapseTime") == null ? "" : (String) dbObj.get("collapseTime"));
                map.put("startTime", dbObj.get("startTime") == null ? "" : (String) dbObj.get("startTime"));
                map.put("runningTime", dbObj.get("runningTime") == null ? "" : String.valueOf((Long) dbObj.get("runningTime")));
                map.put("deviceId", dbObj.get("deviceId") == null ? "" : (String) dbObj.get("deviceId"));
                map.put("ip", dbObj.get("ip") == null ? "" : (String) dbObj.get("ip"));
                map.put("attribution", dbObj.get("attribution") == null ? "未知" : (String) dbObj.get("attribution"));
                map.put("mobileBrand", dbObj.get("mobileBrand") == null ? "" : (String) dbObj.get("mobileBrand"));
                map.put("systemVersion", dbObj.get("systemVersion") == null ? "" : (String) dbObj.get("systemVersion"));
                map.put("operatingSystem", dbObj.get("operatingSystem") == null ? "" : (String) dbObj.get("operatingSystem"));
                map.put("appVersion", dbObj.get("appVersion") == null ? "" : (String) dbObj.get("appVersion"));
                map.put("model", dbObj.get("model") == null ? "" : (String) dbObj.get("model"));
                String createTime = (dbObj.get("deviceCreateTime") == null ? "" : ((String) dbObj.get("deviceCreateTime")).split(" ")[0]);
                String time = DateUtil.getBeforeDate("yyyy-MM-dd", deadline);
                if(createTime.compareTo(time) >0){
                    map.put("isNewDevice","Y");
                }else{
                    map.put("isNewDevice","N");
                }
                ObjectId _id = (ObjectId) dbObj.get("_id");
                map.put("_id", _id.toString());
                result.add(map);
            } catch (Exception e) {
                logger.error("format result error !",e);
            }
        }
        return result;
    }

    @Override
    public String getCollapseLog(String _id) {
        DBCollection newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_COLLAPSE);
        DBObject query = new BasicDBObject();
        ObjectId objectId = new ObjectId(_id);
        query.put("_id", objectId);
        DBObject data = mongoDBUtil.getDocumentByQuery(newApp, query);
        String collapseLog = (String) data.get("collapseLog");
        return collapseLog ;
    }

    @Override
    public Result getLocationAndVersionData(JSONObject jsonObject,String source) {
        Map<String, Object> data = new HashMap<String, Object>();
        DBCollection newApp = null;
        List<String> appVersions = new ArrayList<String>();
        DBObject distinctQuery = new BasicDBObject();
        distinctQuery.put("appId", jsonObject.getString("appId"));
        //通过appId去重系统版本号
        if(CommConstants.ATTACK.equalsIgnoreCase(source)){
            newApp = mongoDBUtil.getDBCollection(ATTACK_INFO);
            appVersions =  (List<String>) mongoDBUtil.distinctByQuery(newApp,"appVersion",distinctQuery);
            if(appVersions.size() != 0){
                data.put("attackTypes", AttackType.getAllTypes());
            }
            data.put("appVersions", appVersions);
        }else if(CommConstants.ENV.equalsIgnoreCase(source)){
            newApp = mongoDBUtil.getDBCollection(ENVIRONMENTAL_SATETY_INSPECTION);
            appVersions =  (List<String>) mongoDBUtil.distinctByQuery(newApp,"appVersion",distinctQuery);
            if(appVersions.size() != 0){
                data.put("riskTypes", RiskType.getAllTypes());
            }
            data.put("appVersions", appVersions);
        }else if(CommConstants.COLLAPSE.equalsIgnoreCase(source)){
            newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_COLLAPSE);
            appVersions =  (List<String>) mongoDBUtil.distinctByQuery(newApp,"appVersion",distinctQuery);
            data.put("appVersions", appVersions);
        }else if(CommConstants.OPERATION.equalsIgnoreCase(source)){
            newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_NORMAL);
            appVersions =  (List<String>) mongoDBUtil.distinctByQuery(newApp,"appVersion",distinctQuery);
            data.put("appVersions", appVersions);
        }
        
        //通过appId去重归属地信息
        List<String> attributions =  (List<String>) mongoDBUtil.distinctByQuery(newApp,"attribution",distinctQuery);
        attributions.removeAll(Collections.singleton(null));
        attributions.removeAll(Collections.singleton(""));
        if(attributions.size() == 0 && appVersions.size() != 0){
            attributions.add("unKnow");
        }
        data.put("attributions", attributions);
        return ResultUtil.success(data);
    }

    @Override
    public Map<String, Object> getCollapseAndOperationDetail(String _id,String source) {
        Map<String, Object> result = new HashMap<String, Object>();
        ObjectId objectId = new ObjectId(_id);
        DBCollection newApp;
        if(CommConstants.COLLAPSE.equalsIgnoreCase(source)){
            newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_COLLAPSE);
        }else{
            newApp = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_NORMAL);
        }
        DBObject query = new BasicDBObject();
        query.put("_id", objectId);
        DBObject data = mongoDBUtil.getDocumentByQuery(newApp, query);
        if(null != data){
            for (String key : data.keySet()) {
                try {
                    result.put(key,data.get(key));
                } catch (Exception e) {
                    logger.info("parse result error!",e);
                }
            }
        }
        String deviceId = (String) data.get("deviceId");
        String appId = (String) data.get("appId");
        String userId = (String) data.get("userId");
        DBCollection envInfo = mongoDBUtil.getDBCollection(ENVIRONMENTAL_SATETY_INSPECTION);
        DBObject envQuery = new BasicDBObject();
        envQuery.put("deviceId", deviceId);
        envQuery.put("appId", appId);
        envQuery.put("userId", userId);
        DBObject envData = mongoDBUtil.getDocumentByQuery(envInfo, envQuery);
        if(null != envData){
            for (String key : envData.keySet()) {
                try {
                    if("appMd5".equals(key) || "signName".equals(key) || "issuer".equals(key)
                             || "installTime".equals(key) || "updateTime".equals(key) 
                             || "startTime".equals(key) ||  "appVersion".equals(key)
                             || "appName".equals(key) || "ip".equals(key)){
                        continue;
                    }
                    if("location".equals(key)){
                        result.put(key,parseLoaction(envData.get(key)));
                    }else{
                        result.put(key,envData.get(key));
                    }
                } catch (Exception e) {
                    logger.info("parse result error!",e);
                }
            }
        }
        if(CommConstants.COLLAPSE.equalsIgnoreCase(source)){
            DBCollection startInfo = mongoDBUtil.getDBCollection(MONGO_DB_PROGRAM_RUNNING_NORMAL);
            DBObject startData = mongoDBUtil.getDocumentByQuery(startInfo, envQuery);
            if(null != startData){
                result.put("startTime",startData.get("startTime") == null ? "" : startData.get("startTime"));
            }
        }
        return result;
    }

}
