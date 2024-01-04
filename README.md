# SQLite Viewer Application

## Overview
The SQLite Viewer is a Java Swing application that allows users to easily interact with SQLite databases. It provides a graphical user interface to open and view database files, browse tables, and execute SQL queries without the need for command-line tools or additional database management software.

## Features
- **Open SQLite Files**: Users can open any SQLite database file using the application's file dialog.
- **Browse Tables**: Upon opening a file, the application lists all available tables in the database.
- **Execute Queries**: Users can execute custom SQL queries and view the results directly within the application.
- **Error Handling**: The application includes comprehensive error handling, providing user-friendly messages for issues such as missing files or incorrect SQL syntax.
- **Table View**: Results of the SQL queries are displayed in a tabular format, allowing for easy data review and analysis.

## Getting Started
### Prerequisites
- Java Runtime Environment (JRE) 8 or later must be installed to run the application.
- SQLite JDBC driver is required and should be included in the classpath.

### Installation
1. Clone the repository or download the source code.
2. Compile the code using your favorite Java IDE or the command line with `javac`.
3. Run the application using `java SQLiteViewer`.

## Usage
1. **Opening a Database File**: Click the "Open" button and select an SQLite database file through the dialog.
2. **Viewing Tables**: Select a table from the dropdown box to view its structure.
3. **Executing SQL Queries**: Enter your SQL query in the text area and click "Execute" to see the results.
