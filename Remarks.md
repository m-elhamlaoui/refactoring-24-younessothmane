
# Project Remarks

## Code Structure, Functionality & Ressource management:
- Database connections and statements are not properly closed, leading to potential resource leaks.
- The approach to handling odd numbers of players is overly complicated and should be abstracted and streamlined.
- The `main` method handles too many responsibilities. Refactoring is needed to separate concerns, such as database setup, GUI handling, and business logic.
- There are no unit tests, making it difficult to validate code changes or ensure that the logic is working correctly.
- Error handling in the GUI is minimal. There is no graceful error handling or detailed feedback when things go wrong.
- The `Match` class is defined but never used, indicating incomplete functionality or unnecessary code.


## Code Quality:
- There's a lack of code documentation and comments.
- The Single Responsibility Principle is not respected, making the code harder to maintain.
- Poor variable naming practices contribute to the difficulty of understanding the code.
- The current code structure makes it hard to add new classes in the future.
- There’s a general lack of class structure, with some classes being excessively long.


## Design Remarks:
- **Singleton Database Layer:** The database manipulation needs a Singleton class for database connections.
- **Encapsulation:** The code lacks proper encapsulation of class attributes.
- **Logging System:** The application requires logging system for tracking events and errors.
- **Dependency Injection for Logging:** Lacks dependency injection to log and manage rounds across classes.
- **Entity-Object Mapping:** There is no mapping layer between DBMS entities and objects.
- **Repository and Business Logic Layer:** The code has database access and business rules in the same method.
- **Observer Pattern:** The code lacks the usage of the Observer pattern for automatic event handling.


## Data Persistence:
- The database only works locally and is memory-based, leading to the risk of data loss (no rollbacks).
- There's no future-proofing, especially when considering switching from the current setup to a legitimate DBMS.
- No password is set for the superadmin user, posing a serious security vulnerability (e.g., DROP database and other potential exploits).
- The absence of a configuration file makes it difficult to edit credentials and database connection links.
- Direct SQL queries are vulnerable to SQL injection. These should be replaced with parameterized queries or an ORM for better security.
- There are no checks for the existence of the SQL file neither the Jdbc Driver before attempting to read them, which can lead to runtime errors.


## SQL & Table Structure:
- There are issues with foreign keys, leading to referential integrity problems.
- The database is poorly structured, with computed values being stored as attributes.
- Incorrect data types are used (e.g., "oui/non" instead of proper booleans).
- SQL logic is mixed directly into the GUI, violating separation of concerns. A separate data access layer should be used.
- The business logic is tightly coupled with the GUI. Adopting design patterns like MVC would help improve separation of concerns.



