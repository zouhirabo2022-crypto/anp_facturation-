
try {
    $body = @{ username = "admin"; password = "admin123" } | ConvertTo-Json
    $response = Invoke-RestMethod -Uri http://localhost:8080/api/auth/login -Method Post -Body $body -ContentType "application/json" -ErrorAction Stop
    Write-Output "Login Success!"
    $token = $response.accessToken
    Write-Output "Token: $token"
    
    $headers = @{ Authorization = "Bearer $token" }
    $clients = Invoke-RestMethod -Uri http://localhost:8080/api/clients -Method Get -Headers $headers -ErrorAction Stop
    Write-Output "Clients loaded successfully:"
    Write-Output $clients
} catch {
    Write-Error $_
}
