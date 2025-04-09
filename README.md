# t1_hw1

Задание 1 Дедлайн 31.03.2025
Задание необходимо сдавать в виде PullReuest на GitHub

создайте новый репозиторий, пусть init скоммит содержит только readme и gitignore

далее в новой ветке разработайте следующее:

CRUD приложение + логи через аспекты

1. Создать простой RESTful сервис для управления задачами:

Task(id, title, description,userId)

1. POST /tasks — создание новой задачи.

2. GET /tasks/{id} — получение задачи по ID.

3. PUT /tasks/{id} — обновление задачи.

4. DELETE /tasks/{id} — удаление задачи.

5. GET /tasks — получение списка всех задач.

3. Релизуйте класс аспект, со следующими advice:

1. Before

2. AfterThrowing

3. AfterReturning

4. Around (замер выполнения)

В приложении должна быть реализована логика на каждый advice - свой метод, можно сделать больше, использовать несколько advice на отдельные методы, но меньше нельзя.

==============

Можно протестировать приложение с помощью curl:

# Создать задачу
curl -X POST -H "Content-Type: application/json" -d '{"title":"First task","description":"Do something","userId":1}' http://localhost:8080/tasks

# Получить все задачи
curl http://localhost:8080/tasks

# Получить задачу по ID
curl http://localhost:8080/tasks/1

# Обновить задачу
curl -X PUT -H "Content-Type: application/json" -d '{"title":"Updated task","description":"Do something else","userId":1}' http://localhost:8080/tasks/1

# Удалить задачу
curl -X DELETE http://localhost:8080/tasks/1

