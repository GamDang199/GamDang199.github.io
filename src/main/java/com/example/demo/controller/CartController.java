package com.example.demo.controller;

import com.example.demo.dao.CartDAO;
import com.example.demo.model.Book;
import com.example.demo.service.Book.BookService;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class CartController {
    @Autowired
    BookService bookService;
    @ModelAttribute("cart")
    public CartDAO cartDto(){
        return new CartDAO();
    }

    @GetMapping("/cart")
    public String index(@SessionAttribute(value = "cart",required = false) CartDAO cartDto, Model model, HttpSession session){
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
            model.addAttribute("cartDto",cartDto);
            return "cart/list";
        }
        return "redirect:/login";
    }

    @GetMapping("/cart/delete/{id}")
    public String delete(@PathVariable int id, @SessionAttribute("cart") CartDAO cartDto, RedirectAttributes redirectAttributes){
        Optional<Book> book = bookService.findById(id);
        if(!book.isPresent()){
            redirectAttributes.addFlashAttribute("msg","Not Found!");
            return "redirect:/cart";
        }
        cartDto.removeBook(book.get());
        redirectAttributes.addFlashAttribute("msg","Delete Book Success!");
        return "redirect:/cart";
    }
    @GetMapping("/cart/cancel")
    public String cancel(@SessionAttribute("cart") CartDAO cartDto){
        cartDto.getBooks().clear();
        return "redirect:/cart";
    }
    @GetMapping("/cart/checkout")
    public String checkout(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("msg","Đặt hàng thành công. Đơn đang được xử lý.");
        return "redirect:/cart";
    }
}
