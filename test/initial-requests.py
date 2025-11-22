import http.client

conn = http.client.HTTPConnection("localhost:8081")
headers = { 'content-type': "application/json" }

def request(method, path, body):
    conn.request(method, path, body.encode('utf-8'), headers)
    res = conn.getresponse()
    data = res.read().decode("utf-8").strip()
    print(data if data else "ok")

# Create customer: ivanovp
payload = "{\n  \"nickname\": \"ivanovp\",\n  \"forename\": \"Пётр\",\n  \"surname\": \"Иванов\",\n  \"birthdate\": \"1985-10-06\"\n}"
request("POST", "/customers", payload)

# Create customer: jsmithy
payload = "{\n  \"nickname\": \"jsmithy\",\n  \"forename\": \"John\",\n  \"surname\": \"Smith\",\n  \"birthdate\": \"1969-03-26\"\n}"
request("POST", "/customers", payload)

# Create customer: tt_goat
payload = "{\n  \"nickname\": \"tt_goat\",\n  \"forename\": \"Ma\",\n  \"surname\": \"Long\",\n  \"birthdate\": \"1988-10-20\"\n}"
request("POST", "/customers", payload)

# Create account: 1000000001
payload = "{\n  \"customerName\": \"ivanovp\",\n  \"currency\": \"RUB\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000002
payload = "{\n  \"customerName\": \"ivanovp\",\n  \"currency\": \"USD\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000003
payload = "{\n  \"customerName\": \"jsmithy\",\n  \"currency\": \"USD\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000004
payload = "{\n  \"customerName\": \"jsmithy\",\n  \"currency\": \"EUR\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000005
payload = "{\n  \"customerName\": \"jsmithy\",\n  \"currency\": \"GBP\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000006
payload = "{\n  \"customerName\": \"tt_goat\",\n  \"currency\": \"CNY\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000007
payload = "{\n  \"customerName\": \"tt_goat\",\n  \"currency\": \"USD\"\n}"
request("POST", "/accounts", payload)

# Top up account: 1000000001
payload = "{\n  \"augend\": 4567.89\n}"
request("PUT", "/accounts/1000000001/top-up", payload)

# Top up account: 1000000002
payload = "{\n  \"augend\": 150.00\n}"
request("PUT", "/accounts/1000000002/top-up", payload)

# Top up account: 1000000003
payload = "{\n  \"augend\": 234.56\n}"
request("PUT", "/accounts/1000000003/top-up", payload)

# Top up account: 1000000004
payload = "{\n  \"augend\": 136.00\n}"
request("PUT", "/accounts/1000000004/top-up", payload)

# Top up account: 1000000005
payload = "{\n  \"augend\": 94.00\n}"
request("PUT", "/accounts/1000000005/top-up", payload)

# Top up account: 1000000006
payload = "{\n  \"augend\": 789.01\n}"
request("PUT", "/accounts/1000000006/top-up", payload)

# Top up account: 1000000007
payload = "{\n  \"augend\": 127.00\n}"
request("PUT", "/accounts/1000000007/top-up", payload)
