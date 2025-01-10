# Documentation

## I. Architecture
The refactored code implements an MVC (Model-View-Controller) architecture. The main models (`Equipe`, `Match`, `Tournoi`) represent the core entities. Views manage the GUI using `JPanel` components, while controllers use DAOs and strategies to interact with the persistence layer and application logic.

---

## II. Persistence, Utils, and Views

### 1. **JDBCTemplate**
- Implements the Singleton pattern to ensure only one instance interacts with the database connection (`Connector` class).
- Provides generic `Query` methods for executing SQL commands and retrieving data as objects mapped using `IRowMapper` interface.
- This separation ensures no matter how we change the database management system, the rest of the components won't be touched.

### 2. **Utils**
- **AppContext**: Centralizes the state, such as the current tournament, simplifying access for different panels and logic layers.
- **DateFormatter**: Formats dates consistently across the application.
- **LanguageSelector** and **ResourceLoader**: Dynamically handle multi-language support, loading localized resources at runtime.
- **Validator**: Enforces input constraints to ensure the integrity of data across models.

### 3. **Views**
The **Views** section defines how the user interacts with the application, presenting data and handling user input. Views are built using `JPanel` components, organized into different panels to display tournament details, team management, and match management, among other features.

- **MainFrame**: 
  - The main container window for the application, using a `CardLayout` to switch between various views.
  - It includes `TournamentSelectionPanel` for selecting tournaments, `TeamManagementPanel` for managing teams, and other panels like `TournamentDetailsPanel` to display the details of a selected tournament.
  - Each view is linked to the main frame, ensuring that the user can navigate seamlessly between different sections of the application.

- **TournamentSelectionPanel**: 
  - Allows users to select, delete, or create tournaments.
  - Displays a list of available tournaments and provides buttons to manage them (create, select, delete).
  - This panel uses a `JList` for displaying the tournaments and dynamically updates button states depending on user interaction.

- **TournamentDetailsPanel**: 
  - Displays detailed information about the selected tournament, including its name, status, and the number of matches.
  - Uses `JLabel` components to display tournament details, and action buttons navigate users to other related panels (such as `TeamManagementPanel`).

- **MatchManagementPanel**: 
  - Allows users to manage the matches within a tournament.
  - Provides functionality for adding match by entering each team's score.

- **ResultManagementPanel**: 
  - Focuses on displaying and managing the leaderboard, while ensuring order by won games and total score.

- **TeamManagementPanel**: 
  - Manages the teams participating in a tournament.
  - Allows adding teams and generating tournament macthes.

Each view is designed to be modular, allowing for easy expansion and updates as the application grows. The use of **ResourceBundle** allows dynamic internationalization support, ensuring that the user interface adapts based on the selected language.

---

## III. Design Patterns

### 1. **Strategy**
- Each button action in the GUI uses a specific strategy. For instance:
  - **AddMatchScoresStrategy**: Updates the scores of a match.
  - **AddTeamStrategy**: Adds a new team to the current tournament.
  - **CreateTournamentStrategy**: Creates a new tournament.
  - **DeleteTournamentStrategy**: Deletes a selected tournament.
  - **SelectTournamentStrategy**: Selects a tournament and loads its details.
  - **ValidateTeamsStrategy**: Validates the teams in a tournament.

### 2. **DAO**
- Follows the Data Access Object pattern for separation of concerns.
- **AbstractDAO** provides a generic framework, with classes like `MatchDAO`, `TournoiDAO`, and `EquipeDAO` implementing CRUD operations for their respective entities. Appending a ready `JDBCTemplate`.

### 3. **Observer**
- The `AppContext` acts as a lightweight observer, enabling panels to react to changes in the current tournament state.

### 4. **Singleton**
- Used in `JDBCTemplate` and `Connector` to manage database connections and prevent multiple instances from conflicting.
- Also seen in `FactoryDAO` for providing a single instance of each DAO.

### 5. **Factory**
- **FactoryDAO** uses a static inner class for lazy instantiation, providing DAOs for matches, teams, and tournaments.

### 6. **RowMappers**
- Simplifies mapping of `ResultSet` data to Java objects (`Equipe`, `Match`, `Tournoi`) using `IRowMapper` implementations.

---

This design ensures modularity, maintainability, and scalability, adhering to best practices for software engineering.

