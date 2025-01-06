## To see our remarks on the old version go to "Remarks.md" 
## To see changes made go to "updates_summary.md"
## How to Run the Project

1. **Set Up MySQL**
   - Ensure that MySQL is installed and running on your machine.
   - Create a new database for the project using the following command in your MySQL shell:
     ```sql
     CREATE DATABASE your_database_name;
     ```

2. **Configure Database Connection**
   - Update the database connection settings in your project (usually found in a configuration file or within the `Belote.java` class) to match your MySQL credentials:
     ```java
     String url = "jdbc:mysql://localhost:3306/your_database_name";
     String username = "your_username";
     String password = "your_password";
     ```

3. **Import MySQL Connector**
   - Ensure that the MySQL Connector/J (JAR file) is included in your project’s build path. You can download it from [MySQL's official site](https://dev.mysql.com/downloads/connector/j/).

4. **Build the Project**
   - In Eclipse, right-click on your project and select **Build Project** to compile the code.

5. **Run the Project**
   - To run the project, right-click on the main class (e.g., `Belote.java`) and select **Run As** > **Java Application**.
