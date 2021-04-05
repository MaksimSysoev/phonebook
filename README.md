## REST приложение по работе с пользователями и их телефонной книжкой.
### Программа должна предоставлять REST API для:
> * Получения списка всех пользователей (владельцев телефонных книжек)
> * Создания, получения (по id), удаления, редактирования пользователя
> * Создания, получения (по id), удаления, редактирования записи в телефонной книжке
> * Получения списка всех записей в телефонной книжке пользователя
> * Поиск пользователей по имени (или его части)
> * Поиск телефонной записи по номеру телефона

***В качестве запросов к серверу использовал консольную утилиту*** [Curl](https://curl.haxx.se/download.html)
#
**1. Создание нового пользователя** 
```
curl -H Content-Type:application/json -X POST -d "{\"name\":\"Maxim\",\"list\":"[""]"}" http://localhost:8080/users/
```
![Создание нового пользователя](images/1.jpg)
#

**2. Получение пользователя по id** 
```
curl -i http://localhost:8080/users/1/
```
![Получение пользователя по id](images/2.jpg)
#

**3. Обновление пользователя с id=1** 
```
curl -i -H Content-Type:application/json -X PUT -d "{\"id\":\"1\",\"name\":\"Update User\"}" http://localhost:8080/users/
```
![Обновление пользователя с id=1](images/3.jpg)
#

**4. Удаление пользователя с id=1** 
```
curl -i -X DELETE http://localhost:8080/users/1/
```
![Удаление пользователя с id=1](images/4.jpg)
#

**5. Получение всех пользователей**
```
curl -i http://localhost:8080/users/
```
![Получение всех пользователей](images/5.jpg)
#

**6. Создание нескольких записей в телефонных книгах пользователей. Пользователи должны быть уже созданы!**
```
curl -H Content-Type:application/json -X POST -d "{\"id\":\"1\",\"name\":\"Contact 1\",\"phone\":\"1234567\"}" http://localhost:8080/users/1/phonebook/
curl -H Content-Type:application/json -X POST -d "{\"id\":\"2\",\"name\":\"Contact 2\",\"phone\":\"2222222\"}" http://localhost:8080/users/1/phonebook/
curl -H Content-Type:application/json -X POST -d "{\"id\":\"3\",\"name\":\"Contact 3\",\"phone\":\"3333333\"}" http://localhost:8080/users/1/phonebook/
curl -H Content-Type:application/json -X POST -d "{\"id\":\"1\",\"name\":\"Contact 1\",\"phone\":\"1111111\"}" http://localhost:8080/users/2/phonebook/
curl -H Content-Type:application/json -X POST -d "{\"id\":\"2\",\"name\":\"Contact 2\",\"phone\":\"2222222\"}" http://localhost:8080/users/2/phonebook/
curl -H Content-Type:application/json -X POST -d "{\"id\":\"1\",\"name\":\"User 1\",\"phone\":\"1231231\"}" http://localhost:8080/users/3/phonebook/
curl -H Content-Type:application/json -X POST -d "{\"id\":\"2\",\"name\":\"User 2\",\"phone\":\"3213213\"}" http://localhost:8080/users/3/phonebook/
```
![Создание записи в телефонной книге пользователя](images/6.jpg)
#

**7. Обновление записи в телефонной книге пользователя**
```
curl -i -H Content-Type:application/json -X PUT -d "{\"id\":\"1\",\"name\":\"Update User\",\"phone\":\"7654321\"}" http://localhost:8080/users/1/phonebook/
```
![Обновление записи в телефонной книге пользователя](images/7.jpg)
#
