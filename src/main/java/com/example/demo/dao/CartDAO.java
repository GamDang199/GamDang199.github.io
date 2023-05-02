package com.example.demo.dao;

import com.example.demo.model.Book;

import java.util.HashMap;
import java.util.Map;

public class CartDAO {
    private Map<Book,Integer> books = new HashMap<>();
    public CartDAO(){

    }
    public CartDAO(Map<Book, Integer> books){
        this.books = books;
    }
    public Map<Book, Integer> getBooks(){
        return books;
    }
    public void setBooks(Map<Book, Integer> books){
        this.books = books;
    }
    private boolean checkItemInCart(Book book){
        for(Map.Entry<Book,Integer> entry: books.entrySet()){
            if(entry.getKey().getId() == book.getId())
                return true;
        }
        return false;
    }

    private Map.Entry<Book,Integer> selectItemInCart(Book book){
        for (Map.Entry<Book,Integer> entry: books.entrySet()){
            if(entry.getKey().getId() == book.getId())
                return entry;
        }
        return null;
    }

    public void addBook(Book book,int quantity){
        if(!checkItemInCart(book)){
            books.put(book,quantity);
        }
        else{
            Map.Entry<Book,Integer> itemEntry = selectItemInCart(book);
            Integer newQuantity = itemEntry.getValue()+quantity;
            books.put(itemEntry.getKey(),newQuantity);
        }
    }

    public void subBook(Book book){
        if(checkItemInCart(book)){
            Map.Entry<Book,Integer> itemEntry = selectItemInCart(book);
            Integer newQuantity = itemEntry.getValue()-1;
            if(newQuantity == 0){
                books.remove(book);
            }
            else{
                books.replace(itemEntry.getKey(),newQuantity);
            }
        }
    }

    public void removeBook(Book book){
        Map.Entry<Book,Integer> itemEntry = selectItemInCart(book);
        books.remove(itemEntry.getKey());
    }

    public String toTalItem(){
        float total = 0;
        for (Map.Entry<Book,Integer> item: books.entrySet()){
            total+=item.getKey().getPrice()*item.getValue();
        }
        return String.format("%.02f",total);
    }
}
