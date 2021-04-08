package ru.sysoevm.phonebook.repository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.sysoevm.phonebook.domain.PhoneBookEntry;
import ru.sysoevm.phonebook.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class UserRepository {
    /**
     * Хранилище пользователей.
     */
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();

    /**
     * Создание пользователя.
     * @param user передаётся пользователь
     * @return статус создан
     */
    public HttpStatus createUser(User user) {
        users.put(user.getId(), user);
        return HttpStatus.CREATED;
    }

    /**
     * Поиск пользователя по id
     * @param id для поиска
     * @return объект типа User
     */
    public User findById(int id) {
        User findUser = users.get(id);
        if (findUser==null) {
            HttpStatus.NOT_FOUND.value();
        }
        return findUser;
    }

    /**
     * Обновляет пользователя. Поиск пользователя осуществляется по id.
     * @param newUser переданный объект должен быть с таким же id, как и не обновляемый
     */
    public HttpStatus update(User newUser) {
        User oldUser = users.get(newUser.getId());
        int userID = newUser.getId();

        if (users.replace(userID, oldUser, newUser)) {
            return HttpStatus.CREATED;
        }
        return HttpStatus.NOT_FOUND;
    }

    /**
     * Удаляет пользователя из хранилища. ПОиск осуществляется по id
     * @param user удаляемый объект
     */
    public HttpStatus delete(User user) {
        int userID = user.getId();
        if (users.remove(userID, user)) {
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }

    /**
     * Поиск всех созданных пользователей
     * @return коллекцию типа Collection<User>
     */
    public Collection<User> findAll() {
        return users.values();
    }

    /**
     * Создаёт запись в телефонной книге пользователя
     * @param userId пользователя к которому добавляется запись в телефонную книгу
     * @param entry запись
     * @return статус создано
     */
    public HttpStatus createEntry(int userId, PhoneBookEntry entry) {
        User user = findById(userId);
        user.getPhoneBook().add(entry);
        return HttpStatus.CREATED;
    }

    /**
     * Обновляет запись в телефонной книге пользователя
     * @param userId пользователя
     * @param newEntry обновлённая запись
     */
    public void updateEntry(int userId, PhoneBookEntry newEntry) {
        User user = findById(userId);
        for (PhoneBookEntry e : user.getPhoneBook()) {
            if (newEntry.getId()==e.getId()) {
                e.setId(newEntry.getId());
                e.setName(newEntry.getName());
                e.setPhone(newEntry.getPhone());
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
        return findById(userId).getPhoneBook();
    }

    /**
     * Поиск пользователя по совпадению имени или его части
     * @param name искомое имя или часть
     * @return список типа List<User>
     */
    public User findUsersByName(String name) {
        User findUser = new User();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            User user = entry.getValue();
            Matcher matcher = pattern.matcher(user.getName());
            if (matcher.find()) {
                findUser.setId(user.getId());
                findUser.setName(user.getName());
                findUser.setPhoneBook(user.getPhoneBook());
            }
        }
      return findUser;
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
