package com.thymeleafDungeon.controller;

import java.util.ArrayList;
import java.util.List;

import com.thymeleafDungeon.form.HeroForm;
import com.thymeleafDungeon.model.Hero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@Controller
public class MainController {

    @Autowired
    private RestTemplate restTemplate;

    private static final List<Hero> heroes = new ArrayList<>();

    // Injectez (inject) via application.properties.
    @Value("${welcome.message}")
    private String message;

    @Value("${error.message}")
    private String errorMessage;

    @Value("http://localhost:8092/list/")
    private String url;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("message", message);

        return "index";
    }

    @RequestMapping(value = {"/heroList"}, method = RequestMethod.GET)
    public String heroList(Model model) {
        ResponseEntity<Hero[]> res = restTemplate.getForEntity(url, Hero[].class);
        Hero[] heroes = res.getBody();

        model.addAttribute("heroes", heroes);

        return "heroList";
    }

    @RequestMapping(value = {"/addHero"}, method = RequestMethod.GET)
    public String showAddHeroPage(Model model) {

        HeroForm heroForm = new HeroForm();
        model.addAttribute("heroForm", heroForm);

        return "addHero";
    }

    @PostMapping(value = {"/addHero"})
    public String saveHero(Model model, //
                           @ModelAttribute("heroForm") HeroForm heroForm) {
        String name = heroForm.getName();
        String type = heroForm.getType();
        int id = heroForm.getId();

        if (name != null && name.length() > 0) {
            Hero newHero = new Hero(id, name, type);
            ResponseEntity<Hero> res = restTemplate.postForEntity(url,
                    newHero,
                    Hero.class);
            return "redirect:/heroList";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "addHero";
    }

    @RequestMapping(value = {"/showHero/{id}"}, method = RequestMethod.GET)
    public String showHeroPage(Model model, @PathVariable int id) {

        ResponseEntity<Hero> res = restTemplate.getForEntity(url + id, Hero.class);

        Hero hero = res.getBody();
        model.addAttribute("hero", hero);

        return "showHero";
    }

    @GetMapping(value = "/updateForm/{id}")
    public String GetById(Model model, @PathVariable("id") Integer id) {
        ResponseEntity<Hero> response = restTemplate.getForEntity(url + id, Hero.class);
        Hero heroToUpdate = response.getBody();
        HeroForm heroForm = new HeroForm();

        model.addAttribute("heroForm", heroForm);
        model.addAttribute("hero", heroToUpdate);
        return "updateHero";
    }

    @PostMapping(value = "/update/{id}")
    public String update(Model model, @ModelAttribute("heroForm") HeroForm heroForm,
                         @PathVariable("id") Integer id) {
        String name = heroForm.getName();
        String type = heroForm.getType();

        if (name != null && name.length() > 0) {
            Hero newHero = new Hero(id,name,type);
            HttpEntity<Hero> request = new HttpEntity<>(newHero);
            restTemplate.put(url+id, request, Hero.class);

            return "redirect:/heroList";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "updateForm";
    }

    @RequestMapping(value = {"/delete/{id}"}, method = RequestMethod.GET)
    public String delete(@PathVariable("id") Integer id) {
        restTemplate.delete(url+id);
        return "redirect:/heroList";
    }


}