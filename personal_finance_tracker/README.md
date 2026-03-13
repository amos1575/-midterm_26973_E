# Personal Finance Tracker - Spring Boot Application

A comprehensive personal finance management system built with Spring Boot and PostgreSQL, demonstrating advanced database relationships, RESTful API design, and modern backend development practices.

---

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Database Schema](#database-schema)
- [Project Development Journey](#project-development-journey)
- [Features Implemented](#features-implemented)
- [API Endpoints](#api-endpoints)
- [Setup Instructions](#setup-instructions)
- [Sample Data](#sample-data)
- [Key Learning Outcomes](#key-learning-outcomes)

---

## 🎯 Project Overview

The Personal Finance Tracker is a backend application that allows users to manage their financial accounts, track transactions, set budgets, and categorize expenses. The system supports multiple users with hierarchical location data (Province → District → Sector) based on Rwanda's administrative structure.

**Academic Context**: This project fulfills the requirements for a Spring Boot practical assessment, demonstrating proficiency in:
- Entity Relationship Modeling
- Database Relationships (One-to-One, One-to-Many, Many-to-Many)
- Spring Data JPA
- RESTful API Development
- Pagination and Sorting
- Custom Query Methods

---

## 🛠 Technologies Used

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 21
- **Database**: PostgreSQL 18.1
- **ORM**: Hibernate (Spring Data JPA)
- **Build Tool**: Maven
- **Server**: Apache Tomcat (Embedded)
- **Port**: 8082

---

## 🗄 Database Schema

### Tables (8 Total):

1. **users** - User account information
2. **location** - Hierarchical location data (Province, District, Sector)
3. **account** - User financial accounts (banks, mobile money)
4. **transaction** - Financial transactions
5. **category** - Transaction categories
6. **budget** - User budget allocations
7. **user_profile** - Extended user profile information
8. **user_favorite_categories** - Join table for user-category favorites

### Entity Relationship Diagram:

```
User (1) ←→ (1) UserProfile          [One-to-One]
User (1) ←→ (*) Account              [One-to-Many]
User (1) ←→ (*) Transaction          [One-to-Many]
User (*) ←→ (*) Category             [Many-to-Many via user_favorite_categories]
Account (1) ←→ (*) Transaction       [One-to-Many]
Category (1) ←→ (*) Transaction      [Many-to-One]
Location (1) ←→ (*) User             [Many-to-One]
Location (1) ←→ (1) Location         [Self-referencing for hierarchy]
```

---

## 📖 Project Development Journey

### Phase 1: Initial Setup & Database Configuration

**Challenge**: Started with H2 in-memory database, needed to migrate to PostgreSQL for production-ready persistence.

**Actions Taken**:
1. Installed PostgreSQL 18.1
2. Created database: `personal_finance_db`
3. Configured `application.properties` with PostgreSQL connection
4. Resolved password authentication issues by modifying `pg_hba.conf`
5. Set `spring.jpa.hibernate.ddl-auto=update` to preserve data

**Configuration**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/personal_finance_db
spring.datasource.username=postgres
spring.datasource.password=amos123
spring.jpa.hibernate.ddl-auto=update
server.port=8082
```

---

### Phase 2: Entity Design & Relationships

#### 2.1 User Entity
**Initial Design**: Used UUID for primary key, included `profileId` field
**Final Design**: 
- Changed to `Long` (bigint) for better performance and simplicity
- Removed `profileId` field (replaced with One-to-One relationship)
- Renamed `province` to `location` for better semantics
- Added `@JsonIgnore` to prevent circular references

**Key Fields**:
- `id` (Long, auto-increment)
- `username` (unique, not null)
- `password` (not null)
- `email`, `name`, `phoneNumber`
- `location` (ManyToOne relationship)
- `accounts` (OneToMany relationship)
- `userProfile` (OneToOne relationship)
- `favoriteCategories` (ManyToMany relationship)

#### 2.2 Location Entity
**Design**: Hierarchical self-referencing structure for Rwanda's administrative divisions

**Key Features**:
- UUID primary key with auto-generation
- `parentLocation` field for hierarchy (Province → District → Sector)
- Fields: `code`, `name`, `type`

**Example Hierarchy**:
```
Kigali (Province, parent=null)
  └─ Gasabo (District, parent=Kigali)
      └─ Kimironko (Sector, parent=Gasabo)
```

#### 2.3 Account Entity
**Purpose**: Store user's financial accounts (banks, mobile money)

**Key Fields**:
- `id` (Long, auto-increment)
- `name` (e.g., "Bank of Kigali", "MTN Mobile Money")
- `balance` (automatically updated when transactions are created)
- `user` (ManyToOne relationship)

#### 2.4 Transaction Entity
**Purpose**: Record financial transactions

**Key Fields**:
- `id` (Long, auto-increment)
- `amount`, `date`, `description`
- `account` (ManyToOne - which account the transaction belongs to)
- `category` (ManyToOne - transaction category)
- `location` (ManyToOne - where transaction occurred)

**Critical Feature**: Automatic balance deduction implemented in `TransactionService`

#### 2.5 Category Entity
**Purpose**: Categorize transactions (Food, Transport, Housing, etc.)

**Enhancement**: Added Many-to-Many relationship with User for favorite categories

#### 2.6 Budget Entity
**Purpose**: Store user budget allocations per category

#### 2.7 UserProfile Entity (NEW)
**Purpose**: Extended user information (One-to-One relationship)

**Key Fields**:
- `id` (Long, auto-increment)
- `user` (OneToOne with unique constraint)
- `bio`, `dateOfBirth`, `occupation`, `profilePictureUrl`

---

### Phase 3: Repository Layer

Created JPA repositories for all entities with custom query methods:

#### UserRepository
```java
boolean existsByEmail(String email);
boolean existsByUsername(String username);
List<User> findByLocationCode(String code);
List<User> findByLocationName(String name);
```

#### TransactionRepository
```java
Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
@Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId")
Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
```

#### UserProfileRepository
```java
Optional<UserProfile> findByUserId(Long userId);
boolean existsByUserId(Long userId);
```

---

### Phase 4: Service Layer

Implemented business logic for all entities:

#### Key Services:
- **UserService**: User management, favorite categories, location queries
- **AccountService**: Account management, balance calculations
- **TransactionService**: Transaction creation with automatic balance updates, pagination
- **UserProfileService**: Profile management with user ID lookup
- **LocationService**: Hierarchical location management

**Critical Bug Fix**: Initially, account balances weren't updating when transactions were created. Fixed by adding balance deduction logic in `TransactionService.createTransaction()`:

```java
Account fullAccount = accountRepository.findById(account.getId()).orElse(null);
if (fullAccount != null) {
    fullAccount.setBalance(fullAccount.getBalance() - transaction.getAmount());
    accountRepository.save(fullAccount);
}
```

---

### Phase 5: Controller Layer (REST API)

Developed RESTful endpoints for all operations:

#### Pagination & Sorting Implementation
Added advanced query capabilities to Transaction endpoints:

```java
@GetMapping("/paginated")
public Page<Transaction> getAllTransactionsPaginated(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id") String sortBy,
    @RequestParam(defaultValue = "DESC") String sortDirection)
```

**Benefits**:
- Reduces memory usage (only loads requested page)
- Faster query execution (database LIMIT/OFFSET)
- Better user experience with large datasets

---

### Phase 6: Data Population

Created comprehensive sample data files:

1. **Category_Data.md**: 20 transaction categories
2. **Location_data.txt**: Rwanda's hierarchical location structure
3. **Account_Data.md**: 20 Rwandan banks and mobile money providers
4. **Budget_Data.md**: 20 budget allocations
5. **Transaction_Data.md**: 100+ realistic transactions
6. **User Profiles**: 8 user profiles with diverse occupations
7. **Favorite Categories**: User-category associations

**Data Characteristics**:
- Realistic Rwandan context (Bank of Kigali, Equity Bank, MTN, Airtel)
- RWF currency amounts
- Diverse transaction categories
- Hierarchical location data (Kigali, Gasabo, Kimironko, etc.)

---

### Phase 7: Debugging & Issue Resolution

#### Issue 1: User Balance Mismatch
**Problem**: User 4 showed User 2's balance data
**Root Cause**: Accounts created with incorrect `user_id` in database
**Solution**: Verified account ownership via debug endpoint, corrected user_id values

#### Issue 2: JSON Field Case Sensitivity
**Problem**: Location field returning null during user registration
**Root Cause**: JSON used "Location" (capital L) instead of "location"
**Solution**: Ensured JSON field names match Java entity field names exactly

#### Issue 3: Transaction Repository Error
**Problem**: `No property 'userId' found for type 'Transaction'`
**Root Cause**: Transaction doesn't have direct userId field
**Solution**: Used custom JPQL query to navigate relationship: `t.account.user.id`

#### Issue 4: Port 8082 Already in Use
**Problem**: Application failed to start due to port conflict
**Solution**: Used `taskkill /F /PID <process_id>` to free the port

---

## ✨ Features Implemented

### 1. Entity Relationship Diagram (3 Marks) ✅
- 8 tables with clear relationships
- Proper foreign key constraints
- Self-referencing hierarchy for locations

### 2. Location Saving (2 Marks) ✅
- Hierarchical structure (Province → District → Sector)
- Self-referencing ManyToOne relationship
- UUID primary key with auto-generation

### 3. Pagination & Sorting (5 Marks) ✅
- Implemented on Transaction endpoints
- Configurable page size, page number, sort field, sort direction
- Returns `Page<T>` with metadata (totalElements, totalPages, etc.)
- Performance optimization for large datasets

### 4. Many-to-Many Relationship (3 Marks) ✅
- User ↔ Category (favorite categories)
- Join table: `user_favorite_categories`
- Endpoints to add/remove favorites

### 5. One-to-Many Relationship (2 Marks) ✅
- User → Accounts
- User → Transactions
- Account → Transactions
- Location → Users

### 6. One-to-One Relationship (2 Marks) ✅
- User ↔ UserProfile
- Unique constraint on foreign key
- Extended user information storage

### 7. existBy() Methods (2 Marks) ✅
- `existsByEmail()` - Check email availability
- `existsByUsername()` - Check username availability
- `existsByUserId()` - Check if profile exists
- Efficient COUNT queries instead of full entity retrieval

### 8. Location-based User Retrieval (4 Marks) ✅
- `findByLocationCode()` - Get users by location code
- `findByLocationName()` - Get users by location name
- Custom JPQL queries with relationship navigation

---

## 🔌 API Endpoints

### User Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |
| GET | `/api/users/exists/email/{email}` | Check email exists |
| GET | `/api/users/exists/username/{username}` | Check username exists |
| GET | `/api/users/location/code/{code}` | Get users by location code |
| GET | `/api/users/location/name/{name}` | Get users by location name |
| POST | `/api/users/{userId}/favorites/{categoryId}` | Add favorite category |
| DELETE | `/api/users/{userId}/favorites/{categoryId}` | Remove favorite category |

### UserProfile Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/user-profiles` | Create user profile |
| GET | `/api/user-profiles/user/{userId}` | Get user's profile |
| GET | `/api/user-profiles/exists/user/{userId}` | Check profile exists |
| PUT | `/api/user-profiles/{id}` | Update profile by ID |
| PUT | `/api/user-profiles/user/{userId}` | Update profile by user ID |

### Account Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts` | Create account |
| GET | `/api/accounts` | Get all accounts |
| GET | `/api/accounts/user/{userId}` | Get user's accounts |
| GET | `/api/accounts/user/{userId}/total-balance` | Get user's total balance |

### Transaction Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create transaction |
| GET | `/api/transactions/history/{accountId}` | Get account transactions |
| GET | `/api/transactions/paginated?page=0&size=10&sortBy=date&sortDirection=DESC` | Get all transactions (paginated) |
| GET | `/api/transactions/account/{accountId}/paginated` | Get account transactions (paginated) |
| GET | `/api/transactions/user/{userId}/paginated` | Get user transactions (paginated) |

### Location Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/locations` | Create location |
| GET | `/api/locations` | Get all locations |
| PUT | `/api/locations/{id}` | Update location |

### Category Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/categories` | Create category |
| GET | `/api/categories` | Get all categories |

### Budget Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/budgets` | Create budget |

---

## 🚀 Setup Instructions

### Prerequisites
- Java 21 or higher
- PostgreSQL 18.1 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd personal_finance_tracker
   ```

2. **Create PostgreSQL Database**
   ```sql
   CREATE DATABASE personal_finance_db;
   ```

3. **Configure Database Connection**
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/personal_finance_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run from IDE: `PersonalFinanceTrackerApplication.java`

6. **Access the Application**
   
   Base URL: `http://localhost:8082`

### Troubleshooting

**Port 8082 already in use:**
```bash
# Windows
netstat -ano | findstr :8082
taskkill /F /PID <process_id>

# Linux/Mac
lsof -i :8082
kill -9 <process_id>
```

**PostgreSQL Authentication Error:**
- Check `pg_hba.conf` file
- Ensure password is correct
- Restart PostgreSQL service

---

## 📊 Sample Data

### Create a User
```json
POST /api/users/register
{
  "username": "john_doe",
  "password": "password123",
  "email": "john@example.com",
  "name": "John Doe",
  "phoneNumber": "+250788123456",
  "location": {"id": "uuid-of-location"}
}
```

### Create User Profile
```json
POST /api/user-profiles
{
  "user": {"id": 1},
  "bio": "Software Engineer passionate about finance",
  "dateOfBirth": "1995-03-15",
  "occupation": "Software Developer",
  "profilePictureUrl": "https://example.com/photo.jpg"
}
```

### Create Account
```json
POST /api/accounts
{
  "name": "Bank of Kigali - Savings",
  "balance": 500000,
  "user": {"id": 1}
}
```

### Create Transaction
```json
POST /api/transactions
{
  "amount": 15000,
  "date": "2024-03-10",
  "description": "Grocery shopping at Simba Supermarket",
  "account": {"id": 1},
  "category": {"id": 1},
  "location": {"id": "uuid-of-location"}
}
```

### Add Favorite Category
```
POST /api/users/1/favorites/5
```

### Get Paginated Transactions
```
GET /api/transactions/paginated?page=0&size=10&sortBy=date&sortDirection=DESC
```

---

## 🎓 Key Learning Outcomes

### Technical Skills Acquired:

1. **Spring Boot Framework**
   - Dependency injection with `@Autowired`
   - Component scanning and auto-configuration
   - RESTful API development with `@RestController`

2. **Spring Data JPA**
   - Entity mapping with annotations
   - Repository pattern implementation
   - Custom query methods (derived and JPQL)
   - Pagination and sorting

3. **Database Design**
   - Normalization principles
   - Foreign key relationships
   - Join table design for Many-to-Many
   - Self-referencing relationships

4. **Hibernate ORM**
   - Entity lifecycle management
   - Lazy vs Eager loading
   - Cascade operations
   - Bidirectional relationships

5. **PostgreSQL**
   - Database creation and configuration
   - Connection management
   - Authentication setup
   - Query optimization

6. **RESTful API Design**
   - HTTP methods (GET, POST, PUT, DELETE)
   - Request/Response handling
   - Path variables and request parameters
   - JSON serialization/deserialization

7. **Problem Solving**
   - Debugging circular reference issues
   - Resolving foreign key constraints
   - Handling case-sensitive JSON fields
   - Query optimization for relationships

### Best Practices Implemented:

- ✅ Separation of concerns (Controller → Service → Repository)
- ✅ Proper exception handling
- ✅ Use of DTOs to prevent circular references (`@JsonIgnore`)
- ✅ Meaningful endpoint naming conventions
- ✅ Database indexing on foreign keys
- ✅ Pagination for performance optimization
- ✅ Custom query methods for complex operations
- ✅ Comprehensive documentation

---

## 📝 Project Statistics

- **Total Entities**: 7
- **Total Tables**: 8 (including join table)
- **Total Endpoints**: 30+
- **Relationships Implemented**: 8
- **Custom Query Methods**: 6
- **Sample Data Records**: 200+
- **Lines of Code**: ~2000+

---

## 🏆 Assessment Criteria Met

| Criteria | Marks | Status |
|----------|-------|--------|
| ERD with 5+ tables | 3 | ✅ (8 tables) |
| Location saving implementation | 2 | ✅ |
| Sorting & Pagination | 5 | ✅ |
| Many-to-Many relationship | 3 | ✅ |
| One-to-Many relationship | 2 | ✅ |
| One-to-One relationship | 2 | ✅ |
| existBy() methods | 2 | ✅ |
| Location-based user retrieval | 4 | ✅ |
| Viva-Voce preparation | 7 | ✅ |
| **TOTAL** | **30** | **✅** |

---

## 📚 Additional Documentation

For detailed technical explanations of each implementation, refer to:
- **PRACTICAL_ASSESSMENT_DOCUMENTATION.md** - Comprehensive guide for viva-voce preparation

---

## 👨‍💻 Author

**Project Developer**: [Your Name]  
**Course**: Web Technologies  
**Institution**: [Your Institution]  
**Year**: 2024

---

## 📄 License

This project is developed for academic purposes as part of a Spring Boot practical assessment.

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- PostgreSQL Documentation
- Rwanda Administrative Structure for location data
- Rwandan Banking System for realistic account data

---

**Last Updated**: March 2024  
**Version**: 1.0.0  
**Status**: ✅ Complete and Production Ready
