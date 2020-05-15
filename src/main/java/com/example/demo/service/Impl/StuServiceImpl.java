package com.example.demo.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entity.Student;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.service.StuService;
import com.github.pagehelper.PageHelper;

@Service
@Transactional
public class StuServiceImpl implements StuService {
	@Autowired
	private StudentMapper studentDao;
	@Autowired//pageHelper
	private PageHelper pageHelper;
	@Override
	public List<Student> selectPage(String page,int pageSize){
		pageHelper.startPage(Integer.valueOf(page), pageSize);
		return studentDao.selectAll();
	}
	@Override
	public List<Student> selectAll() {
		return studentDao.selectAll();
	}
	@Override
	public List<Student> getByXh(String id) {
		return studentDao.getByXh(id);
	}

	@Override
	public List<Student> getByName(String name) {
		return studentDao.getByName(name);
	}
	@Override
	public List<Student> getBySex(final String sex) {
		return studentDao.getBySex(sex);
	}

	@Override
	public List<Student> getByClassName(final String className) {
		return studentDao.getByClassName(className);
	}

	@Override
	public boolean addStu(final Student student) {
		return studentDao.addStu(student);
	}

	@Override
	public boolean updateStu(final Student student) {
		return studentDao.updateStu(student);
	}

	@Override
	public boolean deleteStu(final String xh) {
		return studentDao.deleteStu(xh);
	}
}
