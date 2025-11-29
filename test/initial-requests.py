import http.client

conn = http.client.HTTPConnection("localhost:8081")
headers = { 'content-type': "application/json" }

def request(method, path, body):
    conn.request(method, path, body.encode('utf-8'), headers)
    res = conn.getresponse()
    data = res.read().decode("utf-8").strip()
    print(data if data else "ok")

# Create customer: ivanovp
payload = "{\n  \"login\": \"ivanovp\",\n  \"forename\": \"Пётр\",\n  \"surname\": \"Иванов\",\n  \"birthdate\": \"1985-10-06\"\n}"
request("POST", "/customers", payload)

# Create customer: jsmithy
payload = "{\n  \"login\": \"jsmithy\",\n  \"forename\": \"John\",\n  \"surname\": \"Smith\",\n  \"birthdate\": \"1969-03-26\"\n}"
request("POST", "/customers", payload)

# Create customer: tt_goat
payload = "{\n  \"login\": \"tt_goat\",\n  \"forename\": \"Ma\",\n  \"surname\": \"Long\",\n  \"birthdate\": \"1988-10-20\"\n}"
request("POST", "/customers", payload)

# Create account: 1000000001
payload = "{\n  \"customerLogin\": \"ivanovp\",\n  \"currency\": \"RUB\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000002
payload = "{\n  \"customerLogin\": \"ivanovp\",\n  \"currency\": \"USD\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000003
payload = "{\n  \"customerLogin\": \"jsmithy\",\n  \"currency\": \"USD\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000004
payload = "{\n  \"customerLogin\": \"jsmithy\",\n  \"currency\": \"EUR\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000005
payload = "{\n  \"customerLogin\": \"jsmithy\",\n  \"currency\": \"GBP\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000006
payload = "{\n  \"customerLogin\": \"tt_goat\",\n  \"currency\": \"CNY\"\n}"
request("POST", "/accounts", payload)

# Create account: 1000000007
payload = "{\n  \"customerLogin\": \"tt_goat\",\n  \"currency\": \"USD\"\n}"
request("POST", "/accounts", payload)

# Top up account: 1000000001
payload = "{\n  \"augend\": 4567.89\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-225c9fcfb454'
request("PUT", "/accounts/1000000001/top-up", payload)

# Top up account: 1000000002
payload = "{\n  \"augend\": 150.00\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-325c9fcfb454'
request("PUT", "/accounts/1000000002/top-up", payload)

# Top up account: 1000000003
payload = "{\n  \"augend\": 234.56\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-425c9fcfb454'
request("PUT", "/accounts/1000000003/top-up", payload)

# Top up account: 1000000004
payload = "{\n  \"augend\": 136.00\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-525c9fcfb454'
request("PUT", "/accounts/1000000004/top-up", payload)

# Top up account: 1000000005
payload = "{\n  \"augend\": 94.00\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-625c9fcfb454'
request("PUT", "/accounts/1000000005/top-up", payload)

# Top up account: 1000000006
payload = "{\n  \"augend\": 789.01\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-725c9fcfb454'
request("PUT", "/accounts/1000000006/top-up", payload)

# Top up account: 1000000007
payload = "{\n  \"augend\": 127.00\n}"
headers['Idempotency-Key'] = '787cd067-8a8a-4ad8-81f6-825c9fcfb454'
request("PUT", "/accounts/1000000007/top-up", payload)

del headers['Idempotency-Key']
