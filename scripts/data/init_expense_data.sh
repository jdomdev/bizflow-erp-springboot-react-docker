#!/bin/bash
# set -e deshabilitado para permitir continuar aunque fallen creaciones de usuarios duplicados

# ====================================================================
# Script de inicialización automática de expense_user y expense
# ====================================================================
# Este script se ejecuta después de que el backend esté levantado
# y crea automáticamente expense_users y expenses vía API REST
# ====================================================================

# Configuración
API_URL="${API_URL:-http://localhost:8181}"
ADMIN_EMAIL="${ADMIN_EMAIL:-ada.lovelace@bizflowerp.com}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-SEED_PASSWORD_PLACEHOLDER}"

echo "=================================================="
echo "Inicializando datos de expense_user y expense"
echo "=================================================="
echo "API URL: $API_URL"
echo ""

# Función para esperar que el backend esté disponible
wait_for_backend() {
    echo "Esperando a que el backend esté disponible..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$API_URL/actuator/health" > /dev/null 2>&1; then
            echo "✅ Backend disponible"
            return 0
        fi
        echo "Intento $attempt/$max_attempts - Backend no disponible, esperando 2s..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo "❌ ERROR: Backend no disponible después de $max_attempts intentos"
    return 1
}

# Función para crear los usuarios admin bootstrap (sin autenticación previa)
create_bootstrap_admins() {
    echo ""
    echo "================================================="
    echo "Creando usuarios administradores bootstrap..."
    echo "================================================="
    
    # Crear Ada Lovelace (ADMIN)
    echo "Creando Ada Lovelace (admin)..."
    local ada_response=$(curl -s -X POST "$API_URL/api/v1/auth/signup" \
        -H "Content-Type: application/json" \
        -d '{"email":"ada.lovelace@bizflowerp.com","password":"SEED_PASSWORD_PLACEHOLDER","name":"Ada","surname":"Lovelace","employeeId":5,"roles":["ADMIN","USER"]}')
    
    local ada_token=$(echo "$ada_response" | jq -r '.accessToken // empty')
    if [ -n "$ada_token" ] && [ "$ada_token" != "null" ]; then
        echo "✅ Ada Lovelace creada exitosamente"
    else
        echo "⚠️  No se pudo crear Ada Lovelace (puede que ya exista)"
    fi
    
    # Crear Alan Turing (ADMIN)
    echo "Creando Alan Turing (admin)..."
    local alan_response=$(curl -s -X POST "$API_URL/api/v1/auth/signup" \
        -H "Content-Type: application/json" \
        -d '{"email":"alan.turing@bizflowerp.com","password":"<PASSWORD>","name":"Alan","surname":"Turing","employeeId":6,"roles":["ADMIN","USER"]}')
    
    local alan_token=$(echo "$alan_response" | jq -r '.accessToken // empty')
    if [ -n "$alan_token" ] && [ "$alan_token" != "null" ]; then
        echo "✅ Alan Turing creado exitosamente"
    else
        echo "⚠️  No se pudo crear Alan Turing (puede que ya exista)"
    fi
    
    echo "✅ Administradores bootstrap creados"
}

# Función para obtener el token JWT de Ada Lovelace
get_auth_token() {
    echo "" >&2
    echo "Obteniendo token de autenticación de Ada Lovelace..." >&2
    
    # Intentar login con Ada Lovelace
    local login_response=$(curl -s -X POST "$API_URL/api/v1/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"email":"ada.lovelace@bizflowerp.com","password":"SEED_PASSWORD_PLACEHOLDER"}')
    
    local token=$(echo "$login_response" | jq -r '.accessToken // empty')
    
    if [ -n "$token" ] && [ "$token" != "null" ]; then
        echo "✅ Token obtenido de Ada Lovelace" >&2
        echo "$token"
        return 0
    fi
    
    echo "❌ ERROR: No se pudo obtener token de autenticación" >&2
    echo "Login response: $login_response" >&2
    return 1
}

# Función para crear un expense_user
create_expense_user() {
    local token=$1
    local email=$2
    local name=$3
    local surname=$4
    local password=$5
    local employee_id=$6
    local roles=$7
    
    local http_code=$(curl -s -w "%{http_code}" -o /tmp/expense_user_response.json -X POST "$API_URL/api/v1/auth/signup" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $token" \
        -d "{\"email\":\"$email\",\"password\":\"$password\",\"name\":\"$name\",\"surname\":\"$surname\",\"employeeId\":$employee_id,\"roles\":$roles}")
    
    if [ "$http_code" = "201" ]; then
        echo "✅ Usuario creado: $email"
        sleep 0.05  # Pequeña pausa para evitar problemas de concurrencia
        return 0
    else
        echo "⚠️  No se pudo crear usuario: $email (HTTP $http_code - puede que ya exista)"
        return 1
    fi
}

# Función para crear una expense
create_expense() {
    local token=$1
    local amount=$2
    local concept=$3
    local expense_date=$4
    local note=$5
    local expense_user_id=$6  # ID del usuario al que pertenece el gasto
    
    local http_code=$(curl -s -w "%{http_code}" -o /tmp/expense_response.json -X POST "$API_URL/api/v1/expense/" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $token" \
        -d "{\"amount\":$amount,\"concept\":\"$concept\",\"expenseDate\":\"$expense_date\",\"note\":\"$note\",\"expenseUserId\":$expense_user_id}")
    
    if [ "$http_code" = "201" ] || [ "$http_code" = "200" ]; then
        echo "✅ Expense creada: $concept ($amount) para user_id=$expense_user_id"
        return 0
    else
        echo "⚠️  No se pudo crear expense: $concept (HTTP $http_code)"
        cat /tmp/expense_response.json
        return 1
    fi
}

# Main execution
main() {
    # Esperar a que el backend esté disponible
    if ! wait_for_backend; then
        exit 1
    fi
    
    # NOTA: Ada Lovelace y Alan Turing ya vienen creados por sql/common/05_expense_admin_bootstrap.sql
    # (incluido desde los masters de cada entorno), así que solo obtenemos el token de Ada para el resto
    
    # Obtener token de autenticación de Ada Lovelace
    echo ""
    TOKEN=$(get_auth_token)
    if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
        echo "❌ No se pudo obtener token de autenticación"
        exit 1
    fi
    
    echo ""
    echo "=================================================="
    echo "Creando expense_users restantes..."
    echo "=================================================="
    
    # Crear expense_users basados en employees existentes (7-61)
    # Ada (5) y Alan (6) ya fueron creados via SQL bootstrap
    # MANAGER: usuarios con responsabilidades de gestión
    # USER: usuarios estándar
    
    # Managers
    echo "Creando managers..."
    create_expense_user "$TOKEN" "grace.hopper@bizflowerp.com" "Grace" "Hopper" "SEED_PASSWORD_PLACEHOLDER" 7 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "tim.bernerslee@bizflowerp.com" "Tim" "Berners-Lee" "SEED_PASSWORD_PLACEHOLDER" 9 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "margaret.hamilton@bizflowerp.com" "Margaret" "Hamilton" "SEED_PASSWORD_PLACEHOLDER" 11 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "john.vonneumann@bizflowerp.com" "John" "von Neumann" "SEED_PASSWORD_PLACEHOLDER" 12 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "donald.knuth@bizflowerp.com" "Donald" "Knuth" "SEED_PASSWORD_PLACEHOLDER" 14 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "albert.einstein@bizflowerp.com" "Albert" "Einstein" "SEED_PASSWORD_PLACEHOLDER" 16 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "stephen.hawking@bizflowerp.com" "Stephen" "Hawking" "SEED_PASSWORD_PLACEHOLDER" 18 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "niels.bohr@bizflowerp.com" "Niels" "Bohr" "SEED_PASSWORD_PLACEHOLDER" 20 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "nikola.tesla@bizflowerp.com" "Nikola" "Tesla" "SEED_PASSWORD_PLACEHOLDER" 22 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "carl.sagan@bizflowerp.com" "Carl" "Sagan" "SEED_PASSWORD_PLACEHOLDER" 24 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "serena.williams@bizflowerp.com" "Serena" "Williams" "SEED_PASSWORD_PLACEHOLDER" 26 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "simone.biles@bizflowerp.com" "Simone" "Biles" "SEED_PASSWORD_PLACEHOLDER" 28 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "cristiano.ronaldo@bizflowerp.com" "Cristiano" "Ronaldo" "SEED_PASSWORD_PLACEHOLDER" 30 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "rafael.nadal@bizflowerp.com" "Rafael" "Nadal" "SEED_PASSWORD_PLACEHOLDER" 32 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "diego.maradona@bizflowerp.com" "Diego" "Maradona" "SEED_PASSWORD_PLACEHOLDER" 34 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "florence.nightingale@bizflowerp.com" "Florence" "Nightingale" "SEED_PASSWORD_PLACEHOLDER" 36 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "sally.ride@bizflowerp.com" "Sally" "Ride" "SEED_PASSWORD_PLACEHOLDER" 38 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "yuri.gagarin@bizflowerp.com" "Yuri" "Gagarin" "SEED_PASSWORD_PLACEHOLDER" 40 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "amelia.earhart@bizflowerp.com" "Amelia" "Earhart" "SEED_PASSWORD_PLACEHOLDER" 42 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "bill.gates@bizflowerp.com" "Bill" "Gates" "SEED_PASSWORD_PLACEHOLDER" 44 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "jeff.bezos@bizflowerp.com" "Jeff" "Bezos" "SEED_PASSWORD_PLACEHOLDER" 46 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "larry.page@bizflowerp.com" "Larry" "Page" "SEED_PASSWORD_PLACEHOLDER" 48 '["MANAGER","USER"]' || true
    create_expense_user "$TOKEN" "sheryl.sandberg@bizflowerp.com" "Sheryl" "Sandberg" "SEED_PASSWORD_PLACEHOLDER" 50 '["MANAGER","USER"]' || true
    
    # Usuarios estándar
    echo "Creando usuarios estándar..."
    create_expense_user "$TOKEN" "katherine.johnson@bizflowerp.com" "Katherine" "Johnson" "SEED_PASSWORD_PLACEHOLDER" 8 '["USER"]'
    create_expense_user "$TOKEN" "linus.torvalds@bizflowerp.com" "Linus" "Torvalds" "SEED_PASSWORD_PLACEHOLDER" 10 '["USER"]'
    create_expense_user "$TOKEN" "dennis.ritchie@bizflowerp.com" "Dennis" "Ritchie" "SEED_PASSWORD_PLACEHOLDER" 13 '["USER"]'
    create_expense_user "$TOKEN" "marie.curie@bizflowerp.com" "Marie" "Curie" "SEED_PASSWORD_PLACEHOLDER" 15 '["USER"]'
    create_expense_user "$TOKEN" "isaac.newton@bizflowerp.com" "Isaac" "Newton" "SEED_PASSWORD_PLACEHOLDER" 17 '["USER"]'
    create_expense_user "$TOKEN" "richard.feynman@bizflowerp.com" "Richard" "Feynman" "SEED_PASSWORD_PLACEHOLDER" 19 '["USER"]'
    create_expense_user "$TOKEN" "galileo.galilei@bizflowerp.com" "Galileo" "Galilei" "SEED_PASSWORD_PLACEHOLDER" 21 '["USER"]'
    create_expense_user "$TOKEN" "rosalind.franklin@bizflowerp.com" "Rosalind" "Franklin" "<PASSWORD>" 23 '["USER"]'
    create_expense_user "$TOKEN" "michael.jordan@bizflowerp.com" "Michael" "Jordan" "SEED_PASSWORD_PLACEHOLDER" 25 '["USER"]'
    create_expense_user "$TOKEN" "usain.bolt@bizflowerp.com" "Usain" "Bolt" "SEED_PASSWORD_PLACEHOLDER" 27 '["USER"]'
    create_expense_user "$TOKEN" "lionel.messi@bizflowerp.com" "Lionel" "Messi" "SEED_PASSWORD_PLACEHOLDER" 29 '["USER"]'
    create_expense_user "$TOKEN" "roger.federer@bizflowerp.com" "Roger" "Federer" "SEED_PASSWORD_PLACEHOLDER" 31 '["USER"]'
    create_expense_user "$TOKEN" "pele.nascimento@bizflowerp.com" "Pele" "Nascimento" "SEED_PASSWORD_PLACEHOLDER" 33 '["USER"]'
    create_expense_user "$TOKEN" "simone.weil@bizflowerp.com" "Simone" "Weil" "SEED_PASSWORD_PLACEHOLDER" 35 '["USER"]'
    create_expense_user "$TOKEN" "jane.goodall@bizflowerp.com" "Jane" "Goodall" "SEED_PASSWORD_PLACEHOLDER" 37 '["USER"]'
    create_expense_user "$TOKEN" "neil.armstrong@bizflowerp.com" "Neil" "Armstrong" "SEED_PASSWORD_PLACEHOLDER" 39 '["USER"]'
    create_expense_user "$TOKEN" "valentina.tereshkova@bizflowerp.com" "Valentina" "Tereshkova" "SEED_PASSWORD_PLACEHOLDER" 41 '["USER"]'
    create_expense_user "$TOKEN" "steve.jobs@bizflowerp.com" "Steve" "Jobs" "<PASSWORD>" 43 '["USER"]'
    create_expense_user "$TOKEN" "mark.zuckerberg@bizflowerp.com" "Mark" "Zuckerberg" "SEED_PASSWORD_PLACEHOLDER" 45 '["USER"]'
    create_expense_user "$TOKEN" "elon.musk@bizflowerp.com" "Elon" "Musk" "SEED_PASSWORD_PLACEHOLDER" 47 '["USER"]'
    create_expense_user "$TOKEN" "sergey.brin@bizflowerp.com" "Sergey" "Brin" "SEED_PASSWORD_PLACEHOLDER" 49 '["USER"]'
    
    # Usuarios freelance/externos (employees 51-61, añadidos recientemente)
    echo "Creando usuarios freelance/externos..."
    create_expense_user "$TOKEN" "dorothy.hodgkin@bizflowerp.com" "Dorothy" "Hodgkin" "SEED_PASSWORD_PLACEHOLDER" 51 '["USER"]'
    create_expense_user "$TOKEN" "james.clerkmaxwell@bizflowerp.com" "James" "ClerkMaxwell" "SEED_PASSWORD_PLACEHOLDER" 52 '["USER"]'
    create_expense_user "$TOKEN" "nadia.comaneci@bizflowerp.com" "Nadia" "Comaneci" "SEED_PASSWORD_PLACEHOLDER" 53 '["USER"]'
    create_expense_user "$TOKEN" "ludwig.wittgenstein@bizflowerp.com" "Ludwig" "Wittgenstein" "SEED_PASSWORD_PLACEHOLDER" 54 '["MANAGER","USER"]'
    create_expense_user "$TOKEN" "contractor1@bizflowerp.com" "Robert" "Contractor" "SEED_PASSWORD_PLACEHOLDER" 55 '["USER"]'
    create_expense_user "$TOKEN" "contractor2@bizflowerp.com" "Linda" "Freelance" "SEED_PASSWORD_PLACEHOLDER" 56 '["USER"]'
    create_expense_user "$TOKEN" "contractor3@bizflowerp.com" "Michael" "External" "SEED_PASSWORD_PLACEHOLDER" 57 '["USER"]'
    create_expense_user "$TOKEN" "contractor4@bizflowerp.com" "Sarah" "Consultant" "SEED_PASSWORD_PLACEHOLDER" 58 '["MANAGER","USER"]'
    create_expense_user "$TOKEN" "contractor5@bizflowerp.com" "David" "Specialist" "SEED_PASSWORD_PLACEHOLDER" 59 '["USER"]'
    create_expense_user "$TOKEN" "contractor6@bizflowerp.com" "Emma" "Advisor" "SEED_PASSWORD_PLACEHOLDER" 60 '["MANAGER","USER"]'
    create_expense_user "$TOKEN" "contractor7@bizflowerp.com" "Oliver" "Expert" "SEED_PASSWORD_PLACEHOLDER" 61 '["USER"]'
    
    echo ""
    echo "=================================================="
    echo "Creando expenses de ejemplo..."
    echo "=================================================="
    
    # Crear expenses variadas para diferentes usuarios (el token corresponde al usuario autenticado)
    # Nota: Las expenses se crean con el usuario que tiene el token, por lo que todas serán del admin
    # Para crear expenses de diferentes usuarios, necesitaríamos obtener tokens de cada uno
    
    create_expense "$TOKEN" 150.50 "Office Supplies" "2025-12-01T10:00:00" "Purchase of office materials" 1 || true
    create_expense "$TOKEN" 89.99 "Software License" "2025-12-02T14:30:00" "Annual license renewal" 2 || true
    create_expense "$TOKEN" 45.00 "Team Lunch" "2025-12-03T12:00:00" "Monthly team building lunch" 3 || true
    create_expense "$TOKEN" 200.00 "Training Course" "2025-12-05T09:00:00" "Professional development" 4 || true
    create_expense "$TOKEN" 75.25 "Taxi Service" "2025-12-06T18:00:00" "Client meeting transportation" 5 || true
    create_expense "$TOKEN" 120.00 "Hotel Accommodation" "2025-12-07T20:00:00" "Business trip lodging" 6 || true
    create_expense "$TOKEN" 35.50 "Parking Fee" "2025-12-08T08:00:00" "Office parking monthly" 7 || true
    create_expense "$TOKEN" 250.00 "Conference Ticket" "2025-12-10T10:00:00" "Industry conference attendance" 8 || true
    create_expense "$TOKEN" 60.00 "Internet Service" "2025-12-11T00:00:00" "Monthly home office internet" 9 || true
    create_expense "$TOKEN" 95.75 "Client Dinner" "2025-12-12T19:30:00" "Business development dinner" 10 || true
    create_expense "$TOKEN" 180.00 "Equipment Purchase" "2025-12-13T10:00:00" "New keyboard and mouse" 1 || true
    create_expense "$TOKEN" 125.50 "Mobile Phone Bill" "2025-12-14T00:00:00" "Monthly business phone" 2 || true
    create_expense "$TOKEN" 340.00 "Flight Tickets" "2025-12-15T06:00:00" "Business trip to client site" 3 || true
    create_expense "$TOKEN" 55.25 "Books and Publications" "2025-12-16T14:00:00" "Technical books for team" 4 || true
    create_expense "$TOKEN" 90.00 "Cloud Services" "2025-12-17T00:00:00" "AWS monthly subscription" 5 || true
    create_expense "$TOKEN" 420.00 "Team Workshop" "2025-12-18T09:00:00" "Quarterly team workshop" 6 || true
    create_expense "$TOKEN" 65.80 "Coffee Meeting" "2025-12-19T11:00:00" "Informal client meeting" 7 || true
    create_expense "$TOKEN" 155.00 "Marketing Materials" "2025-12-20T13:00:00" "Brochures and business cards" 8 || true
    create_expense "$TOKEN" 210.00 "Software Subscription" "2025-12-21T00:00:00" "Annual IDE subscription" 9 || true
    create_expense "$TOKEN" 48.90 "Office Snacks" "2025-12-22T10:00:00" "Monthly office refreshments" 10 || true
    
    echo ""
    echo "=================================================="
    echo "✅ Inicialización completada exitosamente"
    echo "=================================================="
    echo "Expense users creados: 57+ (2 ADMIN, 25 MANAGER, 30+ USER)"
    echo "  - Admins: Ada Lovelace, Alan Turing"
    echo "  - Managers: 25 usuarios con responsabilidades de gestión"
    echo "  - Users: 30+ usuarios estándar y freelance"
    echo "Expenses creadas: 20+"
    echo ""
}

# Ejecutar script principal
main
