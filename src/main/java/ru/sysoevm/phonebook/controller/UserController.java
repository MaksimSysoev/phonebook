package ru.sysoevm.phonebook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sysoevm.phonebook.domain.PhoneBookEntry;
import ru.sysoevm.phonebook.domain.User;
import ru.sysoevm.phonebook.repository.UserRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    /**
     * Поиск всех пользователей по GET запросу curl -i http://localhost:8080/users/
     * @return
     */
    @GetMapping("/")
    public List<User> findAll() {
        return StreamSupport.stream(
                this.users.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    /**
     * Поиск пользователя по его id в GET запросе curl -i http://localhost:8080/users/1/
     * @param id искомого пользователя
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable int id) {
        var user = this.users.findById(id);
        return new ResponseEntity<User>(
                user,
                user!=null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    /**
     * Создаёт нового пользователя POST запросом curl -H Content-Type:application/json -X POST -d "{\"name\":\"User\",\"list\":"[""]"}" http://localhost:8080/users/
     * @param user
     * @return
     */
    @PostMapping(value = "/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<User> (
                this.users.createUser(user)
        );

    }

    /**
     * Редактирует пользователя PUT запросом curl -i -H Content-Type:application/json -X PUT -d "{\"id\":\"1\",\"name\":\"UpdateUser\"}" http://localhost:8080/users/
     * @param user обновлённый пользователь
     * @return
     */
    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody User user) {
        this.users.update(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаляеет пользователя DELETE запросом curl -i -X DELETE http://localhost:8080/users/2/
     * @param id удаляемого пользователя
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        User user = this.users.findById(id);
        this.users.delete(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Созадёт запись в телефонной книге пользователя POST запросом curl -H Content-Type:application/json -X POST -d "{\"id\":\"1\",\"name\":\"Contact 1\",\"phone\":\"1234567\"}" http://localhost:8080/users/1/phonebook/
     * @param id пользователя
     * @param entry создаваемая телефонная запись
     * @return
     */
    @PostMapping("/{id}/phonebook")
    public ResponseEntity<PhoneBookEntry> createEntry(@PathVariable int id, @RequestBody PhoneBookEntry entry) {
        return new ResponseEntity<PhoneBookEntry>(
                this.users.createEntry(id, entry)
        );
    }

    /**
     * Обновляет запись в телефонной книге пользователя PUT запросом curl -i -H Content-Type:application/json -X PUT -d "{\"id\":\"1\",\"name\":\"Update Contact 1\",\"phone\":\"7654321\"}" http://localhost:8080/users/1/phonebook/
     * @param id пользователя
     * @param entry обновлённая запись
     * @return
     */
    @PutMapping("/{id}/phonebook")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody PhoneBookEntry entry) {
        this.users.updateEntry(id, entry);
        return ResponseEntity.ok().build();
    }

    /**
     * Поиск записи в телефонной книге пользователя по id GET запросом curl -i http://localhost:8080/users/1/phonebook/1/
     * @param userId пользователя
     * @param entryId записи, которую необходимо вернуть
     * @return
     */
    @GetMapping("/{userId}/phonebook/{entryId}")
    public ResponseEntity<PhoneBookEntry> findEntryById(@PathVariable int userId, @PathVariable int entryId) {
        var entry = this.users.findEntryById(userId, entryId);
        return new ResponseEntity<PhoneBookEntry>(
                entry,
                entry!=null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    /**
     * Удаление записи в телефонной книге пользователя DELETE запросом curl -i -X DELETE http://localhost:8080/users/1/phonebook/1/
     * @param userId пользоватлея
     * @param entryId удаляемой записи
     * @return
     */
    @DeleteMapping("/{userId}/phonebook/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable int userId, @PathVariable int entryId) {
        this.users.deleteEntry(userId, entryId);
        return ResponseEntity.ok().build();
    }

    /**
     * Находит все записи в телефонной книге пользователя GET запросом curl -i http://localhost:8080/users/1/phonebook/
     * @param userId пользователя
     * @return
     */
    @GetMapping("/{userId}/phonebook/")
    public List<PhoneBookEntry> findAllEntry(@PathVariable int userId) {
        return StreamSupport.stream(
                this.users.findAllEntry(userId).spliterator(), false
        ).collect(Collectors.toList());
    }

    /**
     * Получает пользователя по имени или его части GET запросом curl -i http://localhost:8080/users/search/name?name=Max
     * @param name искомое имя или часть
     * @return
     */
    @GetMapping("/search/name")
    public List<User> findUsersByName(@RequestParam String name) {
        return StreamSupport.stream(
                this.users.findUsersByName(name).spliterator(), false
        ).collect(Collectors.toList());
    }

    /**
     * Находит телефонную запись по номеру телефона GET запросом curl -i http://localhost:8080/users/search/phone?phone=1111111
     * @param phone искомый номер
     * @return
     */
    @GetMapping("/search/phone")
    public ResponseEntity<PhoneBookEntry> searchEntryByPhoneNumber(@RequestParam String phone) {
        var entry = this.users.findEntryByPhone(phone);
        return new ResponseEntity<PhoneBookEntry>(
                entry,
                entry!=null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

}
