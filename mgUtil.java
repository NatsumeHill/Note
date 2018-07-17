package com.payegis.applib.sdk.web.util;

import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtil {

	private static final Logger logger = LoggerFactory.getLogger(MongoDBUtil.class);

	private static int POOL_SIZE = 2000;


	private DB db = null;


	private MongoDBUtil() {
	}

	public MongoDBUtil(String host, int port, String dbName, final String username, final String password) {
		try {
			MongoClientOptions options = new MongoClientOptions.Builder().connectionsPerHost(POOL_SIZE).build();
			final ServerAddress addr = new ServerAddress(host, port);
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
				MongoClient mongoClient = new MongoClient(addr, options);
				this.db = mongoClient.getDB(dbName);
			} else {
				List<MongoCredential> credentials = new ArrayList<MongoCredential>();
				credentials.add(
						MongoCredential.createCredential(
								username,
								"admin",
								password.toCharArray())
						);
				MongoClient mongoClient = new MongoClient(addr, credentials, options);
				this.db = mongoClient.getDB(dbName);
				this.db.command("ping");
			}


		} catch (Exception e) {
			throw new RuntimeException("Connect mongodb failed.", e);
		}
		logger.info(">>> MongoUtil init successfully.");
	}


	public void insert(DBCollection dbColl, DBObject query) {
		dbColl.insert(query);
	}

	public DBCollection getDBCollection(String collectionName) {
		return getDb().getCollection(collectionName);
	}

	public boolean isExists(DBCollection dbColl, DBObject query) {
		/*DBCursor dbCursor = dbColl.find(query);
        int count = dbCursor.count();
        return count > 0;*/
		return dbColl.getCount(query) > 0;
	}

	public void createIndexs(DBCollection dbcoll, String[] fields) {
		for (String field : fields) {
			DBObject index = new BasicDBObject(field, 1);
			dbcoll.createIndex(index);
		}
	}

	public DBObject getDocumentByQuery(DBCollection dbcoll, DBObject query) {
		return query != null ? dbcoll.findOne(query) : dbcoll.findOne();
	}

	public List<?> distinctByQuery(DBCollection dbColl,String distinct,DBObject query){
		return dbColl.distinct(distinct, query);
	}
	

	public List<DBObject> getDocumentsByQuery(DBCollection dbcoll, DBObject query) {
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = dbcoll.find(query);
		while (cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			list.add(dbObject);
		}
		cursor.close();
		return list;
	}

	public List<DBObject> getAllDocuments(DBCollection dbcoll) {
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = dbcoll.find();
		while (cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			list.add(dbObject);
		}
		cursor.close();
		return list;
	}

	public List<DBObject> getDocuments(DBCollection dbcoll, DBObject query, DBObject sortKey, int limit) {
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = dbcoll.find(query);

		if (sortKey != null) {
			cursor = cursor.sort(sortKey);
		}
		if (limit > 0) {
			cursor = cursor.limit(limit);
		}

		while (cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			list.add(dbObject);
		}
		cursor.close();
		return list;

	}

	/**
	 * 传统skip分页查询
	 * @param dbColl
	 * @param query
	 * @param sort
	 * @param skipSize
	 * @param pageSize
	 * @return
	 */
	public List<DBObject> pageList(DBCollection dbColl,DBObject query,DBObject sort,int skipSize,int pageSize){
		DBCursor limit = null;
		List<DBObject> list = new ArrayList<DBObject>();
		limit = dbColl.find(query).sort(sort).skip(skipSize).limit(pageSize);
		while (limit.hasNext()) {
			list.add(limit.next());
		}
		return list;
	}

	/**
	 * 大数据量数据分页优化
	 * @param dbColl
	 * @param query
	 * @param pageSize
	 * @param key 分页的条件[推荐使用id(效率高)]
	 * @param lastId 上一页最大值
	 * @return
	 */
	 public List<DBObject> pageList(DBCollection dbColl, DBObject query, int pageSize,String key, Object lastId) {
		DBCursor limit = null;
		List<DBObject> list = new ArrayList<DBObject>();
		if (null == lastId) {
			limit = dbColl.find(query).limit(pageSize);
		} else {
			// $gt（查询id大于lastId的数据）
			if(null == query){
				query = new BasicDBObject("_id", new BasicDBObject(QueryOperators.GT, lastId));
			} else {
				query.put("_id", new BasicDBObject(QueryOperators.GT, lastId)); 
			}
			limit = dbColl.find(query).limit(pageSize);
		}

		while (limit.hasNext()) {
			list.add(limit.next());
		}
		return list;
	 }

	 private DB getDb() {
		 return db;
	 }

	 private void setDb(DB db) {
		 this.db = db;
	 }
}
