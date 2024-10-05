package com.smart.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MyController {
    
	
	 @Autowired 
	 private BCryptPasswordEncoder bCryptPasswordEncoder;
	  
	 

	 
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home1()
	{
		return "home";
	}
	@RequestMapping("/home")
	public String home()
	{
		return "home";
	}

	@RequestMapping("/about")
	public String about()
	{
		return "about";
	}
	
	@GetMapping("/signin")
	public String login(Model m)
	{
		return "login";
	}
	
	
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("user",new User());
		return "signup";
	}
	
	//handle for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model m,HttpSession session )
	{
		try {
			if(agreement==false)
			{
				System.out.println("User hasn't agreed to terms and conditions");
			    throw new Exception("User hasn't agreed to terms and conditions");
			}
			if(result.hasErrors())
			{
				System.out.println("Error"+result.toString());
				m.addAttribute("user",user);
				return "signup";	
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			System.out.println("Agreement "+agreement);
			System.out.println("User "+user);
			
			userRepository.save(user);
			
			m.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Registered!!","alert-success"));
			return "signup";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			m.addAttribute("user",user);
			session.setAttribute("message", new Message("Something Went Wrong!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}	
	}
	
	 /* @GetMapping("/invalidateMessage") 
	  public void invalidateMessage(HttpSession session)
	  {
		  session.removeAttribute("message");
	  }*/
	 
}