package com.smart.controller;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();
		System.out.println("USERNAME" + userName);

		User user = userRepository.getUserByUserName(userName);
		// at the time of login we used email and password , so our email is considered
		// as the username..
		System.out.println(user);

		m.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model m, Principal p) {

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// open add form handler lec-54
	@PostMapping("/process-contact")
	public String processcontact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal p, HttpSession session) {
		try {
			String name = p.getName();
			User user = userRepository.getUserByUserName(name);

			// processing and uploading file

			if (file.isEmpty()) {
				contact.setImage("contact.png");// setting as a default image
			} else {
				// upload the file in the folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);

			userRepository.save(user);

			System.out.print(contact);

			// message success..

			session.setAttribute("message", new Message("Your contact is added", "success"));
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong", "danger"));

		}

		return "normal/add_contact_form";

	}

	// show contacts handler lec-60 pagination
	// current page=0(page) and per page show 5 so n=5
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal p) {

		// let me help
		String userName = p.getName();
		User user = userRepository.getUserByUserName(userName);

		// current page=0(page) and per page show 5 so n=5
		PageRequest pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details
	@RequestMapping("/{id}/contact")
	public String showContactDetails(@PathVariable("id") Integer id, Model m, Principal p) {
		System.out.print("CID " + id);

		Optional<Contact> contactOptional = contactRepository.findById(id);
		Contact contact = contactOptional.get();

		String userName = p.getName();
		User user = userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	// delete contact handler
	@GetMapping("/delete/{id}")
	@Transactional
	public String deleteContact(@PathVariable("id") Integer id, Model m, HttpSession session, Principal p) {
		Optional<Contact> contactOptional = contactRepository.findById(id);
		Contact contact = contactOptional.get();

		// check assignment
		System.out.println("Contact " + contact.getId());

		contact.setUser(null);
		// remove img contact.getImage() do it yourself

		
		/*  User user=userRepository.getUserByUserName(p.getName());
		  user.getContacts().remove(contact); //lec-66  problem is not resolved
		  userRepository.save(user);
		 */

		contactRepository.delete(contact);
		System.out.println("Deleted");

		session.setAttribute("message", new Message("Contact deleted successfully...", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{id}")
	public String updateForm(@PathVariable("id") Integer id, Model m) {// let me help

		Contact contact = contactRepository.findById(id).get();
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}

	/* let me help */
	// update contact handler
	@PostMapping(value = "/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal p) {
		try {
			Contact oldcontactDetail = contactRepository.findById(contact.getId()).get();

			// image
			if (file.isEmpty() == false) {
				// file work
				// rewrite..
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();

				File file1 = new File(deleteFile, oldcontactDetail.getImage());

				file1.delete();

				// update photo
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());
			} else {
				// agr file empty hai toh fir purani wali image hi daal do
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = userRepository.getUserByUserName(p.getName());
			contact.setUser(user);
			contactRepository.save(contact);

			session.setAttribute("message", new Message("Your contact is updated...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Contact name" + contact.getName());
		System.out.println("Contact ID" + contact.getId());

		return "redirect:/user/" + contact.getId() + "/contact";
	}
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model m)
	{
		return"normal/profile";
	}
}
