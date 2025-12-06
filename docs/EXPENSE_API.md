# Expense Management API Documentation

## Base URL
```
http://localhost:8080/api/v1/expense
```

## Authentication
All endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <token>
```

## Endpoints

### 1. Get All Expenses
Retrieve all expenses (Admin only).

**Endpoint:** `GET /`

**Authorization:** `ROLE_ADMIN`

**Response:**
```json
[
  {
    "id": 1,
    "concept": "Conference Travel",
    "note": "Business trip to tech conference",
    "date": "2024-12-06T10:30:00",
    "amount": 150.50,
    "status": "PENDING",
    "approvedBy": null,
    "approvalDate": null,
    "rejectionReason": null,
    "createdAt": "2024-12-06T08:00:00",
    "updatedAt": "2024-12-06T08:00:00",
    "employee": {
      "id": 1,
      "name": "John",
      "surname": "Doe",
      "email": "john.doe@example.com"
    },
    "attachments": []
  }
]
```

### 2. Get Expenses by Employee
Retrieve all expenses for a specific employee.

**Endpoint:** `GET /employee/{employeeId}`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER` (own expenses only)

**Parameters:**
- `employeeId` (path) - Employee ID

**Response:** Same as Get All Expenses

### 3. Get Expense by ID
Retrieve a specific expense by its ID.

**Endpoint:** `GET /{expenseId}`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER` (own expenses only)

**Parameters:**
- `expenseId` (path) - Expense ID

**Response:** Single expense object

### 4. Create Expense
Create a new expense.

**Endpoint:** `POST /`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER`

**Request Body:**
```json
{
  "concept": "Conference Travel",
  "note": "Business trip to tech conference",
  "date": "2024-12-06T10:30:00",
  "amount": 150.50,
  "employee": {
    "id": 1
  }
}
```

**Validations:**
- `concept`: Required, 3-128 characters
- `note`: Optional, max 500 characters
- `date`: Required, must be present or past
- `amount`: Required, must be > 0
- `employee`: Required

**Response:** Created expense object with status 201

### 5. Update Expense
Update an existing expense. Only pending expenses can be updated.

**Endpoint:** `PUT /`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER` (own expenses only)

**Request Body:**
```json
{
  "id": 1,
  "concept": "Updated Conference Travel",
  "note": "Updated description",
  "date": "2024-12-06T10:30:00",
  "amount": 175.50,
  "employee": {
    "id": 1
  }
}
```

**Business Rules:**
- Cannot update approved or rejected expenses
- Only the expense owner or admin can update

**Response:** Updated expense object

### 6. Delete Expense
Delete an expense (Admin only).

**Endpoint:** `DELETE /{expenseId}`

**Authorization:** `ROLE_ADMIN`

**Parameters:**
- `expenseId` (path) - Expense ID

**Response:**
```json
true
```

### 7. Approve Expense
Approve a pending expense (Admin only).

**Endpoint:** `PUT /{expenseId}/approve`

**Authorization:** `ROLE_ADMIN`

**Parameters:**
- `expenseId` (path) - Expense ID

**Business Rules:**
- Only pending expenses can be approved
- Approval sets status to APPROVED, records approver email and approval date

**Response:** Approved expense object

**Error Responses:**
- `403 Forbidden` - Non-admin trying to approve
- `400 Bad Request` - Expense is not pending

### 8. Reject Expense
Reject a pending expense with a reason (Admin only).

**Endpoint:** `PUT /{expenseId}/reject`

**Authorization:** `ROLE_ADMIN`

**Parameters:**
- `expenseId` (path) - Expense ID

**Request Body:**
```json
{
  "reason": "Missing receipts or insufficient documentation"
}
```

**Business Rules:**
- Only pending expenses can be rejected
- Rejection reason is required
- Rejection sets status to REJECTED, records approver email and rejection reason

**Response:** Rejected expense object

**Error Responses:**
- `403 Forbidden` - Non-admin trying to reject
- `400 Bad Request` - Missing reason or expense is not pending

### 9. Get Expenses by Status
Retrieve all expenses with a specific status (Admin only).

**Endpoint:** `GET /status/{status}`

**Authorization:** `ROLE_ADMIN`

**Parameters:**
- `status` (path) - One of: `PENDING`, `APPROVED`, `REJECTED`

**Response:** Array of expenses with the specified status

**Error Responses:**
- `400 Bad Request` - Invalid status value

### 10. Upload Attachment
Upload a file attachment for an expense.

**Endpoint:** `POST /{expenseId}/attachment`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER` (own expenses only)

**Content-Type:** `multipart/form-data`

**Parameters:**
- `expenseId` (path) - Expense ID
- `file` (form-data) - File to upload

**Response:**
```json
{
  "id": 1,
  "fileName": "receipt.pdf",
  "filePath": "/uploads/expenses/uuid-receipt.pdf",
  "fileType": "application/pdf",
  "fileSize": 12345,
  "uploadedAt": "2024-12-06T10:30:00"
}
```

**Configuration:**
- Default upload directory: `uploads/expenses`
- Can be configured via `expense.upload.dir` property

### 11. Get Attachments
Retrieve all attachments for an expense.

**Endpoint:** `GET /{expenseId}/attachment`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER` (own expenses only)

**Parameters:**
- `expenseId` (path) - Expense ID

**Response:** Array of attachment objects

### 12. Delete Attachment
Delete a file attachment.

**Endpoint:** `DELETE /attachment/{attachmentId}`

**Authorization:** `ROLE_ADMIN` or `ROLE_USER` (own expenses only)

**Parameters:**
- `attachmentId` (path) - Attachment ID

**Response:**
```json
true
```

**Note:** Deletes both the database record and the physical file.

## Expense Status Workflow

```
┌─────────┐
│ PENDING │ (Initial state)
└────┬────┘
     │
     ├──── Admin Approves ────► APPROVED (final)
     │
     └──── Admin Rejects ─────► REJECTED (final)
```

### Status Descriptions

- **PENDING**: Expense has been submitted but not yet reviewed
- **APPROVED**: Expense has been reviewed and approved by an admin
- **REJECTED**: Expense has been reviewed and rejected by an admin

### Workflow Rules

1. **Creation**: All expenses start with `PENDING` status
2. **Editing**: Only `PENDING` expenses can be edited
3. **Approval**: Only admins can approve expenses; sets `approvedBy`, `approvalDate`
4. **Rejection**: Only admins can reject expenses; requires a `rejectionReason`
5. **Finality**: Once approved or rejected, expenses cannot be modified or changed status

## Error Responses

All endpoints follow consistent error response format:

```json
{
  "error": "Error message describing what went wrong"
}
```

### HTTP Status Codes

- `200 OK` - Successful GET/PUT/DELETE
- `201 Created` - Successful POST
- `400 Bad Request` - Validation error or invalid state
- `403 Forbidden` - Authorization error
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Security Considerations

### Role-Based Access Control

- **ROLE_ADMIN**:
  - Can view all expenses
  - Can approve/reject expenses
  - Can delete any expense
  - Can view expenses by status

- **ROLE_USER**:
  - Can view own expenses only
  - Can create expenses
  - Can update own pending expenses
  - Can upload/delete attachments for own expenses

### Authorization Flow

1. JWT token is extracted from `Authorization` header
2. User email and roles are extracted from token claims
3. For employee-specific operations, employee email is matched with token email
4. Admin role bypasses employee email check

## Examples

### Create an Expense (cURL)

```bash
curl -X POST http://localhost:8080/api/v1/expense \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "concept": "Office Supplies",
    "note": "Purchased notebooks and pens",
    "date": "2024-12-06T14:30:00",
    "amount": 45.99,
    "employee": {
      "id": 1
    }
  }'
```

### Approve an Expense (cURL)

```bash
curl -X PUT http://localhost:8080/api/v1/expense/1/approve \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

### Upload Attachment (cURL)

```bash
curl -X POST http://localhost:8080/api/v1/expense/1/attachment \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/receipt.pdf"
```

## Testing

### Unit Tests

Run unit tests with Maven:
```bash
cd backend-springboot
./mvnw test -Dtest=ExpenseServiceTest
```

Tests cover:
- CRUD operations
- Approval/rejection workflow
- Security and authorization
- Validation rules
- Attachment handling

### Integration Tests

```bash
./mvnw test -Dtest=ExpenseTest
```

Tests cover:
- Database persistence
- Entity relationships
- Status workflow
- Query methods

## Configuration

### Application Properties

```properties
# Upload directory for expense attachments
expense.upload.dir=uploads/expenses

# JWT secret key
app.jwt.secret=your-secret-key
```

## Changelog

### Version 1.1.0
- Added approval workflow (PENDING → APPROVED/REJECTED)
- Added attachment support
- Enhanced validation and error handling
- Added comprehensive logging with SLF4J
- Improved security checks
- Added status filtering for admins
