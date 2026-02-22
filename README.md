
# MyBookShopApp

This is a test internet store of books. 

## Tech Stack

**Frontend**

HTML, CSS, JavaScript, jQuery, Thymeleaf

**Backend:**

MyBookShopApp is developed on Java programming language using the follow stack of technology:
* Maven -  Dependency management and automatic project builder
* Spring Framework (Spring MVC, Spring Security, Spring Boot, Spring Data, Java Persistanse API, Hibernate ORM)
* JWT - json web token of access
* oAuth2
* sms and mail distributing
* jUnit test framework
* Database PostgreSQL
* Springfox API documentation generator


## Deployment

To deploy this project:

1. Clone this project to your local computer
2. Install PostgreSql Server
3. Specify PostgreSql login and password in the application.properties in the project
4. Open project in IDEA and download all dependencies
5. Run src/main/java/com/example/my_book_shop_app/MyBookShopAppApplication.java
6. Open localhost:8085 address in the browser

Or instead of 4-5 points do this:
1. Execute 'mvn install' command on the terminal in the project directory
2. Run MyBookShopApp-0.0.1-SNAPSHOT.jar in the 'target' directory using command: 'java -jar MyBookShopApp-0.0.1-SNAPSHOT.jar --server-port=8085'



## Authors

Developed by Rifat Galliamov

docker compose up -d ollama
# Ждем ~10 секунд пока Ollama запустится
docker exec ollama-rag ollama pull qwen:7b
docker exec ollama-rag ollama pull nomic-embed-text
# Затем запускаем остальные сервисы
docker compose up -d

docker exec rag-api find / -type f -name "*.py" -o -name "*.java" -o -name "*.kt" -o -name "*.md" -o -name "*.txt" | head -20


