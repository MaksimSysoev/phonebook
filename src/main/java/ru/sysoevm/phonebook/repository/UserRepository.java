package ru.sysoevm.phonebook.repository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.sysoevm.phonebook.domain.PhoneBookEntry;
import ru.sysoevm.phonebook.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class UserRepository {
    /**
     * Хранилище пользователей.
     */
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();

    /**
     * Ключ в коллекции users .
     */
    private AtomicInteger id = new AtomicInteger(1);

    /**
     * Id созданного пользователя.
     */
    private AtomicInteger userId = new AtomicInteger(1);

    /**
     * Создание пользователя.
     * @param user передаётся пользователь
     * @return статус создан
     */
    public HttpStatus createUser(User user) {
        user.setId((int)(userId.getAndIncrement()));
        users.put(id.getAndIncrement(), user);
        return HttpStatus.CREATED;
    }

    /**
     * Поиск пользователя по id
     * @param id для поиска
     * @return объект типа User
     */
    public User findById(int id) {
        User findUser = null;
        for (User user : findAll()) {
            if (user.getId() == id) {
                findUser = new User();
                findUser.setId(user.getId());
                findUser.setName(user.getName());
                findUser.setPhoneBook(user.getPhoneBook());
                break;
            }
        }
        if (findUser==null) {
            HttpStatus.NOT_FOUND.value();
        }
        return findUser;
    }

    /**
     * Обновляет пользователя. Поиск пользователя осуществляется по id.
     * @param user переданный объект должен быть с таким же id, как и не обновляемый
     */
    public void update(User user) {
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            if (user.getId() == entry.getValue().getId()) {
                users.replace(entry.getKey(), user);
                break;
            }
        }
    }

    /**
     * Удаляет пользователя из хранилища. ПОиск осуществляется по id
     * @param user удаляемый объект
     */
    public void delete(User user) {
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            if (user.getId()==entry.getValue().getId()) {
                users.remove(entry.getKey());
                break;
            }
        }
    }

    /**
     * Поиск всех созданных пользователей
     * @return коллекцию типа List<User>
     */
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            User user = entry.getValue();
            result.add(user);
        }
        return result;
    }

    /**
     * Создаёт запись в телефонной книге пользователя
     * @param id пользователя к которому добавляется запись в телефонную книгу
     * @param entry запись
     * @return статус создано
     */
    public HttpStatus createEntry(int id, PhoneBookEntry entry) {
        User user = findById(id);
        user.getPhoneBook().add(entry);
        return HttpStatus.CREATED;
    }

    /**
     * Обновляет запись в телефонной книге пользователя
     * @param id пользователя
     * @param entry обновлённая запись
     */
    public void updateEntry(int id, PhoneBookEntry entry) {
        User user = findById(id);
        for (PhoneBookEntry e : user.getPhoneBook()) {
            if (entry.getId()==e.getId()) {
                e.setId(entry.getId());
                e.setName(entry.getName());
                e.setPhone(entry.getPhone());
                break;
            }
        }
    }

    /**
     * Поиск телефонной записи по id
     * @param userId пользователя у которого есть телефонная книга
     * @param entryId самой записи в телефонной книге пользователя
     * @return телефонную запись
     */
    public PhoneBookEntry findEntryById(int userId, int entryId) {
        PhoneBookEntry findEntry = null;
        User user = findById(userId);
        for (PhoneBookEntry entry : user.getPhoneBook()) {
            if (entry.getId() == entryId) {
                findEntry = new PhoneBookEntry();
                findEntry.setId(entry.getId());
                findEntry.setPhone(entry.getPhone());
                findEntry.setName(entry.getName());
                break;
            }
        }
        if (findEntry==null) {
            HttpStatus.NOT_FOUND.value();
        }
        return findEntry;
    }

    /**
     * Удаляет запись в телефонной книге пользователя
     * @param userId пользователя
     * @param entryId записи
     */
    public void deleteEntry(int userId, int entryId) {
        User user = findById(userId);
        for (PhoneBookEntry entry : user.getPhoneBook()) {
            if (entryId == entry.getId()) {
                entry.setId(0);
                entry.setName(null);
                entry.setPhone(null);
                break;
            }
        }

    }

    /**
     * Поиск всех записей в телефонной книге пользователя
     * @param userId пользователя
     * @return список типа List<PhoneBookEntry>
     */
    public List<PhoneBookEntry> findAllEntry(int userId) {
        List<PhoneBookEntry> result = new ArrayList<>();
        User user = findById(userId);
        for(PhoneBookEntry entry : user.getPhoneBook()) {
            result.add(entry);
        }
        return result;
    }

    /**
     * Поиск пользователя по совпадению имени или его части
     * @param name искомое имя или часть
     * @return список типа List<User>
     */
    public List<User> findUsersByName(String name) {
        List<User> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            User user = entry.getValue();
            Matcher matcher = pattern.matcher(user.getName());
            if (matcher.find()) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Осуществляет поиск номера телефона по всему хранилищу users
     * @param phone искомый номер
     * @return объект типа PhoneBookEntry
     */
    public PhoneBookEntry findEntryByPhone(String phone) {
        PhoneBookEntry findEntry = null;
        for (Map.Entry<Integer, User> map : users.entrySet()) {
            List<PhoneBookEntry> phoneBook = map.getValue().getPhoneBook();
            for (PhoneBookEntry entry : phoneBook) {
                if (phone.equals(entry.getPhone())) {
                    findEntry = new PhoneBookEntry();
                    findEntry.setId(entry.getId());
                    findEntry.setName(entry.getName());
                    findEntry.setPhone(entry.getPhone());
                    break;
                }
            }
        }

        if (findEntry==null) {
            HttpStatus.NOT_FOUND.value();
        }
        return findEntry;
    }

}
