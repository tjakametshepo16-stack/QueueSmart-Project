# QueueSmart — Java + MySQL + HTML/CSS

QueueSmart is now a browser-based frontend backed by Java, JDBC and MySQL.

## Stack
- Java 23
- Java built-in HTTP server
- HTML5 + CSS3 + vanilla JavaScript
- MySQL 8+
- MySQL Connector/J 9.4.0
- Maven

## 1. Create the database
Open MySQL Workbench, open `sql/schema.sql`, and run the whole script.

It creates the `queuesmart` database using the same structure requested for QueueSmart:
`roles`, `users`, `departments`, `students`, `staff`, `services`, `appointments`,
`queue_entries`, `laptops`, `laptop_allocations`, `feedback`, `notifications`,
and `audit_logs`.

Demo accounts:
- admin / admin123
- staff01 / staff123
- student1 / student123

## 2. MySQL connection
`src/main/resources/db.properties` is configured for:
- user: root
- password: Tj@kametshep0
- database: queuesmart
- port: 3306

If your local MySQL root password is different, change only `db.password`.

## 3. Run
In the QueueSmart folder:
```text
mvn clean package
java -jar target/QueueSmart-jar-with-dependencies.jar
```

Then open:
`http://localhost:8080`

Or use NetBeans: open the Maven project and run `queuesmart.Main`.

## Main features
### Student
- Login/register
- Join a live queue
- See queue number and position
- Book/cancel appointments
- Submit feedback

### Staff
- See today's queue
- Call next student
- Complete service
- Allocate available laptops

### Admin
- View average feedback
- View audit activity
- Register staff

The frontend is intentionally plain HTML/CSS/JavaScript so it is easy to demonstrate, edit and explain for the Java coursework.
