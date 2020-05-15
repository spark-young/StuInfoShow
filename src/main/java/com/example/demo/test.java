package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class test {
	@RequestMapping("list")
	public String stuInfo() {
		return "List";
	}
	@RequestMapping("listUI")
	public String stuInfoUI() {
		return "elementList";
	}
}
