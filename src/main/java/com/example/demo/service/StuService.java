package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Student;

public interface StuService {
	public List<Student> selectAll();
	public List<Student> selectPage(String page,int pageSize);
	public List<Student> getByXh(String id);
	public List<Student> getByName(String name);
	public List<Student> getBySex(String sex);
	public List<Student> getByClassName(String className);
	public boolean addStu(Student student);
	public boolean updateStu(Student student);
	public boolean deleteStu(String xh);
}
