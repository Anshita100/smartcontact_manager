package com.smart.services;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

	
	public void  removeMessageFromSession()
	{
		try {
			
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			HttpSession session = request.getSession();
			session.removeAttribute("message");
			System.out.println("Removing message from session");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
