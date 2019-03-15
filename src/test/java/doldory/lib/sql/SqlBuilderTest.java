package doldory.lib.sql;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class SqlBuilderTest {

	SqlBuilder sqlBuilder = null;
	private File file = null;
	SampleVo sample = null;
	SampleVo sample2 = null;
	Map<String, Object> params = null;
	
	@Before
	public void before() {
		file = new File(System.getProperty("user.dir").concat("\\target\\classes\\sample.xml"));
		sqlBuilder = SqlBuilder.getInstance();
		
		sample = new SampleVo();
        sample.setName("doldory");
        sample.setAge(32);
        sample.setEmail("doldory33@gmail.com");
        sample.setGender("M");
        sample.setTell("010-1212-3434");
        sample.setAddress("Incheon Namdongu");
        sample.setPass("12341234");
        sample.setId("doldory");
        sample.setCountry("korean");
        
        sample.setHobby(new ArrayList<String>());
        sample.getHobby().add("game");
        sample.getHobby().add("music");
        sample.getHobby().add("movie");
        
        sample2 = new SampleVo();
        sample2.setAge(32);
        sample2.setName("doldory");
        sample2.setEmail("doldory33@gmail.com");
        sample2.setGender("M");
        sample2.setTell("010-1212-3434");
        sample2.setPass("12341234");
        sample2.setAddress("Incheon Namdongu");
        sample2.setCountry("korean");
        sample2.setId("doldory");
        
        sample2.setFavoriteNumber(new ArrayList<Integer>());
        sample2.getFavoriteNumber().add(7);
        sample2.getFavoriteNumber().add(36);
        sample2.getFavoriteNumber().add(77);
        
        params = new HashMap<String, Object>();
        params.put("name", "doldory");
        params.put("age", 32);
        params.put("email", "doldory33@gmail.com");
        params.put("gender", "F");
        params.put("tel", "010-1212-3434");
        params.put("address", "Incheon Namdongu");
        params.put("id", "doldory");
		
	}
	
	@Test
	public void getSql() {
		try {
			String sql = null;
			sql = sqlBuilder.getSql(new FileInputStream(file), "SELECT USER1", params);
			sql = sqlBuilder.getSql(new FileInputStream(file), "SELECT USER2", sample);
			sql = sqlBuilder.getSql(new FileInputStream(file), "SELECT USER3", sample2);	
			sqlBuilder.print();
			assertEquals(3, sqlBuilder.cacheSize());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
