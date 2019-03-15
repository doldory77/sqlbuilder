package doldory.lib.sql;

import java.util.List;

public class SampleVo {

	private String name;
	private int age;
	private String gender;
	private String email;
	private String tell;
	private String address;
	private String pass;
	private String id;
	private String country;
	private List<String> hobby;
	private List<Integer> favoriteNumber;
	
	public List<Integer> getFavoriteNumber() {
		return favoriteNumber;
	}
	public void setFavoriteNumber(List<Integer> favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}
	public List<String> getHobby() {
		return hobby;
	}
	public void setHobby(List<String> hobby) {
		this.hobby = hobby;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTell() {
		return tell;
	}
	public void setTell(String tell) {
		this.tell = tell;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "SampleVo [name=" + name + ", age=" + age + ", gender=" + gender + ", email=" + email + ", tell=" + tell
				+ ", address=" + address + ", pass=" + pass + ", id=" + id + ", country=" + country + "]";
	}
	
}
