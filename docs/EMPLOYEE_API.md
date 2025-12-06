# Employee API Documentation

## Base URL
```
/api/v1/employee
```

## Authentication
All endpoints require JWT authentication via Bearer token in the Authorization header:
```
Authorization: Bearer <token>
```

## Endpoints

### 1. Get All Employees
Retrieves all employees in the system.

**Endpoint:** `GET /api/v1/employee/`

**Authorization:** Required (ROLE_ADMIN)

**Response:** 200 OK
```json
[
  {
    "id": 1,
    "name": "John",
    "surname": "Doe",
    "email": "john.doe@example.com",
    "birthDate": "1990-01-15T00:00:00",
    "startDate": "2023-01-01",
    "status": "ACTIVE",
    "position": {
      "id": 1,
      "name": "Software Developer"
    }
  }
]
```

**Error Response:** 404 NOT FOUND
```json
{
  "error": "Error. Please, Try it later. It is NOT possible to SHOW all employees"
}
```

---

### 2. Get Employee by ID
Retrieves a specific employee by their ID.

**Endpoint:** `GET /api/v1/employee/{employeeId}`

**Authorization:** Required (ROLE_ADMIN or ROLE_USER)

**Path Parameters:**
- `employeeId` (Long): The ID of the employee

**Response:** 200 OK
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "birthDate": "1990-01-15T00:00:00",
  "startDate": "2023-01-01",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer"
  }
}
```

**Error Response:** 404 NOT FOUND
```json
{
  "error": "Error. Please, Try it later. NOT possible to SHOW the payroll which you find."
}
```

---

### 3. Create Employee
Creates a new employee.

**Endpoint:** `POST /api/v1/employee/`

**Authorization:** Required (ROLE_ADMIN)

**Request Body:**
```json
{
  "name": "Jane",
  "surname": "Smith",
  "email": "jane.smith@example.com",
  "birthDate": "1992-05-20T00:00:00",
  "startDate": "2023-02-01",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer"
  }
}
```

**Validation Rules:**
- `name`: Required, 3-128 characters
- `surname`: Required, 2-255 characters
- `email`: Required, valid email format, unique, max 255 characters
- `birthDate`: Required, must be in the past or present
- `startDate`: Required
- `status`: Required, one of: ACTIVE, INACTIVE, TERMINATED
- `position`: Required

**Response:** 200 OK
```json
{
  "id": 2,
  "name": "Jane",
  "surname": "Smith",
  "email": "jane.smith@example.com",
  "birthDate": "1992-05-20T00:00:00",
  "startDate": "2023-02-01",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer"
  }
}
```

**Error Response:** 400 BAD REQUEST
```json
{
  "error": "Error. Please, Try it later. It is NOT possible to SAVE the employee."
}
```

---

### 4. Update Employee
Updates an existing employee.

**Endpoint:** `PUT /api/v1/employee/{employeeId}`

**Authorization:** Required (ROLE_ADMIN)

**Path Parameters:**
- `employeeId` (Long): The ID of the employee to update

**Request Body:**
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe Updated",
  "email": "john.doe@example.com",
  "birthDate": "1990-01-15T00:00:00",
  "startDate": "2023-01-01",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer"
  }
}
```

**Validation Rules:** Same as Create Employee

**Response:** 200 OK
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe Updated",
  "email": "john.doe@example.com",
  "birthDate": "1990-01-15T00:00:00",
  "startDate": "2023-01-01",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer"
  }
}
```

**Error Response:** 400 BAD REQUEST
```json
{
  "error": "Error. Please, Try it later. It is NOT possible UPDATE the employee who you are looking for."
}
```

---

### 5. Delete Employee
Deletes an employee by their ID.

**Endpoint:** `DELETE /api/v1/employee/{employeeId}`

**Authorization:** Required (ROLE_ADMIN)

**Path Parameters:**
- `employeeId` (Long): The ID of the employee to delete

**Response:** 200 OK
```json
true
```

**Error Response:** 400 BAD REQUEST
```json
{
  "error": "Error. Please, Try it later. It is NOT possible to DELETE the employee."
}
```

---

### 6. Get Employee by Name and Surname
Retrieves an employee by their name and surname (case-insensitive).

**Endpoint:** `GET /api/v1/employee/{name}/{surname}`

**Authorization:** Required (ROLE_ADMIN or ROLE_USER)

**Path Parameters:**
- `name` (String): The first name of the employee
- `surname` (String): The last name of the employee

**Response:** 200 OK
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "birthDate": "1990-01-15T00:00:00",
  "startDate": "2023-01-01",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer"
  }
}
```

**Error Response:** 400 BAD REQUEST
```json
{
  "error": "Error. Please, Try it later. It is NOT possible to FIND the employee who you are looking for."
}
```

---

## Employee Status Values

- `ACTIVE`: Employee is currently active and working
- `INACTIVE`: Employee is temporarily inactive (e.g., on leave)
- `TERMINATED`: Employee has been terminated

---

## Business Rules

1. **Email Uniqueness**: Each employee must have a unique email address
2. **Required Fields**: name, surname, email, birthDate, startDate, status, and position are required
3. **Date Validation**: birthDate must be in the past or present
4. **String Length**: 
   - name: 3-128 characters
   - surname: 2-255 characters
   - email: max 255 characters
5. **Authorization**: 
   - Only ADMIN users can create, update, or delete employees
   - Both ADMIN and USER roles can view employee information

---

## Error Handling

All endpoints follow a consistent error handling pattern:
- **200 OK**: Successful operation
- **400 BAD REQUEST**: Invalid request data or business rule violation
- **401 UNAUTHORIZED**: Missing or invalid authentication token
- **403 FORBIDDEN**: Insufficient permissions
- **404 NOT FOUND**: Resource not found

---

## Frontend Integration

### React Component Example

```javascript
import { employeeService } from '../services/api';

// Get all employees
const loadEmployees = async () => {
  try {
    const response = await employeeService.getAll();
    setEmployees(response.data);
  } catch (error) {
    console.error('Error loading employees:', error);
  }
};

// Create employee
const createEmployee = async (employeeData) => {
  try {
    const response = await employeeService.create(employeeData);
    return response.data;
  } catch (error) {
    console.error('Error creating employee:', error);
  }
};

// Update employee
const updateEmployee = async (id, employeeData) => {
  try {
    const response = await employeeService.update(id, employeeData);
    return response.data;
  } catch (error) {
    console.error('Error updating employee:', error);
  }
};

// Delete employee
const deleteEmployee = async (id) => {
  try {
    await employeeService.delete(id);
  } catch (error) {
    console.error('Error deleting employee:', error);
  }
};
```
