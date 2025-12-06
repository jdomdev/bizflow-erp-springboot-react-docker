# CI/CD Pipeline Documentation

## Overview

This project implements a comprehensive Continuous Integration and Continuous Deployment (CI/CD) pipeline using GitHub Actions. The pipeline automates testing, building, and deployment processes for both backend (Spring Boot) and frontend (React) components.

## Table of Contents

- [Workflows](#workflows)
- [CI Pipeline](#ci-pipeline)
- [CD Pipeline](#cd-pipeline)
- [Test Coverage](#test-coverage)
- [Configuration](#configuration)
- [Best Practices](#best-practices)

## Workflows

The project includes three main workflows:

### 1. Backend CI (`backend-ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches (when backend files change)
- Pull requests to `main` or `develop` branches (when backend files change)

**Steps:**
1. Checkout code
2. Set up JDK 21 with Maven cache
3. Set up PostgreSQL test database
4. Build with Maven
5. Run tests with coverage
6. Generate JaCoCo coverage report
7. Package application
8. Upload artifacts (JAR, test results, coverage reports)

**Technologies:**
- Java 21
- Maven
- Spring Boot 3.3.4
- JUnit 5
- JaCoCo for coverage
- PostgreSQL 16 (test database)

### 2. Frontend CI (`frontend-ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches (when frontend files change)
- Pull requests to `main` or `develop` branches (when frontend files change)

**Steps:**
1. Checkout code
2. Set up Node.js 18 with npm cache
3. Install dependencies
4. Run ESLint
5. Run tests with Vitest
6. Build production bundle
7. Upload artifacts (dist folder)
8. Report bundle size

**Technologies:**
- Node.js 18
- React 18
- Vite 5
- Vitest
- React Testing Library
- ESLint

### 3. Docker CD (`docker-cd.yml`)

**Triggers:**
- Push to `main` branch
- Git tags matching `v*.*.*` pattern
- Manual workflow dispatch

**Steps:**
1. Build and push Docker images for backend and frontend
2. Tag images with version information
3. Push to GitHub Container Registry (ghcr.io)
4. Create deployment summary

**Technologies:**
- Docker Buildx
- GitHub Container Registry
- Multi-stage builds
- Docker layer caching

## CI Pipeline

### Backend CI Pipeline

The backend CI pipeline ensures code quality and correctness for the Spring Boot application.

#### Database Setup

The pipeline uses a PostgreSQL service container for integration tests:

```yaml
services:
  postgres:
    image: postgres:16-alpine
    env:
      POSTGRES_DB: expense_note_app_test
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
```

#### Build Process

```bash
# Compile source code
mvn clean compile -DskipTests

# Run all tests
mvn test

# Generate coverage report
mvn jacoco:report

# Package application
mvn package -DskipTests
```

#### Test Coverage

The pipeline uses JaCoCo for test coverage analysis:
- **Minimum coverage requirement**: 30% line coverage per package
- Reports are generated in HTML, XML, and CSV formats
- Coverage reports are uploaded as artifacts for review

#### Artifacts

The following artifacts are uploaded after each run:
- **test-results**: JUnit test reports (XML format)
- **coverage-report**: JaCoCo HTML coverage report
- **backend-jar**: Compiled JAR file (retained for 7 days)

### Frontend CI Pipeline

The frontend CI pipeline ensures code quality for the React application.

#### Build Process

```bash
# Install dependencies
npm ci

# Run linter
npm run lint

# Run tests
npm test

# Build production bundle
npm run build
```

#### Testing

The frontend uses:
- **Vitest**: Modern test runner optimized for Vite
- **React Testing Library**: Component testing utilities
- **@testing-library/jest-dom**: Custom matchers for better assertions
- **jsdom**: Browser environment simulation

#### Test Setup

Tests are configured with:
- Global test utilities
- jsdom environment
- Setup file for common mocks (matchMedia, IntersectionObserver)
- Coverage reporting with v8 provider

#### Artifacts

- **frontend-dist**: Production build artifacts (retained for 7 days)
- Bundle size report in GitHub Actions summary

## CD Pipeline

### Docker Image Building

The CD pipeline builds and publishes Docker images for both backend and frontend.

#### Image Tagging Strategy

Images are tagged with multiple tags for flexibility:
- `branch-name`: Latest build from a branch (e.g., `main`)
- `pr-number`: Pull request builds
- `version`: Semantic version tags (e.g., `1.1.0`)
- `major.minor`: Shortened version (e.g., `1.1`)
- `branch-sha`: Branch with commit SHA (e.g., `main-abc123`)

#### Registry

Images are pushed to GitHub Container Registry:
- **Backend**: `ghcr.io/jdomdev/bizflow-erp-springboot-react-docker-backend`
- **Frontend**: `ghcr.io/jdomdev/bizflow-erp-springboot-react-docker-frontend`

#### Build Optimization

- Uses Docker Buildx for multi-platform builds
- Implements layer caching for faster builds
- Separate builds for backend and frontend (parallel execution)

### Deployment

The deployment step is currently a placeholder. To implement actual deployment:

1. **SSH Deployment**:
```bash
- name: Deploy to server
  run: |
    ssh user@server 'cd /app && docker-compose pull && docker-compose up -d'
```

2. **Kubernetes Deployment**:
```bash
- name: Deploy to Kubernetes
  run: |
    kubectl set image deployment/backend backend=ghcr.io/...
    kubectl set image deployment/frontend frontend=ghcr.io/...
```

3. **Cloud Deployment (AWS, Azure, GCP)**:
Configure cloud-specific deployment actions

## Test Coverage

### Backend Coverage

Current test coverage includes:
- **DAO Layer**: Repository tests with H2 in-memory database
- **Service Layer**: Business logic tests with Mockito
- **Entity Layer**: Model validation tests
- **Security Layer**: JWT and authentication tests

**Example test structure**:
```java
@SpringBootTest
@AutoConfigureTestDatabase
class EmployeeServiceTest {
    @Autowired
    private EmployeeService service;
    
    @Test
    void shouldCreateEmployee() {
        // Test implementation
    }
}
```

### Frontend Coverage

Current test coverage includes:
- **Component Tests**: UI component behavior and rendering
- **Integration Tests**: Component interaction tests
- **Accessibility Tests**: Basic a11y checks

**Example test structure**:
```javascript
describe('Button Component', () => {
  it('calls onClick handler when clicked', async () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Click</Button>);
    await userEvent.click(screen.getByText('Click'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

## Configuration

### Environment Variables

#### Backend CI
- `SPRING_PROFILES_ACTIVE=test`: Activates test profile

#### Frontend CI
- No special environment variables required

#### Docker CD
- `REGISTRY`: Container registry URL
- `IMAGE_NAME`: Base image name from repository

### Secrets Required

To fully enable the CD pipeline, configure these secrets in GitHub:

1. **GITHUB_TOKEN**: Automatically provided by GitHub Actions
2. **DEPLOY_SSH_KEY** (optional): SSH key for server deployment
3. **KUBE_CONFIG** (optional): Kubernetes config for K8s deployment

### Branch Protection

Recommended branch protection rules for `main`:
- ✅ Require pull request reviews
- ✅ Require status checks to pass (CI workflows)
- ✅ Require branches to be up to date
- ✅ Require linear history
- ❌ Allow force pushes (disabled)

## Best Practices

### For Developers

1. **Run tests locally before pushing**:
```bash
# Backend
cd backend-springboot && mvn test

# Frontend
cd frontend && npm test
```

2. **Check linting**:
```bash
# Frontend
cd frontend && npm run lint:fix
```

3. **Keep tests fast**: Unit tests should run in milliseconds, integration tests in seconds

4. **Write meaningful test names**: Use descriptive names that explain what is being tested

5. **Maintain test coverage**: Aim for at least 70% coverage on new code

### For CI/CD

1. **Use caching**: Maven and npm caches are enabled to speed up builds

2. **Fail fast**: Tests run before building to catch issues early

3. **Parallel execution**: Backend and frontend workflows run independently

4. **Artifact retention**: Keep artifacts for 7 days by default (configurable)

5. **Resource limits**: PostgreSQL service has health checks to ensure availability

### For Deployment

1. **Tag releases**: Use semantic versioning for releases (`v1.0.0`, `v1.1.0`, etc.)

2. **Review deployment logs**: Check GitHub Actions summaries after deployment

3. **Rollback strategy**: Keep previous Docker images for quick rollback

4. **Health checks**: Verify application health after deployment

## Monitoring and Alerts

### Build Status

Monitor build status:
- GitHub Actions dashboard
- Pull request checks
- Branch status badges (add to README)

### Example Badge Code

Add to README.md:
```markdown
![Backend CI](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/workflows/Backend%20CI/badge.svg)
![Frontend CI](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/workflows/Frontend%20CI/badge.svg)
![Docker CD](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/workflows/Docker%20CD/badge.svg)
```

### Notifications

Configure GitHub notifications:
- Email notifications for failed builds
- Slack/Discord webhooks for team notifications
- GitHub mobile app for on-the-go monitoring

## Troubleshooting

### Common Issues

#### Backend Tests Failing

1. **Database connection issues**:
   - Check PostgreSQL service health
   - Verify database credentials

2. **Dependency conflicts**:
   - Clear Maven cache: `mvn dependency:purge-local-repository`
   - Check for version conflicts in pom.xml

3. **Test isolation issues**:
   - Ensure `@DirtiesContext` is used when needed
   - Check for test data conflicts

#### Frontend Tests Failing

1. **Module not found**:
   - Run `npm ci` to clean install dependencies
   - Check import paths

2. **Mock issues**:
   - Verify setup.js includes necessary mocks
   - Check that jsdom is properly configured

3. **Timeout errors**:
   - Increase test timeout in vitest.config.js
   - Check for infinite loops or async issues

#### Docker Build Failing

1. **Build context issues**:
   - Verify Dockerfile paths
   - Check .dockerignore configuration

2. **Layer caching problems**:
   - Clear cache and rebuild
   - Check cache-to and cache-from settings

3. **Registry authentication**:
   - Verify GITHUB_TOKEN has package write permissions
   - Check registry URL is correct

## Future Enhancements

- [ ] Add performance testing (JMeter, Lighthouse)
- [ ] Implement semantic release automation
- [ ] Add security scanning (Snyk, Dependabot)
- [ ] Implement blue-green deployment
- [ ] Add canary deployment support
- [ ] Configure automatic rollback on failure
- [ ] Add integration with monitoring tools (Prometheus, Grafana)
- [ ] Implement chaos engineering tests
- [ ] Add E2E tests with Playwright/Cypress
- [ ] Configure multi-environment deployments (dev, staging, prod)

## Support

For questions or issues with CI/CD:
1. Check GitHub Actions logs
2. Review this documentation
3. Open an issue with logs and error messages
4. Contact the DevOps team

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Documentation](https://docs.docker.com/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Vitest Documentation](https://vitest.dev/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
