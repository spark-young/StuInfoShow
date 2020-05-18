package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Student;
import com.example.demo.service.StuService;

@RestController
@RequestMapping("/StuInfo")
public class MainController {
	@Autowired	
	private StuService stuService;
	@Value("${pageSize}")
	private int pageSize;

	@GetMapping
	@CrossOrigin
	public List<Student> selectAll() {
		return stuService.selectAll();
	}
	@GetMapping("/{type}")
	@CrossOrigin
	public List<Student> getByParam(@PathVariable("type")String type,@RequestParam("param")String param) {
		switch(type){
			case "xh":return stuService.getByXh(param);
			case "name":return stuService.getByName(param);
			case "sex":return stuService.getBySex(param);
			case "className":return stuService.getByClassName(param);
			case "page":return stuService.selectPage(param,pageSize);
		}
		return null;
	}
	@PostMapping
	@CrossOrigin
	public boolean addStu(@RequestBody Student student) {
		System.out.println(student);
		return stuService.addStu(student);
	}
	@PutMapping
	@CrossOrigin
	public boolean updateStu(@RequestBody Student student) {
		return stuService.updateStu(student);
	}
	@DeleteMapping
	public boolean deleteStu(@RequestParam("xh") String xh) {
		return stuService.deleteStu(xh);
	}
}
