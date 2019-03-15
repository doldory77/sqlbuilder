package doldory.lib.sql;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings({"unused", "unchecked"})
public class SqlBuilder {

	private static SqlBuilder instance;
	private List<SqlItem> sqlCache;
	public static final int CACHE_SIZE = 10;
	
	public static final String PARAM_GUBUN_CHAR = "#";
	
	public static final String TAG_TEXT = "#text";
	public static final String TAG_NOT_EMPTY = "notEmpty";
	public static final String TAG_COMPARE = "compare";
	public static final String TAG_LIST = "list";
	public static final String TAG_CHOOSE = "choose";
	public static final String TAG_WHEN = "when";
	public static final String ATTR_PROPERTY = "property";
	public static final String ATTR_VALUE = "value";
	
	private static final Pattern pattern;
	
	static {
		pattern = Pattern.compile(PARAM_GUBUN_CHAR.concat("(.+)").concat(PARAM_GUBUN_CHAR));
	}
	
	private SqlBuilder() {
		this.sqlCache = new ArrayList<SqlBuilder.SqlItem>();
	}
	public static SqlBuilder getInstance() {
		if (instance == null) {
			instance = new SqlBuilder();
		}
		return instance;
	}
	
	private Map<String, String> buildParameter(Object obj) throws IllegalArgumentException, IllegalAccessException {
		Map<String, String> params = new HashMap<String, String>();
		String classType;
		String listType = "";
		StringBuilder sb = null;
		if ("java.util.HashMap".equals(obj.getClass().getTypeName())) {
			Map<String, Object> tmp = (HashMap<String, Object>) obj;
			for (Entry<String, Object> elem: tmp.entrySet()) {
				classType = elem.getValue().getClass().getTypeName();
				if ("int".equals(classType) || "java.lang.Integer".equals(classType)) {
					params.put(elem.getKey(), String.valueOf(elem.getValue()));
				} else if ("long".equals(classType) || "java.lang.Long".equals(classType)) {
					params.put(elem.getKey(), String.valueOf(elem.getValue()));
				} else if ("java.lang.String".equals(classType)) {
					params.put(elem.getKey(), Util.qtStr(elem.getValue()));
				} else if ("java.util.List".equals(classType)
						|| "java.util.ArrayList".equals(classType)) {
					
					List<?> list = (List<?>) elem.getValue();
					if (list != null && list.size() > 0) {        			
	        			if ("java.lang.String".equals(list.get(0).getClass().getTypeName())) {
	        				listType = "list_string";
	        			} else if ("int".equals(list.get(0).getClass().getTypeName()) 
	        					|| "java.lang.Integer".equals(list.get(0).getClass().getTypeName())
	        					|| "long".equals(list.get(0).getClass().getTypeName()) 
	        					|| "java.lang.Long".equals(list.get(0).getClass().getTypeName())) {
	        				listType = "list_numeric";
	        			}
	        			
	        			sb = new StringBuilder();
	        			for (int i=0, max=list.size(); i<max; i++) {
	        				if (listType.equals("list_string")) {
	        					if (i == max-1) {
	        						sb.append(SqlBuilder.Util.qtStr(list.get(i)));
	        					} else {
	        						sb.append(SqlBuilder.Util.qtStr(list.get(i)).concat(","));
	        					}
	        				} else if (listType.equals("list_numeric")) {
	        					if (i == max-1) {
	        						sb.append(String.valueOf(list.get(i)));
	        					} else {
	        						sb.append(String.valueOf(list.get(i)).concat(","));
	        					}
	        				}
	        			}
	        			
	        			params.put(elem.getKey(), sb.toString());
	        		}
				}
			}
		} else {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field: fields) {
				classType = field.getType().getCanonicalName();
				field.setAccessible(true);
				if ("int".equals(classType) || "java.lang.Integer".equals(classType)) {
					params.put(field.getName(), String.valueOf(field.get(obj)));
				} else if ("long".equals(classType) || "java.lang.Long".equals(classType)) {
					params.put(field.getName(), String.valueOf(field.get(obj)));
				} else if ("java.lang.String".equals(classType)) {
					params.put(field.getName(), Util.qtStr(field.get(obj)));
				} else if ("java.util.List".equals(classType)
						|| "java.util.ArrayList".equals(classType)) {
				
					List<?> list = (List<?>) field.get(obj);
					if (list != null && list.size() > 0) {        			
	        			if ("java.lang.String".equals(list.get(0).getClass().getTypeName())) {
	        				listType = "list_string";
	        			} else if ("int".equals(list.get(0).getClass().getTypeName()) 
	        					|| "java.lang.Integer".equals(list.get(0).getClass().getTypeName())
	        					|| "long".equals(list.get(0).getClass().getTypeName()) 
	        					|| "java.lang.Long".equals(list.get(0).getClass().getTypeName())) {
	        				listType = "list_numeric";
	        			}
	        			
	        			sb = new StringBuilder();
	        			for (int i=0, max=list.size(); i<max; i++) {
	        				if (listType.equals("list_string")) {
	        					if (i == max-1) {
	        						sb.append(Util.qtStr(list.get(i)));
	        					} else {
	        						sb.append(Util.qtStr(list.get(i)).concat(","));
	        					}
	        				} else if (listType.equals("list_numeric")) {
	        					if (i == max-1) {
	        						sb.append(String.valueOf(list.get(i)));
	        					} else {
	        						sb.append(String.valueOf(list.get(i)).concat(","));
	        					}
	        				}
	        			}
	        			
	        			params.put(field.getName(), sb.toString());
	        		}
				}
			}
		}
		return params;
	}
	
	private String buildHashKey(Object obj) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = buildParameter(obj);
		for (Entry<String, String> elem: params.entrySet()) {
			sb.append(elem.getValue());
		}
		return sb.toString();
	}
	
	private String buildHashKey(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> elem: params.entrySet()) {
			sb.append(elem.getValue());
		}
		return sb.toString();
	}
	
	private String mappingParams(String sqlStr, Map<String, String> params) {
		String resultSql = sqlStr;
		if (params != null && params.size() > 0) {
			Matcher matcher = pattern.matcher(sqlStr);
			while (matcher.find()) {
				String key = matcher.group(1);
				if (!params.containsKey(key)) throw new RuntimeException("field " + PARAM_GUBUN_CHAR + key + PARAM_GUBUN_CHAR +" not mapping");
				resultSql = resultSql.replace(matcher.group(), params.get(key));
			}
		}
		return resultSql;	
	}
	
	private String buildSql(InputStream src, String id, Map<String, String> params) throws SAXException, IOException, ParserConfigurationException {
		StringBuilder sb = new StringBuilder();
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(src);
		Node sqlNode = document.getElementsByTagName("sql").item(0);
		NodeList sqlChildNodes = sqlNode.getChildNodes();
		Node tmpNode;
		NamedNodeMap tmpNodeMap;
		String tmpNodeKey;
		String tmpNodeValue;
		for (int i=0; i<sqlChildNodes.getLength(); i++) {
			tmpNode = sqlChildNodes.item(i);
			if (i == 0) {
				if (id != null) sb.append("/*" + id + "*/\n");
			}
			if (TAG_TEXT.equals(sqlChildNodes.item(i).getNodeName())) {
				sb.append(tmpNode.getNodeValue());
			} else if (TAG_NOT_EMPTY.equalsIgnoreCase(tmpNode.getNodeName())) {
				tmpNodeMap = tmpNode.getAttributes();
				tmpNodeKey = tmpNodeMap.getNamedItem(ATTR_PROPERTY).getNodeValue();
				if (!Util.isNullOrEmpty(params.get(tmpNodeKey))) {
					sb.append(tmpNode.getFirstChild().getNodeValue());
				}
			} else if (TAG_COMPARE.equalsIgnoreCase(tmpNode.getNodeName())) {
				tmpNodeMap = tmpNode.getAttributes();
				tmpNodeKey = tmpNodeMap.getNamedItem(ATTR_PROPERTY).getNodeValue();
				tmpNodeValue = tmpNodeMap.getNamedItem(ATTR_VALUE).getNodeValue();
				
				if (!Util.isNullOrEmpty(params.get(tmpNodeKey)) && tmpNodeValue.equals(Util.splitQt(params.get(tmpNodeKey)))) {
					sb.append(tmpNode.getFirstChild().getNodeValue());
				}
			} else if (TAG_LIST.equalsIgnoreCase(tmpNode.getNodeName())) {
				tmpNodeMap = tmpNode.getAttributes();
				tmpNodeKey = tmpNodeMap.getNamedItem(ATTR_PROPERTY).getNodeValue();
				if (!Util.isNullOrEmpty(params.get(tmpNodeKey))) {
					sb.append(tmpNode.getFirstChild().getNodeValue());
				}
				
			} else if (TAG_CHOOSE.equalsIgnoreCase(tmpNode.getNodeName())) {
				tmpNodeMap = tmpNode.getAttributes();
				tmpNodeKey = tmpNodeMap.getNamedItem(ATTR_PROPERTY).getNodeValue();
				NodeList tmpChildNodes = tmpNode.getChildNodes();
				for (int j=0; j<tmpChildNodes.getLength(); j++) {
					if (TAG_WHEN.equalsIgnoreCase(tmpChildNodes.item(j).getNodeName())) {
						tmpNodeValue = tmpChildNodes.item(j).getAttributes().getNamedItem(ATTR_VALUE).getNodeValue();
						if (!Util.isNullOrEmpty(params.get(tmpNodeKey)) && tmpNodeValue.equals(Util.splitQt(params.get(tmpNodeKey)))) {
							sb.append(tmpChildNodes.item(j).getFirstChild().getNodeValue());
							break;
						}
						
					}
				}
				
			}
		}
		
		return mappingParams(sb.toString(), params);
		
	}
	
	public String getSql(InputStream src, String sqlId, Object params) throws Exception {
		
		SqlItem sqlItem = getSqlItem(src, sqlId, params);
		
		return sqlItem == null ? null : sqlItem.getSql();
		
	}
	
	private SqlItem getSqlItem(InputStream src, String sqlId, Object obj) throws IllegalArgumentException, IllegalAccessException, SAXException, IOException, ParserConfigurationException {
		
		Map<String, String> params = buildParameter(obj);
		String hashKey = buildHashKey(params);
		SqlItem sqlItem = null;
		String sql = null;
		
		synchronized (this) {
			// 케시에 현재 파라미터해시값과 동일한 SQL이 존재하면 useCount값 증가 후 리턴
			for (int i=0; i<sqlCache.size(); i++) {
				if (sqlCache.get(i).getHashKey().contentEquals(hashKey)) {
					sqlItem = sqlCache.get(i);
					sqlItem.useCount += 1; 
					break;
				}
			}
			
			if (sqlItem != null)
				return sqlItem;
			
			
			// 캐시에 없으면 캐시보관 상한값을 체크하여 저장 후 리턴
			sql = buildSql(src, sqlId, params);
			if (Util.isNullOrEmpty(sql))
				throw new RuntimeException("source xml sql not found");
			
			sqlItem = new SqlItem(sql, hashKey);
			if (this.sqlCache.size() >= CACHE_SIZE) {
				int minCount = sqlCache.get(0).getUseCount();
				int minIdx = 0;
				Date currentDate = new Date();
				for (int i=1; i<sqlCache.size(); i++) {
					if (sqlCache.get(i).useCount <= minCount) {
						if (sqlCache.get(i).useCount < minCount) {
							minCount = sqlCache.get(i).useCount;
							minIdx = i;
						} else if (sqlCache.get(i).useCount == minCount) {
							if (sqlCache.get(i).getDate().getTime() < currentDate.getTime()) {
								minCount = sqlCache.get(i).useCount;
								minIdx = i;
							}
						}
					}
				}
				sqlCache.set(minIdx, sqlItem);
			} else {
				sqlCache.add(sqlItem);
				
			}
		}
		
		
		return sqlItem;
	}
	
	public int cacheSize() {
		return sqlCache.size();
	}
	
	public void print() {
		System.out.println("cache size: " + sqlCache.size());
		for (SqlItem sqlItem: sqlCache) {
			System.out.println(sqlItem);
		}
	}
	
	private String regStr(String str) {
		return PARAM_GUBUN_CHAR.concat(str).concat(PARAM_GUBUN_CHAR);
	}
	
	public static class Util {
		public static String qtStr(Object str) {
			return "'".concat(String.valueOf(str)).concat("'");
		}
		
		public static String splitQt(String str) {
			if (str.startsWith("'")) {
				return str.replaceAll("'", "");
			}
			return str;
		}
		
		public static boolean isNullOrEmpty(String str) {
			return str == null || "".equals(str);
		}
	}
	
	
	public static class SqlItem {
		
		public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		
		private String sql;
		private String hashKey;
		private int useCount;
		private Date date;
		
		public SqlItem() {}
		public SqlItem(String sql, String hashkey) {
			this.sql = sql;
			this.hashKey = hashkey;
			this.useCount = 1;
			this.date = new Date();
		}
		
		public String getHashKey() {
			return hashKey;
		}
		public void setHashKey(String hashKey) {
			this.hashKey = hashKey;
		}
		public String getSql() {
			return sql;
		}
		public void setSql(String sql) {
			this.sql = sql;
		}
		public int getUseCount() {
			return useCount;
		}
		public void setUseCount(int useCount) {
			this.useCount = useCount;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		@Override
		public String toString() {
			return "[hashKey: " + hashKey + "][" + useCount + "](" + dateFormatter.format(date) +")\n" + sql; 
		}
	}
	
}
