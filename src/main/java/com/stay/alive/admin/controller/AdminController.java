package com.stay.alive.admin.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stay.alive.common.SHA256Util;
import com.stay.alive.login.service.LoginService;
import com.stay.alive.login.vo.LoginVo;
import com.stay.alive.statistics.service.StatisticsService;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	private LoginService loginService;
	@Autowired
	private StatisticsService statisticService;
	
	@GetMapping("main")
	public String admin(Model model, HttpSession session) {
		if(session.getAttribute("groupName") == null) {
			model.addAttribute("msg", "관리자 로그인이 필요합니다.");
			model.addAttribute("url", "/admin/adminLogin");
			return "alert";
		}
		String groupName = (String)session.getAttribute("groupName");
		if(groupName.equals("관리자")) {
			model.addAttribute("memberCount", statisticService.getMemberCount());
			return "admin/admin";
		} else {
			model.addAttribute("msg", "관리자 로그인이 필요합니다.");
			model.addAttribute("url", "/admin/adminLogin");
			return "alert";
		}
	}
	
	@GetMapping("adminLogin")
	public String adminLogin() {
		return "admin/adminLogin/adminLogin";
	}
	
	@PostMapping("adminLogin")
	public String memberLogin(Model model, HttpSession session, LoginVo loginVo) throws IOException {
		
		System.out.println("LoginController.java");
		LoginVo sessionLogin = null;
		String salt = loginService.getMemberSaltFromId(loginVo.getMemberId()); // 아이디가 없으면 null값이 들어간다
		if(salt != null) {
			String passWord = loginVo.getMemberPassword();
			passWord = SHA256Util.getEncrypt(passWord, salt);
			loginVo.setMemberPassword(passWord);
			sessionLogin = loginService.memberLogin(loginVo);
		}
		if(loginService.memberLogin(loginVo)!=null) {
			sessionLogin = loginService.memberLogin(loginVo);
			session.setAttribute("sessionLogin", sessionLogin);		
			session.setAttribute("memberId", sessionLogin.getMemberId());
			System.out.println(sessionLogin.getMemberId() + "<--memberId");
			session.setAttribute("groupName", sessionLogin.getGroupName());
			System.out.println(sessionLogin.getGroupName() + "<--groupName");
			session.setAttribute("memberNickname", sessionLogin.getMemberNickname());
			System.out.println(sessionLogin.getMemberNickname() + "<--memberNickname");
			return "redirect:/admin/main";
		}else {
			model.addAttribute("msg", "관리자 로그인이 필요합니다.");
			model.addAttribute("url", "/admin/adminLogin");
			return "alert";
		}
	}
	
	@GetMapping("adminLogout")
	public String userLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	
}
