package com.example.demo.controller;

import com.example.demo.dao.CartDAO;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.service.Book.BookService;
import com.example.demo.service.Review.ReviewService;
import com.example.demo.service.User.UserService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
@SessionAttributes("cart")
public class BookController {
    @Autowired
    BookService bookService;
    @Autowired
    UserService userService;

    @Autowired
    ReviewService reviewService;
    @ModelAttribute("categoryList")
    public List<Category> getCate(){
        List categories = new ArrayList();
        categories.add("CNTT");
        categories.add("ATTT");
        categories.add("LTW");
        categories.add("LTHDT");
        categories.add("CTDLVGT");
        return categories;
    }
    @GetMapping("/")
    public String index(Model model, HttpSession session){
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
        }
        List<Book> books = bookService.findAll();
        model.addAttribute("books",books);
        return "books";
    }

    @GetMapping("/create")
    public String create(Model model,HttpSession session){
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
        }
        model.addAttribute("bookForm",new Book());
        model.addAttribute("btnName","Add");
        model.addAttribute("method","post");
        return "detail";
    }

    @GetMapping("/book")
    public String detail(@RequestParam("id") int id,Model model,HttpSession session){
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
            Optional<Book> book = bookService.findById(id);
            model.addAttribute("bookForm",book.get());
            model.addAttribute("btnName","Edit");
            model.addAttribute("readonly",true);
            model.addAttribute("method","get");
            return "detail";
        }
        return "redirect:/login";

    }
    @RequestMapping(value = "/save",method = RequestMethod.POST,params = "action=Add")
    public String save(@ModelAttribute("bookForm") @Valid Book bookForm, BindingResult bindingResult, @RequestParam("filename") MultipartFile photo, RedirectAttributes redirectAttributes, Model model, HttpSession session){
        if(bindingResult.hasErrors()){
            model.addAttribute("btnName","Add");
            if(session.getAttribute("USER")!=null){
                model.addAttribute("userName",session.getAttribute("USER"));
            }
            return "detail";
        }
        Path path = Paths.get("uploads/");
        try {
            InputStream is= photo.getInputStream();
            Random ran = new Random();
            int r= ran.nextInt(1000000);
            String newName=String.valueOf(r)+photo.getOriginalFilename();
            Files.copy(is,path.resolve(newName), StandardCopyOption.REPLACE_EXISTING);
            bookForm.setImagepath(newName);
        }catch (Exception e){
            e.printStackTrace();
        }
        bookService.save(bookForm);
        redirectAttributes.addFlashAttribute("mess","Create Book Success!");
        return "redirect:/";
    }
    @RequestMapping(value = "/save",method = RequestMethod.POST,params = "action=Edit")
    public String update(@RequestParam("id") int id,Model model,HttpSession session){
        Optional<Book> book = bookService.findById(id);
        model.addAttribute("bookForm",book.get());
        model.addAttribute("btnName","Save");
        model.addAttribute("readonly",false);
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
        }
        return "detail";
    }
    @RequestMapping(value = "/save",method = RequestMethod.POST,params = "action=Save")
    public String Update(@ModelAttribute("bookForm") @Valid Book bookForm,BindingResult bindingResult,RedirectAttributes redirectAttributes,HttpSession session,Model model,@RequestParam("filename")MultipartFile photo){
        System.out.println(bookForm);
        if(bindingResult.hasErrors()){
            model.addAttribute("btnName","Save");
            return "detail";
        }
        Path path = Paths.get("uploads/");
        if(!photo.isEmpty()){
            try {
                Optional<Book> book = bookService.findById(bookForm.getId());
                String pathOld = "uploads/"+book.get().getImagepath();
                File file = new File(pathOld);
                file.delete();
                InputStream is= photo.getInputStream();
                Random ran = new Random();
                int r= ran.nextInt(1000000);
                String newName=String.valueOf(r)+photo.getOriginalFilename();
                Files.copy(is,path.resolve(newName), StandardCopyOption.REPLACE_EXISTING);
                bookForm.setImagepath(newName);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(bookForm);
        bookService.save(bookForm);
        redirectAttributes.addFlashAttribute("mess","Update Book Success!");
        return "redirect:/";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("USER");
        return "redirect:/login";
    }
    @GetMapping("/bookDelete")
    public String remote(@RequestParam("id") int id,RedirectAttributes redirectAttributes, HttpSession session,Model model){
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
            Optional<Book> book = bookService.findById(id);
            String path = "uploads/"+book.get().getImagepath();
            File file = new File(path);
            file.delete();
            System.out.println(path);
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("mess","Delete Book Success!");
            return "redirect:/";
        }
        return "redirect:/login";

    }

    // Render image from db
    @GetMapping("uploads/{photo}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable("photo") String photo){
        if (!photo.equals("") || photo!=null){
            try{
                Path path =Paths.get("uploads",photo);
                byte[] data=Files.readAllBytes(path);
                ByteArrayResource byteArrayResource = new ByteArrayResource(data);
                return ResponseEntity.ok().contentLength(data.length).contentType(MediaType.parseMediaType("image/png")).body(byteArrayResource);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/bookDetail")
    public String bookdetail(@RequestParam("id") int id,Model model,HttpSession session){
        if(session.getAttribute("USER")!=null){
            model.addAttribute("userName",session.getAttribute("USER"));
            Optional<Book> book = bookService.findById(id);
            model.addAttribute("bookForm",book.get());
            model.addAttribute("review",new Review());
            model.addAttribute("idPost",id);
            List<Review> reviews = reviewService.findAllReviewByIdBook(id);
            model.addAttribute("reviews",reviews);
            return "bookDetail";
        }
        return "redirect:/login";
    }

    @ModelAttribute("cart")
    public CartDAO getCart(){
        return new CartDAO();
    }

    @PostMapping("/shop/add/{id}")
    public String addToCart(@PathVariable int id,@RequestParam("quantity") int quantity, Model model, @SessionAttribute("cart") CartDAO cartDto,@RequestParam("action") String action,RedirectAttributes redirectAttributes){
        Optional<Book> book = bookService.findById(id);
        if(!book.isPresent()){
            model.addAttribute("msgError","Not Found!");
            return "redirect:/";
        }
        if(action.equals("add")){
            cartDto.addBook(book.get(),1);
            return "redirect:/cart";
        }
        if(action.equals("sub")){
            cartDto.subBook(book.get());
            return "redirect:/cart";
        }
        cartDto.addBook(book.get(),quantity);
        redirectAttributes.addFlashAttribute("mess","Add To Cart Success!");
        return "redirect:/";
    }
    @GetMapping("/shop/add/{id}")
    public String addToCart2(@PathVariable int id,Model model, @SessionAttribute("cart") CartDAO cartDto,@RequestParam("action") String action,RedirectAttributes redirectAttributes){
        Optional<Book> book = bookService.findById(id);
        if(action.equals("add")){
            cartDto.addBook(book.get(),1);
            return "redirect:/cart";
        }else {
            cartDto.subBook(book.get());
            return "redirect:/cart";
        }
    }
    @ModelAttribute("currentUser")
    public User currentUser(HttpSession httpSession){
        if(httpSession.getAttribute("ID")!=null){
            int id = (int) httpSession.getAttribute("ID");
            User user = userService.findById(id).get();
            return user;
        }
       return null;
    }
    @PostMapping("/review/save")
    public String reviewSave(@ModelAttribute("review") Review review, @Param("idPost") int idPost,HttpSession httpSession){
        review.setDatepost(new Date());
        Book book = bookService.findById(idPost).get();
        review.setBook(book);
        review.setUser(currentUser(httpSession));
        reviewService.create(review);
        System.out.println(review);
        return "redirect:/bookDetail?id="+idPost;
    }
}
