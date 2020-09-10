## Kotlin Server Side Example


#### Background
Software Engineer, specialized in Mobile apps with Flutter, Android Development using Kotlin, and 
backend development mostly in Django.
- ConchaLabs Android Engineer
- Kiwibot Software Engineer

### Overview
I started to learn the Go programming language
after reading many medium posts like [this](https://towardsdatascience.com/why-python-is-not-the-programming-language-of-the-future-30ddc5339b66) 
related to the languages with the best future, after a while I end up stopping, because about 2 things:
- a few years will pass until languages like Go, Rust, or Julia beat Python
- I did not like the language at all, off course, the performance benefits are remarkable, but reminds me so much of C++ :confused:

Go lang [udemy course](https://www.udemy.com/course/go-programming-language/) I used for learning, which 
btw is one of the best courses I've taken in the platform.

I end up watching a LinkedIn post about KMM, which is Kotlin Multiplatform, check more details [here](https://blog.jetbrains.com/kotlin/2020/08/kotlin-multiplatform-mobile-goes-alpha)

As you can see, something like this will be huge for Android Developers like me (Kotlin is my fav language btw), the possibility of 
writing beautiful code, and being able to *use the same business logic code in both iOS and Android applications*. As soon as I
read the article, I thought, I need to get better in Kotlin, ```TODO complete the overview later```
### Branches explanations

1. mutablelist-crud (First steps on kotlin for server side)
    - The main idea was to implement a simple users "CRUD", using a mutableList in order to **FAKE** the database behavior, 
    having a base ```users/``` route with 5 different endpoints on it 
        - GET / -> get all users from the list
        - GET /{id} -> get user by id
        - POST / -> create user
        - PUT /{id} -> update user
        - DELETE /{id} -> delete user
    - Resources used
        * Youtube [PreBeta](https://www.youtube.com/watch?v=fYoqw6EIX6Y&t) channel, pretty good starting point, if you speak spanish
        * Youtube [goobar](https://www.youtube.com/watch?v=zHQ7oBYSHrY) simple request routing example
        
2. db-crud (PostgreSQL CRUD operations with users table)
    
    Having the previous endpoints from the branch ```mutablelist-crud```, my next step was to create a real CRUD. 
    So I end up creating a heroku project, with a free postgres database add on. 
    
    - Create the db connection, for this I used the [Exposed](https://github.com/JetBrains/Exposed) library
    
        Dependencies
        ```groovy
        compile 'org.jetbrains.exposed:exposed:0.17.7'
        compile("org.postgresql:postgresql:42.2.2")
        ```
        Database Connection (Please ignore the credentials, are just here for your ease understanding)
       ```kotlin
        val url = "jdbc:postgresql://ec2-54-172-173-58.compute-1.amazonaws.com:5432/d7dokb84n45r9e?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
        val user = "kyjxbkmfybtuzh"
        val password = "226085995a4d0383cf99a8b71d3284e6fdfb118696ea8c1b5f96b30acb30e2cc"
        Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
      ```
    - Then, create the Users table schema
        ```kotlin
        object Users : Table() {
            val id: Column<Int> = integer("id").autoIncrement().primaryKey()
            val firstName: Column<String> = text("first_name")
            val lastName: Column<String> = text("last_name")
            val age: Column<Int> = integer("age")
        }
        ```
    - Adding flyway for database migrations, we don't want to create manually the database tables.
        Dependency
        ```groovy
          compile 'org.flywaydb:flyway-core:5.2.4'  
      ```
        Below build.gradle buildscript{}
        ```groovy
          plugins {
              id "org.flywaydb.flyway" version "5.2.4"
          }
          
          flyway {
              url = 'jdbc:postgresql://ec2-54-172-173-58.compute-1.amazonaws.com:5432/d7dokb84n45r9e?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory'
              user = 'kyjxbkmfybtuzh'
              password = '226085995a4d0383cf99a8b71d3284e6fdfb118696ea8c1b5f96b30acb30e2cc'
              baselineOnMigrate=true
              locations = ["filesystem:resources/db/migration"]
          }
        ```
        Lastly add the following lines after the Database.Connect
        ```kotlin
          val flyway = Flyway.configure().dataSource(url, user, password).load()
          flyway.migrate()
        ```
    - The Users operations controller, for this, the following function was added to the DbSettings class, in order
    to run the Db operations on the IO Thread using a suspend function, check the ```class UserController```, all db operations are executed
    there.
        ```kotlin
        suspend fun <T> dbQuery(block: () -> T): T =
                withContext(Dispatchers.IO) {
                    transaction { block() }
                }
        ```
    - Lastly, fix the enpoints to not use anymore the mutableList, instead use the usersController class for the CRUD operations
    - Resources used:
        1. https://www.novatec-gmbh.de/en/blog/creating-a-rest-application-with-ktor-and-exposed/
        2. https://www.thebookofjoel.com/kotlin-ktor-exposed-postgres
        
3. basic-authentication
    - Dependencies required:
    ```groovy
       implementation "io.ktor:ktor-auth-jwt:$ktor_version"
    ```
    - Features installed
    ```kotlin
   install(Authentication) {
           basic("basicAuthExample") {
               realm = "ktor"
               validate { credentials ->
                   // Basically, you decide what is a valid user, does not matter how
                   println(credentials)
                   println(UserIdPrincipal(credentials.name))
                   if (credentials.password == "${credentials.name}123") UserIdPrincipal(credentials.name) else null
               }
           }
       }
    ```
    There is something important in here *Basically, you decide what is a valid user, does not matter how*,
     what it means, is that you receive credentials object with property name and password, after that you decide
     what is a valid user for your application.
   - Set authentication in endpoints, for doing that you just have to wrap the route with ```authenticate("name") {route{}}```
   ``` kotlin
    authenticate("basicAuthExample") {
        get {
            call.respond(usersController.getAll())
        }
    }
    ```
   - Now on ```users.http``` in order to use the endpoint ```users/``` the following modification was required
   ``` http request
    ### Get users
    GET http://localhost:8080/users
    Authorization: Basic josancamon19 josancamon19123
    ```