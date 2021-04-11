package ru.sysoevm.phonebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.sysoevm.phonebook.domain.PhoneBookEntry;
import ru.sysoevm.phonebook.domain.User;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.ArrayList;

@SpringBootTest(classes = PhonebookApplication.class)
@AutoConfigureMockMvc
class PhonebookApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Когда создаётся пользователь, тогда статус статус 201.
     * @throws Exception
     */
    @Test
    public void whenCreateUserThenStatus201() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()))
             .andExpect(status()
             .isCreated());
    }

    /**
     * Поиск созданных пользователей.
     * @throws Exception
     */
    @Test
    public void whenGetAllUsersReturnList() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createUser(new User(1, "User 2", new ArrayList<PhoneBookEntry>()));
        get("http://localhost:8080/users")
                .andExpect(content().json("[{'name':'User 1'},{'name':'User 2'}]"))
                .andExpect(status().isOk());
    }

    /**
     * Когда пользователь найден, то статус 200 при GET запросе.
     * @throws Exception
     */
    @Test
    public void whenGetUserByIdThenStatus200() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        get("http://localhost:8080/users/0")
                .andExpect(content().json("{'name':'User 1'}"))
                .andExpect(status().isOk());
    }

    /**
     * Когда обновление пользователя.
     * @throws Exception
     */
    @Test
    public void whenUpdateUser() throws Exception {
        User updateUser = new User(0, "Update User", new ArrayList<PhoneBookEntry>());
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));

        mockMvc.perform( MockMvcRequestBuilders
                .put("http://localhost:8080/users")
                .content(asJsonString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print());

        get("http://localhost:8080/users/0")
                .andDo(print())
                .andExpect(content().json("{'name':'Update User'}"));
    }

    /**
     * Когда удаляю пользователя, то DELETE запрос выполняется успешно.
     * @throws Exception
     */
    @Test
    public void whenDeleteUserThenStatus200() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        mockMvc.perform( MockMvcRequestBuilders
                .delete("http://localhost:8080/users/0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()
                );
    }

    /**
     * Когда создаётся новая запись, тогда возвращается статус 201.
     * @throws Exception
     */
    @Test
    public void whenCreateEntryThenStatus201() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createEntry(new PhoneBookEntry(0, "Contact 1", "1234567"))
            .andExpect(status()
            .isCreated());
    }

    /**
     * Когда создаётся запись, тогда статус 200.
     */
    @Test
    public void whenGetEntryByIdThenStatus200() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createEntry(new PhoneBookEntry(0, "Contact 1", "1234567"));
        get("http://localhost:8080/users/0/phonebook/0")
                .andExpect(content().json("{'name':'Contact 1'}"))
                .andExpect(status().isOk());
    }

    /**
     * Когда возвращает все записи в телефонной книге, возвращает список.
     * @throws Exception
     */
    @Test
    public void whenGetAllEntryReturnList() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createEntry(new PhoneBookEntry(0, "Contact 1", "1234567"));
        createEntry(new PhoneBookEntry(1, "Contact 2", "7777777"));
        get("http://localhost:8080/users/0/phonebook")
                .andExpect(content().json("[{'phone':'1234567'}, {'phone':'7777777'}]"))
                .andExpect(status().isOk());
    }

    /**
     * Когда обновление телефонной записи.
     * @throws Exception
     */
    @Test
    public void whenUpdateEntry() throws Exception {
        PhoneBookEntry updateEntry = new PhoneBookEntry(0, "Update Entry", "1111111");
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createEntry(new PhoneBookEntry(0, "Contact 1", "7777777"));

        mockMvc.perform( MockMvcRequestBuilders
                .put("http://localhost:8080/users/0/phonebook")
                .content(asJsonString(updateEntry))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print());

        get("http://localhost:8080/users/0/phonebook/0")
                .andDo(print())
                .andExpect(content().json("{'name':'Update Entry', 'phone':'1111111'}"));
    }

    /**
     * Когда удаляется запись, тогда статус 200.
     * @throws Exception
     */
    @Test
    public void whenDeleteEntryThenStatus200() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createEntry(new PhoneBookEntry(0, "Contact 1", "1234567"));
        mockMvc.perform( MockMvcRequestBuilders
            .delete("http://localhost:8080/users/0/phonebook/0")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * Поиск по имени пользователя или по совпадению с именем.
     * @throws Exception
     */
    @Test
    public void whenSearchByNameThenReturnUser() throws Exception {
        createUser(new User(0, "Ivan", new ArrayList<PhoneBookEntry>()));
        get("http://localhost:8080/users/search/name?name=iv")
                .andExpect(content().json("{'name':'Ivan'}"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenSearchByPhoneReturnEntry() throws Exception {
        createUser(new User(0, "User 1", new ArrayList<PhoneBookEntry>()));
        createEntry(new PhoneBookEntry(0, "Contact 1", "7777777"));
        get("http://localhost:8080/users/search/phone?phone=7777777")
                .andExpect(content().json("{'name':'Contact 1'}"))
                .andExpect(status().isOk());
    }


    /**
     * Создаёт нового пользователя методом POST.
     * @param user
     * @return
     * @throws Exception
     */
    public ResultActions createUser(User user) throws Exception {
        return  mockMvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users")
                .content(asJsonString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    /**
     * Создаёт новую запись методом POST.
     * @param entry
     * @return
     * @throws Exception
     */
    public ResultActions createEntry(PhoneBookEntry entry) throws Exception {
        return  mockMvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users/0/phonebook")
                .content(asJsonString(entry))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    /**
     * Получение результатов методом GET.
     * @param request
     * @return
     * @throws Exception
     */
    public ResultActions get(String request) throws Exception {
        return mockMvc.perform( MockMvcRequestBuilders
                .get(request)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    /**
     * Преобразует переданный объект в json.
     * @param obj переданынй объект
     * @return преобразованную в json строку
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
