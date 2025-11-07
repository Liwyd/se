# University Library Management System

This is a console-based University Library Management System implemented in Java.

## Features

### Roles

1. **Guest**
   - View registered students count
   - Search books by title
   - View statistics

2. **Student**
   - Register
   - Login
   - Search books (title, year, author, combined)
   - Create borrow request
   - View own borrow requests

3. **Employee**
   - Login
   - Change password
   - Add book
   - Search and edit book
   - Review/approve/reject borrow requests
   - View student report
   - Toggle student active status
   - Receive returned book

4. **Admin**
   - Add employee
   - View employee performance
   - View borrow statistics
   - View student statistics

## Build and Run

Prerequisites: Java 17+ and Maven.

```bash
mvn clean package
mvn exec:java -Dexec.mainClass=com.university.library.LibraryManagementSystem
```

## Usage

### Admin login
- Username: `admin`
- Password: `admin123`

### Notes
- Data is stored in memory; it resets when the app stops.
- This is a CLI application.

## Project Structure

```
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/university/library/
                ├── LibraryManagementSystem.java
                └── modules/
                    ├── AdminManager.java
                    ├── BookManager.java
                    ├── BorrowManager.java
                    ├── EmployeeManager.java
                    ├── GuestManager.java
                    └── StudentManager.java
```

## Future Work

- Add a database for persistence
- GUI front-end
- Advanced reporting
- Book categories management
- Late return penalty system
