package com.lee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

	@GetMapping("hello")
	public String hello() {
		return "hello";
	}

	/**
	 * 响应index.jsp中的${base}/center.action请求，跳转主页
	 */
	@GetMapping("center")
	public String center() {
		return "center";
	}
}
