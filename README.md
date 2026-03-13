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
<img width="1536" height="1024" alt="MY ERD" src="https://github.com/user-attachments/assets/3143714e-d6fe-4f02-a36e-42aa38bfb425" />

---

## 📖 Project Development Journey

### Phase 1: Initial Setup & Database Configuration

**Challenge**: Started with H2 in-memory database, needed to migrate to PostgreSQL for production-ready persistence.

**Actions Taken**:

1. Created database: `personal_finance_db`
2. Configured `application.properties` with PostgreSQL connection
3. Set `spring.jpa.hibernate.ddl-auto=update` to preserve data

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

<img width="1246" height="396" alt="image" src="https://github.com/user-attachments/assets/af7e8024-1383-470b-8657-a89c3ed36669" />


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

<img width="1067" height="858" alt="image" src="https://github.com/user-attachments/assets/8c458e52-2da7-49d9-8d53-5e0c1fb37cab" />



#### 2.3 Account Entity
**Purpose**: Store user's financial accounts (banks, mobile money)

**Key Fields**:
- `id` (Long, auto-increment)
- `name` (e.g., "Bank of Kigali", "MTN Mobile Money")
- `balance` (automatically updated when transactions are created)
- `user` (ManyToOne relationship)

<img width="508" height="566" alt="image" src="https://github.com/user-attachments/assets/12844a94-2725-4f76-8e9b-d0ee39408c5e" />


#### 2.4 Transaction Entity
**Purpose**: Record financial transactions

**Key Fields**:
- `id` (Long, auto-increment)
- `amount`, `date`, `description`
- `account` (ManyToOne - which account the transaction belongs to)
- `category` (ManyToOne - transaction category)
- `location` (ManyToOne - where transaction occurred)

**Critical Feature**: Automatic balance deduction implemented in `TransactionService`

<img width="1066" height="862" alt="image" src="https://github.com/user-attachments/assets/af1c4090-4d13-43db-8238-6c015dd18d5d" />



#### 2.5 Category Entity
**Purpose**: Categorize transactions (Food, Transport, Housing, etc.)

**Enhancement**: Added Many-to-Many relationship with User for favorite categories

<img width="666" height="460" alt="image" src="https://github.com/user-attachments/assets/4053f242-3847-4cf9-ae53-3a37a0bb5c35" />



#### 2.6 Budget Entity
**Purpose**: Store user budget allocations per category

<img width="392" height="473" alt="image" src="https://github.com/user-attachments/assets/dbf88d10-1e67-4fcc-ae3e-33abdcb1c1a4" />



#### 2.7 UserProfile Entity (NEW)
**Purpose**: Extended user information (One-to-One relationship)

**Key Fields**:
- `id` (Long, auto-increment)
- `user` (OneToOne with unique constraint)
- `bio`, `dateOfBirth`, `occupation`, `profilePictureUrl`

---

<img width="1239" height="391" alt="image" src="https://github.com/user-attachments/assets/63405c90-a744-4424-9101-68c794862682" />

#### 2.8 UserFavoriteCategory Entity
**Purpose**: Store user favorite categories

<img width="453" height="705" alt="image" src="https://github.com/user-attachments/assets/0cb0bdfe-f4fb-4ce4-bd22-9111cd5cf108" />




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

**Data Characteristics**:
- Realistic Rwandan context (Bank of Kigali, Equity Bank, MTN, Airtel)
- RWF currency amounts
- Diverse transaction categories
- Hierarchical location data (Kigali, Gasabo, Kimironko, etc.)

---



## ✨ Features Implemented

### 1. Entity Relationship Diagram  
- 8 tables with clear relationships
- Proper foreign key constraints
- Self-referencing hierarchy for locations

### 2. Location Saving 
- Hierarchical structure (Province → District → Sector)
- Self-referencing ManyToOne relationship
- UUID primary key with auto-generation

### 3. Pagination & Sorting 
- Implemented on Transaction endpoints
- Configurable page size, page number, sort field, sort direction
- Returns `Page<T>` with metadata (totalElements, totalPages, etc.)
- Performance optimization for large datasets

### 4. Many-to-Many Relationship 
- User ↔ Category (favorite categories)
- Join table: `user_favorite_categories`
- Endpoints to add/remove favorites

### 5. One-to-Many Relationship
- User → Accounts
- User → Transactions
- Account → Transactions
- Location → Users

### 6. One-to-One Relationship 
- User ↔ UserProfile
- Unique constraint on foreign key
- Extended user information storage

### 7. existBy() Methods 
- `existsByEmail()` - Check email availability
- `existsByUsername()` - Check username availability
- `existsByUserId()` - Check if profile exists
- Efficient COUNT queries instead of full entity retrieval

### 8. Location-based User Retrieval 
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

**Register new user**

<img width="1543" height="909" alt="image" src="https://github.com/user-attachments/assets/862508f0-be7f-4cc6-a34a-ab72562d67ae" />


**Get user by ID**

<img width="1540" height="947" alt="image" src="https://github.com/user-attachments/assets/39e33182-5525-4ae3-8da6-245aa728b7a6" />


**Update user**

<img width="1534" height="929" alt="image" src="https://github.com/user-attachments/assets/71174253-fcb0-4a2f-8e32-b367796a73dc" />


**Delete user**

<img width="1539" height="920" alt="image" src="https://github.com/user-attachments/assets/61c438b1-3da0-4b09-bc25-cb956c2afcdc" />


**Check email exists**

<img width="1542" height="930" alt="image" src="https://github.com/user-attachments/assets/f226669d-236d-49be-a1a5-417af128c285" />


**Check username exists**

<img width="1540" height="945" alt="image" src="https://github.com/user-attachments/assets/c7ee7a51-6d54-4442-8642-55af9f13451a" />


**Get users by location code**

<img width="1540" height="953" alt="image" src="https://github.com/user-attachments/assets/0449e1dd-bdad-405f-9a39-43e5710a5050" />


**Get users by location name**

<img width="1537" height="951" alt="image" src="https://github.com/user-attachments/assets/e9f4e5d7-1baa-44f1-b546-e91b80b5d9d1" />


**Add favorite category**

<img width="1538" height="941" alt="image" src="https://github.com/user-attachments/assets/5229a0f8-1865-41ed-b71c-124aafdad5ec" />


**Remove favorite category**

<img width="1538" height="941" alt="image" src="https://github.com/user-attachments/assets/3289cfd5-c376-46dc-b483-8ed20d58cadc" />



### UserProfile Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/user-profiles` | Create user profile |
| GET | `/api/user-profiles/user/{userId}` | Get user's profile |
| GET | `/api/user-profiles/exists/user/{userId}` | Check profile exists |
| PUT | `/api/user-profiles/user/{userId}` | Update profile by user ID |

**Create user profile**

<img width="1528" height="769" alt="image" src="https://github.com/user-attachments/assets/b8139e20-dcbc-4eaa-946b-e11410962b14" />


**Get user's profile**

<img width="1524" height="924" alt="image" src="https://github.com/user-attachments/assets/fec07fe3-58db-499a-9999-a9417f253aee" />


**Check profile exists**

<img width="1533" height="462" alt="image" src="https://github.com/user-attachments/assets/adc37d1d-54a1-4b66-9e77-e23bbc8d7c8b" />


**Update profile by user ID**

<img width="1531" height="923" alt="image" src="https://github.com/user-attachments/assets/827dd0c1-655a-426f-b8ac-cfc265c4f095" />


### Account Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts` | Create account |
| GET | `/api/accounts` | Get all accounts |
| GET | `/api/accounts/user/{userId}` | Get user's accounts |
| GET | `/api/accounts/user/{userId}/total-balance` | Get user's total balance |

**Create account**

<img width="1536" height="779" alt="image" src="https://github.com/user-attachments/assets/62923c1c-8a9d-4b32-93ff-80bc65199e57" />


**Get all accounts**

<img width="1530" height="922" alt="image" src="https://github.com/user-attachments/assets/a5d07e20-4a6d-4741-a5ea-8d10c76cc883" />


**Get user's accounts**

<img width="1508" height="913" alt="image" src="https://github.com/user-attachments/assets/c4f1c7a7-7b42-4077-ba7d-5aece9045521" />


**Get user's total balance**

<img width="1534" height="920" alt="image" src="https://github.com/user-attachments/assets/762ca23a-f3d0-4547-8e83-b2b98fe3216f" />


### Transaction Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create transaction |
| GET | `/api/transactions/history/{accountId}` | Get account transactions |
| GET | `/api/transactions/paginated?page=0&size=10&sortBy=date&sortDirection=DESC` | Get all transactions (paginated) |
| GET | `/api/transactions/account/{accountId}/paginated` | Get account transactions (paginated) |
| GET | `/api/transactions/user/{userId}/paginated` | Get user transactions (paginated) |

**Create transaction**

<img width="1535" height="913" alt="image" src="https://github.com/user-attachments/assets/624754f1-b1ca-4576-9ed9-6af5170fe2af" />


**Get account transactions**

<img width="1528" height="926" alt="image" src="https://github.com/user-attachments/assets/b72cac04-7d75-4b51-b6dd-6d337b287e03" />


**Get all transactions (paginated)**

<img width="1532" height="920" alt="image" src="https://github.com/user-attachments/assets/f8a369fb-6220-4dcf-85a7-2bac4add9cfa" />


**Get account transactions (paginated)**

<img width="1532" height="927" alt="image" src="https://github.com/user-attachments/assets/a76a76b9-fda3-4ee6-80c3-2c01f49538bb" />


**Get user transactions (paginated)**

<img width="1530" height="937" alt="image" src="https://github.com/user-attachments/assets/5ac64a35-7e5f-48c5-a5f4-d7953bd57c97" />


### Location Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/locations` | Create location(Province) |
| POST | `/api/locations` | Create location(District) |
| POST | `/api/locations` | Create location(Sector) |
| POST | `/api/locations` | Create location(Cell) |
| POST | `/api/locations` | Create location(Village) |
| PUT | `/api/locations/{id}` | Update location |
| GET | `/api/locations` | Get all locations |


**Create location(Province)**

<img width="1540" height="921" alt="image" src="https://github.com/user-attachments/assets/0ce9db01-0439-4a73-8ca9-487755c5818c" />


**Create location(District)**

<img width="1540" height="918" alt="image" src="https://github.com/user-attachments/assets/e20bac68-4d10-488f-bbf6-b27087b4fe2f" />


**Create location(Sector)**

<img width="1502" height="905" alt="image" src="https://github.com/user-attachments/assets/e8431b3e-7e4c-4bfa-b040-6613679a1925" />


**Create location(Cell)**

<img width="1491" height="895" alt="image" src="https://github.com/user-attachments/assets/266860b5-ac26-46fe-b918-9d7cb4a86c4f" />


**Create location(Village)**

<img width="1539" height="902" alt="image" src="https://github.com/user-attachments/assets/6f74cb3b-48da-451d-ba06-a233be27017b" />


**UPdate Location**

<img width="1509" height="873" alt="image" src="https://github.com/user-attachments/assets/a52366f5-799e-4ee2-8e04-488ff3ab48e4" />


**Get all locations**

<img width="1509" height="873" alt="image" src="https://github.com/user-attachments/assets/024695ab-d653-4507-b40d-bbfc531f4a15" />


### Category Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/categories` | Create category |
| GET | `/api/categories` | Get all categories |

**Create category**

<img width="1543" height="763" alt="image" src="https://github.com/user-attachments/assets/8d466d6d-71c1-4c4e-971d-a2a8435f2b4a" />

**Get all categories**

<img width="1482" height="903" alt="image" src="https://github.com/user-attachments/assets/7a8b3505-ff71-4559-abb2-c3cf6a20c5a9" />


### Budget Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/budgets` | Create budget |

**Create budget**

<img width="1497" height="785" alt="image" src="https://github.com/user-attachments/assets/b8b432cf-aded-4a0b-9ed4-15a372da7c93" />


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
   spring.datasource.username=postgres
   spring.datasource.password=amos123
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



## 📚 Additional Documentation

For detailed technical explanations of each implementation, refer to:
- **PRACTICAL_ASSESSMENT_DOCUMENTATION.md** - Comprehensive guide for viva-voce preparation

---

## 👨‍💻 Author

**Project Developer**: AMOS NKURUNZIZA 
**Course**: Web Technology and Internet
**Institution**: AUCA
**Year**: 2026

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

**Last Updated**: March 2026  
**Version**: 1.0.0  
**Status**: ✅ Complete and Production Ready
