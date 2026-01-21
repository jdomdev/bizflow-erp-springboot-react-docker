FROM python:3.11-slim

LABEL maintainer="BizFlow ERP Team"
LABEL description="API-based seed runner for BizFlow ERP"

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application files
COPY seed_runner.py .
COPY config/ ./config/
COPY data/ ./data/

# Default environment variables
ENV SEED_ENV=dev
ENV API_URL=http://backend:8080
ENV ADMIN_EMAIL=admin@bizflow.com
ENV ADMIN_PASSWORD=<PASSWORD>
ENV WAIT_FOR_BACKEND=true

# Run seeder
CMD ["sh", "-c", "python seed_runner.py --env ${SEED_ENV} --api-url ${API_URL} --admin-email ${ADMIN_EMAIL} --admin-password ${ADMIN_PASSWORD} ${WAIT_FOR_BACKEND:+--wait}"]
