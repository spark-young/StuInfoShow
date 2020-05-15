package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.entity.Student;
/**
 * 
 * <p>Title: StudentMapper</p>
 * <p>Description: </p>
 * @author Spark
 * @date 2020年5月7日
 */
public interface StudentMapper {
	//所有学生
	@Select("select * from test.student")
	public List<Student> selectAll();
	//根据xh查询
	@Select("select * from test.student where xh=#{xh}")
	public List<Student> getByXh(String id);
	//根据姓名查询
	@Select("select * from test.student where name=#{name}")
	public List<Student> getByName(String name);
	//根据性别查询
	@Select("select * from test.student where sex=#{sex}")
	public List<Student> getBySex(String sex);
	//根据班级查询
	@Select("select * from test.student where className=#{className}")
	public List<Student> getByClassName(String className);
	//添加学生
	@Insert("insert into test.student values(#{xh},#{name},#{sex},#{className})")
	public boolean addStu(Student student);
	//修改学生信息
	@Update("update test.student set name=#{name},sex=#{sex},className=#{className} where xh=#{xh}")
	public boolean updateStu(Student student);
	//删除学生信息
	@Delete("delete from test.student where xh=#{xh}")
	public boolean deleteStu(String xh);
}
