#!/usr/bin/env python3
"""
Seed Runner - Seeds data via REST API respecting Service layer logic.

This script loads JSON seed data and inserts it through the backend API,
ensuring that all business logic (like employee-user auto-linking) is executed.

Usage:
    python seed_runner.py --env dev --api-url http://localhost:8080
    
Environment variables:
    SEED_ENV: Environment to seed (test/dev/prod)
    API_URL: Backend base URL
    ADMIN_EMAIL: Admin email for authentication
    ADMIN_PASSWORD: Admin password
    WAIT_SECONDS: Max seconds to wait for backend
"""

import os
import sys
import json
import time
import argparse
import logging
from pathlib import Path
from typing import Optional, Dict, Any, List

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)
logger = logging.getLogger(__name__)

# Constants
SCRIPT_DIR = Path(__file__).parent
DATA_DIR = SCRIPT_DIR / "data"
SECRETS_DIR = SCRIPT_DIR.parent / "secrets"  # scripts/secrets/ for sensitive data
DEFAULT_API_URL = "http://localhost:8080"
DEFAULT_ADMIN_EMAIL = "admin@bizflowerp.com"
DEFAULT_ADMIN_PASSWORD = "<PASSWORD>"
DEFAULT_WAIT_SECONDS = 60


class SeedRunner:
    """Main seed runner class that handles API seeding."""
    
    def __init__(self, api_url: str, admin_email: str, admin_password: str):
        self.api_url = api_url.rstrip('/')
        self.admin_email = admin_email
        self.admin_password = admin_password
        self.token: Optional[str] = None
        self.session = self._create_session()
        
        # Statistics
        self.stats = {
            "employees_created": 0,
            "employees_skipped": 0,
            "users_created": 0,
            "users_skipped": 0,
            "payrolls_created": 0,
            "payrolls_skipped": 0,
            "expenses_created": 0,
            "expenses_skipped": 0,
        }
    
    def _create_session(self) -> requests.Session:
        """Create a requests session with retry logic."""
        session = requests.Session()
        retry = Retry(
            total=3,
            backoff_factor=0.5,
            status_forcelist=[502, 503, 504]
        )
        adapter = HTTPAdapter(max_retries=retry)
        session.mount('http://', adapter)
        session.mount('https://', adapter)
        return session
    
    def wait_for_backend(self, max_wait: int = 60) -> bool:
        """Wait for backend to be ready."""
        logger.info(f"Waiting for backend at {self.api_url} (max {max_wait}s)...")
        
        start_time = time.time()
        while time.time() - start_time < max_wait:
            try:
                response = self.session.get(f"{self.api_url}/actuator/health", timeout=5)
                if response.status_code == 200:
                    logger.info("✅ Backend is ready!")
                    return True
            except requests.RequestException:
                pass
            
            # Try root endpoint as fallback
            try:
                response = self.session.get(f"{self.api_url}/", timeout=5)
                if response.status_code in [200, 401, 403]:
                    logger.info("✅ Backend is responding!")
                    return True
            except requests.RequestException:
                pass
            
            logger.debug("Backend not ready, waiting...")
            time.sleep(2)
        
        logger.error("❌ Backend did not become ready in time")
        return False
    
    def login(self) -> bool:
        """Login as admin and obtain JWT token."""
        logger.info(f"Logging in as {self.admin_email}...")
        
        try:
            response = self.session.post(
                f"{self.api_url}/api/v1/auth/login",
                json={
                    "email": self.admin_email,
                    "password": self.admin_password
                },
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code == 200:
                data = response.json()
                self.token = data.get("token") or data.get("accessToken")
                if self.token:
                    logger.info("✅ Login successful!")
                    return True
                else:
                    logger.error(f"No token in response: {data}")
                    return False
            else:
                logger.error(f"Login failed: {response.status_code} - {response.text}")
                return False
                
        except requests.RequestException as e:
            logger.error(f"Login request failed: {e}")
            return False
    
    def _auth_headers(self) -> Dict[str, str]:
        """Get headers with authorization token."""
        headers = {"Content-Type": "application/json"}
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        return headers
    
    def load_json_data(self, env: str, entity: str) -> List[Dict[str, Any]]:
        """Load JSON data file for given environment and entity.
        
        For users, loads from scripts/secrets/users_with_passwords/ (gitignored)
        to keep passwords out of version control.
        For other entities (employees, payrolls, expenses), loads from scripts/seeds/data/.
        """
        # Users are loaded from secrets directory (gitignored) for security
        if entity == "users":
            file_path = SECRETS_DIR / "users_with_passwords" / f"{env}_users.json"
            if not file_path.exists():
                logger.error(f"❌ CRITICAL: Users file not found: {file_path}")
                logger.error("   Users with real passwords must be in scripts/secrets/users_with_passwords/")
                logger.error("   Copy from backup or create manually. See scripts/seeds/README.md")
                logger.error("   Aborting seed - cannot proceed without users file.")
                raise FileNotFoundError(f"Required users secrets file not found: {file_path}")
        else:
            file_path = DATA_DIR / env / f"{entity}.json"
        
        if not file_path.exists():
            logger.warning(f"Data file not found: {file_path}")
            return []
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                logger.info(f"Loaded {len(data)} {entity} from {file_path.name}")
                return data
        except json.JSONDecodeError as e:
            logger.error(f"Invalid JSON in {file_path}: {e}")
            return []
    
    def seed_employees(self, employees: List[Dict[str, Any]]) -> None:
        """Seed employees via API."""
        logger.info(f"Seeding {len(employees)} employees...")
        
        for emp in employees:
            try:
                # Check if employee already exists by email
                check_response = self.session.get(
                    f"{self.api_url}/api/v1/employee",
                    headers=self._auth_headers()
                )
                
                existing_emails = []
                if check_response.status_code == 200:
                    existing = check_response.json()
                    if isinstance(existing, list):
                        existing_emails = [e.get("email", "") for e in existing]
                
                if emp.get("email") in existing_emails:
                    logger.debug(f"Employee {emp.get('email')} already exists, skipping")
                    self.stats["employees_skipped"] += 1
                    continue
                
                # Create employee
                response = self.session.post(
                    f"{self.api_url}/api/v1/employee/",
                    json=emp,
                    headers=self._auth_headers()
                )
                
                if response.status_code in [200, 201]:
                    self.stats["employees_created"] += 1
                    logger.debug(f"Created employee: {emp.get('name')} {emp.get('surname')}")
                else:
                    logger.warning(f"Failed to create employee {emp.get('email')}: {response.status_code}")
                    self.stats["employees_skipped"] += 1
                    
            except requests.RequestException as e:
                logger.error(f"Error creating employee: {e}")
                self.stats["employees_skipped"] += 1
        
        logger.info(f"✅ Employees: {self.stats['employees_created']} created, "
                   f"{self.stats['employees_skipped']} skipped")
    
    def seed_users(self, users: List[Dict[str, Any]]) -> None:
        """Seed users via signup endpoint (auto-links with employees by email)."""
        logger.info(f"Seeding {len(users)} users...")
        
        for user in users:
            try:
                # Check if user already exists by email
                check_response = self.session.get(
                    f"{self.api_url}/api/v1/auth/check-email",
                    params={"email": user.get("email")},
                    headers={"Content-Type": "application/json"}
                )
                
                if check_response.status_code == 200:
                    data = check_response.json()
                    if data.get("exists", False):
                        logger.debug(f"User {user.get('email')} already exists, skipping")
                        self.stats["users_skipped"] += 1
                        continue
                
                # Create user via signup (this triggers auto-linking in UserService)
                signup_data = {
                    "name": user.get("name"),
                    "surname": user.get("surname"),
                    "email": user.get("email"),
                    "password": user.get("password")
                }
                
                response = self.session.post(
                    f"{self.api_url}/api/v1/auth/signup",
                    json=signup_data,
                    headers={"Content-Type": "application/json"}
                )
                
                if response.status_code in [200, 201]:
                    self.stats["users_created"] += 1
                    logger.debug(f"Created user: {user.get('email')}")
                elif response.status_code == 400 and "already" in response.text.lower():
                    self.stats["users_skipped"] += 1
                    logger.debug(f"User {user.get('email')} already exists")
                else:
                    logger.warning(f"Failed to create user {user.get('email')}: {response.status_code}")
                    self.stats["users_skipped"] += 1
                    
            except requests.RequestException as e:
                logger.error(f"Error creating user: {e}")
                self.stats["users_skipped"] += 1
        
        logger.info(f"✅ Users: {self.stats['users_created']} created, "
                   f"{self.stats['users_skipped']} skipped")
    
    def _fetch_existing_payrolls(self) -> set:
        """Fetch existing payrolls and return set of (employeeId, payroll_date) tuples."""
        existing = set()
        try:
            response = self.session.get(
                f"{self.api_url}/api/v1/payroll",
                headers=self._auth_headers()
            )
            if response.status_code == 200:
                payrolls = response.json()
                for p in payrolls:
                    # Unique key: employeeId + full payroll date
                    emp_id = p.get("employeeId")
                    payroll_date = p.get("payrollDate", "")[:10]  # YYYY-MM-DD
                    if emp_id and payroll_date:
                        existing.add((emp_id, payroll_date))
                logger.debug(f"Found {len(existing)} existing payrolls")
        except Exception as e:
            logger.warning(f"Could not fetch existing payrolls: {e}")
        return existing

    def seed_payrolls(self, payrolls: List[Dict[str, Any]]) -> None:
        """Seed payrolls via API with duplicate detection."""
        logger.info(f"Seeding {len(payrolls)} payrolls...")
        
        # Pre-fetch existing payrolls for idempotency
        existing_payrolls = self._fetch_existing_payrolls()
        
        for payroll in payrolls:
            try:
                # Unique key: employeeId + full date
                emp_id = payroll.get("employeeId")
                payroll_date = payroll.get("payrollDate", "")[:10]  # YYYY-MM-DD
                payroll_key = (emp_id, payroll_date)
                
                if payroll_key in existing_payrolls:
                    logger.debug(f"Payroll for employee {emp_id} on {payroll_date} already exists, skipping")
                    self.stats["payrolls_skipped"] += 1
                    continue
                
                response = self.session.post(
                    f"{self.api_url}/api/v1/payroll/",
                    json=payroll,
                    headers=self._auth_headers()
                )
                
                if response.status_code in [200, 201]:
                    self.stats["payrolls_created"] += 1
                    existing_payrolls.add(payroll_key)  # Add to set to avoid duplicates in same batch
                else:
                    self.stats["payrolls_skipped"] += 1
                    
            except requests.RequestException as e:
                logger.error(f"Error creating payroll: {e}")
                self.stats["payrolls_skipped"] += 1
        
        logger.info(f"✅ Payrolls: {self.stats['payrolls_created']} created, "
                   f"{self.stats['payrolls_skipped']} skipped")
    
    def _fetch_existing_expenses(self) -> set:
        """Fetch existing expenses and return set of unique keys."""
        existing = set()
        try:
            response = self.session.get(
                f"{self.api_url}/api/v1/expense",
                headers=self._auth_headers()
            )
            if response.status_code == 200:
                expenses = response.json()
                for e in expenses:
                    # Unique key: userId + date + amount + concept
                    user_id = e.get("expenseUserId")
                    expense_date = e.get("expenseDate", "")[:10]  # YYYY-MM-DD
                    amount = round(float(e.get("amount", 0)), 2)
                    concept = e.get("concept", "").lower()
                    if user_id:
                        existing.add((user_id, expense_date, amount, concept))
                logger.debug(f"Found {len(existing)} existing expenses")
        except Exception as e:
            logger.warning(f"Could not fetch existing expenses: {e}")
        return existing

    def seed_expenses(self, expenses: List[Dict[str, Any]]) -> None:
        """Seed expenses via API with duplicate detection."""
        logger.info(f"Seeding {len(expenses)} expenses...")
        
        # Pre-fetch existing expenses for idempotency
        existing_expenses = self._fetch_existing_expenses()
        
        for expense in expenses:
            try:
                # Unique key: userId + date + amount + concept
                user_id = expense.get("expenseUserId")
                expense_date = expense.get("expenseDate", "")[:10]  # YYYY-MM-DD
                amount = round(float(expense.get("amount", 0)), 2)
                concept = expense.get("concept", "").lower()
                expense_key = (user_id, expense_date, amount, concept)
                
                if expense_key in existing_expenses:
                    logger.debug(f"Expense for user {user_id} on {expense_date} ({concept}) already exists, skipping")
                    self.stats["expenses_skipped"] += 1
                    continue
                
                response = self.session.post(
                    f"{self.api_url}/api/v1/expense/",
                    json=expense,
                    headers=self._auth_headers()
                )
                
                if response.status_code in [200, 201]:
                    self.stats["expenses_created"] += 1
                    existing_expenses.add(expense_key)  # Add to set to avoid duplicates in same batch
                else:
                    self.stats["expenses_skipped"] += 1
                    
            except requests.RequestException as e:
                logger.error(f"Error creating expense: {e}")
                self.stats["expenses_skipped"] += 1
        
        logger.info(f"✅ Expenses: {self.stats['expenses_created']} created, "
                   f"{self.stats['expenses_skipped']} skipped")
    
    def run(self, env: str, wait_seconds: int = 60) -> bool:
        """Run the complete seeding process."""
        logger.info(f"=" * 60)
        logger.info(f"Starting seed process for environment: {env.upper()}")
        logger.info(f"=" * 60)
        
        # Wait for backend
        if not self.wait_for_backend(wait_seconds):
            return False
        
        # Login
        if not self.login():
            logger.error("Cannot proceed without authentication")
            return False
        
        # Load and seed data in order
        # 1. Employees first (required for user linking)
        employees = self.load_json_data(env, "employees")
        if employees:
            self.seed_employees(employees)
        
        # 2. Users (will auto-link with employees by email)
        users = self.load_json_data(env, "users")
        if users:
            self.seed_users(users)
        
        # 3. Payrolls (reference employees)
        payrolls = self.load_json_data(env, "payrolls")
        if payrolls:
            self.seed_payrolls(payrolls)
        
        # 4. Expenses (reference users)
        expenses = self.load_json_data(env, "expenses")
        if expenses:
            self.seed_expenses(expenses)
        
        # Summary
        logger.info(f"=" * 60)
        logger.info("SEEDING COMPLETE - Summary:")
        logger.info(f"  Employees: {self.stats['employees_created']} created, "
                   f"{self.stats['employees_skipped']} skipped")
        logger.info(f"  Users: {self.stats['users_created']} created, "
                   f"{self.stats['users_skipped']} skipped")
        logger.info(f"  Payrolls: {self.stats['payrolls_created']} created, "
                   f"{self.stats['payrolls_skipped']} skipped")
        logger.info(f"  Expenses: {self.stats['expenses_created']} created, "
                   f"{self.stats['expenses_skipped']} skipped")
        logger.info(f"=" * 60)
        
        return True


def main():
    parser = argparse.ArgumentParser(description="Seed data via REST API")
    parser.add_argument(
        "--env", "-e",
        default=os.getenv("SEED_ENV", "dev"),
        choices=["test", "dev", "prod"],
        help="Environment to seed (default: dev)"
    )
    parser.add_argument(
        "--api-url", "-u",
        default=os.getenv("API_URL", DEFAULT_API_URL),
        help=f"Backend API URL (default: {DEFAULT_API_URL})"
    )
    parser.add_argument(
        "--admin-email",
        default=os.getenv("ADMIN_EMAIL", DEFAULT_ADMIN_EMAIL),
        help="Admin email for authentication"
    )
    parser.add_argument(
        "--admin-password",
        default=os.getenv("ADMIN_PASSWORD", DEFAULT_ADMIN_PASSWORD),
        help="Admin password for authentication"
    )
    parser.add_argument(
        "--wait", "-w",
        type=int,
        default=int(os.getenv("WAIT_SECONDS", DEFAULT_WAIT_SECONDS)),
        help=f"Max seconds to wait for backend (default: {DEFAULT_WAIT_SECONDS})"
    )
    parser.add_argument(
        "--verbose", "-v",
        action="store_true",
        help="Enable verbose output"
    )
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    runner = SeedRunner(
        api_url=args.api_url,
        admin_email=args.admin_email,
        admin_password=args.admin_password
    )
    
    success = runner.run(env=args.env, wait_seconds=args.wait)
    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
