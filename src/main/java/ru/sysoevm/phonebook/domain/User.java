package ru.sysoevm.phonebook.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Модель описывающая пользователей
 */
@EntityScan
public class User {

    private int id;
    private String name;
    private List<PhoneBookEntry> phoneBook = new ArrayList<>();

    public User() {

    }

    public User(String name, List<PhoneBookEntry> phoneBook) {
        this.name = name;
        this.phoneBook = phoneBook;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PhoneBookEntry> getPhoneBook() {
        return phoneBook;
    }

    public void setPhoneBook(List<PhoneBookEntry> phoneBook) {
        this.phoneBook = phoneBook;
    }
}
