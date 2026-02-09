
try {
    # Ignorer les erreurs SSL pour le certificat auto-signé
    [System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}

    $baseUrl = "https://localhost"
    $loginUrl = "$baseUrl/api/auth/login"
    $clientsUrl = "$baseUrl/api/clients"

    Write-Output "1. Testing Login against Prod Environment ($loginUrl)..."
    $body = @{ username = "admin"; password = "admin123" } | ConvertTo-Json
    $response = Invoke-RestMethod -Uri $loginUrl -Method Post -Body $body -ContentType "application/json" -ErrorAction Stop
    
    Write-Output "   Login Success!"
    $token = $response.accessToken
    Write-Output "   Token received (truncated): $($token.Substring(0, 20))..."
    
    Write-Output "2. Testing Protected Endpoint ($clientsUrl)..."
    $headers = @{ Authorization = "Bearer $token" }
    $clients = Invoke-RestMethod -Uri $clientsUrl -Method Get -Headers $headers -ErrorAction Stop
    
    Write-Output "   Clients loaded successfully. Count: $($clients.Count)"
    Write-Output "   First Client: $($clients[0].nom)"

    Write-Output "✅ E2E Prod Test Passed!"
} catch {
    Write-Error "❌ E2E Test Failed: $_"
    exit 1
}
