package ru.sysoevm.phonebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.sysoevm.phonebook.domain.PhoneBookEntry;
import ru.sysoevm.phonebook.domain.User;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = PhonebookApplication.class)
@AutoConfigureMockMvc
class PhonebookApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    User user1;

    @Before
    public void setUp() {
        user1 = new User("User 1", new ArrayList<PhoneBookEntry>());
    }

    /**
     * Преобразует переданный объект в json
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

    /**
     * Когда создаётся пользователь, тогда статус статус Создан 201
     * @throws Exception
     */
    @Test
    public void whenCreateUserThenStatus201() throws Exception {
        setUp();
        mockMvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users/")
                .content(asJsonString(user1))
                 .contentType(MediaType.APPLICATION_JSON)
                 .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated()
        );
    }

    /**
     * Поиск созданных пользователей. Сначала идёт создание нового пользователя, далее я его получаю методом GET
     * @throws Exception
     */
    @Test
    public void getAllUsers() throws Exception {
        setUp();
        mockMvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users/")
                .content(asJsonString(user1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated()
                );

        mockMvc.perform( MockMvcRequestBuilders
                .get("http://localhost:8080/users/")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json("[{'name':'User 1'}]"))
                .andExpect(status().isOk()
        );
    }

    /**
     * Когда пользователь найден, то статус 200 при GET запросе
     * @throws Exception
     */
    @Test
    public void getUserById() throws Exception {
        setUp();
        mockMvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users/")
                .content(asJsonString(user1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()
                );
        mockMvc.perform( MockMvcRequestBuilders
                .get("http://localhost:8080/users/1/")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()
                );
    }

    /**
     * Когда удаляю пользователя, то DELETE запрос выполняется успешно.
     * @throws Exception
     */
    @Test
    public void whenDeleteUser() throws Exception {
        setUp();
        mockMvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users/")
                .content(asJsonString(user1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()
                );

        mockMvc.perform( MockMvcRequestBuilders
                .delete("http://localhost:8080/users/1/")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()
                );
    }

}
