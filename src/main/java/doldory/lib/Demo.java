package doldory.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Demo {

	public static void main(String[] args) {
		Map<String, Object> params2 = null;
		params2 = new HashMap<String, Object>();
		params2.put("s_faci_task_dvsn_cd", "FF");
		params2.put("s_plan_start_dt", "20181213");
		params2.put("s_plan_end_dt", "20181213");
		params2.put("s_insp_dvsn_cd", "04");
		params2.put("s_insp_stn_cd", "0101");
		params2.put("s_enfc_cmp_cd", "");
		params2.put("s_insp_kind_cd", "0402");
		
		File file = new File(System.getProperty("user.dir").concat("\\src\\main\\resources\\sample2.xml"));
		SqlBuilder sqlBuilder = SqlBuilder.getInstance();
		try {
			sqlBuilder.getSql(new FileInputStream(file), "SELECT USER1", params2);
			sqlBuilder.print();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
