package com.mlorenzo.tutorials.spring.mvc.estore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	// Nota: "Model", "ModelMap" y "ModelAndView" nos permiten pasar atributos a las vistas
	
	// Devuelve el nombre de la vista
	// Si usamos JSP & JSTL, Spring utilizará este nombre de vista junto con el prefijo y sufijo configurados en el archivo de propiedades para localizar la página JSP correspondiente(sería prefijo="/WEB-INF/jsp/" + nombre_vista="home" + sufijo=".jsp")
	@GetMapping({"/", "/model"})
	public String homePage(Model model) {
		model.addAttribute("firstName", "Manuel");
		model.addAttribute("lastName", "Lorenzo");
		return "home";
	}
	
	@GetMapping({"/modelmap"})
	public String homePage(ModelMap model) {
		model.addAttribute("firstName", "Manuel");
		model.addAttribute("lastName", "Lorenzo");
		return "home";
	}
	
	@GetMapping({"/modelandview"})
	public ModelAndView homePage() {
		// Primera forma
		/*Map<String, String> model = new HashMap<>();
		
		model.put("firstName", "Manuel");
		model.put("lastName", "Lorenzo");
	
		return new ModelAndView("home", model);*/
		
		// Segunda forma
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName("home");
		modelAndView.addObject("firstName", "Manuel");
		modelAndView.addObject("lastName", "Lorenzo");
		
		return modelAndView;
	}
}
